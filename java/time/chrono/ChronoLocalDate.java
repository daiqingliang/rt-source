package java.time.chrono;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Comparator;
import java.util.Objects;

public interface ChronoLocalDate extends Temporal, TemporalAdjuster, Comparable<ChronoLocalDate> {
  static Comparator<ChronoLocalDate> timeLineOrder() { return AbstractChronology.DATE_ORDER; }
  
  static ChronoLocalDate from(TemporalAccessor paramTemporalAccessor) {
    if (paramTemporalAccessor instanceof ChronoLocalDate)
      return (ChronoLocalDate)paramTemporalAccessor; 
    Objects.requireNonNull(paramTemporalAccessor, "temporal");
    Chronology chronology = (Chronology)paramTemporalAccessor.query(TemporalQueries.chronology());
    if (chronology == null)
      throw new DateTimeException("Unable to obtain ChronoLocalDate from TemporalAccessor: " + paramTemporalAccessor.getClass()); 
    return chronology.date(paramTemporalAccessor);
  }
  
  Chronology getChronology();
  
  default Era getEra() { return getChronology().eraOf(get(ChronoField.ERA)); }
  
  default boolean isLeapYear() { return getChronology().isLeapYear(getLong(ChronoField.YEAR)); }
  
  int lengthOfMonth();
  
  default int lengthOfYear() { return isLeapYear() ? 366 : 365; }
  
  default boolean isSupported(TemporalField paramTemporalField) { return (paramTemporalField instanceof ChronoField) ? paramTemporalField.isDateBased() : ((paramTemporalField != null && paramTemporalField.isSupportedBy(this)) ? 1 : 0); }
  
  default boolean isSupported(TemporalUnit paramTemporalUnit) { return (paramTemporalUnit instanceof ChronoUnit) ? paramTemporalUnit.isDateBased() : ((paramTemporalUnit != null && paramTemporalUnit.isSupportedBy(this)) ? 1 : 0); }
  
  default ChronoLocalDate with(TemporalAdjuster paramTemporalAdjuster) { return ChronoLocalDateImpl.ensureValid(getChronology(), super.with(paramTemporalAdjuster)); }
  
  default ChronoLocalDate with(TemporalField paramTemporalField, long paramLong) {
    if (paramTemporalField instanceof ChronoField)
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField); 
    return ChronoLocalDateImpl.ensureValid(getChronology(), paramTemporalField.adjustInto(this, paramLong));
  }
  
  default ChronoLocalDate plus(TemporalAmount paramTemporalAmount) { return ChronoLocalDateImpl.ensureValid(getChronology(), super.plus(paramTemporalAmount)); }
  
  default ChronoLocalDate plus(long paramLong, TemporalUnit paramTemporalUnit) {
    if (paramTemporalUnit instanceof ChronoUnit)
      throw new UnsupportedTemporalTypeException("Unsupported unit: " + paramTemporalUnit); 
    return ChronoLocalDateImpl.ensureValid(getChronology(), paramTemporalUnit.addTo(this, paramLong));
  }
  
  default ChronoLocalDate minus(TemporalAmount paramTemporalAmount) { return ChronoLocalDateImpl.ensureValid(getChronology(), super.minus(paramTemporalAmount)); }
  
  default ChronoLocalDate minus(long paramLong, TemporalUnit paramTemporalUnit) { return ChronoLocalDateImpl.ensureValid(getChronology(), super.minus(paramLong, paramTemporalUnit)); }
  
  default <R> R query(TemporalQuery<R> paramTemporalQuery) { return (paramTemporalQuery == TemporalQueries.zoneId() || paramTemporalQuery == TemporalQueries.zone() || paramTemporalQuery == TemporalQueries.offset()) ? null : ((paramTemporalQuery == TemporalQueries.localTime()) ? null : ((paramTemporalQuery == TemporalQueries.chronology()) ? (R)getChronology() : ((paramTemporalQuery == TemporalQueries.precision()) ? (R)ChronoUnit.DAYS : (R)paramTemporalQuery.queryFrom(this)))); }
  
  default Temporal adjustInto(Temporal paramTemporal) { return paramTemporal.with(ChronoField.EPOCH_DAY, toEpochDay()); }
  
  long until(Temporal paramTemporal, TemporalUnit paramTemporalUnit);
  
  ChronoPeriod until(ChronoLocalDate paramChronoLocalDate);
  
  default String format(DateTimeFormatter paramDateTimeFormatter) {
    Objects.requireNonNull(paramDateTimeFormatter, "formatter");
    return paramDateTimeFormatter.format(this);
  }
  
  default ChronoLocalDateTime<?> atTime(LocalTime paramLocalTime) { return ChronoLocalDateTimeImpl.of(this, paramLocalTime); }
  
  default long toEpochDay() { return getLong(ChronoField.EPOCH_DAY); }
  
  default int compareTo(ChronoLocalDate paramChronoLocalDate) {
    int i = Long.compare(toEpochDay(), paramChronoLocalDate.toEpochDay());
    if (i == 0)
      i = getChronology().compareTo(paramChronoLocalDate.getChronology()); 
    return i;
  }
  
  default boolean isAfter(ChronoLocalDate paramChronoLocalDate) { return (toEpochDay() > paramChronoLocalDate.toEpochDay()); }
  
  default boolean isBefore(ChronoLocalDate paramChronoLocalDate) { return (toEpochDay() < paramChronoLocalDate.toEpochDay()); }
  
  default boolean isEqual(ChronoLocalDate paramChronoLocalDate) { return (toEpochDay() == paramChronoLocalDate.toEpochDay()); }
  
  boolean equals(Object paramObject);
  
  int hashCode();
  
  String toString();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\chrono\ChronoLocalDate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */