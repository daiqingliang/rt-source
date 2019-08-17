package javax.management.relation;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Util;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class RelationTypeSupport implements RelationType {
  private static final long oldSerialVersionUID = -8179019472410837190L;
  
  private static final long newSerialVersionUID = 4611072955724144607L;
  
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("myTypeName", String.class), new ObjectStreamField("myRoleName2InfoMap", HashMap.class), new ObjectStreamField("myIsInRelServFlg", boolean.class) };
  
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("typeName", String.class), new ObjectStreamField("roleName2InfoMap", Map.class), new ObjectStreamField("isInRelationService", boolean.class) };
  
  private static final long serialVersionUID;
  
  private static final ObjectStreamField[] serialPersistentFields;
  
  private static boolean compat = false;
  
  private String typeName = null;
  
  private Map<String, RoleInfo> roleName2InfoMap = new HashMap();
  
  private boolean isInRelationService = false;
  
  public RelationTypeSupport(String paramString, RoleInfo[] paramArrayOfRoleInfo) throws IllegalArgumentException, InvalidRelationTypeException {
    if (paramString == null || paramArrayOfRoleInfo == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationTypeSupport.class.getName(), "RelationTypeSupport", paramString);
    initMembers(paramString, paramArrayOfRoleInfo);
    JmxProperties.RELATION_LOGGER.exiting(RelationTypeSupport.class.getName(), "RelationTypeSupport");
  }
  
  protected RelationTypeSupport(String paramString) {
    if (paramString == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationTypeSupport.class.getName(), "RelationTypeSupport", paramString);
    this.typeName = paramString;
    JmxProperties.RELATION_LOGGER.exiting(RelationTypeSupport.class.getName(), "RelationTypeSupport");
  }
  
  public String getRelationTypeName() { return this.typeName; }
  
  public List<RoleInfo> getRoleInfos() { return new ArrayList(this.roleName2InfoMap.values()); }
  
  public RoleInfo getRoleInfo(String paramString) throws IllegalArgumentException, RoleInfoNotFoundException {
    if (paramString == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationTypeSupport.class.getName(), "getRoleInfo", paramString);
    RoleInfo roleInfo = (RoleInfo)this.roleName2InfoMap.get(paramString);
    if (roleInfo == null) {
      StringBuilder stringBuilder = new StringBuilder();
      String str = "No role info for role ";
      stringBuilder.append(str);
      stringBuilder.append(paramString);
      throw new RoleInfoNotFoundException(stringBuilder.toString());
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationTypeSupport.class.getName(), "getRoleInfo");
    return roleInfo;
  }
  
  protected void addRoleInfo(RoleInfo paramRoleInfo) throws IllegalArgumentException, InvalidRelationTypeException {
    if (paramRoleInfo == null) {
      String str1 = "Invalid parameter.";
      throw new IllegalArgumentException(str1);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationTypeSupport.class.getName(), "addRoleInfo", paramRoleInfo);
    if (this.isInRelationService) {
      String str1 = "Relation type cannot be updated as it is declared in the Relation Service.";
      throw new RuntimeException(str1);
    } 
    String str = paramRoleInfo.getName();
    if (this.roleName2InfoMap.containsKey(str)) {
      StringBuilder stringBuilder = new StringBuilder();
      String str1 = "Two role infos provided for role ";
      stringBuilder.append(str1);
      stringBuilder.append(str);
      throw new InvalidRelationTypeException(stringBuilder.toString());
    } 
    this.roleName2InfoMap.put(str, new RoleInfo(paramRoleInfo));
    JmxProperties.RELATION_LOGGER.exiting(RelationTypeSupport.class.getName(), "addRoleInfo");
  }
  
  void setRelationServiceFlag(boolean paramBoolean) { this.isInRelationService = paramBoolean; }
  
  private void initMembers(String paramString, RoleInfo[] paramArrayOfRoleInfo) throws IllegalArgumentException, InvalidRelationTypeException {
    if (paramString == null || paramArrayOfRoleInfo == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationTypeSupport.class.getName(), "initMembers", paramString);
    this.typeName = paramString;
    checkRoleInfos(paramArrayOfRoleInfo);
    for (byte b = 0; b < paramArrayOfRoleInfo.length; b++) {
      RoleInfo roleInfo = paramArrayOfRoleInfo[b];
      this.roleName2InfoMap.put(roleInfo.getName(), new RoleInfo(roleInfo));
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationTypeSupport.class.getName(), "initMembers");
  }
  
  static void checkRoleInfos(RoleInfo[] paramArrayOfRoleInfo) throws IllegalArgumentException, InvalidRelationTypeException {
    if (paramArrayOfRoleInfo == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    if (paramArrayOfRoleInfo.length == 0) {
      String str = "No role info provided.";
      throw new InvalidRelationTypeException(str);
    } 
    HashSet hashSet = new HashSet();
    for (byte b = 0; b < paramArrayOfRoleInfo.length; b++) {
      RoleInfo roleInfo = paramArrayOfRoleInfo[b];
      if (roleInfo == null) {
        String str1 = "Null role info provided.";
        throw new InvalidRelationTypeException(str1);
      } 
      String str = roleInfo.getName();
      if (hashSet.contains(str)) {
        StringBuilder stringBuilder = new StringBuilder();
        String str1 = "Two role infos provided for role ";
        stringBuilder.append(str1);
        stringBuilder.append(str);
        throw new InvalidRelationTypeException(stringBuilder.toString());
      } 
      hashSet.add(str);
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    if (compat) {
      ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
      this.typeName = (String)getField.get("myTypeName", null);
      if (getField.defaulted("myTypeName"))
        throw new NullPointerException("myTypeName"); 
      this.roleName2InfoMap = (Map)Util.cast(getField.get("myRoleName2InfoMap", null));
      if (getField.defaulted("myRoleName2InfoMap"))
        throw new NullPointerException("myRoleName2InfoMap"); 
      this.isInRelationService = getField.get("myIsInRelServFlg", false);
      if (getField.defaulted("myIsInRelServFlg"))
        throw new NullPointerException("myIsInRelServFlg"); 
    } else {
      paramObjectInputStream.defaultReadObject();
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (compat) {
      ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
      putField.put("myTypeName", this.typeName);
      putField.put("myRoleName2InfoMap", this.roleName2InfoMap);
      putField.put("myIsInRelServFlg", this.isInRelationService);
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
      serialVersionUID = -8179019472410837190L;
    } else {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = 4611072955724144607L;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\relation\RelationTypeSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */