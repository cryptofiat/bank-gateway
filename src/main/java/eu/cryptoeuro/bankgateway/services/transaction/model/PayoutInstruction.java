package eu.cryptoeuro.bankgateway.services.transaction.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
@RequiredArgsConstructor
public class PayoutInstruction implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Date dateCreated;
    private String processingStatus;
    private String accountDebitTxHash;
    private BigDecimal amount;
    private String recipientIban;
    private String recipientName;

    private String reference;
    private String referenceNumber;

    private Long transactionId;
    private String accountServicerReference;
    private String supplyDecreaseTxHash;

    public interface ProcessingStatus {
        String NEW = "NEW";
        String DEBIT_CONFIRMED = "ACCOUNT_DEBIT_CONFIRMED";
        String SUBMITTED_TO_BANK = "SUBMITTED_TO_BANK";
        String RECEIVED_BY_BANK = "RECEIVED_BY_BANK";
        String PAID_OUT = "PAID_OUT";
        String SUPPLY_DECREASED = "SUPPLY_DECREASED";
    }


}
