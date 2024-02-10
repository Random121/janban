package ui;

public class Main {
    public static void main(String[] args) {
        RunnableApp app = new JanbanConsoleApp();
        app.initTesting();
        app.run();
    }
}
