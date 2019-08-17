package sun.security.acl;

import java.security.Principal;

public class PrincipalImpl implements Principal {
  private String user;
  
  public PrincipalImpl(String paramString) { this.user = paramString; }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof PrincipalImpl) {
      PrincipalImpl principalImpl = (PrincipalImpl)paramObject;
      return this.user.equals(principalImpl.toString());
    } 
    return false;
  }
  
  public String toString() { return this.user; }
  
  public int hashCode() { return this.user.hashCode(); }
  
  public String getName() { return this.user; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\acl\PrincipalImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */