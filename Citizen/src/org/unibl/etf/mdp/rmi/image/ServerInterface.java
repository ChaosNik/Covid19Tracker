package org.unibl.etf.mdp.rmi.image;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerInterface extends Remote
{
	//Save data sent by userFrom to userTo
	public boolean save(long token, String fileName, byte[] data) throws RemoteException;
	//List all files in <user>'s directory
	public List<String> view(long token) throws RemoteException;
	//Get file sent by userFrom to userTo
	public byte[] download(long token, String fileName) throws RemoteException;
}