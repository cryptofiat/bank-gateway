package eu.cryptoeuro.bankgateway.services.balance;

import java.sql.ResultSet;
import java.sql.SQLException;

import eu.cryptoeuro.bankgateway.services.balance.model.Balance;
import eu.cryptoeuro.bankgateway.services.common.AbstractRowMapper;
import eu.cryptoeuro.bankgateway.services.transaction.model.CreditDebitIndicator;

/**
 * JDBC RowMapper for converting bank account balance records.
 *
 * @author Erko Hansar
 */
public class BalanceRowMapper extends AbstractRowMapper<Balance> {

    public static final BalanceRowMapper INSTANCE = new BalanceRowMapper();

    @Override
    public Balance mapRow(ResultSet rs, int i) throws SQLException {
        Balance balance = new Balance();
        balance.setId(getLong(rs, "id"));
        balance.setIban(getString(rs, "iban"));
        balance.setCurrency(getString(rs, "currency"));
        balance.setBalance(getBigDecimal(rs, "balance"));
        String creditDebitIndicator = getString(rs, "credit_debit_indicator");
        if (creditDebitIndicator != null) {
            balance.setCreditDebitIndicator(CreditDebitIndicator.valueOf(creditDebitIndicator));
        }
        balance.setBalanceDate(getTimestamp(rs, "balance_date"));
        balance.setSyncedToDate(getTimestamp(rs, "synced_to_date"));
        return balance;
    }

}
