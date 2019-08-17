package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.corba.RequestImpl;
import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orbutil.closure.ClosureFactory;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.ForwardException;
import com.sun.corba.se.spi.protocol.PIHandler;
import com.sun.corba.se.spi.protocol.RetryType;
import java.util.HashMap;
import java.util.Stack;
import org.omg.CORBA.Any;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.NVList;
import org.omg.CORBA.Policy;
import org.omg.CORBA.PolicyError;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.IOP.CodecFactory;
import org.omg.PortableInterceptor.Current;
import org.omg.PortableInterceptor.Interceptor;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.omg.PortableInterceptor.ORBInitializer;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;
import org.omg.PortableInterceptor.PolicyFactory;

public class PIHandlerImpl implements PIHandler {
  boolean printPushPopEnabled = false;
  
  int pushLevel = 0;
  
  private ORB orb;
  
  InterceptorsSystemException wrapper;
  
  ORBUtilSystemException orbutilWrapper;
  
  OMGSystemException omgWrapper;
  
  private int serverRequestIdCounter = 0;
  
  CodecFactory codecFactory = null;
  
  String[] arguments = null;
  
  private InterceptorList interceptorList;
  
  private boolean hasIORInterceptors;
  
  private boolean hasClientInterceptors;
  
  private boolean hasServerInterceptors;
  
  private InterceptorInvoker interceptorInvoker;
  
  private PICurrent current;
  
  private HashMap policyFactoryTable;
  
  private static final short[] REPLY_MESSAGE_TO_PI_REPLY_STATUS = { 0, 2, 1, 3, 3, 4 };
  
  private ThreadLocal threadLocalClientRequestInfoStack = new ThreadLocal() {
      protected Object initialValue() { return new PIHandlerImpl.RequestInfoStack(PIHandlerImpl.this, null); }
    };
  
  private ThreadLocal threadLocalServerRequestInfoStack = new ThreadLocal() {
      protected Object initialValue() { return new PIHandlerImpl.RequestInfoStack(PIHandlerImpl.this, null); }
    };
  
  private void printPush() {
    if (!this.printPushPopEnabled)
      return; 
    printSpaces(this.pushLevel);
    this.pushLevel++;
    System.out.println("PUSH");
  }
  
  private void printPop() {
    if (!this.printPushPopEnabled)
      return; 
    this.pushLevel--;
    printSpaces(this.pushLevel);
    System.out.println("POP");
  }
  
  private void printSpaces(int paramInt) {
    for (byte b = 0; b < paramInt; b++)
      System.out.print(" "); 
  }
  
  public void close() {
    this.orb = null;
    this.wrapper = null;
    this.orbutilWrapper = null;
    this.omgWrapper = null;
    this.codecFactory = null;
    this.arguments = null;
    this.interceptorList = null;
    this.interceptorInvoker = null;
    this.current = null;
    this.policyFactoryTable = null;
    this.threadLocalClientRequestInfoStack = null;
    this.threadLocalServerRequestInfoStack = null;
  }
  
  public PIHandlerImpl(ORB paramORB, String[] paramArrayOfString) {
    this.orb = paramORB;
    this.wrapper = InterceptorsSystemException.get(paramORB, "rpc.protocol");
    this.orbutilWrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
    this.omgWrapper = OMGSystemException.get(paramORB, "rpc.protocol");
    this.arguments = paramArrayOfString;
    this.codecFactory = new CodecFactoryImpl(paramORB);
    this.interceptorList = new InterceptorList(this.wrapper);
    this.current = new PICurrent(paramORB);
    this.interceptorInvoker = new InterceptorInvoker(paramORB, this.interceptorList, this.current);
    paramORB.getLocalResolver().register("PICurrent", ClosureFactory.makeConstant(this.current));
    paramORB.getLocalResolver().register("CodecFactory", ClosureFactory.makeConstant(this.codecFactory));
  }
  
  public void initialize() {
    if (this.orb.getORBData().getORBInitializers() != null) {
      ORBInitInfoImpl oRBInitInfoImpl = createORBInitInfo();
      this.current.setORBInitializing(true);
      preInitORBInitializers(oRBInitInfoImpl);
      postInitORBInitializers(oRBInitInfoImpl);
      this.interceptorList.sortInterceptors();
      this.current.setORBInitializing(false);
      oRBInitInfoImpl.setStage(2);
      this.hasIORInterceptors = this.interceptorList.hasInterceptorsOfType(2);
      this.hasClientInterceptors = true;
      this.hasServerInterceptors = this.interceptorList.hasInterceptorsOfType(1);
      this.interceptorInvoker.setEnabled(true);
    } 
  }
  
  public void destroyInterceptors() { this.interceptorList.destroyAll(); }
  
  public void objectAdapterCreated(ObjectAdapter paramObjectAdapter) {
    if (!this.hasIORInterceptors)
      return; 
    this.interceptorInvoker.objectAdapterCreated(paramObjectAdapter);
  }
  
  public void adapterManagerStateChanged(int paramInt, short paramShort) {
    if (!this.hasIORInterceptors)
      return; 
    this.interceptorInvoker.adapterManagerStateChanged(paramInt, paramShort);
  }
  
  public void adapterStateChanged(ObjectReferenceTemplate[] paramArrayOfObjectReferenceTemplate, short paramShort) {
    if (!this.hasIORInterceptors)
      return; 
    this.interceptorInvoker.adapterStateChanged(paramArrayOfObjectReferenceTemplate, paramShort);
  }
  
  public void disableInterceptorsThisThread() {
    if (!this.hasClientInterceptors)
      return; 
    RequestInfoStack requestInfoStack = (RequestInfoStack)this.threadLocalClientRequestInfoStack.get();
    requestInfoStack.disableCount++;
  }
  
  public void enableInterceptorsThisThread() {
    if (!this.hasClientInterceptors)
      return; 
    RequestInfoStack requestInfoStack = (RequestInfoStack)this.threadLocalClientRequestInfoStack.get();
    requestInfoStack.disableCount--;
  }
  
  public void invokeClientPIStartingPoint() { // Byte code:
    //   0: aload_0
    //   1: getfield hasClientInterceptors : Z
    //   4: ifne -> 8
    //   7: return
    //   8: aload_0
    //   9: invokespecial isClientPIEnabledForThisThread : ()Z
    //   12: ifne -> 16
    //   15: return
    //   16: aload_0
    //   17: invokespecial peekClientRequestInfoImplStack : ()Lcom/sun/corba/se/impl/interceptors/ClientRequestInfoImpl;
    //   20: astore_1
    //   21: aload_0
    //   22: getfield interceptorInvoker : Lcom/sun/corba/se/impl/interceptors/InterceptorInvoker;
    //   25: aload_1
    //   26: invokevirtual invokeClientInterceptorStartingPoint : (Lcom/sun/corba/se/impl/interceptors/ClientRequestInfoImpl;)V
    //   29: aload_1
    //   30: invokevirtual getReplyStatus : ()S
    //   33: istore_2
    //   34: iload_2
    //   35: iconst_1
    //   36: if_icmpeq -> 44
    //   39: iload_2
    //   40: iconst_3
    //   41: if_icmpne -> 111
    //   44: aload_0
    //   45: aload_0
    //   46: iload_2
    //   47: invokespecial convertPIReplyStatusToReplyMessage : (S)I
    //   50: aload_1
    //   51: invokevirtual getException : ()Ljava/lang/Exception;
    //   54: invokevirtual invokeClientPIEndingPoint : (ILjava/lang/Exception;)Ljava/lang/Exception;
    //   57: astore_3
    //   58: aload_3
    //   59: ifnonnull -> 62
    //   62: aload_3
    //   63: instanceof org/omg/CORBA/SystemException
    //   66: ifeq -> 74
    //   69: aload_3
    //   70: checkcast org/omg/CORBA/SystemException
    //   73: athrow
    //   74: aload_3
    //   75: instanceof org/omg/CORBA/portable/RemarshalException
    //   78: ifeq -> 86
    //   81: aload_3
    //   82: checkcast org/omg/CORBA/portable/RemarshalException
    //   85: athrow
    //   86: aload_3
    //   87: instanceof org/omg/CORBA/UserException
    //   90: ifne -> 100
    //   93: aload_3
    //   94: instanceof org/omg/CORBA/portable/ApplicationException
    //   97: ifeq -> 108
    //   100: aload_0
    //   101: getfield wrapper : Lcom/sun/corba/se/impl/logging/InterceptorsSystemException;
    //   104: invokevirtual exceptionInvalid : ()Lorg/omg/CORBA/INTERNAL;
    //   107: athrow
    //   108: goto -> 124
    //   111: iload_2
    //   112: iconst_m1
    //   113: if_icmpeq -> 124
    //   116: aload_0
    //   117: getfield wrapper : Lcom/sun/corba/se/impl/logging/InterceptorsSystemException;
    //   120: invokevirtual replyStatusNotInit : ()Lorg/omg/CORBA/INTERNAL;
    //   123: athrow
    //   124: return }
  
  public Exception makeCompletedClientRequest(int paramInt, Exception paramException) { return handleClientPIEndingPoint(paramInt, paramException, false); }
  
  public Exception invokeClientPIEndingPoint(int paramInt, Exception paramException) { return handleClientPIEndingPoint(paramInt, paramException, true); }
  
  public Exception handleClientPIEndingPoint(int paramInt, Exception paramException, boolean paramBoolean) {
    if (!this.hasClientInterceptors)
      return paramException; 
    if (!isClientPIEnabledForThisThread())
      return paramException; 
    short s = REPLY_MESSAGE_TO_PI_REPLY_STATUS[paramInt];
    ClientRequestInfoImpl clientRequestInfoImpl = peekClientRequestInfoImplStack();
    clientRequestInfoImpl.setReplyStatus(s);
    clientRequestInfoImpl.setException(paramException);
    if (paramBoolean) {
      this.interceptorInvoker.invokeClientInterceptorEndingPoint(clientRequestInfoImpl);
      s = clientRequestInfoImpl.getReplyStatus();
    } 
    if (s == 3 || s == 4) {
      clientRequestInfoImpl.reset();
      if (paramBoolean) {
        clientRequestInfoImpl.setRetryRequest(RetryType.AFTER_RESPONSE);
      } else {
        clientRequestInfoImpl.setRetryRequest(RetryType.BEFORE_RESPONSE);
      } 
      paramException = new RemarshalException();
    } else if (s == 1 || s == 2) {
      paramException = clientRequestInfoImpl.getException();
    } 
    return paramException;
  }
  
  public void initiateClientPIRequest(boolean paramBoolean) {
    if (!this.hasClientInterceptors)
      return; 
    if (!isClientPIEnabledForThisThread())
      return; 
    RequestInfoStack requestInfoStack = (RequestInfoStack)this.threadLocalClientRequestInfoStack.get();
    ClientRequestInfoImpl clientRequestInfoImpl = null;
    if (!requestInfoStack.empty())
      clientRequestInfoImpl = (ClientRequestInfoImpl)requestInfoStack.peek(); 
    if (!paramBoolean && clientRequestInfoImpl != null && clientRequestInfoImpl.isDIIInitiate()) {
      clientRequestInfoImpl.setDIIInitiate(false);
    } else {
      if (clientRequestInfoImpl == null || !clientRequestInfoImpl.getRetryRequest().isRetry()) {
        clientRequestInfoImpl = new ClientRequestInfoImpl(this.orb);
        requestInfoStack.push(clientRequestInfoImpl);
        printPush();
      } 
      clientRequestInfoImpl.setRetryRequest(RetryType.NONE);
      clientRequestInfoImpl.incrementEntryCount();
      clientRequestInfoImpl.setReplyStatus((short)-1);
      if (paramBoolean)
        clientRequestInfoImpl.setDIIInitiate(true); 
    } 
  }
  
  public void cleanupClientPIRequest() {
    if (!this.hasClientInterceptors)
      return; 
    if (!isClientPIEnabledForThisThread())
      return; 
    ClientRequestInfoImpl clientRequestInfoImpl = peekClientRequestInfoImplStack();
    RetryType retryType = clientRequestInfoImpl.getRetryRequest();
    short s = clientRequestInfoImpl.getReplyStatus();
    clientRequestInfoImpl;
    if (!retryType.equals(RetryType.BEFORE_RESPONSE) && s == -1)
      invokeClientPIEndingPoint(2, this.wrapper.unknownRequestInvoke(CompletionStatus.COMPLETED_MAYBE)); 
    clientRequestInfoImpl.decrementEntryCount();
    if (clientRequestInfoImpl.getEntryCount() == 0 && !clientRequestInfoImpl.getRetryRequest().isRetry()) {
      RequestInfoStack requestInfoStack = (RequestInfoStack)this.threadLocalClientRequestInfoStack.get();
      requestInfoStack.pop();
      printPop();
    } 
  }
  
  public void setClientPIInfo(CorbaMessageMediator paramCorbaMessageMediator) {
    if (!this.hasClientInterceptors)
      return; 
    if (!isClientPIEnabledForThisThread())
      return; 
    peekClientRequestInfoImplStack().setInfo(paramCorbaMessageMediator);
  }
  
  public void setClientPIInfo(RequestImpl paramRequestImpl) {
    if (!this.hasClientInterceptors)
      return; 
    if (!isClientPIEnabledForThisThread())
      return; 
    peekClientRequestInfoImplStack().setDIIRequest(paramRequestImpl);
  }
  
  public void invokeServerPIStartingPoint() {
    if (!this.hasServerInterceptors)
      return; 
    ServerRequestInfoImpl serverRequestInfoImpl = peekServerRequestInfoImplStack();
    this.interceptorInvoker.invokeServerInterceptorStartingPoint(serverRequestInfoImpl);
    serverPIHandleExceptions(serverRequestInfoImpl);
  }
  
  public void invokeServerPIIntermediatePoint() {
    if (!this.hasServerInterceptors)
      return; 
    ServerRequestInfoImpl serverRequestInfoImpl = peekServerRequestInfoImplStack();
    this.interceptorInvoker.invokeServerInterceptorIntermediatePoint(serverRequestInfoImpl);
    serverRequestInfoImpl.releaseServant();
    serverPIHandleExceptions(serverRequestInfoImpl);
  }
  
  public void invokeServerPIEndingPoint(ReplyMessage paramReplyMessage) {
    if (!this.hasServerInterceptors)
      return; 
    ServerRequestInfoImpl serverRequestInfoImpl = peekServerRequestInfoImplStack();
    serverRequestInfoImpl.setReplyMessage(paramReplyMessage);
    serverRequestInfoImpl;
    serverRequestInfoImpl.setCurrentExecutionPoint(2);
    if (!serverRequestInfoImpl.getAlreadyExecuted()) {
      int i = paramReplyMessage.getReplyStatus();
      short s1 = REPLY_MESSAGE_TO_PI_REPLY_STATUS[i];
      if (s1 == 3 || s1 == 4)
        serverRequestInfoImpl.setForwardRequest(paramReplyMessage.getIOR()); 
      Exception exception1 = serverRequestInfoImpl.getException();
      if (!serverRequestInfoImpl.isDynamic() && s1 == 2)
        serverRequestInfoImpl.setException(this.omgWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE)); 
      serverRequestInfoImpl.setReplyStatus(s1);
      this.interceptorInvoker.invokeServerInterceptorEndingPoint(serverRequestInfoImpl);
      short s2 = serverRequestInfoImpl.getReplyStatus();
      Exception exception2 = serverRequestInfoImpl.getException();
      if (s2 == 1 && exception2 != exception1)
        throw (SystemException)exception2; 
      if (s2 == 3) {
        if (s1 != 3) {
          IOR iOR = serverRequestInfoImpl.getForwardRequestIOR();
          throw new ForwardException(this.orb, iOR);
        } 
        if (serverRequestInfoImpl.isForwardRequestRaisedInEnding())
          paramReplyMessage.setIOR(serverRequestInfoImpl.getForwardRequestIOR()); 
      } 
    } 
  }
  
  public void setServerPIInfo(Exception paramException) {
    if (!this.hasServerInterceptors)
      return; 
    ServerRequestInfoImpl serverRequestInfoImpl = peekServerRequestInfoImplStack();
    serverRequestInfoImpl.setException(paramException);
  }
  
  public void setServerPIInfo(NVList paramNVList) {
    if (!this.hasServerInterceptors)
      return; 
    ServerRequestInfoImpl serverRequestInfoImpl = peekServerRequestInfoImplStack();
    serverRequestInfoImpl.setDSIArguments(paramNVList);
  }
  
  public void setServerPIExceptionInfo(Any paramAny) {
    if (!this.hasServerInterceptors)
      return; 
    ServerRequestInfoImpl serverRequestInfoImpl = peekServerRequestInfoImplStack();
    serverRequestInfoImpl.setDSIException(paramAny);
  }
  
  public void setServerPIInfo(Any paramAny) {
    if (!this.hasServerInterceptors)
      return; 
    ServerRequestInfoImpl serverRequestInfoImpl = peekServerRequestInfoImplStack();
    serverRequestInfoImpl.setDSIResult(paramAny);
  }
  
  public void initializeServerPIInfo(CorbaMessageMediator paramCorbaMessageMediator, ObjectAdapter paramObjectAdapter, byte[] paramArrayOfByte, ObjectKeyTemplate paramObjectKeyTemplate) {
    if (!this.hasServerInterceptors)
      return; 
    RequestInfoStack requestInfoStack = (RequestInfoStack)this.threadLocalServerRequestInfoStack.get();
    ServerRequestInfoImpl serverRequestInfoImpl = new ServerRequestInfoImpl(this.orb);
    requestInfoStack.push(serverRequestInfoImpl);
    printPush();
    paramCorbaMessageMediator.setExecutePIInResponseConstructor(true);
    serverRequestInfoImpl.setInfo(paramCorbaMessageMediator, paramObjectAdapter, paramArrayOfByte, paramObjectKeyTemplate);
  }
  
  public void setServerPIInfo(Object paramObject, String paramString) {
    if (!this.hasServerInterceptors)
      return; 
    ServerRequestInfoImpl serverRequestInfoImpl = peekServerRequestInfoImplStack();
    serverRequestInfoImpl.setInfo(paramObject, paramString);
  }
  
  public void cleanupServerPIRequest() {
    if (!this.hasServerInterceptors)
      return; 
    RequestInfoStack requestInfoStack = (RequestInfoStack)this.threadLocalServerRequestInfoStack.get();
    requestInfoStack.pop();
    printPop();
  }
  
  private void serverPIHandleExceptions(ServerRequestInfoImpl paramServerRequestInfoImpl) {
    int i = paramServerRequestInfoImpl.getEndingPointCall();
    if (i == 1)
      throw (SystemException)paramServerRequestInfoImpl.getException(); 
    if (i == 2 && paramServerRequestInfoImpl.getForwardRequestException() != null) {
      IOR iOR = paramServerRequestInfoImpl.getForwardRequestIOR();
      throw new ForwardException(this.orb, iOR);
    } 
  }
  
  private int convertPIReplyStatusToReplyMessage(short paramShort) {
    byte b1 = 0;
    for (byte b2 = 0; b2 < REPLY_MESSAGE_TO_PI_REPLY_STATUS.length; b2++) {
      if (REPLY_MESSAGE_TO_PI_REPLY_STATUS[b2] == paramShort) {
        b1 = b2;
        break;
      } 
    } 
    return b1;
  }
  
  private ClientRequestInfoImpl peekClientRequestInfoImplStack() {
    RequestInfoStack requestInfoStack = (RequestInfoStack)this.threadLocalClientRequestInfoStack.get();
    ClientRequestInfoImpl clientRequestInfoImpl = null;
    if (!requestInfoStack.empty()) {
      clientRequestInfoImpl = (ClientRequestInfoImpl)requestInfoStack.peek();
    } else {
      throw this.wrapper.clientInfoStackNull();
    } 
    return clientRequestInfoImpl;
  }
  
  private ServerRequestInfoImpl peekServerRequestInfoImplStack() {
    RequestInfoStack requestInfoStack = (RequestInfoStack)this.threadLocalServerRequestInfoStack.get();
    ServerRequestInfoImpl serverRequestInfoImpl = null;
    if (!requestInfoStack.empty()) {
      serverRequestInfoImpl = (ServerRequestInfoImpl)requestInfoStack.peek();
    } else {
      throw this.wrapper.serverInfoStackNull();
    } 
    return serverRequestInfoImpl;
  }
  
  private boolean isClientPIEnabledForThisThread() {
    RequestInfoStack requestInfoStack = (RequestInfoStack)this.threadLocalClientRequestInfoStack.get();
    return (requestInfoStack.disableCount == 0);
  }
  
  private void preInitORBInitializers(ORBInitInfoImpl paramORBInitInfoImpl) {
    paramORBInitInfoImpl.setStage(0);
    for (byte b = 0; b < this.orb.getORBData().getORBInitializers().length; b++) {
      ORBInitializer oRBInitializer = this.orb.getORBData().getORBInitializers()[b];
      if (oRBInitializer != null)
        try {
          oRBInitializer.pre_init(paramORBInitInfoImpl);
        } catch (Exception exception) {} 
    } 
  }
  
  private void postInitORBInitializers(ORBInitInfoImpl paramORBInitInfoImpl) {
    paramORBInitInfoImpl.setStage(1);
    for (byte b = 0; b < this.orb.getORBData().getORBInitializers().length; b++) {
      ORBInitializer oRBInitializer = this.orb.getORBData().getORBInitializers()[b];
      if (oRBInitializer != null)
        try {
          oRBInitializer.post_init(paramORBInitInfoImpl);
        } catch (Exception exception) {} 
    } 
  }
  
  private ORBInitInfoImpl createORBInitInfo() {
    null = null;
    String str = this.orb.getORBData().getORBId();
    return new ORBInitInfoImpl(this.orb, this.arguments, str, this.codecFactory);
  }
  
  public void register_interceptor(Interceptor paramInterceptor, int paramInt) throws DuplicateName {
    if (paramInt >= 3 || paramInt < 0)
      throw this.wrapper.typeOutOfRange(new Integer(paramInt)); 
    String str = paramInterceptor.name();
    if (str == null)
      throw this.wrapper.nameNull(); 
    this.interceptorList.register_interceptor(paramInterceptor, paramInt);
  }
  
  public Current getPICurrent() { return this.current; }
  
  private void nullParam() { throw this.orbutilWrapper.nullParam(); }
  
  public Policy create_policy(int paramInt, Any paramAny) throws PolicyError {
    if (paramAny == null)
      nullParam(); 
    if (this.policyFactoryTable == null)
      throw new PolicyError("There is no PolicyFactory Registered for type " + paramInt, (short)0); 
    PolicyFactory policyFactory = (PolicyFactory)this.policyFactoryTable.get(new Integer(paramInt));
    if (policyFactory == null)
      throw new PolicyError(" Could Not Find PolicyFactory for the Type " + paramInt, (short)0); 
    return policyFactory.create_policy(paramInt, paramAny);
  }
  
  public void registerPolicyFactory(int paramInt, PolicyFactory paramPolicyFactory) {
    if (this.policyFactoryTable == null)
      this.policyFactoryTable = new HashMap(); 
    Integer integer = new Integer(paramInt);
    Object object = this.policyFactoryTable.get(integer);
    if (object == null) {
      this.policyFactoryTable.put(integer, paramPolicyFactory);
    } else {
      throw this.omgWrapper.policyFactoryRegFailed(new Integer(paramInt));
    } 
  }
  
  public int allocateServerRequestId() { return this.serverRequestIdCounter++; }
  
  private final class RequestInfoStack extends Stack {
    public int disableCount = 0;
    
    private RequestInfoStack() {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\interceptors\PIHandlerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */