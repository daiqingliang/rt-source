package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpDefinitions;
import com.sun.jmx.snmp.SnmpMessage;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpPduFactory;
import com.sun.jmx.snmp.SnmpPduPacket;
import com.sun.jmx.snmp.SnmpPduRequest;
import com.sun.jmx.snmp.SnmpPduRequestType;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpTooBigException;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.SnmpVarBindList;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.logging.Level;

public class SnmpInformRequest implements SnmpDefinitions {
  private static SnmpRequestCounter requestCounter = new SnmpRequestCounter();
  
  private SnmpVarBindList varBindList = null;
  
  int errorStatus = 0;
  
  int errorIndex = 0;
  
  SnmpVarBind[] internalVarBind = null;
  
  String reason = null;
  
  private SnmpAdaptorServer adaptor;
  
  private SnmpSession informSession;
  
  private SnmpInformHandler callback = null;
  
  SnmpPdu requestPdu;
  
  SnmpPduRequestType responsePdu;
  
  private static final int stBase = 1;
  
  public static final int stInProgress = 1;
  
  public static final int stWaitingToSend = 3;
  
  public static final int stWaitingForReply = 5;
  
  public static final int stReceivedReply = 9;
  
  public static final int stAborted = 16;
  
  public static final int stTimeout = 32;
  
  public static final int stInternalError = 64;
  
  public static final int stResultsAvailable = 128;
  
  public static final int stNeverUsed = 256;
  
  private int numTries = 0;
  
  private int timeout = 3000;
  
  private int reqState = 256;
  
  private long prevPollTime = 0L;
  
  private long nextPollTime = 0L;
  
  private long waitTimeForResponse;
  
  private Date debugDate = new Date();
  
  private int requestId = 0;
  
  private int port = 0;
  
  private InetAddress address = null;
  
  private String communityString = null;
  
  SnmpInformRequest(SnmpSession paramSnmpSession, SnmpAdaptorServer paramSnmpAdaptorServer, InetAddress paramInetAddress, String paramString, int paramInt, SnmpInformHandler paramSnmpInformHandler) throws SnmpStatusException {
    this.informSession = paramSnmpSession;
    this.adaptor = paramSnmpAdaptorServer;
    this.address = paramInetAddress;
    this.communityString = paramString;
    this.port = paramInt;
    this.callback = paramSnmpInformHandler;
    this.informSession.addInformRequest(this);
    setTimeout(this.adaptor.getTimeout());
  }
  
  public final int getRequestId() { return this.requestId; }
  
  InetAddress getAddress() { return this.address; }
  
  public final int getRequestStatus() { return this.reqState; }
  
  public final boolean isAborted() { return ((this.reqState & 0x10) == 16); }
  
  public final boolean inProgress() { return ((this.reqState & true) == 1); }
  
  public final boolean isResultAvailable() { return (this.reqState == 128); }
  
  public final int getErrorStatus() { return this.errorStatus; }
  
  public final int getErrorIndex() { return this.errorIndex; }
  
  public final int getMaxTries() { return this.adaptor.getMaxTries(); }
  
  public final int getNumTries() { return this.numTries; }
  
  final void setTimeout(int paramInt) { this.timeout = paramInt; }
  
  public final long getAbsNextPollTime() { return this.nextPollTime; }
  
  public final long getAbsMaxTimeToWait() { return (this.prevPollTime == 0L) ? System.currentTimeMillis() : this.waitTimeForResponse; }
  
  public final SnmpVarBindList getResponseVarBindList() { return inProgress() ? null : this.varBindList; }
  
  public final boolean waitForCompletion(long paramLong) {
    if (!inProgress())
      return true; 
    if (this.informSession.thisSessionContext()) {
      SnmpInformHandler snmpInformHandler = this.callback;
      this.callback = null;
      this.informSession.waitForResponse(this, paramLong);
      this.callback = snmpInformHandler;
    } else {
      synchronized (this) {
        SnmpInformHandler snmpInformHandler = this.callback;
        try {
          this.callback = null;
          wait(paramLong);
        } catch (InterruptedException interruptedException) {}
        this.callback = snmpInformHandler;
      } 
    } 
    return !inProgress();
  }
  
  public final void cancelRequest() {
    this.errorStatus = 225;
    stopRequest();
    deleteRequest();
    notifyClient();
  }
  
  public final void notifyClient() { notifyAll(); }
  
  protected void finalize() {
    this.callback = null;
    this.varBindList = null;
    this.internalVarBind = null;
    this.adaptor = null;
    this.informSession = null;
    this.requestPdu = null;
    this.responsePdu = null;
  }
  
  public static String snmpErrorToString(int paramInt) {
    switch (paramInt) {
      case 0:
        return "noError";
      case 1:
        return "tooBig";
      case 2:
        return "noSuchName";
      case 3:
        return "badValue";
      case 4:
        return "readOnly";
      case 5:
        return "genErr";
      case 6:
        return "noAccess";
      case 7:
        return "wrongType";
      case 8:
        return "wrongLength";
      case 9:
        return "wrongEncoding";
      case 10:
        return "wrongValue";
      case 11:
        return "noCreation";
      case 12:
        return "inconsistentValue";
      case 13:
        return "resourceUnavailable";
      case 14:
        return "commitFailed";
      case 15:
        return "undoFailed";
      case 16:
        return "authorizationError";
      case 17:
        return "notWritable";
      case 18:
        return "inconsistentName";
      case 224:
        return "reqTimeout";
      case 225:
        return "reqAborted";
      case 226:
        return "rspDecodingError";
      case 227:
        return "reqEncodingError";
      case 228:
        return "reqPacketOverflow";
      case 229:
        return "rspEndOfTable";
      case 230:
        return "reqRefireAfterVbFix";
      case 231:
        return "reqHandleTooBig";
      case 232:
        return "reqTooBigImpossible";
      case 240:
        return "reqInternalError";
      case 241:
        return "reqSocketIOError";
      case 242:
        return "reqUnknownError";
      case 243:
        return "wrongSnmpVersion";
      case 244:
        return "snmpUnknownPrincipal";
      case 245:
        return "snmpAuthNotSupported";
      case 246:
        return "snmpPrivNotSupported";
      case 249:
        return "snmpBadSecurityLevel";
      case 247:
        return "snmpUsmBadEngineId";
      case 248:
        return "snmpUsmInvalidTimeliness";
    } 
    return "Unknown Error = " + paramInt;
  }
  
  void start(SnmpVarBindList paramSnmpVarBindList) throws SnmpStatusException {
    if (inProgress())
      throw new SnmpStatusException("Inform request already in progress."); 
    setVarBindList(paramSnmpVarBindList);
    initializeAndFire();
  }
  
  private void initializeAndFire() {
    this.requestPdu = null;
    this.responsePdu = null;
    this.reason = null;
    startRequest(System.currentTimeMillis());
    setErrorStatusAndIndex(0, 0);
  }
  
  private void startRequest(long paramLong) {
    this.nextPollTime = paramLong;
    this.prevPollTime = 0L;
    schedulePoll();
  }
  
  private void schedulePoll() {
    this.numTries = 0;
    initNewRequest();
    setRequestStatus(3);
    this.informSession.getSnmpQManager().addRequest(this);
  }
  
  void action() {
    if (!inProgress())
      return; 
    while (true) {
      try {
        if (this.numTries == 0) {
          invokeOnReady();
        } else if (this.numTries < getMaxTries()) {
          invokeOnRetry();
        } else {
          invokeOnTimeout();
        } 
        return;
      } catch (OutOfMemoryError outOfMemoryError) {
        this.numTries++;
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "action", "Inform request hit out of memory situation..."); 
        Thread.yield();
      } 
    } 
  }
  
  private void invokeOnReady() {
    if (this.requestPdu == null)
      this.requestPdu = constructPduPacket(); 
    if (this.requestPdu != null && !sendPdu())
      queueResponse(); 
  }
  
  private void invokeOnRetry() { invokeOnReady(); }
  
  private void invokeOnTimeout() {
    this.errorStatus = 224;
    queueResponse();
  }
  
  private void queueResponse() { this.informSession.addResponse(this); }
  
  SnmpPdu constructPduPacket() {
    SnmpPduRequest snmpPduRequest = null;
    Exception exception = null;
    try {
      snmpPduRequest = new SnmpPduRequest();
      snmpPduRequest.port = this.port;
      snmpPduRequest.type = 166;
      snmpPduRequest.version = 1;
      snmpPduRequest.community = this.communityString.getBytes("8859_1");
      snmpPduRequest.requestId = getRequestId();
      snmpPduRequest.varBindList = this.internalVarBind;
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpInformRequest.class.getName(), "constructPduPacket", "Packet built"); 
    } catch (Exception exception1) {
      exception = exception1;
      this.errorStatus = 242;
      this.reason = exception1.getMessage();
    } 
    if (exception != null) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "constructPduPacket", "Got unexpected exception", exception); 
      snmpPduRequest = null;
      queueResponse();
    } 
    return snmpPduRequest;
  }
  
  boolean sendPdu() {
    try {
      this.responsePdu = null;
      SnmpPduFactory snmpPduFactory = this.adaptor.getPduFactory();
      SnmpMessage snmpMessage = (SnmpMessage)snmpPduFactory.encodeSnmpPdu((SnmpPduPacket)this.requestPdu, this.adaptor.getBufferSize().intValue());
      if (snmpMessage == null) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "sendPdu", "pdu factory returned a null value"); 
        throw new SnmpStatusException(242);
      } 
      int i = this.adaptor.getBufferSize().intValue();
      byte[] arrayOfByte = new byte[i];
      int j = snmpMessage.encodeMessage(arrayOfByte);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpInformRequest.class.getName(), "sendPdu", "Dump : \n" + snmpMessage.printMessage()); 
      sendPduPacket(arrayOfByte, j);
      return true;
    } catch (SnmpTooBigException snmpTooBigException) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "sendPdu", "Got unexpected exception", snmpTooBigException); 
      setErrorStatusAndIndex(228, snmpTooBigException.getVarBindCount());
      this.requestPdu = null;
      this.reason = snmpTooBigException.getMessage();
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "sendPdu", "Packet Overflow while building inform request"); 
    } catch (IOException iOException) {
      setErrorStatusAndIndex(241, 0);
      this.reason = iOException.getMessage();
    } catch (Exception exception) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "sendPdu", "Got unexpected exception", exception); 
      setErrorStatusAndIndex(242, 0);
      this.reason = exception.getMessage();
    } 
    return false;
  }
  
  final void sendPduPacket(byte[] paramArrayOfByte, int paramInt) throws IOException {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpInformRequest.class.getName(), "sendPduPacket", "Send to peer. Peer/Port : " + this.address.getHostName() + "/" + this.port + ". Length = " + paramInt + "\nDump : \n" + SnmpMessage.dumpHexBuffer(paramArrayOfByte, 0, paramInt)); 
    SnmpSocket snmpSocket = this.informSession.getSocket();
    synchronized (snmpSocket) {
      snmpSocket.sendPacket(paramArrayOfByte, paramInt, this.address, this.port);
      setRequestSentTime(System.currentTimeMillis());
    } 
  }
  
  final void processResponse() {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpInformRequest.class.getName(), "processResponse", "errstatus = " + this.errorStatus); 
    if (!inProgress()) {
      this.responsePdu = null;
      return;
    } 
    if (this.errorStatus >= 240) {
      handleInternalError("Internal Error...");
      return;
    } 
    try {
      parsePduPacket(this.responsePdu);
      switch (this.errorStatus) {
        case 0:
          handleSuccess();
          return;
        case 224:
          handleTimeout();
          return;
        case 240:
          handleInternalError("Unknown internal error.  deal with it later!");
          return;
        case 231:
          setErrorStatusAndIndex(1, 0);
          handleError("Cannot handle too-big situation...");
          return;
        case 230:
          initializeAndFire();
          return;
      } 
      handleError("Error status set in packet...!!");
      return;
    } catch (Exception exception) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "processResponse", "Got unexpected exception", exception); 
      this.reason = exception.getMessage();
      handleInternalError(this.reason);
      return;
    } 
  }
  
  void parsePduPacket(SnmpPduRequestType paramSnmpPduRequestType) {
    if (paramSnmpPduRequestType == null)
      return; 
    this.errorStatus = paramSnmpPduRequestType.getErrorStatus();
    this.errorIndex = paramSnmpPduRequestType.getErrorIndex();
    if (this.errorStatus == 0) {
      updateInternalVarBindWithResult(((SnmpPdu)paramSnmpPduRequestType).varBindList);
      return;
    } 
    if (this.errorStatus != 0)
      this.errorIndex--; 
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpInformRequest.class.getName(), "parsePduPacket", "received inform response. ErrorStatus/ErrorIndex = " + this.errorStatus + "/" + this.errorIndex); 
  }
  
  private void handleSuccess() {
    setRequestStatus(128);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpInformRequest.class.getName(), "handleSuccess", "Invoking user defined callback..."); 
    deleteRequest();
    notifyClient();
    this.requestPdu = null;
    this.internalVarBind = null;
    try {
      if (this.callback != null)
        this.callback.processSnmpPollData(this, this.errorStatus, this.errorIndex, getVarBindList()); 
    } catch (Exception exception) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleSuccess", "Exception generated by user callback", exception); 
    } catch (OutOfMemoryError outOfMemoryError) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleSuccess", "OutOfMemory Error generated by user callback", outOfMemoryError); 
      Thread.yield();
    } 
  }
  
  private void handleTimeout() {
    setRequestStatus(32);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleTimeout", "Snmp error/index = " + snmpErrorToString(this.errorStatus) + "/" + this.errorIndex + ". Invoking timeout user defined callback..."); 
    deleteRequest();
    notifyClient();
    this.requestPdu = null;
    this.responsePdu = null;
    this.internalVarBind = null;
    try {
      if (this.callback != null)
        this.callback.processSnmpPollTimeout(this); 
    } catch (Exception exception) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleTimeout", "Exception generated by user callback", exception); 
    } catch (OutOfMemoryError outOfMemoryError) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleTimeout", "OutOfMemory Error generated by user callback", outOfMemoryError); 
      Thread.yield();
    } 
  }
  
  private void handleError(String paramString) {
    setRequestStatus(128);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleError", "Snmp error/index = " + snmpErrorToString(this.errorStatus) + "/" + this.errorIndex + ". Invoking error user defined callback...\n" + getVarBindList()); 
    deleteRequest();
    notifyClient();
    this.requestPdu = null;
    this.responsePdu = null;
    this.internalVarBind = null;
    try {
      if (this.callback != null)
        this.callback.processSnmpPollData(this, getErrorStatus(), getErrorIndex(), getVarBindList()); 
    } catch (Exception exception) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleError", "Exception generated by user callback", exception); 
    } catch (OutOfMemoryError outOfMemoryError) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleError", "OutOfMemory Error generated by user callback", outOfMemoryError); 
      Thread.yield();
    } 
  }
  
  private void handleInternalError(String paramString) {
    setRequestStatus(64);
    if (this.reason == null)
      this.reason = paramString; 
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleInternalError", "Snmp error/index = " + snmpErrorToString(this.errorStatus) + "/" + this.errorIndex + ". Invoking internal error user defined callback...\n" + getVarBindList()); 
    deleteRequest();
    notifyClient();
    this.requestPdu = null;
    this.responsePdu = null;
    this.internalVarBind = null;
    try {
      if (this.callback != null)
        this.callback.processSnmpInternalError(this, this.reason); 
    } catch (Exception exception) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleInternalError", "Exception generated by user callback", exception); 
    } catch (OutOfMemoryError outOfMemoryError) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpInformRequest.class.getName(), "handleInternalError", "OutOfMemory Error generated by user callback", outOfMemoryError); 
      Thread.yield();
    } 
  }
  
  void updateInternalVarBindWithResult(SnmpVarBind[] paramArrayOfSnmpVarBind) {
    if (paramArrayOfSnmpVarBind == null || paramArrayOfSnmpVarBind.length == 0)
      return; 
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.internalVarBind.length && b1 < paramArrayOfSnmpVarBind.length; b2++) {
      SnmpVarBind snmpVarBind = this.internalVarBind[b2];
      if (snmpVarBind != null) {
        SnmpVarBind snmpVarBind1 = paramArrayOfSnmpVarBind[b1];
        snmpVarBind.setSnmpValue(snmpVarBind1.getSnmpValue());
        b1++;
      } 
    } 
  }
  
  final void invokeOnResponse(Object paramObject) {
    if (paramObject != null)
      if (paramObject instanceof SnmpPduRequestType) {
        this.responsePdu = (SnmpPduRequestType)paramObject;
      } else {
        return;
      }  
    setRequestStatus(9);
    queueResponse();
  }
  
  private void stopRequest() {
    synchronized (this) {
      setRequestStatus(16);
    } 
    this.informSession.getSnmpQManager().removeRequest(this);
    synchronized (this) {
      this.requestId = 0;
    } 
  }
  
  final void deleteRequest() { this.informSession.removeInformRequest(this); }
  
  final SnmpVarBindList getVarBindList() { return this.varBindList; }
  
  final void setVarBindList(SnmpVarBindList paramSnmpVarBindList) throws SnmpStatusException {
    this.varBindList = paramSnmpVarBindList;
    if (this.internalVarBind == null || this.internalVarBind.length != this.varBindList.size())
      this.internalVarBind = new SnmpVarBind[this.varBindList.size()]; 
    this.varBindList.copyInto(this.internalVarBind);
  }
  
  final void setErrorStatusAndIndex(int paramInt1, int paramInt2) {
    this.errorStatus = paramInt1;
    this.errorIndex = paramInt2;
  }
  
  final void setPrevPollTime(long paramLong) { this.prevPollTime = paramLong; }
  
  final void setRequestSentTime(long paramLong) {
    this.numTries++;
    setPrevPollTime(paramLong);
    this.waitTimeForResponse = this.prevPollTime + (this.timeout * this.numTries);
    setRequestStatus(5);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpInformRequest.class.getName(), "setRequestSentTime", "Inform request Successfully sent"); 
    this.informSession.getSnmpQManager().addWaiting(this);
  }
  
  final void initNewRequest() { this.requestId = requestCounter.getNewId(); }
  
  long timeRemainingForAction(long paramLong) {
    switch (this.reqState) {
      case 3:
        return this.nextPollTime - paramLong;
      case 5:
        return this.waitTimeForResponse - paramLong;
    } 
    return -1L;
  }
  
  static String statusDescription(int paramInt) {
    switch (paramInt) {
      case 3:
        return "Waiting to send.";
      case 5:
        return "Waiting for reply.";
      case 9:
        return "Response arrived.";
      case 16:
        return "Aborted by user.";
      case 32:
        return "Timeout Occured.";
      case 64:
        return "Internal error.";
      case 128:
        return "Results available";
      case 256:
        return "Inform request in createAndWait state";
    } 
    return "Unknown inform request state.";
  }
  
  final void setRequestStatus(int paramInt) { this.reqState = paramInt; }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer(300);
    stringBuffer.append(tostring());
    stringBuffer.append("\nPeer/Port : " + this.address.getHostName() + "/" + this.port);
    return stringBuffer.toString();
  }
  
  private String tostring() {
    StringBuffer stringBuffer = new StringBuffer("InformRequestId = " + this.requestId);
    stringBuffer.append("   Status = " + statusDescription(this.reqState));
    stringBuffer.append("  Timeout/MaxTries/NumTries = " + (this.timeout * this.numTries) + "/" + getMaxTries() + "/" + this.numTries);
    if (this.prevPollTime > 0L) {
      this.debugDate.setTime(this.prevPollTime);
      stringBuffer.append("\nPrevPolled = " + this.debugDate.toString());
    } else {
      stringBuffer.append("\nNeverPolled");
    } 
    stringBuffer.append(" / RemainingTime(millis) = " + timeRemainingForAction(System.currentTimeMillis()));
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\daemon\SnmpInformRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */