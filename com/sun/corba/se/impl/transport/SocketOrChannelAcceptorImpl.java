package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.encoding.CDRInputObject;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.oa.poa.Policies;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.EventHandler;
import com.sun.corba.se.pept.transport.InboundConnectionCache;
import com.sun.corba.se.pept.transport.Selector;
import com.sun.corba.se.spi.extension.RequestPartitioningPolicy;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import com.sun.corba.se.spi.ior.iiop.AlternateIIOPAddressComponent;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketEndPointInfo;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.transport.CorbaAcceptor;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.transport.SocketInfo;
import com.sun.corba.se.spi.transport.SocketOrChannelAcceptor;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectableChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import sun.corba.OutputStreamFactory;

public class SocketOrChannelAcceptorImpl extends EventHandlerBase implements CorbaAcceptor, SocketOrChannelAcceptor, Work, SocketInfo, LegacyServerSocketEndPointInfo {
  protected ServerSocketChannel serverSocketChannel;
  
  protected ServerSocket serverSocket;
  
  protected int port;
  
  protected long enqueueTime;
  
  protected boolean initialized;
  
  protected ORBUtilSystemException wrapper;
  
  protected InboundConnectionCache connectionCache;
  
  protected String type = "";
  
  protected String name = "";
  
  protected String hostname;
  
  protected int locatorPort;
  
  public SocketOrChannelAcceptorImpl(ORB paramORB) {
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.transport");
    setWork(this);
    this.initialized = false;
    this.hostname = paramORB.getORBData().getORBServerHost();
    this.name = "NO_NAME";
    this.locatorPort = -1;
  }
  
  public SocketOrChannelAcceptorImpl(ORB paramORB, int paramInt) {
    this(paramORB);
    this.port = paramInt;
  }
  
  public SocketOrChannelAcceptorImpl(ORB paramORB, int paramInt, String paramString1, String paramString2) {
    this(paramORB, paramInt);
    this.name = paramString1;
    this.type = paramString2;
  }
  
  public boolean initialize() {
    if (this.initialized)
      return false; 
    if (this.orb.transportDebugFlag)
      dprint(".initialize: " + this); 
    InetSocketAddress inetSocketAddress = null;
    try {
      if (this.orb.getORBData().getListenOnAllInterfaces().equals("com.sun.CORBA.INTERNAL USE ONLY: listen on all interfaces")) {
        inetSocketAddress = new InetSocketAddress(this.port);
      } else {
        String str = this.orb.getORBData().getORBServerHost();
        inetSocketAddress = new InetSocketAddress(str, this.port);
      } 
      this.serverSocket = this.orb.getORBData().getSocketFactory().createServerSocket(this.type, inetSocketAddress);
      internalInitialize();
    } catch (Throwable throwable) {
      throw this.wrapper.createListenerFailed(throwable, Integer.toString(this.port));
    } 
    this.initialized = true;
    return true;
  }
  
  protected void internalInitialize() throws Exception {
    this.port = this.serverSocket.getLocalPort();
    this.orb.getCorbaTransportManager().getInboundConnectionCache(this);
    this.serverSocketChannel = this.serverSocket.getChannel();
    if (this.serverSocketChannel != null) {
      setUseSelectThreadToWait(this.orb.getORBData().acceptorSocketUseSelectThreadToWait());
      this.serverSocketChannel.configureBlocking(!this.orb.getORBData().acceptorSocketUseSelectThreadToWait());
    } else {
      setUseSelectThreadToWait(false);
    } 
    setUseWorkerThreadForEvent(this.orb.getORBData().acceptorSocketUseWorkerThreadForEvent());
  }
  
  public boolean initialized() { return this.initialized; }
  
  public String getConnectionCacheType() { return getClass().toString(); }
  
  public void setConnectionCache(InboundConnectionCache paramInboundConnectionCache) { this.connectionCache = paramInboundConnectionCache; }
  
  public InboundConnectionCache getConnectionCache() { return this.connectionCache; }
  
  public boolean shouldRegisterAcceptEvent() { return true; }
  
  public void accept() throws Exception {
    try {
      SocketChannel socketChannel = null;
      Socket socket = null;
      if (this.serverSocketChannel == null) {
        socket = this.serverSocket.accept();
      } else {
        socketChannel = this.serverSocketChannel.accept();
        socket = socketChannel.socket();
      } 
      this.orb.getORBData().getSocketFactory().setAcceptedSocketOptions(this, this.serverSocket, socket);
      if (this.orb.transportDebugFlag)
        dprint(".accept: " + ((this.serverSocketChannel == null) ? this.serverSocket.toString() : this.serverSocketChannel.toString())); 
      SocketOrChannelConnectionImpl socketOrChannelConnectionImpl = new SocketOrChannelConnectionImpl(this.orb, this, socket);
      if (this.orb.transportDebugFlag)
        dprint(".accept: new: " + socketOrChannelConnectionImpl); 
      getConnectionCache().stampTime(socketOrChannelConnectionImpl);
      getConnectionCache().put(this, socketOrChannelConnectionImpl);
      if (socketOrChannelConnectionImpl.shouldRegisterServerReadEvent()) {
        Selector selector = this.orb.getTransportManager().getSelector(0);
        if (selector != null) {
          if (this.orb.transportDebugFlag)
            dprint(".accept: registerForEvent: " + socketOrChannelConnectionImpl); 
          selector.registerForEvent(socketOrChannelConnectionImpl.getEventHandler());
        } 
      } 
      getConnectionCache().reclaim();
    } catch (IOException iOException) {
      if (this.orb.transportDebugFlag)
        dprint(".accept:", iOException); 
      Selector selector = this.orb.getTransportManager().getSelector(0);
      if (selector != null) {
        selector.unregisterForEvent(this);
        selector.registerForEvent(this);
      } 
    } 
  }
  
  public void close() throws Exception {
    try {
      if (this.orb.transportDebugFlag)
        dprint(".close->:"); 
      Selector selector = this.orb.getTransportManager().getSelector(0);
      if (selector != null)
        selector.unregisterForEvent(this); 
      if (this.serverSocketChannel != null)
        this.serverSocketChannel.close(); 
      if (this.serverSocket != null)
        this.serverSocket.close(); 
    } catch (IOException iOException) {
      if (this.orb.transportDebugFlag)
        dprint(".close:", iOException); 
    } finally {
      if (this.orb.transportDebugFlag)
        dprint(".close<-:"); 
    } 
  }
  
  public EventHandler getEventHandler() { return this; }
  
  public String getObjectAdapterId() { return null; }
  
  public String getObjectAdapterManagerId() { return null; }
  
  public void addToIORTemplate(IORTemplate paramIORTemplate, Policies paramPolicies, String paramString) {
    Iterator iterator = paramIORTemplate.iteratorById(0);
    String str = this.orb.getORBData().getORBServerHost();
    if (iterator.hasNext()) {
      IIOPAddress iIOPAddress = IIOPFactories.makeIIOPAddress(this.orb, str, this.port);
      AlternateIIOPAddressComponent alternateIIOPAddressComponent = IIOPFactories.makeAlternateIIOPAddressComponent(iIOPAddress);
      while (iterator.hasNext()) {
        TaggedProfileTemplate taggedProfileTemplate = (TaggedProfileTemplate)iterator.next();
        taggedProfileTemplate.add(alternateIIOPAddressComponent);
      } 
    } else {
      int i;
      GIOPVersion gIOPVersion = this.orb.getORBData().getGIOPVersion();
      if (paramPolicies.forceZeroPort()) {
        i = 0;
      } else if (paramPolicies.isTransient()) {
        i = this.port;
      } else {
        i = this.orb.getLegacyServerSocketManager().legacyGetPersistentServerPort("IIOP_CLEAR_TEXT");
      } 
      IIOPAddress iIOPAddress = IIOPFactories.makeIIOPAddress(this.orb, str, i);
      IIOPProfileTemplate iIOPProfileTemplate = IIOPFactories.makeIIOPProfileTemplate(this.orb, gIOPVersion, iIOPAddress);
      if (gIOPVersion.supportsIORIIOPProfileComponents()) {
        iIOPProfileTemplate.add(IIOPFactories.makeCodeSetsComponent(this.orb));
        iIOPProfileTemplate.add(IIOPFactories.makeMaxStreamFormatVersionComponent());
        RequestPartitioningPolicy requestPartitioningPolicy = (RequestPartitioningPolicy)paramPolicies.get_effective_policy(1398079491);
        if (requestPartitioningPolicy != null)
          iIOPProfileTemplate.add(IIOPFactories.makeRequestPartitioningComponent(requestPartitioningPolicy.getValue())); 
        if (paramString != null && paramString != "")
          iIOPProfileTemplate.add(IIOPFactories.makeJavaCodebaseComponent(paramString)); 
        if (this.orb.getORBData().isJavaSerializationEnabled())
          iIOPProfileTemplate.add(IIOPFactories.makeJavaSerializationComponent()); 
      } 
      paramIORTemplate.add(iIOPProfileTemplate);
    } 
  }
  
  public String getMonitoringName() { return "AcceptedConnections"; }
  
  public SelectableChannel getChannel() { return this.serverSocketChannel; }
  
  public int getInterestOps() { return 16; }
  
  public Acceptor getAcceptor() { return this; }
  
  public Connection getConnection() { throw new RuntimeException("Should not happen."); }
  
  public void doWork() throws Exception {
    try {
      if (this.orb.transportDebugFlag)
        dprint(".doWork->: " + this); 
      if (this.selectionKey.isAcceptable()) {
        accept();
      } else if (this.orb.transportDebugFlag) {
        dprint(".doWork: ! selectionKey.isAcceptable: " + this);
      } 
    } catch (SecurityException securityException) {
      if (this.orb.transportDebugFlag)
        dprint(".doWork: ignoring SecurityException: " + securityException + " " + this); 
      String str = ORBUtility.getClassSecurityInfo(getClass());
      this.wrapper.securityExceptionInAccept(securityException, str);
    } catch (Exception exception) {
      if (this.orb.transportDebugFlag)
        dprint(".doWork: ignoring Exception: " + exception + " " + this); 
      this.wrapper.exceptionInAccept(exception);
    } catch (Throwable throwable) {
      if (this.orb.transportDebugFlag)
        dprint(".doWork: ignoring Throwable: " + throwable + " " + this); 
    } finally {
      Selector selector = this.orb.getTransportManager().getSelector(0);
      if (selector != null)
        selector.registerInterestOps(this); 
      if (this.orb.transportDebugFlag)
        dprint(".doWork<-:" + this); 
    } 
  }
  
  public void setEnqueueTime(long paramLong) { this.enqueueTime = paramLong; }
  
  public long getEnqueueTime() { return this.enqueueTime; }
  
  public MessageMediator createMessageMediator(Broker paramBroker, Connection paramConnection) {
    SocketOrChannelContactInfoImpl socketOrChannelContactInfoImpl = new SocketOrChannelContactInfoImpl();
    return socketOrChannelContactInfoImpl.createMessageMediator(paramBroker, paramConnection);
  }
  
  public MessageMediator finishCreatingMessageMediator(Broker paramBroker, Connection paramConnection, MessageMediator paramMessageMediator) {
    SocketOrChannelContactInfoImpl socketOrChannelContactInfoImpl = new SocketOrChannelContactInfoImpl();
    return socketOrChannelContactInfoImpl.finishCreatingMessageMediator(paramBroker, paramConnection, paramMessageMediator);
  }
  
  public InputObject createInputObject(Broker paramBroker, MessageMediator paramMessageMediator) {
    CorbaMessageMediator corbaMessageMediator = (CorbaMessageMediator)paramMessageMediator;
    return new CDRInputObject((ORB)paramBroker, (CorbaConnection)paramMessageMediator.getConnection(), corbaMessageMediator.getDispatchBuffer(), corbaMessageMediator.getDispatchHeader());
  }
  
  public OutputObject createOutputObject(Broker paramBroker, MessageMediator paramMessageMediator) {
    CorbaMessageMediator corbaMessageMediator = (CorbaMessageMediator)paramMessageMediator;
    return OutputStreamFactory.newCDROutputObject((ORB)paramBroker, corbaMessageMediator, corbaMessageMediator.getReplyHeader(), corbaMessageMediator.getStreamFormatVersion());
  }
  
  public ServerSocket getServerSocket() { return this.serverSocket; }
  
  public String toString() {
    String str;
    if (this.serverSocketChannel == null) {
      if (this.serverSocket == null) {
        str = "(not initialized)";
      } else {
        str = this.serverSocket.toString();
      } 
    } else {
      str = this.serverSocketChannel.toString();
    } 
    return toStringName() + "[" + str + " " + this.type + " " + shouldUseSelectThreadToWait() + " " + shouldUseWorkerThreadForEvent() + "]";
  }
  
  protected String toStringName() { return "SocketOrChannelAcceptorImpl"; }
  
  protected void dprint(String paramString) { ORBUtility.dprint(toStringName(), paramString); }
  
  protected void dprint(String paramString, Throwable paramThrowable) {
    dprint(paramString);
    paramThrowable.printStackTrace(System.out);
  }
  
  public String getType() { return this.type; }
  
  public String getHostName() { return this.hostname; }
  
  public String getHost() { return this.hostname; }
  
  public int getPort() { return this.port; }
  
  public int getLocatorPort() { return this.locatorPort; }
  
  public void setLocatorPort(int paramInt) { this.locatorPort = paramInt; }
  
  public String getName() { return this.name.equals("NO_NAME") ? toString() : this.name; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\transport\SocketOrChannelAcceptorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */