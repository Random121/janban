package model.exceptions;

// This exception is thrown whenever the specific story points is negative.
public class NegativeStoryPointsException extends Exception {
    public NegativeStoryPointsException() {
        super("Story points must be positive");
    }
}
