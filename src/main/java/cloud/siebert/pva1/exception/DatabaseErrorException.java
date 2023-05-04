package cloud.siebert.pva1.exception;

public class DatabaseErrorException extends Exception {

    public DatabaseErrorException(String message) {
        super(message);
    }

    public DatabaseErrorException() {
        super("Error: Database Error.");
    }
}
