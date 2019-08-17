package javax.management.relation;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Util;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.management.Notification;
import javax.management.ObjectName;

public class RelationNotification extends Notification {
  private static final long oldSerialVersionUID = -2126464566505527147L;
  
  private static final long newSerialVersionUID = -6871117877523310399L;
  
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("myNewRoleValue", ArrayList.class), new ObjectStreamField("myOldRoleValue", ArrayList.class), new ObjectStreamField("myRelId", String.class), new ObjectStreamField("myRelObjName", ObjectName.class), new ObjectStreamField("myRelTypeName", String.class), new ObjectStreamField("myRoleName", String.class), new ObjectStreamField("myUnregMBeanList", ArrayList.class) };
  
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("newRoleValue", List.class), new ObjectStreamField("oldRoleValue", List.class), new ObjectStreamField("relationId", String.class), new ObjectStreamField("relationObjName", ObjectName.class), new ObjectStreamField("relationTypeName", String.class), new ObjectStreamField("roleName", String.class), new ObjectStreamField("unregisterMBeanList", List.class) };
  
  private static final long serialVersionUID;
  
  private static final ObjectStreamField[] serialPersistentFields;
  
  private static boolean compat = false;
  
  public static final String RELATION_BASIC_CREATION = "jmx.relation.creation.basic";
  
  public static final String RELATION_MBEAN_CREATION = "jmx.relation.creation.mbean";
  
  public static final String RELATION_BASIC_UPDATE = "jmx.relation.update.basic";
  
  public static final String RELATION_MBEAN_UPDATE = "jmx.relation.update.mbean";
  
  public static final String RELATION_BASIC_REMOVAL = "jmx.relation.removal.basic";
  
  public static final String RELATION_MBEAN_REMOVAL = "jmx.relation.removal.mbean";
  
  private String relationId = null;
  
  private String relationTypeName = null;
  
  private ObjectName relationObjName = null;
  
  private List<ObjectName> unregisterMBeanList = null;
  
  private String roleName = null;
  
  private List<ObjectName> oldRoleValue = null;
  
  private List<ObjectName> newRoleValue = null;
  
  public RelationNotification(String paramString1, Object paramObject, long paramLong1, long paramLong2, String paramString2, String paramString3, String paramString4, ObjectName paramObjectName, List<ObjectName> paramList) throws IllegalArgumentException {
    super(paramString1, paramObject, paramLong1, paramLong2, paramString2);
    if (!isValidBasicStrict(paramString1, paramObject, paramString3, paramString4) || !isValidCreate(paramString1))
      throw new IllegalArgumentException("Invalid parameter."); 
    this.relationId = paramString3;
    this.relationTypeName = paramString4;
    this.relationObjName = safeGetObjectName(paramObjectName);
    this.unregisterMBeanList = safeGetObjectNameList(paramList);
  }
  
  public RelationNotification(String paramString1, Object paramObject, long paramLong1, long paramLong2, String paramString2, String paramString3, String paramString4, ObjectName paramObjectName, String paramString5, List<ObjectName> paramList1, List<ObjectName> paramList2) throws IllegalArgumentException {
    super(paramString1, paramObject, paramLong1, paramLong2, paramString2);
    if (!isValidBasicStrict(paramString1, paramObject, paramString3, paramString4) || !isValidUpdate(paramString1, paramString5, paramList1, paramList2))
      throw new IllegalArgumentException("Invalid parameter."); 
    this.relationId = paramString3;
    this.relationTypeName = paramString4;
    this.relationObjName = safeGetObjectName(paramObjectName);
    this.roleName = paramString5;
    this.oldRoleValue = safeGetObjectNameList(paramList2);
    this.newRoleValue = safeGetObjectNameList(paramList1);
  }
  
  public String getRelationId() { return this.relationId; }
  
  public String getRelationTypeName() { return this.relationTypeName; }
  
  public ObjectName getObjectName() { return this.relationObjName; }
  
  public List<ObjectName> getMBeansToUnregister() {
    List list;
    if (this.unregisterMBeanList != null) {
      list = new ArrayList(this.unregisterMBeanList);
    } else {
      list = Collections.emptyList();
    } 
    return list;
  }
  
  public String getRoleName() {
    String str = null;
    if (this.roleName != null)
      str = this.roleName; 
    return str;
  }
  
  public List<ObjectName> getOldRoleValue() {
    List list;
    if (this.oldRoleValue != null) {
      list = new ArrayList(this.oldRoleValue);
    } else {
      list = Collections.emptyList();
    } 
    return list;
  }
  
  public List<ObjectName> getNewRoleValue() {
    List list;
    if (this.newRoleValue != null) {
      list = new ArrayList(this.newRoleValue);
    } else {
      list = Collections.emptyList();
    } 
    return list;
  }
  
  private boolean isValidBasicStrict(String paramString1, Object paramObject, String paramString2, String paramString3) { return (paramObject == null) ? false : isValidBasic(paramString1, paramObject, paramString2, paramString3); }
  
  private boolean isValidBasic(String paramString1, Object paramObject, String paramString2, String paramString3) { return (paramString1 == null || paramString2 == null || paramString3 == null) ? false : (!(paramObject != null && !(paramObject instanceof RelationService) && !(paramObject instanceof ObjectName))); }
  
  private boolean isValidCreate(String paramString) {
    String[] arrayOfString = { "jmx.relation.creation.basic", "jmx.relation.creation.mbean", "jmx.relation.removal.basic", "jmx.relation.removal.mbean" };
    HashSet hashSet = new HashSet(Arrays.asList(arrayOfString));
    return hashSet.contains(paramString);
  }
  
  private boolean isValidUpdate(String paramString1, String paramString2, List<ObjectName> paramList1, List<ObjectName> paramList2) { return (!paramString1.equals("jmx.relation.update.basic") && !paramString1.equals("jmx.relation.update.mbean")) ? false : (!(paramString2 == null || paramList2 == null || paramList1 == null)); }
  
  private ArrayList<ObjectName> safeGetObjectNameList(List<ObjectName> paramList) {
    ArrayList arrayList = null;
    if (paramList != null) {
      arrayList = new ArrayList();
      Iterator iterator = paramList.iterator();
      while (iterator.hasNext()) {
        ObjectName objectName;
        arrayList.add((objectName = (ObjectName)iterator.next()).getInstance(objectName));
      } 
    } 
    return arrayList;
  }
  
  private ObjectName safeGetObjectName(ObjectName paramObjectName) {
    ObjectName objectName = null;
    if (paramObjectName != null)
      objectName = ObjectName.getInstance(paramObjectName); 
    return objectName;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    List list3;
    List list2;
    List list1;
    ObjectName objectName;
    String str3;
    String str2;
    String str1;
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    if (compat) {
      str1 = (String)getField.get("myRelId", null);
      str2 = (String)getField.get("myRelTypeName", null);
      str3 = (String)getField.get("myRoleName", null);
      objectName = (ObjectName)getField.get("myRelObjName", null);
      list1 = (List)Util.cast(getField.get("myNewRoleValue", null));
      list2 = (List)Util.cast(getField.get("myOldRoleValue", null));
      list3 = (List)Util.cast(getField.get("myUnregMBeanList", null));
    } else {
      str1 = (String)getField.get("relationId", null);
      str2 = (String)getField.get("relationTypeName", null);
      str3 = (String)getField.get("roleName", null);
      objectName = (ObjectName)getField.get("relationObjName", null);
      list1 = (List)Util.cast(getField.get("newRoleValue", null));
      list2 = (List)Util.cast(getField.get("oldRoleValue", null));
      list3 = (List)Util.cast(getField.get("unregisterMBeanList", null));
    } 
    String str4 = getType();
    if (!isValidBasic(str4, getSource(), str1, str2) || (!isValidCreate(str4) && !isValidUpdate(str4, str3, list1, list2))) {
      setSource(null);
      throw new InvalidObjectException("Invalid object read");
    } 
    this.relationObjName = safeGetObjectName(objectName);
    this.newRoleValue = safeGetObjectNameList(list1);
    this.oldRoleValue = safeGetObjectNameList(list2);
    this.unregisterMBeanList = safeGetObjectNameList(list3);
    this.relationId = str1;
    this.relationTypeName = str2;
    this.roleName = str3;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (compat) {
      ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
      putField.put("myNewRoleValue", this.newRoleValue);
      putField.put("myOldRoleValue", this.oldRoleValue);
      putField.put("myRelId", this.relationId);
      putField.put("myRelObjName", this.relationObjName);
      putField.put("myRelTypeName", this.relationTypeName);
      putField.put("myRoleName", this.roleName);
      putField.put("myUnregMBeanList", this.unregisterMBeanList);
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
      serialVersionUID = -2126464566505527147L;
    } else {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = -6871117877523310399L;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\relation\RelationNotification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */