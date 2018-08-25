package eu.cryptoeuro.service.exception;

public class AccountNotApprovedException extends RuntimeException {

    public AccountNotApprovedException() {
        super("Source account not approved");
    }

}
