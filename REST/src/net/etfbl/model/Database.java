package net.etfbl.model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.FileHandler;
import java.util.logging.Level;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class Database
{
	private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("logger");
	private static String REDIS_ADDRESS = "127.0.0.1";
	private static int REDIS_PORT = 6379;
	public static int n = 10; //refresh time
	public static int p = 60; //time difference
	public static int k = 5; //distance
	static
	{
		FileReader reader;  
	      
	    Properties p=new Properties();  
	    try
	    {
	    	reader=new FileReader("config.properties"); 
			p.load(reader);
		}
	    catch (IOException e)
	    {
			e.printStackTrace();
		}  
	    REDIS_ADDRESS = p.getProperty("REDIS_ADDRESS");
	    REDIS_PORT = Integer.parseInt(p.getProperty("REDIS_PORT"));
	    Database.n = Integer.parseInt(p.getProperty("N"));
	    Database.p = Integer.parseInt(p.getProperty("P"));
	    Database.k = Integer.parseInt(p.getProperty("K"));
	    
		try
		{
			File logdir = new File("log");
			logdir.mkdir();
			logger.addHandler(new FileHandler("log/db.txt", true));
		}
		catch (Exception e)
		{
			logger.log(Level.INFO, e.toString(), e);
		}
		
		JedisPool pool = new JedisPool(REDIS_ADDRESS, REDIS_PORT);
		try (Jedis jedis = pool.getResource())
		{
			jedis.flushAll();
		}
		catch(Exception e)
		{
			logger.log(Level.INFO, e.toString(), e);
		}
		finally
		{
			pool.close();
		}
		
		addUser(new User("miki", "miki", "miki", 1234, 1, false));
		addUser(new User("muki", "muki", "muki", 4315, 2, false));
		addUser(new User("maki", "maki", "maki", 4567, 3, false));
		addUser(new User("moki", "moki", "moki", 2747, 4, false));
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(2019, 11, 10, 10, 0, 0);
		addStat(new Stat("1", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 5, 0);
		addStat(new Stat("1", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 10, 0);
		addStat(new Stat("1", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 15, 0);
		addStat(new Stat("1", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 20, 0);
		addStat(new Stat("1", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 25, 0);
		addStat(new Stat("1", calendar.getTime()));

		calendar.set(2019, 11, 10, 10, 0, 10);
		addStat(new Stat("2", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 5, 10);
		addStat(new Stat("2", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 10, 10);
		addStat(new Stat("2", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 15, 10);
		addStat(new Stat("2", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 20, 10);
		addStat(new Stat("2", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 25, 10);
		addStat(new Stat("2", calendar.getTime()));

		calendar.set(2019, 11, 10, 10, 0, 20);
		addStat(new Stat("3", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 5, 20);
		addStat(new Stat("3", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 10, 20);
		addStat(new Stat("3", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 15, 20);
		addStat(new Stat("3", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 20, 20);
		addStat(new Stat("3", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 25, 20);
		addStat(new Stat("3", calendar.getTime()));

		calendar.set(2019, 11, 10, 10, 0, 30);
		addStat(new Stat("4", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 5, 30);
		addStat(new Stat("4", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 10, 30);
		addStat(new Stat("4", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 15, 30);
		addStat(new Stat("4", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 20, 30);
		addStat(new Stat("4", calendar.getTime()));
		calendar.set(2019, 11, 10, 10, 25, 30);
		addStat(new Stat("4", calendar.getTime()));
		
		calendar.set(2019, 11, 10, 10, 45, 30);
		addTokenInfo((new TokenInfo(1, 14, 16, calendar.getTime())));
		calendar.set(2019, 11, 10, 10, 45, 30);
		addTokenInfo((new TokenInfo(1, 11, 9, calendar.getTime())));
		
		calendar.set(2019, 11, 10, 10, 45, 30);
		addTokenInfo((new TokenInfo(2, 14, 16, calendar.getTime())));
		calendar.set(2019, 11, 15, 10, 45, 30);
		addTokenInfo((new TokenInfo(2, 14, 16, calendar.getTime())));
		
		calendar.set(2019, 11, 10, 10, 45, 30);
		addTokenInfo((new TokenInfo(3, 11, 9, calendar.getTime())));
		calendar.set(2019, 11, 20, 10, 45, 30);
		addTokenInfo((new TokenInfo(3, 14, 16, calendar.getTime())));
		
		calendar.set(2019, 11, 10, 17, 45, 00);
		addTokenInfo((new TokenInfo(4, 12, 8, calendar.getTime())));
		calendar.set(2019, 11, 25, 10, 45, 30);
		addTokenInfo((new TokenInfo(4, 14, 16, calendar.getTime())));
		
		//setInfected(2, 1);
	}
	public static User getUser(long token)
	{
		logger.log(Level.INFO, "getUser");
		User user = null;
		JedisPool pool = new JedisPool(REDIS_ADDRESS, REDIS_PORT);
		try
		{
			Jedis jedis = pool.getResource();
			String name = jedis.get("database:user:" + token + ":name");
			String surname = jedis.get("database:user:" + token + ":surname");
			String password = jedis.get("database:user:" + token + ":password");
			long UUID = Long.parseLong(jedis.get("database:user:" + token + ":uuid"));
			boolean blocked = "true".equals(jedis.get("database:user:" + token + ":blocked"));
			boolean infected = "true".equals(jedis.get("database:user:" + token + ":infected"));
			boolean potentialyInfected = "true".equals(jedis.get("database:user:" + token + ":potentialyInfected"));
			boolean notInfected = "true".equals(jedis.get("database:user:" + token + ":notInfected"));
			user = new User(name, surname, password, UUID, token, blocked);
			user.infected = infected;
			user.potentialyInfected = potentialyInfected;
			user.notInfected = notInfected;
		}
		catch(Exception e)
		{
			logger.log(Level.INFO, e.toString(), e);
			return null;
		}
		finally
		{
			pool.close();
		}
		return user;
	}
	public static long addUser(User user)
	{
		logger.log(Level.INFO, "addUser");
		long token = 0;
		for(long item : getTokens())
			if(item > token)
				token = item;
		token++;
		JedisPool pool = new JedisPool(REDIS_ADDRESS, REDIS_PORT);
		try (Jedis jedis = pool.getResource())
		{
			jedis.sadd("tokens-", "" + user.token);
			jedis.set("database:user:" + user.token, "" + user.token);
			jedis.set("database:user:" + user.token + ":name", user.name);
			jedis.set("database:user:" + user.token + ":surname", "" + user.surname);
			jedis.set("database:user:" + user.token + ":password", "" + user.password);
			jedis.set("database:user:" + user.token + ":uuid", "" + user.UUID);
			jedis.set("database:user:" + user.token + ":blocked", "" + user.blocked);
			jedis.set("database:user:" + user.token + ":infected", "" + user.infected);
			jedis.set("database:user:" + user.token + ":potentialyInfected", "" + user.potentialyInfected);
			jedis.set("database:user:" + user.token + ":notInfected", "" + user.notInfected);
		}
		catch(Exception e)
		{
			logger.log(Level.INFO, e.toString(), e);
			return 0;
		}
		finally
		{
			pool.close();
		}
		return token;
	}
	public static boolean editUser(User user)
	{
		logger.log(Level.INFO, "editUser");
		JedisPool pool = new JedisPool(REDIS_ADDRESS, REDIS_PORT);
		try (Jedis jedis = pool.getResource())
		{
			jedis.del("database:user:" + user.token + ":name", user.name);
			jedis.del("database:user:" + user.token + ":surname", "" + user.surname);
			jedis.del("database:user:" + user.token + ":password", "" + user.password);
			jedis.del("database:user:" + user.token + ":uuid", "" + user.UUID);
			jedis.del("database:user:" + user.token + ":blocked", "" + user.blocked);
			jedis.del("database:user:" + user.token + ":infected", "" + user.infected);
			jedis.del("database:user:" + user.token + ":potentialyInfected", "" + user.potentialyInfected);
			jedis.del("database:user:" + user.token + ":notInfected", "" + user.notInfected);
			
			jedis.set("database:user:" + user.token + ":name", user.name);
			jedis.set("database:user:" + user.token + ":surname", "" + user.surname);
			jedis.set("database:user:" + user.token + ":password", "" + user.password);
			jedis.set("database:user:" + user.token + ":uuid", "" + user.UUID);
			jedis.set("database:user:" + user.token + ":blocked", "" + user.blocked);
			jedis.set("database:user:" + user.token + ":infected", "" + user.infected);
			jedis.set("database:user:" + user.token + ":potentialyInfected", "" + user.potentialyInfected);
			jedis.set("database:user:" + user.token + ":notInfected", "" + user.notInfected);
		}
		catch(Exception e)
		{
			logger.log(Level.INFO, e.toString(), e);
			return false;
		}
		finally
		{
			pool.close();
		}
		return true;
	}
	public static ArrayList<Stat> getStat(String username)
	{
		logger.log(Level.INFO, "getStat");
		ArrayList<Stat> stats = new ArrayList<Stat>();
		JedisPool pool = new JedisPool(REDIS_ADDRESS, REDIS_PORT);
		try (Jedis jedis = pool.getResource())
		{
			Set<String> set = jedis.smembers("datetimes-" + username);
			for (String item : set)
			{
				String[] datetime = item.split(":");
				int year = Integer.parseInt(datetime[0]);
				int month = Integer.parseInt(datetime[1]);
				int day = Integer.parseInt(datetime[2]);
				int hour = Integer.parseInt(datetime[3]);
				int minute = Integer.parseInt(datetime[4]);
				int second = Integer.parseInt(datetime[5]);
				Calendar calendar = Calendar.getInstance();
				calendar.set(year, month, day, hour, minute, second);
				stats.add(new Stat(username, calendar.getTime()));
			}
		}
		catch(Exception e)
		{
			logger.log(Level.INFO, e.toString(), e);
			return null;
		}
		finally
		{
			pool.close();
		}
		return stats;
	}
	public static boolean addStat(Stat stat)
	{
		logger.log(Level.INFO, "addStat");
		JedisPool pool = new JedisPool(REDIS_ADDRESS, REDIS_PORT);
		try (Jedis jedis = pool.getResource())
		{
			Calendar calendar= Calendar.getInstance();
			calendar.setTime(stat.getDateTime());
			String datetime = "";
			datetime += calendar.get(Calendar.YEAR) + ":";
			datetime += calendar.get(Calendar.MONTH) + ":";
			datetime += calendar.get(Calendar.DAY_OF_MONTH) + ":";
			datetime += calendar.get(Calendar.HOUR) + ":";
			datetime += calendar.get(Calendar.MINUTE) + ":";
			datetime += calendar.get(Calendar.SECOND);
			jedis.sadd("datetimes-" + stat.getUsername(), datetime);
		}
		catch(Exception e)
		{
			logger.log(Level.INFO, e.toString(), e);
			return false;
		}
		finally
		{
			pool.close();
		}
		return true;
	}
	public static ArrayList<TokenInfo> getTokenInfo(long token)
	{
		logger.log(Level.INFO, "getTokenInfo");
		ArrayList<TokenInfo> result = new ArrayList<TokenInfo>();
		JedisPool pool = new JedisPool(REDIS_ADDRESS, REDIS_PORT);
		try (Jedis jedis = pool.getResource())
		{
			Set<String> set = jedis.smembers("infos-" + token);
			for (String item : set)
			{
				String[] attributes = item.split(":");
				double x = Double.parseDouble(attributes[1]);
				double y = Double.parseDouble(attributes[2]);
				String[] datetime = attributes[3].split("x");
				int year = Integer.parseInt(datetime[0]);
				int month = Integer.parseInt(datetime[1]);
				int day = Integer.parseInt(datetime[2]);
				int hour = Integer.parseInt(datetime[3]);
				int minute = Integer.parseInt(datetime[4]);
				int second = Integer.parseInt(datetime[5]);
				Calendar calendar = Calendar.getInstance();
				calendar.set(year, month, day, hour, minute, second);
				result.add(new TokenInfo(token, x, y, calendar.getTime()));
			}
		}
		catch(Exception e)
		{
			logger.log(Level.INFO, e.toString(), e);
			return null;
		}
		finally
		{
			pool.close();
		}
		return result;
	}
	public static boolean addTokenInfo(TokenInfo info)
	{
		logger.log(Level.INFO, "addTokenInfo");
		JedisPool pool = new JedisPool(REDIS_ADDRESS, REDIS_PORT);
		try (Jedis jedis = pool.getResource())
		{
			Calendar calendar= Calendar.getInstance();
			calendar.setTime(info.datetime);
			String datetime = "";
			datetime += calendar.get(Calendar.YEAR) + "x";
			datetime += calendar.get(Calendar.MONTH) + "x";
			datetime += calendar.get(Calendar.DAY_OF_MONTH) + "x";
			datetime += calendar.get(Calendar.HOUR) + "x";
			datetime += calendar.get(Calendar.MINUTE) + "x";
			datetime += calendar.get(Calendar.SECOND);
			jedis.sadd("infos-" + info.token, info.token + ":" + info.x + ":" + info.y + ":" + datetime);
		}
		catch(Exception e)
		{
			logger.log(Level.INFO, e.toString(), e);
			return false;
		}
		finally
		{
			pool.close();
		}
		return true;
	}
	public static long[] getTokens()
	{
		logger.log(Level.INFO, "getTokens");
		ArrayList<Long> resultList = new ArrayList<Long>();
		JedisPool pool = new JedisPool(REDIS_ADDRESS, REDIS_PORT);
		try (Jedis jedis = pool.getResource())
		{
			Set<String> set = jedis.smembers("tokens-");
			for (String item : set)
			{
				resultList.add(Long.parseLong(item));
			}
		}
		catch(Exception e)
		{
			logger.log(Level.INFO, e.toString(), e);
			return null;
		}
		finally
		{
			pool.close();
		}
		long[] result = new long[resultList.size()];
		for(int i=0; i < resultList.size(); ++i)
		{
			result[i] = resultList.get(i);
		}
		return result;
	}
	public static ArrayList<TokenInfo> getContacts(long token)
	{
		logger.log(Level.INFO, "getContacts");
		ArrayList<TokenInfo> tokenInfos = getTokenInfo(token);
		ArrayList<TokenInfo> result = new ArrayList<TokenInfo>();
		long[] tokens = getTokens();
		for(long tokenOther : tokens)
		{
			if(tokenOther != token)
			{
				ArrayList<TokenInfo> tokenInfosOther = getTokenInfo(tokenOther);
				for(TokenInfo info : tokenInfos)
					for(TokenInfo infoOther : tokenInfosOther)
						if(info.wasNear(infoOther))
							result.add(new TokenInfo(infoOther.token, info.x, info.y, info.datetime));
			}
		}
		return result;
	}
	public static boolean setInfected(long token, int mode)
	{
		logger.log(Level.INFO, "setInfected " + mode);
		User user = getUser(token);
		if(user == null)
			return false;
		if(mode == 1)
			user.infected();
		if(mode == 2)
			user.potentialyInfected();
		if(mode == 3)
			user.notInfected();
		if(mode == 1 || mode == 2)
			for(TokenInfo info : getContacts(token))
				if(!getUser(info.token).infected)
				{
					User u = getUser(info.token);
					u.potentialyInfected();
					editUser(u);
				}
		return editUser(user);
	}
	public static boolean isNotInfected(long token)
	{
		logger.log(Level.INFO, "isNotInfected?");
		User user = getUser(token);
		return user.notInfected;
	}
	public static boolean setPassword(long token, String password)
	{
		logger.log(Level.INFO, "setPassword");
		User user = getUser(token);
		user.password = password;
		return editUser(user);
	}
	public static ArrayList<TokenInfo> getAllPotentialContacts()
	{
		logger.log(Level.INFO, "getAllPotentialContacts");
		ArrayList<TokenInfo> contacts = new ArrayList<>();
		for(long token :  getTokens())
			if(getUser(token).infected)
				for(TokenInfo contact : getContacts(token))
				{
					if(getUser(contact.token).notInfected)
						getUser(contact.token).potentialyInfected();
					contacts.add(new TokenInfo(contact.token, contact.x, contact.y, contact.datetime));
				}
		Set<TokenInfo> s =
			new TreeSet<TokenInfo>
			(
				new Comparator<Object>()
				{
			        @Override
			        public int compare(Object o1, Object o2)
			        {
			            if(o1 != null && o2 != null)
			            {
			            	TokenInfo t1 = (TokenInfo)o1;
			            	TokenInfo t2 = (TokenInfo)o2;
			            	if(t1.token == t2.token && t1.x == t2.x && t1.y == t2.y && t1.datetime == t2.datetime)
			            		return 0;
			            }
			            return -1;
			        }
				}
			);
	    s.addAll(contacts);
	    ArrayList<TokenInfo> result = new ArrayList<>();
	    for(TokenInfo info : s)
	    	result.add(info);
	    return result;
	}
}
