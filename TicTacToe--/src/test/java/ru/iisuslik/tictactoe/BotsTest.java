package ru.iisuslik.tictactoe;

import org.junit.Before;
import org.junit.Test;
import ru.iisuslik.tictactoe.GameLogic.*;

import static org.junit.Assert.*;
import static ru.iisuslik.tictactoe.GameLogic.SIZE;

/**
 * Some tests for simple and hard bot
 */
public class BotsTest {

    private GameLogic game;
    private SimpleBot simpleBot = new SimpleBot();
    private HardBot hardBot = new HardBot();

    /**
     * It is called before any test to initialize game
     */
    @Before
    public void initializeTest() {
        game = new GameLogic();
    }

    /**
     * Check that simple bot take turns somehow
     */
    @Test
    public void simpleBotCanTakeTurns() {
        simpleBot.takeTurn(game);
        int turnCount = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (game.getCell(i, j) != Cell.EMPTY) {
                    turnCount++;
                }
            }
        }
        assertEquals(1, turnCount);
    }


    /**
     * Check that bots don't do anything bad if they try to take turn after game over
     */
    @Test
    public void botTakeTurnAfterGameEnd() {
        game.play(0, 1);
        game.play(0, 0);
        game.play(1, 1);
        game.play(2, 2);
        game.play(2, 1);
        assertTrue(game.isThisTheEnd());
        Cell[][] copy = new Cell[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                copy[i][j] = game.getCell(i, j);
            }
        }
        simpleBot.takeTurn(game);
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                assertEquals(copy[i][j], game.getCell(i, j));
            }
        }
        hardBot.takeTurn(game);
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                assertEquals(copy[i][j], game.getCell(i, j));
            }
        }
    }

    /**
     * Check that hard bot won't loose if he can
     */
    @Test
    public void hardBotDontLoose() {
        game.play(0, 0);
        game.play(2, 2);
        game.play(0, 1);
        hardBot.takeTurn(game);
        assertEquals(Cell.O, game.getCell(0, 2));
    }

    /**
     * Check that hard bot will win if he can
     */
    @Test
    public void hardBotWin() {
        game.play(0,0);
        game.play(1,1);
        game.play(0,1);
        game.play(2,2);
        hardBot.takeTurn(game);
        assertEquals(GameResult.X_WINS, game.getGameResult());
    }

    /**
     * Check that hard bot wants to win more, then not to loose
     */
    @Test
    public void hardBotAlwaysWin() {
        game.play(0,0);
        game.play(1,0);
        game.play(0,1);
        game.play(1,1);
        hardBot.takeTurn(game);
        assertEquals(GameResult.X_WINS, game.getGameResult());
    }

    /**
     * Check that simple bot will take turn if there is only one empty cell
     */
    @Test
    public void simpleBotAlwaysTakeTurn() {
        game.play(0, 0);
        game.play(1, 0);
        game.play(1, 1);
        game.play(0, 1);
        game.play(2, 0);
        game.play(0, 2);
        game.play(2, 1);
        game.play(2, 2);
        simpleBot.takeTurn(game);
        assertEquals(GameLogic.GameResult.DRAW, game.getGameResult());
    }


}