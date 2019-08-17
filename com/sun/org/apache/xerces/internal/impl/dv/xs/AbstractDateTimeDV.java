package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.jaxp.datatype.DatatypeFactoryImpl;
import com.sun.org.apache.xerces.internal.xs.datatypes.XSDateTime;
import java.math.BigDecimal;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

public abstract class AbstractDateTimeDV extends TypeValidator {
  private static final boolean DEBUG = false;
  
  protected static final int YEAR = 2000;
  
  protected static final int MONTH = 1;
  
  protected static final int DAY = 1;
  
  protected static final DatatypeFactory datatypeFactory = new DatatypeFactoryImpl();
  
  public short getAllowedFacets() { return 2552; }
  
  public boolean isIdentical(Object paramObject1, Object paramObject2) {
    if (!(paramObject1 instanceof DateTimeData) || !(paramObject2 instanceof DateTimeData))
      return false; 
    DateTimeData dateTimeData1 = (DateTimeData)paramObject1;
    DateTimeData dateTimeData2 = (DateTimeData)paramObject2;
    return (dateTimeData1.timezoneHr == dateTimeData2.timezoneHr && dateTimeData1.timezoneMin == dateTimeData2.timezoneMin) ? dateTimeData1.equals(dateTimeData2) : 0;
  }
  
  public int compare(Object paramObject1, Object paramObject2) { return compareDates((DateTimeData)paramObject1, (DateTimeData)paramObject2, true); }
  
  protected short compareDates(DateTimeData paramDateTimeData1, DateTimeData paramDateTimeData2, boolean paramBoolean) {
    if (paramDateTimeData1.utc == paramDateTimeData2.utc)
      return compareOrder(paramDateTimeData1, paramDateTimeData2); 
    DateTimeData dateTimeData = new DateTimeData(null, this);
    if (paramDateTimeData1.utc == 90) {
      cloneDate(paramDateTimeData2, dateTimeData);
      dateTimeData.timezoneHr = 14;
      dateTimeData.timezoneMin = 0;
      dateTimeData.utc = 43;
      normalize(dateTimeData);
      short s1 = compareOrder(paramDateTimeData1, dateTimeData);
      if (s1 == -1)
        return s1; 
      cloneDate(paramDateTimeData2, dateTimeData);
      dateTimeData.timezoneHr = -14;
      dateTimeData.timezoneMin = 0;
      dateTimeData.utc = 45;
      normalize(dateTimeData);
      short s2 = compareOrder(paramDateTimeData1, dateTimeData);
      return (s2 == 1) ? s2 : 2;
    } 
    if (paramDateTimeData2.utc == 90) {
      cloneDate(paramDateTimeData1, dateTimeData);
      dateTimeData.timezoneHr = -14;
      dateTimeData.timezoneMin = 0;
      dateTimeData.utc = 45;
      normalize(dateTimeData);
      short s1 = compareOrder(dateTimeData, paramDateTimeData2);
      if (s1 == -1)
        return s1; 
      cloneDate(paramDateTimeData1, dateTimeData);
      dateTimeData.timezoneHr = 14;
      dateTimeData.timezoneMin = 0;
      dateTimeData.utc = 43;
      normalize(dateTimeData);
      short s2 = compareOrder(dateTimeData, paramDateTimeData2);
      return (s2 == 1) ? s2 : 2;
    } 
    return 2;
  }
  
  protected short compareOrder(DateTimeData paramDateTimeData1, DateTimeData paramDateTimeData2) {
    if (paramDateTimeData1.position < 1) {
      if (paramDateTimeData1.year < paramDateTimeData2.year)
        return -1; 
      if (paramDateTimeData1.year > paramDateTimeData2.year)
        return 1; 
    } 
    if (paramDateTimeData1.position < 2) {
      if (paramDateTimeData1.month < paramDateTimeData2.month)
        return -1; 
      if (paramDateTimeData1.month > paramDateTimeData2.month)
        return 1; 
    } 
    return (paramDateTimeData1.day < paramDateTimeData2.day) ? -1 : ((paramDateTimeData1.day > paramDateTimeData2.day) ? 1 : ((paramDateTimeData1.hour < paramDateTimeData2.hour) ? -1 : ((paramDateTimeData1.hour > paramDateTimeData2.hour) ? 1 : ((paramDateTimeData1.minute < paramDateTimeData2.minute) ? -1 : ((paramDateTimeData1.minute > paramDateTimeData2.minute) ? 1 : ((paramDateTimeData1.second < paramDateTimeData2.second) ? -1 : ((paramDateTimeData1.second > paramDateTimeData2.second) ? 1 : ((paramDateTimeData1.utc < paramDateTimeData2.utc) ? -1 : ((paramDateTimeData1.utc > paramDateTimeData2.utc) ? 1 : 0)))))))));
  }
  
  protected void getTime(String paramString, int paramInt1, int paramInt2, DateTimeData paramDateTimeData) throws RuntimeException {
    int i = paramInt1 + 2;
    paramDateTimeData.hour = parseInt(paramString, paramInt1, i);
    if (paramString.charAt(i++) != ':')
      throw new RuntimeException("Error in parsing time zone"); 
    paramInt1 = i;
    i += 2;
    paramDateTimeData.minute = parseInt(paramString, paramInt1, i);
    if (paramString.charAt(i++) != ':')
      throw new RuntimeException("Error in parsing time zone"); 
    int j = findUTCSign(paramString, paramInt1, paramInt2);
    paramInt1 = i;
    i = (j < 0) ? paramInt2 : j;
    paramDateTimeData.second = parseSecond(paramString, paramInt1, i);
    if (j > 0)
      getTimeZone(paramString, paramDateTimeData, j, paramInt2); 
  }
  
  protected int getDate(String paramString, int paramInt1, int paramInt2, DateTimeData paramDateTimeData) throws RuntimeException {
    paramInt1 = getYearMonth(paramString, paramInt1, paramInt2, paramDateTimeData);
    if (paramString.charAt(paramInt1++) != '-')
      throw new RuntimeException("CCYY-MM must be followed by '-' sign"); 
    int i = paramInt1 + 2;
    paramDateTimeData.day = parseInt(paramString, paramInt1, i);
    return i;
  }
  
  protected int getYearMonth(String paramString, int paramInt1, int paramInt2, DateTimeData paramDateTimeData) throws RuntimeException {
    if (paramString.charAt(0) == '-')
      paramInt1++; 
    int i = indexOf(paramString, paramInt1, paramInt2, '-');
    if (i == -1)
      throw new RuntimeException("Year separator is missing or misplaced"); 
    int j = i - paramInt1;
    if (j < 4)
      throw new RuntimeException("Year must have 'CCYY' format"); 
    if (j > 4 && paramString.charAt(paramInt1) == '0')
      throw new RuntimeException("Leading zeros are required if the year value would otherwise have fewer than four digits; otherwise they are forbidden"); 
    paramDateTimeData.year = parseIntYear(paramString, i);
    if (paramString.charAt(i) != '-')
      throw new RuntimeException("CCYY must be followed by '-' sign"); 
    paramInt1 = ++i;
    i = paramInt1 + 2;
    paramDateTimeData.month = parseInt(paramString, paramInt1, i);
    return i;
  }
  
  protected void parseTimeZone(String paramString, int paramInt1, int paramInt2, DateTimeData paramDateTimeData) throws RuntimeException {
    if (paramInt1 < paramInt2) {
      if (!isNextCharUTCSign(paramString, paramInt1, paramInt2))
        throw new RuntimeException("Error in month parsing"); 
      getTimeZone(paramString, paramDateTimeData, paramInt1, paramInt2);
    } 
  }
  
  protected void getTimeZone(String paramString, DateTimeData paramDateTimeData, int paramInt1, int paramInt2) throws RuntimeException {
    paramDateTimeData.utc = paramString.charAt(paramInt1);
    if (paramString.charAt(paramInt1) == 'Z') {
      if (paramInt2 > ++paramInt1)
        throw new RuntimeException("Error in parsing time zone"); 
      return;
    } 
    if (paramInt1 <= paramInt2 - 6) {
      int i = (paramString.charAt(paramInt1) == '-') ? -1 : 1;
      int j = ++paramInt1 + 2;
      paramDateTimeData.timezoneHr = i * parseInt(paramString, paramInt1, j);
      if (paramString.charAt(j++) != ':')
        throw new RuntimeException("Error in parsing time zone"); 
      paramDateTimeData.timezoneMin = i * parseInt(paramString, j, j + 2);
      if (j + 2 != paramInt2)
        throw new RuntimeException("Error in parsing time zone"); 
      if (paramDateTimeData.timezoneHr != 0 || paramDateTimeData.timezoneMin != 0)
        paramDateTimeData.normalized = false; 
    } else {
      throw new RuntimeException("Error in parsing time zone");
    } 
  }
  
  protected int indexOf(String paramString, int paramInt1, int paramInt2, char paramChar) {
    for (int i = paramInt1; i < paramInt2; i++) {
      if (paramString.charAt(i) == paramChar)
        return i; 
    } 
    return -1;
  }
  
  protected void validateDateTime(DateTimeData paramDateTimeData) {
    if (paramDateTimeData.year == 0)
      throw new RuntimeException("The year \"0000\" is an illegal year value"); 
    if (paramDateTimeData.month < 1 || paramDateTimeData.month > 12)
      throw new RuntimeException("The month must have values 1 to 12"); 
    if (paramDateTimeData.day > maxDayInMonthFor(paramDateTimeData.year, paramDateTimeData.month) || paramDateTimeData.day < 1)
      throw new RuntimeException("The day must have values 1 to 31"); 
    if (paramDateTimeData.hour > 23 || paramDateTimeData.hour < 0)
      if (paramDateTimeData.hour == 24 && paramDateTimeData.minute == 0 && paramDateTimeData.second == 0.0D) {
        paramDateTimeData.hour = 0;
        paramDateTimeData.day = 1;
        paramDateTimeData.month = 1;
        if (++paramDateTimeData.day > maxDayInMonthFor(paramDateTimeData.year, paramDateTimeData.month) && ++paramDateTimeData.month > 12 && ++paramDateTimeData.year == 0)
          paramDateTimeData.year = 1; 
      } else {
        throw new RuntimeException("Hour must have values 0-23, unless 24:00:00");
      }  
    if (paramDateTimeData.minute > 59 || paramDateTimeData.minute < 0)
      throw new RuntimeException("Minute must have values 0-59"); 
    if (paramDateTimeData.second >= 60.0D || paramDateTimeData.second < 0.0D)
      throw new RuntimeException("Second must have values 0-59"); 
    if (paramDateTimeData.timezoneHr > 14 || paramDateTimeData.timezoneHr < -14)
      throw new RuntimeException("Time zone should have range -14:00 to +14:00"); 
    if ((paramDateTimeData.timezoneHr == 14 || paramDateTimeData.timezoneHr == -14) && paramDateTimeData.timezoneMin != 0)
      throw new RuntimeException("Time zone should have range -14:00 to +14:00"); 
    if (paramDateTimeData.timezoneMin > 59 || paramDateTimeData.timezoneMin < -59)
      throw new RuntimeException("Minute must have values 0-59"); 
  }
  
  protected int findUTCSign(String paramString, int paramInt1, int paramInt2) {
    for (int i = paramInt1; i < paramInt2; i++) {
      char c = paramString.charAt(i);
      if (c == 'Z' || c == '+' || c == '-')
        return i; 
    } 
    return -1;
  }
  
  protected final boolean isNextCharUTCSign(String paramString, int paramInt1, int paramInt2) {
    if (paramInt1 < paramInt2) {
      char c = paramString.charAt(paramInt1);
      return (c == 'Z' || c == '+' || c == '-');
    } 
    return false;
  }
  
  protected int parseInt(String paramString, int paramInt1, int paramInt2) {
    int i = 10;
    int j = 0;
    int k = 0;
    int m = -2147483647;
    int n = m / i;
    int i1 = paramInt1;
    do {
      k = getDigit(paramString.charAt(i1));
      if (k < 0)
        throw new NumberFormatException("'" + paramString + "' has wrong format"); 
      if (j < n)
        throw new NumberFormatException("'" + paramString + "' has wrong format"); 
      j *= i;
      if (j < m + k)
        throw new NumberFormatException("'" + paramString + "' has wrong format"); 
      j -= k;
    } while (++i1 < paramInt2);
    return -j;
  }
  
  protected int parseIntYear(String paramString, int paramInt) {
    int k;
    int i = 10;
    int j = 0;
    boolean bool = false;
    byte b = 0;
    int n = 0;
    if (paramString.charAt(0) == '-') {
      bool = true;
      k = Integer.MIN_VALUE;
      b++;
    } else {
      k = -2147483647;
    } 
    int m = k / i;
    while (b < paramInt) {
      n = getDigit(paramString.charAt(b++));
      if (n < 0)
        throw new NumberFormatException("'" + paramString + "' has wrong format"); 
      if (j < m)
        throw new NumberFormatException("'" + paramString + "' has wrong format"); 
      j *= i;
      if (j < k + n)
        throw new NumberFormatException("'" + paramString + "' has wrong format"); 
      j -= n;
    } 
    if (bool) {
      if (b > 1)
        return j; 
      throw new NumberFormatException("'" + paramString + "' has wrong format");
    } 
    return -j;
  }
  
  protected void normalize(DateTimeData paramDateTimeData) {
    int i = -1;
    int j = paramDateTimeData.minute + i * paramDateTimeData.timezoneMin;
    int k = fQuotient(j, 60);
    paramDateTimeData.minute = mod(j, 60, k);
    j = paramDateTimeData.hour + i * paramDateTimeData.timezoneHr + k;
    k = fQuotient(j, 24);
    paramDateTimeData.hour = mod(j, 24, k);
    paramDateTimeData.day += k;
    while (true) {
      j = maxDayInMonthFor(paramDateTimeData.year, paramDateTimeData.month);
      if (paramDateTimeData.day < 1) {
        paramDateTimeData.day += maxDayInMonthFor(paramDateTimeData.year, paramDateTimeData.month - 1);
        k = -1;
      } else if (paramDateTimeData.day > j) {
        paramDateTimeData.day -= j;
        k = 1;
      } else {
        break;
      } 
      j = paramDateTimeData.month + k;
      paramDateTimeData.month = modulo(j, 1, 13);
      paramDateTimeData.year += fQuotient(j, 1, 13);
      if (paramDateTimeData.year == 0)
        paramDateTimeData.year = (paramDateTimeData.timezoneHr < 0 || paramDateTimeData.timezoneMin < 0) ? 1 : -1; 
    } 
    paramDateTimeData.utc = 90;
  }
  
  protected void saveUnnormalized(DateTimeData paramDateTimeData) {
    paramDateTimeData.unNormYear = paramDateTimeData.year;
    paramDateTimeData.unNormMonth = paramDateTimeData.month;
    paramDateTimeData.unNormDay = paramDateTimeData.day;
    paramDateTimeData.unNormHour = paramDateTimeData.hour;
    paramDateTimeData.unNormMinute = paramDateTimeData.minute;
    paramDateTimeData.unNormSecond = paramDateTimeData.second;
  }
  
  protected void resetDateObj(DateTimeData paramDateTimeData) {
    paramDateTimeData.year = 0;
    paramDateTimeData.month = 0;
    paramDateTimeData.day = 0;
    paramDateTimeData.hour = 0;
    paramDateTimeData.minute = 0;
    paramDateTimeData.second = 0.0D;
    paramDateTimeData.utc = 0;
    paramDateTimeData.timezoneHr = 0;
    paramDateTimeData.timezoneMin = 0;
  }
  
  protected int maxDayInMonthFor(int paramInt1, int paramInt2) { return (paramInt2 == 4 || paramInt2 == 6 || paramInt2 == 9 || paramInt2 == 11) ? 30 : ((paramInt2 == 2) ? (isLeapYear(paramInt1) ? 29 : 28) : 31); }
  
  private boolean isLeapYear(int paramInt) { return (paramInt % 4 == 0 && (paramInt % 100 != 0 || paramInt % 400 == 0)); }
  
  protected int mod(int paramInt1, int paramInt2, int paramInt3) { return paramInt1 - paramInt3 * paramInt2; }
  
  protected int fQuotient(int paramInt1, int paramInt2) { return (int)Math.floor((paramInt1 / paramInt2)); }
  
  protected int modulo(int paramInt1, int paramInt2, int paramInt3) {
    int i = paramInt1 - paramInt2;
    int j = paramInt3 - paramInt2;
    return mod(i, j, fQuotient(i, j)) + paramInt2;
  }
  
  protected int fQuotient(int paramInt1, int paramInt2, int paramInt3) { return fQuotient(paramInt1 - paramInt2, paramInt3 - paramInt2); }
  
  protected String dateToString(DateTimeData paramDateTimeData) {
    StringBuffer stringBuffer = new StringBuffer(25);
    append(stringBuffer, paramDateTimeData.year, 4);
    stringBuffer.append('-');
    append(stringBuffer, paramDateTimeData.month, 2);
    stringBuffer.append('-');
    append(stringBuffer, paramDateTimeData.day, 2);
    stringBuffer.append('T');
    append(stringBuffer, paramDateTimeData.hour, 2);
    stringBuffer.append(':');
    append(stringBuffer, paramDateTimeData.minute, 2);
    stringBuffer.append(':');
    append(stringBuffer, paramDateTimeData.second);
    append(stringBuffer, (char)paramDateTimeData.utc, 0);
    return stringBuffer.toString();
  }
  
  protected final void append(StringBuffer paramStringBuffer, int paramInt1, int paramInt2) {
    if (paramInt1 == Integer.MIN_VALUE) {
      paramStringBuffer.append(paramInt1);
      return;
    } 
    if (paramInt1 < 0) {
      paramStringBuffer.append('-');
      paramInt1 = -paramInt1;
    } 
    if (paramInt2 == 4) {
      if (paramInt1 < 10) {
        paramStringBuffer.append("000");
      } else if (paramInt1 < 100) {
        paramStringBuffer.append("00");
      } else if (paramInt1 < 1000) {
        paramStringBuffer.append('0');
      } 
      paramStringBuffer.append(paramInt1);
    } else if (paramInt2 == 2) {
      if (paramInt1 < 10)
        paramStringBuffer.append('0'); 
      paramStringBuffer.append(paramInt1);
    } else if (paramInt1 != 0) {
      paramStringBuffer.append((char)paramInt1);
    } 
  }
  
  protected final void append(StringBuffer paramStringBuffer, double paramDouble) {
    if (paramDouble < 0.0D) {
      paramStringBuffer.append('-');
      paramDouble = -paramDouble;
    } 
    if (paramDouble < 10.0D)
      paramStringBuffer.append('0'); 
    append2(paramStringBuffer, paramDouble);
  }
  
  protected final void append2(StringBuffer paramStringBuffer, double paramDouble) {
    int i = (int)paramDouble;
    if (paramDouble == i) {
      paramStringBuffer.append(i);
    } else {
      append3(paramStringBuffer, paramDouble);
    } 
  }
  
  private void append3(StringBuffer paramStringBuffer, double paramDouble) {
    String str = String.valueOf(paramDouble);
    int i = str.indexOf('E');
    if (i == -1) {
      paramStringBuffer.append(str);
      return;
    } 
    if (paramDouble < 1.0D) {
      int j;
      try {
        j = parseInt(str, i + 2, str.length());
      } catch (Exception exception) {
        paramStringBuffer.append(str);
        return;
      } 
      paramStringBuffer.append("0.");
      int k;
      for (k = 1; k < j; k++)
        paramStringBuffer.append('0'); 
      for (k = i - 1; k > 0; k--) {
        char c = str.charAt(k);
        if (c != '0')
          break; 
      } 
      for (byte b = 0; b <= k; b++) {
        char c = str.charAt(b);
        if (c != '.')
          paramStringBuffer.append(c); 
      } 
    } else {
      int j;
      try {
        j = parseInt(str, i + 1, str.length());
      } catch (Exception exception) {
        paramStringBuffer.append(str);
        return;
      } 
      int k = j + 2;
      int m;
      for (m = 0; m < i; m++) {
        char c = str.charAt(m);
        if (c != '.') {
          if (m == k)
            paramStringBuffer.append('.'); 
          paramStringBuffer.append(c);
        } 
      } 
      for (m = k - i; m > 0; m--)
        paramStringBuffer.append('0'); 
    } 
  }
  
  protected double parseSecond(String paramString, int paramInt1, int paramInt2) throws NumberFormatException {
    int i = -1;
    for (int j = paramInt1; j < paramInt2; j++) {
      char c = paramString.charAt(j);
      if (c == '.') {
        i = j;
      } else if (c > '9' || c < '0') {
        throw new NumberFormatException("'" + paramString + "' has wrong format");
      } 
    } 
    if (i == -1) {
      if (paramInt1 + 2 != paramInt2)
        throw new NumberFormatException("'" + paramString + "' has wrong format"); 
    } else if (paramInt1 + 2 != i || i + 1 == paramInt2) {
      throw new NumberFormatException("'" + paramString + "' has wrong format");
    } 
    return Double.parseDouble(paramString.substring(paramInt1, paramInt2));
  }
  
  private void cloneDate(DateTimeData paramDateTimeData1, DateTimeData paramDateTimeData2) {
    paramDateTimeData2.year = paramDateTimeData1.year;
    paramDateTimeData2.month = paramDateTimeData1.month;
    paramDateTimeData2.day = paramDateTimeData1.day;
    paramDateTimeData2.hour = paramDateTimeData1.hour;
    paramDateTimeData2.minute = paramDateTimeData1.minute;
    paramDateTimeData2.second = paramDateTimeData1.second;
    paramDateTimeData2.utc = paramDateTimeData1.utc;
    paramDateTimeData2.timezoneHr = paramDateTimeData1.timezoneHr;
    paramDateTimeData2.timezoneMin = paramDateTimeData1.timezoneMin;
  }
  
  protected XMLGregorianCalendar getXMLGregorianCalendar(DateTimeData paramDateTimeData) { return null; }
  
  protected Duration getDuration(DateTimeData paramDateTimeData) { return null; }
  
  protected final BigDecimal getFractionalSecondsAsBigDecimal(DateTimeData paramDateTimeData) {
    StringBuffer stringBuffer = new StringBuffer();
    append3(stringBuffer, paramDateTimeData.unNormSecond);
    String str = stringBuffer.toString();
    int i = str.indexOf('.');
    if (i == -1)
      return null; 
    str = str.substring(i);
    BigDecimal bigDecimal;
    return (bigDecimal.compareTo((bigDecimal = new BigDecimal(str)).valueOf(0L)) == 0) ? null : bigDecimal;
  }
  
  static final class DateTimeData implements XSDateTime {
    int year;
    
    int month;
    
    int day;
    
    int hour;
    
    int minute;
    
    int utc;
    
    double second;
    
    int timezoneHr;
    
    int timezoneMin;
    
    private String originalValue;
    
    boolean normalized = true;
    
    int unNormYear;
    
    int unNormMonth;
    
    int unNormDay;
    
    int unNormHour;
    
    int unNormMinute;
    
    double unNormSecond;
    
    int position;
    
    final AbstractDateTimeDV type;
    
    public DateTimeData(String param1String, AbstractDateTimeDV param1AbstractDateTimeDV) {
      this.originalValue = param1String;
      this.type = param1AbstractDateTimeDV;
    }
    
    public DateTimeData(int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, double param1Double, int param1Int6, String param1String, boolean param1Boolean, AbstractDateTimeDV param1AbstractDateTimeDV) {
      this.year = param1Int1;
      this.month = param1Int2;
      this.day = param1Int3;
      this.hour = param1Int4;
      this.minute = param1Int5;
      this.second = param1Double;
      this.utc = param1Int6;
      this.type = param1AbstractDateTimeDV;
      this.originalValue = param1String;
    }
    
    public boolean equals(Object param1Object) { return !(param1Object instanceof DateTimeData) ? false : ((this.type.compareDates(this, (DateTimeData)param1Object, true) == 0)); }
    
    public int hashCode() {
      DateTimeData dateTimeData = new DateTimeData(null, this.type);
      this.type.cloneDate(this, dateTimeData);
      this.type.normalize(dateTimeData);
      return this.type.dateToString(dateTimeData).hashCode();
    }
    
    public String toString() {
      if (this.canonical == null)
        this.canonical = this.type.dateToString(this); 
      return this.canonical;
    }
    
    public int getYears() { return (this.type instanceof DurationDV) ? 0 : (this.normalized ? this.year : this.unNormYear); }
    
    public int getMonths() { return (this.type instanceof DurationDV) ? (this.year * 12 + this.month) : (this.normalized ? this.month : this.unNormMonth); }
    
    public int getDays() { return (this.type instanceof DurationDV) ? 0 : (this.normalized ? this.day : this.unNormDay); }
    
    public int getHours() { return (this.type instanceof DurationDV) ? 0 : (this.normalized ? this.hour : this.unNormHour); }
    
    public int getMinutes() { return (this.type instanceof DurationDV) ? 0 : (this.normalized ? this.minute : this.unNormMinute); }
    
    public double getSeconds() { return (this.type instanceof DurationDV) ? ((this.day * 24 * 60 * 60 + this.hour * 60 * 60 + this.minute * 60) + this.second) : (this.normalized ? this.second : this.unNormSecond); }
    
    public boolean hasTimeZone() { return (this.utc != 0); }
    
    public int getTimeZoneHours() { return this.timezoneHr; }
    
    public int getTimeZoneMinutes() { return this.timezoneMin; }
    
    public String getLexicalValue() { return this.originalValue; }
    
    public XSDateTime normalize() {
      if (!this.normalized) {
        DateTimeData dateTimeData = (DateTimeData)clone();
        dateTimeData.normalized = true;
        return dateTimeData;
      } 
      return this;
    }
    
    public boolean isNormalized() { return this.normalized; }
    
    public Object clone() {
      DateTimeData dateTimeData = new DateTimeData(this.year, this.month, this.day, this.hour, this.minute, this.second, this.utc, this.originalValue, this.normalized, this.type);
      dateTimeData.canonical = this.canonical;
      dateTimeData.position = this.position;
      dateTimeData.timezoneHr = this.timezoneHr;
      dateTimeData.timezoneMin = this.timezoneMin;
      dateTimeData.unNormYear = this.unNormYear;
      dateTimeData.unNormMonth = this.unNormMonth;
      dateTimeData.unNormDay = this.unNormDay;
      dateTimeData.unNormHour = this.unNormHour;
      dateTimeData.unNormMinute = this.unNormMinute;
      dateTimeData.unNormSecond = this.unNormSecond;
      return dateTimeData;
    }
    
    public XMLGregorianCalendar getXMLGregorianCalendar() { return this.type.getXMLGregorianCalendar(this); }
    
    public Duration getDuration() { return this.type.getDuration(this); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\AbstractDateTimeDV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */