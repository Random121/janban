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

## Usage

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
- have the option to save all the kanban boards I have created to file when quitting the application
- have the option to load all the kanban boards I have previously saved when starting the application