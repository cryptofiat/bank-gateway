package eu.cryptoeuro.bankgateway.jobs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LhvStatementRequestingJob {

	@Scheduled(cron = "0 42 * * * *")
	public void requestStatement() {
		log.info("Pretending to request statement");
	}
}
