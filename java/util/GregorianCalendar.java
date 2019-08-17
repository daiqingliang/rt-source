package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import sun.util.calendar.BaseCalendar;
import sun.util.calendar.CalendarDate;
import sun.util.calendar.CalendarSystem;
import sun.util.calendar.CalendarUtils;
import sun.util.calendar.Era;
import sun.util.calendar.Gregorian;
import sun.util.calendar.JulianCalendar;
import sun.util.calendar.ZoneInfo;

public class GregorianCalendar extends Calendar {
  public static final int BC = 0;
  
  static final int BCE = 0;
  
  public static final int AD = 1;
  
  static final int CE = 1;
  
  private static final int EPOCH_OFFSET = 719163;
  
  private static final int EPOCH_YEAR = 1970;
  
  static final int[] MONTH_LENGTH = { 
      31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 
      30, 31 };
  
  static final int[] LEAP_MONTH_LENGTH = { 
      31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 
      30, 31 };
  
  private static final int ONE_SECOND = 1000;
  
  private static final int ONE_MINUTE = 60000;
  
  private static final int ONE_HOUR = 3600000;
  
  private static final long ONE_DAY = 86400000L;
  
  private static final long ONE_WEEK = 604800000L;
  
  static final int[] MIN_VALUES = { 
      0, 1, 0, 1, 0, 1, 1, 1, 1, 0, 
      0, 0, 0, 0, 0, -46800000, 0 };
  
  static final int[] LEAST_MAX_VALUES = { 
      1, 292269054, 11, 52, 4, 28, 365, 7, 4, 1, 
      11, 23, 59, 59, 999, 50400000, 1200000 };
  
  static final int[] MAX_VALUES = { 
      1, 292278994, 11, 53, 6, 31, 366, 7, 6, 1, 
      11, 23, 59, 59, 999, 50400000, 7200000 };
  
  static final long serialVersionUID = -8125100834729963327L;
  
  private static final Gregorian gcal = CalendarSystem.getGregorianCalendar();
  
  private static JulianCalendar jcal;
  
  private static Era[] jeras;
  
  static final long DEFAULT_GREGORIAN_CUTOVER = -12219292800000L;
  
  private long gregorianCutover = -12219292800000L;
  
  private long gregorianCutoverDate = 577736L;
  
  private int gregorianCutoverYear = 1582;
  
  private int gregorianCutoverYearJulian = 1582;
  
  private BaseCalendar.Date gdate;
  
  private BaseCalendar.Date cdate;
  
  private BaseCalendar calsys;
  
  private int[] zoneOffsets;
  
  private int[] originalFields;
  
  private long cachedFixedDate = Float.MIN_VALUE;
  
  public GregorianCalendar() {
    this(TimeZone.getDefaultRef(), Locale.getDefault(Locale.Category.FORMAT));
    setZoneShared(true);
  }
  
  public GregorianCalendar(TimeZone paramTimeZone) { this(paramTimeZone, Locale.getDefault(Locale.Category.FORMAT)); }
  
  public GregorianCalendar(Locale paramLocale) {
    this(TimeZone.getDefaultRef(), paramLocale);
    setZoneShared(true);
  }
  
  public GregorianCalendar(TimeZone paramTimeZone, Locale paramLocale) {
    super(paramTimeZone, paramLocale);
    this.gdate = gcal.newCalendarDate(paramTimeZone);
    setTimeInMillis(System.currentTimeMillis());
  }
  
  public GregorianCalendar(int paramInt1, int paramInt2, int paramInt3) { this(paramInt1, paramInt2, paramInt3, 0, 0, 0, 0); }
  
  public GregorianCalendar(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { this(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, 0, 0); }
  
  public GregorianCalendar(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) { this(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, 0); }
  
  GregorianCalendar(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
    this.gdate = gcal.newCalendarDate(getZone());
    set(1, paramInt1);
    set(2, paramInt2);
    set(5, paramInt3);
    if (paramInt4 >= 12 && paramInt4 <= 23) {
      internalSet(9, 1);
      internalSet(10, paramInt4 - 12);
    } else {
      internalSet(10, paramInt4);
    } 
    setFieldsComputed(1536);
    set(11, paramInt4);
    set(12, paramInt5);
    set(13, paramInt6);
    internalSet(14, paramInt7);
  }
  
  GregorianCalendar(TimeZone paramTimeZone, Locale paramLocale, boolean paramBoolean) {
    super(paramTimeZone, paramLocale);
    this.gdate = gcal.newCalendarDate(getZone());
  }
  
  public void setGregorianChange(Date paramDate) {
    long l = paramDate.getTime();
    if (l == this.gregorianCutover)
      return; 
    complete();
    setGregorianChange(l);
  }
  
  private void setGregorianChange(long paramLong) {
    this.gregorianCutover = paramLong;
    this.gregorianCutoverDate = CalendarUtils.floorDivide(paramLong, 86400000L) + 719163L;
    if (paramLong == Float.MAX_VALUE)
      this.gregorianCutoverDate++; 
    BaseCalendar.Date date = getGregorianCutoverDate();
    this.gregorianCutoverYear = date.getYear();
    BaseCalendar baseCalendar = getJulianCalendarSystem();
    date = (BaseCalendar.Date)baseCalendar.newCalendarDate(TimeZone.NO_TIMEZONE);
    baseCalendar.getCalendarDateFromFixedDate(date, this.gregorianCutoverDate - 1L);
    this.gregorianCutoverYearJulian = date.getNormalizedYear();
    if (this.time < this.gregorianCutover)
      setUnnormalized(); 
  }
  
  public final Date getGregorianChange() { return new Date(this.gregorianCutover); }
  
  public boolean isLeapYear(int paramInt) {
    boolean bool;
    if ((paramInt & 0x3) != 0)
      return false; 
    if (paramInt > this.gregorianCutoverYear)
      return (paramInt % 100 != 0 || paramInt % 400 == 0); 
    if (paramInt < this.gregorianCutoverYearJulian)
      return true; 
    if (this.gregorianCutoverYear == this.gregorianCutoverYearJulian) {
      BaseCalendar.Date date = getCalendarDate(this.gregorianCutoverDate);
      bool = (date.getMonth() < 3) ? 1 : 0;
    } else {
      bool = (paramInt == this.gregorianCutoverYear) ? 1 : 0;
    } 
    return bool ? ((paramInt % 100 != 0 || paramInt % 400 == 0)) : true;
  }
  
  public String getCalendarType() { return "gregory"; }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof GregorianCalendar && super.equals(paramObject) && this.gregorianCutover == ((GregorianCalendar)paramObject).gregorianCutover); }
  
  public int hashCode() { return super.hashCode() ^ (int)this.gregorianCutoverDate; }
  
  public void add(int paramInt1, int paramInt2) {
    if (paramInt2 == 0)
      return; 
    if (paramInt1 < 0 || paramInt1 >= 15)
      throw new IllegalArgumentException(); 
    complete();
    if (paramInt1 == 1) {
      int i = internalGet(1);
      if (internalGetEra() == 1) {
        i += paramInt2;
        if (i > 0) {
          set(1, i);
        } else {
          set(1, 1 - i);
          set(0, 0);
        } 
      } else {
        i -= paramInt2;
        if (i > 0) {
          set(1, i);
        } else {
          set(1, 1 - i);
          set(0, 1);
        } 
      } 
      pinDayOfMonth();
    } else if (paramInt1 == 2) {
      int k;
      int i = internalGet(2) + paramInt2;
      int j = internalGet(1);
      if (i >= 0) {
        k = i / 12;
      } else {
        k = (i + 1) / 12 - 1;
      } 
      if (k != 0)
        if (internalGetEra() == 1) {
          j += k;
          if (j > 0) {
            set(1, j);
          } else {
            set(1, 1 - j);
            set(0, 0);
          } 
        } else {
          j -= k;
          if (j > 0) {
            set(1, j);
          } else {
            set(1, 1 - j);
            set(0, 1);
          } 
        }  
      if (i >= 0) {
        set(2, i % 12);
      } else {
        i %= 12;
        if (i < 0)
          i += 12; 
        set(2, 0 + i);
      } 
      pinDayOfMonth();
    } else if (paramInt1 == 0) {
      int i = internalGet(0) + paramInt2;
      if (i < 0)
        i = 0; 
      if (i > 1)
        i = 1; 
      set(0, i);
    } else {
      long l1 = paramInt2;
      long l2 = 0L;
      switch (paramInt1) {
        case 10:
        case 11:
          l1 *= 3600000L;
          break;
        case 12:
          l1 *= 60000L;
          break;
        case 13:
          l1 *= 1000L;
          break;
        case 3:
        case 4:
        case 8:
          l1 *= 7L;
          break;
        case 9:
          l1 = (paramInt2 / 2);
          l2 = (12 * paramInt2 % 2);
          break;
      } 
      if (paramInt1 >= 10) {
        setTimeInMillis(this.time + l1);
        return;
      } 
      long l3 = getCurrentFixedDate();
      l2 += internalGet(11);
      l2 *= 60L;
      l2 += internalGet(12);
      l2 *= 60L;
      l2 += internalGet(13);
      l2 *= 1000L;
      l2 += internalGet(14);
      if (l2 >= 86400000L) {
        l3++;
        l2 -= 86400000L;
      } else if (l2 < 0L) {
        l3--;
        l2 += 86400000L;
      } 
      l3 += l1;
      int i = internalGet(15) + internalGet(16);
      setTimeInMillis((l3 - 719163L) * 86400000L + l2 - i);
      i -= internalGet(15) + internalGet(16);
      if (i != 0) {
        setTimeInMillis(this.time + i);
        long l = getCurrentFixedDate();
        if (l != l3)
          setTimeInMillis(this.time - i); 
      } 
    } 
  }
  
  public void roll(int paramInt, boolean paramBoolean) { roll(paramInt, paramBoolean ? 1 : -1); }
  
  public void roll(int paramInt1, int paramInt2) {
    int i9;
    long l6;
    int i8;
    BaseCalendar.Date date4;
    BaseCalendar.Date date3;
    Gregorian gregorian;
    long l5;
    int i7;
    int i5;
    int i6;
    int i4;
    BaseCalendar baseCalendar;
    BaseCalendar.Date date2;
    int i3;
    BaseCalendar.Date date1;
    int i2;
    int i1;
    long l4;
    long l3;
    CalendarDate calendarDate;
    int n;
    long l2;
    int m;
    boolean bool;
    int k;
    long l1;
    if (paramInt2 == 0)
      return; 
    if (paramInt1 < 0 || paramInt1 >= 15)
      throw new IllegalArgumentException(); 
    complete();
    int i = getMinimum(paramInt1);
    int j = getMaximum(paramInt1);
    switch (paramInt1) {
      case 10:
      case 11:
        k = j + 1;
        m = internalGet(paramInt1);
        n = (m + paramInt2) % k;
        if (n < 0)
          n += k; 
        this.time += (3600000 * (n - m));
        calendarDate = this.calsys.getCalendarDate(this.time, getZone());
        if (internalGet(5) != calendarDate.getDayOfMonth()) {
          calendarDate.setDate(internalGet(1), internalGet(2) + 1, internalGet(5));
          if (paramInt1 == 10) {
            assert internalGet(9) == 1;
            calendarDate.addHours(12);
          } 
          this.time = this.calsys.getTime(calendarDate);
        } 
        l4 = calendarDate.getHours();
        internalSet(paramInt1, l4 % k);
        if (paramInt1 == 10) {
          internalSet(11, l4);
        } else {
          internalSet(9, l4 / 12);
          internalSet(10, l4 % 12);
        } 
        i4 = calendarDate.getZoneOffset();
        i6 = calendarDate.getDaylightSaving();
        internalSet(15, i4 - i6);
        internalSet(16, i6);
        return;
      case 2:
        if (!isCutoverYear(this.cdate.getNormalizedYear())) {
          k = (internalGet(2) + paramInt2) % 12;
          if (k < 0)
            k += 12; 
          set(2, k);
          m = monthLength(k);
          if (internalGet(5) > m)
            set(5, m); 
        } else {
          k = getActualMaximum(2) + 1;
          m = (internalGet(2) + paramInt2) % k;
          if (m < 0)
            m += k; 
          set(2, m);
          n = getActualMaximum(5);
          if (internalGet(5) > n)
            set(5, n); 
        } 
        return;
      case 3:
        k = this.cdate.getNormalizedYear();
        j = getActualMaximum(3);
        set(7, internalGet(7));
        m = internalGet(3);
        n = m + paramInt2;
        if (!isCutoverYear(k)) {
          int i10 = getWeekYear();
          if (i10 == k) {
            if (n > i && n < j) {
              set(3, n);
              return;
            } 
            long l7 = getCurrentFixedDate();
            long l8 = l7 - (7 * (m - i));
            if (this.calsys.getYearFromFixedDate(l8) != k)
              i++; 
            l7 += (7 * (j - internalGet(3)));
            if (this.calsys.getYearFromFixedDate(l7) != k)
              j--; 
          } else if (i10 > k) {
            if (paramInt2 < 0)
              paramInt2++; 
            m = j;
          } else {
            if (paramInt2 > 0)
              paramInt2 -= m - j; 
            m = i;
          } 
          set(paramInt1, getRolledValue(m, paramInt2, i, j));
          return;
        } 
        l3 = getCurrentFixedDate();
        if (this.gregorianCutoverYear == this.gregorianCutoverYearJulian) {
          baseCalendar = getCutoverCalendarSystem();
        } else if (k == this.gregorianCutoverYear) {
          baseCalendar = gcal;
        } else {
          baseCalendar = getJulianCalendarSystem();
        } 
        i5 = l3 - (7 * (m - i));
        if (baseCalendar.getYearFromFixedDate(i5) != k)
          i++; 
        l3 += (7 * (j - m));
        baseCalendar = (l3 >= this.gregorianCutoverDate) ? gcal : getJulianCalendarSystem();
        if (baseCalendar.getYearFromFixedDate(l3) != k)
          j--; 
        n = getRolledValue(m, paramInt2, i, j) - 1;
        date3 = getCalendarDate(i5 + (n * 7));
        set(2, date3.getMonth() - 1);
        set(5, date3.getDayOfMonth());
        return;
      case 4:
        bool = isCutoverYear(this.cdate.getNormalizedYear());
        m = internalGet(7) - getFirstDayOfWeek();
        if (m < 0)
          m += 7; 
        l2 = getCurrentFixedDate();
        if (bool) {
          long l = getFixedDateMonth1(this.cdate, l2);
          int i10 = actualMonthLength();
        } else {
          l4 = l2 - internalGet(5) + 1L;
          i5 = this.calsys.getMonthLength(this.cdate);
        } 
        l5 = BaseCalendar.getDayOfWeekDateOnOrBefore(l4 + 6L, getFirstDayOfWeek());
        if ((int)(l5 - l4) >= getMinimalDaysInFirstWeek())
          l5 -= 7L; 
        j = getActualMaximum(paramInt1);
        i8 = getRolledValue(internalGet(paramInt1), paramInt2, 1, j) - 1;
        l6 = l5 + (i8 * 7) + m;
        if (l6 < l4) {
          l6 = l4;
        } else if (l6 >= l4 + i5) {
          l6 = l4 + i5 - 1L;
        } 
        if (bool) {
          BaseCalendar.Date date = getCalendarDate(l6);
          i9 = date.getDayOfMonth();
        } else {
          i9 = (int)(l6 - l4) + 1;
        } 
        set(5, i9);
        return;
      case 5:
        if (!isCutoverYear(this.cdate.getNormalizedYear())) {
          j = this.calsys.getMonthLength(this.cdate);
          break;
        } 
        l1 = getCurrentFixedDate();
        l2 = getFixedDateMonth1(this.cdate, l1);
        i2 = getRolledValue((int)(l1 - l2), paramInt2, 0, actualMonthLength() - 1);
        date2 = getCalendarDate(l2 + i2);
        assert date2.getMonth() - 1 == internalGet(2);
        set(5, date2.getDayOfMonth());
        return;
      case 6:
        j = getActualMaximum(paramInt1);
        if (!isCutoverYear(this.cdate.getNormalizedYear()))
          break; 
        l1 = getCurrentFixedDate();
        l2 = l1 - internalGet(6) + 1L;
        i2 = getRolledValue((int)(l1 - l2) + 1, paramInt2, i, j);
        date2 = getCalendarDate(l2 + i2 - 1L);
        set(2, date2.getMonth() - 1);
        set(5, date2.getDayOfMonth());
        return;
      case 7:
        if (!isCutoverYear(this.cdate.getNormalizedYear())) {
          int i10 = internalGet(3);
          if (i10 > 1 && i10 < 52) {
            set(3, i10);
            j = 7;
            break;
          } 
        } 
        paramInt2 %= 7;
        if (paramInt2 == 0)
          return; 
        l1 = getCurrentFixedDate();
        l2 = BaseCalendar.getDayOfWeekDateOnOrBefore(l1, getFirstDayOfWeek());
        l1 += paramInt2;
        if (l1 < l2) {
          l1 += 7L;
        } else if (l1 >= l2 + 7L) {
          l1 -= 7L;
        } 
        date1 = getCalendarDate(l1);
        set(0, (date1.getNormalizedYear() <= 0) ? 0 : 1);
        set(date1.getYear(), date1.getMonth() - 1, date1.getDayOfMonth());
        return;
      case 8:
        i = 1;
        if (!isCutoverYear(this.cdate.getNormalizedYear())) {
          int i10 = internalGet(5);
          m = this.calsys.getMonthLength(this.cdate);
          int i11 = m % 7;
          j = m / 7;
          int i12 = (i10 - 1) % 7;
          if (i12 < i11)
            j++; 
          set(7, internalGet(7));
          break;
        } 
        l1 = getCurrentFixedDate();
        l2 = getFixedDateMonth1(this.cdate, l1);
        i1 = actualMonthLength();
        i3 = i1 % 7;
        j = i1 / 7;
        i5 = (int)(l1 - l2) % 7;
        if (i5 < i3)
          j++; 
        i7 = getRolledValue(internalGet(paramInt1), paramInt2, i, j) - 1;
        l1 = l2 + (i7 * 7) + i5;
        gregorian = (l1 >= this.gregorianCutoverDate) ? gcal : getJulianCalendarSystem();
        date4 = (BaseCalendar.Date)gregorian.newCalendarDate(TimeZone.NO_TIMEZONE);
        gregorian.getCalendarDateFromFixedDate(date4, l1);
        set(5, date4.getDayOfMonth());
        return;
    } 
    set(paramInt1, getRolledValue(internalGet(paramInt1), paramInt2, i, j));
  }
  
  public int getMinimum(int paramInt) { return MIN_VALUES[paramInt]; }
  
  public int getMaximum(int paramInt) {
    int j;
    int i;
    GregorianCalendar gregorianCalendar;
    switch (paramInt) {
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 8:
        if (this.gregorianCutoverYear > 200)
          break; 
        gregorianCalendar = (GregorianCalendar)clone();
        gregorianCalendar.setLenient(true);
        gregorianCalendar.setTimeInMillis(this.gregorianCutover);
        i = gregorianCalendar.getActualMaximum(paramInt);
        gregorianCalendar.setTimeInMillis(this.gregorianCutover - 1L);
        j = gregorianCalendar.getActualMaximum(paramInt);
        return Math.max(MAX_VALUES[paramInt], Math.max(i, j));
    } 
    return MAX_VALUES[paramInt];
  }
  
  public int getGreatestMinimum(int paramInt) {
    if (paramInt == 5) {
      BaseCalendar.Date date = getGregorianCutoverDate();
      long l = getFixedDateMonth1(date, this.gregorianCutoverDate);
      date = getCalendarDate(l);
      return Math.max(MIN_VALUES[paramInt], date.getDayOfMonth());
    } 
    return MIN_VALUES[paramInt];
  }
  
  public int getLeastMaximum(int paramInt) {
    int j;
    int i;
    GregorianCalendar gregorianCalendar;
    switch (paramInt) {
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 8:
        gregorianCalendar = (GregorianCalendar)clone();
        gregorianCalendar.setLenient(true);
        gregorianCalendar.setTimeInMillis(this.gregorianCutover);
        i = gregorianCalendar.getActualMaximum(paramInt);
        gregorianCalendar.setTimeInMillis(this.gregorianCutover - 1L);
        j = gregorianCalendar.getActualMaximum(paramInt);
        return Math.min(LEAST_MAX_VALUES[paramInt], Math.min(i, j));
    } 
    return LEAST_MAX_VALUES[paramInt];
  }
  
  public int getActualMinimum(int paramInt) {
    if (paramInt == 5) {
      GregorianCalendar gregorianCalendar = getNormalizedCalendar();
      int i = gregorianCalendar.cdate.getNormalizedYear();
      if (i == this.gregorianCutoverYear || i == this.gregorianCutoverYearJulian) {
        long l = getFixedDateMonth1(gregorianCalendar.cdate, gregorianCalendar.calsys.getFixedDate(gregorianCalendar.cdate));
        BaseCalendar.Date date = getCalendarDate(l);
        return date.getDayOfMonth();
      } 
    } 
    return getMinimum(paramInt);
  }
  
  public int getActualMaximum(int paramInt) {
    int i1;
    int n;
    int m;
    long l;
    int k;
    int j;
    if ((0x1FE81 & 1 << paramInt) != 0)
      return getMaximum(paramInt); 
    GregorianCalendar gregorianCalendar = getNormalizedCalendar();
    BaseCalendar.Date date = gregorianCalendar.cdate;
    BaseCalendar baseCalendar = gregorianCalendar.calsys;
    int i = date.getNormalizedYear();
    null = -1;
    switch (paramInt) {
      case 2:
        if (!gregorianCalendar.isCutoverYear(i)) {
          null = 11;
        } else {
          do {
            k = gcal.getFixedDate(++i, 1, 1, null);
          } while (k < this.gregorianCutoverDate);
          BaseCalendar.Date date1 = (BaseCalendar.Date)date.clone();
          baseCalendar.getCalendarDateFromFixedDate(date1, k - 1L);
          null = date1.getMonth() - 1;
        } 
        return null;
      case 5:
        null = baseCalendar.getMonthLength(date);
        if (gregorianCalendar.isCutoverYear(i) && date.getDayOfMonth() != null) {
          k = gregorianCalendar.getCurrentFixedDate();
          if (k < this.gregorianCutoverDate) {
            int i2 = gregorianCalendar.actualMonthLength();
            long l1 = gregorianCalendar.getFixedDateMonth1(gregorianCalendar.cdate, k) + i2 - 1L;
            BaseCalendar.Date date1 = gregorianCalendar.getCalendarDate(l1);
            null = date1.getDayOfMonth();
          } 
        } 
        return null;
      case 6:
        if (!gregorianCalendar.isCutoverYear(i)) {
          null = baseCalendar.getYearLength(date);
        } else {
          if (this.gregorianCutoverYear == this.gregorianCutoverYearJulian) {
            BaseCalendar baseCalendar1 = gregorianCalendar.getCutoverCalendarSystem();
            k = baseCalendar1.getFixedDate(i, 1, 1, null);
          } else if (i == this.gregorianCutoverYearJulian) {
            k = baseCalendar.getFixedDate(i, 1, 1, null);
          } else {
            k = this.gregorianCutoverDate;
          } 
          long l1 = gcal.getFixedDate(++i, 1, 1, null);
          if (l1 < this.gregorianCutoverDate)
            l1 = this.gregorianCutoverDate; 
          assert k <= baseCalendar.getFixedDate(date.getNormalizedYear(), date.getMonth(), date.getDayOfMonth(), date);
          assert l1 >= baseCalendar.getFixedDate(date.getNormalizedYear(), date.getMonth(), date.getDayOfMonth(), date);
          null = (int)(l1 - k);
        } 
        return null;
      case 3:
        if (!gregorianCalendar.isCutoverYear(i)) {
          CalendarDate calendarDate = baseCalendar.newCalendarDate(TimeZone.NO_TIMEZONE);
          calendarDate.setDate(date.getYear(), 1, 1);
          m = baseCalendar.getDayOfWeek(calendarDate);
          m -= getFirstDayOfWeek();
          if (m < 0)
            m += 7; 
          null = 52;
          int i2 = m + getMinimalDaysInFirstWeek() - 1;
          if (i2 == 6 || (date.isLeapYear() && (i2 == 5 || i2 == 12)))
            null++; 
        } else {
          if (gregorianCalendar == this)
            gregorianCalendar = (GregorianCalendar)gregorianCalendar.clone(); 
          k = getActualMaximum(6);
          gregorianCalendar.set(6, k);
          null = gregorianCalendar.get(3);
          if (internalGet(1) != gregorianCalendar.getWeekYear()) {
            gregorianCalendar.set(6, k - 7);
            null = gregorianCalendar.get(3);
          } 
        } 
        return null;
      case 4:
        if (!gregorianCalendar.isCutoverYear(i)) {
          CalendarDate calendarDate = baseCalendar.newCalendarDate(null);
          calendarDate.setDate(date.getYear(), date.getMonth(), 1);
          m = baseCalendar.getDayOfWeek(calendarDate);
          int i2 = baseCalendar.getMonthLength(calendarDate);
          m -= getFirstDayOfWeek();
          if (m < 0)
            m += 7; 
          int i3 = 7 - m;
          null = 3;
          if (i3 >= getMinimalDaysInFirstWeek())
            null++; 
          i2 -= i3 + 21;
          if (i2 > 0) {
            null++;
            if (i2 > 7)
              null++; 
          } 
        } else {
          if (gregorianCalendar == this)
            gregorianCalendar = (GregorianCalendar)gregorianCalendar.clone(); 
          k = gregorianCalendar.internalGet(1);
          m = gregorianCalendar.internalGet(2);
          do {
            null = gregorianCalendar.get(4);
            gregorianCalendar.add(4, 1);
          } while (gregorianCalendar.get(1) == k && gregorianCalendar.get(2) == m);
        } 
        return null;
      case 8:
        n = date.getDayOfWeek();
        if (!gregorianCalendar.isCutoverYear(i)) {
          BaseCalendar.Date date1 = (BaseCalendar.Date)date.clone();
          k = baseCalendar.getMonthLength(date1);
          date1.setDayOfMonth(1);
          baseCalendar.normalize(date1);
          m = date1.getDayOfWeek();
        } else {
          if (gregorianCalendar == this)
            gregorianCalendar = (GregorianCalendar)clone(); 
          k = gregorianCalendar.actualMonthLength();
          gregorianCalendar.set(5, gregorianCalendar.getActualMinimum(5));
          m = gregorianCalendar.get(7);
        } 
        i1 = n - m;
        if (i1 < 0)
          i1 += 7; 
        k -= i1;
        return (k + 6) / 7;
      case 1:
        if (gregorianCalendar == this)
          gregorianCalendar = (GregorianCalendar)clone(); 
        l = gregorianCalendar.getYearOffsetInMillis();
        if (gregorianCalendar.internalGetEra() == 1) {
          gregorianCalendar.setTimeInMillis(Float.MAX_VALUE);
          j = gregorianCalendar.get(1);
          long l1 = gregorianCalendar.getYearOffsetInMillis();
          if (l > l1)
            j--; 
        } else {
          Gregorian gregorian = (gregorianCalendar.getTimeInMillis() >= this.gregorianCutover) ? gcal : getJulianCalendarSystem();
          CalendarDate calendarDate = gregorian.getCalendarDate(Float.MIN_VALUE, getZone());
          long l1 = (baseCalendar.getDayOfYear(calendarDate) - 1L) * 24L + calendarDate.getHours();
          l1 *= 60L;
          l1 += calendarDate.getMinutes();
          l1 *= 60L;
          l1 += calendarDate.getSeconds();
          l1 *= 1000L;
          l1 += calendarDate.getMillis();
          j = calendarDate.getYear();
          if (j <= 0) {
            assert gregorian == gcal;
            j = 1 - j;
          } 
          if (l < l1)
            j--; 
        } 
        return j;
    } 
    throw new ArrayIndexOutOfBoundsException(paramInt);
  }
  
  private long getYearOffsetInMillis() {
    long l = ((internalGet(6) - 1) * 24);
    l += internalGet(11);
    l *= 60L;
    l += internalGet(12);
    l *= 60L;
    l += internalGet(13);
    l *= 1000L;
    return l + internalGet(14) - (internalGet(15) + internalGet(16));
  }
  
  public Object clone() {
    GregorianCalendar gregorianCalendar = (GregorianCalendar)super.clone();
    gregorianCalendar.gdate = (BaseCalendar.Date)this.gdate.clone();
    if (this.cdate != null)
      if (this.cdate != this.gdate) {
        gregorianCalendar.cdate = (BaseCalendar.Date)this.cdate.clone();
      } else {
        gregorianCalendar.cdate = gregorianCalendar.gdate;
      }  
    gregorianCalendar.originalFields = null;
    gregorianCalendar.zoneOffsets = null;
    return gregorianCalendar;
  }
  
  public TimeZone getTimeZone() {
    TimeZone timeZone = super.getTimeZone();
    this.gdate.setZone(timeZone);
    if (this.cdate != null && this.cdate != this.gdate)
      this.cdate.setZone(timeZone); 
    return timeZone;
  }
  
  public void setTimeZone(TimeZone paramTimeZone) {
    super.setTimeZone(paramTimeZone);
    this.gdate.setZone(paramTimeZone);
    if (this.cdate != null && this.cdate != this.gdate)
      this.cdate.setZone(paramTimeZone); 
  }
  
  public final boolean isWeekDateSupported() { return true; }
  
  public int getWeekYear() {
    int i = get(1);
    if (internalGetEra() == 0)
      i = 1 - i; 
    if (i > this.gregorianCutoverYear + 1) {
      int i2 = internalGet(3);
      if (internalGet(2) == 0) {
        if (i2 >= 52)
          i--; 
      } else if (i2 == 1) {
        i++;
      } 
      return i;
    } 
    int j = internalGet(6);
    int k = getActualMaximum(6);
    int m = getMinimalDaysInFirstWeek();
    if (j > m && j < k - 6)
      return i; 
    GregorianCalendar gregorianCalendar = (GregorianCalendar)clone();
    gregorianCalendar.setLenient(true);
    gregorianCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
    gregorianCalendar.set(6, 1);
    gregorianCalendar.complete();
    int n = getFirstDayOfWeek() - gregorianCalendar.get(7);
    if (n != 0) {
      if (n < 0)
        n += 7; 
      gregorianCalendar.add(6, n);
    } 
    int i1 = gregorianCalendar.get(6);
    if (j < i1) {
      if (i1 <= m)
        i--; 
    } else {
      gregorianCalendar.set(1, i + 1);
      gregorianCalendar.set(6, 1);
      gregorianCalendar.complete();
      int i2 = getFirstDayOfWeek() - gregorianCalendar.get(7);
      if (i2 != 0) {
        if (i2 < 0)
          i2 += 7; 
        gregorianCalendar.add(6, i2);
      } 
      i1 = gregorianCalendar.get(6) - 1;
      if (i1 == 0)
        i1 = 7; 
      if (i1 >= m) {
        int i3 = k - j + 1;
        if (i3 <= 7 - i1)
          i++; 
      } 
    } 
    return i;
  }
  
  public void setWeekDate(int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt3 < 1 || paramInt3 > 7)
      throw new IllegalArgumentException("invalid dayOfWeek: " + paramInt3); 
    GregorianCalendar gregorianCalendar = (GregorianCalendar)clone();
    gregorianCalendar.setLenient(true);
    int i = gregorianCalendar.get(0);
    gregorianCalendar.clear();
    gregorianCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
    gregorianCalendar.set(0, i);
    gregorianCalendar.set(1, paramInt1);
    gregorianCalendar.set(3, 1);
    gregorianCalendar.set(7, getFirstDayOfWeek());
    int j = paramInt3 - getFirstDayOfWeek();
    if (j < 0)
      j += 7; 
    j += 7 * (paramInt2 - 1);
    if (j != 0) {
      gregorianCalendar.add(6, j);
    } else {
      gregorianCalendar.complete();
    } 
    if (!isLenient() && (gregorianCalendar.getWeekYear() != paramInt1 || gregorianCalendar.internalGet(3) != paramInt2 || gregorianCalendar.internalGet(7) != paramInt3))
      throw new IllegalArgumentException(); 
    set(0, gregorianCalendar.internalGet(0));
    set(1, gregorianCalendar.internalGet(1));
    set(2, gregorianCalendar.internalGet(2));
    set(5, gregorianCalendar.internalGet(5));
    internalSet(3, paramInt2);
    complete();
  }
  
  public int getWeeksInWeekYear() {
    GregorianCalendar gregorianCalendar = getNormalizedCalendar();
    int i = gregorianCalendar.getWeekYear();
    if (i == gregorianCalendar.internalGet(1))
      return gregorianCalendar.getActualMaximum(3); 
    if (gregorianCalendar == this)
      gregorianCalendar = (GregorianCalendar)gregorianCalendar.clone(); 
    gregorianCalendar.setWeekDate(i, 2, internalGet(7));
    return gregorianCalendar.getActualMaximum(3);
  }
  
  protected void computeFields() {
    int i;
    if (isPartiallyNormalized()) {
      i = getSetStateFields();
      int j = (i ^ 0xFFFFFFFF) & 0x1FFFF;
      if (j != 0 || this.calsys == null) {
        i |= computeFields(j, i & 0x18000);
        assert i == 131071;
      } 
    } else {
      i = 131071;
      computeFields(i, 0);
    } 
    setFieldsComputed(i);
  }
  
  private int computeFields(int paramInt1, int paramInt2) {
    int k;
    int i = 0;
    TimeZone timeZone = getZone();
    if (this.zoneOffsets == null)
      this.zoneOffsets = new int[2]; 
    if (paramInt2 != 98304)
      if (timeZone instanceof ZoneInfo) {
        i = ((ZoneInfo)timeZone).getOffsets(this.time, this.zoneOffsets);
      } else {
        i = timeZone.getOffset(this.time);
        this.zoneOffsets[0] = timeZone.getRawOffset();
        this.zoneOffsets[1] = i - this.zoneOffsets[0];
      }  
    if (paramInt2 != 0) {
      if (isFieldSet(paramInt2, 15))
        this.zoneOffsets[0] = internalGet(15); 
      if (isFieldSet(paramInt2, 16))
        this.zoneOffsets[1] = internalGet(16); 
      i = this.zoneOffsets[0] + this.zoneOffsets[1];
    } 
    long l = i / 86400000L;
    int j = i % 86400000;
    l += this.time / 86400000L;
    j += (int)(this.time % 86400000L);
    if (j >= 86400000L) {
      j = (int)(j - 86400000L);
      l++;
    } else {
      while (j < 0) {
        j = (int)(j + 86400000L);
        l--;
      } 
    } 
    l += 719163L;
    byte b = 1;
    if (l >= this.gregorianCutoverDate) {
      assert this.cachedFixedDate == Float.MIN_VALUE || this.gdate.isNormalized() : "cache control: not normalized";
      assert this.cachedFixedDate == Float.MIN_VALUE || gcal.getFixedDate(this.gdate.getNormalizedYear(), this.gdate.getMonth(), this.gdate.getDayOfMonth(), this.gdate) == this.cachedFixedDate : "cache control: inconsictency, cachedFixedDate=" + this.cachedFixedDate + ", computed=" + gcal.getFixedDate(this.gdate.getNormalizedYear(), this.gdate.getMonth(), this.gdate.getDayOfMonth(), this.gdate) + ", date=" + this.gdate;
      if (l != this.cachedFixedDate) {
        gcal.getCalendarDateFromFixedDate(this.gdate, l);
        this.cachedFixedDate = l;
      } 
      k = this.gdate.getYear();
      if (k <= 0) {
        k = 1 - k;
        b = 0;
      } 
      this.calsys = gcal;
      this.cdate = this.gdate;
      assert this.cdate.getDayOfWeek() > 0 : "dow=" + this.cdate.getDayOfWeek() + ", date=" + this.cdate;
    } else {
      this.calsys = getJulianCalendarSystem();
      this.cdate = jcal.newCalendarDate(getZone());
      jcal.getCalendarDateFromFixedDate(this.cdate, l);
      Era era = this.cdate.getEra();
      if (era == jeras[false])
        b = 0; 
      k = this.cdate.getYear();
    } 
    internalSet(0, b);
    internalSet(1, k);
    int m = paramInt1 | 0x3;
    int n = this.cdate.getMonth() - 1;
    int i1 = this.cdate.getDayOfMonth();
    if ((paramInt1 & 0xA4) != 0) {
      internalSet(2, n);
      internalSet(5, i1);
      internalSet(7, this.cdate.getDayOfWeek());
      m |= 0xA4;
    } 
    if ((paramInt1 & 0x7E00) != 0) {
      if (j != 0) {
        int i2 = j / 3600000;
        internalSet(11, i2);
        internalSet(9, i2 / 12);
        internalSet(10, i2 % 12);
        int i3 = j % 3600000;
        internalSet(12, i3 / 60000);
        i3 %= 60000;
        internalSet(13, i3 / 1000);
        internalSet(14, i3 % 1000);
      } else {
        internalSet(11, 0);
        internalSet(9, 0);
        internalSet(10, 0);
        internalSet(12, 0);
        internalSet(13, 0);
        internalSet(14, 0);
      } 
      m |= 0x7E00;
    } 
    if ((paramInt1 & 0x18000) != 0) {
      internalSet(15, this.zoneOffsets[0]);
      internalSet(16, this.zoneOffsets[1]);
      m |= 0x18000;
    } 
    if ((paramInt1 & 0x158) != 0) {
      int i2 = this.cdate.getNormalizedYear();
      long l1 = this.calsys.getFixedDate(i2, 1, 1, this.cdate);
      int i3 = (int)(l - l1) + 1;
      long l2 = l - i1 + 1L;
      int i4 = 0;
      int i5 = (this.calsys == gcal) ? this.gregorianCutoverYear : this.gregorianCutoverYearJulian;
      int i6 = i1 - 1;
      if (i2 == i5) {
        if (this.gregorianCutoverYearJulian <= this.gregorianCutoverYear) {
          l1 = getFixedDateJan1(this.cdate, l);
          if (l >= this.gregorianCutoverDate)
            l2 = getFixedDateMonth1(this.cdate, l); 
        } 
        int i8 = (int)(l - l1) + 1;
        i4 = i3 - i8;
        i3 = i8;
        i6 = (int)(l - l2);
      } 
      internalSet(6, i3);
      internalSet(8, i6 / 7 + 1);
      int i7 = getWeekNumber(l1, l);
      if (i7 == 0) {
        long l3 = l1 - 1L;
        long l4 = l1 - 365L;
        if (i2 > i5 + 1) {
          if (CalendarUtils.isGregorianLeapYear(i2 - 1))
            l4--; 
        } else if (i2 <= this.gregorianCutoverYearJulian) {
          if (CalendarUtils.isJulianLeapYear(i2 - 1))
            l4--; 
        } else {
          BaseCalendar baseCalendar = this.calsys;
          int i8 = getCalendarDate(l3).getNormalizedYear();
          if (i8 == this.gregorianCutoverYear) {
            baseCalendar = getCutoverCalendarSystem();
            if (baseCalendar == jcal) {
              l4 = baseCalendar.getFixedDate(i8, 1, 1, null);
            } else {
              l4 = this.gregorianCutoverDate;
              baseCalendar = gcal;
            } 
          } else if (i8 <= this.gregorianCutoverYearJulian) {
            baseCalendar = getJulianCalendarSystem();
            l4 = baseCalendar.getFixedDate(i8, 1, 1, null);
          } 
        } 
        i7 = getWeekNumber(l4, l3);
      } else if (i2 > this.gregorianCutoverYear || i2 < this.gregorianCutoverYearJulian - 1) {
        if (i7 >= 52) {
          long l3 = l1 + 365L;
          if (this.cdate.isLeapYear())
            l3++; 
          long l4 = BaseCalendar.getDayOfWeekDateOnOrBefore(l3 + 6L, getFirstDayOfWeek());
          int i8 = (int)(l4 - l3);
          if (i8 >= getMinimalDaysInFirstWeek() && l >= l4 - 7L)
            i7 = 1; 
        } 
      } else {
        long l3;
        BaseCalendar baseCalendar = this.calsys;
        int i8 = i2 + 1;
        if (i8 == this.gregorianCutoverYearJulian + 1 && i8 < this.gregorianCutoverYear)
          i8 = this.gregorianCutoverYear; 
        if (i8 == this.gregorianCutoverYear)
          baseCalendar = getCutoverCalendarSystem(); 
        if (i8 > this.gregorianCutoverYear || this.gregorianCutoverYearJulian == this.gregorianCutoverYear || i8 == this.gregorianCutoverYearJulian) {
          l3 = baseCalendar.getFixedDate(i8, 1, 1, null);
        } else {
          l3 = this.gregorianCutoverDate;
          baseCalendar = gcal;
        } 
        long l4 = BaseCalendar.getDayOfWeekDateOnOrBefore(l3 + 6L, getFirstDayOfWeek());
        int i9 = (int)(l4 - l3);
        if (i9 >= getMinimalDaysInFirstWeek() && l >= l4 - 7L)
          i7 = 1; 
      } 
      internalSet(3, i7);
      internalSet(4, getWeekNumber(l2, l));
      m |= 0x158;
    } 
    return m;
  }
  
  private int getWeekNumber(long paramLong1, long paramLong2) {
    long l = Gregorian.getDayOfWeekDateOnOrBefore(paramLong1 + 6L, getFirstDayOfWeek());
    int i = (int)(l - paramLong1);
    assert i <= 7;
    if (i >= getMinimalDaysInFirstWeek())
      l -= 7L; 
    int j = (int)(paramLong2 - l);
    return (j >= 0) ? (j / 7 + 1) : (CalendarUtils.floorDivide(j, 7) + 1);
  }
  
  protected void computeTime() { // Byte code:
    //   0: aload_0
    //   1: invokevirtual isLenient : ()Z
    //   4: ifne -> 87
    //   7: aload_0
    //   8: getfield originalFields : [I
    //   11: ifnonnull -> 22
    //   14: aload_0
    //   15: bipush #17
    //   17: newarray int
    //   19: putfield originalFields : [I
    //   22: iconst_0
    //   23: istore_1
    //   24: iload_1
    //   25: bipush #17
    //   27: if_icmpge -> 87
    //   30: aload_0
    //   31: iload_1
    //   32: invokevirtual internalGet : (I)I
    //   35: istore_2
    //   36: aload_0
    //   37: iload_1
    //   38: invokevirtual isExternallySet : (I)Z
    //   41: ifeq -> 74
    //   44: iload_2
    //   45: aload_0
    //   46: iload_1
    //   47: invokevirtual getMinimum : (I)I
    //   50: if_icmplt -> 62
    //   53: iload_2
    //   54: aload_0
    //   55: iload_1
    //   56: invokevirtual getMaximum : (I)I
    //   59: if_icmple -> 74
    //   62: new java/lang/IllegalArgumentException
    //   65: dup
    //   66: iload_1
    //   67: invokestatic getFieldName : (I)Ljava/lang/String;
    //   70: invokespecial <init> : (Ljava/lang/String;)V
    //   73: athrow
    //   74: aload_0
    //   75: getfield originalFields : [I
    //   78: iload_1
    //   79: iload_2
    //   80: iastore
    //   81: iinc #1, 1
    //   84: goto -> 24
    //   87: aload_0
    //   88: invokevirtual selectFields : ()I
    //   91: istore_1
    //   92: aload_0
    //   93: iconst_1
    //   94: invokevirtual isSet : (I)Z
    //   97: ifeq -> 108
    //   100: aload_0
    //   101: iconst_1
    //   102: invokevirtual internalGet : (I)I
    //   105: goto -> 111
    //   108: sipush #1970
    //   111: istore_2
    //   112: aload_0
    //   113: invokespecial internalGetEra : ()I
    //   116: istore_3
    //   117: iload_3
    //   118: ifne -> 128
    //   121: iconst_1
    //   122: iload_2
    //   123: isub
    //   124: istore_2
    //   125: goto -> 143
    //   128: iload_3
    //   129: iconst_1
    //   130: if_icmpeq -> 143
    //   133: new java/lang/IllegalArgumentException
    //   136: dup
    //   137: ldc 'Invalid era'
    //   139: invokespecial <init> : (Ljava/lang/String;)V
    //   142: athrow
    //   143: iload_2
    //   144: ifgt -> 164
    //   147: aload_0
    //   148: iconst_0
    //   149: invokevirtual isSet : (I)Z
    //   152: ifne -> 164
    //   155: iload_1
    //   156: iconst_1
    //   157: ior
    //   158: istore_1
    //   159: aload_0
    //   160: iconst_1
    //   161: invokevirtual setFieldsComputed : (I)V
    //   164: lconst_0
    //   165: lstore #4
    //   167: iload_1
    //   168: bipush #11
    //   170: invokestatic isFieldSet : (II)Z
    //   173: ifeq -> 191
    //   176: lload #4
    //   178: aload_0
    //   179: bipush #11
    //   181: invokevirtual internalGet : (I)I
    //   184: i2l
    //   185: ladd
    //   186: lstore #4
    //   188: goto -> 227
    //   191: lload #4
    //   193: aload_0
    //   194: bipush #10
    //   196: invokevirtual internalGet : (I)I
    //   199: i2l
    //   200: ladd
    //   201: lstore #4
    //   203: iload_1
    //   204: bipush #9
    //   206: invokestatic isFieldSet : (II)Z
    //   209: ifeq -> 227
    //   212: lload #4
    //   214: bipush #12
    //   216: aload_0
    //   217: bipush #9
    //   219: invokevirtual internalGet : (I)I
    //   222: imul
    //   223: i2l
    //   224: ladd
    //   225: lstore #4
    //   227: lload #4
    //   229: ldc2_w 60
    //   232: lmul
    //   233: lstore #4
    //   235: lload #4
    //   237: aload_0
    //   238: bipush #12
    //   240: invokevirtual internalGet : (I)I
    //   243: i2l
    //   244: ladd
    //   245: lstore #4
    //   247: lload #4
    //   249: ldc2_w 60
    //   252: lmul
    //   253: lstore #4
    //   255: lload #4
    //   257: aload_0
    //   258: bipush #13
    //   260: invokevirtual internalGet : (I)I
    //   263: i2l
    //   264: ladd
    //   265: lstore #4
    //   267: lload #4
    //   269: ldc2_w 1000
    //   272: lmul
    //   273: lstore #4
    //   275: lload #4
    //   277: aload_0
    //   278: bipush #14
    //   280: invokevirtual internalGet : (I)I
    //   283: i2l
    //   284: ladd
    //   285: lstore #4
    //   287: lload #4
    //   289: ldc2_w 86400000
    //   292: ldiv
    //   293: lstore #6
    //   295: lload #4
    //   297: ldc2_w 86400000
    //   300: lrem
    //   301: lstore #4
    //   303: lload #4
    //   305: lconst_0
    //   306: lcmp
    //   307: ifge -> 327
    //   310: lload #4
    //   312: ldc2_w 86400000
    //   315: ladd
    //   316: lstore #4
    //   318: lload #6
    //   320: lconst_1
    //   321: lsub
    //   322: lstore #6
    //   324: goto -> 303
    //   327: iload_2
    //   328: aload_0
    //   329: getfield gregorianCutoverYear : I
    //   332: if_icmple -> 391
    //   335: iload_2
    //   336: aload_0
    //   337: getfield gregorianCutoverYearJulian : I
    //   340: if_icmple -> 391
    //   343: lload #6
    //   345: aload_0
    //   346: getstatic java/util/GregorianCalendar.gcal : Lsun/util/calendar/Gregorian;
    //   349: iload_2
    //   350: iload_1
    //   351: invokespecial getFixedDate : (Lsun/util/calendar/BaseCalendar;II)J
    //   354: ladd
    //   355: lstore #8
    //   357: lload #8
    //   359: aload_0
    //   360: getfield gregorianCutoverDate : J
    //   363: lcmp
    //   364: iflt -> 374
    //   367: lload #8
    //   369: lstore #6
    //   371: goto -> 619
    //   374: lload #6
    //   376: aload_0
    //   377: invokestatic getJulianCalendarSystem : ()Lsun/util/calendar/BaseCalendar;
    //   380: iload_2
    //   381: iload_1
    //   382: invokespecial getFixedDate : (Lsun/util/calendar/BaseCalendar;II)J
    //   385: ladd
    //   386: lstore #10
    //   388: goto -> 473
    //   391: iload_2
    //   392: aload_0
    //   393: getfield gregorianCutoverYear : I
    //   396: if_icmpge -> 445
    //   399: iload_2
    //   400: aload_0
    //   401: getfield gregorianCutoverYearJulian : I
    //   404: if_icmpge -> 445
    //   407: lload #6
    //   409: aload_0
    //   410: invokestatic getJulianCalendarSystem : ()Lsun/util/calendar/BaseCalendar;
    //   413: iload_2
    //   414: iload_1
    //   415: invokespecial getFixedDate : (Lsun/util/calendar/BaseCalendar;II)J
    //   418: ladd
    //   419: lstore #10
    //   421: lload #10
    //   423: aload_0
    //   424: getfield gregorianCutoverDate : J
    //   427: lcmp
    //   428: ifge -> 438
    //   431: lload #10
    //   433: lstore #6
    //   435: goto -> 619
    //   438: lload #10
    //   440: lstore #8
    //   442: goto -> 473
    //   445: lload #6
    //   447: aload_0
    //   448: invokestatic getJulianCalendarSystem : ()Lsun/util/calendar/BaseCalendar;
    //   451: iload_2
    //   452: iload_1
    //   453: invokespecial getFixedDate : (Lsun/util/calendar/BaseCalendar;II)J
    //   456: ladd
    //   457: lstore #10
    //   459: lload #6
    //   461: aload_0
    //   462: getstatic java/util/GregorianCalendar.gcal : Lsun/util/calendar/Gregorian;
    //   465: iload_2
    //   466: iload_1
    //   467: invokespecial getFixedDate : (Lsun/util/calendar/BaseCalendar;II)J
    //   470: ladd
    //   471: lstore #8
    //   473: iload_1
    //   474: bipush #6
    //   476: invokestatic isFieldSet : (II)Z
    //   479: ifne -> 490
    //   482: iload_1
    //   483: iconst_3
    //   484: invokestatic isFieldSet : (II)Z
    //   487: ifeq -> 523
    //   490: aload_0
    //   491: getfield gregorianCutoverYear : I
    //   494: aload_0
    //   495: getfield gregorianCutoverYearJulian : I
    //   498: if_icmpne -> 508
    //   501: lload #10
    //   503: lstore #6
    //   505: goto -> 619
    //   508: iload_2
    //   509: aload_0
    //   510: getfield gregorianCutoverYear : I
    //   513: if_icmpne -> 523
    //   516: lload #8
    //   518: lstore #6
    //   520: goto -> 619
    //   523: lload #8
    //   525: aload_0
    //   526: getfield gregorianCutoverDate : J
    //   529: lcmp
    //   530: iflt -> 581
    //   533: lload #10
    //   535: aload_0
    //   536: getfield gregorianCutoverDate : J
    //   539: lcmp
    //   540: iflt -> 550
    //   543: lload #8
    //   545: lstore #6
    //   547: goto -> 619
    //   550: aload_0
    //   551: getfield calsys : Lsun/util/calendar/BaseCalendar;
    //   554: getstatic java/util/GregorianCalendar.gcal : Lsun/util/calendar/Gregorian;
    //   557: if_acmpeq -> 567
    //   560: aload_0
    //   561: getfield calsys : Lsun/util/calendar/BaseCalendar;
    //   564: ifnonnull -> 574
    //   567: lload #8
    //   569: lstore #6
    //   571: goto -> 619
    //   574: lload #10
    //   576: lstore #6
    //   578: goto -> 619
    //   581: lload #10
    //   583: aload_0
    //   584: getfield gregorianCutoverDate : J
    //   587: lcmp
    //   588: ifge -> 598
    //   591: lload #10
    //   593: lstore #6
    //   595: goto -> 619
    //   598: aload_0
    //   599: invokevirtual isLenient : ()Z
    //   602: ifne -> 615
    //   605: new java/lang/IllegalArgumentException
    //   608: dup
    //   609: ldc 'the specified date doesn't exist'
    //   611: invokespecial <init> : (Ljava/lang/String;)V
    //   614: athrow
    //   615: lload #10
    //   617: lstore #6
    //   619: lload #6
    //   621: ldc2_w 719163
    //   624: lsub
    //   625: ldc2_w 86400000
    //   628: lmul
    //   629: lload #4
    //   631: ladd
    //   632: lstore #8
    //   634: aload_0
    //   635: invokevirtual getZone : ()Ljava/util/TimeZone;
    //   638: astore #10
    //   640: aload_0
    //   641: getfield zoneOffsets : [I
    //   644: ifnonnull -> 654
    //   647: aload_0
    //   648: iconst_2
    //   649: newarray int
    //   651: putfield zoneOffsets : [I
    //   654: iload_1
    //   655: ldc 98304
    //   657: iand
    //   658: istore #11
    //   660: iload #11
    //   662: ldc 98304
    //   664: if_icmpeq -> 734
    //   667: aload #10
    //   669: instanceof sun/util/calendar/ZoneInfo
    //   672: ifeq -> 693
    //   675: aload #10
    //   677: checkcast sun/util/calendar/ZoneInfo
    //   680: lload #8
    //   682: aload_0
    //   683: getfield zoneOffsets : [I
    //   686: invokevirtual getOffsetsByWall : (J[I)I
    //   689: pop
    //   690: goto -> 734
    //   693: iload_1
    //   694: bipush #15
    //   696: invokestatic isFieldSet : (II)Z
    //   699: ifeq -> 711
    //   702: aload_0
    //   703: bipush #15
    //   705: invokevirtual internalGet : (I)I
    //   708: goto -> 716
    //   711: aload #10
    //   713: invokevirtual getRawOffset : ()I
    //   716: istore #12
    //   718: aload #10
    //   720: lload #8
    //   722: iload #12
    //   724: i2l
    //   725: lsub
    //   726: aload_0
    //   727: getfield zoneOffsets : [I
    //   730: invokevirtual getOffsets : (J[I)I
    //   733: pop
    //   734: iload #11
    //   736: ifeq -> 783
    //   739: iload #11
    //   741: bipush #15
    //   743: invokestatic isFieldSet : (II)Z
    //   746: ifeq -> 761
    //   749: aload_0
    //   750: getfield zoneOffsets : [I
    //   753: iconst_0
    //   754: aload_0
    //   755: bipush #15
    //   757: invokevirtual internalGet : (I)I
    //   760: iastore
    //   761: iload #11
    //   763: bipush #16
    //   765: invokestatic isFieldSet : (II)Z
    //   768: ifeq -> 783
    //   771: aload_0
    //   772: getfield zoneOffsets : [I
    //   775: iconst_1
    //   776: aload_0
    //   777: bipush #16
    //   779: invokevirtual internalGet : (I)I
    //   782: iastore
    //   783: lload #8
    //   785: aload_0
    //   786: getfield zoneOffsets : [I
    //   789: iconst_0
    //   790: iaload
    //   791: aload_0
    //   792: getfield zoneOffsets : [I
    //   795: iconst_1
    //   796: iaload
    //   797: iadd
    //   798: i2l
    //   799: lsub
    //   800: lstore #8
    //   802: aload_0
    //   803: lload #8
    //   805: putfield time : J
    //   808: aload_0
    //   809: iload_1
    //   810: aload_0
    //   811: invokevirtual getSetStateFields : ()I
    //   814: ior
    //   815: iload #11
    //   817: invokespecial computeFields : (II)I
    //   820: istore #12
    //   822: aload_0
    //   823: invokevirtual isLenient : ()Z
    //   826: ifne -> 963
    //   829: iconst_0
    //   830: istore #13
    //   832: iload #13
    //   834: bipush #17
    //   836: if_icmpge -> 963
    //   839: aload_0
    //   840: iload #13
    //   842: invokevirtual isExternallySet : (I)Z
    //   845: ifne -> 851
    //   848: goto -> 957
    //   851: aload_0
    //   852: getfield originalFields : [I
    //   855: iload #13
    //   857: iaload
    //   858: aload_0
    //   859: iload #13
    //   861: invokevirtual internalGet : (I)I
    //   864: if_icmpeq -> 957
    //   867: new java/lang/StringBuilder
    //   870: dup
    //   871: invokespecial <init> : ()V
    //   874: aload_0
    //   875: getfield originalFields : [I
    //   878: iload #13
    //   880: iaload
    //   881: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   884: ldc ' -> '
    //   886: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   889: aload_0
    //   890: iload #13
    //   892: invokevirtual internalGet : (I)I
    //   895: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   898: invokevirtual toString : ()Ljava/lang/String;
    //   901: astore #14
    //   903: aload_0
    //   904: getfield originalFields : [I
    //   907: iconst_0
    //   908: aload_0
    //   909: getfield fields : [I
    //   912: iconst_0
    //   913: aload_0
    //   914: getfield fields : [I
    //   917: arraylength
    //   918: invokestatic arraycopy : (Ljava/lang/Object;ILjava/lang/Object;II)V
    //   921: new java/lang/IllegalArgumentException
    //   924: dup
    //   925: new java/lang/StringBuilder
    //   928: dup
    //   929: invokespecial <init> : ()V
    //   932: iload #13
    //   934: invokestatic getFieldName : (I)Ljava/lang/String;
    //   937: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   940: ldc ': '
    //   942: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   945: aload #14
    //   947: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   950: invokevirtual toString : ()Ljava/lang/String;
    //   953: invokespecial <init> : (Ljava/lang/String;)V
    //   956: athrow
    //   957: iinc #13, 1
    //   960: goto -> 832
    //   963: aload_0
    //   964: iload #12
    //   966: invokevirtual setFieldsNormalized : (I)V
    //   969: return }
  
  private long getFixedDate(BaseCalendar paramBaseCalendar, int paramInt1, int paramInt2) {
    int i = 0;
    if (isFieldSet(paramInt2, 2)) {
      i = internalGet(2);
      if (i > 11) {
        paramInt1 += i / 12;
        i %= 12;
      } else if (i < 0) {
        int[] arrayOfInt = new int[1];
        paramInt1 += CalendarUtils.floorDivide(i, 12, arrayOfInt);
        i = arrayOfInt[0];
      } 
    } 
    long l = paramBaseCalendar.getFixedDate(paramInt1, i + 1, 1, (paramBaseCalendar == gcal) ? this.gdate : null);
    if (isFieldSet(paramInt2, 2)) {
      if (isFieldSet(paramInt2, 5)) {
        if (isSet(5)) {
          l += internalGet(5);
          l--;
        } 
      } else if (isFieldSet(paramInt2, 4)) {
        long l1 = BaseCalendar.getDayOfWeekDateOnOrBefore(l + 6L, getFirstDayOfWeek());
        if (l1 - l >= getMinimalDaysInFirstWeek())
          l1 -= 7L; 
        if (isFieldSet(paramInt2, 7))
          l1 = BaseCalendar.getDayOfWeekDateOnOrBefore(l1 + 6L, internalGet(7)); 
        l = l1 + (7 * (internalGet(4) - 1));
      } else {
        boolean bool;
        int j;
        if (isFieldSet(paramInt2, 7)) {
          j = internalGet(7);
        } else {
          j = getFirstDayOfWeek();
        } 
        if (isFieldSet(paramInt2, 8)) {
          bool = internalGet(8);
        } else {
          bool = true;
        } 
        if (bool) {
          l = BaseCalendar.getDayOfWeekDateOnOrBefore(l + (7 * bool) - 1L, j);
        } else {
          int k = monthLength(i, paramInt1) + 7 * (bool + true);
          l = BaseCalendar.getDayOfWeekDateOnOrBefore(l + k - 1L, j);
        } 
      } 
    } else {
      if (paramInt1 == this.gregorianCutoverYear && paramBaseCalendar == gcal && l < this.gregorianCutoverDate && this.gregorianCutoverYear != this.gregorianCutoverYearJulian)
        l = this.gregorianCutoverDate; 
      if (isFieldSet(paramInt2, 6)) {
        l += internalGet(6);
        l--;
      } else {
        long l1 = BaseCalendar.getDayOfWeekDateOnOrBefore(l + 6L, getFirstDayOfWeek());
        if (l1 - l >= getMinimalDaysInFirstWeek())
          l1 -= 7L; 
        if (isFieldSet(paramInt2, 7)) {
          int j = internalGet(7);
          if (j != getFirstDayOfWeek())
            l1 = BaseCalendar.getDayOfWeekDateOnOrBefore(l1 + 6L, j); 
        } 
        l = l1 + 7L * (internalGet(3) - 1L);
      } 
    } 
    return l;
  }
  
  private GregorianCalendar getNormalizedCalendar() {
    GregorianCalendar gregorianCalendar;
    if (isFullyNormalized()) {
      gregorianCalendar = this;
    } else {
      gregorianCalendar = (GregorianCalendar)clone();
      gregorianCalendar.setLenient(true);
      gregorianCalendar.complete();
    } 
    return gregorianCalendar;
  }
  
  private static BaseCalendar getJulianCalendarSystem() {
    if (jcal == null) {
      jcal = (JulianCalendar)CalendarSystem.forName("julian");
      jeras = jcal.getEras();
    } 
    return jcal;
  }
  
  private BaseCalendar getCutoverCalendarSystem() { return (this.gregorianCutoverYearJulian < this.gregorianCutoverYear) ? gcal : getJulianCalendarSystem(); }
  
  private boolean isCutoverYear(int paramInt) {
    int i = (this.calsys == gcal) ? this.gregorianCutoverYear : this.gregorianCutoverYearJulian;
    return (paramInt == i);
  }
  
  private long getFixedDateJan1(BaseCalendar.Date paramDate, long paramLong) {
    assert paramDate.getNormalizedYear() == this.gregorianCutoverYear || paramDate.getNormalizedYear() == this.gregorianCutoverYearJulian;
    if (this.gregorianCutoverYear != this.gregorianCutoverYearJulian && paramLong >= this.gregorianCutoverDate)
      return this.gregorianCutoverDate; 
    BaseCalendar baseCalendar = getJulianCalendarSystem();
    return baseCalendar.getFixedDate(paramDate.getNormalizedYear(), 1, 1, null);
  }
  
  private long getFixedDateMonth1(BaseCalendar.Date paramDate, long paramLong) {
    long l;
    assert paramDate.getNormalizedYear() == this.gregorianCutoverYear || paramDate.getNormalizedYear() == this.gregorianCutoverYearJulian;
    BaseCalendar.Date date = getGregorianCutoverDate();
    if (date.getMonth() == 1 && date.getDayOfMonth() == 1)
      return paramLong - paramDate.getDayOfMonth() + 1L; 
    if (paramDate.getMonth() == date.getMonth()) {
      BaseCalendar.Date date1 = getLastJulianDate();
      if (this.gregorianCutoverYear == this.gregorianCutoverYearJulian && date.getMonth() == date1.getMonth()) {
        l = jcal.getFixedDate(paramDate.getNormalizedYear(), paramDate.getMonth(), 1, null);
      } else {
        l = this.gregorianCutoverDate;
      } 
    } else {
      l = paramLong - paramDate.getDayOfMonth() + 1L;
    } 
    return l;
  }
  
  private BaseCalendar.Date getCalendarDate(long paramLong) {
    Gregorian gregorian = (paramLong >= this.gregorianCutoverDate) ? gcal : getJulianCalendarSystem();
    BaseCalendar.Date date = (BaseCalendar.Date)gregorian.newCalendarDate(TimeZone.NO_TIMEZONE);
    gregorian.getCalendarDateFromFixedDate(date, paramLong);
    return date;
  }
  
  private BaseCalendar.Date getGregorianCutoverDate() { return getCalendarDate(this.gregorianCutoverDate); }
  
  private BaseCalendar.Date getLastJulianDate() { return getCalendarDate(this.gregorianCutoverDate - 1L); }
  
  private int monthLength(int paramInt1, int paramInt2) { return isLeapYear(paramInt2) ? LEAP_MONTH_LENGTH[paramInt1] : MONTH_LENGTH[paramInt1]; }
  
  private int monthLength(int paramInt) {
    int i = internalGet(1);
    if (internalGetEra() == 0)
      i = 1 - i; 
    return monthLength(paramInt, i);
  }
  
  private int actualMonthLength() {
    int i = this.cdate.getNormalizedYear();
    if (i != this.gregorianCutoverYear && i != this.gregorianCutoverYearJulian)
      return this.calsys.getMonthLength(this.cdate); 
    BaseCalendar.Date date = (BaseCalendar.Date)this.cdate.clone();
    long l1 = this.calsys.getFixedDate(date);
    long l2 = getFixedDateMonth1(date, l1);
    long l3 = l2 + this.calsys.getMonthLength(date);
    if (l3 < this.gregorianCutoverDate)
      return (int)(l3 - l2); 
    if (this.cdate != this.gdate)
      date = gcal.newCalendarDate(TimeZone.NO_TIMEZONE); 
    gcal.getCalendarDateFromFixedDate(date, l3);
    l3 = getFixedDateMonth1(date, l3);
    return (int)(l3 - l2);
  }
  
  private int yearLength(int paramInt) { return isLeapYear(paramInt) ? 366 : 365; }
  
  private int yearLength() {
    int i = internalGet(1);
    if (internalGetEra() == 0)
      i = 1 - i; 
    return yearLength(i);
  }
  
  private void pinDayOfMonth() {
    int j;
    int i = internalGet(1);
    if (i > this.gregorianCutoverYear || i < this.gregorianCutoverYearJulian) {
      j = monthLength(internalGet(2));
    } else {
      GregorianCalendar gregorianCalendar = getNormalizedCalendar();
      j = gregorianCalendar.getActualMaximum(5);
    } 
    int k = internalGet(5);
    if (k > j)
      set(5, j); 
  }
  
  private long getCurrentFixedDate() { return (this.calsys == gcal) ? this.cachedFixedDate : this.calsys.getFixedDate(this.cdate); }
  
  private static int getRolledValue(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    assert paramInt1 >= paramInt3 && paramInt1 <= paramInt4;
    int i = paramInt4 - paramInt3 + 1;
    paramInt2 %= i;
    int j = paramInt1 + paramInt2;
    if (j > paramInt4) {
      j -= i;
    } else if (j < paramInt3) {
      j += i;
    } 
    assert j >= paramInt3 && j <= paramInt4;
    return j;
  }
  
  private int internalGetEra() { return isSet(0) ? internalGet(0) : 1; }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    if (this.gdate == null) {
      this.gdate = gcal.newCalendarDate(getZone());
      this.cachedFixedDate = Float.MIN_VALUE;
    } 
    setGregorianChange(this.gregorianCutover);
  }
  
  public ZonedDateTime toZonedDateTime() { return ZonedDateTime.ofInstant(Instant.ofEpochMilli(getTimeInMillis()), getTimeZone().toZoneId()); }
  
  public static GregorianCalendar from(ZonedDateTime paramZonedDateTime) {
    GregorianCalendar gregorianCalendar = new GregorianCalendar(TimeZone.getTimeZone(paramZonedDateTime.getZone()));
    gregorianCalendar.setGregorianChange(new Date(Float.MIN_VALUE));
    gregorianCalendar.setFirstDayOfWeek(2);
    gregorianCalendar.setMinimalDaysInFirstWeek(4);
    try {
      gregorianCalendar.setTimeInMillis(Math.addExact(Math.multiplyExact(paramZonedDateTime.toEpochSecond(), 1000L), paramZonedDateTime.get(ChronoField.MILLI_OF_SECOND)));
    } catch (ArithmeticException arithmeticException) {
      throw new IllegalArgumentException(arithmeticException);
    } 
    return gregorianCalendar;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\GregorianCalendar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */