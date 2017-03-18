package eu.cryptoeuro.bankgateway.services.lhv.model;

import java.io.Serializable;
import java.util.Date;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Model class for LHV Connect service request
 *
 * @author Erko Hansar
 * @author Kaarel Jõgeva
 */
@Getter
@Setter
@EqualsAndHashCode
public class LhvRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Type type;
    private String iban;
    private Date fromDate;
    private Date toDate;
    private String messageRequestId;
    private String messageResponseId;
    private Status status;

    public static LhvRequest ofAccountStatement(String iban, Date fromDate, Date toDate, String messageRequestId) {
        LhvRequest request = new LhvRequest();
        request.setType(Type.ACCOUNT_STATEMENT_REPORT);
        request.setIban(iban);
        request.setFromDate(fromDate);
        request.setToDate(toDate);
        request.setMessageRequestId(messageRequestId);
        request.setStatus(Status.REQUESTED);
        return request;
    }

    public enum Type {
        ACCOUNT_STATEMENT_REPORT,
        CARD_PAYMENT_REPORT,
        PAYMENT_INITIATION
    }

    public enum Status {
        REQUESTED,
        DELETED,
        ERROR
    }

}
