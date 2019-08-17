package com.sun.corba.se.pept.transport;

import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;

public interface Connection {
  boolean shouldRegisterReadEvent();
  
  boolean shouldRegisterServerReadEvent();
  
  boolean read();
  
  void close();
  
  Acceptor getAcceptor();
  
  ContactInfo getContactInfo();
  
  EventHandler getEventHandler();
  
  boolean isServer();
  
  boolean isBusy();
  
  long getTimeStamp();
  
  void setTimeStamp(long paramLong);
  
  void setState(String paramString);
  
  void writeLock();
  
  void writeUnlock();
  
  void sendWithoutLock(OutputObject paramOutputObject);
  
  void registerWaiter(MessageMediator paramMessageMediator);
  
  InputObject waitForResponse(MessageMediator paramMessageMediator);
  
  void unregisterWaiter(MessageMediator paramMessageMediator);
  
  void setConnectionCache(ConnectionCache paramConnectionCache);
  
  ConnectionCache getConnectionCache();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\pept\transport\Connection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */