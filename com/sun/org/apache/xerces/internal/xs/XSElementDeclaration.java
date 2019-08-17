package com.sun.org.apache.xerces.internal.xs;

public interface XSElementDeclaration extends XSTerm {
  XSTypeDefinition getTypeDefinition();
  
  short getScope();
  
  XSComplexTypeDefinition getEnclosingCTDefinition();
  
  short getConstraintType();
  
  String getConstraintValue();
  
  Object getActualVC() throws XSException;
  
  short getActualVCType();
  
  ShortList getItemValueTypes() throws XSException;
  
  boolean getNillable();
  
  XSNamedMap getIdentityConstraints();
  
  XSElementDeclaration getSubstitutionGroupAffiliation();
  
  boolean isSubstitutionGroupExclusion(short paramShort);
  
  short getSubstitutionGroupExclusions();
  
  boolean isDisallowedSubstitution(short paramShort);
  
  short getDisallowedSubstitutions();
  
  boolean getAbstract();
  
  XSAnnotation getAnnotation();
  
  XSObjectList getAnnotations();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xs\XSElementDeclaration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */