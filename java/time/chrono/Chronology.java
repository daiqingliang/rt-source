package java.time.chrono;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public interface Chronology extends Comparable<Chronology> {
  static Chronology from(TemporalAccessor paramTemporalAccessor) {
    Objects.requireNonNull(paramTemporalAccessor, "temporal");
    Chronology chronology = (Chronology)paramTemporalAccessor.query(TemporalQueries.chronology());
    return (chronology != null) ? chronology : IsoChronology.INSTANCE;
  }
  
  static Chronology ofLocale(Locale paramLocale) { return AbstractChronology.ofLocale(paramLocale); }
  
  static Chronology of(String paramString) { return AbstractChronology.of(paramString); }
  
  static Set<Chronology> getAvailableChronologies() { return AbstractChronology.getAvailableChronologies(); }
  
  String getId();
  
  String getCalendarType();
  
  default ChronoLocalDate date(Era paramEra, int paramInt1, int paramInt2, int paramInt3) { return date(prolepticYear(paramEra, paramInt1), paramInt2, paramInt3); }
  
  ChronoLocalDate date(int paramInt1, int paramInt2, int paramInt3);
  
  default ChronoLocalDate dateYearDay(Era paramEra, int paramInt1, int paramInt2) { return dateYearDay(prolepticYear(paramEra, paramInt1), paramInt2); }
  
  ChronoLocalDate dateYearDay(int paramInt1, int paramInt2);
  
  ChronoLocalDate dateEpochDay(long paramLong);
  
  default ChronoLocalDate dateNow() { return dateNow(Clock.systemDefaultZone()); }
  
  default ChronoLocalDate dateNow(ZoneId paramZoneId) { return dateNow(Clock.system(paramZoneId)); }
  
  default ChronoLocalDate dateNow(Clock paramClock) {
    Objects.requireNonNull(paramClock, "clock");
    return date(LocalDate.now(paramClock));
  }
  
  ChronoLocalDate date(TemporalAccessor paramTemporalAccessor);
  
  default ChronoLocalDateTime<? extends ChronoLocalDate> localDateTime(TemporalAccessor paramTemporalAccessor) {
    try {
      return date(paramTemporalAccessor).atTime(LocalTime.from(paramTemporalAccessor));
    } catch (DateTimeException dateTimeException) {
      throw new DateTimeException("Unable to obtain ChronoLocalDateTime from TemporalAccessor: " + paramTemporalAccessor.getClass(), dateTimeException);
    } 
  }
  
  default ChronoZonedDateTime<? extends ChronoLocalDate> zonedDateTime(TemporalAccessor paramTemporalAccessor) {
    try {
      ZoneId zoneId = ZoneId.from(paramTemporalAccessor);
      try {
        Instant instant = Instant.from(paramTemporalAccessor);
        return zonedDateTime(instant, zoneId);
      } catch (DateTimeException dateTimeException) {
        ChronoLocalDateTimeImpl chronoLocalDateTimeImpl = ChronoLocalDateTimeImpl.ensureValid(this, localDateTime(paramTemporalAccessor));
        return ChronoZonedDateTimeImpl.ofBest(chronoLocalDateTimeImpl, zoneId, null);
      } 
    } catch (DateTimeException dateTimeException) {
      throw new DateTimeException("Unable to obtain ChronoZonedDateTime from TemporalAccessor: " + paramTemporalAccessor.getClass(), dateTimeException);
    } 
  }
  
  default ChronoZonedDateTime<? extends ChronoLocalDate> zonedDateTime(Instant paramInstant, ZoneId paramZoneId) { return ChronoZonedDateTimeImpl.ofInstant(this, paramInstant, paramZoneId); }
  
  boolean isLeapYear(long paramLong);
  
  int prolepticYear(Era paramEra, int paramInt);
  
  Era eraOf(int paramInt);
  
  List<Era> eras();
  
  ValueRange range(ChronoField paramChronoField);
  
  default String getDisplayName(TextStyle paramTextStyle, Locale paramLocale) {
    TemporalAccessor temporalAccessor = new TemporalAccessor() {
        public boolean isSupported(TemporalField param1TemporalField) { return false; }
        
        public long getLong(TemporalField param1TemporalField) { throw new UnsupportedTemporalTypeException("Unsupported field: " + param1TemporalField); }
        
        public <R> R query(TemporalQuery<R> param1TemporalQuery) { return (param1TemporalQuery == TemporalQueries.chronology()) ? (R)Chronology.this : (R)super.query(param1TemporalQuery); }
      };
    return (new DateTimeFormatterBuilder()).appendChronologyText(paramTextStyle).toFormatter(paramLocale).format(temporalAccessor);
  }
  
  ChronoLocalDate resolveDate(Map<TemporalField, Long> paramMap, ResolverStyle paramResolverStyle);
  
  default ChronoPeriod period(int paramInt1, int paramInt2, int paramInt3) { return new ChronoPeriodImpl(this, paramInt1, paramInt2, paramInt3); }
  
  int compareTo(Chronology paramChronology);
  
  boolean equals(Object paramObject);
  
  int hashCode();
  
  String toString();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\chrono\Chronology.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */