package java.time.chrono;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import sun.util.calendar.CalendarSystem;
import sun.util.calendar.LocalGregorianCalendar;

public final class JapaneseChronology extends AbstractChronology implements Serializable {
  static final LocalGregorianCalendar JCAL = (LocalGregorianCalendar)CalendarSystem.forName("japanese");
  
  static final Locale LOCALE = Locale.forLanguageTag("ja-JP-u-ca-japanese");
  
  public static final JapaneseChronology INSTANCE = new JapaneseChronology();
  
  private static final long serialVersionUID = 459996390165777884L;
  
  public String getId() { return "Japanese"; }
  
  public String getCalendarType() { return "japanese"; }
  
  public JapaneseDate date(Era paramEra, int paramInt1, int paramInt2, int paramInt3) {
    if (!(paramEra instanceof JapaneseEra))
      throw new ClassCastException("Era must be JapaneseEra"); 
    return JapaneseDate.of((JapaneseEra)paramEra, paramInt1, paramInt2, paramInt3);
  }
  
  public JapaneseDate date(int paramInt1, int paramInt2, int paramInt3) { return new JapaneseDate(LocalDate.of(paramInt1, paramInt2, paramInt3)); }
  
  public JapaneseDate dateYearDay(Era paramEra, int paramInt1, int paramInt2) { return JapaneseDate.ofYearDay((JapaneseEra)paramEra, paramInt1, paramInt2); }
  
  public JapaneseDate dateYearDay(int paramInt1, int paramInt2) { return new JapaneseDate(LocalDate.ofYearDay(paramInt1, paramInt2)); }
  
  public JapaneseDate dateEpochDay(long paramLong) { return new JapaneseDate(LocalDate.ofEpochDay(paramLong)); }
  
  public JapaneseDate dateNow() { return dateNow(Clock.systemDefaultZone()); }
  
  public JapaneseDate dateNow(ZoneId paramZoneId) { return dateNow(Clock.system(paramZoneId)); }
  
  public JapaneseDate dateNow(Clock paramClock) { return date(LocalDate.now(paramClock)); }
  
  public JapaneseDate date(TemporalAccessor paramTemporalAccessor) { return (paramTemporalAccessor instanceof JapaneseDate) ? (JapaneseDate)paramTemporalAccessor : new JapaneseDate(LocalDate.from(paramTemporalAccessor)); }
  
  public ChronoLocalDateTime<JapaneseDate> localDateTime(TemporalAccessor paramTemporalAccessor) { return super.localDateTime(paramTemporalAccessor); }
  
  public ChronoZonedDateTime<JapaneseDate> zonedDateTime(TemporalAccessor paramTemporalAccessor) { return super.zonedDateTime(paramTemporalAccessor); }
  
  public ChronoZonedDateTime<JapaneseDate> zonedDateTime(Instant paramInstant, ZoneId paramZoneId) { return super.zonedDateTime(paramInstant, paramZoneId); }
  
  public boolean isLeapYear(long paramLong) { return IsoChronology.INSTANCE.isLeapYear(paramLong); }
  
  public int prolepticYear(Era paramEra, int paramInt) {
    if (!(paramEra instanceof JapaneseEra))
      throw new ClassCastException("Era must be JapaneseEra"); 
    JapaneseEra japaneseEra = (JapaneseEra)paramEra;
    int i = japaneseEra.getPrivateEra().getSinceDate().getYear() + paramInt - 1;
    if (paramInt == 1)
      return i; 
    if (i >= -999999999 && i <= 999999999) {
      LocalGregorianCalendar.Date date = JCAL.newCalendarDate(null);
      date.setEra(japaneseEra.getPrivateEra()).setDate(paramInt, 1, 1);
      if (JCAL.validate(date))
        return i; 
    } 
    throw new DateTimeException("Invalid yearOfEra value");
  }
  
  public JapaneseEra eraOf(int paramInt) { return JapaneseEra.of(paramInt); }
  
  public List<Era> eras() { return Arrays.asList(JapaneseEra.values()); }
  
  JapaneseEra getCurrentEra() {
    JapaneseEra[] arrayOfJapaneseEra = JapaneseEra.values();
    return arrayOfJapaneseEra[arrayOfJapaneseEra.length - 1];
  }
  
  public ValueRange range(ChronoField paramChronoField) {
    int i;
    Calendar calendar;
    switch (paramChronoField) {
      case ALIGNED_DAY_OF_WEEK_IN_MONTH:
      case ALIGNED_DAY_OF_WEEK_IN_YEAR:
      case ALIGNED_WEEK_OF_MONTH:
      case ALIGNED_WEEK_OF_YEAR:
        throw new UnsupportedTemporalTypeException("Unsupported field: " + paramChronoField);
      case YEAR_OF_ERA:
        calendar = Calendar.getInstance(LOCALE);
        i = getCurrentEra().getPrivateEra().getSinceDate().getYear();
        return ValueRange.of(1L, calendar.getGreatestMinimum(1), (calendar.getLeastMaximum(1) + 1), (999999999 - i));
      case DAY_OF_YEAR:
        calendar = Calendar.getInstance(LOCALE);
        i = 6;
        return ValueRange.of(calendar.getMinimum(i), calendar.getGreatestMinimum(i), calendar.getLeastMaximum(i), calendar.getMaximum(i));
      case YEAR:
        return ValueRange.of(JapaneseDate.MEIJI_6_ISODATE.getYear(), 999999999L);
      case ERA:
        return ValueRange.of(JapaneseEra.MEIJI.getValue(), getCurrentEra().getValue());
    } 
    return paramChronoField.range();
  }
  
  public JapaneseDate resolveDate(Map<TemporalField, Long> paramMap, ResolverStyle paramResolverStyle) { return (JapaneseDate)super.resolveDate(paramMap, paramResolverStyle); }
  
  ChronoLocalDate resolveYearOfEra(Map<TemporalField, Long> paramMap, ResolverStyle paramResolverStyle) {
    Long long1 = (Long)paramMap.get(ChronoField.ERA);
    JapaneseEra japaneseEra = null;
    if (long1 != null)
      japaneseEra = eraOf(range(ChronoField.ERA).checkValidIntValue(long1.longValue(), ChronoField.ERA)); 
    Long long2 = (Long)paramMap.get(ChronoField.YEAR_OF_ERA);
    int i = 0;
    if (long2 != null)
      i = range(ChronoField.YEAR_OF_ERA).checkValidIntValue(long2.longValue(), ChronoField.YEAR_OF_ERA); 
    if (japaneseEra == null && long2 != null && !paramMap.containsKey(ChronoField.YEAR) && paramResolverStyle != ResolverStyle.STRICT)
      japaneseEra = JapaneseEra.values()[JapaneseEra.values().length - 1]; 
    if (long2 != null && japaneseEra != null) {
      if (paramMap.containsKey(ChronoField.MONTH_OF_YEAR) && paramMap.containsKey(ChronoField.DAY_OF_MONTH))
        return resolveYMD(japaneseEra, i, paramMap, paramResolverStyle); 
      if (paramMap.containsKey(ChronoField.DAY_OF_YEAR))
        return resolveYD(japaneseEra, i, paramMap, paramResolverStyle); 
    } 
    return null;
  }
  
  private int prolepticYearLenient(JapaneseEra paramJapaneseEra, int paramInt) { return paramJapaneseEra.getPrivateEra().getSinceDate().getYear() + paramInt - 1; }
  
  private ChronoLocalDate resolveYMD(JapaneseEra paramJapaneseEra, int paramInt, Map<TemporalField, Long> paramMap, ResolverStyle paramResolverStyle) {
    paramMap.remove(ChronoField.ERA);
    paramMap.remove(ChronoField.YEAR_OF_ERA);
    if (paramResolverStyle == ResolverStyle.LENIENT) {
      int k = prolepticYearLenient(paramJapaneseEra, paramInt);
      long l1 = Math.subtractExact(((Long)paramMap.remove(ChronoField.MONTH_OF_YEAR)).longValue(), 1L);
      long l2 = Math.subtractExact(((Long)paramMap.remove(ChronoField.DAY_OF_MONTH)).longValue(), 1L);
      return date(k, 1, 1).plus(l1, ChronoUnit.MONTHS).plus(l2, ChronoUnit.DAYS);
    } 
    int i = range(ChronoField.MONTH_OF_YEAR).checkValidIntValue(((Long)paramMap.remove(ChronoField.MONTH_OF_YEAR)).longValue(), ChronoField.MONTH_OF_YEAR);
    int j = range(ChronoField.DAY_OF_MONTH).checkValidIntValue(((Long)paramMap.remove(ChronoField.DAY_OF_MONTH)).longValue(), ChronoField.DAY_OF_MONTH);
    if (paramResolverStyle == ResolverStyle.SMART) {
      JapaneseDate japaneseDate;
      if (paramInt < 1)
        throw new DateTimeException("Invalid YearOfEra: " + paramInt); 
      int k = prolepticYearLenient(paramJapaneseEra, paramInt);
      try {
        japaneseDate = date(k, i, j);
      } catch (DateTimeException dateTimeException) {
        japaneseDate = date(k, i, 1).with(TemporalAdjusters.lastDayOfMonth());
      } 
      if (japaneseDate.getEra() != paramJapaneseEra && japaneseDate.get(ChronoField.YEAR_OF_ERA) > 1 && paramInt > 1)
        throw new DateTimeException("Invalid YearOfEra for Era: " + paramJapaneseEra + " " + paramInt); 
      return japaneseDate;
    } 
    return date(paramJapaneseEra, paramInt, i, j);
  }
  
  private ChronoLocalDate resolveYD(JapaneseEra paramJapaneseEra, int paramInt, Map<TemporalField, Long> paramMap, ResolverStyle paramResolverStyle) {
    paramMap.remove(ChronoField.ERA);
    paramMap.remove(ChronoField.YEAR_OF_ERA);
    if (paramResolverStyle == ResolverStyle.LENIENT) {
      int j = prolepticYearLenient(paramJapaneseEra, paramInt);
      long l = Math.subtractExact(((Long)paramMap.remove(ChronoField.DAY_OF_YEAR)).longValue(), 1L);
      return dateYearDay(j, 1).plus(l, ChronoUnit.DAYS);
    } 
    int i = range(ChronoField.DAY_OF_YEAR).checkValidIntValue(((Long)paramMap.remove(ChronoField.DAY_OF_YEAR)).longValue(), ChronoField.DAY_OF_YEAR);
    return dateYearDay(paramJapaneseEra, paramInt, i);
  }
  
  Object writeReplace() { return super.writeReplace(); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Deserialization via serialization delegate"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\chrono\JapaneseChronology.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */