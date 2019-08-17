package sun.security.action;

import java.security.PrivilegedAction;

public class GetIntegerAction extends Object implements PrivilegedAction<Integer> {
  private String theProp;
  
  private int defaultVal;
  
  private boolean defaultSet = false;
  
  public GetIntegerAction(String paramString) { this.theProp = paramString; }
  
  public GetIntegerAction(String paramString, int paramInt) {
    this.theProp = paramString;
    this.defaultVal = paramInt;
    this.defaultSet = true;
  }
  
  public Integer run() {
    Integer integer = Integer.getInteger(this.theProp);
    return (integer == null && this.defaultSet) ? new Integer(this.defaultVal) : integer;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\action\GetIntegerAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */