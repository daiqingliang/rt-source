package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import sun.util.calendar.BaseCalendar;
import sun.util.calendar.CalendarDate;
import sun.util.calendar.CalendarSystem;
import sun.util.calendar.CalendarUtils;
import sun.util.calendar.Era;
import sun.util.calendar.Gregorian;
import sun.util.calendar.LocalGregorianCalendar;
import sun.util.calendar.ZoneInfo;
import sun.util.locale.provider.CalendarDataUtility;

class JapaneseImperialCalendar extends Calendar {
  public static final int BEFORE_MEIJI = 0;
  
  public static final int MEIJI = 1;
  
  public static final int TAISHO = 2;
  
  public static final int SHOWA = 3;
  
  public static final int HEISEI = 4;
  
  private static final int EPOCH_OFFSET = 719163;
  
  private static final int EPOCH_YEAR = 1970;
  
  private static final int ONE_SECOND = 1000;
  
  private static final int ONE_MINUTE = 60000;
  
  private static final int ONE_HOUR = 3600000;
  
  private static final long ONE_DAY = 86400000L;
  
  private static final long ONE_WEEK = 604800000L;
  
  private static final LocalGregorianCalendar jcal = (LocalGregorianCalendar)CalendarSystem.forName("japanese");
  
  private static final Gregorian gcal = CalendarSystem.getGregorianCalendar();
  
  private static final Era BEFORE_MEIJI_ERA = new Era("BeforeMeiji", "BM", Float.MIN_VALUE, false);
  
  private static final Era[] eras;
  
  private static final long[] sinceFixedDates;
  
  static final int[] MIN_VALUES = { 
      0, -292275055, 0, 1, 0, 1, 1, 1, 1, 0, 
      0, 0, 0, 0, 0, -46800000, 0 };
  
  static final int[] LEAST_MAX_VALUES = { 
      0, 0, 0, 0, 4, 28, 0, 7, 4, 1, 
      11, 23, 59, 59, 999, 50400000, 1200000 };
  
  static final int[] MAX_VALUES = { 
      0, 292278994, 11, 53, 6, 31, 366, 7, 6, 1, 
      11, 23, 59, 59, 999, 50400000, 7200000 };
  
  private static final long serialVersionUID = -3364572813905467929L;
  
  private LocalGregorianCalendar.Date jdate;
  
  private int[] zoneOffsets;
  
  private int[] originalFields;
  
  private long cachedFixedDate = Float.MIN_VALUE;
  
  JapaneseImperialCalendar(TimeZone paramTimeZone, Locale paramLocale) {
    super(paramTimeZone, paramLocale);
    this.jdate = jcal.newCalendarDate(paramTimeZone);
    setTimeInMillis(System.currentTimeMillis());
  }
  
  JapaneseImperialCalendar(TimeZone paramTimeZone, Locale paramLocale, boolean paramBoolean) {
    super(paramTimeZone, paramLocale);
    this.jdate = jcal.newCalendarDate(paramTimeZone);
  }
  
  public String getCalendarType() { return "japanese"; }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof JapaneseImperialCalendar && super.equals(paramObject)); }
  
  public int hashCode() { return super.hashCode() ^ this.jdate.hashCode(); }
  
  public void add(int paramInt1, int paramInt2) {
    if (paramInt2 == 0)
      return; 
    if (paramInt1 < 0 || paramInt1 >= 15)
      throw new IllegalArgumentException(); 
    complete();
    if (paramInt1 == 1) {
      LocalGregorianCalendar.Date date = (LocalGregorianCalendar.Date)this.jdate.clone();
      date.addYear(paramInt2);
      pinDayOfMonth(date);
      set(0, getEraIndex(date));
      set(1, date.getYear());
      set(2, date.getMonth() - 1);
      set(5, date.getDayOfMonth());
    } else if (paramInt1 == 2) {
      LocalGregorianCalendar.Date date = (LocalGregorianCalendar.Date)this.jdate.clone();
      date.addMonth(paramInt2);
      pinDayOfMonth(date);
      set(0, getEraIndex(date));
      set(1, date.getYear());
      set(2, date.getMonth() - 1);
      set(5, date.getDayOfMonth());
    } else if (paramInt1 == 0) {
      int i = internalGet(0) + paramInt2;
      if (i < 0) {
        i = 0;
      } else if (i > eras.length - 1) {
        i = eras.length - 1;
      } 
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
      long l3 = this.cachedFixedDate;
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
        long l = this.cachedFixedDate;
        if (l != l3)
          setTimeInMillis(this.time - i); 
      } 
    } 
  }
  
  public void roll(int paramInt, boolean paramBoolean) { roll(paramInt, paramBoolean ? 1 : -1); }
  
  public void roll(int paramInt1, int paramInt2) {
    long l11;
    int i8;
    LocalGregorianCalendar.Date date5;
    long l10;
    LocalGregorianCalendar.Date date4;
    int i7;
    int i6;
    int i4;
    long l9;
    int i5;
    LocalGregorianCalendar.Date date3;
    long l8;
    int i3;
    long l6;
    long l7;
    LocalGregorianCalendar.Date date2;
    LocalGregorianCalendar.Date date1;
    long l5;
    int i1;
    long l4;
    int i2;
    int n;
    long l3;
    int k;
    boolean bool;
    long l2;
    long l1;
    int m;
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
        m = j + 1;
        n = internalGet(paramInt1);
        i2 = (n + paramInt2) % m;
        if (i2 < 0)
          i2 += m; 
        this.time += (3600000 * (i2 - n));
        date2 = jcal.getCalendarDate(this.time, getZone());
        if (internalGet(5) != date2.getDayOfMonth()) {
          date2.setEra(this.jdate.getEra());
          date2.setDate(internalGet(1), internalGet(2) + 1, internalGet(5));
          if (paramInt1 == 10) {
            assert internalGet(9) == 1;
            date2.addHours(12);
          } 
          this.time = jcal.getTime(date2);
        } 
        l8 = date2.getHours();
        internalSet(paramInt1, l8 % m);
        if (paramInt1 == 10) {
          internalSet(11, l8);
        } else {
          internalSet(9, l8 / 12);
          internalSet(10, l8 % 12);
        } 
        i5 = date2.getZoneOffset();
        i6 = date2.getDaylightSaving();
        internalSet(15, i5 - i6);
        internalSet(16, i6);
        return;
      case 1:
        i = getActualMinimum(paramInt1);
        j = getActualMaximum(paramInt1);
        break;
      case 2:
        if (!isTransitionYear(this.jdate.getNormalizedYear())) {
          m = this.jdate.getYear();
          if (m == getMaximum(1)) {
            LocalGregorianCalendar.Date date6 = jcal.getCalendarDate(this.time, getZone());
            LocalGregorianCalendar.Date date7 = jcal.getCalendarDate(Float.MAX_VALUE, getZone());
            j = date7.getMonth() - 1;
            int i9 = getRolledValue(internalGet(paramInt1), paramInt2, i, j);
            if (i9 == j) {
              date6.addYear(-400);
              date6.setMonth(i9 + 1);
              if (date6.getDayOfMonth() > date7.getDayOfMonth()) {
                date6.setDayOfMonth(date7.getDayOfMonth());
                jcal.normalize(date6);
              } 
              if (date6.getDayOfMonth() == date7.getDayOfMonth() && date6.getTimeOfDay() > date7.getTimeOfDay()) {
                date6.setMonth(i9 + 1);
                date6.setDayOfMonth(date7.getDayOfMonth() - 1);
                jcal.normalize(date6);
                i9 = date6.getMonth() - 1;
              } 
              set(5, date6.getDayOfMonth());
            } 
            set(2, i9);
          } else if (m == getMinimum(1)) {
            LocalGregorianCalendar.Date date6 = jcal.getCalendarDate(this.time, getZone());
            LocalGregorianCalendar.Date date7 = jcal.getCalendarDate(Float.MIN_VALUE, getZone());
            i = date7.getMonth() - 1;
            int i9 = getRolledValue(internalGet(paramInt1), paramInt2, i, j);
            if (i9 == i) {
              date6.addYear(400);
              date6.setMonth(i9 + 1);
              if (date6.getDayOfMonth() < date7.getDayOfMonth()) {
                date6.setDayOfMonth(date7.getDayOfMonth());
                jcal.normalize(date6);
              } 
              if (date6.getDayOfMonth() == date7.getDayOfMonth() && date6.getTimeOfDay() < date7.getTimeOfDay()) {
                date6.setMonth(i9 + 1);
                date6.setDayOfMonth(date7.getDayOfMonth() + 1);
                jcal.normalize(date6);
                i9 = date6.getMonth() - 1;
              } 
              set(5, date6.getDayOfMonth());
            } 
            set(2, i9);
          } else {
            n = (internalGet(2) + paramInt2) % 12;
            if (n < 0)
              n += 12; 
            set(2, n);
            i2 = monthLength(n);
            if (internalGet(5) > i2)
              set(5, i2); 
          } 
        } else {
          m = getEraIndex(this.jdate);
          CalendarDate calendarDate = null;
          if (this.jdate.getYear() == 1) {
            calendarDate = eras[m].getSinceDate();
            i = calendarDate.getMonth() - 1;
          } else if (m < eras.length - 1) {
            calendarDate = eras[m + 1].getSinceDate();
            if (calendarDate.getYear() == this.jdate.getNormalizedYear()) {
              j = calendarDate.getMonth() - 1;
              if (calendarDate.getDayOfMonth() == 1)
                j--; 
            } 
          } 
          if (i == j)
            return; 
          i2 = getRolledValue(internalGet(paramInt1), paramInt2, i, j);
          set(2, i2);
          if (i2 == i) {
            if ((calendarDate.getMonth() != 1 || calendarDate.getDayOfMonth() != 1) && this.jdate.getDayOfMonth() < calendarDate.getDayOfMonth())
              set(5, calendarDate.getDayOfMonth()); 
          } else if (i2 == j && calendarDate.getMonth() - 1 == i2) {
            int i9 = calendarDate.getDayOfMonth();
            if (this.jdate.getDayOfMonth() >= i9)
              set(5, i9 - 1); 
          } 
        } 
        return;
      case 3:
        m = this.jdate.getNormalizedYear();
        j = getActualMaximum(3);
        set(7, internalGet(7));
        n = internalGet(3);
        i2 = n + paramInt2;
        if (!isTransitionYear(this.jdate.getNormalizedYear())) {
          int i9 = this.jdate.getYear();
          if (i9 == getMaximum(1)) {
            j = getActualMaximum(3);
          } else if (i9 == getMinimum(1)) {
            i = getActualMinimum(3);
            j = getActualMaximum(3);
            if (i2 > i && i2 < j) {
              set(3, i2);
              return;
            } 
          } 
          if (i2 > i && i2 < j) {
            set(3, i2);
            return;
          } 
          long l12 = this.cachedFixedDate;
          long l13 = l12 - (7 * (n - i));
          if (i9 != getMinimum(1)) {
            if (gcal.getYearFromFixedDate(l13) != m)
              i++; 
          } else {
            LocalGregorianCalendar.Date date = jcal.getCalendarDate(Float.MIN_VALUE, getZone());
            if (l13 < jcal.getFixedDate(date))
              i++; 
          } 
          l12 += (7 * (j - internalGet(3)));
          if (gcal.getYearFromFixedDate(l12) != m)
            j--; 
          break;
        } 
        l7 = this.cachedFixedDate;
        l9 = l7 - (7 * (n - i));
        date4 = getCalendarDate(l9);
        if (date4.getEra() != this.jdate.getEra() || date4.getYear() != this.jdate.getYear())
          i++; 
        l7 += (7 * (j - n));
        jcal.getCalendarDateFromFixedDate(date4, l7);
        if (date4.getEra() != this.jdate.getEra() || date4.getYear() != this.jdate.getYear())
          j--; 
        i2 = getRolledValue(n, paramInt2, i, j) - 1;
        date4 = getCalendarDate(l9 + (i2 * 7));
        set(2, date4.getMonth() - 1);
        set(5, date4.getDayOfMonth());
        return;
      case 4:
        bool = isTransitionYear(this.jdate.getNormalizedYear());
        n = internalGet(7) - getFirstDayOfWeek();
        if (n < 0)
          n += 7; 
        l5 = this.cachedFixedDate;
        if (bool) {
          long l = getFixedDateMonth1(this.jdate, l5);
          i6 = actualMonthLength();
        } else {
          l8 = l5 - internalGet(5) + 1L;
          i6 = jcal.getMonthLength(this.jdate);
        } 
        l10 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(l8 + 6L, getFirstDayOfWeek());
        if ((int)(l10 - l8) >= getMinimalDaysInFirstWeek())
          l10 -= 7L; 
        j = getActualMaximum(paramInt1);
        i8 = getRolledValue(internalGet(paramInt1), paramInt2, 1, j) - 1;
        l11 = l10 + (i8 * 7) + n;
        if (l11 < l8) {
          l11 = l8;
        } else if (l11 >= l8 + i6) {
          l11 = l8 + i6 - 1L;
        } 
        set(5, (int)(l11 - l8) + 1);
        return;
      case 5:
        if (!isTransitionYear(this.jdate.getNormalizedYear())) {
          j = jcal.getMonthLength(this.jdate);
          break;
        } 
        l2 = getFixedDateMonth1(this.jdate, this.cachedFixedDate);
        i1 = getRolledValue((int)(this.cachedFixedDate - l2), paramInt2, 0, actualMonthLength() - 1);
        date1 = getCalendarDate(l2 + i1);
        assert getEraIndex(date1) == internalGetEra() && date1.getYear() == internalGet(1) && date1.getMonth() - 1 == internalGet(2);
        set(5, date1.getDayOfMonth());
        return;
      case 6:
        j = getActualMaximum(paramInt1);
        if (!isTransitionYear(this.jdate.getNormalizedYear()))
          break; 
        k = getRolledValue(internalGet(6), paramInt2, i, j);
        l3 = this.cachedFixedDate - internalGet(6);
        date1 = getCalendarDate(l3 + k);
        assert getEraIndex(date1) == internalGetEra() && date1.getYear() == internalGet(1);
        set(2, date1.getMonth() - 1);
        set(5, date1.getDayOfMonth());
        return;
      case 7:
        k = this.jdate.getNormalizedYear();
        if (!isTransitionYear(k) && !isTransitionYear(k - 1)) {
          int i9 = internalGet(3);
          if (i9 > 1 && i9 < 52) {
            set(3, internalGet(3));
            j = 7;
            break;
          } 
        } 
        paramInt2 %= 7;
        if (paramInt2 == 0)
          return; 
        l3 = this.cachedFixedDate;
        l6 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(l3, getFirstDayOfWeek());
        l3 += paramInt2;
        if (l3 < l6) {
          l3 += 7L;
        } else if (l3 >= l6 + 7L) {
          l3 -= 7L;
        } 
        date3 = getCalendarDate(l3);
        set(0, getEraIndex(date3));
        set(date3.getYear(), date3.getMonth() - 1, date3.getDayOfMonth());
        return;
      case 8:
        i = 1;
        if (!isTransitionYear(this.jdate.getNormalizedYear())) {
          k = internalGet(5);
          int i9 = jcal.getMonthLength(this.jdate);
          i1 = i9 % 7;
          j = i9 / 7;
          int i10 = (k - 1) % 7;
          if (i10 < i1)
            j++; 
          set(7, internalGet(7));
          break;
        } 
        l1 = this.cachedFixedDate;
        l4 = getFixedDateMonth1(this.jdate, l1);
        i3 = actualMonthLength();
        i4 = i3 % 7;
        j = i3 / 7;
        i6 = (int)(l1 - l4) % 7;
        if (i6 < i4)
          j++; 
        i7 = getRolledValue(internalGet(paramInt1), paramInt2, i, j) - 1;
        l1 = l4 + (i7 * 7) + i6;
        date5 = getCalendarDate(l1);
        set(5, date5.getDayOfMonth());
        return;
    } 
    set(paramInt1, getRolledValue(internalGet(paramInt1), paramInt2, i, j));
  }
  
  public String getDisplayName(int paramInt1, int paramInt2, Locale paramLocale) {
    if (!checkDisplayNameParams(paramInt1, paramInt2, 1, 4, paramLocale, 647))
      return null; 
    int i = get(paramInt1);
    if (paramInt1 == 1 && (getBaseStyle(paramInt2) != 2 || i != 1 || get(0) == 0))
      return null; 
    String str = CalendarDataUtility.retrieveFieldValueName(getCalendarType(), paramInt1, i, paramInt2, paramLocale);
    if (str == null && paramInt1 == 0 && i < eras.length) {
      Era era = eras[i];
      str = (paramInt2 == 1) ? era.getAbbreviation() : era.getName();
    } 
    return str;
  }
  
  public Map<String, Integer> getDisplayNames(int paramInt1, int paramInt2, Locale paramLocale) {
    if (!checkDisplayNameParams(paramInt1, paramInt2, 0, 4, paramLocale, 647))
      return null; 
    Map map = CalendarDataUtility.retrieveFieldValueNames(getCalendarType(), paramInt1, paramInt2, paramLocale);
    if (map != null && paramInt1 == 0) {
      int i = map.size();
      if (paramInt2 == 0) {
        HashSet hashSet = new HashSet();
        for (String str : map.keySet())
          hashSet.add(map.get(str)); 
        i = hashSet.size();
      } 
      if (i < eras.length) {
        int j = getBaseStyle(paramInt2);
        for (int k = i; k < eras.length; k++) {
          Era era = eras[k];
          if (j == 0 || j == 1 || j == 4)
            map.put(era.getAbbreviation(), Integer.valueOf(k)); 
          if (j == 0 || j == 2)
            map.put(era.getName(), Integer.valueOf(k)); 
        } 
      } 
    } 
    return map;
  }
  
  public int getMinimum(int paramInt) { return MIN_VALUES[paramInt]; }
  
  public int getMaximum(int paramInt) {
    LocalGregorianCalendar.Date date;
    switch (paramInt) {
      case 1:
        date = jcal.getCalendarDate(Float.MAX_VALUE, getZone());
        return Math.max(LEAST_MAX_VALUES[1], date.getYear());
    } 
    return MAX_VALUES[paramInt];
  }
  
  public int getGreatestMinimum(int paramInt) { return (paramInt == 1) ? 1 : MIN_VALUES[paramInt]; }
  
  public int getLeastMaximum(int paramInt) {
    switch (paramInt) {
      case 1:
        return Math.min(LEAST_MAX_VALUES[1], getMaximum(1));
    } 
    return LEAST_MAX_VALUES[paramInt];
  }
  
  public int getActualMinimum(int paramInt) {
    long l3;
    int m;
    long l2;
    int k;
    long l1;
    LocalGregorianCalendar.Date date2;
    if (!isFieldSet(14, paramInt))
      return getMinimum(paramInt); 
    int i = 0;
    JapaneseImperialCalendar japaneseImperialCalendar = getNormalizedCalendar();
    LocalGregorianCalendar.Date date1 = jcal.getCalendarDate(japaneseImperialCalendar.getTimeInMillis(), getZone());
    int j = getEraIndex(date1);
    switch (paramInt) {
      case 1:
        if (j > 0) {
          i = 1;
          long l = eras[j].getSince(getZone());
          LocalGregorianCalendar.Date date = jcal.getCalendarDate(l, getZone());
          date1.setYear(date.getYear());
          jcal.normalize(date1);
          assert date1.isLeapYear() == date.isLeapYear();
          if (getYearOffsetInMillis(date1) < getYearOffsetInMillis(date))
            i++; 
          break;
        } 
        i = getMinimum(paramInt);
        date2 = jcal.getCalendarDate(Float.MIN_VALUE, getZone());
        k = date2.getYear();
        if (k > 400)
          k -= 400; 
        date1.setYear(k);
        jcal.normalize(date1);
        if (getYearOffsetInMillis(date1) < getYearOffsetInMillis(date2))
          i++; 
        break;
      case 2:
        if (j > 1 && date1.getYear() == 1) {
          long l = eras[j].getSince(getZone());
          LocalGregorianCalendar.Date date = jcal.getCalendarDate(l, getZone());
          i = date.getMonth() - 1;
          if (date1.getDayOfMonth() < date.getDayOfMonth())
            i++; 
        } 
        break;
      case 3:
        i = 1;
        date2 = jcal.getCalendarDate(Float.MIN_VALUE, getZone());
        date2.addYear(400);
        jcal.normalize(date2);
        date1.setEra(date2.getEra());
        date1.setYear(date2.getYear());
        jcal.normalize(date1);
        l1 = jcal.getFixedDate(date2);
        l2 = jcal.getFixedDate(date1);
        m = getWeekNumber(l1, l2);
        l3 = l2 - (7 * (m - 1));
        if (l3 < l1 || (l3 == l1 && date1.getTimeOfDay() < date2.getTimeOfDay()))
          i++; 
        break;
    } 
    return i;
  }
  
  public int getActualMaximum(int paramInt) {
    int i1;
    BaseCalendar.Date date;
    int n;
    LocalGregorianCalendar.Date date4;
    int m;
    int k;
    LocalGregorianCalendar.Date date2;
    LocalGregorianCalendar.Date date3;
    int j;
    if ((0x1FE81 & 1 << paramInt) != 0)
      return getMaximum(paramInt); 
    JapaneseImperialCalendar japaneseImperialCalendar = getNormalizedCalendar();
    LocalGregorianCalendar.Date date1 = japaneseImperialCalendar.jdate;
    int i = date1.getNormalizedYear();
    null = -1;
    switch (paramInt) {
      case 2:
        null = 11;
        if (isTransitionYear(date1.getNormalizedYear())) {
          int i2 = getEraIndex(date1);
          if (date1.getYear() != 1)
            assert ++i2 < eras.length; 
          long l1 = sinceFixedDates[i2];
          long l2 = japaneseImperialCalendar.cachedFixedDate;
          if (l2 < l1) {
            LocalGregorianCalendar.Date date5 = (LocalGregorianCalendar.Date)date1.clone();
            jcal.getCalendarDateFromFixedDate(date5, l1 - 1L);
            null = date5.getMonth() - 1;
          } 
        } else {
          LocalGregorianCalendar.Date date5 = jcal.getCalendarDate(Float.MAX_VALUE, getZone());
          if (date1.getEra() == date5.getEra() && date1.getYear() == date5.getYear())
            null = date5.getMonth() - 1; 
        } 
        return null;
      case 5:
        return jcal.getMonthLength(date1);
      case 6:
        if (isTransitionYear(date1.getNormalizedYear())) {
          int i2 = getEraIndex(date1);
          if (date1.getYear() != 1)
            assert ++i2 < eras.length; 
          long l1 = sinceFixedDates[i2];
          long l2 = japaneseImperialCalendar.cachedFixedDate;
          Gregorian.Date date5 = gcal.newCalendarDate(TimeZone.NO_TIMEZONE);
          date5.setDate(date1.getNormalizedYear(), 1, 1);
          if (l2 < l1) {
            null = (int)(l1 - gcal.getFixedDate(date5));
          } else {
            date5.addYear(1);
            null = (int)(gcal.getFixedDate(date5) - l1);
          } 
        } else {
          LocalGregorianCalendar.Date date5 = jcal.getCalendarDate(Float.MAX_VALUE, getZone());
          if (date1.getEra() == date5.getEra() && date1.getYear() == date5.getYear()) {
            long l1 = jcal.getFixedDate(date5);
            long l2 = getFixedDateJan1(date5, l1);
            null = (int)(l1 - l2) + 1;
          } else if (date1.getYear() == getMinimum(1)) {
            date4 = jcal.getCalendarDate(Float.MIN_VALUE, getZone());
            long l1 = jcal.getFixedDate(date4);
            date4.addYear(1);
            date4.setMonth(1).setDayOfMonth(1);
            jcal.normalize(date4);
            long l2 = jcal.getFixedDate(date4);
            null = (int)(l2 - l1);
          } else {
            null = jcal.getYearLength(date1);
          } 
        } 
        return null;
      case 3:
        if (!isTransitionYear(date1.getNormalizedYear())) {
          LocalGregorianCalendar.Date date5 = jcal.getCalendarDate(Float.MAX_VALUE, getZone());
          if (date1.getEra() == date5.getEra() && date1.getYear() == date5.getYear()) {
            long l1 = jcal.getFixedDate(date5);
            long l2 = getFixedDateJan1(date5, l1);
            null = getWeekNumber(l2, l1);
          } else if (date1.getEra() == null && date1.getYear() == getMinimum(1)) {
            date4 = jcal.getCalendarDate(Float.MIN_VALUE, getZone());
            date4.addYear(400);
            jcal.normalize(date4);
            date5.setEra(date4.getEra());
            date5.setDate(date4.getYear() + 1, 1, 1);
            jcal.normalize(date5);
            long l1 = jcal.getFixedDate(date4);
            long l2 = jcal.getFixedDate(date5);
            long l3 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(l2 + 6L, getFirstDayOfWeek());
            int i2 = (int)(l3 - l2);
            if (i2 >= getMinimalDaysInFirstWeek())
              l3 -= 7L; 
            null = getWeekNumber(l1, l3);
          } else {
            date4 = gcal.newCalendarDate(TimeZone.NO_TIMEZONE);
            date4.setDate(date1.getNormalizedYear(), 1, 1);
            int i2 = gcal.getDayOfWeek(date4);
            i2 -= getFirstDayOfWeek();
            if (i2 < 0)
              i2 += 7; 
            null = 52;
            int i3 = i2 + getMinimalDaysInFirstWeek() - 1;
            if (i3 == 6 || (date1.isLeapYear() && (i3 == 5 || i3 == 12)))
              null++; 
          } 
        } else {
          if (japaneseImperialCalendar == this)
            japaneseImperialCalendar = (JapaneseImperialCalendar)japaneseImperialCalendar.clone(); 
          int i2 = getActualMaximum(6);
          japaneseImperialCalendar.set(6, i2);
          null = japaneseImperialCalendar.get(3);
          if (null == 1 && i2 > 7) {
            japaneseImperialCalendar.add(3, -1);
            null = japaneseImperialCalendar.get(3);
          } 
        } 
        return null;
      case 4:
        date3 = jcal.getCalendarDate(Float.MAX_VALUE, getZone());
        if (date1.getEra() != date3.getEra() || date1.getYear() != date3.getYear()) {
          date4 = gcal.newCalendarDate(TimeZone.NO_TIMEZONE);
          date4.setDate(date1.getNormalizedYear(), date1.getMonth(), 1);
          int i2 = gcal.getDayOfWeek(date4);
          int i3 = gcal.getMonthLength(date4);
          i2 -= getFirstDayOfWeek();
          if (i2 < 0)
            i2 += 7; 
          int i4 = 7 - i2;
          null = 3;
          if (i4 >= getMinimalDaysInFirstWeek())
            null++; 
          i3 -= i4 + 21;
          if (i3 > 0) {
            null++;
            if (i3 > 7)
              null++; 
          } 
        } else {
          long l1 = jcal.getFixedDate(date3);
          long l2 = l1 - date3.getDayOfMonth() + 1L;
          null = getWeekNumber(l2, l1);
        } 
        return null;
      case 8:
        n = date1.getDayOfWeek();
        date = (BaseCalendar.Date)date1.clone();
        k = jcal.getMonthLength(date);
        date.setDayOfMonth(1);
        jcal.normalize(date);
        m = date.getDayOfWeek();
        i1 = n - m;
        if (i1 < 0)
          i1 += 7; 
        k -= i1;
        return (k + 6) / 7;
      case 1:
        date2 = jcal.getCalendarDate(japaneseImperialCalendar.getTimeInMillis(), getZone());
        n = getEraIndex(date1);
        if (n == eras.length - 1) {
          date4 = jcal.getCalendarDate(Float.MAX_VALUE, getZone());
          j = date4.getYear();
          if (j > 400)
            date2.setYear(j - 400); 
        } else {
          date4 = jcal.getCalendarDate(eras[n + 1].getSince(getZone()) - 1L, getZone());
          j = date4.getYear();
          date2.setYear(j);
        } 
        jcal.normalize(date2);
        if (getYearOffsetInMillis(date2) > getYearOffsetInMillis(date4))
          j--; 
        return j;
    } 
    throw new ArrayIndexOutOfBoundsException(paramInt);
  }
  
  private long getYearOffsetInMillis(CalendarDate paramCalendarDate) {
    long l = (jcal.getDayOfYear(paramCalendarDate) - 1L) * 86400000L;
    return l + paramCalendarDate.getTimeOfDay() - paramCalendarDate.getZoneOffset();
  }
  
  public Object clone() {
    JapaneseImperialCalendar japaneseImperialCalendar = (JapaneseImperialCalendar)super.clone();
    japaneseImperialCalendar.jdate = (LocalGregorianCalendar.Date)this.jdate.clone();
    japaneseImperialCalendar.originalFields = null;
    japaneseImperialCalendar.zoneOffsets = null;
    return japaneseImperialCalendar;
  }
  
  public TimeZone getTimeZone() {
    TimeZone timeZone = super.getTimeZone();
    this.jdate.setZone(timeZone);
    return timeZone;
  }
  
  public void setTimeZone(TimeZone paramTimeZone) {
    super.setTimeZone(paramTimeZone);
    this.jdate.setZone(paramTimeZone);
  }
  
  protected void computeFields() {
    int i = 0;
    if (isPartiallyNormalized()) {
      i = getSetStateFields();
      int j = (i ^ 0xFFFFFFFF) & 0x1FFFF;
      if (j != 0 || this.cachedFixedDate == Float.MIN_VALUE) {
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
    if (l != this.cachedFixedDate || l < 0L) {
      jcal.getCalendarDateFromFixedDate(this.jdate, l);
      this.cachedFixedDate = l;
    } 
    int k = getEraIndex(this.jdate);
    int m = this.jdate.getYear();
    internalSet(0, k);
    internalSet(1, m);
    int n = paramInt1 | 0x3;
    int i1 = this.jdate.getMonth() - 1;
    int i2 = this.jdate.getDayOfMonth();
    if ((paramInt1 & 0xA4) != 0) {
      internalSet(2, i1);
      internalSet(5, i2);
      internalSet(7, this.jdate.getDayOfWeek());
      n |= 0xA4;
    } 
    if ((paramInt1 & 0x7E00) != 0) {
      if (j != 0) {
        int i3 = j / 3600000;
        internalSet(11, i3);
        internalSet(9, i3 / 12);
        internalSet(10, i3 % 12);
        int i4 = j % 3600000;
        internalSet(12, i4 / 60000);
        i4 %= 60000;
        internalSet(13, i4 / 1000);
        internalSet(14, i4 % 1000);
      } else {
        internalSet(11, 0);
        internalSet(9, 0);
        internalSet(10, 0);
        internalSet(12, 0);
        internalSet(13, 0);
        internalSet(14, 0);
      } 
      n |= 0x7E00;
    } 
    if ((paramInt1 & 0x18000) != 0) {
      internalSet(15, this.zoneOffsets[0]);
      internalSet(16, this.zoneOffsets[1]);
      n |= 0x18000;
    } 
    if ((paramInt1 & 0x158) != 0) {
      long l1;
      int i4;
      int i3 = this.jdate.getNormalizedYear();
      boolean bool = isTransitionYear(this.jdate.getNormalizedYear());
      if (bool) {
        l1 = getFixedDateJan1(this.jdate, l);
        i4 = (int)(l - l1) + 1;
      } else if (i3 == MIN_VALUES[1]) {
        LocalGregorianCalendar.Date date = jcal.getCalendarDate(Float.MIN_VALUE, getZone());
        l1 = jcal.getFixedDate(date);
        i4 = (int)(l - l1) + 1;
      } else {
        i4 = (int)jcal.getDayOfYear(this.jdate);
        l1 = l - i4 + 1L;
      } 
      long l2 = bool ? getFixedDateMonth1(this.jdate, l) : (l - i2 + 1L);
      internalSet(6, i4);
      internalSet(8, (i2 - 1) / 7 + 1);
      int i5 = getWeekNumber(l1, l);
      if (i5 == 0) {
        long l4;
        long l3 = l1 - 1L;
        LocalGregorianCalendar.Date date = getCalendarDate(l3);
        if (!bool && !isTransitionYear(date.getNormalizedYear())) {
          l4 = l1 - 365L;
          if (date.isLeapYear())
            l4--; 
        } else if (bool) {
          if (this.jdate.getYear() == 1) {
            if (k > 4) {
              CalendarDate calendarDate = eras[k - 1].getSinceDate();
              if (i3 == calendarDate.getYear())
                date.setMonth(calendarDate.getMonth()).setDayOfMonth(calendarDate.getDayOfMonth()); 
            } else {
              date.setMonth(1).setDayOfMonth(1);
            } 
            jcal.normalize(date);
            l4 = jcal.getFixedDate(date);
          } else {
            l4 = l1 - 365L;
            if (date.isLeapYear())
              l4--; 
          } 
        } else {
          CalendarDate calendarDate = eras[getEraIndex(this.jdate)].getSinceDate();
          date.setMonth(calendarDate.getMonth()).setDayOfMonth(calendarDate.getDayOfMonth());
          jcal.normalize(date);
          l4 = jcal.getFixedDate(date);
        } 
        i5 = getWeekNumber(l4, l3);
      } else if (!bool) {
        if (i5 >= 52) {
          long l3 = l1 + 365L;
          if (this.jdate.isLeapYear())
            l3++; 
          long l4 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(l3 + 6L, getFirstDayOfWeek());
          int i6 = (int)(l4 - l3);
          if (i6 >= getMinimalDaysInFirstWeek() && l >= l4 - 7L)
            i5 = 1; 
        } 
      } else {
        long l3;
        LocalGregorianCalendar.Date date = (LocalGregorianCalendar.Date)this.jdate.clone();
        if (this.jdate.getYear() == 1) {
          date.addYear(1);
          date.setMonth(1).setDayOfMonth(1);
          l3 = jcal.getFixedDate(date);
        } else {
          int i7 = getEraIndex(date) + 1;
          CalendarDate calendarDate = eras[i7].getSinceDate();
          date.setEra(eras[i7]);
          date.setDate(1, calendarDate.getMonth(), calendarDate.getDayOfMonth());
          jcal.normalize(date);
          l3 = jcal.getFixedDate(date);
        } 
        long l4 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(l3 + 6L, getFirstDayOfWeek());
        int i6 = (int)(l4 - l3);
        if (i6 >= getMinimalDaysInFirstWeek() && l >= l4 - 7L)
          i5 = 1; 
      } 
      internalSet(3, i5);
      internalSet(4, getWeekNumber(l2, l));
      n |= 0x158;
    } 
    return n;
  }
  
  private int getWeekNumber(long paramLong1, long paramLong2) {
    long l = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(paramLong1 + 6L, getFirstDayOfWeek());
    int i = (int)(l - paramLong1);
    assert i <= 7;
    if (i >= getMinimalDaysInFirstWeek())
      l -= 7L; 
    int j = (int)(paramLong2 - l);
    return (j >= 0) ? (j / 7 + 1) : (CalendarUtils.floorDivide(j, 7) + 1);
  }
  
  protected void computeTime() {
    byte b2;
    byte b1;
    if (!isLenient()) {
      if (this.originalFields == null)
        this.originalFields = new int[17]; 
      for (byte b = 0; b < 17; b++) {
        b1 = internalGet(b);
        if (isExternallySet(b) && (b1 < getMinimum(b) || b1 > getMaximum(b)))
          throw new IllegalArgumentException(getFieldName(b)); 
        this.originalFields[b] = b1;
      } 
    } 
    int i = selectFields();
    if (isSet(0)) {
      b2 = internalGet(0);
      b1 = isSet(1) ? internalGet(1) : 1;
    } else if (isSet(1)) {
      b2 = eras.length - 1;
      b1 = internalGet(1);
    } else {
      b2 = 3;
      b1 = 45;
    } 
    long l1 = 0L;
    if (isFieldSet(i, 11)) {
      l1 += internalGet(11);
    } else {
      l1 += internalGet(10);
      if (isFieldSet(i, 9))
        l1 += (12 * internalGet(9)); 
    } 
    l1 *= 60L;
    l1 += internalGet(12);
    l1 *= 60L;
    l1 += internalGet(13);
    l1 *= 1000L;
    l1 += internalGet(14);
    long l2 = l1 / 86400000L;
    l1 %= 86400000L;
    while (l1 < 0L) {
      l1 += 86400000L;
      l2--;
    } 
    l2 += getFixedDate(b2, b1, i);
    long l3 = (l2 - 719163L) * 86400000L + l1;
    TimeZone timeZone = getZone();
    if (this.zoneOffsets == null)
      this.zoneOffsets = new int[2]; 
    int j = i & 0x18000;
    if (j != 98304)
      if (timeZone instanceof ZoneInfo) {
        ((ZoneInfo)timeZone).getOffsetsByWall(l3, this.zoneOffsets);
      } else {
        timeZone.getOffsets(l3 - timeZone.getRawOffset(), this.zoneOffsets);
      }  
    if (j != 0) {
      if (isFieldSet(j, 15))
        this.zoneOffsets[0] = internalGet(15); 
      if (isFieldSet(j, 16))
        this.zoneOffsets[1] = internalGet(16); 
    } 
    l3 -= (this.zoneOffsets[0] + this.zoneOffsets[1]);
    this.time = l3;
    int k = computeFields(i | getSetStateFields(), j);
    if (!isLenient())
      for (byte b = 0; b < 17; b++) {
        if (isExternallySet(b) && this.originalFields[b] != internalGet(b)) {
          int m = internalGet(b);
          System.arraycopy(this.originalFields, 0, this.fields, 0, this.fields.length);
          throw new IllegalArgumentException(getFieldName(b) + "=" + m + ", expected " + this.originalFields[b]);
        } 
      }  
    setFieldsNormalized(k);
  }
  
  private long getFixedDate(int paramInt1, int paramInt2, int paramInt3) {
    int i = 0;
    int j = 1;
    if (isFieldSet(paramInt3, 2)) {
      i = internalGet(2);
      if (i > 11) {
        paramInt2 += i / 12;
        i %= 12;
      } else if (i < 0) {
        int[] arrayOfInt = new int[1];
        paramInt2 += CalendarUtils.floorDivide(i, 12, arrayOfInt);
        i = arrayOfInt[0];
      } 
    } else if (paramInt2 == 1 && paramInt1 != 0) {
      CalendarDate calendarDate = eras[paramInt1].getSinceDate();
      i = calendarDate.getMonth() - 1;
      j = calendarDate.getDayOfMonth();
    } 
    if (paramInt2 == MIN_VALUES[1]) {
      LocalGregorianCalendar.Date date1 = jcal.getCalendarDate(Float.MIN_VALUE, getZone());
      int k = date1.getMonth() - 1;
      if (i < k)
        i = k; 
      if (i == k)
        j = date1.getDayOfMonth(); 
    } 
    LocalGregorianCalendar.Date date = jcal.newCalendarDate(TimeZone.NO_TIMEZONE);
    date.setEra((paramInt1 > 0) ? eras[paramInt1] : null);
    date.setDate(paramInt2, i + 1, j);
    jcal.normalize(date);
    long l = jcal.getFixedDate(date);
    if (isFieldSet(paramInt3, 2)) {
      if (isFieldSet(paramInt3, 5)) {
        if (isSet(5)) {
          l += internalGet(5);
          l -= j;
        } 
      } else if (isFieldSet(paramInt3, 4)) {
        long l1 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(l + 6L, getFirstDayOfWeek());
        if (l1 - l >= getMinimalDaysInFirstWeek())
          l1 -= 7L; 
        if (isFieldSet(paramInt3, 7))
          l1 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(l1 + 6L, internalGet(7)); 
        l = l1 + (7 * (internalGet(4) - 1));
      } else {
        boolean bool;
        int k;
        if (isFieldSet(paramInt3, 7)) {
          k = internalGet(7);
        } else {
          k = getFirstDayOfWeek();
        } 
        if (isFieldSet(paramInt3, 8)) {
          bool = internalGet(8);
        } else {
          bool = true;
        } 
        if (bool) {
          l = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(l + (7 * bool) - 1L, k);
        } else {
          int m = monthLength(i, paramInt2) + 7 * (bool + true);
          l = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(l + m - 1L, k);
        } 
      } 
    } else if (isFieldSet(paramInt3, 6)) {
      if (isTransitionYear(date.getNormalizedYear()))
        l = getFixedDateJan1(date, l); 
      l += internalGet(6);
      l--;
    } else {
      long l1 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(l + 6L, getFirstDayOfWeek());
      if (l1 - l >= getMinimalDaysInFirstWeek())
        l1 -= 7L; 
      if (isFieldSet(paramInt3, 7)) {
        int k = internalGet(7);
        if (k != getFirstDayOfWeek())
          l1 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(l1 + 6L, k); 
      } 
      l = l1 + 7L * (internalGet(3) - 1L);
    } 
    return l;
  }
  
  private long getFixedDateJan1(LocalGregorianCalendar.Date paramDate, long paramLong) {
    Era era = paramDate.getEra();
    if (paramDate.getEra() != null && paramDate.getYear() == 1) {
      int i = getEraIndex(paramDate);
      while (i > 0) {
        CalendarDate calendarDate = eras[i].getSinceDate();
        long l = gcal.getFixedDate(calendarDate);
        if (l > paramLong) {
          i--;
          continue;
        } 
        return l;
      } 
    } 
    Gregorian.Date date = gcal.newCalendarDate(TimeZone.NO_TIMEZONE);
    date.setDate(paramDate.getNormalizedYear(), 1, 1);
    return gcal.getFixedDate(date);
  }
  
  private long getFixedDateMonth1(LocalGregorianCalendar.Date paramDate, long paramLong) {
    int i = getTransitionEraIndex(paramDate);
    if (i != -1) {
      long l = sinceFixedDates[i];
      if (l <= paramLong)
        return l; 
    } 
    return paramLong - paramDate.getDayOfMonth() + 1L;
  }
  
  private static LocalGregorianCalendar.Date getCalendarDate(long paramLong) {
    LocalGregorianCalendar.Date date = jcal.newCalendarDate(TimeZone.NO_TIMEZONE);
    jcal.getCalendarDateFromFixedDate(date, paramLong);
    return date;
  }
  
  private int monthLength(int paramInt1, int paramInt2) { return CalendarUtils.isGregorianLeapYear(paramInt2) ? GregorianCalendar.LEAP_MONTH_LENGTH[paramInt1] : GregorianCalendar.MONTH_LENGTH[paramInt1]; }
  
  private int monthLength(int paramInt) {
    assert this.jdate.isNormalized();
    return this.jdate.isLeapYear() ? GregorianCalendar.LEAP_MONTH_LENGTH[paramInt] : GregorianCalendar.MONTH_LENGTH[paramInt];
  }
  
  private int actualMonthLength() {
    int i = jcal.getMonthLength(this.jdate);
    int j = getTransitionEraIndex(this.jdate);
    if (j == -1) {
      long l = sinceFixedDates[j];
      CalendarDate calendarDate = eras[j].getSinceDate();
      if (l <= this.cachedFixedDate) {
        i -= calendarDate.getDayOfMonth() - 1;
      } else {
        i = calendarDate.getDayOfMonth() - 1;
      } 
    } 
    return i;
  }
  
  private static int getTransitionEraIndex(LocalGregorianCalendar.Date paramDate) {
    int i = getEraIndex(paramDate);
    CalendarDate calendarDate = eras[i].getSinceDate();
    if (calendarDate.getYear() == paramDate.getNormalizedYear() && calendarDate.getMonth() == paramDate.getMonth())
      return i; 
    if (i < eras.length - 1) {
      calendarDate = eras[++i].getSinceDate();
      if (calendarDate.getYear() == paramDate.getNormalizedYear() && calendarDate.getMonth() == paramDate.getMonth())
        return i; 
    } 
    return -1;
  }
  
  private boolean isTransitionYear(int paramInt) {
    for (int i = eras.length - 1; i > 0; i--) {
      int j = eras[i].getSinceDate().getYear();
      if (paramInt == j)
        return true; 
      if (paramInt > j)
        break; 
    } 
    return false;
  }
  
  private static int getEraIndex(LocalGregorianCalendar.Date paramDate) {
    Era era = paramDate.getEra();
    for (int i = eras.length - 1; i > 0; i--) {
      if (eras[i] == era)
        return i; 
    } 
    return 0;
  }
  
  private JapaneseImperialCalendar getNormalizedCalendar() {
    JapaneseImperialCalendar japaneseImperialCalendar;
    if (isFullyNormalized()) {
      japaneseImperialCalendar = this;
    } else {
      japaneseImperialCalendar = (JapaneseImperialCalendar)clone();
      japaneseImperialCalendar.setLenient(true);
      japaneseImperialCalendar.complete();
    } 
    return japaneseImperialCalendar;
  }
  
  private void pinDayOfMonth(LocalGregorianCalendar.Date paramDate) {
    int i = paramDate.getYear();
    int j = paramDate.getDayOfMonth();
    if (i != getMinimum(1)) {
      paramDate.setDayOfMonth(1);
      jcal.normalize(paramDate);
      int k = jcal.getMonthLength(paramDate);
      if (j > k) {
        paramDate.setDayOfMonth(k);
      } else {
        paramDate.setDayOfMonth(j);
      } 
      jcal.normalize(paramDate);
    } else {
      LocalGregorianCalendar.Date date1 = jcal.getCalendarDate(Float.MIN_VALUE, getZone());
      LocalGregorianCalendar.Date date2 = jcal.getCalendarDate(this.time, getZone());
      long l = date2.getTimeOfDay();
      date2.addYear(400);
      date2.setMonth(paramDate.getMonth());
      date2.setDayOfMonth(1);
      jcal.normalize(date2);
      int k = jcal.getMonthLength(date2);
      if (j > k) {
        date2.setDayOfMonth(k);
      } else if (j < date1.getDayOfMonth()) {
        date2.setDayOfMonth(date1.getDayOfMonth());
      } else {
        date2.setDayOfMonth(j);
      } 
      if (date2.getDayOfMonth() == date1.getDayOfMonth() && l < date1.getTimeOfDay())
        date2.setDayOfMonth(Math.min(j + 1, k)); 
      paramDate.setDate(i, date2.getMonth(), date2.getDayOfMonth());
    } 
  }
  
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
  
  private int internalGetEra() { return isSet(0) ? internalGet(0) : (eras.length - 1); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    if (this.jdate == null) {
      this.jdate = jcal.newCalendarDate(getZone());
      this.cachedFixedDate = Float.MIN_VALUE;
    } 
  }
  
  static  {
    Era[] arrayOfEra = jcal.getEras();
    int i = arrayOfEra.length + 1;
    eras = new Era[i];
    sinceFixedDates = new long[i];
    byte b1 = 0;
    sinceFixedDates[b1] = gcal.getFixedDate(BEFORE_MEIJI_ERA.getSinceDate());
    eras[b1++] = BEFORE_MEIJI_ERA;
    for (Era era : arrayOfEra) {
      CalendarDate calendarDate = era.getSinceDate();
      sinceFixedDates[b1] = gcal.getFixedDate(calendarDate);
      eras[b1++] = era;
    } 
    MAX_VALUES[0] = eras.length - 1;
    LEAST_MAX_VALUES[0] = eras.length - 1;
    int j = Integer.MAX_VALUE;
    int k = Integer.MAX_VALUE;
    Gregorian.Date date = gcal.newCalendarDate(TimeZone.NO_TIMEZONE);
    for (byte b2 = 1; b2 < eras.length; b2++) {
      long l1 = sinceFixedDates[b2];
      CalendarDate calendarDate = eras[b2].getSinceDate();
      date.setDate(calendarDate.getYear(), 1, 1);
      long l2 = gcal.getFixedDate(date);
      if (l1 != l2)
        k = Math.min((int)(l1 - l2) + 1, k); 
      date.setDate(calendarDate.getYear(), 12, 31);
      l2 = gcal.getFixedDate(date);
      if (l1 != l2)
        k = Math.min((int)(l2 - l1) + 1, k); 
      LocalGregorianCalendar.Date date1 = getCalendarDate(l1 - 1L);
      int m = date1.getYear();
      if (date1.getMonth() != 1 || date1.getDayOfMonth() != 1)
        m--; 
      j = Math.min(m, j);
    } 
    LEAST_MAX_VALUES[1] = j;
    LEAST_MAX_VALUES[6] = k;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\JapaneseImperialCalendar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */