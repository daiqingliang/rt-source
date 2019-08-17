package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import javax.xml.datatype.XMLGregorianCalendar;

public class YearDV extends AbstractDateTimeDV {
  public Object getActualValue(String paramString, ValidationContext paramValidationContext) throws InvalidDatatypeValueException {
    try {
      return parse(paramString);
    } catch (Exception exception) {
      throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { paramString, "gYear" });
    } 
  }
  
  protected AbstractDateTimeDV.DateTimeData parse(String paramString) throws SchemaDateTimeException {
    AbstractDateTimeDV.DateTimeData dateTimeData = new AbstractDateTimeDV.DateTimeData(paramString, this);
    int i = paramString.length();
    int j = 0;
    if (paramString.charAt(0) == '-')
      j = 1; 
    int k = findUTCSign(paramString, j, i);
    int m = ((k == -1) ? i : k) - j;
    if (m < 4)
      throw new RuntimeException("Year must have 'CCYY' format"); 
    if (m > 4 && paramString.charAt(j) == '0')
      throw new RuntimeException("Leading zeros are required if the year value would otherwise have fewer than four digits; otherwise they are forbidden"); 
    if (k == -1) {
      dateTimeData.year = parseIntYear(paramString, i);
    } else {
      dateTimeData.year = parseIntYear(paramString, k);
      getTimeZone(paramString, dateTimeData, k, i);
    } 
    dateTimeData.month = 1;
    dateTimeData.day = 1;
    validateDateTime(dateTimeData);
    saveUnnormalized(dateTimeData);
    if (dateTimeData.utc != 0 && dateTimeData.utc != 90)
      normalize(dateTimeData); 
    dateTimeData.position = 0;
    return dateTimeData;
  }
  
  protected String dateToString(AbstractDateTimeDV.DateTimeData paramDateTimeData) {
    StringBuffer stringBuffer = new StringBuffer(5);
    append(stringBuffer, paramDateTimeData.year, 4);
    append(stringBuffer, (char)paramDateTimeData.utc, 0);
    return stringBuffer.toString();
  }
  
  protected XMLGregorianCalendar getXMLGregorianCalendar(AbstractDateTimeDV.DateTimeData paramDateTimeData) { return datatypeFactory.newXMLGregorianCalendar(paramDateTimeData.unNormYear, -2147483648, -2147483648, -2147483648, -2147483648, -2147483648, -2147483648, paramDateTimeData.hasTimeZone() ? (paramDateTimeData.timezoneHr * 60 + paramDateTimeData.timezoneMin) : Integer.MIN_VALUE); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\YearDV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */