package eu.cryptoeuro.bankgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@ComponentScan("eu.cryptoeuro")
public class BankGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankGatewayApplication.class, args);
    }

    @RequestMapping(value = "/", produces = "text/plain")
    public String index() {
        return "OK";
    }

}
