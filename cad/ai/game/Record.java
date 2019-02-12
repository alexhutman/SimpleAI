package cad.ai.game;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;
public class Record 
{
	String record;
	int player;
	double score = 0;
	public static double INITIAL_SCORE = 0.5; // could be 0.8
	public static double alpha = 0.2; //Blending value 
	public Record (int player, String record)
	{
		this.player = player;
		this.record = record;
		score = INITIAL_SCORE; 
	}
	public Record( int wins, int losses, int ties)
	{
		this.record = "";
		for(int i = 0; i < wins; i++)
		{
			this.record = this.record + "H";
			if(this.player == 0)
				{
					this.score = this.score  * (1 - alpha) + 1 * alpha; 
				}
				else
				{
					this.score  = this.score  * (1 - alpha) + 0 * alpha; 
				}
			
		}
		for(int i = 0; i < losses; i++)
		{
			this.record = this.record + "A";
			if(this.player == 0)
				{
					this.score  = this.score  * (1 - alpha) + 0 * alpha; 
				}
				else
				{
					this.score  = this.score  * (1 - alpha) + 1 * alpha; 
				}
		}
		for(int i = 0; i < ties; i++)
		{
			this.record = this.record + "T";
			this.score  = this.score  * (1 - alpha) + 0.5 * alpha; 
		}
	}
	public void updateRecord(String updated)
	{
		this.record = this.record + updated;
	}
	public double getScore() 
	{
		//this.score = INITIAL_SCORE;
		return score; 
	}
	public String ReturnRecord()
	{
		int winWeight = 3; //in case we experiment and tweak later on
		int home = 0, away = 0, ties =0;
		for(int i = 0; i< this.record.length(); i++)
		{
			if(this.record.charAt(i) == 'H')
			{
				home++;
				
			}
			if(this.record.charAt(i) == 'A')
			{
				away++;
				
			}
			if(this.record.charAt(i) == 'T')
			{
				ties++;
				
			}
		}
		if(this.player == 0)
			return ""+home+"-"+away+"-"+ties;
		else
			return ""+away+"-"+home+"-"+ties;
	}
}