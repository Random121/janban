package ui.graphical;

import model.Card;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CardListCellRenderer implements ListCellRenderer<Card> {
    @Override
    public Component getListCellRendererComponent(JList<? extends Card> list,
                                                  Card value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
        JLabel titleLabel = new JLabel(value.getTitle());

        // color for JLabels can only be displayed if they have an
        // opaque background
        titleLabel.setOpaque(true);
        titleLabel.setBorder(new EmptyBorder(5, 5, 5, 5));

        handleSelection(list, isSelected, titleLabel);

        return titleLabel;
    }

    // MODIFIES: label
    // EFFECTS: Sets the background and foreground of label
    //          to match its current selection state.
    //          Implementation should match with that of the DefaultListCellRenderer.
    private void handleSelection(JList<? extends Card> list,
                                 boolean isSelected,
                                 JLabel label) {
        Color foreground = isSelected ? list.getSelectionForeground() : list.getForeground();
        Color background = isSelected ? list.getSelectionBackground() : list.getBackground();

        label.setBackground(background);
        label.setForeground(foreground);
    }
}
