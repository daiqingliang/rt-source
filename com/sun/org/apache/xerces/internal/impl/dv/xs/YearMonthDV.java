package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import javax.xml.datatype.XMLGregorianCalendar;

public class YearMonthDV extends AbstractDateTimeDV {
  public Object getActualValue(String paramString, ValidationContext paramValidationContext) throws InvalidDatatypeValueException {
    try {
      return parse(paramString);
    } catch (Exception exception) {
      throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { paramString, "gYearMonth" });
    } 
  }
  
  protected AbstractDateTimeDV.DateTimeData parse(String paramString) throws SchemaDateTimeException {
    AbstractDateTimeDV.DateTimeData dateTimeData = new AbstractDateTimeDV.DateTimeData(paramString, this);
    int i = paramString.length();
    int j = getYearMonth(paramString, 0, i, dateTimeData);
    dateTimeData.day = 1;
    parseTimeZone(paramString, j, i, dateTimeData);
    validateDateTime(dateTimeData);
    saveUnnormalized(dateTimeData);
    if (dateTimeData.utc != 0 && dateTimeData.utc != 90)
      normalize(dateTimeData); 
    dateTimeData.position = 0;
    return dateTimeData;
  }
  
  protected String dateToString(AbstractDateTimeDV.DateTimeData paramDateTimeData) {
    StringBuffer stringBuffer = new StringBuffer(25);
    append(stringBuffer, paramDateTimeData.year, 4);
    stringBuffer.append('-');
    append(stringBuffer, paramDateTimeData.month, 2);
    append(stringBuffer, (char)paramDateTimeData.utc, 0);
    return stringBuffer.toString();
  }
  
  protected XMLGregorianCalendar getXMLGregorianCalendar(AbstractDateTimeDV.DateTimeData paramDateTimeData) { return datatypeFactory.newXMLGregorianCalendar(paramDateTimeData.unNormYear, paramDateTimeData.unNormMonth, -2147483648, -2147483648, -2147483648, -2147483648, -2147483648, paramDateTimeData.hasTimeZone() ? (paramDateTimeData.timezoneHr * 60 + paramDateTimeData.timezoneMin) : Integer.MIN_VALUE); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\YearMonthDV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */