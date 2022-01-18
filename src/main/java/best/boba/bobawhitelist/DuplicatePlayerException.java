package best.boba.bobawhitelist;

public class DuplicatePlayerException extends IllegalArgumentException {
    public DuplicatePlayerException() {}

    public DuplicatePlayerException(String message) {
        super(message);
    }

    public DuplicatePlayerException(Throwable cause) {
        super(cause);
    }

    public DuplicatePlayerException(String message, Throwable cause) {
        super(message, cause);
    }
}
