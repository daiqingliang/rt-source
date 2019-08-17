package com.sun.org.apache.xerces.internal.jaxp.datatype;

import com.sun.org.apache.xerces.internal.util.DatatypeMessageFormatter;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

class DurationImpl extends Duration implements Serializable {
  private static final int FIELD_NUM = 6;
  
  private static final DatatypeConstants.Field[] FIELDS = { DatatypeConstants.YEARS, DatatypeConstants.MONTHS, DatatypeConstants.DAYS, DatatypeConstants.HOURS, DatatypeConstants.MINUTES, DatatypeConstants.SECONDS };
  
  private static final int[] FIELD_IDS = { DatatypeConstants.YEARS.getId(), DatatypeConstants.MONTHS.getId(), DatatypeConstants.DAYS.getId(), DatatypeConstants.HOURS.getId(), DatatypeConstants.MINUTES.getId(), DatatypeConstants.SECONDS.getId() };
  
  private static final TimeZone GMT = TimeZone.getTimeZone("GMT");
  
  private static final BigDecimal ZERO = BigDecimal.valueOf(0L);
  
  protected int signum;
  
  protected BigInteger years;
  
  protected BigInteger months;
  
  protected BigInteger days;
  
  protected BigInteger hours;
  
  protected BigInteger minutes;
  
  protected BigDecimal seconds;
  
  private static final XMLGregorianCalendar[] TEST_POINTS = { XMLGregorianCalendarImpl.parse("1696-09-01T00:00:00Z"), XMLGregorianCalendarImpl.parse("1697-02-01T00:00:00Z"), XMLGregorianCalendarImpl.parse("1903-03-01T00:00:00Z"), XMLGregorianCalendarImpl.parse("1903-07-01T00:00:00Z") };
  
  private static final BigDecimal[] FACTORS = { BigDecimal.valueOf(12L), (new BigDecimal[5][3] = (new BigDecimal[5][2] = (new BigDecimal[5][1] = null).valueOf(24L)).valueOf(60L)).valueOf(60L) };
  
  private static final long serialVersionUID = 1L;
  
  public int getSign() { return this.signum; }
  
  protected int calcSignum(boolean paramBoolean) { return ((this.years == null || this.years.signum() == 0) && (this.months == null || this.months.signum() == 0) && (this.days == null || this.days.signum() == 0) && (this.hours == null || this.hours.signum() == 0) && (this.minutes == null || this.minutes.signum() == 0) && (this.seconds == null || this.seconds.signum() == 0)) ? 0 : (paramBoolean ? 1 : -1); }
  
  protected DurationImpl(boolean paramBoolean, BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4, BigInteger paramBigInteger5, BigDecimal paramBigDecimal) {
    this.years = paramBigInteger1;
    this.months = paramBigInteger2;
    this.days = paramBigInteger3;
    this.hours = paramBigInteger4;
    this.minutes = paramBigInteger5;
    this.seconds = paramBigDecimal;
    this.signum = calcSignum(paramBoolean);
    if (paramBigInteger1 == null && paramBigInteger2 == null && paramBigInteger3 == null && paramBigInteger4 == null && paramBigInteger5 == null && paramBigDecimal == null)
      throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "AllFieldsNull", null)); 
    testNonNegative(paramBigInteger1, DatatypeConstants.YEARS);
    testNonNegative(paramBigInteger2, DatatypeConstants.MONTHS);
    testNonNegative(paramBigInteger3, DatatypeConstants.DAYS);
    testNonNegative(paramBigInteger4, DatatypeConstants.HOURS);
    testNonNegative(paramBigInteger5, DatatypeConstants.MINUTES);
    testNonNegative(paramBigDecimal, DatatypeConstants.SECONDS);
  }
  
  protected static void testNonNegative(BigInteger paramBigInteger, DatatypeConstants.Field paramField) {
    if (paramBigInteger != null && paramBigInteger.signum() < 0)
      throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "NegativeField", new Object[] { paramField.toString() })); 
  }
  
  protected static void testNonNegative(BigDecimal paramBigDecimal, DatatypeConstants.Field paramField) {
    if (paramBigDecimal != null && paramBigDecimal.signum() < 0)
      throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "NegativeField", new Object[] { paramField.toString() })); 
  }
  
  protected DurationImpl(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) { this(paramBoolean, wrap(paramInt1), wrap(paramInt2), wrap(paramInt3), wrap(paramInt4), wrap(paramInt5), (paramInt6 != Integer.MIN_VALUE) ? new BigDecimal(String.valueOf(paramInt6)) : null); }
  
  protected static BigInteger wrap(int paramInt) { return (paramInt == Integer.MIN_VALUE) ? null : new BigInteger(String.valueOf(paramInt)); }
  
  protected DurationImpl(long paramLong) {
    long l1 = paramLong;
    if (l1 > 0L) {
      this.signum = 1;
    } else if (l1 < 0L) {
      this.signum = -1;
      if (l1 == Float.MIN_VALUE)
        l1++; 
      l1 *= -1L;
    } else {
      this.signum = 0;
    } 
    GregorianCalendar gregorianCalendar = new GregorianCalendar(GMT);
    gregorianCalendar.setTimeInMillis(l1);
    long l2 = 0L;
    l2 = (gregorianCalendar.get(1) - 1970);
    this.years = BigInteger.valueOf(l2);
    l2 = gregorianCalendar.get(2);
    this.months = BigInteger.valueOf(l2);
    l2 = (gregorianCalendar.get(5) - 1);
    this.days = BigInteger.valueOf(l2);
    l2 = gregorianCalendar.get(11);
    this.hours = BigInteger.valueOf(l2);
    l2 = gregorianCalendar.get(12);
    this.minutes = BigInteger.valueOf(l2);
    l2 = (gregorianCalendar.get(13) * 1000 + gregorianCalendar.get(14));
    this.seconds = BigDecimal.valueOf(l2, 3);
  }
  
  protected DurationImpl(String paramString) throws IllegalArgumentException {
    String str = paramString;
    int[] arrayOfInt1 = new int[1];
    int i = str.length();
    boolean bool1 = false;
    if (paramString == null)
      throw new NullPointerException(); 
    arrayOfInt1[0] = 0;
    if (i != arrayOfInt1[0] && str.charAt(arrayOfInt1[0]) == '-') {
      arrayOfInt1[0] = arrayOfInt1[0] + 1;
      bool = false;
    } else {
      bool = true;
    } 
    arrayOfInt1[0] = arrayOfInt1[0] + 1;
    if (i != arrayOfInt1[0] && str.charAt(arrayOfInt1[0]) != 'P')
      throw new IllegalArgumentException(str); 
    byte b1 = 0;
    String[] arrayOfString1 = new String[3];
    int[] arrayOfInt2 = new int[3];
    while (i != arrayOfInt1[0] && isDigit(str.charAt(arrayOfInt1[0])) && b1 < 3) {
      arrayOfInt2[b1] = arrayOfInt1[0];
      arrayOfString1[b1++] = parsePiece(str, arrayOfInt1);
    } 
    if (i != arrayOfInt1[0]) {
      arrayOfInt1[0] = arrayOfInt1[0] + 1;
      if (str.charAt(arrayOfInt1[0]) == 'T') {
        bool1 = true;
      } else {
        throw new IllegalArgumentException(str);
      } 
    } 
    byte b2 = 0;
    String[] arrayOfString2 = new String[3];
    int[] arrayOfInt3 = new int[3];
    while (i != arrayOfInt1[0] && isDigitOrPeriod(str.charAt(arrayOfInt1[0])) && b2 < 3) {
      arrayOfInt3[b2] = arrayOfInt1[0];
      arrayOfString2[b2++] = parsePiece(str, arrayOfInt1);
    } 
    if (bool1 && b2 == 0)
      throw new IllegalArgumentException(str); 
    if (i != arrayOfInt1[0])
      throw new IllegalArgumentException(str); 
    if (b1 == 0 && b2 == 0)
      throw new IllegalArgumentException(str); 
    organizeParts(str, arrayOfString1, arrayOfInt2, b1, "YMD");
    organizeParts(str, arrayOfString2, arrayOfInt3, b2, "HMS");
    this.years = parseBigInteger(str, arrayOfString1[0], arrayOfInt2[0]);
    this.months = parseBigInteger(str, arrayOfString1[1], arrayOfInt2[1]);
    this.days = parseBigInteger(str, arrayOfString1[2], arrayOfInt2[2]);
    this.hours = parseBigInteger(str, arrayOfString2[0], arrayOfInt3[0]);
    this.minutes = parseBigInteger(str, arrayOfString2[1], arrayOfInt3[1]);
    this.seconds = parseBigDecimal(str, arrayOfString2[2], arrayOfInt3[2]);
    this.signum = calcSignum(bool);
  }
  
  private static boolean isDigit(char paramChar) { return ('0' <= paramChar && paramChar <= '9'); }
  
  private static boolean isDigitOrPeriod(char paramChar) { return (isDigit(paramChar) || paramChar == '.'); }
  
  private static String parsePiece(String paramString, int[] paramArrayOfInt) throws IllegalArgumentException {
    int i = paramArrayOfInt[0];
    while (paramArrayOfInt[0] < paramString.length() && isDigitOrPeriod(paramString.charAt(paramArrayOfInt[0])))
      paramArrayOfInt[0] = paramArrayOfInt[0] + 1; 
    if (paramArrayOfInt[0] == paramString.length())
      throw new IllegalArgumentException(paramString); 
    paramArrayOfInt[0] = paramArrayOfInt[0] + 1;
    return paramString.substring(i, paramArrayOfInt[0]);
  }
  
  private static void organizeParts(String paramString1, String[] paramArrayOfString, int[] paramArrayOfInt, int paramInt, String paramString2) throws IllegalArgumentException {
    int i = paramString2.length();
    for (int j = paramInt - 1; j >= 0; j--) {
      int k = paramString2.lastIndexOf(paramArrayOfString[j].charAt(paramArrayOfString[j].length() - 1), i - 1);
      if (k == -1)
        throw new IllegalArgumentException(paramString1); 
      for (int m = k + 1; m < i; m++)
        paramArrayOfString[m] = null; 
      i = k;
      paramArrayOfString[i] = paramArrayOfString[j];
      paramArrayOfInt[i] = paramArrayOfInt[j];
    } 
    while (--i >= 0) {
      paramArrayOfString[i] = null;
      i--;
    } 
  }
  
  private static BigInteger parseBigInteger(String paramString1, String paramString2, int paramInt) throws IllegalArgumentException {
    if (paramString2 == null)
      return null; 
    paramString2 = paramString2.substring(0, paramString2.length() - 1);
    return new BigInteger(paramString2);
  }
  
  private static BigDecimal parseBigDecimal(String paramString1, String paramString2, int paramInt) throws IllegalArgumentException {
    if (paramString2 == null)
      return null; 
    paramString2 = paramString2.substring(0, paramString2.length() - 1);
    return new BigDecimal(paramString2);
  }
  
  public int compare(Duration paramDuration) {
    BigInteger bigInteger1;
    BigInteger bigInteger2 = (bigInteger1 = BigInteger.valueOf(2147483647L)).valueOf(-2147483648L);
    if (this.years != null && this.years.compareTo(bigInteger1) == 1)
      throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.YEARS.toString(), this.years.toString() })); 
    if (this.months != null && this.months.compareTo(bigInteger1) == 1)
      throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.MONTHS.toString(), this.months.toString() })); 
    if (this.days != null && this.days.compareTo(bigInteger1) == 1)
      throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.DAYS.toString(), this.days.toString() })); 
    if (this.hours != null && this.hours.compareTo(bigInteger1) == 1)
      throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.HOURS.toString(), this.hours.toString() })); 
    if (this.minutes != null && this.minutes.compareTo(bigInteger1) == 1)
      throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.MINUTES.toString(), this.minutes.toString() })); 
    if (this.seconds != null && this.seconds.toBigInteger().compareTo(bigInteger1) == 1)
      throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.SECONDS.toString(), this.seconds.toString() })); 
    BigInteger bigInteger3 = (BigInteger)paramDuration.getField(DatatypeConstants.YEARS);
    if (bigInteger3 != null && bigInteger3.compareTo(bigInteger1) == 1)
      throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.YEARS.toString(), bigInteger3.toString() })); 
    BigInteger bigInteger4 = (BigInteger)paramDuration.getField(DatatypeConstants.MONTHS);
    if (bigInteger4 != null && bigInteger4.compareTo(bigInteger1) == 1)
      throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.MONTHS.toString(), bigInteger4.toString() })); 
    BigInteger bigInteger5 = (BigInteger)paramDuration.getField(DatatypeConstants.DAYS);
    if (bigInteger5 != null && bigInteger5.compareTo(bigInteger1) == 1)
      throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.DAYS.toString(), bigInteger5.toString() })); 
    BigInteger bigInteger6 = (BigInteger)paramDuration.getField(DatatypeConstants.HOURS);
    if (bigInteger6 != null && bigInteger6.compareTo(bigInteger1) == 1)
      throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.HOURS.toString(), bigInteger6.toString() })); 
    BigInteger bigInteger7 = (BigInteger)paramDuration.getField(DatatypeConstants.MINUTES);
    if (bigInteger7 != null && bigInteger7.compareTo(bigInteger1) == 1)
      throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.MINUTES.toString(), bigInteger7.toString() })); 
    BigDecimal bigDecimal = (BigDecimal)paramDuration.getField(DatatypeConstants.SECONDS);
    BigInteger bigInteger8 = null;
    if (bigDecimal != null)
      bigInteger8 = bigDecimal.toBigInteger(); 
    if (bigInteger8 != null && bigInteger8.compareTo(bigInteger1) == 1)
      throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.SECONDS.toString(), bigInteger8.toString() })); 
    GregorianCalendar gregorianCalendar1 = new GregorianCalendar(1970, 1, 1, 0, 0, 0);
    gregorianCalendar1.add(1, getYears() * getSign());
    gregorianCalendar1.add(2, getMonths() * getSign());
    gregorianCalendar1.add(6, getDays() * getSign());
    gregorianCalendar1.add(11, getHours() * getSign());
    gregorianCalendar1.add(12, getMinutes() * getSign());
    gregorianCalendar1.add(13, getSeconds() * getSign());
    GregorianCalendar gregorianCalendar2 = new GregorianCalendar(1970, 1, 1, 0, 0, 0);
    gregorianCalendar2.add(1, paramDuration.getYears() * paramDuration.getSign());
    gregorianCalendar2.add(2, paramDuration.getMonths() * paramDuration.getSign());
    gregorianCalendar2.add(6, paramDuration.getDays() * paramDuration.getSign());
    gregorianCalendar2.add(11, paramDuration.getHours() * paramDuration.getSign());
    gregorianCalendar2.add(12, paramDuration.getMinutes() * paramDuration.getSign());
    gregorianCalendar2.add(13, paramDuration.getSeconds() * paramDuration.getSign());
    return gregorianCalendar1.equals(gregorianCalendar2) ? 0 : compareDates(this, paramDuration);
  }
  
  private int compareDates(Duration paramDuration1, Duration paramDuration2) {
    null = 2;
    int i = 2;
    XMLGregorianCalendar xMLGregorianCalendar1 = (XMLGregorianCalendar)TEST_POINTS[0].clone();
    XMLGregorianCalendar xMLGregorianCalendar2 = (XMLGregorianCalendar)TEST_POINTS[0].clone();
    xMLGregorianCalendar1.add(paramDuration1);
    xMLGregorianCalendar2.add(paramDuration2);
    null = xMLGregorianCalendar1.compare(xMLGregorianCalendar2);
    if (null == 2)
      return 2; 
    xMLGregorianCalendar1 = (XMLGregorianCalendar)TEST_POINTS[1].clone();
    xMLGregorianCalendar2 = (XMLGregorianCalendar)TEST_POINTS[1].clone();
    xMLGregorianCalendar1.add(paramDuration1);
    xMLGregorianCalendar2.add(paramDuration2);
    i = xMLGregorianCalendar1.compare(xMLGregorianCalendar2);
    null = compareResults(null, i);
    if (null == 2)
      return 2; 
    xMLGregorianCalendar1 = (XMLGregorianCalendar)TEST_POINTS[2].clone();
    xMLGregorianCalendar2 = (XMLGregorianCalendar)TEST_POINTS[2].clone();
    xMLGregorianCalendar1.add(paramDuration1);
    xMLGregorianCalendar2.add(paramDuration2);
    i = xMLGregorianCalendar1.compare(xMLGregorianCalendar2);
    null = compareResults(null, i);
    if (null == 2)
      return 2; 
    xMLGregorianCalendar1 = (XMLGregorianCalendar)TEST_POINTS[3].clone();
    xMLGregorianCalendar2 = (XMLGregorianCalendar)TEST_POINTS[3].clone();
    xMLGregorianCalendar1.add(paramDuration1);
    xMLGregorianCalendar2.add(paramDuration2);
    i = xMLGregorianCalendar1.compare(xMLGregorianCalendar2);
    return compareResults(null, i);
  }
  
  private int compareResults(int paramInt1, int paramInt2) { return (paramInt2 == 2) ? 2 : ((paramInt1 != paramInt2) ? 2 : paramInt1); }
  
  public int hashCode() {
    GregorianCalendar gregorianCalendar = TEST_POINTS[0].toGregorianCalendar();
    addTo(gregorianCalendar);
    return (int)getCalendarTimeInMillis(gregorianCalendar);
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.signum < 0)
      stringBuffer.append('-'); 
    stringBuffer.append('P');
    if (this.years != null)
      stringBuffer.append(this.years + "Y"); 
    if (this.months != null)
      stringBuffer.append(this.months + "M"); 
    if (this.days != null)
      stringBuffer.append(this.days + "D"); 
    if (this.hours != null || this.minutes != null || this.seconds != null) {
      stringBuffer.append('T');
      if (this.hours != null)
        stringBuffer.append(this.hours + "H"); 
      if (this.minutes != null)
        stringBuffer.append(this.minutes + "M"); 
      if (this.seconds != null)
        stringBuffer.append(toString(this.seconds) + "S"); 
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
  
  public boolean isSet(DatatypeConstants.Field paramField) {
    if (paramField == null) {
      String str1 = "javax.xml.datatype.Duration#isSet(DatatypeConstants.Field field)";
      throw new NullPointerException(DatatypeMessageFormatter.formatMessage(null, "FieldCannotBeNull", new Object[] { str1 }));
    } 
    if (paramField == DatatypeConstants.YEARS)
      return (this.years != null); 
    if (paramField == DatatypeConstants.MONTHS)
      return (this.months != null); 
    if (paramField == DatatypeConstants.DAYS)
      return (this.days != null); 
    if (paramField == DatatypeConstants.HOURS)
      return (this.hours != null); 
    if (paramField == DatatypeConstants.MINUTES)
      return (this.minutes != null); 
    if (paramField == DatatypeConstants.SECONDS)
      return (this.seconds != null); 
    String str = "javax.xml.datatype.Duration#isSet(DatatypeConstants.Field field)";
    throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "UnknownField", new Object[] { str, paramField.toString() }));
  }
  
  public Number getField(DatatypeConstants.Field paramField) {
    if (paramField == null) {
      String str1 = "javax.xml.datatype.Duration#isSet(DatatypeConstants.Field field) ";
      throw new NullPointerException(DatatypeMessageFormatter.formatMessage(null, "FieldCannotBeNull", new Object[] { str1 }));
    } 
    if (paramField == DatatypeConstants.YEARS)
      return this.years; 
    if (paramField == DatatypeConstants.MONTHS)
      return this.months; 
    if (paramField == DatatypeConstants.DAYS)
      return this.days; 
    if (paramField == DatatypeConstants.HOURS)
      return this.hours; 
    if (paramField == DatatypeConstants.MINUTES)
      return this.minutes; 
    if (paramField == DatatypeConstants.SECONDS)
      return this.seconds; 
    String str = "javax.xml.datatype.Duration#(getSet(DatatypeConstants.Field field)";
    throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "UnknownField", new Object[] { str, paramField.toString() }));
  }
  
  public int getYears() { return getInt(DatatypeConstants.YEARS); }
  
  public int getMonths() { return getInt(DatatypeConstants.MONTHS); }
  
  public int getDays() { return getInt(DatatypeConstants.DAYS); }
  
  public int getHours() { return getInt(DatatypeConstants.HOURS); }
  
  public int getMinutes() { return getInt(DatatypeConstants.MINUTES); }
  
  public int getSeconds() { return getInt(DatatypeConstants.SECONDS); }
  
  private int getInt(DatatypeConstants.Field paramField) {
    Number number = getField(paramField);
    return (number == null) ? 0 : number.intValue();
  }
  
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
  
  public Duration normalizeWith(Calendar paramCalendar) {
    Calendar calendar = (Calendar)paramCalendar.clone();
    calendar.add(1, getYears() * this.signum);
    calendar.add(2, getMonths() * this.signum);
    calendar.add(5, getDays() * this.signum);
    long l = getCalendarTimeInMillis(calendar) - getCalendarTimeInMillis(paramCalendar);
    int i = (int)(l / 86400000L);
    return new DurationImpl((i >= 0), null, null, wrap(Math.abs(i)), (BigInteger)getField(DatatypeConstants.HOURS), (BigInteger)getField(DatatypeConstants.MINUTES), (BigDecimal)getField(DatatypeConstants.SECONDS));
  }
  
  public Duration multiply(int paramInt) { return multiply(BigDecimal.valueOf(paramInt)); }
  
  public Duration multiply(BigDecimal paramBigDecimal) {
    BigDecimal bigDecimal = ZERO;
    int i = paramBigDecimal.signum();
    paramBigDecimal = paramBigDecimal.abs();
    BigDecimal[] arrayOfBigDecimal = new BigDecimal[6];
    for (byte b = 0; b < 5; b++) {
      BigDecimal bigDecimal1 = getFieldAsBigDecimal(FIELDS[b]);
      bigDecimal1 = bigDecimal1.multiply(paramBigDecimal).add(bigDecimal);
      arrayOfBigDecimal[b] = bigDecimal1.setScale(0, 1);
      bigDecimal1 = bigDecimal1.subtract(arrayOfBigDecimal[b]);
      if (b == 1) {
        if (bigDecimal1.signum() != 0)
          throw new IllegalStateException(); 
        bigDecimal = ZERO;
      } else {
        bigDecimal = bigDecimal1.multiply(FACTORS[b]);
      } 
    } 
    if (this.seconds != null) {
      arrayOfBigDecimal[5] = this.seconds.multiply(paramBigDecimal).add(bigDecimal);
    } else {
      arrayOfBigDecimal[5] = bigDecimal;
    } 
    return new DurationImpl((this.signum * i >= 0), toBigInteger(arrayOfBigDecimal[0], (null == this.years)), toBigInteger(arrayOfBigDecimal[1], (null == this.months)), toBigInteger(arrayOfBigDecimal[2], (null == this.days)), toBigInteger(arrayOfBigDecimal[3], (null == this.hours)), toBigInteger(arrayOfBigDecimal[4], (null == this.minutes)), (arrayOfBigDecimal[5].signum() == 0 && this.seconds == null) ? null : arrayOfBigDecimal[5]);
  }
  
  private BigDecimal getFieldAsBigDecimal(DatatypeConstants.Field paramField) {
    if (paramField == DatatypeConstants.SECONDS)
      return (this.seconds != null) ? this.seconds : ZERO; 
    BigInteger bigInteger = (BigInteger)getField(paramField);
    return (bigInteger == null) ? ZERO : new BigDecimal(bigInteger);
  }
  
  private static BigInteger toBigInteger(BigDecimal paramBigDecimal, boolean paramBoolean) { return (paramBoolean && paramBigDecimal.signum() == 0) ? null : paramBigDecimal.unscaledValue(); }
  
  public Duration add(Duration paramDuration) {
    DurationImpl durationImpl = this;
    BigDecimal[] arrayOfBigDecimal = new BigDecimal[6];
    arrayOfBigDecimal[0] = sanitize((BigInteger)durationImpl.getField(DatatypeConstants.YEARS), durationImpl.getSign()).add(sanitize((BigInteger)paramDuration.getField(DatatypeConstants.YEARS), paramDuration.getSign()));
    arrayOfBigDecimal[1] = sanitize((BigInteger)durationImpl.getField(DatatypeConstants.MONTHS), durationImpl.getSign()).add(sanitize((BigInteger)paramDuration.getField(DatatypeConstants.MONTHS), paramDuration.getSign()));
    arrayOfBigDecimal[2] = sanitize((BigInteger)durationImpl.getField(DatatypeConstants.DAYS), durationImpl.getSign()).add(sanitize((BigInteger)paramDuration.getField(DatatypeConstants.DAYS), paramDuration.getSign()));
    arrayOfBigDecimal[3] = sanitize((BigInteger)durationImpl.getField(DatatypeConstants.HOURS), durationImpl.getSign()).add(sanitize((BigInteger)paramDuration.getField(DatatypeConstants.HOURS), paramDuration.getSign()));
    arrayOfBigDecimal[4] = sanitize((BigInteger)durationImpl.getField(DatatypeConstants.MINUTES), durationImpl.getSign()).add(sanitize((BigInteger)paramDuration.getField(DatatypeConstants.MINUTES), paramDuration.getSign()));
    arrayOfBigDecimal[5] = sanitize((BigDecimal)durationImpl.getField(DatatypeConstants.SECONDS), durationImpl.getSign()).add(sanitize((BigDecimal)paramDuration.getField(DatatypeConstants.SECONDS), paramDuration.getSign()));
    alignSigns(arrayOfBigDecimal, 0, 2);
    alignSigns(arrayOfBigDecimal, 2, 6);
    int i = 0;
    for (byte b = 0; b < 6; b++) {
      if (i * arrayOfBigDecimal[b].signum() < 0)
        throw new IllegalStateException(); 
      if (i == 0)
        i = arrayOfBigDecimal[b].signum(); 
    } 
    return new DurationImpl((i >= 0), toBigInteger(sanitize(arrayOfBigDecimal[0], i), (durationImpl.getField(DatatypeConstants.YEARS) == null && paramDuration.getField(DatatypeConstants.YEARS) == null)), toBigInteger(sanitize(arrayOfBigDecimal[1], i), (durationImpl.getField(DatatypeConstants.MONTHS) == null && paramDuration.getField(DatatypeConstants.MONTHS) == null)), toBigInteger(sanitize(arrayOfBigDecimal[2], i), (durationImpl.getField(DatatypeConstants.DAYS) == null && paramDuration.getField(DatatypeConstants.DAYS) == null)), toBigInteger(sanitize(arrayOfBigDecimal[3], i), (durationImpl.getField(DatatypeConstants.HOURS) == null && paramDuration.getField(DatatypeConstants.HOURS) == null)), toBigInteger(sanitize(arrayOfBigDecimal[4], i), (durationImpl.getField(DatatypeConstants.MINUTES) == null && paramDuration.getField(DatatypeConstants.MINUTES) == null)), (arrayOfBigDecimal[5].signum() == 0 && durationImpl.getField(DatatypeConstants.SECONDS) == null && paramDuration.getField(DatatypeConstants.SECONDS) == null) ? null : sanitize(arrayOfBigDecimal[5], i));
  }
  
  private static void alignSigns(BigDecimal[] paramArrayOfBigDecimal, int paramInt1, int paramInt2) {
    boolean bool;
    do {
      bool = false;
      int i = 0;
      for (int j = paramInt1; j < paramInt2; j++) {
        if (i * paramArrayOfBigDecimal[j].signum() < 0) {
          bool = true;
          BigDecimal bigDecimal = paramArrayOfBigDecimal[j].abs().divide(FACTORS[j - 1], 0);
          if (paramArrayOfBigDecimal[j].signum() > 0)
            bigDecimal = bigDecimal.negate(); 
          paramArrayOfBigDecimal[j - 1] = paramArrayOfBigDecimal[j - 1].subtract(bigDecimal);
          paramArrayOfBigDecimal[j] = paramArrayOfBigDecimal[j].add(bigDecimal.multiply(FACTORS[j - 1]));
        } 
        if (paramArrayOfBigDecimal[j].signum() != 0)
          i = paramArrayOfBigDecimal[j].signum(); 
      } 
    } while (bool);
  }
  
  private static BigDecimal sanitize(BigInteger paramBigInteger, int paramInt) { return (paramInt == 0 || paramBigInteger == null) ? ZERO : ((paramInt > 0) ? new BigDecimal(paramBigInteger) : new BigDecimal(paramBigInteger.negate())); }
  
  static BigDecimal sanitize(BigDecimal paramBigDecimal, int paramInt) { return (paramInt == 0 || paramBigDecimal == null) ? ZERO : ((paramInt > 0) ? paramBigDecimal : paramBigDecimal.negate()); }
  
  public Duration subtract(Duration paramDuration) { return add(paramDuration.negate()); }
  
  public Duration negate() { return new DurationImpl((this.signum <= 0), this.years, this.months, this.days, this.hours, this.minutes, this.seconds); }
  
  public int signum() { return this.signum; }
  
  public void addTo(Calendar paramCalendar) {
    paramCalendar.add(1, getYears() * this.signum);
    paramCalendar.add(2, getMonths() * this.signum);
    paramCalendar.add(5, getDays() * this.signum);
    paramCalendar.add(10, getHours() * this.signum);
    paramCalendar.add(12, getMinutes() * this.signum);
    paramCalendar.add(13, getSeconds() * this.signum);
    if (this.seconds != null) {
      BigDecimal bigDecimal = this.seconds.subtract(this.seconds.setScale(0, 1));
      int i = bigDecimal.movePointRight(3).intValue();
      paramCalendar.add(14, i * this.signum);
    } 
  }
  
  public void addTo(Date paramDate) {
    GregorianCalendar gregorianCalendar = new GregorianCalendar();
    gregorianCalendar.setTime(paramDate);
    addTo(gregorianCalendar);
    paramDate.setTime(getCalendarTimeInMillis(gregorianCalendar));
  }
  
  private Object writeReplace() throws IOException { return new DurationStream(toString(), null); }
  
  private static long getCalendarTimeInMillis(Calendar paramCalendar) { return paramCalendar.getTime().getTime(); }
  
  private static class DurationStream implements Serializable {
    private final String lexical;
    
    private static final long serialVersionUID = 1L;
    
    private DurationStream(String param1String) throws IllegalArgumentException { this.lexical = param1String; }
    
    private Object readResolve() throws IOException { return new DurationImpl(this.lexical); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\datatype\DurationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */