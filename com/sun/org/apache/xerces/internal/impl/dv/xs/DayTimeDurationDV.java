package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.datatype.Duration;

class DayTimeDurationDV extends DurationDV {
  public Object getActualValue(String paramString, ValidationContext paramValidationContext) throws InvalidDatatypeValueException {
    try {
      return parse(paramString, 2);
    } catch (Exception exception) {
      throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { paramString, "dayTimeDuration" });
    } 
  }
  
  protected Duration getDuration(AbstractDateTimeDV.DateTimeData paramDateTimeData) {
    int i = 1;
    if (paramDateTimeData.day < 0 || paramDateTimeData.hour < 0 || paramDateTimeData.minute < 0 || paramDateTimeData.second < 0.0D)
      i = -1; 
    return datatypeFactory.newDuration((i == 1), null, null, (paramDateTimeData.day != Integer.MIN_VALUE) ? BigInteger.valueOf((i * paramDateTimeData.day)) : null, (paramDateTimeData.hour != Integer.MIN_VALUE) ? BigInteger.valueOf((i * paramDateTimeData.hour)) : null, (paramDateTimeData.minute != Integer.MIN_VALUE) ? BigInteger.valueOf((i * paramDateTimeData.minute)) : null, (paramDateTimeData.second != -2.147483648E9D) ? new BigDecimal(String.valueOf(i * paramDateTimeData.second)) : null);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\DayTimeDurationDV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */