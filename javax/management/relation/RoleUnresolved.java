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

public class RoleUnresolved implements Serializable {
  private static final long oldSerialVersionUID = -9026457686611660144L;
  
  private static final long newSerialVersionUID = -48350262537070138L;
  
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("myRoleName", String.class), new ObjectStreamField("myRoleValue", ArrayList.class), new ObjectStreamField("myPbType", int.class) };
  
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("roleName", String.class), new ObjectStreamField("roleValue", List.class), new ObjectStreamField("problemType", int.class) };
  
  private static final long serialVersionUID;
  
  private static final ObjectStreamField[] serialPersistentFields;
  
  private static boolean compat = false;
  
  private String roleName = null;
  
  private List<ObjectName> roleValue = null;
  
  private int problemType;
  
  public RoleUnresolved(String paramString, List<ObjectName> paramList, int paramInt) throws IllegalArgumentException {
    if (paramString == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    setRoleName(paramString);
    setRoleValue(paramList);
    setProblemType(paramInt);
  }
  
  public String getRoleName() { return this.roleName; }
  
  public List<ObjectName> getRoleValue() { return this.roleValue; }
  
  public int getProblemType() { return this.problemType; }
  
  public void setRoleName(String paramString) throws IllegalArgumentException {
    if (paramString == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    this.roleName = paramString;
  }
  
  public void setRoleValue(List<ObjectName> paramList) {
    if (paramList != null) {
      this.roleValue = new ArrayList(paramList);
    } else {
      this.roleValue = null;
    } 
  }
  
  public void setProblemType(int paramInt) throws IllegalArgumentException {
    if (!RoleStatus.isRoleStatus(paramInt)) {
      String str = "Incorrect problem type.";
      throw new IllegalArgumentException(str);
    } 
    this.problemType = paramInt;
  }
  
  public Object clone() {
    try {
      return new RoleUnresolved(this.roleName, this.roleValue, this.problemType);
    } catch (IllegalArgumentException illegalArgumentException) {
      return null;
    } 
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("role name: " + this.roleName);
    if (this.roleValue != null) {
      stringBuilder.append("; value: ");
      Iterator iterator = this.roleValue.iterator();
      while (iterator.hasNext()) {
        ObjectName objectName = (ObjectName)iterator.next();
        stringBuilder.append(objectName.toString());
        if (iterator.hasNext())
          stringBuilder.append(", "); 
      } 
    } 
    stringBuilder.append("; problem type: " + this.problemType);
    return stringBuilder.toString();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    if (compat) {
      ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
      this.roleName = (String)getField.get("myRoleName", null);
      if (getField.defaulted("myRoleName"))
        throw new NullPointerException("myRoleName"); 
      this.roleValue = (List)Util.cast(getField.get("myRoleValue", null));
      if (getField.defaulted("myRoleValue"))
        throw new NullPointerException("myRoleValue"); 
      this.problemType = getField.get("myPbType", 0);
      if (getField.defaulted("myPbType"))
        throw new NullPointerException("myPbType"); 
    } else {
      paramObjectInputStream.defaultReadObject();
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (compat) {
      ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
      putField.put("myRoleName", this.roleName);
      putField.put("myRoleValue", this.roleValue);
      putField.put("myPbType", this.problemType);
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
      serialVersionUID = -9026457686611660144L;
    } else {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = -48350262537070138L;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\relation\RoleUnresolved.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */