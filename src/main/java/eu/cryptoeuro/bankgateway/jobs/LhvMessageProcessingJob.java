package eu.cryptoeuro.bankgateway.jobs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LhvMessageProcessingJob {

	@Scheduled(cron = "0 */10 * * * *")
	public void processMessages() {
		log.info("Pretending to process messages");
	}
}
