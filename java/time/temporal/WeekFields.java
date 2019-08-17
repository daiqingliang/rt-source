package java.time.temporal;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.util.locale.provider.CalendarDataUtility;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleResources;

public final class WeekFields implements Serializable {
  private static final ConcurrentMap<String, WeekFields> CACHE = new ConcurrentHashMap(4, 0.75F, 2);
  
  public static final WeekFields ISO;
  
  public static final WeekFields SUNDAY_START = (ISO = new WeekFields(DayOfWeek.MONDAY, 4)).of(DayOfWeek.SUNDAY, 1);
  
  public static final TemporalUnit WEEK_BASED_YEARS = IsoFields.WEEK_BASED_YEARS;
  
  private static final long serialVersionUID = -1177360819670808121L;
  
  private final DayOfWeek firstDayOfWeek;
  
  private final int minimalDays;
  
  private final TemporalField dayOfWeek = ComputedDayOfField.ofDayOfWeekField(this);
  
  private final TemporalField weekOfMonth = ComputedDayOfField.ofWeekOfMonthField(this);
  
  private final TemporalField weekOfYear = ComputedDayOfField.ofWeekOfYearField(this);
  
  private final TemporalField weekOfWeekBasedYear = ComputedDayOfField.ofWeekOfWeekBasedYearField(this);
  
  private final TemporalField weekBasedYear = ComputedDayOfField.ofWeekBasedYearField(this);
  
  public static WeekFields of(Locale paramLocale) {
    Objects.requireNonNull(paramLocale, "locale");
    paramLocale = new Locale(paramLocale.getLanguage(), paramLocale.getCountry());
    int i = CalendarDataUtility.retrieveFirstDayOfWeek(paramLocale);
    DayOfWeek dayOfWeek1 = DayOfWeek.SUNDAY.plus((i - 1));
    int j = CalendarDataUtility.retrieveMinimalDaysInFirstWeek(paramLocale);
    return of(dayOfWeek1, j);
  }
  
  public static WeekFields of(DayOfWeek paramDayOfWeek, int paramInt) {
    String str = paramDayOfWeek.toString() + paramInt;
    WeekFields weekFields = (WeekFields)CACHE.get(str);
    if (weekFields == null) {
      weekFields = new WeekFields(paramDayOfWeek, paramInt);
      CACHE.putIfAbsent(str, weekFields);
      weekFields = (WeekFields)CACHE.get(str);
    } 
    return weekFields;
  }
  
  private WeekFields(DayOfWeek paramDayOfWeek, int paramInt) {
    Objects.requireNonNull(paramDayOfWeek, "firstDayOfWeek");
    if (paramInt < 1 || paramInt > 7)
      throw new IllegalArgumentException("Minimal number of days is invalid"); 
    this.firstDayOfWeek = paramDayOfWeek;
    this.minimalDays = paramInt;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException, InvalidObjectException {
    paramObjectInputStream.defaultReadObject();
    if (this.firstDayOfWeek == null)
      throw new InvalidObjectException("firstDayOfWeek is null"); 
    if (this.minimalDays < 1 || this.minimalDays > 7)
      throw new InvalidObjectException("Minimal number of days is invalid"); 
  }
  
  private Object readResolve() throws InvalidObjectException {
    try {
      return of(this.firstDayOfWeek, this.minimalDays);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new InvalidObjectException("Invalid serialized WeekFields: " + illegalArgumentException.getMessage());
    } 
  }
  
  public DayOfWeek getFirstDayOfWeek() { return this.firstDayOfWeek; }
  
  public int getMinimalDaysInFirstWeek() { return this.minimalDays; }
  
  public TemporalField dayOfWeek() { return this.dayOfWeek; }
  
  public TemporalField weekOfMonth() { return this.weekOfMonth; }
  
  public TemporalField weekOfYear() { return this.weekOfYear; }
  
  public TemporalField weekOfWeekBasedYear() { return this.weekOfWeekBasedYear; }
  
  public TemporalField weekBasedYear() { return this.weekBasedYear; }
  
  public boolean equals(Object paramObject) { return (this == paramObject) ? true : ((paramObject instanceof WeekFields) ? ((hashCode() == paramObject.hashCode())) : false); }
  
  public int hashCode() { return this.firstDayOfWeek.ordinal() * 7 + this.minimalDays; }
  
  public String toString() { return "WeekFields[" + this.firstDayOfWeek + ',' + this.minimalDays + ']'; }
  
  static class ComputedDayOfField implements TemporalField {
    private final String name;
    
    private final WeekFields weekDef;
    
    private final TemporalUnit baseUnit;
    
    private final TemporalUnit rangeUnit;
    
    private final ValueRange range;
    
    private static final ValueRange DAY_OF_WEEK_RANGE;
    
    private static final ValueRange WEEK_OF_MONTH_RANGE;
    
    private static final ValueRange WEEK_OF_YEAR_RANGE;
    
    private static final ValueRange WEEK_OF_WEEK_BASED_YEAR_RANGE = (WEEK_OF_YEAR_RANGE = (WEEK_OF_MONTH_RANGE = (DAY_OF_WEEK_RANGE = ValueRange.of(1L, 7L)).of(0L, 1L, 4L, 6L)).of(0L, 1L, 52L, 54L)).of(1L, 52L, 53L);
    
    static ComputedDayOfField ofDayOfWeekField(WeekFields param1WeekFields) { return new ComputedDayOfField("DayOfWeek", param1WeekFields, ChronoUnit.DAYS, ChronoUnit.WEEKS, DAY_OF_WEEK_RANGE); }
    
    static ComputedDayOfField ofWeekOfMonthField(WeekFields param1WeekFields) { return new ComputedDayOfField("WeekOfMonth", param1WeekFields, ChronoUnit.WEEKS, ChronoUnit.MONTHS, WEEK_OF_MONTH_RANGE); }
    
    static ComputedDayOfField ofWeekOfYearField(WeekFields param1WeekFields) { return new ComputedDayOfField("WeekOfYear", param1WeekFields, ChronoUnit.WEEKS, ChronoUnit.YEARS, WEEK_OF_YEAR_RANGE); }
    
    static ComputedDayOfField ofWeekOfWeekBasedYearField(WeekFields param1WeekFields) { return new ComputedDayOfField("WeekOfWeekBasedYear", param1WeekFields, ChronoUnit.WEEKS, IsoFields.WEEK_BASED_YEARS, WEEK_OF_WEEK_BASED_YEAR_RANGE); }
    
    static ComputedDayOfField ofWeekBasedYearField(WeekFields param1WeekFields) { return new ComputedDayOfField("WeekBasedYear", param1WeekFields, IsoFields.WEEK_BASED_YEARS, ChronoUnit.FOREVER, ChronoField.YEAR.range()); }
    
    private ChronoLocalDate ofWeekBasedYear(Chronology param1Chronology, int param1Int1, int param1Int2, int param1Int3) {
      ChronoLocalDate chronoLocalDate = param1Chronology.date(param1Int1, 1, 1);
      int i = localizedDayOfWeek(chronoLocalDate);
      int j = startOfWeekOffset(1, i);
      int k = chronoLocalDate.lengthOfYear();
      int m = computeWeek(j, k + this.weekDef.getMinimalDaysInFirstWeek());
      param1Int2 = Math.min(param1Int2, m - 1);
      int n = -j + param1Int3 - 1 + (param1Int2 - 1) * 7;
      return chronoLocalDate.plus(n, ChronoUnit.DAYS);
    }
    
    private ComputedDayOfField(String param1String, WeekFields param1WeekFields, TemporalUnit param1TemporalUnit1, TemporalUnit param1TemporalUnit2, ValueRange param1ValueRange) {
      this.name = param1String;
      this.weekDef = param1WeekFields;
      this.baseUnit = param1TemporalUnit1;
      this.rangeUnit = param1TemporalUnit2;
      this.range = param1ValueRange;
    }
    
    public long getFrom(TemporalAccessor param1TemporalAccessor) {
      if (this.rangeUnit == ChronoUnit.WEEKS)
        return localizedDayOfWeek(param1TemporalAccessor); 
      if (this.rangeUnit == ChronoUnit.MONTHS)
        return localizedWeekOfMonth(param1TemporalAccessor); 
      if (this.rangeUnit == ChronoUnit.YEARS)
        return localizedWeekOfYear(param1TemporalAccessor); 
      if (this.rangeUnit == WeekFields.WEEK_BASED_YEARS)
        return localizedWeekOfWeekBasedYear(param1TemporalAccessor); 
      if (this.rangeUnit == ChronoUnit.FOREVER)
        return localizedWeekBasedYear(param1TemporalAccessor); 
      throw new IllegalStateException("unreachable, rangeUnit: " + this.rangeUnit + ", this: " + this);
    }
    
    private int localizedDayOfWeek(TemporalAccessor param1TemporalAccessor) {
      int i = this.weekDef.getFirstDayOfWeek().getValue();
      int j = param1TemporalAccessor.get(ChronoField.DAY_OF_WEEK);
      return Math.floorMod(j - i, 7) + 1;
    }
    
    private int localizedDayOfWeek(int param1Int) {
      int i = this.weekDef.getFirstDayOfWeek().getValue();
      return Math.floorMod(param1Int - i, 7) + 1;
    }
    
    private long localizedWeekOfMonth(TemporalAccessor param1TemporalAccessor) {
      int i = localizedDayOfWeek(param1TemporalAccessor);
      int j = param1TemporalAccessor.get(ChronoField.DAY_OF_MONTH);
      int k = startOfWeekOffset(j, i);
      return computeWeek(k, j);
    }
    
    private long localizedWeekOfYear(TemporalAccessor param1TemporalAccessor) {
      int i = localizedDayOfWeek(param1TemporalAccessor);
      int j = param1TemporalAccessor.get(ChronoField.DAY_OF_YEAR);
      int k = startOfWeekOffset(j, i);
      return computeWeek(k, j);
    }
    
    private int localizedWeekBasedYear(TemporalAccessor param1TemporalAccessor) {
      int i = localizedDayOfWeek(param1TemporalAccessor);
      int j = param1TemporalAccessor.get(ChronoField.YEAR);
      int k = param1TemporalAccessor.get(ChronoField.DAY_OF_YEAR);
      int m = startOfWeekOffset(k, i);
      int n = computeWeek(m, k);
      if (n == 0)
        return j - 1; 
      ValueRange valueRange = param1TemporalAccessor.range(ChronoField.DAY_OF_YEAR);
      int i1 = (int)valueRange.getMaximum();
      int i2 = computeWeek(m, i1 + this.weekDef.getMinimalDaysInFirstWeek());
      return (n >= i2) ? (j + 1) : j;
    }
    
    private int localizedWeekOfWeekBasedYear(TemporalAccessor param1TemporalAccessor) {
      int i = localizedDayOfWeek(param1TemporalAccessor);
      int j = param1TemporalAccessor.get(ChronoField.DAY_OF_YEAR);
      int k = startOfWeekOffset(j, i);
      int m = computeWeek(k, j);
      if (m == 0) {
        ChronoLocalDate chronoLocalDate = Chronology.from(param1TemporalAccessor).date(param1TemporalAccessor);
        chronoLocalDate = chronoLocalDate.minus(j, ChronoUnit.DAYS);
        return localizedWeekOfWeekBasedYear(chronoLocalDate);
      } 
      if (m > 50) {
        ValueRange valueRange = param1TemporalAccessor.range(ChronoField.DAY_OF_YEAR);
        int n = (int)valueRange.getMaximum();
        int i1 = computeWeek(k, n + this.weekDef.getMinimalDaysInFirstWeek());
        if (m >= i1)
          m = m - i1 + 1; 
      } 
      return m;
    }
    
    private int startOfWeekOffset(int param1Int1, int param1Int2) {
      int i = Math.floorMod(param1Int1 - param1Int2, 7);
      int j = -i;
      if (i + 1 > this.weekDef.getMinimalDaysInFirstWeek())
        j = 7 - i; 
      return j;
    }
    
    private int computeWeek(int param1Int1, int param1Int2) { return (7 + param1Int1 + param1Int2 - 1) / 7; }
    
    public <R extends Temporal> R adjustInto(R param1R, long param1Long) {
      int i = this.range.checkValidIntValue(param1Long, this);
      int j = param1R.get(this);
      if (i == j)
        return param1R; 
      if (this.rangeUnit == ChronoUnit.FOREVER) {
        int k = param1R.get(this.weekDef.dayOfWeek);
        int m = param1R.get(this.weekDef.weekOfWeekBasedYear);
        return (R)ofWeekBasedYear(Chronology.from(param1R), (int)param1Long, m, k);
      } 
      return (R)param1R.plus((i - j), this.baseUnit);
    }
    
    public ChronoLocalDate resolve(Map<TemporalField, Long> param1Map, TemporalAccessor param1TemporalAccessor, ResolverStyle param1ResolverStyle) {
      long l = ((Long)param1Map.get(this)).longValue();
      int i = Math.toIntExact(l);
      if (this.rangeUnit == ChronoUnit.WEEKS) {
        int m = this.range.checkValidIntValue(l, this);
        int n = this.weekDef.getFirstDayOfWeek().getValue();
        long l1 = (Math.floorMod(n - 1 + m - 1, 7) + 1);
        param1Map.remove(this);
        param1Map.put(ChronoField.DAY_OF_WEEK, Long.valueOf(l1));
        return null;
      } 
      if (!param1Map.containsKey(ChronoField.DAY_OF_WEEK))
        return null; 
      int j = ChronoField.DAY_OF_WEEK.checkValidIntValue(((Long)param1Map.get(ChronoField.DAY_OF_WEEK)).longValue());
      int k = localizedDayOfWeek(j);
      Chronology chronology = Chronology.from(param1TemporalAccessor);
      if (param1Map.containsKey(ChronoField.YEAR)) {
        int m = ChronoField.YEAR.checkValidIntValue(((Long)param1Map.get(ChronoField.YEAR)).longValue());
        if (this.rangeUnit == ChronoUnit.MONTHS && param1Map.containsKey(ChronoField.MONTH_OF_YEAR)) {
          long l1 = ((Long)param1Map.get(ChronoField.MONTH_OF_YEAR)).longValue();
          return resolveWoM(param1Map, chronology, m, l1, i, k, param1ResolverStyle);
        } 
        if (this.rangeUnit == ChronoUnit.YEARS)
          return resolveWoY(param1Map, chronology, m, i, k, param1ResolverStyle); 
      } else if ((this.rangeUnit == WeekFields.WEEK_BASED_YEARS || this.rangeUnit == ChronoUnit.FOREVER) && param1Map.containsKey(this.weekDef.weekBasedYear) && param1Map.containsKey(this.weekDef.weekOfWeekBasedYear)) {
        return resolveWBY(param1Map, chronology, k, param1ResolverStyle);
      } 
      return null;
    }
    
    private ChronoLocalDate resolveWoM(Map<TemporalField, Long> param1Map, Chronology param1Chronology, int param1Int1, long param1Long1, long param1Long2, int param1Int2, ResolverStyle param1ResolverStyle) {
      ChronoLocalDate chronoLocalDate;
      if (param1ResolverStyle == ResolverStyle.LENIENT) {
        chronoLocalDate = param1Chronology.date(param1Int1, 1, 1).plus(Math.subtractExact(param1Long1, 1L), ChronoUnit.MONTHS);
        long l = Math.subtractExact(param1Long2, localizedWeekOfMonth(chronoLocalDate));
        int i = param1Int2 - localizedDayOfWeek(chronoLocalDate);
        chronoLocalDate = chronoLocalDate.plus(Math.addExact(Math.multiplyExact(l, 7L), i), ChronoUnit.DAYS);
      } else {
        int i = ChronoField.MONTH_OF_YEAR.checkValidIntValue(param1Long1);
        chronoLocalDate = param1Chronology.date(param1Int1, i, 1);
        int j = this.range.checkValidIntValue(param1Long2, this);
        int k = (int)(j - localizedWeekOfMonth(chronoLocalDate));
        int m = param1Int2 - localizedDayOfWeek(chronoLocalDate);
        chronoLocalDate = chronoLocalDate.plus((k * 7 + m), ChronoUnit.DAYS);
        if (param1ResolverStyle == ResolverStyle.STRICT && chronoLocalDate.getLong(ChronoField.MONTH_OF_YEAR) != param1Long1)
          throw new DateTimeException("Strict mode rejected resolved date as it is in a different month"); 
      } 
      param1Map.remove(this);
      param1Map.remove(ChronoField.YEAR);
      param1Map.remove(ChronoField.MONTH_OF_YEAR);
      param1Map.remove(ChronoField.DAY_OF_WEEK);
      return chronoLocalDate;
    }
    
    private ChronoLocalDate resolveWoY(Map<TemporalField, Long> param1Map, Chronology param1Chronology, int param1Int1, long param1Long, int param1Int2, ResolverStyle param1ResolverStyle) {
      ChronoLocalDate chronoLocalDate = param1Chronology.date(param1Int1, 1, 1);
      if (param1ResolverStyle == ResolverStyle.LENIENT) {
        long l = Math.subtractExact(param1Long, localizedWeekOfYear(chronoLocalDate));
        int i = param1Int2 - localizedDayOfWeek(chronoLocalDate);
        chronoLocalDate = chronoLocalDate.plus(Math.addExact(Math.multiplyExact(l, 7L), i), ChronoUnit.DAYS);
      } else {
        int i = this.range.checkValidIntValue(param1Long, this);
        int j = (int)(i - localizedWeekOfYear(chronoLocalDate));
        int k = param1Int2 - localizedDayOfWeek(chronoLocalDate);
        chronoLocalDate = chronoLocalDate.plus((j * 7 + k), ChronoUnit.DAYS);
        if (param1ResolverStyle == ResolverStyle.STRICT && chronoLocalDate.getLong(ChronoField.YEAR) != param1Int1)
          throw new DateTimeException("Strict mode rejected resolved date as it is in a different year"); 
      } 
      param1Map.remove(this);
      param1Map.remove(ChronoField.YEAR);
      param1Map.remove(ChronoField.DAY_OF_WEEK);
      return chronoLocalDate;
    }
    
    private ChronoLocalDate resolveWBY(Map<TemporalField, Long> param1Map, Chronology param1Chronology, int param1Int, ResolverStyle param1ResolverStyle) {
      ChronoLocalDate chronoLocalDate;
      int i = this.weekDef.weekBasedYear.range().checkValidIntValue(((Long)param1Map.get(this.weekDef.weekBasedYear)).longValue(), this.weekDef.weekBasedYear);
      if (param1ResolverStyle == ResolverStyle.LENIENT) {
        chronoLocalDate = ofWeekBasedYear(param1Chronology, i, 1, param1Int);
        long l1 = ((Long)param1Map.get(this.weekDef.weekOfWeekBasedYear)).longValue();
        long l2 = Math.subtractExact(l1, 1L);
        chronoLocalDate = chronoLocalDate.plus(l2, ChronoUnit.WEEKS);
      } else {
        int j = this.weekDef.weekOfWeekBasedYear.range().checkValidIntValue(((Long)param1Map.get(this.weekDef.weekOfWeekBasedYear)).longValue(), this.weekDef.weekOfWeekBasedYear);
        chronoLocalDate = ofWeekBasedYear(param1Chronology, i, j, param1Int);
        if (param1ResolverStyle == ResolverStyle.STRICT && localizedWeekBasedYear(chronoLocalDate) != i)
          throw new DateTimeException("Strict mode rejected resolved date as it is in a different week-based-year"); 
      } 
      param1Map.remove(this);
      param1Map.remove(this.weekDef.weekBasedYear);
      param1Map.remove(this.weekDef.weekOfWeekBasedYear);
      param1Map.remove(ChronoField.DAY_OF_WEEK);
      return chronoLocalDate;
    }
    
    public String getDisplayName(Locale param1Locale) {
      Objects.requireNonNull(param1Locale, "locale");
      if (this.rangeUnit == ChronoUnit.YEARS) {
        LocaleResources localeResources = LocaleProviderAdapter.getResourceBundleBased().getLocaleResources(param1Locale);
        ResourceBundle resourceBundle = localeResources.getJavaTimeFormatData();
        return resourceBundle.containsKey("field.week") ? resourceBundle.getString("field.week") : this.name;
      } 
      return this.name;
    }
    
    public TemporalUnit getBaseUnit() { return this.baseUnit; }
    
    public TemporalUnit getRangeUnit() { return this.rangeUnit; }
    
    public boolean isDateBased() { return true; }
    
    public boolean isTimeBased() { return false; }
    
    public ValueRange range() { return this.range; }
    
    public boolean isSupportedBy(TemporalAccessor param1TemporalAccessor) {
      if (param1TemporalAccessor.isSupported(ChronoField.DAY_OF_WEEK)) {
        if (this.rangeUnit == ChronoUnit.WEEKS)
          return true; 
        if (this.rangeUnit == ChronoUnit.MONTHS)
          return param1TemporalAccessor.isSupported(ChronoField.DAY_OF_MONTH); 
        if (this.rangeUnit == ChronoUnit.YEARS)
          return param1TemporalAccessor.isSupported(ChronoField.DAY_OF_YEAR); 
        if (this.rangeUnit == WeekFields.WEEK_BASED_YEARS)
          return param1TemporalAccessor.isSupported(ChronoField.DAY_OF_YEAR); 
        if (this.rangeUnit == ChronoUnit.FOREVER)
          return param1TemporalAccessor.isSupported(ChronoField.YEAR); 
      } 
      return false;
    }
    
    public ValueRange rangeRefinedBy(TemporalAccessor param1TemporalAccessor) {
      if (this.rangeUnit == ChronoUnit.WEEKS)
        return this.range; 
      if (this.rangeUnit == ChronoUnit.MONTHS)
        return rangeByWeek(param1TemporalAccessor, ChronoField.DAY_OF_MONTH); 
      if (this.rangeUnit == ChronoUnit.YEARS)
        return rangeByWeek(param1TemporalAccessor, ChronoField.DAY_OF_YEAR); 
      if (this.rangeUnit == WeekFields.WEEK_BASED_YEARS)
        return rangeWeekOfWeekBasedYear(param1TemporalAccessor); 
      if (this.rangeUnit == ChronoUnit.FOREVER)
        return ChronoField.YEAR.range(); 
      throw new IllegalStateException("unreachable, rangeUnit: " + this.rangeUnit + ", this: " + this);
    }
    
    private ValueRange rangeByWeek(TemporalAccessor param1TemporalAccessor, TemporalField param1TemporalField) {
      int i = localizedDayOfWeek(param1TemporalAccessor);
      int j = startOfWeekOffset(param1TemporalAccessor.get(param1TemporalField), i);
      ValueRange valueRange;
      return (valueRange = param1TemporalAccessor.range(param1TemporalField)).of(computeWeek(j, (int)valueRange.getMinimum()), computeWeek(j, (int)valueRange.getMaximum()));
    }
    
    private ValueRange rangeWeekOfWeekBasedYear(TemporalAccessor param1TemporalAccessor) {
      if (!param1TemporalAccessor.isSupported(ChronoField.DAY_OF_YEAR))
        return WEEK_OF_YEAR_RANGE; 
      int i = localizedDayOfWeek(param1TemporalAccessor);
      int j = param1TemporalAccessor.get(ChronoField.DAY_OF_YEAR);
      int k = startOfWeekOffset(j, i);
      int m = computeWeek(k, j);
      if (m == 0) {
        ChronoLocalDate chronoLocalDate = Chronology.from(param1TemporalAccessor).date(param1TemporalAccessor);
        chronoLocalDate = chronoLocalDate.minus((j + 7), ChronoUnit.DAYS);
        return rangeWeekOfWeekBasedYear(chronoLocalDate);
      } 
      ValueRange valueRange = param1TemporalAccessor.range(ChronoField.DAY_OF_YEAR);
      int n = (int)valueRange.getMaximum();
      int i1 = computeWeek(k, n + this.weekDef.getMinimalDaysInFirstWeek());
      if (m >= i1) {
        ChronoLocalDate chronoLocalDate = Chronology.from(param1TemporalAccessor).date(param1TemporalAccessor);
        chronoLocalDate = chronoLocalDate.plus((n - j + 1 + 7), ChronoUnit.DAYS);
        return rangeWeekOfWeekBasedYear(chronoLocalDate);
      } 
      return ValueRange.of(1L, (i1 - 1));
    }
    
    public String toString() { return this.name + "[" + this.weekDef.toString() + "]"; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\temporal\WeekFields.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */