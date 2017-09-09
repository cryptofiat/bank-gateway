package eu.cryptoeuro.bankgateway.jobs;

import eu.cryptoeuro.bankgateway.services.lhv.LhvConnectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BankJobs {
	@Autowired
	LhvConnectService lhvService;

	@Scheduled(fixedRate = 10 * 1000)
	public void heartbeat() {
		if(lhvService.checkLhvHeartbeat()) {
			log.info("LHV Connect is alive");
		} else {
			log.error("LHV Connect heartbeat failed");
		}
	}

	@Scheduled(fixedRate = 60 * 60 * 1000)
	public void requestStatement() {
		lhvService.postAccountStatementRequest();

	}

	@Scheduled(fixedDelay = 30 * 1000)
	public void fetchMessages() {
		lhvService.processLhvMessages();
	}
}
