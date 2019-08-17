package com.sun.nio.sctp;

import jdk.Exported;

@Exported
public class IllegalReceiveException extends IllegalStateException {
  private static final long serialVersionUID = 2296619040988576224L;
  
  public IllegalReceiveException() {}
  
  public IllegalReceiveException(String paramString) { super(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\nio\sctp\IllegalReceiveException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */