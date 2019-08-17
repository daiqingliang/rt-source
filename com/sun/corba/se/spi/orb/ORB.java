package com.sun.corba.se.spi.orb;

import com.sun.corba.se.impl.corba.TypeCodeFactory;
import com.sun.corba.se.impl.corba.TypeCodeImpl;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.oa.poa.BadServerIdHandler;
import com.sun.corba.se.impl.presentation.rmi.PresentationManagerImpl;
import com.sun.corba.se.impl.transport.ByteBufferPoolImpl;
import com.sun.corba.se.org.omg.CORBA.ORB;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.spi.copyobject.CopierManager;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyFactory;
import com.sun.corba.se.spi.ior.TaggedComponentFactoryFinder;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketManager;
import com.sun.corba.se.spi.logging.LogWrapperBase;
import com.sun.corba.se.spi.logging.LogWrapperFactory;
import com.sun.corba.se.spi.monitoring.MonitoringFactories;
import com.sun.corba.se.spi.monitoring.MonitoringManager;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;
import com.sun.corba.se.spi.presentation.rmi.PresentationDefaults;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.protocol.ClientDelegateFactory;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.protocol.PIHandler;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import com.sun.corba.se.spi.resolver.LocalResolver;
import com.sun.corba.se.spi.resolver.Resolver;
import com.sun.corba.se.spi.servicecontext.ServiceContextRegistry;
import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory;
import com.sun.corba.se.spi.transport.CorbaTransportManager;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import sun.awt.AppContext;
import sun.corba.SharedSecrets;

public abstract class ORB extends ORB implements Broker, TypeCodeFactory {
  public static boolean ORBInitDebug = false;
  
  public boolean transportDebugFlag = false;
  
  public boolean subcontractDebugFlag = false;
  
  public boolean poaDebugFlag = false;
  
  public boolean poaConcurrencyDebugFlag = false;
  
  public boolean poaFSMDebugFlag = false;
  
  public boolean orbdDebugFlag = false;
  
  public boolean namingDebugFlag = false;
  
  public boolean serviceContextDebugFlag = false;
  
  public boolean transientObjectManagerDebugFlag = false;
  
  public boolean giopVersionDebugFlag = false;
  
  public boolean shutdownDebugFlag = false;
  
  public boolean giopDebugFlag = false;
  
  public boolean invocationTimingDebugFlag = false;
  
  public boolean orbInitDebugFlag = false;
  
  protected static ORBUtilSystemException staticWrapper;
  
  protected ORBUtilSystemException wrapper = ORBUtilSystemException.get(this, "rpc.presentation");
  
  protected OMGSystemException omgWrapper = OMGSystemException.get(this, "rpc.presentation");
  
  private Map typeCodeMap = new HashMap();
  
  private TypeCodeImpl[] primitiveTypeCodeConstants = { 
      new TypeCodeImpl(this, 0), new TypeCodeImpl(this, 1), new TypeCodeImpl(this, 2), new TypeCodeImpl(this, 3), new TypeCodeImpl(this, 4), new TypeCodeImpl(this, 5), new TypeCodeImpl(this, 6), new TypeCodeImpl(this, 7), new TypeCodeImpl(this, 8), new TypeCodeImpl(this, 9), 
      new TypeCodeImpl(this, 10), new TypeCodeImpl(this, 11), new TypeCodeImpl(this, 12), new TypeCodeImpl(this, 13), new TypeCodeImpl(this, 14), null, null, null, new TypeCodeImpl(this, 18), null, 
      null, null, null, new TypeCodeImpl(this, 23), new TypeCodeImpl(this, 24), new TypeCodeImpl(this, 25), new TypeCodeImpl(this, 26), new TypeCodeImpl(this, 27), new TypeCodeImpl(this, 28), new TypeCodeImpl(this, 29), 
      new TypeCodeImpl(this, 30), new TypeCodeImpl(this, 31), new TypeCodeImpl(this, 32) };
  
  ByteBufferPool byteBufferPool;
  
  private Map wrapperMap = new ConcurrentHashMap();
  
  private static final Object pmLock = new Object();
  
  private static Map staticWrapperMap = new ConcurrentHashMap();
  
  protected MonitoringManager monitoringManager = MonitoringFactories.getMonitoringManagerFactory().createMonitoringManager("orb", "ORB Management and Monitoring Root");
  
  public abstract boolean isLocalHost(String paramString);
  
  public abstract boolean isLocalServerId(int paramInt1, int paramInt2);
  
  public abstract OAInvocationInfo peekInvocationInfo();
  
  public abstract void pushInvocationInfo(OAInvocationInfo paramOAInvocationInfo);
  
  public abstract OAInvocationInfo popInvocationInfo();
  
  public abstract CorbaTransportManager getCorbaTransportManager();
  
  public abstract LegacyServerSocketManager getLegacyServerSocketManager();
  
  private static PresentationManager setupPresentationManager() {
    staticWrapper = ORBUtilSystemException.get("rpc.presentation");
    boolean bool = ((Boolean)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() { return Boolean.valueOf(Boolean.getBoolean("com.sun.CORBA.ORBUseDynamicStub")); }
        })).booleanValue();
    PresentationManager.StubFactoryFactory stubFactoryFactory = (PresentationManager.StubFactoryFactory)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            PresentationManager.StubFactoryFactory stubFactoryFactory = PresentationDefaults.getProxyStubFactoryFactory();
            String str = System.getProperty("com.sun.CORBA.ORBDynamicStubFactoryFactoryClass", "com.sun.corba.se.impl.presentation.rmi.bcel.StubFactoryFactoryBCELImpl");
            try {
              Class clazz = SharedSecrets.getJavaCorbaAccess().loadClass(str);
              stubFactoryFactory = (PresentationManager.StubFactoryFactory)clazz.newInstance();
            } catch (Exception exception) {
              ORB.staticWrapper.errorInSettingDynamicStubFactoryFactory(exception, str);
            } 
            return stubFactoryFactory;
          }
        });
    PresentationManagerImpl presentationManagerImpl = new PresentationManagerImpl(bool);
    presentationManagerImpl.setStubFactoryFactory(false, PresentationDefaults.getStaticStubFactoryFactory());
    presentationManagerImpl.setStubFactoryFactory(true, stubFactoryFactory);
    return presentationManagerImpl;
  }
  
  public void destroy() {
    this.wrapper = null;
    this.omgWrapper = null;
    this.typeCodeMap = null;
    this.primitiveTypeCodeConstants = null;
    this.byteBufferPool = null;
  }
  
  public static PresentationManager getPresentationManager() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null && AppContext.getAppContexts().size() > 0) {
      AppContext appContext = AppContext.getAppContext();
      if (appContext != null)
        synchronized (pmLock) {
          PresentationManager presentationManager = (PresentationManager)appContext.get(PresentationManager.class);
          if (presentationManager == null) {
            presentationManager = setupPresentationManager();
            appContext.put(PresentationManager.class, presentationManager);
          } 
          return presentationManager;
        }  
    } 
    return Holder.defaultPresentationManager;
  }
  
  public static PresentationManager.StubFactoryFactory getStubFactoryFactory() {
    PresentationManager presentationManager = getPresentationManager();
    boolean bool = presentationManager.useDynamicStubs();
    return presentationManager.getStubFactoryFactory(bool);
  }
  
  public TypeCodeImpl get_primitive_tc(int paramInt) {
    synchronized (this) {
      checkShutdownState();
    } 
    try {
      return this.primitiveTypeCodeConstants[paramInt];
    } catch (Throwable throwable) {
      throw this.wrapper.invalidTypecodeKind(throwable, new Integer(paramInt));
    } 
  }
  
  public void setTypeCode(String paramString, TypeCodeImpl paramTypeCodeImpl) {
    checkShutdownState();
    this.typeCodeMap.put(paramString, paramTypeCodeImpl);
  }
  
  public TypeCodeImpl getTypeCode(String paramString) {
    checkShutdownState();
    return (TypeCodeImpl)this.typeCodeMap.get(paramString);
  }
  
  public MonitoringManager getMonitoringManager() {
    synchronized (this) {
      checkShutdownState();
    } 
    return this.monitoringManager;
  }
  
  public abstract void set_parameters(Properties paramProperties);
  
  public abstract ORBVersion getORBVersion();
  
  public abstract void setORBVersion(ORBVersion paramORBVersion);
  
  public abstract IOR getFVDCodeBaseIOR();
  
  public abstract void handleBadServerId(ObjectKey paramObjectKey);
  
  public abstract void setBadServerIdHandler(BadServerIdHandler paramBadServerIdHandler);
  
  public abstract void initBadServerIdHandler();
  
  public abstract void notifyORB();
  
  public abstract PIHandler getPIHandler();
  
  public abstract void checkShutdownState();
  
  public abstract boolean isDuringDispatch();
  
  public abstract void startingDispatch();
  
  public abstract void finishedDispatch();
  
  public abstract int getTransientServerId();
  
  public abstract ServiceContextRegistry getServiceContextRegistry();
  
  public abstract RequestDispatcherRegistry getRequestDispatcherRegistry();
  
  public abstract ORBData getORBData();
  
  public abstract void setClientDelegateFactory(ClientDelegateFactory paramClientDelegateFactory);
  
  public abstract ClientDelegateFactory getClientDelegateFactory();
  
  public abstract void setCorbaContactInfoListFactory(CorbaContactInfoListFactory paramCorbaContactInfoListFactory);
  
  public abstract CorbaContactInfoListFactory getCorbaContactInfoListFactory();
  
  public abstract void setResolver(Resolver paramResolver);
  
  public abstract Resolver getResolver();
  
  public abstract void setLocalResolver(LocalResolver paramLocalResolver);
  
  public abstract LocalResolver getLocalResolver();
  
  public abstract void setURLOperation(Operation paramOperation);
  
  public abstract Operation getURLOperation();
  
  public abstract void setINSDelegate(CorbaServerRequestDispatcher paramCorbaServerRequestDispatcher);
  
  public abstract TaggedComponentFactoryFinder getTaggedComponentFactoryFinder();
  
  public abstract IdentifiableFactoryFinder getTaggedProfileFactoryFinder();
  
  public abstract IdentifiableFactoryFinder getTaggedProfileTemplateFactoryFinder();
  
  public abstract ObjectKeyFactory getObjectKeyFactory();
  
  public abstract void setObjectKeyFactory(ObjectKeyFactory paramObjectKeyFactory);
  
  public Logger getLogger(String paramString) {
    String str;
    synchronized (this) {
      checkShutdownState();
    } 
    ORBData oRBData = getORBData();
    if (oRBData == null) {
      str = "_INITIALIZING_";
    } else {
      str = oRBData.getORBId();
      if (str.equals(""))
        str = "_DEFAULT_"; 
    } 
    return getCORBALogger(str, paramString);
  }
  
  public static Logger staticGetLogger(String paramString) { return getCORBALogger("_CORBA_", paramString); }
  
  private static Logger getCORBALogger(String paramString1, String paramString2) {
    String str = "javax.enterprise.resource.corba." + paramString1 + "." + paramString2;
    return Logger.getLogger(str, "com.sun.corba.se.impl.logging.LogStrings");
  }
  
  public LogWrapperBase getLogWrapper(String paramString1, String paramString2, LogWrapperFactory paramLogWrapperFactory) {
    StringPair stringPair = new StringPair(paramString1, paramString2);
    LogWrapperBase logWrapperBase = (LogWrapperBase)this.wrapperMap.get(stringPair);
    if (logWrapperBase == null) {
      logWrapperBase = paramLogWrapperFactory.create(getLogger(paramString1));
      this.wrapperMap.put(stringPair, logWrapperBase);
    } 
    return logWrapperBase;
  }
  
  public static LogWrapperBase staticGetLogWrapper(String paramString1, String paramString2, LogWrapperFactory paramLogWrapperFactory) {
    StringPair stringPair = new StringPair(paramString1, paramString2);
    LogWrapperBase logWrapperBase = (LogWrapperBase)staticWrapperMap.get(stringPair);
    if (logWrapperBase == null) {
      logWrapperBase = paramLogWrapperFactory.create(staticGetLogger(paramString1));
      staticWrapperMap.put(stringPair, logWrapperBase);
    } 
    return logWrapperBase;
  }
  
  public ByteBufferPool getByteBufferPool() {
    synchronized (this) {
      checkShutdownState();
    } 
    if (this.byteBufferPool == null)
      this.byteBufferPool = new ByteBufferPoolImpl(this); 
    return this.byteBufferPool;
  }
  
  public abstract void setThreadPoolManager(ThreadPoolManager paramThreadPoolManager);
  
  public abstract ThreadPoolManager getThreadPoolManager();
  
  public abstract CopierManager getCopierManager();
  
  public abstract void validateIORClass(String paramString);
  
  static class Holder {
    static final PresentationManager defaultPresentationManager = ORB.setupPresentationManager();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\orb\ORB.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */