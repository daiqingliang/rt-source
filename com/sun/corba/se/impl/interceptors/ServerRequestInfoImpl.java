package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.legacy.connection.Connection;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import java.util.ArrayList;
import java.util.HashMap;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.NVList;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.CORBA.TypeCode;
import org.omg.Dynamic.Parameter;
import org.omg.IOP.ServiceContext;
import org.omg.PortableInterceptor.InvalidSlot;
import org.omg.PortableInterceptor.ServerRequestInfo;
import org.omg.PortableServer.Servant;

public final class ServerRequestInfoImpl extends RequestInfoImpl implements ServerRequestInfo {
  static final int CALL_RECEIVE_REQUEST_SERVICE_CONTEXT = 0;
  
  static final int CALL_RECEIVE_REQUEST = 0;
  
  static final int CALL_INTERMEDIATE_NONE = 1;
  
  static final int CALL_SEND_REPLY = 0;
  
  static final int CALL_SEND_EXCEPTION = 1;
  
  static final int CALL_SEND_OTHER = 2;
  
  private boolean forwardRequestRaisedInEnding;
  
  private CorbaMessageMediator request;
  
  private Object servant;
  
  private byte[] objectId;
  
  private ObjectKeyTemplate oktemp;
  
  private byte[] adapterId;
  
  private String[] adapterName;
  
  private ArrayList addReplyServiceContextQueue;
  
  private ReplyMessage replyMessage;
  
  private String targetMostDerivedInterface;
  
  private NVList dsiArguments;
  
  private Any dsiResult;
  
  private Any dsiException;
  
  private boolean isDynamic;
  
  private ObjectAdapter objectAdapter;
  
  private int serverRequestId;
  
  private Parameter[] cachedArguments;
  
  private Any cachedSendingException;
  
  private HashMap cachedRequestServiceContexts;
  
  private HashMap cachedReplyServiceContexts;
  
  protected static final int MID_SENDING_EXCEPTION = 14;
  
  protected static final int MID_OBJECT_ID = 15;
  
  protected static final int MID_ADAPTER_ID = 16;
  
  protected static final int MID_TARGET_MOST_DERIVED_INTERFACE = 17;
  
  protected static final int MID_GET_SERVER_POLICY = 18;
  
  protected static final int MID_SET_SLOT = 19;
  
  protected static final int MID_TARGET_IS_A = 20;
  
  protected static final int MID_ADD_REPLY_SERVICE_CONTEXT = 21;
  
  protected static final int MID_SERVER_ID = 22;
  
  protected static final int MID_ORB_ID = 23;
  
  protected static final int MID_ADAPTER_NAME = 24;
  
  private static final boolean[][] validCall = { 
      { true, true, true, true, true }, { true, true, true, true, true }, { false, true, true, false, false }, { false, true, true, true, true }, { false, true, true, true, true }, { false, true, true, false, false }, { false, false, true, false, false }, { true, true, true, true, true }, { true, true, true, true, true }, { false, false, true, true, true }, 
      { false, false, false, false, true }, { true, true, true, true, true }, { true, true, true, true, true }, { false, false, true, true, true }, { false, false, false, true, false }, { false, true, true, true, true }, { false, true, true, true, true }, { false, true, false, false, false }, { true, true, true, true, true }, { true, true, true, true, true }, 
      { false, true, false, false, false }, { true, true, true, true, true }, { false, true, true, true, true }, { false, true, true, true, true }, { false, true, true, true, true } };
  
  void reset() {
    super.reset();
    this.forwardRequestRaisedInEnding = false;
    this.request = null;
    this.servant = null;
    this.objectId = null;
    this.oktemp = null;
    this.adapterId = null;
    this.adapterName = null;
    this.addReplyServiceContextQueue = null;
    this.replyMessage = null;
    this.targetMostDerivedInterface = null;
    this.dsiArguments = null;
    this.dsiResult = null;
    this.dsiException = null;
    this.isDynamic = false;
    this.objectAdapter = null;
    this.serverRequestId = this.myORB.getPIHandler().allocateServerRequestId();
    this.cachedArguments = null;
    this.cachedSendingException = null;
    this.cachedRequestServiceContexts = null;
    this.cachedReplyServiceContexts = null;
    this.startingPointCall = 0;
    this.intermediatePointCall = 0;
    this.endingPointCall = 0;
  }
  
  ServerRequestInfoImpl(ORB paramORB) {
    super(paramORB);
    this.serverRequestId = paramORB.getPIHandler().allocateServerRequestId();
  }
  
  public Any sending_exception() {
    checkAccess(14);
    if (this.cachedSendingException == null) {
      Any any = null;
      if (this.dsiException != null) {
        any = this.dsiException;
      } else if (this.exception != null) {
        any = exceptionToAny(this.exception);
      } else {
        throw this.wrapper.exceptionUnavailable();
      } 
      this.cachedSendingException = any;
    } 
    return this.cachedSendingException;
  }
  
  public byte[] object_id() {
    checkAccess(15);
    if (this.objectId == null)
      throw this.stdWrapper.piOperationNotSupported6(); 
    return this.objectId;
  }
  
  private void checkForNullTemplate() {
    if (this.oktemp == null)
      throw this.stdWrapper.piOperationNotSupported7(); 
  }
  
  public String server_id() {
    checkAccess(22);
    checkForNullTemplate();
    return Integer.toString(this.oktemp.getServerId());
  }
  
  public String orb_id() {
    checkAccess(23);
    return this.myORB.getORBData().getORBId();
  }
  
  public String[] adapter_name() {
    checkAccess(24);
    if (this.adapterName == null) {
      checkForNullTemplate();
      ObjectAdapterId objectAdapterId = this.oktemp.getObjectAdapterId();
      this.adapterName = objectAdapterId.getAdapterName();
    } 
    return this.adapterName;
  }
  
  public byte[] adapter_id() {
    checkAccess(16);
    if (this.adapterId == null) {
      checkForNullTemplate();
      this.adapterId = this.oktemp.getAdapterId();
    } 
    return this.adapterId;
  }
  
  public String target_most_derived_interface() {
    checkAccess(17);
    return this.targetMostDerivedInterface;
  }
  
  public Policy get_server_policy(int paramInt) {
    Policy policy = null;
    if (this.objectAdapter != null)
      policy = this.objectAdapter.getEffectivePolicy(paramInt); 
    return policy;
  }
  
  public void set_slot(int paramInt, Any paramAny) throws InvalidSlot { this.slotTable.set_slot(paramInt, paramAny); }
  
  public boolean target_is_a(String paramString) {
    checkAccess(20);
    boolean bool = false;
    if (this.servant instanceof Servant) {
      bool = ((Servant)this.servant)._is_a(paramString);
    } else if (StubAdapter.isStub(this.servant)) {
      bool = ((Object)this.servant)._is_a(paramString);
    } else {
      throw this.wrapper.servantInvalid();
    } 
    return bool;
  }
  
  public void add_reply_service_context(ServiceContext paramServiceContext, boolean paramBoolean) {
    if (this.currentExecutionPoint == 2) {
      ServiceContexts serviceContexts = this.replyMessage.getServiceContexts();
      if (serviceContexts == null) {
        serviceContexts = new ServiceContexts(this.myORB);
        this.replyMessage.setServiceContexts(serviceContexts);
      } 
      if (this.cachedReplyServiceContexts == null)
        this.cachedReplyServiceContexts = new HashMap(); 
      addServiceContext(this.cachedReplyServiceContexts, serviceContexts, paramServiceContext, paramBoolean);
    } 
    AddReplyServiceContextCommand addReplyServiceContextCommand = new AddReplyServiceContextCommand(null);
    addReplyServiceContextCommand.service_context = paramServiceContext;
    addReplyServiceContextCommand.replace = paramBoolean;
    if (this.addReplyServiceContextQueue == null)
      this.addReplyServiceContextQueue = new ArrayList(); 
    enqueue(addReplyServiceContextCommand);
  }
  
  public int request_id() { return this.serverRequestId; }
  
  public String operation() { return this.request.getOperationName(); }
  
  public Parameter[] arguments() {
    checkAccess(2);
    if (this.cachedArguments == null) {
      if (!this.isDynamic)
        throw this.stdWrapper.piOperationNotSupported1(); 
      if (this.dsiArguments == null)
        throw this.stdWrapper.piOperationNotSupported8(); 
      this.cachedArguments = nvListToParameterArray(this.dsiArguments);
    } 
    return this.cachedArguments;
  }
  
  public TypeCode[] exceptions() {
    checkAccess(3);
    throw this.stdWrapper.piOperationNotSupported2();
  }
  
  public String[] contexts() {
    checkAccess(4);
    throw this.stdWrapper.piOperationNotSupported3();
  }
  
  public String[] operation_context() {
    checkAccess(5);
    throw this.stdWrapper.piOperationNotSupported4();
  }
  
  public Any result() {
    checkAccess(6);
    if (!this.isDynamic)
      throw this.stdWrapper.piOperationNotSupported5(); 
    if (this.dsiResult == null)
      throw this.wrapper.piDsiResultIsNull(); 
    return this.dsiResult;
  }
  
  public boolean response_expected() { return !this.request.isOneWay(); }
  
  public Object forward_reference() {
    checkAccess(10);
    if (this.replyStatus != 3)
      throw this.stdWrapper.invalidPiCall1(); 
    return (getForwardRequestException()).forward;
  }
  
  public ServiceContext get_request_service_context(int paramInt) {
    checkAccess(12);
    if (this.cachedRequestServiceContexts == null)
      this.cachedRequestServiceContexts = new HashMap(); 
    return getServiceContext(this.cachedRequestServiceContexts, this.request.getRequestServiceContexts(), paramInt);
  }
  
  public ServiceContext get_reply_service_context(int paramInt) {
    checkAccess(13);
    if (this.cachedReplyServiceContexts == null)
      this.cachedReplyServiceContexts = new HashMap(); 
    return getServiceContext(this.cachedReplyServiceContexts, this.replyMessage.getServiceContexts(), paramInt);
  }
  
  private void enqueue(AddReplyServiceContextCommand paramAddReplyServiceContextCommand) {
    int i = this.addReplyServiceContextQueue.size();
    boolean bool = false;
    for (byte b = 0; b < i; b++) {
      AddReplyServiceContextCommand addReplyServiceContextCommand = (AddReplyServiceContextCommand)this.addReplyServiceContextQueue.get(b);
      if (addReplyServiceContextCommand.service_context.context_id == paramAddReplyServiceContextCommand.service_context.context_id) {
        bool = true;
        if (paramAddReplyServiceContextCommand.replace) {
          this.addReplyServiceContextQueue.set(b, paramAddReplyServiceContextCommand);
          break;
        } 
        throw this.stdWrapper.serviceContextAddFailed(new Integer(addReplyServiceContextCommand.service_context.context_id));
      } 
    } 
    if (!bool)
      this.addReplyServiceContextQueue.add(paramAddReplyServiceContextCommand); 
  }
  
  protected void setCurrentExecutionPoint(int paramInt) {
    super.setCurrentExecutionPoint(paramInt);
    if (paramInt == 2 && this.addReplyServiceContextQueue != null) {
      int i = this.addReplyServiceContextQueue.size();
      for (byte b = 0; b < i; b++) {
        AddReplyServiceContextCommand addReplyServiceContextCommand = (AddReplyServiceContextCommand)this.addReplyServiceContextQueue.get(b);
        try {
          add_reply_service_context(addReplyServiceContextCommand.service_context, addReplyServiceContextCommand.replace);
        } catch (BAD_INV_ORDER bAD_INV_ORDER) {}
      } 
    } 
  }
  
  protected void setInfo(CorbaMessageMediator paramCorbaMessageMediator, ObjectAdapter paramObjectAdapter, byte[] paramArrayOfByte, ObjectKeyTemplate paramObjectKeyTemplate) {
    this.request = paramCorbaMessageMediator;
    this.objectId = paramArrayOfByte;
    this.oktemp = paramObjectKeyTemplate;
    this.objectAdapter = paramObjectAdapter;
    this.connection = (Connection)paramCorbaMessageMediator.getConnection();
  }
  
  protected void setDSIArguments(NVList paramNVList) { this.dsiArguments = paramNVList; }
  
  protected void setDSIException(Any paramAny) {
    this.dsiException = paramAny;
    this.cachedSendingException = null;
  }
  
  protected void setDSIResult(Any paramAny) { this.dsiResult = paramAny; }
  
  protected void setException(Exception paramException) {
    super.setException(paramException);
    this.dsiException = null;
    this.cachedSendingException = null;
  }
  
  protected void setInfo(Object paramObject, String paramString) {
    this.servant = paramObject;
    this.targetMostDerivedInterface = paramString;
    this.isDynamic = (paramObject instanceof org.omg.PortableServer.DynamicImplementation || paramObject instanceof org.omg.CORBA.DynamicImplementation);
  }
  
  void setReplyMessage(ReplyMessage paramReplyMessage) { this.replyMessage = paramReplyMessage; }
  
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
  
  void releaseServant() { this.servant = null; }
  
  void setForwardRequestRaisedInEnding() { this.forwardRequestRaisedInEnding = true; }
  
  boolean isForwardRequestRaisedInEnding() { return this.forwardRequestRaisedInEnding; }
  
  boolean isDynamic() { return this.isDynamic; }
  
  protected void checkAccess(int paramInt) {
    byte b = 0;
    switch (this.currentExecutionPoint) {
      case 0:
        b = 0;
        break;
      case 1:
        b = 1;
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
  
  private class AddReplyServiceContextCommand {
    ServiceContext service_context;
    
    boolean replace;
    
    private AddReplyServiceContextCommand() {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\interceptors\ServerRequestInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */