package javax.management.relation;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.AccessController;
import javax.management.NotCompliantMBeanException;

public class RoleInfo implements Serializable {
  private static final long oldSerialVersionUID = 7227256952085334351L;
  
  private static final long newSerialVersionUID = 2504952983494636987L;
  
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("myName", String.class), new ObjectStreamField("myIsReadableFlg", boolean.class), new ObjectStreamField("myIsWritableFlg", boolean.class), new ObjectStreamField("myDescription", String.class), new ObjectStreamField("myMinDegree", int.class), new ObjectStreamField("myMaxDegree", int.class), new ObjectStreamField("myRefMBeanClassName", String.class) };
  
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("name", String.class), new ObjectStreamField("isReadable", boolean.class), new ObjectStreamField("isWritable", boolean.class), new ObjectStreamField("description", String.class), new ObjectStreamField("minDegree", int.class), new ObjectStreamField("maxDegree", int.class), new ObjectStreamField("referencedMBeanClassName", String.class) };
  
  private static final long serialVersionUID;
  
  private static final ObjectStreamField[] serialPersistentFields;
  
  private static boolean compat = false;
  
  public static final int ROLE_CARDINALITY_INFINITY = -1;
  
  private String name = null;
  
  private boolean isReadable;
  
  private boolean isWritable;
  
  private String description = null;
  
  private int minDegree;
  
  private int maxDegree;
  
  private String referencedMBeanClassName = null;
  
  public RoleInfo(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2, String paramString3) throws IllegalArgumentException, InvalidRoleInfoException, ClassNotFoundException, NotCompliantMBeanException { init(paramString1, paramString2, paramBoolean1, paramBoolean2, paramInt1, paramInt2, paramString3); }
  
  public RoleInfo(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2) throws IllegalArgumentException, ClassNotFoundException, NotCompliantMBeanException {
    try {
      init(paramString1, paramString2, paramBoolean1, paramBoolean2, 1, 1, null);
    } catch (InvalidRoleInfoException invalidRoleInfoException) {}
  }
  
  public RoleInfo(String paramString1, String paramString2) throws IllegalArgumentException, ClassNotFoundException, NotCompliantMBeanException {
    try {
      init(paramString1, paramString2, true, true, 1, 1, null);
    } catch (InvalidRoleInfoException invalidRoleInfoException) {}
  }
  
  public RoleInfo(RoleInfo paramRoleInfo) throws IllegalArgumentException {
    if (paramRoleInfo == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    try {
      init(paramRoleInfo.getName(), paramRoleInfo.getRefMBeanClassName(), paramRoleInfo.isReadable(), paramRoleInfo.isWritable(), paramRoleInfo.getMinDegree(), paramRoleInfo.getMaxDegree(), paramRoleInfo.getDescription());
    } catch (InvalidRoleInfoException invalidRoleInfoException) {}
  }
  
  public String getName() { return this.name; }
  
  public boolean isReadable() { return this.isReadable; }
  
  public boolean isWritable() { return this.isWritable; }
  
  public String getDescription() { return this.description; }
  
  public int getMinDegree() { return this.minDegree; }
  
  public int getMaxDegree() { return this.maxDegree; }
  
  public String getRefMBeanClassName() { return this.referencedMBeanClassName; }
  
  public boolean checkMinDegree(int paramInt) { return (paramInt >= -1 && (this.minDegree == -1 || paramInt >= this.minDegree)); }
  
  public boolean checkMaxDegree(int paramInt) { return (paramInt >= -1 && (this.maxDegree == -1 || (paramInt != -1 && paramInt <= this.maxDegree))); }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("role info name: " + this.name);
    stringBuilder.append("; isReadable: " + this.isReadable);
    stringBuilder.append("; isWritable: " + this.isWritable);
    stringBuilder.append("; description: " + this.description);
    stringBuilder.append("; minimum degree: " + this.minDegree);
    stringBuilder.append("; maximum degree: " + this.maxDegree);
    stringBuilder.append("; MBean class: " + this.referencedMBeanClassName);
    return stringBuilder.toString();
  }
  
  private void init(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2, String paramString3) throws IllegalArgumentException, InvalidRoleInfoException, ClassNotFoundException, NotCompliantMBeanException {
    if (paramString1 == null || paramString2 == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    this.name = paramString1;
    this.isReadable = paramBoolean1;
    this.isWritable = paramBoolean2;
    if (paramString3 != null)
      this.description = paramString3; 
    boolean bool = false;
    StringBuilder stringBuilder = new StringBuilder();
    if (paramInt2 != -1 && (paramInt1 == -1 || paramInt1 > paramInt2)) {
      stringBuilder.append("Minimum degree ");
      stringBuilder.append(paramInt1);
      stringBuilder.append(" is greater than maximum degree ");
      stringBuilder.append(paramInt2);
      bool = true;
    } else if (paramInt1 < -1 || paramInt2 < -1) {
      stringBuilder.append("Minimum or maximum degree has an illegal value, must be [0, ROLE_CARDINALITY_INFINITY].");
      bool = true;
    } 
    if (bool)
      throw new InvalidRoleInfoException(stringBuilder.toString()); 
    this.minDegree = paramInt1;
    this.maxDegree = paramInt2;
    this.referencedMBeanClassName = paramString2;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    if (compat) {
      ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
      this.name = (String)getField.get("myName", null);
      if (getField.defaulted("myName"))
        throw new NullPointerException("myName"); 
      this.isReadable = getField.get("myIsReadableFlg", false);
      if (getField.defaulted("myIsReadableFlg"))
        throw new NullPointerException("myIsReadableFlg"); 
      this.isWritable = getField.get("myIsWritableFlg", false);
      if (getField.defaulted("myIsWritableFlg"))
        throw new NullPointerException("myIsWritableFlg"); 
      this.description = (String)getField.get("myDescription", null);
      if (getField.defaulted("myDescription"))
        throw new NullPointerException("myDescription"); 
      this.minDegree = getField.get("myMinDegree", 0);
      if (getField.defaulted("myMinDegree"))
        throw new NullPointerException("myMinDegree"); 
      this.maxDegree = getField.get("myMaxDegree", 0);
      if (getField.defaulted("myMaxDegree"))
        throw new NullPointerException("myMaxDegree"); 
      this.referencedMBeanClassName = (String)getField.get("myRefMBeanClassName", null);
      if (getField.defaulted("myRefMBeanClassName"))
        throw new NullPointerException("myRefMBeanClassName"); 
    } else {
      paramObjectInputStream.defaultReadObject();
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (compat) {
      ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
      putField.put("myName", this.name);
      putField.put("myIsReadableFlg", this.isReadable);
      putField.put("myIsWritableFlg", this.isWritable);
      putField.put("myDescription", this.description);
      putField.put("myMinDegree", this.minDegree);
      putField.put("myMaxDegree", this.maxDegree);
      putField.put("myRefMBeanClassName", this.referencedMBeanClassName);
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
      serialVersionUID = 7227256952085334351L;
    } else {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = 2504952983494636987L;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\relation\RoleInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */