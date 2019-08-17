package java.lang;

import java.security.BasicPermission;

public final class RuntimePermission extends BasicPermission {
  private static final long serialVersionUID = 7399184964622342223L;
  
  public RuntimePermission(String paramString) { super(paramString); }
  
  public RuntimePermission(String paramString1, String paramString2) { super(paramString1, paramString2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\RuntimePermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */