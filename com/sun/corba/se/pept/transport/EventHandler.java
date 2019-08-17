package com.sun.corba.se.pept.transport;

import com.sun.corba.se.spi.orbutil.threadpool.Work;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

public interface EventHandler {
  void setUseSelectThreadToWait(boolean paramBoolean);
  
  boolean shouldUseSelectThreadToWait();
  
  SelectableChannel getChannel();
  
  int getInterestOps();
  
  void setSelectionKey(SelectionKey paramSelectionKey);
  
  SelectionKey getSelectionKey();
  
  void handleEvent();
  
  void setUseWorkerThreadForEvent(boolean paramBoolean);
  
  boolean shouldUseWorkerThreadForEvent();
  
  void setWork(Work paramWork);
  
  Work getWork();
  
  Acceptor getAcceptor();
  
  Connection getConnection();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\pept\transport\EventHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */