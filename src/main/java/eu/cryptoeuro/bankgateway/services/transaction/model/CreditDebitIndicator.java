package eu.cryptoeuro.bankgateway.services.transaction.model;

import lombok.Getter;

/**
 * Enum for balance and transaction classification.
 *
 * @author Erko Hansar
 * @author Kaarel Jõgeva
 */
@Getter
public enum CreditDebitIndicator {

    /** Positive amount or zero */
    CRDT("Credit"),
    /** Negative amount */
    DBIT("Debit");

    private final String description;

    CreditDebitIndicator(String description) {
        this.description = description;
    }

}
