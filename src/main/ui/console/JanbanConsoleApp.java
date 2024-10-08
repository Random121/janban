package ui.console;

import model.*;
import model.exceptions.*;
import org.json.JSONException;
import persistence.KanbanJsonReader;
import persistence.KanbanJsonWriter;
import ui.RunnableApp;

import java.io.IOException;
import java.util.*;

// This class represents the console user interface for the Janban app.
// Contains all the logic for processing and displaying the app.
public class JanbanConsoleApp implements RunnableApp {
    private static final String SAVE_DATA_FILE = "./data/save.json";
    private final KanbanJsonWriter kanbanJsonWriter;
    private final KanbanJsonReader kanbanJsonReader;

    private KanbanBoardList kanbanBoards;
    private KanbanBoard currentKanbanBoard;

    // This is used by utility methods for displaying better looking menus
    private final Deque<String> menuClosingStrings;

    // EFFECTS: constructs a new console app for Janban with
    //          no kanban boards, no menus, a json writer, and a json reader
    public JanbanConsoleApp() {
        kanbanJsonWriter = new KanbanJsonWriter(SAVE_DATA_FILE);
        kanbanJsonReader = new KanbanJsonReader(SAVE_DATA_FILE);

        kanbanBoards = new KanbanBoardList();
        menuClosingStrings = new ArrayDeque<>();
    }

    // EFFECTS: runs the Janban console app, starting from the main menu
    public void run() {
        System.out.println("====================================");
        System.out.println("Janban: A kanban board made in Java!");
        System.out.println("====================================");

        ConsoleHelper.newLine();

        promptLoadBoards();

        launchMainMenu();

        ConsoleHelper.newLine();

        promptSaveBoards();

        ConsoleHelper.newLine();
        System.out.println("===================================");
        System.out.println("Exiting... Thanks for using Janban!");
        System.out.println("===================================");
    }

    //
    // Saving
    //

    // MODIFIES: this
    // EFFECTS: prompts the user whether they want to load the previously saved boards
    private void promptLoadBoards() {
        boolean completed = false;

        do {
            String command = ConsoleHelper.readString("Would you like to load previously saved boards (y/n)? ", true);

            switch (command) {
                case "y":
                    completed = loadBoardsFromFile();
                    break;
                case "n":
                    System.out.println("Skipping loading!");
                    completed = true;
                    break;
                default:
                    displayUnknownCommandError();
                    break;
            }
        } while (!completed);
    }

    // MODIFIES: this
    // EFFECTS: loads the kanban boards from the save file and returns whether it was successful
    private boolean loadBoardsFromFile() {
        try {
            kanbanBoards = kanbanJsonReader.read();
            System.out.println("Successfully loaded all kanban boards from file!");

            return true;
        } catch (IOException | CorruptedSaveDataException | JSONException e) {
            ConsoleHelper.newLine();
            System.out.println("There was a problem with reading the save file!");
            System.out.println("Please ensure that the save file is not corrupted and try again");
            System.out.println("The error message is: " + e.getMessage());
            ConsoleHelper.newLine();

            return false;
        }
    }

    // EFFECTS: prompts the user whether they want to save their current boards
    private void promptSaveBoards() {
        boolean completed = false;

        do {
            String command = ConsoleHelper.readString("Would you like to save all your boards (y/n)? ", true);

            switch (command) {
                case "y":
                    completed = saveBoardsToFile();
                    break;
                case "n":
                    System.out.println("Skipping saving!");
                    completed = true;
                    break;
                default:
                    displayUnknownCommandError();
                    break;
            }
        } while (!completed);
    }

    // EFFECTS: saves the kanban boards to the save file and returns whether it was successful
    private boolean saveBoardsToFile() {
        try {
            kanbanJsonWriter.open();
            kanbanJsonWriter.writeBoards(kanbanBoards);
            kanbanJsonWriter.close();

            System.out.println("Successfully saved all kanban boards to file!");

            return true;
        } catch (IOException e) {
            ConsoleHelper.newLine();
            System.out.println("There was a problem with writing to the save file!");
            System.out.println("The error message is: " + e.getMessage());
            ConsoleHelper.newLine();

            return false;
        }
    }

    //
    // Main menu
    //

    // EFFECTS: displays the main menu and takes input for the menu commands
    private void launchMainMenu() {
        do {
            displayCreatedKanbanBoards();
            displayMainMenuOptions();
        } while (processMainMenuInput());
    }

    // EFFECTS: displays all the created kanban boards for the console app
    private void displayCreatedKanbanBoards() {
        displayMenuStart("Created Kanban Boards");

        if (!kanbanBoards.isEmpty()) {
            for (int index = 0; index < kanbanBoards.size(); index++) {
                KanbanBoard board = kanbanBoards.getBoard(index);

                ConsoleHelper.newLine();
                System.out.println("(" + index + ") " + board.getName());
                System.out.println("\t- " + board.getDescription());
            }
        } else {
            System.out.println("You haven't created any kanban boards yet!");
        }

        displayMenuEnd(false);
    }

    // EFFECTS: displays the commands available on the main menu
    private void displayMainMenuOptions() {
        ConsoleHelper.newLine();
        System.out.println("Main Menu Commands:");
        System.out.println("\t'a' -> Add a new kanban board");
        System.out.println("\t's' -> Select an existing kanban board");
        System.out.println("\t'q' -> Quit the application");
        ConsoleHelper.newLine();
    }

    // EFFECTS: gets and processes user input for the main menu
    //          returns whether the menu should be active
    private boolean processMainMenuInput() {
        String command = ConsoleHelper.readString("Please enter a command: ", true);

        switch (command) {
            case "a":
                launchNewKanbanBoardWizard();
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

    // MODIFIES: this
    // EFFECTS: displays the menu which adds new kanban boards and
    //          adds a new kanban board based on the user input
    private void launchNewKanbanBoardWizard() {
        displayMenuStart("New Kanban Board Wizard");

        String name = ConsoleHelper.readString("Enter a name: ", false);
        String description = ConsoleHelper.readString("Enter a description: ", false);
        String completedColumnName = ConsoleHelper.readString("Enter a completed column name (default: 'Done'): ",
                                                              false);

        completedColumnName = completedColumnName.isBlank() ? "Done" : completedColumnName;

        ConsoleHelper.newLine();

        KanbanBoard newBoard = new KanbanBoard(name, description, completedColumnName);

        try {
            newBoard.addDefaultColumns();
            kanbanBoards.addBoard(newBoard);

            System.out.println("Added a new kanban board: " + newBoard.getName());
        } catch (DuplicateColumnException e) {
            System.out.println("Failed to add new kanban board: " + e.getMessage());
        }

        displayMenuEnd(true);
    }

    //
    // Kanban board menu
    //

    // MODIFIES: this
    // EFFECTS: lets the user select a created kanban board, displays
    //          the kanban board, the kanban board menu options,
    //          and takes in user input
    private void launchKanbanBoardMenu() {
        int boardIndex = ConsoleHelper.readInteger("Enter the index of the kanban board: ");

        if (!validIndex(kanbanBoards.getBoards(), boardIndex)) {
            System.out.println("Invalid kanban board selection");
            return;
        }

        currentKanbanBoard = kanbanBoards.getBoard(boardIndex);

        do {
            displayKanbanBoard(currentKanbanBoard);
            displayKanbanBoardMenuOptions();
        } while (processKanbanBoardMenuInput());
    }

    // EFFECTS: displays the currently selected kanban board
    private void displayKanbanBoard(KanbanBoard board) {
        displayMenuStart("Kanban Board for '" + currentKanbanBoard.getName() + "'");

        displayColumns(board.getColumns(), board.getCompletedColumn());

        displayMenuEnd(false);
    }

    // EFFECTS: displays the columns within a kanban board
    private void displayColumns(List<Column> columns, Column completedColumn) {
        for (int index = 0; index < columns.size(); index++) {
            Column column = columns.get(index);
            List<Card> cards = column.getCards();

            ConsoleHelper.newLine();
            System.out.println("(" + index + ") " + column.getName() + (column == completedColumn ? " [D]" : ""));

            if (cards.isEmpty()) {
                System.out.println("\t- There are no cards in this column!");
                continue;
            }

            displayColumnCards(cards);
        }
    }

    // EFFECTS: displays the columns within a kanban board but with the cards
    //          filtered by keywords
    private void displayColumns(List<Column> columns, Set<String> keywords) {
        for (int index = 0; index < columns.size(); index++) {
            Column column = columns.get(index);
            List<Card> cards = column.getCardsWithQuery(keywords);

            ConsoleHelper.newLine();
            System.out.println("(" + index + ") " + column.getName());

            if (!cards.isEmpty()) {
                displayColumnCards(cards);
            } else {
                System.out.println("\t- There are no cards in this column!");
            }
        }
    }

    // EFFECTS: displays the columns within a kanban board but with the cards
    //          filtered by type
    private void displayColumns(List<Column> columns, CardType type) {
        for (int index = 0; index < columns.size(); index++) {
            Column column = columns.get(index);
            List<Card> cards = column.getCardsOfType(type);

            ConsoleHelper.newLine();
            System.out.println("(" + index + ") " + column.getName());

            if (!cards.isEmpty()) {
                displayColumnCards(cards);
            } else {
                System.out.println("\t- There are no cards in this column!");
            }
        }
    }

    // EFFECTS: displays the cards within a column
    private void displayColumnCards(List<Card> cards) {
        for (int index = 0; index < cards.size(); index++) {
            Card card = cards.get(index);
            Set<String> cardTags = card.getTags();

            System.out.println("\t- (" + index + ") " + card.getTitle());
            System.out.println("\t\t- Type: " + card.getType().toString());
            System.out.println("\t\t- Story points: " + card.getStoryPoints());
            System.out.println("\t\t- Assignee: " + card.getAssignee());
            System.out.println("\t\t- Tags: " + String.join(", ", cardTags));
            System.out.println("\t\t- Description: " + card.getDescription());
        }
    }

    // EFFECTS: display the options available for the kanban board menu
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
        String command = ConsoleHelper.readString("Please enter a command: ", true);

        switch (command) {
            case "k":
                launchColumnActionMenu();
                break;
            case "c":
                launchCardActionMenu();
                break;
            case "f":
                launchFilteringMenu();
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
    // Column menu
    //

    // EFFECTS: displays the column action menu and takes in user input
    private void launchColumnActionMenu() {
        do {
            displayColumnActionMenuOptions();
        } while (processColumnActionMenuInput());
    }

    // EFFECTS: displays the options available on the column action menu
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
        String command = ConsoleHelper.readString("Please enter a command: ", true);

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

    // MODIFIES: this
    // EFFECTS: launches the menu for adding a new column and also adds
    //          a new column to the current kanban board based on user input
    private void launchNewColumnWizard() {
        displayMenuStart("New Column Wizard");

        String name = ConsoleHelper.readString("Enter a column name: ", false);

        ConsoleHelper.newLine();

        Column newColumn = new Column(name);

        try {
            currentKanbanBoard.addColumn(newColumn);
            System.out.println("Added a new column: " + newColumn.getName());
        } catch (DuplicateColumnException e) {
            System.out.println("Failed to add new column: " + e.getMessage());
        }

        displayMenuEnd(true);
    }

    // MODIFIES: this
    // EFFECTS: launches the menu for editing a column and also edits the
    //          column based on user input
    private void launchEditColumnWizard() {
        displayMenuStart("Edit Column Wizard");

        int columnIndex = ConsoleHelper.readInteger("Enter the index of the column: ");
        List<Column> columns = currentKanbanBoard.getColumns();

        if (validIndex(columns, columnIndex)) {
            Column column = columns.get(columnIndex);

            String newName = ConsoleHelper.readString("Enter a new name for the column: ", false);

            ConsoleHelper.newLine();

            try {
                currentKanbanBoard.editColumnName(column, newName);

                System.out.println("Edited the column name to '" + column.getName() + "'");
            } catch (DuplicateColumnException e) {
                System.out.println("Failed to edit the column: " + e.getMessage());
            }
        } else {
            System.out.println("Invalid column selection");
        }

        displayMenuEnd(true);
    }

    // MODIFIES: this
    // EFFECTS: launches the menu for deleting a column and also deletes the
    //          column from the current kanban board based on user input
    private void launchDeleteColumnWizard() {
        displayMenuStart("Delete Column Wizard");

        int columnIndex = ConsoleHelper.readInteger("Enter the index of the column: ");
        List<Column> columns = currentKanbanBoard.getColumns();

        if (validIndex(columns, columnIndex)) {
            Column column = columns.get(columnIndex);

            currentKanbanBoard.removeColumn(column);

            ConsoleHelper.newLine();

            System.out.println("Deleted the column '" + column.getName() + "'.");
        } else {
            System.out.println("Invalid column selection");
        }

        displayMenuEnd(true);
    }

    //
    // Card menu
    //

    // EFFECTS: displays the card action menu and takes in user input
    private void launchCardActionMenu() {
        do {
            displayCardActionMenuOptions();
        } while (processCardActionMenuInput());
    }

    // EFFECTS: displays the options available for the card action menu
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
        String command = ConsoleHelper.readString("Please enter a command: ", true);

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

    // EFFECTS: displays the menu for adding a new card
    private void launchNewCardWizard() {
        displayMenuStart("New Card Wizard");

        if (!currentKanbanBoard.getColumns().isEmpty()) {
            doAddNewCard();
        } else {
            System.out.println("Cannot create a card when there are no columns");
        }

        displayMenuEnd(true);
    }

    // MODIFIES: this
    // EFFECTS: adds a new card to the kanban board based on user inputs
    private void doAddNewCard() {
        String title = ConsoleHelper.readString("Enter a title: ", false);
        String description = ConsoleHelper.readString("Enter a description: ", false);
        String assignee = ConsoleHelper.readString("Enter an assignee: ", false);
        String typeString = ConsoleHelper.readString("Enter a type (story/task/issue): ", true);
        String tags = ConsoleHelper.readString("Enter some comma separated tags (optional): ", false);
        int storyPoints = ConsoleHelper.readInteger("Enter the number of story points: ");

        ConsoleHelper.newLine();

        CardType type = parseTypeFromString(typeString);

        if (type == null) {
            System.out.println("Failed to create a card: Card type can only be one of story, task, or issue");
            return;
        }

        try {
            Card newCard = new Card(title, description, assignee, type, parseKeywordsFromString(tags), storyPoints);
            Column firstColumn = currentKanbanBoard.getColumn(0);

            currentKanbanBoard.moveCard(newCard, firstColumn);

            System.out.println("Added a new card: " + newCard.getTitle());
        } catch (NegativeStoryPointsException e) {
            System.out.println("Failed to create a card: " + e.getMessage());
        }

    }

    // MODIFIES: this
    // EFFECTS: launches the menu for editing a card within the kanban board
    private void launchEditCardWizard() {
        displayMenuStart("Edit Card Wizard");

        int columnIndex = ConsoleHelper.readInteger("Enter the index of the column: ");
        List<Column> columns = currentKanbanBoard.getColumns();

        if (!validIndex(columns, columnIndex)) {
            System.out.println("Invalid column selection");
            displayMenuEnd(true);
            return;
        }

        Column column = columns.get(columnIndex);

        int cardIndex = ConsoleHelper.readInteger("Enter the index of the card: ");
        List<Card> cards = column.getCards();

        if (!validIndex(cards, cardIndex)) {
            System.out.println("Invalid card selection");
            displayMenuEnd(true);
            return;
        }

        Card card = cards.get(cardIndex);

        ConsoleHelper.newLine();

        doEditCard(card);

        displayMenuEnd(true);
    }

    // EFFECTS: edits a card within the kanban board
    private void doEditCard(Card card) {
        editCardStringOptions(card);
        editCardType(card);
        editStoryPoints(card);
        editCardColumn(card);
    }

    // MODIFIES: card
    // EFFECTS: edits the string properties of specified card based on user inputs
    private void editCardStringOptions(Card card) {
        String title = ConsoleHelper.readString("Enter a new title (optional): ", false);

        if (!title.isBlank()) {
            card.setTitle(title);
        }

        String description = ConsoleHelper.readString("Enter a new description (optional): ", false);

        if (!description.isBlank()) {
            card.setDescription(description);
        }

        String assignee = ConsoleHelper.readString("Enter a new assignee (optional): ", false);

        if (!assignee.isBlank()) {
            card.setAssignee(assignee);
        }

        String tags = ConsoleHelper.readString("Enter some comma separated tags (optional): ", false);

        if (!tags.isBlank()) {
            card.setTags(parseKeywordsFromString(tags));
        }
    }

    // MODIFIES: card
    // EFFECTS: edits the type of the card based on user inputs
    private void editCardType(Card card) {
        String typeString = ConsoleHelper.readString("Enter a new type (story/task/issue) (optional): ", true);

        if (typeString.isBlank()) {
            return;
        }

        CardType type = parseTypeFromString(typeString);

        if (type == null) {
            System.out.println("Failed to edit: Card type can only be one of story, task, or issue");
            return;
        }

        card.setType(type);
    }

    // MODIFIES: card
    // EFFECTS: edits the story points of the card based on user inputs
    private void editStoryPoints(Card card) {
        Optional<Integer> storyPoints = ConsoleHelper.readOptionalInteger(
                "Enter the number of story points (optional): ");

        if (storyPoints.isEmpty()) {
            return;
        }

        try {
            card.setStoryPoints(storyPoints.get());
        } catch (NegativeStoryPointsException e) {
            System.out.println("Failed to edit: " + e.getMessage());
        }
    }

    // MODIFIES: card
    // EFFECTS: edits the parent column of the card based on user inputs
    private void editCardColumn(Card card) {
        Optional<Integer> columnIndex = ConsoleHelper.readOptionalInteger(
                "Enter the index of the new parent column (optional): ");

        if (columnIndex.isEmpty()) {
            return;
        }

        List<Column> columns = currentKanbanBoard.getColumns();

        if (!validIndex(columns, columnIndex.get())) {
            System.out.println("Failed to edit: Invalid column selection");
            return;
        }

        Column newColumn = columns.get(columnIndex.get());

        currentKanbanBoard.moveCard(card, newColumn);
    }

    // MODIFIES: this
    // EFFECTS: deletes the card specified by user inputs from the current kanban board
    private void launchDeleteCardWizard() {
        displayMenuStart("Delete Column Wizard");

        int columnIndex = ConsoleHelper.readInteger("Enter the index of the column: ");
        List<Column> columns = currentKanbanBoard.getColumns();

        if (!validIndex(columns, columnIndex)) {
            System.out.println("Invalid column selection");
            displayMenuEnd(true);
            return;
        }

        Column column = columns.get(columnIndex);

        int cardIndex = ConsoleHelper.readInteger("Enter the index of the card: ");
        List<Card> cards = column.getCards();

        if (!validIndex(cards, cardIndex)) {
            System.out.println("Invalid card selection");
            displayMenuEnd(true);
            return;
        }

        Card card = cards.get(cardIndex);

        column.removeCard(card);

        ConsoleHelper.newLine();

        System.out.println("Deleted the card '" + card.getTitle() + "'");

        displayMenuEnd(true);
    }

    //
    // Filtering menu
    //

    // EFFECTS: launches the menu for filtering cards
    private void launchFilteringMenu() {
        displayFilteringMenuOptions();
        processFilteringMenuInput();
    }

    // EFFECTS: displays the options available for the filtering menu
    private void displayFilteringMenuOptions() {
        ConsoleHelper.newLine();
        System.out.println("Filtering Commands:");
        System.out.println("\t's' -> Search for cards (case insensitive)");
        System.out.println("\t't' -> Filter cards of a certain type");
        ConsoleHelper.newLine();
    }

    // EFFECTS: takes and processes the input for the filtering menu
    private void processFilteringMenuInput() {
        String command = ConsoleHelper.readString("Please enter a command: ", true);

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

    // EFFECTS: launches the menu for searching cards by keywords: takes in a user input of keywords
    //          and displays the results
    private void launchCardSearch() {
        ConsoleHelper.newLine();

        String keywordString = ConsoleHelper.readString("Enter some comma separated keywords: ", false);
        Set<String> keywords = parseKeywordsFromString(keywordString);

        displayMenuStart("Search Results for '" + String.join(", ", keywords) + "'");

        displayColumns(currentKanbanBoard.getColumns(), keywords);

        displayMenuEnd(true);
    }

    // EFFECTS: launches the menu for searching cards by type: takes in a user input of a type
    //          and displays the results
    private void launchCardTypeFilter() {
        ConsoleHelper.newLine();

        String typeString = ConsoleHelper.readString("Enter a card type (story/task/issue): ", true);
        CardType type = parseTypeFromString(typeString);

        displayMenuStart("Search Results for Card Type '" + type + "'");

        displayColumns(currentKanbanBoard.getColumns(), type);

        displayMenuEnd(true);
    }

    //
    // Stats menu
    //

    // EFFECTS: displays statistics about the story points and card count for the
    //          current kanban board
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

    // MODIFIES: this
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

        menuClosingStrings.push("=".repeat(titleLength));
    }

    // MODIFIES: this
    // EFFECTS: displays a corresponding ending delimiter for a menu
    private void displayMenuEnd(boolean pause) {
        ConsoleHelper.newLine();
        System.out.println(menuClosingStrings.pop());

        if (pause) {
            ConsoleHelper.pause();
        }
    }

    // EFFECTS: displays an error for an unknown command
    private void displayUnknownCommandError() {
        System.out.println("Unknown command");
        ConsoleHelper.pause("Press enter to try again.");
    }

    // EFFECTS: returns whether the index is within the valid range of the list
    private boolean validIndex(List<?> list, int index) {
        return index >= 0 && index < list.size();
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
            case "todo":
            case "task":
                return CardType.TASK;
            case "bug":
            case "issue":
                return CardType.ISSUE;
        }

        return null;
    }
}
