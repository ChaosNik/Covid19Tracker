package net.etfbl.api;

public class ServiceProxy implements net.etfbl.api.Service {
  private String _endpoint = null;
  private net.etfbl.api.Service service = null;
  
  public ServiceProxy() {
    _initServiceProxy();
  }
  
  public ServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initServiceProxy();
  }
  
  private void _initServiceProxy() {
    try {
      service = (new net.etfbl.api.ServiceServiceLocator()).getService();
      if (service != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)service)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)service)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (service != null)
      ((javax.xml.rpc.Stub)service)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public net.etfbl.api.Service getService() {
    if (service == null)
      _initServiceProxy();
    return service;
  }
  
  public long register(java.lang.String name, java.lang.String surname, java.lang.String password, long UUID, boolean blocked) throws java.rmi.RemoteException{
    if (service == null)
      _initServiceProxy();
    return service.register(name, surname, password, UUID, blocked);
  }
  
  public boolean unregister(long token) throws java.rmi.RemoteException{
    if (service == null)
      _initServiceProxy();
    return service.unregister(token);
  }
  
  public boolean isValid(long token) throws java.rmi.RemoteException{
    if (service == null)
      _initServiceProxy();
    return service.isValid(token);
  }
  
  public boolean logout(long token) throws java.rmi.RemoteException{
    if (service == null)
      _initServiceProxy();
    return service.logout(token);
  }
  
  public boolean login(long token) throws java.rmi.RemoteException{
    if (service == null)
      _initServiceProxy();
    return service.login(token);
  }
  
  public boolean blockUser(long token) throws java.rmi.RemoteException{
    if (service == null)
      _initServiceProxy();
    return service.blockUser(token);
  }
  
  
}