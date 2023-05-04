package cloud.siebert.pva1.exception;

public class UserCheckException extends Exception {

    public UserCheckException(String message) {
        super(message);
    }

    public UserCheckException() {
        super("Error: User alredy exists.");
    }
}
