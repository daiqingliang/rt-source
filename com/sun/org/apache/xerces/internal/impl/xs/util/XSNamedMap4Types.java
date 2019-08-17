package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;

public final class XSNamedMap4Types extends XSNamedMapImpl {
  private final short fType;
  
  public XSNamedMap4Types(String paramString, SymbolHash paramSymbolHash, short paramShort) {
    super(paramString, paramSymbolHash);
    this.fType = paramShort;
  }
  
  public XSNamedMap4Types(String[] paramArrayOfString, SymbolHash[] paramArrayOfSymbolHash, int paramInt, short paramShort) {
    super(paramArrayOfString, paramArrayOfSymbolHash, paramInt);
    this.fType = paramShort;
  }
  
  public int getLength() {
    if (this.fLength == -1) {
      int i = 0;
      int j;
      for (j = 0; j < this.fNSNum; j++)
        i += this.fMaps[j].getLength(); 
      j = 0;
      XSObject[] arrayOfXSObject = new XSObject[i];
      for (byte b1 = 0; b1 < this.fNSNum; b1++)
        j += this.fMaps[b1].getValues(arrayOfXSObject, j); 
      this.fLength = 0;
      this.fArray = new XSObject[i];
      for (byte b2 = 0; b2 < i; b2++) {
        XSTypeDefinition xSTypeDefinition = (XSTypeDefinition)arrayOfXSObject[b2];
        if (xSTypeDefinition.getTypeCategory() == this.fType)
          this.fArray[this.fLength++] = xSTypeDefinition; 
      } 
    } 
    return this.fLength;
  }
  
  public XSObject itemByName(String paramString1, String paramString2) {
    for (byte b = 0; b < this.fNSNum; b++) {
      if (isEqual(paramString1, this.fNamespaces[b])) {
        XSTypeDefinition xSTypeDefinition = (XSTypeDefinition)this.fMaps[b].get(paramString2);
        return (xSTypeDefinition != null && xSTypeDefinition.getTypeCategory() == this.fType) ? xSTypeDefinition : null;
      } 
    } 
    return null;
  }
  
  public XSObject item(int paramInt) {
    if (this.fArray == null)
      getLength(); 
    return (paramInt < 0 || paramInt >= this.fLength) ? null : this.fArray[paramInt];
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\x\\util\XSNamedMap4Types.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */