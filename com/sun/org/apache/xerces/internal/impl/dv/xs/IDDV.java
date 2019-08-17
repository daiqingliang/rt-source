package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.util.XMLChar;

public class IDDV extends TypeValidator {
  public short getAllowedFacets() { return 2079; }
  
  public Object getActualValue(String paramString, ValidationContext paramValidationContext) throws InvalidDatatypeValueException {
    if (!XMLChar.isValidNCName(paramString))
      throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { paramString, "NCName" }); 
    return paramString;
  }
  
  public void checkExtraRules(Object paramObject, ValidationContext paramValidationContext) throws InvalidDatatypeValueException {
    String str = (String)paramObject;
    if (paramValidationContext.isIdDeclared(str))
      throw new InvalidDatatypeValueException("cvc-id.2", new Object[] { str }); 
    paramValidationContext.addId(str);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\IDDV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */