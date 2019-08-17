package java.time.format;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.ValueRange;
import java.util.Locale;
import java.util.Objects;

final class DateTimePrintContext {
  private TemporalAccessor temporal;
  
  private DateTimeFormatter formatter;
  
  private int optional;
  
  DateTimePrintContext(TemporalAccessor paramTemporalAccessor, DateTimeFormatter paramDateTimeFormatter) {
    this.temporal = adjust(paramTemporalAccessor, paramDateTimeFormatter);
    this.formatter = paramDateTimeFormatter;
  }
  
  private static TemporalAccessor adjust(final TemporalAccessor temporal, DateTimeFormatter paramDateTimeFormatter) {
    final ChronoLocalDate effectiveDate;
    Chronology chronology1 = paramDateTimeFormatter.getChronology();
    ZoneId zoneId1 = paramDateTimeFormatter.getZone();
    if (chronology1 == null && zoneId1 == null)
      return paramTemporalAccessor; 
    Chronology chronology2 = (Chronology)paramTemporalAccessor.query(TemporalQueries.chronology());
    ZoneId zoneId2 = (ZoneId)paramTemporalAccessor.query(TemporalQueries.zoneId());
    if (Objects.equals(chronology1, chronology2))
      chronology1 = null; 
    if (Objects.equals(zoneId1, zoneId2))
      zoneId1 = null; 
    if (chronology1 == null && zoneId1 == null)
      return paramTemporalAccessor; 
    final Chronology effectiveChrono = (chronology1 != null) ? chronology1 : chronology2;
    if (zoneId1 != null) {
      if (paramTemporalAccessor.isSupported(ChronoField.INSTANT_SECONDS)) {
        Chronology chronology = (chronology3 != null) ? chronology3 : IsoChronology.INSTANCE;
        return chronology.zonedDateTime(Instant.from(paramTemporalAccessor), zoneId1);
      } 
      if (zoneId1.normalized() instanceof java.time.ZoneOffset && paramTemporalAccessor.isSupported(ChronoField.OFFSET_SECONDS) && paramTemporalAccessor.get(ChronoField.OFFSET_SECONDS) != zoneId1.getRules().getOffset(Instant.EPOCH).getTotalSeconds())
        throw new DateTimeException("Unable to apply override zone '" + zoneId1 + "' because the temporal object being formatted has a different offset but does not represent an instant: " + paramTemporalAccessor); 
    } 
    final ZoneId effectiveZone = (zoneId1 != null) ? zoneId1 : zoneId2;
    if (chronology1 != null) {
      if (paramTemporalAccessor.isSupported(ChronoField.EPOCH_DAY)) {
        chronoLocalDate = chronology3.date(paramTemporalAccessor);
      } else {
        if (chronology1 != IsoChronology.INSTANCE || chronology2 != null)
          for (ChronoField chronoField : ChronoField.values()) {
            if (chronoField.isDateBased() && paramTemporalAccessor.isSupported(chronoField))
              throw new DateTimeException("Unable to apply override chronology '" + chronology1 + "' because the temporal object being formatted contains date fields but does not represent a whole date: " + paramTemporalAccessor); 
          }  
        chronoLocalDate = null;
      } 
    } else {
      chronoLocalDate = null;
    } 
    return new TemporalAccessor() {
        public boolean isSupported(TemporalField param1TemporalField) { return (effectiveDate != null && param1TemporalField.isDateBased()) ? effectiveDate.isSupported(param1TemporalField) : temporal.isSupported(param1TemporalField); }
        
        public ValueRange range(TemporalField param1TemporalField) { return (effectiveDate != null && param1TemporalField.isDateBased()) ? effectiveDate.range(param1TemporalField) : temporal.range(param1TemporalField); }
        
        public long getLong(TemporalField param1TemporalField) { return (effectiveDate != null && param1TemporalField.isDateBased()) ? effectiveDate.getLong(param1TemporalField) : temporal.getLong(param1TemporalField); }
        
        public <R> R query(TemporalQuery<R> param1TemporalQuery) { return (param1TemporalQuery == TemporalQueries.chronology()) ? (R)effectiveChrono : ((param1TemporalQuery == TemporalQueries.zoneId()) ? (R)effectiveZone : ((param1TemporalQuery == TemporalQueries.precision()) ? (R)temporal.query(param1TemporalQuery) : (R)param1TemporalQuery.queryFrom(this))); }
      };
  }
  
  TemporalAccessor getTemporal() { return this.temporal; }
  
  Locale getLocale() { return this.formatter.getLocale(); }
  
  DecimalStyle getDecimalStyle() { return this.formatter.getDecimalStyle(); }
  
  void startOptional() { this.optional++; }
  
  void endOptional() { this.optional--; }
  
  <R> R getValue(TemporalQuery<R> paramTemporalQuery) {
    Object object = this.temporal.query(paramTemporalQuery);
    if (object == null && this.optional == 0)
      throw new DateTimeException("Unable to extract value: " + this.temporal.getClass()); 
    return (R)object;
  }
  
  Long getValue(TemporalField paramTemporalField) {
    try {
      return Long.valueOf(this.temporal.getLong(paramTemporalField));
    } catch (DateTimeException dateTimeException) {
      if (this.optional > 0)
        return null; 
      throw dateTimeException;
    } 
  }
  
  public String toString() { return this.temporal.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\format\DateTimePrintContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */