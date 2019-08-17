package com.sun.security.auth;

import java.io.Serializable;
import java.security.Principal;
import java.text.MessageFormat;
import jdk.Exported;
import sun.security.util.ResourcesMgr;

@Exported
public class UnixNumericGroupPrincipal implements Principal, Serializable {
  private static final long serialVersionUID = 3941535899328403223L;
  
  private String name;
  
  private boolean primaryGroup;
  
  public UnixNumericGroupPrincipal(String paramString, boolean paramBoolean) {
    if (paramString == null) {
      MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString("invalid.null.input.value", "sun.security.util.AuthResources"));
      Object[] arrayOfObject = { "name" };
      throw new NullPointerException(messageFormat.format(arrayOfObject));
    } 
    this.name = paramString;
    this.primaryGroup = paramBoolean;
  }
  
  public UnixNumericGroupPrincipal(long paramLong, boolean paramBoolean) {
    this.name = (new Long(paramLong)).toString();
    this.primaryGroup = paramBoolean;
  }
  
  public String getName() { return this.name; }
  
  public long longValue() { return (new Long(this.name)).longValue(); }
  
  public boolean isPrimaryGroup() { return this.primaryGroup; }
  
  public String toString() {
    if (this.primaryGroup) {
      MessageFormat messageFormat1 = new MessageFormat(ResourcesMgr.getString("UnixNumericGroupPrincipal.Primary.Group.name", "sun.security.util.AuthResources"));
      Object[] arrayOfObject1 = { this.name };
      return messageFormat1.format(arrayOfObject1);
    } 
    MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString("UnixNumericGroupPrincipal.Supplementary.Group.name", "sun.security.util.AuthResources"));
    Object[] arrayOfObject = { this.name };
    return messageFormat.format(arrayOfObject);
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof UnixNumericGroupPrincipal))
      return false; 
    UnixNumericGroupPrincipal unixNumericGroupPrincipal = (UnixNumericGroupPrincipal)paramObject;
    return (getName().equals(unixNumericGroupPrincipal.getName()) && isPrimaryGroup() == unixNumericGroupPrincipal.isPrimaryGroup());
  }
  
  public int hashCode() { return toString().hashCode(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\auth\UnixNumericGroupPrincipal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */