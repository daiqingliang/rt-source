package java.net;

import java.security.BasicPermission;

public final class NetPermission extends BasicPermission {
  private static final long serialVersionUID = -8343910153355041693L;
  
  public NetPermission(String paramString) { super(paramString); }
  
  public NetPermission(String paramString1, String paramString2) { super(paramString1, paramString2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\NetPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */