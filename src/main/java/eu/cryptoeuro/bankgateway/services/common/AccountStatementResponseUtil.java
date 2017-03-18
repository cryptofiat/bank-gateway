package eu.cryptoeuro.bankgateway.services.common;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.AccountIdentification4Choice;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.AccountStatement2;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.BalanceType12Code;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.BranchAndFinancialInstitutionIdentification4;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.CashAccount16;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.CashBalance3;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.CreditorReferenceInformation2;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.CreditorReferenceType1Choice;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.CreditorReferenceType2;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.DocumentType3Code;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.EntryDetails1;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.EntryTransaction2;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.FinancialInstitutionIdentification7;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.GenericAccountIdentification1;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.PartyIdentification32;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.RemittanceInformation5;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.ReportEntry2;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.StructuredRemittanceInformation7;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.TransactionAgents2;
import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.TransactionParty2;
import eu.cryptoeuro.bankgateway.services.transaction.model.CreditDebitIndicator;
import eu.cryptoeuro.bankgateway.services.transaction.model.Statement;
import eu.cryptoeuro.bankgateway.services.transaction.model.Transaction;

/**
 * Util class containing functionality for mapping {@link Transaction} objects from LHV Connect Account Statement Response
 *
 * @author Erko Hansar
 * @author Kaarel Jõgeva
 */
public class AccountStatementResponseUtil {

    private static final java.lang.String SCOR = "SCOR";

    private AccountStatementResponseUtil() {
        // No new instances
    }

    public static Statement parseStatement(AccountStatement2 statement) {
        Statement stmt = new Statement();
        stmt.setIban(statement.getAcct().getId().getIBAN());
        stmt.setCurrency(statement.getAcct().getCcy());

        Predicate<CashBalance3> closingBalancePredicate = cashBalance3 -> BalanceType12Code.CLBD.equals(cashBalance3.getTp().getCdOrPrtry().getCd());
        CashBalance3 balance = statement.getBal().stream()
                .filter(closingBalancePredicate)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Closing balance element is mandatory!"));
        Assert.state(stmt.getCurrency().equals(balance.getAmt().getCcy()), "Account and balance currency must match!");
        stmt.setBalance(balance.getAmt().getValue());
        stmt.setBalanceDate(balance.getDt().getDt().toGregorianCalendar().getTime());
        stmt.setCreditDebitIndicator(CreditDebitIndicator.valueOf(balance.getCdtDbtInd().name()));
        stmt.setFromDate(statement.getFrToDt().getFrDtTm().toGregorianCalendar().getTime());
        stmt.setToDate(statement.getFrToDt().getToDtTm().toGregorianCalendar().getTime());

        return stmt;
    }

    public static List<Transaction> parseTransaction(final ReportEntry2 entry) {
        return entry.getNtryDtls().stream().map(EntryDetails1::getTxDtls).flatMap(List::stream)
                .map(details -> parseTransactionDetails(details, entry)).collect(Collectors.toList());
    }


    private static Transaction parseTransactionDetails(final EntryTransaction2 details, final ReportEntry2 entry) {
        Transaction t = new Transaction();

        // Mandatory
        if (details.getAmtDtls() != null) {
            t.setCurrency(details.getAmtDtls().getTxAmt().getAmt().getCcy());
            t.setAmount(details.getAmtDtls().getTxAmt().getAmt().getValue());
        } else {
            t.setCurrency(entry.getAmt().getCcy());
            t.setAmount(entry.getAmt().getValue());
        }
        t.setCreditDebitIndicator(CreditDebitIndicator.valueOf(entry.getCdtDbtInd().name()));
        if (entry.getBookgDt().getDtTm() != null) {
            t.setBookingDate(entry.getBookgDt().getDtTm().toGregorianCalendar().getTime());
        } else {
            t.setBookingDate(entry.getBookgDt().getDt().toGregorianCalendar().getTime());
        }
        t.setStatus(entry.getSts().name());

        t.setTransactionDomainCode(entry.getBkTxCd().getDomn().getCd());
        t.setTransactionFamilyCode(entry.getBkTxCd().getDomn().getFmly().getCd());
        t.setTransactionSubFamilyCode(entry.getBkTxCd().getDomn().getFmly().getSubFmlyCd());

        t.setAccountServicerReference(details.getRefs().getAcctSvcrRef());
        t.setInstructionId(details.getRefs().getInstrId());

        // Optional
        Optional<TransactionParty2> rltdPties = Optional.ofNullable(details.getRltdPties());
        rltdPties.map(TransactionParty2::getDbtr).map(PartyIdentification32::getNm).ifPresent(t::setDebtorName);
        rltdPties.map(TransactionParty2::getUltmtDbtr).map(PartyIdentification32::getNm).ifPresent(t::setUltimateDebtorName);
        Optional<AccountIdentification4Choice> debtorAccountId = rltdPties.map(TransactionParty2::getDbtrAcct).map(CashAccount16::getId);
        debtorAccountId.map(AccountIdentification4Choice::getIBAN).ifPresent(t::setDebtorAccountIban);
        debtorAccountId.map(AccountIdentification4Choice::getOthr).map(GenericAccountIdentification1::getId).ifPresent(t::setDebtorAccountOtherId);

        rltdPties.map(TransactionParty2::getCdtr).map(PartyIdentification32::getNm).ifPresent(t::setCreditorName);
        Optional<AccountIdentification4Choice> creditorAccountId = rltdPties.map(TransactionParty2::getCdtrAcct).map(CashAccount16::getId);
        creditorAccountId.map(AccountIdentification4Choice::getIBAN).ifPresent(t::setCreditorAccountIban);
        creditorAccountId.map(AccountIdentification4Choice::getOthr).map(GenericAccountIdentification1::getId).ifPresent(t::setCreditorAccountOtherId);

        Optional<FinancialInstitutionIdentification7> debtorInstitution =
                Optional.ofNullable(details.getRltdAgts()).map(TransactionAgents2::getDbtrAgt).map(BranchAndFinancialInstitutionIdentification4::getFinInstnId);
        debtorInstitution.map(FinancialInstitutionIdentification7::getBIC).ifPresent(t::setDebtorAgentBicOrBei);
        debtorInstitution.map(FinancialInstitutionIdentification7::getNm).ifPresent(t::setDebtorAgentName);

        Optional<FinancialInstitutionIdentification7> creditorInstitution =
                Optional.ofNullable(details.getRltdAgts()).map(TransactionAgents2::getCdtrAgt).map(BranchAndFinancialInstitutionIdentification4::getFinInstnId);
        creditorInstitution.map(FinancialInstitutionIdentification7::getBIC).ifPresent(t::setCreditorAgentBicOrBei);
        creditorInstitution.map(FinancialInstitutionIdentification7::getNm).ifPresent(t::setCreditorAgentName);

        Optional<RemittanceInformation5> remittanceInformation = Optional.ofNullable(details.getRmtInf());
        remittanceInformation.map(RemittanceInformation5::getUstrd).map(list -> StringUtils.join(list, ", ")).ifPresent(t::setRemittanceInformation);
        remittanceInformation.map(RemittanceInformation5::getStrd)
                .map(info -> info.stream().filter(AccountStatementResponseUtil::isScorReference).findFirst().orElse(null))
                .map(StructuredRemittanceInformation7::getCdtrRefInf).map(CreditorReferenceInformation2::getRef).ifPresent(t::setReferenceNumber);

        return t;
    }

    private static boolean isScorReference(StructuredRemittanceInformation7 info) {
        Optional<String> referenceType =
                Optional.ofNullable(info).map(StructuredRemittanceInformation7::getCdtrRefInf).map(CreditorReferenceInformation2::getTp)
                        .map(CreditorReferenceType2::getCdOrPrtry).map(CreditorReferenceType1Choice::getCd).map(DocumentType3Code::value);
        return referenceType.isPresent() && SCOR.equals(referenceType.get());
    }

}
