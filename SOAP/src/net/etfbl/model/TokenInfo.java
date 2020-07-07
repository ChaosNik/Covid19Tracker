package net.etfbl.model;
import java.util.Date;

public class TokenInfo
{
	public static double deltaSpace = 50.0;
	public static long deltaMinutes = 10;
	
	public long token;
	public double x,y;
	public Date datetime;
	
	public TokenInfo(long token, double x, double y, Date datetime)
	{
		this.token = token;
		this.x = x;
		this.y = y;
		this.datetime = datetime;
	}
	
	public boolean wasNear(TokenInfo other)
	{

		double x = Math.abs(other.x - this.x);
		double y = Math.abs(other.y - this.y);
		
		if(Math.hypot(x, y) <= deltaSpace)
			if(Math.abs(datetime.getTime() - other.datetime.getTime()) <= deltaMinutes * 60000)
				return true;
		return false;
	}
}
