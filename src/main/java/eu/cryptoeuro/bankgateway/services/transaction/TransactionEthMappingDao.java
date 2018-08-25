package eu.cryptoeuro.bankgateway.services.transaction;

import eu.cryptoeuro.bankgateway.services.common.AbstractDao;
import eu.cryptoeuro.bankgateway.services.common.AdvancedParameterSource;
import eu.cryptoeuro.bankgateway.services.transaction.model.TransactionEthMapping;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.IntStream;

@Repository
public class TransactionEthMappingDao extends AbstractDao {

    public List<TransactionEthMapping> findAll() {
        String sql = "SELECT * FROM transaction_eth_mapping ORDER BY id DESC";
        return getNamedParameterJdbcTemplate().query(sql, TransactionEthMappingRowMapper.INSTANCE);
    }

    public void insertOrUpdate(TransactionEthMapping transactionEthMapping) {
        String sql = "INSERT INTO transaction_eth_mapping "
                + "(transaction_id, supply_increase_txhash, account_credit_txhash) VALUES "
                + "(:transactionId, :supplyIncreaseTxHash, :accountCreditTxHash) "
                + "ON CONFLICT (transaction_id) "
                + "DO UPDATE SET "
                + "supply_increase_txhash = :supplyIncreaseTxHash, "
                + "account_credit_txhash = :accountCreditTxHash;";

        AdvancedParameterSource source = new AdvancedParameterSource().addBean(transactionEthMapping);
        getNamedParameterJdbcTemplate().update(sql, source);
    }

    public TransactionEthMapping findByTransactionId(long transactionId) {
        String sql = "SELECT * FROM transaction_eth_mapping WHERE transaction_id = :transactionId";
        AdvancedParameterSource source = new AdvancedParameterSource().addValue("transactionId", transactionId);
        return getNamedParameterJdbcTemplate().queryForObject(sql, source, TransactionEthMappingRowMapper.INSTANCE);
    }
}
