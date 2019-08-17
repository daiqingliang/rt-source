package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.logging.ActivationSystemException;
import com.sun.corba.se.impl.oa.poa.BadServerIdHandler;
import com.sun.corba.se.spi.activation.EndPointInfo;
import com.sun.corba.se.spi.activation.InvalidORBid;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocation;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORB;
import com.sun.corba.se.spi.activation.NoSuchEndPoint;
import com.sun.corba.se.spi.activation.ORBAlreadyRegistered;
import com.sun.corba.se.spi.activation.ORBPortInfo;
import com.sun.corba.se.spi.activation.Repository;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import com.sun.corba.se.spi.activation.Server;
import com.sun.corba.se.spi.activation.ServerAlreadyActive;
import com.sun.corba.se.spi.activation.ServerHeldDown;
import com.sun.corba.se.spi.activation.ServerNotActive;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import com.sun.corba.se.spi.activation._ServerManagerImplBase;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketEndPointInfo;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.ForwardException;
import com.sun.corba.se.spi.transport.CorbaTransportManager;
import com.sun.corba.se.spi.transport.SocketOrChannelAcceptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ServerManagerImpl extends _ServerManagerImplBase implements BadServerIdHandler {
  HashMap serverTable;
  
  Repository repository;
  
  CorbaTransportManager transportManager;
  
  int initialPort;
  
  ORB orb;
  
  ActivationSystemException wrapper;
  
  String dbDirName;
  
  boolean debug = false;
  
  private int serverStartupDelay;
  
  ServerManagerImpl(ORB paramORB, CorbaTransportManager paramCorbaTransportManager, Repository paramRepository, String paramString, boolean paramBoolean) {
    this.orb = paramORB;
    this.wrapper = ActivationSystemException.get(paramORB, "orbd.activator");
    this.transportManager = paramCorbaTransportManager;
    this.repository = paramRepository;
    this.dbDirName = paramString;
    this.debug = paramBoolean;
    LegacyServerSocketEndPointInfo legacyServerSocketEndPointInfo = paramORB.getLegacyServerSocketManager().legacyGetEndpoint("BOOT_NAMING");
    this.initialPort = ((SocketOrChannelAcceptor)legacyServerSocketEndPointInfo).getServerSocket().getLocalPort();
    this.serverTable = new HashMap(256);
    this.serverStartupDelay = 1000;
    String str = System.getProperty("com.sun.CORBA.activation.ServerStartupDelay");
    if (str != null)
      try {
        this.serverStartupDelay = Integer.parseInt(str);
      } catch (Exception exception) {} 
    Class clazz = paramORB.getORBData().getBadServerIdHandler();
    if (clazz == null) {
      paramORB.setBadServerIdHandler(this);
    } else {
      paramORB.initBadServerIdHandler();
    } 
    paramORB.connect(this);
    ProcessMonitorThread.start(this.serverTable);
  }
  
  public void activate(int paramInt) throws ServerAlreadyActive, ServerNotRegistered, ServerHeldDown {
    ServerTableEntry serverTableEntry;
    Integer integer = new Integer(paramInt);
    synchronized (this.serverTable) {
      serverTableEntry = (ServerTableEntry)this.serverTable.get(integer);
    } 
    if (serverTableEntry != null && serverTableEntry.isActive()) {
      if (this.debug)
        System.out.println("ServerManagerImpl: activate for server Id " + paramInt + " failed because server is already active. entry = " + serverTableEntry); 
      throw new ServerAlreadyActive(paramInt);
    } 
    try {
      serverTableEntry = getEntry(paramInt);
      if (this.debug)
        System.out.println("ServerManagerImpl: locateServer called with  serverId=" + paramInt + " endpointType=" + "IIOP_CLEAR_TEXT" + " block=false"); 
      ServerLocation serverLocation = locateServer(serverTableEntry, "IIOP_CLEAR_TEXT", false);
      if (this.debug)
        System.out.println("ServerManagerImpl: activate for server Id " + paramInt + " found location " + serverLocation.hostname + " and activated it"); 
    } catch (NoSuchEndPoint noSuchEndPoint) {
      if (this.debug)
        System.out.println("ServerManagerImpl: activate for server Id  threw NoSuchEndpoint exception, which was ignored"); 
    } 
  }
  
  public void active(int paramInt, Server paramServer) throws ServerNotRegistered {
    Integer integer = new Integer(paramInt);
    synchronized (this.serverTable) {
      ServerTableEntry serverTableEntry = (ServerTableEntry)this.serverTable.get(integer);
      if (serverTableEntry == null) {
        if (this.debug)
          System.out.println("ServerManagerImpl: active for server Id " + paramInt + " called, but no such server is registered."); 
        throw this.wrapper.serverNotExpectedToRegister();
      } 
      if (this.debug)
        System.out.println("ServerManagerImpl: active for server Id " + paramInt + " called.  This server is now active."); 
      serverTableEntry.register(paramServer);
    } 
  }
  
  public void registerEndpoints(int paramInt, String paramString, EndPointInfo[] paramArrayOfEndPointInfo) throws NoSuchEndPoint, ServerNotRegistered, ORBAlreadyRegistered {
    Integer integer = new Integer(paramInt);
    synchronized (this.serverTable) {
      ServerTableEntry serverTableEntry = (ServerTableEntry)this.serverTable.get(integer);
      if (serverTableEntry == null) {
        if (this.debug)
          System.out.println("ServerManagerImpl: registerEndpoint for server Id " + paramInt + " called, but no such server is registered."); 
        throw this.wrapper.serverNotExpectedToRegister();
      } 
      if (this.debug)
        System.out.println("ServerManagerImpl: registerEndpoints for server Id " + paramInt + " called.  This server is now active."); 
      serverTableEntry.registerPorts(paramString, paramArrayOfEndPointInfo);
    } 
  }
  
  public int[] getActiveServers() {
    int[] arrayOfInt = null;
    synchronized (this.serverTable) {
      ArrayList arrayList = new ArrayList(0);
      Iterator iterator = this.serverTable.keySet().iterator();
      try {
        while (iterator.hasNext()) {
          Integer integer = (Integer)iterator.next();
          ServerTableEntry serverTableEntry = (ServerTableEntry)this.serverTable.get(integer);
          if (serverTableEntry.isValid() && serverTableEntry.isActive())
            arrayList.add(serverTableEntry); 
        } 
      } catch (NoSuchElementException noSuchElementException) {}
      arrayOfInt = new int[arrayList.size()];
      for (byte b = 0; b < arrayList.size(); b++) {
        ServerTableEntry serverTableEntry = (ServerTableEntry)arrayList.get(b);
        arrayOfInt[b] = serverTableEntry.getServerId();
      } 
    } 
    if (this.debug) {
      StringBuffer stringBuffer = new StringBuffer();
      for (byte b = 0; b < arrayOfInt.length; b++) {
        stringBuffer.append(' ');
        stringBuffer.append(arrayOfInt[b]);
      } 
      System.out.println("ServerManagerImpl: getActiveServers returns" + stringBuffer.toString());
    } 
    return arrayOfInt;
  }
  
  public void shutdown(int paramInt) throws ServerAlreadyActive, ServerNotRegistered, ServerHeldDown {
    Integer integer = new Integer(paramInt);
    synchronized (this.serverTable) {
      ServerTableEntry serverTableEntry = (ServerTableEntry)this.serverTable.remove(integer);
      if (serverTableEntry == null) {
        if (this.debug)
          System.out.println("ServerManagerImpl: shutdown for server Id " + paramInt + " throws ServerNotActive."); 
        throw new ServerNotActive(paramInt);
      } 
      try {
        serverTableEntry.destroy();
        if (this.debug)
          System.out.println("ServerManagerImpl: shutdown for server Id " + paramInt + " completed."); 
      } catch (Exception exception) {
        if (this.debug)
          System.out.println("ServerManagerImpl: shutdown for server Id " + paramInt + " threw exception " + exception); 
      } 
    } 
  }
  
  private ServerTableEntry getEntry(int paramInt) throws ServerNotRegistered {
    Integer integer = new Integer(paramInt);
    ServerTableEntry serverTableEntry = null;
    synchronized (this.serverTable) {
      serverTableEntry = (ServerTableEntry)this.serverTable.get(integer);
      if (this.debug)
        if (serverTableEntry == null) {
          System.out.println("ServerManagerImpl: getEntry: no active server found.");
        } else {
          System.out.println("ServerManagerImpl: getEntry:  active server found " + serverTableEntry + ".");
        }  
      if (serverTableEntry != null && !serverTableEntry.isValid()) {
        this.serverTable.remove(integer);
        serverTableEntry = null;
      } 
      if (serverTableEntry == null) {
        ServerDef serverDef = this.repository.getServer(paramInt);
        serverTableEntry = new ServerTableEntry(this.wrapper, paramInt, serverDef, this.initialPort, this.dbDirName, false, this.debug);
        this.serverTable.put(integer, serverTableEntry);
        serverTableEntry.activate();
      } 
    } 
    return serverTableEntry;
  }
  
  private ServerLocation locateServer(ServerTableEntry paramServerTableEntry, String paramString, boolean paramBoolean) throws NoSuchEndPoint, ServerNotRegistered, ServerHeldDown {
    ServerLocation serverLocation = new ServerLocation();
    if (paramBoolean) {
      boolean bool;
      ORBPortInfo[] arrayOfORBPortInfo;
      try {
        arrayOfORBPortInfo = paramServerTableEntry.lookup(paramString);
      } catch (Exception exception) {
        if (this.debug)
          System.out.println("ServerManagerImpl: locateServer: server held down"); 
        throw new ServerHeldDown(paramServerTableEntry.getServerId());
      } 
      String str = this.orb.getLegacyServerSocketManager().legacyGetEndpoint("DEFAULT_ENDPOINT").getHostName();
      serverLocation.hostname = str;
      if (arrayOfORBPortInfo != null) {
        bool = arrayOfORBPortInfo.length;
      } else {
        bool = false;
      } 
      serverLocation.ports = new ORBPortInfo[bool];
      for (byte b = 0; b < bool; b++) {
        serverLocation.ports[b] = new ORBPortInfo((arrayOfORBPortInfo[b]).orbId, (arrayOfORBPortInfo[b]).port);
        if (this.debug)
          System.out.println("ServerManagerImpl: locateServer: server located at location " + serverLocation.hostname + " ORBid  " + (arrayOfORBPortInfo[b]).orbId + " Port " + (arrayOfORBPortInfo[b]).port); 
      } 
    } 
    return serverLocation;
  }
  
  private ServerLocationPerORB locateServerForORB(ServerTableEntry paramServerTableEntry, String paramString, boolean paramBoolean) throws InvalidORBid, ServerNotRegistered, ServerHeldDown {
    ServerLocationPerORB serverLocationPerORB = new ServerLocationPerORB();
    if (paramBoolean) {
      boolean bool;
      EndPointInfo[] arrayOfEndPointInfo;
      try {
        arrayOfEndPointInfo = paramServerTableEntry.lookupForORB(paramString);
      } catch (InvalidORBid invalidORBid) {
        throw invalidORBid;
      } catch (Exception exception) {
        if (this.debug)
          System.out.println("ServerManagerImpl: locateServerForORB: server held down"); 
        throw new ServerHeldDown(paramServerTableEntry.getServerId());
      } 
      String str = this.orb.getLegacyServerSocketManager().legacyGetEndpoint("DEFAULT_ENDPOINT").getHostName();
      serverLocationPerORB.hostname = str;
      if (arrayOfEndPointInfo != null) {
        bool = arrayOfEndPointInfo.length;
      } else {
        bool = false;
      } 
      serverLocationPerORB.ports = new EndPointInfo[bool];
      for (byte b = 0; b < bool; b++) {
        serverLocationPerORB.ports[b] = new EndPointInfo((arrayOfEndPointInfo[b]).endpointType, (arrayOfEndPointInfo[b]).port);
        if (this.debug)
          System.out.println("ServerManagerImpl: locateServer: server located at location " + serverLocationPerORB.hostname + " endpointType  " + (arrayOfEndPointInfo[b]).endpointType + " Port " + (arrayOfEndPointInfo[b]).port); 
      } 
    } 
    return serverLocationPerORB;
  }
  
  public String[] getORBNames(int paramInt) throws ServerNotRegistered {
    try {
      ServerTableEntry serverTableEntry = getEntry(paramInt);
      return serverTableEntry.getORBList();
    } catch (Exception exception) {
      throw new ServerNotRegistered(paramInt);
    } 
  }
  
  private ServerTableEntry getRunningEntry(int paramInt) throws ServerNotRegistered {
    ServerTableEntry serverTableEntry = getEntry(paramInt);
    try {
      ORBPortInfo[] arrayOfORBPortInfo = serverTableEntry.lookup("IIOP_CLEAR_TEXT");
    } catch (Exception exception) {
      return null;
    } 
    return serverTableEntry;
  }
  
  public void install(int paramInt) throws ServerAlreadyActive, ServerNotRegistered, ServerHeldDown {
    ServerTableEntry serverTableEntry = getRunningEntry(paramInt);
    if (serverTableEntry != null) {
      this.repository.install(paramInt);
      serverTableEntry.install();
    } 
  }
  
  public void uninstall(int paramInt) throws ServerAlreadyActive, ServerNotRegistered, ServerHeldDown {
    ServerTableEntry serverTableEntry = (ServerTableEntry)this.serverTable.get(new Integer(paramInt));
    if (serverTableEntry != null) {
      serverTableEntry = (ServerTableEntry)this.serverTable.remove(new Integer(paramInt));
      if (serverTableEntry == null) {
        if (this.debug)
          System.out.println("ServerManagerImpl: shutdown for server Id " + paramInt + " throws ServerNotActive."); 
        throw new ServerHeldDown(paramInt);
      } 
      serverTableEntry.uninstall();
    } 
  }
  
  public ServerLocation locateServer(int paramInt, String paramString) throws NoSuchEndPoint, ServerNotRegistered, ServerHeldDown {
    ServerTableEntry serverTableEntry = getEntry(paramInt);
    if (this.debug)
      System.out.println("ServerManagerImpl: locateServer called with  serverId=" + paramInt + " endpointType=" + paramString + " block=true"); 
    return locateServer(serverTableEntry, paramString, true);
  }
  
  public ServerLocationPerORB locateServerForORB(int paramInt, String paramString) throws InvalidORBid, ServerNotRegistered, ServerHeldDown {
    ServerTableEntry serverTableEntry = getEntry(paramInt);
    if (this.debug)
      System.out.println("ServerManagerImpl: locateServerForORB called with  serverId=" + paramInt + " orbId=" + paramString + " block=true"); 
    return locateServerForORB(serverTableEntry, paramString, true);
  }
  
  public void handle(ObjectKey paramObjectKey) {
    IOR iOR = null;
    ObjectKeyTemplate objectKeyTemplate = paramObjectKey.getTemplate();
    int i = objectKeyTemplate.getServerId();
    String str = objectKeyTemplate.getORBId();
    try {
      ServerTableEntry serverTableEntry = getEntry(i);
      ServerLocationPerORB serverLocationPerORB = locateServerForORB(serverTableEntry, str, true);
      if (this.debug)
        System.out.println("ServerManagerImpl: handle called for server id" + i + "  orbid  " + str); 
      int j = 0;
      EndPointInfo[] arrayOfEndPointInfo = serverLocationPerORB.ports;
      for (byte b = 0; b < arrayOfEndPointInfo.length; b++) {
        if ((arrayOfEndPointInfo[b]).endpointType.equals("IIOP_CLEAR_TEXT")) {
          j = (arrayOfEndPointInfo[b]).port;
          break;
        } 
      } 
      IIOPAddress iIOPAddress = IIOPFactories.makeIIOPAddress(this.orb, serverLocationPerORB.hostname, j);
      IIOPProfileTemplate iIOPProfileTemplate = IIOPFactories.makeIIOPProfileTemplate(this.orb, GIOPVersion.V1_2, iIOPAddress);
      if (GIOPVersion.V1_2.supportsIORIIOPProfileComponents()) {
        iIOPProfileTemplate.add(IIOPFactories.makeCodeSetsComponent(this.orb));
        iIOPProfileTemplate.add(IIOPFactories.makeMaxStreamFormatVersionComponent());
      } 
      IORTemplate iORTemplate = IORFactories.makeIORTemplate(objectKeyTemplate);
      iORTemplate.add(iIOPProfileTemplate);
      iOR = iORTemplate.makeIOR(this.orb, "IDL:org/omg/CORBA/Object:1.0", paramObjectKey.getId());
    } catch (Exception exception) {
      throw this.wrapper.errorInBadServerIdHandler(exception);
    } 
    if (this.debug)
      System.out.println("ServerManagerImpl: handle throws ForwardException"); 
    try {
      Thread.sleep(this.serverStartupDelay);
    } catch (Exception exception) {
      System.out.println("Exception = " + exception);
      exception.printStackTrace();
    } 
    throw new ForwardException(this.orb, iOR);
  }
  
  public int getEndpoint(String paramString) throws NoSuchEndPoint { return this.orb.getLegacyServerSocketManager().legacyGetTransientServerPort(paramString); }
  
  public int getServerPortForType(ServerLocationPerORB paramServerLocationPerORB, String paramString) throws NoSuchEndPoint {
    EndPointInfo[] arrayOfEndPointInfo = paramServerLocationPerORB.ports;
    for (byte b = 0; b < arrayOfEndPointInfo.length; b++) {
      if ((arrayOfEndPointInfo[b]).endpointType.equals(paramString))
        return (arrayOfEndPointInfo[b]).port; 
    } 
    throw new NoSuchEndPoint();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\activation\ServerManagerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */