package eu.cryptoeuro.bankgateway.services.slack;

import eu.cryptoeuro.bankgateway.services.slack.json.Message;

/**
 * Service interface for Slack integrations.
 *
 * @author Erko Hansar
 */
public interface SlackService {

    /**
     * Send a message to Slack reserve channel.
     *
     * @param message Slack message
     * @return true if message was successfully sent, false otherwise
     */
    boolean sendReserveMessage(Message message);

}
