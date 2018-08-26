package eu.cryptoeuro.transferInfo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.cryptoeuro.transferInfo.command.TransferInfoRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class TransferInfoService {

    @Value("${transferInfo.server.url}")
    private String transferInfo;

    private ObjectMapper mapper = new ObjectMapper();
    private RestTemplate restTemplate = new RestTemplate();

    public TransferInfoRecord send(String blockHash, TransferInfoRecord transferInfoRecord) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        String json;
        try {
            json = mapper.writeValueAsString(transferInfoRecord);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        HttpEntity<Object> request = new HttpEntity<>(json, headers);

        log.info("Sending a spray to transfer info");
        log.info("JSON:\n" + request.toString());

        TransferInfoRecord response = restTemplate.postForObject(transferInfo+blockHash, request, TransferInfoRecord.class);
        return response;
    }
}
