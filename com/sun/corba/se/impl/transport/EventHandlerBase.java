package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.EventHandler;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchThreadPoolException;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchWorkQueueException;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import java.nio.channels.SelectionKey;
import org.omg.CORBA.INTERNAL;

public abstract class EventHandlerBase implements EventHandler {
  protected ORB orb;
  
  protected Work work;
  
  protected boolean useWorkerThreadForEvent;
  
  protected boolean useSelectThreadToWait;
  
  protected SelectionKey selectionKey;
  
  public void setUseSelectThreadToWait(boolean paramBoolean) { this.useSelectThreadToWait = paramBoolean; }
  
  public boolean shouldUseSelectThreadToWait() { return this.useSelectThreadToWait; }
  
  public void setSelectionKey(SelectionKey paramSelectionKey) { this.selectionKey = paramSelectionKey; }
  
  public SelectionKey getSelectionKey() { return this.selectionKey; }
  
  public void handleEvent() {
    if (this.orb.transportDebugFlag)
      dprint(".handleEvent->: " + this); 
    getSelectionKey().interestOps(getSelectionKey().interestOps() & (getInterestOps() ^ 0xFFFFFFFF));
    if (shouldUseWorkerThreadForEvent()) {
      NoSuchWorkQueueException noSuchWorkQueueException = null;
      try {
        if (this.orb.transportDebugFlag)
          dprint(".handleEvent: addWork to pool: 0"); 
        this.orb.getThreadPoolManager().getThreadPool(0).getWorkQueue(0).addWork(getWork());
      } catch (NoSuchThreadPoolException noSuchThreadPoolException) {
        noSuchWorkQueueException = noSuchThreadPoolException;
      } catch (NoSuchWorkQueueException noSuchWorkQueueException1) {
        noSuchWorkQueueException = noSuchWorkQueueException1;
      } 
      if (noSuchWorkQueueException != null) {
        if (this.orb.transportDebugFlag)
          dprint(".handleEvent: " + noSuchWorkQueueException); 
        INTERNAL iNTERNAL = new INTERNAL("NoSuchThreadPoolException");
        iNTERNAL.initCause(noSuchWorkQueueException);
        throw iNTERNAL;
      } 
    } else {
      if (this.orb.transportDebugFlag)
        dprint(".handleEvent: doWork"); 
      getWork().doWork();
    } 
    if (this.orb.transportDebugFlag)
      dprint(".handleEvent<-: " + this); 
  }
  
  public boolean shouldUseWorkerThreadForEvent() { return this.useWorkerThreadForEvent; }
  
  public void setUseWorkerThreadForEvent(boolean paramBoolean) { this.useWorkerThreadForEvent = paramBoolean; }
  
  public void setWork(Work paramWork) { this.work = paramWork; }
  
  public Work getWork() { return this.work; }
  
  private void dprint(String paramString) { ORBUtility.dprint("EventHandlerBase", paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\transport\EventHandlerBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */