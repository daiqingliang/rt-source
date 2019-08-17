package com.sun.corba.se.impl.orb;

import com.sun.corba.se.impl.copyobject.CopierManagerImpl;
import com.sun.corba.se.impl.corba.AnyImpl;
import com.sun.corba.se.impl.corba.AsynchInvoke;
import com.sun.corba.se.impl.corba.ContextListImpl;
import com.sun.corba.se.impl.corba.EnvironmentImpl;
import com.sun.corba.se.impl.corba.ExceptionListImpl;
import com.sun.corba.se.impl.corba.NVListImpl;
import com.sun.corba.se.impl.corba.NamedValueImpl;
import com.sun.corba.se.impl.corba.RequestImpl;
import com.sun.corba.se.impl.corba.TypeCodeImpl;
import com.sun.corba.se.impl.encoding.CachedCodeBase;
import com.sun.corba.se.impl.interceptors.PIHandlerImpl;
import com.sun.corba.se.impl.interceptors.PINoOpHandlerImpl;
import com.sun.corba.se.impl.ior.IORTypeCheckRegistryImpl;
import com.sun.corba.se.impl.ior.TaggedComponentFactoryFinderImpl;
import com.sun.corba.se.impl.ior.TaggedProfileFactoryFinderImpl;
import com.sun.corba.se.impl.ior.TaggedProfileTemplateFactoryFinderImpl;
import com.sun.corba.se.impl.legacy.connection.LegacyServerSocketManagerImpl;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.oa.poa.BadServerIdHandler;
import com.sun.corba.se.impl.oa.poa.POAFactory;
import com.sun.corba.se.impl.oa.toa.TOAFactory;
import com.sun.corba.se.impl.orbutil.ORBConstants;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.orbutil.StackImpl;
import com.sun.corba.se.impl.orbutil.threadpool.ThreadPoolManagerImpl;
import com.sun.corba.se.impl.protocol.CorbaInvocationInfo;
import com.sun.corba.se.impl.protocol.RequestDispatcherRegistryImpl;
import com.sun.corba.se.impl.transport.CorbaTransportManagerImpl;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.pept.protocol.ClientInvocationInfo;
import com.sun.corba.se.pept.transport.TransportManager;
import com.sun.corba.se.spi.copyobject.CopierManager;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORTypeCheckRegistry;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyFactory;
import com.sun.corba.se.spi.ior.TaggedComponentFactoryFinder;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketManager;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.orb.DataCollector;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBConfigurator;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.orb.OperationFactory;
import com.sun.corba.se.spi.orb.ParserImplBase;
import com.sun.corba.se.spi.orb.PropertyParser;
import com.sun.corba.se.spi.orbutil.closure.ClosureFactory;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.corba.se.spi.protocol.ClientDelegateFactory;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.protocol.PIHandler;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import com.sun.corba.se.spi.resolver.LocalResolver;
import com.sun.corba.se.spi.resolver.Resolver;
import com.sun.corba.se.spi.servicecontext.ServiceContextRegistry;
import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory;
import com.sun.corba.se.spi.transport.CorbaTransportManager;
import com.sun.org.omg.SendingContext.CodeBase;
import java.applet.Applet;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Security;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.WeakHashMap;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandler;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.Current;
import org.omg.CORBA.Environment;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.CORBA.PolicyError;
import org.omg.CORBA.Request;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.UnionMember;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.WrongTransaction;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ValueFactory;
import org.omg.PortableServer.Servant;
import sun.corba.OutputStreamFactory;

public class ORBImpl extends ORB {
  protected TransportManager transportManager;
  
  protected LegacyServerSocketManager legacyServerSocketManager;
  
  private ThreadLocal OAInvocationInfoStack;
  
  private ThreadLocal clientInvocationInfoStack;
  
  private static IOR codeBaseIOR;
  
  private Vector dynamicRequests;
  
  private SynchVariable svResponseReceived;
  
  private Object runObj = new Object();
  
  private Object shutdownObj = new Object();
  
  private Object waitForCompletionObj = new Object();
  
  private static final byte STATUS_OPERATING = 1;
  
  private static final byte STATUS_SHUTTING_DOWN = 2;
  
  private static final byte STATUS_SHUTDOWN = 3;
  
  private static final byte STATUS_DESTROYED = 4;
  
  private byte status = 1;
  
  private Object invocationObj = new Object();
  
  private int numInvocations = 0;
  
  private ThreadLocal isProcessingInvocation = new ThreadLocal() {
      protected Object initialValue() { return Boolean.FALSE; }
    };
  
  private Map typeCodeForClassMap;
  
  private Hashtable valueFactoryCache = new Hashtable();
  
  private ThreadLocal orbVersionThreadLocal;
  
  private RequestDispatcherRegistry requestDispatcherRegistry;
  
  private CopierManager copierManager;
  
  private int transientServerId;
  
  private ServiceContextRegistry serviceContextRegistry;
  
  private IORTypeCheckRegistry iorTypeCheckRegistry;
  
  private TOAFactory toaFactory;
  
  private POAFactory poaFactory;
  
  private PIHandler pihandler;
  
  private ORBData configData;
  
  private BadServerIdHandler badServerIdHandler;
  
  private ClientDelegateFactory clientDelegateFactory;
  
  private CorbaContactInfoListFactory corbaContactInfoListFactory;
  
  private Resolver resolver;
  
  private LocalResolver localResolver;
  
  private Operation urlOperation;
  
  private final Object urlOperationLock = new Object();
  
  private CorbaServerRequestDispatcher insNamingDelegate;
  
  private final Object resolverLock = new Object();
  
  private static final String IORTYPECHECKREGISTRY_FILTER_PROPNAME = "com.sun.CORBA.ORBIorTypeCheckRegistryFilter";
  
  private TaggedComponentFactoryFinder taggedComponentFactoryFinder;
  
  private IdentifiableFactoryFinder taggedProfileFactoryFinder;
  
  private IdentifiableFactoryFinder taggedProfileTemplateFactoryFinder;
  
  private ObjectKeyFactory objectKeyFactory;
  
  private boolean orbOwnsThreadPoolManager = false;
  
  private ThreadPoolManager threadpoolMgr;
  
  private Object badServerIdHandlerAccessLock = new Object();
  
  private static String localHostString = null;
  
  private Object clientDelegateFactoryAccessorLock = new Object();
  
  private Object corbaContactInfoListFactoryAccessLock = new Object();
  
  private Object objectKeyFactoryAccessLock = new Object();
  
  private Object transportManagerAccessorLock = new Object();
  
  private Object legacyServerSocketManagerAccessLock = new Object();
  
  private Object threadPoolManagerAccessLock = new Object();
  
  private void dprint(String paramString) { ORBUtility.dprint(this, paramString); }
  
  public ORBData getORBData() { return this.configData; }
  
  public PIHandler getPIHandler() { return this.pihandler; }
  
  public ORBVersion getORBVersion() {
    synchronized (this) {
      checkShutdownState();
    } 
    return (ORBVersion)this.orbVersionThreadLocal.get();
  }
  
  public void setORBVersion(ORBVersion paramORBVersion) {
    synchronized (this) {
      checkShutdownState();
    } 
    this.orbVersionThreadLocal.set(paramORBVersion);
  }
  
  private void preInit(String[] paramArrayOfString, Properties paramProperties) {
    this.pihandler = new PINoOpHandlerImpl();
    this.transientServerId = (int)System.currentTimeMillis();
    this.orbVersionThreadLocal = new ThreadLocal() {
        protected Object initialValue() { return ORBVersionFactory.getORBVersion(); }
      };
    this.requestDispatcherRegistry = new RequestDispatcherRegistryImpl(this, 2);
    this.copierManager = new CopierManagerImpl(this);
    this.taggedComponentFactoryFinder = new TaggedComponentFactoryFinderImpl(this);
    this.taggedProfileFactoryFinder = new TaggedProfileFactoryFinderImpl(this);
    this.taggedProfileTemplateFactoryFinder = new TaggedProfileTemplateFactoryFinderImpl(this);
    this.dynamicRequests = new Vector();
    this.svResponseReceived = new SynchVariable();
    this.OAInvocationInfoStack = new ThreadLocal() {
        protected Object initialValue() { return new StackImpl(); }
      };
    this.clientInvocationInfoStack = new ThreadLocal() {
        protected Object initialValue() { return new StackImpl(); }
      };
    this.serviceContextRegistry = new ServiceContextRegistry(this);
  }
  
  private void initIORTypeCheckRegistry() {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() {
            String str = System.getProperty("com.sun.CORBA.ORBIorTypeCheckRegistryFilter");
            if (str == null)
              str = Security.getProperty("com.sun.CORBA.ORBIorTypeCheckRegistryFilter"); 
            return str;
          }
        });
    if (str != null) {
      try {
        this.iorTypeCheckRegistry = new IORTypeCheckRegistryImpl(str, this);
      } catch (Exception exception) {
        throw this.wrapper.bootstrapException(exception);
      } 
      if (this.orbInitDebugFlag)
        dprint(".initIORTypeCheckRegistry, IORTypeCheckRegistryImpl created for properties == " + str); 
    } else if (this.orbInitDebugFlag) {
      dprint(".initIORTypeCheckRegistry, IORTypeCheckRegistryImpl NOT created for properties == ");
    } 
  }
  
  protected void setDebugFlags(String[] paramArrayOfString) {
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      String str = paramArrayOfString[b];
      try {
        Field field = getClass().getField(str + "DebugFlag");
        int i = field.getModifiers();
        if (Modifier.isPublic(i) && !Modifier.isStatic(i) && field.getType() == boolean.class)
          field.setBoolean(this, true); 
      } catch (Exception exception) {}
    } 
  }
  
  private void postInit(String[] paramArrayOfString, DataCollector paramDataCollector) {
    this.configData = new ORBDataParserImpl(this, paramDataCollector);
    setDebugFlags(this.configData.getORBDebugFlags());
    getTransportManager();
    getLegacyServerSocketManager();
    ConfigParser configParser = new ConfigParser(null);
    configParser.init(paramDataCollector);
    ORBConfigurator oRBConfigurator = null;
    try {
      oRBConfigurator = (ORBConfigurator)configParser.configurator.newInstance();
    } catch (Exception exception) {
      throw this.wrapper.badOrbConfigurator(exception, configParser.configurator.getName());
    } 
    try {
      oRBConfigurator.configure(paramDataCollector, this);
    } catch (Exception exception) {
      throw this.wrapper.orbConfiguratorError(exception);
    } 
    this.pihandler = new PIHandlerImpl(this, paramArrayOfString);
    this.pihandler.initialize();
    getThreadPoolManager();
    getByteBufferPool();
    initIORTypeCheckRegistry();
  }
  
  private POAFactory getPOAFactory() {
    if (this.poaFactory == null)
      this.poaFactory = (POAFactory)this.requestDispatcherRegistry.getObjectAdapterFactory(32); 
    return this.poaFactory;
  }
  
  private TOAFactory getTOAFactory() {
    if (this.toaFactory == null)
      this.toaFactory = (TOAFactory)this.requestDispatcherRegistry.getObjectAdapterFactory(2); 
    return this.toaFactory;
  }
  
  public void set_parameters(Properties paramProperties) {
    synchronized (this) {
      checkShutdownState();
    } 
    preInit(null, paramProperties);
    DataCollector dataCollector = DataCollectorFactory.create(paramProperties, getLocalHostName());
    postInit(null, dataCollector);
  }
  
  protected void set_parameters(Applet paramApplet, Properties paramProperties) {
    preInit(null, paramProperties);
    DataCollector dataCollector = DataCollectorFactory.create(paramApplet, paramProperties, getLocalHostName());
    postInit(null, dataCollector);
  }
  
  protected void set_parameters(String[] paramArrayOfString, Properties paramProperties) {
    preInit(paramArrayOfString, paramProperties);
    DataCollector dataCollector = DataCollectorFactory.create(paramArrayOfString, paramProperties, getLocalHostName());
    postInit(paramArrayOfString, dataCollector);
  }
  
  public OutputStream create_output_stream() {
    checkShutdownState();
    return OutputStreamFactory.newEncapsOutputStream(this);
  }
  
  public Current get_current() {
    checkShutdownState();
    throw this.wrapper.genericNoImpl();
  }
  
  public NVList create_list(int paramInt) {
    checkShutdownState();
    return new NVListImpl(this, paramInt);
  }
  
  public NVList create_operation_list(Object paramObject) {
    checkShutdownState();
    throw this.wrapper.genericNoImpl();
  }
  
  public NamedValue create_named_value(String paramString, Any paramAny, int paramInt) {
    checkShutdownState();
    return new NamedValueImpl(this, paramString, paramAny, paramInt);
  }
  
  public ExceptionList create_exception_list() {
    checkShutdownState();
    return new ExceptionListImpl();
  }
  
  public ContextList create_context_list() {
    checkShutdownState();
    return new ContextListImpl(this);
  }
  
  public Context get_default_context() {
    checkShutdownState();
    throw this.wrapper.genericNoImpl();
  }
  
  public Environment create_environment() {
    checkShutdownState();
    return new EnvironmentImpl();
  }
  
  public void send_multiple_requests_oneway(Request[] paramArrayOfRequest) {
    checkShutdownState();
    for (byte b = 0; b < paramArrayOfRequest.length; b++)
      paramArrayOfRequest[b].send_oneway(); 
  }
  
  public void send_multiple_requests_deferred(Request[] paramArrayOfRequest) {
    checkShutdownState();
    byte b;
    for (b = 0; b < paramArrayOfRequest.length; b++)
      this.dynamicRequests.addElement(paramArrayOfRequest[b]); 
    for (b = 0; b < paramArrayOfRequest.length; b++) {
      AsynchInvoke asynchInvoke = new AsynchInvoke(this, (RequestImpl)paramArrayOfRequest[b], true);
      (new Thread(asynchInvoke)).start();
    } 
  }
  
  public boolean poll_next_response() {
    checkShutdownState();
    Enumeration enumeration = this.dynamicRequests.elements();
    while (enumeration.hasMoreElements() == true) {
      Request request = (Request)enumeration.nextElement();
      if (request.poll_response() == true)
        return true; 
    } 
    return false;
  }
  
  public Request get_next_response() throws WrongTransaction {
    synchronized (this) {
      checkShutdownState();
    } 
    while (true) {
      synchronized (this.dynamicRequests) {
        Enumeration enumeration = this.dynamicRequests.elements();
        while (enumeration.hasMoreElements()) {
          Request request = (Request)enumeration.nextElement();
          if (request.poll_response()) {
            request.get_response();
            this.dynamicRequests.removeElement(request);
            return request;
          } 
        } 
      } 
      synchronized (this.svResponseReceived) {
        while (!this.svResponseReceived.value()) {
          try {
            this.svResponseReceived.wait();
          } catch (InterruptedException interruptedException) {}
        } 
        this.svResponseReceived.reset();
      } 
    } 
  }
  
  public void notifyORB() {
    synchronized (this) {
      checkShutdownState();
    } 
    synchronized (this.svResponseReceived) {
      this.svResponseReceived.set();
      this.svResponseReceived.notify();
    } 
  }
  
  public String object_to_string(Object paramObject) {
    checkShutdownState();
    if (paramObject == null) {
      IOR iOR1 = IORFactories.makeIOR(this);
      return iOR1.stringify();
    } 
    IOR iOR = null;
    try {
      iOR = ORBUtility.connectAndGetIOR(this, paramObject);
    } catch (BAD_PARAM bAD_PARAM) {
      if (bAD_PARAM.minor == 1398079694)
        throw this.omgWrapper.notAnObjectImpl(bAD_PARAM); 
      throw bAD_PARAM;
    } 
    return iOR.stringify();
  }
  
  public Object string_to_object(String paramString) {
    Operation operation;
    synchronized (this) {
      checkShutdownState();
      operation = this.urlOperation;
    } 
    if (paramString == null)
      throw this.wrapper.nullParam(); 
    synchronized (this.urlOperationLock) {
      return (Object)operation.operate(paramString);
    } 
  }
  
  public IOR getFVDCodeBaseIOR() {
    checkShutdownState();
    if (codeBaseIOR != null)
      return codeBaseIOR; 
    ValueHandler valueHandler = ORBUtility.createValueHandler();
    CodeBase codeBase = (CodeBase)valueHandler.getRunTimeCodeBase();
    return ORBUtility.connectAndGetIOR(this, codeBase);
  }
  
  public TypeCode get_primitive_tc(TCKind paramTCKind) {
    checkShutdownState();
    return get_primitive_tc(paramTCKind.value());
  }
  
  public TypeCode create_struct_tc(String paramString1, String paramString2, StructMember[] paramArrayOfStructMember) {
    checkShutdownState();
    return new TypeCodeImpl(this, 15, paramString1, paramString2, paramArrayOfStructMember);
  }
  
  public TypeCode create_union_tc(String paramString1, String paramString2, TypeCode paramTypeCode, UnionMember[] paramArrayOfUnionMember) {
    checkShutdownState();
    return new TypeCodeImpl(this, 16, paramString1, paramString2, paramTypeCode, paramArrayOfUnionMember);
  }
  
  public TypeCode create_enum_tc(String paramString1, String paramString2, String[] paramArrayOfString) {
    checkShutdownState();
    return new TypeCodeImpl(this, 17, paramString1, paramString2, paramArrayOfString);
  }
  
  public TypeCode create_alias_tc(String paramString1, String paramString2, TypeCode paramTypeCode) {
    checkShutdownState();
    return new TypeCodeImpl(this, 21, paramString1, paramString2, paramTypeCode);
  }
  
  public TypeCode create_exception_tc(String paramString1, String paramString2, StructMember[] paramArrayOfStructMember) {
    checkShutdownState();
    return new TypeCodeImpl(this, 22, paramString1, paramString2, paramArrayOfStructMember);
  }
  
  public TypeCode create_interface_tc(String paramString1, String paramString2) {
    checkShutdownState();
    return new TypeCodeImpl(this, 14, paramString1, paramString2);
  }
  
  public TypeCode create_string_tc(int paramInt) {
    checkShutdownState();
    return new TypeCodeImpl(this, 18, paramInt);
  }
  
  public TypeCode create_wstring_tc(int paramInt) {
    checkShutdownState();
    return new TypeCodeImpl(this, 27, paramInt);
  }
  
  public TypeCode create_sequence_tc(int paramInt, TypeCode paramTypeCode) {
    checkShutdownState();
    return new TypeCodeImpl(this, 19, paramInt, paramTypeCode);
  }
  
  public TypeCode create_recursive_sequence_tc(int paramInt1, int paramInt2) {
    checkShutdownState();
    return new TypeCodeImpl(this, 19, paramInt1, paramInt2);
  }
  
  public TypeCode create_array_tc(int paramInt, TypeCode paramTypeCode) {
    checkShutdownState();
    return new TypeCodeImpl(this, 20, paramInt, paramTypeCode);
  }
  
  public TypeCode create_native_tc(String paramString1, String paramString2) {
    checkShutdownState();
    return new TypeCodeImpl(this, 31, paramString1, paramString2);
  }
  
  public TypeCode create_abstract_interface_tc(String paramString1, String paramString2) {
    checkShutdownState();
    return new TypeCodeImpl(this, 32, paramString1, paramString2);
  }
  
  public TypeCode create_fixed_tc(short paramShort1, short paramShort2) {
    checkShutdownState();
    return new TypeCodeImpl(this, 28, paramShort1, paramShort2);
  }
  
  public TypeCode create_value_tc(String paramString1, String paramString2, short paramShort, TypeCode paramTypeCode, ValueMember[] paramArrayOfValueMember) {
    checkShutdownState();
    return new TypeCodeImpl(this, 29, paramString1, paramString2, paramShort, paramTypeCode, paramArrayOfValueMember);
  }
  
  public TypeCode create_recursive_tc(String paramString) {
    checkShutdownState();
    return new TypeCodeImpl(this, paramString);
  }
  
  public TypeCode create_value_box_tc(String paramString1, String paramString2, TypeCode paramTypeCode) {
    checkShutdownState();
    return new TypeCodeImpl(this, 30, paramString1, paramString2, paramTypeCode);
  }
  
  public Any create_any() {
    checkShutdownState();
    return new AnyImpl(this);
  }
  
  public void setTypeCodeForClass(Class paramClass, TypeCodeImpl paramTypeCodeImpl) {
    checkShutdownState();
    if (this.typeCodeForClassMap == null)
      this.typeCodeForClassMap = Collections.synchronizedMap(new WeakHashMap(64)); 
    if (!this.typeCodeForClassMap.containsKey(paramClass))
      this.typeCodeForClassMap.put(paramClass, paramTypeCodeImpl); 
  }
  
  public TypeCodeImpl getTypeCodeForClass(Class paramClass) {
    checkShutdownState();
    return (this.typeCodeForClassMap == null) ? null : (TypeCodeImpl)this.typeCodeForClassMap.get(paramClass);
  }
  
  public String[] list_initial_services() {
    Resolver resolver1;
    synchronized (this) {
      checkShutdownState();
      resolver1 = this.resolver;
    } 
    synchronized (this.resolverLock) {
      Set set = resolver1.list();
      return (String[])set.toArray(new String[set.size()]);
    } 
  }
  
  public Object resolve_initial_references(String paramString) {
    Resolver resolver1;
    synchronized (this) {
      checkShutdownState();
      resolver1 = this.resolver;
    } 
    synchronized (this.resolverLock) {
      Object object = resolver1.resolve(paramString);
      if (object == null)
        throw new InvalidName(); 
      return object;
    } 
  }
  
  public void register_initial_reference(String paramString, Object paramObject) throws InvalidName {
    CorbaServerRequestDispatcher corbaServerRequestDispatcher;
    synchronized (this) {
      checkShutdownState();
    } 
    if (paramString == null || paramString.length() == 0)
      throw new InvalidName(); 
    synchronized (this) {
      checkShutdownState();
    } 
    synchronized (this.resolverLock) {
      corbaServerRequestDispatcher = this.insNamingDelegate;
      Object object = this.localResolver.resolve(paramString);
      if (object != null)
        throw new InvalidName(paramString + " already registered"); 
      this.localResolver.register(paramString, ClosureFactory.makeConstant(paramObject));
    } 
    synchronized (this) {
      if (StubAdapter.isStub(paramObject))
        this.requestDispatcherRegistry.registerServerRequestDispatcher(corbaServerRequestDispatcher, paramString); 
    } 
  }
  
  public void run() {
    synchronized (this) {
      checkShutdownState();
    } 
    synchronized (this.runObj) {
      try {
        this.runObj.wait();
      } catch (InterruptedException interruptedException) {}
    } 
  }
  
  public void shutdown(boolean paramBoolean) {
    boolean bool = false;
    synchronized (this) {
      checkShutdownState();
      if (paramBoolean && this.isProcessingInvocation.get() == Boolean.TRUE)
        throw this.omgWrapper.shutdownWaitForCompletionDeadlock(); 
      if (this.status == 2)
        if (paramBoolean) {
          bool = true;
        } else {
          return;
        }  
      this.status = 2;
    } 
    synchronized (this.shutdownObj) {
      if (bool) {
        while (true) {
          synchronized (this) {
            if (this.status == 3)
              break; 
          } 
          try {
            this.shutdownObj.wait();
          } catch (InterruptedException interruptedException) {}
        } 
      } else {
        shutdownServants(paramBoolean);
        if (paramBoolean)
          synchronized (this.waitForCompletionObj) {
            while (this.numInvocations > 0) {
              try {
                this.waitForCompletionObj.wait();
              } catch (InterruptedException interruptedException) {}
            } 
          }  
        synchronized (this.runObj) {
          this.runObj.notifyAll();
        } 
        this.status = 3;
        this.shutdownObj.notifyAll();
      } 
    } 
  }
  
  protected void shutdownServants(boolean paramBoolean) {
    HashSet hashSet;
    synchronized (this) {
      hashSet = new HashSet(this.requestDispatcherRegistry.getObjectAdapterFactories());
    } 
    for (ObjectAdapterFactory objectAdapterFactory : hashSet)
      objectAdapterFactory.shutdown(paramBoolean); 
  }
  
  public void checkShutdownState() {
    if (this.status == 4)
      throw this.wrapper.orbDestroyed(); 
    if (this.status == 3)
      throw this.omgWrapper.badOperationAfterShutdown(); 
  }
  
  public boolean isDuringDispatch() {
    synchronized (this) {
      checkShutdownState();
    } 
    Boolean bool = (Boolean)this.isProcessingInvocation.get();
    return bool.booleanValue();
  }
  
  public void startingDispatch() {
    synchronized (this) {
      checkShutdownState();
    } 
    synchronized (this.invocationObj) {
      this.isProcessingInvocation.set(Boolean.TRUE);
      this.numInvocations++;
    } 
  }
  
  public void finishedDispatch() {
    synchronized (this) {
      checkShutdownState();
    } 
    synchronized (this.invocationObj) {
      this.numInvocations--;
      this.isProcessingInvocation.set(Boolean.valueOf(false));
      if (this.numInvocations == 0) {
        synchronized (this.waitForCompletionObj) {
          this.waitForCompletionObj.notifyAll();
        } 
      } else if (this.numInvocations < 0) {
        throw this.wrapper.numInvocationsAlreadyZero(CompletionStatus.COMPLETED_YES);
      } 
    } 
  }
  
  public void destroy() {
    boolean bool = false;
    synchronized (this) {
      bool = (this.status == 1) ? 1 : 0;
    } 
    if (bool)
      shutdown(true); 
    synchronized (this) {
      if (this.status < 4) {
        getCorbaTransportManager().close();
        getPIHandler().destroyInterceptors();
        this.status = 4;
      } 
    } 
    synchronized (this.threadPoolManagerAccessLock) {
      if (this.orbOwnsThreadPoolManager)
        try {
          this.threadpoolMgr.close();
          this.threadpoolMgr = null;
        } catch (IOException iOException) {
          this.wrapper.ioExceptionOnClose(iOException);
        }  
    } 
    try {
      this.monitoringManager.close();
      this.monitoringManager = null;
    } catch (IOException iOException) {
      this.wrapper.ioExceptionOnClose(iOException);
    } 
    CachedCodeBase.cleanCache(this);
    try {
      this.pihandler.close();
    } catch (IOException iOException) {
      this.wrapper.ioExceptionOnClose(iOException);
    } 
    super.destroy();
    this.badServerIdHandlerAccessLock = null;
    this.clientDelegateFactoryAccessorLock = null;
    this.corbaContactInfoListFactoryAccessLock = null;
    this.objectKeyFactoryAccessLock = null;
    this.legacyServerSocketManagerAccessLock = null;
    this.threadPoolManagerAccessLock = null;
    this.transportManager = null;
    this.legacyServerSocketManager = null;
    this.OAInvocationInfoStack = null;
    this.clientInvocationInfoStack = null;
    codeBaseIOR = null;
    this.dynamicRequests = null;
    this.svResponseReceived = null;
    this.runObj = null;
    this.shutdownObj = null;
    this.waitForCompletionObj = null;
    this.invocationObj = null;
    this.isProcessingInvocation = null;
    this.typeCodeForClassMap = null;
    this.valueFactoryCache = null;
    this.orbVersionThreadLocal = null;
    this.requestDispatcherRegistry = null;
    this.copierManager = null;
    this.toaFactory = null;
    this.poaFactory = null;
    this.pihandler = null;
    this.configData = null;
    this.badServerIdHandler = null;
    this.clientDelegateFactory = null;
    this.corbaContactInfoListFactory = null;
    this.resolver = null;
    this.localResolver = null;
    this.insNamingDelegate = null;
    this.urlOperation = null;
    this.taggedComponentFactoryFinder = null;
    this.taggedProfileFactoryFinder = null;
    this.taggedProfileTemplateFactoryFinder = null;
    this.objectKeyFactory = null;
  }
  
  public ValueFactory register_value_factory(String paramString, ValueFactory paramValueFactory) {
    checkShutdownState();
    if (paramString == null || paramValueFactory == null)
      throw this.omgWrapper.unableRegisterValueFactory(); 
    return (ValueFactory)this.valueFactoryCache.put(paramString, paramValueFactory);
  }
  
  public void unregister_value_factory(String paramString) {
    checkShutdownState();
    if (this.valueFactoryCache.remove(paramString) == null)
      throw this.wrapper.nullParam(); 
  }
  
  public ValueFactory lookup_value_factory(String paramString) {
    checkShutdownState();
    ValueFactory valueFactory = (ValueFactory)this.valueFactoryCache.get(paramString);
    if (valueFactory == null)
      try {
        valueFactory = Utility.getFactory(null, null, null, paramString);
      } catch (MARSHAL mARSHAL) {
        throw this.wrapper.unableFindValueFactory(mARSHAL);
      }  
    return valueFactory;
  }
  
  public OAInvocationInfo peekInvocationInfo() {
    synchronized (this) {
      checkShutdownState();
    } 
    StackImpl stackImpl = (StackImpl)this.OAInvocationInfoStack.get();
    return (OAInvocationInfo)stackImpl.peek();
  }
  
  public void pushInvocationInfo(OAInvocationInfo paramOAInvocationInfo) {
    synchronized (this) {
      checkShutdownState();
    } 
    StackImpl stackImpl = (StackImpl)this.OAInvocationInfoStack.get();
    stackImpl.push(paramOAInvocationInfo);
  }
  
  public OAInvocationInfo popInvocationInfo() {
    synchronized (this) {
      checkShutdownState();
    } 
    StackImpl stackImpl = (StackImpl)this.OAInvocationInfoStack.get();
    return (OAInvocationInfo)stackImpl.pop();
  }
  
  public void initBadServerIdHandler() {
    synchronized (this) {
      checkShutdownState();
    } 
    synchronized (this.badServerIdHandlerAccessLock) {
      Class clazz = this.configData.getBadServerIdHandler();
      if (clazz != null)
        try {
          Class[] arrayOfClass = { org.omg.CORBA.ORB.class };
          Object[] arrayOfObject = { this };
          Constructor constructor = clazz.getConstructor(arrayOfClass);
          this.badServerIdHandler = (BadServerIdHandler)constructor.newInstance(arrayOfObject);
        } catch (Exception exception) {
          throw this.wrapper.errorInitBadserveridhandler(exception);
        }  
    } 
  }
  
  public void setBadServerIdHandler(BadServerIdHandler paramBadServerIdHandler) {
    synchronized (this) {
      checkShutdownState();
    } 
    synchronized (this.badServerIdHandlerAccessLock) {
      this.badServerIdHandler = paramBadServerIdHandler;
    } 
  }
  
  public void handleBadServerId(ObjectKey paramObjectKey) {
    synchronized (this) {
      checkShutdownState();
    } 
    synchronized (this.badServerIdHandlerAccessLock) {
      if (this.badServerIdHandler == null)
        throw this.wrapper.badServerId(); 
      this.badServerIdHandler.handle(paramObjectKey);
    } 
  }
  
  public Policy create_policy(int paramInt, Any paramAny) throws PolicyError {
    checkShutdownState();
    return this.pihandler.create_policy(paramInt, paramAny);
  }
  
  public void connect(Object paramObject) {
    checkShutdownState();
    if (getTOAFactory() == null)
      throw this.wrapper.noToa(); 
    try {
      String str = Util.getCodebase(paramObject.getClass());
      getTOAFactory().getTOA(str).connect(paramObject);
    } catch (Exception exception) {
      throw this.wrapper.orbConnectError(exception);
    } 
  }
  
  public void disconnect(Object paramObject) {
    checkShutdownState();
    if (getTOAFactory() == null)
      throw this.wrapper.noToa(); 
    try {
      getTOAFactory().getTOA().disconnect(paramObject);
    } catch (Exception exception) {
      throw this.wrapper.orbConnectError(exception);
    } 
  }
  
  public int getTransientServerId() {
    synchronized (this) {
      checkShutdownState();
    } 
    return this.configData.getORBServerIdPropertySpecified() ? this.configData.getPersistentServerId() : this.transientServerId;
  }
  
  public RequestDispatcherRegistry getRequestDispatcherRegistry() {
    synchronized (this) {
      checkShutdownState();
    } 
    return this.requestDispatcherRegistry;
  }
  
  public ServiceContextRegistry getServiceContextRegistry() {
    synchronized (this) {
      checkShutdownState();
    } 
    return this.serviceContextRegistry;
  }
  
  public boolean isLocalHost(String paramString) {
    synchronized (this) {
      checkShutdownState();
    } 
    return (paramString.equals(this.configData.getORBServerHost()) || paramString.equals(getLocalHostName()));
  }
  
  public boolean isLocalServerId(int paramInt1, int paramInt2) {
    synchronized (this) {
      checkShutdownState();
    } 
    return (paramInt1 < 32 || paramInt1 > 63) ? ((paramInt2 == getTransientServerId())) : (ORBConstants.isTransient(paramInt1) ? ((paramInt2 == getTransientServerId())) : (this.configData.getPersistentServerIdInitialized() ? ((paramInt2 == this.configData.getPersistentServerId())) : false));
  }
  
  private String getHostName(String paramString) throws UnknownHostException { return InetAddress.getByName(paramString).getHostAddress(); }
  
  private String getLocalHostName() {
    if (localHostString == null)
      try {
        localHostString = InetAddress.getLocalHost().getHostAddress();
      } catch (Exception exception) {
        throw this.wrapper.getLocalHostFailed(exception);
      }  
    return localHostString;
  }
  
  public boolean work_pending() {
    checkShutdownState();
    throw this.wrapper.genericNoImpl();
  }
  
  public void perform_work() {
    checkShutdownState();
    throw this.wrapper.genericNoImpl();
  }
  
  public void set_delegate(Object paramObject) {
    checkShutdownState();
    POAFactory pOAFactory = getPOAFactory();
    if (pOAFactory != null) {
      ((Servant)paramObject)._set_delegate(pOAFactory.getDelegateImpl());
    } else {
      throw this.wrapper.noPoa();
    } 
  }
  
  public ClientInvocationInfo createOrIncrementInvocationInfo() {
    synchronized (this) {
      checkShutdownState();
    } 
    StackImpl stackImpl = (StackImpl)this.clientInvocationInfoStack.get();
    ClientInvocationInfo clientInvocationInfo = null;
    if (!stackImpl.empty())
      clientInvocationInfo = (ClientInvocationInfo)stackImpl.peek(); 
    if (clientInvocationInfo == null || !clientInvocationInfo.isRetryInvocation()) {
      clientInvocationInfo = new CorbaInvocationInfo(this);
      startingDispatch();
      stackImpl.push(clientInvocationInfo);
    } 
    clientInvocationInfo.setIsRetryInvocation(false);
    clientInvocationInfo.incrementEntryCount();
    return clientInvocationInfo;
  }
  
  public void releaseOrDecrementInvocationInfo() {
    synchronized (this) {
      checkShutdownState();
    } 
    int i = -1;
    ClientInvocationInfo clientInvocationInfo = null;
    StackImpl stackImpl = (StackImpl)this.clientInvocationInfoStack.get();
    if (!stackImpl.empty()) {
      clientInvocationInfo = (ClientInvocationInfo)stackImpl.peek();
    } else {
      throw this.wrapper.invocationInfoStackEmpty();
    } 
    clientInvocationInfo.decrementEntryCount();
    i = clientInvocationInfo.getEntryCount();
    if (clientInvocationInfo.getEntryCount() == 0) {
      if (!clientInvocationInfo.isRetryInvocation())
        stackImpl.pop(); 
      finishedDispatch();
    } 
  }
  
  public ClientInvocationInfo getInvocationInfo() {
    synchronized (this) {
      checkShutdownState();
    } 
    StackImpl stackImpl = (StackImpl)this.clientInvocationInfoStack.get();
    return (ClientInvocationInfo)stackImpl.peek();
  }
  
  public void setClientDelegateFactory(ClientDelegateFactory paramClientDelegateFactory) {
    synchronized (this) {
      checkShutdownState();
    } 
    synchronized (this.clientDelegateFactoryAccessorLock) {
      this.clientDelegateFactory = paramClientDelegateFactory;
    } 
  }
  
  public ClientDelegateFactory getClientDelegateFactory() {
    synchronized (this) {
      checkShutdownState();
    } 
    synchronized (this.clientDelegateFactoryAccessorLock) {
      return this.clientDelegateFactory;
    } 
  }
  
  public void setCorbaContactInfoListFactory(CorbaContactInfoListFactory paramCorbaContactInfoListFactory) {
    synchronized (this) {
      checkShutdownState();
    } 
    synchronized (this.corbaContactInfoListFactoryAccessLock) {
      this.corbaContactInfoListFactory = paramCorbaContactInfoListFactory;
    } 
  }
  
  public CorbaContactInfoListFactory getCorbaContactInfoListFactory() {
    checkShutdownState();
    return this.corbaContactInfoListFactory;
  }
  
  public void setResolver(Resolver paramResolver) {
    synchronized (this) {
      checkShutdownState();
    } 
    synchronized (this.resolverLock) {
      this.resolver = paramResolver;
    } 
  }
  
  public Resolver getResolver() {
    synchronized (this) {
      checkShutdownState();
    } 
    synchronized (this.resolverLock) {
      return this.resolver;
    } 
  }
  
  public void setLocalResolver(LocalResolver paramLocalResolver) {
    synchronized (this) {
      checkShutdownState();
    } 
    synchronized (this.resolverLock) {
      this.localResolver = paramLocalResolver;
    } 
  }
  
  public LocalResolver getLocalResolver() {
    synchronized (this) {
      checkShutdownState();
    } 
    synchronized (this.resolverLock) {
      return this.localResolver;
    } 
  }
  
  public void setURLOperation(Operation paramOperation) {
    synchronized (this) {
      checkShutdownState();
    } 
    synchronized (this.urlOperationLock) {
      this.urlOperation = paramOperation;
    } 
  }
  
  public Operation getURLOperation() {
    synchronized (this) {
      checkShutdownState();
    } 
    synchronized (this.urlOperationLock) {
      return this.urlOperation;
    } 
  }
  
  public void setINSDelegate(CorbaServerRequestDispatcher paramCorbaServerRequestDispatcher) {
    synchronized (this) {
      checkShutdownState();
    } 
    synchronized (this.resolverLock) {
      this.insNamingDelegate = paramCorbaServerRequestDispatcher;
    } 
  }
  
  public TaggedComponentFactoryFinder getTaggedComponentFactoryFinder() {
    synchronized (this) {
      checkShutdownState();
    } 
    return this.taggedComponentFactoryFinder;
  }
  
  public IdentifiableFactoryFinder getTaggedProfileFactoryFinder() {
    synchronized (this) {
      checkShutdownState();
    } 
    return this.taggedProfileFactoryFinder;
  }
  
  public IdentifiableFactoryFinder getTaggedProfileTemplateFactoryFinder() {
    synchronized (this) {
      checkShutdownState();
    } 
    return this.taggedProfileTemplateFactoryFinder;
  }
  
  public ObjectKeyFactory getObjectKeyFactory() {
    synchronized (this) {
      checkShutdownState();
    } 
    synchronized (this.objectKeyFactoryAccessLock) {
      return this.objectKeyFactory;
    } 
  }
  
  public void setObjectKeyFactory(ObjectKeyFactory paramObjectKeyFactory) {
    synchronized (this) {
      checkShutdownState();
    } 
    synchronized (this.objectKeyFactoryAccessLock) {
      this.objectKeyFactory = paramObjectKeyFactory;
    } 
  }
  
  public TransportManager getTransportManager() {
    synchronized (this.transportManagerAccessorLock) {
      if (this.transportManager == null)
        this.transportManager = new CorbaTransportManagerImpl(this); 
      return this.transportManager;
    } 
  }
  
  public CorbaTransportManager getCorbaTransportManager() { return (CorbaTransportManager)getTransportManager(); }
  
  public LegacyServerSocketManager getLegacyServerSocketManager() {
    synchronized (this) {
      checkShutdownState();
    } 
    synchronized (this.legacyServerSocketManagerAccessLock) {
      if (this.legacyServerSocketManager == null)
        this.legacyServerSocketManager = new LegacyServerSocketManagerImpl(this); 
      return this.legacyServerSocketManager;
    } 
  }
  
  public void setThreadPoolManager(ThreadPoolManager paramThreadPoolManager) {
    synchronized (this) {
      checkShutdownState();
    } 
    synchronized (this.threadPoolManagerAccessLock) {
      this.threadpoolMgr = paramThreadPoolManager;
    } 
  }
  
  public ThreadPoolManager getThreadPoolManager() {
    synchronized (this) {
      checkShutdownState();
    } 
    synchronized (this.threadPoolManagerAccessLock) {
      if (this.threadpoolMgr == null) {
        this.threadpoolMgr = new ThreadPoolManagerImpl();
        this.orbOwnsThreadPoolManager = true;
      } 
      return this.threadpoolMgr;
    } 
  }
  
  public CopierManager getCopierManager() {
    synchronized (this) {
      checkShutdownState();
    } 
    return this.copierManager;
  }
  
  public void validateIORClass(String paramString) {
    if (this.iorTypeCheckRegistry != null && !this.iorTypeCheckRegistry.isValidIORType(paramString))
      throw ORBUtilSystemException.get(this, "oa.ior").badStringifiedIor(); 
  }
  
  private static class ConfigParser extends ParserImplBase {
    public Class configurator = ORBConfiguratorImpl.class;
    
    private ConfigParser() {}
    
    public PropertyParser makeParser() {
      PropertyParser propertyParser = new PropertyParser();
      propertyParser.add("com.sun.CORBA.ORBConfigurator", OperationFactory.classAction(), "configurator");
      return propertyParser;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orb\ORBImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */