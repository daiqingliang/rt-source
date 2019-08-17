package java.sql;

import java.security.BasicPermission;

public final class SQLPermission extends BasicPermission {
  static final long serialVersionUID = -1439323187199563495L;
  
  public SQLPermission(String paramString) { super(paramString); }
  
  public SQLPermission(String paramString1, String paramString2) { super(paramString1, paramString2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\sql\SQLPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */