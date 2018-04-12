package ru.iisuslik.tictactoe;

import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import static ru.iisuslik.tictactoe.GameLogic.SIZE;
import static ru.iisuslik.tictactoe.GameLogic.Cell;

/**
 * Some game logic tests
 */
public class GameLogicTest {

    private GameLogic game;

    /**
     * It is called after any test to initialize game
     */
    @Before
    public void initializeTest() {
        game = new GameLogic();
    }

    /**
     * Checks that if you create new GameLogic object, it has correct init state
     */
    @Test
    public void initializationTest() {
        assertFalse(game.isThisTheEnd());
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                assertTrue(game.canPlay(i, j));
                assertEquals(Cell.EMPTY, game.getCell(i, j));
            }
        }
        assertEquals(Cell.X, game.getCurrentPlayer());
    }

    /**
     * Checks that if you call startNewGame() in changed game, it will be initialized again
     */
    @Test
    public void startNewGame() {
        game.play(0, 0);
        game.startNewGame();
        initializationTest();
    }

    /**
     * Tries to make one turn
     */
    @Test
    public void makeOneTurn() {
        game.play(0, 0);
        assertEquals(Cell.O, game.getCurrentPlayer());
        assertFalse(game.canPlay(0, 0));
        assertEquals(Cell.X, game.getCell(0, 0));
    }

    /**
     * Tries to make two turns
     */
    @Test
    public void makeTwoTurns() {
        game.play(0, 0);
        game.play(1, 1);
        assertEquals(Cell.X, game.getCurrentPlayer());
        assertFalse(game.canPlay(0, 0));
        assertFalse(game.canPlay(1, 1));
        assertEquals(Cell.X, game.getCell(0, 0));
        assertEquals(Cell.O, game.getCell(1, 1));
    }

    /**
     * Checks that draw is the end of the game
     */
    @Test
    public void draw() {
        game.play(0, 0);
        game.play(1, 0);
        game.play(1, 1);
        game.play(0, 1);
        game.play(2, 0);
        game.play(0, 2);
        game.play(2, 1);
        game.play(2, 2);
        game.play(1, 2);
        assertEquals(GameLogic.GameResult.DRAW, game.getGameResult());
        assertThatCantPlay();
    }

    private void assertThatCantPlay() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                assertFalse(game.canPlay(i, j));
            }
        }
    }

    /**
     * Checks that if there is 3 X in diagonal then X wins
     */
    @Test
    public void mainDiagonalWin() {
        game.play(0, 0);
        game.play(0, 1);
        game.play(1, 1);
        game.play(0, 2);
        game.play(2, 2);
        assertThatCantPlay();
        assertEquals(GameLogic.GameResult.X_WINS, game.getGameResult());
        assertTrue(game.isThisTheEnd());
    }

    /**
     * Checks that if there is 3 X in another diagonal then X wins
     */
    @Test
    public void otherDiagonalWin() {
        game.play(0, 2);
        game.play(0, 1);
        game.play(1, 1);
        game.play(0, 0);
        game.play(2, 0);
        assertThatCantPlay();
        assertEquals(GameLogic.GameResult.X_WINS, game.getGameResult());
        assertTrue(game.isThisTheEnd());
    }

    /**
     * Checks that if there is 3 X in first row then O wins
     */
    @Test
    public void rowWin() {
        game.play(1, 0);
        game.play(0, 0);
        game.play(1, 1);
        game.play(0, 1);
        game.play(2, 0);
        game.play(0, 2);
        assertThatCantPlay();
        assertEquals(GameLogic.GameResult.O_WINS, game.getGameResult());
        assertTrue(game.isThisTheEnd());
    }

    /**
     * Checks that if there is 3 X in second column then X wins
     */
    @Test
    public void columnWin() {
        game.play(0, 1);
        game.play(0, 0);
        game.play(1, 1);
        game.play(2, 2);
        game.play(2, 1);
        assertThatCantPlay();
        assertEquals(GameLogic.GameResult.X_WINS, game.getGameResult());
        assertTrue(game.isThisTheEnd());
    }

    /**
     * Checks that if there is X E X in column then there is win position for X
     */
    @Test
    public void winPositionInColumn() {
        game.play(0, 2);
        game.play(0, 0);
        game.play(2, 2);
        Pair<Integer, Integer> position = game.winPosition(Cell.X);
        assertNull(game.winPosition(Cell.O));
        assertEquals(1, (int) position.getKey());
        assertEquals(2, (int) position.getValue());
    }

    /**
     * Checks that if there is O O E in row then there is win position for O
     */
    @Test
    public void winPositionInRow() {
        game.play(1, 1);
        game.play(0, 0);
        game.play(2, 2);
        game.play(0, 1);
        Pair<Integer, Integer> position = game.winPosition(Cell.O);
        assertNull(game.winPosition(Cell.X));
        assertEquals(0, (int) position.getKey());
        assertEquals(2, (int) position.getValue());
    }

    /**
     * Checks that if there is X E X in diagonal then there is win position for X
     */
    @Test
    public void winPositionInMainDiagonal() {
        game.play(0, 0);
        game.play(0, 2);
        game.play(2, 2);
        Pair<Integer, Integer> position = game.winPosition(Cell.X);
        assertNull(game.winPosition(Cell.O));
        assertEquals(1, (int) position.getKey());
        assertEquals(1, (int) position.getValue());
    }

    /**
     * Checks that if there is X E X in column then there is win position for X
     */
    @Test
    public void winPositionInOtherDiagonal() {
        game.play(0, 2);
        game.play(0, 0);
        game.play(2, 0);
        Pair<Integer, Integer> position = game.winPosition(Cell.X);
        assertNull(game.winPosition(Cell.O));
        assertEquals(1, (int) position.getKey());
        assertEquals(1, (int) position.getValue());
    }
}