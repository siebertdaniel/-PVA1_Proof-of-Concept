package cloud.siebert.pva1.exception;

public class WrongPasswordException extends Exception {

    public WrongPasswordException(String message) {
        super(message);
    }

    public WrongPasswordException() {
        super("Error: Wrong Password.");
    }
}
