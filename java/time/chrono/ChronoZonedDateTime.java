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
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Comparator;
import java.util.Objects;

public interface ChronoZonedDateTime<D extends ChronoLocalDate> extends Temporal, Comparable<ChronoZonedDateTime<?>> {
  static Comparator<ChronoZonedDateTime<?>> timeLineOrder() { return AbstractChronology.INSTANT_ORDER; }
  
  static ChronoZonedDateTime<?> from(TemporalAccessor paramTemporalAccessor) {
    if (paramTemporalAccessor instanceof ChronoZonedDateTime)
      return (ChronoZonedDateTime)paramTemporalAccessor; 
    Objects.requireNonNull(paramTemporalAccessor, "temporal");
    Chronology chronology = (Chronology)paramTemporalAccessor.query(TemporalQueries.chronology());
    if (chronology == null)
      throw new DateTimeException("Unable to obtain ChronoZonedDateTime from TemporalAccessor: " + paramTemporalAccessor.getClass()); 
    return chronology.zonedDateTime(paramTemporalAccessor);
  }
  
  default ValueRange range(TemporalField paramTemporalField) { return (paramTemporalField instanceof ChronoField) ? ((paramTemporalField == ChronoField.INSTANT_SECONDS || paramTemporalField == ChronoField.OFFSET_SECONDS) ? paramTemporalField.range() : toLocalDateTime().range(paramTemporalField)) : paramTemporalField.rangeRefinedBy(this); }
  
  default int get(TemporalField paramTemporalField) {
    if (paramTemporalField instanceof ChronoField) {
      switch ((ChronoField)paramTemporalField) {
        case INSTANT_SECONDS:
          throw new UnsupportedTemporalTypeException("Invalid field 'InstantSeconds' for get() method, use getLong() instead");
        case OFFSET_SECONDS:
          return getOffset().getTotalSeconds();
      } 
      return toLocalDateTime().get(paramTemporalField);
    } 
    return super.get(paramTemporalField);
  }
  
  default long getLong(TemporalField paramTemporalField) {
    if (paramTemporalField instanceof ChronoField) {
      switch ((ChronoField)paramTemporalField) {
        case INSTANT_SECONDS:
          return toEpochSecond();
        case OFFSET_SECONDS:
          return getOffset().getTotalSeconds();
      } 
      return toLocalDateTime().getLong(paramTemporalField);
    } 
    return paramTemporalField.getFrom(this);
  }
  
  default D toLocalDate() { return (D)toLocalDateTime().toLocalDate(); }
  
  default LocalTime toLocalTime() { return toLocalDateTime().toLocalTime(); }
  
  ChronoLocalDateTime<D> toLocalDateTime();
  
  default Chronology getChronology() { return toLocalDate().getChronology(); }
  
  ZoneOffset getOffset();
  
  ZoneId getZone();
  
  ChronoZonedDateTime<D> withEarlierOffsetAtOverlap();
  
  ChronoZonedDateTime<D> withLaterOffsetAtOverlap();
  
  ChronoZonedDateTime<D> withZoneSameLocal(ZoneId paramZoneId);
  
  ChronoZonedDateTime<D> withZoneSameInstant(ZoneId paramZoneId);
  
  boolean isSupported(TemporalField paramTemporalField);
  
  default boolean isSupported(TemporalUnit paramTemporalUnit) { return (paramTemporalUnit instanceof ChronoUnit) ? ((paramTemporalUnit != ChronoUnit.FOREVER)) : ((paramTemporalUnit != null && paramTemporalUnit.isSupportedBy(this))); }
  
  default ChronoZonedDateTime<D> with(TemporalAdjuster paramTemporalAdjuster) { return ChronoZonedDateTimeImpl.ensureValid(getChronology(), super.with(paramTemporalAdjuster)); }
  
  ChronoZonedDateTime<D> with(TemporalField paramTemporalField, long paramLong);
  
  default ChronoZonedDateTime<D> plus(TemporalAmount paramTemporalAmount) { return ChronoZonedDateTimeImpl.ensureValid(getChronology(), super.plus(paramTemporalAmount)); }
  
  ChronoZonedDateTime<D> plus(long paramLong, TemporalUnit paramTemporalUnit);
  
  default ChronoZonedDateTime<D> minus(TemporalAmount paramTemporalAmount) { return ChronoZonedDateTimeImpl.ensureValid(getChronology(), super.minus(paramTemporalAmount)); }
  
  default ChronoZonedDateTime<D> minus(long paramLong, TemporalUnit paramTemporalUnit) { return ChronoZonedDateTimeImpl.ensureValid(getChronology(), super.minus(paramLong, paramTemporalUnit)); }
  
  default <R> R query(TemporalQuery<R> paramTemporalQuery) { return (paramTemporalQuery == TemporalQueries.zone() || paramTemporalQuery == TemporalQueries.zoneId()) ? (R)getZone() : ((paramTemporalQuery == TemporalQueries.offset()) ? (R)getOffset() : ((paramTemporalQuery == TemporalQueries.localTime()) ? (R)toLocalTime() : ((paramTemporalQuery == TemporalQueries.chronology()) ? (R)getChronology() : ((paramTemporalQuery == TemporalQueries.precision()) ? (R)ChronoUnit.NANOS : (R)paramTemporalQuery.queryFrom(this))))); }
  
  default String format(DateTimeFormatter paramDateTimeFormatter) {
    Objects.requireNonNull(paramDateTimeFormatter, "formatter");
    return paramDateTimeFormatter.format(this);
  }
  
  default Instant toInstant() { return Instant.ofEpochSecond(toEpochSecond(), toLocalTime().getNano()); }
  
  default long toEpochSecond() {
    long l = toLocalDate().toEpochDay();
    null = l * 86400L + toLocalTime().toSecondOfDay();
    return getOffset().getTotalSeconds();
  }
  
  default int compareTo(ChronoZonedDateTime<?> paramChronoZonedDateTime) {
    int i = Long.compare(toEpochSecond(), paramChronoZonedDateTime.toEpochSecond());
    if (i == 0) {
      i = toLocalTime().getNano() - paramChronoZonedDateTime.toLocalTime().getNano();
      if (i == 0) {
        i = toLocalDateTime().compareTo(paramChronoZonedDateTime.toLocalDateTime());
        if (i == 0) {
          i = getZone().getId().compareTo(paramChronoZonedDateTime.getZone().getId());
          if (i == 0)
            i = getChronology().compareTo(paramChronoZonedDateTime.getChronology()); 
        } 
      } 
    } 
    return i;
  }
  
  default boolean isBefore(ChronoZonedDateTime<?> paramChronoZonedDateTime) {
    long l1 = toEpochSecond();
    long l2 = paramChronoZonedDateTime.toEpochSecond();
    return (l1 < l2 || (l1 == l2 && toLocalTime().getNano() < paramChronoZonedDateTime.toLocalTime().getNano()));
  }
  
  default boolean isAfter(ChronoZonedDateTime<?> paramChronoZonedDateTime) {
    long l1 = toEpochSecond();
    long l2 = paramChronoZonedDateTime.toEpochSecond();
    return (l1 > l2 || (l1 == l2 && toLocalTime().getNano() > paramChronoZonedDateTime.toLocalTime().getNano()));
  }
  
  default boolean isEqual(ChronoZonedDateTime<?> paramChronoZonedDateTime) { return (toEpochSecond() == paramChronoZonedDateTime.toEpochSecond() && toLocalTime().getNano() == paramChronoZonedDateTime.toLocalTime().getNano()); }
  
  boolean equals(Object paramObject);
  
  int hashCode();
  
  String toString();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\chrono\ChronoZonedDateTime.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */