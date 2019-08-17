package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.pept.protocol.ClientInvocationInfo;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Iterator;

public class CorbaInvocationInfo implements ClientInvocationInfo {
  private boolean isRetryInvocation;
  
  private int entryCount;
  
  private ORB orb;
  
  private Iterator contactInfoListIterator;
  
  private ClientRequestDispatcher clientRequestDispatcher;
  
  private MessageMediator messageMediator;
  
  private CorbaInvocationInfo() {}
  
  public CorbaInvocationInfo(ORB paramORB) {
    this.orb = paramORB;
    this.isRetryInvocation = false;
    this.entryCount = 0;
  }
  
  public Iterator getContactInfoListIterator() { return this.contactInfoListIterator; }
  
  public void setContactInfoListIterator(Iterator paramIterator) { this.contactInfoListIterator = paramIterator; }
  
  public boolean isRetryInvocation() { return this.isRetryInvocation; }
  
  public void setIsRetryInvocation(boolean paramBoolean) { this.isRetryInvocation = paramBoolean; }
  
  public int getEntryCount() { return this.entryCount; }
  
  public void incrementEntryCount() { this.entryCount++; }
  
  public void decrementEntryCount() { this.entryCount--; }
  
  public void setClientRequestDispatcher(ClientRequestDispatcher paramClientRequestDispatcher) { this.clientRequestDispatcher = paramClientRequestDispatcher; }
  
  public ClientRequestDispatcher getClientRequestDispatcher() { return this.clientRequestDispatcher; }
  
  public void setMessageMediator(MessageMediator paramMessageMediator) { this.messageMediator = paramMessageMediator; }
  
  public MessageMediator getMessageMediator() { return this.messageMediator; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\CorbaInvocationInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */