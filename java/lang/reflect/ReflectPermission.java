package java.lang.reflect;

import java.security.BasicPermission;

public final class ReflectPermission extends BasicPermission {
  private static final long serialVersionUID = 7412737110241507485L;
  
  public ReflectPermission(String paramString) { super(paramString); }
  
  public ReflectPermission(String paramString1, String paramString2) { super(paramString1, paramString2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\reflect\ReflectPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */