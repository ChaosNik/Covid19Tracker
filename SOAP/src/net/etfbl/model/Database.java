package net.etfbl.model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class Database
{
	private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("logger");
	private static String REDIS_ADDRESS = "127.0.0.1";
	private static int REDIS_PORT = 6379;
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
	    
		try
		{
			File logdir = new File("log");
			logdir.mkdir();
			logger.addHandler(new FileHandler("log/db2.txt", true));
		}
		catch (Exception e)
		{
			logger.log(Level.INFO, e.toString(), e);
		}
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
			jedis.sadd("tokens-", "" + token);
			jedis.set("database:user:" + token, "" + token);
			jedis.set("database:user:" + token + ":name", user.name);
			jedis.set("database:user:" + token + ":surname", "" + user.surname);
			jedis.set("database:user:" + token + ":password", "" + user.password);
			jedis.set("database:user:" + token + ":uuid", "" + user.UUID);
			jedis.set("database:user:" + token + ":blocked", "" + user.blocked);
			jedis.set("database:user:" + token + ":infected", "" + user.infected);
			jedis.set("database:user:" + token + ":potentialyInfected", "" + user.potentialyInfected);
			jedis.set("database:user:" + token + ":notInfected", "" + user.notInfected);
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
	public static boolean removeUser(long token)
	{
		logger.log(Level.INFO, "removeUser");
		JedisPool pool = new JedisPool(REDIS_ADDRESS, REDIS_PORT);
		try (Jedis jedis = pool.getResource())
		{
			jedis.srem("tokens-", "" + token);
			jedis.del("database:user:" + token + ":name");
			jedis.del("database:user:" + token + ":surname");
			jedis.del("database:user:" + token + ":password");
			jedis.del("database:user:" + token + ":uuid");
			jedis.del("database:user:" + token + ":blocked");
			jedis.del("database:user:" + token);
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
	public static boolean isValid(long token)
	{
		for(long item : getTokens())
		{
			if(item == token)
				return true;
		}
		return false;
	}
	public static boolean blockUser(long token)
	{
		User user = getUser(token);
		user.block();
		return editUser(user);
	}
}