package com.sun.corba.se.impl.protocol;

public class RequestCanceledException extends RuntimeException {
  private int requestId = 0;
  
  public RequestCanceledException(int paramInt) { this.requestId = paramInt; }
  
  public int getRequestId() { return this.requestId; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\RequestCanceledException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */