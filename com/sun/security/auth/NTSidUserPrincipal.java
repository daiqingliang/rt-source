package com.sun.security.auth;

import java.text.MessageFormat;
import jdk.Exported;
import sun.security.util.ResourcesMgr;

@Exported
public class NTSidUserPrincipal extends NTSid {
  private static final long serialVersionUID = -5573239889517749525L;
  
  public NTSidUserPrincipal(String paramString) { super(paramString); }
  
  public String toString() {
    MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString("NTSidUserPrincipal.name", "sun.security.util.AuthResources"));
    Object[] arrayOfObject = { getName() };
    return messageFormat.format(arrayOfObject);
  }
  
  public boolean equals(Object paramObject) { return (paramObject == null) ? false : ((this == paramObject) ? true : (!(paramObject instanceof NTSidUserPrincipal) ? false : super.equals(paramObject))); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\auth\NTSidUserPrincipal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */