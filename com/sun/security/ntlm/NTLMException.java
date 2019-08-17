package com.sun.security.ntlm;

import java.security.GeneralSecurityException;

public final class NTLMException extends GeneralSecurityException {
  private static final long serialVersionUID = -3298539507906689430L;
  
  public static final int PACKET_READ_ERROR = 1;
  
  public static final int NO_DOMAIN_INFO = 2;
  
  public static final int USER_UNKNOWN = 3;
  
  public static final int AUTH_FAILED = 4;
  
  public static final int BAD_VERSION = 5;
  
  public static final int PROTOCOL = 6;
  
  private int errorCode;
  
  public NTLMException(int paramInt, String paramString) {
    super(paramString);
    this.errorCode = paramInt;
  }
  
  public int errorCode() { return this.errorCode; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\ntlm\NTLMException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */