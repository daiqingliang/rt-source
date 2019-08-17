package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PermissionCollection;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.util.BuddhistCalendar;
import sun.util.calendar.ZoneInfo;
import sun.util.locale.provider.CalendarDataUtility;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.spi.CalendarProvider;

public abstract class Calendar extends Object implements Serializable, Cloneable, Comparable<Calendar> {
  public static final int ERA = 0;
  
  public static final int YEAR = 1;
  
  public static final int MONTH = 2;
  
  public static final int WEEK_OF_YEAR = 3;
  
  public static final int WEEK_OF_MONTH = 4;
  
  public static final int DATE = 5;
  
  public static final int DAY_OF_MONTH = 5;
  
  public static final int DAY_OF_YEAR = 6;
  
  public static final int DAY_OF_WEEK = 7;
  
  public static final int DAY_OF_WEEK_IN_MONTH = 8;
  
  public static final int AM_PM = 9;
  
  public static final int HOUR = 10;
  
  public static final int HOUR_OF_DAY = 11;
  
  public static final int MINUTE = 12;
  
  public static final int SECOND = 13;
  
  public static final int MILLISECOND = 14;
  
  public static final int ZONE_OFFSET = 15;
  
  public static final int DST_OFFSET = 16;
  
  public static final int FIELD_COUNT = 17;
  
  public static final int SUNDAY = 1;
  
  public static final int MONDAY = 2;
  
  public static final int TUESDAY = 3;
  
  public static final int WEDNESDAY = 4;
  
  public static final int THURSDAY = 5;
  
  public static final int FRIDAY = 6;
  
  public static final int SATURDAY = 7;
  
  public static final int JANUARY = 0;
  
  public static final int FEBRUARY = 1;
  
  public static final int MARCH = 2;
  
  public static final int APRIL = 3;
  
  public static final int MAY = 4;
  
  public static final int JUNE = 5;
  
  public static final int JULY = 6;
  
  public static final int AUGUST = 7;
  
  public static final int SEPTEMBER = 8;
  
  public static final int OCTOBER = 9;
  
  public static final int NOVEMBER = 10;
  
  public static final int DECEMBER = 11;
  
  public static final int UNDECIMBER = 12;
  
  public static final int AM = 0;
  
  public static final int PM = 1;
  
  public static final int ALL_STYLES = 0;
  
  static final int STANDALONE_MASK = 32768;
  
  public static final int SHORT = 1;
  
  public static final int LONG = 2;
  
  public static final int NARROW_FORMAT = 4;
  
  public static final int NARROW_STANDALONE = 32772;
  
  public static final int SHORT_FORMAT = 1;
  
  public static final int LONG_FORMAT = 2;
  
  public static final int SHORT_STANDALONE = 32769;
  
  public static final int LONG_STANDALONE = 32770;
  
  protected int[] fields = new int[17];
  
  protected boolean[] isSet = new boolean[17];
  
  private int[] stamp = new int[17];
  
  protected long time;
  
  protected boolean isTimeSet;
  
  protected boolean areFieldsSet;
  
  boolean areAllFieldsSet;
  
  private boolean lenient = true;
  
  private TimeZone zone;
  
  private boolean sharedZone = false;
  
  private int firstDayOfWeek;
  
  private int minimalDaysInFirstWeek;
  
  private static final ConcurrentMap<Locale, int[]> cachedLocaleData = new ConcurrentHashMap(3);
  
  private static final int UNSET = 0;
  
  private static final int COMPUTED = 1;
  
  private static final int MINIMUM_USER_STAMP = 2;
  
  static final int ALL_FIELDS = 131071;
  
  private int nextStamp = 2;
  
  static final int currentSerialVersion = 1;
  
  private int serialVersionOnStream = 1;
  
  static final long serialVersionUID = -1807547505821590642L;
  
  static final int ERA_MASK = 1;
  
  static final int YEAR_MASK = 2;
  
  static final int MONTH_MASK = 4;
  
  static final int WEEK_OF_YEAR_MASK = 8;
  
  static final int WEEK_OF_MONTH_MASK = 16;
  
  static final int DAY_OF_MONTH_MASK = 32;
  
  static final int DATE_MASK = 32;
  
  static final int DAY_OF_YEAR_MASK = 64;
  
  static final int DAY_OF_WEEK_MASK = 128;
  
  static final int DAY_OF_WEEK_IN_MONTH_MASK = 256;
  
  static final int AM_PM_MASK = 512;
  
  static final int HOUR_MASK = 1024;
  
  static final int HOUR_OF_DAY_MASK = 2048;
  
  static final int MINUTE_MASK = 4096;
  
  static final int SECOND_MASK = 8192;
  
  static final int MILLISECOND_MASK = 16384;
  
  static final int ZONE_OFFSET_MASK = 32768;
  
  static final int DST_OFFSET_MASK = 65536;
  
  private static final String[] FIELD_NAME = { 
      "ERA", "YEAR", "MONTH", "WEEK_OF_YEAR", "WEEK_OF_MONTH", "DAY_OF_MONTH", "DAY_OF_YEAR", "DAY_OF_WEEK", "DAY_OF_WEEK_IN_MONTH", "AM_PM", 
      "HOUR", "HOUR_OF_DAY", "MINUTE", "SECOND", "MILLISECOND", "ZONE_OFFSET", "DST_OFFSET" };
  
  protected Calendar() {
    this(TimeZone.getDefaultRef(), Locale.getDefault(Locale.Category.FORMAT));
    this.sharedZone = true;
  }
  
  protected Calendar(TimeZone paramTimeZone, Locale paramLocale) {
    this.zone = paramTimeZone;
    setWeekCountData(paramLocale);
  }
  
  public static Calendar getInstance() { return createCalendar(TimeZone.getDefault(), Locale.getDefault(Locale.Category.FORMAT)); }
  
  public static Calendar getInstance(TimeZone paramTimeZone) { return createCalendar(paramTimeZone, Locale.getDefault(Locale.Category.FORMAT)); }
  
  public static Calendar getInstance(Locale paramLocale) { return createCalendar(TimeZone.getDefault(), paramLocale); }
  
  public static Calendar getInstance(TimeZone paramTimeZone, Locale paramLocale) { return createCalendar(paramTimeZone, paramLocale); }
  
  private static Calendar createCalendar(TimeZone paramTimeZone, Locale paramLocale) {
    CalendarProvider calendarProvider = LocaleProviderAdapter.getAdapter(CalendarProvider.class, paramLocale).getCalendarProvider();
    if (calendarProvider != null)
      try {
        return calendarProvider.getInstance(paramTimeZone, paramLocale);
      } catch (IllegalArgumentException illegalArgumentException) {} 
    GregorianCalendar gregorianCalendar = null;
    if (paramLocale.hasExtensions()) {
      String str = paramLocale.getUnicodeLocaleType("ca");
      if (str != null) {
        JapaneseImperialCalendar japaneseImperialCalendar;
        switch (str) {
          case "buddhist":
            gregorianCalendar = new BuddhistCalendar(paramTimeZone, paramLocale);
            break;
          case "japanese":
            japaneseImperialCalendar = new JapaneseImperialCalendar(paramTimeZone, paramLocale);
            break;
          case "gregory":
            gregorianCalendar = new GregorianCalendar(paramTimeZone, paramLocale);
            break;
        } 
      } 
    } 
    if (gregorianCalendar == null)
      if (paramLocale.getLanguage() == "th" && paramLocale.getCountry() == "TH") {
        gregorianCalendar = new BuddhistCalendar(paramTimeZone, paramLocale);
      } else if (paramLocale.getVariant() == "JP" && paramLocale.getLanguage() == "ja" && paramLocale.getCountry() == "JP") {
        JapaneseImperialCalendar japaneseImperialCalendar = new JapaneseImperialCalendar(paramTimeZone, paramLocale);
      } else {
        gregorianCalendar = new GregorianCalendar(paramTimeZone, paramLocale);
      }  
    return gregorianCalendar;
  }
  
  public static Locale[] getAvailableLocales() { return DateFormat.getAvailableLocales(); }
  
  protected abstract void computeTime();
  
  protected abstract void computeFields();
  
  public final Date getTime() { return new Date(getTimeInMillis()); }
  
  public final void setTime(Date paramDate) { setTimeInMillis(paramDate.getTime()); }
  
  public long getTimeInMillis() {
    if (!this.isTimeSet)
      updateTime(); 
    return this.time;
  }
  
  public void setTimeInMillis(long paramLong) {
    if (this.time == paramLong && this.isTimeSet && this.areFieldsSet && this.areAllFieldsSet && this.zone instanceof ZoneInfo && !((ZoneInfo)this.zone).isDirty())
      return; 
    this.time = paramLong;
    this.isTimeSet = true;
    this.areFieldsSet = false;
    computeFields();
    this.areAllFieldsSet = this.areFieldsSet = true;
  }
  
  public int get(int paramInt) {
    complete();
    return internalGet(paramInt);
  }
  
  protected final int internalGet(int paramInt) { return this.fields[paramInt]; }
  
  final void internalSet(int paramInt1, int paramInt2) { this.fields[paramInt1] = paramInt2; }
  
  public void set(int paramInt1, int paramInt2) {
    if (this.areFieldsSet && !this.areAllFieldsSet)
      computeFields(); 
    internalSet(paramInt1, paramInt2);
    this.isTimeSet = false;
    this.areFieldsSet = false;
    this.isSet[paramInt1] = true;
    this.stamp[paramInt1] = this.nextStamp++;
    if (this.nextStamp == Integer.MAX_VALUE)
      adjustStamp(); 
  }
  
  public final void set(int paramInt1, int paramInt2, int paramInt3) {
    set(1, paramInt1);
    set(2, paramInt2);
    set(5, paramInt3);
  }
  
  public final void set(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    set(1, paramInt1);
    set(2, paramInt2);
    set(5, paramInt3);
    set(11, paramInt4);
    set(12, paramInt5);
  }
  
  public final void set(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    set(1, paramInt1);
    set(2, paramInt2);
    set(5, paramInt3);
    set(11, paramInt4);
    set(12, paramInt5);
    set(13, paramInt6);
  }
  
  public final void clear() {
    byte b = 0;
    while (b < this.fields.length) {
      this.fields[b] = 0;
      this.stamp[b] = 0;
      this.isSet[b++] = false;
    } 
    this.areAllFieldsSet = this.areFieldsSet = false;
    this.isTimeSet = false;
  }
  
  public final void clear(int paramInt) {
    this.fields[paramInt] = 0;
    this.stamp[paramInt] = 0;
    this.isSet[paramInt] = false;
    this.areAllFieldsSet = this.areFieldsSet = false;
    this.isTimeSet = false;
  }
  
  public final boolean isSet(int paramInt) { return (this.stamp[paramInt] != 0); }
  
  public String getDisplayName(int paramInt1, int paramInt2, Locale paramLocale) {
    if (!checkDisplayNameParams(paramInt1, paramInt2, 1, 4, paramLocale, 645))
      return null; 
    String str = getCalendarType();
    int i = get(paramInt1);
    if (isStandaloneStyle(paramInt2) || isNarrowFormatStyle(paramInt2)) {
      String str1 = CalendarDataUtility.retrieveFieldValueName(str, paramInt1, i, paramInt2, paramLocale);
      if (str1 == null)
        if (isNarrowFormatStyle(paramInt2)) {
          str1 = CalendarDataUtility.retrieveFieldValueName(str, paramInt1, i, toStandaloneStyle(paramInt2), paramLocale);
        } else if (isStandaloneStyle(paramInt2)) {
          str1 = CalendarDataUtility.retrieveFieldValueName(str, paramInt1, i, getBaseStyle(paramInt2), paramLocale);
        }  
      return str1;
    } 
    DateFormatSymbols dateFormatSymbols = DateFormatSymbols.getInstance(paramLocale);
    String[] arrayOfString = getFieldStrings(paramInt1, paramInt2, dateFormatSymbols);
    return (arrayOfString != null && i < arrayOfString.length) ? arrayOfString[i] : null;
  }
  
  public Map<String, Integer> getDisplayNames(int paramInt1, int paramInt2, Locale paramLocale) {
    if (!checkDisplayNameParams(paramInt1, paramInt2, 0, 4, paramLocale, 645))
      return null; 
    String str = getCalendarType();
    if (paramInt2 == 0 || isStandaloneStyle(paramInt2) || isNarrowFormatStyle(paramInt2)) {
      Map map = CalendarDataUtility.retrieveFieldValueNames(str, paramInt1, paramInt2, paramLocale);
      if (map == null)
        if (isNarrowFormatStyle(paramInt2)) {
          map = CalendarDataUtility.retrieveFieldValueNames(str, paramInt1, toStandaloneStyle(paramInt2), paramLocale);
        } else if (paramInt2 != 0) {
          map = CalendarDataUtility.retrieveFieldValueNames(str, paramInt1, getBaseStyle(paramInt2), paramLocale);
        }  
      return map;
    } 
    return getDisplayNamesImpl(paramInt1, paramInt2, paramLocale);
  }
  
  private Map<String, Integer> getDisplayNamesImpl(int paramInt1, int paramInt2, Locale paramLocale) {
    DateFormatSymbols dateFormatSymbols = DateFormatSymbols.getInstance(paramLocale);
    String[] arrayOfString = getFieldStrings(paramInt1, paramInt2, dateFormatSymbols);
    if (arrayOfString != null) {
      HashMap hashMap = new HashMap();
      for (byte b = 0; b < arrayOfString.length; b++) {
        if (arrayOfString[b].length() != 0)
          hashMap.put(arrayOfString[b], Integer.valueOf(b)); 
      } 
      return hashMap;
    } 
    return null;
  }
  
  boolean checkDisplayNameParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Locale paramLocale, int paramInt5) {
    int i = getBaseStyle(paramInt2);
    if (paramInt1 < 0 || paramInt1 >= this.fields.length || i < paramInt3 || i > paramInt4)
      throw new IllegalArgumentException(); 
    if (paramLocale == null)
      throw new NullPointerException(); 
    return isFieldSet(paramInt5, paramInt1);
  }
  
  private String[] getFieldStrings(int paramInt1, int paramInt2, DateFormatSymbols paramDateFormatSymbols) {
    int i = getBaseStyle(paramInt2);
    if (i == 4)
      return null; 
    String[] arrayOfString = null;
    switch (paramInt1) {
      case 0:
        arrayOfString = paramDateFormatSymbols.getEras();
        break;
      case 2:
        arrayOfString = (i == 2) ? paramDateFormatSymbols.getMonths() : paramDateFormatSymbols.getShortMonths();
        break;
      case 7:
        arrayOfString = (i == 2) ? paramDateFormatSymbols.getWeekdays() : paramDateFormatSymbols.getShortWeekdays();
        break;
      case 9:
        arrayOfString = paramDateFormatSymbols.getAmPmStrings();
        break;
    } 
    return arrayOfString;
  }
  
  protected void complete() {
    if (!this.isTimeSet)
      updateTime(); 
    if (!this.areFieldsSet || !this.areAllFieldsSet) {
      computeFields();
      this.areAllFieldsSet = this.areFieldsSet = true;
    } 
  }
  
  final boolean isExternallySet(int paramInt) { return (this.stamp[paramInt] >= 2); }
  
  final int getSetStateFields() {
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.fields.length; b2++) {
      if (this.stamp[b2] != 0)
        b1 |= true << b2; 
    } 
    return b1;
  }
  
  final void setFieldsComputed(int paramInt) {
    if (paramInt == 131071) {
      for (byte b = 0; b < this.fields.length; b++) {
        this.stamp[b] = 1;
        this.isSet[b] = true;
      } 
      this.areFieldsSet = this.areAllFieldsSet = true;
    } else {
      for (byte b = 0; b < this.fields.length; b++) {
        if ((paramInt & true) == 1) {
          this.stamp[b] = 1;
          this.isSet[b] = true;
        } else if (this.areAllFieldsSet && !this.isSet[b]) {
          this.areAllFieldsSet = false;
        } 
        paramInt >>>= 1;
      } 
    } 
  }
  
  final void setFieldsNormalized(int paramInt) {
    if (paramInt != 131071)
      for (byte b = 0; b < this.fields.length; b++) {
        if ((paramInt & true) == 0) {
          this.fields[b] = 0;
          this.stamp[b] = 0;
          this.isSet[b] = false;
        } 
        paramInt >>= 1;
      }  
    this.areFieldsSet = true;
    this.areAllFieldsSet = false;
  }
  
  final boolean isPartiallyNormalized() { return (this.areFieldsSet && !this.areAllFieldsSet); }
  
  final boolean isFullyNormalized() { return (this.areFieldsSet && this.areAllFieldsSet); }
  
  final void setUnnormalized() { this.areFieldsSet = this.areAllFieldsSet = false; }
  
  static boolean isFieldSet(int paramInt1, int paramInt2) { return ((paramInt1 & 1 << paramInt2) != 0); }
  
  final int selectFields() {
    int i = 2;
    if (this.stamp[0] != 0)
      i |= 0x1; 
    int j = this.stamp[7];
    int k = this.stamp[2];
    int m = this.stamp[5];
    int n = aggregateStamp(this.stamp[4], j);
    int i1 = aggregateStamp(this.stamp[8], j);
    int i2 = this.stamp[6];
    int i3 = aggregateStamp(this.stamp[3], j);
    int i4 = m;
    if (n > i4)
      i4 = n; 
    if (i1 > i4)
      i4 = i1; 
    if (i2 > i4)
      i4 = i2; 
    if (i3 > i4)
      i4 = i3; 
    if (i4 == 0) {
      n = this.stamp[4];
      i1 = Math.max(this.stamp[8], j);
      i3 = this.stamp[3];
      i4 = Math.max(Math.max(n, i1), i3);
      if (i4 == 0)
        i4 = m = k; 
    } 
    if (i4 == m || (i4 == n && this.stamp[4] >= this.stamp[3]) || (i4 == i1 && this.stamp[8] >= this.stamp[3])) {
      i |= 0x4;
      if (i4 == m) {
        i |= 0x20;
      } else {
        assert i4 == n || i4 == i1;
        if (j != 0)
          i |= 0x80; 
        if (n == i1) {
          if (this.stamp[4] >= this.stamp[8]) {
            i |= 0x10;
          } else {
            i |= 0x100;
          } 
        } else if (i4 == n) {
          i |= 0x10;
        } else {
          assert i4 == i1;
          if (this.stamp[8] != 0)
            i |= 0x100; 
        } 
      } 
    } else {
      assert i4 == i2 || i4 == i3 || i4 == 0;
      if (i4 == i2) {
        i |= 0x40;
      } else {
        assert i4 == i3;
        if (j != 0)
          i |= 0x80; 
        i |= 0x8;
      } 
    } 
    int i5 = this.stamp[11];
    int i6 = aggregateStamp(this.stamp[10], this.stamp[9]);
    i4 = (i6 > i5) ? i6 : i5;
    if (i4 == 0)
      i4 = Math.max(this.stamp[10], this.stamp[9]); 
    if (i4 != 0)
      if (i4 == i5) {
        i |= 0x800;
      } else {
        i |= 0x400;
        if (this.stamp[9] != 0)
          i |= 0x200; 
      }  
    if (this.stamp[12] != 0)
      i |= 0x1000; 
    if (this.stamp[13] != 0)
      i |= 0x2000; 
    if (this.stamp[14] != 0)
      i |= 0x4000; 
    if (this.stamp[15] >= 2)
      i |= 0x8000; 
    if (this.stamp[16] >= 2)
      i |= 0x10000; 
    return i;
  }
  
  int getBaseStyle(int paramInt) { return paramInt & 0xFFFF7FFF; }
  
  private int toStandaloneStyle(int paramInt) { return paramInt | 0x8000; }
  
  private boolean isStandaloneStyle(int paramInt) { return ((paramInt & 0x8000) != 0); }
  
  private boolean isNarrowStyle(int paramInt) { return (paramInt == 4 || paramInt == 32772); }
  
  private boolean isNarrowFormatStyle(int paramInt) { return (paramInt == 4); }
  
  private static int aggregateStamp(int paramInt1, int paramInt2) { return (paramInt1 == 0 || paramInt2 == 0) ? 0 : ((paramInt1 > paramInt2) ? paramInt1 : paramInt2); }
  
  public static Set<String> getAvailableCalendarTypes() { return SET; }
  
  public String getCalendarType() { return getClass().getName(); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    try {
      Calendar calendar;
      return (compareTo((calendar = (Calendar)paramObject).getMillisOf(calendar)) == 0 && this.lenient == calendar.lenient && this.firstDayOfWeek == calendar.firstDayOfWeek && this.minimalDaysInFirstWeek == calendar.minimalDaysInFirstWeek && this.zone.equals(calendar.zone));
    } catch (Exception exception) {
      return false;
    } 
  }
  
  public int hashCode() {
    int i = (this.lenient ? 1 : 0) | this.firstDayOfWeek << 1 | this.minimalDaysInFirstWeek << 4 | this.zone.hashCode() << 7;
    long l = getMillisOf(this);
    return (int)l ^ (int)(l >> 32) ^ i;
  }
  
  public boolean before(Object paramObject) { return (paramObject instanceof Calendar && compareTo((Calendar)paramObject) < 0); }
  
  public boolean after(Object paramObject) { return (paramObject instanceof Calendar && compareTo((Calendar)paramObject) > 0); }
  
  public int compareTo(Calendar paramCalendar) { return compareTo(getMillisOf(paramCalendar)); }
  
  public abstract void add(int paramInt1, int paramInt2);
  
  public abstract void roll(int paramInt, boolean paramBoolean);
  
  public void roll(int paramInt1, int paramInt2) {
    while (paramInt2 > 0) {
      roll(paramInt1, true);
      paramInt2--;
    } 
    while (paramInt2 < 0) {
      roll(paramInt1, false);
      paramInt2++;
    } 
  }
  
  public void setTimeZone(TimeZone paramTimeZone) {
    this.zone = paramTimeZone;
    this.sharedZone = false;
    this.areAllFieldsSet = this.areFieldsSet = false;
  }
  
  public TimeZone getTimeZone() {
    if (this.sharedZone) {
      this.zone = (TimeZone)this.zone.clone();
      this.sharedZone = false;
    } 
    return this.zone;
  }
  
  TimeZone getZone() { return this.zone; }
  
  void setZoneShared(boolean paramBoolean) { this.sharedZone = paramBoolean; }
  
  public void setLenient(boolean paramBoolean) { this.lenient = paramBoolean; }
  
  public boolean isLenient() { return this.lenient; }
  
  public void setFirstDayOfWeek(int paramInt) {
    if (this.firstDayOfWeek == paramInt)
      return; 
    this.firstDayOfWeek = paramInt;
    invalidateWeekFields();
  }
  
  public int getFirstDayOfWeek() { return this.firstDayOfWeek; }
  
  public void setMinimalDaysInFirstWeek(int paramInt) {
    if (this.minimalDaysInFirstWeek == paramInt)
      return; 
    this.minimalDaysInFirstWeek = paramInt;
    invalidateWeekFields();
  }
  
  public int getMinimalDaysInFirstWeek() { return this.minimalDaysInFirstWeek; }
  
  public boolean isWeekDateSupported() { return false; }
  
  public int getWeekYear() { throw new UnsupportedOperationException(); }
  
  public void setWeekDate(int paramInt1, int paramInt2, int paramInt3) { throw new UnsupportedOperationException(); }
  
  public int getWeeksInWeekYear() { throw new UnsupportedOperationException(); }
  
  public abstract int getMinimum(int paramInt);
  
  public abstract int getMaximum(int paramInt);
  
  public abstract int getGreatestMinimum(int paramInt);
  
  public abstract int getLeastMaximum(int paramInt);
  
  public int getActualMinimum(int paramInt) {
    int i = getGreatestMinimum(paramInt);
    int j = getMinimum(paramInt);
    if (i == j)
      return i; 
    Calendar calendar = (Calendar)clone();
    calendar.setLenient(true);
    int k = i;
    do {
      calendar.set(paramInt, i);
      if (calendar.get(paramInt) != i)
        break; 
      k = i;
    } while (--i >= j);
    return k;
  }
  
  public int getActualMaximum(int paramInt) {
    int i = getLeastMaximum(paramInt);
    int j = getMaximum(paramInt);
    if (i == j)
      return i; 
    Calendar calendar = (Calendar)clone();
    calendar.setLenient(true);
    if (paramInt == 3 || paramInt == 4)
      calendar.set(7, this.firstDayOfWeek); 
    int k = i;
    do {
      calendar.set(paramInt, i);
      if (calendar.get(paramInt) != i)
        break; 
      k = i;
    } while (++i <= j);
    return k;
  }
  
  public Object clone() {
    try {
      Calendar calendar = (Calendar)super.clone();
      calendar.fields = new int[17];
      calendar.isSet = new boolean[17];
      calendar.stamp = new int[17];
      for (byte b = 0; b < 17; b++) {
        calendar.fields[b] = this.fields[b];
        calendar.stamp[b] = this.stamp[b];
        calendar.isSet[b] = this.isSet[b];
      } 
      calendar.zone = (TimeZone)this.zone.clone();
      return calendar;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
  
  static String getFieldName(int paramInt) { return FIELD_NAME[paramInt]; }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(800);
    stringBuilder.append(getClass().getName()).append('[');
    appendValue(stringBuilder, "time", this.isTimeSet, this.time);
    stringBuilder.append(",areFieldsSet=").append(this.areFieldsSet);
    stringBuilder.append(",areAllFieldsSet=").append(this.areAllFieldsSet);
    stringBuilder.append(",lenient=").append(this.lenient);
    stringBuilder.append(",zone=").append(this.zone);
    appendValue(stringBuilder, ",firstDayOfWeek", true, this.firstDayOfWeek);
    appendValue(stringBuilder, ",minimalDaysInFirstWeek", true, this.minimalDaysInFirstWeek);
    for (byte b = 0; b < 17; b++) {
      stringBuilder.append(',');
      appendValue(stringBuilder, FIELD_NAME[b], isSet(b), this.fields[b]);
    } 
    stringBuilder.append(']');
    return stringBuilder.toString();
  }
  
  private static void appendValue(StringBuilder paramStringBuilder, String paramString, boolean paramBoolean, long paramLong) {
    paramStringBuilder.append(paramString).append('=');
    if (paramBoolean) {
      paramStringBuilder.append(paramLong);
    } else {
      paramStringBuilder.append('?');
    } 
  }
  
  private void setWeekCountData(Locale paramLocale) {
    int[] arrayOfInt = (int[])cachedLocaleData.get(paramLocale);
    if (arrayOfInt == null) {
      arrayOfInt = new int[2];
      arrayOfInt[0] = CalendarDataUtility.retrieveFirstDayOfWeek(paramLocale);
      arrayOfInt[1] = CalendarDataUtility.retrieveMinimalDaysInFirstWeek(paramLocale);
      cachedLocaleData.putIfAbsent(paramLocale, arrayOfInt);
    } 
    this.firstDayOfWeek = arrayOfInt[0];
    this.minimalDaysInFirstWeek = arrayOfInt[1];
  }
  
  private void updateTime() {
    computeTime();
    this.isTimeSet = true;
  }
  
  private int compareTo(long paramLong) {
    long l = getMillisOf(this);
    return (l > paramLong) ? 1 : ((l == paramLong) ? 0 : -1);
  }
  
  private static long getMillisOf(Calendar paramCalendar) {
    if (paramCalendar.isTimeSet)
      return paramCalendar.time; 
    Calendar calendar = (Calendar)paramCalendar.clone();
    calendar.setLenient(true);
    return calendar.getTimeInMillis();
  }
  
  private void adjustStamp() {
    int j;
    int i = 2;
    byte b = 2;
    do {
      j = Integer.MAX_VALUE;
      byte b1;
      for (b1 = 0; b1 < this.stamp.length; b1++) {
        int k = this.stamp[b1];
        if (k >= b && j > k)
          j = k; 
        if (i < k)
          i = k; 
      } 
      if (i != j && j == Integer.MAX_VALUE)
        break; 
      for (b1 = 0; b1 < this.stamp.length; b1++) {
        if (this.stamp[b1] == j)
          this.stamp[b1] = b; 
      } 
      b++;
    } while (j != i);
    this.nextStamp = b;
  }
  
  private void invalidateWeekFields() {
    if (this.stamp[4] != 1 && this.stamp[3] != 1)
      return; 
    Calendar calendar = (Calendar)clone();
    calendar.setLenient(true);
    calendar.clear(4);
    calendar.clear(3);
    if (this.stamp[4] == 1) {
      int i = calendar.get(4);
      if (this.fields[4] != i)
        this.fields[4] = i; 
    } 
    if (this.stamp[3] == 1) {
      int i = calendar.get(3);
      if (this.fields[3] != i)
        this.fields[3] = i; 
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (!this.isTimeSet)
      try {
        updateTime();
      } catch (IllegalArgumentException illegalArgumentException) {} 
    TimeZone timeZone = null;
    if (this.zone instanceof ZoneInfo) {
      SimpleTimeZone simpleTimeZone = ((ZoneInfo)this.zone).getLastRuleInstance();
      if (simpleTimeZone == null)
        simpleTimeZone = new SimpleTimeZone(this.zone.getRawOffset(), this.zone.getID()); 
      timeZone = this.zone;
      this.zone = simpleTimeZone;
    } 
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(timeZone);
    if (timeZone != null)
      this.zone = timeZone; 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    final ObjectInputStream input = paramObjectInputStream;
    objectInputStream.defaultReadObject();
    this.stamp = new int[17];
    if (this.serialVersionOnStream >= 2) {
      this.isTimeSet = true;
      if (this.fields == null)
        this.fields = new int[17]; 
      if (this.isSet == null)
        this.isSet = new boolean[17]; 
    } else if (this.serialVersionOnStream >= 0) {
      for (byte b = 0; b < 17; b++)
        this.stamp[b] = this.isSet[b] ? 1 : 0; 
    } 
    this.serialVersionOnStream = 1;
    ZoneInfo zoneInfo = null;
    try {
      zoneInfo = (ZoneInfo)AccessController.doPrivileged(new PrivilegedExceptionAction<ZoneInfo>() {
            public ZoneInfo run() throws Exception { return (ZoneInfo)input.readObject(); }
          },  INSTANCE);
    } catch (PrivilegedActionException privilegedActionException) {
      Exception exception = privilegedActionException.getException();
      if (!(exception instanceof java.io.OptionalDataException)) {
        if (exception instanceof RuntimeException)
          throw (RuntimeException)exception; 
        if (exception instanceof IOException)
          throw (IOException)exception; 
        if (exception instanceof ClassNotFoundException)
          throw (ClassNotFoundException)exception; 
        throw new RuntimeException(exception);
      } 
    } 
    if (zoneInfo != null)
      this.zone = zoneInfo; 
    if (this.zone instanceof SimpleTimeZone) {
      String str = this.zone.getID();
      TimeZone timeZone = TimeZone.getTimeZone(str);
      if (timeZone != null && timeZone.hasSameRules(this.zone) && timeZone.getID().equals(str))
        this.zone = timeZone; 
    } 
  }
  
  public final Instant toInstant() { return Instant.ofEpochMilli(getTimeInMillis()); }
  
  private static class AvailableCalendarTypes {
    private static final Set<String> SET;
    
    static  {
      HashSet hashSet = new HashSet(3);
      hashSet.add("gregory");
      hashSet.add("buddhist");
      hashSet.add("japanese");
      SET = Collections.unmodifiableSet(hashSet);
    }
  }
  
  public static class Builder {
    private static final int NFIELDS = 18;
    
    private static final int WEEK_YEAR = 17;
    
    private long instant;
    
    private int[] fields;
    
    private int nextStamp;
    
    private int maxFieldIndex;
    
    private String type;
    
    private TimeZone zone;
    
    private boolean lenient = true;
    
    private Locale locale;
    
    private int firstDayOfWeek;
    
    private int minimalDaysInFirstWeek;
    
    public Builder setInstant(long param1Long) {
      if (this.fields != null)
        throw new IllegalStateException(); 
      this.instant = param1Long;
      this.nextStamp = 1;
      return this;
    }
    
    public Builder setInstant(Date param1Date) { return setInstant(param1Date.getTime()); }
    
    public Builder set(int param1Int1, int param1Int2) {
      if (param1Int1 < 0 || param1Int1 >= 17)
        throw new IllegalArgumentException("field is invalid"); 
      if (isInstantSet())
        throw new IllegalStateException("instant has been set"); 
      allocateFields();
      internalSet(param1Int1, param1Int2);
      return this;
    }
    
    public Builder setFields(int... param1VarArgs) {
      int i = param1VarArgs.length;
      if (i % 2 != 0)
        throw new IllegalArgumentException(); 
      if (isInstantSet())
        throw new IllegalStateException("instant has been set"); 
      if (this.nextStamp + i / 2 < 0)
        throw new IllegalStateException("stamp counter overflow"); 
      allocateFields();
      byte b = 0;
      while (b < i) {
        int j = param1VarArgs[b++];
        if (j < 0 || j >= 17)
          throw new IllegalArgumentException("field is invalid"); 
        internalSet(j, param1VarArgs[b++]);
      } 
      return this;
    }
    
    public Builder setDate(int param1Int1, int param1Int2, int param1Int3) { return setFields(new int[] { 1, param1Int1, 2, param1Int2, 5, param1Int3 }); }
    
    public Builder setTimeOfDay(int param1Int1, int param1Int2, int param1Int3) { return setTimeOfDay(param1Int1, param1Int2, param1Int3, 0); }
    
    public Builder setTimeOfDay(int param1Int1, int param1Int2, int param1Int3, int param1Int4) { return setFields(new int[] { 11, param1Int1, 12, param1Int2, 13, param1Int3, 14, param1Int4 }); }
    
    public Builder setWeekDate(int param1Int1, int param1Int2, int param1Int3) {
      allocateFields();
      internalSet(17, param1Int1);
      internalSet(3, param1Int2);
      internalSet(7, param1Int3);
      return this;
    }
    
    public Builder setTimeZone(TimeZone param1TimeZone) {
      if (param1TimeZone == null)
        throw new NullPointerException(); 
      this.zone = param1TimeZone;
      return this;
    }
    
    public Builder setLenient(boolean param1Boolean) {
      this.lenient = param1Boolean;
      return this;
    }
    
    public Builder setCalendarType(String param1String) {
      if (param1String.equals("gregorian"))
        param1String = "gregory"; 
      if (!Calendar.getAvailableCalendarTypes().contains(param1String) && !param1String.equals("iso8601"))
        throw new IllegalArgumentException("unknown calendar type: " + param1String); 
      if (this.type == null) {
        this.type = param1String;
      } else if (!this.type.equals(param1String)) {
        throw new IllegalStateException("calendar type override");
      } 
      return this;
    }
    
    public Builder setLocale(Locale param1Locale) {
      if (param1Locale == null)
        throw new NullPointerException(); 
      this.locale = param1Locale;
      return this;
    }
    
    public Builder setWeekDefinition(int param1Int1, int param1Int2) {
      if (!isValidWeekParameter(param1Int1) || !isValidWeekParameter(param1Int2))
        throw new IllegalArgumentException(); 
      this.firstDayOfWeek = param1Int1;
      this.minimalDaysInFirstWeek = param1Int2;
      return this;
    }
    
    public Calendar build() {
      GregorianCalendar gregorianCalendar2;
      GregorianCalendar gregorianCalendar1;
      JapaneseImperialCalendar japaneseImperialCalendar;
      if (this.locale == null)
        this.locale = Locale.getDefault(); 
      if (this.zone == null)
        this.zone = TimeZone.getDefault(); 
      if (this.type == null)
        this.type = this.locale.getUnicodeLocaleType("ca"); 
      if (this.type == null)
        if (this.locale.getCountry() == "TH" && this.locale.getLanguage() == "th") {
          this.type = "buddhist";
        } else {
          this.type = "gregory";
        }  
      switch (this.type) {
        case "gregory":
          gregorianCalendar1 = new GregorianCalendar(this.zone, this.locale, true);
          break;
        case "iso8601":
          gregorianCalendar2 = new GregorianCalendar(this.zone, this.locale, true);
          gregorianCalendar2.setGregorianChange(new Date(Float.MIN_VALUE));
          setWeekDefinition(2, 4);
          gregorianCalendar1 = gregorianCalendar2;
          break;
        case "buddhist":
          gregorianCalendar1 = new BuddhistCalendar(this.zone, this.locale);
          gregorianCalendar1.clear();
          break;
        case "japanese":
          japaneseImperialCalendar = new JapaneseImperialCalendar(this.zone, this.locale, true);
          break;
        default:
          throw new IllegalArgumentException("unknown calendar type: " + this.type);
      } 
      japaneseImperialCalendar.setLenient(this.lenient);
      if (this.firstDayOfWeek != 0) {
        japaneseImperialCalendar.setFirstDayOfWeek(this.firstDayOfWeek);
        japaneseImperialCalendar.setMinimalDaysInFirstWeek(this.minimalDaysInFirstWeek);
      } 
      if (isInstantSet()) {
        japaneseImperialCalendar.setTimeInMillis(this.instant);
        japaneseImperialCalendar.complete();
        return japaneseImperialCalendar;
      } 
      if (this.fields != null) {
        boolean bool = (isSet(17) && this.fields[17] > this.fields[1]) ? 1 : 0;
        if (bool && !japaneseImperialCalendar.isWeekDateSupported())
          throw new IllegalArgumentException("week date is unsupported by " + this.type); 
        int i;
        for (i = 2; i < this.nextStamp; i++) {
          for (byte b = 0; b <= this.maxFieldIndex; b++) {
            if (this.fields[b] == i) {
              japaneseImperialCalendar.set(b, this.fields[18 + b]);
              break;
            } 
          } 
        } 
        if (bool) {
          i = isSet(3) ? this.fields[21] : 1;
          int j = isSet(7) ? this.fields[25] : japaneseImperialCalendar.getFirstDayOfWeek();
          japaneseImperialCalendar.setWeekDate(this.fields[35], i, j);
        } 
        japaneseImperialCalendar.complete();
      } 
      return japaneseImperialCalendar;
    }
    
    private void allocateFields() {
      if (this.fields == null) {
        this.fields = new int[36];
        this.nextStamp = 2;
        this.maxFieldIndex = -1;
      } 
    }
    
    private void internalSet(int param1Int1, int param1Int2) {
      this.fields[param1Int1] = this.nextStamp++;
      if (this.nextStamp < 0)
        throw new IllegalStateException("stamp counter overflow"); 
      this.fields[18 + param1Int1] = param1Int2;
      if (param1Int1 > this.maxFieldIndex && param1Int1 < 17)
        this.maxFieldIndex = param1Int1; 
    }
    
    private boolean isInstantSet() { return (this.nextStamp == 1); }
    
    private boolean isSet(int param1Int) { return (this.fields != null && this.fields[param1Int] > 0); }
    
    private boolean isValidWeekParameter(int param1Int) { return (param1Int > 0 && param1Int <= 7); }
  }
  
  private static class CalendarAccessControlContext {
    private static final AccessControlContext INSTANCE;
    
    static  {
      RuntimePermission runtimePermission = new RuntimePermission("accessClassInPackage.sun.util.calendar");
      PermissionCollection permissionCollection = runtimePermission.newPermissionCollection();
      permissionCollection.add(runtimePermission);
      INSTANCE = new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, permissionCollection) });
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\Calendar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */