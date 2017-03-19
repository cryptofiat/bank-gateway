package eu.cryptoeuro.bankgateway.services.transaction;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Repository;

import eu.cryptoeuro.bankgateway.services.common.AbstractDao;
import eu.cryptoeuro.bankgateway.services.common.AdvancedParameterSource;
import eu.cryptoeuro.bankgateway.services.transaction.model.Transaction;

/**
 * DAO implementation for reading and writing transaction data.
 *
 * @author Erko Hansar
 */
@Repository
public class TransactionDao extends AbstractDao {

    public List<Transaction> findAll() {
        String sql = "SELECT * FROM lhv.transaction ORDER BY id DESC";
        return getNamedParameterJdbcTemplate().query(sql, TransactionRowMapper.INSTANCE);
    }

    public int insert(List<Transaction> transactions) {
        String sql = "INSERT INTO lhv.transaction "
                + "(processing_status, iban, currency, amount, credit_debit_indicator, booking_date, status, transaction_domain_code, transaction_family_code, transaction_sub_family_code, account_servicer_reference, instruction_id, debtor_name, debtor_id, ultimate_debtor_name, debtor_account_iban, debtor_account_other_id, debtor_agent_bic_or_bei, debtor_agent_name, creditor_name, creditor_account_iban, creditor_account_other_id, creditor_agent_bic_or_bei, creditor_agent_name, remittance_information, reference_number, import_source) VALUES "
                + "(:processingStatus, :iban, :currency, :amount, :creditDebitIndicator, :bookingDate, :status, :transactionDomainCode, :transactionFamilyCode, :transactionSubFamilyCode, :accountServicerReference, :instructionId, :debtorName, :debtorId, :ultimateDebtorName, :debtorAccountIban, :debtorAccountOtherId, :debtorAgentBicOrBei, :debtorAgentName, :creditorName, :creditorAccountIban, :creditorAccountOtherId, :creditorAgentBicOrBei, :creditorAgentName, :remittanceInformation, :referenceNumber, :importSource) "
                + "ON CONFLICT (account_servicer_reference) DO NOTHING;";

        int[] created = getNamedParameterJdbcTemplate().batchUpdate(sql, getBatchParameterSource(transactions));
        return IntStream.of(created).sum();
    }

    public List<Transaction> findUnprocessed() {
        String sql = "SELECT * FROM lhv.transaction WHERE processing_status IN (:processingStatus) ORDER BY id";
        AdvancedParameterSource source = new AdvancedParameterSource().addValue("processingStatus", Arrays.asList(Transaction.ProcessingStatus.NEW));
        return getNamedParameterJdbcTemplate().query(sql, source, TransactionRowMapper.INSTANCE);
    }

    public void updateProcessingStatus(long transactionId, String processingStatus) {
        String sql = "UPDATE lhv.transaction "
                + "SET processing_status=:processingStatus "
                + "WHERE id = :id";
        AdvancedParameterSource source = new AdvancedParameterSource()
                .addValue("id", transactionId)
                .addValue("processingStatus", processingStatus);
        getNamedParameterJdbcTemplate().update(sql, source);
    }

}
