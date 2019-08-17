package com.sun.security.auth;

import java.io.Serializable;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ResourceBundle;
import jdk.Exported;

@Exported(false)
@Deprecated
public class SolarisPrincipal implements Principal, Serializable {
  private static final long serialVersionUID = -7840670002439379038L;
  
  private static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction<ResourceBundle>() {
        public ResourceBundle run() { return ResourceBundle.getBundle("sun.security.util.AuthResources"); }
      });
  
  private String name;
  
  public SolarisPrincipal(String paramString) {
    if (paramString == null)
      throw new NullPointerException(rb.getString("provided.null.name")); 
    this.name = paramString;
  }
  
  public String getName() { return this.name; }
  
  public String toString() { return rb.getString("SolarisPrincipal.") + this.name; }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof SolarisPrincipal))
      return false; 
    SolarisPrincipal solarisPrincipal = (SolarisPrincipal)paramObject;
    return getName().equals(solarisPrincipal.getName());
  }
  
  public int hashCode() { return this.name.hashCode(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\auth\SolarisPrincipal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */