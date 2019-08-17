package java.time.chrono;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.lang.invoke.SerializedLambda;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import sun.util.logging.PlatformLogger;

public abstract class AbstractChronology implements Chronology {
  static final Comparator<ChronoLocalDate> DATE_ORDER = (Comparator)((paramChronoLocalDate1, paramChronoLocalDate2) -> Long.compare(paramChronoLocalDate1.toEpochDay(), paramChronoLocalDate2.toEpochDay()));
  
  static final Comparator<ChronoLocalDateTime<? extends ChronoLocalDate>> DATE_TIME_ORDER = (Comparator)((paramChronoLocalDateTime1, paramChronoLocalDateTime2) -> {
      int i = Long.compare(paramChronoLocalDateTime1.toLocalDate().toEpochDay(), paramChronoLocalDateTime2.toLocalDate().toEpochDay());
      if (i == 0)
        i = Long.compare(paramChronoLocalDateTime1.toLocalTime().toNanoOfDay(), paramChronoLocalDateTime2.toLocalTime().toNanoOfDay()); 
      return i;
    });
  
  static final Comparator<ChronoZonedDateTime<?>> INSTANT_ORDER = (Comparator)((paramChronoZonedDateTime1, paramChronoZonedDateTime2) -> {
      int i = Long.compare(paramChronoZonedDateTime1.toEpochSecond(), paramChronoZonedDateTime2.toEpochSecond());
      if (i == 0)
        i = Long.compare(paramChronoZonedDateTime1.toLocalTime().getNano(), paramChronoZonedDateTime2.toLocalTime().getNano()); 
      return i;
    });
  
  private static final ConcurrentHashMap<String, Chronology> CHRONOS_BY_ID = new ConcurrentHashMap();
  
  private static final ConcurrentHashMap<String, Chronology> CHRONOS_BY_TYPE = new ConcurrentHashMap();
  
  static Chronology registerChrono(Chronology paramChronology) { return registerChrono(paramChronology, paramChronology.getId()); }
  
  static Chronology registerChrono(Chronology paramChronology, String paramString) {
    Chronology chronology = (Chronology)CHRONOS_BY_ID.putIfAbsent(paramString, paramChronology);
    if (chronology == null) {
      String str = paramChronology.getCalendarType();
      if (str != null)
        CHRONOS_BY_TYPE.putIfAbsent(str, paramChronology); 
    } 
    return chronology;
  }
  
  private static boolean initCache() {
    if (CHRONOS_BY_ID.get("ISO") == null) {
      registerChrono(HijrahChronology.INSTANCE);
      registerChrono(JapaneseChronology.INSTANCE);
      registerChrono(MinguoChronology.INSTANCE);
      registerChrono(ThaiBuddhistChronology.INSTANCE);
      ServiceLoader serviceLoader = ServiceLoader.load(AbstractChronology.class, null);
      for (AbstractChronology abstractChronology : serviceLoader) {
        String str = abstractChronology.getId();
        if (str.equals("ISO") || registerChrono(abstractChronology) != null) {
          PlatformLogger platformLogger = PlatformLogger.getLogger("java.time.chrono");
          platformLogger.warning("Ignoring duplicate Chronology, from ServiceLoader configuration " + str);
        } 
      } 
      registerChrono(IsoChronology.INSTANCE);
      return true;
    } 
    return false;
  }
  
  static Chronology ofLocale(Locale paramLocale) {
    Objects.requireNonNull(paramLocale, "locale");
    String str = paramLocale.getUnicodeLocaleType("ca");
    if (str == null || "iso".equals(str) || "iso8601".equals(str))
      return IsoChronology.INSTANCE; 
    do {
      Chronology chronology = (Chronology)CHRONOS_BY_TYPE.get(str);
      if (chronology != null)
        return chronology; 
    } while (initCache());
    ServiceLoader serviceLoader = ServiceLoader.load(Chronology.class);
    for (Chronology chronology : serviceLoader) {
      if (str.equals(chronology.getCalendarType()))
        return chronology; 
    } 
    throw new DateTimeException("Unknown calendar system: " + str);
  }
  
  static Chronology of(String paramString) {
    Objects.requireNonNull(paramString, "id");
    do {
      Chronology chronology = of0(paramString);
      if (chronology != null)
        return chronology; 
    } while (initCache());
    ServiceLoader serviceLoader = ServiceLoader.load(Chronology.class);
    for (Chronology chronology : serviceLoader) {
      if (paramString.equals(chronology.getId()) || paramString.equals(chronology.getCalendarType()))
        return chronology; 
    } 
    throw new DateTimeException("Unknown chronology: " + paramString);
  }
  
  private static Chronology of0(String paramString) {
    Chronology chronology = (Chronology)CHRONOS_BY_ID.get(paramString);
    if (chronology == null)
      chronology = (Chronology)CHRONOS_BY_TYPE.get(paramString); 
    return chronology;
  }
  
  static Set<Chronology> getAvailableChronologies() {
    initCache();
    HashSet hashSet = new HashSet(CHRONOS_BY_ID.values());
    ServiceLoader serviceLoader = ServiceLoader.load(Chronology.class);
    for (Chronology chronology : serviceLoader)
      hashSet.add(chronology); 
    return hashSet;
  }
  
  public ChronoLocalDate resolveDate(Map<TemporalField, Long> paramMap, ResolverStyle paramResolverStyle) {
    if (paramMap.containsKey(ChronoField.EPOCH_DAY))
      return dateEpochDay(((Long)paramMap.remove(ChronoField.EPOCH_DAY)).longValue()); 
    resolveProlepticMonth(paramMap, paramResolverStyle);
    ChronoLocalDate chronoLocalDate = resolveYearOfEra(paramMap, paramResolverStyle);
    if (chronoLocalDate != null)
      return chronoLocalDate; 
    if (paramMap.containsKey(ChronoField.YEAR)) {
      if (paramMap.containsKey(ChronoField.MONTH_OF_YEAR)) {
        if (paramMap.containsKey(ChronoField.DAY_OF_MONTH))
          return resolveYMD(paramMap, paramResolverStyle); 
        if (paramMap.containsKey(ChronoField.ALIGNED_WEEK_OF_MONTH)) {
          if (paramMap.containsKey(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH))
            return resolveYMAA(paramMap, paramResolverStyle); 
          if (paramMap.containsKey(ChronoField.DAY_OF_WEEK))
            return resolveYMAD(paramMap, paramResolverStyle); 
        } 
      } 
      if (paramMap.containsKey(ChronoField.DAY_OF_YEAR))
        return resolveYD(paramMap, paramResolverStyle); 
      if (paramMap.containsKey(ChronoField.ALIGNED_WEEK_OF_YEAR)) {
        if (paramMap.containsKey(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR))
          return resolveYAA(paramMap, paramResolverStyle); 
        if (paramMap.containsKey(ChronoField.DAY_OF_WEEK))
          return resolveYAD(paramMap, paramResolverStyle); 
      } 
    } 
    return null;
  }
  
  void resolveProlepticMonth(Map<TemporalField, Long> paramMap, ResolverStyle paramResolverStyle) {
    Long long = (Long)paramMap.remove(ChronoField.PROLEPTIC_MONTH);
    if (long != null) {
      if (paramResolverStyle != ResolverStyle.LENIENT)
        ChronoField.PROLEPTIC_MONTH.checkValidValue(long.longValue()); 
      ChronoLocalDate chronoLocalDate = dateNow().with(ChronoField.DAY_OF_MONTH, 1L).with(ChronoField.PROLEPTIC_MONTH, long.longValue());
      addFieldValue(paramMap, ChronoField.MONTH_OF_YEAR, chronoLocalDate.get(ChronoField.MONTH_OF_YEAR));
      addFieldValue(paramMap, ChronoField.YEAR, chronoLocalDate.get(ChronoField.YEAR));
    } 
  }
  
  ChronoLocalDate resolveYearOfEra(Map<TemporalField, Long> paramMap, ResolverStyle paramResolverStyle) {
    Long long = (Long)paramMap.remove(ChronoField.YEAR_OF_ERA);
    if (long != null) {
      int i;
      Long long1 = (Long)paramMap.remove(ChronoField.ERA);
      if (paramResolverStyle != ResolverStyle.LENIENT) {
        i = range(ChronoField.YEAR_OF_ERA).checkValidIntValue(long.longValue(), ChronoField.YEAR_OF_ERA);
      } else {
        i = Math.toIntExact(long.longValue());
      } 
      if (long1 != null) {
        Era era = eraOf(range(ChronoField.ERA).checkValidIntValue(long1.longValue(), ChronoField.ERA));
        addFieldValue(paramMap, ChronoField.YEAR, prolepticYear(era, i));
      } else if (paramMap.containsKey(ChronoField.YEAR)) {
        int j = range(ChronoField.YEAR).checkValidIntValue(((Long)paramMap.get(ChronoField.YEAR)).longValue(), ChronoField.YEAR);
        ChronoLocalDate chronoLocalDate = dateYearDay(j, 1);
        addFieldValue(paramMap, ChronoField.YEAR, prolepticYear(chronoLocalDate.getEra(), i));
      } else if (paramResolverStyle == ResolverStyle.STRICT) {
        paramMap.put(ChronoField.YEAR_OF_ERA, long);
      } else {
        List list = eras();
        if (list.isEmpty()) {
          addFieldValue(paramMap, ChronoField.YEAR, i);
        } else {
          Era era = (Era)list.get(list.size() - 1);
          addFieldValue(paramMap, ChronoField.YEAR, prolepticYear(era, i));
        } 
      } 
    } else if (paramMap.containsKey(ChronoField.ERA)) {
      range(ChronoField.ERA).checkValidValue(((Long)paramMap.get(ChronoField.ERA)).longValue(), ChronoField.ERA);
    } 
    return null;
  }
  
  ChronoLocalDate resolveYMD(Map<TemporalField, Long> paramMap, ResolverStyle paramResolverStyle) {
    int i = range(ChronoField.YEAR).checkValidIntValue(((Long)paramMap.remove(ChronoField.YEAR)).longValue(), ChronoField.YEAR);
    if (paramResolverStyle == ResolverStyle.LENIENT) {
      long l1 = Math.subtractExact(((Long)paramMap.remove(ChronoField.MONTH_OF_YEAR)).longValue(), 1L);
      long l2 = Math.subtractExact(((Long)paramMap.remove(ChronoField.DAY_OF_MONTH)).longValue(), 1L);
      return date(i, 1, 1).plus(l1, ChronoUnit.MONTHS).plus(l2, ChronoUnit.DAYS);
    } 
    int j = range(ChronoField.MONTH_OF_YEAR).checkValidIntValue(((Long)paramMap.remove(ChronoField.MONTH_OF_YEAR)).longValue(), ChronoField.MONTH_OF_YEAR);
    ValueRange valueRange = range(ChronoField.DAY_OF_MONTH);
    int k = valueRange.checkValidIntValue(((Long)paramMap.remove(ChronoField.DAY_OF_MONTH)).longValue(), ChronoField.DAY_OF_MONTH);
    if (paramResolverStyle == ResolverStyle.SMART)
      try {
        return date(i, j, k);
      } catch (DateTimeException dateTimeException) {
        return date(i, j, 1).with(TemporalAdjusters.lastDayOfMonth());
      }  
    return date(i, j, k);
  }
  
  ChronoLocalDate resolveYD(Map<TemporalField, Long> paramMap, ResolverStyle paramResolverStyle) {
    int i = range(ChronoField.YEAR).checkValidIntValue(((Long)paramMap.remove(ChronoField.YEAR)).longValue(), ChronoField.YEAR);
    if (paramResolverStyle == ResolverStyle.LENIENT) {
      long l = Math.subtractExact(((Long)paramMap.remove(ChronoField.DAY_OF_YEAR)).longValue(), 1L);
      return dateYearDay(i, 1).plus(l, ChronoUnit.DAYS);
    } 
    int j = range(ChronoField.DAY_OF_YEAR).checkValidIntValue(((Long)paramMap.remove(ChronoField.DAY_OF_YEAR)).longValue(), ChronoField.DAY_OF_YEAR);
    return dateYearDay(i, j);
  }
  
  ChronoLocalDate resolveYMAA(Map<TemporalField, Long> paramMap, ResolverStyle paramResolverStyle) {
    int i = range(ChronoField.YEAR).checkValidIntValue(((Long)paramMap.remove(ChronoField.YEAR)).longValue(), ChronoField.YEAR);
    if (paramResolverStyle == ResolverStyle.LENIENT) {
      long l1 = Math.subtractExact(((Long)paramMap.remove(ChronoField.MONTH_OF_YEAR)).longValue(), 1L);
      long l2 = Math.subtractExact(((Long)paramMap.remove(ChronoField.ALIGNED_WEEK_OF_MONTH)).longValue(), 1L);
      long l3 = Math.subtractExact(((Long)paramMap.remove(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH)).longValue(), 1L);
      return date(i, 1, 1).plus(l1, ChronoUnit.MONTHS).plus(l2, ChronoUnit.WEEKS).plus(l3, ChronoUnit.DAYS);
    } 
    int j = range(ChronoField.MONTH_OF_YEAR).checkValidIntValue(((Long)paramMap.remove(ChronoField.MONTH_OF_YEAR)).longValue(), ChronoField.MONTH_OF_YEAR);
    int k = range(ChronoField.ALIGNED_WEEK_OF_MONTH).checkValidIntValue(((Long)paramMap.remove(ChronoField.ALIGNED_WEEK_OF_MONTH)).longValue(), ChronoField.ALIGNED_WEEK_OF_MONTH);
    int m = range(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH).checkValidIntValue(((Long)paramMap.remove(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH)).longValue(), ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH);
    ChronoLocalDate chronoLocalDate = date(i, j, 1).plus(((k - 1) * 7 + m - 1), ChronoUnit.DAYS);
    if (paramResolverStyle == ResolverStyle.STRICT && chronoLocalDate.get(ChronoField.MONTH_OF_YEAR) != j)
      throw new DateTimeException("Strict mode rejected resolved date as it is in a different month"); 
    return chronoLocalDate;
  }
  
  ChronoLocalDate resolveYMAD(Map<TemporalField, Long> paramMap, ResolverStyle paramResolverStyle) {
    int i = range(ChronoField.YEAR).checkValidIntValue(((Long)paramMap.remove(ChronoField.YEAR)).longValue(), ChronoField.YEAR);
    if (paramResolverStyle == ResolverStyle.LENIENT) {
      long l1 = Math.subtractExact(((Long)paramMap.remove(ChronoField.MONTH_OF_YEAR)).longValue(), 1L);
      long l2 = Math.subtractExact(((Long)paramMap.remove(ChronoField.ALIGNED_WEEK_OF_MONTH)).longValue(), 1L);
      long l3 = Math.subtractExact(((Long)paramMap.remove(ChronoField.DAY_OF_WEEK)).longValue(), 1L);
      return resolveAligned(date(i, 1, 1), l1, l2, l3);
    } 
    int j = range(ChronoField.MONTH_OF_YEAR).checkValidIntValue(((Long)paramMap.remove(ChronoField.MONTH_OF_YEAR)).longValue(), ChronoField.MONTH_OF_YEAR);
    int k = range(ChronoField.ALIGNED_WEEK_OF_MONTH).checkValidIntValue(((Long)paramMap.remove(ChronoField.ALIGNED_WEEK_OF_MONTH)).longValue(), ChronoField.ALIGNED_WEEK_OF_MONTH);
    int m = range(ChronoField.DAY_OF_WEEK).checkValidIntValue(((Long)paramMap.remove(ChronoField.DAY_OF_WEEK)).longValue(), ChronoField.DAY_OF_WEEK);
    ChronoLocalDate chronoLocalDate = date(i, j, 1).plus(((k - 1) * 7), ChronoUnit.DAYS).with(TemporalAdjusters.nextOrSame(DayOfWeek.of(m)));
    if (paramResolverStyle == ResolverStyle.STRICT && chronoLocalDate.get(ChronoField.MONTH_OF_YEAR) != j)
      throw new DateTimeException("Strict mode rejected resolved date as it is in a different month"); 
    return chronoLocalDate;
  }
  
  ChronoLocalDate resolveYAA(Map<TemporalField, Long> paramMap, ResolverStyle paramResolverStyle) {
    int i = range(ChronoField.YEAR).checkValidIntValue(((Long)paramMap.remove(ChronoField.YEAR)).longValue(), ChronoField.YEAR);
    if (paramResolverStyle == ResolverStyle.LENIENT) {
      long l1 = Math.subtractExact(((Long)paramMap.remove(ChronoField.ALIGNED_WEEK_OF_YEAR)).longValue(), 1L);
      long l2 = Math.subtractExact(((Long)paramMap.remove(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR)).longValue(), 1L);
      return dateYearDay(i, 1).plus(l1, ChronoUnit.WEEKS).plus(l2, ChronoUnit.DAYS);
    } 
    int j = range(ChronoField.ALIGNED_WEEK_OF_YEAR).checkValidIntValue(((Long)paramMap.remove(ChronoField.ALIGNED_WEEK_OF_YEAR)).longValue(), ChronoField.ALIGNED_WEEK_OF_YEAR);
    int k = range(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR).checkValidIntValue(((Long)paramMap.remove(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR)).longValue(), ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR);
    ChronoLocalDate chronoLocalDate = dateYearDay(i, 1).plus(((j - 1) * 7 + k - 1), ChronoUnit.DAYS);
    if (paramResolverStyle == ResolverStyle.STRICT && chronoLocalDate.get(ChronoField.YEAR) != i)
      throw new DateTimeException("Strict mode rejected resolved date as it is in a different year"); 
    return chronoLocalDate;
  }
  
  ChronoLocalDate resolveYAD(Map<TemporalField, Long> paramMap, ResolverStyle paramResolverStyle) {
    int i = range(ChronoField.YEAR).checkValidIntValue(((Long)paramMap.remove(ChronoField.YEAR)).longValue(), ChronoField.YEAR);
    if (paramResolverStyle == ResolverStyle.LENIENT) {
      long l1 = Math.subtractExact(((Long)paramMap.remove(ChronoField.ALIGNED_WEEK_OF_YEAR)).longValue(), 1L);
      long l2 = Math.subtractExact(((Long)paramMap.remove(ChronoField.DAY_OF_WEEK)).longValue(), 1L);
      return resolveAligned(dateYearDay(i, 1), 0L, l1, l2);
    } 
    int j = range(ChronoField.ALIGNED_WEEK_OF_YEAR).checkValidIntValue(((Long)paramMap.remove(ChronoField.ALIGNED_WEEK_OF_YEAR)).longValue(), ChronoField.ALIGNED_WEEK_OF_YEAR);
    int k = range(ChronoField.DAY_OF_WEEK).checkValidIntValue(((Long)paramMap.remove(ChronoField.DAY_OF_WEEK)).longValue(), ChronoField.DAY_OF_WEEK);
    ChronoLocalDate chronoLocalDate = dateYearDay(i, 1).plus(((j - 1) * 7), ChronoUnit.DAYS).with(TemporalAdjusters.nextOrSame(DayOfWeek.of(k)));
    if (paramResolverStyle == ResolverStyle.STRICT && chronoLocalDate.get(ChronoField.YEAR) != i)
      throw new DateTimeException("Strict mode rejected resolved date as it is in a different year"); 
    return chronoLocalDate;
  }
  
  ChronoLocalDate resolveAligned(ChronoLocalDate paramChronoLocalDate, long paramLong1, long paramLong2, long paramLong3) {
    ChronoLocalDate chronoLocalDate = paramChronoLocalDate.plus(paramLong1, ChronoUnit.MONTHS).plus(paramLong2, ChronoUnit.WEEKS);
    if (paramLong3 > 7L) {
      chronoLocalDate = chronoLocalDate.plus((paramLong3 - 1L) / 7L, ChronoUnit.WEEKS);
      paramLong3 = (paramLong3 - 1L) % 7L + 1L;
    } else if (paramLong3 < 1L) {
      chronoLocalDate = chronoLocalDate.plus(Math.subtractExact(paramLong3, 7L) / 7L, ChronoUnit.WEEKS);
      paramLong3 = (paramLong3 + 6L) % 7L + 1L;
    } 
    return chronoLocalDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.of((int)paramLong3)));
  }
  
  void addFieldValue(Map<TemporalField, Long> paramMap, ChronoField paramChronoField, long paramLong) {
    Long long = (Long)paramMap.get(paramChronoField);
    if (long != null && long.longValue() != paramLong)
      throw new DateTimeException("Conflict found: " + paramChronoField + " " + long + " differs from " + paramChronoField + " " + paramLong); 
    paramMap.put(paramChronoField, Long.valueOf(paramLong));
  }
  
  public int compareTo(Chronology paramChronology) { return getId().compareTo(paramChronology.getId()); }
  
  public boolean equals(Object paramObject) { return (this == paramObject) ? true : ((paramObject instanceof AbstractChronology) ? ((compareTo((AbstractChronology)paramObject) == 0)) : false); }
  
  public int hashCode() { return getClass().hashCode() ^ getId().hashCode(); }
  
  public String toString() { return getId(); }
  
  Object writeReplace() { return new Ser((byte)1, this); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ObjectStreamException { throw new InvalidObjectException("Deserialization via serialization delegate"); }
  
  void writeExternal(DataOutput paramDataOutput) throws IOException { paramDataOutput.writeUTF(getId()); }
  
  static Chronology readExternal(DataInput paramDataInput) throws IOException {
    String str = paramDataInput.readUTF();
    return Chronology.of(str);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\chrono\AbstractChronology.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */