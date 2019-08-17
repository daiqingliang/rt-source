package com.sun.net.ssl;

import java.security.BasicPermission;

@Deprecated
public final class SSLPermission extends BasicPermission {
  private static final long serialVersionUID = -2583684302506167542L;
  
  public SSLPermission(String paramString) { super(paramString); }
  
  public SSLPermission(String paramString1, String paramString2) { super(paramString1, paramString2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\net\ssl\SSLPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */