package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.util.XMLChar;

public class IDREFDV extends TypeValidator {
  public short getAllowedFacets() { return 2079; }
  
  public Object getActualValue(String paramString, ValidationContext paramValidationContext) throws InvalidDatatypeValueException {
    if (!XMLChar.isValidNCName(paramString))
      throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { paramString, "NCName" }); 
    return paramString;
  }
  
  public void checkExtraRules(Object paramObject, ValidationContext paramValidationContext) throws InvalidDatatypeValueException { paramValidationContext.addIdRef((String)paramObject); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\IDREFDV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */