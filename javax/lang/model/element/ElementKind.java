package javax.lang.model.element;

public static enum ElementKind {
  PACKAGE, ENUM, CLASS, ANNOTATION_TYPE, INTERFACE, ENUM_CONSTANT, FIELD, PARAMETER, LOCAL_VARIABLE, EXCEPTION_PARAMETER, METHOD, CONSTRUCTOR, STATIC_INIT, INSTANCE_INIT, TYPE_PARAMETER, OTHER, RESOURCE_VARIABLE;
  
  public boolean isClass() { return (this == CLASS || this == ENUM); }
  
  public boolean isInterface() { return (this == INTERFACE || this == ANNOTATION_TYPE); }
  
  public boolean isField() { return (this == FIELD || this == ENUM_CONSTANT); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\model\element\ElementKind.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */