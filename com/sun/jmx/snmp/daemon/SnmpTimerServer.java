package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import java.util.logging.Level;

final class SnmpTimerServer extends Thread {
  private SnmpInformRequest req = null;
  
  SnmpQManager snmpq = null;
  
  boolean isBeingDestroyed = false;
  
  public SnmpTimerServer(ThreadGroup paramThreadGroup, SnmpQManager paramSnmpQManager) {
    super(paramThreadGroup, "SnmpTimerServer");
    setName("SnmpTimerServer");
    this.snmpq = paramSnmpQManager;
    start();
  }
  
  public void stopTimerServer() {
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
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpTimerServer.class.getName(), "run", "Timer Thread started"); 
    while (true) {
      try {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpTimerServer.class.getName(), "run", "Blocking for inform requests"); 
        if (this.req == null)
          this.req = this.snmpq.getTimeoutRequests(); 
        if (this.req != null && this.req.inProgress()) {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpTimerServer.class.getName(), "run", "Handle timeout inform request " + this.req.getRequestId()); 
          this.req.action();
          this.req = null;
        } 
        if (this.isBeingDestroyed == true)
          break; 
      } catch (Exception exception) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpTimerServer.class.getName(), "run", "Got unexpected exception", exception); 
      } catch (ThreadDeath threadDeath) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpTimerServer.class.getName(), "run", "ThreadDeath, timer server unexpectedly shutting down", threadDeath); 
        throw threadDeath;
      } catch (OutOfMemoryError outOfMemoryError) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpTimerServer.class.getName(), "run", "OutOfMemoryError", outOfMemoryError); 
        yield();
      } catch (Error error) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpTimerServer.class.getName(), "run", "Received Internal error", error); 
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\daemon\SnmpTimerServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */