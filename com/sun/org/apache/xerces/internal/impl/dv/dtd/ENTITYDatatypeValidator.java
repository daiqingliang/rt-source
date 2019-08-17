package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

public class ENTITYDatatypeValidator implements DatatypeValidator {
  public void validate(String paramString, ValidationContext paramValidationContext) throws InvalidDatatypeValueException {
    if (!paramValidationContext.isEntityUnparsed(paramString))
      throw new InvalidDatatypeValueException("ENTITYNotUnparsed", new Object[] { paramString }); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\dtd\ENTITYDatatypeValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */