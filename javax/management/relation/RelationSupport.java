package javax.management.relation;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.Util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public class RelationSupport implements RelationSupportMBean, MBeanRegistration {
  private String myRelId = null;
  
  private ObjectName myRelServiceName = null;
  
  private MBeanServer myRelServiceMBeanServer = null;
  
  private String myRelTypeName = null;
  
  private final Map<String, Role> myRoleName2ValueMap = new HashMap();
  
  private final AtomicBoolean myInRelServFlg = new AtomicBoolean();
  
  public RelationSupport(String paramString1, ObjectName paramObjectName, String paramString2, RoleList paramRoleList) throws InvalidRoleValueException, IllegalArgumentException {
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "RelationSupport");
    initMembers(paramString1, paramObjectName, null, paramString2, paramRoleList);
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "RelationSupport");
  }
  
  public RelationSupport(String paramString1, ObjectName paramObjectName, MBeanServer paramMBeanServer, String paramString2, RoleList paramRoleList) throws InvalidRoleValueException, IllegalArgumentException {
    if (paramMBeanServer == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "RelationSupport");
    initMembers(paramString1, paramObjectName, paramMBeanServer, paramString2, paramRoleList);
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "RelationSupport");
  }
  
  public List<ObjectName> getRole(String paramString) throws IllegalArgumentException, RoleNotFoundException, RelationServiceNotRegisteredException {
    if (paramString == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getRole", paramString);
    List list = (List)Util.cast(getRoleInt(paramString, false, null, false));
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getRole");
    return list;
  }
  
  public RoleResult getRoles(String[] paramArrayOfString) throws IllegalArgumentException, RelationServiceNotRegisteredException {
    if (paramArrayOfString == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getRoles");
    RoleResult roleResult = getRolesInt(paramArrayOfString, false, null);
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getRoles");
    return roleResult;
  }
  
  public RoleResult getAllRoles() throws RelationServiceNotRegisteredException {
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getAllRoles");
    RoleResult roleResult = null;
    try {
      roleResult = getAllRolesInt(false, null);
    } catch (IllegalArgumentException illegalArgumentException) {}
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getAllRoles");
    return roleResult;
  }
  
  public RoleList retrieveAllRoles() {
    RoleList roleList;
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "retrieveAllRoles");
    synchronized (this.myRoleName2ValueMap) {
      roleList = new RoleList(new ArrayList(this.myRoleName2ValueMap.values()));
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "retrieveAllRoles");
    return roleList;
  }
  
  public Integer getRoleCardinality(String paramString) throws IllegalArgumentException, RoleNotFoundException {
    Role role;
    if (paramString == null) {
      role = "Invalid parameter.";
      throw new IllegalArgumentException(role);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getRoleCardinality", paramString);
    synchronized (this.myRoleName2ValueMap) {
      role = (Role)this.myRoleName2ValueMap.get(paramString);
    } 
    if (role == null) {
      byte b = 1;
      try {
        RelationService.throwRoleProblemException(b, paramString);
      } catch (InvalidRoleValueException invalidRoleValueException) {}
    } 
    List list = role.getRoleValue();
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getRoleCardinality");
    return Integer.valueOf(list.size());
  }
  
  public void setRole(Role paramRole) throws IllegalArgumentException, RoleNotFoundException, RelationTypeNotFoundException, InvalidRoleValueException, RelationServiceNotRegisteredException, RelationNotFoundException {
    if (paramRole == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "setRole", paramRole);
    Object object = setRoleInt(paramRole, false, null, false);
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "setRole");
  }
  
  public RoleResult setRoles(RoleList paramRoleList) throws IllegalArgumentException, RelationServiceNotRegisteredException, RelationTypeNotFoundException, RelationNotFoundException {
    if (paramRoleList == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "setRoles", paramRoleList);
    RoleResult roleResult = setRolesInt(paramRoleList, false, null);
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "setRoles");
    return roleResult;
  }
  
  public void handleMBeanUnregistration(ObjectName paramObjectName, String paramString) throws IllegalArgumentException, RoleNotFoundException, InvalidRoleValueException, RelationServiceNotRegisteredException, RelationTypeNotFoundException, RelationNotFoundException {
    if (paramObjectName == null || paramString == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "handleMBeanUnregistration", new Object[] { paramObjectName, paramString });
    handleMBeanUnregistrationInt(paramObjectName, paramString, false, null);
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "handleMBeanUnregistration");
  }
  
  public Map<ObjectName, List<String>> getReferencedMBeans() {
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getReferencedMBeans");
    HashMap hashMap = new HashMap();
    synchronized (this.myRoleName2ValueMap) {
      for (Role role : this.myRoleName2ValueMap.values()) {
        String str = role.getRoleName();
        List list = role.getRoleValue();
        for (ObjectName objectName : list) {
          List list1 = (List)hashMap.get(objectName);
          boolean bool = false;
          if (list1 == null) {
            bool = true;
            list1 = new ArrayList();
          } 
          list1.add(str);
          if (bool)
            hashMap.put(objectName, list1); 
        } 
      } 
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getReferencedMBeans");
    return hashMap;
  }
  
  public String getRelationTypeName() { return this.myRelTypeName; }
  
  public ObjectName getRelationServiceName() { return this.myRelServiceName; }
  
  public String getRelationId() { return this.myRelId; }
  
  public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName) throws Exception {
    this.myRelServiceMBeanServer = paramMBeanServer;
    return paramObjectName;
  }
  
  public void postRegister(Boolean paramBoolean) {}
  
  public void preDeregister() throws Exception {}
  
  public void postDeregister() throws Exception {}
  
  public Boolean isInRelationService() { return Boolean.valueOf(this.myInRelServFlg.get()); }
  
  public void setRelationServiceManagementFlag(Boolean paramBoolean) {
    if (paramBoolean == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    this.myInRelServFlg.set(paramBoolean.booleanValue());
  }
  
  Object getRoleInt(String paramString, boolean paramBoolean1, RelationService paramRelationService, boolean paramBoolean2) throws IllegalArgumentException, RoleNotFoundException, RelationServiceNotRegisteredException {
    RoleUnresolved roleUnresolved;
    Role role;
    if (paramString == null || (paramBoolean1 && paramRelationService == null)) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getRoleInt", paramString);
    int i = 0;
    synchronized (this.myRoleName2ValueMap) {
      role = (Role)this.myRoleName2ValueMap.get(paramString);
    } 
    if (role == null) {
      i = 1;
    } else {
      if (paramBoolean1) {
        try {
          roleUnresolved = paramRelationService.checkRoleReading(paramString, this.myRelTypeName);
        } catch (RelationTypeNotFoundException relationTypeNotFoundException) {
          throw new RuntimeException(relationTypeNotFoundException.getMessage());
        } 
      } else {
        Object[] arrayOfObject = new Object[2];
        arrayOfObject[0] = paramString;
        arrayOfObject[1] = this.myRelTypeName;
        String[] arrayOfString = new String[2];
        arrayOfString[0] = "java.lang.String";
        arrayOfString[1] = "java.lang.String";
        try {
          roleUnresolved = (Integer)this.myRelServiceMBeanServer.invoke(this.myRelServiceName, "checkRoleReading", arrayOfObject, arrayOfString);
        } catch (MBeanException mBeanException) {
          throw new RuntimeException("incorrect relation type");
        } catch (ReflectionException reflectionException) {
          throw new RuntimeException(reflectionException.getMessage());
        } catch (InstanceNotFoundException instanceNotFoundException) {
          throw new RelationServiceNotRegisteredException(instanceNotFoundException.getMessage());
        } 
      } 
      i = roleUnresolved.intValue();
    } 
    if (i == 0) {
      if (!paramBoolean2) {
        roleUnresolved = new ArrayList(role.getRoleValue());
      } else {
        roleUnresolved = (Role)role.clone();
      } 
    } else {
      if (!paramBoolean2)
        try {
          RelationService.throwRoleProblemException(i, paramString);
          return null;
        } catch (InvalidRoleValueException invalidRoleValueException) {
          throw new RuntimeException(invalidRoleValueException.getMessage());
        }  
      roleUnresolved = new RoleUnresolved(paramString, null, i);
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getRoleInt");
    return roleUnresolved;
  }
  
  RoleResult getRolesInt(String[] paramArrayOfString, boolean paramBoolean, RelationService paramRelationService) throws IllegalArgumentException, RelationServiceNotRegisteredException {
    if (paramArrayOfString == null || (paramBoolean && paramRelationService == null)) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getRolesInt");
    RoleList roleList = new RoleList();
    RoleUnresolvedList roleUnresolvedList = new RoleUnresolvedList();
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      Object object;
      String str = paramArrayOfString[b];
      try {
        object = getRoleInt(str, paramBoolean, paramRelationService, true);
      } catch (RoleNotFoundException roleNotFoundException) {
        return null;
      } 
      if (object instanceof Role) {
        try {
          roleList.add((Role)object);
        } catch (IllegalArgumentException illegalArgumentException) {
          throw new RuntimeException(illegalArgumentException.getMessage());
        } 
      } else if (object instanceof RoleUnresolved) {
        try {
          roleUnresolvedList.add((RoleUnresolved)object);
        } catch (IllegalArgumentException illegalArgumentException) {
          throw new RuntimeException(illegalArgumentException.getMessage());
        } 
      } 
    } 
    RoleResult roleResult = new RoleResult(roleList, roleUnresolvedList);
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getRolesInt");
    return roleResult;
  }
  
  RoleResult getAllRolesInt(boolean paramBoolean, RelationService paramRelationService) throws IllegalArgumentException, RelationServiceNotRegisteredException {
    ArrayList arrayList;
    if (paramBoolean && paramRelationService == null) {
      arrayList = "Invalid parameter.";
      throw new IllegalArgumentException(arrayList);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getAllRolesInt");
    synchronized (this.myRoleName2ValueMap) {
      arrayList = new ArrayList(this.myRoleName2ValueMap.keySet());
    } 
    String[] arrayOfString = new String[arrayList.size()];
    arrayList.toArray(arrayOfString);
    RoleResult roleResult = getRolesInt(arrayOfString, paramBoolean, paramRelationService);
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getAllRolesInt");
    return roleResult;
  }
  
  Object setRoleInt(Role paramRole, boolean paramBoolean1, RelationService paramRelationService, boolean paramBoolean2) throws IllegalArgumentException, RoleNotFoundException, InvalidRoleValueException, RelationServiceNotRegisteredException, RelationTypeNotFoundException, RelationNotFoundException {
    Boolean bool;
    List list;
    Role role;
    if (paramRole == null || (paramBoolean1 && paramRelationService == null)) {
      String str1 = "Invalid parameter.";
      throw new IllegalArgumentException(str1);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "setRoleInt", new Object[] { paramRole, Boolean.valueOf(paramBoolean1), paramRelationService, Boolean.valueOf(paramBoolean2) });
    String str = paramRole.getRoleName();
    int i = 0;
    synchronized (this.myRoleName2ValueMap) {
      role = (Role)this.myRoleName2ValueMap.get(str);
    } 
    if (role == null) {
      bool = Boolean.valueOf(true);
      list = new ArrayList();
    } else {
      bool = Boolean.valueOf(false);
      list = role.getRoleValue();
    } 
    try {
      Integer integer;
      if (paramBoolean1) {
        integer = paramRelationService.checkRoleWriting(paramRole, this.myRelTypeName, bool);
      } else {
        Object[] arrayOfObject = new Object[3];
        arrayOfObject[0] = paramRole;
        arrayOfObject[1] = this.myRelTypeName;
        arrayOfObject[2] = bool;
        String[] arrayOfString = new String[3];
        arrayOfString[0] = "javax.management.relation.Role";
        arrayOfString[1] = "java.lang.String";
        arrayOfString[2] = "java.lang.Boolean";
        integer = (Integer)this.myRelServiceMBeanServer.invoke(this.myRelServiceName, "checkRoleWriting", arrayOfObject, arrayOfString);
      } 
      i = integer.intValue();
    } catch (MBeanException mBeanException) {
      Exception exception = mBeanException.getTargetException();
      if (exception instanceof RelationTypeNotFoundException)
        throw (RelationTypeNotFoundException)exception; 
      throw new RuntimeException(exception.getMessage());
    } catch (ReflectionException reflectionException) {
      throw new RuntimeException(reflectionException.getMessage());
    } catch (RelationTypeNotFoundException relationTypeNotFoundException) {
      throw new RuntimeException(relationTypeNotFoundException.getMessage());
    } catch (InstanceNotFoundException instanceNotFoundException) {
      throw new RelationServiceNotRegisteredException(instanceNotFoundException.getMessage());
    } 
    RoleUnresolved roleUnresolved = null;
    if (i == 0) {
      if (!bool.booleanValue()) {
        sendRoleUpdateNotification(paramRole, list, paramBoolean1, paramRelationService);
        updateRelationServiceMap(paramRole, list, paramBoolean1, paramRelationService);
      } 
      synchronized (this.myRoleName2ValueMap) {
        this.myRoleName2ValueMap.put(str, (Role)paramRole.clone());
      } 
      if (paramBoolean2)
        roleUnresolved = paramRole; 
    } else {
      if (!paramBoolean2) {
        RelationService.throwRoleProblemException(i, str);
        return null;
      } 
      roleUnresolved = new RoleUnresolved(str, paramRole.getRoleValue(), i);
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "setRoleInt");
    return roleUnresolved;
  }
  
  private void sendRoleUpdateNotification(Role paramRole, List<ObjectName> paramList, boolean paramBoolean, RelationService paramRelationService) throws IllegalArgumentException, RelationServiceNotRegisteredException, RelationNotFoundException {
    if (paramRole == null || paramList == null || (paramBoolean && paramRelationService == null)) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "sendRoleUpdateNotification", new Object[] { paramRole, paramList, Boolean.valueOf(paramBoolean), paramRelationService });
    if (paramBoolean) {
      try {
        paramRelationService.sendRoleUpdateNotification(this.myRelId, paramRole, paramList);
      } catch (RelationNotFoundException relationNotFoundException) {
        throw new RuntimeException(relationNotFoundException.getMessage());
      } 
    } else {
      Object[] arrayOfObject = new Object[3];
      arrayOfObject[0] = this.myRelId;
      arrayOfObject[1] = paramRole;
      arrayOfObject[2] = paramList;
      String[] arrayOfString = new String[3];
      arrayOfString[0] = "java.lang.String";
      arrayOfString[1] = "javax.management.relation.Role";
      arrayOfString[2] = "java.util.List";
      try {
        this.myRelServiceMBeanServer.invoke(this.myRelServiceName, "sendRoleUpdateNotification", arrayOfObject, arrayOfString);
      } catch (ReflectionException reflectionException) {
        throw new RuntimeException(reflectionException.getMessage());
      } catch (InstanceNotFoundException instanceNotFoundException) {
        throw new RelationServiceNotRegisteredException(instanceNotFoundException.getMessage());
      } catch (MBeanException mBeanException) {
        Exception exception = mBeanException.getTargetException();
        if (exception instanceof RelationNotFoundException)
          throw (RelationNotFoundException)exception; 
        throw new RuntimeException(exception.getMessage());
      } 
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "sendRoleUpdateNotification");
  }
  
  private void updateRelationServiceMap(Role paramRole, List<ObjectName> paramList, boolean paramBoolean, RelationService paramRelationService) throws IllegalArgumentException, RelationServiceNotRegisteredException, RelationNotFoundException {
    if (paramRole == null || paramList == null || (paramBoolean && paramRelationService == null)) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "updateRelationServiceMap", new Object[] { paramRole, paramList, Boolean.valueOf(paramBoolean), paramRelationService });
    if (paramBoolean) {
      try {
        paramRelationService.updateRoleMap(this.myRelId, paramRole, paramList);
      } catch (RelationNotFoundException relationNotFoundException) {
        throw new RuntimeException(relationNotFoundException.getMessage());
      } 
    } else {
      Object[] arrayOfObject = new Object[3];
      arrayOfObject[0] = this.myRelId;
      arrayOfObject[1] = paramRole;
      arrayOfObject[2] = paramList;
      String[] arrayOfString = new String[3];
      arrayOfString[0] = "java.lang.String";
      arrayOfString[1] = "javax.management.relation.Role";
      arrayOfString[2] = "java.util.List";
      try {
        this.myRelServiceMBeanServer.invoke(this.myRelServiceName, "updateRoleMap", arrayOfObject, arrayOfString);
      } catch (ReflectionException reflectionException) {
        throw new RuntimeException(reflectionException.getMessage());
      } catch (InstanceNotFoundException instanceNotFoundException) {
        throw new RelationServiceNotRegisteredException(instanceNotFoundException.getMessage());
      } catch (MBeanException mBeanException) {
        Exception exception = mBeanException.getTargetException();
        if (exception instanceof RelationNotFoundException)
          throw (RelationNotFoundException)exception; 
        throw new RuntimeException(exception.getMessage());
      } 
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "updateRelationServiceMap");
  }
  
  RoleResult setRolesInt(RoleList paramRoleList, boolean paramBoolean, RelationService paramRelationService) throws IllegalArgumentException, RelationServiceNotRegisteredException, RelationTypeNotFoundException, RelationNotFoundException {
    if (paramRoleList == null || (paramBoolean && paramRelationService == null)) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "setRolesInt", new Object[] { paramRoleList, Boolean.valueOf(paramBoolean), paramRelationService });
    RoleList roleList = new RoleList();
    RoleUnresolvedList roleUnresolvedList = new RoleUnresolvedList();
    for (Role role : paramRoleList.asList()) {
      Object object = null;
      try {
        object = setRoleInt(role, paramBoolean, paramRelationService, true);
      } catch (RoleNotFoundException roleNotFoundException) {
      
      } catch (InvalidRoleValueException invalidRoleValueException) {}
      if (object instanceof Role)
        try {
          roleList.add((Role)object);
          continue;
        } catch (IllegalArgumentException illegalArgumentException) {
          throw new RuntimeException(illegalArgumentException.getMessage());
        }  
      if (object instanceof RoleUnresolved)
        try {
          roleUnresolvedList.add((RoleUnresolved)object);
        } catch (IllegalArgumentException illegalArgumentException) {
          throw new RuntimeException(illegalArgumentException.getMessage());
        }  
    } 
    RoleResult roleResult = new RoleResult(roleList, roleUnresolvedList);
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "setRolesInt");
    return roleResult;
  }
  
  private void initMembers(String paramString1, ObjectName paramObjectName, MBeanServer paramMBeanServer, String paramString2, RoleList paramRoleList) throws InvalidRoleValueException, IllegalArgumentException {
    if (paramString1 == null || paramObjectName == null || paramString2 == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "initMembers", new Object[] { paramString1, paramObjectName, paramMBeanServer, paramString2, paramRoleList });
    this.myRelId = paramString1;
    this.myRelServiceName = paramObjectName;
    this.myRelServiceMBeanServer = paramMBeanServer;
    this.myRelTypeName = paramString2;
    initRoleMap(paramRoleList);
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "initMembers");
  }
  
  private void initRoleMap(RoleList paramRoleList) throws InvalidRoleValueException {
    if (paramRoleList == null)
      return; 
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "initRoleMap", paramRoleList);
    synchronized (this.myRoleName2ValueMap) {
      for (Role role : paramRoleList.asList()) {
        String str = role.getRoleName();
        if (this.myRoleName2ValueMap.containsKey(str)) {
          StringBuilder stringBuilder = new StringBuilder("Role name ");
          stringBuilder.append(str);
          stringBuilder.append(" used for two roles.");
          throw new InvalidRoleValueException(stringBuilder.toString());
        } 
        this.myRoleName2ValueMap.put(str, (Role)role.clone());
      } 
    } 
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "initRoleMap");
  }
  
  void handleMBeanUnregistrationInt(ObjectName paramObjectName, String paramString, boolean paramBoolean, RelationService paramRelationService) throws IllegalArgumentException, RoleNotFoundException, InvalidRoleValueException, RelationServiceNotRegisteredException, RelationTypeNotFoundException, RelationNotFoundException {
    Role role1;
    if (paramObjectName == null || paramString == null || (paramBoolean && paramRelationService == null)) {
      role1 = "Invalid parameter.";
      throw new IllegalArgumentException(role1);
    } 
    JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "handleMBeanUnregistrationInt", new Object[] { paramObjectName, paramString, Boolean.valueOf(paramBoolean), paramRelationService });
    synchronized (this.myRoleName2ValueMap) {
      role1 = (Role)this.myRoleName2ValueMap.get(paramString);
    } 
    if (role1 == null) {
      StringBuilder stringBuilder = new StringBuilder();
      String str = "No role with name ";
      stringBuilder.append(str);
      stringBuilder.append(paramString);
      throw new RoleNotFoundException(stringBuilder.toString());
    } 
    List list = role1.getRoleValue();
    ArrayList arrayList = new ArrayList(list);
    arrayList.remove(paramObjectName);
    Role role2 = new Role(paramString, arrayList);
    Object object = setRoleInt(role2, paramBoolean, paramRelationService, false);
    JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "handleMBeanUnregistrationInt");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\relation\RelationSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */