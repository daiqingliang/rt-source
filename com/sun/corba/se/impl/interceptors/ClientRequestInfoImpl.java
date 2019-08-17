package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.CorbaInvocationInfo;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.legacy.connection.Connection;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.RetryType;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.transport.CorbaContactInfoListIterator;
import java.util.HashMap;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.CORBA.Request;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.Dynamic.Parameter;
import org.omg.IOP.ServiceContext;
import org.omg.IOP.TaggedComponent;
import org.omg.IOP.TaggedProfile;
import org.omg.PortableInterceptor.ClientRequestInfo;

public final class ClientRequestInfoImpl extends RequestInfoImpl implements ClientRequestInfo {
  static final int CALL_SEND_REQUEST = 0;
  
  static final int CALL_SEND_POLL = 1;
  
  static final int CALL_RECEIVE_REPLY = 0;
  
  static final int CALL_RECEIVE_EXCEPTION = 1;
  
  static final int CALL_RECEIVE_OTHER = 2;
  
  private RetryType retryRequest;
  
  private int entryCount = 0;
  
  private Request request;
  
  private boolean diiInitiate;
  
  private CorbaMessageMediator messageMediator;
  
  private Object cachedTargetObject;
  
  private Object cachedEffectiveTargetObject;
  
  private Parameter[] cachedArguments;
  
  private TypeCode[] cachedExceptions;
  
  private String[] cachedContexts;
  
  private String[] cachedOperationContext;
  
  private String cachedReceivedExceptionId;
  
  private Any cachedResult;
  
  private Any cachedReceivedException;
  
  private TaggedProfile cachedEffectiveProfile;
  
  private HashMap cachedRequestServiceContexts;
  
  private HashMap cachedReplyServiceContexts;
  
  private HashMap cachedEffectiveComponents;
  
  protected boolean piCurrentPushed;
  
  protected static final int MID_TARGET = 14;
  
  protected static final int MID_EFFECTIVE_TARGET = 15;
  
  protected static final int MID_EFFECTIVE_PROFILE = 16;
  
  protected static final int MID_RECEIVED_EXCEPTION = 17;
  
  protected static final int MID_RECEIVED_EXCEPTION_ID = 18;
  
  protected static final int MID_GET_EFFECTIVE_COMPONENT = 19;
  
  protected static final int MID_GET_EFFECTIVE_COMPONENTS = 20;
  
  protected static final int MID_GET_REQUEST_POLICY = 21;
  
  protected static final int MID_ADD_REQUEST_SERVICE_CONTEXT = 22;
  
  private static final boolean[][] validCall = { 
      { true, true, true, true, true }, { true, true, true, true, true }, { true, false, true, false, false }, { true, false, true, true, true }, { true, false, true, true, true }, { true, false, true, true, true }, { false, false, true, false, false }, { true, true, true, true, true }, { true, false, true, true, true }, { false, false, true, true, true }, 
      { false, false, false, false, true }, { true, true, true, true, true }, { true, false, true, true, true }, { false, false, true, true, true }, { true, true, true, true, true }, { true, true, true, true, true }, { true, true, true, true, true }, { false, false, false, true, false }, { false, false, false, true, false }, { true, false, true, true, true }, 
      { true, false, true, true, true }, { true, false, true, true, true }, { true, false, false, false, false } };
  
  void reset() {
    super.reset();
    this.retryRequest = RetryType.NONE;
    this.request = null;
    this.diiInitiate = false;
    this.messageMediator = null;
    this.cachedTargetObject = null;
    this.cachedEffectiveTargetObject = null;
    this.cachedArguments = null;
    this.cachedExceptions = null;
    this.cachedContexts = null;
    this.cachedOperationContext = null;
    this.cachedReceivedExceptionId = null;
    this.cachedResult = null;
    this.cachedReceivedException = null;
    this.cachedEffectiveProfile = null;
    this.cachedRequestServiceContexts = null;
    this.cachedReplyServiceContexts = null;
    this.cachedEffectiveComponents = null;
    this.piCurrentPushed = false;
    this.startingPointCall = 0;
    this.endingPointCall = 0;
  }
  
  protected ClientRequestInfoImpl(ORB paramORB) { super(paramORB); }
  
  public Object target() {
    if (this.cachedTargetObject == null) {
      CorbaContactInfo corbaContactInfo = (CorbaContactInfo)this.messageMediator.getContactInfo();
      this.cachedTargetObject = iorToObject(corbaContactInfo.getTargetIOR());
    } 
    return this.cachedTargetObject;
  }
  
  public Object effective_target() {
    if (this.cachedEffectiveTargetObject == null) {
      CorbaContactInfo corbaContactInfo = (CorbaContactInfo)this.messageMediator.getContactInfo();
      this.cachedEffectiveTargetObject = iorToObject(corbaContactInfo.getEffectiveTargetIOR());
    } 
    return this.cachedEffectiveTargetObject;
  }
  
  public TaggedProfile effective_profile() {
    if (this.cachedEffectiveProfile == null) {
      CorbaContactInfo corbaContactInfo = (CorbaContactInfo)this.messageMediator.getContactInfo();
      this.cachedEffectiveProfile = corbaContactInfo.getEffectiveProfile().getIOPProfile();
    } 
    return this.cachedEffectiveProfile;
  }
  
  public Any received_exception() {
    checkAccess(17);
    if (this.cachedReceivedException == null)
      this.cachedReceivedException = exceptionToAny(this.exception); 
    return this.cachedReceivedException;
  }
  
  public String received_exception_id() {
    checkAccess(18);
    if (this.cachedReceivedExceptionId == null) {
      String str = null;
      if (this.exception == null)
        throw this.wrapper.exceptionWasNull(); 
      if (this.exception instanceof org.omg.CORBA.SystemException) {
        String str1 = this.exception.getClass().getName();
        str = ORBUtility.repositoryIdOf(str1);
      } else if (this.exception instanceof ApplicationException) {
        str = ((ApplicationException)this.exception).getId();
      } 
      this.cachedReceivedExceptionId = str;
    } 
    return this.cachedReceivedExceptionId;
  }
  
  public TaggedComponent get_effective_component(int paramInt) {
    checkAccess(19);
    return get_effective_components(paramInt)[0];
  }
  
  public TaggedComponent[] get_effective_components(int paramInt) {
    checkAccess(20);
    Integer integer = new Integer(paramInt);
    TaggedComponent[] arrayOfTaggedComponent = null;
    boolean bool = false;
    if (this.cachedEffectiveComponents == null) {
      this.cachedEffectiveComponents = new HashMap();
      bool = true;
    } else {
      arrayOfTaggedComponent = (TaggedComponent[])this.cachedEffectiveComponents.get(integer);
    } 
    if (arrayOfTaggedComponent == null && (bool || !this.cachedEffectiveComponents.containsKey(integer))) {
      CorbaContactInfo corbaContactInfo = (CorbaContactInfo)this.messageMediator.getContactInfo();
      IIOPProfileTemplate iIOPProfileTemplate = (IIOPProfileTemplate)corbaContactInfo.getEffectiveProfile().getTaggedProfileTemplate();
      arrayOfTaggedComponent = iIOPProfileTemplate.getIOPComponents(this.myORB, paramInt);
      this.cachedEffectiveComponents.put(integer, arrayOfTaggedComponent);
    } 
    if (arrayOfTaggedComponent == null || arrayOfTaggedComponent.length == 0)
      throw this.stdWrapper.invalidComponentId(integer); 
    return arrayOfTaggedComponent;
  }
  
  public Policy get_request_policy(int paramInt) {
    checkAccess(21);
    throw this.wrapper.piOrbNotPolicyBased();
  }
  
  public void add_request_service_context(ServiceContext paramServiceContext, boolean paramBoolean) {
    checkAccess(22);
    if (this.cachedRequestServiceContexts == null)
      this.cachedRequestServiceContexts = new HashMap(); 
    addServiceContext(this.cachedRequestServiceContexts, this.messageMediator.getRequestServiceContexts(), paramServiceContext, paramBoolean);
  }
  
  public int request_id() { return this.messageMediator.getRequestId(); }
  
  public String operation() { return this.messageMediator.getOperationName(); }
  
  public Parameter[] arguments() {
    checkAccess(2);
    if (this.cachedArguments == null) {
      if (this.request == null)
        throw this.stdWrapper.piOperationNotSupported1(); 
      this.cachedArguments = nvListToParameterArray(this.request.arguments());
    } 
    return this.cachedArguments;
  }
  
  public TypeCode[] exceptions() {
    checkAccess(3);
    if (this.cachedExceptions == null) {
      if (this.request == null)
        throw this.stdWrapper.piOperationNotSupported2(); 
      ExceptionList exceptionList = this.request.exceptions();
      int i = exceptionList.count();
      TypeCode[] arrayOfTypeCode = new TypeCode[i];
      try {
        for (byte b = 0; b < i; b++)
          arrayOfTypeCode[b] = exceptionList.item(b); 
      } catch (Exception exception) {
        throw this.wrapper.exceptionInExceptions(exception);
      } 
      this.cachedExceptions = arrayOfTypeCode;
    } 
    return this.cachedExceptions;
  }
  
  public String[] contexts() {
    checkAccess(4);
    if (this.cachedContexts == null) {
      if (this.request == null)
        throw this.stdWrapper.piOperationNotSupported3(); 
      ContextList contextList = this.request.contexts();
      int i = contextList.count();
      String[] arrayOfString = new String[i];
      try {
        for (byte b = 0; b < i; b++)
          arrayOfString[b] = contextList.item(b); 
      } catch (Exception exception) {
        throw this.wrapper.exceptionInContexts(exception);
      } 
      this.cachedContexts = arrayOfString;
    } 
    return this.cachedContexts;
  }
  
  public String[] operation_context() {
    checkAccess(5);
    if (this.cachedOperationContext == null) {
      if (this.request == null)
        throw this.stdWrapper.piOperationNotSupported4(); 
      Context context = this.request.ctx();
      NVList nVList = context.get_values("", 15, "*");
      String[] arrayOfString = new String[nVList.count() * 2];
      if (nVList != null && nVList.count() != 0) {
        byte b1 = 0;
        for (byte b2 = 0; b2 < nVList.count(); b2++) {
          NamedValue namedValue;
          try {
            namedValue = nVList.item(b2);
          } catch (Exception exception) {
            return (String[])null;
          } 
          arrayOfString[b1] = namedValue.name();
          arrayOfString[++b1] = namedValue.value().extract_string();
          b1++;
        } 
      } 
      this.cachedOperationContext = arrayOfString;
    } 
    return this.cachedOperationContext;
  }
  
  public Any result() {
    checkAccess(6);
    if (this.cachedResult == null) {
      if (this.request == null)
        throw this.stdWrapper.piOperationNotSupported5(); 
      NamedValue namedValue = this.request.result();
      if (namedValue == null)
        throw this.wrapper.piDiiResultIsNull(); 
      this.cachedResult = namedValue.value();
    } 
    return this.cachedResult;
  }
  
  public boolean response_expected() { return !this.messageMediator.isOneWay(); }
  
  public Object forward_reference() {
    checkAccess(10);
    if (this.replyStatus != 3)
      throw this.stdWrapper.invalidPiCall1(); 
    IOR iOR = getLocatedIOR();
    return iorToObject(iOR);
  }
  
  private IOR getLocatedIOR() {
    CorbaContactInfoList corbaContactInfoList = (CorbaContactInfoList)this.messageMediator.getContactInfo().getContactInfoList();
    return corbaContactInfoList.getEffectiveTargetIOR();
  }
  
  protected void setLocatedIOR(IOR paramIOR) {
    ORB oRB = (ORB)this.messageMediator.getBroker();
    CorbaContactInfoListIterator corbaContactInfoListIterator = (CorbaContactInfoListIterator)((CorbaInvocationInfo)oRB.getInvocationInfo()).getContactInfoListIterator();
    corbaContactInfoListIterator.reportRedirect((CorbaContactInfo)this.messageMediator.getContactInfo(), paramIOR);
  }
  
  public ServiceContext get_request_service_context(int paramInt) {
    checkAccess(12);
    if (this.cachedRequestServiceContexts == null)
      this.cachedRequestServiceContexts = new HashMap(); 
    return getServiceContext(this.cachedRequestServiceContexts, this.messageMediator.getRequestServiceContexts(), paramInt);
  }
  
  public ServiceContext get_reply_service_context(int paramInt) {
    checkAccess(13);
    if (this.cachedReplyServiceContexts == null)
      this.cachedReplyServiceContexts = new HashMap(); 
    try {
      ServiceContexts serviceContexts = this.messageMediator.getReplyServiceContexts();
      if (serviceContexts == null)
        throw new NullPointerException(); 
      return getServiceContext(this.cachedReplyServiceContexts, serviceContexts, paramInt);
    } catch (NullPointerException nullPointerException) {
      throw this.stdWrapper.invalidServiceContextId(nullPointerException);
    } 
  }
  
  public Connection connection() { return (Connection)this.messageMediator.getConnection(); }
  
  protected void setInfo(MessageMediator paramMessageMediator) {
    this.messageMediator = (CorbaMessageMediator)paramMessageMediator;
    this.messageMediator.setDIIInfo(this.request);
  }
  
  void setRetryRequest(RetryType paramRetryType) { this.retryRequest = paramRetryType; }
  
  RetryType getRetryRequest() { return this.retryRequest; }
  
  void incrementEntryCount() { this.entryCount++; }
  
  void decrementEntryCount() { this.entryCount--; }
  
  int getEntryCount() { return this.entryCount; }
  
  protected void setReplyStatus(short paramShort) {
    super.setReplyStatus(paramShort);
    switch (paramShort) {
      case 0:
        this.endingPointCall = 0;
        break;
      case 1:
      case 2:
        this.endingPointCall = 1;
        break;
      case 3:
      case 4:
        this.endingPointCall = 2;
        break;
    } 
  }
  
  protected void setDIIRequest(Request paramRequest) { this.request = paramRequest; }
  
  protected void setDIIInitiate(boolean paramBoolean) { this.diiInitiate = paramBoolean; }
  
  protected boolean isDIIInitiate() { return this.diiInitiate; }
  
  protected void setPICurrentPushed(boolean paramBoolean) { this.piCurrentPushed = paramBoolean; }
  
  protected boolean isPICurrentPushed() { return this.piCurrentPushed; }
  
  protected void setException(Exception paramException) {
    super.setException(paramException);
    this.cachedReceivedException = null;
    this.cachedReceivedExceptionId = null;
  }
  
  protected boolean getIsOneWay() { return !response_expected(); }
  
  protected void checkAccess(int paramInt) throws BAD_INV_ORDER {
    byte b = 0;
    switch (this.currentExecutionPoint) {
      case 0:
        switch (this.startingPointCall) {
          case 0:
            b = 0;
            break;
          case 1:
            b = 1;
            break;
        } 
        break;
      case 2:
        switch (this.endingPointCall) {
          case 0:
            b = 2;
            break;
          case 1:
            b = 3;
            break;
          case 2:
            b = 4;
            break;
        } 
        break;
    } 
    if (!validCall[paramInt][b])
      throw this.stdWrapper.invalidPiCall2(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\interceptors\ClientRequestInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */