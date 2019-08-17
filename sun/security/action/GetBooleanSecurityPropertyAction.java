package sun.security.action;

import java.security.PrivilegedAction;
import java.security.Security;

public class GetBooleanSecurityPropertyAction extends Object implements PrivilegedAction<Boolean> {
  private String theProp;
  
  public GetBooleanSecurityPropertyAction(String paramString) { this.theProp = paramString; }
  
  public Boolean run() {
    boolean bool = false;
    try {
      String str = Security.getProperty(this.theProp);
      bool = (str != null && str.equalsIgnoreCase("true"));
    } catch (NullPointerException nullPointerException) {}
    return Boolean.valueOf(bool);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\action\GetBooleanSecurityPropertyAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */