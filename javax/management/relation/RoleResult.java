package javax.management.relation;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.AccessController;

public class RoleResult implements Serializable {
  private static final long oldSerialVersionUID = 3786616013762091099L;
  
  private static final long newSerialVersionUID = -6304063118040985512L;
  
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("myRoleList", RoleList.class), new ObjectStreamField("myRoleUnresList", RoleUnresolvedList.class) };
  
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("roleList", RoleList.class), new ObjectStreamField("unresolvedRoleList", RoleUnresolvedList.class) };
  
  private static final long serialVersionUID;
  
  private static final ObjectStreamField[] serialPersistentFields;
  
  private static boolean compat = false;
  
  private RoleList roleList = null;
  
  private RoleUnresolvedList unresolvedRoleList = null;
  
  public RoleResult(RoleList paramRoleList, RoleUnresolvedList paramRoleUnresolvedList) {
    setRoles(paramRoleList);
    setRolesUnresolved(paramRoleUnresolvedList);
  }
  
  public RoleList getRoles() { return this.roleList; }
  
  public RoleUnresolvedList getRolesUnresolved() { return this.unresolvedRoleList; }
  
  public void setRoles(RoleList paramRoleList) {
    if (paramRoleList != null) {
      this.roleList = new RoleList();
      for (Role role : paramRoleList)
        this.roleList.add((Role)role.clone()); 
    } else {
      this.roleList = null;
    } 
  }
  
  public void setRolesUnresolved(RoleUnresolvedList paramRoleUnresolvedList) {
    if (paramRoleUnresolvedList != null) {
      this.unresolvedRoleList = new RoleUnresolvedList();
      for (RoleUnresolved roleUnresolved : paramRoleUnresolvedList)
        this.unresolvedRoleList.add((RoleUnresolved)roleUnresolved.clone()); 
    } else {
      this.unresolvedRoleList = null;
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    if (compat) {
      ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
      this.roleList = (RoleList)getField.get("myRoleList", null);
      if (getField.defaulted("myRoleList"))
        throw new NullPointerException("myRoleList"); 
      this.unresolvedRoleList = (RoleUnresolvedList)getField.get("myRoleUnresList", null);
      if (getField.defaulted("myRoleUnresList"))
        throw new NullPointerException("myRoleUnresList"); 
    } else {
      paramObjectInputStream.defaultReadObject();
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (compat) {
      ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
      putField.put("myRoleList", this.roleList);
      putField.put("myRoleUnresList", this.unresolvedRoleList);
      paramObjectOutputStream.writeFields();
    } else {
      paramObjectOutputStream.defaultWriteObject();
    } 
  }
  
  static  {
    try {
      GetPropertyAction getPropertyAction = new GetPropertyAction("jmx.serial.form");
      String str = (String)AccessController.doPrivileged(getPropertyAction);
      compat = (str != null && str.equals("1.0"));
    } catch (Exception exception) {}
    if (compat) {
      serialPersistentFields = oldSerialPersistentFields;
      serialVersionUID = 3786616013762091099L;
    } else {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = -6304063118040985512L;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\relation\RoleResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */