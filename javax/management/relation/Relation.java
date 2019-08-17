package javax.management.relation;

import java.util.List;
import java.util.Map;
import javax.management.ObjectName;

public interface Relation {
  List<ObjectName> getRole(String paramString) throws IllegalArgumentException, RoleNotFoundException, RelationServiceNotRegisteredException;
  
  RoleResult getRoles(String[] paramArrayOfString) throws IllegalArgumentException, RelationServiceNotRegisteredException;
  
  Integer getRoleCardinality(String paramString) throws IllegalArgumentException, RoleNotFoundException;
  
  RoleResult getAllRoles() throws RelationServiceNotRegisteredException;
  
  RoleList retrieveAllRoles();
  
  void setRole(Role paramRole) throws IllegalArgumentException, RoleNotFoundException, RelationTypeNotFoundException, InvalidRoleValueException, RelationServiceNotRegisteredException, RelationNotFoundException;
  
  RoleResult setRoles(RoleList paramRoleList) throws IllegalArgumentException, RelationServiceNotRegisteredException, RelationTypeNotFoundException, RelationNotFoundException;
  
  void handleMBeanUnregistration(ObjectName paramObjectName, String paramString) throws IllegalArgumentException, RoleNotFoundException, InvalidRoleValueException, RelationServiceNotRegisteredException, RelationTypeNotFoundException, RelationNotFoundException;
  
  Map<ObjectName, List<String>> getReferencedMBeans();
  
  String getRelationTypeName();
  
  ObjectName getRelationServiceName();
  
  String getRelationId();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\relation\Relation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */