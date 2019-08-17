package java.security;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

final class BasicPermissionCollection extends PermissionCollection implements Serializable {
  private static final long serialVersionUID = 739301742472979399L;
  
  private Map<String, Permission> perms = new HashMap(11);
  
  private boolean all_allowed = false;
  
  private Class<?> permClass;
  
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("permissions", Hashtable.class), new ObjectStreamField("all_allowed", boolean.class), new ObjectStreamField("permClass", Class.class) };
  
  public BasicPermissionCollection(Class<?> paramClass) { this.permClass = paramClass; }
  
  public void add(Permission paramPermission) {
    if (!(paramPermission instanceof BasicPermission))
      throw new IllegalArgumentException("invalid permission: " + paramPermission); 
    if (isReadOnly())
      throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection"); 
    BasicPermission basicPermission = (BasicPermission)paramPermission;
    if (this.permClass == null) {
      this.permClass = basicPermission.getClass();
    } else if (basicPermission.getClass() != this.permClass) {
      throw new IllegalArgumentException("invalid permission: " + paramPermission);
    } 
    synchronized (this) {
      this.perms.put(basicPermission.getCanonicalName(), paramPermission);
    } 
    if (!this.all_allowed && basicPermission.getCanonicalName().equals("*"))
      this.all_allowed = true; 
  }
  
  public boolean implies(Permission paramPermission) {
    Permission permission;
    if (!(paramPermission instanceof BasicPermission))
      return false; 
    BasicPermission basicPermission = (BasicPermission)paramPermission;
    if (basicPermission.getClass() != this.permClass)
      return false; 
    if (this.all_allowed)
      return true; 
    String str = basicPermission.getCanonicalName();
    synchronized (this) {
      permission = (Permission)this.perms.get(str);
    } 
    if (permission != null)
      return permission.implies(paramPermission); 
    int i;
    int j;
    for (j = str.length() - 1; (i = str.lastIndexOf(".", j)) != -1; j = i - 1) {
      str = str.substring(0, i + 1) + "*";
      synchronized (this) {
        permission = (Permission)this.perms.get(str);
      } 
      if (permission != null)
        return permission.implies(paramPermission); 
    } 
    return false;
  }
  
  public Enumeration<Permission> elements() {
    synchronized (this) {
      return Collections.enumeration(this.perms.values());
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    Hashtable hashtable = new Hashtable(this.perms.size() * 2);
    synchronized (this) {
      hashtable.putAll(this.perms);
    } 
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    putField.put("all_allowed", this.all_allowed);
    putField.put("permissions", hashtable);
    putField.put("permClass", this.permClass);
    paramObjectOutputStream.writeFields();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    Hashtable hashtable = (Hashtable)getField.get("permissions", null);
    this.perms = new HashMap(hashtable.size() * 2);
    this.perms.putAll(hashtable);
    this.all_allowed = getField.get("all_allowed", false);
    this.permClass = (Class)getField.get("permClass", null);
    if (this.permClass == null) {
      Enumeration enumeration = hashtable.elements();
      if (enumeration.hasMoreElements()) {
        Permission permission = (Permission)enumeration.nextElement();
        this.permClass = permission.getClass();
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\BasicPermissionCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */