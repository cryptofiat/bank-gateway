package eu.cryptoeuro.bankgateway.services.lhv.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBElement;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import org.springframework.http.HttpStatus;

import eu.cryptoeuro.bankgateway.jaxb.lhv.Errors;
import eu.cryptoeuro.bankgateway.services.lhv.LhvConnectApiImpl;

/**
 * Object representing LHV Connect Service response
 *
 * @author Erko Hansar
 * @author Kaarel Jõgeva
 */
@Getter
@Setter
@EqualsAndHashCode
public class LhvMessage<T> {

    private HttpStatus statusCode;
    private String messageRequestId;
    private String messageResponseId;
    private T entity;

    public void setEntity(T entity) {
        if (entity instanceof JAXBElement) {
            @SuppressWarnings("unchecked")
            JAXBElement<T> jaxbElement = (JAXBElement<T>)entity;
            this.entity = jaxbElement.getValue();
        } else {
            this.entity = entity;
        }
    }

    public boolean isAccountStatementEntity() {
        return entity instanceof eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.Document;
    }

    public eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.Document getAccountStatementDocument() {
        if (!isAccountStatementEntity()) {
            throw new IllegalStateException();
        }
        return (eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02.Document) entity;
    }

    public boolean isDebitCreditNotification() {
        return entity instanceof eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_054_001_02.Document;
    }

    public eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_054_001_02.Document getDebitCreditNotificationDocument() {
        if (!isDebitCreditNotification()) {
            throw new IllegalStateException();
        }
        return (eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_054_001_02.Document) entity;
    }

    public boolean isErrorEntity() {
        return isError() || isErrors();
    }

    public boolean hasError() {
        return LhvConnectApiImpl.LhvErrorHandler.isErrorCode(statusCode) || isErrorEntity();
    }

    private boolean isError() {
        return entity instanceof Errors.Error;
    }

    private boolean isErrors() {
        return entity instanceof Errors;
    }

    public String getErrors() {
        if (entity == null) {
            return null;
        }

        List<Errors.Error> errors = new ArrayList<>();
        if (isErrors()) {
            errors.addAll(((Errors)entity).getError());
        } else if (isError()) {
            errors.add((Errors.Error)entity);
        }

        return errors.stream()
                .map(e -> String.format("Field: %s; ErrorCode: %s; Description: %s.", e.getField(), e.getErrorCode(), e.getDescription()))
                .collect(Collectors.joining(" "));
    }

}
