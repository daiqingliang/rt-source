package java.io;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

final class FilePermissionCollection extends PermissionCollection implements Serializable {
  private List<Permission> perms = new ArrayList();
  
  private static final long serialVersionUID = 2202956749081564585L;
  
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("permissions", Vector.class) };
  
  public void add(Permission paramPermission) {
    if (!(paramPermission instanceof FilePermission))
      throw new IllegalArgumentException("invalid permission: " + paramPermission); 
    if (isReadOnly())
      throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection"); 
    synchronized (this) {
      this.perms.add(paramPermission);
    } 
  }
  
  public boolean implies(Permission paramPermission) {
    if (!(paramPermission instanceof FilePermission))
      return false; 
    FilePermission filePermission = (FilePermission)paramPermission;
    int i = filePermission.getMask();
    int j = 0;
    int k = i;
    synchronized (this) {
      int m = this.perms.size();
      for (byte b = 0; b < m; b++) {
        FilePermission filePermission1 = (FilePermission)this.perms.get(b);
        if ((k & filePermission1.getMask()) != 0 && filePermission1.impliesIgnoreMask(filePermission)) {
          j |= filePermission1.getMask();
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
    for (Permission permission : vector)
      this.perms.add(permission); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\FilePermissionCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */