package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

public abstract class TypeValidator {
  public static final short LESS_THAN = -1;
  
  public static final short EQUAL = 0;
  
  public static final short GREATER_THAN = 1;
  
  public static final short INDETERMINATE = 2;
  
  public abstract short getAllowedFacets();
  
  public abstract Object getActualValue(String paramString, ValidationContext paramValidationContext) throws InvalidDatatypeValueException;
  
  public void checkExtraRules(Object paramObject, ValidationContext paramValidationContext) throws InvalidDatatypeValueException {}
  
  public boolean isIdentical(Object paramObject1, Object paramObject2) { return paramObject1.equals(paramObject2); }
  
  public int compare(Object paramObject1, Object paramObject2) { return -1; }
  
  public int getDataLength(Object paramObject) { return (paramObject instanceof String) ? ((String)paramObject).length() : -1; }
  
  public int getTotalDigits(Object paramObject) { return -1; }
  
  public int getFractionDigits(Object paramObject) { return -1; }
  
  public static final boolean isDigit(char paramChar) { return (paramChar >= '0' && paramChar <= '9'); }
  
  public static final int getDigit(char paramChar) { return isDigit(paramChar) ? (paramChar - '0') : -1; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\TypeValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */