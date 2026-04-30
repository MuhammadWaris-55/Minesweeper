import java.awt.*;
import javax.swing.*;

/**
 * Inheritance: EmptyCell extends Cell
 * Represents a safe cell — may show adjacent mine count.
 */
public class EmptyCell extends Cell {

    private int adjacentMines = 0;

    public EmptyCell() { super(); }

    public void setAdjacentMines(int count) { this.adjacentMines = count; }
    public int  getAdjacentMines()          { return adjacentMines; }

    @Override public boolean isMine() { return false; }

    @Override
    public void reveal() {
        if (flagged) return;
        revealed = true;
        draw();
    }

    @Override
    public void draw() {
        if (flagged && !revealed) {
            setBackground(FLAGGED_BG);
            setBorder(BorderFactory.createLineBorder(FLAGGED_BORDER, 1));
            setText("F");
            setForeground(FLAGGED_BORDER);
            return;
        }
        if (!revealed) {
            setBackground(HIDDEN_COLOR);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(15, 15, 30)),
                BorderFactory.createMatteBorder(1, 1, 0, 1, new Color(80, 80, 120, 80))
            ));
            setText("");
            return;
        }
        // Revealed
        setBackground(REVEALED_COLOR);
        setBorder(BorderFactory.createLineBorder(new Color(25, 25, 45), 1));
        if (adjacentMines > 0) {
            setText(String.valueOf(adjacentMines));
            setForeground(NUMBER_COLORS[adjacentMines]);
        } else {
            setText("");
        }
    }

    @Override public void onLeftClick() { /* handled by GameBoard */ }
}
