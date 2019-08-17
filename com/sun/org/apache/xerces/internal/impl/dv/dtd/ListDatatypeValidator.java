package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import java.util.StringTokenizer;

public class ListDatatypeValidator implements DatatypeValidator {
  DatatypeValidator fItemValidator;
  
  public ListDatatypeValidator(DatatypeValidator paramDatatypeValidator) { this.fItemValidator = paramDatatypeValidator; }
  
  public void validate(String paramString, ValidationContext paramValidationContext) throws InvalidDatatypeValueException {
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, " ");
    int i = stringTokenizer.countTokens();
    if (i == 0)
      throw new InvalidDatatypeValueException("EmptyList", null); 
    while (stringTokenizer.hasMoreTokens())
      this.fItemValidator.validate(stringTokenizer.nextToken(), paramValidationContext); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\dtd\ListDatatypeValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */