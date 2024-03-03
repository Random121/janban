package model.exceptions;

// This exception is thrown when there is a duplicate column in the kanban board.
public class DuplicateColumnException extends Exception {
    public DuplicateColumnException(String columnName) {
        super("A column with the name '" + columnName + "' already exists");
    }
}
