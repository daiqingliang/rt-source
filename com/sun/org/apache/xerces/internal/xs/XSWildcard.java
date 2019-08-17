package com.sun.org.apache.xerces.internal.xs;

public interface XSWildcard extends XSTerm {
  public static final short NSCONSTRAINT_ANY = 1;
  
  public static final short NSCONSTRAINT_NOT = 2;
  
  public static final short NSCONSTRAINT_LIST = 3;
  
  public static final short PC_STRICT = 1;
  
  public static final short PC_SKIP = 2;
  
  public static final short PC_LAX = 3;
  
  short getConstraintType();
  
  StringList getNsConstraintList();
  
  short getProcessContents();
  
  XSAnnotation getAnnotation();
  
  XSObjectList getAnnotations();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xs\XSWildcard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */