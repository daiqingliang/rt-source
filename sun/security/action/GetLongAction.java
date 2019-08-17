package sun.security.action;

import java.security.PrivilegedAction;

public class GetLongAction extends Object implements PrivilegedAction<Long> {
  private String theProp;
  
  private long defaultVal;
  
  private boolean defaultSet = false;
  
  public GetLongAction(String paramString) { this.theProp = paramString; }
  
  public GetLongAction(String paramString, long paramLong) {
    this.theProp = paramString;
    this.defaultVal = paramLong;
    this.defaultSet = true;
  }
  
  public Long run() {
    Long long = Long.getLong(this.theProp);
    return (long == null && this.defaultSet) ? new Long(this.defaultVal) : long;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\action\GetLongAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */