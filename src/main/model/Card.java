package model;

import model.exceptions.EmptyCardTitleException;
import model.exceptions.NegativeStoryPointsException;

import java.util.Set;

// This class represents the most basic unit of organization
// within a Kanban Board. It stores information relating to
// a specific goal/task.
public class Card {
    private String title;
    private String description;

    private String assignee;

    private CardType type;
    private Set<String> tags;
    private int storyPoints;

    // Column which contains the current card,
    // this information is useful when moving
    // the card.
    private Column containingColumn;

    // EFFECTS: constructs a new Card with a give title, description,
    //          assignee, type, tags, story points, and no containing column.
    //          throws an EmptyCardTitleException if the title is empty.
    //          throws an NegativeStoryPointsException if the story point amount is negative.
    public Card(String title,
                String description,
                String assignee,
                CardType type,
                Set<String> tags,
                int storyPoints) throws NegativeStoryPointsException, EmptyCardTitleException {
        assertCardTitleNotEmpty(title);
        assertStoryPointsNotNegative(storyPoints);

        this.title = title;
        this.description = description;
        this.assignee = assignee;
        this.type = type;
        this.tags = tags;
        this.storyPoints = storyPoints;
        this.containingColumn = null;
    }

    // EFFECTS: gets how relevant this card is to a query containing
    //          certain keywords, higher score means more relevant
    //          with 0 being not relevant at all
    public int getQueryRelevancyScore(Set<String> keywords) {
        int score = 0;

        String normalizedTitle = title.toLowerCase();
        String normalizedDescription = description.toLowerCase();

        for (String keyword : keywords) {
            String normalizedKeyword = keyword.toLowerCase();

            if (normalizedTitle.contains(normalizedKeyword)) {
                score++;
            }

            if (normalizedDescription.contains(normalizedKeyword)) {
                score++;
            }

            if (tags.contains(normalizedKeyword)) {
                score++;
            }
        }

        return score;
    }

    public String getTitle() {
        return title;
    }

    // EFFECTS: sets the title of the current card.
    //          throws an EmptyCardTitleException if the title is empty.
    public void setTitle(String title) throws EmptyCardTitleException {
        assertCardTitleNotEmpty(title);
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public int getStoryPoints() {
        return storyPoints;
    }

    // EFFECTS: sets the story point estimate for this card.
    //          throws NegativeStoryPointsException if the story point amount is negative.
    public void setStoryPoints(int storyPoints) throws NegativeStoryPointsException {
        assertStoryPointsNotNegative(storyPoints);
        this.storyPoints = storyPoints;
    }

    public Column getContainingColumn() {
        return containingColumn;
    }

    public void setContainingColumn(Column containingColumn) {
        this.containingColumn = containingColumn;
    }

    private void assertCardTitleNotEmpty(String title) throws EmptyCardTitleException {
        if (title.isBlank()) {
            throw new EmptyCardTitleException("Card title must not be empty");
        }
    }

    private void assertStoryPointsNotNegative(int storyPoints) throws NegativeStoryPointsException {
        if (storyPoints < 0) {
            throw new NegativeStoryPointsException("Story points must be positive");
        }
    }
}
