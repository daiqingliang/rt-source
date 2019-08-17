package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.EventHandler;
import com.sun.corba.se.pept.transport.ListenerThread;
import com.sun.corba.se.pept.transport.ReaderThread;
import com.sun.corba.se.pept.transport.Selector;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchThreadPoolException;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchWorkQueueException;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class SelectorImpl extends Thread implements Selector {
  private ORB orb;
  
  private Selector selector;
  
  private long timeout;
  
  private List deferredRegistrations;
  
  private List interestOpsList;
  
  private HashMap listenerThreads;
  
  private Map readerThreads;
  
  private boolean selectorStarted;
  
  private ORBUtilSystemException wrapper;
  
  public SelectorImpl(ORB paramORB) {
    this.orb = paramORB;
    this.selector = null;
    this.selectorStarted = false;
    this.timeout = 60000L;
    this.deferredRegistrations = new ArrayList();
    this.interestOpsList = new ArrayList();
    this.listenerThreads = new HashMap();
    this.readerThreads = Collections.synchronizedMap(new HashMap());
    this.closed = false;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.transport");
  }
  
  public void setTimeout(long paramLong) { this.timeout = paramLong; }
  
  public long getTimeout() { return this.timeout; }
  
  public void registerInterestOps(EventHandler paramEventHandler) {
    if (this.orb.transportDebugFlag)
      dprint(".registerInterestOps:-> " + paramEventHandler); 
    SelectionKey selectionKey = paramEventHandler.getSelectionKey();
    if (selectionKey.isValid()) {
      int i = paramEventHandler.getInterestOps();
      SelectionKeyAndOp selectionKeyAndOp = new SelectionKeyAndOp(selectionKey, i);
      synchronized (this.interestOpsList) {
        this.interestOpsList.add(selectionKeyAndOp);
      } 
      try {
        if (this.selector != null)
          this.selector.wakeup(); 
      } catch (Throwable throwable) {
        if (this.orb.transportDebugFlag)
          dprint(".registerInterestOps: selector.wakeup: ", throwable); 
      } 
    } else {
      this.wrapper.selectionKeyInvalid(paramEventHandler.toString());
      if (this.orb.transportDebugFlag)
        dprint(".registerInterestOps: EventHandler SelectionKey not valid " + paramEventHandler); 
    } 
    if (this.orb.transportDebugFlag)
      dprint(".registerInterestOps:<- "); 
  }
  
  public void registerForEvent(EventHandler paramEventHandler) {
    if (this.orb.transportDebugFlag)
      dprint(".registerForEvent: " + paramEventHandler); 
    if (isClosed()) {
      if (this.orb.transportDebugFlag)
        dprint(".registerForEvent: closed: " + paramEventHandler); 
      return;
    } 
    if (paramEventHandler.shouldUseSelectThreadToWait()) {
      synchronized (this.deferredRegistrations) {
        this.deferredRegistrations.add(paramEventHandler);
      } 
      if (!this.selectorStarted)
        startSelector(); 
      this.selector.wakeup();
      return;
    } 
    switch (paramEventHandler.getInterestOps()) {
      case 16:
        createListenerThread(paramEventHandler);
        return;
      case 1:
        createReaderThread(paramEventHandler);
        return;
    } 
    if (this.orb.transportDebugFlag)
      dprint(".registerForEvent: default: " + paramEventHandler); 
    throw new RuntimeException("SelectorImpl.registerForEvent: unknown interest ops");
  }
  
  public void unregisterForEvent(EventHandler paramEventHandler) {
    if (this.orb.transportDebugFlag)
      dprint(".unregisterForEvent: " + paramEventHandler); 
    if (isClosed()) {
      if (this.orb.transportDebugFlag)
        dprint(".unregisterForEvent: closed: " + paramEventHandler); 
      return;
    } 
    if (paramEventHandler.shouldUseSelectThreadToWait()) {
      SelectionKey selectionKey;
      synchronized (this.deferredRegistrations) {
        selectionKey = paramEventHandler.getSelectionKey();
      } 
      if (selectionKey != null)
        selectionKey.cancel(); 
      if (this.selector != null)
        this.selector.wakeup(); 
      return;
    } 
    switch (paramEventHandler.getInterestOps()) {
      case 16:
        destroyListenerThread(paramEventHandler);
        return;
      case 1:
        destroyReaderThread(paramEventHandler);
        return;
    } 
    if (this.orb.transportDebugFlag)
      dprint(".unregisterForEvent: default: " + paramEventHandler); 
    throw new RuntimeException("SelectorImpl.uregisterForEvent: unknown interest ops");
  }
  
  public void close() {
    if (this.orb.transportDebugFlag)
      dprint(".close"); 
    if (isClosed()) {
      if (this.orb.transportDebugFlag)
        dprint(".close: already closed"); 
      return;
    } 
    setClosed(true);
    for (ListenerThread listenerThread : this.listenerThreads.values())
      listenerThread.close(); 
    for (ReaderThread readerThread : this.readerThreads.values())
      readerThread.close(); 
    clearDeferredRegistrations();
    try {
      if (this.selector != null)
        this.selector.wakeup(); 
    } catch (Throwable throwable) {
      if (this.orb.transportDebugFlag)
        dprint(".close: selector.wakeup: ", throwable); 
    } 
  }
  
  public void run() {
    setName("SelectorThread");
    while (!this.closed) {
      try {
        int i = 0;
        if (this.timeout == 0L && this.orb.transportDebugFlag)
          dprint(".run: Beginning of selection cycle"); 
        handleDeferredRegistrations();
        enableInterestOps();
        try {
          i = this.selector.select(this.timeout);
        } catch (IOException iOException) {
          if (this.orb.transportDebugFlag)
            dprint(".run: selector.select: ", iOException); 
        } catch (ClosedSelectorException closedSelectorException) {
          if (this.orb.transportDebugFlag)
            dprint(".run: selector.select: ", closedSelectorException); 
          break;
        } 
        if (this.closed)
          break; 
        Iterator iterator = this.selector.selectedKeys().iterator();
        if (this.orb.transportDebugFlag && iterator.hasNext())
          dprint(".run: n = " + i); 
        while (iterator.hasNext()) {
          SelectionKey selectionKey = (SelectionKey)iterator.next();
          iterator.remove();
          EventHandler eventHandler = (EventHandler)selectionKey.attachment();
          try {
            eventHandler.handleEvent();
          } catch (Throwable throwable) {
            if (this.orb.transportDebugFlag)
              dprint(".run: eventHandler.handleEvent", throwable); 
          } 
        } 
        if (this.timeout == 0L && this.orb.transportDebugFlag)
          dprint(".run: End of selection cycle"); 
      } catch (Throwable throwable) {
        if (this.orb.transportDebugFlag)
          dprint(".run: ignoring", throwable); 
      } 
    } 
    try {
      if (this.selector != null) {
        if (this.orb.transportDebugFlag)
          dprint(".run: selector.close "); 
        this.selector.close();
      } 
    } catch (Throwable throwable) {
      if (this.orb.transportDebugFlag)
        dprint(".run: selector.close: ", throwable); 
    } 
  }
  
  private void clearDeferredRegistrations() {
    synchronized (this.deferredRegistrations) {
      int i = this.deferredRegistrations.size();
      if (this.orb.transportDebugFlag)
        dprint(".clearDeferredRegistrations:deferred list size == " + i); 
      for (byte b = 0; b < i; b++) {
        EventHandler eventHandler = (EventHandler)this.deferredRegistrations.get(b);
        if (this.orb.transportDebugFlag)
          dprint(".clearDeferredRegistrations: " + eventHandler); 
        SelectableChannel selectableChannel = eventHandler.getChannel();
        SelectionKey selectionKey = null;
        try {
          if (this.orb.transportDebugFlag) {
            dprint(".clearDeferredRegistrations:close channel == " + selectableChannel);
            dprint(".clearDeferredRegistrations:close channel class == " + selectableChannel.getClass().getName());
          } 
          selectableChannel.close();
          selectionKey = eventHandler.getSelectionKey();
          if (selectionKey != null) {
            selectionKey.cancel();
            selectionKey.attach(null);
          } 
        } catch (IOException iOException) {
          if (this.orb.transportDebugFlag)
            dprint(".clearDeferredRegistrations: ", iOException); 
        } 
      } 
      this.deferredRegistrations.clear();
    } 
  }
  
  private boolean isClosed() { return this.closed; }
  
  private void setClosed(boolean paramBoolean) { this.closed = paramBoolean; }
  
  private void startSelector() {
    try {
      this.selector = Selector.open();
    } catch (IOException iOException) {
      if (this.orb.transportDebugFlag)
        dprint(".startSelector: Selector.open: IOException: ", iOException); 
      RuntimeException runtimeException = new RuntimeException(".startSelector: Selector.open exception");
      runtimeException.initCause(iOException);
      throw runtimeException;
    } 
    setDaemon(true);
    start();
    this.selectorStarted = true;
    if (this.orb.transportDebugFlag)
      dprint(".startSelector: selector.start completed."); 
  }
  
  private void handleDeferredRegistrations() {
    synchronized (this.deferredRegistrations) {
      int i = this.deferredRegistrations.size();
      for (byte b = 0; b < i; b++) {
        EventHandler eventHandler = (EventHandler)this.deferredRegistrations.get(b);
        if (this.orb.transportDebugFlag)
          dprint(".handleDeferredRegistrations: " + eventHandler); 
        SelectableChannel selectableChannel = eventHandler.getChannel();
        SelectionKey selectionKey = null;
        try {
          selectionKey = selectableChannel.register(this.selector, eventHandler.getInterestOps(), eventHandler);
        } catch (ClosedChannelException closedChannelException) {
          if (this.orb.transportDebugFlag)
            dprint(".handleDeferredRegistrations: ", closedChannelException); 
        } 
        eventHandler.setSelectionKey(selectionKey);
      } 
      this.deferredRegistrations.clear();
    } 
  }
  
  private void enableInterestOps() {
    synchronized (this.interestOpsList) {
      int i = this.interestOpsList.size();
      if (i > 0) {
        if (this.orb.transportDebugFlag)
          dprint(".enableInterestOps:->"); 
        SelectionKey selectionKey = null;
        SelectionKeyAndOp selectionKeyAndOp = null;
        int j = 0;
        for (byte b = 0; b < i; b++) {
          selectionKeyAndOp = (SelectionKeyAndOp)this.interestOpsList.get(b);
          selectionKey = selectionKeyAndOp.selectionKey;
          if (selectionKey.isValid()) {
            if (this.orb.transportDebugFlag)
              dprint(".enableInterestOps: " + selectionKeyAndOp); 
            int k = selectionKeyAndOp.keyOp;
            j = selectionKey.interestOps();
            selectionKey.interestOps(j | k);
          } 
        } 
        this.interestOpsList.clear();
        if (this.orb.transportDebugFlag)
          dprint(".enableInterestOps:<-"); 
      } 
    } 
  }
  
  private void createListenerThread(EventHandler paramEventHandler) {
    if (this.orb.transportDebugFlag)
      dprint(".createListenerThread: " + paramEventHandler); 
    Acceptor acceptor = paramEventHandler.getAcceptor();
    ListenerThreadImpl listenerThreadImpl = new ListenerThreadImpl(this.orb, acceptor, this);
    this.listenerThreads.put(paramEventHandler, listenerThreadImpl);
    NoSuchWorkQueueException noSuchWorkQueueException = null;
    try {
      this.orb.getThreadPoolManager().getThreadPool(0).getWorkQueue(0).addWork((Work)listenerThreadImpl);
    } catch (NoSuchThreadPoolException noSuchThreadPoolException) {
      noSuchWorkQueueException = noSuchThreadPoolException;
    } catch (NoSuchWorkQueueException noSuchWorkQueueException1) {
      noSuchWorkQueueException = noSuchWorkQueueException1;
    } 
    if (noSuchWorkQueueException != null) {
      RuntimeException runtimeException = new RuntimeException(noSuchWorkQueueException.toString());
      runtimeException.initCause(noSuchWorkQueueException);
      throw runtimeException;
    } 
  }
  
  private void destroyListenerThread(EventHandler paramEventHandler) {
    if (this.orb.transportDebugFlag)
      dprint(".destroyListenerThread: " + paramEventHandler); 
    ListenerThread listenerThread = (ListenerThread)this.listenerThreads.get(paramEventHandler);
    if (listenerThread == null) {
      if (this.orb.transportDebugFlag)
        dprint(".destroyListenerThread: cannot find ListenerThread - ignoring."); 
      return;
    } 
    this.listenerThreads.remove(paramEventHandler);
    listenerThread.close();
  }
  
  private void createReaderThread(EventHandler paramEventHandler) {
    if (this.orb.transportDebugFlag)
      dprint(".createReaderThread: " + paramEventHandler); 
    Connection connection = paramEventHandler.getConnection();
    ReaderThreadImpl readerThreadImpl = new ReaderThreadImpl(this.orb, connection, this);
    this.readerThreads.put(paramEventHandler, readerThreadImpl);
    NoSuchWorkQueueException noSuchWorkQueueException = null;
    try {
      this.orb.getThreadPoolManager().getThreadPool(0).getWorkQueue(0).addWork((Work)readerThreadImpl);
    } catch (NoSuchThreadPoolException noSuchThreadPoolException) {
      noSuchWorkQueueException = noSuchThreadPoolException;
    } catch (NoSuchWorkQueueException noSuchWorkQueueException1) {
      noSuchWorkQueueException = noSuchWorkQueueException1;
    } 
    if (noSuchWorkQueueException != null) {
      RuntimeException runtimeException = new RuntimeException(noSuchWorkQueueException.toString());
      runtimeException.initCause(noSuchWorkQueueException);
      throw runtimeException;
    } 
  }
  
  private void destroyReaderThread(EventHandler paramEventHandler) {
    if (this.orb.transportDebugFlag)
      dprint(".destroyReaderThread: " + paramEventHandler); 
    ReaderThread readerThread = (ReaderThread)this.readerThreads.get(paramEventHandler);
    if (readerThread == null) {
      if (this.orb.transportDebugFlag)
        dprint(".destroyReaderThread: cannot find ReaderThread - ignoring."); 
      return;
    } 
    this.readerThreads.remove(paramEventHandler);
    readerThread.close();
  }
  
  private void dprint(String paramString) { ORBUtility.dprint("SelectorImpl", paramString); }
  
  protected void dprint(String paramString, Throwable paramThrowable) {
    dprint(paramString);
    paramThrowable.printStackTrace(System.out);
  }
  
  private class SelectionKeyAndOp {
    public int keyOp;
    
    public SelectionKey selectionKey;
    
    public SelectionKeyAndOp(SelectionKey param1SelectionKey, int param1Int) {
      this.selectionKey = param1SelectionKey;
      this.keyOp = param1Int;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\transport\SelectorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */