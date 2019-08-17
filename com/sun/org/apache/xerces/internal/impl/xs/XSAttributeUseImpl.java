package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.XSAttributeDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSAttributeUse;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;

public class XSAttributeUseImpl implements XSAttributeUse {
  public XSAttributeDecl fAttrDecl = null;
  
  public short fUse = 0;
  
  public short fConstraintType = 0;
  
  public ValidatedInfo fDefault = null;
  
  public XSObjectList fAnnotations = null;
  
  public void reset() {
    this.fDefault = null;
    this.fAttrDecl = null;
    this.fUse = 0;
    this.fConstraintType = 0;
    this.fAnnotations = null;
  }
  
  public short getType() { return 4; }
  
  public String getName() { return null; }
  
  public String getNamespace() { return null; }
  
  public boolean getRequired() { return (this.fUse == 1); }
  
  public XSAttributeDeclaration getAttrDeclaration() { return this.fAttrDecl; }
  
  public short getConstraintType() { return this.fConstraintType; }
  
  public String getConstraintValue() { return (getConstraintType() == 0) ? null : this.fDefault.stringValue(); }
  
  public XSNamespaceItem getNamespaceItem() { return null; }
  
  public Object getActualVC() { return (getConstraintType() == 0) ? null : this.fDefault.actualValue; }
  
  public short getActualVCType() { return (getConstraintType() == 0) ? 45 : this.fDefault.actualValueType; }
  
  public ShortList getItemValueTypes() { return (getConstraintType() == 0) ? null : this.fDefault.itemValueTypes; }
  
  public XSObjectList getAnnotations() { return (this.fAnnotations != null) ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\XSAttributeUseImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */