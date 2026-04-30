# 💣 Minesweeper — Java Swing

> A classic Minesweeper game built with Java Swing featuring a modern dark UI, three difficulty levels, and a live timer. Developed as a semester-end project demonstrating core OOP principles.

---

## 📸 Preview

```
┌─────────────────────────────────┐
│  M  10          :)       T  000 │
├─────────────────────────────────┤
│  ■  ■  ■  ■  ■  ■  ■  ■  ■    │
│  ■  ■  ■  ■  ■  ■  ■  ■  ■    │
│  ■  ■  ■  ■  ■  ■  ■  ■  ■    │
│  ■  ■  ■  ■  ■  ■  ■  ■  ■    │
│  ■  ■  ■  ■  ■  ■  ■  ■  ■    │
├─────────────────────────────────┤
│  Click a cell to start!         │
└─────────────────────────────────┘
```

---

## 🚀 Getting Started

### Prerequisites
- Java **8 or newer** installed
- Verify with: `java -version`

### Run the Game
```bash
java -jar Minesweeper.jar
```

### Compile from Source
```bash
javac -d out src/Clickable.java src/Cell.java src/EmptyCell.java src/MineCell.java src/GameBoard.java src/Main.java
java -cp out Main
```

---

## 🎮 How to Play

| Action | Control |
|--------|---------|
| Reveal a cell | Left-click |
| Place / remove flag | Right-click |
| Reset game | Click the `:)` button |
| Change difficulty | Dropdown at the bottom |

### Rules
- **Left-click** a cell to reveal it. If it's a mine — game over!
- Numbers show how many mines are in the 8 surrounding cells.
- **Right-click** to flag a cell you suspect contains a mine.
- Reveal all safe cells to **win**.
- Your **first click is always safe** — mines are placed after it.

---

## 🏆 Difficulty Levels

| Level | Grid | Mines |
|-------|------|-------|
| Beginner | 9 × 9 | 10 |
| Intermediate | 16 × 16 | 40 |
| Expert | 16 × 30 | 99 |

---

## 🗂️ Project Structure

```
Minesweeper/
├── src/
│   ├── Main.java          # Entry point — JFrame, UI, timer thread
│   ├── GameBoard.java     # Game logic, grid, mine placement, flood-fill
│   ├── Cell.java          # Abstract base class for all cells
│   ├── EmptyCell.java     # Safe cell — displays adjacent mine count
│   ├── MineCell.java      # Mine cell — handles detonation display
│   └── Clickable.java     # Interface defining click behaviour
├── Minesweeper.jar        # Runnable JAR
└── README.md
```

---

## 🧠 OOP Concepts Used

### 1. Abstraction — `Cell.java`
`Cell` is an `abstract` class that defines the contract every cell must follow. It cannot be instantiated directly.

---

### 2. Inheritance — `EmptyCell` & `MineCell`
Both cell types **extend** `Cell`, inheriting shared state (`revealed`, `flagged`, color constants) and overriding abstract behaviour.

---

### 3. Interface — `Clickable.java`
Defines a contract for handling mouse input, implemented by `Cell`.

---

### 4. Polymorphism — `GameBoard.java`
The grid stores all cells as `Cell` references. Methods like `reveal()` and `draw()` behave differently depending on the actual object type at runtime.

---

### 5. Generics — `GameBoard.java`


---

## 🎨 UI Design

The game uses a **modern dark theme** built entirely with Java Swing — no external libraries.

| Element | Detail |
|---------|--------|
| Background | Deep navy `#12121E` |
| Hidden cells | Indigo `#2A2A4A` with depth border |
| Revealed cells | Dark `#1E1E35` |
| Number colors | Teal · Lime · Coral · Lavender · Pink · Mint · Yellow · Gray |
| Flag cells | Dark red background with coral border |
| Mine (detonated) | Bright red flash |
| Panels | Rounded corners via custom `paintComponent` |
| Hover effect | Cell lightens on mouse-over |

---

## 🔁 Game Flow

```
Launch App
    │
    ▼
initGrid() — all EmptyCell placeholders, no mines yet
    │
    ▼
Player left-clicks (first click)
    │
    ▼
placeMines() — mines placed, safe zone around first click
    │
    ├──▶ Mine clicked? ──▶ detonate() ──▶ revealAllMines() ──▶ GAME OVER
    │
    └──▶ Safe cell? ──▶ floodReveal() ──▶ checkWin()
                                              │
                                              └──▶ All safe cells revealed? ──▶ WIN
```

---

## 👨‍💻 Author


### Waris
---

## 📄 License

This project is for educational purposes.
