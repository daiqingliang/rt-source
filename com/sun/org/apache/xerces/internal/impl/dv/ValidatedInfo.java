package com.sun.org.apache.xerces.internal.impl.dv;

import com.sun.org.apache.xerces.internal.xs.ShortList;

public class ValidatedInfo {
  public String normalizedValue;
  
  public Object actualValue;
  
  public short actualValueType;
  
  public XSSimpleType memberType;
  
  public XSSimpleType[] memberTypes;
  
  public ShortList itemValueTypes;
  
  public void reset() {
    this.normalizedValue = null;
    this.actualValue = null;
    this.memberType = null;
    this.memberTypes = null;
  }
  
  public String stringValue() { return (this.actualValue == null) ? this.normalizedValue : this.actualValue.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\ValidatedInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */