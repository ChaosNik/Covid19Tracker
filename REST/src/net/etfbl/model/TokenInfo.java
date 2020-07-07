package net.etfbl.model;
import java.util.Date;

public class TokenInfo
{
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
		/*double r = 6378137; //Earths radius
		
		double dLat = Math.PI * (x - other.x) / 180;
		double dLon = Math.PI * (y - other.y) / 180;
		
		double dx = Math.abs(dLat * r);
		double dy = Math.abs(dLon * r * Math.cos(Math.PI * x / 180));*/
		double dx = Math.abs(x - other.x);
		double dy = Math.abs(y - other.y);
		
		if(Math.hypot(dx, dy) <= Database.k)
			if(Math.abs(datetime.getTime() - other.datetime.getTime()) <= Database.p * 60000)
				return true;
		return false;
	}
}
