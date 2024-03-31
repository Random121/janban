# Janban

A kanban board for scrum written in Java.

### What does it do?

Janban is an application which enables the user to manage their project in a visual manner through the kanban system
with specific features for the scrum project management framework.

### Who is this for?

Janban is built for anyone who is working on or managing a complex project.

### Why make this?

I have worked with many tools that implements a kanban board in the past, an example being Jira or just a Trello board.
However, most of these services are fully online, and also not as customizable. So Janban serves as an applications
which allows people to use a kanban board fully offline and is also fully customizable (it is open source!).

## Instructions for Grader

### Console App

- When you create a new kanban board, you have to specify the `completed column name`
    - This is the name of the column which holds cards you have completed
    - Any new column with this name will also become the completed column
- The column marked with a `[D]` is the completed/done column
    - Cards which are done should be moved to this column if you want the kanban board statistics to work properly
- The first column of the kanban board is designated as the backlog
    - New cards will always be added there, but they can be moved elsewhere after

### Graphical App

- After loading a project, you can view a panel which displays the list of cards which were added to
  columns and the list of columns which were added to the kanban board.
- You can generate the two required list actions by clicking any of the buttons at the top of the kanban board. Some
  examples include adding a card, filtering through the cards, and removing a column.
- You can view the visual component (a logo image) on the main menu which appears right after opening the
  app.
- You are given the option to save all user data if you close the project selection window.
- You are given the option to load all previous user data on the main menu.

## User Stories

### Phase 0 & Phase 1

As a user, I want to be able to...

- create a kanban board with a name and description and add it to a list of kanban boards
- view all the kanban boards I have created
- create a column with a name and add it to the list of columns of the kanban board
- view all the columns I have created for a kanban board
- edit the name of a column
- remove a column from a kanban board
- add a card to a column and specify its title, description, assignee, type, story points, and tags
- view all the cards within a column
- edit the title, description, assignee, type, story points, and tags of a card
- move a card to a different column
- filter cards within all columns based on keywords or type
- remove a card from a column
- view basic statistics about the kanban board like the number of cards and story points

### Phase 2

As a user, I want to be able to...

- have the option to save all the kanban boards I have created to file when quitting the application
- have the option to load all the kanban boards I have previously saved when starting the application

### Phase 4

#### Task 2

```
Fri Mar 29 17:13:49 PDT 2024
Adding default columns to kanban board 'My Project'
Fri Mar 29 17:13:49 PDT 2024
Adding column 'Backlog' to kanban board 'My Project'
Fri Mar 29 17:13:49 PDT 2024
Adding column 'In Progress' to kanban board 'My Project'
Fri Mar 29 17:13:49 PDT 2024
Adding column 'Done' to kanban board 'My Project'
Fri Mar 29 17:13:49 PDT 2024
Adding kanban board 'My Project' to list
Fri Mar 29 17:13:57 PDT 2024
Moving card 'Center div' from column 'None' to 'Backlog'
Fri Mar 29 17:13:57 PDT 2024
Adding card 'Center div' to column 'Backlog'
Fri Mar 29 17:14:04 PDT 2024
Moving card 'Add dark mode' from column 'None' to 'Backlog'
Fri Mar 29 17:14:04 PDT 2024
Adding card 'Add dark mode' to column 'Backlog'
Fri Mar 29 17:14:09 PDT 2024
Removing card 'Center div' from column 'Backlog'
Fri Mar 29 17:14:15 PDT 2024
Adding column 'Under Review' to kanban board 'My Project'
Fri Mar 29 17:14:24 PDT 2024
Querying relevancy score for card 'Add dark mode' with result '2'
Fri Mar 29 17:14:24 PDT 2024
Querying cards in column 'Backlog' with keywords 'add,dark' with 1 results
Fri Mar 29 17:14:24 PDT 2024
Querying relevancy score for card 'Center div' with result '0'
Fri Mar 29 17:14:24 PDT 2024
Querying cards in column 'In Progress' with keywords 'add,dark' with 0 results
Fri Mar 29 17:14:24 PDT 2024
Querying cards in column 'Done' with keywords 'add,dark' with 0 results
Fri Mar 29 17:14:24 PDT 2024
Querying cards in column 'Under Review' with keywords 'add,dark' with 0 results
Fri Mar 29 17:14:36 PDT 2024
Editing column name from 'Backlog' to 'Not Started' in kanban board 'My Project'
Fri Mar 29 17:14:40 PDT 2024
Removing card 'Add dark mode' from column 'Not Started'
Fri Mar 29 17:14:44 PDT 2024
Removing column 'Under Review' from kanban board 'My Project'
```

#### Task 3

I feel that the `KanbanJsonReader` is a class in need of major refactoring. While it has great cohesion
(only handling loading the model from JSON), it also has high semantic coupling with classes in the model,
which is undesirable. If I wanted to change what is being saved for a class, I would not only have to modify `toJSON()`
located within that class (which is fine) but also have to correspondingly modify `KanbanJsonReader` to ensure that
their implementations match, which could lead to errors that is only detectable at runtime. Instead, I could implement
the reading method within each class (like a `fromJSON()` method) which makes it clear that both `toJSON()`
and `fromJSON()` should be modified at the same time. Another area of improvement is with the handling of default column
names. Currently, both implementations within `Column` and `KanbanBoard` sets the name to a default value if
it `isBlank()`. However, this means that the two classes are semantically coupled which would cause issues if I were to
change, for example, one of the implementations to be `isEmpty()` but forget to change the other. Instead, I could
create a `toColumnName()` method within `Column` that would handle setting the default value and ensure all
implementations are the same. Another thing I've noticed is that all the model classes have a name of some form. Thus,
to decrease repetition and improve coupling I would create a parent class `NamedObject` that would just contain the name
field and methods related to it.