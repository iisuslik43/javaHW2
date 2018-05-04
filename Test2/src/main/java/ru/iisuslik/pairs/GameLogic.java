package ru.iisuslik.pairs;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class for game logic of Pairs
 */
public class GameLogic {
  private int[][] field;
  private boolean[][] pressed;
  private int N;
  private int iPrev = -1;
  private int jPrev = -1;
  private int restCells;

  /**
   * Creates new game logic
   *
   * @param size field size
   */
  public GameLogic(int size) {
    N = size;
    field = new int[N][N];
    pressed = new boolean[N][N];
    restCells = N * N;
    initializeGameField();
  }

  private void initializeGameField() {
    ArrayList<Integer> shuffle = new ArrayList<>();
    for (int i = 0; i < N * N / 2; i++) {
      shuffle.add(i);
    }
    for (int i = 0; i < N * N / 2; i++) {
      shuffle.add(i);
    }
    Collections.shuffle(shuffle);
    for (int i = 0; i < N * N; i++) {
      field[i / N][i % N] = shuffle.get(i);
    }
  }

  public void open(int i, int j) {
    if (pressed[i][j]) {
      return;
    }
    pressed[i][j] = true;
    if (iPrev == -1) {
      iPrev = i;
      jPrev = j;
    } else {
      if (field[i][j] != field[iPrev][jPrev]) {
        pressed[i][j] = pressed[iPrev][jPrev] = false;
        iPrev = -1;
        jPrev = -1;
      } else {
        iPrev = -1;
        jPrev = -1;
        restCells -= 2;
      }
    }
  }

  /**
   * Get content of cell
   *
   * @return i j cell value
   */
  public int getCell(int i, int j) {
    return field[i][j];
  }

  /**
   * Get is button pressed
   *
   * @return true if this button is pressed
   */
  public boolean isPressed(int i, int j) {
    return pressed[i][j];
  }

  /**
   * Get field size
   *
   * @return size
   */
  public int getN() {
    return N;
  }

  /**
   * Get is game over
   *
   * @return true if game is over
   */
  public boolean gameOver() {
    return restCells == 0;
  }
}
