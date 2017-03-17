package com.kryptoeuro.bankgateway;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class BankGatewayApplication {
	public static void main(String[] args) {
		SpringApplication.run(BankGatewayApplication.class, args);
	}

	@RequestMapping(value = "/", produces = "text/plain")
	public String index() {
		return "OK";
	}

	@RequestMapping(value = "/statement", produces = "text/csv")
	public String statement() {

	}



}
