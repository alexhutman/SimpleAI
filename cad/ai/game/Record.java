package cad.ai.game;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;

public class Record 
{
	//Test
	String record;
	int player;
	public double score = 0;
	public static double INITIAL_SCORE = 0.5; // could be 0.8
	public static double alpha = 0.2; //Blending value 
	public int wins = 0;
	public int losses = 0;
	public int ties = 0;
	public Record(int player, boolean isAtEnd)
	{
		this.score = INITIAL_SCORE;
		
	}
	public Record (int player, String result)
	{
		this.player = player;
		this.score = INITIAL_SCORE;
			updateRecord(result);
		this.record = ReturnRecord();
	}
	public Record( int wins, int losses, int ties, double score)
	{
		this.wins = wins;
		this.losses = losses;
		this.ties = ties;
		this.score = score;
		this.record = ReturnRecord(); 
	}
	public void updateRecord(String result)
	{
		if(this.player == 0)
		{
			if(result.equals("H"))
			{
				this.wins++;
				this.score = this.score * (1 - alpha) + 1 * alpha; 
			}
			else if(result.equals("A"))
			{
				this.losses++;
				this.score = this.score * (1 - alpha) + 0 * alpha; 
			}
			else
			{
				this.ties++;
				this.score = this.score * (1 - alpha) + .5 * alpha; 
			}
		}
		else
		{
			if(result.equals("H"))
			{
				this.losses++;
				this.score = this.score * (1 - alpha) + 0 * alpha;  
			}
			else if(result.equals("A"))
			{
				this.wins++;
				this.score = this.score * (1 - alpha) + 1 * alpha; 
			}
			else
			{
				this.ties++;
				this.score = this.score * (1 - alpha) + .5 * alpha; 
			}
		}
	}
	public double getScore() 
	{
		return score; 
	}
	public String ReturnRecord()
	{
		return ""+this.wins+"-"+this.losses+"-"+this.ties;
	}
	public String ReturnScore()
	{
		return ""+score+"";
	}
}