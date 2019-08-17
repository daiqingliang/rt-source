package sun.security.acl;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.Vector;

public class GroupImpl implements Group {
  private Vector<Principal> groupMembers = new Vector(50, 100);
  
  private String group;
  
  public GroupImpl(String paramString) { this.group = paramString; }
  
  public boolean addMember(Principal paramPrincipal) {
    if (this.groupMembers.contains(paramPrincipal))
      return false; 
    if (this.group.equals(paramPrincipal.toString()))
      throw new IllegalArgumentException(); 
    this.groupMembers.addElement(paramPrincipal);
    return true;
  }
  
  public boolean removeMember(Principal paramPrincipal) { return this.groupMembers.removeElement(paramPrincipal); }
  
  public Enumeration<? extends Principal> members() { return this.groupMembers.elements(); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof Group))
      return false; 
    Group group1 = (Group)paramObject;
    return this.group.equals(group1.toString());
  }
  
  public boolean equals(Group paramGroup) { return equals(paramGroup); }
  
  public String toString() { return this.group; }
  
  public int hashCode() { return this.group.hashCode(); }
  
  public boolean isMember(Principal paramPrincipal) {
    if (this.groupMembers.contains(paramPrincipal))
      return true; 
    Vector vector = new Vector(10);
    return isMemberRecurse(paramPrincipal, vector);
  }
  
  public String getName() { return this.group; }
  
  boolean isMemberRecurse(Principal paramPrincipal, Vector<Group> paramVector) {
    Enumeration enumeration = members();
    while (enumeration.hasMoreElements()) {
      boolean bool = false;
      Principal principal = (Principal)enumeration.nextElement();
      if (principal.equals(paramPrincipal))
        return true; 
      if (principal instanceof GroupImpl) {
        GroupImpl groupImpl = (GroupImpl)principal;
        paramVector.addElement(this);
        if (!paramVector.contains(groupImpl))
          bool = groupImpl.isMemberRecurse(paramPrincipal, paramVector); 
      } else if (principal instanceof Group) {
        Group group1 = (Group)principal;
        if (!paramVector.contains(group1))
          bool = group1.isMember(paramPrincipal); 
      } 
      if (bool)
        return bool; 
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\acl\GroupImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */