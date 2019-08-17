package javax.management.relation;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Util;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.management.ObjectName;

public class Role implements Serializable {
  private static final long oldSerialVersionUID = -1959486389343113026L;
  
  private static final long newSerialVersionUID = -279985518429862552L;
  
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("myName", String.class), new ObjectStreamField("myObjNameList", ArrayList.class) };
  
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("name", String.class), new ObjectStreamField("objectNameList", List.class) };
  
  private static final long serialVersionUID;
  
  private static final ObjectStreamField[] serialPersistentFields;
  
  private static boolean compat = false;
  
  private String name = null;
  
  private List<ObjectName> objectNameList = new ArrayList();
  
  public Role(String paramString, List<ObjectName> paramList) throws IllegalArgumentException {
    if (paramString == null || paramList == null) {
      String str = "Invalid parameter";
      throw new IllegalArgumentException(str);
    } 
    setRoleName(paramString);
    setRoleValue(paramList);
  }
  
  public String getRoleName() { return this.name; }
  
  public List<ObjectName> getRoleValue() { return this.objectNameList; }
  
  public void setRoleName(String paramString) throws IllegalArgumentException {
    if (paramString == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    this.name = paramString;
  }
  
  public void setRoleValue(List<ObjectName> paramList) throws IllegalArgumentException {
    if (paramList == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    this.objectNameList = new ArrayList(paramList);
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("role name: " + this.name + "; role value: ");
    Iterator iterator = this.objectNameList.iterator();
    while (iterator.hasNext()) {
      ObjectName objectName = (ObjectName)iterator.next();
      stringBuilder.append(objectName.toString());
      if (iterator.hasNext())
        stringBuilder.append(", "); 
    } 
    return stringBuilder.toString();
  }
  
  public Object clone() {
    try {
      return new Role(this.name, this.objectNameList);
    } catch (IllegalArgumentException illegalArgumentException) {
      return null;
    } 
  }
  
  public static String roleValueToString(List<ObjectName> paramList) throws IllegalArgumentException {
    if (paramList == null) {
      String str = "Invalid parameter";
      throw new IllegalArgumentException(str);
    } 
    StringBuilder stringBuilder = new StringBuilder();
    for (ObjectName objectName : paramList) {
      if (stringBuilder.length() > 0)
        stringBuilder.append("\n"); 
      stringBuilder.append(objectName.toString());
    } 
    return stringBuilder.toString();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    if (compat) {
      ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
      this.name = (String)getField.get("myName", null);
      if (getField.defaulted("myName"))
        throw new NullPointerException("myName"); 
      this.objectNameList = (List)Util.cast(getField.get("myObjNameList", null));
      if (getField.defaulted("myObjNameList"))
        throw new NullPointerException("myObjNameList"); 
    } else {
      paramObjectInputStream.defaultReadObject();
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (compat) {
      ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
      putField.put("myName", this.name);
      putField.put("myObjNameList", this.objectNameList);
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
      serialVersionUID = -1959486389343113026L;
    } else {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = -279985518429862552L;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\relation\Role.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */