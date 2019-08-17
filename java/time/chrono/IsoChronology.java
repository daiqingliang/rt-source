package java.time.chrono;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class IsoChronology extends AbstractChronology implements Serializable {
  public static final IsoChronology INSTANCE = new IsoChronology();
  
  private static final long serialVersionUID = -1440403870442975015L;
  
  public String getId() { return "ISO"; }
  
  public String getCalendarType() { return "iso8601"; }
  
  public LocalDate date(Era paramEra, int paramInt1, int paramInt2, int paramInt3) { return date(prolepticYear(paramEra, paramInt1), paramInt2, paramInt3); }
  
  public LocalDate date(int paramInt1, int paramInt2, int paramInt3) { return LocalDate.of(paramInt1, paramInt2, paramInt3); }
  
  public LocalDate dateYearDay(Era paramEra, int paramInt1, int paramInt2) { return dateYearDay(prolepticYear(paramEra, paramInt1), paramInt2); }
  
  public LocalDate dateYearDay(int paramInt1, int paramInt2) { return LocalDate.ofYearDay(paramInt1, paramInt2); }
  
  public LocalDate dateEpochDay(long paramLong) { return LocalDate.ofEpochDay(paramLong); }
  
  public LocalDate date(TemporalAccessor paramTemporalAccessor) { return LocalDate.from(paramTemporalAccessor); }
  
  public LocalDateTime localDateTime(TemporalAccessor paramTemporalAccessor) { return LocalDateTime.from(paramTemporalAccessor); }
  
  public ZonedDateTime zonedDateTime(TemporalAccessor paramTemporalAccessor) { return ZonedDateTime.from(paramTemporalAccessor); }
  
  public ZonedDateTime zonedDateTime(Instant paramInstant, ZoneId paramZoneId) { return ZonedDateTime.ofInstant(paramInstant, paramZoneId); }
  
  public LocalDate dateNow() { return dateNow(Clock.systemDefaultZone()); }
  
  public LocalDate dateNow(ZoneId paramZoneId) { return dateNow(Clock.system(paramZoneId)); }
  
  public LocalDate dateNow(Clock paramClock) {
    Objects.requireNonNull(paramClock, "clock");
    return date(LocalDate.now(paramClock));
  }
  
  public boolean isLeapYear(long paramLong) { return ((paramLong & 0x3L) == 0L && (paramLong % 100L != 0L || paramLong % 400L == 0L)); }
  
  public int prolepticYear(Era paramEra, int paramInt) {
    if (!(paramEra instanceof IsoEra))
      throw new ClassCastException("Era must be IsoEra"); 
    return (paramEra == IsoEra.CE) ? paramInt : (1 - paramInt);
  }
  
  public IsoEra eraOf(int paramInt) { return IsoEra.of(paramInt); }
  
  public List<Era> eras() { return Arrays.asList(IsoEra.values()); }
  
  public LocalDate resolveDate(Map<TemporalField, Long> paramMap, ResolverStyle paramResolverStyle) { return (LocalDate)super.resolveDate(paramMap, paramResolverStyle); }
  
  void resolveProlepticMonth(Map<TemporalField, Long> paramMap, ResolverStyle paramResolverStyle) {
    Long long = (Long)paramMap.remove(ChronoField.PROLEPTIC_MONTH);
    if (long != null) {
      if (paramResolverStyle != ResolverStyle.LENIENT)
        ChronoField.PROLEPTIC_MONTH.checkValidValue(long.longValue()); 
      addFieldValue(paramMap, ChronoField.MONTH_OF_YEAR, Math.floorMod(long.longValue(), 12L) + 1L);
      addFieldValue(paramMap, ChronoField.YEAR, Math.floorDiv(long.longValue(), 12L));
    } 
  }
  
  LocalDate resolveYearOfEra(Map<TemporalField, Long> paramMap, ResolverStyle paramResolverStyle) {
    Long long = (Long)paramMap.remove(ChronoField.YEAR_OF_ERA);
    if (long != null) {
      if (paramResolverStyle != ResolverStyle.LENIENT)
        ChronoField.YEAR_OF_ERA.checkValidValue(long.longValue()); 
      Long long1 = (Long)paramMap.remove(ChronoField.ERA);
      if (long1 == null) {
        Long long2 = (Long)paramMap.get(ChronoField.YEAR);
        if (paramResolverStyle == ResolverStyle.STRICT) {
          if (long2 != null) {
            addFieldValue(paramMap, ChronoField.YEAR, (long2.longValue() > 0L) ? long.longValue() : Math.subtractExact(1L, long.longValue()));
          } else {
            paramMap.put(ChronoField.YEAR_OF_ERA, long);
          } 
        } else {
          addFieldValue(paramMap, ChronoField.YEAR, (long2 == null || long2.longValue() > 0L) ? long.longValue() : Math.subtractExact(1L, long.longValue()));
        } 
      } else if (long1.longValue() == 1L) {
        addFieldValue(paramMap, ChronoField.YEAR, long.longValue());
      } else if (long1.longValue() == 0L) {
        addFieldValue(paramMap, ChronoField.YEAR, Math.subtractExact(1L, long.longValue()));
      } else {
        throw new DateTimeException("Invalid value for era: " + long1);
      } 
    } else if (paramMap.containsKey(ChronoField.ERA)) {
      ChronoField.ERA.checkValidValue(((Long)paramMap.get(ChronoField.ERA)).longValue());
    } 
    return null;
  }
  
  LocalDate resolveYMD(Map<TemporalField, Long> paramMap, ResolverStyle paramResolverStyle) {
    int i = ChronoField.YEAR.checkValidIntValue(((Long)paramMap.remove(ChronoField.YEAR)).longValue());
    if (paramResolverStyle == ResolverStyle.LENIENT) {
      long l1 = Math.subtractExact(((Long)paramMap.remove(ChronoField.MONTH_OF_YEAR)).longValue(), 1L);
      long l2 = Math.subtractExact(((Long)paramMap.remove(ChronoField.DAY_OF_MONTH)).longValue(), 1L);
      return LocalDate.of(i, 1, 1).plusMonths(l1).plusDays(l2);
    } 
    int j = ChronoField.MONTH_OF_YEAR.checkValidIntValue(((Long)paramMap.remove(ChronoField.MONTH_OF_YEAR)).longValue());
    int k = ChronoField.DAY_OF_MONTH.checkValidIntValue(((Long)paramMap.remove(ChronoField.DAY_OF_MONTH)).longValue());
    if (paramResolverStyle == ResolverStyle.SMART)
      if (j == 4 || j == 6 || j == 9 || j == 11) {
        k = Math.min(k, 30);
      } else if (j == 2) {
        k = Math.min(k, Month.FEBRUARY.length(Year.isLeap(i)));
      }  
    return LocalDate.of(i, j, k);
  }
  
  public ValueRange range(ChronoField paramChronoField) { return paramChronoField.range(); }
  
  public Period period(int paramInt1, int paramInt2, int paramInt3) { return Period.of(paramInt1, paramInt2, paramInt3); }
  
  Object writeReplace() { return super.writeReplace(); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Deserialization via serialization delegate"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\chrono\IsoChronology.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */