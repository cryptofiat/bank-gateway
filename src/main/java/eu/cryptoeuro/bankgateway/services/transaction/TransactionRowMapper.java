package eu.cryptoeuro.bankgateway.services.transaction;

import java.sql.ResultSet;
import java.sql.SQLException;

import eu.cryptoeuro.bankgateway.services.common.AbstractRowMapper;
import eu.cryptoeuro.bankgateway.services.transaction.model.CreditDebitIndicator;
import eu.cryptoeuro.bankgateway.services.transaction.model.Transaction;

/**
 * JDBC RowMapper for converting transaction records.
 *
 * @author Erko Hansar
 */
public class TransactionRowMapper extends AbstractRowMapper<Transaction> {

    public static final TransactionRowMapper INSTANCE = new TransactionRowMapper();

    @Override
    public Transaction mapRow(ResultSet rs, int i) throws SQLException {
        Transaction result = new Transaction();
        result.setId(getLong(rs, "id"));
        result.setProcessingStatus(getString(rs, "processing_status"));
        result.setIban(getString(rs, "iban"));
        result.setCurrency(getString(rs, "currency"));
        result.setAmount(getBigDecimal(rs, "amount"));
        result.setCreditDebitIndicator(CreditDebitIndicator.valueOf(getString(rs, "credit_debit_indicator")));
        result.setBookingDate(getTimestamp(rs, "booking_date"));
        result.setStatus(getString(rs, "status"));
        result.setTransactionDomainCode(getString(rs, "transaction_domain_code"));
        result.setTransactionFamilyCode(getString(rs, "transaction_family_code"));
        result.setTransactionSubFamilyCode(getString(rs, "transaction_sub_family_code"));
        result.setAccountServicerReference(getString(rs, "account_servicer_reference"));
        result.setInstructionId(getString(rs, "instruction_id"));
        result.setDebtorName(getString(rs, "debtor_name"));
        result.setDebtorId(getString(rs, "debtor_id"));
        result.setUltimateDebtorName(getString(rs, "ultimate_debtor_name"));
        result.setDebtorAccountIban(getString(rs, "debtor_account_iban"));
        result.setDebtorAccountOtherId(getString(rs, "debtor_account_other_id"));
        result.setDebtorAgentBicOrBei(getString(rs, "debtor_agent_bic_or_bei"));
        result.setDebtorAgentName(getString(rs, "debtor_agent_name"));
        result.setCreditorName(getString(rs, "creditor_name"));
        result.setCreditorAccountIban(getString(rs, "creditor_account_iban"));
        result.setCreditorAccountOtherId(getString(rs, "creditor_account_other_id"));
        result.setCreditorAgentBicOrBei(getString(rs, "creditor_agent_bic_or_bei"));
        result.setCreditorAgentName(getString(rs, "creditor_agent_name"));
        result.setRemittanceInformation(getString(rs, "remittance_information"));
        result.setReferenceNumber(getString(rs, "reference_number"));
        result.setImportSource(getString(rs, "import_source"));

        return result;
    }
}
