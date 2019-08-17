package com.sun.jmx.snmp.daemon;

import java.io.Serializable;
import java.util.Vector;

final class SnmpQManager implements Serializable {
  private static final long serialVersionUID = 2163709017015248264L;
  
  private SendQ newq = new SendQ(20, 5);
  
  private WaitQ waitq = new WaitQ(20, 5);
  
  private ThreadGroup queueThreadGroup = null;
  
  private Thread requestQThread = null;
  
  private Thread timerQThread = null;
  
  SnmpQManager() {
    this.queueThreadGroup = new ThreadGroup("Qmanager Thread Group");
    startQThreads();
  }
  
  public void startQThreads() {
    if (this.timerQThread == null || !this.timerQThread.isAlive())
      this.timerQThread = new SnmpTimerServer(this.queueThreadGroup, this); 
    if (this.requestQThread == null || !this.requestQThread.isAlive())
      this.requestQThread = new SnmpSendServer(this.queueThreadGroup, this); 
  }
  
  public void stopQThreads() {
    ((SnmpTimerServer)this.timerQThread).isBeingDestroyed = true;
    this.waitq.isBeingDestroyed = true;
    ((SnmpSendServer)this.requestQThread).isBeingDestroyed = true;
    this.newq.isBeingDestroyed = true;
    if (this.timerQThread != null && this.timerQThread.isAlive() == true)
      ((SnmpTimerServer)this.timerQThread).stopTimerServer(); 
    this.waitq = null;
    this.timerQThread = null;
    if (this.requestQThread != null && this.requestQThread.isAlive() == true)
      ((SnmpSendServer)this.requestQThread).stopSendServer(); 
    this.newq = null;
    this.requestQThread = null;
  }
  
  public void addRequest(SnmpInformRequest paramSnmpInformRequest) { this.newq.addRequest(paramSnmpInformRequest); }
  
  public void addWaiting(SnmpInformRequest paramSnmpInformRequest) { this.waitq.addWaiting(paramSnmpInformRequest); }
  
  public Vector<SnmpInformRequest> getAllOutstandingRequest(long paramLong) { return this.newq.getAllOutstandingRequest(paramLong); }
  
  public SnmpInformRequest getTimeoutRequests() { return this.waitq.getTimeoutRequests(); }
  
  public void removeRequest(SnmpInformRequest paramSnmpInformRequest) {
    this.newq.removeElement(paramSnmpInformRequest);
    this.waitq.removeElement(paramSnmpInformRequest);
  }
  
  public SnmpInformRequest removeRequest(long paramLong) {
    SnmpInformRequest snmpInformRequest;
    if ((snmpInformRequest = this.newq.removeRequest(paramLong)) == null)
      snmpInformRequest = this.waitq.removeRequest(paramLong); 
    return snmpInformRequest;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\daemon\SnmpQManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */