package eu.cryptoeuro.bankgateway.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class AccountBalanceService {

	public BigDecimal getCurrentBalance() {
		return new BigDecimal(1000);
	}
}
