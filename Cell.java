import javax.swing.*;
import java.awt.*;

/**
 * Abstract Class: Cell
 * Abstraction layer for all cell types in the grid.
 * Implements Clickable to enforce click handling.
 */
public abstract class Cell extends JButton implements Clickable {

    protected boolean revealed = false;
    protected boolean flagged  = false;

    // Modern dark theme palette
    protected static final Color HIDDEN_COLOR   = new Color(42, 42, 74);
    protected static final Color REVEALED_COLOR = new Color(30, 30, 53);
    protected static final Color FLAGGED_BG     = new Color(80, 20, 20);
    protected static final Color FLAGGED_BORDER = new Color(255, 107, 107);

    // Modern number colors
    protected static final Color[] NUMBER_COLORS = {
        null,
        new Color( 78, 205, 196),  // 1 - Teal
        new Color(168, 224,  99),  // 2 - Lime
        new Color(255, 107, 107),  // 3 - Coral
        new Color(162, 155, 254),  // 4 - Lavender
        new Color(253, 121, 168),  // 5 - Pink
        new Color( 85, 239, 196),  // 6 - Mint
        new Color(255, 234, 167),  // 7 - Yellow
        new Color(178, 190, 195),  // 8 - Gray
    };

    public Cell() {
        setPreferredSize(new Dimension(36, 36));
        setFont(new Font("Segoe UI", Font.BOLD, 13));
        setFocusPainted(false);
        setBackground(HIDDEN_COLOR);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setOpaque(true);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(15, 15, 30)),
            BorderFactory.createMatteBorder(1, 1, 0, 1, new Color(80, 80, 120, 80))
        ));
    }

    public abstract void reveal();
    public abstract void draw();
    public abstract boolean isMine();

    public boolean isRevealed() { return revealed; }
    public boolean isFlagged()  { return flagged;  }

    public void setFlagged(boolean f) { this.flagged = f; draw(); }

    @Override
    public void onRightClick() {
        if (revealed) return;
        flagged = !flagged;
        draw();
    }
}
