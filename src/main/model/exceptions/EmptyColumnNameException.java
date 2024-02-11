package model.exceptions;

// This exception is thrown whenever the column name is empty.
public class EmptyColumnNameException extends Exception {
    public EmptyColumnNameException(String message) {
        super(message);
    }
}
