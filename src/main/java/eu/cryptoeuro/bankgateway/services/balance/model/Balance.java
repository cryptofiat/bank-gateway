package eu.cryptoeuro.bankgateway.services.balance.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import eu.cryptoeuro.bankgateway.services.transaction.model.CreditDebitIndicator;

/**
 * Model class for a bank account balance.
 *
 * @author Erko Hansar
 */
@Getter
@Setter
@EqualsAndHashCode
public class Balance implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String iban;
    private String currency;
    private BigDecimal balance;
    private CreditDebitIndicator creditDebitIndicator;
    private Date balanceDate;
    private Date syncedToDate;

    public boolean isCredit() {
        return CreditDebitIndicator.CRDT == creditDebitIndicator;
    }

    public boolean isDebit() {
        return CreditDebitIndicator.DBIT == creditDebitIndicator;
    }

    public BigDecimal getBalanceSigned() {
        if (balance == null) {
            return null;
        }
        if (isCredit()) {
            return balance;
        } else {
            return balance.negate();
        }
    }

}
