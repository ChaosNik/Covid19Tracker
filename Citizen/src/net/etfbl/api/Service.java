package net.etfbl.api;

public interface Service extends java.rmi.Remote {
    public long register(java.lang.String name, java.lang.String surname, java.lang.String password, long UUID, boolean blocked) throws java.rmi.RemoteException;
    public boolean unregister(long token) throws java.rmi.RemoteException;
    public boolean isValid(long token) throws java.rmi.RemoteException;
    public boolean logout(long token) throws java.rmi.RemoteException;
    public boolean login(long token) throws java.rmi.RemoteException;
    public boolean blockUser(long token) throws java.rmi.RemoteException;
}
