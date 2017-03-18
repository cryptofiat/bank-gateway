package eu.cryptoeuro.bankgateway.services.transaction.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Account statement containing basic info and list transactions from specified period.
 *
 * @author Erko Hansar
 * @author Kaarel Jõgeva
 */
@Getter
@Setter
@EqualsAndHashCode
public class Statement implements Serializable {

    private static final long serialVersionUID = 1L;

    private String iban;
    private String currency;
    private BigDecimal balance;
    private Date balanceDate;
    private CreditDebitIndicator creditDebitIndicator;
    private Date fromDate;
    private Date toDate;
    private List<Transaction> transactions;

    public List<Transaction> getTransactions() {
        if (transactions == null) {
            transactions = new ArrayList<>();
        }
        return transactions;
    }

    public void addTransactions(List<Transaction> newTransactions) {
        getTransactions().addAll(newTransactions);
    }

}
