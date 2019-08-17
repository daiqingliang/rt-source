package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.corba.RequestImpl;
import com.sun.corba.se.impl.encoding.BufferManagerReadStream;
import com.sun.corba.se.impl.encoding.CDRInputObject;
import com.sun.corba.se.impl.encoding.CDROutputObject;
import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.giopmsgheaders.AddressingDispositionHelper;
import com.sun.corba.se.impl.protocol.giopmsgheaders.CancelRequestMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage_1_1;
import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage_1_2;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyMessage_1_0;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyMessage_1_1;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyMessage_1_2;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyOrReplyMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateRequestMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateRequestMessage_1_0;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateRequestMessage_1_1;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateRequestMessage_1_2;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageBase;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageHandler;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage_1_0;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage_1_1;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage_1_2;
import com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage_1_0;
import com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage_1_1;
import com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage_1_2;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.protocol.ProtocolHandler;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.EventHandler;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.ior.iiop.MaxStreamFormatVersionComponent;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.CorbaProtocolHandler;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.protocol.ForwardException;
import com.sun.corba.se.spi.servicecontext.MaxStreamFormatVersionServiceContext;
import com.sun.corba.se.spi.servicecontext.ORBVersionServiceContext;
import com.sun.corba.se.spi.servicecontext.SendingContextServiceContext;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.spi.servicecontext.UEInfoServiceContext;
import com.sun.corba.se.spi.servicecontext.UnknownServiceContext;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.EmptyStackException;
import java.util.Iterator;
import org.omg.CORBA.Any;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.Request;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.UnknownUserException;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.UnknownException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import sun.corba.OutputStreamFactory;

public class CorbaMessageMediatorImpl implements CorbaMessageMediator, CorbaProtocolHandler, MessageHandler {
  protected ORB orb;
  
  protected ORBUtilSystemException wrapper;
  
  protected InterceptorsSystemException interceptorWrapper;
  
  protected CorbaContactInfo contactInfo;
  
  protected CorbaConnection connection;
  
  protected short addrDisposition;
  
  protected CDROutputObject outputObject;
  
  protected CDRInputObject inputObject;
  
  protected Message messageHeader;
  
  protected RequestMessage requestHeader;
  
  protected LocateReplyOrReplyMessage replyHeader;
  
  protected String replyExceptionDetailMessage;
  
  protected IOR replyIOR;
  
  protected Integer requestIdInteger;
  
  protected Message dispatchHeader;
  
  protected ByteBuffer dispatchByteBuffer;
  
  protected byte streamFormatVersion;
  
  protected boolean streamFormatVersionSet = false;
  
  protected Request diiRequest;
  
  protected boolean cancelRequestAlreadySent = false;
  
  protected ProtocolHandler protocolHandler;
  
  protected boolean _executeReturnServantInResponseConstructor = false;
  
  protected boolean _executeRemoveThreadInfoInResponseConstructor = false;
  
  protected boolean _executePIInResponseConstructor = false;
  
  protected boolean isThreadDone = false;
  
  public CorbaMessageMediatorImpl(ORB paramORB, ContactInfo paramContactInfo, Connection paramConnection, GIOPVersion paramGIOPVersion, IOR paramIOR, int paramInt, short paramShort, String paramString, boolean paramBoolean) {
    this(paramORB, paramConnection);
    this.contactInfo = (CorbaContactInfo)paramContactInfo;
    this.addrDisposition = paramShort;
    this.streamFormatVersion = getStreamFormatVersionForThisRequest(this.contactInfo.getEffectiveTargetIOR(), paramGIOPVersion);
    this.streamFormatVersionSet = true;
    this.requestHeader = MessageBase.createRequest(this.orb, paramGIOPVersion, ORBUtility.getEncodingVersion(paramORB, paramIOR), paramInt, !paramBoolean, this.contactInfo.getEffectiveTargetIOR(), this.addrDisposition, paramString, new ServiceContexts(paramORB), null);
  }
  
  public CorbaMessageMediatorImpl(ORB paramORB, Connection paramConnection) {
    this.orb = paramORB;
    this.connection = (CorbaConnection)paramConnection;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
    this.interceptorWrapper = InterceptorsSystemException.get(paramORB, "rpc.protocol");
  }
  
  public CorbaMessageMediatorImpl(ORB paramORB, CorbaConnection paramCorbaConnection, Message paramMessage, ByteBuffer paramByteBuffer) {
    this(paramORB, paramCorbaConnection);
    this.dispatchHeader = paramMessage;
    this.dispatchByteBuffer = paramByteBuffer;
  }
  
  public Broker getBroker() { return this.orb; }
  
  public ContactInfo getContactInfo() { return this.contactInfo; }
  
  public Connection getConnection() { return this.connection; }
  
  public void initializeMessage() { getRequestHeader().write(this.outputObject); }
  
  public void finishSendingRequest() { this.outputObject.finishSendingMessage(); }
  
  public InputObject waitForResponse() { return getRequestHeader().isResponseExpected() ? this.connection.waitForResponse(this) : null; }
  
  public void setOutputObject(OutputObject paramOutputObject) { this.outputObject = (CDROutputObject)paramOutputObject; }
  
  public OutputObject getOutputObject() { return this.outputObject; }
  
  public void setInputObject(InputObject paramInputObject) { this.inputObject = (CDRInputObject)paramInputObject; }
  
  public InputObject getInputObject() { return this.inputObject; }
  
  public void setReplyHeader(LocateReplyOrReplyMessage paramLocateReplyOrReplyMessage) {
    this.replyHeader = paramLocateReplyOrReplyMessage;
    this.replyIOR = paramLocateReplyOrReplyMessage.getIOR();
  }
  
  public LocateReplyMessage getLocateReplyHeader() { return (LocateReplyMessage)this.replyHeader; }
  
  public ReplyMessage getReplyHeader() { return (ReplyMessage)this.replyHeader; }
  
  public void setReplyExceptionDetailMessage(String paramString) { this.replyExceptionDetailMessage = paramString; }
  
  public RequestMessage getRequestHeader() { return this.requestHeader; }
  
  public GIOPVersion getGIOPVersion() { return (this.messageHeader != null) ? this.messageHeader.getGIOPVersion() : getRequestHeader().getGIOPVersion(); }
  
  public byte getEncodingVersion() { return (this.messageHeader != null) ? this.messageHeader.getEncodingVersion() : getRequestHeader().getEncodingVersion(); }
  
  public int getRequestId() { return getRequestHeader().getRequestId(); }
  
  public Integer getRequestIdInteger() {
    if (this.requestIdInteger == null)
      this.requestIdInteger = new Integer(getRequestHeader().getRequestId()); 
    return this.requestIdInteger;
  }
  
  public boolean isOneWay() { return !getRequestHeader().isResponseExpected(); }
  
  public short getAddrDisposition() { return this.addrDisposition; }
  
  public String getOperationName() { return getRequestHeader().getOperation(); }
  
  public ServiceContexts getRequestServiceContexts() { return getRequestHeader().getServiceContexts(); }
  
  public ServiceContexts getReplyServiceContexts() { return getReplyHeader().getServiceContexts(); }
  
  public void sendCancelRequestIfFinalFragmentNotSent() {
    if (!sentFullMessage() && sentFragment() && !this.cancelRequestAlreadySent)
      try {
        if (this.orb.subcontractDebugFlag)
          dprint(".sendCancelRequestIfFinalFragmentNotSent->: " + opAndId(this)); 
        this.connection.sendCancelRequestWithLock(getGIOPVersion(), getRequestId());
        this.cancelRequestAlreadySent = true;
      } catch (IOException iOException) {
        if (this.orb.subcontractDebugFlag)
          dprint(".sendCancelRequestIfFinalFragmentNotSent: !ERROR : " + opAndId(this), iOException); 
        throw this.interceptorWrapper.ioexceptionDuringCancelRequest(CompletionStatus.COMPLETED_MAYBE, iOException);
      } finally {
        if (this.orb.subcontractDebugFlag)
          dprint(".sendCancelRequestIfFinalFragmentNotSent<-: " + opAndId(this)); 
      }  
  }
  
  public boolean sentFullMessage() { return this.outputObject.getBufferManager().sentFullMessage(); }
  
  public boolean sentFragment() { return this.outputObject.getBufferManager().sentFragment(); }
  
  public void setDIIInfo(Request paramRequest) { this.diiRequest = paramRequest; }
  
  public boolean isDIIRequest() { return (this.diiRequest != null); }
  
  public Exception unmarshalDIIUserException(String paramString, InputStream paramInputStream) {
    if (!isDIIRequest())
      return null; 
    ExceptionList exceptionList = this.diiRequest.exceptions();
    try {
      for (byte b = 0; b < exceptionList.count(); b++) {
        TypeCode typeCode = exceptionList.item(b);
        if (typeCode.id().equals(paramString)) {
          Any any = this.orb.create_any();
          any.read_value(paramInputStream, typeCode);
          return new UnknownUserException(any);
        } 
      } 
    } catch (Exception exception) {
      throw this.wrapper.unexpectedDiiException(exception);
    } 
    return this.wrapper.unknownCorbaExc(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public void setDIIException(Exception paramException) { this.diiRequest.env().exception(paramException); }
  
  public void handleDIIReply(InputStream paramInputStream) {
    if (!isDIIRequest())
      return; 
    ((RequestImpl)this.diiRequest).unmarshalReply(paramInputStream);
  }
  
  public Message getDispatchHeader() { return this.dispatchHeader; }
  
  public void setDispatchHeader(Message paramMessage) { this.dispatchHeader = paramMessage; }
  
  public ByteBuffer getDispatchBuffer() { return this.dispatchByteBuffer; }
  
  public void setDispatchBuffer(ByteBuffer paramByteBuffer) { this.dispatchByteBuffer = paramByteBuffer; }
  
  public int getThreadPoolToUse() {
    int i = 0;
    Message message = getDispatchHeader();
    if (message != null)
      i = message.getThreadPoolToUse(); 
    return i;
  }
  
  public byte getStreamFormatVersion() { return this.streamFormatVersionSet ? this.streamFormatVersion : getStreamFormatVersionForReply(); }
  
  public byte getStreamFormatVersionForReply() {
    ServiceContexts serviceContexts = getRequestServiceContexts();
    MaxStreamFormatVersionServiceContext maxStreamFormatVersionServiceContext = (MaxStreamFormatVersionServiceContext)serviceContexts.get(17);
    if (maxStreamFormatVersionServiceContext != null) {
      byte b1 = ORBUtility.getMaxStreamFormatVersion();
      byte b2 = maxStreamFormatVersionServiceContext.getMaximumStreamFormatVersion();
      return (byte)Math.min(b1, b2);
    } 
    return getGIOPVersion().lessThan(GIOPVersion.V1_3) ? 1 : 2;
  }
  
  public boolean isSystemExceptionReply() { return (this.replyHeader.getReplyStatus() == 2); }
  
  public boolean isUserExceptionReply() { return (this.replyHeader.getReplyStatus() == 1); }
  
  public boolean isLocationForwardReply() { return (this.replyHeader.getReplyStatus() == 3 || this.replyHeader.getReplyStatus() == 4); }
  
  public boolean isDifferentAddrDispositionRequestedReply() { return (this.replyHeader.getReplyStatus() == 5); }
  
  public short getAddrDispositionReply() { return this.replyHeader.getAddrDisposition(); }
  
  public IOR getForwardedIOR() { return this.replyHeader.getIOR(); }
  
  public SystemException getSystemExceptionReply() { return this.replyHeader.getSystemException(this.replyExceptionDetailMessage); }
  
  public ObjectKey getObjectKey() { return getRequestHeader().getObjectKey(); }
  
  public void setProtocolHandler(CorbaProtocolHandler paramCorbaProtocolHandler) { throw this.wrapper.methodShouldNotBeCalled(); }
  
  public CorbaProtocolHandler getProtocolHandler() { return this; }
  
  public OutputStream createReply() {
    getProtocolHandler().createResponse(this, (ServiceContexts)null);
    return (OutputStream)getOutputObject();
  }
  
  public OutputStream createExceptionReply() {
    getProtocolHandler().createUserExceptionResponse(this, (ServiceContexts)null);
    return (OutputStream)getOutputObject();
  }
  
  public boolean executeReturnServantInResponseConstructor() { return this._executeReturnServantInResponseConstructor; }
  
  public void setExecuteReturnServantInResponseConstructor(boolean paramBoolean) { this._executeReturnServantInResponseConstructor = paramBoolean; }
  
  public boolean executeRemoveThreadInfoInResponseConstructor() { return this._executeRemoveThreadInfoInResponseConstructor; }
  
  public void setExecuteRemoveThreadInfoInResponseConstructor(boolean paramBoolean) { this._executeRemoveThreadInfoInResponseConstructor = paramBoolean; }
  
  public boolean executePIInResponseConstructor() { return this._executePIInResponseConstructor; }
  
  public void setExecutePIInResponseConstructor(boolean paramBoolean) { this._executePIInResponseConstructor = paramBoolean; }
  
  private byte getStreamFormatVersionForThisRequest(IOR paramIOR, GIOPVersion paramGIOPVersion) {
    byte b1 = ORBUtility.getMaxStreamFormatVersion();
    IOR iOR = this.contactInfo.getEffectiveTargetIOR();
    IIOPProfileTemplate iIOPProfileTemplate = (IIOPProfileTemplate)iOR.getProfile().getTaggedProfileTemplate();
    Iterator iterator = iIOPProfileTemplate.iteratorById(38);
    if (!iterator.hasNext())
      return paramGIOPVersion.lessThan(GIOPVersion.V1_3) ? 1 : 2; 
    byte b2 = ((MaxStreamFormatVersionComponent)iterator.next()).getMaxStreamFormatVersion();
    return (byte)Math.min(b1, b2);
  }
  
  public boolean handleRequest(MessageMediator paramMessageMediator) {
    try {
      this.dispatchHeader.callback(this);
    } catch (IOException iOException) {}
    return this.isThreadDone;
  }
  
  private void setWorkThenPoolOrResumeSelect(Message paramMessage) {
    if (getConnection().getEventHandler().shouldUseSelectThreadToWait()) {
      resumeSelect(paramMessage);
    } else {
      this.isThreadDone = true;
      this.orb.getTransportManager().getSelector(0).unregisterForEvent(getConnection().getEventHandler());
      this.orb.getTransportManager().getSelector(0).registerForEvent(getConnection().getEventHandler());
    } 
  }
  
  private void setWorkThenReadOrResumeSelect(Message paramMessage) {
    if (getConnection().getEventHandler().shouldUseSelectThreadToWait()) {
      resumeSelect(paramMessage);
    } else {
      this.isThreadDone = false;
    } 
  }
  
  private void resumeSelect(Message paramMessage) {
    if (transportDebug()) {
      dprint(".resumeSelect:->");
      String str = "?";
      if (paramMessage instanceof RequestMessage) {
        str = (new Integer(((RequestMessage)paramMessage).getRequestId())).toString();
      } else if (paramMessage instanceof ReplyMessage) {
        str = (new Integer(((ReplyMessage)paramMessage).getRequestId())).toString();
      } else if (paramMessage instanceof FragmentMessage_1_2) {
        str = (new Integer(((FragmentMessage_1_2)paramMessage).getRequestId())).toString();
      } 
      dprint(".resumeSelect: id/" + str + " " + getConnection());
    } 
    EventHandler eventHandler = getConnection().getEventHandler();
    this.orb.getTransportManager().getSelector(0).registerInterestOps(eventHandler);
    if (transportDebug())
      dprint(".resumeSelect:<-"); 
  }
  
  private void setInputObject() {
    if (getConnection().getContactInfo() != null) {
      this.inputObject = (CDRInputObject)getConnection().getContactInfo().createInputObject(this.orb, this);
    } else if (getConnection().getAcceptor() != null) {
      this.inputObject = (CDRInputObject)getConnection().getAcceptor().createInputObject(this.orb, this);
    } else {
      throw new RuntimeException("CorbaMessageMediatorImpl.setInputObject");
    } 
    this.inputObject.setMessageMediator(this);
    setInputObject(this.inputObject);
  }
  
  private void signalResponseReceived() { this.connection.getResponseWaitingRoom().responseReceived(this.inputObject); }
  
  public void handleInput(Message paramMessage) {
    try {
      this.messageHeader = paramMessage;
      if (transportDebug())
        dprint(".handleInput->: " + MessageBase.typeToString(paramMessage.getType())); 
      setWorkThenReadOrResumeSelect(paramMessage);
      switch (paramMessage.getType()) {
        case 5:
          if (transportDebug())
            dprint(".handleInput: CloseConnection: purging"); 
          this.connection.purgeCalls(this.wrapper.connectionRebind(), true, false);
          break;
        case 6:
          if (transportDebug())
            dprint(".handleInput: MessageError: purging"); 
          this.connection.purgeCalls(this.wrapper.recvMsgError(), true, false);
          break;
        default:
          if (transportDebug())
            dprint(".handleInput: ERROR: " + MessageBase.typeToString(paramMessage.getType())); 
          throw this.wrapper.badGiopRequestType();
      } 
      releaseByteBufferToPool();
    } finally {
      if (transportDebug())
        dprint(".handleInput<-: " + MessageBase.typeToString(paramMessage.getType())); 
    } 
  }
  
  public void handleInput(RequestMessage_1_0 paramRequestMessage_1_0) throws IOException {
    try {
      if (transportDebug())
        dprint(".REQUEST 1.0->: " + paramRequestMessage_1_0); 
      try {
        this.messageHeader = this.requestHeader = paramRequestMessage_1_0;
        setInputObject();
      } finally {
        setWorkThenPoolOrResumeSelect(paramRequestMessage_1_0);
      } 
      getProtocolHandler().handleRequest(paramRequestMessage_1_0, this);
    } catch (Throwable throwable) {
      if (transportDebug())
        dprint(".REQUEST 1.0: !!ERROR!!: " + paramRequestMessage_1_0, throwable); 
    } finally {
      if (transportDebug())
        dprint(".REQUEST 1.0<-: " + paramRequestMessage_1_0); 
    } 
  }
  
  public void handleInput(RequestMessage_1_1 paramRequestMessage_1_1) throws IOException {
    try {
      if (transportDebug())
        dprint(".REQUEST 1.1->: " + paramRequestMessage_1_1); 
      try {
        this.messageHeader = this.requestHeader = paramRequestMessage_1_1;
        setInputObject();
        this.connection.serverRequest_1_1_Put(this);
      } finally {
        setWorkThenPoolOrResumeSelect(paramRequestMessage_1_1);
      } 
      getProtocolHandler().handleRequest(paramRequestMessage_1_1, this);
    } catch (Throwable throwable) {
      if (transportDebug())
        dprint(".REQUEST 1.1: !!ERROR!!: " + paramRequestMessage_1_1, throwable); 
    } finally {
      if (transportDebug())
        dprint(".REQUEST 1.1<-: " + paramRequestMessage_1_1); 
    } 
  }
  
  public void handleInput(RequestMessage_1_2 paramRequestMessage_1_2) throws IOException {
    try {
      try {
        this.messageHeader = this.requestHeader = paramRequestMessage_1_2;
        paramRequestMessage_1_2.unmarshalRequestID(this.dispatchByteBuffer);
        setInputObject();
        if (transportDebug())
          dprint(".REQUEST 1.2->: id/" + paramRequestMessage_1_2.getRequestId() + ": " + paramRequestMessage_1_2); 
        this.connection.serverRequestMapPut(paramRequestMessage_1_2.getRequestId(), this);
      } finally {
        setWorkThenPoolOrResumeSelect(paramRequestMessage_1_2);
      } 
      getProtocolHandler().handleRequest(paramRequestMessage_1_2, this);
    } catch (Throwable throwable) {
      if (transportDebug())
        dprint(".REQUEST 1.2: id/" + paramRequestMessage_1_2.getRequestId() + ": !!ERROR!!: " + paramRequestMessage_1_2, throwable); 
    } finally {
      this.connection.serverRequestMapRemove(paramRequestMessage_1_2.getRequestId());
      if (transportDebug())
        dprint(".REQUEST 1.2<-: id/" + paramRequestMessage_1_2.getRequestId() + ": " + paramRequestMessage_1_2); 
    } 
  }
  
  public void handleInput(ReplyMessage_1_0 paramReplyMessage_1_0) throws IOException {
    try {
      try {
        if (transportDebug())
          dprint(".REPLY 1.0->: " + paramReplyMessage_1_0); 
        this.messageHeader = this.replyHeader = paramReplyMessage_1_0;
        setInputObject();
        this.inputObject.unmarshalHeader();
        signalResponseReceived();
      } finally {
        setWorkThenReadOrResumeSelect(paramReplyMessage_1_0);
      } 
    } catch (Throwable throwable) {
      if (transportDebug())
        dprint(".REPLY 1.0: !!ERROR!!: " + paramReplyMessage_1_0, throwable); 
    } finally {
      if (transportDebug())
        dprint(".REPLY 1.0<-: " + paramReplyMessage_1_0); 
    } 
  }
  
  public void handleInput(ReplyMessage_1_1 paramReplyMessage_1_1) throws IOException {
    try {
      if (transportDebug())
        dprint(".REPLY 1.1->: " + paramReplyMessage_1_1); 
      this.messageHeader = this.replyHeader = paramReplyMessage_1_1;
      setInputObject();
      if (paramReplyMessage_1_1.moreFragmentsToFollow()) {
        this.connection.clientReply_1_1_Put(this);
        setWorkThenPoolOrResumeSelect(paramReplyMessage_1_1);
        this.inputObject.unmarshalHeader();
        signalResponseReceived();
      } else {
        this.inputObject.unmarshalHeader();
        signalResponseReceived();
        setWorkThenReadOrResumeSelect(paramReplyMessage_1_1);
      } 
    } catch (Throwable throwable) {
      if (transportDebug())
        dprint(".REPLY 1.1: !!ERROR!!: " + paramReplyMessage_1_1); 
    } finally {
      if (transportDebug())
        dprint(".REPLY 1.1<-: " + paramReplyMessage_1_1); 
    } 
  }
  
  public void handleInput(ReplyMessage_1_2 paramReplyMessage_1_2) throws IOException {
    try {
      try {
        this.messageHeader = this.replyHeader = paramReplyMessage_1_2;
        paramReplyMessage_1_2.unmarshalRequestID(this.dispatchByteBuffer);
        if (transportDebug())
          dprint(".REPLY 1.2->: id/" + paramReplyMessage_1_2.getRequestId() + ": more?: " + paramReplyMessage_1_2.moreFragmentsToFollow() + ": " + paramReplyMessage_1_2); 
        setInputObject();
        signalResponseReceived();
      } finally {
        setWorkThenReadOrResumeSelect(paramReplyMessage_1_2);
      } 
    } catch (Throwable throwable) {
      if (transportDebug())
        dprint(".REPLY 1.2: id/" + paramReplyMessage_1_2.getRequestId() + ": !!ERROR!!: " + paramReplyMessage_1_2, throwable); 
    } finally {
      if (transportDebug())
        dprint(".REPLY 1.2<-: id/" + paramReplyMessage_1_2.getRequestId() + ": " + paramReplyMessage_1_2); 
    } 
  }
  
  public void handleInput(LocateRequestMessage_1_0 paramLocateRequestMessage_1_0) throws IOException {
    try {
      if (transportDebug())
        dprint(".LOCATE_REQUEST 1.0->: " + paramLocateRequestMessage_1_0); 
      try {
        this.messageHeader = paramLocateRequestMessage_1_0;
        setInputObject();
      } finally {
        setWorkThenPoolOrResumeSelect(paramLocateRequestMessage_1_0);
      } 
      getProtocolHandler().handleRequest(paramLocateRequestMessage_1_0, this);
    } catch (Throwable throwable) {
      if (transportDebug())
        dprint(".LOCATE_REQUEST 1.0: !!ERROR!!: " + paramLocateRequestMessage_1_0, throwable); 
    } finally {
      if (transportDebug())
        dprint(".LOCATE_REQUEST 1.0<-: " + paramLocateRequestMessage_1_0); 
    } 
  }
  
  public void handleInput(LocateRequestMessage_1_1 paramLocateRequestMessage_1_1) throws IOException {
    try {
      if (transportDebug())
        dprint(".LOCATE_REQUEST 1.1->: " + paramLocateRequestMessage_1_1); 
      try {
        this.messageHeader = paramLocateRequestMessage_1_1;
        setInputObject();
      } finally {
        setWorkThenPoolOrResumeSelect(paramLocateRequestMessage_1_1);
      } 
      getProtocolHandler().handleRequest(paramLocateRequestMessage_1_1, this);
    } catch (Throwable throwable) {
      if (transportDebug())
        dprint(".LOCATE_REQUEST 1.1: !!ERROR!!: " + paramLocateRequestMessage_1_1, throwable); 
    } finally {
      if (transportDebug())
        dprint(".LOCATE_REQUEST 1.1<-:" + paramLocateRequestMessage_1_1); 
    } 
  }
  
  public void handleInput(LocateRequestMessage_1_2 paramLocateRequestMessage_1_2) throws IOException {
    try {
      try {
        this.messageHeader = paramLocateRequestMessage_1_2;
        paramLocateRequestMessage_1_2.unmarshalRequestID(this.dispatchByteBuffer);
        setInputObject();
        if (transportDebug())
          dprint(".LOCATE_REQUEST 1.2->: id/" + paramLocateRequestMessage_1_2.getRequestId() + ": " + paramLocateRequestMessage_1_2); 
        if (paramLocateRequestMessage_1_2.moreFragmentsToFollow())
          this.connection.serverRequestMapPut(paramLocateRequestMessage_1_2.getRequestId(), this); 
      } finally {
        setWorkThenPoolOrResumeSelect(paramLocateRequestMessage_1_2);
      } 
      getProtocolHandler().handleRequest(paramLocateRequestMessage_1_2, this);
    } catch (Throwable throwable) {
      if (transportDebug())
        dprint(".LOCATE_REQUEST 1.2: id/" + paramLocateRequestMessage_1_2.getRequestId() + ": !!ERROR!!: " + paramLocateRequestMessage_1_2, throwable); 
    } finally {
      if (transportDebug())
        dprint(".LOCATE_REQUEST 1.2<-: id/" + paramLocateRequestMessage_1_2.getRequestId() + ": " + paramLocateRequestMessage_1_2); 
    } 
  }
  
  public void handleInput(LocateReplyMessage_1_0 paramLocateReplyMessage_1_0) throws IOException {
    try {
      if (transportDebug())
        dprint(".LOCATE_REPLY 1.0->:" + paramLocateReplyMessage_1_0); 
      try {
        this.messageHeader = paramLocateReplyMessage_1_0;
        setInputObject();
        this.inputObject.unmarshalHeader();
        signalResponseReceived();
      } finally {
        setWorkThenReadOrResumeSelect(paramLocateReplyMessage_1_0);
      } 
    } catch (Throwable throwable) {
      if (transportDebug())
        dprint(".LOCATE_REPLY 1.0: !!ERROR!!: " + paramLocateReplyMessage_1_0, throwable); 
    } finally {
      if (transportDebug())
        dprint(".LOCATE_REPLY 1.0<-: " + paramLocateReplyMessage_1_0); 
    } 
  }
  
  public void handleInput(LocateReplyMessage_1_1 paramLocateReplyMessage_1_1) throws IOException {
    try {
      if (transportDebug())
        dprint(".LOCATE_REPLY 1.1->: " + paramLocateReplyMessage_1_1); 
      try {
        this.messageHeader = paramLocateReplyMessage_1_1;
        setInputObject();
        this.inputObject.unmarshalHeader();
        signalResponseReceived();
      } finally {
        setWorkThenReadOrResumeSelect(paramLocateReplyMessage_1_1);
      } 
    } catch (Throwable throwable) {
      if (transportDebug())
        dprint(".LOCATE_REPLY 1.1: !!ERROR!!: " + paramLocateReplyMessage_1_1, throwable); 
    } finally {
      if (transportDebug())
        dprint(".LOCATE_REPLY 1.1<-: " + paramLocateReplyMessage_1_1); 
    } 
  }
  
  public void handleInput(LocateReplyMessage_1_2 paramLocateReplyMessage_1_2) throws IOException {
    try {
      try {
        this.messageHeader = paramLocateReplyMessage_1_2;
        paramLocateReplyMessage_1_2.unmarshalRequestID(this.dispatchByteBuffer);
        setInputObject();
        if (transportDebug())
          dprint(".LOCATE_REPLY 1.2->: id/" + paramLocateReplyMessage_1_2.getRequestId() + ": " + paramLocateReplyMessage_1_2); 
        signalResponseReceived();
      } finally {
        setWorkThenPoolOrResumeSelect(paramLocateReplyMessage_1_2);
      } 
    } catch (Throwable throwable) {
      if (transportDebug())
        dprint(".LOCATE_REPLY 1.2: id/" + paramLocateReplyMessage_1_2.getRequestId() + ": !!ERROR!!: " + paramLocateReplyMessage_1_2, throwable); 
    } finally {
      if (transportDebug())
        dprint(".LOCATE_REPLY 1.2<-: id/" + paramLocateReplyMessage_1_2.getRequestId() + ": " + paramLocateReplyMessage_1_2); 
    } 
  }
  
  public void handleInput(FragmentMessage_1_1 paramFragmentMessage_1_1) throws IOException {
    try {
      if (transportDebug())
        dprint(".FRAGMENT 1.1->: more?: " + paramFragmentMessage_1_1.moreFragmentsToFollow() + ": " + paramFragmentMessage_1_1); 
      try {
        this.messageHeader = paramFragmentMessage_1_1;
        MessageMediator messageMediator = null;
        CDRInputObject cDRInputObject = null;
        if (this.connection.isServer()) {
          messageMediator = this.connection.serverRequest_1_1_Get();
        } else {
          messageMediator = this.connection.clientReply_1_1_Get();
        } 
        if (messageMediator != null)
          cDRInputObject = (CDRInputObject)messageMediator.getInputObject(); 
        if (cDRInputObject == null) {
          if (transportDebug())
            dprint(".FRAGMENT 1.1: ++++DISCARDING++++: " + paramFragmentMessage_1_1); 
          releaseByteBufferToPool();
          return;
        } 
        cDRInputObject.getBufferManager().processFragment(this.dispatchByteBuffer, paramFragmentMessage_1_1);
        if (!paramFragmentMessage_1_1.moreFragmentsToFollow())
          if (this.connection.isServer()) {
            this.connection.serverRequest_1_1_Remove();
          } else {
            this.connection.clientReply_1_1_Remove();
          }  
      } finally {
        setWorkThenReadOrResumeSelect(paramFragmentMessage_1_1);
      } 
    } catch (Throwable throwable) {
      if (transportDebug())
        dprint(".FRAGMENT 1.1: !!ERROR!!: " + paramFragmentMessage_1_1, throwable); 
    } finally {
      if (transportDebug())
        dprint(".FRAGMENT 1.1<-: " + paramFragmentMessage_1_1); 
    } 
  }
  
  public void handleInput(FragmentMessage_1_2 paramFragmentMessage_1_2) throws IOException {
    try {
      try {
        this.messageHeader = paramFragmentMessage_1_2;
        paramFragmentMessage_1_2.unmarshalRequestID(this.dispatchByteBuffer);
        if (transportDebug())
          dprint(".FRAGMENT 1.2->: id/" + paramFragmentMessage_1_2.getRequestId() + ": more?: " + paramFragmentMessage_1_2.moreFragmentsToFollow() + ": " + paramFragmentMessage_1_2); 
        MessageMediator messageMediator = null;
        InputObject inputObject1 = null;
        if (this.connection.isServer()) {
          messageMediator = this.connection.serverRequestMapGet(paramFragmentMessage_1_2.getRequestId());
        } else {
          messageMediator = this.connection.clientRequestMapGet(paramFragmentMessage_1_2.getRequestId());
        } 
        if (messageMediator != null)
          inputObject1 = messageMediator.getInputObject(); 
        if (inputObject1 == null) {
          if (transportDebug())
            dprint(".FRAGMENT 1.2: id/" + paramFragmentMessage_1_2.getRequestId() + ": ++++DISCARDING++++: " + paramFragmentMessage_1_2); 
          releaseByteBufferToPool();
          return;
        } 
        ((CDRInputObject)inputObject1).getBufferManager().processFragment(this.dispatchByteBuffer, paramFragmentMessage_1_2);
        if (!this.connection.isServer());
      } finally {
        setWorkThenReadOrResumeSelect(paramFragmentMessage_1_2);
      } 
    } catch (Throwable throwable) {
      if (transportDebug())
        dprint(".FRAGMENT 1.2: id/" + paramFragmentMessage_1_2.getRequestId() + ": !!ERROR!!: " + paramFragmentMessage_1_2, throwable); 
    } finally {
      if (transportDebug())
        dprint(".FRAGMENT 1.2<-: id/" + paramFragmentMessage_1_2.getRequestId() + ": " + paramFragmentMessage_1_2); 
    } 
  }
  
  public void handleInput(CancelRequestMessage paramCancelRequestMessage) throws IOException {
    try {
      try {
        this.messageHeader = paramCancelRequestMessage;
        setInputObject();
        this.inputObject.unmarshalHeader();
        if (transportDebug())
          dprint(".CANCEL->: id/" + paramCancelRequestMessage.getRequestId() + ": " + paramCancelRequestMessage.getGIOPVersion() + ": " + paramCancelRequestMessage); 
        processCancelRequest(paramCancelRequestMessage.getRequestId());
        releaseByteBufferToPool();
      } finally {
        setWorkThenReadOrResumeSelect(paramCancelRequestMessage);
      } 
    } catch (Throwable throwable) {
      if (transportDebug())
        dprint(".CANCEL: id/" + paramCancelRequestMessage.getRequestId() + ": !!ERROR!!: " + paramCancelRequestMessage, throwable); 
    } finally {
      if (transportDebug())
        dprint(".CANCEL<-: id/" + paramCancelRequestMessage.getRequestId() + ": " + paramCancelRequestMessage.getGIOPVersion() + ": " + paramCancelRequestMessage); 
    } 
  }
  
  private void throwNotImplemented() {
    this.isThreadDone = false;
    throwNotImplemented("");
  }
  
  private void throwNotImplemented(String paramString) { throw new RuntimeException("CorbaMessageMediatorImpl: not implemented " + paramString); }
  
  private void dprint(String paramString, Throwable paramThrowable) {
    dprint(paramString);
    paramThrowable.printStackTrace(System.out);
  }
  
  private void dprint(String paramString) { ORBUtility.dprint("CorbaMessageMediatorImpl", paramString); }
  
  protected String opAndId(CorbaMessageMediator paramCorbaMessageMediator) { return ORBUtility.operationNameAndRequestId(paramCorbaMessageMediator); }
  
  private boolean transportDebug() { return this.orb.transportDebugFlag; }
  
  private final void processCancelRequest(int paramInt) {
    if (!this.connection.isServer())
      return; 
    MessageMediator messageMediator = this.connection.serverRequestMapGet(paramInt);
    if (messageMediator == null) {
      messageMediator = this.connection.serverRequest_1_1_Get();
      if (messageMediator == null)
        return; 
      int i = ((CorbaMessageMediator)messageMediator).getRequestId();
      if (i != paramInt)
        return; 
      if (i == 0)
        return; 
    } else {
      int i = ((CorbaMessageMediator)messageMediator).getRequestId();
    } 
    RequestMessage requestMessage = ((CorbaMessageMediator)messageMediator).getRequestHeader();
    if (requestMessage.getType() != 0)
      this.wrapper.badMessageTypeForCancel(); 
    BufferManagerReadStream bufferManagerReadStream = (BufferManagerReadStream)((CDRInputObject)messageMediator.getInputObject()).getBufferManager();
    bufferManagerReadStream.cancelProcessing(paramInt);
  }
  
  public void handleRequest(RequestMessage paramRequestMessage, CorbaMessageMediator paramCorbaMessageMediator) {
    try {
      beginRequest(paramCorbaMessageMediator);
      try {
        handleRequestRequest(paramCorbaMessageMediator);
        if (paramCorbaMessageMediator.isOneWay())
          return; 
      } catch (Throwable throwable) {
        if (paramCorbaMessageMediator.isOneWay())
          return; 
        handleThrowableDuringServerDispatch(paramCorbaMessageMediator, throwable, CompletionStatus.COMPLETED_MAYBE);
      } 
      sendResponse(paramCorbaMessageMediator);
      endRequest(paramCorbaMessageMediator);
    } catch (Throwable throwable) {
      dispatchError(paramCorbaMessageMediator, "RequestMessage", throwable);
    } finally {
      endRequest(paramCorbaMessageMediator);
    } 
  }
  
  public void handleRequest(LocateRequestMessage paramLocateRequestMessage, CorbaMessageMediator paramCorbaMessageMediator) {
    try {
      beginRequest(paramCorbaMessageMediator);
      try {
        handleLocateRequest(paramCorbaMessageMediator);
      } catch (Throwable throwable) {
        handleThrowableDuringServerDispatch(paramCorbaMessageMediator, throwable, CompletionStatus.COMPLETED_MAYBE);
      } 
      sendResponse(paramCorbaMessageMediator);
    } catch (Throwable throwable) {
      dispatchError(paramCorbaMessageMediator, "LocateRequestMessage", throwable);
    } finally {
      endRequest(paramCorbaMessageMediator);
    } 
  }
  
  private void beginRequest(CorbaMessageMediator paramCorbaMessageMediator) {
    ORB oRB = (ORB)paramCorbaMessageMediator.getBroker();
    if (oRB.subcontractDebugFlag)
      dprint(".handleRequest->:"); 
    this.connection.serverRequestProcessingBegins();
  }
  
  private void dispatchError(CorbaMessageMediator paramCorbaMessageMediator, String paramString, Throwable paramThrowable) {
    if (this.orb.subcontractDebugFlag)
      dprint(".handleRequest: " + opAndId(paramCorbaMessageMediator) + ": !!ERROR!!: " + paramString, paramThrowable); 
  }
  
  private void sendResponse(CorbaMessageMediator paramCorbaMessageMediator) {
    if (this.orb.subcontractDebugFlag)
      dprint(".handleRequest: " + opAndId(paramCorbaMessageMediator) + ": sending response"); 
    CDROutputObject cDROutputObject = (CDROutputObject)paramCorbaMessageMediator.getOutputObject();
    if (cDROutputObject != null)
      cDROutputObject.finishSendingMessage(); 
  }
  
  private void endRequest(CorbaMessageMediator paramCorbaMessageMediator) {
    ORB oRB = (ORB)paramCorbaMessageMediator.getBroker();
    if (oRB.subcontractDebugFlag)
      dprint(".handleRequest<-: " + opAndId(paramCorbaMessageMediator)); 
    try {
      OutputObject outputObject1 = paramCorbaMessageMediator.getOutputObject();
      if (outputObject1 != null)
        outputObject1.close(); 
      InputObject inputObject1 = paramCorbaMessageMediator.getInputObject();
      if (inputObject1 != null)
        inputObject1.close(); 
    } catch (IOException iOException) {
      if (oRB.subcontractDebugFlag)
        dprint(".endRequest: IOException:" + iOException.getMessage(), iOException); 
    } finally {
      ((CorbaConnection)paramCorbaMessageMediator.getConnection()).serverRequestProcessingEnds();
    } 
  }
  
  protected void handleRequestRequest(CorbaMessageMediator paramCorbaMessageMediator) {
    ((CDRInputObject)paramCorbaMessageMediator.getInputObject()).unmarshalHeader();
    oRB = (ORB)paramCorbaMessageMediator.getBroker();
    synchronized (oRB) {
      oRB.checkShutdownState();
    } 
    ObjectKey objectKey = paramCorbaMessageMediator.getObjectKey();
    if (oRB.subcontractDebugFlag) {
      ObjectKeyTemplate objectKeyTemplate = objectKey.getTemplate();
      dprint(".handleRequest: " + opAndId(paramCorbaMessageMediator) + ": dispatching to scid: " + objectKeyTemplate.getSubcontractId());
    } 
    CorbaServerRequestDispatcher corbaServerRequestDispatcher = objectKey.getServerRequestDispatcher(oRB);
    if (oRB.subcontractDebugFlag)
      dprint(".handleRequest: " + opAndId(paramCorbaMessageMediator) + ": dispatching to sc: " + corbaServerRequestDispatcher); 
    if (corbaServerRequestDispatcher == null)
      throw this.wrapper.noServerScInDispatch(); 
    try {
      oRB.startingDispatch();
      corbaServerRequestDispatcher.dispatch(paramCorbaMessageMediator);
    } finally {
      oRB.finishedDispatch();
    } 
  }
  
  protected void handleLocateRequest(CorbaMessageMediator paramCorbaMessageMediator) {
    ORB oRB = (ORB)paramCorbaMessageMediator.getBroker();
    LocateRequestMessage locateRequestMessage = (LocateRequestMessage)paramCorbaMessageMediator.getDispatchHeader();
    IOR iOR = null;
    LocateReplyMessage locateReplyMessage = null;
    short s = -1;
    try {
      ((CDRInputObject)paramCorbaMessageMediator.getInputObject()).unmarshalHeader();
      CorbaServerRequestDispatcher corbaServerRequestDispatcher = locateRequestMessage.getObjectKey().getServerRequestDispatcher(oRB);
      if (corbaServerRequestDispatcher == null)
        return; 
      iOR = corbaServerRequestDispatcher.locate(locateRequestMessage.getObjectKey());
      if (iOR == null) {
        locateReplyMessage = MessageBase.createLocateReply(oRB, locateRequestMessage.getGIOPVersion(), locateRequestMessage.getEncodingVersion(), locateRequestMessage.getRequestId(), 1, null);
      } else {
        locateReplyMessage = MessageBase.createLocateReply(oRB, locateRequestMessage.getGIOPVersion(), locateRequestMessage.getEncodingVersion(), locateRequestMessage.getRequestId(), 2, iOR);
      } 
    } catch (AddressingDispositionException addressingDispositionException) {
      locateReplyMessage = MessageBase.createLocateReply(oRB, locateRequestMessage.getGIOPVersion(), locateRequestMessage.getEncodingVersion(), locateRequestMessage.getRequestId(), 5, null);
      s = addressingDispositionException.expectedAddrDisp();
    } catch (RequestCanceledException requestCanceledException) {
      return;
    } catch (Exception exception) {
      locateReplyMessage = MessageBase.createLocateReply(oRB, locateRequestMessage.getGIOPVersion(), locateRequestMessage.getEncodingVersion(), locateRequestMessage.getRequestId(), 0, null);
    } 
    CDROutputObject cDROutputObject = createAppropriateOutputObject(paramCorbaMessageMediator, locateRequestMessage, locateReplyMessage);
    paramCorbaMessageMediator.setOutputObject(cDROutputObject);
    cDROutputObject.setMessageMediator(paramCorbaMessageMediator);
    locateReplyMessage.write(cDROutputObject);
    if (iOR != null)
      iOR.write(cDROutputObject); 
    if (s != -1)
      AddressingDispositionHelper.write(cDROutputObject, s); 
  }
  
  private CDROutputObject createAppropriateOutputObject(CorbaMessageMediator paramCorbaMessageMediator, Message paramMessage, LocateReplyMessage paramLocateReplyMessage) {
    CDROutputObject cDROutputObject;
    if (paramMessage.getGIOPVersion().lessThan(GIOPVersion.V1_2)) {
      cDROutputObject = OutputStreamFactory.newCDROutputObject((ORB)paramCorbaMessageMediator.getBroker(), this, GIOPVersion.V1_0, (CorbaConnection)paramCorbaMessageMediator.getConnection(), paramLocateReplyMessage, (byte)1);
    } else {
      cDROutputObject = OutputStreamFactory.newCDROutputObject((ORB)paramCorbaMessageMediator.getBroker(), paramCorbaMessageMediator, paramLocateReplyMessage, (byte)1);
    } 
    return cDROutputObject;
  }
  
  public void handleThrowableDuringServerDispatch(CorbaMessageMediator paramCorbaMessageMediator, Throwable paramThrowable, CompletionStatus paramCompletionStatus) {
    if (((ORB)paramCorbaMessageMediator.getBroker()).subcontractDebugFlag)
      dprint(".handleThrowableDuringServerDispatch: " + opAndId(paramCorbaMessageMediator) + ": " + paramThrowable); 
    handleThrowableDuringServerDispatch(paramCorbaMessageMediator, paramThrowable, paramCompletionStatus, 1);
  }
  
  protected void handleThrowableDuringServerDispatch(CorbaMessageMediator paramCorbaMessageMediator, Throwable paramThrowable, CompletionStatus paramCompletionStatus, int paramInt) {
    if (paramInt > 10) {
      if (((ORB)paramCorbaMessageMediator.getBroker()).subcontractDebugFlag)
        dprint(".handleThrowableDuringServerDispatch: " + opAndId(paramCorbaMessageMediator) + ": cannot handle: " + paramThrowable); 
      RuntimeException runtimeException = new RuntimeException("handleThrowableDuringServerDispatch: cannot create response.");
      runtimeException.initCause(paramThrowable);
      throw runtimeException;
    } 
    try {
      if (paramThrowable instanceof ForwardException) {
        ForwardException forwardException = (ForwardException)paramThrowable;
        createLocationForward(paramCorbaMessageMediator, forwardException.getIOR(), null);
        return;
      } 
      if (paramThrowable instanceof AddressingDispositionException) {
        handleAddressingDisposition(paramCorbaMessageMediator, (AddressingDispositionException)paramThrowable);
        return;
      } 
      SystemException systemException = convertThrowableToSystemException(paramThrowable, paramCompletionStatus);
      createSystemExceptionResponse(paramCorbaMessageMediator, systemException, null);
      return;
    } catch (Throwable throwable) {
      handleThrowableDuringServerDispatch(paramCorbaMessageMediator, throwable, paramCompletionStatus, paramInt + 1);
      return;
    } 
  }
  
  protected SystemException convertThrowableToSystemException(Throwable paramThrowable, CompletionStatus paramCompletionStatus) { return (paramThrowable instanceof SystemException) ? (SystemException)paramThrowable : ((paramThrowable instanceof RequestCanceledException) ? this.wrapper.requestCanceled(paramThrowable) : this.wrapper.runtimeexception(CompletionStatus.COMPLETED_MAYBE, paramThrowable)); }
  
  protected void handleAddressingDisposition(CorbaMessageMediator paramCorbaMessageMediator, AddressingDispositionException paramAddressingDispositionException) {
    Object object;
    LocateReplyMessage locateReplyMessage;
    CDROutputObject cDROutputObject;
    ReplyMessage replyMessage;
    short s = -1;
    switch (paramCorbaMessageMediator.getRequestHeader().getType()) {
      case 0:
        replyMessage = MessageBase.createReply((ORB)paramCorbaMessageMediator.getBroker(), paramCorbaMessageMediator.getGIOPVersion(), paramCorbaMessageMediator.getEncodingVersion(), paramCorbaMessageMediator.getRequestId(), 5, null, null);
        cDROutputObject = OutputStreamFactory.newCDROutputObject((ORB)paramCorbaMessageMediator.getBroker(), this, paramCorbaMessageMediator.getGIOPVersion(), (CorbaConnection)paramCorbaMessageMediator.getConnection(), replyMessage, (byte)1);
        paramCorbaMessageMediator.setOutputObject(cDROutputObject);
        cDROutputObject.setMessageMediator(paramCorbaMessageMediator);
        replyMessage.write(cDROutputObject);
        AddressingDispositionHelper.write(cDROutputObject, paramAddressingDispositionException.expectedAddrDisp());
        return;
      case 3:
        locateReplyMessage = MessageBase.createLocateReply((ORB)paramCorbaMessageMediator.getBroker(), paramCorbaMessageMediator.getGIOPVersion(), paramCorbaMessageMediator.getEncodingVersion(), paramCorbaMessageMediator.getRequestId(), 5, null);
        s = paramAddressingDispositionException.expectedAddrDisp();
        cDROutputObject = createAppropriateOutputObject(paramCorbaMessageMediator, paramCorbaMessageMediator.getRequestHeader(), locateReplyMessage);
        paramCorbaMessageMediator.setOutputObject(cDROutputObject);
        cDROutputObject.setMessageMediator(paramCorbaMessageMediator);
        locateReplyMessage.write(cDROutputObject);
        object = null;
        if (object != null)
          object.write(cDROutputObject); 
        if (s != -1)
          AddressingDispositionHelper.write(cDROutputObject, s); 
        return;
    } 
  }
  
  public CorbaMessageMediator createResponse(CorbaMessageMediator paramCorbaMessageMediator, ServiceContexts paramServiceContexts) { return createResponseHelper(paramCorbaMessageMediator, getServiceContextsForReply(paramCorbaMessageMediator, null)); }
  
  public CorbaMessageMediator createUserExceptionResponse(CorbaMessageMediator paramCorbaMessageMediator, ServiceContexts paramServiceContexts) { return createResponseHelper(paramCorbaMessageMediator, getServiceContextsForReply(paramCorbaMessageMediator, null), true); }
  
  public CorbaMessageMediator createUnknownExceptionResponse(CorbaMessageMediator paramCorbaMessageMediator, UnknownException paramUnknownException) {
    ServiceContexts serviceContexts = null;
    UNKNOWN uNKNOWN = new UNKNOWN(0, CompletionStatus.COMPLETED_MAYBE);
    serviceContexts = new ServiceContexts((ORB)paramCorbaMessageMediator.getBroker());
    UEInfoServiceContext uEInfoServiceContext = new UEInfoServiceContext(uNKNOWN);
    serviceContexts.put(uEInfoServiceContext);
    return createSystemExceptionResponse(paramCorbaMessageMediator, uNKNOWN, serviceContexts);
  }
  
  public CorbaMessageMediator createSystemExceptionResponse(CorbaMessageMediator paramCorbaMessageMediator, SystemException paramSystemException, ServiceContexts paramServiceContexts) {
    if (paramCorbaMessageMediator.getConnection() != null) {
      CorbaMessageMediatorImpl corbaMessageMediatorImpl = (CorbaMessageMediatorImpl)((CorbaConnection)paramCorbaMessageMediator.getConnection()).serverRequestMapGet(paramCorbaMessageMediator.getRequestId());
      OutputObject outputObject1 = null;
      if (corbaMessageMediatorImpl != null)
        outputObject1 = corbaMessageMediatorImpl.getOutputObject(); 
      if (outputObject1 != null && corbaMessageMediatorImpl.sentFragment() && !corbaMessageMediatorImpl.sentFullMessage())
        return corbaMessageMediatorImpl; 
    } 
    if (paramCorbaMessageMediator.executePIInResponseConstructor())
      ((ORB)paramCorbaMessageMediator.getBroker()).getPIHandler().setServerPIInfo(paramSystemException); 
    if (((ORB)paramCorbaMessageMediator.getBroker()).subcontractDebugFlag && paramSystemException != null)
      dprint(".createSystemExceptionResponse: " + opAndId(paramCorbaMessageMediator), paramSystemException); 
    ServiceContexts serviceContexts = getServiceContextsForReply(paramCorbaMessageMediator, paramServiceContexts);
    addExceptionDetailMessage(paramCorbaMessageMediator, paramSystemException, serviceContexts);
    CorbaMessageMediator corbaMessageMediator = createResponseHelper(paramCorbaMessageMediator, serviceContexts, false);
    ORBUtility.writeSystemException(paramSystemException, (OutputStream)corbaMessageMediator.getOutputObject());
    return corbaMessageMediator;
  }
  
  private void addExceptionDetailMessage(CorbaMessageMediator paramCorbaMessageMediator, SystemException paramSystemException, ServiceContexts paramServiceContexts) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
    paramSystemException.printStackTrace(printWriter);
    printWriter.flush();
    EncapsOutputStream encapsOutputStream = OutputStreamFactory.newEncapsOutputStream((ORB)paramCorbaMessageMediator.getBroker());
    encapsOutputStream.putEndian();
    encapsOutputStream.write_wstring(byteArrayOutputStream.toString());
    UnknownServiceContext unknownServiceContext = new UnknownServiceContext(14, encapsOutputStream.toByteArray());
    paramServiceContexts.put(unknownServiceContext);
  }
  
  public CorbaMessageMediator createLocationForward(CorbaMessageMediator paramCorbaMessageMediator, IOR paramIOR, ServiceContexts paramServiceContexts) {
    ReplyMessage replyMessage = MessageBase.createReply((ORB)paramCorbaMessageMediator.getBroker(), paramCorbaMessageMediator.getGIOPVersion(), paramCorbaMessageMediator.getEncodingVersion(), paramCorbaMessageMediator.getRequestId(), 3, getServiceContextsForReply(paramCorbaMessageMediator, paramServiceContexts), paramIOR);
    return createResponseHelper(paramCorbaMessageMediator, replyMessage, paramIOR);
  }
  
  protected CorbaMessageMediator createResponseHelper(CorbaMessageMediator paramCorbaMessageMediator, ServiceContexts paramServiceContexts) {
    ReplyMessage replyMessage = MessageBase.createReply((ORB)paramCorbaMessageMediator.getBroker(), paramCorbaMessageMediator.getGIOPVersion(), paramCorbaMessageMediator.getEncodingVersion(), paramCorbaMessageMediator.getRequestId(), 0, paramServiceContexts, null);
    return createResponseHelper(paramCorbaMessageMediator, replyMessage, null);
  }
  
  protected CorbaMessageMediator createResponseHelper(CorbaMessageMediator paramCorbaMessageMediator, ServiceContexts paramServiceContexts, boolean paramBoolean) {
    ReplyMessage replyMessage = MessageBase.createReply((ORB)paramCorbaMessageMediator.getBroker(), paramCorbaMessageMediator.getGIOPVersion(), paramCorbaMessageMediator.getEncodingVersion(), paramCorbaMessageMediator.getRequestId(), paramBoolean ? 1 : 2, paramServiceContexts, null);
    return createResponseHelper(paramCorbaMessageMediator, replyMessage, null);
  }
  
  protected CorbaMessageMediator createResponseHelper(CorbaMessageMediator paramCorbaMessageMediator, ReplyMessage paramReplyMessage, IOR paramIOR) {
    OutputObject outputObject1;
    runServantPostInvoke(paramCorbaMessageMediator);
    runInterceptors(paramCorbaMessageMediator, paramReplyMessage);
    runRemoveThreadInfo(paramCorbaMessageMediator);
    if (((ORB)paramCorbaMessageMediator.getBroker()).subcontractDebugFlag)
      dprint(".createResponseHelper: " + opAndId(paramCorbaMessageMediator) + ": " + paramReplyMessage); 
    paramCorbaMessageMediator.setReplyHeader(paramReplyMessage);
    if (paramCorbaMessageMediator.getConnection() == null) {
      outputObject1 = OutputStreamFactory.newCDROutputObject(this.orb, paramCorbaMessageMediator, paramCorbaMessageMediator.getReplyHeader(), paramCorbaMessageMediator.getStreamFormatVersion(), 0);
    } else {
      outputObject1 = paramCorbaMessageMediator.getConnection().getAcceptor().createOutputObject(paramCorbaMessageMediator.getBroker(), paramCorbaMessageMediator);
    } 
    paramCorbaMessageMediator.setOutputObject(outputObject1);
    paramCorbaMessageMediator.getOutputObject().setMessageMediator(paramCorbaMessageMediator);
    paramReplyMessage.write((OutputStream)paramCorbaMessageMediator.getOutputObject());
    if (paramReplyMessage.getIOR() != null)
      paramReplyMessage.getIOR().write((OutputStream)paramCorbaMessageMediator.getOutputObject()); 
    return paramCorbaMessageMediator;
  }
  
  protected void runServantPostInvoke(CorbaMessageMediator paramCorbaMessageMediator) {
    ORB oRB = null;
    if (paramCorbaMessageMediator.executeReturnServantInResponseConstructor()) {
      paramCorbaMessageMediator.setExecuteReturnServantInResponseConstructor(false);
      paramCorbaMessageMediator.setExecuteRemoveThreadInfoInResponseConstructor(true);
      try {
        oRB = (ORB)paramCorbaMessageMediator.getBroker();
        OAInvocationInfo oAInvocationInfo = oRB.peekInvocationInfo();
        objectAdapter = oAInvocationInfo.oa();
        try {
          objectAdapter.returnServant();
        } catch (Throwable throwable) {
          this.wrapper.unexpectedException(throwable);
          if (throwable instanceof Error)
            throw (Error)throwable; 
          if (throwable instanceof RuntimeException)
            throw (RuntimeException)throwable; 
        } finally {
          objectAdapter.exit();
        } 
      } catch (EmptyStackException emptyStackException) {
        throw this.wrapper.emptyStackRunServantPostInvoke(emptyStackException);
      } 
    } 
  }
  
  protected void runInterceptors(CorbaMessageMediator paramCorbaMessageMediator, ReplyMessage paramReplyMessage) {
    if (paramCorbaMessageMediator.executePIInResponseConstructor()) {
      ((ORB)paramCorbaMessageMediator.getBroker()).getPIHandler().invokeServerPIEndingPoint(paramReplyMessage);
      ((ORB)paramCorbaMessageMediator.getBroker()).getPIHandler().cleanupServerPIRequest();
      paramCorbaMessageMediator.setExecutePIInResponseConstructor(false);
    } 
  }
  
  protected void runRemoveThreadInfo(CorbaMessageMediator paramCorbaMessageMediator) {
    if (paramCorbaMessageMediator.executeRemoveThreadInfoInResponseConstructor()) {
      paramCorbaMessageMediator.setExecuteRemoveThreadInfoInResponseConstructor(false);
      ((ORB)paramCorbaMessageMediator.getBroker()).popInvocationInfo();
    } 
  }
  
  protected ServiceContexts getServiceContextsForReply(CorbaMessageMediator paramCorbaMessageMediator, ServiceContexts paramServiceContexts) {
    CorbaConnection corbaConnection = (CorbaConnection)paramCorbaMessageMediator.getConnection();
    if (((ORB)paramCorbaMessageMediator.getBroker()).subcontractDebugFlag)
      dprint(".getServiceContextsForReply: " + opAndId(paramCorbaMessageMediator) + ": " + corbaConnection); 
    if (paramServiceContexts == null)
      paramServiceContexts = new ServiceContexts((ORB)paramCorbaMessageMediator.getBroker()); 
    if (corbaConnection != null && !corbaConnection.isPostInitialContexts()) {
      corbaConnection.setPostInitialContexts();
      SendingContextServiceContext sendingContextServiceContext = new SendingContextServiceContext(((ORB)paramCorbaMessageMediator.getBroker()).getFVDCodeBaseIOR());
      if (paramServiceContexts.get(sendingContextServiceContext.getId()) != null)
        throw this.wrapper.duplicateSendingContextServiceContext(); 
      paramServiceContexts.put(sendingContextServiceContext);
      if (((ORB)paramCorbaMessageMediator.getBroker()).subcontractDebugFlag)
        dprint(".getServiceContextsForReply: " + opAndId(paramCorbaMessageMediator) + ": added SendingContextServiceContext"); 
    } 
    ORBVersionServiceContext oRBVersionServiceContext = new ORBVersionServiceContext(ORBVersionFactory.getORBVersion());
    if (paramServiceContexts.get(oRBVersionServiceContext.getId()) != null)
      throw this.wrapper.duplicateOrbVersionServiceContext(); 
    paramServiceContexts.put(oRBVersionServiceContext);
    if (((ORB)paramCorbaMessageMediator.getBroker()).subcontractDebugFlag)
      dprint(".getServiceContextsForReply: " + opAndId(paramCorbaMessageMediator) + ": added ORB version service context"); 
    return paramServiceContexts;
  }
  
  private void releaseByteBufferToPool() {
    if (this.dispatchByteBuffer != null) {
      this.orb.getByteBufferPool().releaseByteBuffer(this.dispatchByteBuffer);
      if (transportDebug()) {
        int i = System.identityHashCode(this.dispatchByteBuffer);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(".handleInput: releasing ByteBuffer (" + i + ") to ByteBufferPool");
        dprint(stringBuffer.toString());
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\CorbaMessageMediatorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */