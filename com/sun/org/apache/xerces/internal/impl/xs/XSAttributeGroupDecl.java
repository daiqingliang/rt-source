package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xs.XSAnnotation;
import com.sun.org.apache.xerces.internal.xs.XSAttributeGroupDefinition;
import com.sun.org.apache.xerces.internal.xs.XSAttributeUse;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSWildcard;

public class XSAttributeGroupDecl implements XSAttributeGroupDefinition {
  public String fName = null;
  
  public String fTargetNamespace = null;
  
  int fAttrUseNum = 0;
  
  private static final int INITIAL_SIZE = 5;
  
  XSAttributeUseImpl[] fAttributeUses = new XSAttributeUseImpl[5];
  
  public XSWildcardDecl fAttributeWC = null;
  
  public String fIDAttrName = null;
  
  public XSObjectList fAnnotations;
  
  protected XSObjectListImpl fAttrUses = null;
  
  private XSNamespaceItem fNamespaceItem = null;
  
  public String addAttributeUse(XSAttributeUseImpl paramXSAttributeUseImpl) {
    if (paramXSAttributeUseImpl.fUse != 2 && paramXSAttributeUseImpl.fAttrDecl.fType.isIDType())
      if (this.fIDAttrName == null) {
        this.fIDAttrName = paramXSAttributeUseImpl.fAttrDecl.fName;
      } else {
        return this.fIDAttrName;
      }  
    if (this.fAttrUseNum == this.fAttributeUses.length)
      this.fAttributeUses = resize(this.fAttributeUses, this.fAttrUseNum * 2); 
    this.fAttributeUses[this.fAttrUseNum++] = paramXSAttributeUseImpl;
    return null;
  }
  
  public void replaceAttributeUse(XSAttributeUse paramXSAttributeUse, XSAttributeUseImpl paramXSAttributeUseImpl) {
    for (byte b = 0; b < this.fAttrUseNum; b++) {
      if (this.fAttributeUses[b] == paramXSAttributeUse)
        this.fAttributeUses[b] = paramXSAttributeUseImpl; 
    } 
  }
  
  public XSAttributeUse getAttributeUse(String paramString1, String paramString2) {
    for (byte b = 0; b < this.fAttrUseNum; b++) {
      if ((this.fAttributeUses[b]).fAttrDecl.fTargetNamespace == paramString1 && (this.fAttributeUses[b]).fAttrDecl.fName == paramString2)
        return this.fAttributeUses[b]; 
    } 
    return null;
  }
  
  public XSAttributeUse getAttributeUseNoProhibited(String paramString1, String paramString2) {
    for (byte b = 0; b < this.fAttrUseNum; b++) {
      if ((this.fAttributeUses[b]).fAttrDecl.fTargetNamespace == paramString1 && (this.fAttributeUses[b]).fAttrDecl.fName == paramString2 && (this.fAttributeUses[b]).fUse != 2)
        return this.fAttributeUses[b]; 
    } 
    return null;
  }
  
  public void removeProhibitedAttrs() {
    if (this.fAttrUseNum == 0)
      return; 
    byte b1 = 0;
    XSAttributeUseImpl[] arrayOfXSAttributeUseImpl = new XSAttributeUseImpl[this.fAttrUseNum];
    for (byte b2 = 0; b2 < this.fAttrUseNum; b2++) {
      if ((this.fAttributeUses[b2]).fUse != 2)
        arrayOfXSAttributeUseImpl[b1++] = this.fAttributeUses[b2]; 
    } 
    this.fAttributeUses = arrayOfXSAttributeUseImpl;
    this.fAttrUseNum = b1;
  }
  
  public Object[] validRestrictionOf(String paramString, XSAttributeGroupDecl paramXSAttributeGroupDecl) {
    Object[] arrayOfObject = null;
    XSAttributeUseImpl xSAttributeUseImpl1 = null;
    XSAttributeDecl xSAttributeDecl1 = null;
    XSAttributeUseImpl xSAttributeUseImpl2 = null;
    XSAttributeDecl xSAttributeDecl2 = null;
    byte b;
    for (b = 0; b < this.fAttrUseNum; b++) {
      xSAttributeUseImpl1 = this.fAttributeUses[b];
      xSAttributeDecl1 = xSAttributeUseImpl1.fAttrDecl;
      xSAttributeUseImpl2 = (XSAttributeUseImpl)paramXSAttributeGroupDecl.getAttributeUse(xSAttributeDecl1.fTargetNamespace, xSAttributeDecl1.fName);
      if (xSAttributeUseImpl2 != null) {
        if (xSAttributeUseImpl2.getRequired() && !xSAttributeUseImpl1.getRequired())
          return new Object[] { paramString, xSAttributeDecl1.fName, (xSAttributeUseImpl1.fUse == 0) ? "optional" : "prohibited", "derivation-ok-restriction.2.1.1" }; 
        if (xSAttributeUseImpl1.fUse != 2) {
          xSAttributeDecl2 = xSAttributeUseImpl2.fAttrDecl;
          if (!XSConstraints.checkSimpleDerivationOk(xSAttributeDecl1.fType, xSAttributeDecl2.fType, xSAttributeDecl2.fType.getFinal()))
            return new Object[] { paramString, xSAttributeDecl1.fName, xSAttributeDecl1.fType.getName(), xSAttributeDecl2.fType.getName(), "derivation-ok-restriction.2.1.2" }; 
          short s1 = (xSAttributeUseImpl2.fConstraintType != 0) ? xSAttributeUseImpl2.fConstraintType : xSAttributeDecl2.getConstraintType();
          short s2 = (xSAttributeUseImpl1.fConstraintType != 0) ? xSAttributeUseImpl1.fConstraintType : xSAttributeDecl1.getConstraintType();
          if (s1 == 2) {
            if (s2 != 2)
              return new Object[] { paramString, xSAttributeDecl1.fName, "derivation-ok-restriction.2.1.3.a" }; 
            ValidatedInfo validatedInfo1 = (xSAttributeUseImpl2.fDefault != null) ? xSAttributeUseImpl2.fDefault : xSAttributeDecl2.fDefault;
            ValidatedInfo validatedInfo2 = (xSAttributeUseImpl1.fDefault != null) ? xSAttributeUseImpl1.fDefault : xSAttributeDecl1.fDefault;
            if (!validatedInfo1.actualValue.equals(validatedInfo2.actualValue))
              return new Object[] { paramString, xSAttributeDecl1.fName, validatedInfo2.stringValue(), validatedInfo1.stringValue(), "derivation-ok-restriction.2.1.3.b" }; 
          } 
        } 
      } else {
        if (paramXSAttributeGroupDecl.fAttributeWC == null)
          return new Object[] { paramString, xSAttributeDecl1.fName, "derivation-ok-restriction.2.2.a" }; 
        if (!paramXSAttributeGroupDecl.fAttributeWC.allowNamespace(xSAttributeDecl1.fTargetNamespace))
          return new Object[] { paramString, xSAttributeDecl1.fName, (xSAttributeDecl1.fTargetNamespace == null) ? "" : xSAttributeDecl1.fTargetNamespace, "derivation-ok-restriction.2.2.b" }; 
      } 
    } 
    for (b = 0; b < paramXSAttributeGroupDecl.fAttrUseNum; b++) {
      xSAttributeUseImpl2 = paramXSAttributeGroupDecl.fAttributeUses[b];
      if (xSAttributeUseImpl2.fUse == 1) {
        xSAttributeDecl2 = xSAttributeUseImpl2.fAttrDecl;
        if (getAttributeUse(xSAttributeDecl2.fTargetNamespace, xSAttributeDecl2.fName) == null)
          return new Object[] { paramString, xSAttributeUseImpl2.fAttrDecl.fName, "derivation-ok-restriction.3" }; 
      } 
    } 
    if (this.fAttributeWC != null) {
      if (paramXSAttributeGroupDecl.fAttributeWC == null)
        return new Object[] { paramString, "derivation-ok-restriction.4.1" }; 
      if (!this.fAttributeWC.isSubsetOf(paramXSAttributeGroupDecl.fAttributeWC))
        return new Object[] { paramString, "derivation-ok-restriction.4.2" }; 
      if (this.fAttributeWC.weakerProcessContents(paramXSAttributeGroupDecl.fAttributeWC))
        return new Object[] { paramString, this.fAttributeWC.getProcessContentsAsString(), paramXSAttributeGroupDecl.fAttributeWC.getProcessContentsAsString(), "derivation-ok-restriction.4.3" }; 
    } 
    return null;
  }
  
  static final XSAttributeUseImpl[] resize(XSAttributeUseImpl[] paramArrayOfXSAttributeUseImpl, int paramInt) {
    XSAttributeUseImpl[] arrayOfXSAttributeUseImpl = new XSAttributeUseImpl[paramInt];
    System.arraycopy(paramArrayOfXSAttributeUseImpl, 0, arrayOfXSAttributeUseImpl, 0, Math.min(paramArrayOfXSAttributeUseImpl.length, paramInt));
    return arrayOfXSAttributeUseImpl;
  }
  
  public void reset() {
    this.fName = null;
    this.fTargetNamespace = null;
    for (byte b = 0; b < this.fAttrUseNum; b++)
      this.fAttributeUses[b] = null; 
    this.fAttrUseNum = 0;
    this.fAttributeWC = null;
    this.fAnnotations = null;
    this.fIDAttrName = null;
  }
  
  public short getType() { return 5; }
  
  public String getName() { return this.fName; }
  
  public String getNamespace() { return this.fTargetNamespace; }
  
  public XSObjectList getAttributeUses() {
    if (this.fAttrUses == null)
      this.fAttrUses = new XSObjectListImpl(this.fAttributeUses, this.fAttrUseNum); 
    return this.fAttrUses;
  }
  
  public XSWildcard getAttributeWildcard() { return this.fAttributeWC; }
  
  public XSAnnotation getAnnotation() { return (this.fAnnotations != null) ? (XSAnnotation)this.fAnnotations.item(0) : null; }
  
  public XSObjectList getAnnotations() { return (this.fAnnotations != null) ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST; }
  
  public XSNamespaceItem getNamespaceItem() { return this.fNamespaceItem; }
  
  void setNamespaceItem(XSNamespaceItem paramXSNamespaceItem) { this.fNamespaceItem = paramXSNamespaceItem; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\XSAttributeGroupDecl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */