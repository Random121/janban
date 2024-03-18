package ui.graphical;

import model.Card;
import model.Column;
import model.KanbanBoard;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

// This class represents a viewable list of columns containing cards.
public class ColumnsScrollPane extends JScrollPane {
    private static final Color COMPLETED_COLUMN_COLOR = new Color(209, 245, 189);
    private static final Dimension COLUMN_DIM = new Dimension(250, 0);

    private final KanbanBoard board;

    private final CardListCellRenderer cardListCellRenderer;
    private final ListSelectionChangedListener selectionChangedListener;
    private final List<JList<Card>> cardLists;

    private Function<Column, List<Card>> filteringGetter;

    // EFFECTS: Creates a ScrollPane containing Columns with a kanban board
    //          a card list cell renderer, a list selection listener,
    //          no card lists, and no filter.
    public ColumnsScrollPane(KanbanBoard board) {
        super();

        this.board = board;

        this.cardListCellRenderer = new CardListCellRenderer();
        this.selectionChangedListener = new ListSelectionChangedListener();
        this.cardLists = new ArrayList<>();
        this.filteringGetter = null;
    }

    // MODIFIES: this
    // EFFECTS: Synchronizes everything about the actual Column model with the
    //          scroll pane used for the view.
    public void syncAll() {
        this.cardLists.clear();

        JPanel columnsPanel = new JPanel();
        columnsPanel.setLayout(new GridLayout(1, 0));

        for (Column column : board.getColumns()) {
            DefaultListModel<Card> cardListModel = new DefaultListModel<>();

            cardListModel.addAll(filteringGetter == null ? column.getCards() : filteringGetter.apply(column));

            TitledBorder border = new TitledBorder(column.getName());
            border.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            border.setTitleJustification(TitledBorder.CENTER);

            JList<Card> cardList = new JList<>(cardListModel);
            cardList.setBorder(border);
            cardList.setCellRenderer(cardListCellRenderer);
            cardList.addListSelectionListener(selectionChangedListener);

            // add some color to the completed column,
            // so it is easily differentiable from the other columns
            if (column == board.getCompletedColumn()) {
                cardList.setBackground(COMPLETED_COLUMN_COLOR);
            }

            this.cardLists.add(cardList);

            JScrollPane cardScrollPane = new JScrollPane(cardList);

            // the columns compress down sometimes rather than just making the scroll pane expand???
            // this should fix that issue
            cardScrollPane.setMinimumSize(COLUMN_DIM);
            cardScrollPane.setPreferredSize(COLUMN_DIM);

            columnsPanel.add(cardScrollPane);
        }

        setViewportView(columnsPanel);
    }

    // EFFECTS: Gets the currently selected card.
    public Card getSelection() {
        for (JList<Card> list : cardLists) {
            Card selection = list.getSelectedValue();

            if (selection != null) {
                return selection;
            }
        }

        return null;
    }

    // MODIFIES: this
    // EFFECTS: Sets a filtering getter that will be used to restrict what cards
    //          will be displayed. A value of null will mean no filtering is performed.
    public void setFilteringGetter(Function<Column, List<Card>> filteringGetter) {
        this.filteringGetter = filteringGetter;
    }

    // This class is listener for changes in the selected card.
    private class ListSelectionChangedListener implements ListSelectionListener {

        // EFFECTS: When a new item is selected between all the columns, it
        //          clears all previously selected values to ensure only one
        //          item is selected across all columns.
        @Override
        public void valueChanged(ListSelectionEvent e) {
            // prevents repeatedly clearing selection for other lists
            // when the user is dragging
            if (e.getValueIsAdjusting()) {
                return;
            }

            JList<Card> listThatChanged = (JList<Card>) e.getSource();

            // we ignore this change since it is caused by clearing the selection for a list,
            // and we only want to care when a new selection happens
            if (listThatChanged.getSelectedValue() == null) {
                return;
            }

            // ensure that there is only one selection across all columns,
            // clear the existing selection in other columns
            for (JList<Card> list : cardLists) {
                if (list != listThatChanged) {
                    list.clearSelection();
                }
            }
        }
    }
}
