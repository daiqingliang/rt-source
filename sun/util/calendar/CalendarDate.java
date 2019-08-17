package sun.util.calendar;

import java.util.Locale;
import java.util.TimeZone;

public abstract class CalendarDate implements Cloneable {
  public static final int FIELD_UNDEFINED = -2147483648;
  
  public static final long TIME_UNDEFINED = -9223372036854775808L;
  
  private Era era;
  
  private int year;
  
  private int month;
  
  private int dayOfMonth;
  
  private int dayOfWeek = Integer.MIN_VALUE;
  
  private boolean leapYear;
  
  private int hours;
  
  private int minutes;
  
  private int seconds;
  
  private int millis;
  
  private long fraction;
  
  private boolean normalized;
  
  private TimeZone zoneinfo;
  
  private int zoneOffset;
  
  private int daylightSaving;
  
  private boolean forceStandardTime;
  
  private Locale locale;
  
  protected CalendarDate() { this(TimeZone.getDefault()); }
  
  protected CalendarDate(TimeZone paramTimeZone) { this.zoneinfo = paramTimeZone; }
  
  public Era getEra() { return this.era; }
  
  public CalendarDate setEra(Era paramEra) {
    if (this.era == paramEra)
      return this; 
    this.era = paramEra;
    this.normalized = false;
    return this;
  }
  
  public int getYear() { return this.year; }
  
  public CalendarDate setYear(int paramInt) {
    if (this.year != paramInt) {
      this.year = paramInt;
      this.normalized = false;
    } 
    return this;
  }
  
  public CalendarDate addYear(int paramInt) {
    if (paramInt != 0) {
      this.year += paramInt;
      this.normalized = false;
    } 
    return this;
  }
  
  public boolean isLeapYear() { return this.leapYear; }
  
  void setLeapYear(boolean paramBoolean) { this.leapYear = paramBoolean; }
  
  public int getMonth() { return this.month; }
  
  public CalendarDate setMonth(int paramInt) {
    if (this.month != paramInt) {
      this.month = paramInt;
      this.normalized = false;
    } 
    return this;
  }
  
  public CalendarDate addMonth(int paramInt) {
    if (paramInt != 0) {
      this.month += paramInt;
      this.normalized = false;
    } 
    return this;
  }
  
  public int getDayOfMonth() { return this.dayOfMonth; }
  
  public CalendarDate setDayOfMonth(int paramInt) {
    if (this.dayOfMonth != paramInt) {
      this.dayOfMonth = paramInt;
      this.normalized = false;
    } 
    return this;
  }
  
  public CalendarDate addDayOfMonth(int paramInt) {
    if (paramInt != 0) {
      this.dayOfMonth += paramInt;
      this.normalized = false;
    } 
    return this;
  }
  
  public int getDayOfWeek() {
    if (!isNormalized())
      this.dayOfWeek = Integer.MIN_VALUE; 
    return this.dayOfWeek;
  }
  
  public int getHours() { return this.hours; }
  
  public CalendarDate setHours(int paramInt) {
    if (this.hours != paramInt) {
      this.hours = paramInt;
      this.normalized = false;
    } 
    return this;
  }
  
  public CalendarDate addHours(int paramInt) {
    if (paramInt != 0) {
      this.hours += paramInt;
      this.normalized = false;
    } 
    return this;
  }
  
  public int getMinutes() { return this.minutes; }
  
  public CalendarDate setMinutes(int paramInt) {
    if (this.minutes != paramInt) {
      this.minutes = paramInt;
      this.normalized = false;
    } 
    return this;
  }
  
  public CalendarDate addMinutes(int paramInt) {
    if (paramInt != 0) {
      this.minutes += paramInt;
      this.normalized = false;
    } 
    return this;
  }
  
  public int getSeconds() { return this.seconds; }
  
  public CalendarDate setSeconds(int paramInt) {
    if (this.seconds != paramInt) {
      this.seconds = paramInt;
      this.normalized = false;
    } 
    return this;
  }
  
  public CalendarDate addSeconds(int paramInt) {
    if (paramInt != 0) {
      this.seconds += paramInt;
      this.normalized = false;
    } 
    return this;
  }
  
  public int getMillis() { return this.millis; }
  
  public CalendarDate setMillis(int paramInt) {
    if (this.millis != paramInt) {
      this.millis = paramInt;
      this.normalized = false;
    } 
    return this;
  }
  
  public CalendarDate addMillis(int paramInt) {
    if (paramInt != 0) {
      this.millis += paramInt;
      this.normalized = false;
    } 
    return this;
  }
  
  public long getTimeOfDay() { return !isNormalized() ? (this.fraction = Float.MIN_VALUE) : this.fraction; }
  
  public CalendarDate setDate(int paramInt1, int paramInt2, int paramInt3) {
    setYear(paramInt1);
    setMonth(paramInt2);
    setDayOfMonth(paramInt3);
    return this;
  }
  
  public CalendarDate addDate(int paramInt1, int paramInt2, int paramInt3) {
    addYear(paramInt1);
    addMonth(paramInt2);
    addDayOfMonth(paramInt3);
    return this;
  }
  
  public CalendarDate setTimeOfDay(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    setHours(paramInt1);
    setMinutes(paramInt2);
    setSeconds(paramInt3);
    setMillis(paramInt4);
    return this;
  }
  
  public CalendarDate addTimeOfDay(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    addHours(paramInt1);
    addMinutes(paramInt2);
    addSeconds(paramInt3);
    addMillis(paramInt4);
    return this;
  }
  
  protected void setTimeOfDay(long paramLong) { this.fraction = paramLong; }
  
  public boolean isNormalized() { return this.normalized; }
  
  public boolean isStandardTime() { return this.forceStandardTime; }
  
  public void setStandardTime(boolean paramBoolean) { this.forceStandardTime = paramBoolean; }
  
  public boolean isDaylightTime() { return isStandardTime() ? false : ((this.daylightSaving != 0)); }
  
  protected void setLocale(Locale paramLocale) { this.locale = paramLocale; }
  
  public TimeZone getZone() { return this.zoneinfo; }
  
  public CalendarDate setZone(TimeZone paramTimeZone) {
    this.zoneinfo = paramTimeZone;
    return this;
  }
  
  public boolean isSameDate(CalendarDate paramCalendarDate) { return (getDayOfWeek() == paramCalendarDate.getDayOfWeek() && getMonth() == paramCalendarDate.getMonth() && getYear() == paramCalendarDate.getYear() && getEra() == paramCalendarDate.getEra()); }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof CalendarDate))
      return false; 
    CalendarDate calendarDate = (CalendarDate)paramObject;
    if (isNormalized() != calendarDate.isNormalized())
      return false; 
    boolean bool1 = (this.zoneinfo != null) ? 1 : 0;
    boolean bool2 = (calendarDate.zoneinfo != null) ? 1 : 0;
    return (bool1 != bool2) ? false : ((bool1 && !this.zoneinfo.equals(calendarDate.zoneinfo)) ? false : ((getEra() == calendarDate.getEra() && this.year == calendarDate.year && this.month == calendarDate.month && this.dayOfMonth == calendarDate.dayOfMonth && this.hours == calendarDate.hours && this.minutes == calendarDate.minutes && this.seconds == calendarDate.seconds && this.millis == calendarDate.millis && this.zoneOffset == calendarDate.zoneOffset)));
  }
  
  public int hashCode() {
    long l = (((this.year - 1970L) * 12L + (this.month - 1)) * 30L + this.dayOfMonth) * 24L;
    l = (((l + this.hours) * 60L + this.minutes) * 60L + this.seconds) * 1000L + this.millis;
    l -= this.zoneOffset;
    int i = isNormalized() ? 1 : 0;
    int j = 0;
    Era era1 = getEra();
    if (era1 != null)
      j = era1.hashCode(); 
    int k = (this.zoneinfo != null) ? this.zoneinfo.hashCode() : 0;
    return (int)l * (int)(l >> 32) ^ j ^ i ^ k;
  }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    CalendarUtils.sprintf0d(stringBuilder, this.year, 4).append('-');
    CalendarUtils.sprintf0d(stringBuilder, this.month, 2).append('-');
    CalendarUtils.sprintf0d(stringBuilder, this.dayOfMonth, 2).append('T');
    CalendarUtils.sprintf0d(stringBuilder, this.hours, 2).append(':');
    CalendarUtils.sprintf0d(stringBuilder, this.minutes, 2).append(':');
    CalendarUtils.sprintf0d(stringBuilder, this.seconds, 2).append('.');
    CalendarUtils.sprintf0d(stringBuilder, this.millis, 3);
    if (this.zoneOffset == 0) {
      stringBuilder.append('Z');
    } else if (this.zoneOffset != Integer.MIN_VALUE) {
      char c;
      int i;
      if (this.zoneOffset > 0) {
        i = this.zoneOffset;
        c = '+';
      } else {
        i = -this.zoneOffset;
        c = '-';
      } 
      i /= 60000;
      stringBuilder.append(c);
      CalendarUtils.sprintf0d(stringBuilder, i / 60, 2);
      CalendarUtils.sprintf0d(stringBuilder, i % 60, 2);
    } else {
      stringBuilder.append(" local time");
    } 
    return stringBuilder.toString();
  }
  
  protected void setDayOfWeek(int paramInt) { this.dayOfWeek = paramInt; }
  
  protected void setNormalized(boolean paramBoolean) { this.normalized = paramBoolean; }
  
  public int getZoneOffset() { return this.zoneOffset; }
  
  protected void setZoneOffset(int paramInt) { this.zoneOffset = paramInt; }
  
  public int getDaylightSaving() { return this.daylightSaving; }
  
  protected void setDaylightSaving(int paramInt) { this.daylightSaving = paramInt; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\calendar\CalendarDate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */