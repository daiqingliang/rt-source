package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import java.math.BigInteger;
import javax.xml.datatype.Duration;

class YearMonthDurationDV extends DurationDV {
  public Object getActualValue(String paramString, ValidationContext paramValidationContext) throws InvalidDatatypeValueException {
    try {
      return parse(paramString, 1);
    } catch (Exception exception) {
      throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { paramString, "yearMonthDuration" });
    } 
  }
  
  protected Duration getDuration(AbstractDateTimeDV.DateTimeData paramDateTimeData) {
    int i = 1;
    if (paramDateTimeData.year < 0 || paramDateTimeData.month < 0)
      i = -1; 
    return datatypeFactory.newDuration((i == 1), (paramDateTimeData.year != Integer.MIN_VALUE) ? BigInteger.valueOf((i * paramDateTimeData.year)) : null, (paramDateTimeData.month != Integer.MIN_VALUE) ? BigInteger.valueOf((i * paramDateTimeData.month)) : null, null, null, null, null);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\YearMonthDurationDV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */