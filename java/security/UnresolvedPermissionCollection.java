package java.security;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

final class UnresolvedPermissionCollection extends PermissionCollection implements Serializable {
  private Map<String, List<UnresolvedPermission>> perms = new HashMap(11);
  
  private static final long serialVersionUID = -7176153071733132400L;
  
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("permissions", Hashtable.class) };
  
  public void add(Permission paramPermission) {
    List list;
    if (!(paramPermission instanceof UnresolvedPermission))
      throw new IllegalArgumentException("invalid permission: " + paramPermission); 
    UnresolvedPermission unresolvedPermission = (UnresolvedPermission)paramPermission;
    synchronized (this) {
      list = (List)this.perms.get(unresolvedPermission.getName());
      if (list == null) {
        list = new ArrayList();
        this.perms.put(unresolvedPermission.getName(), list);
      } 
    } 
    synchronized (list) {
      list.add(unresolvedPermission);
    } 
  }
  
  List<UnresolvedPermission> getUnresolvedPermissions(Permission paramPermission) {
    synchronized (this) {
      return (List)this.perms.get(paramPermission.getClass().getName());
    } 
  }
  
  public boolean implies(Permission paramPermission) { return false; }
  
  public Enumeration<Permission> elements() {
    ArrayList arrayList = new ArrayList();
    synchronized (this) {
      for (List list : this.perms.values()) {
        synchronized (list) {
          arrayList.addAll(list);
        } 
      } 
    } 
    return Collections.enumeration(arrayList);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    Hashtable hashtable = new Hashtable(this.perms.size() * 2);
    synchronized (this) {
      Set set = this.perms.entrySet();
      for (Map.Entry entry : set) {
        List list = (List)entry.getValue();
        Vector vector = new Vector(list.size());
        synchronized (list) {
          vector.addAll(list);
        } 
        hashtable.put(entry.getKey(), vector);
      } 
    } 
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    putField.put("permissions", hashtable);
    paramObjectOutputStream.writeFields();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    Hashtable hashtable = (Hashtable)getField.get("permissions", null);
    this.perms = new HashMap(hashtable.size() * 2);
    Set set = hashtable.entrySet();
    for (Map.Entry entry : set) {
      Vector vector = (Vector)entry.getValue();
      ArrayList arrayList = new ArrayList(vector.size());
      arrayList.addAll(vector);
      this.perms.put(entry.getKey(), arrayList);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\UnresolvedPermissionCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */