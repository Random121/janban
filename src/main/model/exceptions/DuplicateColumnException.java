package model.exceptions;

// This exception is thrown when there is a duplicate column in the kanban board.
public class DuplicateColumnException extends Exception {
    public DuplicateColumnException(String message) {
        super(message);
    }
}
