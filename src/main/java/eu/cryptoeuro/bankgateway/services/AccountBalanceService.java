package eu.cryptoeuro.bankgateway.services;

import eu.cryptoeuro.bankgateway.services.balance.BalanceDao;
import eu.cryptoeuro.bankgateway.services.balance.model.Balance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class AccountBalanceService {
  @Autowired
	BalanceDao balanceDao;

	public Balance getCurrentBalance() {
		return balanceDao.findLatest();
	}
}
