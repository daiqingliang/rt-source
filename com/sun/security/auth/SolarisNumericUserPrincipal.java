package com.sun.security.auth;

import java.io.Serializable;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ResourceBundle;
import jdk.Exported;

@Exported(false)
@Deprecated
public class SolarisNumericUserPrincipal implements Principal, Serializable {
  private static final long serialVersionUID = -3178578484679887104L;
  
  private static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction<ResourceBundle>() {
        public ResourceBundle run() { return ResourceBundle.getBundle("sun.security.util.AuthResources"); }
      });
  
  private String name;
  
  public SolarisNumericUserPrincipal(String paramString) {
    if (paramString == null)
      throw new NullPointerException(rb.getString("provided.null.name")); 
    this.name = paramString;
  }
  
  public SolarisNumericUserPrincipal(long paramLong) { this.name = (new Long(paramLong)).toString(); }
  
  public String getName() { return this.name; }
  
  public long longValue() { return (new Long(this.name)).longValue(); }
  
  public String toString() { return rb.getString("SolarisNumericUserPrincipal.") + this.name; }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof SolarisNumericUserPrincipal))
      return false; 
    SolarisNumericUserPrincipal solarisNumericUserPrincipal = (SolarisNumericUserPrincipal)paramObject;
    return getName().equals(solarisNumericUserPrincipal.getName());
  }
  
  public int hashCode() { return this.name.hashCode(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\auth\SolarisNumericUserPrincipal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */