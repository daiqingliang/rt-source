package com.sun.corba.se.impl.orb;

import com.sun.corba.se.impl.corba.AnyImpl;
import com.sun.corba.se.impl.corba.ContextListImpl;
import com.sun.corba.se.impl.corba.EnvironmentImpl;
import com.sun.corba.se.impl.corba.ExceptionListImpl;
import com.sun.corba.se.impl.corba.NVListImpl;
import com.sun.corba.se.impl.corba.NamedValueImpl;
import com.sun.corba.se.impl.corba.TypeCodeImpl;
import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.impl.oa.poa.BadServerIdHandler;
import com.sun.corba.se.pept.protocol.ClientInvocationInfo;
import com.sun.corba.se.pept.transport.ConnectionCache;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.Selector;
import com.sun.corba.se.pept.transport.TransportManager;
import com.sun.corba.se.spi.copyobject.CopierManager;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyFactory;
import com.sun.corba.se.spi.ior.TaggedComponentFactoryFinder;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketEndPointInfo;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketManager;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.orbutil.closure.Closure;
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
import java.applet.Applet;
import java.net.URL;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Properties;
import org.omg.CORBA.Any;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.Current;
import org.omg.CORBA.Environment;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NO_IMPLEMENT;
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
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ValueFactory;
import sun.corba.OutputStreamFactory;

public class ORBSingleton extends ORB {
  private ORB fullORB;
  
  private static PresentationManager.StubFactoryFactory staticStubFactoryFactory = PresentationDefaults.getStaticStubFactoryFactory();
  
  public void set_parameters(Properties paramProperties) {}
  
  protected void set_parameters(Applet paramApplet, Properties paramProperties) {}
  
  protected void set_parameters(String[] paramArrayOfString, Properties paramProperties) {}
  
  public OutputStream create_output_stream() { return OutputStreamFactory.newEncapsOutputStream(this); }
  
  public TypeCode create_struct_tc(String paramString1, String paramString2, StructMember[] paramArrayOfStructMember) { return new TypeCodeImpl(this, 15, paramString1, paramString2, paramArrayOfStructMember); }
  
  public TypeCode create_union_tc(String paramString1, String paramString2, TypeCode paramTypeCode, UnionMember[] paramArrayOfUnionMember) { return new TypeCodeImpl(this, 16, paramString1, paramString2, paramTypeCode, paramArrayOfUnionMember); }
  
  public TypeCode create_enum_tc(String paramString1, String paramString2, String[] paramArrayOfString) { return new TypeCodeImpl(this, 17, paramString1, paramString2, paramArrayOfString); }
  
  public TypeCode create_alias_tc(String paramString1, String paramString2, TypeCode paramTypeCode) { return new TypeCodeImpl(this, 21, paramString1, paramString2, paramTypeCode); }
  
  public TypeCode create_exception_tc(String paramString1, String paramString2, StructMember[] paramArrayOfStructMember) { return new TypeCodeImpl(this, 22, paramString1, paramString2, paramArrayOfStructMember); }
  
  public TypeCode create_interface_tc(String paramString1, String paramString2) { return new TypeCodeImpl(this, 14, paramString1, paramString2); }
  
  public TypeCode create_string_tc(int paramInt) { return new TypeCodeImpl(this, 18, paramInt); }
  
  public TypeCode create_wstring_tc(int paramInt) { return new TypeCodeImpl(this, 27, paramInt); }
  
  public TypeCode create_sequence_tc(int paramInt, TypeCode paramTypeCode) { return new TypeCodeImpl(this, 19, paramInt, paramTypeCode); }
  
  public TypeCode create_recursive_sequence_tc(int paramInt1, int paramInt2) { return new TypeCodeImpl(this, 19, paramInt1, paramInt2); }
  
  public TypeCode create_array_tc(int paramInt, TypeCode paramTypeCode) { return new TypeCodeImpl(this, 20, paramInt, paramTypeCode); }
  
  public TypeCode create_native_tc(String paramString1, String paramString2) { return new TypeCodeImpl(this, 31, paramString1, paramString2); }
  
  public TypeCode create_abstract_interface_tc(String paramString1, String paramString2) { return new TypeCodeImpl(this, 32, paramString1, paramString2); }
  
  public TypeCode create_fixed_tc(short paramShort1, short paramShort2) { return new TypeCodeImpl(this, 28, paramShort1, paramShort2); }
  
  public TypeCode create_value_tc(String paramString1, String paramString2, short paramShort, TypeCode paramTypeCode, ValueMember[] paramArrayOfValueMember) { return new TypeCodeImpl(this, 29, paramString1, paramString2, paramShort, paramTypeCode, paramArrayOfValueMember); }
  
  public TypeCode create_recursive_tc(String paramString) { return new TypeCodeImpl(this, paramString); }
  
  public TypeCode create_value_box_tc(String paramString1, String paramString2, TypeCode paramTypeCode) { return new TypeCodeImpl(this, 30, paramString1, paramString2, paramTypeCode); }
  
  public TypeCode get_primitive_tc(TCKind paramTCKind) { return get_primitive_tc(paramTCKind.value()); }
  
  public Any create_any() { return new AnyImpl(this); }
  
  public NVList create_list(int paramInt) { return new NVListImpl(this, paramInt); }
  
  public NVList create_operation_list(Object paramObject) { throw this.wrapper.genericNoImpl(); }
  
  public NamedValue create_named_value(String paramString, Any paramAny, int paramInt) { return new NamedValueImpl(this, paramString, paramAny, paramInt); }
  
  public ExceptionList create_exception_list() { return new ExceptionListImpl(); }
  
  public ContextList create_context_list() { return new ContextListImpl(this); }
  
  public Context get_default_context() { throw this.wrapper.genericNoImpl(); }
  
  public Environment create_environment() { return new EnvironmentImpl(); }
  
  public Current get_current() { throw this.wrapper.genericNoImpl(); }
  
  public String[] list_initial_services() { throw this.wrapper.genericNoImpl(); }
  
  public Object resolve_initial_references(String paramString) throws InvalidName { throw this.wrapper.genericNoImpl(); }
  
  public void register_initial_reference(String paramString, Object paramObject) throws InvalidName { throw this.wrapper.genericNoImpl(); }
  
  public void send_multiple_requests_oneway(Request[] paramArrayOfRequest) { throw new SecurityException("ORBSingleton: access denied"); }
  
  public void send_multiple_requests_deferred(Request[] paramArrayOfRequest) { throw new SecurityException("ORBSingleton: access denied"); }
  
  public boolean poll_next_response() { throw new SecurityException("ORBSingleton: access denied"); }
  
  public Request get_next_response() { throw new SecurityException("ORBSingleton: access denied"); }
  
  public String object_to_string(Object paramObject) { throw new SecurityException("ORBSingleton: access denied"); }
  
  public Object string_to_object(String paramString) throws InvalidName { throw new SecurityException("ORBSingleton: access denied"); }
  
  public Remote string_to_remote(String paramString) throws RemoteException { throw new SecurityException("ORBSingleton: access denied"); }
  
  public void connect(Object paramObject) { throw new SecurityException("ORBSingleton: access denied"); }
  
  public void disconnect(Object paramObject) { throw new SecurityException("ORBSingleton: access denied"); }
  
  public void run() { throw new SecurityException("ORBSingleton: access denied"); }
  
  public void shutdown(boolean paramBoolean) { throw new SecurityException("ORBSingleton: access denied"); }
  
  protected void shutdownServants(boolean paramBoolean) { throw new SecurityException("ORBSingleton: access denied"); }
  
  protected void destroyConnections() { throw new SecurityException("ORBSingleton: access denied"); }
  
  public void destroy() { throw new SecurityException("ORBSingleton: access denied"); }
  
  public boolean work_pending() { throw new SecurityException("ORBSingleton: access denied"); }
  
  public void perform_work() { throw new SecurityException("ORBSingleton: access denied"); }
  
  public ValueFactory register_value_factory(String paramString, ValueFactory paramValueFactory) { throw new SecurityException("ORBSingleton: access denied"); }
  
  public void unregister_value_factory(String paramString) { throw new SecurityException("ORBSingleton: access denied"); }
  
  public ValueFactory lookup_value_factory(String paramString) { throw new SecurityException("ORBSingleton: access denied"); }
  
  public TransportManager getTransportManager() { throw new SecurityException("ORBSingleton: access denied"); }
  
  public CorbaTransportManager getCorbaTransportManager() { throw new SecurityException("ORBSingleton: access denied"); }
  
  public LegacyServerSocketManager getLegacyServerSocketManager() { throw new SecurityException("ORBSingleton: access denied"); }
  
  private ORB getFullORB() {
    if (this.fullORB == null) {
      Properties properties = new Properties();
      this.fullORB = new ORBImpl();
      this.fullORB.set_parameters(properties);
    } 
    return this.fullORB;
  }
  
  public RequestDispatcherRegistry getRequestDispatcherRegistry() { return getFullORB().getRequestDispatcherRegistry(); }
  
  public ServiceContextRegistry getServiceContextRegistry() { throw new SecurityException("ORBSingleton: access denied"); }
  
  public int getTransientServerId() { throw new SecurityException("ORBSingleton: access denied"); }
  
  public int getORBInitialPort() { throw new SecurityException("ORBSingleton: access denied"); }
  
  public String getORBInitialHost() { throw new SecurityException("ORBSingleton: access denied"); }
  
  public String getORBServerHost() { throw new SecurityException("ORBSingleton: access denied"); }
  
  public int getORBServerPort() { throw new SecurityException("ORBSingleton: access denied"); }
  
  public CodeSetComponentInfo getCodeSetComponentInfo() { return new CodeSetComponentInfo(); }
  
  public boolean isLocalHost(String paramString) { return false; }
  
  public boolean isLocalServerId(int paramInt1, int paramInt2) { return false; }
  
  public ORBVersion getORBVersion() { return ORBVersionFactory.getORBVersion(); }
  
  public void setORBVersion(ORBVersion paramORBVersion) { throw new SecurityException("ORBSingleton: access denied"); }
  
  public String getAppletHost() { throw new SecurityException("ORBSingleton: access denied"); }
  
  public URL getAppletCodeBase() { throw new SecurityException("ORBSingleton: access denied"); }
  
  public int getHighWaterMark() { throw new SecurityException("ORBSingleton: access denied"); }
  
  public int getLowWaterMark() { throw new SecurityException("ORBSingleton: access denied"); }
  
  public int getNumberToReclaim() { throw new SecurityException("ORBSingleton: access denied"); }
  
  public int getGIOPFragmentSize() { return 1024; }
  
  public int getGIOPBuffMgrStrategy(GIOPVersion paramGIOPVersion) { return 0; }
  
  public IOR getFVDCodeBaseIOR() { throw new SecurityException("ORBSingleton: access denied"); }
  
  public Policy create_policy(int paramInt, Any paramAny) throws PolicyError { throw new NO_IMPLEMENT(); }
  
  public LegacyServerSocketEndPointInfo getServerEndpoint() { return null; }
  
  public void setPersistentServerId(int paramInt) {}
  
  public TypeCodeImpl getTypeCodeForClass(Class paramClass) { return null; }
  
  public void setTypeCodeForClass(Class paramClass, TypeCodeImpl paramTypeCodeImpl) {}
  
  public boolean alwaysSendCodeSetServiceContext() { return true; }
  
  public boolean isDuringDispatch() { return false; }
  
  public void notifyORB() {}
  
  public PIHandler getPIHandler() { return null; }
  
  public void checkShutdownState() {}
  
  public void startingDispatch() {}
  
  public void finishedDispatch() {}
  
  public void registerInitialReference(String paramString, Closure paramClosure) {}
  
  public ORBData getORBData() { return getFullORB().getORBData(); }
  
  public void setClientDelegateFactory(ClientDelegateFactory paramClientDelegateFactory) {}
  
  public ClientDelegateFactory getClientDelegateFactory() { return getFullORB().getClientDelegateFactory(); }
  
  public void setCorbaContactInfoListFactory(CorbaContactInfoListFactory paramCorbaContactInfoListFactory) {}
  
  public CorbaContactInfoListFactory getCorbaContactInfoListFactory() { return getFullORB().getCorbaContactInfoListFactory(); }
  
  public Operation getURLOperation() { return null; }
  
  public void setINSDelegate(CorbaServerRequestDispatcher paramCorbaServerRequestDispatcher) {}
  
  public TaggedComponentFactoryFinder getTaggedComponentFactoryFinder() { return getFullORB().getTaggedComponentFactoryFinder(); }
  
  public IdentifiableFactoryFinder getTaggedProfileFactoryFinder() { return getFullORB().getTaggedProfileFactoryFinder(); }
  
  public IdentifiableFactoryFinder getTaggedProfileTemplateFactoryFinder() { return getFullORB().getTaggedProfileTemplateFactoryFinder(); }
  
  public ObjectKeyFactory getObjectKeyFactory() { return getFullORB().getObjectKeyFactory(); }
  
  public void setObjectKeyFactory(ObjectKeyFactory paramObjectKeyFactory) { throw new SecurityException("ORBSingleton: access denied"); }
  
  public void handleBadServerId(ObjectKey paramObjectKey) {}
  
  public OAInvocationInfo peekInvocationInfo() { return null; }
  
  public void pushInvocationInfo(OAInvocationInfo paramOAInvocationInfo) {}
  
  public OAInvocationInfo popInvocationInfo() { return null; }
  
  public ClientInvocationInfo createOrIncrementInvocationInfo() { return null; }
  
  public void releaseOrDecrementInvocationInfo() {}
  
  public ClientInvocationInfo getInvocationInfo() { return null; }
  
  public ConnectionCache getConnectionCache(ContactInfo paramContactInfo) { return null; }
  
  public void setResolver(Resolver paramResolver) {}
  
  public Resolver getResolver() { return null; }
  
  public void setLocalResolver(LocalResolver paramLocalResolver) {}
  
  public LocalResolver getLocalResolver() { return null; }
  
  public void setURLOperation(Operation paramOperation) {}
  
  public void setBadServerIdHandler(BadServerIdHandler paramBadServerIdHandler) {}
  
  public void initBadServerIdHandler() {}
  
  public Selector getSelector(int paramInt) { return null; }
  
  public void setThreadPoolManager(ThreadPoolManager paramThreadPoolManager) {}
  
  public ThreadPoolManager getThreadPoolManager() { return null; }
  
  public CopierManager getCopierManager() { return null; }
  
  public void validateIORClass(String paramString) { getFullORB().validateIORClass(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orb\ORBSingleton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */