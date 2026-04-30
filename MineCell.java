import java.awt.*;
import javax.swing.*;

/**
 * Inheritance: MineCell extends Cell
 * Represents a cell containing a mine.
 */
public class MineCell extends Cell {

    private boolean detonated = false;

    public MineCell() { super(); }

    @Override public boolean isMine() { return true; }

    @Override
    public void reveal() {
        if (flagged) return;
        revealed = true;
        draw();
    }

    public void detonate() {
        detonated = true;
        revealed  = true;
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
        setBorder(BorderFactory.createLineBorder(new Color(255, 68, 68, 120), 1));
        if (detonated) {
            setBackground(new Color(200, 40, 40));
            setForeground(Color.WHITE);
        } else {
            setBackground(new Color(40, 20, 20));
            setForeground(new Color(255, 107, 107));
        }
        setText("*");
        setFont(new Font("Segoe UI", Font.BOLD, 16));
    }

    @Override public void onLeftClick() { /* handled by GameBoard */ }
}
