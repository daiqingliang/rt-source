package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import java.util.Vector;
import java.util.logging.Level;

class SendQ extends Vector<SnmpInformRequest> {
  boolean isBeingDestroyed = false;
  
  SendQ(int paramInt1, int paramInt2) { super(paramInt1, paramInt2); }
  
  private void notifyClients() { notifyAll(); }
  
  public void addRequest(SnmpInformRequest paramSnmpInformRequest) {
    long l = paramSnmpInformRequest.getAbsNextPollTime();
    int i;
    for (i = size(); i > 0 && l >= getRequestAt(i - 1).getAbsNextPollTime(); i--);
    if (i == size()) {
      addElement(paramSnmpInformRequest);
      notifyClients();
    } else {
      insertElementAt(paramSnmpInformRequest, i);
    } 
  }
  
  public boolean waitUntilReady() {
    while (true) {
      if (this.isBeingDestroyed == true)
        return false; 
      long l = 0L;
      if (!isEmpty()) {
        long l1 = System.currentTimeMillis();
        SnmpInformRequest snmpInformRequest = (SnmpInformRequest)lastElement();
        l = snmpInformRequest.getAbsNextPollTime() - l1;
        if (l <= 0L)
          return true; 
      } 
      waitOnThisQueue(l);
    } 
  }
  
  public Vector<SnmpInformRequest> getAllOutstandingRequest(long paramLong) {
    Vector vector = new Vector();
    while (waitUntilReady() == true) {
      long l = System.currentTimeMillis() + paramLong;
      for (int i = size(); i > 0; i--) {
        SnmpInformRequest snmpInformRequest = getRequestAt(i - 1);
        if (snmpInformRequest.getAbsNextPollTime() > l)
          break; 
        vector.addElement(snmpInformRequest);
      } 
      if (!vector.isEmpty()) {
        this.elementCount -= vector.size();
        return vector;
      } 
    } 
    return null;
  }
  
  public void waitOnThisQueue(long paramLong) {
    if (paramLong == 0L && !isEmpty() && JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpQManager.class.getName(), "waitOnThisQueue", "[" + Thread.currentThread().toString() + "]:Fatal BUG :: Blocking on newq permenantly. But size = " + size()); 
    try {
      wait(paramLong);
    } catch (InterruptedException interruptedException) {}
  }
  
  public SnmpInformRequest getRequestAt(int paramInt) { return (SnmpInformRequest)elementAt(paramInt); }
  
  public SnmpInformRequest removeRequest(long paramLong) {
    int i = size();
    for (byte b = 0; b < i; b++) {
      SnmpInformRequest snmpInformRequest = getRequestAt(b);
      if (paramLong == snmpInformRequest.getRequestId()) {
        removeElementAt(b);
        return snmpInformRequest;
      } 
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\daemon\SendQ.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */