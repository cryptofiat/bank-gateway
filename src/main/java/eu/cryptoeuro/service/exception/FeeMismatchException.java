package eu.cryptoeuro.service.exception;

public class FeeMismatchException extends RuntimeException {

    public FeeMismatchException() {
        super("Submitted and signed fee amount doesn't match config");
    }

    public FeeMismatchException(long submittedFee, long configuredFee) {
        super("Submitted and signed fee " + String.valueOf(submittedFee) + " doesn't match configured " + String.valueOf(configuredFee) + ".");
    }
}
