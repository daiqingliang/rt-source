package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ReaderThread;
import com.sun.corba.se.pept.transport.Selector;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orbutil.threadpool.Work;

public class ReaderThreadImpl implements ReaderThread, Work {
  private ORB orb;
  
  private Connection connection;
  
  private Selector selector;
  
  private boolean keepRunning;
  
  private long enqueueTime;
  
  public ReaderThreadImpl(ORB paramORB, Connection paramConnection, Selector paramSelector) {
    this.orb = paramORB;
    this.connection = paramConnection;
    this.selector = paramSelector;
    this.keepRunning = true;
  }
  
  public Connection getConnection() { return this.connection; }
  
  public void close() {
    if (this.orb.transportDebugFlag)
      dprint(".close: " + this.connection); 
    this.keepRunning = false;
  }
  
  public void doWork() {
    try {
      if (this.orb.transportDebugFlag)
        dprint(".doWork: Start ReaderThread: " + this.connection); 
      while (this.keepRunning) {
        try {
          if (this.orb.transportDebugFlag)
            dprint(".doWork: Start ReaderThread cycle: " + this.connection); 
          if (this.connection.read())
            return; 
          if (this.orb.transportDebugFlag)
            dprint(".doWork: End ReaderThread cycle: " + this.connection); 
        } catch (Throwable throwable) {
          if (this.orb.transportDebugFlag)
            dprint(".doWork: exception in read: " + this.connection, throwable); 
          this.orb.getTransportManager().getSelector(0).unregisterForEvent(getConnection().getEventHandler());
          getConnection().close();
        } 
      } 
    } finally {
      if (this.orb.transportDebugFlag)
        dprint(".doWork: Terminated ReaderThread: " + this.connection); 
    } 
  }
  
  public void setEnqueueTime(long paramLong) { this.enqueueTime = paramLong; }
  
  public long getEnqueueTime() { return this.enqueueTime; }
  
  public String getName() { return "ReaderThread"; }
  
  private void dprint(String paramString) { ORBUtility.dprint("ReaderThreadImpl", paramString); }
  
  protected void dprint(String paramString, Throwable paramThrowable) {
    dprint(paramString);
    paramThrowable.printStackTrace(System.out);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\transport\ReaderThreadImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */