package sun.util.calendar;

import java.util.Locale;
import java.util.TimeZone;

class ImmutableGregorianDate extends BaseCalendar.Date {
  private final BaseCalendar.Date date;
  
  ImmutableGregorianDate(BaseCalendar.Date paramDate) {
    if (paramDate == null)
      throw new NullPointerException(); 
    this.date = paramDate;
  }
  
  public Era getEra() { return this.date.getEra(); }
  
  public CalendarDate setEra(Era paramEra) {
    unsupported();
    return this;
  }
  
  public int getYear() { return this.date.getYear(); }
  
  public CalendarDate setYear(int paramInt) {
    unsupported();
    return this;
  }
  
  public CalendarDate addYear(int paramInt) {
    unsupported();
    return this;
  }
  
  public boolean isLeapYear() { return this.date.isLeapYear(); }
  
  void setLeapYear(boolean paramBoolean) { unsupported(); }
  
  public int getMonth() { return this.date.getMonth(); }
  
  public CalendarDate setMonth(int paramInt) {
    unsupported();
    return this;
  }
  
  public CalendarDate addMonth(int paramInt) {
    unsupported();
    return this;
  }
  
  public int getDayOfMonth() { return this.date.getDayOfMonth(); }
  
  public CalendarDate setDayOfMonth(int paramInt) {
    unsupported();
    return this;
  }
  
  public CalendarDate addDayOfMonth(int paramInt) {
    unsupported();
    return this;
  }
  
  public int getDayOfWeek() { return this.date.getDayOfWeek(); }
  
  public int getHours() { return this.date.getHours(); }
  
  public CalendarDate setHours(int paramInt) {
    unsupported();
    return this;
  }
  
  public CalendarDate addHours(int paramInt) {
    unsupported();
    return this;
  }
  
  public int getMinutes() { return this.date.getMinutes(); }
  
  public CalendarDate setMinutes(int paramInt) {
    unsupported();
    return this;
  }
  
  public CalendarDate addMinutes(int paramInt) {
    unsupported();
    return this;
  }
  
  public int getSeconds() { return this.date.getSeconds(); }
  
  public CalendarDate setSeconds(int paramInt) {
    unsupported();
    return this;
  }
  
  public CalendarDate addSeconds(int paramInt) {
    unsupported();
    return this;
  }
  
  public int getMillis() { return this.date.getMillis(); }
  
  public CalendarDate setMillis(int paramInt) {
    unsupported();
    return this;
  }
  
  public CalendarDate addMillis(int paramInt) {
    unsupported();
    return this;
  }
  
  public long getTimeOfDay() { return this.date.getTimeOfDay(); }
  
  public CalendarDate setDate(int paramInt1, int paramInt2, int paramInt3) {
    unsupported();
    return this;
  }
  
  public CalendarDate addDate(int paramInt1, int paramInt2, int paramInt3) {
    unsupported();
    return this;
  }
  
  public CalendarDate setTimeOfDay(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    unsupported();
    return this;
  }
  
  public CalendarDate addTimeOfDay(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    unsupported();
    return this;
  }
  
  protected void setTimeOfDay(long paramLong) { unsupported(); }
  
  public boolean isNormalized() { return this.date.isNormalized(); }
  
  public boolean isStandardTime() { return this.date.isStandardTime(); }
  
  public void setStandardTime(boolean paramBoolean) { unsupported(); }
  
  public boolean isDaylightTime() { return this.date.isDaylightTime(); }
  
  protected void setLocale(Locale paramLocale) { unsupported(); }
  
  public TimeZone getZone() { return this.date.getZone(); }
  
  public CalendarDate setZone(TimeZone paramTimeZone) {
    unsupported();
    return this;
  }
  
  public boolean isSameDate(CalendarDate paramCalendarDate) { return paramCalendarDate.isSameDate(paramCalendarDate); }
  
  public boolean equals(Object paramObject) { return (this == paramObject) ? true : (!(paramObject instanceof ImmutableGregorianDate) ? false : this.date.equals(((ImmutableGregorianDate)paramObject).date)); }
  
  public int hashCode() { return this.date.hashCode(); }
  
  public Object clone() { return super.clone(); }
  
  public String toString() { return this.date.toString(); }
  
  protected void setDayOfWeek(int paramInt) { unsupported(); }
  
  protected void setNormalized(boolean paramBoolean) { unsupported(); }
  
  public int getZoneOffset() { return this.date.getZoneOffset(); }
  
  protected void setZoneOffset(int paramInt) { unsupported(); }
  
  public int getDaylightSaving() { return this.date.getDaylightSaving(); }
  
  protected void setDaylightSaving(int paramInt) { unsupported(); }
  
  public int getNormalizedYear() { return this.date.getNormalizedYear(); }
  
  public void setNormalizedYear(int paramInt) { unsupported(); }
  
  private void unsupported() { throw new UnsupportedOperationException(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\calendar\ImmutableGregorianDate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */