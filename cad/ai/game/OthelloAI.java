/*******************
 * Christian A. Duncan
 * Modified by: ENTER YOUR NAMES HERE!!!!!
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
public class OthelloAI extends AbstractAI {
    public OthelloGame game;  // The game that this AI system is playing
    protected Random ran;
    public OthelloGame practiceGame;
    public OthelloAI() {
        game = null;
        ran = new Random();
        practiceGame = new OthelloGame(-1, null, null, false, 0);

    }
    public synchronized void attachGame(Game g) {
        game = (OthelloGame) g;
    }
    
    /**
     * Returns the Move as a String "rc" (e.g. 2b)
     **/
    public synchronized String computeMove() 
    {
        if (game == null) {
           System.err.println("CODE ERROR: AI is not attached to a game.");
           return "0a";


       }

       char[][] board = (char[][]) game.getStateAsObject();

        OthelloGame.Action bestAction = null;
        int bestScore = Integer.MIN_VALUE;
        // First get the list of possible moves
        int player = game.getPlayer(); // Which player are we?
        ArrayList<OthelloGame.Action> actions = game.getActions(player);
        for(OthelloGame.Action a : actions)
        {
            char [][] copyBoard = result(board, a, game.getPlayer());
            int score = maxValue(copyBoard);
            if (score > bestScore) 
            {
                
                bestAction = a;
                bestScore = score;
            }
        }

        // Now just pick of them out at random
        if(player == 1)
        {
        int choice = ran.nextInt(actions.size());
        return actions.get(choice).toString();
        }
        else
        {
            System.out.println("BESTACTION: " + bestAction.toString());
            return actions.get(actions.indexOf(bestAction)).toString();
        }
    }   
    

    public char [][] result (char [][] board, OthelloGame.Action action, int player) {
        char[][] res = (char[][]) board.clone();
        int row = action.row;
        int col = action.col;
        res[row][col] = (player == 0 ? 'X' : 'O');
        return res;
    }

  /**
     * Away wishes to MINimize the score.
     * @param: The board state to determine minimum move
     **/
    private int minValue(char[][] board) 
    {
        int turn = 1 - practiceGame.getPlayer();
        // Is this a terminal board
        practiceGame.updateState(turn,board);
        if (practiceGame.computeWinner()) 
        {
            // We have a winner - return its Utility
            //   1 for Player wins, -1 for Player loses, 0 for Tie
            int w = practiceGame.getWinner();
            return w < 0 ? 0 : w == game.getPlayer() ? 1 : -1;
        }
        int player = game.getPlayer(); 
        ArrayList<OthelloGame.Action> actions = game.getActions(turn);
        // Determine Maximum value among all possible actions
        int bestScore = Integer.MAX_VALUE; // Positive "Infinity"
        for (OthelloGame.Action a : actions) 
        {
            char [][] copyBoard = result(board, a, turn);
            System.out.println("-----------------------------");
            System.out.println("MIN NODESSSS: ");
            for (char[] i : copyBoard) {
                for (char j : i) {
                    System.out.print(j);
                }
                System.out.println();
            }
            bestScore = Math.min(bestScore, maxValue(copyBoard));
            System.out.println("MAX VALUE: " + bestScore);
            System.out.println("-----------------------------");
            System.out.println();
        }
        return bestScore;
    }
     /**
     * Home wishes to MAXimize the score.
     * @param: The board state to determine maximum move
     **/
    private int maxValue(char[] [] board) {
        int turn = practiceGame.getPlayer();
        // Is this a terminal board
        practiceGame.updateState(turn, board);
        if (practiceGame.computeWinner()) 
        {
            // We have a winner - return its Utility
            //   1 for Player wins, -1 for Player loses, 0 for Tie
            int w = practiceGame.getWinner();
            return w < 0 ? 0 : w == game.getPlayer() ? 1 : -1;
        }
        int player = game.getPlayer(); 
        ArrayList<OthelloGame.Action> actions = game.getActions(turn);
        // Determine Maximum value among all possible actions
        int bestScore = Integer.MIN_VALUE; // Negative "Infinity"
        for (OthelloGame.Action a: actions)
        {
            char [][] copyBoard = result(board, a, turn);
            System.out.println("+++++++++++++++++++++++++++++");
            System.out.println("MAX NODESSSS: ");
            for (char[] i : copyBoard) {
                for (char j : i) {
                    System.out.print(j);
                }
                System.out.println();
            }
            bestScore = Math.max(bestScore, minValue(copyBoard));
            System.out.println("MAX VALUE: " + bestScore);
            System.out.println("+++++++++++++++++++++++++++++");
            System.out.println();
        }
        return bestScore;
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
