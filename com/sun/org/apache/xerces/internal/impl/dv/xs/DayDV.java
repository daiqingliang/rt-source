package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import javax.xml.datatype.XMLGregorianCalendar;

public class DayDV extends AbstractDateTimeDV {
  private static final int DAY_SIZE = 5;
  
  public Object getActualValue(String paramString, ValidationContext paramValidationContext) throws InvalidDatatypeValueException {
    try {
      return parse(paramString);
    } catch (Exception exception) {
      throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { paramString, "gDay" });
    } 
  }
  
  protected AbstractDateTimeDV.DateTimeData parse(String paramString) throws SchemaDateTimeException {
    AbstractDateTimeDV.DateTimeData dateTimeData = new AbstractDateTimeDV.DateTimeData(paramString, this);
    int i = paramString.length();
    if (paramString.charAt(0) != '-' || paramString.charAt(1) != '-' || paramString.charAt(2) != '-')
      throw new SchemaDateTimeException("Error in day parsing"); 
    dateTimeData.year = 2000;
    dateTimeData.month = 1;
    dateTimeData.day = parseInt(paramString, 3, 5);
    if (5 < i) {
      if (!isNextCharUTCSign(paramString, 5, i))
        throw new SchemaDateTimeException("Error in day parsing"); 
      getTimeZone(paramString, dateTimeData, 5, i);
    } 
    validateDateTime(dateTimeData);
    saveUnnormalized(dateTimeData);
    if (dateTimeData.utc != 0 && dateTimeData.utc != 90)
      normalize(dateTimeData); 
    dateTimeData.position = 2;
    return dateTimeData;
  }
  
  protected String dateToString(AbstractDateTimeDV.DateTimeData paramDateTimeData) {
    StringBuffer stringBuffer = new StringBuffer(6);
    stringBuffer.append('-');
    stringBuffer.append('-');
    stringBuffer.append('-');
    append(stringBuffer, paramDateTimeData.day, 2);
    append(stringBuffer, (char)paramDateTimeData.utc, 0);
    return stringBuffer.toString();
  }
  
  protected XMLGregorianCalendar getXMLGregorianCalendar(AbstractDateTimeDV.DateTimeData paramDateTimeData) { return datatypeFactory.newXMLGregorianCalendar(-2147483648, -2147483648, paramDateTimeData.unNormDay, -2147483648, -2147483648, -2147483648, -2147483648, paramDateTimeData.hasTimeZone() ? (paramDateTimeData.timezoneHr * 60 + paramDateTimeData.timezoneMin) : Integer.MIN_VALUE); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\DayDV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */