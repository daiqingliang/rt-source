package sun.security.acl;

import java.security.Principal;
import java.security.acl.Acl;
import java.security.acl.AclEntry;
import java.security.acl.Group;
import java.security.acl.NotOwnerException;
import java.security.acl.Permission;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class AclImpl extends OwnerImpl implements Acl {
  private Hashtable<Principal, AclEntry> allowedUsersTable = new Hashtable(23);
  
  private Hashtable<Principal, AclEntry> allowedGroupsTable = new Hashtable(23);
  
  private Hashtable<Principal, AclEntry> deniedUsersTable = new Hashtable(23);
  
  private Hashtable<Principal, AclEntry> deniedGroupsTable = new Hashtable(23);
  
  private String aclName = null;
  
  private Vector<Permission> zeroSet = new Vector(1, 1);
  
  public AclImpl(Principal paramPrincipal, String paramString) {
    super(paramPrincipal);
    try {
      setName(paramPrincipal, paramString);
    } catch (Exception exception) {}
  }
  
  public void setName(Principal paramPrincipal, String paramString) {
    if (!isOwner(paramPrincipal))
      throw new NotOwnerException(); 
    this.aclName = paramString;
  }
  
  public String getName() { return this.aclName; }
  
  public boolean addEntry(Principal paramPrincipal, AclEntry paramAclEntry) throws NotOwnerException {
    if (!isOwner(paramPrincipal))
      throw new NotOwnerException(); 
    Hashtable hashtable = findTable(paramAclEntry);
    Principal principal = paramAclEntry.getPrincipal();
    if (hashtable.get(principal) != null)
      return false; 
    hashtable.put(principal, paramAclEntry);
    return true;
  }
  
  public boolean removeEntry(Principal paramPrincipal, AclEntry paramAclEntry) throws NotOwnerException {
    if (!isOwner(paramPrincipal))
      throw new NotOwnerException(); 
    Hashtable hashtable = findTable(paramAclEntry);
    Principal principal = paramAclEntry.getPrincipal();
    AclEntry aclEntry = (AclEntry)hashtable.remove(principal);
    return (aclEntry != null);
  }
  
  public Enumeration<Permission> getPermissions(Principal paramPrincipal) {
    Enumeration enumeration3 = subtract(getGroupPositive(paramPrincipal), getGroupNegative(paramPrincipal));
    Enumeration enumeration4 = subtract(getGroupNegative(paramPrincipal), getGroupPositive(paramPrincipal));
    Enumeration enumeration1 = subtract(getIndividualPositive(paramPrincipal), getIndividualNegative(paramPrincipal));
    Enumeration enumeration2 = subtract(getIndividualNegative(paramPrincipal), getIndividualPositive(paramPrincipal));
    Enumeration enumeration5 = subtract(enumeration3, enumeration2);
    Enumeration enumeration6 = union(enumeration1, enumeration5);
    enumeration1 = subtract(getIndividualPositive(paramPrincipal), getIndividualNegative(paramPrincipal));
    enumeration2 = subtract(getIndividualNegative(paramPrincipal), getIndividualPositive(paramPrincipal));
    enumeration5 = subtract(enumeration4, enumeration1);
    Enumeration enumeration7 = union(enumeration2, enumeration5);
    return subtract(enumeration6, enumeration7);
  }
  
  public boolean checkPermission(Principal paramPrincipal, Permission paramPermission) {
    Enumeration enumeration = getPermissions(paramPrincipal);
    while (enumeration.hasMoreElements()) {
      Permission permission = (Permission)enumeration.nextElement();
      if (permission.equals(paramPermission))
        return true; 
    } 
    return false;
  }
  
  public Enumeration<AclEntry> entries() { return new AclEnumerator(this, this.allowedUsersTable, this.allowedGroupsTable, this.deniedUsersTable, this.deniedGroupsTable); }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    Enumeration enumeration = entries();
    while (enumeration.hasMoreElements()) {
      AclEntry aclEntry = (AclEntry)enumeration.nextElement();
      stringBuffer.append(aclEntry.toString().trim());
      stringBuffer.append("\n");
    } 
    return stringBuffer.toString();
  }
  
  private Hashtable<Principal, AclEntry> findTable(AclEntry paramAclEntry) {
    Hashtable hashtable = null;
    Principal principal = paramAclEntry.getPrincipal();
    if (principal instanceof Group) {
      if (paramAclEntry.isNegative()) {
        hashtable = this.deniedGroupsTable;
      } else {
        hashtable = this.allowedGroupsTable;
      } 
    } else if (paramAclEntry.isNegative()) {
      hashtable = this.deniedUsersTable;
    } else {
      hashtable = this.allowedUsersTable;
    } 
    return hashtable;
  }
  
  private static Enumeration<Permission> union(Enumeration<Permission> paramEnumeration1, Enumeration<Permission> paramEnumeration2) {
    Vector vector = new Vector(20, 20);
    while (paramEnumeration1.hasMoreElements())
      vector.addElement(paramEnumeration1.nextElement()); 
    while (paramEnumeration2.hasMoreElements()) {
      Permission permission = (Permission)paramEnumeration2.nextElement();
      if (!vector.contains(permission))
        vector.addElement(permission); 
    } 
    return vector.elements();
  }
  
  private Enumeration<Permission> subtract(Enumeration<Permission> paramEnumeration1, Enumeration<Permission> paramEnumeration2) {
    Vector vector = new Vector(20, 20);
    while (paramEnumeration1.hasMoreElements())
      vector.addElement(paramEnumeration1.nextElement()); 
    while (paramEnumeration2.hasMoreElements()) {
      Permission permission = (Permission)paramEnumeration2.nextElement();
      if (vector.contains(permission))
        vector.removeElement(permission); 
    } 
    return vector.elements();
  }
  
  private Enumeration<Permission> getGroupPositive(Principal paramPrincipal) {
    Enumeration enumeration1 = this.zeroSet.elements();
    Enumeration enumeration2 = this.allowedGroupsTable.keys();
    while (enumeration2.hasMoreElements()) {
      Group group = (Group)enumeration2.nextElement();
      if (group.isMember(paramPrincipal)) {
        AclEntry aclEntry = (AclEntry)this.allowedGroupsTable.get(group);
        enumeration1 = union(aclEntry.permissions(), enumeration1);
      } 
    } 
    return enumeration1;
  }
  
  private Enumeration<Permission> getGroupNegative(Principal paramPrincipal) {
    Enumeration enumeration1 = this.zeroSet.elements();
    Enumeration enumeration2 = this.deniedGroupsTable.keys();
    while (enumeration2.hasMoreElements()) {
      Group group = (Group)enumeration2.nextElement();
      if (group.isMember(paramPrincipal)) {
        AclEntry aclEntry = (AclEntry)this.deniedGroupsTable.get(group);
        enumeration1 = union(aclEntry.permissions(), enumeration1);
      } 
    } 
    return enumeration1;
  }
  
  private Enumeration<Permission> getIndividualPositive(Principal paramPrincipal) {
    Enumeration enumeration = this.zeroSet.elements();
    AclEntry aclEntry = (AclEntry)this.allowedUsersTable.get(paramPrincipal);
    if (aclEntry != null)
      enumeration = aclEntry.permissions(); 
    return enumeration;
  }
  
  private Enumeration<Permission> getIndividualNegative(Principal paramPrincipal) {
    Enumeration enumeration = this.zeroSet.elements();
    AclEntry aclEntry = (AclEntry)this.deniedUsersTable.get(paramPrincipal);
    if (aclEntry != null)
      enumeration = aclEntry.permissions(); 
    return enumeration;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\acl\AclImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */