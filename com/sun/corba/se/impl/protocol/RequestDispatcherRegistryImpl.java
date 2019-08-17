package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.orbutil.DenseIntMapImpl;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcherFactory;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RequestDispatcherRegistryImpl implements RequestDispatcherRegistry {
  private ORB orb;
  
  protected int defaultId;
  
  private DenseIntMapImpl SDRegistry;
  
  private DenseIntMapImpl CSRegistry;
  
  private DenseIntMapImpl OAFRegistry;
  
  private DenseIntMapImpl LCSFRegistry;
  
  private Set objectAdapterFactories;
  
  private Set objectAdapterFactoriesView;
  
  private Map stringToServerSubcontract;
  
  public RequestDispatcherRegistryImpl(ORB paramORB, int paramInt) {
    this.orb = paramORB;
    this.defaultId = paramInt;
    this.SDRegistry = new DenseIntMapImpl();
    this.CSRegistry = new DenseIntMapImpl();
    this.OAFRegistry = new DenseIntMapImpl();
    this.LCSFRegistry = new DenseIntMapImpl();
    this.objectAdapterFactories = new HashSet();
    this.objectAdapterFactoriesView = Collections.unmodifiableSet(this.objectAdapterFactories);
    this.stringToServerSubcontract = new HashMap();
  }
  
  public void registerClientRequestDispatcher(ClientRequestDispatcher paramClientRequestDispatcher, int paramInt) { this.CSRegistry.set(paramInt, paramClientRequestDispatcher); }
  
  public void registerLocalClientRequestDispatcherFactory(LocalClientRequestDispatcherFactory paramLocalClientRequestDispatcherFactory, int paramInt) { this.LCSFRegistry.set(paramInt, paramLocalClientRequestDispatcherFactory); }
  
  public void registerServerRequestDispatcher(CorbaServerRequestDispatcher paramCorbaServerRequestDispatcher, int paramInt) { this.SDRegistry.set(paramInt, paramCorbaServerRequestDispatcher); }
  
  public void registerServerRequestDispatcher(CorbaServerRequestDispatcher paramCorbaServerRequestDispatcher, String paramString) { this.stringToServerSubcontract.put(paramString, paramCorbaServerRequestDispatcher); }
  
  public void registerObjectAdapterFactory(ObjectAdapterFactory paramObjectAdapterFactory, int paramInt) {
    this.objectAdapterFactories.add(paramObjectAdapterFactory);
    this.OAFRegistry.set(paramInt, paramObjectAdapterFactory);
  }
  
  public CorbaServerRequestDispatcher getServerRequestDispatcher(int paramInt) {
    CorbaServerRequestDispatcher corbaServerRequestDispatcher = (CorbaServerRequestDispatcher)this.SDRegistry.get(paramInt);
    if (corbaServerRequestDispatcher == null)
      corbaServerRequestDispatcher = (CorbaServerRequestDispatcher)this.SDRegistry.get(this.defaultId); 
    return corbaServerRequestDispatcher;
  }
  
  public CorbaServerRequestDispatcher getServerRequestDispatcher(String paramString) {
    CorbaServerRequestDispatcher corbaServerRequestDispatcher = (CorbaServerRequestDispatcher)this.stringToServerSubcontract.get(paramString);
    if (corbaServerRequestDispatcher == null)
      corbaServerRequestDispatcher = (CorbaServerRequestDispatcher)this.SDRegistry.get(this.defaultId); 
    return corbaServerRequestDispatcher;
  }
  
  public LocalClientRequestDispatcherFactory getLocalClientRequestDispatcherFactory(int paramInt) {
    LocalClientRequestDispatcherFactory localClientRequestDispatcherFactory = (LocalClientRequestDispatcherFactory)this.LCSFRegistry.get(paramInt);
    if (localClientRequestDispatcherFactory == null)
      localClientRequestDispatcherFactory = (LocalClientRequestDispatcherFactory)this.LCSFRegistry.get(this.defaultId); 
    return localClientRequestDispatcherFactory;
  }
  
  public ClientRequestDispatcher getClientRequestDispatcher(int paramInt) {
    ClientRequestDispatcher clientRequestDispatcher = (ClientRequestDispatcher)this.CSRegistry.get(paramInt);
    if (clientRequestDispatcher == null)
      clientRequestDispatcher = (ClientRequestDispatcher)this.CSRegistry.get(this.defaultId); 
    return clientRequestDispatcher;
  }
  
  public ObjectAdapterFactory getObjectAdapterFactory(int paramInt) {
    ObjectAdapterFactory objectAdapterFactory = (ObjectAdapterFactory)this.OAFRegistry.get(paramInt);
    if (objectAdapterFactory == null)
      objectAdapterFactory = (ObjectAdapterFactory)this.OAFRegistry.get(this.defaultId); 
    return objectAdapterFactory;
  }
  
  public Set getObjectAdapterFactories() { return this.objectAdapterFactoriesView; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\RequestDispatcherRegistryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */