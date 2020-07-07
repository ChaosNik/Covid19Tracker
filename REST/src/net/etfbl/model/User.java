package net.etfbl.model;

public class User
{
	public String name;
	public String surname;
	public String password;
	public long UUID;
	public long token;
	public boolean blocked;
	public boolean infected;
	public boolean potentialyInfected;
	public boolean notInfected;
	public User()
	{
		name = "";
		surname = "";
		password = "";
		UUID = 0;
		token = 0;
		blocked = false;
	}
	public User(String name, String surname, String password, long UUID, long token, boolean blocked)
	{
		this.name = name;
		this.surname = surname;
		this.password = password;
		this.UUID = UUID;
		this.token = token;
		this.blocked = blocked;
		this.infected = false;
		this.notInfected();
	}
	public void block()
	{
		blocked = true;
	}
	public void infected()
	{
		infected = true;
		potentialyInfected = false;
		notInfected = false;
	}
	public void potentialyInfected()
	{
		infected = false;
		potentialyInfected = true;
		notInfected = false;
	}
	public void notInfected()
	{
		infected = false;
		potentialyInfected = false;
		notInfected = true;
	}	
}
