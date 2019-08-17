package java.security;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public final class Permissions extends PermissionCollection implements Serializable {
  private Map<Class<?>, PermissionCollection> permsMap = new HashMap(11);
  
  private boolean hasUnresolved = false;
  
  PermissionCollection allPermission = null;
  
  private static final long serialVersionUID = 4858622370623524688L;
  
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("perms", Hashtable.class), new ObjectStreamField("allPermission", PermissionCollection.class) };
  
  public void add(Permission paramPermission) {
    PermissionCollection permissionCollection;
    if (isReadOnly())
      throw new SecurityException("attempt to add a Permission to a readonly Permissions object"); 
    synchronized (this) {
      permissionCollection = getPermissionCollection(paramPermission, true);
      permissionCollection.add(paramPermission);
    } 
    if (paramPermission instanceof AllPermission)
      this.allPermission = permissionCollection; 
    if (paramPermission instanceof UnresolvedPermission)
      this.hasUnresolved = true; 
  }
  
  public boolean implies(Permission paramPermission) {
    if (this.allPermission != null)
      return true; 
    synchronized (this) {
      PermissionCollection permissionCollection = getPermissionCollection(paramPermission, false);
      if (permissionCollection != null)
        return permissionCollection.implies(paramPermission); 
      return false;
    } 
  }
  
  public Enumeration<Permission> elements() {
    synchronized (this) {
      return new PermissionsEnumerator(this.permsMap.values().iterator());
    } 
  }
  
  private PermissionCollection getPermissionCollection(Permission paramPermission, boolean paramBoolean) {
    Class clazz = paramPermission.getClass();
    PermissionCollection permissionCollection = (PermissionCollection)this.permsMap.get(clazz);
    if (!this.hasUnresolved && !paramBoolean)
      return permissionCollection; 
    if (permissionCollection == null) {
      permissionCollection = this.hasUnresolved ? getUnresolvedPermissions(paramPermission) : null;
      if (permissionCollection == null && paramBoolean) {
        permissionCollection = paramPermission.newPermissionCollection();
        if (permissionCollection == null)
          permissionCollection = new PermissionsHash(); 
      } 
      if (permissionCollection != null)
        this.permsMap.put(clazz, permissionCollection); 
    } 
    return permissionCollection;
  }
  
  private PermissionCollection getUnresolvedPermissions(Permission paramPermission) {
    UnresolvedPermissionCollection unresolvedPermissionCollection = (UnresolvedPermissionCollection)this.permsMap.get(UnresolvedPermission.class);
    if (unresolvedPermissionCollection == null)
      return null; 
    List list = unresolvedPermissionCollection.getUnresolvedPermissions(paramPermission);
    if (list == null)
      return null; 
    Certificate[] arrayOfCertificate = null;
    Object[] arrayOfObject = paramPermission.getClass().getSigners();
    byte b = 0;
    if (arrayOfObject != null) {
      byte b1;
      for (b1 = 0; b1 < arrayOfObject.length; b1++) {
        if (arrayOfObject[b1] instanceof Certificate)
          b++; 
      } 
      arrayOfCertificate = new Certificate[b];
      b = 0;
      for (b1 = 0; b1 < arrayOfObject.length; b1++) {
        if (arrayOfObject[b1] instanceof Certificate)
          arrayOfCertificate[b++] = (Certificate)arrayOfObject[b1]; 
      } 
    } 
    PermissionCollection permissionCollection = null;
    synchronized (list) {
      int i = list.size();
      for (byte b1 = 0; b1 < i; b1++) {
        UnresolvedPermission unresolvedPermission = (UnresolvedPermission)list.get(b1);
        Permission permission = unresolvedPermission.resolve(paramPermission, arrayOfCertificate);
        if (permission != null) {
          if (permissionCollection == null) {
            permissionCollection = paramPermission.newPermissionCollection();
            if (permissionCollection == null)
              permissionCollection = new PermissionsHash(); 
          } 
          permissionCollection.add(permission);
        } 
      } 
    } 
    return permissionCollection;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    Hashtable hashtable = new Hashtable(this.permsMap.size() * 2);
    synchronized (this) {
      hashtable.putAll(this.permsMap);
    } 
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    putField.put("allPermission", this.allPermission);
    putField.put("perms", hashtable);
    paramObjectOutputStream.writeFields();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    this.allPermission = (PermissionCollection)getField.get("allPermission", null);
    Hashtable hashtable = (Hashtable)getField.get("perms", null);
    this.permsMap = new HashMap(hashtable.size() * 2);
    this.permsMap.putAll(hashtable);
    UnresolvedPermissionCollection unresolvedPermissionCollection = (UnresolvedPermissionCollection)this.permsMap.get(UnresolvedPermission.class);
    this.hasUnresolved = (unresolvedPermissionCollection != null && unresolvedPermissionCollection.elements().hasMoreElements());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\Permissions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */