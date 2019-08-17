package javax.xml.datatype;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.namespace.QName;

public abstract class Duration {
  private static final boolean DEBUG = true;
  
  public QName getXMLSchemaType() {
    boolean bool1 = isSet(DatatypeConstants.YEARS);
    boolean bool2 = isSet(DatatypeConstants.MONTHS);
    boolean bool3 = isSet(DatatypeConstants.DAYS);
    boolean bool4 = isSet(DatatypeConstants.HOURS);
    boolean bool5 = isSet(DatatypeConstants.MINUTES);
    boolean bool6 = isSet(DatatypeConstants.SECONDS);
    if (bool1 && bool2 && bool3 && bool4 && bool5 && bool6)
      return DatatypeConstants.DURATION; 
    if (!bool1 && !bool2 && bool3 && bool4 && bool5 && bool6)
      return DatatypeConstants.DURATION_DAYTIME; 
    if (bool1 && bool2 && !bool3 && !bool4 && !bool5 && !bool6)
      return DatatypeConstants.DURATION_YEARMONTH; 
    throw new IllegalStateException("javax.xml.datatype.Duration#getXMLSchemaType(): this Duration does not match one of the XML Schema date/time datatypes: year set = " + bool1 + " month set = " + bool2 + " day set = " + bool3 + " hour set = " + bool4 + " minute set = " + bool5 + " second set = " + bool6);
  }
  
  public abstract int getSign();
  
  public int getYears() { return getField(DatatypeConstants.YEARS).intValue(); }
  
  public int getMonths() { return getField(DatatypeConstants.MONTHS).intValue(); }
  
  public int getDays() { return getField(DatatypeConstants.DAYS).intValue(); }
  
  public int getHours() { return getField(DatatypeConstants.HOURS).intValue(); }
  
  public int getMinutes() { return getField(DatatypeConstants.MINUTES).intValue(); }
  
  public int getSeconds() { return getField(DatatypeConstants.SECONDS).intValue(); }
  
  public long getTimeInMillis(Calendar paramCalendar) {
    Calendar calendar = (Calendar)paramCalendar.clone();
    addTo(calendar);
    return getCalendarTimeInMillis(calendar) - getCalendarTimeInMillis(paramCalendar);
  }
  
  public long getTimeInMillis(Date paramDate) {
    GregorianCalendar gregorianCalendar = new GregorianCalendar();
    gregorianCalendar.setTime(paramDate);
    addTo(gregorianCalendar);
    return getCalendarTimeInMillis(gregorianCalendar) - paramDate.getTime();
  }
  
  public abstract Number getField(DatatypeConstants.Field paramField);
  
  public abstract boolean isSet(DatatypeConstants.Field paramField);
  
  public abstract Duration add(Duration paramDuration);
  
  public abstract void addTo(Calendar paramCalendar);
  
  public void addTo(Date paramDate) {
    if (paramDate == null)
      throw new NullPointerException("Cannot call " + getClass().getName() + "#addTo(Date date) with date == null."); 
    GregorianCalendar gregorianCalendar = new GregorianCalendar();
    gregorianCalendar.setTime(paramDate);
    addTo(gregorianCalendar);
    paramDate.setTime(getCalendarTimeInMillis(gregorianCalendar));
  }
  
  public Duration subtract(Duration paramDuration) { return add(paramDuration.negate()); }
  
  public Duration multiply(int paramInt) { return multiply(new BigDecimal(String.valueOf(paramInt))); }
  
  public abstract Duration multiply(BigDecimal paramBigDecimal);
  
  public abstract Duration negate();
  
  public abstract Duration normalizeWith(Calendar paramCalendar);
  
  public abstract int compare(Duration paramDuration);
  
  public boolean isLongerThan(Duration paramDuration) { return (compare(paramDuration) == 1); }
  
  public boolean isShorterThan(Duration paramDuration) { return (compare(paramDuration) == -1); }
  
  public boolean equals(Object paramObject) { return (paramObject == null || !(paramObject instanceof Duration)) ? false : ((compare((Duration)paramObject) == 0)); }
  
  public abstract int hashCode();
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (getSign() < 0)
      stringBuffer.append('-'); 
    stringBuffer.append('P');
    BigInteger bigInteger1 = (BigInteger)getField(DatatypeConstants.YEARS);
    if (bigInteger1 != null)
      stringBuffer.append(bigInteger1 + "Y"); 
    BigInteger bigInteger2 = (BigInteger)getField(DatatypeConstants.MONTHS);
    if (bigInteger2 != null)
      stringBuffer.append(bigInteger2 + "M"); 
    BigInteger bigInteger3 = (BigInteger)getField(DatatypeConstants.DAYS);
    if (bigInteger3 != null)
      stringBuffer.append(bigInteger3 + "D"); 
    BigInteger bigInteger4 = (BigInteger)getField(DatatypeConstants.HOURS);
    BigInteger bigInteger5 = (BigInteger)getField(DatatypeConstants.MINUTES);
    BigDecimal bigDecimal = (BigDecimal)getField(DatatypeConstants.SECONDS);
    if (bigInteger4 != null || bigInteger5 != null || bigDecimal != null) {
      stringBuffer.append('T');
      if (bigInteger4 != null)
        stringBuffer.append(bigInteger4 + "H"); 
      if (bigInteger5 != null)
        stringBuffer.append(bigInteger5 + "M"); 
      if (bigDecimal != null)
        stringBuffer.append(toString(bigDecimal) + "S"); 
    } 
    return stringBuffer.toString();
  }
  
  private String toString(BigDecimal paramBigDecimal) {
    StringBuffer stringBuffer;
    String str = paramBigDecimal.unscaledValue().toString();
    int i = paramBigDecimal.scale();
    if (i == 0)
      return str; 
    int j = str.length() - i;
    if (j == 0)
      return "0." + str; 
    if (j > 0) {
      stringBuffer = new StringBuffer(str);
      stringBuffer.insert(j, '.');
    } else {
      stringBuffer = new StringBuffer(3 - j + str.length());
      stringBuffer.append("0.");
      for (byte b = 0; b < -j; b++)
        stringBuffer.append('0'); 
      stringBuffer.append(str);
    } 
    return stringBuffer.toString();
  }
  
  private static long getCalendarTimeInMillis(Calendar paramCalendar) { return paramCalendar.getTime().getTime(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\datatype\Duration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */