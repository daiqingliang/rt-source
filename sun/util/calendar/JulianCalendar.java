package sun.util.calendar;

import java.util.TimeZone;

public class JulianCalendar extends BaseCalendar {
  private static final int BCE = 0;
  
  private static final int CE = 1;
  
  private static final Era[] eras = { new Era("BeforeCommonEra", "B.C.E.", Float.MIN_VALUE, false), new Era("CommonEra", "C.E.", -62135709175808L, true) };
  
  private static final int JULIAN_EPOCH = -1;
  
  JulianCalendar() { setEras(eras); }
  
  public String getName() { return "julian"; }
  
  public Date getCalendarDate() { return getCalendarDate(System.currentTimeMillis(), newCalendarDate()); }
  
  public Date getCalendarDate(long paramLong) { return getCalendarDate(paramLong, newCalendarDate()); }
  
  public Date getCalendarDate(long paramLong, CalendarDate paramCalendarDate) { return (Date)super.getCalendarDate(paramLong, paramCalendarDate); }
  
  public Date getCalendarDate(long paramLong, TimeZone paramTimeZone) { return getCalendarDate(paramLong, newCalendarDate(paramTimeZone)); }
  
  public Date newCalendarDate() { return new Date(); }
  
  public Date newCalendarDate(TimeZone paramTimeZone) { return new Date(paramTimeZone); }
  
  public long getFixedDate(int paramInt1, int paramInt2, int paramInt3, BaseCalendar.Date paramDate) {
    boolean bool = (paramInt2 == 1 && paramInt3 == 1) ? 1 : 0;
    if (paramDate != null && paramDate.hit(paramInt1))
      return bool ? paramDate.getCachedJan1() : (paramDate.getCachedJan1() + getDayOfYear(paramInt1, paramInt2, paramInt3) - 1L); 
    long l1 = paramInt1;
    long l2 = -2L + 365L * (l1 - 1L) + paramInt3;
    if (l1 > 0L) {
      l2 += (l1 - 1L) / 4L;
    } else {
      l2 += CalendarUtils.floorDivide(l1 - 1L, 4L);
    } 
    if (paramInt2 > 0) {
      l2 += (367L * paramInt2 - 362L) / 12L;
    } else {
      l2 += CalendarUtils.floorDivide(367L * paramInt2 - 362L, 12L);
    } 
    if (paramInt2 > 2)
      l2 -= (CalendarUtils.isJulianLeapYear(paramInt1) ? 1L : 2L); 
    if (paramDate != null && bool)
      paramDate.setCache(paramInt1, l2, CalendarUtils.isJulianLeapYear(paramInt1) ? 366 : 365); 
    return l2;
  }
  
  public void getCalendarDateFromFixedDate(CalendarDate paramCalendarDate, long paramLong) {
    int i;
    Date date = (Date)paramCalendarDate;
    long l = 4L * (paramLong - -1L) + 1464L;
    if (l >= 0L) {
      i = (int)(l / 1461L);
    } else {
      i = (int)CalendarUtils.floorDivide(l, 1461L);
    } 
    int j = (int)(paramLong - getFixedDate(i, 1, 1, date));
    boolean bool = CalendarUtils.isJulianLeapYear(i);
    if (paramLong >= getFixedDate(i, 3, 1, date))
      j += (bool ? 1 : 2); 
    int k = 12 * j + 373;
    if (k > 0) {
      k /= 367;
    } else {
      k = CalendarUtils.floorDivide(k, 367);
    } 
    int m = (int)(paramLong - getFixedDate(i, k, 1, date)) + 1;
    int n = getDayOfWeekFromFixedDate(paramLong);
    assert n > 0 : "negative day of week " + n;
    date.setNormalizedYear(i);
    date.setMonth(k);
    date.setDayOfMonth(m);
    date.setDayOfWeek(n);
    date.setLeapYear(bool);
    date.setNormalized(true);
  }
  
  public int getYearFromFixedDate(long paramLong) { return (int)CalendarUtils.floorDivide(4L * (paramLong - -1L) + 1464L, 1461L); }
  
  public int getDayOfWeek(CalendarDate paramCalendarDate) {
    long l = getFixedDate(paramCalendarDate);
    return getDayOfWeekFromFixedDate(l);
  }
  
  boolean isLeapYear(int paramInt) { return CalendarUtils.isJulianLeapYear(paramInt); }
  
  private static class Date extends BaseCalendar.Date {
    protected Date() { setCache(1, -1L, 365); }
    
    protected Date(TimeZone param1TimeZone) {
      super(param1TimeZone);
      setCache(1, -1L, 365);
    }
    
    public Date setEra(Era param1Era) {
      if (param1Era == null)
        throw new NullPointerException(); 
      if (param1Era != eras[false] || param1Era != eras[true])
        throw new IllegalArgumentException("unknown era: " + param1Era); 
      super.setEra(param1Era);
      return this;
    }
    
    protected void setKnownEra(Era param1Era) { super.setEra(param1Era); }
    
    public int getNormalizedYear() { return (getEra() == eras[false]) ? (1 - getYear()) : getYear(); }
    
    public void setNormalizedYear(int param1Int) {
      if (param1Int <= 0) {
        setYear(1 - param1Int);
        setKnownEra(eras[0]);
      } else {
        setYear(param1Int);
        setKnownEra(eras[1]);
      } 
    }
    
    public String toString() {
      String str = super.toString();
      str = str.substring(str.indexOf('T'));
      StringBuffer stringBuffer = new StringBuffer();
      Era era = getEra();
      if (era != null) {
        String str1 = era.getAbbreviation();
        if (str1 != null)
          stringBuffer.append(str1).append(' '); 
      } 
      stringBuffer.append(getYear()).append('-');
      CalendarUtils.sprintf0d(stringBuffer, getMonth(), 2).append('-');
      CalendarUtils.sprintf0d(stringBuffer, getDayOfMonth(), 2);
      stringBuffer.append(str);
      return stringBuffer.toString();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\calendar\JulianCalendar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */