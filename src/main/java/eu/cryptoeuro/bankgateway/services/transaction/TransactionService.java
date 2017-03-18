package eu.cryptoeuro.bankgateway.services.transaction;

import java.io.File;

import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.Document;

/**
 * Service interface for transaction related logic.
 *
 * @author Erko Hansar
 */
public interface TransactionService {

    int ABNORMAL_IMPORT_RESULT = -1;

    /**
     * Tries to parse given input stream to bank account statement XML and import found transactions.
     *
     * @param inputFile ISO20022 CAMT.053.001.02 or CAMT.052.001.02 compatible XML file
     * @param importSource source of this transaction
     * @return number of transactions imported or {@link TransactionService#ABNORMAL_IMPORT_RESULT} if no suitable data was found from input
     * @see eu.cryptoeuro.bankgateway.services.transaction.Transaction.Source
     */
    int importTransactions(File inputFile, String importSource) throws Exception;

    /**
     * Extracts transactions from given document and updates bank account balances if possible.
     *
     * @param accountStatementDocument ISO20022 CAMT.053.001.02 Document
     * @param importSource source of this transaction
     * @return number of transactions imported or {@link TransactionService#ABNORMAL_IMPORT_RESULT} if no suitable data was found from input
     * @see eu.cryptoeuro.bankgateway.services.transaction.Transaction.Source
     */
    int importTransactions(Document accountStatementDocument, String importSource);

}
