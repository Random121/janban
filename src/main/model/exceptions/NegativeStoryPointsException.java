package model.exceptions;

public class NegativeStoryPointsException extends Exception {
    public NegativeStoryPointsException(String message) {
        super(message);
    }
}
