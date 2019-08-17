package com.sun.org.apache.xerces.internal.xs;

public interface XSAttributeDeclaration extends XSObject {
  XSSimpleTypeDefinition getTypeDefinition();
  
  short getScope();
  
  XSComplexTypeDefinition getEnclosingCTDefinition();
  
  short getConstraintType();
  
  String getConstraintValue();
  
  Object getActualVC() throws XSException;
  
  short getActualVCType();
  
  ShortList getItemValueTypes() throws XSException;
  
  XSAnnotation getAnnotation();
  
  XSObjectList getAnnotations();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xs\XSAttributeDeclaration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */