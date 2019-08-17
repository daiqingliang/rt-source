package sun.security.action;

import java.security.PrivilegedAction;

public class GetBooleanAction extends Object implements PrivilegedAction<Boolean> {
  private String theProp;
  
  public GetBooleanAction(String paramString) { this.theProp = paramString; }
  
  public Boolean run() { return Boolean.valueOf(Boolean.getBoolean(this.theProp)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\action\GetBooleanAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */