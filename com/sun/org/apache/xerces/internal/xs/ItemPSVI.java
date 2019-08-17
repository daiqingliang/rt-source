package com.sun.org.apache.xerces.internal.xs;

public interface ItemPSVI {
  public static final short VALIDITY_NOTKNOWN = 0;
  
  public static final short VALIDITY_INVALID = 1;
  
  public static final short VALIDITY_VALID = 2;
  
  public static final short VALIDATION_NONE = 0;
  
  public static final short VALIDATION_PARTIAL = 1;
  
  public static final short VALIDATION_FULL = 2;
  
  String getValidationContext();
  
  short getValidity();
  
  short getValidationAttempted();
  
  StringList getErrorCodes();
  
  String getSchemaNormalizedValue();
  
  Object getActualNormalizedValue() throws XSException;
  
  short getActualNormalizedValueType();
  
  ShortList getItemValueTypes() throws XSException;
  
  XSTypeDefinition getTypeDefinition();
  
  XSSimpleTypeDefinition getMemberTypeDefinition();
  
  String getSchemaDefault();
  
  boolean getIsSchemaSpecified();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xs\ItemPSVI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */