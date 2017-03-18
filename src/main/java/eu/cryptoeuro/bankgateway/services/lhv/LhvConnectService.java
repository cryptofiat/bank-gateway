package eu.cryptoeuro.bankgateway.services.lhv;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.removeStart;

import lombok.Getter;

/**
 * Service interface for communicating with LHV Connect API.
 *
 * @author Erko Hansar
 * @author Kaarel Jõgeva
 */
public interface LhvConnectService {

    /**
     * Checks if LHV Connect service is available and operational.
     *
     * @return true if service is responsive
     */
    boolean checkLhvHeartbeat();

    /**
     * Posts an account statement request for our reserve bank account.
     */
    void postAccountStatementRequest();

    /**
     * Processes all available messages from LHV CONNECT message queue.
     */
    void processLhvMessages();

    /**
     * API description for LHV CONNECT service
     *
     * @see <a href="http://partners.lhv.ee/en/connect/">Specification for LHV CONNECT</a>
     */
    @Getter
    enum API {
        HEARTBEAT("https://connect.lhv.eu/heartbeat"),
        ACCOUNT_STATEMENT("https://connect.lhv.eu/account-statement"),
        MESSAGES("https://connect.lhv.eu/messages");

        public static final String HEADER_REQUEST_ID = "Message-Request-Id";
        public static final String HEADER_RESPONSE_ID = "Message-Response-Id";

        private final String url;

        API(String serviceUrl) {
            this.url = serviceUrl;
        }

        public String getUrl(String appendPath) {
            if (isBlank(appendPath)) {
                return this.url;
            }
            return this.url + "/" + removeStart(appendPath, "/");
        }
    }

}
