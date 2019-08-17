package java.nio.file.attribute;

import java.io.IOException;

public class UserPrincipalNotFoundException extends IOException {
  static final long serialVersionUID = -5369283889045833024L;
  
  private final String name;
  
  public UserPrincipalNotFoundException(String paramString) { this.name = paramString; }
  
  public String getName() { return this.name; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\attribute\UserPrincipalNotFoundException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */