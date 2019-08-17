package com.sun.nio.sctp;

import jdk.Exported;

@Exported
public class InvalidStreamException extends IllegalArgumentException {
  private static final long serialVersionUID = -9172703378046665558L;
  
  public InvalidStreamException() {}
  
  public InvalidStreamException(String paramString) { super(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\nio\sctp\InvalidStreamException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */