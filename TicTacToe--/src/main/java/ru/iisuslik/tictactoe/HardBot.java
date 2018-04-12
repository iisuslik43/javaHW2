package ru.iisuslik.tictactoe;

import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Bot interface realization. This bot will win in one turn, if it's possible,
 * and will never loose in this turn, if it's possible too. In another situations
 * it put O in random available cell.
 */
public class HardBot implements Bot {
    /**
     * {@link Bot#getName()}
     */
    @Override
    public String getName() {
        return "Hard bot";
    }

    /**
     * {@link Bot#takeTurn(GameLogic)}
     */
    @Override
    public void takeTurn(@NotNull GameLogic game) {
        if (game.isThisTheEnd())
            return;
        if (!win(game) && !dontLoose(game)) {
            new SimpleBot().takeTurn(game);
        }
    }

    private boolean win(GameLogic game) {
        Pair<Integer, Integer> winPos = game.winPosition(game.getCurrentPlayer());
        if (winPos != null) {
            game.play(winPos.getKey(), winPos.getValue());
            return true;
        }
        return false;
    }

    private boolean dontLoose(GameLogic game) {

        Pair<Integer, Integer> winPos = game.winPosition(game.getCurrentPlayer().getOpposit());
        if (winPos != null) {
            game.play(winPos.getKey(), winPos.getValue());
            return true;
        }
        return false;
    }
}
