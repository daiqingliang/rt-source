package javax.accessibility;

public class AccessibleRelation extends AccessibleBundle {
  private Object[] target = new Object[0];
  
  public static final String LABEL_FOR = new String("labelFor");
  
  public static final String LABELED_BY = new String("labeledBy");
  
  public static final String MEMBER_OF = new String("memberOf");
  
  public static final String CONTROLLER_FOR = new String("controllerFor");
  
  public static final String CONTROLLED_BY = new String("controlledBy");
  
  public static final String FLOWS_TO = "flowsTo";
  
  public static final String FLOWS_FROM = "flowsFrom";
  
  public static final String SUBWINDOW_OF = "subwindowOf";
  
  public static final String PARENT_WINDOW_OF = "parentWindowOf";
  
  public static final String EMBEDS = "embeds";
  
  public static final String EMBEDDED_BY = "embeddedBy";
  
  public static final String CHILD_NODE_OF = "childNodeOf";
  
  public static final String LABEL_FOR_PROPERTY = "labelForProperty";
  
  public static final String LABELED_BY_PROPERTY = "labeledByProperty";
  
  public static final String MEMBER_OF_PROPERTY = "memberOfProperty";
  
  public static final String CONTROLLER_FOR_PROPERTY = "controllerForProperty";
  
  public static final String CONTROLLED_BY_PROPERTY = "controlledByProperty";
  
  public static final String FLOWS_TO_PROPERTY = "flowsToProperty";
  
  public static final String FLOWS_FROM_PROPERTY = "flowsFromProperty";
  
  public static final String SUBWINDOW_OF_PROPERTY = "subwindowOfProperty";
  
  public static final String PARENT_WINDOW_OF_PROPERTY = "parentWindowOfProperty";
  
  public static final String EMBEDS_PROPERTY = "embedsProperty";
  
  public static final String EMBEDDED_BY_PROPERTY = "embeddedByProperty";
  
  public static final String CHILD_NODE_OF_PROPERTY = "childNodeOfProperty";
  
  public AccessibleRelation(String paramString) {
    this.key = paramString;
    this.target = null;
  }
  
  public AccessibleRelation(String paramString, Object paramObject) {
    this.key = paramString;
    this.target = new Object[1];
    this.target[0] = paramObject;
  }
  
  public AccessibleRelation(String paramString, Object[] paramArrayOfObject) {
    this.key = paramString;
    this.target = paramArrayOfObject;
  }
  
  public String getKey() { return this.key; }
  
  public Object[] getTarget() {
    if (this.target == null)
      this.target = new Object[0]; 
    Object[] arrayOfObject = new Object[this.target.length];
    for (byte b = 0; b < this.target.length; b++)
      arrayOfObject[b] = this.target[b]; 
    return arrayOfObject;
  }
  
  public void setTarget(Object paramObject) {
    this.target = new Object[1];
    this.target[0] = paramObject;
  }
  
  public void setTarget(Object[] paramArrayOfObject) { this.target = paramArrayOfObject; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\accessibility\AccessibleRelation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */