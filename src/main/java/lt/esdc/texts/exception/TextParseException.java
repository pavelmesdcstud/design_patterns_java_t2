package lt.esdc.texts.exception;

public class TextParseException extends Exception {
    public TextParseException() {
        super();
    }

    public TextParseException(String message) {
        super(message);
    }

    public TextParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public TextParseException(Throwable cause) {
        super(cause);
    }
}
