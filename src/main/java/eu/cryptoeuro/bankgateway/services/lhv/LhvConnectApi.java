package eu.cryptoeuro.bankgateway.services.lhv;

import java.util.Optional;

import javax.xml.bind.JAXBElement;

import org.springframework.web.client.RestClientException;

import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_060_001_03.Document;
import eu.cryptoeuro.bankgateway.jaxb.lhv.HeartBeatResponse;
import eu.cryptoeuro.bankgateway.services.lhv.model.LhvMessage;

/**
 * Describes available operations with LHV Connect API
 *
 * @see <a href="http://partners.lhv.ee/en/connect/">Specification for LHV CONNECT</a>
 * @author Erko Hansar
 * @author Kaarel Jõgeva
 */
public interface LhvConnectApi {

    /**
     * Checks if LHV Connect service is available and operational.
     *
     * @return {@link LhvMessage} containing {@link HeartBeatResponse}
     */
    LhvMessage<HeartBeatResponse> getHeartbeat();

    /**
     * Submits account statement request for processing
     *
     * @param requestDocument {@link Document} specifying account statement parameters
     * @return {@link LhvMessage} containing {@link LhvMessage#getMessageRequestId()} for the performed request.
     * @throws RestClientException
     */
    LhvMessage<?> postForMessageRequestId(JAXBElement<Document> requestDocument) throws RestClientException;

    /**
     * Fetches next message from LHV CONNECT message queue
     *
     * @return Optional indicating whether message was available or not.
     */
    Optional<LhvMessage<?>> getNextMessage();

    /**
     * Deletes message from LHV CONNECT message queue where {@link LhvConnectService.API#HEADER_RESPONSE_ID} equals {@link LhvMessage#getMessageResponseId()}
     *
     * @param message {@link LhvMessage} with {@link LhvMessage#getMessageResponseId()}
     * @return Response from LHV CONNECT
     */
    LhvMessage<?> deleteMessage(LhvMessage<?> message);

    /**
     * Checks if LHV CONNECT is configured.
     *
     * @return true if service can be used
     */
    default boolean isAvailable() {
        return false;
    }

}
