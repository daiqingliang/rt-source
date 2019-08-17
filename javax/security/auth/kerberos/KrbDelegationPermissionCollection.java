package javax.security.auth.kerberos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

final class KrbDelegationPermissionCollection extends PermissionCollection implements Serializable {
  private List<Permission> perms = new ArrayList();
  
  private static final long serialVersionUID = -3383936936589966948L;
  
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("permissions", Vector.class) };
  
  public boolean implies(Permission paramPermission) {
    if (!(paramPermission instanceof DelegationPermission))
      return false; 
    synchronized (this) {
      for (Permission permission : this.perms) {
        if (permission.implies(paramPermission))
          return true; 
      } 
    } 
    return false;
  }
  
  public void add(Permission paramPermission) {
    if (!(paramPermission instanceof DelegationPermission))
      throw new IllegalArgumentException("invalid permission: " + paramPermission); 
    if (isReadOnly())
      throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection"); 
    synchronized (this) {
      this.perms.add(0, paramPermission);
    } 
  }
  
  public Enumeration<Permission> elements() {
    synchronized (this) {
      return Collections.enumeration(this.perms);
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    Vector vector = new Vector(this.perms.size());
    synchronized (this) {
      vector.addAll(this.perms);
    } 
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    putField.put("permissions", vector);
    paramObjectOutputStream.writeFields();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    Vector vector = (Vector)getField.get("permissions", null);
    this.perms = new ArrayList(vector.size());
    this.perms.addAll(vector);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\auth\kerberos\KrbDelegationPermissionCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */