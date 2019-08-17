package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import com.sun.org.apache.xerces.internal.impl.dv.util.ByteListImpl;

public class Base64BinaryDV extends TypeValidator {
  public short getAllowedFacets() { return 2079; }
  
  public Object getActualValue(String paramString, ValidationContext paramValidationContext) throws InvalidDatatypeValueException {
    byte[] arrayOfByte = Base64.decode(paramString);
    if (arrayOfByte == null)
      throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { paramString, "base64Binary" }); 
    return new XBase64(arrayOfByte);
  }
  
  public int getDataLength(Object paramObject) { return ((XBase64)paramObject).getLength(); }
  
  private static final class XBase64 extends ByteListImpl {
    public XBase64(byte[] param1ArrayOfByte) { super(param1ArrayOfByte); }
    
    public String toString() {
      if (this.canonical == null)
        this.canonical = Base64.encode(this.data); 
      return this.canonical;
    }
    
    public boolean equals(Object param1Object) {
      if (!(param1Object instanceof XBase64))
        return false; 
      byte[] arrayOfByte = ((XBase64)param1Object).data;
      int i = this.data.length;
      if (i != arrayOfByte.length)
        return false; 
      for (byte b = 0; b < i; b++) {
        if (this.data[b] != arrayOfByte[b])
          return false; 
      } 
      return true;
    }
    
    public int hashCode() {
      byte b = 0;
      for (byte b1 = 0; b1 < this.data.length; b1++)
        b = b * 37 + (this.data[b1] & 0xFF); 
      return b;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\Base64BinaryDV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */