package org.unibl.etf.mdp.chat.model;

import java.util.Date;

public class Stat
{
	public String username;
	public Date datetime;
	
	public Stat(){}
	
	public Stat(String username, Date datetime)
	{
		this.username = username;
		this.datetime = datetime;
	}
	public String getUsername()
	{
		return username;
	}
	public Date getDateTime()
	{
		return datetime;
	}
}
