package javax.net.ssl;

import java.security.BasicPermission;

public final class SSLPermission extends BasicPermission {
  private static final long serialVersionUID = -3456898025505876775L;
  
  public SSLPermission(String paramString) { super(paramString); }
  
  public SSLPermission(String paramString1, String paramString2) { super(paramString1, paramString2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\ssl\SSLPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */