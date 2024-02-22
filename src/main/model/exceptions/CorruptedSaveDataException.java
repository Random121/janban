package model.exceptions;

// This exception is thrown when the save data is found to be corrupted during loading.
public class CorruptedSaveDataException extends Exception {
    public CorruptedSaveDataException(Throwable cause) {
        super(cause);
    }
}
