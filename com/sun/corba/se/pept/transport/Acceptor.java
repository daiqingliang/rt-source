package com.sun.corba.se.pept.transport;

import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;

public interface Acceptor {
  boolean initialize();
  
  boolean initialized();
  
  String getConnectionCacheType();
  
  void setConnectionCache(InboundConnectionCache paramInboundConnectionCache);
  
  InboundConnectionCache getConnectionCache();
  
  boolean shouldRegisterAcceptEvent();
  
  void accept();
  
  void close();
  
  EventHandler getEventHandler();
  
  MessageMediator createMessageMediator(Broker paramBroker, Connection paramConnection);
  
  MessageMediator finishCreatingMessageMediator(Broker paramBroker, Connection paramConnection, MessageMediator paramMessageMediator);
  
  InputObject createInputObject(Broker paramBroker, MessageMediator paramMessageMediator);
  
  OutputObject createOutputObject(Broker paramBroker, MessageMediator paramMessageMediator);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\pept\transport\Acceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */