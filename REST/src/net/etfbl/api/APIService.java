package net.etfbl.api;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;

import net.etfbl.model.Database;
import net.etfbl.model.Stat;
import net.etfbl.model.TokenInfo;

@Path("/rest")
public class APIService
{
	
	private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("logger");
	private static Database initScript = new Database();
	static
	{
		try
		{
			File logdir = new File("log");
			logdir.mkdir();
			logger.addHandler(new FileHandler("log/rest.txt", true));
		}
		catch (Exception e)
		{
			logger.log(Level.INFO, e.toString(), e);
		}
	}
	
	//Get all tokens
	@GET
	@Path("/token")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTokens()
	{
		logger.log(Level.INFO, "getTokens");
		long[] result = Database.getTokens();
		if(result.length == 0)
			return Response.status(204).entity("No active users!").build();
		return Response.status(200).entity(result).build();
	}
	
	//Get locations, times and tokens of all contacts of certain token
	@GET
	@Path("/contact")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllPotentialContacts()
	{
		logger.log(Level.INFO, "getPotentialContacts");
		ArrayList<TokenInfo> result = Database.getAllPotentialContacts();
		if(result == null)
			return Response.status(204).entity("No potential contacts!").build();
		return Response.status(200).entity(result.toArray()).build();
	}
	
	//Get locations, times and tokens of all contacts of certain token
	@GET
	@Path("/contact/{token}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getContacts(@PathParam("token")long token)
	{
		logger.log(Level.INFO, "getContacts");
		ArrayList<TokenInfo> result = Database.getContacts(token);
		if(result == null)
			return Response.status(204).entity("No potential contacts!").build();
		return Response.status(200).entity(result.toArray()).build();
	}
	
	//Get statistics for certain token
	@GET
	@Path("/stat/{token}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStats(@PathParam("token")long token)
	{
		logger.log(Level.INFO, "getStats");
		ArrayList<Stat> result = Database.getStat("" + token);
		if(result == null)
			return Response.status(204).entity("No potential contacts!").build();
		return Response.status(200).entity(result.toArray()).build();
	}
	
	//Get all locations, times for certain token
	@GET
	@Path("/location/{token}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLocations(@PathParam("token")long token)
	{
		logger.log(Level.INFO, "getLocations");
		ArrayList<TokenInfo> result = Database.getTokenInfo(token);
		if(result == null)
			return Response.status(204).entity("No active users!").build();
		return Response.status(200).entity(result.toArray()).build();
	}
	
	//Add new location, time, token
	@POST
	@Path("/location/{token}/{x}/{y}")
	public Response setLocation(@PathParam("token")long token, @PathParam("x")double x, @PathParam("y")double y)
	{
		logger.log(Level.INFO, "setLocation");
		boolean result = Database.addTokenInfo(new TokenInfo(token, x, y, new Date()));
		if(result == false)
			return Response.status(204).entity("Couldn't set location!").build();
		return Response.status(200).entity("SUCCESS").build();
	}
	
	//Change token password
	@POST
	@Path("/pass/{token}/{password}")
	public Response setPassword(@PathParam("token")long token, @PathParam("password")String password)
	{
		logger.log(Level.INFO, "setPassword");
		boolean result = Database.setPassword(token, password);
		if(result == false)
			return Response.status(204).entity("Couldn't change password!").build();
		return Response.status(200).entity("SUCCESS").build();
	}
	
	//Set token as infected
	@POST
	@Path("/infected-yes/{token}")
	public Response setInfected(@PathParam("token")long token)
	{
		logger.log(Level.INFO, "setInfected");
		boolean result = Database.setInfected(token, 1);
		if(result == false)
			return Response.status(204).entity("Couldn't change status!").build();
		return Response.status(200).entity("SUCCESS").build();
	}
	
	//Set token as potentially infected
	@POST
	@Path("/infected-potentialy/{token}")
	public Response setPotentialyInfected(@PathParam("token")long token)
	{
		logger.log(Level.INFO, "setPotentialyInfected");
		boolean result = Database.setInfected(token, 2);
		if(result == false)
			return Response.status(204).entity("Couldn't change status!").build();
		return Response.status(200).entity("SUCCESS").build();
	}
	
	//Set token as not infected
	@POST
	@Path("/infected-not/{token}")
	public Response setNotInfected(@PathParam("token")long token)
	{
		logger.log(Level.INFO, "setNotInfected");
		boolean result = Database.setInfected(token, 3);
		if(result == false)
			return Response.status(204).entity("Couldn't change status!").build();
		return Response.status(200).entity("SUCCESS").build();
	}
	
	//Is token infected
	@GET
	@Path("/is-notinfected/{token}")
	public Response isNotInfected(@PathParam("token")long token)
	{
		logger.log(Level.INFO, "setLocation");
		boolean result = Database.isNotInfected(token);
		if(result == false)
			return Response.status(204).entity("Couldn't change status!").build();
		return Response.status(200).entity("SUCCESS").build();
	}
}
