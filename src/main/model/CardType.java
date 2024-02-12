package model;

// Represents all the different types of Cards.
public enum CardType {
    USER_STORY("User Story"),
    TASK("Task"),
    ISSUE("Issue");

    private final String readableValue;

    CardType(String readableValue) {
        this.readableValue = readableValue;
    }

    @Override
    public String toString() {
        return this.readableValue;
    }
}
