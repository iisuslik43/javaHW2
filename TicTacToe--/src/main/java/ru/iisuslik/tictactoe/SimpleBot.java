package ru.iisuslik.tictactoe;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * Bot interface realization. It puts O in random cell, if this cell is available.
 */
public class SimpleBot implements Bot {

    /**
     * {@link Bot#getName()}
     */
    @Override
    public String getName() {
        return "Simple bot";
    }

    /**
     * {@link Bot#takeTurn(GameLogic)}
     */
    @Override
    public void takeTurn(@NotNull GameLogic game) {
        if (game.isThisTheEnd())
            return;
        Random rand = new Random();
        while (true) {
            int i = rand.nextInt() % GameLogic.SIZE;
            int j = rand.nextInt() % GameLogic.SIZE;
            if (game.canPlay(i, j)) {
                game.play(i, j);
                break;
            }
        }
    }
}
