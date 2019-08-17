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

final class PermissionsHash extends PermissionCollection implements Serializable {
  private Map<Permission, Permission> permsMap = new HashMap(11);
  
  private static final long serialVersionUID = -8491988220802933440L;
  
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("perms", Hashtable.class) };
  
  public void add(Permission paramPermission) {
    synchronized (this) {
      this.permsMap.put(paramPermission, paramPermission);
    } 
  }
  
  public boolean implies(Permission paramPermission) {
    synchronized (this) {
      Permission permission = (Permission)this.permsMap.get(paramPermission);
      if (permission == null) {
        for (Permission permission1 : this.permsMap.values()) {
          if (permission1.implies(paramPermission))
            return true; 
        } 
        return false;
      } 
      return true;
    } 
  }
  
  public Enumeration<Permission> elements() {
    synchronized (this) {
      return Collections.enumeration(this.permsMap.values());
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    Hashtable hashtable = new Hashtable(this.permsMap.size() * 2);
    synchronized (this) {
      hashtable.putAll(this.permsMap);
    } 
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    putField.put("perms", hashtable);
    paramObjectOutputStream.writeFields();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    Hashtable hashtable = (Hashtable)getField.get("perms", null);
    this.permsMap = new HashMap(hashtable.size() * 2);
    this.permsMap.putAll(hashtable);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\PermissionsHash.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */