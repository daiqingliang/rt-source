package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeFacetException;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.impl.dv.XSFacets;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;

public class XSSimpleTypeDelegate implements XSSimpleType {
  protected final XSSimpleType type;
  
  public XSSimpleTypeDelegate(XSSimpleType paramXSSimpleType) {
    if (paramXSSimpleType == null)
      throw new NullPointerException(); 
    this.type = paramXSSimpleType;
  }
  
  public XSSimpleType getWrappedXSSimpleType() { return this.type; }
  
  public XSObjectList getAnnotations() { return this.type.getAnnotations(); }
  
  public boolean getBounded() { return this.type.getBounded(); }
  
  public short getBuiltInKind() { return this.type.getBuiltInKind(); }
  
  public short getDefinedFacets() { return this.type.getDefinedFacets(); }
  
  public XSObjectList getFacets() { return this.type.getFacets(); }
  
  public boolean getFinite() { return this.type.getFinite(); }
  
  public short getFixedFacets() { return this.type.getFixedFacets(); }
  
  public XSSimpleTypeDefinition getItemType() { return this.type.getItemType(); }
  
  public StringList getLexicalEnumeration() { return this.type.getLexicalEnumeration(); }
  
  public String getLexicalFacetValue(short paramShort) { return this.type.getLexicalFacetValue(paramShort); }
  
  public StringList getLexicalPattern() { return this.type.getLexicalPattern(); }
  
  public XSObjectList getMemberTypes() { return this.type.getMemberTypes(); }
  
  public XSObjectList getMultiValueFacets() { return this.type.getMultiValueFacets(); }
  
  public boolean getNumeric() { return this.type.getNumeric(); }
  
  public short getOrdered() { return this.type.getOrdered(); }
  
  public XSSimpleTypeDefinition getPrimitiveType() { return this.type.getPrimitiveType(); }
  
  public short getVariety() { return this.type.getVariety(); }
  
  public boolean isDefinedFacet(short paramShort) { return this.type.isDefinedFacet(paramShort); }
  
  public boolean isFixedFacet(short paramShort) { return this.type.isFixedFacet(paramShort); }
  
  public boolean derivedFrom(String paramString1, String paramString2, short paramShort) { return this.type.derivedFrom(paramString1, paramString2, paramShort); }
  
  public boolean derivedFromType(XSTypeDefinition paramXSTypeDefinition, short paramShort) { return this.type.derivedFromType(paramXSTypeDefinition, paramShort); }
  
  public boolean getAnonymous() { return this.type.getAnonymous(); }
  
  public XSTypeDefinition getBaseType() { return this.type.getBaseType(); }
  
  public short getFinal() { return this.type.getFinal(); }
  
  public short getTypeCategory() { return this.type.getTypeCategory(); }
  
  public boolean isFinal(short paramShort) { return this.type.isFinal(paramShort); }
  
  public String getName() { return this.type.getName(); }
  
  public String getNamespace() { return this.type.getNamespace(); }
  
  public XSNamespaceItem getNamespaceItem() { return this.type.getNamespaceItem(); }
  
  public short getType() { return this.type.getType(); }
  
  public void applyFacets(XSFacets paramXSFacets, short paramShort1, short paramShort2, ValidationContext paramValidationContext) throws InvalidDatatypeFacetException { this.type.applyFacets(paramXSFacets, paramShort1, paramShort2, paramValidationContext); }
  
  public short getPrimitiveKind() { return this.type.getPrimitiveKind(); }
  
  public short getWhitespace() { return this.type.getWhitespace(); }
  
  public boolean isEqual(Object paramObject1, Object paramObject2) { return this.type.isEqual(paramObject1, paramObject2); }
  
  public boolean isIDType() { return this.type.isIDType(); }
  
  public void validate(ValidationContext paramValidationContext, ValidatedInfo paramValidatedInfo) throws InvalidDatatypeValueException { this.type.validate(paramValidationContext, paramValidatedInfo); }
  
  public Object validate(String paramString, ValidationContext paramValidationContext, ValidatedInfo paramValidatedInfo) throws InvalidDatatypeValueException { return this.type.validate(paramString, paramValidationContext, paramValidatedInfo); }
  
  public Object validate(Object paramObject, ValidationContext paramValidationContext, ValidatedInfo paramValidatedInfo) throws InvalidDatatypeValueException { return this.type.validate(paramObject, paramValidationContext, paramValidatedInfo); }
  
  public String toString() { return this.type.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\XSSimpleTypeDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */