package com.sun.org.apache.xerces.internal.jaxp.datatype;

import com.sun.org.apache.xerces.internal.util.DatatypeMessageFormatter;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

public class XMLGregorianCalendarImpl extends XMLGregorianCalendar implements Serializable, Cloneable {
  private BigInteger eon = null;
  
  private int year = Integer.MIN_VALUE;
  
  private int month = Integer.MIN_VALUE;
  
  private int day = Integer.MIN_VALUE;
  
  private int timezone = Integer.MIN_VALUE;
  
  private int hour = Integer.MIN_VALUE;
  
  private int minute = Integer.MIN_VALUE;
  
  private int second = Integer.MIN_VALUE;
  
  private BigDecimal fractionalSecond = null;
  
  private static final BigInteger BILLION = new BigInteger("1000000000");
  
  private static final Date PURE_GREGORIAN_CHANGE = new Date(Float.MIN_VALUE);
  
  private static final int YEAR = 0;
  
  private static final int MONTH = 1;
  
  private static final int DAY = 2;
  
  private static final int HOUR = 3;
  
  private static final int MINUTE = 4;
  
  private static final int SECOND = 5;
  
  private static final int MILLISECOND = 6;
  
  private static final int TIMEZONE = 7;
  
  private static final String[] FIELD_NAME = { "Year", "Month", "Day", "Hour", "Minute", "Second", "Millisecond", "Timezone" };
  
  private static final long serialVersionUID = 1L;
  
  public static final XMLGregorianCalendar LEAP_YEAR_DEFAULT = createDateTime(400, 1, 1, 0, 0, 0, -2147483648, -2147483648);
  
  private static final BigInteger FOUR;
  
  private static final BigInteger HUNDRED;
  
  private static final BigInteger FOUR_HUNDRED;
  
  private static final BigInteger SIXTY;
  
  private static final BigInteger TWENTY_FOUR;
  
  private static final BigInteger TWELVE = (TWENTY_FOUR = (SIXTY = (FOUR_HUNDRED = (HUNDRED = (FOUR = BigInteger.valueOf(4L)).valueOf(100L)).valueOf(400L)).valueOf(60L)).valueOf(24L)).valueOf(12L);
  
  private static final BigDecimal DECIMAL_ZERO = new BigDecimal("0");
  
  private static final BigDecimal DECIMAL_ONE = new BigDecimal("1");
  
  private static final BigDecimal DECIMAL_SIXTY = new BigDecimal("60");
  
  private static int[] daysInMonth = { 
      0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 
      31, 30, 31 };
  
  protected XMLGregorianCalendarImpl(String paramString) throws IllegalArgumentException {
    String str1 = null;
    String str2 = paramString;
    byte b = -1;
    int i = str2.length();
    if (str2.indexOf('T') != -1) {
      str1 = "%Y-%M-%DT%h:%m:%s%z";
    } else if (i >= 3 && str2.charAt(2) == ':') {
      str1 = "%h:%m:%s%z";
    } else if (str2.startsWith("--")) {
      if (i >= 3 && str2.charAt(2) == '-') {
        str1 = "---%D%z";
      } else if (i == 4 || i == 5 || i == 10) {
        str1 = "--%M%z";
      } else {
        str1 = "--%M-%D%z";
      } 
    } else {
      byte b1 = 0;
      int j = str2.indexOf(':');
      if (j != -1)
        i -= 6; 
      for (byte b2 = 1; b2 < i; b2++) {
        if (str2.charAt(b2) == '-')
          b1++; 
      } 
      if (b1 == 0) {
        str1 = "%Y%z";
      } else if (b1 == 1) {
        str1 = "%Y-%M%z";
      } else {
        str1 = "%Y-%M-%D%z";
      } 
    } 
    Parser parser = new Parser(str1, str2, null);
    parser.parse();
    if (!isValid())
      throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "InvalidXGCRepresentation", new Object[] { paramString })); 
  }
  
  public XMLGregorianCalendarImpl() {}
  
  protected XMLGregorianCalendarImpl(BigInteger paramBigInteger, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, BigDecimal paramBigDecimal, int paramInt6) {
    setYear(paramBigInteger);
    setMonth(paramInt1);
    setDay(paramInt2);
    setTime(paramInt3, paramInt4, paramInt5, paramBigDecimal);
    setTimezone(paramInt6);
    if (!isValid())
      throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "InvalidXGCValue-fractional", new Object[] { paramBigInteger, new Integer(paramInt1), new Integer(paramInt2), new Integer(paramInt3), new Integer(paramInt4), new Integer(paramInt5), paramBigDecimal, new Integer(paramInt6) })); 
  }
  
  private XMLGregorianCalendarImpl(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8) {
    setYear(paramInt1);
    setMonth(paramInt2);
    setDay(paramInt3);
    setTime(paramInt4, paramInt5, paramInt6);
    setTimezone(paramInt8);
    setMillisecond(paramInt7);
    if (!isValid())
      throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "InvalidXGCValue-milli", new Object[] { new Integer(paramInt1), new Integer(paramInt2), new Integer(paramInt3), new Integer(paramInt4), new Integer(paramInt5), new Integer(paramInt6), new Integer(paramInt7), new Integer(paramInt8) })); 
  }
  
  public XMLGregorianCalendarImpl(GregorianCalendar paramGregorianCalendar) {
    int i = paramGregorianCalendar.get(1);
    if (paramGregorianCalendar.get(0) == 0)
      i = -i; 
    setYear(i);
    setMonth(paramGregorianCalendar.get(2) + 1);
    setDay(paramGregorianCalendar.get(5));
    setTime(paramGregorianCalendar.get(11), paramGregorianCalendar.get(12), paramGregorianCalendar.get(13), paramGregorianCalendar.get(14));
    int j = (paramGregorianCalendar.get(15) + paramGregorianCalendar.get(16)) / 60000;
    setTimezone(j);
  }
  
  public static XMLGregorianCalendar createDateTime(BigInteger paramBigInteger, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, BigDecimal paramBigDecimal, int paramInt6) { return new XMLGregorianCalendarImpl(paramBigInteger, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramBigDecimal, paramInt6); }
  
  public static XMLGregorianCalendar createDateTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) { return new XMLGregorianCalendarImpl(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, -2147483648, -2147483648); }
  
  public static XMLGregorianCalendar createDateTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8) { return new XMLGregorianCalendarImpl(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8); }
  
  public static XMLGregorianCalendar createDate(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return new XMLGregorianCalendarImpl(paramInt1, paramInt2, paramInt3, -2147483648, -2147483648, -2147483648, -2147483648, paramInt4); }
  
  public static XMLGregorianCalendar createTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return new XMLGregorianCalendarImpl(-2147483648, -2147483648, -2147483648, paramInt1, paramInt2, paramInt3, -2147483648, paramInt4); }
  
  public static XMLGregorianCalendar createTime(int paramInt1, int paramInt2, int paramInt3, BigDecimal paramBigDecimal, int paramInt4) { return new XMLGregorianCalendarImpl(null, -2147483648, -2147483648, paramInt1, paramInt2, paramInt3, paramBigDecimal, paramInt4); }
  
  public static XMLGregorianCalendar createTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { return new XMLGregorianCalendarImpl(-2147483648, -2147483648, -2147483648, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public BigInteger getEon() { return this.eon; }
  
  public int getYear() { return this.year; }
  
  public BigInteger getEonAndYear() { return (this.year != Integer.MIN_VALUE && this.eon != null) ? this.eon.add(BigInteger.valueOf(this.year)) : ((this.year != Integer.MIN_VALUE && this.eon == null) ? BigInteger.valueOf(this.year) : null); }
  
  public int getMonth() { return this.month; }
  
  public int getDay() { return this.day; }
  
  public int getTimezone() { return this.timezone; }
  
  public int getHour() { return this.hour; }
  
  public int getMinute() { return this.minute; }
  
  public int getSecond() { return this.second; }
  
  private BigDecimal getSeconds() {
    if (this.second == Integer.MIN_VALUE)
      return DECIMAL_ZERO; 
    BigDecimal bigDecimal = BigDecimal.valueOf(this.second);
    return (this.fractionalSecond != null) ? bigDecimal.add(this.fractionalSecond) : bigDecimal;
  }
  
  public int getMillisecond() { return (this.fractionalSecond == null) ? Integer.MIN_VALUE : this.fractionalSecond.movePointRight(3).intValue(); }
  
  public BigDecimal getFractionalSecond() { return this.fractionalSecond; }
  
  public void setYear(BigInteger paramBigInteger) {
    if (paramBigInteger == null) {
      this.eon = null;
      this.year = Integer.MIN_VALUE;
    } else {
      BigInteger bigInteger = paramBigInteger.remainder(BILLION);
      this.year = bigInteger.intValue();
      setEon(paramBigInteger.subtract(bigInteger));
    } 
  }
  
  public void setYear(int paramInt) {
    if (paramInt == Integer.MIN_VALUE) {
      this.year = Integer.MIN_VALUE;
      this.eon = null;
    } else if (Math.abs(paramInt) < BILLION.intValue()) {
      this.year = paramInt;
      this.eon = null;
    } else {
      BigInteger bigInteger1 = BigInteger.valueOf(paramInt);
      BigInteger bigInteger2 = bigInteger1.remainder(BILLION);
      this.year = bigInteger2.intValue();
      setEon(bigInteger1.subtract(bigInteger2));
    } 
  }
  
  private void setEon(BigInteger paramBigInteger) {
    if (paramBigInteger != null && paramBigInteger.compareTo(BigInteger.ZERO) == 0) {
      this.eon = null;
    } else {
      this.eon = paramBigInteger;
    } 
  }
  
  public void setMonth(int paramInt) {
    if ((paramInt < 1 || 12 < paramInt) && paramInt != Integer.MIN_VALUE)
      invalidFieldValue(1, paramInt); 
    this.month = paramInt;
  }
  
  public void setDay(int paramInt) {
    if ((paramInt < 1 || 31 < paramInt) && paramInt != Integer.MIN_VALUE)
      invalidFieldValue(2, paramInt); 
    this.day = paramInt;
  }
  
  public void setTimezone(int paramInt) {
    if ((paramInt < -840 || 840 < paramInt) && paramInt != Integer.MIN_VALUE)
      invalidFieldValue(7, paramInt); 
    this.timezone = paramInt;
  }
  
  public void setTime(int paramInt1, int paramInt2, int paramInt3) { setTime(paramInt1, paramInt2, paramInt3, null); }
  
  private void invalidFieldValue(int paramInt1, int paramInt2) { throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "InvalidFieldValue", new Object[] { new Integer(paramInt2), FIELD_NAME[paramInt1] })); }
  
  private void testHour() {
    if (getHour() == 24) {
      if (getMinute() != 0 || getSecond() != 0)
        invalidFieldValue(3, getHour()); 
      setHour(0, false);
      add(new DurationImpl(true, 0, 0, 1, 0, 0, 0));
    } 
  }
  
  public void setHour(int paramInt) { setHour(paramInt, true); }
  
  private void setHour(int paramInt, boolean paramBoolean) {
    if ((paramInt < 0 || paramInt > 24) && paramInt != Integer.MIN_VALUE)
      invalidFieldValue(3, paramInt); 
    this.hour = paramInt;
    if (paramBoolean)
      testHour(); 
  }
  
  public void setMinute(int paramInt) {
    if ((paramInt < 0 || 59 < paramInt) && paramInt != Integer.MIN_VALUE)
      invalidFieldValue(4, paramInt); 
    this.minute = paramInt;
  }
  
  public void setSecond(int paramInt) {
    if ((paramInt < 0 || 60 < paramInt) && paramInt != Integer.MIN_VALUE)
      invalidFieldValue(5, paramInt); 
    this.second = paramInt;
  }
  
  public void setTime(int paramInt1, int paramInt2, int paramInt3, BigDecimal paramBigDecimal) {
    setHour(paramInt1, false);
    setMinute(paramInt2);
    if (paramInt3 != 60) {
      setSecond(paramInt3);
    } else if ((paramInt1 == 23 && paramInt2 == 59) || (paramInt1 == 0 && paramInt2 == 0)) {
      setSecond(paramInt3);
    } else {
      invalidFieldValue(5, paramInt3);
    } 
    setFractionalSecond(paramBigDecimal);
    testHour();
  }
  
  public void setTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    setHour(paramInt1, false);
    setMinute(paramInt2);
    if (paramInt3 != 60) {
      setSecond(paramInt3);
    } else if ((paramInt1 == 23 && paramInt2 == 59) || (paramInt1 == 0 && paramInt2 == 0)) {
      setSecond(paramInt3);
    } else {
      invalidFieldValue(5, paramInt3);
    } 
    setMillisecond(paramInt4);
    testHour();
  }
  
  public int compare(XMLGregorianCalendar paramXMLGregorianCalendar) {
    XMLGregorianCalendarImpl xMLGregorianCalendarImpl1 = this;
    int i = 2;
    XMLGregorianCalendarImpl xMLGregorianCalendarImpl2 = (XMLGregorianCalendarImpl)xMLGregorianCalendarImpl1;
    XMLGregorianCalendarImpl xMLGregorianCalendarImpl3 = (XMLGregorianCalendarImpl)paramXMLGregorianCalendar;
    if (xMLGregorianCalendarImpl2.getTimezone() == xMLGregorianCalendarImpl3.getTimezone())
      return internalCompare(xMLGregorianCalendarImpl2, xMLGregorianCalendarImpl3); 
    if (xMLGregorianCalendarImpl2.getTimezone() != Integer.MIN_VALUE && xMLGregorianCalendarImpl3.getTimezone() != Integer.MIN_VALUE) {
      xMLGregorianCalendarImpl2 = (XMLGregorianCalendarImpl)xMLGregorianCalendarImpl2.normalize();
      return (xMLGregorianCalendarImpl3 = (XMLGregorianCalendarImpl)xMLGregorianCalendarImpl3.normalize()).internalCompare(xMLGregorianCalendarImpl2, xMLGregorianCalendarImpl3);
    } 
    if (xMLGregorianCalendarImpl2.getTimezone() != Integer.MIN_VALUE) {
      if (xMLGregorianCalendarImpl2.getTimezone() != 0)
        xMLGregorianCalendarImpl2 = (XMLGregorianCalendarImpl)xMLGregorianCalendarImpl2.normalize(); 
      XMLGregorianCalendar xMLGregorianCalendar3 = xMLGregorianCalendarImpl3.normalizeToTimezone(840);
      i = internalCompare(xMLGregorianCalendarImpl2, xMLGregorianCalendar3);
      if (i == -1)
        return i; 
      XMLGregorianCalendar xMLGregorianCalendar4 = xMLGregorianCalendarImpl3.normalizeToTimezone(-840);
      i = internalCompare(xMLGregorianCalendarImpl2, xMLGregorianCalendar4);
      return (i == 1) ? i : 2;
    } 
    if (xMLGregorianCalendarImpl3.getTimezone() != 0)
      xMLGregorianCalendarImpl3 = (XMLGregorianCalendarImpl)xMLGregorianCalendarImpl3.normalizeToTimezone(xMLGregorianCalendarImpl3.getTimezone()); 
    XMLGregorianCalendar xMLGregorianCalendar1 = xMLGregorianCalendarImpl2.normalizeToTimezone(-840);
    i = internalCompare(xMLGregorianCalendar1, xMLGregorianCalendarImpl3);
    if (i == -1)
      return i; 
    XMLGregorianCalendar xMLGregorianCalendar2 = xMLGregorianCalendarImpl2.normalizeToTimezone(840);
    i = internalCompare(xMLGregorianCalendar2, xMLGregorianCalendarImpl3);
    return (i == 1) ? i : 2;
  }
  
  public XMLGregorianCalendar normalize() {
    XMLGregorianCalendar xMLGregorianCalendar = normalizeToTimezone(this.timezone);
    if (getTimezone() == Integer.MIN_VALUE)
      xMLGregorianCalendar.setTimezone(-2147483648); 
    if (getMillisecond() == Integer.MIN_VALUE)
      xMLGregorianCalendar.setMillisecond(-2147483648); 
    return xMLGregorianCalendar;
  }
  
  private XMLGregorianCalendar normalizeToTimezone(int paramInt) {
    int i = paramInt;
    XMLGregorianCalendar xMLGregorianCalendar = (XMLGregorianCalendar)clone();
    i = -i;
    DurationImpl durationImpl = new DurationImpl((i >= 0), 0, 0, 0, 0, (i < 0) ? -i : i, 0);
    xMLGregorianCalendar.add(durationImpl);
    xMLGregorianCalendar.setTimezone(0);
    return xMLGregorianCalendar;
  }
  
  private static int internalCompare(XMLGregorianCalendar paramXMLGregorianCalendar1, XMLGregorianCalendar paramXMLGregorianCalendar2) {
    if (paramXMLGregorianCalendar1.getEon() == paramXMLGregorianCalendar2.getEon()) {
      int i = compareField(paramXMLGregorianCalendar1.getYear(), paramXMLGregorianCalendar2.getYear());
      if (i != 0)
        return i; 
    } else {
      int i = compareField(paramXMLGregorianCalendar1.getEonAndYear(), paramXMLGregorianCalendar2.getEonAndYear());
      if (i != 0)
        return i; 
    } 
    null = compareField(paramXMLGregorianCalendar1.getMonth(), paramXMLGregorianCalendar2.getMonth());
    if (null != 0)
      return null; 
    null = compareField(paramXMLGregorianCalendar1.getDay(), paramXMLGregorianCalendar2.getDay());
    if (null != 0)
      return null; 
    null = compareField(paramXMLGregorianCalendar1.getHour(), paramXMLGregorianCalendar2.getHour());
    if (null != 0)
      return null; 
    null = compareField(paramXMLGregorianCalendar1.getMinute(), paramXMLGregorianCalendar2.getMinute());
    if (null != 0)
      return null; 
    null = compareField(paramXMLGregorianCalendar1.getSecond(), paramXMLGregorianCalendar2.getSecond());
    return (null != 0) ? null : compareField(paramXMLGregorianCalendar1.getFractionalSecond(), paramXMLGregorianCalendar2.getFractionalSecond());
  }
  
  private static int compareField(int paramInt1, int paramInt2) { return (paramInt1 == paramInt2) ? 0 : ((paramInt1 == Integer.MIN_VALUE || paramInt2 == Integer.MIN_VALUE) ? 2 : ((paramInt1 < paramInt2) ? -1 : 1)); }
  
  private static int compareField(BigInteger paramBigInteger1, BigInteger paramBigInteger2) { return (paramBigInteger1 == null) ? ((paramBigInteger2 == null) ? 0 : 2) : ((paramBigInteger2 == null) ? 2 : paramBigInteger1.compareTo(paramBigInteger2)); }
  
  private static int compareField(BigDecimal paramBigDecimal1, BigDecimal paramBigDecimal2) {
    if (paramBigDecimal1 == paramBigDecimal2)
      return 0; 
    if (paramBigDecimal1 == null)
      paramBigDecimal1 = DECIMAL_ZERO; 
    if (paramBigDecimal2 == null)
      paramBigDecimal2 = DECIMAL_ZERO; 
    return paramBigDecimal1.compareTo(paramBigDecimal2);
  }
  
  public boolean equals(Object paramObject) { return (paramObject == null || !(paramObject instanceof XMLGregorianCalendar)) ? false : ((compare((XMLGregorianCalendar)paramObject) == 0)); }
  
  public int hashCode() {
    int i = getTimezone();
    if (i == Integer.MIN_VALUE)
      i = 0; 
    XMLGregorianCalendar xMLGregorianCalendar = this;
    if (i != 0)
      xMLGregorianCalendar = normalizeToTimezone(getTimezone()); 
    return xMLGregorianCalendar.getYear() + xMLGregorianCalendar.getMonth() + xMLGregorianCalendar.getDay() + xMLGregorianCalendar.getHour() + xMLGregorianCalendar.getMinute() + xMLGregorianCalendar.getSecond();
  }
  
  public static XMLGregorianCalendar parse(String paramString) { return new XMLGregorianCalendarImpl(paramString); }
  
  public String toXMLFormat() {
    QName qName = getXMLSchemaType();
    String str = null;
    if (qName == DatatypeConstants.DATETIME) {
      str = "%Y-%M-%DT%h:%m:%s%z";
    } else if (qName == DatatypeConstants.DATE) {
      str = "%Y-%M-%D%z";
    } else if (qName == DatatypeConstants.TIME) {
      str = "%h:%m:%s%z";
    } else if (qName == DatatypeConstants.GMONTH) {
      str = "--%M%z";
    } else if (qName == DatatypeConstants.GDAY) {
      str = "---%D%z";
    } else if (qName == DatatypeConstants.GYEAR) {
      str = "%Y%z";
    } else if (qName == DatatypeConstants.GYEARMONTH) {
      str = "%Y-%M%z";
    } else if (qName == DatatypeConstants.GMONTHDAY) {
      str = "--%M-%D%z";
    } 
    return format(str);
  }
  
  public QName getXMLSchemaType() {
    byte b = ((this.year != Integer.MIN_VALUE) ? 32 : 0) | ((this.month != Integer.MIN_VALUE) ? 16 : 0) | ((this.day != Integer.MIN_VALUE) ? 8 : 0) | ((this.hour != Integer.MIN_VALUE) ? 4 : 0) | ((this.minute != Integer.MIN_VALUE) ? 2 : 0) | ((this.second != Integer.MIN_VALUE) ? 1 : 0);
    switch (b) {
      case 63:
        return DatatypeConstants.DATETIME;
      case 56:
        return DatatypeConstants.DATE;
      case 7:
        return DatatypeConstants.TIME;
      case 48:
        return DatatypeConstants.GYEARMONTH;
      case 24:
        return DatatypeConstants.GMONTHDAY;
      case 32:
        return DatatypeConstants.GYEAR;
      case 16:
        return DatatypeConstants.GMONTH;
      case 8:
        return DatatypeConstants.GDAY;
    } 
    throw new IllegalStateException(getClass().getName() + "#getXMLSchemaType() :" + DatatypeMessageFormatter.formatMessage(null, "InvalidXGCFields", null));
  }
  
  public boolean isValid() {
    if (getMonth() == 2) {
      int i = 29;
      if (this.eon == null) {
        if (this.year != Integer.MIN_VALUE)
          i = maximumDayInMonthFor(this.year, getMonth()); 
      } else {
        BigInteger bigInteger = getEonAndYear();
        if (bigInteger != null)
          i = maximumDayInMonthFor(getEonAndYear(), 2); 
      } 
      if (getDay() > i)
        return false; 
    } 
    if (getHour() == 24) {
      if (getMinute() != 0)
        return false; 
      if (getSecond() != 0)
        return false; 
    } 
    if (this.eon == null) {
      if (this.year == 0)
        return false; 
    } else {
      BigInteger bigInteger = getEonAndYear();
      if (bigInteger != null) {
        int i = compareField(bigInteger, BigInteger.ZERO);
        if (i == 0)
          return false; 
      } 
    } 
    return true;
  }
  
  public void add(Duration paramDuration) {
    BigInteger bigInteger9;
    BigDecimal bigDecimal1;
    boolean[] arrayOfBoolean = { false, false, false, false, false, false };
    int i = paramDuration.getSign();
    int j = getMonth();
    if (j == Integer.MIN_VALUE) {
      j = 1;
      arrayOfBoolean[1] = true;
    } 
    BigInteger bigInteger1;
    BigInteger bigInteger2 = (bigInteger1 = sanitize(paramDuration.getField(DatatypeConstants.MONTHS), i)).valueOf(j).add(bigInteger1);
    setMonth(bigInteger2.subtract(BigInteger.ONE).mod(TWELVE).intValue() + 1);
    BigInteger bigInteger3 = (new BigDecimal(bigInteger2.subtract(BigInteger.ONE))).divide(new BigDecimal(TWELVE), 3).toBigInteger();
    BigInteger bigInteger4 = getEonAndYear();
    if (bigInteger4 == null) {
      arrayOfBoolean[0] = true;
      bigInteger4 = BigInteger.ZERO;
    } 
    BigInteger bigInteger5 = sanitize(paramDuration.getField(DatatypeConstants.YEARS), i);
    BigInteger bigInteger6 = bigInteger4.add(bigInteger5).add(bigInteger3);
    setYear(bigInteger6);
    if (getSecond() == Integer.MIN_VALUE) {
      arrayOfBoolean[5] = true;
      bigDecimal1 = DECIMAL_ZERO;
    } else {
      bigDecimal1 = getSeconds();
    } 
    BigDecimal bigDecimal2 = DurationImpl.sanitize((BigDecimal)paramDuration.getField(DatatypeConstants.SECONDS), i);
    BigDecimal bigDecimal3 = bigDecimal1.add(bigDecimal2);
    BigDecimal bigDecimal4 = new BigDecimal((new BigDecimal(bigDecimal3.toBigInteger())).divide(DECIMAL_SIXTY, 3).toBigInteger());
    BigDecimal bigDecimal5 = bigDecimal3.subtract(bigDecimal4.multiply(DECIMAL_SIXTY));
    bigInteger3 = bigDecimal4.toBigInteger();
    setSecond(bigDecimal5.intValue());
    BigDecimal bigDecimal6 = bigDecimal5.subtract(new BigDecimal(BigInteger.valueOf(getSecond())));
    if (bigDecimal6.compareTo(DECIMAL_ZERO) < 0) {
      setFractionalSecond(DECIMAL_ONE.add(bigDecimal6));
      if (getSecond() == 0) {
        setSecond(59);
        bigInteger3 = bigInteger3.subtract(BigInteger.ONE);
      } else {
        setSecond(getSecond() - 1);
      } 
    } else {
      setFractionalSecond(bigDecimal6);
    } 
    int k = getMinute();
    if (k == Integer.MIN_VALUE) {
      arrayOfBoolean[4] = true;
      k = 0;
    } 
    BigInteger bigInteger7;
    bigInteger2 = (bigInteger7 = sanitize(paramDuration.getField(DatatypeConstants.MINUTES), i)).valueOf(k).add(bigInteger7).add(bigInteger3);
    setMinute(bigInteger2.mod(SIXTY).intValue());
    bigInteger3 = (new BigDecimal(bigInteger2)).divide(DECIMAL_SIXTY, 3).toBigInteger();
    int m = getHour();
    if (m == Integer.MIN_VALUE) {
      arrayOfBoolean[3] = true;
      m = 0;
    } 
    BigInteger bigInteger8;
    bigInteger2 = (bigInteger8 = sanitize(paramDuration.getField(DatatypeConstants.HOURS), i)).valueOf(m).add(bigInteger8).add(bigInteger3);
    setHour(bigInteger2.mod(TWENTY_FOUR).intValue(), false);
    bigInteger3 = (new BigDecimal(bigInteger2)).divide(new BigDecimal(TWENTY_FOUR), 3).toBigInteger();
    int n = getDay();
    if (n == Integer.MIN_VALUE) {
      arrayOfBoolean[2] = true;
      n = 1;
    } 
    BigInteger bigInteger10 = sanitize(paramDuration.getField(DatatypeConstants.DAYS), i);
    int i1 = maximumDayInMonthFor(getEonAndYear(), getMonth());
    if (n > i1) {
      bigInteger9 = BigInteger.valueOf(i1);
    } else if (n < 1) {
      bigInteger9 = BigInteger.ONE;
    } else {
      bigInteger9 = BigInteger.valueOf(n);
    } 
    BigInteger bigInteger11 = bigInteger9.add(bigInteger10).add(bigInteger3);
    while (true) {
      int i5;
      int i2;
      if (bigInteger11.compareTo(BigInteger.ONE) < 0) {
        BigInteger bigInteger = null;
        if (this.month >= 2) {
          bigInteger = BigInteger.valueOf(maximumDayInMonthFor(getEonAndYear(), getMonth() - 1));
        } else {
          bigInteger = BigInteger.valueOf(maximumDayInMonthFor(getEonAndYear().subtract(BigInteger.valueOf(1L)), 12));
        } 
        bigInteger11 = bigInteger11.add(bigInteger);
        i2 = -1;
      } else if (bigInteger11.compareTo(BigInteger.valueOf(maximumDayInMonthFor(getEonAndYear(), getMonth()))) > 0) {
        bigInteger11 = bigInteger11.add(BigInteger.valueOf(-maximumDayInMonthFor(getEonAndYear(), getMonth())));
        i2 = 1;
      } else {
        break;
      } 
      int i3 = getMonth() + i2;
      int i4 = (i3 - 1) % 12;
      if (i4 < 0) {
        i4 = 12 + i4 + 1;
        i5 = (new BigDecimal(i3 - 1)).divide(new BigDecimal(TWELVE), 0).intValue();
      } else {
        i5 = (i3 - 1) / 12;
        i4++;
      } 
      setMonth(i4);
      if (i5 != 0)
        setYear(getEonAndYear().add(BigInteger.valueOf(i5))); 
    } 
    setDay(bigInteger11.intValue());
    for (byte b = 0; b <= 5; b++) {
      if (arrayOfBoolean[b])
        switch (b) {
          case false:
            setYear(-2147483648);
            break;
          case true:
            setMonth(-2147483648);
            break;
          case true:
            setDay(-2147483648);
            break;
          case true:
            setHour(-2147483648, false);
            break;
          case true:
            setMinute(-2147483648);
            break;
          case true:
            setSecond(-2147483648);
            setFractionalSecond(null);
            break;
        }  
    } 
  }
  
  private static int maximumDayInMonthFor(BigInteger paramBigInteger, int paramInt) { return (paramInt != 2) ? daysInMonth[paramInt] : ((paramBigInteger.mod(FOUR_HUNDRED).equals(BigInteger.ZERO) || (!paramBigInteger.mod(HUNDRED).equals(BigInteger.ZERO) && paramBigInteger.mod(FOUR).equals(BigInteger.ZERO))) ? 29 : daysInMonth[paramInt]); }
  
  private static int maximumDayInMonthFor(int paramInt1, int paramInt2) { return (paramInt2 != 2) ? daysInMonth[paramInt2] : ((paramInt1 % 400 == 0 || (paramInt1 % 100 != 0 && paramInt1 % 4 == 0)) ? 29 : daysInMonth[2]); }
  
  public GregorianCalendar toGregorianCalendar() {
    GregorianCalendar gregorianCalendar = null;
    int i = Integer.MIN_VALUE;
    TimeZone timeZone = getTimeZone(-2147483648);
    Locale locale = getDefaultLocale();
    gregorianCalendar = new GregorianCalendar(timeZone, locale);
    gregorianCalendar.clear();
    gregorianCalendar.setGregorianChange(PURE_GREGORIAN_CHANGE);
    BigInteger bigInteger = getEonAndYear();
    if (bigInteger != null) {
      gregorianCalendar.set(0, (bigInteger.signum() == -1) ? 0 : 1);
      gregorianCalendar.set(1, bigInteger.abs().intValue());
    } 
    if (this.month != Integer.MIN_VALUE)
      gregorianCalendar.set(2, this.month - 1); 
    if (this.day != Integer.MIN_VALUE)
      gregorianCalendar.set(5, this.day); 
    if (this.hour != Integer.MIN_VALUE)
      gregorianCalendar.set(11, this.hour); 
    if (this.minute != Integer.MIN_VALUE)
      gregorianCalendar.set(12, this.minute); 
    if (this.second != Integer.MIN_VALUE)
      gregorianCalendar.set(13, this.second); 
    if (this.fractionalSecond != null)
      gregorianCalendar.set(14, getMillisecond()); 
    return gregorianCalendar;
  }
  
  private Locale getDefaultLocale() {
    String str1 = SecuritySupport.getSystemProperty("user.language.format");
    String str2 = SecuritySupport.getSystemProperty("user.country.format");
    String str3 = SecuritySupport.getSystemProperty("user.variant.format");
    Locale locale = null;
    if (str1 != null)
      if (str2 != null) {
        if (str3 != null) {
          locale = new Locale(str1, str2, str3);
        } else {
          locale = new Locale(str1, str2);
        } 
      } else {
        locale = new Locale(str1);
      }  
    if (locale == null)
      locale = Locale.getDefault(); 
    return locale;
  }
  
  public GregorianCalendar toGregorianCalendar(TimeZone paramTimeZone, Locale paramLocale, XMLGregorianCalendar paramXMLGregorianCalendar) {
    GregorianCalendar gregorianCalendar = null;
    TimeZone timeZone = paramTimeZone;
    if (timeZone == null) {
      int i = Integer.MIN_VALUE;
      if (paramXMLGregorianCalendar != null)
        i = paramXMLGregorianCalendar.getTimezone(); 
      timeZone = getTimeZone(i);
    } 
    if (paramLocale == null)
      paramLocale = Locale.getDefault(); 
    gregorianCalendar = new GregorianCalendar(timeZone, paramLocale);
    gregorianCalendar.clear();
    gregorianCalendar.setGregorianChange(PURE_GREGORIAN_CHANGE);
    BigInteger bigInteger = getEonAndYear();
    if (bigInteger != null) {
      gregorianCalendar.set(0, (bigInteger.signum() == -1) ? 0 : 1);
      gregorianCalendar.set(1, bigInteger.abs().intValue());
    } else {
      BigInteger bigInteger1 = (paramXMLGregorianCalendar != null) ? paramXMLGregorianCalendar.getEonAndYear() : null;
      if (bigInteger1 != null) {
        gregorianCalendar.set(0, (bigInteger1.signum() == -1) ? 0 : 1);
        gregorianCalendar.set(1, bigInteger1.abs().intValue());
      } 
    } 
    if (this.month != Integer.MIN_VALUE) {
      gregorianCalendar.set(2, this.month - 1);
    } else {
      int i = (paramXMLGregorianCalendar != null) ? paramXMLGregorianCalendar.getMonth() : Integer.MIN_VALUE;
      if (i != Integer.MIN_VALUE)
        gregorianCalendar.set(2, i - 1); 
    } 
    if (this.day != Integer.MIN_VALUE) {
      gregorianCalendar.set(5, this.day);
    } else {
      int i = (paramXMLGregorianCalendar != null) ? paramXMLGregorianCalendar.getDay() : Integer.MIN_VALUE;
      if (i != Integer.MIN_VALUE)
        gregorianCalendar.set(5, i); 
    } 
    if (this.hour != Integer.MIN_VALUE) {
      gregorianCalendar.set(11, this.hour);
    } else {
      int i = (paramXMLGregorianCalendar != null) ? paramXMLGregorianCalendar.getHour() : Integer.MIN_VALUE;
      if (i != Integer.MIN_VALUE)
        gregorianCalendar.set(11, i); 
    } 
    if (this.minute != Integer.MIN_VALUE) {
      gregorianCalendar.set(12, this.minute);
    } else {
      int i = (paramXMLGregorianCalendar != null) ? paramXMLGregorianCalendar.getMinute() : Integer.MIN_VALUE;
      if (i != Integer.MIN_VALUE)
        gregorianCalendar.set(12, i); 
    } 
    if (this.second != Integer.MIN_VALUE) {
      gregorianCalendar.set(13, this.second);
    } else {
      int i = (paramXMLGregorianCalendar != null) ? paramXMLGregorianCalendar.getSecond() : Integer.MIN_VALUE;
      if (i != Integer.MIN_VALUE)
        gregorianCalendar.set(13, i); 
    } 
    if (this.fractionalSecond != null) {
      gregorianCalendar.set(14, getMillisecond());
    } else {
      BigDecimal bigDecimal = (paramXMLGregorianCalendar != null) ? paramXMLGregorianCalendar.getFractionalSecond() : null;
      if (bigDecimal != null)
        gregorianCalendar.set(14, paramXMLGregorianCalendar.getMillisecond()); 
    } 
    return gregorianCalendar;
  }
  
  public TimeZone getTimeZone(int paramInt) {
    TimeZone timeZone = null;
    int i = getTimezone();
    if (i == Integer.MIN_VALUE)
      i = paramInt; 
    if (i == Integer.MIN_VALUE) {
      timeZone = TimeZone.getDefault();
    } else {
      char c = (i < 0) ? '-' : '+';
      if (c == '-')
        i = -i; 
      int j = i / 60;
      int k = i - j * 60;
      StringBuffer stringBuffer = new StringBuffer(8);
      stringBuffer.append("GMT");
      stringBuffer.append(c);
      stringBuffer.append(j);
      if (k != 0) {
        if (k < 10)
          stringBuffer.append('0'); 
        stringBuffer.append(k);
      } 
      timeZone = TimeZone.getTimeZone(stringBuffer.toString());
    } 
    return timeZone;
  }
  
  public Object clone() { return new XMLGregorianCalendarImpl(getEonAndYear(), this.month, this.day, this.hour, this.minute, this.second, this.fractionalSecond, this.timezone); }
  
  public void clear() {
    this.eon = null;
    this.year = Integer.MIN_VALUE;
    this.month = Integer.MIN_VALUE;
    this.day = Integer.MIN_VALUE;
    this.timezone = Integer.MIN_VALUE;
    this.hour = Integer.MIN_VALUE;
    this.minute = Integer.MIN_VALUE;
    this.second = Integer.MIN_VALUE;
    this.fractionalSecond = null;
  }
  
  public void setMillisecond(int paramInt) {
    if (paramInt == Integer.MIN_VALUE) {
      this.fractionalSecond = null;
    } else {
      if ((paramInt < 0 || 999 < paramInt) && paramInt != Integer.MIN_VALUE)
        invalidFieldValue(6, paramInt); 
      this.fractionalSecond = (new BigDecimal(paramInt)).movePointLeft(3);
    } 
  }
  
  public void setFractionalSecond(BigDecimal paramBigDecimal) {
    if (paramBigDecimal != null && (paramBigDecimal.compareTo(DECIMAL_ZERO) < 0 || paramBigDecimal.compareTo(DECIMAL_ONE) > 0))
      throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "InvalidFractional", new Object[] { paramBigDecimal.toString() })); 
    this.fractionalSecond = paramBigDecimal;
  }
  
  private static boolean isDigit(char paramChar) { return ('0' <= paramChar && paramChar <= '9'); }
  
  private String format(String paramString) {
    char[] arrayOfChar = new char[32];
    int i = 0;
    byte b = 0;
    int j = paramString.length();
    while (b < j) {
      int m;
      char[] arrayOfChar1;
      String str;
      int k;
      char c = paramString.charAt(b++);
      if (c != '%') {
        arrayOfChar[i++] = c;
        continue;
      } 
      switch (paramString.charAt(b++)) {
        case 'Y':
          if (this.eon == null) {
            int n = getYear();
            if (n < 0) {
              arrayOfChar[i++] = '-';
              n = -n;
            } 
            i = print4Number(arrayOfChar, i, n);
            continue;
          } 
          str = getEonAndYear().toString();
          arrayOfChar1 = new char[arrayOfChar.length + str.length()];
          System.arraycopy(arrayOfChar, 0, arrayOfChar1, 0, i);
          arrayOfChar = arrayOfChar1;
          for (m = str.length(); m < 4; m++)
            arrayOfChar[i++] = '0'; 
          str.getChars(0, str.length(), arrayOfChar, i);
          i += str.length();
          continue;
        case 'M':
          i = print2Number(arrayOfChar, i, getMonth());
          continue;
        case 'D':
          i = print2Number(arrayOfChar, i, getDay());
          continue;
        case 'h':
          i = print2Number(arrayOfChar, i, getHour());
          continue;
        case 'm':
          i = print2Number(arrayOfChar, i, getMinute());
          continue;
        case 's':
          i = print2Number(arrayOfChar, i, getSecond());
          if (getFractionalSecond() != null) {
            str = getFractionalSecond().toString();
            int n = str.indexOf("E-");
            if (n >= 0) {
              String str1 = str.substring(n + 2);
              str = str.substring(0, n);
              n = str.indexOf(".");
              if (n >= 0)
                str = str.substring(0, n) + str.substring(n + 1); 
              int i1 = Integer.parseInt(str1);
              if (i1 < 40) {
                str = "00000000000000000000000000000000000000000".substring(0, i1 - 1) + str;
              } else {
                while (i1 > 1) {
                  str = "0" + str;
                  i1--;
                } 
              } 
              str = "0." + str;
            } 
            char[] arrayOfChar2 = new char[arrayOfChar.length + str.length()];
            System.arraycopy(arrayOfChar, 0, arrayOfChar2, 0, i);
            arrayOfChar = arrayOfChar2;
            str.getChars(1, str.length(), arrayOfChar, i);
            i += str.length() - 1;
          } 
          continue;
        case 'z':
          k = getTimezone();
          if (k == 0) {
            arrayOfChar[i++] = 'Z';
            continue;
          } 
          if (k != Integer.MIN_VALUE) {
            if (k < 0) {
              arrayOfChar[i++] = '-';
              k *= -1;
            } else {
              arrayOfChar[i++] = '+';
            } 
            i = print2Number(arrayOfChar, i, k / 60);
            arrayOfChar[i++] = ':';
            i = print2Number(arrayOfChar, i, k % 60);
          } 
          continue;
      } 
      throw new InternalError();
    } 
    return new String(arrayOfChar, 0, i);
  }
  
  private int print2Number(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    paramArrayOfChar[paramInt1++] = (char)(48 + paramInt2 / 10);
    paramArrayOfChar[paramInt1++] = (char)(48 + paramInt2 % 10);
    return paramInt1;
  }
  
  private int print4Number(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    paramArrayOfChar[paramInt1 + 3] = (char)(48 + paramInt2 % 10);
    paramInt2 /= 10;
    paramArrayOfChar[paramInt1 + 2] = (char)(48 + paramInt2 % 10);
    paramInt2 /= 10;
    paramArrayOfChar[paramInt1 + 1] = (char)(48 + paramInt2 % 10);
    paramInt2 /= 10;
    paramArrayOfChar[paramInt1] = (char)(48 + paramInt2 % 10);
    return paramInt1 + 4;
  }
  
  static BigInteger sanitize(Number paramNumber, int paramInt) { return (paramInt == 0 || paramNumber == null) ? BigInteger.ZERO : ((paramInt < 0) ? ((BigInteger)paramNumber).negate() : (BigInteger)paramNumber); }
  
  public void reset() {}
  
  private final class Parser {
    private final String format;
    
    private final String value;
    
    private final int flen;
    
    private final int vlen;
    
    private int fidx;
    
    private int vidx;
    
    private Parser(String param1String1, String param1String2) {
      this.format = param1String1;
      this.value = param1String2;
      this.flen = param1String1.length();
      this.vlen = param1String2.length();
    }
    
    public void parse() {
      while (this.fidx < this.flen) {
        char c2;
        char c1 = this.format.charAt(this.fidx++);
        if (c1 != '%') {
          skip(c1);
          continue;
        } 
        switch (this.format.charAt(this.fidx++)) {
          case 'Y':
            parseAndSetYear(4);
            continue;
          case 'M':
            XMLGregorianCalendarImpl.this.setMonth(parseInt(2, 2));
            continue;
          case 'D':
            XMLGregorianCalendarImpl.this.setDay(parseInt(2, 2));
            continue;
          case 'h':
            XMLGregorianCalendarImpl.this.setHour(parseInt(2, 2), false);
            continue;
          case 'm':
            XMLGregorianCalendarImpl.this.setMinute(parseInt(2, 2));
            continue;
          case 's':
            XMLGregorianCalendarImpl.this.setSecond(parseInt(2, 2));
            if (peek() == '.')
              XMLGregorianCalendarImpl.this.setFractionalSecond(parseBigDecimal()); 
            continue;
          case 'z':
            c2 = peek();
            if (c2 == 'Z') {
              this.vidx++;
              XMLGregorianCalendarImpl.this.setTimezone(0);
              continue;
            } 
            if (c2 == '+' || c2 == '-') {
              this.vidx++;
              int i = parseInt(2, 2);
              skip(':');
              int j = parseInt(2, 2);
              XMLGregorianCalendarImpl.this.setTimezone((i * 60 + j) * ((c2 == '+') ? 1 : -1));
            } 
            continue;
        } 
        throw new InternalError();
      } 
      if (this.vidx != this.vlen)
        throw new IllegalArgumentException(this.value); 
      XMLGregorianCalendarImpl.this.testHour();
    }
    
    private char peek() throws IllegalArgumentException { return (this.vidx == this.vlen) ? Character.MAX_VALUE : this.value.charAt(this.vidx); }
    
    private char read() throws IllegalArgumentException {
      if (this.vidx == this.vlen)
        throw new IllegalArgumentException(this.value); 
      return this.value.charAt(this.vidx++);
    }
    
    private void skip(char param1Char) throws IllegalArgumentException {
      if (read() != param1Char)
        throw new IllegalArgumentException(this.value); 
    }
    
    private int parseInt(int param1Int1, int param1Int2) {
      char c = Character.MIN_VALUE;
      int i = this.vidx;
      char c1;
      while (XMLGregorianCalendarImpl.isDigit(c1 = peek()) && this.vidx - i <= param1Int2) {
        this.vidx++;
        c = c * 10 + c1 - '0';
      } 
      if (this.vidx - i < param1Int1)
        throw new IllegalArgumentException(this.value); 
      return c;
    }
    
    private void parseAndSetYear(int param1Int) {
      int i = this.vidx;
      char c = Character.MIN_VALUE;
      boolean bool = false;
      if (peek() == '-') {
        this.vidx++;
        bool = true;
      } 
      while (true) {
        char c1 = peek();
        if (!XMLGregorianCalendarImpl.isDigit(c1))
          break; 
        this.vidx++;
        c = c * 10 + c1 - '0';
      } 
      if (this.vidx - i < param1Int)
        throw new IllegalArgumentException(this.value); 
      if (this.vidx - i < 7) {
        if (bool)
          c = -c; 
        XMLGregorianCalendarImpl.this.year = c;
        XMLGregorianCalendarImpl.this.eon = null;
      } else {
        XMLGregorianCalendarImpl.this.setYear(new BigInteger(this.value.substring(i, this.vidx)));
      } 
    }
    
    private BigDecimal parseBigDecimal() {
      int i = this.vidx;
      if (peek() == '.') {
        this.vidx++;
      } else {
        throw new IllegalArgumentException(this.value);
      } 
      while (XMLGregorianCalendarImpl.isDigit(peek()))
        this.vidx++; 
      return new BigDecimal(this.value.substring(i, this.vidx));
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\datatype\XMLGregorianCalendarImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */