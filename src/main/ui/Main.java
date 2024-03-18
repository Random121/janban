package ui;

import ui.console.JanbanConsoleApp;
import ui.graphical.JanbanGraphicalApp;

public class Main {
    public static void main(String[] args) {
        RunnableApp app = new JanbanGraphicalApp();
        app.run();
    }
}
