package java.net;

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

final class SocketPermissionCollection extends PermissionCollection implements Serializable {
  private List<SocketPermission> perms = new ArrayList();
  
  private static final long serialVersionUID = 2787186408602843674L;
  
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("permissions", Vector.class) };
  
  public void add(Permission paramPermission) {
    if (!(paramPermission instanceof SocketPermission))
      throw new IllegalArgumentException("invalid permission: " + paramPermission); 
    if (isReadOnly())
      throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection"); 
    synchronized (this) {
      this.perms.add(0, (SocketPermission)paramPermission);
    } 
  }
  
  public boolean implies(Permission paramPermission) {
    if (!(paramPermission instanceof SocketPermission))
      return false; 
    SocketPermission socketPermission = (SocketPermission)paramPermission;
    int i = socketPermission.getMask();
    int j = 0;
    int k = i;
    synchronized (this) {
      int m = this.perms.size();
      for (byte b = 0; b < m; b++) {
        SocketPermission socketPermission1 = (SocketPermission)this.perms.get(b);
        if ((k & socketPermission1.getMask()) != 0 && socketPermission1.impliesIgnoreMask(socketPermission)) {
          j |= socketPermission1.getMask();
          if ((j & i) == i)
            return true; 
          k = i ^ j;
        } 
      } 
    } 
    return false;
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\SocketPermissionCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */