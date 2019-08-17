package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import javax.management.MBeanServer;
import javax.management.ObjectName;

abstract class ClientHandler implements Runnable {
  protected CommunicatorServer adaptorServer = null;
  
  protected int requestId = -1;
  
  protected MBeanServer mbs = null;
  
  protected ObjectName objectName = null;
  
  protected Thread thread = null;
  
  protected boolean interruptCalled = false;
  
  protected String dbgTag = null;
  
  public ClientHandler(CommunicatorServer paramCommunicatorServer, int paramInt, MBeanServer paramMBeanServer, ObjectName paramObjectName) {
    this.adaptorServer = paramCommunicatorServer;
    this.requestId = paramInt;
    this.mbs = paramMBeanServer;
    this.objectName = paramObjectName;
    this.interruptCalled = false;
    this.dbgTag = makeDebugTag();
    this.thread = createThread(this);
  }
  
  Thread createThread(Runnable paramRunnable) { return new Thread(this); }
  
  public void interrupt() {
    JmxProperties.SNMP_ADAPTOR_LOGGER.entering(this.dbgTag, "interrupt");
    this.interruptCalled = true;
    if (this.thread != null)
      this.thread.interrupt(); 
    JmxProperties.SNMP_ADAPTOR_LOGGER.exiting(this.dbgTag, "interrupt");
  }
  
  public void join() {
    if (this.thread != null)
      try {
        this.thread.join();
      } catch (InterruptedException interruptedException) {} 
  }
  
  public void run() {
    try {
      this.adaptorServer.notifyClientHandlerCreated(this);
      doRun();
    } finally {
      this.adaptorServer.notifyClientHandlerDeleted(this);
    } 
  }
  
  public abstract void doRun();
  
  protected String makeDebugTag() { return "ClientHandler[" + this.adaptorServer.getProtocol() + ":" + this.adaptorServer.getPort() + "][" + this.requestId + "]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\daemon\ClientHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */