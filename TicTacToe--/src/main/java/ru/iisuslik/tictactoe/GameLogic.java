package ru.iisuslik.tictactoe;

import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

/**
 * This class contains all game information, also it can give a hard bot a hint, where to put X or O
 */
public class GameLogic {


    /**
     * Constant field size
     */
    public static final int SIZE = 3;

    /**
     * 3 states of cell in field - X, O, or empty cell
     */
    public enum Cell {
        EMPTY, X, O;

        /**
         * Returns "xor" of current cell
         *
         * @return X if cell is O or O if cell is X
         */
        public Cell getOpposit() {
            if (this == X) {
                return O;
            } else if (this == O) {
                return X;
            }
            return this;
        }
    }

    public GameResult getGameResult() {
        return winner;
    }

    /**
     * 4 game results - no result(game isn't over), X wins, O wins or draw
     */
    public enum GameResult {
        NO_RESULT, X_WINS, O_WINS, DRAW
    }

    private Cell currentPlayer = Cell.X;
    private GameResult winner = GameResult.NO_RESULT;
    private Cell field[][] = new Cell[SIZE][SIZE];
    private int turnCount = 0;

    /**
     * Constructor that starts new game immediately
     */
    public GameLogic() {
        startNewGame();
    }

    /**
     * Who is playing now
     *
     * @return X or O
     */
    public Cell getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Is current game over
     *
     * @return true if no one wins and it isn't draw
     */
    public boolean isThisTheEnd() {
        return winner != GameResult.NO_RESULT;
    }

    /**
     * Starting new game - clearing game field, update current player and so on
     */
    public void startNewGame() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                field[i][j] = Cell.EMPTY;
            }
        }
        currentPlayer = Cell.X;
        winner = GameResult.NO_RESULT;
        turnCount = 0;
    }

    /**
     * Get one cell in field
     *
     * @return [i][j] field cell
     */
    public Cell getCell(int i, int j) {
        return field[i][j];
    }

    /**
     * Check that current player can play in this position
     *
     * @return true if it can be played
     */
    public boolean canPlay(int i, int j) {
        return i >= 0 && j >= 0 && i < SIZE && j < SIZE &&
                field[i][j] == Cell.EMPTY && !isThisTheEnd();
    }

    /**
     * Makes current player playing in  [i][j] cell
     */
    public void play(int i, int j) {
        field[i][j] = currentPlayer;
        checkThatGameIsOver();
        if (!isThisTheEnd())
            startNextTurn();
    }

    /**
     * Return cell coordinates where player can make turn and then win immediately
     *
     * @param cell X or O - for who it will return winning position
     * @return Pair of coordinates, if win position exists, or null else
     */
    public Pair<Integer, Integer> winPosition(@NotNull Cell cell) {
        for (int i = 0; i < SIZE; i++) {
            boolean empty = false;
            int j0 = -1;
            for (int j = 0; j < SIZE; j++) {
                if (field[i][j] == cell.getOpposit()) {
                    empty = false;
                    break;
                }
                if (field[i][j] == Cell.EMPTY) {
                    if (empty) {
                        empty = false;
                        break;
                    }
                    empty = true;
                    j0 = j;
                }
            }
            if (empty) {
                return new Pair<>(i, j0);
            }
        }
        for (int i = 0; i < SIZE; i++) {
            boolean empty = false;
            int j0 = -1;
            for (int j = 0; j < SIZE; j++) {
                if (field[j][i] == cell.getOpposit()) {
                    empty = false;
                    break;
                }
                if (field[j][i] == Cell.EMPTY) {
                    if (empty) {
                        empty = false;
                        break;
                    }
                    empty = true;
                    j0 = j;
                }
            }
            if (empty) {
                return new Pair<>(j0, i);
            }
        }
        boolean empty = false;
        int j0 = -1;
        for (int j = 0; j < SIZE; j++) {
            if (field[j][j] == cell.getOpposit()) {
                empty = false;
                break;
            }
            if (field[j][j] == Cell.EMPTY) {
                if (empty) {
                    empty = false;
                    break;
                }
                empty = true;
                j0 = j;
            }
        }
        if (empty) {
            return new Pair<>(j0, j0);
        }
        for (int j = 0; j < SIZE; j++) {
            if (field[j][SIZE - 1 - j] == cell.getOpposit()) {
                empty = false;
                break;
            }
            if (field[j][SIZE - 1 - j] == Cell.EMPTY) {
                if (empty) {
                    empty = false;
                    break;
                }
                empty = true;
                j0 = j;
            }
        }
        if (empty) {
            return new Pair<>(j0, SIZE - 1 - j0);
        }
        return null;
    }

    private void startNextTurn() {
        turnCount++;
        if (currentPlayer == Cell.X)
            currentPlayer = Cell.O;
        else
            currentPlayer = Cell.X;
    }

    private void setWinner() {
        if (currentPlayer == Cell.X) {
            winner = GameResult.X_WINS;
        } else {
            winner = GameResult.O_WINS;
        }
    }

    private void checkThatGameIsOver() {
        if (turnCount == 8) {
            winner = GameResult.DRAW;
            return;
        }
        for (int i = 0; i < SIZE; i++) {
            checkThatRowIs(i, Cell.X);
            checkThatRowIs(i, Cell.O);
        }

        for (int j = 0; j < SIZE; j++) {
            checkThatCalIs(j, Cell.X);
            checkThatCalIs(j, Cell.O);
        }

        checkThatDiagIs(Cell.X);
        checkThatDiagIs(Cell.O);
        checkThatOtherDiagIs(Cell.X);
        checkThatOtherDiagIs(Cell.X);
    }

    private void checkThatRowIs(int i, Cell cell) {
        for (Cell c : field[i]) {
            if (c != cell) {
                return;
            }
        }
        setWinner();
    }

    private void checkThatCalIs(int j, Cell cell) {
        for (int i = 0; i < SIZE; i++) {
            if (field[i][j] != cell) {
                return;
            }
        }
        setWinner();
    }

    private void checkThatDiagIs(Cell cell) {
        for (int i = 0; i < Integer.min(SIZE, SIZE); i++) {
            if (field[i][i] != cell) {
                return;
            }
        }
        setWinner();
    }

    private void checkThatOtherDiagIs(Cell cell) {
        for (int i = 0; i < SIZE; i++) {
            if (field[i][SIZE - 1 - i] != cell) {
                return;
            }
        }
        setWinner();
    }

}
