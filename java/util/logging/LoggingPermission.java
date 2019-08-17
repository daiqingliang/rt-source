package java.util.logging;

import java.security.BasicPermission;

public final class LoggingPermission extends BasicPermission {
  private static final long serialVersionUID = 63564341580231582L;
  
  public LoggingPermission(String paramString1, String paramString2) throws IllegalArgumentException {
    super(paramString1);
    if (!paramString1.equals("control"))
      throw new IllegalArgumentException("name: " + paramString1); 
    if (paramString2 != null && paramString2.length() > 0)
      throw new IllegalArgumentException("actions: " + paramString2); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\logging\LoggingPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */