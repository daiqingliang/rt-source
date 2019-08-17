package com.oracle.nio;

import java.security.BasicPermission;

public final class BufferSecretsPermission extends BasicPermission {
  private static final long serialVersionUID = 0L;
  
  public BufferSecretsPermission(String paramString) {
    super(paramString);
    if (!paramString.equals("access"))
      throw new IllegalArgumentException(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\nio\BufferSecretsPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */