package eu.cryptoeuro.bankgateway.services.balance;

import java.util.List;

import org.springframework.stereotype.Repository;

import eu.cryptoeuro.bankgateway.services.balance.model.Balance;
import eu.cryptoeuro.bankgateway.services.common.AbstractDao;

/**
 * DAO implementation for reading and writing bank_account_balance data.
 *
 * @author Erko Hansar
 */
@Repository
public class BalanceDao extends AbstractDao {

    public List<Balance> findAll() {
        String sql = "SELECT * FROM lhv.balance ORDER BY balance_date DESC, id DESC";
        return getJdbcTemplate().query(sql, BalanceRowMapper.INSTANCE);
    }

    public void insert(List<Balance> balances) {
        String sql = "INSERT INTO lhv.balance (iban, currency, balance, credit_debit_indicator, balance_date, synced_to_date) "
                + "VALUES (:iban, :currency, :balance, :creditDebitIndicator, :balanceDate, :syncedToDate)";
        getNamedParameterJdbcTemplate().batchUpdate(sql, getBatchParameterSource(balances));
    }

}

