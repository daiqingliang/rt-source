package com.sun.jmx.remote.internal;

import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Properties;

public interface IIOPProxy {
  boolean isStub(Object paramObject);
  
  Object getDelegate(Object paramObject);
  
  void setDelegate(Object paramObject1, Object paramObject2);
  
  Object getOrb(Object paramObject);
  
  void connect(Object paramObject1, Object paramObject2);
  
  boolean isOrb(Object paramObject);
  
  Object createOrb(String[] paramArrayOfString, Properties paramProperties);
  
  Object stringToObject(Object paramObject, String paramString);
  
  String objectToString(Object paramObject1, Object paramObject2);
  
  <T> T narrow(Object paramObject, Class<T> paramClass);
  
  void exportObject(Remote paramRemote) throws RemoteException;
  
  void unexportObject(Remote paramRemote) throws RemoteException;
  
  Remote toStub(Remote paramRemote) throws NoSuchObjectException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\remote\internal\IIOPProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */