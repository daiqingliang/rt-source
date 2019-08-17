package com.sun.security.auth;

import java.text.MessageFormat;
import jdk.Exported;
import sun.security.util.ResourcesMgr;

@Exported
public class NTSidDomainPrincipal extends NTSid {
  private static final long serialVersionUID = 5247810785821650912L;
  
  public NTSidDomainPrincipal(String paramString) { super(paramString); }
  
  public String toString() {
    MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString("NTSidDomainPrincipal.name", "sun.security.util.AuthResources"));
    Object[] arrayOfObject = { getName() };
    return messageFormat.format(arrayOfObject);
  }
  
  public boolean equals(Object paramObject) { return (paramObject == null) ? false : ((this == paramObject) ? true : (!(paramObject instanceof NTSidDomainPrincipal) ? false : super.equals(paramObject))); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\auth\NTSidDomainPrincipal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */