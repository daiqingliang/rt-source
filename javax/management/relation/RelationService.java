package javax.management.relation;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.Util;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.MBeanServerDelegate;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public class RelationService extends NotificationBroadcasterSupport implements RelationServiceMBean, MBeanRegistration, NotificationListener {
  private Map<String, Object> myRelId2ObjMap = new HashMap();
  
  private Map<String, String> myRelId2RelTypeMap = new HashMap();
  
  private Map<ObjectName, String> myRelMBeanObjName2RelIdMap = new HashMap();
  
  private Map<String, RelationType> myRelType2ObjMap = new HashMap();
  
  private Map<String, List<String>> myRelType2RelIdsMap = new HashMap();
  
  private final Map<ObjectName, Map<String, List<String>>> myRefedMBeanObjName2RelIdsMap = new HashMap();
  
  private boolean myPurgeFlag = true;
  
  private final AtomicLong atomicSeqNo = new AtomicLong();
  
  private ObjectName myObjName = null;
  
  private MBeanServer myMBeanServer = null;
  
  private MBeanServerNotificationFilter myUnregNtfFilter = null;
  
  private List<MBeanServerNotification> myUnregNtfList = new ArrayList();
  
  public RelationService(boolean paramBoolean) {
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "RelationService");
    setPurgeFlag(paramBoolean);
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "RelationService");
  }
  
  public void isActive() throws RelationServiceNotRegisteredException {
    if (this.myMBeanServer == null) {
      String str = "Relation Service not registered in the MBean Server.";
      throw new RelationServiceNotRegisteredException(str);
    } 
  }
  
  public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName) throws Exception {
    this.myMBeanServer = paramMBeanServer;
    this.myObjName = paramObjectName;
    return paramObjectName;
  }
  
  public void postRegister(Boolean paramBoolean) {}
  
  public void preDeregister() throws RelationServiceNotRegisteredException {}
  
  public void postDeregister() throws RelationServiceNotRegisteredException {}
  
  public boolean getPurgeFlag() { return this.myPurgeFlag; }
  
  public void setPurgeFlag(boolean paramBoolean) { this.myPurgeFlag = paramBoolean; }
  
  public void createRelationType(String paramString, RoleInfo[] paramArrayOfRoleInfo) throws IllegalArgumentException, InvalidRelationTypeException {
    if (paramString == null || paramArrayOfRoleInfo == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "createRelationType", paramString);
    RelationTypeSupport relationTypeSupport = new RelationTypeSupport(paramString, paramArrayOfRoleInfo);
    addRelationTypeInt(relationTypeSupport);
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "createRelationType");
  }
  
  public void addRelationType(RelationType paramRelationType) throws IllegalArgumentException, InvalidRelationTypeException {
    if (paramRelationType == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "addRelationType");
    List list = paramRelationType.getRoleInfos();
    if (list == null) {
      String str = "No role info provided.";
      throw new InvalidRelationTypeException(str);
    } 
    RoleInfo[] arrayOfRoleInfo = new RoleInfo[list.size()];
    byte b = 0;
    for (RoleInfo roleInfo : list) {
      arrayOfRoleInfo[b] = roleInfo;
      b++;
    } 
    RelationTypeSupport.checkRoleInfos(arrayOfRoleInfo);
    addRelationTypeInt(paramRelationType);
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "addRelationType");
  }
  
  public List<String> getAllRelationTypeNames() {
    ArrayList arrayList;
    synchronized (this.myRelType2ObjMap) {
      arrayList = new ArrayList(this.myRelType2ObjMap.keySet());
    } 
    return arrayList;
  }
  
  public List<RoleInfo> getRoleInfos(String paramString) throws IllegalArgumentException, RelationTypeNotFoundException {
    if (paramString == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRoleInfos", paramString);
    RelationType relationType = getRelationType(paramString);
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRoleInfos");
    return relationType.getRoleInfos();
  }
  
  public RoleInfo getRoleInfo(String paramString1, String paramString2) throws IllegalArgumentException, RelationTypeNotFoundException, RoleInfoNotFoundException {
    if (paramString1 == null || paramString2 == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRoleInfo", new Object[] { paramString1, paramString2 });
    RelationType relationType = getRelationType(paramString1);
    RoleInfo roleInfo = relationType.getRoleInfo(paramString2);
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRoleInfo");
    return roleInfo;
  }
  
  public void removeRelationType(String paramString) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationTypeNotFoundException {
    isActive();
    if (paramString == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "removeRelationType", paramString);
    RelationType relationType = getRelationType(paramString);
    ArrayList arrayList = null;
    synchronized (this.myRelType2RelIdsMap) {
      List list = (List)this.myRelType2RelIdsMap.get(paramString);
      if (list != null)
        arrayList = new ArrayList(list); 
    } 
    synchronized (this.myRelType2ObjMap) {
      this.myRelType2ObjMap.remove(paramString);
    } 
    synchronized (this.myRelType2RelIdsMap) {
      this.myRelType2RelIdsMap.remove(paramString);
    } 
    if (arrayList != null)
      for (String str : arrayList) {
        try {
          removeRelation(str);
        } catch (RelationNotFoundException relationNotFoundException) {
          throw new RuntimeException(relationNotFoundException.getMessage());
        } 
      }  
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "removeRelationType");
  }
  
  public void createRelation(String paramString1, String paramString2, RoleList paramRoleList) throws RelationServiceNotRegisteredException, IllegalArgumentException, RoleNotFoundException, InvalidRelationIdException, RelationTypeNotFoundException, InvalidRoleValueException {
    isActive();
    if (paramString1 == null || paramString2 == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "createRelation", new Object[] { paramString1, paramString2, paramRoleList });
    RelationSupport relationSupport = new RelationSupport(paramString1, this.myObjName, paramString2, paramRoleList);
    addRelationInt(true, relationSupport, null, paramString1, paramString2, paramRoleList);
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "createRelation");
  }
  
  public void addRelation(ObjectName paramObjectName) throws IllegalArgumentException, RelationServiceNotRegisteredException, NoSuchMethodException, InvalidRelationIdException, InstanceNotFoundException, InvalidRelationServiceException, RelationTypeNotFoundException, RoleNotFoundException, InvalidRoleValueException {
    RoleList roleList;
    String str2;
    ObjectName objectName;
    String str1;
    if (paramObjectName == null) {
      str1 = "Invalid parameter.";
      throw new IllegalArgumentException(str1);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "addRelation", paramObjectName);
    isActive();
    if (!this.myMBeanServer.isInstanceOf(paramObjectName, "javax.management.relation.Relation")) {
      str1 = "This MBean does not implement the Relation interface.";
      throw new NoSuchMethodException(str1);
    } 
    try {
      str1 = (String)this.myMBeanServer.getAttribute(paramObjectName, "RelationId");
    } catch (MBeanException null) {
      throw new RuntimeException(objectName.getTargetException().getMessage());
    } catch (ReflectionException null) {
      throw new RuntimeException(objectName.getMessage());
    } catch (AttributeNotFoundException null) {
      throw new RuntimeException(objectName.getMessage());
    } 
    if (str1 == null) {
      objectName = "This MBean does not provide a relation id.";
      throw new InvalidRelationIdException(objectName);
    } 
    try {
      objectName = (ObjectName)this.myMBeanServer.getAttribute(paramObjectName, "RelationServiceName");
    } catch (MBeanException mBeanException) {
      throw new RuntimeException(mBeanException.getTargetException().getMessage());
    } catch (ReflectionException reflectionException) {
      throw new RuntimeException(reflectionException.getMessage());
    } catch (AttributeNotFoundException attributeNotFoundException) {
      throw new RuntimeException(attributeNotFoundException.getMessage());
    } 
    boolean bool = false;
    if (objectName == null) {
      bool = true;
    } else if (!objectName.equals(this.myObjName)) {
      bool = true;
    } 
    if (bool) {
      str2 = "The Relation Service referenced in the MBean is not the current one.";
      throw new InvalidRelationServiceException(str2);
    } 
    try {
      str2 = (String)this.myMBeanServer.getAttribute(paramObjectName, "RelationTypeName");
    } catch (MBeanException null) {
      throw new RuntimeException(roleList.getTargetException().getMessage());
    } catch (ReflectionException null) {
      throw new RuntimeException(roleList.getMessage());
    } catch (AttributeNotFoundException null) {
      throw new RuntimeException(roleList.getMessage());
    } 
    if (str2 == null) {
      roleList = "No relation type provided.";
      throw new RelationTypeNotFoundException(roleList);
    } 
    try {
      roleList = (RoleList)this.myMBeanServer.invoke(paramObjectName, "retrieveAllRoles", null, null);
    } catch (MBeanException mBeanException) {
      throw new RuntimeException(mBeanException.getTargetException().getMessage());
    } catch (ReflectionException reflectionException) {
      throw new RuntimeException(reflectionException.getMessage());
    } 
    addRelationInt(false, null, paramObjectName, str1, str2, roleList);
    synchronized (this.myRelMBeanObjName2RelIdMap) {
      this.myRelMBeanObjName2RelIdMap.put(paramObjectName, str1);
    } 
    try {
      this.myMBeanServer.setAttribute(paramObjectName, new Attribute("RelationServiceManagementFlag", Boolean.TRUE));
    } catch (Exception exception) {}
    ArrayList arrayList = new ArrayList();
    arrayList.add(paramObjectName);
    updateUnregistrationListener(arrayList, null);
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "addRelation");
  }
  
  public ObjectName isRelationMBean(String paramString) throws IllegalArgumentException, RelationNotFoundException {
    if (paramString == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "isRelationMBean", paramString);
    Object object = getRelation(paramString);
    return (object instanceof ObjectName) ? (ObjectName)object : null;
  }
  
  public String isRelation(ObjectName paramObjectName) throws IllegalArgumentException {
    if (paramObjectName == null) {
      String str1 = "Invalid parameter.";
      throw new IllegalArgumentException(str1);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "isRelation", paramObjectName);
    String str = null;
    synchronized (this.myRelMBeanObjName2RelIdMap) {
      String str1 = (String)this.myRelMBeanObjName2RelIdMap.get(paramObjectName);
      if (str1 != null)
        str = str1; 
    } 
    return str;
  }
  
  public Boolean hasRelation(String paramString) throws IllegalArgumentException {
    if (paramString == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "hasRelation", paramString);
    try {
      Object object = getRelation(paramString);
      return Boolean.valueOf(true);
    } catch (RelationNotFoundException relationNotFoundException) {
      return Boolean.valueOf(false);
    } 
  }
  
  public List<String> getAllRelationIds() {
    ArrayList arrayList;
    synchronized (this.myRelId2ObjMap) {
      arrayList = new ArrayList(this.myRelId2ObjMap.keySet());
    } 
    return arrayList;
  }
  
  public Integer checkRoleReading(String paramString1, String paramString2) throws IllegalArgumentException, RelationTypeNotFoundException {
    Integer integer;
    if (paramString1 == null || paramString2 == null) {
      integer = "Invalid parameter.";
      throw new IllegalArgumentException(integer);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "checkRoleReading", new Object[] { paramString1, paramString2 });
    RelationType relationType = getRelationType(paramString2);
    try {
      RoleInfo roleInfo = relationType.getRoleInfo(paramString1);
      integer = checkRoleInt(1, paramString1, null, roleInfo, false);
    } catch (RoleInfoNotFoundException roleInfoNotFoundException) {
      integer = Integer.valueOf(1);
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleReading");
    return integer;
  }
  
  public Integer checkRoleWriting(Role paramRole, String paramString, Boolean paramBoolean) throws IllegalArgumentException, RelationTypeNotFoundException {
    RoleInfo roleInfo;
    if (paramRole == null || paramString == null || paramBoolean == null) {
      String str1 = "Invalid parameter.";
      throw new IllegalArgumentException(str1);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "checkRoleWriting", new Object[] { paramRole, paramString, paramBoolean });
    RelationType relationType = getRelationType(paramString);
    String str = paramRole.getRoleName();
    List list = paramRole.getRoleValue();
    boolean bool = true;
    if (paramBoolean.booleanValue())
      bool = false; 
    try {
      roleInfo = relationType.getRoleInfo(str);
    } catch (RoleInfoNotFoundException roleInfoNotFoundException) {
      JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleWriting");
      return Integer.valueOf(1);
    } 
    Integer integer = checkRoleInt(2, str, list, roleInfo, bool);
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleWriting");
    return integer;
  }
  
  public void sendRelationCreationNotification(String paramString) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationTypeNotFoundException {
    if (paramString == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "sendRelationCreationNotification", paramString);
    StringBuilder stringBuilder = new StringBuilder("Creation of relation ");
    stringBuilder.append(paramString);
    sendNotificationInt(1, stringBuilder.toString(), paramString, null, null, null, null);
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "sendRelationCreationNotification");
  }
  
  public void sendRoleUpdateNotification(String paramString, Role paramRole, List<ObjectName> paramList) throws IllegalArgumentException, RelationNotFoundException {
    if (paramString == null || paramRole == null || paramList == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    if (!(paramList instanceof ArrayList))
      paramList = new ArrayList<ObjectName>(paramList); 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "sendRoleUpdateNotification", new Object[] { paramString, paramRole, paramList });
    String str1 = paramRole.getRoleName();
    List list = paramRole.getRoleValue();
    String str2 = Role.roleValueToString(list);
    String str3 = Role.roleValueToString(paramList);
    StringBuilder stringBuilder = new StringBuilder("Value of role ");
    stringBuilder.append(str1);
    stringBuilder.append(" has changed\nOld value:\n");
    stringBuilder.append(str3);
    stringBuilder.append("\nNew value:\n");
    stringBuilder.append(str2);
    sendNotificationInt(2, stringBuilder.toString(), paramString, null, str1, list, paramList);
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "sendRoleUpdateNotification");
  }
  
  public void sendRelationRemovalNotification(String paramString, List<ObjectName> paramList) throws IllegalArgumentException, RelationNotFoundException {
    if (paramString == null) {
      String str = "Invalid parameter";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "sendRelationRemovalNotification", new Object[] { paramString, paramList });
    sendNotificationInt(3, "Removal of relation " + paramString, paramString, paramList, null, null, null);
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "sendRelationRemovalNotification");
  }
  
  public void updateRoleMap(String paramString, Role paramRole, List<ObjectName> paramList) throws IllegalArgumentException, RelationNotFoundException {
    if (paramString == null || paramRole == null || paramList == null) {
      String str1 = "Invalid parameter.";
      throw new IllegalArgumentException(str1);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "updateRoleMap", new Object[] { paramString, paramRole, paramList });
    isActive();
    Object object = getRelation(paramString);
    String str = paramRole.getRoleName();
    List list = paramRole.getRoleValue();
    ArrayList arrayList1 = new ArrayList(paramList);
    ArrayList arrayList2 = new ArrayList();
    for (ObjectName objectName : list) {
      int i = arrayList1.indexOf(objectName);
      if (i == -1) {
        boolean bool = addNewMBeanReference(objectName, paramString, str);
        if (bool)
          arrayList2.add(objectName); 
        continue;
      } 
      arrayList1.remove(i);
    } 
    ArrayList arrayList3 = new ArrayList();
    for (ObjectName objectName : arrayList1) {
      boolean bool = removeMBeanReference(objectName, paramString, str, false);
      if (bool)
        arrayList3.add(objectName); 
    } 
    updateUnregistrationListener(arrayList2, arrayList3);
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "updateRoleMap");
  }
  
  public void removeRelation(String paramString) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationTypeNotFoundException {
    String str;
    isActive();
    if (paramString == null) {
      String str1 = "Invalid parameter.";
      throw new IllegalArgumentException(str1);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "removeRelation", paramString);
    Object object = getRelation(paramString);
    if (object instanceof ObjectName) {
      ArrayList arrayList = new ArrayList();
      arrayList.add((ObjectName)object);
      updateUnregistrationListener(null, arrayList);
    } 
    sendRelationRemovalNotification(paramString, null);
    ArrayList arrayList1 = new ArrayList();
    ArrayList arrayList2 = new ArrayList();
    synchronized (this.myRefedMBeanObjName2RelIdsMap) {
      for (ObjectName objectName : this.myRefedMBeanObjName2RelIdsMap.keySet()) {
        Map map = (Map)this.myRefedMBeanObjName2RelIdsMap.get(objectName);
        if (map.containsKey(paramString)) {
          map.remove(paramString);
          arrayList1.add(objectName);
        } 
        if (map.isEmpty())
          arrayList2.add(objectName); 
      } 
      for (ObjectName objectName : arrayList2)
        this.myRefedMBeanObjName2RelIdsMap.remove(objectName); 
    } 
    synchronized (this.myRelId2ObjMap) {
      this.myRelId2ObjMap.remove(paramString);
    } 
    if (object instanceof ObjectName)
      synchronized (this.myRelMBeanObjName2RelIdMap) {
        this.myRelMBeanObjName2RelIdMap.remove((ObjectName)object);
      }  
    synchronized (this.myRelId2RelTypeMap) {
      str = (String)this.myRelId2RelTypeMap.get(paramString);
      this.myRelId2RelTypeMap.remove(paramString);
    } 
    synchronized (this.myRelType2RelIdsMap) {
      List list = (List)this.myRelType2RelIdsMap.get(str);
      if (list != null) {
        list.remove(paramString);
        if (list.isEmpty())
          this.myRelType2RelIdsMap.remove(str); 
      } 
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "removeRelation");
  }
  
  public void purgeRelations() throws RelationServiceNotRegisteredException {
    ArrayList arrayList1;
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "purgeRelations");
    isActive();
    synchronized (this.myRefedMBeanObjName2RelIdsMap) {
      arrayList1 = new ArrayList(this.myUnregNtfList);
      this.myUnregNtfList = new ArrayList();
    } 
    ArrayList arrayList2 = new ArrayList();
    HashMap hashMap = new HashMap();
    synchronized (this.myRefedMBeanObjName2RelIdsMap) {
      for (MBeanServerNotification mBeanServerNotification : arrayList1) {
        ObjectName objectName = mBeanServerNotification.getMBeanName();
        arrayList2.add(objectName);
        Map map = (Map)this.myRefedMBeanObjName2RelIdsMap.get(objectName);
        hashMap.put(objectName, map);
        this.myRefedMBeanObjName2RelIdsMap.remove(objectName);
      } 
    } 
    updateUnregistrationListener(null, arrayList2);
    for (MBeanServerNotification mBeanServerNotification : arrayList1) {
      ObjectName objectName = mBeanServerNotification.getMBeanName();
      Map map = (Map)hashMap.get(objectName);
      for (Map.Entry entry : map.entrySet()) {
        String str = (String)entry.getKey();
        List list = (List)entry.getValue();
        try {
          handleReferenceUnregistration(str, objectName, list);
        } catch (RelationNotFoundException relationNotFoundException) {
          throw new RuntimeException(relationNotFoundException.getMessage());
        } catch (RoleNotFoundException roleNotFoundException) {
          throw new RuntimeException(roleNotFoundException.getMessage());
        } 
      } 
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "purgeRelations");
  }
  
  public Map<String, List<String>> findReferencingRelations(ObjectName paramObjectName, String paramString1, String paramString2) throws IllegalArgumentException {
    if (paramObjectName == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "findReferencingRelations", new Object[] { paramObjectName, paramString1, paramString2 });
    HashMap hashMap = new HashMap();
    synchronized (this.myRefedMBeanObjName2RelIdsMap) {
      Map map = (Map)this.myRefedMBeanObjName2RelIdsMap.get(paramObjectName);
      if (map != null) {
        ArrayList arrayList;
        Set set = map.keySet();
        if (paramString1 == null) {
          arrayList = new ArrayList(set);
        } else {
          arrayList = new ArrayList();
          for (String str1 : set) {
            String str2;
            synchronized (this.myRelId2RelTypeMap) {
              str2 = (String)this.myRelId2RelTypeMap.get(str1);
            } 
            if (str2.equals(paramString1))
              arrayList.add(str1); 
          } 
        } 
        for (String str : arrayList) {
          List list = (List)map.get(str);
          if (paramString2 == null) {
            hashMap.put(str, new ArrayList(list));
            continue;
          } 
          if (list.contains(paramString2)) {
            ArrayList arrayList1 = new ArrayList();
            arrayList1.add(paramString2);
            hashMap.put(str, arrayList1);
          } 
        } 
      } 
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "findReferencingRelations");
    return hashMap;
  }
  
  public Map<ObjectName, List<String>> findAssociatedMBeans(ObjectName paramObjectName, String paramString1, String paramString2) throws IllegalArgumentException {
    if (paramObjectName == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "findAssociatedMBeans", new Object[] { paramObjectName, paramString1, paramString2 });
    Map map = findReferencingRelations(paramObjectName, paramString1, paramString2);
    HashMap hashMap = new HashMap();
    for (String str : map.keySet()) {
      Map map1;
      try {
        map1 = getReferencedMBeans(str);
      } catch (RelationNotFoundException relationNotFoundException) {
        throw new RuntimeException(relationNotFoundException.getMessage());
      } 
      for (ObjectName objectName : map1.keySet()) {
        if (!objectName.equals(paramObjectName)) {
          List list = (List)hashMap.get(objectName);
          if (list == null) {
            list = new ArrayList();
            list.add(str);
            hashMap.put(objectName, list);
            continue;
          } 
          list.add(str);
        } 
      } 
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "findAssociatedMBeans");
    return hashMap;
  }
  
  public List<String> findRelationsOfType(String paramString) throws IllegalArgumentException, RelationTypeNotFoundException {
    ArrayList arrayList;
    if (paramString == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "findRelationsOfType");
    RelationType relationType = getRelationType(paramString);
    synchronized (this.myRelType2RelIdsMap) {
      List list = (List)this.myRelType2RelIdsMap.get(paramString);
      if (list == null) {
        arrayList = new ArrayList();
      } else {
        arrayList = new ArrayList(list);
      } 
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "findRelationsOfType");
    return arrayList;
  }
  
  public List<ObjectName> getRole(String paramString1, String paramString2) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException, RoleNotFoundException {
    ArrayList arrayList;
    if (paramString1 == null || paramString2 == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRole", new Object[] { paramString1, paramString2 });
    isActive();
    Object object = getRelation(paramString1);
    if (object instanceof RelationSupport) {
      arrayList = (List)Util.cast(((RelationSupport)object).getRoleInt(paramString2, true, this, false));
    } else {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramString2;
      String[] arrayOfString = new String[1];
      arrayOfString[0] = "java.lang.String";
      try {
        List list = (List)Util.cast(this.myMBeanServer.invoke((ObjectName)object, "getRole", arrayOfObject, arrayOfString));
        if (list == null || list instanceof ArrayList) {
          arrayList = list;
        } else {
          arrayList = new ArrayList(list);
        } 
      } catch (InstanceNotFoundException instanceNotFoundException) {
        throw new RuntimeException(instanceNotFoundException.getMessage());
      } catch (ReflectionException reflectionException) {
        throw new RuntimeException(reflectionException.getMessage());
      } catch (MBeanException mBeanException) {
        Exception exception = mBeanException.getTargetException();
        if (exception instanceof RoleNotFoundException)
          throw (RoleNotFoundException)exception; 
        throw new RuntimeException(exception.getMessage());
      } 
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRole");
    return arrayList;
  }
  
  public RoleResult getRoles(String paramString, String[] paramArrayOfString) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException {
    RoleResult roleResult;
    if (paramString == null || paramArrayOfString == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRoles", paramString);
    isActive();
    Object object = getRelation(paramString);
    if (object instanceof RelationSupport) {
      roleResult = ((RelationSupport)object).getRolesInt(paramArrayOfString, true, this);
    } else {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramArrayOfString;
      String[] arrayOfString = new String[1];
      try {
        arrayOfString[0] = paramArrayOfString.getClass().getName();
      } catch (Exception exception) {}
      try {
        roleResult = (RoleResult)this.myMBeanServer.invoke((ObjectName)object, "getRoles", arrayOfObject, arrayOfString);
      } catch (InstanceNotFoundException instanceNotFoundException) {
        throw new RuntimeException(instanceNotFoundException.getMessage());
      } catch (ReflectionException reflectionException) {
        throw new RuntimeException(reflectionException.getMessage());
      } catch (MBeanException mBeanException) {
        throw new RuntimeException(mBeanException.getTargetException().getMessage());
      } 
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRoles");
    return roleResult;
  }
  
  public RoleResult getAllRoles(String paramString) throws IllegalArgumentException, RelationNotFoundException, RelationServiceNotRegisteredException {
    RoleResult roleResult;
    if (paramString == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRoles", paramString);
    Object object = getRelation(paramString);
    if (object instanceof RelationSupport) {
      roleResult = ((RelationSupport)object).getAllRolesInt(true, this);
    } else {
      try {
        roleResult = (RoleResult)this.myMBeanServer.getAttribute((ObjectName)object, "AllRoles");
      } catch (Exception exception) {
        throw new RuntimeException(exception.getMessage());
      } 
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRoles");
    return roleResult;
  }
  
  public Integer getRoleCardinality(String paramString1, String paramString2) throws IllegalArgumentException, RelationTypeNotFoundException {
    Integer integer;
    if (paramString1 == null || paramString2 == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRoleCardinality", new Object[] { paramString1, paramString2 });
    Object object = getRelation(paramString1);
    if (object instanceof RelationSupport) {
      integer = ((RelationSupport)object).getRoleCardinality(paramString2);
    } else {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramString2;
      String[] arrayOfString = new String[1];
      arrayOfString[0] = "java.lang.String";
      try {
        integer = (Integer)this.myMBeanServer.invoke((ObjectName)object, "getRoleCardinality", arrayOfObject, arrayOfString);
      } catch (InstanceNotFoundException instanceNotFoundException) {
        throw new RuntimeException(instanceNotFoundException.getMessage());
      } catch (ReflectionException reflectionException) {
        throw new RuntimeException(reflectionException.getMessage());
      } catch (MBeanException mBeanException) {
        Exception exception = mBeanException.getTargetException();
        if (exception instanceof RoleNotFoundException)
          throw (RoleNotFoundException)exception; 
        throw new RuntimeException(exception.getMessage());
      } 
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRoleCardinality");
    return integer;
  }
  
  public void setRole(String paramString, Role paramRole) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException, RoleNotFoundException, InvalidRoleValueException {
    if (paramString == null || paramRole == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "setRole", new Object[] { paramString, paramRole });
    isActive();
    Object object = getRelation(paramString);
    if (object instanceof RelationSupport) {
      try {
        ((RelationSupport)object).setRoleInt(paramRole, true, this, false);
      } catch (RelationTypeNotFoundException relationTypeNotFoundException) {
        throw new RuntimeException(relationTypeNotFoundException.getMessage());
      } 
    } else {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramRole;
      String[] arrayOfString = new String[1];
      arrayOfString[0] = "javax.management.relation.Role";
      try {
        this.myMBeanServer.setAttribute((ObjectName)object, new Attribute("Role", paramRole));
      } catch (InstanceNotFoundException instanceNotFoundException) {
        throw new RuntimeException(instanceNotFoundException.getMessage());
      } catch (ReflectionException reflectionException) {
        throw new RuntimeException(reflectionException.getMessage());
      } catch (MBeanException mBeanException) {
        Exception exception = mBeanException.getTargetException();
        if (exception instanceof RoleNotFoundException)
          throw (RoleNotFoundException)exception; 
        if (exception instanceof InvalidRoleValueException)
          throw (InvalidRoleValueException)exception; 
        throw new RuntimeException(exception.getMessage());
      } catch (AttributeNotFoundException attributeNotFoundException) {
        throw new RuntimeException(attributeNotFoundException.getMessage());
      } catch (InvalidAttributeValueException invalidAttributeValueException) {
        throw new RuntimeException(invalidAttributeValueException.getMessage());
      } 
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "setRole");
  }
  
  public RoleResult setRoles(String paramString, RoleList paramRoleList) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException {
    RoleResult roleResult;
    if (paramString == null || paramRoleList == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "setRoles", new Object[] { paramString, paramRoleList });
    isActive();
    Object object = getRelation(paramString);
    if (object instanceof RelationSupport) {
      try {
        roleResult = ((RelationSupport)object).setRolesInt(paramRoleList, true, this);
      } catch (RelationTypeNotFoundException relationTypeNotFoundException) {
        throw new RuntimeException(relationTypeNotFoundException.getMessage());
      } 
    } else {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramRoleList;
      String[] arrayOfString = new String[1];
      arrayOfString[0] = "javax.management.relation.RoleList";
      try {
        roleResult = (RoleResult)this.myMBeanServer.invoke((ObjectName)object, "setRoles", arrayOfObject, arrayOfString);
      } catch (InstanceNotFoundException instanceNotFoundException) {
        throw new RuntimeException(instanceNotFoundException.getMessage());
      } catch (ReflectionException reflectionException) {
        throw new RuntimeException(reflectionException.getMessage());
      } catch (MBeanException mBeanException) {
        throw new RuntimeException(mBeanException.getTargetException().getMessage());
      } 
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "setRoles");
    return roleResult;
  }
  
  public Map<ObjectName, List<String>> getReferencedMBeans(String paramString) throws IllegalArgumentException, RelationNotFoundException {
    Map map;
    if (paramString == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getReferencedMBeans", paramString);
    Object object = getRelation(paramString);
    if (object instanceof RelationSupport) {
      map = ((RelationSupport)object).getReferencedMBeans();
    } else {
      try {
        map = (Map)Util.cast(this.myMBeanServer.getAttribute((ObjectName)object, "ReferencedMBeans"));
      } catch (Exception exception) {
        throw new RuntimeException(exception.getMessage());
      } 
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getReferencedMBeans");
    return map;
  }
  
  public String getRelationTypeName(String paramString) throws IllegalArgumentException, RelationNotFoundException {
    String str;
    if (paramString == null) {
      String str1 = "Invalid parameter.";
      throw new IllegalArgumentException(str1);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRelationTypeName", paramString);
    Object object = getRelation(paramString);
    if (object instanceof RelationSupport) {
      str = ((RelationSupport)object).getRelationTypeName();
    } else {
      try {
        str = (String)this.myMBeanServer.getAttribute((ObjectName)object, "RelationTypeName");
      } catch (Exception exception) {
        throw new RuntimeException(exception.getMessage());
      } 
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRelationTypeName");
    return str;
  }
  
  public void handleNotification(Notification paramNotification, Object paramObject) {
    if (paramNotification == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "handleNotification", paramNotification);
    if (paramNotification instanceof MBeanServerNotification) {
      MBeanServerNotification mBeanServerNotification = (MBeanServerNotification)paramNotification;
      String str = paramNotification.getType();
      if (str.equals("JMX.mbean.unregistered")) {
        String str1;
        ObjectName objectName = ((MBeanServerNotification)paramNotification).getMBeanName();
        boolean bool = false;
        synchronized (this.myRefedMBeanObjName2RelIdsMap) {
          if (this.myRefedMBeanObjName2RelIdsMap.containsKey(objectName)) {
            synchronized (this.myUnregNtfList) {
              this.myUnregNtfList.add(mBeanServerNotification);
            } 
            bool = true;
          } 
          if (bool && this.myPurgeFlag)
            try {
              purgeRelations();
            } catch (Exception exception) {
              throw new RuntimeException(exception.getMessage());
            }  
        } 
        synchronized (this.myRelMBeanObjName2RelIdMap) {
          str1 = (String)this.myRelMBeanObjName2RelIdMap.get(objectName);
        } 
        if (str1 != null)
          try {
            removeRelation(str1);
          } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
          }  
      } 
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "handleNotification");
  }
  
  public MBeanNotificationInfo[] getNotificationInfo() {
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getNotificationInfo");
    String str1 = "javax.management.relation.RelationNotification";
    String[] arrayOfString = { "jmx.relation.creation.basic", "jmx.relation.creation.mbean", "jmx.relation.update.basic", "jmx.relation.update.mbean", "jmx.relation.removal.basic", "jmx.relation.removal.mbean" };
    String str2 = "Sent when a relation is created, updated or deleted.";
    MBeanNotificationInfo mBeanNotificationInfo = new MBeanNotificationInfo(arrayOfString, str1, str2);
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getNotificationInfo");
    return new MBeanNotificationInfo[] { mBeanNotificationInfo };
  }
  
  private void addRelationTypeInt(RelationType paramRelationType) throws IllegalArgumentException, InvalidRelationTypeException {
    if (paramRelationType == null) {
      String str1 = "Invalid parameter.";
      throw new IllegalArgumentException(str1);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "addRelationTypeInt");
    String str = paramRelationType.getRelationTypeName();
    try {
      RelationType relationType = getRelationType(str);
      if (relationType != null) {
        String str1 = "There is already a relation type in the Relation Service with name ";
        StringBuilder stringBuilder = new StringBuilder(str1);
        stringBuilder.append(str);
        throw new InvalidRelationTypeException(stringBuilder.toString());
      } 
    } catch (RelationTypeNotFoundException relationTypeNotFoundException) {}
    synchronized (this.myRelType2ObjMap) {
      this.myRelType2ObjMap.put(str, paramRelationType);
    } 
    if (paramRelationType instanceof RelationTypeSupport)
      ((RelationTypeSupport)paramRelationType).setRelationServiceFlag(true); 
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "addRelationTypeInt");
  }
  
  RelationType getRelationType(String paramString) throws IllegalArgumentException, RelationTypeNotFoundException {
    RelationType relationType;
    if (paramString == null) {
      relationType = "Invalid parameter.";
      throw new IllegalArgumentException(relationType);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRelationType", paramString);
    synchronized (this.myRelType2ObjMap) {
      relationType = (RelationType)this.myRelType2ObjMap.get(paramString);
    } 
    if (relationType == null) {
      String str = "No relation type created in the Relation Service with the name ";
      StringBuilder stringBuilder = new StringBuilder(str);
      stringBuilder.append(paramString);
      throw new RelationTypeNotFoundException(stringBuilder.toString());
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRelationType");
    return relationType;
  }
  
  Object getRelation(String paramString) throws IllegalArgumentException, RelationNotFoundException {
    Object object;
    if (paramString == null) {
      object = "Invalid parameter.";
      throw new IllegalArgumentException(object);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRelation", paramString);
    synchronized (this.myRelId2ObjMap) {
      object = this.myRelId2ObjMap.get(paramString);
    } 
    if (object == null) {
      String str = "No relation associated to relation id " + paramString;
      throw new RelationNotFoundException(str);
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRelation");
    return object;
  }
  
  private boolean addNewMBeanReference(ObjectName paramObjectName, String paramString1, String paramString2) throws IllegalArgumentException {
    if (paramObjectName == null || paramString1 == null || paramString2 == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "addNewMBeanReference", new Object[] { paramObjectName, paramString1, paramString2 });
    boolean bool = false;
    synchronized (this.myRefedMBeanObjName2RelIdsMap) {
      Map map = (Map)this.myRefedMBeanObjName2RelIdsMap.get(paramObjectName);
      if (map == null) {
        bool = true;
        ArrayList arrayList = new ArrayList();
        arrayList.add(paramString2);
        map = new HashMap();
        map.put(paramString1, arrayList);
        this.myRefedMBeanObjName2RelIdsMap.put(paramObjectName, map);
      } else {
        List list = (List)map.get(paramString1);
        if (list == null) {
          list = new ArrayList();
          list.add(paramString2);
          map.put(paramString1, list);
        } else {
          list.add(paramString2);
        } 
      } 
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "addNewMBeanReference");
    return bool;
  }
  
  private boolean removeMBeanReference(ObjectName paramObjectName, String paramString1, String paramString2, boolean paramBoolean) throws IllegalArgumentException {
    if (paramObjectName == null || paramString1 == null || paramString2 == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "removeMBeanReference", new Object[] { paramObjectName, paramString1, paramString2, Boolean.valueOf(paramBoolean) });
    boolean bool = false;
    synchronized (this.myRefedMBeanObjName2RelIdsMap) {
      Map map = (Map)this.myRefedMBeanObjName2RelIdsMap.get(paramObjectName);
      if (map == null) {
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "removeMBeanReference");
        return true;
      } 
      List list = null;
      if (!paramBoolean) {
        list = (List)map.get(paramString1);
        int i = list.indexOf(paramString2);
        if (i != -1)
          list.remove(i); 
      } 
      if (list.isEmpty() || paramBoolean)
        map.remove(paramString1); 
      if (map.isEmpty()) {
        this.myRefedMBeanObjName2RelIdsMap.remove(paramObjectName);
        bool = true;
      } 
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "removeMBeanReference");
    return bool;
  }
  
  private void updateUnregistrationListener(List<ObjectName> paramList1, List<ObjectName> paramList2) throws RelationServiceNotRegisteredException {
    if (paramList1 != null && paramList2 != null && paramList1.isEmpty() && paramList2.isEmpty())
      return; 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "updateUnregistrationListener", new Object[] { paramList1, paramList2 });
    isActive();
    if (paramList1 != null || paramList2 != null) {
      boolean bool = false;
      if (this.myUnregNtfFilter == null) {
        this.myUnregNtfFilter = new MBeanServerNotificationFilter();
        bool = true;
      } 
      synchronized (this.myUnregNtfFilter) {
        if (paramList1 != null)
          for (ObjectName objectName : paramList1)
            this.myUnregNtfFilter.enableObjectName(objectName);  
        if (paramList2 != null)
          for (ObjectName objectName : paramList2)
            this.myUnregNtfFilter.disableObjectName(objectName);  
        if (bool)
          try {
            this.myMBeanServer.addNotificationListener(MBeanServerDelegate.DELEGATE_NAME, this, this.myUnregNtfFilter, null);
          } catch (InstanceNotFoundException instanceNotFoundException) {
            throw new RelationServiceNotRegisteredException(instanceNotFoundException.getMessage());
          }  
      } 
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "updateUnregistrationListener");
  }
  
  private void addRelationInt(boolean paramBoolean, RelationSupport paramRelationSupport, ObjectName paramObjectName, String paramString1, String paramString2, RoleList paramRoleList) throws IllegalArgumentException, RelationServiceNotRegisteredException, RoleNotFoundException, InvalidRelationIdException, RelationTypeNotFoundException, InvalidRoleValueException {
    if (paramString1 == null || paramString2 == null || (paramBoolean && (paramRelationSupport == null || paramObjectName != null)) || (!paramBoolean && (paramObjectName == null || paramRelationSupport != null))) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "addRelationInt", new Object[] { Boolean.valueOf(paramBoolean), paramRelationSupport, paramObjectName, paramString1, paramString2, paramRoleList });
    isActive();
    try {
      Object object = getRelation(paramString1);
      if (object != null) {
        String str = "There is already a relation with id ";
        StringBuilder stringBuilder = new StringBuilder(str);
        stringBuilder.append(paramString1);
        throw new InvalidRelationIdException(stringBuilder.toString());
      } 
    } catch (RelationNotFoundException relationNotFoundException) {}
    RelationType relationType = getRelationType(paramString2);
    ArrayList arrayList = new ArrayList(relationType.getRoleInfos());
    if (paramRoleList != null)
      for (Role role : paramRoleList.asList()) {
        RoleInfo roleInfo;
        String str = role.getRoleName();
        List list = role.getRoleValue();
        try {
          roleInfo = relationType.getRoleInfo(str);
        } catch (RoleInfoNotFoundException roleInfoNotFoundException) {
          throw new RoleNotFoundException(roleInfoNotFoundException.getMessage());
        } 
        Integer integer = checkRoleInt(2, str, list, roleInfo, false);
        int i = integer.intValue();
        if (i != 0)
          throwRoleProblemException(i, str); 
        int j = arrayList.indexOf(roleInfo);
        arrayList.remove(j);
      }  
    initializeMissingRoles(paramBoolean, paramRelationSupport, paramObjectName, paramString1, paramString2, arrayList);
    synchronized (this.myRelId2ObjMap) {
      if (paramBoolean) {
        this.myRelId2ObjMap.put(paramString1, paramRelationSupport);
      } else {
        this.myRelId2ObjMap.put(paramString1, paramObjectName);
      } 
    } 
    synchronized (this.myRelId2RelTypeMap) {
      this.myRelId2RelTypeMap.put(paramString1, paramString2);
    } 
    synchronized (this.myRelType2RelIdsMap) {
      List list = (List)this.myRelType2RelIdsMap.get(paramString2);
      boolean bool = false;
      if (list == null) {
        bool = true;
        list = new ArrayList();
      } 
      list.add(paramString1);
      if (bool)
        this.myRelType2RelIdsMap.put(paramString2, list); 
    } 
    for (Role role : paramRoleList.asList()) {
      ArrayList arrayList1 = new ArrayList();
      try {
        updateRoleMap(paramString1, role, arrayList1);
      } catch (RelationNotFoundException relationNotFoundException) {}
    } 
    try {
      sendRelationCreationNotification(paramString1);
    } catch (RelationNotFoundException relationNotFoundException) {}
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "addRelationInt");
  }
  
  private Integer checkRoleInt(int paramInt, String paramString, List<ObjectName> paramList, RoleInfo paramRoleInfo, boolean paramBoolean) throws IllegalArgumentException {
    if (paramString == null || paramRoleInfo == null || (paramInt == 2 && paramList == null)) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "checkRoleInt", new Object[] { Integer.valueOf(paramInt), paramString, paramList, paramRoleInfo, Boolean.valueOf(paramBoolean) });
    String str1 = paramRoleInfo.getName();
    if (!paramString.equals(str1)) {
      JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
      return Integer.valueOf(1);
    } 
    if (paramInt == 1) {
      boolean bool = paramRoleInfo.isReadable();
      if (!bool) {
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
        return Integer.valueOf(2);
      } 
      JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
      return new Integer(0);
    } 
    if (paramBoolean) {
      boolean bool = paramRoleInfo.isWritable();
      if (!bool) {
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
        return new Integer(3);
      } 
    } 
    int i = paramList.size();
    boolean bool1 = paramRoleInfo.checkMinDegree(i);
    if (!bool1) {
      JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
      return new Integer(4);
    } 
    boolean bool2 = paramRoleInfo.checkMaxDegree(i);
    if (!bool2) {
      JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
      return new Integer(5);
    } 
    String str2 = paramRoleInfo.getRefMBeanClassName();
    for (ObjectName objectName : paramList) {
      if (objectName == null) {
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
        return new Integer(7);
      } 
      try {
        boolean bool = this.myMBeanServer.isInstanceOf(objectName, str2);
        if (!bool) {
          JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
          return new Integer(6);
        } 
      } catch (InstanceNotFoundException instanceNotFoundException) {
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
        return new Integer(7);
      } 
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
    return new Integer(0);
  }
  
  private void initializeMissingRoles(boolean paramBoolean, RelationSupport paramRelationSupport, ObjectName paramObjectName, String paramString1, String paramString2, List<RoleInfo> paramList) throws IllegalArgumentException, RelationServiceNotRegisteredException, InvalidRoleValueException {
    if ((paramBoolean && (paramRelationSupport == null || paramObjectName != null)) || (!paramBoolean && (paramObjectName == null || paramRelationSupport != null)) || paramString1 == null || paramString2 == null || paramList == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "initializeMissingRoles", new Object[] { Boolean.valueOf(paramBoolean), paramRelationSupport, paramObjectName, paramString1, paramString2, paramList });
    isActive();
    for (RoleInfo roleInfo : paramList) {
      String str = roleInfo.getName();
      ArrayList arrayList = new ArrayList();
      Role role = new Role(str, arrayList);
      if (paramBoolean)
        try {
          paramRelationSupport.setRoleInt(role, true, this, false);
          continue;
        } catch (RoleNotFoundException roleNotFoundException) {
          throw new RuntimeException(roleNotFoundException.getMessage());
        } catch (RelationNotFoundException relationNotFoundException) {
          throw new RuntimeException(relationNotFoundException.getMessage());
        } catch (RelationTypeNotFoundException relationTypeNotFoundException) {
          throw new RuntimeException(relationTypeNotFoundException.getMessage());
        }  
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = role;
      String[] arrayOfString = new String[1];
      arrayOfString[0] = "javax.management.relation.Role";
      try {
        this.myMBeanServer.setAttribute(paramObjectName, new Attribute("Role", role));
      } catch (InstanceNotFoundException instanceNotFoundException) {
        throw new RuntimeException(instanceNotFoundException.getMessage());
      } catch (ReflectionException reflectionException) {
        throw new RuntimeException(reflectionException.getMessage());
      } catch (MBeanException mBeanException) {
        Exception exception = mBeanException.getTargetException();
        if (exception instanceof InvalidRoleValueException)
          throw (InvalidRoleValueException)exception; 
        throw new RuntimeException(exception.getMessage());
      } catch (AttributeNotFoundException attributeNotFoundException) {
        throw new RuntimeException(attributeNotFoundException.getMessage());
      } catch (InvalidAttributeValueException invalidAttributeValueException) {
        throw new RuntimeException(invalidAttributeValueException.getMessage());
      } 
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "initializeMissingRoles");
  }
  
  static void throwRoleProblemException(int paramInt, String paramString) throws IllegalArgumentException, RoleNotFoundException, InvalidRoleValueException {
    if (paramString == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    byte b = 0;
    String str1 = null;
    switch (paramInt) {
      case 1:
        str1 = " does not exist in relation.";
        b = 1;
        break;
      case 2:
        str1 = " is not readable.";
        b = 1;
        break;
      case 3:
        str1 = " is not writable.";
        b = 1;
        break;
      case 4:
        str1 = " has a number of MBean references less than the expected minimum degree.";
        b = 2;
        break;
      case 5:
        str1 = " has a number of MBean references greater than the expected maximum degree.";
        b = 2;
        break;
      case 6:
        str1 = " has an MBean reference to an MBean not of the expected class of references for that role.";
        b = 2;
        break;
      case 7:
        str1 = " has a reference to null or to an MBean not registered.";
        b = 2;
        break;
    } 
    StringBuilder stringBuilder = new StringBuilder(paramString);
    stringBuilder.append(str1);
    String str2 = stringBuilder.toString();
    if (b == 1)
      throw new RoleNotFoundException(str2); 
    if (b == 2)
      throw new InvalidRoleValueException(str2); 
  }
  
  private void sendNotificationInt(int paramInt, String paramString1, String paramString2, List<ObjectName> paramList1, String paramString3, List<ObjectName> paramList2, List<ObjectName> paramList3) throws IllegalArgumentException, RelationNotFoundException {
    String str1;
    if (paramString1 == null || paramString2 == null || (paramInt != 3 && paramList1 != null) || (paramInt == 2 && (paramString3 == null || paramList2 == null || paramList3 == null))) {
      str1 = "Invalid parameter.";
      throw new IllegalArgumentException(str1);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "sendNotificationInt", new Object[] { Integer.valueOf(paramInt), paramString1, paramString2, paramList1, paramString3, paramList2, paramList3 });
    synchronized (this.myRelId2RelTypeMap) {
      str1 = (String)this.myRelId2RelTypeMap.get(paramString2);
    } 
    ObjectName objectName = isRelationMBean(paramString2);
    String str2 = null;
    if (objectName != null) {
      switch (paramInt) {
        case 1:
          str2 = "jmx.relation.creation.mbean";
          break;
        case 2:
          str2 = "jmx.relation.update.mbean";
          break;
        case 3:
          str2 = "jmx.relation.removal.mbean";
          break;
      } 
    } else {
      switch (paramInt) {
        case 1:
          str2 = "jmx.relation.creation.basic";
          break;
        case 2:
          str2 = "jmx.relation.update.basic";
          break;
        case 3:
          str2 = "jmx.relation.removal.basic";
          break;
      } 
    } 
    Long long = Long.valueOf(this.atomicSeqNo.incrementAndGet());
    Date date = new Date();
    long l = date.getTime();
    RelationNotification relationNotification = null;
    if (str2.equals("jmx.relation.creation.basic") || str2.equals("jmx.relation.creation.mbean") || str2.equals("jmx.relation.removal.basic") || str2.equals("jmx.relation.removal.mbean")) {
      relationNotification = new RelationNotification(str2, this, long.longValue(), l, paramString1, paramString2, str1, objectName, paramList1);
    } else if (str2.equals("jmx.relation.update.basic") || str2.equals("jmx.relation.update.mbean")) {
      relationNotification = new RelationNotification(str2, this, long.longValue(), l, paramString1, paramString2, str1, objectName, paramString3, paramList2, paramList3);
    } 
    sendNotification(relationNotification);
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "sendNotificationInt");
  }
  
  private void handleReferenceUnregistration(String paramString, ObjectName paramObjectName, List<String> paramList) throws IllegalArgumentException, RelationServiceNotRegisteredException, RelationNotFoundException, RoleNotFoundException {
    if (paramString == null || paramList == null || paramObjectName == null) {
      String str1 = "Invalid parameter.";
      throw new IllegalArgumentException(str1);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "handleReferenceUnregistration", new Object[] { paramString, paramObjectName, paramList });
    isActive();
    String str = getRelationTypeName(paramString);
    Object object = getRelation(paramString);
    boolean bool = false;
    for (String str1 : paramList) {
      RoleInfo roleInfo;
      if (bool)
        break; 
      int i = getRoleCardinality(paramString, str1).intValue();
      int j = i - 1;
      try {
        roleInfo = getRoleInfo(str, str1);
      } catch (RelationTypeNotFoundException relationTypeNotFoundException) {
        throw new RuntimeException(relationTypeNotFoundException.getMessage());
      } catch (RoleInfoNotFoundException roleInfoNotFoundException) {
        throw new RuntimeException(roleInfoNotFoundException.getMessage());
      } 
      boolean bool1 = roleInfo.checkMinDegree(j);
      if (!bool1)
        bool = true; 
    } 
    if (bool) {
      removeRelation(paramString);
    } else {
      for (String str1 : paramList) {
        if (object instanceof RelationSupport)
          try {
            ((RelationSupport)object).handleMBeanUnregistrationInt(paramObjectName, str1, true, this);
            continue;
          } catch (RelationTypeNotFoundException relationTypeNotFoundException) {
            throw new RuntimeException(relationTypeNotFoundException.getMessage());
          } catch (InvalidRoleValueException invalidRoleValueException) {
            throw new RuntimeException(invalidRoleValueException.getMessage());
          }  
        Object[] arrayOfObject = new Object[2];
        arrayOfObject[0] = paramObjectName;
        arrayOfObject[1] = str1;
        String[] arrayOfString = new String[2];
        arrayOfString[0] = "javax.management.ObjectName";
        arrayOfString[1] = "java.lang.String";
        try {
          this.myMBeanServer.invoke((ObjectName)object, "handleMBeanUnregistration", arrayOfObject, arrayOfString);
        } catch (InstanceNotFoundException instanceNotFoundException) {
          throw new RuntimeException(instanceNotFoundException.getMessage());
        } catch (ReflectionException reflectionException) {
          throw new RuntimeException(reflectionException.getMessage());
        } catch (MBeanException mBeanException) {
          Exception exception = mBeanException.getTargetException();
          throw new RuntimeException(exception.getMessage());
        } 
      } 
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "handleReferenceUnregistration");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\relation\RelationService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */