package com.sun.jndi.ldap;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.naming.CommunicationException;

final class LdapRequest {
  LdapRequest next;
  
  int msgId;
  
  private int gotten = 0;
  
  private BlockingQueue<BerDecoder> replies;
  
  private int highWatermark = -1;
  
  private boolean cancelled = false;
  
  private boolean pauseAfterReceipt = false;
  
  private boolean completed = false;
  
  LdapRequest(int paramInt, boolean paramBoolean) { this(paramInt, paramBoolean, -1); }
  
  LdapRequest(int paramInt1, boolean paramBoolean, int paramInt2) {
    this.msgId = paramInt1;
    this.pauseAfterReceipt = paramBoolean;
    if (paramInt2 == -1) {
      this.replies = new LinkedBlockingQueue();
    } else {
      this.replies = new LinkedBlockingQueue(paramInt2);
      this.highWatermark = paramInt2 * 80 / 100;
    } 
  }
  
  void cancel() {
    this.cancelled = true;
    notify();
  }
  
  boolean addReplyBer(BerDecoder paramBerDecoder) {
    if (this.cancelled)
      return false; 
    try {
      this.replies.put(paramBerDecoder);
    } catch (InterruptedException interruptedException) {}
    try {
      paramBerDecoder.parseSeq(null);
      paramBerDecoder.parseInt();
      this.completed = (paramBerDecoder.peekByte() == 101);
    } catch (IOException iOException) {}
    paramBerDecoder.reset();
    notify();
    return (this.highWatermark != -1 && this.replies.size() >= this.highWatermark) ? true : this.pauseAfterReceipt;
  }
  
  BerDecoder getReplyBer() throws CommunicationException {
    if (this.cancelled)
      throw new CommunicationException("Request: " + this.msgId + " cancelled"); 
    return (BerDecoder)this.replies.poll();
  }
  
  boolean hasSearchCompleted() { return this.completed; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\LdapRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */