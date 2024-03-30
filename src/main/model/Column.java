package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.JsonSerializable;

import java.util.*;

// This class represents a column within a Kanban Board.
// It organizes and stores Cards that are under the same stage
// of a workflow.
public class Column implements JsonSerializable {
    public static final String DEFAULT_COLUMN_NAME = "Unnamed column";

    private String name;
    private final List<Card> cards;

    // EFFECTS: constructs a new Column with a name (or DEFAULT_COLUMN_NAME if blank) and no cards
    public Column(String name) {
        this.name = !name.isBlank() ? name : DEFAULT_COLUMN_NAME;
        this.cards = new ArrayList<>();
    }

    // MODIFIES: this, card
    // EFFECTS: adds a card to this column
    public void addCard(Card card) {
        if (cards.contains(card)) {
            return;
        }

        {
            String eventDescription = String.format("Adding card '%s' to column '%s'", card.getTitle(), name);
            EventLog.getInstance().logEvent(new Event(eventDescription));
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

        {
            String eventDescription = String.format("Removing card '%s' from column '%s'", card.getTitle(), name);
            EventLog.getInstance().logEvent(new Event(eventDescription));
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

        {
            String eventDescription = String.format("Querying cards in column '%s' with keywords '%s' with %s results",
                                                    name,
                                                    String.join(",", keywords),
                                                    results.size());
            EventLog.getInstance().logEvent(new Event(eventDescription));
        }

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

        {
            String eventDescription = String.format("Querying cards of type '%s' in column '%s'", type, name);
            EventLog.getInstance().logEvent(new Event(eventDescription));
        }

        return results;
    }

    public String getName() {
        return name;
    }

    // MODIFIES: this
    // EFFECTS: sets the name of this column or DEFAULT_COLUMN_NAME if blank
    public void setName(String name) {
        this.name = !name.isBlank() ? name : DEFAULT_COLUMN_NAME;
    }

    // EFFECTS: gets the total story points of all cards within this column
    public int getTotalStoryPoints() {
        int storyPoints = 0;

        for (Card card : cards) {
            storyPoints += card.getStoryPoints();
        }

        return storyPoints;
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
