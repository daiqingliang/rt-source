package javax.xml.datatype;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class DatatypeFactory {
  public static final String DATATYPEFACTORY_PROPERTY = "javax.xml.datatype.DatatypeFactory";
  
  public static final String DATATYPEFACTORY_IMPLEMENTATION_CLASS = new String("com.sun.org.apache.xerces.internal.jaxp.datatype.DatatypeFactoryImpl");
  
  private static final Pattern XDTSCHEMA_YMD;
  
  private static final Pattern XDTSCHEMA_DTD = (XDTSCHEMA_YMD = Pattern.compile("[^DT]*")).compile("[^YM]*[DT].*");
  
  public static DatatypeFactory newInstance() throws DatatypeConfigurationException { return (DatatypeFactory)FactoryFinder.find(DatatypeFactory.class, DATATYPEFACTORY_IMPLEMENTATION_CLASS); }
  
  public static DatatypeFactory newInstance(String paramString, ClassLoader paramClassLoader) throws DatatypeConfigurationException { return (DatatypeFactory)FactoryFinder.newInstance(DatatypeFactory.class, paramString, paramClassLoader, false); }
  
  public abstract Duration newDuration(String paramString);
  
  public abstract Duration newDuration(long paramLong);
  
  public abstract Duration newDuration(boolean paramBoolean, BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4, BigInteger paramBigInteger5, BigDecimal paramBigDecimal);
  
  public Duration newDuration(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    BigInteger bigInteger1;
    BigInteger bigInteger2;
    BigInteger bigInteger3;
    BigInteger bigInteger4;
    BigInteger bigInteger5 = (paramInt5 != Integer.MIN_VALUE) ? (bigInteger4 = (paramInt4 != Integer.MIN_VALUE) ? (bigInteger3 = (paramInt3 != Integer.MIN_VALUE) ? (bigInteger2 = (paramInt2 != Integer.MIN_VALUE) ? (bigInteger1 = (paramInt1 != Integer.MIN_VALUE) ? BigInteger.valueOf(paramInt1) : null).valueOf(paramInt2) : null).valueOf(paramInt3) : null).valueOf(paramInt4) : null).valueOf(paramInt5) : null;
    BigDecimal bigDecimal = (paramInt6 != Integer.MIN_VALUE) ? BigDecimal.valueOf(paramInt6) : null;
    return newDuration(paramBoolean, bigInteger1, bigInteger2, bigInteger3, bigInteger4, bigInteger5, bigDecimal);
  }
  
  public Duration newDurationDayTime(String paramString) {
    if (paramString == null)
      throw new NullPointerException("Trying to create an xdt:dayTimeDuration with an invalid lexical representation of \"null\""); 
    Matcher matcher = XDTSCHEMA_DTD.matcher(paramString);
    if (!matcher.matches())
      throw new IllegalArgumentException("Trying to create an xdt:dayTimeDuration with an invalid lexical representation of \"" + paramString + "\", data model requires years and months only."); 
    return newDuration(paramString);
  }
  
  public Duration newDurationDayTime(long paramLong) { return newDuration(paramLong); }
  
  public Duration newDurationDayTime(boolean paramBoolean, BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4) { return newDuration(paramBoolean, null, null, paramBigInteger1, paramBigInteger2, paramBigInteger3, (paramBigInteger4 != null) ? new BigDecimal(paramBigInteger4) : null); }
  
  public Duration newDurationDayTime(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return newDurationDayTime(paramBoolean, BigInteger.valueOf(paramInt1), BigInteger.valueOf(paramInt2), BigInteger.valueOf(paramInt3), BigInteger.valueOf(paramInt4)); }
  
  public Duration newDurationYearMonth(String paramString) {
    if (paramString == null)
      throw new NullPointerException("Trying to create an xdt:yearMonthDuration with an invalid lexical representation of \"null\""); 
    Matcher matcher = XDTSCHEMA_YMD.matcher(paramString);
    if (!matcher.matches())
      throw new IllegalArgumentException("Trying to create an xdt:yearMonthDuration with an invalid lexical representation of \"" + paramString + "\", data model requires days and times only."); 
    return newDuration(paramString);
  }
  
  public Duration newDurationYearMonth(long paramLong) {
    Duration duration = newDuration(paramLong);
    boolean bool = !(duration.getSign() == -1);
    BigInteger bigInteger1 = (BigInteger)duration.getField(DatatypeConstants.YEARS);
    if (bigInteger1 == null)
      bigInteger1 = BigInteger.ZERO; 
    BigInteger bigInteger2 = (BigInteger)duration.getField(DatatypeConstants.MONTHS);
    if (bigInteger2 == null)
      bigInteger2 = BigInteger.ZERO; 
    return newDurationYearMonth(bool, bigInteger1, bigInteger2);
  }
  
  public Duration newDurationYearMonth(boolean paramBoolean, BigInteger paramBigInteger1, BigInteger paramBigInteger2) { return newDuration(paramBoolean, paramBigInteger1, paramBigInteger2, null, null, null, null); }
  
  public Duration newDurationYearMonth(boolean paramBoolean, int paramInt1, int paramInt2) { return newDurationYearMonth(paramBoolean, BigInteger.valueOf(paramInt1), BigInteger.valueOf(paramInt2)); }
  
  public abstract XMLGregorianCalendar newXMLGregorianCalendar();
  
  public abstract XMLGregorianCalendar newXMLGregorianCalendar(String paramString);
  
  public abstract XMLGregorianCalendar newXMLGregorianCalendar(GregorianCalendar paramGregorianCalendar);
  
  public abstract XMLGregorianCalendar newXMLGregorianCalendar(BigInteger paramBigInteger, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, BigDecimal paramBigDecimal, int paramInt6);
  
  public XMLGregorianCalendar newXMLGregorianCalendar(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8) {
    BigInteger bigInteger = (paramInt1 != Integer.MIN_VALUE) ? BigInteger.valueOf(paramInt1) : null;
    BigDecimal bigDecimal = null;
    if (paramInt7 != Integer.MIN_VALUE) {
      if (paramInt7 < 0 || paramInt7 > 1000)
        throw new IllegalArgumentException("javax.xml.datatype.DatatypeFactory#newXMLGregorianCalendar(int year, int month, int day, int hour, int minute, int second, int millisecond, int timezone)with invalid millisecond: " + paramInt7); 
      bigDecimal = BigDecimal.valueOf(paramInt7).movePointLeft(3);
    } 
    return newXMLGregorianCalendar(bigInteger, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, bigDecimal, paramInt8);
  }
  
  public XMLGregorianCalendar newXMLGregorianCalendarDate(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return newXMLGregorianCalendar(paramInt1, paramInt2, paramInt3, -2147483648, -2147483648, -2147483648, -2147483648, paramInt4); }
  
  public XMLGregorianCalendar newXMLGregorianCalendarTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return newXMLGregorianCalendar(-2147483648, -2147483648, -2147483648, paramInt1, paramInt2, paramInt3, -2147483648, paramInt4); }
  
  public XMLGregorianCalendar newXMLGregorianCalendarTime(int paramInt1, int paramInt2, int paramInt3, BigDecimal paramBigDecimal, int paramInt4) { return newXMLGregorianCalendar(null, -2147483648, -2147483648, paramInt1, paramInt2, paramInt3, paramBigDecimal, paramInt4); }
  
  public XMLGregorianCalendar newXMLGregorianCalendarTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    BigDecimal bigDecimal = null;
    if (paramInt4 != Integer.MIN_VALUE) {
      if (paramInt4 < 0 || paramInt4 > 1000)
        throw new IllegalArgumentException("javax.xml.datatype.DatatypeFactory#newXMLGregorianCalendarTime(int hours, int minutes, int seconds, int milliseconds, int timezone)with invalid milliseconds: " + paramInt4); 
      bigDecimal = BigDecimal.valueOf(paramInt4).movePointLeft(3);
    } 
    return newXMLGregorianCalendarTime(paramInt1, paramInt2, paramInt3, bigDecimal, paramInt5);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\datatype\DatatypeFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */