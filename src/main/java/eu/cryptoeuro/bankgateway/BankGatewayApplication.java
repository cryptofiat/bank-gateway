package eu.cryptoeuro.bankgateway;

import eu.cryptoeuro.bankgateway.services.LhvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class BankGatewayApplication {
	@Autowired
	LhvService service;

	public static void main(String[] args) {
		SpringApplication.run(BankGatewayApplication.class, args);
	}

	@RequestMapping(value = "/", produces = "text/plain")
	public String index() {
		return "OK";
	}

	@RequestMapping(value = "/statement", produces = "text/csv")
	public String statement() {
		return service.getStatement();
	}



}
