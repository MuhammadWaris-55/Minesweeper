import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Random;

/**
 * GameBoard: Core game logic, grid management.
 * Generics: ArrayList<Cell>, ArrayList<int[]>
 * Polymorphism: Cell references handle MineCell and EmptyCell
 */
public class GameBoard extends JPanel {

    private final int rows, cols, mineCount;

    private final ArrayList<ArrayList<Cell>> grid = new ArrayList<>();
    private final ArrayList<int[]> minePositions  = new ArrayList<>();

    private boolean gameOver   = false;
    private boolean gameWon    = false;
    private boolean firstClick = true;
    private int flagsPlaced    = 0;
    private int cellsRevealed  = 0;

    private final GameListener listener;

    public interface GameListener {
        void onFlagCountChanged(int flags, int mines);
        void onGameOver(boolean won);
        void onFirstClick();
    }

    public GameBoard(int rows, int cols, int mineCount, GameListener listener) {
        this.rows = rows; this.cols = cols;
        this.mineCount = mineCount; this.listener = listener;
        setLayout(new GridLayout(rows, cols, 3, 3));
        setBackground(new Color(18, 18, 30));
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        initGrid();
    }

    private void initGrid() {
        removeAll();
        grid.clear(); minePositions.clear();
        gameOver = false; gameWon = false; firstClick = true;
        flagsPlaced = 0; cellsRevealed = 0;

        for (int r = 0; r < rows; r++) {
            ArrayList<Cell> row = new ArrayList<>();
            for (int c = 0; c < cols; c++) {
                EmptyCell cell = new EmptyCell();
                final int fr = r, fc = c;
                cell.addMouseListener(new MouseAdapter() {
                    @Override public void mousePressed(MouseEvent e) {
                        if (gameOver) return;
                        if (SwingUtilities.isLeftMouseButton(e))  handleLeftClick(fr, fc);
                        else if (SwingUtilities.isRightMouseButton(e)) handleRightClick(fr, fc);
                    }
                    @Override public void mouseEntered(MouseEvent e) {
                        Cell cc = grid.get(fr).get(fc);
                        if (!cc.isRevealed() && !cc.isFlagged())
                            cc.setBackground(new Color(58, 58, 106));
                    }
                    @Override public void mouseExited(MouseEvent e) {
                        Cell cc = grid.get(fr).get(fc);
                        if (!cc.isRevealed() && !cc.isFlagged())
                            cc.setBackground(new Color(42, 42, 74));
                    }
                });
                row.add(cell);
                add(cell);
            }
            grid.add(row);
        }
        revalidate(); repaint();
    }

    private void placeMines(int safeRow, int safeCol) {
        Random rand = new Random();
        int placed = 0;
        while (placed < mineCount) {
            int r = rand.nextInt(rows), c = rand.nextInt(cols);
            if (Math.abs(r - safeRow) <= 1 && Math.abs(c - safeCol) <= 1) continue;
            if (grid.get(r).get(c).isMine()) continue;

            MineCell mine = new MineCell();
            final int fr = r, fc = c;
            mine.addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) {
                    if (gameOver) return;
                    if (SwingUtilities.isLeftMouseButton(e))  handleLeftClick(fr, fc);
                    else if (SwingUtilities.isRightMouseButton(e)) handleRightClick(fr, fc);
                }
                @Override public void mouseEntered(MouseEvent e) {
                    Cell cc = grid.get(fr).get(fc);
                    if (!cc.isRevealed() && !cc.isFlagged())
                        cc.setBackground(new Color(58, 58, 106));
                }
                @Override public void mouseExited(MouseEvent e) {
                    Cell cc = grid.get(fr).get(fc);
                    if (!cc.isRevealed() && !cc.isFlagged())
                        cc.setBackground(new Color(42, 42, 74));
                }
            });
            grid.get(r).set(c, mine);
            minePositions.add(new int[]{r, c});
            Component[] components = getComponents();
            int idx = r * cols + c;
            remove(idx); add(mine, idx);
            placed++;
        }

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++) {
                Cell cell = grid.get(r).get(c);
                if (!cell.isMine()) ((EmptyCell) cell).setAdjacentMines(countAdjacentMines(r, c));
            }

        revalidate(); repaint();
    }

    private int countAdjacentMines(int row, int col) {
        int count = 0;
        for (int dr = -1; dr <= 1; dr++)
            for (int dc = -1; dc <= 1; dc++) {
                int nr = row+dr, nc = col+dc;
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid.get(nr).get(nc).isMine()) count++;
            }
        return count;
    }

    private void handleLeftClick(int row, int col) {
        Cell cell = grid.get(row).get(col);
        if (cell.isRevealed() || cell.isFlagged()) return;

        if (firstClick) {
            firstClick = false;
            placeMines(row, col);
            listener.onFirstClick();
            cell = grid.get(row).get(col);
        }

        if (cell.isMine()) {
            ((MineCell) cell).detonate();
            revealAllMines();
            gameOver = true;
            listener.onGameOver(false);
        } else {
            floodReveal(row, col);
            checkWin();
        }
    }

    private void handleRightClick(int row, int col) {
        Cell cell = grid.get(row).get(col);
        if (cell.isRevealed()) return;
        boolean wasFlagged = cell.isFlagged();
        cell.onRightClick();
        flagsPlaced += wasFlagged ? -1 : 1;
        listener.onFlagCountChanged(flagsPlaced, mineCount);
    }

    private void floodReveal(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) return;
        Cell cell = grid.get(row).get(col);
        if (cell.isRevealed() || cell.isFlagged() || cell.isMine()) return;
        cell.reveal();
        cellsRevealed++;
        if (cell instanceof EmptyCell && ((EmptyCell) cell).getAdjacentMines() == 0)
            for (int dr = -1; dr <= 1; dr++)
                for (int dc = -1; dc <= 1; dc++)
                    if (dr != 0 || dc != 0) floodReveal(row+dr, col+dc);
    }

    private void revealAllMines() {
        for (int[] pos : minePositions) {
            Cell cell = grid.get(pos[0]).get(pos[1]);
            if (!cell.isFlagged()) cell.reveal();
        }
    }

    private void checkWin() {
        if (cellsRevealed >= rows * cols - mineCount) {
            gameWon = true; gameOver = true;
            for (int[] pos : minePositions) {
                Cell cell = grid.get(pos[0]).get(pos[1]);
                if (!cell.isFlagged()) cell.setFlagged(true);
            }
            listener.onGameOver(true);
        }
    }

    public void reset() { initGrid(); }
}
