package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;

final class PropertyPermissionCollection extends PermissionCollection implements Serializable {
  private Map<String, PropertyPermission> perms = new HashMap(32);
  
  private boolean all_allowed = false;
  
  private static final long serialVersionUID = 7015263904581634791L;
  
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("permissions", Hashtable.class), new ObjectStreamField("all_allowed", boolean.class) };
  
  public void add(Permission paramPermission) {
    if (!(paramPermission instanceof PropertyPermission))
      throw new IllegalArgumentException("invalid permission: " + paramPermission); 
    if (isReadOnly())
      throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection"); 
    PropertyPermission propertyPermission = (PropertyPermission)paramPermission;
    String str = propertyPermission.getName();
    synchronized (this) {
      PropertyPermission propertyPermission1 = (PropertyPermission)this.perms.get(str);
      if (propertyPermission1 != null) {
        int i = propertyPermission1.getMask();
        int j = propertyPermission.getMask();
        if (i != j) {
          int k = i | j;
          String str1 = PropertyPermission.getActions(k);
          this.perms.put(str, new PropertyPermission(str, str1));
        } 
      } else {
        this.perms.put(str, propertyPermission);
      } 
    } 
    if (!this.all_allowed && str.equals("*"))
      this.all_allowed = true; 
  }
  
  public boolean implies(Permission paramPermission) {
    PropertyPermission propertyPermission2;
    if (!(paramPermission instanceof PropertyPermission))
      return false; 
    PropertyPermission propertyPermission1 = (PropertyPermission)paramPermission;
    int i = propertyPermission1.getMask();
    int j = 0;
    if (this.all_allowed) {
      synchronized (this) {
        propertyPermission2 = (PropertyPermission)this.perms.get("*");
      } 
      if (propertyPermission2 != null) {
        j |= propertyPermission2.getMask();
        if ((j & i) == i)
          return true; 
      } 
    } 
    String str = propertyPermission1.getName();
    synchronized (this) {
      propertyPermission2 = (PropertyPermission)this.perms.get(str);
    } 
    if (propertyPermission2 != null) {
      j |= propertyPermission2.getMask();
      if ((j & i) == i)
        return true; 
    } 
    int k;
    int m;
    for (m = str.length() - 1; (k = str.lastIndexOf(".", m)) != -1; m = k - 1) {
      str = str.substring(0, k + 1) + "*";
      synchronized (this) {
        propertyPermission2 = (PropertyPermission)this.perms.get(str);
      } 
      if (propertyPermission2 != null) {
        j |= propertyPermission2.getMask();
        if ((j & i) == i)
          return true; 
      } 
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
    paramObjectOutputStream.writeFields();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    this.all_allowed = getField.get("all_allowed", false);
    Hashtable hashtable = (Hashtable)getField.get("permissions", null);
    this.perms = new HashMap(hashtable.size() * 2);
    this.perms.putAll(hashtable);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\PropertyPermissionCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */