package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// This class represents a column within a Kanban Board.
// It organizes and stores Cards that are under the same stage
// of a workflow.
public class Column {
    private static int MAX_NAME_LENGTH = 25;

    private String name;
    private int cardLimit;
    private List<Card> cards;

    // MODIFIES: this
    // EFFECTS: adds a card to this column
    public void addCard(Card card) {
        // TODO
        // NOTE: also remember to set the parent of the card
    }

    // MODIFIES: this
    // EFFECTS: removes a card from this column
    public void removeCard(Card card) {
        // TODO
    }

    // EFFECTS: gets all the cards within this column
    //          that matches at least one of the keywords
    //          sorted by relevancy, if there are no specified
    //          keywords then all cards are returned
    public List<Card> getCardsWithQuery(Set<String> keywords) {
        // TODO
        return null;
    }

    // EFFECTS: get all cards within this column
    //          that is of the specified type
    public List<Card> getCardsOfType(CardType type) {
        // TODO
        return null;
    }

    public String getName() {
        return name;
    }

    // EFFECTS: sets the name of this column
    //          with a max length of MAX_NAME_LENGTH
    public void setName(String name) {
        // TODO: add name length check
        this.name = name;
    }

    // MODIFIES: this
    // EFFECTS: sets the expected max number of cards
    //          this column can hold (set to 0 for no limit),
    //          the limit must be a non-negative integer
    public void setCardLimit(int limit) {
        // TODO
    }

    // EFFECTS: returns whether this column is currently
    //          holding more cards than its expected limit
    public boolean isAboveCardLimit() {
        // TODO
        return false;
    }

    // EFFECTS: gets the total story points of
    //          all cards within this column
    public int getTotalStoryPoints() {
        // TODO
        return 0;
    }
}
