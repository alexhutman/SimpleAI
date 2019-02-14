/*******************
 * Christian A. Duncan
 * CSC350: Intelligent Systems
 * Spring 2019
 *
 * AI Game Client
 * This project is designed to link to a basic Game Server to test
 * AI-based solutions.
 * See README file for more details.
 ********************/
package cad.ai.game;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.*;

/***********************************************************
 * The AI system for a TicTacToeGame.
 *   Most of the game control is handled by the Server but
 *   the move selection is made here - either via user or an attached
 *   AI system.
 ***********************************************************/
public class TicTacToeAI extends AbstractAI {
    public TicTacToeGame game;  // The game that this AI system is playing
    protected Random ran;
    protected Map<String, Record> hmap;
    protected Stack<String> setOfMoves ;
    protected BufferedReader reader;
    protected String fileName;
    private String pattern = "#([0-9]+)-([0-9]+)-([0-9]+)#([0-9]+.[0-9]+(E[0-9]+)?)";
    private Pattern r;
    private Matcher m;
	private int numGames = 0;
	private double balancer = 0.0;
    public TicTacToeAI(String fileName) 
    {
    	this.fileName = fileName;
    	game = null;
    	ran = new Random();
    	hmap = new HashMap<String, Record>();
    	setOfMoves =  new Stack<>();
    	this.r = Pattern.compile(this.pattern);
    	String line;
    	String boardState;
    	int wins, losses, ties;
		double score;
    	try {
    		reader = new BufferedReader(new FileReader(this.fileName));
    		while ((line = reader.readLine()) != null) {
    			this.m = this.r.matcher(line);
    			if (m.find()) {
    				boardState = line.substring(0,9);
    				wins = Integer.parseInt(m.group(1));
    				losses = Integer.parseInt(m.group(2));
    				ties = Integer.parseInt(m.group(3));
					String strscore = m.group(4);
					score = Double.valueOf(strscore);
    				Record r = new Record(wins,losses,ties,score);
					//System.out.println(r.ReturnRecord()+" "+r.ReturnScore());
    				hmap.put(boardState,r);
    			}
    			else {
    				System.out.println("oh dear.");
    			}
    		}
    		reader.close(); 	
    	}
    	catch (Exception jeff) {
    		System.out.println("ERROR:" + jeff);
    	}
		for(int i = 0; i < this.numGames; i++)
		{
			balancer += .001;
		}
    }

    public synchronized void attachGame(Game g) {
    	game = (TicTacToeGame) g;
    }
    
    /**
     * Returns the Move as a String "S"
     *    S=Slot chosen (0-8)
     **/
    public synchronized String computeMove() 
    {
    	if (game == null) {
    		System.err.println("CODE ERROR: AI is not attached to a game.");
    		return "0";
    	}

    	char[] board = (char[]) game.getStateAsObject();


		//score*probablity(random)+delta
        // First see how many open slots there are
    	int openSlots = 0;
    	int i = 0;
    	for (i = 0; i < board.length; i++)
    		if (board[i] == ' ')
    		{				
    			openSlots++;
    		}
		 for(int m = 0; m < openSlots; m++)
		{
			balancer += .05;
		} 
    		if(game.getPlayer() == 0 && hmap.isEmpty() == false)
    		{
    			String curboard = new String(board);
    			double maxScore = 0;
    			String str = new String("");
    			for(int x = 0; x < curboard.length(); x++)
    			{
					double randomNum = Math.random()-balancer;
    				if(curboard.charAt(x) == ' ')
    				{
    					StringBuilder strbld = new StringBuilder(curboard);
    					strbld.setCharAt(x,'X');
    					str = strbld.toString();

    					if(hmap.containsKey(str))
    					{

    						if((hmap.get(str).getScore() + (randomNum-balancer)) > maxScore)
							{
							i = x;
							maxScore = hmap.get(str).getScore();
							} 
							else
							{
								i = -1;
							}
    					}
						else 
						{
							Record newRecord = new Record(0, false);
							hmap.put(str, newRecord);
						}
    				}
    				else
    				{
						
    					i = -1;
    				}
    			}
    		}
    		else
    		{
    			i = -1;
    		}  

    		if(i == -1 || game.getPlayer() == 1)
    		{
			// Now pick a random open slot
    			int s = ran.nextInt(openSlots);
			// And get the proper row
    			i = -1;
    			while (s >= 0) 
    			{
    				i++;
				if (board[i] == ' ') s--;  // One more open slot down
			}
		}
		
		if(game.getPlayer() == 0)
		{
			board[i] = 'X';
		}
		else
		{
			board[i] = 'O';
		}
		setOfMoves.push(new String (board));
		return "" + i;
	}

    /**
     * Inform AI who the winner is
     *   result is either (H)ome win, (A)way win, (T)ie
     **/
    @Override
    public synchronized void postWinner(char result) 
    {
        // This AI probably wants to store what it has learned
        // about this particular game.
    	String [] setOfMovesArr = setOfMoves.toArray(new String [0]);
    	setOfMoves.clear();
    	int player = game.getPlayer();

    	for(int i = 0; i < setOfMovesArr.length; i++)
    	{
    		if(hmap.containsKey(setOfMovesArr[i]))
    		{

    			hmap.get(setOfMovesArr[i]).updateRecord(Character.toString(result));


    		}
    		else
    		{
    			Record newRecord = new Record(player, Character.toString(result));
    			hmap.put(setOfMovesArr[i], newRecord); 


    		}

    	} 
		this.numGames ++;
        game = null;  // No longer playing a game though.
    }

    /**
     * Shutdown the AI - allowing it to save its learned experience
     **/
    @Override
    public synchronized void end() 
    {

        // This AI probably wants to store (in a file) what
        // it has learned from playing all the games so far...
    	try {
    		FileWriter fw = new FileWriter(this.fileName, false);
    		BufferedWriter bw = new BufferedWriter(fw);
    		PrintWriter out = new PrintWriter(bw);
    		Object [] keys = hmap.keySet().toArray();
    		for (int i = 0; i < keys.length; i++) 
    		{
    			char separator = '#';
    			Record x = hmap.get(keys[i].toString());

    			out.print(keys[i].toString());
    			out.print(separator);
    			out.print(x.ReturnRecord());
    			out.print(separator);
				out.print(x.ReturnScore());
    			out.println();
    		}
    		out.flush();  
    		out.close(); 
    	} 
    	catch (Exception meme) 
    	{
    		System.out.println("ERROR: " + meme);
    	}


    }
}

