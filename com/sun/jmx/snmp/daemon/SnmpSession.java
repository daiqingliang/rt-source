package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpDefinitions;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBindList;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Stack;
import java.util.logging.Level;

class SnmpSession implements SnmpDefinitions, Runnable {
  protected SnmpAdaptorServer adaptor;
  
  protected SnmpSocket informSocket = null;
  
  private Hashtable<SnmpInformRequest, SnmpInformRequest> informRequestList = new Hashtable();
  
  private Stack<SnmpInformRequest> informRespq = new Stack();
  
  private Thread myThread = null;
  
  private SnmpInformRequest syncInformReq;
  
  SnmpQManager snmpQman = null;
  
  private boolean isBeingCancelled = false;
  
  public SnmpSession(SnmpAdaptorServer paramSnmpAdaptorServer) throws SocketException {
    this.adaptor = paramSnmpAdaptorServer;
    this.snmpQman = new SnmpQManager();
    SnmpResponseHandler snmpResponseHandler = new SnmpResponseHandler(paramSnmpAdaptorServer, this.snmpQman);
    initialize(paramSnmpAdaptorServer, snmpResponseHandler);
  }
  
  public SnmpSession() throws SocketException {}
  
  protected void initialize(SnmpAdaptorServer paramSnmpAdaptorServer, SnmpResponseHandler paramSnmpResponseHandler) throws SocketException {
    this.informSocket = new SnmpSocket(paramSnmpResponseHandler, paramSnmpAdaptorServer.getAddress(), paramSnmpAdaptorServer.getBufferSize().intValue());
    this.myThread = new Thread(this, "SnmpSession");
    this.myThread.start();
  }
  
  boolean isSessionActive() { return (this.adaptor.isActive() && this.myThread != null && this.myThread.isAlive()); }
  
  SnmpSocket getSocket() { return this.informSocket; }
  
  SnmpQManager getSnmpQManager() { return this.snmpQman; }
  
  private boolean syncInProgress() { return (this.syncInformReq != null); }
  
  private void setSyncMode(SnmpInformRequest paramSnmpInformRequest) { this.syncInformReq = paramSnmpInformRequest; }
  
  private void resetSyncMode() throws SocketException {
    if (this.syncInformReq == null)
      return; 
    this.syncInformReq = null;
    if (thisSessionContext())
      return; 
    notifyAll();
  }
  
  boolean thisSessionContext() { return (Thread.currentThread() == this.myThread); }
  
  SnmpInformRequest makeAsyncRequest(InetAddress paramInetAddress, String paramString, SnmpInformHandler paramSnmpInformHandler, SnmpVarBindList paramSnmpVarBindList, int paramInt) throws SnmpStatusException {
    if (!isSessionActive())
      throw new SnmpStatusException("SNMP adaptor server not ONLINE"); 
    SnmpInformRequest snmpInformRequest = new SnmpInformRequest(this, this.adaptor, paramInetAddress, paramString, paramInt, paramSnmpInformHandler);
    snmpInformRequest.start(paramSnmpVarBindList);
    return snmpInformRequest;
  }
  
  void waitForResponse(SnmpInformRequest paramSnmpInformRequest, long paramLong) {
    long l;
    if (!paramSnmpInformRequest.inProgress())
      return; 
    setSyncMode(paramSnmpInformRequest);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSession.class.getName(), "waitForResponse", "Session switching to sync mode for inform request " + paramSnmpInformRequest.getRequestId()); 
    if (paramLong <= 0L) {
      l = System.currentTimeMillis() + 6000000L;
    } else {
      l = System.currentTimeMillis() + paramLong;
    } 
    while (paramSnmpInformRequest.inProgress() || syncInProgress()) {
      paramLong = l - System.currentTimeMillis();
      if (paramLong <= 0L)
        break; 
      synchronized (this) {
        if (!this.informRespq.removeElement(paramSnmpInformRequest)) {
          try {
            wait(paramLong);
          } catch (InterruptedException interruptedException) {}
          continue;
        } 
      } 
      try {
        processResponse(paramSnmpInformRequest);
      } catch (Exception exception) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSession.class.getName(), "waitForResponse", "Got unexpected exception", exception); 
      } 
    } 
    resetSyncMode();
  }
  
  public void run() throws SocketException {
    this.myThread = Thread.currentThread();
    this.myThread.setPriority(5);
    SnmpInformRequest snmpInformRequest = null;
    while (this.myThread != null) {
      try {
        snmpInformRequest = nextResponse();
        if (snmpInformRequest != null)
          processResponse(snmpInformRequest); 
      } catch (ThreadDeath threadDeath) {
        this.myThread = null;
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSession.class.getName(), "run", "ThreadDeath, session thread unexpectedly shutting down"); 
        throw threadDeath;
      } 
    } 
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSession.class.getName(), "run", "Session thread shutting down"); 
    this.myThread = null;
  }
  
  private void processResponse(SnmpInformRequest paramSnmpInformRequest) {
    while (paramSnmpInformRequest != null && this.myThread != null) {
      try {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSession.class.getName(), "processResponse", "Processing response to req = " + paramSnmpInformRequest.getRequestId()); 
        paramSnmpInformRequest.processResponse();
        paramSnmpInformRequest = null;
      } catch (Exception exception) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSession.class.getName(), "processResponse", "Got unexpected exception", exception); 
        paramSnmpInformRequest = null;
      } catch (OutOfMemoryError outOfMemoryError) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSession.class.getName(), "processResponse", "Out of memory error in session thread", outOfMemoryError); 
        Thread.yield();
      } 
    } 
  }
  
  void addInformRequest(SnmpInformRequest paramSnmpInformRequest) {
    if (!isSessionActive())
      throw new SnmpStatusException("SNMP adaptor is not ONLINE or session is dead..."); 
    this.informRequestList.put(paramSnmpInformRequest, paramSnmpInformRequest);
  }
  
  void removeInformRequest(SnmpInformRequest paramSnmpInformRequest) {
    if (!this.isBeingCancelled)
      this.informRequestList.remove(paramSnmpInformRequest); 
    if (this.syncInformReq != null && this.syncInformReq == paramSnmpInformRequest)
      resetSyncMode(); 
  }
  
  private void cancelAllRequests() throws SocketException {
    SnmpInformRequest[] arrayOfSnmpInformRequest;
    synchronized (this) {
      if (this.informRequestList.isEmpty())
        return; 
      this.isBeingCancelled = true;
      arrayOfSnmpInformRequest = new SnmpInformRequest[this.informRequestList.size()];
      Iterator iterator = this.informRequestList.values().iterator();
      byte b1 = 0;
      while (iterator.hasNext()) {
        SnmpInformRequest snmpInformRequest = (SnmpInformRequest)iterator.next();
        arrayOfSnmpInformRequest[b1++] = snmpInformRequest;
        iterator.remove();
      } 
      this.informRequestList.clear();
    } 
    for (byte b = 0; b < arrayOfSnmpInformRequest.length; b++)
      arrayOfSnmpInformRequest[b].cancelRequest(); 
  }
  
  void addResponse(SnmpInformRequest paramSnmpInformRequest) {
    SnmpInformRequest snmpInformRequest = paramSnmpInformRequest;
    if (isSessionActive()) {
      synchronized (this) {
        this.informRespq.push(paramSnmpInformRequest);
        notifyAll();
      } 
    } else if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSession.class.getName(), "addResponse", "Adaptor not ONLINE or session thread dead, so inform response is dropped..." + paramSnmpInformRequest.getRequestId());
    } 
  }
  
  private SnmpInformRequest nextResponse() {
    if (this.informRespq.isEmpty())
      try {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSession.class.getName(), "nextResponse", "Blocking for response"); 
        wait();
      } catch (InterruptedException interruptedException) {} 
    if (this.informRespq.isEmpty())
      return null; 
    SnmpInformRequest snmpInformRequest = (SnmpInformRequest)this.informRespq.firstElement();
    this.informRespq.removeElementAt(0);
    return snmpInformRequest;
  }
  
  private void cancelAllResponses() throws SocketException {
    if (this.informRespq != null) {
      this.syncInformReq = null;
      this.informRespq.removeAllElements();
      notifyAll();
    } 
  }
  
  final void destroySession() throws SocketException {
    cancelAllRequests();
    cancelAllResponses();
    synchronized (this) {
      this.informSocket.close();
      this.informSocket = null;
    } 
    this.snmpQman.stopQThreads();
    this.snmpQman = null;
    killSessionThread();
  }
  
  private void killSessionThread() throws SocketException {
    if (this.myThread != null && this.myThread.isAlive()) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSession.class.getName(), "killSessionThread", "Destroying session"); 
      if (!thisSessionContext()) {
        this.myThread = null;
        notifyAll();
      } else {
        this.myThread = null;
      } 
    } 
  }
  
  protected void finalize() throws SocketException {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSession.class.getName(), "finalize", "Shutting all servers"); 
    if (this.informRespq != null)
      this.informRespq.removeAllElements(); 
    this.informRespq = null;
    if (this.informSocket != null)
      this.informSocket.close(); 
    this.informSocket = null;
    this.snmpQman = null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\daemon\SnmpSession.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */