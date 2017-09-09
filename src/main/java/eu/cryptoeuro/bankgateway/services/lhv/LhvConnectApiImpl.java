package eu.cryptoeuro.bankgateway.services.lhv;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamSource;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.apache.http.client.HttpClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.Assert;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_060_001_03.Document;
import eu.cryptoeuro.bankgateway.jaxb.lhv.HeartBeatResponse;
import eu.cryptoeuro.bankgateway.services.lhv.model.LhvMessage;

/**
 * RestTemplate for communicating with LHV Connect service. Needs HttpClient with properly set up SSL context.
 *
 * @author Erko Hansar
 * @author Kaarel Jõgeva
 */
@Slf4j
public class LhvConnectApiImpl extends RestTemplate implements LhvConnectApi {

    private final boolean available;
    private final LhvMessageExtractor messageExtractor;

    public LhvConnectApiImpl(HttpClient httpClient) {
        super();
        if (!(available = httpClient != null)) {
            log.warn("HttpClient is not configured. LHV Connect functionality will be unavailable!");
            messageExtractor = null;
            return;
        }

        setRequestFactory(new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient)));
        setErrorHandler(new LhvErrorHandler());

        Jaxb2Marshaller marshaller = createMarshaller("eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_060_001_03");
        Jaxb2Marshaller unmarshaller = createMarshaller("eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_053_001_02",
                "eu.cryptoeuro.bankgateway.jaxb.iso20022.camt_054_001_02",
                "eu.cryptoeuro.bankgateway.jaxb.lhv");
        messageExtractor = new LhvMessageExtractor(unmarshaller);
        setMessageConverters(Collections.singletonList(new MarshallingHttpMessageConverter(marshaller, unmarshaller)));
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public LhvMessage<HeartBeatResponse> getHeartbeat() {
        RequestCallback requestCallback = httpEntityCallback(getLhvRequestEntity(HttpEntity.EMPTY));
        @SuppressWarnings("unchecked")
        LhvMessage<HeartBeatResponse> message =
                (LhvMessage<HeartBeatResponse>)execute(LhvConnectService.API.HEARTBEAT.getUrl(), HttpMethod.GET, requestCallback, messageExtractor);
        return message;
    }

    @Override
    public LhvMessage<?> postForMessageRequestId(JAXBElement<Document> accountStatementRequestDocument) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(getLhvRequestEntity(accountStatementRequestDocument));
        return execute(LhvConnectService.API.ACCOUNT_STATEMENT.getUrl(), HttpMethod.POST, requestCallback, messageExtractor);
    }

    @Override
    public Optional<LhvMessage<?>> getNextMessage() {
        RequestCallback requestCallback = httpEntityCallback(getLhvRequestEntity(HttpEntity.EMPTY));
        return Optional.ofNullable(execute(LhvConnectService.API.MESSAGES.getUrl("next"), HttpMethod.GET, requestCallback, messageExtractor));
    }

    @Override
    public LhvMessage<?> deleteMessage(LhvMessage<?> message) {
        Assert.hasText(message.getMessageResponseId());
        String url = LhvConnectService.API.MESSAGES.getUrl(message.getMessageResponseId());
        RequestCallback requestCallback = httpEntityCallback(getLhvRequestEntity(HttpEntity.EMPTY));
        return execute(url, HttpMethod.DELETE, requestCallback, messageExtractor);
    }

    public static Jaxb2Marshaller createMarshaller(String... packagesToScan) {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan(packagesToScan);
        marshaller.setSupportJaxbElementClass(true);

        HashMap<String, Object> props = new HashMap<>();
        props.put(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        props.put(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.setMarshallerProperties(props);

        return marshaller;
    }

    private static HttpEntity<?> getLhvRequestEntity(Object requestBody) {
        if (requestBody instanceof HttpEntity) {
            return (HttpEntity<?>)requestBody;
        }
        return new HttpEntity<>(requestBody);
    }

    /**
     * Response extractor that extracts the response to {@link LhvMessage}.
     */
    @Setter
    private static class LhvMessageExtractor implements ResponseExtractor<LhvMessage<?>> {

        private final Jaxb2Marshaller marshaller;

        public LhvMessageExtractor(Jaxb2Marshaller unmarshaller) {
            this.marshaller = unmarshaller;
        }

        @Override
        public LhvMessage<?> extractData(ClientHttpResponse response) throws IOException {
            if (HttpStatus.NO_CONTENT.equals(response.getStatusCode()) || isHtmlContentType(response)) {
                return null;
            }

            LhvMessage<Object> message = new LhvMessage<>();
            HttpHeaders headers = response.getHeaders();
            message.setStatusCode(response.getStatusCode());
            message.setMessageRequestId(headers.getFirst(LhvConnectService.API.HEADER_REQUEST_ID));
            message.setMessageResponseId(headers.getFirst(LhvConnectService.API.HEADER_RESPONSE_ID));
            if (!LhvErrorHandler.isErrorCode(response.getStatusCode()) && hasContent(response)) {
                try {
                    message.setEntity(marshaller.unmarshal(new StreamSource(response.getBody())));
                } catch (UnmarshallingFailureException e) {
                    log.error("Unmarshalling failed. " + e.getMessage());
                }
            }
            return message;
        }

        private boolean isHtmlContentType(ClientHttpResponse response) {
            try {
                return MediaType.TEXT_HTML.equals(response.getHeaders().getContentType());
            } catch (InvalidMediaTypeException e) {
                return false;
            }
        }

        private boolean hasContent(ClientHttpResponse response) {
            return response.getHeaders().getContentLength() > 0;
        }
    }

    /**
     * Error handler for detecting abnormalities in LHV Connect service operations.
     */
    public static class LhvErrorHandler implements ResponseErrorHandler {

        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return isErrorCode(response.getStatusCode());
        }

        public static boolean isErrorCode(HttpStatus statusCode) {
            return HttpStatus.NOT_FOUND.equals(statusCode)
                    || HttpStatus.FORBIDDEN.equals(statusCode)
                    || HttpStatus.SERVICE_UNAVAILABLE.equals(statusCode)
                    || HttpStatus.INTERNAL_SERVER_ERROR.equals(statusCode);
        }

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            HttpStatus statusCode = response.getStatusCode();
            if (HttpStatus.FORBIDDEN.equals(statusCode)) {
                log.error("LHV Connect service unavailable! Authorization or authentication failure. "
                        + "Requested service is not stated in Connect agreement, Connect agreement is not valid or other failure.");
            } else if (HttpStatus.SERVICE_UNAVAILABLE.equals(statusCode)
                    || HttpStatus.INTERNAL_SERVER_ERROR.equals(statusCode)) {
                log.error("LHV Connect service unavailable! Technical error.");
            } else if (HttpStatus.NOT_FOUND.equals(statusCode)) {
                log.error("Resource not found, check the request! Headers: {}", response.getHeaders().toString());
            }
        }
    }

}
