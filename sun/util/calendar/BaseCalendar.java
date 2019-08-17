package sun.util.calendar;

import java.util.TimeZone;

public abstract class BaseCalendar extends AbstractCalendar {
  public static final int JANUARY = 1;
  
  public static final int FEBRUARY = 2;
  
  public static final int MARCH = 3;
  
  public static final int APRIL = 4;
  
  public static final int MAY = 5;
  
  public static final int JUNE = 6;
  
  public static final int JULY = 7;
  
  public static final int AUGUST = 8;
  
  public static final int SEPTEMBER = 9;
  
  public static final int OCTOBER = 10;
  
  public static final int NOVEMBER = 11;
  
  public static final int DECEMBER = 12;
  
  public static final int SUNDAY = 1;
  
  public static final int MONDAY = 2;
  
  public static final int TUESDAY = 3;
  
  public static final int WEDNESDAY = 4;
  
  public static final int THURSDAY = 5;
  
  public static final int FRIDAY = 6;
  
  public static final int SATURDAY = 7;
  
  private static final int BASE_YEAR = 1970;
  
  private static final int[] FIXED_DATES = { 
      719163, 719528, 719893, 720259, 720624, 720989, 721354, 721720, 722085, 722450, 
      722815, 723181, 723546, 723911, 724276, 724642, 725007, 725372, 725737, 726103, 
      726468, 726833, 727198, 727564, 727929, 728294, 728659, 729025, 729390, 729755, 
      730120, 730486, 730851, 731216, 731581, 731947, 732312, 732677, 733042, 733408, 
      733773, 734138, 734503, 734869, 735234, 735599, 735964, 736330, 736695, 737060, 
      737425, 737791, 738156, 738521, 738886, 739252, 739617, 739982, 740347, 740713, 
      741078, 741443, 741808, 742174, 742539, 742904, 743269, 743635, 744000, 744365 };
  
  static final int[] DAYS_IN_MONTH = { 
      31, 31, 28, 31, 30, 31, 30, 31, 31, 30, 
      31, 30, 31 };
  
  static final int[] ACCUMULATED_DAYS_IN_MONTH = { 
      -30, 0, 31, 59, 90, 120, 151, 181, 212, 243, 
      273, 304, 334 };
  
  static final int[] ACCUMULATED_DAYS_IN_MONTH_LEAP = { 
      -30, 0, 31, 60, 91, 121, 152, 182, 213, 244, 
      274, 305, 335 };
  
  public boolean validate(CalendarDate paramCalendarDate) {
    Date date = (Date)paramCalendarDate;
    if (date.isNormalized())
      return true; 
    int i = date.getMonth();
    if (i < 1 || i > 12)
      return false; 
    int j = date.getDayOfMonth();
    if (j <= 0 || j > getMonthLength(date.getNormalizedYear(), i))
      return false; 
    int k = date.getDayOfWeek();
    if (k != Integer.MIN_VALUE && k != getDayOfWeek(date))
      return false; 
    if (!validateTime(paramCalendarDate))
      return false; 
    date.setNormalized(true);
    return true;
  }
  
  public boolean normalize(CalendarDate paramCalendarDate) {
    if (paramCalendarDate.isNormalized())
      return true; 
    Date date = (Date)paramCalendarDate;
    TimeZone timeZone = date.getZone();
    if (timeZone != null) {
      getTime(paramCalendarDate);
      return true;
    } 
    int i = normalizeTime(date);
    normalizeMonth(date);
    long l = date.getDayOfMonth() + i;
    int j = date.getMonth();
    int k = date.getNormalizedYear();
    int m = getMonthLength(k, j);
    if (l <= 0L || l > m) {
      if (l <= 0L && l > -28L) {
        m = getMonthLength(k, --j);
        l += m;
        date.setDayOfMonth((int)l);
        if (j == 0) {
          j = 12;
          date.setNormalizedYear(k - 1);
        } 
        date.setMonth(j);
      } else if (l > m && l < (m + 28)) {
        l -= m;
        j++;
        date.setDayOfMonth((int)l);
        if (j > 12) {
          date.setNormalizedYear(k + 1);
          j = 1;
        } 
        date.setMonth(j);
      } else {
        long l1 = l + getFixedDate(k, j, 1, date) - 1L;
        getCalendarDateFromFixedDate(date, l1);
      } 
    } else {
      date.setDayOfWeek(getDayOfWeek(date));
    } 
    paramCalendarDate.setLeapYear(isLeapYear(date.getNormalizedYear()));
    paramCalendarDate.setZoneOffset(0);
    paramCalendarDate.setDaylightSaving(0);
    date.setNormalized(true);
    return true;
  }
  
  void normalizeMonth(CalendarDate paramCalendarDate) {
    Date date = (Date)paramCalendarDate;
    int i = date.getNormalizedYear();
    long l = date.getMonth();
    if (l <= 0L) {
      long l1 = 1L - l;
      i -= (int)(l1 / 12L + 1L);
      l = 13L - l1 % 12L;
      date.setNormalizedYear(i);
      date.setMonth((int)l);
    } else if (l > 12L) {
      i += (int)((l - 1L) / 12L);
      l = (l - 1L) % 12L + 1L;
      date.setNormalizedYear(i);
      date.setMonth((int)l);
    } 
  }
  
  public int getYearLength(CalendarDate paramCalendarDate) { return isLeapYear(((Date)paramCalendarDate).getNormalizedYear()) ? 366 : 365; }
  
  public int getYearLengthInMonths(CalendarDate paramCalendarDate) { return 12; }
  
  public int getMonthLength(CalendarDate paramCalendarDate) {
    Date date = (Date)paramCalendarDate;
    int i = date.getMonth();
    if (i < 1 || i > 12)
      throw new IllegalArgumentException("Illegal month value: " + i); 
    return getMonthLength(date.getNormalizedYear(), i);
  }
  
  private int getMonthLength(int paramInt1, int paramInt2) {
    int i = DAYS_IN_MONTH[paramInt2];
    if (paramInt2 == 2 && isLeapYear(paramInt1))
      i++; 
    return i;
  }
  
  public long getDayOfYear(CalendarDate paramCalendarDate) { return getDayOfYear(((Date)paramCalendarDate).getNormalizedYear(), paramCalendarDate.getMonth(), paramCalendarDate.getDayOfMonth()); }
  
  final long getDayOfYear(int paramInt1, int paramInt2, int paramInt3) { return paramInt3 + (isLeapYear(paramInt1) ? ACCUMULATED_DAYS_IN_MONTH_LEAP[paramInt2] : ACCUMULATED_DAYS_IN_MONTH[paramInt2]); }
  
  public long getFixedDate(CalendarDate paramCalendarDate) {
    if (!paramCalendarDate.isNormalized())
      normalizeMonth(paramCalendarDate); 
    return getFixedDate(((Date)paramCalendarDate).getNormalizedYear(), paramCalendarDate.getMonth(), paramCalendarDate.getDayOfMonth(), (Date)paramCalendarDate);
  }
  
  public long getFixedDate(int paramInt1, int paramInt2, int paramInt3, Date paramDate) {
    boolean bool = (paramInt2 == 1 && paramInt3 == 1) ? 1 : 0;
    if (paramDate != null && paramDate.hit(paramInt1))
      return bool ? paramDate.getCachedJan1() : (paramDate.getCachedJan1() + getDayOfYear(paramInt1, paramInt2, paramInt3) - 1L); 
    int i = paramInt1 - 1970;
    if (i >= 0 && i < FIXED_DATES.length) {
      long l = FIXED_DATES[i];
      if (paramDate != null)
        paramDate.setCache(paramInt1, l, isLeapYear(paramInt1) ? 366 : 365); 
      return bool ? l : (l + getDayOfYear(paramInt1, paramInt2, paramInt3) - 1L);
    } 
    long l1 = paramInt1 - 1L;
    long l2 = paramInt3;
    if (l1 >= 0L) {
      l2 += 365L * l1 + l1 / 4L - l1 / 100L + l1 / 400L + ((367 * paramInt2 - 362) / 12);
    } else {
      l2 += 365L * l1 + CalendarUtils.floorDivide(l1, 4L) - CalendarUtils.floorDivide(l1, 100L) + CalendarUtils.floorDivide(l1, 400L) + CalendarUtils.floorDivide(367 * paramInt2 - 362, 12);
    } 
    if (paramInt2 > 2)
      l2 -= (isLeapYear(paramInt1) ? 1L : 2L); 
    if (paramDate != null && bool)
      paramDate.setCache(paramInt1, l2, isLeapYear(paramInt1) ? 366 : 365); 
    return l2;
  }
  
  public void getCalendarDateFromFixedDate(CalendarDate paramCalendarDate, long paramLong) {
    boolean bool;
    long l1;
    int i;
    Date date = (Date)paramCalendarDate;
    if (date.hit(paramLong)) {
      i = date.getCachedYear();
      l1 = date.getCachedJan1();
      bool = isLeapYear(i);
    } else {
      i = getGregorianYearFromFixedDate(paramLong);
      l1 = getFixedDate(i, 1, 1, null);
      bool = isLeapYear(i);
      date.setCache(i, l1, bool ? 366 : 365);
    } 
    int j = (int)(paramLong - l1);
    long l2 = l1 + 31L + 28L;
    if (bool)
      l2++; 
    if (paramLong >= l2)
      j += (bool ? 1 : 2); 
    int k = 12 * j + 373;
    if (k > 0) {
      k /= 367;
    } else {
      k = CalendarUtils.floorDivide(k, 367);
    } 
    long l3 = l1 + ACCUMULATED_DAYS_IN_MONTH[k];
    if (bool && k >= 3)
      l3++; 
    int m = (int)(paramLong - l3) + 1;
    int n = getDayOfWeekFromFixedDate(paramLong);
    assert n > 0 : "negative day of week " + n;
    date.setNormalizedYear(i);
    date.setMonth(k);
    date.setDayOfMonth(m);
    date.setDayOfWeek(n);
    date.setLeapYear(bool);
    date.setNormalized(true);
  }
  
  public int getDayOfWeek(CalendarDate paramCalendarDate) {
    long l = getFixedDate(paramCalendarDate);
    return getDayOfWeekFromFixedDate(l);
  }
  
  public static final int getDayOfWeekFromFixedDate(long paramLong) { return (paramLong >= 0L) ? ((int)(paramLong % 7L) + 1) : ((int)CalendarUtils.mod(paramLong, 7L) + 1); }
  
  public int getYearFromFixedDate(long paramLong) { return getGregorianYearFromFixedDate(paramLong); }
  
  final int getGregorianYearFromFixedDate(long paramLong) {
    int m;
    int k;
    int j;
    int i;
    if (paramLong > 0L) {
      long l = paramLong - 1L;
      i = (int)(l / 146097L);
      int i1 = (int)(l % 146097L);
      j = i1 / 36524;
      int i2 = i1 % 36524;
      k = i2 / 1461;
      int i3 = i2 % 1461;
      m = i3 / 365;
      int i4 = i3 % 365 + 1;
    } else {
      long l = paramLong - 1L;
      i = (int)CalendarUtils.floorDivide(l, 146097L);
      int i1 = (int)CalendarUtils.mod(l, 146097L);
      j = CalendarUtils.floorDivide(i1, 36524);
      int i2 = CalendarUtils.mod(i1, 36524);
      k = CalendarUtils.floorDivide(i2, 1461);
      int i3 = CalendarUtils.mod(i2, 1461);
      m = CalendarUtils.floorDivide(i3, 365);
      int i4 = CalendarUtils.mod(i3, 365) + 1;
    } 
    int n = 400 * i + 100 * j + 4 * k + m;
    if (j != 4 && m != 4)
      n++; 
    return n;
  }
  
  protected boolean isLeapYear(CalendarDate paramCalendarDate) { return isLeapYear(((Date)paramCalendarDate).getNormalizedYear()); }
  
  boolean isLeapYear(int paramInt) { return CalendarUtils.isGregorianLeapYear(paramInt); }
  
  public static abstract class Date extends CalendarDate {
    int cachedYear = 2004;
    
    long cachedFixedDateJan1 = 731581L;
    
    long cachedFixedDateNextJan1 = this.cachedFixedDateJan1 + 366L;
    
    protected Date() {}
    
    protected Date(TimeZone param1TimeZone) { super(param1TimeZone); }
    
    public Date setNormalizedDate(int param1Int1, int param1Int2, int param1Int3) {
      setNormalizedYear(param1Int1);
      setMonth(param1Int2).setDayOfMonth(param1Int3);
      return this;
    }
    
    public abstract int getNormalizedYear();
    
    public abstract void setNormalizedYear(int param1Int);
    
    protected final boolean hit(int param1Int) { return (param1Int == this.cachedYear); }
    
    protected final boolean hit(long param1Long) { return (param1Long >= this.cachedFixedDateJan1 && param1Long < this.cachedFixedDateNextJan1); }
    
    protected int getCachedYear() { return this.cachedYear; }
    
    protected long getCachedJan1() { return this.cachedFixedDateJan1; }
    
    protected void setCache(int param1Int1, long param1Long, int param1Int2) {
      this.cachedYear = param1Int1;
      this.cachedFixedDateJan1 = param1Long;
      this.cachedFixedDateNextJan1 = param1Long + param1Int2;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\calendar\BaseCalendar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */