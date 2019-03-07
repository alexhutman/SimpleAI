/*******************
* Christian A. Duncan
* Modified by: David Lepore Alex Hutman Steve
* CSC350: Intelligent Systems
* Spring 2019
*
* AI Game Client
* This project is designed to link to a basic Game Server to test
* AI-based solutions.
*
* OthelloAI:
*    This class is the main AI system for the Othello game.
*
* See README file for more details.
********************/
package cad.ai.game;

import java.util.Random;
import java.util.ArrayList;

/***********************************************************
* The AI system for a OthelloGame.
*   Most of the game control is handled by the Server but
*   the move selection is made here - either via user or an attached
*   AI system.
***********************************************************/
public class OthelloAlphaBetaAI extends AbstractAI {
  public OthelloGame game;  // The game that this AI system is playing
  protected Random ran;
  public OthelloGame practiceGame;
  protected int maxDepth = 4;

  public OthelloAlphaBetaAI() {
    game = null;
    ran = new Random();
    practiceGame = new OthelloGame(-1, null, null, false, 0);
  }
  public synchronized void attachGame(Game g) {
    game = (OthelloGame) g;
    System.out.println("Alpha beta ai created as player " + game.getPlayer());
  }

  /**
  * Returns the Move as a String "rc" (e.g. 2b)
  **/
  public synchronized String computeMove()
  {
    if (game == null)
    {
      System.err.println("CODE ERROR: AI is not attached to a game.");
      return "0a";
    }
    char[][] board = (char[][]) game.getStateAsObject();
    OthelloGame.Action bestAction = null;
    int bestScore = Integer.MIN_VALUE;
    // First get the list of possible moves
    int player = game.getPlayer(); // Which player are we?
    int score;
    ArrayList<OthelloGame.Action> actions = game.getActions(player);
    //System.out.println("Depth (computeMove) = " + this.maxDepth);
    for(OthelloGame.Action a : actions)
    {
      char [][] copyBoard = result(board, a, game.getPlayer());
      switch(copyBoard.length)
      {
        case 4:
        score = minValue(copyBoard, Integer.MIN_VALUE, Integer.MAX_VALUE, this.maxDepth*2);
        if (score > bestScore)
        {
          bestAction = a;
          bestScore = score;
        }
        case 6:
        score = minValue(copyBoard, Integer.MIN_VALUE, Integer.MAX_VALUE, this.maxDepth*3);
        if (score > bestScore)
        {
          bestAction = a;
          bestScore = score;
        }
        case 8:
        score = minValue(copyBoard, Integer.MIN_VALUE, Integer.MAX_VALUE, this.maxDepth*3);
        if (score > bestScore)
        {
          bestAction = a;
          bestScore = score;
        }
      }
      System.out.println(this.maxDepth);
    }


    return bestAction.toString();
  }


  public char [][] result (char [][] board, OthelloGame.Action action, int player) {
    practiceGame.updateState(player, board);
    practiceGame.processMove(player, action.row, action.col);
    return (char[][]) practiceGame.getStateAsObject();
  }

  /**
  * Away wishes to MINimize the score.
  * @param: The board state to determine minimum move
  **/
  private int minValue(char[][] board, int alpha, int beta, int depth)
  {
    int curAlpha = alpha;
    int curBeta = beta;

    //System.out.println("Depth (minValue) = " + depth);
    int turn = 1 - game.getPlayer();

    if (depth <= 0) {
      // System.out.println("Max depth of " + depth + ", which " + (maxDepth==depth ? "equals " : "DOESN'T EQUAL ") + maxDepth + " reached");
      int[] boardPieces = countPieces(board);
      countCornerPieces(turn, board);
      return boardPieces[0] > boardPieces[1] ? practiceGame.getHomeScore() - practiceGame.getAwayScore() :
      practiceGame.getAwayScore() - practiceGame.getHomeScore();
    }

    // Is this a terminal board
    practiceGame.updateState(turn,board);
    if (practiceGame.computeWinner())
    {
      int w = practiceGame.getWinner();
      return game.getPlayer() == 0 ? practiceGame.getHomeScore() - practiceGame.getAwayScore() :
      practiceGame.getAwayScore() - practiceGame.getHomeScore();
    }
    ArrayList<OthelloGame.Action> actions = practiceGame.getActions(turn);
    if (actions == null || actions.size() == 0) {
      // No moves
      return maxValue(board, curAlpha, curBeta, depth-1);
    }
    // Determine Maximum value among all possible actions
    int bestScore = Integer.MAX_VALUE; // Positive "Infinity"
    for (OthelloGame.Action a : actions)
    {
      char [][] copyBoard = result(board, a, turn);
      bestScore = Math.min(bestScore, maxValue(copyBoard, curAlpha, curBeta, depth-1));

      if (bestScore <= curAlpha) {
        return bestScore;
      }

      curBeta = Math.min(curBeta, bestScore);
    }
    return bestScore;
  }
  /**
  * Home wishes to MAXimize the score.
  * @param: The board state to determine maximum move
  **/
  private int maxValue(char[] [] board, int alpha, int beta, int depth) {
    int curAlpha = alpha;
    int curBeta = beta;

    //System.out.println("Depth (maxValue) = " + depth);

    int turn = game.getPlayer();

    if (depth <= 0) {
      //System.out.println("Max depth of " + depth + ", which " + (maxDepth==depth ? "equals " : "DOESN'T EQUAL ") + maxDepth + " reached");
      int[] boardPieces = countPieces(board);
      countCornerPieces(turn, board);
      return boardPieces[0] > boardPieces[1] ? practiceGame.getHomeScore() - practiceGame.getAwayScore() :
      practiceGame.getAwayScore() - practiceGame.getHomeScore();
    }
    // Is this a terminal board
    practiceGame.updateState(turn, board);
    if (practiceGame.computeWinner())
    {
      int w = practiceGame.getWinner();
      return game.getPlayer() == 0 ? practiceGame.getHomeScore() - practiceGame.getAwayScore() :
      practiceGame.getAwayScore() - practiceGame.getHomeScore();
    }
    ArrayList<OthelloGame.Action> actions = practiceGame.getActions(turn);
    if (actions == null || actions.size() == 0) {
      // No moves
      return minValue(board, curAlpha, curBeta, depth-1);
    }
    // Determine Maximum value among all possible actions
    int bestScore = Integer.MIN_VALUE; // Negative "Infinity"
    for (OthelloGame.Action a: actions)
    {
      char [][] copyBoard = result(board, a, turn);
      bestScore = Math.max(bestScore, minValue(copyBoard, curAlpha, curBeta, depth-1));

      if (bestScore >= curBeta) {
        return bestScore;
      }

      curAlpha = Math.max(curAlpha, bestScore);
    }
    return bestScore;
  }

  private void countCornerPieces(int player, char[][] board) {
    int boardLength = board[0].length-1;
    int[] lol = new int[4];
    int[] x = {0,boardLength,0,boardLength};
    int[] y = {0,0,boardLength,boardLength};

    //System.out.println("Calling func");

    switch (player) {
      case 0:
      for (int i=0; i<4; i++) {
        System.out.println(player);
        if(board[x[i]][y[i]] == 'X') {
          //System.out.println("Corner (" + x[i] + ", " + y[i] + ") is an X");
          lol[i] = 1;
        }
      }
      case 1:
      for (int i=0; i<4; i++) {
        //System.out.println(player);
        if(board[x[i]][y[i]] == 'O') {
          //System.out.println("Corner (" + x[i] + ", " + y[i] + ") is an O");
          lol[i] = 1;
        }
      }
    }

    if(player==0){
      System.out.println("------------");

      System.out.println("Player " + player);
      System.out.println("Board:");

      for (char[] je : board) {
        for (char ff : je) {
          System.out.print(ff);
        }
        System.out.println("");
      }

      for(int jef : lol) {
        System.out.print(jef + ", ");
      }
      System.out.println("------------");
      System.exit(0);
    }
    //return lol;
  }

  private int[] countPieces(char [][] board) {
    int numX = 0;
    int numO = 0;
    int numBlank = 0;

    for (char[] je : board) {
      for (char ff : je) {
        switch (ff) {
          case 'X': numX++;
          case 'O': numO++;
          case ' ': numBlank++;
        }
      }
    }

    return new int[] {numX, numO, numBlank};
  }

  /**
  * Inform AI who the winner is
  *   result is either (H)ome win, (A)way win, (T)ie
  **/
  @Override
  public synchronized void postWinner(char result) {
    // This AI probably wants to store what it has learned
    // about this particular game.
    game = null;  // No longer playing a game though.
  }

  /**
  * Shutdown the AI - allowing it to save its learned experience
  **/
  @Override
  public synchronized void end() {
    // This AI probably wants to store (in a file) what
    // it has learned from playing all the games so far...
  }
}
