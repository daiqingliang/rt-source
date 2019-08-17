package javax.lang.model.type;

public static enum TypeKind {
  BOOLEAN, BYTE, SHORT, INT, LONG, CHAR, FLOAT, DOUBLE, VOID, NONE, NULL, ARRAY, DECLARED, ERROR, TYPEVAR, WILDCARD, PACKAGE, EXECUTABLE, OTHER, UNION, INTERSECTION;
  
  public boolean isPrimitive() {
    switch (this) {
      case BOOLEAN:
      case BYTE:
      case SHORT:
      case INT:
      case LONG:
      case CHAR:
      case FLOAT:
      case DOUBLE:
        return true;
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\model\type\TypeKind.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */