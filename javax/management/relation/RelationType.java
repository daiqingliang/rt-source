package javax.management.relation;

import java.io.Serializable;
import java.util.List;

public interface RelationType extends Serializable {
  String getRelationTypeName();
  
  List<RoleInfo> getRoleInfos();
  
  RoleInfo getRoleInfo(String paramString) throws IllegalArgumentException, RoleInfoNotFoundException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\relation\RelationType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */