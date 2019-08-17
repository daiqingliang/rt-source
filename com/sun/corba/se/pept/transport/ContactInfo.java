package com.sun.corba.se.pept.transport;

import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.pept.protocol.MessageMediator;

public interface ContactInfo {
  Broker getBroker();
  
  ContactInfoList getContactInfoList();
  
  ClientRequestDispatcher getClientRequestDispatcher();
  
  boolean isConnectionBased();
  
  boolean shouldCacheConnection();
  
  String getConnectionCacheType();
  
  void setConnectionCache(OutboundConnectionCache paramOutboundConnectionCache);
  
  OutboundConnectionCache getConnectionCache();
  
  Connection createConnection();
  
  MessageMediator createMessageMediator(Broker paramBroker, ContactInfo paramContactInfo, Connection paramConnection, String paramString, boolean paramBoolean);
  
  MessageMediator createMessageMediator(Broker paramBroker, Connection paramConnection);
  
  MessageMediator finishCreatingMessageMediator(Broker paramBroker, Connection paramConnection, MessageMediator paramMessageMediator);
  
  InputObject createInputObject(Broker paramBroker, MessageMediator paramMessageMediator);
  
  OutputObject createOutputObject(MessageMediator paramMessageMediator);
  
  int hashCode();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\pept\transport\ContactInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */