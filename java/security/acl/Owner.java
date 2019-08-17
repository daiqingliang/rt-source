package java.security.acl;

import java.security.Principal;

public interface Owner {
  boolean addOwner(Principal paramPrincipal1, Principal paramPrincipal2) throws NotOwnerException;
  
  boolean deleteOwner(Principal paramPrincipal1, Principal paramPrincipal2) throws NotOwnerException;
  
  boolean isOwner(Principal paramPrincipal);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\acl\Owner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */