package com.sun.org.apache.xerces.internal.xpointer;

import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;

class ShortHandPointer implements XPointerPart {
  private String fShortHandPointer;
  
  private boolean fIsFragmentResolved = false;
  
  private SymbolTable fSymbolTable;
  
  int fMatchingChildCount = 0;
  
  public ShortHandPointer() {}
  
  public ShortHandPointer(SymbolTable paramSymbolTable) { this.fSymbolTable = paramSymbolTable; }
  
  public void parseXPointer(String paramString) throws XNIException {
    this.fShortHandPointer = paramString;
    this.fIsFragmentResolved = false;
  }
  
  public boolean resolveXPointer(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations, int paramInt) throws XNIException {
    if (this.fMatchingChildCount == 0)
      this.fIsFragmentResolved = false; 
    if (paramInt == 0) {
      if (this.fMatchingChildCount == 0)
        this.fIsFragmentResolved = hasMatchingIdentifier(paramQName, paramXMLAttributes, paramAugmentations, paramInt); 
      if (this.fIsFragmentResolved)
        this.fMatchingChildCount++; 
    } else if (paramInt == 2) {
      if (this.fMatchingChildCount == 0)
        this.fIsFragmentResolved = hasMatchingIdentifier(paramQName, paramXMLAttributes, paramAugmentations, paramInt); 
    } else if (this.fIsFragmentResolved) {
      this.fMatchingChildCount--;
    } 
    return this.fIsFragmentResolved;
  }
  
  private boolean hasMatchingIdentifier(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations, int paramInt) throws XNIException {
    String str = null;
    if (paramXMLAttributes != null)
      for (byte b = 0; b < paramXMLAttributes.getLength(); b++) {
        str = getSchemaDeterminedID(paramXMLAttributes, b);
        if (str != null)
          break; 
        str = getChildrenSchemaDeterminedID(paramXMLAttributes, b);
        if (str != null)
          break; 
        str = getDTDDeterminedID(paramXMLAttributes, b);
        if (str != null)
          break; 
      }  
    return (str != null && str.equals(this.fShortHandPointer));
  }
  
  public String getDTDDeterminedID(XMLAttributes paramXMLAttributes, int paramInt) throws XNIException { return paramXMLAttributes.getType(paramInt).equals("ID") ? paramXMLAttributes.getValue(paramInt) : null; }
  
  public String getSchemaDeterminedID(XMLAttributes paramXMLAttributes, int paramInt) throws XNIException {
    Augmentations augmentations = paramXMLAttributes.getAugmentations(paramInt);
    AttributePSVI attributePSVI = (AttributePSVI)augmentations.getItem("ATTRIBUTE_PSVI");
    if (attributePSVI != null) {
      XSTypeDefinition xSTypeDefinition = attributePSVI.getMemberTypeDefinition();
      if (xSTypeDefinition != null)
        xSTypeDefinition = attributePSVI.getTypeDefinition(); 
      if (xSTypeDefinition != null && ((XSSimpleType)xSTypeDefinition).isIDType())
        return attributePSVI.getSchemaNormalizedValue(); 
    } 
    return null;
  }
  
  public String getChildrenSchemaDeterminedID(XMLAttributes paramXMLAttributes, int paramInt) throws XNIException { return null; }
  
  public boolean isFragmentResolved() { return this.fIsFragmentResolved; }
  
  public boolean isChildFragmentResolved() { return this.fIsFragmentResolved & ((this.fMatchingChildCount > 0) ? 1 : 0); }
  
  public String getSchemeName() { return this.fShortHandPointer; }
  
  public String getSchemeData() { return null; }
  
  public void setSchemeName(String paramString) throws XNIException { this.fShortHandPointer = paramString; }
  
  public void setSchemeData(String paramString) throws XNIException {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xpointer\ShortHandPointer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */