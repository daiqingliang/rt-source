package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.pept.transport.ListenerThread;
import com.sun.corba.se.pept.transport.Selector;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orbutil.threadpool.Work;

public class ListenerThreadImpl implements ListenerThread, Work {
  private ORB orb;
  
  private Acceptor acceptor;
  
  private Selector selector;
  
  private boolean keepRunning;
  
  private long enqueueTime;
  
  public ListenerThreadImpl(ORB paramORB, Acceptor paramAcceptor, Selector paramSelector) {
    this.orb = paramORB;
    this.acceptor = paramAcceptor;
    this.selector = paramSelector;
    this.keepRunning = true;
  }
  
  public Acceptor getAcceptor() { return this.acceptor; }
  
  public void close() {
    if (this.orb.transportDebugFlag)
      dprint(".close: " + this.acceptor); 
    this.keepRunning = false;
  }
  
  public void doWork() {
    try {
      if (this.orb.transportDebugFlag)
        dprint(".doWork: Start ListenerThread: " + this.acceptor); 
      while (this.keepRunning) {
        try {
          if (this.orb.transportDebugFlag)
            dprint(".doWork: BEFORE ACCEPT CYCLE: " + this.acceptor); 
          this.acceptor.accept();
          if (this.orb.transportDebugFlag)
            dprint(".doWork: AFTER ACCEPT CYCLE: " + this.acceptor); 
        } catch (Throwable throwable) {
          if (this.orb.transportDebugFlag)
            dprint(".doWork: Exception in accept: " + this.acceptor, throwable); 
          this.orb.getTransportManager().getSelector(0).unregisterForEvent(getAcceptor().getEventHandler());
          getAcceptor().close();
        } 
      } 
    } finally {
      if (this.orb.transportDebugFlag)
        dprint(".doWork: Terminated ListenerThread: " + this.acceptor); 
    } 
  }
  
  public void setEnqueueTime(long paramLong) { this.enqueueTime = paramLong; }
  
  public long getEnqueueTime() { return this.enqueueTime; }
  
  public String getName() { return "ListenerThread"; }
  
  private void dprint(String paramString) { ORBUtility.dprint("ListenerThreadImpl", paramString); }
  
  private void dprint(String paramString, Throwable paramThrowable) {
    dprint(paramString);
    paramThrowable.printStackTrace(System.out);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\transport\ListenerThreadImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */