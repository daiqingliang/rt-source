package sun.security.acl;

import java.security.Principal;
import java.security.acl.Group;
import java.security.acl.LastOwnerException;
import java.security.acl.NotOwnerException;
import java.security.acl.Owner;
import java.util.Enumeration;

public class OwnerImpl implements Owner {
  private Group ownerGroup = new GroupImpl("AclOwners");
  
  public OwnerImpl(Principal paramPrincipal) { this.ownerGroup.addMember(paramPrincipal); }
  
  public boolean addOwner(Principal paramPrincipal1, Principal paramPrincipal2) throws NotOwnerException {
    if (!isOwner(paramPrincipal1))
      throw new NotOwnerException(); 
    this.ownerGroup.addMember(paramPrincipal2);
    return false;
  }
  
  public boolean deleteOwner(Principal paramPrincipal1, Principal paramPrincipal2) throws NotOwnerException {
    if (!isOwner(paramPrincipal1))
      throw new NotOwnerException(); 
    Enumeration enumeration = this.ownerGroup.members();
    Object object = enumeration.nextElement();
    if (enumeration.hasMoreElements())
      return this.ownerGroup.removeMember(paramPrincipal2); 
    throw new LastOwnerException();
  }
  
  public boolean isOwner(Principal paramPrincipal) { return this.ownerGroup.isMember(paramPrincipal); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\acl\OwnerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */