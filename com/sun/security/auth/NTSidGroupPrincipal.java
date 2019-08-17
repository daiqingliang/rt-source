package com.sun.security.auth;

import java.text.MessageFormat;
import jdk.Exported;
import sun.security.util.ResourcesMgr;

@Exported
public class NTSidGroupPrincipal extends NTSid {
  private static final long serialVersionUID = -1373347438636198229L;
  
  public NTSidGroupPrincipal(String paramString) { super(paramString); }
  
  public String toString() {
    MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString("NTSidGroupPrincipal.name", "sun.security.util.AuthResources"));
    Object[] arrayOfObject = { getName() };
    return messageFormat.format(arrayOfObject);
  }
  
  public boolean equals(Object paramObject) { return (paramObject == null) ? false : ((this == paramObject) ? true : (!(paramObject instanceof NTSidGroupPrincipal) ? false : super.equals(paramObject))); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\auth\NTSidGroupPrincipal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */