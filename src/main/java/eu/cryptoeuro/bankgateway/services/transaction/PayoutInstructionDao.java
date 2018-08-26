package eu.cryptoeuro.bankgateway.services.transaction;

import eu.cryptoeuro.bankgateway.services.common.AbstractDao;
import eu.cryptoeuro.bankgateway.services.common.AdvancedParameterSource;
import eu.cryptoeuro.bankgateway.services.transaction.model.PayoutInstruction;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@Repository
public class PayoutInstructionDao extends AbstractDao {
    public void insert(PayoutInstruction payoutInstruction) {
        String sql = "INSERT INTO payout_instruction "
                + "(account_debit_txhash, recipient_name, recipient_iban, reference, reference_number) VALUES "
                + "(:accountDebitTxHash, :recipientName, :recipientIban, :reference, :referenceNumber);";
        AdvancedParameterSource source = new AdvancedParameterSource().addBean(payoutInstruction);
        getNamedParameterJdbcTemplate().update(sql, source);
    }

    public void insert(String accountDebitTxHash, String recipientIban) {
        String sql = "INSERT INTO payout_instruction "
                + "(account_debit_txhash, recipient_iban, processing_status) VALUES "
                + "(:accountDebitTxHash, :recipientIban, :processingStatus);";
        AdvancedParameterSource source = new AdvancedParameterSource()
                .addValue("accountDebitTxHash", accountDebitTxHash)
                .addValue("recipientIban", recipientIban)
                .addValue("processingStatus", PayoutInstruction.ProcessingStatus.NEW);
        getNamedParameterJdbcTemplate().update(sql, source);
    }

    public void markDebitConfirmedAndSetAmount(String accountDebitTxHash, BigInteger amountInCents) {
        BigDecimal amount = BigDecimal.valueOf(amountInCents.longValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.DOWN);
        String sql = "UPDATE payout_instruction SET "
                + "processing_status = :processingStatus, amount = :amount "
                + "WHERE account_debit_txhash = :accountDebitTxHash;";
        AdvancedParameterSource source = new AdvancedParameterSource()
                .addValue("accountDebitTxHash", accountDebitTxHash)
                .addValue("amount", amount)
                .addValue("processingStatus", PayoutInstruction.ProcessingStatus.DEBIT_CONFIRMED);
        getNamedParameterJdbcTemplate().update(sql, source);
    }

    public void markSubmittedToBank(String accountDebitTxHash) {
        String sql = "UPDATE payout_instruction SET "
                + "processing_status = :processingStatus "
                + "WHERE account_debit_txhash = :accountDebitTxHash;";
        AdvancedParameterSource source = new AdvancedParameterSource()
                .addValue("accountDebitTxHash", accountDebitTxHash)
                .addValue("processingStatus", PayoutInstruction.ProcessingStatus.SUBMITTED_TO_BANK);
        getNamedParameterJdbcTemplate().update(sql, source);
    }

    public void markReceivedByBank(String accountDebitTxHash, String accountServicerReference) {
        String sql = "UPDATE payout_instruction SET "
                + "processing_status = :processingStatus, "
                + "account_servicer_reference = :accountServicerReference "
                + "WHERE account_debit_txhash = :accountDebitTxHash;";
        AdvancedParameterSource source = new AdvancedParameterSource()
                .addValue("accountDebitTxHash", accountDebitTxHash)
                .addValue("accountServicerReference", accountServicerReference)
                .addValue("processingStatus", PayoutInstruction.ProcessingStatus.RECEIVED_BY_BANK);
        getNamedParameterJdbcTemplate().update(sql, source);
    }

    public void markPaidOut(String accountDebitTxHash, long banktransactionId) {
        String sql = "UPDATE payout_instruction SET "
                + "processing_status = :processingStatus, "
                + "transaction_id = :bankTransactionId "
                + "WHERE account_debit_txhash = :accountDebitTxHash;";
        AdvancedParameterSource source = new AdvancedParameterSource()
                .addValue("accountDebitTxHash", accountDebitTxHash)
                .addValue("bankTransactionId", banktransactionId)
                .addValue("processingStatus", PayoutInstruction.ProcessingStatus.PAID_OUT);
        getNamedParameterJdbcTemplate().update(sql, source);
    }

    public void markSupplyDecreased(String accountDebitTxHash, String supplyDecreaseTxHash) {
        String sql = "UPDATE payout_instruction SET "
                + "processing_status = :processingStatus, supply_decrease_txhash = :supplyDecreaseTxHash, "
                + "transaction_id = :bankTransactionId "
                + "WHERE account_debit_txhash = :accountDebitTxHash;";
        AdvancedParameterSource source = new AdvancedParameterSource()
                .addValue("accountDebitTxHash", accountDebitTxHash)
                .addValue("supplyDecreaseTxHash", supplyDecreaseTxHash)
                .addValue("processingStatus", PayoutInstruction.ProcessingStatus.SUPPLY_DECREASED);
        getNamedParameterJdbcTemplate().update(sql, source);
    }
}
