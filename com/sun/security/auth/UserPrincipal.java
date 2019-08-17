package com.sun.security.auth;

import java.io.Serializable;
import java.security.Principal;
import jdk.Exported;

@Exported
public final class UserPrincipal implements Principal, Serializable {
  private static final long serialVersionUID = 892106070870210969L;
  
  private final String name;
  
  public UserPrincipal(String paramString) {
    if (paramString == null)
      throw new NullPointerException("null name is illegal"); 
    this.name = paramString;
  }
  
  public boolean equals(Object paramObject) { return (this == paramObject) ? true : ((paramObject instanceof UserPrincipal) ? this.name.equals(((UserPrincipal)paramObject).getName()) : 0); }
  
  public int hashCode() { return this.name.hashCode(); }
  
  public String getName() { return this.name; }
  
  public String toString() { return this.name; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\auth\UserPrincipal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */