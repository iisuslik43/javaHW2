package ru.iisuslik.pairs;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 
 */
public class GameLogic {
  private int[][] field;
  private boolean[][] pressed;
  private int N;
  private int iPrev = -1;
  private int jPrev = -1;
  private int restCells;

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

  public int getCell(int i, int j) {
    return field[i][j];
  }

  public boolean isPressed(int i, int j) {
    return pressed[i][j];
  }

  public int getN() {
    return N;
  }

  public boolean gameOver() {
    return restCells == 0;
  }
}
