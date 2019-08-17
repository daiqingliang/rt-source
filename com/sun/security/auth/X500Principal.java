package com.sun.security.auth;

import java.io.IOException;
import java.io.NotActiveException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ResourceBundle;
import jdk.Exported;
import sun.security.x509.X500Name;

@Exported(false)
@Deprecated
public class X500Principal implements Principal, Serializable {
  private static final long serialVersionUID = -8222422609431628648L;
  
  private static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction<ResourceBundle>() {
        public ResourceBundle run() { return ResourceBundle.getBundle("sun.security.util.AuthResources"); }
      });
  
  private String name;
  
  private X500Name thisX500Name;
  
  public X500Principal(String paramString) {
    if (paramString == null)
      throw new NullPointerException(rb.getString("provided.null.name")); 
    try {
      this.thisX500Name = new X500Name(paramString);
    } catch (Exception exception) {
      throw new IllegalArgumentException(exception.toString());
    } 
    this.name = paramString;
  }
  
  public String getName() { return this.thisX500Name.getName(); }
  
  public String toString() { return this.thisX500Name.toString(); }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (this == paramObject)
      return true; 
    if (paramObject instanceof X500Principal) {
      X500Principal x500Principal = (X500Principal)paramObject;
      try {
        X500Name x500Name = new X500Name(x500Principal.getName());
        return this.thisX500Name.equals(x500Name);
      } catch (Exception exception) {
        return false;
      } 
    } 
    return (paramObject instanceof Principal) ? paramObject.equals(this.thisX500Name) : 0;
  }
  
  public int hashCode() { return this.thisX500Name.hashCode(); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, NotActiveException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    this.thisX500Name = new X500Name(this.name);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\auth\X500Principal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */