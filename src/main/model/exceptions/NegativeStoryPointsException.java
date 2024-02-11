package model.exceptions;

// This exception is thrown whenever the specific story points is negative.
public class NegativeStoryPointsException extends Exception {
    public NegativeStoryPointsException(String message) {
        super(message);
    }
}
