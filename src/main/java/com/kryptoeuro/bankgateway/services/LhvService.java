package com.kryptoeuro.bankgateway.services;

import com.google.api.client.http.*;
import com.google.api.client.http.apache.ApacheHttpTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class LhvService {
	@Value("${bank.lhv.url}")
	String url;

	@Value("${bank.lhv.loginPath}")
	String loginPath;

	@Value("${bank.lhv.username}")
	String username;

	@Value("${bank.lhv.password}")
	String password;

	public String getStatement() {

		ApacheHttpTransport transport = new ApacheHttpTransport();
		HttpRequestFactory rf = transport.createRequestFactory();
		try {
			Map<String,String> params = new HashMap<>();
			params.put("nickname", username);
			params.put("login_type", "PWD");
			params.put("password", password);
			params.put("mid_step", "1");
			params.put("i_submit", "1");
			params.put("goto", "/portfolio/view.cfm");
			HttpRequest postRequest = rf.buildPostRequest(new GenericUrl(url.concat(loginPath)), new UrlEncodedContent(params));
			HttpResponse response = postRequest.execute();
			System.out.println(response.getStatusCode());

		} catch (IOException e) {
			e.printStackTrace();
		}
		return "1;2;3";
	}

}
