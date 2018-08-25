package eu.cryptoeuro.bankgateway.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.cryptoeuro.accountIdentity.response.Account;
import eu.cryptoeuro.accountIdentity.response.AccountsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.net.URL;

@Component
@Slf4j
public class AccountIdentityService {

    private String accountIdentityServer = "http://id.euro2.ee:8080"; // account-identity node on AWS

    public Account getAddress(String idCode) {
        ObjectMapper mapper = new ObjectMapper();
        AccountsResponse accountsResponse = null;
        log.info("Sending account info call to account-identity service");
        try {
            accountsResponse = mapper.readValue(new URL(accountIdentityServer + "/v1/accounts?ownerId=" + idCode), AccountsResponse.class);
        } catch (Exception e) {
            log.error("Failed loading account data from account-identity", e);
            // TODO: Figure out the reason and throw Validation exception if needed
            throw new RuntimeException(e);
        }
        log.info("... account info query done");

        Account account = accountsResponse.getAccounts().get(0);

        return account;
    }
}
