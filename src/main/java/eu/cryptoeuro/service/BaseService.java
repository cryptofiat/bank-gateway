package eu.cryptoeuro.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import eu.cryptoeuro.service.rpc.JsonRpcCallMap;

@Slf4j
public abstract class BaseService {

    @Value("${ethereum.node.url}")
    protected String URL;

    protected RestTemplate restTemplate = new RestTemplate();

    protected <T> T getCallResponseForObject(JsonRpcCallMap call, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> request = new HttpEntity<String>(call.toString(), headers);

        log.info("Sending call to: " + URL);
        log.info("JSON:\n" + call.toString());
        T response = restTemplate.postForObject(URL, request, responseType);
        log.info("Call response: " + response.toString());

        return response;
    }
}
