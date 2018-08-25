package eu.cryptoeuro.accountIdentity.response;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LdapResponse {
    private Long id;
    private Long idCode;
    private String firstName;
    private String lastName;

}
