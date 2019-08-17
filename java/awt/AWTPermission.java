package java.awt;

import java.security.BasicPermission;

public final class AWTPermission extends BasicPermission {
  private static final long serialVersionUID = 8890392402588814465L;
  
  public AWTPermission(String paramString) { super(paramString); }
  
  public AWTPermission(String paramString1, String paramString2) { super(paramString1, paramString2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\AWTPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */