package javax.management.relation;

public interface RelationSupportMBean extends Relation {
  Boolean isInRelationService();
  
  void setRelationServiceManagementFlag(Boolean paramBoolean) throws IllegalArgumentException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\relation\RelationSupportMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */