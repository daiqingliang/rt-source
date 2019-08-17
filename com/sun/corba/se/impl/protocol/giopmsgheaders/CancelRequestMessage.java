package com.sun.corba.se.impl.protocol.giopmsgheaders;

public interface CancelRequestMessage extends Message {
  public static final int CANCEL_REQ_MSG_SIZE = 4;
  
  int getRequestId();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\CancelRequestMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */