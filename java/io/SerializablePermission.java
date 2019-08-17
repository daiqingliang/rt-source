package java.io;

import java.security.BasicPermission;

public final class SerializablePermission extends BasicPermission {
  private static final long serialVersionUID = 8537212141160296410L;
  
  private String actions;
  
  public SerializablePermission(String paramString) { super(paramString); }
  
  public SerializablePermission(String paramString1, String paramString2) { super(paramString1, paramString2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\SerializablePermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */