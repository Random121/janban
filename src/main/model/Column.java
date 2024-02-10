package model;

import model.exceptions.InvalidColumnNameException;

import java.util.*;

// This class represents a column within a Kanban Board.
// It organizes and stores Cards that are under the same stage
// of a workflow.
public class Column {
    private static final int MAX_NAME_LENGTH = 25;

    private String name;
    private final List<Card> cards;

    // EFFECTS: constructs a new Column with a name and no cards
    public Column(String name) throws InvalidColumnNameException {
        assertColumnNameValid(name);

        this.name = name;
        this.cards = new ArrayList<>();
    }

    // MODIFIES: this
    // EFFECTS: adds a card to this column
    public void addCard(Card card) {
        cards.add(card);
        card.setContainingColumn(this);
    }

    // MODIFIES: this
    // EFFECTS: removes a card from this column
    public void removeCard(Card card) {
        cards.remove(card);
        card.setContainingColumn(null);
    }

    public List<Card> getCards() {
        return cards;
    }

    // EFFECTS: gets all the cards within this column
    //          that matches at least one of the keywords
    //          sorted by relevancy, if there are no specified
    //          keywords then all cards are returned
    public List<Card> getCardsWithQuery(Set<String> keywords) {
        ArrayList<Card> results = new ArrayList<>();
        HashMap<Card, Integer> relevancyMapping = new HashMap<>();

        for (Card card : cards) {
            int relevancyScore = card.getQueryRelevancyScore(keywords);

            // Don't include cards which are not relevant
            if (relevancyScore == 0) {
                continue;
            }

            results.add(card);
            relevancyMapping.put(card, relevancyScore);
        }

        // Sort the cards by relevancy
        results.sort(Comparator.comparing(relevancyMapping::get));

        return results;
    }

    // EFFECTS: get all cards within this column
    //          that is of the specified type
    public List<Card> getCardsOfType(CardType type) {
        ArrayList<Card> results = new ArrayList<>();

        for (Card card : cards) {
            if (card.getType() == type) {
                results.add(card);
            }
        }

        return results;
    }

    public String getName() {
        return name;
    }

    // EFFECTS: sets the name of this column
    //          with a max length of MAX_NAME_LENGTH
    public void setName(String name) throws InvalidColumnNameException {
        assertColumnNameValid(name);
        this.name = name;
    }

    // EFFECTS: gets the total story points of
    //          all cards within this column
    public int getTotalStoryPoints() {
        int storyPoints = 0;

        for (Card card : cards) {
            storyPoints += card.getStoryPoints();
        }

        return storyPoints;
    }

    private void assertColumnNameValid(String name) throws InvalidColumnNameException {
        if (name.isBlank()) {
            throw new InvalidColumnNameException("Column name must not be empty");
        }

        if (name.length() > MAX_NAME_LENGTH) {
            throw new InvalidColumnNameException("Column name must not be longer than "
                                                 + MAX_NAME_LENGTH
                                                 + " characters. Given '" + name + "'");
        }
    }
}
