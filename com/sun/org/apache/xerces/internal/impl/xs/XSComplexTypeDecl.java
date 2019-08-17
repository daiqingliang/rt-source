package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.models.CMBuilder;
import com.sun.org.apache.xerces.internal.impl.xs.models.XSCMValidator;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xs.XSAttributeUse;
import com.sun.org.apache.xerces.internal.xs.XSComplexTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSParticle;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSWildcard;
import org.w3c.dom.TypeInfo;

public class XSComplexTypeDecl implements XSComplexTypeDefinition, TypeInfo {
  String fName = null;
  
  String fTargetNamespace = null;
  
  XSTypeDefinition fBaseType = null;
  
  short fDerivedBy = 2;
  
  short fFinal = 0;
  
  short fBlock = 0;
  
  short fMiscFlags = 0;
  
  XSAttributeGroupDecl fAttrGrp = null;
  
  short fContentType = 0;
  
  XSSimpleType fXSSimpleType = null;
  
  XSParticleDecl fParticle = null;
  
  XSCMValidator fUPACMValidator = null;
  
  XSObjectListImpl fAnnotations = null;
  
  private XSNamespaceItem fNamespaceItem = null;
  
  static final int DERIVATION_ANY = 0;
  
  static final int DERIVATION_RESTRICTION = 1;
  
  static final int DERIVATION_EXTENSION = 2;
  
  static final int DERIVATION_UNION = 4;
  
  static final int DERIVATION_LIST = 8;
  
  private static final short CT_IS_ABSTRACT = 1;
  
  private static final short CT_HAS_TYPE_ID = 2;
  
  private static final short CT_IS_ANONYMOUS = 4;
  
  public void setValues(String paramString1, String paramString2, XSTypeDefinition paramXSTypeDefinition, short paramShort1, short paramShort2, short paramShort3, short paramShort4, boolean paramBoolean, XSAttributeGroupDecl paramXSAttributeGroupDecl, XSSimpleType paramXSSimpleType, XSParticleDecl paramXSParticleDecl, XSObjectListImpl paramXSObjectListImpl) {
    this.fTargetNamespace = paramString2;
    this.fBaseType = paramXSTypeDefinition;
    this.fDerivedBy = paramShort1;
    this.fFinal = paramShort2;
    this.fBlock = paramShort3;
    this.fContentType = paramShort4;
    if (paramBoolean)
      this.fMiscFlags = (short)(this.fMiscFlags | true); 
    this.fAttrGrp = paramXSAttributeGroupDecl;
    this.fXSSimpleType = paramXSSimpleType;
    this.fParticle = paramXSParticleDecl;
    this.fAnnotations = paramXSObjectListImpl;
  }
  
  public void setName(String paramString) { this.fName = paramString; }
  
  public short getTypeCategory() { return 15; }
  
  public String getTypeName() { return this.fName; }
  
  public short getFinalSet() { return this.fFinal; }
  
  public String getTargetNamespace() { return this.fTargetNamespace; }
  
  public boolean containsTypeID() { return ((this.fMiscFlags & 0x2) != 0); }
  
  public void setIsAbstractType() { this.fMiscFlags = (short)(this.fMiscFlags | true); }
  
  public void setContainsTypeID() { this.fMiscFlags = (short)(this.fMiscFlags | 0x2); }
  
  public void setIsAnonymous() { this.fMiscFlags = (short)(this.fMiscFlags | 0x4); }
  
  public XSCMValidator getContentModel(CMBuilder paramCMBuilder) {
    if (this.fContentType == 1 || this.fContentType == 0)
      return null; 
    if (this.fCMValidator == null)
      synchronized (this) {
        if (this.fCMValidator == null)
          this.fCMValidator = paramCMBuilder.getContentModel(this); 
      }  
    return this.fCMValidator;
  }
  
  public XSAttributeGroupDecl getAttrGrp() { return this.fAttrGrp; }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(192);
    appendTypeInfo(stringBuilder);
    return stringBuilder.toString();
  }
  
  void appendTypeInfo(StringBuilder paramStringBuilder) {
    String[] arrayOfString1 = { "EMPTY", "SIMPLE", "ELEMENT", "MIXED" };
    String[] arrayOfString2 = { "EMPTY", "EXTENSION", "RESTRICTION" };
    paramStringBuilder.append("Complex type name='").append(this.fTargetNamespace).append(',').append(getTypeName()).append("', ");
    if (this.fBaseType != null)
      paramStringBuilder.append(" base type name='").append(this.fBaseType.getName()).append("', "); 
    paramStringBuilder.append(" content type='").append(arrayOfString1[this.fContentType]).append("', ");
    paramStringBuilder.append(" isAbstract='").append(getAbstract()).append("', ");
    paramStringBuilder.append(" hasTypeId='").append(containsTypeID()).append("', ");
    paramStringBuilder.append(" final='").append(this.fFinal).append("', ");
    paramStringBuilder.append(" block='").append(this.fBlock).append("', ");
    if (this.fParticle != null)
      paramStringBuilder.append(" particle='").append(this.fParticle.toString()).append("', "); 
    paramStringBuilder.append(" derivedBy='").append(arrayOfString2[this.fDerivedBy]).append("'. ");
  }
  
  public boolean derivedFromType(XSTypeDefinition paramXSTypeDefinition, short paramShort) {
    if (paramXSTypeDefinition == null)
      return false; 
    if (paramXSTypeDefinition == SchemaGrammar.fAnyType)
      return true; 
    XSTypeDefinition xSTypeDefinition = this;
    while (xSTypeDefinition != paramXSTypeDefinition && xSTypeDefinition != SchemaGrammar.fAnySimpleType && xSTypeDefinition != SchemaGrammar.fAnyType)
      xSTypeDefinition = xSTypeDefinition.getBaseType(); 
    return (xSTypeDefinition == paramXSTypeDefinition);
  }
  
  public boolean derivedFrom(String paramString1, String paramString2, short paramShort) {
    if (paramString2 == null)
      return false; 
    if (paramString1 != null && paramString1.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) && paramString2.equals("anyType"))
      return true; 
    XSTypeDefinition xSTypeDefinition = this;
    while ((!paramString2.equals(xSTypeDefinition.getName()) || ((paramString1 != null || xSTypeDefinition.getNamespace() != null) && (paramString1 == null || !paramString1.equals(xSTypeDefinition.getNamespace())))) && xSTypeDefinition != SchemaGrammar.fAnySimpleType && xSTypeDefinition != SchemaGrammar.fAnyType)
      xSTypeDefinition = xSTypeDefinition.getBaseType(); 
    return (xSTypeDefinition != SchemaGrammar.fAnySimpleType && xSTypeDefinition != SchemaGrammar.fAnyType);
  }
  
  public boolean isDOMDerivedFrom(String paramString1, String paramString2, int paramInt) {
    if (paramString2 == null)
      return false; 
    if (paramString1 != null && paramString1.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) && paramString2.equals("anyType") && paramInt == 1 && paramInt == 2)
      return true; 
    if ((paramInt & true) != 0 && isDerivedByRestriction(paramString1, paramString2, paramInt, this))
      return true; 
    if ((paramInt & 0x2) != 0 && isDerivedByExtension(paramString1, paramString2, paramInt, this))
      return true; 
    if (((paramInt & 0x8) != 0 || (paramInt & 0x4) != 0) && (paramInt & true) == 0 && (paramInt & 0x2) == 0) {
      if (paramString1.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) && paramString2.equals("anyType"))
        paramString2 = "anySimpleType"; 
      if (!this.fName.equals("anyType") || !this.fTargetNamespace.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA)) {
        if (this.fBaseType != null && this.fBaseType instanceof XSSimpleTypeDecl)
          return ((XSSimpleTypeDecl)this.fBaseType).isDOMDerivedFrom(paramString1, paramString2, paramInt); 
        if (this.fBaseType != null && this.fBaseType instanceof XSComplexTypeDecl)
          return ((XSComplexTypeDecl)this.fBaseType).isDOMDerivedFrom(paramString1, paramString2, paramInt); 
      } 
    } 
    return ((paramInt & 0x2) == 0 && (paramInt & true) == 0 && (paramInt & 0x8) == 0 && (paramInt & 0x4) == 0) ? isDerivedByAny(paramString1, paramString2, paramInt, this) : 0;
  }
  
  private boolean isDerivedByAny(String paramString1, String paramString2, int paramInt, XSTypeDefinition paramXSTypeDefinition) {
    XSTypeDefinition xSTypeDefinition = null;
    boolean bool = false;
    while (paramXSTypeDefinition != null && paramXSTypeDefinition != xSTypeDefinition) {
      if (paramString2.equals(paramXSTypeDefinition.getName()) && ((paramString1 == null && paramXSTypeDefinition.getNamespace() == null) || (paramString1 != null && paramString1.equals(paramXSTypeDefinition.getNamespace())))) {
        bool = true;
        break;
      } 
      if (isDerivedByRestriction(paramString1, paramString2, paramInt, paramXSTypeDefinition))
        return true; 
      if (!isDerivedByExtension(paramString1, paramString2, paramInt, paramXSTypeDefinition))
        return true; 
      xSTypeDefinition = paramXSTypeDefinition;
      paramXSTypeDefinition = paramXSTypeDefinition.getBaseType();
    } 
    return bool;
  }
  
  private boolean isDerivedByRestriction(String paramString1, String paramString2, int paramInt, XSTypeDefinition paramXSTypeDefinition) {
    XSTypeDefinition xSTypeDefinition = null;
    while (paramXSTypeDefinition != null && paramXSTypeDefinition != xSTypeDefinition) {
      if (paramString1 != null && paramString1.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) && paramString2.equals("anySimpleType"))
        return false; 
      if ((paramString2.equals(paramXSTypeDefinition.getName()) && paramString1 != null && paramString1.equals(paramXSTypeDefinition.getNamespace())) || (paramXSTypeDefinition.getNamespace() == null && paramString1 == null))
        return true; 
      if (paramXSTypeDefinition instanceof XSSimpleTypeDecl) {
        if (paramString1.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) && paramString2.equals("anyType"))
          paramString2 = "anySimpleType"; 
        return ((XSSimpleTypeDecl)paramXSTypeDefinition).isDOMDerivedFrom(paramString1, paramString2, paramInt);
      } 
      if (((XSComplexTypeDecl)paramXSTypeDefinition).getDerivationMethod() != 2)
        return false; 
      xSTypeDefinition = paramXSTypeDefinition;
      paramXSTypeDefinition = paramXSTypeDefinition.getBaseType();
    } 
    return false;
  }
  
  private boolean isDerivedByExtension(String paramString1, String paramString2, int paramInt, XSTypeDefinition paramXSTypeDefinition) {
    boolean bool = false;
    XSTypeDefinition xSTypeDefinition = null;
    while (paramXSTypeDefinition != null && paramXSTypeDefinition != xSTypeDefinition && (paramString1 == null || !paramString1.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) || !paramString2.equals("anySimpleType") || !SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(paramXSTypeDefinition.getNamespace()) || !"anyType".equals(paramXSTypeDefinition.getName()))) {
      if (paramString2.equals(paramXSTypeDefinition.getName()) && ((paramString1 == null && paramXSTypeDefinition.getNamespace() == null) || (paramString1 != null && paramString1.equals(paramXSTypeDefinition.getNamespace()))))
        return bool; 
      if (paramXSTypeDefinition instanceof XSSimpleTypeDecl) {
        if (paramString1.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) && paramString2.equals("anyType"))
          paramString2 = "anySimpleType"; 
        return ((paramInt & 0x2) != 0) ? (bool & ((XSSimpleTypeDecl)paramXSTypeDefinition).isDOMDerivedFrom(paramString1, paramString2, paramInt & true)) : (bool & ((XSSimpleTypeDecl)paramXSTypeDefinition).isDOMDerivedFrom(paramString1, paramString2, paramInt));
      } 
      if (((XSComplexTypeDecl)paramXSTypeDefinition).getDerivationMethod() == 1)
        bool |= true; 
      xSTypeDefinition = paramXSTypeDefinition;
      paramXSTypeDefinition = paramXSTypeDefinition.getBaseType();
    } 
    return false;
  }
  
  public void reset() {
    this.fName = null;
    this.fTargetNamespace = null;
    this.fBaseType = null;
    this.fDerivedBy = 2;
    this.fFinal = 0;
    this.fBlock = 0;
    this.fMiscFlags = 0;
    this.fAttrGrp.reset();
    this.fContentType = 0;
    this.fXSSimpleType = null;
    this.fParticle = null;
    this.fCMValidator = null;
    this.fUPACMValidator = null;
    if (this.fAnnotations != null)
      this.fAnnotations.clearXSObjectList(); 
    this.fAnnotations = null;
  }
  
  public short getType() { return 3; }
  
  public String getName() { return getAnonymous() ? null : this.fName; }
  
  public boolean getAnonymous() { return ((this.fMiscFlags & 0x4) != 0); }
  
  public String getNamespace() { return this.fTargetNamespace; }
  
  public XSTypeDefinition getBaseType() { return this.fBaseType; }
  
  public short getDerivationMethod() { return this.fDerivedBy; }
  
  public boolean isFinal(short paramShort) { return ((this.fFinal & paramShort) != 0); }
  
  public short getFinal() { return this.fFinal; }
  
  public boolean getAbstract() { return ((this.fMiscFlags & true) != 0); }
  
  public XSObjectList getAttributeUses() { return this.fAttrGrp.getAttributeUses(); }
  
  public XSWildcard getAttributeWildcard() { return this.fAttrGrp.getAttributeWildcard(); }
  
  public short getContentType() { return this.fContentType; }
  
  public XSSimpleTypeDefinition getSimpleType() { return this.fXSSimpleType; }
  
  public XSParticle getParticle() { return this.fParticle; }
  
  public boolean isProhibitedSubstitution(short paramShort) { return ((this.fBlock & paramShort) != 0); }
  
  public short getProhibitedSubstitutions() { return this.fBlock; }
  
  public XSObjectList getAnnotations() { return (this.fAnnotations != null) ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST; }
  
  public XSNamespaceItem getNamespaceItem() { return this.fNamespaceItem; }
  
  void setNamespaceItem(XSNamespaceItem paramXSNamespaceItem) { this.fNamespaceItem = paramXSNamespaceItem; }
  
  public XSAttributeUse getAttributeUse(String paramString1, String paramString2) { return this.fAttrGrp.getAttributeUse(paramString1, paramString2); }
  
  public String getTypeNamespace() { return getNamespace(); }
  
  public boolean isDerivedFrom(String paramString1, String paramString2, int paramInt) { return isDOMDerivedFrom(paramString1, paramString2, paramInt); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\XSComplexTypeDecl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */