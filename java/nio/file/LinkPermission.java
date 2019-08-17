package java.nio.file;

import java.security.BasicPermission;

public final class LinkPermission extends BasicPermission {
  static final long serialVersionUID = -1441492453772213220L;
  
  private void checkName(String paramString) {
    if (!paramString.equals("hard") && !paramString.equals("symbolic"))
      throw new IllegalArgumentException("name: " + paramString); 
  }
  
  public LinkPermission(String paramString) {
    super(paramString);
    checkName(paramString);
  }
  
  public LinkPermission(String paramString1, String paramString2) {
    super(paramString1);
    checkName(paramString1);
    if (paramString2 != null && paramString2.length() > 0)
      throw new IllegalArgumentException("actions: " + paramString2); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\LinkPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */