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
    private SimpleBot sbot = new SimpleBot();
    private HardBot hbot = new HardBot();

    /**
     * It is called before any test to initialize game
     */
    @Before
    public void initializeTest() {
        game = new GameLogic();
    }

    @Test
    public void simpleBotCanTakeTurns() {
        sbot.takeTurn(game);
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
        sbot.takeTurn(game);
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                assertEquals(copy[i][j], game.getCell(i, j));
            }
        }
        hbot.takeTurn(game);
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                assertEquals(copy[i][j], game.getCell(i, j));
            }
        }
    }

    @Test
    public void hardBotDontLoose() {
        game.play(0, 0);
        game.play(2, 2);
        game.play(0, 1);
        hbot.takeTurn(game);
        assertEquals(Cell.O, game.getCell(0, 2));
    }

    @Test
    public void hardBotWin() {
        game.play(0,0);
        game.play(1,1);
        game.play(0,1);
        game.play(2,2);
        hbot.takeTurn(game);
        assertEquals(GameResult.X_WINS, game.getGameResult());
    }

    @Test
    public void hardBotAlwaysWin() {
        game.play(0,0);
        game.play(1,0);
        game.play(0,1);
        game.play(1,1);
        hbot.takeTurn(game);
        assertEquals(GameResult.X_WINS, game.getGameResult());
    }

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
        sbot.takeTurn(game);
        assertEquals(GameLogic.GameResult.DRAW, game.getGameResult());
    }


}