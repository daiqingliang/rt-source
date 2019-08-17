package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.time.Instant;
import sun.util.calendar.BaseCalendar;
import sun.util.calendar.CalendarSystem;
import sun.util.calendar.CalendarUtils;
import sun.util.calendar.ZoneInfo;

public class Date extends Object implements Serializable, Cloneable, Comparable<Date> {
  private static final BaseCalendar gcal = CalendarSystem.getGregorianCalendar();
  
  private static BaseCalendar jcal;
  
  private long fastTime;
  
  private BaseCalendar.Date cdate;
  
  private static int defaultCenturyStart;
  
  private static final long serialVersionUID = 7523967970034938905L;
  
  private static final String[] wtb = { 
      "am", "pm", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday", "january", 
      "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", 
      "december", "gmt", "ut", "utc", "est", "edt", "cst", "cdt", "mst", "mdt", 
      "pst", "pdt" };
  
  private static final int[] ttb = { 
      14, 1, 0, 0, 0, 0, 0, 0, 0, 2, 
      3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 
      13, 10000, 10000, 10000, 10300, 10240, 10360, 10300, 10420, 10360, 
      10480, 10420 };
  
  public Date() { this(System.currentTimeMillis()); }
  
  public Date(long paramLong) { this.fastTime = paramLong; }
  
  @Deprecated
  public Date(int paramInt1, int paramInt2, int paramInt3) { this(paramInt1, paramInt2, paramInt3, 0, 0, 0); }
  
  @Deprecated
  public Date(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { this(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, 0); }
  
  @Deprecated
  public Date(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    int i = paramInt1 + 1900;
    if (paramInt2 >= 12) {
      i += paramInt2 / 12;
      paramInt2 %= 12;
    } else if (paramInt2 < 0) {
      i += CalendarUtils.floorDivide(paramInt2, 12);
      paramInt2 = CalendarUtils.mod(paramInt2, 12);
    } 
    BaseCalendar baseCalendar = getCalendarSystem(i);
    this.cdate = (BaseCalendar.Date)baseCalendar.newCalendarDate(TimeZone.getDefaultRef());
    this.cdate.setNormalizedDate(i, paramInt2 + 1, paramInt3).setTimeOfDay(paramInt4, paramInt5, paramInt6, 0);
    getTimeImpl();
    this.cdate = null;
  }
  
  @Deprecated
  public Date(String paramString) { this(parse(paramString)); }
  
  public Object clone() {
    Date date = null;
    try {
      date = (Date)super.clone();
      if (this.cdate != null)
        date.cdate = (BaseCalendar.Date)this.cdate.clone(); 
    } catch (CloneNotSupportedException cloneNotSupportedException) {}
    return date;
  }
  
  @Deprecated
  public static long UTC(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    int i = paramInt1 + 1900;
    if (paramInt2 >= 12) {
      i += paramInt2 / 12;
      paramInt2 %= 12;
    } else if (paramInt2 < 0) {
      i += CalendarUtils.floorDivide(paramInt2, 12);
      paramInt2 = CalendarUtils.mod(paramInt2, 12);
    } 
    int j = paramInt2 + 1;
    BaseCalendar baseCalendar = getCalendarSystem(i);
    BaseCalendar.Date date = (BaseCalendar.Date)baseCalendar.newCalendarDate(null);
    date.setNormalizedDate(i, j, paramInt3).setTimeOfDay(paramInt4, paramInt5, paramInt6, 0);
    Date date1 = new Date(0L);
    date1.normalize(date);
    return date1.fastTime;
  }
  
  @Deprecated
  public static long parse(String paramString) {
    int i = Integer.MIN_VALUE;
    byte b1 = -1;
    byte b2 = -1;
    byte b3 = -1;
    byte b4 = -1;
    byte b5 = -1;
    byte b6 = -1;
    int j = -1;
    byte b = 0;
    int k = -1;
    byte b7 = -1;
    int m = -1;
    int n = 0;
    if (paramString != null) {
      int i1 = paramString.length();
      while (true) {
        int i2;
        if (b < i1) {
          j = paramString.charAt(b);
          b++;
          if (j <= 32 || j == 44)
            continue; 
          if (j == 40) {
            byte b9 = 1;
            while (b < i1) {
              j = paramString.charAt(b);
              b++;
              if (j == 40) {
                b9++;
                continue;
              } 
              if (j == 41 && --b9 <= 0)
                break; 
            } 
            continue;
          } 
          if (48 <= j && j <= 57) {
            k = j - 48;
            while (b < i1 && 48 <= (j = paramString.charAt(b)) && j <= 57) {
              k = k * 10 + j - 48;
              b++;
            } 
            if (n == 43 || (n == 45 && i != Integer.MIN_VALUE)) {
              if (k < 24) {
                k *= 60;
              } else {
                k = k % 100 + k / 100 * 60;
              } 
              if (n == 43)
                k = -k; 
              if (m != 0 && m != -1)
                // Byte code: goto -> 1000 
              m = k;
            } else if (k >= 70) {
              if (i != Integer.MIN_VALUE)
                // Byte code: goto -> 1000 
              if (j <= 32 || j == 44 || j == 47 || b >= i1) {
                i = k;
              } else {
                // Byte code: goto -> 1000
              } 
            } else if (j == 58) {
              if (b3 < 0) {
                b3 = (byte)k;
              } else if (b4 < 0) {
                b4 = (byte)k;
              } else {
                // Byte code: goto -> 1000
              } 
            } else if (j == 47) {
              if (b1 < 0) {
                b1 = (byte)(k - 1);
              } else if (b2 < 0) {
                b2 = (byte)k;
              } else {
                // Byte code: goto -> 1000
              } 
            } else {
              if (b < i1 && j != 44 && j > 32 && j != 45)
                // Byte code: goto -> 1000 
              if (b3 >= 0 && b4 < 0) {
                b4 = (byte)k;
              } else if (b4 >= 0 && b5 < 0) {
                b5 = (byte)k;
              } else if (b2 < 0) {
                b2 = (byte)k;
              } else if (i == Integer.MIN_VALUE) {
                if (b1 >= 0) {
                  if (b2 >= 0) {
                    i = k;
                  } else {
                    // Byte code: goto -> 1000
                  } 
                } else {
                  // Byte code: goto -> 1000
                } 
              } else {
                // Byte code: goto -> 1000
              } 
            } 
            n = 0;
            continue;
          } 
          if (j == 47 || j == 58 || j == 43 || j == 45) {
            n = j;
            continue;
          } 
          byte b8 = b - 1;
          while (b < i1) {
            j = paramString.charAt(b);
            if ((65 > j || j > 90) && (97 > j || j > 122))
              break; 
            b++;
          } 
          if (b <= b8 + 1)
            // Byte code: goto -> 1000 
          i2 = wtb.length;
          while (true) {
            if (--i2 >= 0) {
              if (wtb[i2].regionMatches(true, 0, paramString, b8, b - b8)) {
                int i3 = ttb[i2];
                if (i3 != 0) {
                  if (i3 == 1) {
                    if (b3 <= 12) {
                      if (b3 < 1)
                        // Byte code: goto -> 1000 
                      if (b3 < 12)
                        b3 += 12; 
                      break;
                    } 
                    // Byte code: goto -> 1000
                  } 
                  if (i3 == 14) {
                    if (b3 <= 12) {
                      if (b3 < 1)
                        // Byte code: goto -> 1000 
                      if (b3 == 12)
                        b3 = 0; 
                    } else {
                      // Byte code: goto -> 1000
                    } 
                  } else if (i3 <= 13) {
                    if (b1 < 0) {
                      b1 = (byte)(i3 - 2);
                    } else {
                      // Byte code: goto -> 1000
                    } 
                  } else {
                    m = i3 - 10000;
                    break;
                  } 
                } else {
                  break;
                } 
              } else {
                continue;
              } 
            } else {
              break;
            } 
            if (i2 < 0)
              // Byte code: goto -> 1000 
            n = 0;
          } 
        } else {
          break;
        } 
        if (i2 < 0)
          // Byte code: goto -> 1000 
        n = 0;
      } 
      if (i != Integer.MIN_VALUE && b1 >= 0 && b2 >= 0) {
        if (i < 100) {
          synchronized (Date.class) {
            if (defaultCenturyStart == 0)
              defaultCenturyStart = gcal.getCalendarDate().getYear() - 80; 
          } 
          i += defaultCenturyStart / 100 * 100;
          if (i < defaultCenturyStart)
            i += 100; 
        } 
        if (b5 < 0)
          b5 = 0; 
        if (b4 < 0)
          b4 = 0; 
        if (b3 < 0)
          b3 = 0; 
        BaseCalendar baseCalendar = getCalendarSystem(i);
        if (m == -1) {
          BaseCalendar.Date date1 = (BaseCalendar.Date)baseCalendar.newCalendarDate(TimeZone.getDefaultRef());
          date1.setDate(i, b1 + 1, b2);
          date1.setTimeOfDay(b3, b4, b5, 0);
          return baseCalendar.getTime(date1);
        } 
        BaseCalendar.Date date = (BaseCalendar.Date)baseCalendar.newCalendarDate(null);
        date.setDate(i, b1 + 1, b2);
        date.setTimeOfDay(b3, b4, b5, 0);
        return baseCalendar.getTime(date) + (m * 60000);
      } 
    } 
    throw new IllegalArgumentException();
  }
  
  @Deprecated
  public int getYear() { return normalize().getYear() - 1900; }
  
  @Deprecated
  public void setYear(int paramInt) { getCalendarDate().setNormalizedYear(paramInt + 1900); }
  
  @Deprecated
  public int getMonth() { return normalize().getMonth() - 1; }
  
  @Deprecated
  public void setMonth(int paramInt) {
    int i = 0;
    if (paramInt >= 12) {
      i = paramInt / 12;
      paramInt %= 12;
    } else if (paramInt < 0) {
      i = CalendarUtils.floorDivide(paramInt, 12);
      paramInt = CalendarUtils.mod(paramInt, 12);
    } 
    BaseCalendar.Date date = getCalendarDate();
    if (i != 0)
      date.setNormalizedYear(date.getNormalizedYear() + i); 
    date.setMonth(paramInt + 1);
  }
  
  @Deprecated
  public int getDate() { return normalize().getDayOfMonth(); }
  
  @Deprecated
  public void setDate(int paramInt) { getCalendarDate().setDayOfMonth(paramInt); }
  
  @Deprecated
  public int getDay() { return normalize().getDayOfWeek() - 1; }
  
  @Deprecated
  public int getHours() { return normalize().getHours(); }
  
  @Deprecated
  public void setHours(int paramInt) { getCalendarDate().setHours(paramInt); }
  
  @Deprecated
  public int getMinutes() { return normalize().getMinutes(); }
  
  @Deprecated
  public void setMinutes(int paramInt) { getCalendarDate().setMinutes(paramInt); }
  
  @Deprecated
  public int getSeconds() { return normalize().getSeconds(); }
  
  @Deprecated
  public void setSeconds(int paramInt) { getCalendarDate().setSeconds(paramInt); }
  
  public long getTime() { return getTimeImpl(); }
  
  private final long getTimeImpl() {
    if (this.cdate != null && !this.cdate.isNormalized())
      normalize(); 
    return this.fastTime;
  }
  
  public void setTime(long paramLong) {
    this.fastTime = paramLong;
    this.cdate = null;
  }
  
  public boolean before(Date paramDate) { return (getMillisOf(this) < getMillisOf(paramDate)); }
  
  public boolean after(Date paramDate) { return (getMillisOf(this) > getMillisOf(paramDate)); }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof Date && getTime() == ((Date)paramObject).getTime()); }
  
  static final long getMillisOf(Date paramDate) {
    if (paramDate.cdate == null || paramDate.cdate.isNormalized())
      return paramDate.fastTime; 
    BaseCalendar.Date date = (BaseCalendar.Date)paramDate.cdate.clone();
    return gcal.getTime(date);
  }
  
  public int compareTo(Date paramDate) {
    long l1 = getMillisOf(this);
    long l2 = getMillisOf(paramDate);
    return (l1 < l2) ? -1 : ((l1 == l2) ? 0 : 1);
  }
  
  public int hashCode() {
    long l = getTime();
    return (int)l ^ (int)(l >> 32);
  }
  
  public String toString() {
    BaseCalendar.Date date = normalize();
    StringBuilder stringBuilder = new StringBuilder(28);
    int i = date.getDayOfWeek();
    if (i == 1)
      i = 8; 
    convertToAbbr(stringBuilder, wtb[i]).append(' ');
    convertToAbbr(stringBuilder, wtb[date.getMonth() - 1 + 2 + 7]).append(' ');
    CalendarUtils.sprintf0d(stringBuilder, date.getDayOfMonth(), 2).append(' ');
    CalendarUtils.sprintf0d(stringBuilder, date.getHours(), 2).append(':');
    CalendarUtils.sprintf0d(stringBuilder, date.getMinutes(), 2).append(':');
    CalendarUtils.sprintf0d(stringBuilder, date.getSeconds(), 2).append(' ');
    TimeZone timeZone = date.getZone();
    if (timeZone != null) {
      stringBuilder.append(timeZone.getDisplayName(date.isDaylightTime(), 0, Locale.US));
    } else {
      stringBuilder.append("GMT");
    } 
    stringBuilder.append(' ').append(date.getYear());
    return stringBuilder.toString();
  }
  
  private static final StringBuilder convertToAbbr(StringBuilder paramStringBuilder, String paramString) {
    paramStringBuilder.append(Character.toUpperCase(paramString.charAt(0)));
    paramStringBuilder.append(paramString.charAt(1)).append(paramString.charAt(2));
    return paramStringBuilder;
  }
  
  @Deprecated
  public String toLocaleString() {
    DateFormat dateFormat = DateFormat.getDateTimeInstance();
    return dateFormat.format(this);
  }
  
  @Deprecated
  public String toGMTString() {
    long l = getTime();
    BaseCalendar baseCalendar = getCalendarSystem(l);
    BaseCalendar.Date date = (BaseCalendar.Date)baseCalendar.getCalendarDate(getTime(), (TimeZone)null);
    StringBuilder stringBuilder = new StringBuilder(32);
    CalendarUtils.sprintf0d(stringBuilder, date.getDayOfMonth(), 1).append(' ');
    convertToAbbr(stringBuilder, wtb[date.getMonth() - 1 + 2 + 7]).append(' ');
    stringBuilder.append(date.getYear()).append(' ');
    CalendarUtils.sprintf0d(stringBuilder, date.getHours(), 2).append(':');
    CalendarUtils.sprintf0d(stringBuilder, date.getMinutes(), 2).append(':');
    CalendarUtils.sprintf0d(stringBuilder, date.getSeconds(), 2);
    stringBuilder.append(" GMT");
    return stringBuilder.toString();
  }
  
  @Deprecated
  public int getTimezoneOffset() {
    int i;
    if (this.cdate == null) {
      TimeZone timeZone = TimeZone.getDefaultRef();
      if (timeZone instanceof ZoneInfo) {
        i = ((ZoneInfo)timeZone).getOffsets(this.fastTime, null);
      } else {
        i = timeZone.getOffset(this.fastTime);
      } 
    } else {
      normalize();
      i = this.cdate.getZoneOffset();
    } 
    return -i / 60000;
  }
  
  private final BaseCalendar.Date getCalendarDate() {
    if (this.cdate == null) {
      BaseCalendar baseCalendar = getCalendarSystem(this.fastTime);
      this.cdate = (BaseCalendar.Date)baseCalendar.getCalendarDate(this.fastTime, TimeZone.getDefaultRef());
    } 
    return this.cdate;
  }
  
  private final BaseCalendar.Date normalize() {
    if (this.cdate == null) {
      BaseCalendar baseCalendar = getCalendarSystem(this.fastTime);
      this.cdate = (BaseCalendar.Date)baseCalendar.getCalendarDate(this.fastTime, TimeZone.getDefaultRef());
      return this.cdate;
    } 
    if (!this.cdate.isNormalized())
      this.cdate = normalize(this.cdate); 
    TimeZone timeZone = TimeZone.getDefaultRef();
    if (timeZone != this.cdate.getZone()) {
      this.cdate.setZone(timeZone);
      BaseCalendar baseCalendar = getCalendarSystem(this.cdate);
      baseCalendar.getCalendarDate(this.fastTime, this.cdate);
    } 
    return this.cdate;
  }
  
  private final BaseCalendar.Date normalize(BaseCalendar.Date paramDate) {
    int i = paramDate.getNormalizedYear();
    int j = paramDate.getMonth();
    int k = paramDate.getDayOfMonth();
    int m = paramDate.getHours();
    int n = paramDate.getMinutes();
    int i1 = paramDate.getSeconds();
    int i2 = paramDate.getMillis();
    TimeZone timeZone = paramDate.getZone();
    if (i == 1582 || i > 280000000 || i < -280000000) {
      if (timeZone == null)
        timeZone = TimeZone.getTimeZone("GMT"); 
      GregorianCalendar gregorianCalendar = new GregorianCalendar(timeZone);
      gregorianCalendar.clear();
      gregorianCalendar.set(14, i2);
      gregorianCalendar.set(i, j - 1, k, m, n, i1);
      this.fastTime = gregorianCalendar.getTimeInMillis();
      BaseCalendar baseCalendar = getCalendarSystem(this.fastTime);
      return (BaseCalendar.Date)baseCalendar.getCalendarDate(this.fastTime, timeZone);
    } 
    BaseCalendar baseCalendar1 = getCalendarSystem(i);
    if (baseCalendar1 != getCalendarSystem(paramDate)) {
      paramDate = (BaseCalendar.Date)baseCalendar1.newCalendarDate(timeZone);
      paramDate.setNormalizedDate(i, j, k).setTimeOfDay(m, n, i1, i2);
    } 
    this.fastTime = baseCalendar1.getTime(paramDate);
    BaseCalendar baseCalendar2 = getCalendarSystem(this.fastTime);
    if (baseCalendar2 != baseCalendar1) {
      paramDate = (BaseCalendar.Date)baseCalendar2.newCalendarDate(timeZone);
      paramDate.setNormalizedDate(i, j, k).setTimeOfDay(m, n, i1, i2);
      this.fastTime = baseCalendar2.getTime(paramDate);
    } 
    return paramDate;
  }
  
  private static final BaseCalendar getCalendarSystem(int paramInt) { return (paramInt >= 1582) ? gcal : getJulianCalendar(); }
  
  private static final BaseCalendar getCalendarSystem(long paramLong) { return (paramLong >= 0L || paramLong >= -12219292800000L - TimeZone.getDefaultRef().getOffset(paramLong)) ? gcal : getJulianCalendar(); }
  
  private static final BaseCalendar getCalendarSystem(BaseCalendar.Date paramDate) { return (jcal == null) ? gcal : ((paramDate.getEra() != null) ? jcal : gcal); }
  
  private static final BaseCalendar getJulianCalendar() {
    if (jcal == null)
      jcal = (BaseCalendar)CalendarSystem.forName("julian"); 
    return jcal;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException { paramObjectOutputStream.writeLong(getTimeImpl()); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException { this.fastTime = paramObjectInputStream.readLong(); }
  
  public static Date from(Instant paramInstant) {
    try {
      return new Date(paramInstant.toEpochMilli());
    } catch (ArithmeticException arithmeticException) {
      throw new IllegalArgumentException(arithmeticException);
    } 
  }
  
  public Instant toInstant() { return Instant.ofEpochMilli(getTime()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\Date.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */