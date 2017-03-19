package eu.cryptoeuro.bankgateway.services.slack;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import eu.cryptoeuro.bankgateway.services.slack.json.Message;

@Service
@Transactional
@Slf4j
public class SlackServiceImpl implements SlackService {

    private RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

    @Value("${slack.webhook.reserve.url}")
    private String slackWebhookReserveUrl;

    @Transactional(readOnly = true)
    @Override
    public boolean sendReserveMessage(Message message) {
        if (StringUtils.isBlank(slackWebhookReserveUrl)) {
            log.warn("Failed to send new notification to Slack - webhook url is not configured!");
            return false;
        }

        return sendMessage(slackWebhookReserveUrl, message);
    }

    ///// PRIVATE METHODS /////

    private HttpHeaders prepareRequestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return headers;
    }

    private boolean sendMessage(String url, Message msg) {
        HttpEntity<Message> request = new HttpEntity<>(msg, prepareRequestHeaders());
        Map<String, String> params = new HashMap<>();
        try {
            String response = restTemplate.postForObject(url, request, String.class, params);
            log.debug("Sent a message to Slack: " + response);
            return true;
        } catch (HttpStatusCodeException e) {
            log.error("Failed to send a message to Slack: " + e.getResponseBodyAsString(), e);
            return false;
        }
    }

}
