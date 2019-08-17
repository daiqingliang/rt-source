package com.sun.security.auth;

import java.text.MessageFormat;
import jdk.Exported;
import sun.security.util.ResourcesMgr;

@Exported
public class NTSidPrimaryGroupPrincipal extends NTSid {
  private static final long serialVersionUID = 8011978367305190527L;
  
  public NTSidPrimaryGroupPrincipal(String paramString) { super(paramString); }
  
  public String toString() {
    MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString("NTSidPrimaryGroupPrincipal.name", "sun.security.util.AuthResources"));
    Object[] arrayOfObject = { getName() };
    return messageFormat.format(arrayOfObject);
  }
  
  public boolean equals(Object paramObject) { return (paramObject == null) ? false : ((this == paramObject) ? true : (!(paramObject instanceof NTSidPrimaryGroupPrincipal) ? false : super.equals(paramObject))); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\auth\NTSidPrimaryGroupPrincipal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */