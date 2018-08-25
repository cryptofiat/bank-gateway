package eu.cryptoeuro.accountIdentity.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Account {
	private Long id;
	private String ownerId;
	private String address;
	private boolean activated;
	private String authorisationType;
}
