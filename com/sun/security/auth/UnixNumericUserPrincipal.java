package com.sun.security.auth;

import java.io.Serializable;
import java.security.Principal;
import java.text.MessageFormat;
import jdk.Exported;
import sun.security.util.ResourcesMgr;

@Exported
public class UnixNumericUserPrincipal implements Principal, Serializable {
  private static final long serialVersionUID = -4329764253802397821L;
  
  private String name;
  
  public UnixNumericUserPrincipal(String paramString) {
    if (paramString == null) {
      MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString("invalid.null.input.value", "sun.security.util.AuthResources"));
      Object[] arrayOfObject = { "name" };
      throw new NullPointerException(messageFormat.format(arrayOfObject));
    } 
    this.name = paramString;
  }
  
  public UnixNumericUserPrincipal(long paramLong) { this.name = (new Long(paramLong)).toString(); }
  
  public String getName() { return this.name; }
  
  public long longValue() { return (new Long(this.name)).longValue(); }
  
  public String toString() {
    MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString("UnixNumericUserPrincipal.name", "sun.security.util.AuthResources"));
    Object[] arrayOfObject = { this.name };
    return messageFormat.format(arrayOfObject);
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof UnixNumericUserPrincipal))
      return false; 
    UnixNumericUserPrincipal unixNumericUserPrincipal = (UnixNumericUserPrincipal)paramObject;
    return getName().equals(unixNumericUserPrincipal.getName());
  }
  
  public int hashCode() { return this.name.hashCode(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\auth\UnixNumericUserPrincipal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */