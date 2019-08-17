package java.util;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import sun.util.calendar.BaseCalendar;
import sun.util.calendar.CalendarSystem;
import sun.util.calendar.CalendarUtils;
import sun.util.calendar.Gregorian;

public class SimpleTimeZone extends TimeZone {
  private int startMonth;
  
  private int startDay;
  
  private int startDayOfWeek;
  
  private int startTime;
  
  private int startTimeMode;
  
  private int endMonth;
  
  private int endDay;
  
  private int endDayOfWeek;
  
  private int endTime;
  
  private int endTimeMode;
  
  private int startYear;
  
  private int rawOffset;
  
  private boolean useDaylight = false;
  
  private static final int millisPerHour = 3600000;
  
  private static final int millisPerDay = 86400000;
  
  private final byte[] monthLength = staticMonthLength;
  
  private static final byte[] staticMonthLength = { 
      31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 
      30, 31 };
  
  private static final byte[] staticLeapMonthLength = { 
      31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 
      30, 31 };
  
  private int startMode;
  
  private int endMode;
  
  private int dstSavings;
  
  private static final Gregorian gcal = CalendarSystem.getGregorianCalendar();
  
  private long cacheYear;
  
  private long cacheStart;
  
  private long cacheEnd;
  
  private static final int DOM_MODE = 1;
  
  private static final int DOW_IN_MONTH_MODE = 2;
  
  private static final int DOW_GE_DOM_MODE = 3;
  
  private static final int DOW_LE_DOM_MODE = 4;
  
  public static final int WALL_TIME = 0;
  
  public static final int STANDARD_TIME = 1;
  
  public static final int UTC_TIME = 2;
  
  static final long serialVersionUID = -403250971215465050L;
  
  static final int currentSerialVersion = 2;
  
  private int serialVersionOnStream = 2;
  
  private static final int MAX_RULE_NUM = 6;
  
  public SimpleTimeZone(int paramInt, String paramString) {
    this.rawOffset = paramInt;
    setID(paramString);
    this.dstSavings = 3600000;
  }
  
  public SimpleTimeZone(int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9) { this(paramInt1, paramString, paramInt2, paramInt3, paramInt4, paramInt5, 0, paramInt6, paramInt7, paramInt8, paramInt9, 0, 3600000); }
  
  public SimpleTimeZone(int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10) { this(paramInt1, paramString, paramInt2, paramInt3, paramInt4, paramInt5, 0, paramInt6, paramInt7, paramInt8, paramInt9, 0, paramInt10); }
  
  public SimpleTimeZone(int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11, int paramInt12) {
    setID(paramString);
    this.rawOffset = paramInt1;
    this.startMonth = paramInt2;
    this.startDay = paramInt3;
    this.startDayOfWeek = paramInt4;
    this.startTime = paramInt5;
    this.startTimeMode = paramInt6;
    this.endMonth = paramInt7;
    this.endDay = paramInt8;
    this.endDayOfWeek = paramInt9;
    this.endTime = paramInt10;
    this.endTimeMode = paramInt11;
    this.dstSavings = paramInt12;
    decodeRules();
    if (paramInt12 <= 0)
      throw new IllegalArgumentException("Illegal daylight saving value: " + paramInt12); 
  }
  
  public void setStartYear(int paramInt) {
    this.startYear = paramInt;
    invalidateCache();
  }
  
  public void setStartRule(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.startMonth = paramInt1;
    this.startDay = paramInt2;
    this.startDayOfWeek = paramInt3;
    this.startTime = paramInt4;
    this.startTimeMode = 0;
    decodeStartRule();
    invalidateCache();
  }
  
  public void setStartRule(int paramInt1, int paramInt2, int paramInt3) { setStartRule(paramInt1, paramInt2, 0, paramInt3); }
  
  public void setStartRule(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean) {
    if (paramBoolean) {
      setStartRule(paramInt1, paramInt2, -paramInt3, paramInt4);
    } else {
      setStartRule(paramInt1, -paramInt2, -paramInt3, paramInt4);
    } 
  }
  
  public void setEndRule(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.endMonth = paramInt1;
    this.endDay = paramInt2;
    this.endDayOfWeek = paramInt3;
    this.endTime = paramInt4;
    this.endTimeMode = 0;
    decodeEndRule();
    invalidateCache();
  }
  
  public void setEndRule(int paramInt1, int paramInt2, int paramInt3) { setEndRule(paramInt1, paramInt2, 0, paramInt3); }
  
  public void setEndRule(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean) {
    if (paramBoolean) {
      setEndRule(paramInt1, paramInt2, -paramInt3, paramInt4);
    } else {
      setEndRule(paramInt1, -paramInt2, -paramInt3, paramInt4);
    } 
  }
  
  public int getOffset(long paramLong) { return getOffsets(paramLong, null); }
  
  int getOffsets(long paramLong, int[] paramArrayOfInt) {
    int i = this.rawOffset;
    if (this.useDaylight)
      synchronized (this) {
        if (this.cacheStart != 0L && paramLong >= this.cacheStart && paramLong < this.cacheEnd) {
          i += this.dstSavings;
        } else {
          null = (paramLong >= -12219292800000L) ? gcal : (BaseCalendar)CalendarSystem.forName("julian");
          BaseCalendar.Date date = (BaseCalendar.Date)null.newCalendarDate(TimeZone.NO_TIMEZONE);
          null.getCalendarDate(paramLong + this.rawOffset, date);
          int j = date.getNormalizedYear();
          if (j >= this.startYear) {
            date.setTimeOfDay(0, 0, 0, 0);
            i = getOffset(null, date, j, paramLong);
          } 
        } 
      }  
    if (paramArrayOfInt != null) {
      paramArrayOfInt[0] = this.rawOffset;
      paramArrayOfInt[1] = i - this.rawOffset;
    } 
    return i;
  }
  
  public int getOffset(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    if (paramInt1 != 1 && paramInt1 != 0)
      throw new IllegalArgumentException("Illegal era " + paramInt1); 
    int i = paramInt2;
    if (paramInt1 == 0)
      i = 1 - i; 
    if (i >= 292278994) {
      i = 2800 + i % 2800;
    } else if (i <= -292269054) {
      i = (int)CalendarUtils.mod(i, 28L);
    } 
    int j = paramInt3 + 1;
    BaseCalendar baseCalendar = gcal;
    BaseCalendar.Date date = (BaseCalendar.Date)baseCalendar.newCalendarDate(TimeZone.NO_TIMEZONE);
    date.setDate(i, j, paramInt4);
    long l = baseCalendar.getTime(date);
    l += (paramInt6 - this.rawOffset);
    if (l < -12219292800000L) {
      baseCalendar = (BaseCalendar)CalendarSystem.forName("julian");
      date = (BaseCalendar.Date)baseCalendar.newCalendarDate(TimeZone.NO_TIMEZONE);
      date.setNormalizedDate(i, j, paramInt4);
      l = baseCalendar.getTime(date) + paramInt6 - this.rawOffset;
    } 
    if (date.getNormalizedYear() != i || date.getMonth() != j || date.getDayOfMonth() != paramInt4 || paramInt5 < 1 || paramInt5 > 7 || paramInt6 < 0 || paramInt6 >= 86400000)
      throw new IllegalArgumentException(); 
    return (!this.useDaylight || paramInt2 < this.startYear || paramInt1 != 1) ? this.rawOffset : getOffset(baseCalendar, date, i, l);
  }
  
  private int getOffset(BaseCalendar paramBaseCalendar, BaseCalendar.Date paramDate, int paramInt, long paramLong) {
    synchronized (this) {
      if (this.cacheStart != 0L) {
        if (paramLong >= this.cacheStart && paramLong < this.cacheEnd)
          return this.rawOffset + this.dstSavings; 
        if (paramInt == this.cacheYear)
          return this.rawOffset; 
      } 
    } 
    long l1 = getStart(paramBaseCalendar, paramDate, paramInt);
    long l2 = getEnd(paramBaseCalendar, paramDate, paramInt);
    int i = this.rawOffset;
    if (l1 <= l2) {
      if (paramLong >= l1 && paramLong < l2)
        i += this.dstSavings; 
      synchronized (this) {
        this.cacheYear = paramInt;
        this.cacheStart = l1;
        this.cacheEnd = l2;
      } 
    } else {
      if (paramLong < l2) {
        l1 = getStart(paramBaseCalendar, paramDate, paramInt - 1);
        if (paramLong >= l1)
          i += this.dstSavings; 
      } else if (paramLong >= l1) {
        l2 = getEnd(paramBaseCalendar, paramDate, paramInt + 1);
        if (paramLong < l2)
          i += this.dstSavings; 
      } 
      if (l1 <= l2)
        synchronized (this) {
          this.cacheYear = this.startYear - 1L;
          this.cacheStart = l1;
          this.cacheEnd = l2;
        }  
    } 
    return i;
  }
  
  private long getStart(BaseCalendar paramBaseCalendar, BaseCalendar.Date paramDate, int paramInt) {
    int i = this.startTime;
    if (this.startTimeMode != 2)
      i -= this.rawOffset; 
    return getTransition(paramBaseCalendar, paramDate, this.startMode, paramInt, this.startMonth, this.startDay, this.startDayOfWeek, i);
  }
  
  private long getEnd(BaseCalendar paramBaseCalendar, BaseCalendar.Date paramDate, int paramInt) {
    int i = this.endTime;
    if (this.endTimeMode != 2)
      i -= this.rawOffset; 
    if (this.endTimeMode == 0)
      i -= this.dstSavings; 
    return getTransition(paramBaseCalendar, paramDate, this.endMode, paramInt, this.endMonth, this.endDay, this.endDayOfWeek, i);
  }
  
  private long getTransition(BaseCalendar paramBaseCalendar, BaseCalendar.Date paramDate, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    paramDate.setNormalizedYear(paramInt2);
    paramDate.setMonth(paramInt3 + 1);
    switch (paramInt1) {
      case 1:
        paramDate.setDayOfMonth(paramInt4);
        break;
      case 2:
        paramDate.setDayOfMonth(1);
        if (paramInt4 < 0)
          paramDate.setDayOfMonth(paramBaseCalendar.getMonthLength(paramDate)); 
        paramDate = (BaseCalendar.Date)paramBaseCalendar.getNthDayOfWeek(paramInt4, paramInt5, paramDate);
        break;
      case 3:
        paramDate.setDayOfMonth(paramInt4);
        paramDate = (BaseCalendar.Date)paramBaseCalendar.getNthDayOfWeek(1, paramInt5, paramDate);
        break;
      case 4:
        paramDate.setDayOfMonth(paramInt4);
        paramDate = (BaseCalendar.Date)paramBaseCalendar.getNthDayOfWeek(-1, paramInt5, paramDate);
        break;
    } 
    return paramBaseCalendar.getTime(paramDate) + paramInt6;
  }
  
  public int getRawOffset() { return this.rawOffset; }
  
  public void setRawOffset(int paramInt) { this.rawOffset = paramInt; }
  
  public void setDSTSavings(int paramInt) {
    if (paramInt <= 0)
      throw new IllegalArgumentException("Illegal daylight saving value: " + paramInt); 
    this.dstSavings = paramInt;
  }
  
  public int getDSTSavings() { return this.useDaylight ? this.dstSavings : 0; }
  
  public boolean useDaylightTime() { return this.useDaylight; }
  
  public boolean observesDaylightTime() { return useDaylightTime(); }
  
  public boolean inDaylightTime(Date paramDate) { return (getOffset(paramDate.getTime()) != this.rawOffset); }
  
  public Object clone() { return super.clone(); }
  
  public int hashCode() { return this.startMonth ^ this.startDay ^ this.startDayOfWeek ^ this.startTime ^ this.endMonth ^ this.endDay ^ this.endDayOfWeek ^ this.endTime ^ this.rawOffset; }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof SimpleTimeZone))
      return false; 
    SimpleTimeZone simpleTimeZone = (SimpleTimeZone)paramObject;
    return (getID().equals(simpleTimeZone.getID()) && hasSameRules(simpleTimeZone));
  }
  
  public boolean hasSameRules(TimeZone paramTimeZone) {
    if (this == paramTimeZone)
      return true; 
    if (!(paramTimeZone instanceof SimpleTimeZone))
      return false; 
    SimpleTimeZone simpleTimeZone = (SimpleTimeZone)paramTimeZone;
    return (this.rawOffset == simpleTimeZone.rawOffset && this.useDaylight == simpleTimeZone.useDaylight && (!this.useDaylight || (this.dstSavings == simpleTimeZone.dstSavings && this.startMode == simpleTimeZone.startMode && this.startMonth == simpleTimeZone.startMonth && this.startDay == simpleTimeZone.startDay && this.startDayOfWeek == simpleTimeZone.startDayOfWeek && this.startTime == simpleTimeZone.startTime && this.startTimeMode == simpleTimeZone.startTimeMode && this.endMode == simpleTimeZone.endMode && this.endMonth == simpleTimeZone.endMonth && this.endDay == simpleTimeZone.endDay && this.endDayOfWeek == simpleTimeZone.endDayOfWeek && this.endTime == simpleTimeZone.endTime && this.endTimeMode == simpleTimeZone.endTimeMode && this.startYear == simpleTimeZone.startYear)));
  }
  
  public String toString() { return getClass().getName() + "[id=" + getID() + ",offset=" + this.rawOffset + ",dstSavings=" + this.dstSavings + ",useDaylight=" + this.useDaylight + ",startYear=" + this.startYear + ",startMode=" + this.startMode + ",startMonth=" + this.startMonth + ",startDay=" + this.startDay + ",startDayOfWeek=" + this.startDayOfWeek + ",startTime=" + this.startTime + ",startTimeMode=" + this.startTimeMode + ",endMode=" + this.endMode + ",endMonth=" + this.endMonth + ",endDay=" + this.endDay + ",endDayOfWeek=" + this.endDayOfWeek + ",endTime=" + this.endTime + ",endTimeMode=" + this.endTimeMode + ']'; }
  
  private void invalidateCache() {
    this.cacheYear = (this.startYear - 1);
    this.cacheStart = this.cacheEnd = 0L;
  }
  
  private void decodeRules() {
    decodeStartRule();
    decodeEndRule();
  }
  
  private void decodeStartRule() {
    this.useDaylight = (this.startDay != 0 && this.endDay != 0);
    if (this.startDay != 0) {
      if (this.startMonth < 0 || this.startMonth > 11)
        throw new IllegalArgumentException("Illegal start month " + this.startMonth); 
      if (this.startTime < 0 || this.startTime > 86400000)
        throw new IllegalArgumentException("Illegal start time " + this.startTime); 
      if (this.startDayOfWeek == 0) {
        this.startMode = 1;
      } else {
        if (this.startDayOfWeek > 0) {
          this.startMode = 2;
        } else {
          this.startDayOfWeek = -this.startDayOfWeek;
          if (this.startDay > 0) {
            this.startMode = 3;
          } else {
            this.startDay = -this.startDay;
            this.startMode = 4;
          } 
        } 
        if (this.startDayOfWeek > 7)
          throw new IllegalArgumentException("Illegal start day of week " + this.startDayOfWeek); 
      } 
      if (this.startMode == 2) {
        if (this.startDay < -5 || this.startDay > 5)
          throw new IllegalArgumentException("Illegal start day of week in month " + this.startDay); 
      } else if (this.startDay < 1 || this.startDay > staticMonthLength[this.startMonth]) {
        throw new IllegalArgumentException("Illegal start day " + this.startDay);
      } 
    } 
  }
  
  private void decodeEndRule() {
    this.useDaylight = (this.startDay != 0 && this.endDay != 0);
    if (this.endDay != 0) {
      if (this.endMonth < 0 || this.endMonth > 11)
        throw new IllegalArgumentException("Illegal end month " + this.endMonth); 
      if (this.endTime < 0 || this.endTime > 86400000)
        throw new IllegalArgumentException("Illegal end time " + this.endTime); 
      if (this.endDayOfWeek == 0) {
        this.endMode = 1;
      } else {
        if (this.endDayOfWeek > 0) {
          this.endMode = 2;
        } else {
          this.endDayOfWeek = -this.endDayOfWeek;
          if (this.endDay > 0) {
            this.endMode = 3;
          } else {
            this.endDay = -this.endDay;
            this.endMode = 4;
          } 
        } 
        if (this.endDayOfWeek > 7)
          throw new IllegalArgumentException("Illegal end day of week " + this.endDayOfWeek); 
      } 
      if (this.endMode == 2) {
        if (this.endDay < -5 || this.endDay > 5)
          throw new IllegalArgumentException("Illegal end day of week in month " + this.endDay); 
      } else if (this.endDay < 1 || this.endDay > staticMonthLength[this.endMonth]) {
        throw new IllegalArgumentException("Illegal end day " + this.endDay);
      } 
    } 
  }
  
  private void makeRulesCompatible() {
    switch (this.startMode) {
      case 1:
        this.startDay = 1 + this.startDay / 7;
        this.startDayOfWeek = 1;
        break;
      case 3:
        if (this.startDay != 1)
          this.startDay = 1 + this.startDay / 7; 
        break;
      case 4:
        if (this.startDay >= 30) {
          this.startDay = -1;
          break;
        } 
        this.startDay = 1 + this.startDay / 7;
        break;
    } 
    switch (this.endMode) {
      case 1:
        this.endDay = 1 + this.endDay / 7;
        this.endDayOfWeek = 1;
        break;
      case 3:
        if (this.endDay != 1)
          this.endDay = 1 + this.endDay / 7; 
        break;
      case 4:
        if (this.endDay >= 30) {
          this.endDay = -1;
          break;
        } 
        this.endDay = 1 + this.endDay / 7;
        break;
    } 
    switch (this.startTimeMode) {
      case 2:
        this.startTime += this.rawOffset;
        break;
    } 
    while (this.startTime < 0) {
      this.startTime += 86400000;
      this.startDayOfWeek = 1 + (this.startDayOfWeek + 5) % 7;
    } 
    while (this.startTime >= 86400000) {
      this.startTime -= 86400000;
      this.startDayOfWeek = 1 + this.startDayOfWeek % 7;
    } 
    switch (this.endTimeMode) {
      case 2:
        this.endTime += this.rawOffset + this.dstSavings;
        break;
      case 1:
        this.endTime += this.dstSavings;
        break;
    } 
    while (this.endTime < 0) {
      this.endTime += 86400000;
      this.endDayOfWeek = 1 + (this.endDayOfWeek + 5) % 7;
    } 
    while (this.endTime >= 86400000) {
      this.endTime -= 86400000;
      this.endDayOfWeek = 1 + this.endDayOfWeek % 7;
    } 
  }
  
  private byte[] packRules() {
    byte[] arrayOfByte = new byte[6];
    arrayOfByte[0] = (byte)this.startDay;
    arrayOfByte[1] = (byte)this.startDayOfWeek;
    arrayOfByte[2] = (byte)this.endDay;
    arrayOfByte[3] = (byte)this.endDayOfWeek;
    arrayOfByte[4] = (byte)this.startTimeMode;
    arrayOfByte[5] = (byte)this.endTimeMode;
    return arrayOfByte;
  }
  
  private void unpackRules(byte[] paramArrayOfByte) {
    this.startDay = paramArrayOfByte[0];
    this.startDayOfWeek = paramArrayOfByte[1];
    this.endDay = paramArrayOfByte[2];
    this.endDayOfWeek = paramArrayOfByte[3];
    if (paramArrayOfByte.length >= 6) {
      this.startTimeMode = paramArrayOfByte[4];
      this.endTimeMode = paramArrayOfByte[5];
    } 
  }
  
  private int[] packTimes() {
    int[] arrayOfInt = new int[2];
    arrayOfInt[0] = this.startTime;
    arrayOfInt[1] = this.endTime;
    return arrayOfInt;
  }
  
  private void unpackTimes(int[] paramArrayOfInt) {
    this.startTime = paramArrayOfInt[0];
    this.endTime = paramArrayOfInt[1];
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    byte[] arrayOfByte = packRules();
    int[] arrayOfInt = packTimes();
    makeRulesCompatible();
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(arrayOfByte.length);
    paramObjectOutputStream.write(arrayOfByte);
    paramObjectOutputStream.writeObject(arrayOfInt);
    unpackRules(arrayOfByte);
    unpackTimes(arrayOfInt);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    if (this.serialVersionOnStream < 1) {
      if (this.startDayOfWeek == 0)
        this.startDayOfWeek = 1; 
      if (this.endDayOfWeek == 0)
        this.endDayOfWeek = 1; 
      this.startMode = this.endMode = 2;
      this.dstSavings = 3600000;
    } else {
      int i = paramObjectInputStream.readInt();
      if (i <= 6) {
        byte[] arrayOfByte = new byte[i];
        paramObjectInputStream.readFully(arrayOfByte);
        unpackRules(arrayOfByte);
      } else {
        throw new InvalidObjectException("Too many rules: " + i);
      } 
    } 
    if (this.serialVersionOnStream >= 2) {
      int[] arrayOfInt = (int[])paramObjectInputStream.readObject();
      unpackTimes(arrayOfInt);
    } 
    this.serialVersionOnStream = 2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\SimpleTimeZone.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */