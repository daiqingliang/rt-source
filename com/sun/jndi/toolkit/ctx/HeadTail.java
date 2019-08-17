package com.sun.jndi.toolkit.ctx;

import javax.naming.Name;

public class HeadTail {
  private int status;
  
  private Name head;
  
  private Name tail;
  
  public HeadTail(Name paramName1, Name paramName2) { this(paramName1, paramName2, 0); }
  
  public HeadTail(Name paramName1, Name paramName2, int paramInt) {
    this.status = paramInt;
    this.head = paramName1;
    this.tail = paramName2;
  }
  
  public void setStatus(int paramInt) { this.status = paramInt; }
  
  public Name getHead() { return this.head; }
  
  public Name getTail() { return this.tail; }
  
  public int getStatus() { return this.status; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\toolkit\ctx\HeadTail.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */