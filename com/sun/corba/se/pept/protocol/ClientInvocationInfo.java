package com.sun.corba.se.pept.protocol;

import java.util.Iterator;

public interface ClientInvocationInfo {
  Iterator getContactInfoListIterator();
  
  void setContactInfoListIterator(Iterator paramIterator);
  
  boolean isRetryInvocation();
  
  void setIsRetryInvocation(boolean paramBoolean);
  
  int getEntryCount();
  
  void incrementEntryCount();
  
  void decrementEntryCount();
  
  void setClientRequestDispatcher(ClientRequestDispatcher paramClientRequestDispatcher);
  
  ClientRequestDispatcher getClientRequestDispatcher();
  
  void setMessageMediator(MessageMediator paramMessageMediator);
  
  MessageMediator getMessageMediator();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\pept\protocol\ClientInvocationInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */