package model;

import model.exceptions.EmptyColumnNameException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.JsonSerializable;

import java.util.*;

// This class represents a column within a Kanban Board.
// It organizes and stores Cards that are under the same stage
// of a workflow.
public class Column implements JsonSerializable {
    private String name;
    private final List<Card> cards;

    // EFFECTS: constructs a new Column with a name and no cards.
    //          throws an EmptyColumnNameException if the column name is empty.
    public Column(String name) throws EmptyColumnNameException {
        assertColumnNameNotEmpty(name);

        this.name = name;
        this.cards = new ArrayList<>();
    }

    // MODIFIES: this, card
    // EFFECTS: adds a card to this column
    public void addCard(Card card) {
        if (cards.contains(card)) {
            return;
        }

        cards.add(card);
        card.setContainingColumn(this);
    }

    // MODIFIES: this, card
    // EFFECTS: removes a card from this column
    public void removeCard(Card card) {
        if (!cards.contains(card)) {
            return;
        }

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
        if (keywords.isEmpty()) {
            return cards;
        }

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

        // Sort the cards by relevancy in descending order
        results.sort(Comparator.comparing(relevancyMapping::get, Comparator.reverseOrder()));

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

    // MODIFIES: this
    // EFFECTS: sets the name of this column.
    //          throws an EmptyColumnNameException if the column name is empty.
    public void setName(String name) throws EmptyColumnNameException {
        assertColumnNameNotEmpty(name);
        this.name = name;
    }

    // EFFECTS: gets the total story points of all cards within this column
    public int getTotalStoryPoints() {
        int storyPoints = 0;

        for (Card card : cards) {
            storyPoints += card.getStoryPoints();
        }

        return storyPoints;
    }

    private void assertColumnNameNotEmpty(String name) throws EmptyColumnNameException {
        if (name.isBlank()) {
            throw new EmptyColumnNameException("Column name must not be empty");
        }
    }

    // EFFECTS: returns the JSON representation of this column and its cards
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("cards", cardsToJson());
        return json;
    }

    // EFFECTS: returns the JSON representation of all cards in this column
    private JSONArray cardsToJson() {
        JSONArray jsonArray = new JSONArray();

        for (Card card : cards) {
            jsonArray.put(card.toJson());
        }

        return jsonArray;
    }
}
