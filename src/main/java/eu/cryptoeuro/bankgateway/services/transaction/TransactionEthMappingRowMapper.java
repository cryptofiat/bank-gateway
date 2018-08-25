package eu.cryptoeuro.bankgateway.services.transaction;

import eu.cryptoeuro.bankgateway.services.common.AbstractRowMapper;
import eu.cryptoeuro.bankgateway.services.transaction.model.TransactionEthMapping;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * JDBC RowMapper for converting transaction Eth mapping records.
 */
public class TransactionEthMappingRowMapper extends AbstractRowMapper<TransactionEthMapping> {

    public static final TransactionEthMappingRowMapper INSTANCE = new TransactionEthMappingRowMapper();

    @Override
    public TransactionEthMapping mapRow(ResultSet rs, int i) throws SQLException {
        TransactionEthMapping result = new TransactionEthMapping();
        result.setId(getLong(rs, "id"));
        result.setTransactionId(getLong(rs, "transaction_id"));
        result.setSupplyIncreaseTxHash(getString(rs, "supply_increase_txhash"));
        result.setAccountCreditTxHash(getString(rs, "account_credit_txhash"));
        return result;
    }
}
