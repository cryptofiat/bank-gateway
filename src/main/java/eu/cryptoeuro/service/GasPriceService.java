package eu.cryptoeuro.service;

import eu.cryptoeuro.service.rpc.EthereumRpcMethod;
import eu.cryptoeuro.service.rpc.JsonRpcCall;
import eu.cryptoeuro.service.rpc.JsonRpcStringResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Slf4j
public class GasPriceService extends BaseService {

    // https://etherconverter.online/
    @Value("${fixed.gas.price.gwei:#{null}}")
    private String fixedGasPriceGwei;

    public Long getGasPriceInWei() {
        if (fixedGasPriceGwei != null) {
            Long fixedGasPriceWei = Math.round(Double.parseDouble(fixedGasPriceGwei) * 1e9);
            log.info("Gas price fixed: " + fixedGasPriceWei);
            return fixedGasPriceWei;
        }

        JsonRpcCall call = new JsonRpcCall(EthereumRpcMethod.gasPrice, Arrays.asList());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> request = new HttpEntity<>(call.toString(), headers);

        JsonRpcStringResponse response = restTemplate.postForObject(URL, request, JsonRpcStringResponse.class);
        long networkGasPriceWei = Long.parseLong(HashUtils.without0x(response.getResult()), 16);
        log.info("Gas price retrieved: " + networkGasPriceWei);

        return networkGasPriceWei;
    }
}
