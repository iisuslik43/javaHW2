package ru.iisuslik.tictactoe;

import org.jetbrains.annotations.NotNull;

/**
 * Bot for TicTacToe-- that can take turns
 */
public interface Bot {
    /**
     * Let bot take some turn in current game
     *
     * @param game - Logic class, where turn will be saved
     * @param main - Activity, where it will be showed
     */
    void takeTurn(@NotNull GameLogic game, @NotNull Main main);

    /**
     * This is short bot name(Hard bot or Simple bot)
     *
     * @return Bot's name
     */
    String getName();
}
