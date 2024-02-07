package model;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

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

    public Card(String title,
                String description,
                String assignee,
                CardType type,
                Set<String> tags,
                int storyPoints) {
        this.title = title;
        this.description = description;
        this.assignee = assignee;
        this.type = type;
        this.tags = tags;
        this.storyPoints = storyPoints;
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

    public void setTitle(String title) {
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

    // EFFECTS: sets the story point estimate for this card,
    //          throws an exception if the story point is not a
    //          non-negative integer
    public void setStoryPoints(int storyPoints) throws IllegalArgumentException {
        if (storyPoints < 0) {
            throw new IllegalArgumentException("Story points must be positive");
        }

        this.storyPoints = storyPoints;
    }
}
