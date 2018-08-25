package eu.cryptoeuro.bankgateway.services.transaction.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
public class TransactionEthMapping implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long transactionId;
    private String supplyIncreaseTxHash;
    private String accountCreditTxHash;
}
