package eu.cryptoeuro.accountIdentity.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AccountsResponse {
	List<Account> accounts;

}
