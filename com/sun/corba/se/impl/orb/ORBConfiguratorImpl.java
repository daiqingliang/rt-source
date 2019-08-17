package com.sun.corba.se.impl.orb;

import com.sun.corba.se.impl.dynamicany.DynAnyFactoryImpl;
import com.sun.corba.se.impl.legacy.connection.SocketFactoryAcceptorImpl;
import com.sun.corba.se.impl.legacy.connection.SocketFactoryContactInfoListImpl;
import com.sun.corba.se.impl.legacy.connection.USLPort;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBConstants;
import com.sun.corba.se.impl.transport.SocketOrChannelAcceptorImpl;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.spi.activation.Activator;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import com.sun.corba.se.spi.activation.EndPointInfo;
import com.sun.corba.se.spi.activation.Locator;
import com.sun.corba.se.spi.activation.LocatorHelper;
import com.sun.corba.se.spi.copyobject.CopierManager;
import com.sun.corba.se.spi.copyobject.CopyobjectDefaults;
import com.sun.corba.se.spi.copyobject.ObjectCopierFactory;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import com.sun.corba.se.spi.ior.TaggedComponentFactoryFinder;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketEndPointInfo;
import com.sun.corba.se.spi.legacy.connection.ORBSocketFactory;
import com.sun.corba.se.spi.oa.OADefault;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.orb.DataCollector;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBConfigurator;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.orb.OperationFactory;
import com.sun.corba.se.spi.orb.ParserImplBase;
import com.sun.corba.se.spi.orb.PropertyParser;
import com.sun.corba.se.spi.orbutil.closure.Closure;
import com.sun.corba.se.spi.orbutil.closure.ClosureFactory;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcherFactory;
import com.sun.corba.se.spi.protocol.RequestDispatcherDefault;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import com.sun.corba.se.spi.resolver.LocalResolver;
import com.sun.corba.se.spi.resolver.Resolver;
import com.sun.corba.se.spi.resolver.ResolverDefault;
import com.sun.corba.se.spi.servicecontext.ServiceContextRegistry;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory;
import com.sun.corba.se.spi.transport.TransportDefault;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import java.util.Iterator;
import org.omg.CORBA.CompletionStatus;

public class ORBConfiguratorImpl implements ORBConfigurator {
  private ORBUtilSystemException wrapper;
  
  private static final int ORB_STREAM = 0;
  
  public void configure(DataCollector paramDataCollector, ORB paramORB) {
    ORB oRB = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "orb.lifecycle");
    initObjectCopiers(oRB);
    initIORFinders(oRB);
    oRB.setClientDelegateFactory(TransportDefault.makeClientDelegateFactory(oRB));
    initializeTransport(oRB);
    initializeNaming(oRB);
    initServiceContextRegistry(oRB);
    initRequestDispatcherRegistry(oRB);
    registerInitialReferences(oRB);
    persistentServerInitialization(oRB);
    runUserConfigurators(paramDataCollector, oRB);
  }
  
  private void runUserConfigurators(DataCollector paramDataCollector, ORB paramORB) {
    ConfigParser configParser = new ConfigParser();
    configParser.init(paramDataCollector);
    if (configParser.userConfigurators != null)
      for (byte b = 0; b < configParser.userConfigurators.length; b++) {
        Class clazz = configParser.userConfigurators[b];
        try {
          ORBConfigurator oRBConfigurator = (ORBConfigurator)clazz.newInstance();
          oRBConfigurator.configure(paramDataCollector, paramORB);
        } catch (Exception exception) {}
      }  
  }
  
  private void persistentServerInitialization(ORB paramORB) {
    ORBData oRBData = paramORB.getORBData();
    if (oRBData.getServerIsORBActivated())
      try {
        Locator locator = LocatorHelper.narrow(paramORB.resolve_initial_references("ServerLocator"));
        Activator activator = ActivatorHelper.narrow(paramORB.resolve_initial_references("ServerActivator"));
        Collection collection = paramORB.getCorbaTransportManager().getAcceptors(null, null);
        EndPointInfo[] arrayOfEndPointInfo = new EndPointInfo[collection.size()];
        Iterator iterator = collection.iterator();
        byte b = 0;
        while (iterator.hasNext()) {
          Object object = iterator.next();
          if (!(object instanceof LegacyServerSocketEndPointInfo))
            continue; 
          LegacyServerSocketEndPointInfo legacyServerSocketEndPointInfo = (LegacyServerSocketEndPointInfo)object;
          int i = locator.getEndpoint(legacyServerSocketEndPointInfo.getType());
          if (i == -1) {
            i = locator.getEndpoint("IIOP_CLEAR_TEXT");
            if (i == -1)
              throw new Exception("ORBD must support IIOP_CLEAR_TEXT"); 
          } 
          legacyServerSocketEndPointInfo.setLocatorPort(i);
          arrayOfEndPointInfo[b++] = new EndPointInfo(legacyServerSocketEndPointInfo.getType(), legacyServerSocketEndPointInfo.getPort());
        } 
        activator.registerEndpoints(oRBData.getPersistentServerId(), oRBData.getORBId(), arrayOfEndPointInfo);
      } catch (Exception exception) {
        throw this.wrapper.persistentServerInitError(CompletionStatus.COMPLETED_MAYBE, exception);
      }  
  }
  
  private void initializeTransport(final ORB orb) {
    ORBData oRBData = paramORB.getORBData();
    CorbaContactInfoListFactory corbaContactInfoListFactory = oRBData.getCorbaContactInfoListFactory();
    Acceptor[] arrayOfAcceptor = oRBData.getAcceptors();
    ORBSocketFactory oRBSocketFactory = oRBData.getLegacySocketFactory();
    USLPort[] arrayOfUSLPort1 = oRBData.getUserSpecifiedListenPorts();
    setLegacySocketFactoryORB(paramORB, oRBSocketFactory);
    if (oRBSocketFactory != null && corbaContactInfoListFactory != null)
      throw this.wrapper.socketFactoryAndContactInfoListAtSameTime(); 
    if (arrayOfAcceptor.length != 0 && oRBSocketFactory != null)
      throw this.wrapper.acceptorsAndLegacySocketFactoryAtSameTime(); 
    oRBData.getSocketFactory().setORB(paramORB);
    if (oRBSocketFactory != null) {
      corbaContactInfoListFactory = new CorbaContactInfoListFactory() {
          public void setORB(ORB param1ORB) {}
          
          public CorbaContactInfoList create(IOR param1IOR) { return new SocketFactoryContactInfoListImpl(orb, param1IOR); }
        };
    } else if (corbaContactInfoListFactory != null) {
      corbaContactInfoListFactory.setORB(paramORB);
    } else {
      corbaContactInfoListFactory = TransportDefault.makeCorbaContactInfoListFactory(paramORB);
    } 
    paramORB.setCorbaContactInfoListFactory(corbaContactInfoListFactory);
    int i = -1;
    if (oRBData.getORBServerPort() != 0) {
      i = oRBData.getORBServerPort();
    } else if (oRBData.getPersistentPortInitialized()) {
      i = oRBData.getPersistentServerPort();
    } else if (arrayOfAcceptor.length == 0) {
      i = 0;
    } 
    if (i != -1)
      createAndRegisterAcceptor(paramORB, oRBSocketFactory, i, "DEFAULT_ENDPOINT", "IIOP_CLEAR_TEXT"); 
    for (byte b = 0; b < arrayOfAcceptor.length; b++)
      paramORB.getCorbaTransportManager().registerAcceptor(arrayOfAcceptor[b]); 
    USLPort[] arrayOfUSLPort2 = oRBData.getUserSpecifiedListenPorts();
    if (arrayOfUSLPort2 != null)
      for (byte b1 = 0; b1 < arrayOfUSLPort2.length; b1++)
        createAndRegisterAcceptor(paramORB, oRBSocketFactory, arrayOfUSLPort2[b1].getPort(), "NO_NAME", arrayOfUSLPort2[b1].getType());  
  }
  
  private void createAndRegisterAcceptor(ORB paramORB, ORBSocketFactory paramORBSocketFactory, int paramInt, String paramString1, String paramString2) {
    SocketFactoryAcceptorImpl socketFactoryAcceptorImpl;
    if (paramORBSocketFactory == null) {
      socketFactoryAcceptorImpl = new SocketOrChannelAcceptorImpl(paramORB, paramInt, paramString1, paramString2);
    } else {
      socketFactoryAcceptorImpl = new SocketFactoryAcceptorImpl(paramORB, paramInt, paramString1, paramString2);
    } 
    paramORB.getTransportManager().registerAcceptor(socketFactoryAcceptorImpl);
  }
  
  private void setLegacySocketFactoryORB(final ORB orb, final ORBSocketFactory legacySocketFactory) {
    if (paramORBSocketFactory == null)
      return; 
    try {
      AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Object run() throws InstantiationException, IllegalAccessException {
              try {
                Class[] arrayOfClass = { ORB.class };
                Method method = legacySocketFactory.getClass().getMethod("setORB", arrayOfClass);
                Object[] arrayOfObject = { orb };
                method.invoke(legacySocketFactory, arrayOfObject);
              } catch (NoSuchMethodException noSuchMethodException) {
              
              } catch (IllegalAccessException illegalAccessException) {
                RuntimeException runtimeException = new RuntimeException();
                runtimeException.initCause(illegalAccessException);
                throw runtimeException;
              } catch (InvocationTargetException invocationTargetException) {
                RuntimeException runtimeException = new RuntimeException();
                runtimeException.initCause(invocationTargetException);
                throw runtimeException;
              } 
              return null;
            }
          });
    } catch (Throwable throwable) {
      throw this.wrapper.unableToSetSocketFactoryOrb(throwable);
    } 
  }
  
  private void initializeNaming(ORB paramORB) {
    LocalResolver localResolver = ResolverDefault.makeLocalResolver();
    paramORB.setLocalResolver(localResolver);
    Resolver resolver1 = ResolverDefault.makeBootstrapResolver(paramORB, paramORB.getORBData().getORBInitialHost(), paramORB.getORBData().getORBInitialPort());
    Operation operation = ResolverDefault.makeINSURLOperation(paramORB, resolver1);
    paramORB.setURLOperation(operation);
    Resolver resolver2 = ResolverDefault.makeORBInitRefResolver(operation, paramORB.getORBData().getORBInitialReferences());
    Resolver resolver3 = ResolverDefault.makeORBDefaultInitRefResolver(operation, paramORB.getORBData().getORBDefaultInitialReference());
    Resolver resolver4 = ResolverDefault.makeCompositeResolver(localResolver, ResolverDefault.makeCompositeResolver(resolver2, ResolverDefault.makeCompositeResolver(resolver3, resolver1)));
    paramORB.setResolver(resolver4);
  }
  
  private void initServiceContextRegistry(ORB paramORB) {
    ServiceContextRegistry serviceContextRegistry = paramORB.getServiceContextRegistry();
    serviceContextRegistry.register(com.sun.corba.se.spi.servicecontext.UEInfoServiceContext.class);
    serviceContextRegistry.register(com.sun.corba.se.spi.servicecontext.CodeSetServiceContext.class);
    serviceContextRegistry.register(com.sun.corba.se.spi.servicecontext.SendingContextServiceContext.class);
    serviceContextRegistry.register(com.sun.corba.se.spi.servicecontext.ORBVersionServiceContext.class);
    serviceContextRegistry.register(com.sun.corba.se.spi.servicecontext.MaxStreamFormatVersionServiceContext.class);
  }
  
  private void registerInitialReferences(final ORB orb) {
    Closure closure1 = new Closure() {
        public Object evaluate() throws InstantiationException, IllegalAccessException { return new DynAnyFactoryImpl(orb); }
      };
    Closure closure2 = ClosureFactory.makeFuture(closure1);
    paramORB.getLocalResolver().register("DynAnyFactory", closure2);
  }
  
  private void initObjectCopiers(ORB paramORB) {
    ObjectCopierFactory objectCopierFactory = CopyobjectDefaults.makeORBStreamObjectCopierFactory(paramORB);
    CopierManager copierManager = paramORB.getCopierManager();
    copierManager.setDefaultId(0);
    copierManager.registerObjectCopierFactory(objectCopierFactory, 0);
  }
  
  private void initIORFinders(ORB paramORB) {
    IdentifiableFactoryFinder identifiableFactoryFinder1 = paramORB.getTaggedProfileFactoryFinder();
    identifiableFactoryFinder1.registerFactory(IIOPFactories.makeIIOPProfileFactory());
    IdentifiableFactoryFinder identifiableFactoryFinder2 = paramORB.getTaggedProfileTemplateFactoryFinder();
    identifiableFactoryFinder2.registerFactory(IIOPFactories.makeIIOPProfileTemplateFactory());
    TaggedComponentFactoryFinder taggedComponentFactoryFinder = paramORB.getTaggedComponentFactoryFinder();
    taggedComponentFactoryFinder.registerFactory(IIOPFactories.makeCodeSetsComponentFactory());
    taggedComponentFactoryFinder.registerFactory(IIOPFactories.makeJavaCodebaseComponentFactory());
    taggedComponentFactoryFinder.registerFactory(IIOPFactories.makeORBTypeComponentFactory());
    taggedComponentFactoryFinder.registerFactory(IIOPFactories.makeMaxStreamFormatVersionComponentFactory());
    taggedComponentFactoryFinder.registerFactory(IIOPFactories.makeAlternateIIOPAddressComponentFactory());
    taggedComponentFactoryFinder.registerFactory(IIOPFactories.makeRequestPartitioningComponentFactory());
    taggedComponentFactoryFinder.registerFactory(IIOPFactories.makeJavaSerializationComponentFactory());
    IORFactories.registerValueFactories(paramORB);
    paramORB.setObjectKeyFactory(IORFactories.makeObjectKeyFactory(paramORB));
  }
  
  private void initRequestDispatcherRegistry(ORB paramORB) {
    RequestDispatcherRegistry requestDispatcherRegistry = paramORB.getRequestDispatcherRegistry();
    ClientRequestDispatcher clientRequestDispatcher = RequestDispatcherDefault.makeClientRequestDispatcher();
    requestDispatcherRegistry.registerClientRequestDispatcher(clientRequestDispatcher, 2);
    requestDispatcherRegistry.registerClientRequestDispatcher(clientRequestDispatcher, 32);
    requestDispatcherRegistry.registerClientRequestDispatcher(clientRequestDispatcher, ORBConstants.PERSISTENT_SCID);
    requestDispatcherRegistry.registerClientRequestDispatcher(clientRequestDispatcher, 36);
    requestDispatcherRegistry.registerClientRequestDispatcher(clientRequestDispatcher, ORBConstants.SC_PERSISTENT_SCID);
    requestDispatcherRegistry.registerClientRequestDispatcher(clientRequestDispatcher, 40);
    requestDispatcherRegistry.registerClientRequestDispatcher(clientRequestDispatcher, ORBConstants.IISC_PERSISTENT_SCID);
    requestDispatcherRegistry.registerClientRequestDispatcher(clientRequestDispatcher, 44);
    requestDispatcherRegistry.registerClientRequestDispatcher(clientRequestDispatcher, ORBConstants.MINSC_PERSISTENT_SCID);
    CorbaServerRequestDispatcher corbaServerRequestDispatcher1 = RequestDispatcherDefault.makeServerRequestDispatcher(paramORB);
    requestDispatcherRegistry.registerServerRequestDispatcher(corbaServerRequestDispatcher1, 2);
    requestDispatcherRegistry.registerServerRequestDispatcher(corbaServerRequestDispatcher1, 32);
    requestDispatcherRegistry.registerServerRequestDispatcher(corbaServerRequestDispatcher1, ORBConstants.PERSISTENT_SCID);
    requestDispatcherRegistry.registerServerRequestDispatcher(corbaServerRequestDispatcher1, 36);
    requestDispatcherRegistry.registerServerRequestDispatcher(corbaServerRequestDispatcher1, ORBConstants.SC_PERSISTENT_SCID);
    requestDispatcherRegistry.registerServerRequestDispatcher(corbaServerRequestDispatcher1, 40);
    requestDispatcherRegistry.registerServerRequestDispatcher(corbaServerRequestDispatcher1, ORBConstants.IISC_PERSISTENT_SCID);
    requestDispatcherRegistry.registerServerRequestDispatcher(corbaServerRequestDispatcher1, 44);
    requestDispatcherRegistry.registerServerRequestDispatcher(corbaServerRequestDispatcher1, ORBConstants.MINSC_PERSISTENT_SCID);
    paramORB.setINSDelegate(RequestDispatcherDefault.makeINSServerRequestDispatcher(paramORB));
    LocalClientRequestDispatcherFactory localClientRequestDispatcherFactory = RequestDispatcherDefault.makeJIDLLocalClientRequestDispatcherFactory(paramORB);
    requestDispatcherRegistry.registerLocalClientRequestDispatcherFactory(localClientRequestDispatcherFactory, 2);
    localClientRequestDispatcherFactory = RequestDispatcherDefault.makePOALocalClientRequestDispatcherFactory(paramORB);
    requestDispatcherRegistry.registerLocalClientRequestDispatcherFactory(localClientRequestDispatcherFactory, 32);
    requestDispatcherRegistry.registerLocalClientRequestDispatcherFactory(localClientRequestDispatcherFactory, ORBConstants.PERSISTENT_SCID);
    localClientRequestDispatcherFactory = RequestDispatcherDefault.makeFullServantCacheLocalClientRequestDispatcherFactory(paramORB);
    requestDispatcherRegistry.registerLocalClientRequestDispatcherFactory(localClientRequestDispatcherFactory, 36);
    requestDispatcherRegistry.registerLocalClientRequestDispatcherFactory(localClientRequestDispatcherFactory, ORBConstants.SC_PERSISTENT_SCID);
    localClientRequestDispatcherFactory = RequestDispatcherDefault.makeInfoOnlyServantCacheLocalClientRequestDispatcherFactory(paramORB);
    requestDispatcherRegistry.registerLocalClientRequestDispatcherFactory(localClientRequestDispatcherFactory, 40);
    requestDispatcherRegistry.registerLocalClientRequestDispatcherFactory(localClientRequestDispatcherFactory, ORBConstants.IISC_PERSISTENT_SCID);
    localClientRequestDispatcherFactory = RequestDispatcherDefault.makeMinimalServantCacheLocalClientRequestDispatcherFactory(paramORB);
    requestDispatcherRegistry.registerLocalClientRequestDispatcherFactory(localClientRequestDispatcherFactory, 44);
    requestDispatcherRegistry.registerLocalClientRequestDispatcherFactory(localClientRequestDispatcherFactory, ORBConstants.MINSC_PERSISTENT_SCID);
    CorbaServerRequestDispatcher corbaServerRequestDispatcher2 = RequestDispatcherDefault.makeBootstrapServerRequestDispatcher(paramORB);
    requestDispatcherRegistry.registerServerRequestDispatcher(corbaServerRequestDispatcher2, "INIT");
    requestDispatcherRegistry.registerServerRequestDispatcher(corbaServerRequestDispatcher2, "TINI");
    ObjectAdapterFactory objectAdapterFactory = OADefault.makeTOAFactory(paramORB);
    requestDispatcherRegistry.registerObjectAdapterFactory(objectAdapterFactory, 2);
    objectAdapterFactory = OADefault.makePOAFactory(paramORB);
    requestDispatcherRegistry.registerObjectAdapterFactory(objectAdapterFactory, 32);
    requestDispatcherRegistry.registerObjectAdapterFactory(objectAdapterFactory, ORBConstants.PERSISTENT_SCID);
    requestDispatcherRegistry.registerObjectAdapterFactory(objectAdapterFactory, 36);
    requestDispatcherRegistry.registerObjectAdapterFactory(objectAdapterFactory, ORBConstants.SC_PERSISTENT_SCID);
    requestDispatcherRegistry.registerObjectAdapterFactory(objectAdapterFactory, 40);
    requestDispatcherRegistry.registerObjectAdapterFactory(objectAdapterFactory, ORBConstants.IISC_PERSISTENT_SCID);
    requestDispatcherRegistry.registerObjectAdapterFactory(objectAdapterFactory, 44);
    requestDispatcherRegistry.registerObjectAdapterFactory(objectAdapterFactory, ORBConstants.MINSC_PERSISTENT_SCID);
  }
  
  public static class ConfigParser extends ParserImplBase {
    public Class[] userConfigurators = null;
    
    public PropertyParser makeParser() {
      PropertyParser propertyParser = new PropertyParser();
      Operation operation = OperationFactory.compose(OperationFactory.suffixAction(), OperationFactory.classAction());
      propertyParser.addPrefix("com.sun.CORBA.ORBUserConfigurators", operation, "userConfigurators", Class.class);
      return propertyParser;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orb\ORBConfiguratorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */