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
  protected int[][] weightedScores = {
    {100 , -10 , 8  ,  6 ,  6 , 8  , -10 ,  100},
    {-10 , -25 ,  -4, -4 , -4 , -4 , -25 , -10 },
    {8   ,  -4 ,   6,   4,   4,   6,  -4 ,  8  },
    {6   ,  -4 ,   4,   0,   0,   4,  -4 ,  6  },
    {6   ,  -4 ,   4,   0,   0,   4,  -4 ,  6  },
    {8   ,  -4 ,   6,   4,   4,   6,  -4 ,  8  },
    {-10 , -25 ,  -4, -4 , -4 , -4 , -25 , -10 },
    {100 , -10 , 8  ,  6 ,  6 , 8  , -10 ,  100}};

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
          score = minValue(copyBoard, Integer.MIN_VALUE, Integer.MAX_VALUE, 10);
          if (score > bestScore)
          {
            bestAction = a;
            bestScore = score;
          }
        }
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
        //countCornerPieces(turn, board);
        int myScore = evalBoard(turn,board);
        int oppScore = -1*myScore;

        if(myScore > oppScore) {
          return boardPieces[0] > boardPieces[1] ? practiceGame.getHomeScore() - practiceGame.getAwayScore() :
          practiceGame.getAwayScore() - practiceGame.getHomeScore();
        }
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
        //countCornerPieces(turn, board);
        int myScore = evalBoard(turn,board);
        int oppScore = -1*myScore;

        if(myScore > oppScore) {
          return boardPieces[0] > boardPieces[1] ? practiceGame.getHomeScore() - practiceGame.getAwayScore() :
          practiceGame.getAwayScore() - practiceGame.getHomeScore();
        }
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

    //  private int evalBoard2(int player, char [] [] board)
    //  {
    //    int numOfPermPieces = countPermPieces(countCornerPieces(player, board), board, player);
    //    int numOfNonPermPieces = countPieces(board);
    //  }
    // private int countPermPieces(int [] corners, int [] [] board, int player)
    // {
    //   int numOfPermPieces = 0;
    //   for(int x = 0; x < corners.length; x++)
    //   {
    //     if(corners[x] == 1)
    //     {
    //       for(int i = 0; i < board.length; i++)
    //       {
    //         int n = i;
    //          if(x == 0)
    //          {
    //
    //          }
    //          if(x == 1)
    //          {
    //
    //          }
    //          if(x == 2)
    //          {
    //
    //          }
    //          if(x == 3)
    //          {
    //
    //          }
    //       }
    //     }
    //   }
    // }

    public int evalBoard(int player, char[][] board){
      int opponent = 1-player;

      char myChar = (player == 0) ? 'X' : 'O';
      char oppChar = (opponent == 1) ? 'O' : 'X';

      int myScore = 0, oppScore = 0;

      for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
          if(board[i][j]==myChar) myScore += weightedScores[i][j];
          if(board[i][j]==oppChar) oppScore += weightedScores[i][j];
        }
      }
      return myScore - oppScore;
    }

    // private int [] countCornerPieces(int player, char[][] board) {
    //   int boardLength = board[0].length-1;
    //   int[] lol = new int[4];
    //   int[] x = {0,boardLength,0,boardLength};
    //   int[] y = {0,0,boardLength,boardLength};
    //     for (int i=0; i<4; i++)
    //     {
    //       if(board[x[i]][y[i]] == 'X' && player == 0) {
    //         System.out.println("Corner (" + x[i] + ", " + y[i] + ") is an X");
    //         lol[i] = 1;
    //       }
    //       if(board[x[i]][y[i]] == 'O' && player == 1) {
    //         System.out.println("Corner (" + x[i] + ", " + y[i] + ") is an O");
    //         lol[i] = 1;
    //       }
    //     }
    // }

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
