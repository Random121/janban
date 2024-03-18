package ui.graphical;

import model.KanbanBoard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// This class is for rendering items in the kanban boards JList in a pretty manner.
public class KanbanBoardListCellRenderer implements ListCellRenderer<KanbanBoard> {

    // EFFECTS: Gets the rendered view of the kanban board.
    @Override
    public Component getListCellRendererComponent(JList<? extends KanbanBoard> list,
                                                  KanbanBoard value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
        JPanel panel = new JPanel(new GridLayout(0, 1));

        // add inner padding
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel nameLabel = new JLabel(value.getName());
        Font nameLabelFont = new Font(list.getFont().getFontName(), Font.BOLD, 20);
        nameLabel.setFont(nameLabelFont);

        JTextArea descriptionTextArea = new JTextArea(2, 10);
        descriptionTextArea.setText(value.getDescription());
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setEditable(false);
        descriptionTextArea.setWrapStyleWord(true);

        handleSelection(list, isSelected, panel, descriptionTextArea);

        panel.add(nameLabel);
        panel.add(descriptionTextArea);

        return panel;
    }

    // MODIFIES: panel, description
    // EFFECTS: Sets the background and foreground of panel and description
    //          to match their current selection state.
    //          Implementation should match with that of the DefaultListCellRenderer.
    private void handleSelection(JList<? extends KanbanBoard> list,
                                 boolean isSelected,
                                 JPanel panel,
                                 JTextArea description) {
        Color foreground = isSelected ? list.getSelectionForeground() : list.getForeground();
        Color background = isSelected ? list.getSelectionBackground() : list.getBackground();

        panel.setBackground(background);
        panel.setForeground(foreground);
        description.setBackground(background);
        description.setForeground(foreground);
    }
}
