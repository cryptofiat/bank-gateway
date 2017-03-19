package eu.cryptoeuro.bankgateway.rest;

import eu.cryptoeuro.bankgateway.services.balance.model.Balance;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class TotalReserveResponse {

	BigDecimal amount;
	Date date;

	public static TotalReserveResponse createFromBalance(Balance balance) {
		return TotalReserveResponse.builder().amount(balance.getBalance()).date(balance.getBalanceDate()).build();
	}
}
