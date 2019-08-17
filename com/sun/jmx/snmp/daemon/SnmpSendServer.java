package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;

final class SnmpSendServer extends Thread {
  private int intervalRange = 5000;
  
  private Vector<SnmpInformRequest> readyPool;
  
  SnmpQManager snmpq = null;
  
  boolean isBeingDestroyed = false;
  
  public SnmpSendServer(ThreadGroup paramThreadGroup, SnmpQManager paramSnmpQManager) {
    super(paramThreadGroup, "SnmpSendServer");
    this.snmpq = paramSnmpQManager;
    start();
  }
  
  public void stopSendServer() {
    if (isAlive()) {
      interrupt();
      try {
        join();
      } catch (InterruptedException interruptedException) {}
    } 
  }
  
  public void run() {
    Thread.currentThread().setPriority(5);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSendServer.class.getName(), "run", "Thread Started"); 
    while (true) {
      try {
        prepareAndSendRequest();
        if (this.isBeingDestroyed == true)
          break; 
      } catch (Exception exception) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSendServer.class.getName(), "run", "Exception in send server", exception); 
      } catch (ThreadDeath threadDeath) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSendServer.class.getName(), "run", "Exiting... Fatal error"); 
        throw threadDeath;
      } catch (OutOfMemoryError outOfMemoryError) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSendServer.class.getName(), "run", "Out of memory"); 
      } catch (Error error) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSendServer.class.getName(), "run", "Got unexpected error", error); 
        throw error;
      } 
    } 
  }
  
  private void prepareAndSendRequest() {
    if (this.readyPool == null || this.readyPool.isEmpty()) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSendServer.class.getName(), "prepareAndSendRequest", "Blocking for inform requests"); 
      this.readyPool = this.snmpq.getAllOutstandingRequest(this.intervalRange);
      if (this.isBeingDestroyed == true)
        return; 
    } else if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSendServer.class.getName(), "prepareAndSendRequest", "Inform requests from a previous block left unprocessed. Will try again");
    } 
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSendServer.class.getName(), "prepareAndSendRequest", "List of inform requests to send : " + reqListToString(this.readyPool)); 
    synchronized (this) {
      if (this.readyPool.size() < 2) {
        fireRequestList(this.readyPool);
        return;
      } 
      while (!this.readyPool.isEmpty()) {
        SnmpInformRequest snmpInformRequest = (SnmpInformRequest)this.readyPool.lastElement();
        if (snmpInformRequest != null && snmpInformRequest.inProgress())
          fireRequest(snmpInformRequest); 
        this.readyPool.removeElementAt(this.readyPool.size() - 1);
      } 
      this.readyPool.removeAllElements();
    } 
  }
  
  private void fireRequest(SnmpInformRequest paramSnmpInformRequest) {
    if (paramSnmpInformRequest != null && paramSnmpInformRequest.inProgress()) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSendServer.class.getName(), "fireRequest", "Firing inform request directly. -> " + paramSnmpInformRequest.getRequestId()); 
      paramSnmpInformRequest.action();
    } 
  }
  
  private void fireRequestList(Vector<SnmpInformRequest> paramVector) {
    while (!paramVector.isEmpty()) {
      SnmpInformRequest snmpInformRequest = (SnmpInformRequest)paramVector.lastElement();
      if (snmpInformRequest != null && snmpInformRequest.inProgress())
        fireRequest(snmpInformRequest); 
      paramVector.removeElementAt(paramVector.size() - 1);
    } 
  }
  
  private final String reqListToString(Vector<SnmpInformRequest> paramVector) {
    StringBuilder stringBuilder = new StringBuilder(paramVector.size() * 100);
    Enumeration enumeration = paramVector.elements();
    while (enumeration.hasMoreElements()) {
      SnmpInformRequest snmpInformRequest = (SnmpInformRequest)enumeration.nextElement();
      stringBuilder.append("InformRequestId -> ");
      stringBuilder.append(snmpInformRequest.getRequestId());
      stringBuilder.append(" / Destination -> ");
      stringBuilder.append(snmpInformRequest.getAddress());
      stringBuilder.append(". ");
    } 
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\daemon\SnmpSendServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */