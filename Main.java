import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Main: Entry point.
 * Modern dark-theme UI with rounded panels, custom colors.
 * Threading: Separate daemon thread drives the timer.
 */
public class Main extends JFrame implements GameBoard.GameListener {

    // Colors
    private static final Color BG_DARK      = new Color(18,  18,  30);
    private static final Color BG_PANEL     = new Color(26,  26,  46);
    private static final Color BG_BAR       = new Color(22,  22,  38);
    private static final Color ACCENT_RED   = new Color(255, 107, 107);
    private static final Color ACCENT_TEAL  = new Color( 78, 205, 196);
    private static final Color TEXT_MUTED   = new Color(160, 160, 190);

    private GameBoard  board;
    private JLabel     mineLabel;
    private JLabel     timerLabel;
    private JLabel     statusLabel;
    private JButton    resetButton;

    private Thread  timerThread;
    private volatile boolean timerRunning = false;
    private int     elapsedSeconds = 0;

    private static final int[][] PRESETS = {
        { 9,  9, 10},
        {16, 16, 40},
        {16, 30, 99}
    };
    private static final String[] PRESET_NAMES = {"Beginner  9x9", "Intermediate 16x16", "Expert 16x30"};
    private int currentPreset = 0;

    public Main() {
        setTitle("Minesweeper");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        buildTopBar();
        buildBoard();
        buildBottomBar();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ── Rounded dark panel helper ─────────────────────────────────
    private JPanel roundedPanel(Color bg, int radius) {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
                g2.dispose();
            }
        };
    }

    private void buildTopBar() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG_DARK);
        outer.setBorder(BorderFactory.createEmptyBorder(12, 12, 6, 12));

        JPanel bar = roundedPanel(BG_BAR, 14);
        bar.setLayout(new BorderLayout(0, 0));
        bar.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        bar.setOpaque(false);

        mineLabel = new JLabel("M  " + PRESETS[currentPreset][2]);
        mineLabel.setFont(new Font("Courier New", Font.BOLD, 20));
        mineLabel.setForeground(ACCENT_RED);

        timerLabel = new JLabel("T  000");
        timerLabel.setFont(new Font("Courier New", Font.BOLD, 20));
        timerLabel.setForeground(ACCENT_TEAL);
        timerLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        resetButton = new JButton(":)") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed()
                    ? new Color(55, 55, 80)
                    : getModel().isRollover()
                        ? new Color(65, 65, 95)
                        : new Color(48, 48, 72));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(new Color(255,255,255,30));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        resetButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        resetButton.setPreferredSize(new Dimension(52, 40));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        resetButton.setContentAreaFilled(false);
        resetButton.setBorderPainted(false);
        resetButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        resetButton.addActionListener(e -> resetGame());

        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        center.setOpaque(false);
        center.add(resetButton);

        bar.add(mineLabel,  BorderLayout.WEST);
        bar.add(center,     BorderLayout.CENTER);
        bar.add(timerLabel, BorderLayout.EAST);

        outer.add(bar);
        add(outer, BorderLayout.NORTH);
    }

    private void buildBoard() {
        int[] p = PRESETS[currentPreset];
        board = new GameBoard(p[0], p[1], p[2], this);

        JPanel wrapper = roundedPanel(BG_PANEL, 14);
        wrapper.setLayout(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        wrapper.setOpaque(false);
        wrapper.add(board);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG_DARK);
        outer.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        outer.add(wrapper);
        add(outer, BorderLayout.CENTER);
    }

    private void buildBottomBar() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG_DARK);
        outer.setBorder(BorderFactory.createEmptyBorder(6, 12, 12, 12));

        JPanel bar = roundedPanel(BG_BAR, 12);
        bar.setLayout(new FlowLayout(FlowLayout.CENTER, 12, 8));
        bar.setOpaque(false);

        statusLabel = new JLabel("Click a cell to start!");
        statusLabel.setForeground(TEXT_MUTED);
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));

        // Separator dot
        JLabel sep = new JLabel("·");
        sep.setForeground(new Color(80, 80, 110));

        JLabel diffLabel = new JLabel("Difficulty:");
        diffLabel.setForeground(TEXT_MUTED);
        diffLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JComboBox<String> diffBox = new JComboBox<>(PRESET_NAMES);
        diffBox.setSelectedIndex(currentPreset);
        diffBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        diffBox.setBackground(new Color(38, 38, 60));
        diffBox.setForeground(Color.WHITE);
        diffBox.setFocusable(false);
        UIManager.put("ComboBox.selectionBackground", new ColorUIResource(new Color(55, 55, 85)));
        diffBox.addActionListener(e -> {
            currentPreset = diffBox.getSelectedIndex();
            rebuildBoard();
        });

        bar.add(statusLabel);
        bar.add(sep);
        bar.add(diffLabel);
        bar.add(diffBox);

        outer.add(bar);
        add(outer, BorderLayout.SOUTH);
    }

    private void rebuildBoard() {
        stopTimer();
        elapsedSeconds = 0;
        timerLabel.setText("T  000");
        resetButton.setText(":)");
        statusLabel.setText("Click a cell to start!");
        statusLabel.setForeground(TEXT_MUTED);
        mineLabel.setText("M  " + PRESETS[currentPreset][2]);

        getContentPane().remove(1);
        buildBoard();
        pack();
        setLocationRelativeTo(null);
    }

    private void resetGame() {
        stopTimer();
        elapsedSeconds = 0;
        timerLabel.setText("T  000");
        resetButton.setText(":)");
        statusLabel.setText("Click a cell to start!");
        statusLabel.setForeground(TEXT_MUTED);
        mineLabel.setText("M  " + PRESETS[currentPreset][2]);
        board.reset();
    }

    private void startTimer() {
        stopTimer();
        timerRunning = true;
        timerThread = new Thread(() -> {
            while (timerRunning) {
                try { Thread.sleep(1000); } catch (InterruptedException e) { break; }
                if (!timerRunning) break;
                elapsedSeconds = Math.min(elapsedSeconds + 1, 999);
                SwingUtilities.invokeLater(() ->
                    timerLabel.setText(String.format("T  %03d", elapsedSeconds))
                );
            }
        });
        timerThread.setDaemon(true);
        timerThread.start();
    }

    private void stopTimer() {
        timerRunning = false;
        if (timerThread != null) timerThread.interrupt();
    }

    @Override
    public void onFirstClick() {
        startTimer();
        statusLabel.setText("Stay sharp!");
        statusLabel.setForeground(TEXT_MUTED);
    }

    @Override
    public void onFlagCountChanged(int flags, int mines) {
        mineLabel.setText("M  " + (mines - flags));
    }

    @Override
    public void onGameOver(boolean won) {
        stopTimer();
        if (won) {
            resetButton.setText(";)");
            statusLabel.setText("You Win!  Cleared in " + elapsedSeconds + "s");
            statusLabel.setForeground(new Color(168, 224, 99));
        } else {
            resetButton.setText("X(");
            statusLabel.setText("Boom!  Press :) to retry");
            statusLabel.setForeground(ACCENT_RED);
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(Main::new);
    }
}
