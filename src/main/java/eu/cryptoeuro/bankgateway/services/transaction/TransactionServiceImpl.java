package eu.cryptoeuro.bankgateway.services.transaction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;
import javax.xml.transform.stream.StreamSource;

import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_054_001_02.Document;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_052_001_02.AccountReport11;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.AccountStatement2;
import eu.cryptoeuro.bankgateway.services.balance.BalanceDao;
import eu.cryptoeuro.bankgateway.services.balance.model.Balance;
import eu.cryptoeuro.bankgateway.services.common.AccountReportUtil;
import eu.cryptoeuro.bankgateway.services.common.AccountStatementResponseUtil;
import eu.cryptoeuro.bankgateway.services.lhv.LhvConnectApiImpl;
import eu.cryptoeuro.bankgateway.services.transaction.model.Statement;
import eu.cryptoeuro.bankgateway.services.transaction.model.Transaction;

import static eu.cryptoeuro.bankgateway.services.transaction.model.Transaction.Source.LHV_CONNECT;

@Slf4j
@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionDao transactionDao;
    @Autowired
    private BalanceDao balanceDao;

    private Jaxb2Marshaller unmarshaller;

    @PostConstruct
    public void init() {
        unmarshaller = LhvConnectApiImpl.createMarshaller("eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_052_001_02",
                "eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02", "eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_054_001_02");
    }

    @SuppressWarnings("rawtypes")
    @Override
    public int importTransactions(File inputFile, String importSource) throws Exception {
        Object document = null;
        try (InputStream stream = new FileInputStream(inputFile)) {
            document = ((JAXBElement)unmarshaller.unmarshal(new StreamSource(stream))).getValue();
        } catch (UnmarshallingFailureException e) {
            // Probably XML without a namespace, try to determine if statement or report and unmarshal again
            Optional<String> namespace = determineNamespace(inputFile);
            if (namespace.isPresent()) {
                document = unmarshalWithNamespace(inputFile, namespace.get());
            }

            if (!namespace.isPresent() || document == null) {
                throw new IllegalArgumentException("Unsupported file format for transaction import!");
            }
        }

        if (document instanceof eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.Document) {
            return importTransactions((eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.Document)document, importSource);
        } else if (document instanceof eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_052_001_02.Document) {
            return importTransactions((eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_052_001_02.Document)document, importSource);
        }

        Error e = new Error("Unmarshaller configuration and type handling don't match! Class: " + document.getClass());
        log.error("Failed to process input!", e);
        throw e;
    }

    @Override
    public int importTransactions(eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.Document accountStatementDocument, String importSource) {
        if (!hasStatements(accountStatementDocument)) {
            return ABNORMAL_IMPORT_RESULT;
        }
        return persistTransactions(extractStatements(accountStatementDocument), importSource);
    }

    @Override
    public void importTransaction(Document document) {
        List<Statement> statements = Collections.singletonList(extractStatement(document));
        persistTransactions(statements, LHV_CONNECT);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Transaction> findUnprocessedInboundTransactions() {
        return transactionDao.findUnprocessedInbound();
    }

    @Override
    public void updateProcessingStatus(long transactionId, String processingStatus) {
        transactionDao.updateProcessingStatus(transactionId, processingStatus);
    }

    ///// PRIVATE METHODS /////

    private int importTransactions(eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_052_001_02.Document accountReportDocument, String importSource) {
        if (!hasStatements(accountReportDocument)) {
            return ABNORMAL_IMPORT_RESULT;
        }
        return persistTransactions(extractStatements(accountReportDocument), importSource);
    }

    private Optional<String> determineNamespace(File inputFile) {
        try (Scanner scanner = new Scanner(inputFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (StringUtils.containsIgnoreCase(line, "BkToCstmrAcctRpt")) {
                    return Optional.of("urn:iso:std:iso:20022:tech:xsd:camt.052.001.02");
                } else if (StringUtils.containsIgnoreCase(line, "BkToCstmrStmt")) {
                    return Optional.of("urn:iso:std:iso:20022:tech:xsd:camt.053.001.02");
                }
            }
        } catch (FileNotFoundException e) {
            // If we can't find the file, return empty optional
        }
        return Optional.empty();
    }

    private Object unmarshalWithNamespace(File xmlInputStream, String namespace) throws Exception {
        XMLStreamReader streamReader = null;
        StreamReaderDelegate delegate = null;
        try (InputStream stream = new FileInputStream(xmlInputStream)) {
            streamReader = XMLInputFactory.newFactory().createXMLStreamReader(stream);
            delegate = new StreamReaderDelegate(streamReader) {
                @Override
                public String getAttributeNamespace(int index) {
                    return "";
                }

                @Override
                public String getNamespaceURI() {
                    return namespace;
                }
            };

            Unmarshaller unmarshaller = this.unmarshaller.getJaxbContext().createUnmarshaller();
            return ((JAXBElement<?>)unmarshaller.unmarshal(delegate)).getValue();
        } catch (JAXBException ex) {
            return null; // Let the caller handle
        } finally {
            if (delegate != null) {
                delegate.close();
            }
            if (streamReader != null) {
                streamReader.close();
            }
        }
    }

    private boolean hasStatements(eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.Document accountStatementDocument) {
        return accountStatementDocument != null && accountStatementDocument.getBkToCstmrStmt() != null
                && !accountStatementDocument.getBkToCstmrStmt().getStmt().isEmpty();
    }

    private boolean hasStatements(eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_052_001_02.Document accountReportDocument) {
        return accountReportDocument != null && accountReportDocument.getBkToCstmrAcctRpt() != null
                && !accountReportDocument.getBkToCstmrAcctRpt().getRpt().isEmpty();
    }

    private List<Statement> extractStatements(eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.Document accountStatementDocument) {
        List<Statement> statements = new ArrayList<>();
        for (AccountStatement2 bankStatement : accountStatementDocument.getBkToCstmrStmt().getStmt()) {
            Statement statement = AccountStatementResponseUtil.parseStatement(bankStatement);
            List<Transaction> statementTransactions = bankStatement.getNtry().stream()
                    .map(entry -> AccountStatementResponseUtil.parseTransaction(entry, statement.getIban()))
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            statement.setTransactions(statementTransactions);
            statements.add(statement);
        }
        return statements;
    }

    private Statement extractStatement(eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_054_001_02.Document debitCreditNotificationDocument) {
        return null;
    }

    private List<Statement> extractStatements(eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_052_001_02.Document accountReportDocument) {
        List<Statement> statements = new ArrayList<>();
        for (AccountReport11 bankReport : accountReportDocument.getBkToCstmrAcctRpt().getRpt()) {
            Statement statement = AccountReportUtil.parseReport(bankReport);
            List<Transaction> statementTransactions = bankReport.getNtry().stream()
                    .map(entry -> AccountReportUtil.parseTransaction(entry, statement.getIban()))
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            statement.setTransactions(statementTransactions);
            statements.add(statement);
        }
        return statements;
    }

    private int persistTransactions(List<Statement> statements, final String importSource) {
        List<Balance> balances = new ArrayList<>();
        List<Transaction> messageTransactions = new ArrayList<>();
        for (Statement statement : statements) {
            if (statement.getCurrency() != null) {
                Balance balance = createBalance(statement);
                balances.add(balance);
            }

            messageTransactions.addAll(statement.getTransactions());
        }

        if (CollectionUtils.isNotEmpty(balances)) {
            balanceDao.insert(balances);
        }

        if (CollectionUtils.isEmpty(messageTransactions)) {
            return 0;
        }
        messageTransactions.forEach((transaction) -> transaction.setImportSource(importSource));
        return transactionDao.insert(messageTransactions);
    }

    private Balance createBalance(Statement statement) {
        Balance balance = new Balance();
        balance.setIban(statement.getIban());
        balance.setCurrency(statement.getCurrency());
        balance.setBalance(statement.getBalance());
        balance.setCreditDebitIndicator(statement.getCreditDebitIndicator());
        balance.setBalanceDate(statement.getBalanceDate());
        balance.setSyncedToDate(statement.getToDate());
        return balance;
    }

}
