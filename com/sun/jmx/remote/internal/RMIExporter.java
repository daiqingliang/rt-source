package com.sun.jmx.remote.internal;

import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;

public interface RMIExporter {
  public static final String EXPORTER_ATTRIBUTE = "com.sun.jmx.remote.rmi.exporter";
  
  Remote exportObject(Remote paramRemote, int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory, RMIServerSocketFactory paramRMIServerSocketFactory) throws RemoteException;
  
  boolean unexportObject(Remote paramRemote, boolean paramBoolean) throws NoSuchObjectException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\remote\internal\RMIExporter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */