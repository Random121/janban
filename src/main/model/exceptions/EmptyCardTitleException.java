package model.exceptions;

// This exception is thrown whenever the card title is empty.
public class EmptyCardTitleException extends Exception {
    public EmptyCardTitleException(String message) {
        super(message);
    }
}
