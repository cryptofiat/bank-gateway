package eu.cryptoeuro.bankgateway.services.transaction.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@EqualsAndHashCode
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String processingStatus;

    private String iban;
    private String currency;
    private BigDecimal amount;
    private CreditDebitIndicator creditDebitIndicator;
    private Date bookingDate;
    private String status;

    private String transactionDomainCode;
    private String transactionFamilyCode;
    private String transactionSubFamilyCode;

    /** Unique payment ID assigned by the bank. */
    private String accountServicerReference;
    /** Payment order number or NOTPROVIDED */
    private String instructionId;

    private String debtorName;
    private String debtorId;
    private String ultimateDebtorName;
    private String debtorAccountIban;
    private String debtorAccountOtherId;
    private String debtorAgentBicOrBei;
    private String debtorAgentName;

    private String creditorName;
    private String creditorAccountIban;
    private String creditorAccountOtherId;
    private String creditorAgentBicOrBei;
    private String creditorAgentName;

    private String remittanceInformation;
    private String referenceNumber;

    private String importSource;

    public String getDebtorAccount() {
        return ObjectUtils.defaultIfNull(debtorAccountIban, debtorAccountOtherId);
    }

    public String getCreditorAccount() {
        return ObjectUtils.defaultIfNull(creditorAccountIban, creditorAccountOtherId);
    }

    public boolean isCredit() {
        return CreditDebitIndicator.CRDT == creditDebitIndicator;
    }

    public boolean isDebit() {
        return CreditDebitIndicator.DBIT == creditDebitIndicator;
    }

    public String getPartyName() {
        if (isDebit()) {
            return getCreditorName();
        }

        String debtorName = getDebtorName();
        if (StringUtils.isNotBlank(getUltimateDebtorName())) {
            debtorName += " (" + getUltimateDebtorName() + ")";
        }
        return StringUtils
                .defaultIfBlank(debtorName, getTransactionClassification(transactionDomainCode, transactionFamilyCode, transactionSubFamilyCode, importSource));
    }

    public String getPartyAccount() {
        return isDebit() ? getCreditorAccount() : getDebtorAccount();
    }

    public String getCreditorName() {
        return StringUtils.defaultIfBlank(creditorName,
                getTransactionClassification(transactionDomainCode, transactionFamilyCode, transactionSubFamilyCode, importSource));
    }

    public boolean isFeeOrCharge() {
        return StringUtils.equalsIgnoreCase(transactionDomainCode, "PMNT") && StringUtils.equalsIgnoreCase(transactionFamilyCode, "CCRD") &&
                (StringUtils.equalsIgnoreCase(transactionSubFamilyCode, "CHRG") || StringUtils.equalsIgnoreCase(transactionSubFamilyCode, "FEES"));
    }

    public BigDecimal getAmountSigned() {
        if (isDebit() && getAmount().signum() >= 0) {
            return getAmount().negate();
        }
        return getAmount();
    }

    public static String getTransactionClassification(String domain, String family, String subFamily, String importSource) {
        if (StringUtils.equalsIgnoreCase(domain, "PMNT")) {
            if (StringUtils.equalsIgnoreCase(family, "CCRD")) {
                if (StringUtils.equalsIgnoreCase(subFamily, "POSD")) {
                    return "Debit card payment";
                } else if (StringUtils.equalsIgnoreCase(subFamily, "POSC")) {
                    return "Credit card payment";
                } else if (StringUtils.equalsIgnoreCase(subFamily, "CHRG")) {
                    return "Charge";
                } else if (StringUtils.equalsIgnoreCase(subFamily, "FEES")) {
                    return "Fee";
                }
            } else if (StringUtils.equalsIgnoreCase(subFamily, "MCRD")) {
                if (StringUtils.equalsIgnoreCase(subFamily, "COME")) {
                    return "Commission excluding taxes";
                } else if (StringUtils.equalsIgnoreCase(subFamily, "COMI")) {
                    return "Commission including taxes";
                } else if (StringUtils.equalsIgnoreCase(subFamily, "COMT")) {
                    return "Non Taxable commissions";
                } else if (StringUtils.equalsIgnoreCase(subFamily, "POSP")) {
                    return "Point-of-Sale Payment";
                } else if (StringUtils.equalsIgnoreCase(subFamily, "DAJT")) {
                    return "Credit adjustments";
                } else if (StringUtils.equalsIgnoreCase(subFamily, "CAJT")) {
                    return "Debit adjustments";
                }
            }
        }
        return "Unknown";
    }

    public interface ProcessingStatus {
        String NEW = "NEW";
        String NOTIFIED = "NOTIFIED";
    }

    public interface Source {
        String LHV_CONNECT = "LHV_CONNECT";
        String FILE_IMPORT = "FILE_IMPORT";
    }

}
