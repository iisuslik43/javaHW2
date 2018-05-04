package ru.iisuslik.pairs;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameLogicTest {

  private GameLogic game;
  int N = 4;

  @Before
  public void init() {
    game = new GameLogic(N);
  }

  @Test
  public void initTest() {
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N; j++) {
        assertFalse(game.isPressed(i, j));
        int value = game.getCell(i, j);
        assertTrue(value >= 0 && value < N * N / 2);
      }
    }
  }

  @Test
  public void onlyTwo() {
    int[] counts = new int[N * N / 2];
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N; j++) {
        int value = game.getCell(i, j);
        counts[value]++;
      }
    }
    for (int i = 0; i < N * N / 2; i++) {
      assertEquals(2, counts[i]);
    }
  }

  @Test
  public void openWorks() {
    game.open(0, 0);
    assertTrue(game.isPressed(0, 0));
  }

  @Test
  public void correctOpening() {
    int i1, j1, i2, j2;
    i1 = j1 = i2 = j2 = -1;
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N; j++) {
        if(game.getCell(i, j) == 0) {
          if(i1 == -1) {
            i1 = i;
            j1 = j;
          } else {
            i2 = i;
            j2 = j;
          }
        }
      }
    }
    game.open(i1, j1);
    game.open(i2, j2);
    assertTrue(game.isPressed(i1, j1));
    assertTrue(game.isPressed(i2, j2));
  }

  @Test
  public void wrongOpening() {
    int i1, j1, i2, j2;
    i1 = j1 = i2 = j2 = -1;
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N; j++) {
        if(game.getCell(i, j) == 0) {
          if(i1 == -1) {
            i1 = i;
            j1 = j;
          } else {
            i2 = i;
            j2 = j;
          }
        }
      }
    }
    game.open(i1, j1);
    game.open((i2 + 1) % N, j2);
    assertFalse(game.isPressed(i1, j1));
    assertFalse(game.isPressed((i2 + 1) % N, j2));
  }

  private void print(GameLogic game) {
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N; j++) {
        System.out.print(game.getCell(i, j) + " ");
      }
      System.out.println();
    }
  }

}