package javax.management.relation;

import java.util.List;
import java.util.Map;
import javax.management.InstanceNotFoundException;
import javax.management.ObjectName;

public interface RelationServiceMBean {
  void isActive() throws RelationServiceNotRegisteredException;
  
  boolean getPurgeFlag();
  
  void setPurgeFlag(boolean paramBoolean);
  
  void createRelationType(String paramString, RoleInfo[] paramArrayOfRoleInfo) throws IllegalArgumentException, InvalidRelationTypeException;
  
  void addRelationType(RelationType paramRelationType) throws IllegalArgumentException, InvalidRelationTypeException;
  
  List<String> getAllRelationTypeNames();
  
  List<RoleInfo> getRoleInfos(String paramString) throws IllegalArgumentException, RelationTypeNotFoundException;
  
  RoleInfo getRoleInfo(String paramString1, String paramString2) throws IllegalArgumentException, RelationTypeNotFoundException, RoleInfoNotFoundException;
  
  void removeRelationType(String paramString) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationTypeNotFoundException;
  
  void createRelation(String paramString1, String paramString2, RoleList paramRoleList) throws RelationServiceNotRegisteredException, IllegalArgumentException, RoleNotFoundException, InvalidRelationIdException, RelationTypeNotFoundException, InvalidRoleValueException;
  
  void addRelation(ObjectName paramObjectName) throws IllegalArgumentException, RelationServiceNotRegisteredException, NoSuchMethodException, InvalidRelationIdException, InstanceNotFoundException, InvalidRelationServiceException, RelationTypeNotFoundException, RoleNotFoundException, InvalidRoleValueException;
  
  ObjectName isRelationMBean(String paramString) throws IllegalArgumentException, RelationNotFoundException;
  
  String isRelation(ObjectName paramObjectName) throws IllegalArgumentException;
  
  Boolean hasRelation(String paramString) throws IllegalArgumentException;
  
  List<String> getAllRelationIds();
  
  Integer checkRoleReading(String paramString1, String paramString2) throws IllegalArgumentException, RelationTypeNotFoundException;
  
  Integer checkRoleWriting(Role paramRole, String paramString, Boolean paramBoolean) throws IllegalArgumentException, RelationTypeNotFoundException;
  
  void sendRelationCreationNotification(String paramString) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationTypeNotFoundException;
  
  void sendRoleUpdateNotification(String paramString, Role paramRole, List<ObjectName> paramList) throws IllegalArgumentException, RelationNotFoundException;
  
  void sendRelationRemovalNotification(String paramString, List<ObjectName> paramList) throws IllegalArgumentException, RelationNotFoundException;
  
  void updateRoleMap(String paramString, Role paramRole, List<ObjectName> paramList) throws IllegalArgumentException, RelationNotFoundException;
  
  void removeRelation(String paramString) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationTypeNotFoundException;
  
  void purgeRelations() throws RelationServiceNotRegisteredException;
  
  Map<String, List<String>> findReferencingRelations(ObjectName paramObjectName, String paramString1, String paramString2) throws IllegalArgumentException;
  
  Map<ObjectName, List<String>> findAssociatedMBeans(ObjectName paramObjectName, String paramString1, String paramString2) throws IllegalArgumentException;
  
  List<String> findRelationsOfType(String paramString) throws IllegalArgumentException, RelationTypeNotFoundException;
  
  List<ObjectName> getRole(String paramString1, String paramString2) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException, RoleNotFoundException;
  
  RoleResult getRoles(String paramString, String[] paramArrayOfString) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException;
  
  RoleResult getAllRoles(String paramString) throws IllegalArgumentException, RelationNotFoundException, RelationServiceNotRegisteredException;
  
  Integer getRoleCardinality(String paramString1, String paramString2) throws IllegalArgumentException, RelationTypeNotFoundException;
  
  void setRole(String paramString, Role paramRole) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException, RoleNotFoundException, InvalidRoleValueException, RelationTypeNotFoundException;
  
  RoleResult setRoles(String paramString, RoleList paramRoleList) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException;
  
  Map<ObjectName, List<String>> getReferencedMBeans(String paramString) throws IllegalArgumentException, RelationNotFoundException;
  
  String getRelationTypeName(String paramString) throws IllegalArgumentException, RelationNotFoundException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\relation\RelationServiceMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */