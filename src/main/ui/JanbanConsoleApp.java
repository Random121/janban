package ui;

import model.*;
import model.exceptions.*;

import java.util.*;

// This class represents the console user interface for the Janban app.
// Contains all the logic for processing and displaying the app.
public class JanbanConsoleApp implements RunnableApp {
    private final ArrayList<Project> projects;
    private Project currentProject;
    private KanbanBoard currentKanbanBoard;

    public JanbanConsoleApp() {
        projects = new ArrayList<>();
    }

    // EFFECTS: runs the Janban console app, starting
    //          from the projects menu
    public void run() {
        System.out.println("====================================");
        System.out.println("Janban: A kanban board made in Java!");
        System.out.println("====================================");

        do {
            displayProjectsMenu();
        } while (processProjectsMenuInput());

        ConsoleHelper.newLine();
        System.out.println("===================================");
        System.out.println("Exiting... Thanks for using Janban!");
        System.out.println("===================================");
    }

    public void initTesting() {
        try {
            projects.add(new Project("Test Project", "This is a test description", "Done"));
        } catch (DuplicateColumnException | EmptyColumnNameException e) {
            throw new RuntimeException(e);
        }
    }

    //
    // Project menu section
    //

    private void displayProjectsMenu() {
        displayCreatedProjects();
        displayProjectMenuOptions();
    }

    private void displayCreatedProjects() {
        displayMenuStart("Created Projects");

        if (!projects.isEmpty()) {
            for (int index = 0; index < projects.size(); index++) {
                ConsoleHelper.newLine();

                Project project = projects.get(index);
                System.out.println("(" + index + ") " + project.getName());
                System.out.println("\t- " + project.getDescription());
            }
        } else {
            System.out.println("You haven't created any projects yet!");
        }

        displayMenuEnd(false);
    }

    private void displayProjectMenuOptions() {
        ConsoleHelper.newLine();
        System.out.println("Project Commands:");
        System.out.println("\t'a' -> Add a new project");
        System.out.println("\t's' -> Select an existing project");
        System.out.println("\t'q' -> Quit the application");
        ConsoleHelper.newLine();
    }

    // EFFECTS: gets and processes user input for the projects menu
    //          returns whether the menu should be active
    private boolean processProjectsMenuInput() {
        String command = ConsoleHelper.takeStringInput("Please enter a command: ").toLowerCase();

        switch (command) {
            case "a":
                launchNewProjectWizard();
                break;
            case "s":
                launchKanbanBoardMenu();
                break;
            case "q":
                return false;
            default:
                displayUnknownCommandError();
                break;
        }

        return true;
    }

    private void launchNewProjectWizard() {
        displayMenuStart("New Project Wizard");

        String name = ConsoleHelper.takeStringInput("Enter a project name: ");
        String description = ConsoleHelper.takeStringInput("Enter a project description: ");
        String completedColumnName = ConsoleHelper.takeStringInput(
                "Enter the name of the completed column (default: 'Done'): ");

        completedColumnName = completedColumnName.isBlank() ? "Done" : completedColumnName;

        ConsoleHelper.newLine();

        try {
            Project newProject = new Project(name, description, completedColumnName);

            projects.add(newProject);

            System.out.println("Added a new project: " + name);
        } catch (DuplicateColumnException | EmptyColumnNameException e) {
            System.out.println("Failed to add new project: " + e.getMessage());
        }

        displayMenuEnd(true);
    }

    //
    // Kanban board menu section
    //

    private void launchKanbanBoardMenu() {
        int projectIndex = ConsoleHelper.takeIntInput("Enter the index of the project: ");

        if (!validIndex(projects, projectIndex)) {
            System.out.println("Invalid project index");
            return;
        }

        Project selectedProject = projects.get(projectIndex);

        currentProject = selectedProject;
        currentKanbanBoard = selectedProject.getKanbanBoard();

        do {
            displayKanbanBoard(currentKanbanBoard);
            displayKanbanBoardMenuOptions();
        } while (processKanbanBoardMenuInput());
    }

    private void displayKanbanBoard(KanbanBoard board) {
        displayMenuStart("Kanban Board for Project '" + currentProject.getName() + "'");

        displayColumns(board.getColumns(), board.getCompletedColumn());

        displayMenuEnd(false);
    }

    private void displayColumns(List<Column> columns, Column completedColumn) {
        for (int index = 0; index < columns.size(); index++) {
            ConsoleHelper.newLine();

            Column column = columns.get(index);
            List<Card> cards = column.getCards();

            System.out.println("(" + index + ") " + column.getName() + (column == completedColumn ? " ✅" : ""));

            if (!cards.isEmpty()) {
                displayColumnCards(cards);
            } else {
                System.out.println("\t- There are no cards in this column!");
            }
        }
    }

    private void displayColumns(List<Column> columns, Set<String> keywords) {
        for (int index = 0; index < columns.size(); index++) {
            ConsoleHelper.newLine();

            Column column = columns.get(index);
            List<Card> cards = column.getCardsWithQuery(keywords);

            System.out.println("(" + index + ") " + column.getName());

            if (!cards.isEmpty()) {
                displayColumnCards(cards);
            } else {
                System.out.println("\t- There are no cards in this column!");
            }
        }
    }

    private void displayColumns(List<Column> columns, CardType type) {
        for (int index = 0; index < columns.size(); index++) {
            ConsoleHelper.newLine();

            Column column = columns.get(index);
            List<Card> cards = column.getCardsOfType(type);

            System.out.println("(" + index + ") " + column.getName());

            if (!cards.isEmpty()) {
                displayColumnCards(cards);
            } else {
                System.out.println("\t- There are no cards in this column!");
            }
        }
    }

    private void displayColumnCards(List<Card> cards) {
        for (int index = 0; index < cards.size(); index++) {
            Card card = cards.get(index);
            Set<String> cardTags = card.getTags();

            System.out.println("\t- (" + index + ") " + card.getTitle());
            System.out.println("\t\t- Type: " + card.getType());
            System.out.println("\t\t- Story points: " + card.getStoryPoints());
            System.out.println("\t\t- Assignee: " + card.getAssignee());
            System.out.println("\t\t- Tags: " + String.join(", ", cardTags));
            System.out.println("\t\t- Description: " + card.getDescription());
        }
    }

    private void displayKanbanBoardMenuOptions() {
        ConsoleHelper.newLine();
        System.out.println("Kanban Board Commands:");
        System.out.println("\t'k' -> View column commands");
        System.out.println("\t'c' -> View card commands");
        System.out.println("\t'f' -> View filtering commands");
        System.out.println("\t'v' -> View statistics about the kanban board");
        System.out.println("\t'b' -> Go back");
        ConsoleHelper.newLine();
    }

    // EFFECTS: takes and processes the input for the kanban board action menu.
    //          returns whether the current menu should be active
    private boolean processKanbanBoardMenuInput() {
        String command = ConsoleHelper.takeStringInput("Please enter a command: ").toLowerCase();

        switch (command) {
            case "k":
                displayColumnActionMenu();
                break;
            case "c":
                displayCardActionMenu();
                break;
            case "f":
                displayFilteringMenu();
                break;
            case "v":
                displayKanbanBoardStats();
                break;
            case "b":
                return false;
            default:
                displayUnknownCommandError();
                break;
        }

        return true;
    }

    //
    // Column menu section
    //

    private void displayColumnActionMenu() {
        do {
            displayColumnActionMenuOptions();
        } while (processColumnActionMenuInput());
    }

    private void displayColumnActionMenuOptions() {
        ConsoleHelper.newLine();
        System.out.println("Column Commands:");
        System.out.println("\t'a' -> Add a new column");
        System.out.println("\t'e' -> Edit a column");
        System.out.println("\t'd' -> Delete a column");
        System.out.println("\t'b' -> Go back");
        ConsoleHelper.newLine();
    }

    // EFFECTS: takes and processes the input for the column action menu.
    //          returns whether the current menu should be active
    private boolean processColumnActionMenuInput() {
        String command = ConsoleHelper.takeStringInput("Please enter a command: ").toLowerCase();

        switch (command) {
            case "a":
                launchNewColumnWizard();
                break;
            case "e":
                launchEditColumnWizard();
                break;
            case "d":
                launchDeleteColumnWizard();
                break;
            case "b":
                return false;
            default:
                displayUnknownCommandError();
                break;
        }

        return true;
    }

    private void launchNewColumnWizard() {
        displayMenuStart("New Column Wizard");

        String name = ConsoleHelper.takeStringInput("Enter a column name: ");

        ConsoleHelper.newLine();

        try {
            Column newColumn = new Column(name);

            try {
                currentKanbanBoard.addColumn(newColumn);
                System.out.println("Added a new column: " + name);
            } catch (DuplicateColumnException e) {
                System.out.println("Failed to add new column: " + e.getMessage());
            }
        } catch (EmptyColumnNameException e) {
            System.out.println("Failed to add new column: " + e.getMessage());
        }

        displayMenuEnd(true);
    }

    private void launchEditColumnWizard() {
        displayMenuStart("Edit Column Wizard");

        int columnIndex = ConsoleHelper.takeIntInput("Enter the index of the column: ");
        List<Column> columns = currentKanbanBoard.getColumns();

        if (validIndex(columns, columnIndex)) {
            Column column = columns.get(columnIndex);

            String newName = ConsoleHelper.takeStringInput("Enter a new name for the column: ");

            ConsoleHelper.newLine();

            try {
                currentKanbanBoard.editColumnName(column, newName);

                System.out.println("Edited the column name to '" + newName + "'");
            } catch (DuplicateColumnException | EmptyColumnNameException e) {
                System.out.println("Failed to edit the column: " + e.getMessage());
            }
        } else {
            System.out.println("Invalid column index");
        }

        displayMenuEnd(true);
    }

    private void launchDeleteColumnWizard() {
        displayMenuStart("Delete Column Wizard");

        int columnIndex = ConsoleHelper.takeIntInput("Enter the index of the column: ");
        List<Column> columns = currentKanbanBoard.getColumns();

        if (validIndex(columns, columnIndex)) {
            Column column = columns.get(columnIndex);

            currentKanbanBoard.removeColumn(column);

            ConsoleHelper.newLine();

            System.out.println("Deleted the column '" + column.getName() + "'.");
        } else {
            System.out.println("Invalid column index");
        }

        displayMenuEnd(true);
    }

    //
    // Card menu section
    //

    private void displayCardActionMenu() {
        do {
            displayCardActionMenuOptions();
        } while (processCardActionMenuInput());
    }

    private void displayCardActionMenuOptions() {
        ConsoleHelper.newLine();
        System.out.println("Card Commands:");
        System.out.println("\t'a' -> Add a new card (always added to the leftmost column)");
        System.out.println("\t'e' -> Edit a card");
        System.out.println("\t'd' -> Delete a card");
        System.out.println("\t'b' -> Go back");
        ConsoleHelper.newLine();
    }

    // EFFECTS: takes and processes the input for the card action menu.
    //          returns whether the current menu should be active
    private boolean processCardActionMenuInput() {
        String command = ConsoleHelper.takeStringInput("Please enter a command: ").toLowerCase();

        switch (command) {
            case "a":
                launchNewCardWizard();
                break;
            case "e":
                launchEditCardWizard();
                break;
            case "d":
                launchDeleteCardWizard();
                break;
            case "b":
                return false;
            default:
                displayUnknownCommandError();
                break;
        }

        return true;
    }

    private void launchNewCardWizard() {
        displayMenuStart("New Card Wizard");

        if (!currentKanbanBoard.getColumns().isEmpty()) {
            processAddNewCard();
            System.out.println("Added a new card");
        } else {
            System.out.println("Cannot create a card when there are no columns");
        }

        displayMenuEnd(true);
    }

    private void processAddNewCard() {
        String title = ConsoleHelper.takeStringInput("Enter a title: ");
        String description = ConsoleHelper.takeStringInput("Enter a description: ");
        String assignee = ConsoleHelper.takeStringInput("Enter an assignee: ");
        String typeString = ConsoleHelper.takeStringInput("Enter a type (story/task/issue): ");
        String tags = ConsoleHelper.takeStringInput("Enter some comma separated tags: ");
        int storyPoints = ConsoleHelper.takeIntInput("Enter the number of story points: ");

        ConsoleHelper.newLine();

        CardType type = parseTypeFromString(typeString);

        if (type == null) {
            System.out.println("Failed to create a card: Card type can only be one of story, task, and issue");
            return;
        }

        try {
            Card newCard = new Card(title, description, assignee, type, parseKeywordsFromString(tags), storyPoints);
            Column firstColumn = currentKanbanBoard.getColumn(0);

            currentKanbanBoard.moveCard(newCard, firstColumn);
        } catch (NegativeStoryPointsException | EmptyCardTitleException e) {
            System.out.println("Failed to create a card: " + e.getMessage());
        }
    }

    private void launchEditCardWizard() {
        displayMenuStart("Edit Card Wizard");

        int columnIndex = ConsoleHelper.takeIntInput("Enter the index of the column: ");
        List<Column> columns = currentKanbanBoard.getColumns();

        if (!validIndex(columns, columnIndex)) {
            System.out.println("Invalid column index");
            displayMenuEnd(true);
            return;
        }

        Column column = columns.get(columnIndex);

        int cardIndex = ConsoleHelper.takeIntInput("Enter the index of the card: ");
        List<Card> cards = column.getCards();

        if (!validIndex(cards, cardIndex)) {
            System.out.println("Invalid card index");
            displayMenuEnd(true);
            return;
        }

        Card card = cards.get(cardIndex);

        ConsoleHelper.newLine();

        processEditCard(card);

        displayMenuEnd(true);
    }

    private void processEditCard(Card card) {
        editCardStringOptions(card);
        editCardType(card);
        editStoryPoints(card);
        editCardColumn(card);
    }

    private void editCardStringOptions(Card card) {
        String title = ConsoleHelper.takeStringInput("Enter a new title (put nothing to skip): ");

        if (!title.isBlank()) {
            try {
                card.setTitle(title);
            } catch (EmptyCardTitleException e) {
                System.out.println("Ignoring changes: " + e.getMessage());
            }
        }

        String description = ConsoleHelper.takeStringInput("Enter a new description (put nothing to skip): ");

        if (!description.isBlank()) {
            card.setDescription(description);
        }

        String assignee = ConsoleHelper.takeStringInput("Enter a new assignee (put nothing to skip): ");

        if (!assignee.isBlank()) {
            card.setAssignee(assignee);
        }

        String tags = ConsoleHelper.takeStringInput("Enter some comma separated tags (put nothing to skip): ");

        if (!tags.isBlank()) {
            card.setTags(parseKeywordsFromString(tags));
        }
    }

    private void editCardType(Card card) {
        String typeString = ConsoleHelper.takeStringInput(
                "Enter a new type (story/task/issue) (put nothing to skip): ");

        if (!typeString.isBlank()) {
            CardType type = parseTypeFromString(typeString);

            if (type == null) {
                System.out.println("Ignoring changes: Card type can only be one of story, task, and issue");
                return;
            }

            card.setType(type);
        }
    }

    private void editStoryPoints(Card card) {
        int storyPoints = ConsoleHelper.takeIntInput("Enter the number of story points (enter -1 to skip): ");

        if (storyPoints != -1) {
            try {
                card.setStoryPoints(storyPoints);
            } catch (NegativeStoryPointsException e) {
                System.out.println("Ignoring changes: " + e.getMessage());
            }
        }
    }

    private void editCardColumn(Card card) {
        int columnIndex = ConsoleHelper.takeIntInput("Enter the index of the new parent column (enter -1 to skip): ");

        if (columnIndex != -1) {
            List<Column> columns = currentKanbanBoard.getColumns();

            if (!validIndex(columns, columnIndex)) {
                System.out.println("Ignoring changes: Invalid column index");
                return;
            }

            Column newColumn = columns.get(columnIndex);

            currentKanbanBoard.moveCard(card, newColumn);
        }
    }

    private void launchDeleteCardWizard() {
        displayMenuStart("Delete Column Wizard");

        int columnIndex = ConsoleHelper.takeIntInput("Enter the index of the column: ");
        List<Column> columns = currentKanbanBoard.getColumns();

        if (!validIndex(columns, columnIndex)) {
            System.out.println("Invalid project index");
            displayMenuEnd(true);
            return;
        }

        Column column = columns.get(columnIndex);

        int cardIndex = ConsoleHelper.takeIntInput("Enter the index of the card: ");
        List<Card> cards = column.getCards();

        if (!validIndex(cards, cardIndex)) {
            System.out.println("Invalid card index");
            displayMenuEnd(true);
            return;
        }

        Card card = cards.get(cardIndex);

        column.removeCard(card);

        ConsoleHelper.newLine();

        System.out.println("Deleted the card '" + card.getTitle() + "'");

        displayMenuEnd(true);
    }

    // EFFECTS: parses a string of comma separated keywords
    private Set<String> parseKeywordsFromString(String keywordString) {
        // To make the search feature work properly, all tags have to be lowercase.
        String[] tagsArray = keywordString.trim().toLowerCase().split(",");
        Set<String> tags = new HashSet<>();

        // We also have to remove extra whitespace in case if the user typed
        // "tag1, tag2" instead of "tag1,tag2".
        for (String tag : tagsArray) {
            tags.add(tag.trim());
        }

        return tags;
    }

    // EFFECTS: parses a type string into its corresponding enum,
    //          or null if there is no corresponding type
    private CardType parseTypeFromString(String typeString) {
        String normalized = typeString.trim().toLowerCase();

        switch (normalized) {
            case "userstory":
            case "user story":
            case "story":
                return CardType.USER_STORY;
            case "task":
                return CardType.TASK;
            case "bug":
            case "issue":
                return CardType.ISSUE;
        }

        return null;
    }

    //
    // Filtering menu section
    //

    private void displayFilteringMenu() {
        displayFilteringMenuOptions();
        processFilteringMenuInput();
    }

    private void displayFilteringMenuOptions() {
        ConsoleHelper.newLine();
        System.out.println("Filtering Commands:");
        System.out.println("\t's' -> Search for cards (case insensitive)");
        System.out.println("\t't' -> Filter cards of a certain type");
        ConsoleHelper.newLine();
    }

    private void processFilteringMenuInput() {
        String command = ConsoleHelper.takeStringInput("Please enter a command: ").toLowerCase();

        switch (command) {
            case "s":
                launchCardSearch();
                break;
            case "t":
                launchCardTypeFilter();
                break;
            default:
                displayUnknownCommandError();
                break;
        }
    }

    private void launchCardSearch() {
        ConsoleHelper.newLine();

        String keywordString = ConsoleHelper.takeStringInput(
                "Enter some comma separated keywords: ");
        Set<String> keywords = parseKeywordsFromString(keywordString);

        ConsoleHelper.newLine();

        displayColumns(currentKanbanBoard.getColumns(), keywords);

        ConsoleHelper.newLine();
        ConsoleHelper.pause();
    }

    private void launchCardTypeFilter() {
        ConsoleHelper.newLine();

        String typeString = ConsoleHelper.takeStringInput(
                "Enter a card type (story/task/issue): ");
        CardType type = parseTypeFromString(typeString);

        ConsoleHelper.newLine();

        displayColumns(currentKanbanBoard.getColumns(), type);

        ConsoleHelper.newLine();
        ConsoleHelper.pause();
    }

    //
    // Stats menu section
    //

    private void displayKanbanBoardStats() {
        ConsoleHelper.newLine();

        int totalCardCount = currentKanbanBoard.getCardCount(true);
        int inProgressCardCount = currentKanbanBoard.getCardCount(false);
        int completedCardCount = totalCardCount - inProgressCardCount;

        System.out.println("Total cards: " + totalCardCount);
        System.out.println("\t- In progress: " + inProgressCardCount);
        System.out.println("\t- Completed: " + completedCardCount);

        int totalStoryPoints = currentKanbanBoard.getTotalStoryPoints();
        int completedStoryPoints = currentKanbanBoard.getCompletedStoryPoints();
        int inProgressStoryPoints = totalStoryPoints - completedStoryPoints;

        System.out.println("Story points: " + totalStoryPoints);
        System.out.println("\t- In progress: " + inProgressStoryPoints);
        System.out.println("\t- Completed: " + completedStoryPoints);

        ConsoleHelper.newLine();

        ConsoleHelper.pause();
    }

    //
    // Utilities
    //

    private final Deque<String> sectionEnds = new ArrayDeque<>();

    // EFFECTS: displays a simply title for a menu
    // EXAMPLE:
    // ===============
    // This is a title
    // ---------------
    private void displayMenuStart(String title) {
        int titleLength = title.length();

        ConsoleHelper.newLine();
        System.out.println("=".repeat(titleLength));
        System.out.println(title);
        System.out.println("-".repeat(titleLength));

        sectionEnds.push("=".repeat(titleLength));
    }

    // EFFECTS: displays a corresponding ending delimiter for a menu
    // EXAMPLE:
    // ===============
    private void displayMenuEnd(boolean pause) {
        ConsoleHelper.newLine();
        System.out.println(sectionEnds.pop());

        if (pause) {
            ConsoleHelper.pause();
        }
    }

    // EFFECTS: displays an error for an unknown command
    private void displayUnknownCommandError() {
        System.out.println("Unknown command");
        ConsoleHelper.tryAgain();
    }

    private boolean validIndex(List<?> list, int index) {
        return index >= 0 && index < list.size();
    }
}
