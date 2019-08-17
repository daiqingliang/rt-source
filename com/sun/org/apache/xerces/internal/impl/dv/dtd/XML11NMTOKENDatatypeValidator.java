package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.util.XML11Char;

public class XML11NMTOKENDatatypeValidator extends NMTOKENDatatypeValidator {
  public void validate(String paramString, ValidationContext paramValidationContext) throws InvalidDatatypeValueException {
    if (!XML11Char.isXML11ValidNmtoken(paramString))
      throw new InvalidDatatypeValueException("NMTOKENInvalid", new Object[] { paramString }); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\dtd\XML11NMTOKENDatatypeValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */