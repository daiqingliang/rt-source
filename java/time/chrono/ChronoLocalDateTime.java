package java.time.chrono;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
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
import java.util.Comparator;
import java.util.Objects;

public interface ChronoLocalDateTime<D extends ChronoLocalDate> extends Temporal, TemporalAdjuster, Comparable<ChronoLocalDateTime<?>> {
  static Comparator<ChronoLocalDateTime<?>> timeLineOrder() { return AbstractChronology.DATE_TIME_ORDER; }
  
  static ChronoLocalDateTime<?> from(TemporalAccessor paramTemporalAccessor) {
    if (paramTemporalAccessor instanceof ChronoLocalDateTime)
      return (ChronoLocalDateTime)paramTemporalAccessor; 
    Objects.requireNonNull(paramTemporalAccessor, "temporal");
    Chronology chronology = (Chronology)paramTemporalAccessor.query(TemporalQueries.chronology());
    if (chronology == null)
      throw new DateTimeException("Unable to obtain ChronoLocalDateTime from TemporalAccessor: " + paramTemporalAccessor.getClass()); 
    return chronology.localDateTime(paramTemporalAccessor);
  }
  
  default Chronology getChronology() { return toLocalDate().getChronology(); }
  
  D toLocalDate();
  
  LocalTime toLocalTime();
  
  boolean isSupported(TemporalField paramTemporalField);
  
  default boolean isSupported(TemporalUnit paramTemporalUnit) { return (paramTemporalUnit instanceof ChronoUnit) ? ((paramTemporalUnit != ChronoUnit.FOREVER)) : ((paramTemporalUnit != null && paramTemporalUnit.isSupportedBy(this))); }
  
  default ChronoLocalDateTime<D> with(TemporalAdjuster paramTemporalAdjuster) { return ChronoLocalDateTimeImpl.ensureValid(getChronology(), super.with(paramTemporalAdjuster)); }
  
  ChronoLocalDateTime<D> with(TemporalField paramTemporalField, long paramLong);
  
  default ChronoLocalDateTime<D> plus(TemporalAmount paramTemporalAmount) { return ChronoLocalDateTimeImpl.ensureValid(getChronology(), super.plus(paramTemporalAmount)); }
  
  ChronoLocalDateTime<D> plus(long paramLong, TemporalUnit paramTemporalUnit);
  
  default ChronoLocalDateTime<D> minus(TemporalAmount paramTemporalAmount) { return ChronoLocalDateTimeImpl.ensureValid(getChronology(), super.minus(paramTemporalAmount)); }
  
  default ChronoLocalDateTime<D> minus(long paramLong, TemporalUnit paramTemporalUnit) { return ChronoLocalDateTimeImpl.ensureValid(getChronology(), super.minus(paramLong, paramTemporalUnit)); }
  
  default <R> R query(TemporalQuery<R> paramTemporalQuery) { return (paramTemporalQuery == TemporalQueries.zoneId() || paramTemporalQuery == TemporalQueries.zone() || paramTemporalQuery == TemporalQueries.offset()) ? null : ((paramTemporalQuery == TemporalQueries.localTime()) ? (R)toLocalTime() : ((paramTemporalQuery == TemporalQueries.chronology()) ? (R)getChronology() : ((paramTemporalQuery == TemporalQueries.precision()) ? (R)ChronoUnit.NANOS : (R)paramTemporalQuery.queryFrom(this)))); }
  
  default Temporal adjustInto(Temporal paramTemporal) { return paramTemporal.with(ChronoField.EPOCH_DAY, toLocalDate().toEpochDay()).with(ChronoField.NANO_OF_DAY, toLocalTime().toNanoOfDay()); }
  
  default String format(DateTimeFormatter paramDateTimeFormatter) {
    Objects.requireNonNull(paramDateTimeFormatter, "formatter");
    return paramDateTimeFormatter.format(this);
  }
  
  ChronoZonedDateTime<D> atZone(ZoneId paramZoneId);
  
  default Instant toInstant(ZoneOffset paramZoneOffset) { return Instant.ofEpochSecond(toEpochSecond(paramZoneOffset), toLocalTime().getNano()); }
  
  default long toEpochSecond(ZoneOffset paramZoneOffset) {
    Objects.requireNonNull(paramZoneOffset, "offset");
    long l = toLocalDate().toEpochDay();
    null = l * 86400L + toLocalTime().toSecondOfDay();
    return paramZoneOffset.getTotalSeconds();
  }
  
  default int compareTo(ChronoLocalDateTime<?> paramChronoLocalDateTime) {
    int i = toLocalDate().compareTo(paramChronoLocalDateTime.toLocalDate());
    if (i == 0) {
      i = toLocalTime().compareTo(paramChronoLocalDateTime.toLocalTime());
      if (i == 0)
        i = getChronology().compareTo(paramChronoLocalDateTime.getChronology()); 
    } 
    return i;
  }
  
  default boolean isAfter(ChronoLocalDateTime<?> paramChronoLocalDateTime) {
    long l1 = toLocalDate().toEpochDay();
    long l2 = paramChronoLocalDateTime.toLocalDate().toEpochDay();
    return (l1 > l2 || (l1 == l2 && toLocalTime().toNanoOfDay() > paramChronoLocalDateTime.toLocalTime().toNanoOfDay()));
  }
  
  default boolean isBefore(ChronoLocalDateTime<?> paramChronoLocalDateTime) {
    long l1 = toLocalDate().toEpochDay();
    long l2 = paramChronoLocalDateTime.toLocalDate().toEpochDay();
    return (l1 < l2 || (l1 == l2 && toLocalTime().toNanoOfDay() < paramChronoLocalDateTime.toLocalTime().toNanoOfDay()));
  }
  
  default boolean isEqual(ChronoLocalDateTime<?> paramChronoLocalDateTime) { return (toLocalTime().toNanoOfDay() == paramChronoLocalDateTime.toLocalTime().toNanoOfDay() && toLocalDate().toEpochDay() == paramChronoLocalDateTime.toLocalDate().toEpochDay()); }
  
  boolean equals(Object paramObject);
  
  int hashCode();
  
  String toString();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\chrono\ChronoLocalDateTime.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */