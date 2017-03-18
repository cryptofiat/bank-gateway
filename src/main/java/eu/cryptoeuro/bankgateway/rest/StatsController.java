package eu.cryptoeuro.bankgateway.rest;

import eu.cryptoeuro.bankgateway.services.AccountBalanceService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/v1")
@CrossOrigin(origins = "*")
@Slf4j
public class StatsController {
	@Autowired
	AccountBalanceService accountBalanceService;

	@ApiOperation(value = "Get reserve balance")
	@RequestMapping(
					method = GET,
					value = "/totalReserve")
	public ResponseEntity<TotalReserveResponse> totalReserve() {
		BigDecimal total = accountBalanceService.getCurrentBalance();
		return new ResponseEntity<TotalReserveResponse>(TotalReserveResponse.create(total), HttpStatus.OK);
	}

}
