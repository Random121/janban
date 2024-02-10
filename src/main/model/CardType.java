package model;

// Represents all the different types of Cards.
public enum CardType {
    USER_STORY("User Story"),
    TASK("Task"),
    ISSUE("Issue");

    private final String stringRepr;

    CardType(String stringRepr) {
        this.stringRepr = stringRepr;
    }

    @Override
    public String toString() {
        return this.stringRepr;
    }
}
