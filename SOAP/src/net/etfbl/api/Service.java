package net.etfbl.api;

import java.util.Date;

import net.etfbl.model.Database;
import net.etfbl.model.Stat;
import net.etfbl.model.User;

public class Service
{
	public long register(String name, String surname, String password, long UUID, boolean blocked)
	{
		return Database.addUser(new User(name, surname, password, 0, UUID, blocked));
	}
	public boolean unregister(long token)
	{
		return Database.removeUser(token);
	}
	public boolean isValid(long token)
	{
		return Database.isValid(token);
	}
	public boolean blockUser(long token)
	{
		return Database.blockUser(token);
	}
	public boolean login(long token)
	{
		if(!Database.isValid(token))
			return false;
		if(Database.getUser(token).blocked)
			return false;
		return Database.addStat(new Stat("" + token, new Date()));
	}
	public boolean logout(long token)
	{
		if(!Database.isValid(token))
			return false;
		//if(Database.getUser(token).blocked)
		//	return false;
		return Database.addStat(new Stat("" + token, new Date()));
	}
}