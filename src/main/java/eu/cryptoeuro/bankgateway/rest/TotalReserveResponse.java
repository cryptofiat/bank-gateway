package eu.cryptoeuro.bankgateway.rest;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TotalReserveResponse {

	BigDecimal balance;

	public static TotalReserveResponse create(BigDecimal balance) {
		return TotalReserveResponse.builder().balance(balance).build();
	}
}
