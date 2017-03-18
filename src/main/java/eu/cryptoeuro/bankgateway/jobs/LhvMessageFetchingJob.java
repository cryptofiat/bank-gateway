package eu.cryptoeuro.bankgateway.jobs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LhvMessageFetchingJob {

	@Scheduled(cron = "0 */5 * * * *")
	public void fetchMessages() {
		log.info("Pretending to fetch messages");
	}
}
