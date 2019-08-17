package com.sun.security.auth;

import java.io.Serializable;
import java.security.Principal;
import java.text.MessageFormat;
import jdk.Exported;
import sun.security.util.ResourcesMgr;

@Exported
public class NTUserPrincipal implements Principal, Serializable {
  private static final long serialVersionUID = -8737649811939033735L;
  
  private String name;
  
  public NTUserPrincipal(String paramString) {
    if (paramString == null) {
      MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString("invalid.null.input.value", "sun.security.util.AuthResources"));
      Object[] arrayOfObject = { "name" };
      throw new NullPointerException(messageFormat.format(arrayOfObject));
    } 
    this.name = paramString;
  }
  
  public String getName() { return this.name; }
  
  public String toString() {
    MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString("NTUserPrincipal.name", "sun.security.util.AuthResources"));
    Object[] arrayOfObject = { this.name };
    return messageFormat.format(arrayOfObject);
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof NTUserPrincipal))
      return false; 
    NTUserPrincipal nTUserPrincipal = (NTUserPrincipal)paramObject;
    return this.name.equals(nTUserPrincipal.getName());
  }
  
  public int hashCode() { return getName().hashCode(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\auth\NTUserPrincipal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */