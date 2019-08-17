package com.sun.security.auth;

import java.io.Serializable;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ResourceBundle;
import jdk.Exported;

@Exported(false)
@Deprecated
public class SolarisNumericGroupPrincipal implements Principal, Serializable {
  private static final long serialVersionUID = 2345199581042573224L;
  
  private static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction<ResourceBundle>() {
        public ResourceBundle run() { return ResourceBundle.getBundle("sun.security.util.AuthResources"); }
      });
  
  private String name;
  
  private boolean primaryGroup;
  
  public SolarisNumericGroupPrincipal(String paramString, boolean paramBoolean) {
    if (paramString == null)
      throw new NullPointerException(rb.getString("provided.null.name")); 
    this.name = paramString;
    this.primaryGroup = paramBoolean;
  }
  
  public SolarisNumericGroupPrincipal(long paramLong, boolean paramBoolean) {
    this.name = (new Long(paramLong)).toString();
    this.primaryGroup = paramBoolean;
  }
  
  public String getName() { return this.name; }
  
  public long longValue() { return (new Long(this.name)).longValue(); }
  
  public boolean isPrimaryGroup() { return this.primaryGroup; }
  
  public String toString() { return this.primaryGroup ? (rb.getString("SolarisNumericGroupPrincipal.Primary.Group.") + this.name) : (rb.getString("SolarisNumericGroupPrincipal.Supplementary.Group.") + this.name); }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof SolarisNumericGroupPrincipal))
      return false; 
    SolarisNumericGroupPrincipal solarisNumericGroupPrincipal = (SolarisNumericGroupPrincipal)paramObject;
    return (getName().equals(solarisNumericGroupPrincipal.getName()) && isPrimaryGroup() == solarisNumericGroupPrincipal.isPrimaryGroup());
  }
  
  public int hashCode() { return toString().hashCode(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\auth\SolarisNumericGroupPrincipal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */