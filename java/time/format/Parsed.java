package java.time.format;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.chrono.Chronology;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

final class Parsed implements TemporalAccessor {
  final Map<TemporalField, Long> fieldValues = new HashMap();
  
  ZoneId zone;
  
  Chronology chrono;
  
  boolean leapSecond;
  
  private ResolverStyle resolverStyle;
  
  private ChronoLocalDate date;
  
  private LocalTime time;
  
  Period excessDays = Period.ZERO;
  
  Parsed copy() {
    Parsed parsed = new Parsed();
    parsed.fieldValues.putAll(this.fieldValues);
    parsed.zone = this.zone;
    parsed.chrono = this.chrono;
    parsed.leapSecond = this.leapSecond;
    return parsed;
  }
  
  public boolean isSupported(TemporalField paramTemporalField) { return (this.fieldValues.containsKey(paramTemporalField) || (this.date != null && this.date.isSupported(paramTemporalField)) || (this.time != null && this.time.isSupported(paramTemporalField))) ? true : ((paramTemporalField != null && !(paramTemporalField instanceof ChronoField) && paramTemporalField.isSupportedBy(this))); }
  
  public long getLong(TemporalField paramTemporalField) {
    Objects.requireNonNull(paramTemporalField, "field");
    Long long = (Long)this.fieldValues.get(paramTemporalField);
    if (long != null)
      return long.longValue(); 
    if (this.date != null && this.date.isSupported(paramTemporalField))
      return this.date.getLong(paramTemporalField); 
    if (this.time != null && this.time.isSupported(paramTemporalField))
      return this.time.getLong(paramTemporalField); 
    if (paramTemporalField instanceof ChronoField)
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField); 
    return paramTemporalField.getFrom(this);
  }
  
  public <R> R query(TemporalQuery<R> paramTemporalQuery) { return (paramTemporalQuery == TemporalQueries.zoneId()) ? (R)this.zone : ((paramTemporalQuery == TemporalQueries.chronology()) ? (R)this.chrono : ((paramTemporalQuery == TemporalQueries.localDate()) ? (R)((this.date != null) ? LocalDate.from(this.date) : null) : ((paramTemporalQuery == TemporalQueries.localTime()) ? (R)this.time : ((paramTemporalQuery == TemporalQueries.zone() || paramTemporalQuery == TemporalQueries.offset()) ? (R)paramTemporalQuery.queryFrom(this) : ((paramTemporalQuery == TemporalQueries.precision()) ? null : (R)paramTemporalQuery.queryFrom(this)))))); }
  
  TemporalAccessor resolve(ResolverStyle paramResolverStyle, Set<TemporalField> paramSet) {
    if (paramSet != null)
      this.fieldValues.keySet().retainAll(paramSet); 
    this.resolverStyle = paramResolverStyle;
    resolveFields();
    resolveTimeLenient();
    crossCheck();
    resolvePeriod();
    resolveFractional();
    resolveInstant();
    return this;
  }
  
  private void resolveFields() {
    resolveInstantFields();
    resolveDateFields();
    resolveTimeFields();
    if (this.fieldValues.size() > 0) {
      byte b = 0;
      label41: while (b < 50) {
        for (Map.Entry entry : this.fieldValues.entrySet()) {
          TemporalField temporalField = (TemporalField)entry.getKey();
          TemporalAccessor temporalAccessor = temporalField.resolve(this.fieldValues, this, this.resolverStyle);
          if (temporalAccessor != null) {
            if (temporalAccessor instanceof ChronoZonedDateTime) {
              ChronoZonedDateTime chronoZonedDateTime = (ChronoZonedDateTime)temporalAccessor;
              if (this.zone == null) {
                this.zone = chronoZonedDateTime.getZone();
              } else if (!this.zone.equals(chronoZonedDateTime.getZone())) {
                throw new DateTimeException("ChronoZonedDateTime must use the effective parsed zone: " + this.zone);
              } 
              temporalAccessor = chronoZonedDateTime.toLocalDateTime();
            } 
            if (temporalAccessor instanceof ChronoLocalDateTime) {
              ChronoLocalDateTime chronoLocalDateTime = (ChronoLocalDateTime)temporalAccessor;
              updateCheckConflict(chronoLocalDateTime.toLocalTime(), Period.ZERO);
              updateCheckConflict(chronoLocalDateTime.toLocalDate());
              b++;
              continue label41;
            } 
            if (temporalAccessor instanceof ChronoLocalDate) {
              updateCheckConflict((ChronoLocalDate)temporalAccessor);
              b++;
              continue label41;
            } 
            if (temporalAccessor instanceof LocalTime) {
              updateCheckConflict((LocalTime)temporalAccessor, Period.ZERO);
              b++;
              continue label41;
            } 
            throw new DateTimeException("Method resolve() can only return ChronoZonedDateTime, ChronoLocalDateTime, ChronoLocalDate or LocalTime");
          } 
          if (!this.fieldValues.containsKey(temporalField))
            b++; 
        } 
      } 
      if (b == 50)
        throw new DateTimeException("One of the parsed fields has an incorrectly implemented resolve method"); 
      if (b > 0) {
        resolveInstantFields();
        resolveDateFields();
        resolveTimeFields();
      } 
    } 
  }
  
  private void updateCheckConflict(TemporalField paramTemporalField1, TemporalField paramTemporalField2, Long paramLong) {
    Long long = (Long)this.fieldValues.put(paramTemporalField2, paramLong);
    if (long != null && long.longValue() != paramLong.longValue())
      throw new DateTimeException("Conflict found: " + paramTemporalField2 + " " + long + " differs from " + paramTemporalField2 + " " + paramLong + " while resolving  " + paramTemporalField1); 
  }
  
  private void resolveInstantFields() {
    if (this.fieldValues.containsKey(ChronoField.INSTANT_SECONDS))
      if (this.zone != null) {
        resolveInstantFields0(this.zone);
      } else {
        Long long = (Long)this.fieldValues.get(ChronoField.OFFSET_SECONDS);
        if (long != null) {
          ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(long.intValue());
          resolveInstantFields0(zoneOffset);
        } 
      }  
  }
  
  private void resolveInstantFields0(ZoneId paramZoneId) {
    Instant instant = Instant.ofEpochSecond(((Long)this.fieldValues.remove(ChronoField.INSTANT_SECONDS)).longValue());
    ChronoZonedDateTime chronoZonedDateTime = this.chrono.zonedDateTime(instant, paramZoneId);
    updateCheckConflict(chronoZonedDateTime.toLocalDate());
    updateCheckConflict(ChronoField.INSTANT_SECONDS, ChronoField.SECOND_OF_DAY, Long.valueOf(chronoZonedDateTime.toLocalTime().toSecondOfDay()));
  }
  
  private void resolveDateFields() { updateCheckConflict(this.chrono.resolveDate(this.fieldValues, this.resolverStyle)); }
  
  private void updateCheckConflict(ChronoLocalDate paramChronoLocalDate) {
    if (this.date != null) {
      if (paramChronoLocalDate != null && !this.date.equals(paramChronoLocalDate))
        throw new DateTimeException("Conflict found: Fields resolved to two different dates: " + this.date + " " + paramChronoLocalDate); 
    } else if (paramChronoLocalDate != null) {
      if (!this.chrono.equals(paramChronoLocalDate.getChronology()))
        throw new DateTimeException("ChronoLocalDate must use the effective parsed chronology: " + this.chrono); 
      this.date = paramChronoLocalDate;
    } 
  }
  
  private void resolveTimeFields() {
    if (this.fieldValues.containsKey(ChronoField.CLOCK_HOUR_OF_DAY)) {
      long l = ((Long)this.fieldValues.remove(ChronoField.CLOCK_HOUR_OF_DAY)).longValue();
      if (this.resolverStyle == ResolverStyle.STRICT || (this.resolverStyle == ResolverStyle.SMART && l != 0L))
        ChronoField.CLOCK_HOUR_OF_DAY.checkValidValue(l); 
      updateCheckConflict(ChronoField.CLOCK_HOUR_OF_DAY, ChronoField.HOUR_OF_DAY, Long.valueOf((l == 24L) ? 0L : l));
    } 
    if (this.fieldValues.containsKey(ChronoField.CLOCK_HOUR_OF_AMPM)) {
      long l = ((Long)this.fieldValues.remove(ChronoField.CLOCK_HOUR_OF_AMPM)).longValue();
      if (this.resolverStyle == ResolverStyle.STRICT || (this.resolverStyle == ResolverStyle.SMART && l != 0L))
        ChronoField.CLOCK_HOUR_OF_AMPM.checkValidValue(l); 
      updateCheckConflict(ChronoField.CLOCK_HOUR_OF_AMPM, ChronoField.HOUR_OF_AMPM, Long.valueOf((l == 12L) ? 0L : l));
    } 
    if (this.fieldValues.containsKey(ChronoField.AMPM_OF_DAY) && this.fieldValues.containsKey(ChronoField.HOUR_OF_AMPM)) {
      long l1 = ((Long)this.fieldValues.remove(ChronoField.AMPM_OF_DAY)).longValue();
      long l2 = ((Long)this.fieldValues.remove(ChronoField.HOUR_OF_AMPM)).longValue();
      if (this.resolverStyle == ResolverStyle.LENIENT) {
        updateCheckConflict(ChronoField.AMPM_OF_DAY, ChronoField.HOUR_OF_DAY, Long.valueOf(Math.addExact(Math.multiplyExact(l1, 12L), l2)));
      } else {
        ChronoField.AMPM_OF_DAY.checkValidValue(l1);
        ChronoField.HOUR_OF_AMPM.checkValidValue(l1);
        updateCheckConflict(ChronoField.AMPM_OF_DAY, ChronoField.HOUR_OF_DAY, Long.valueOf(l1 * 12L + l2));
      } 
    } 
    if (this.fieldValues.containsKey(ChronoField.NANO_OF_DAY)) {
      long l = ((Long)this.fieldValues.remove(ChronoField.NANO_OF_DAY)).longValue();
      if (this.resolverStyle != ResolverStyle.LENIENT)
        ChronoField.NANO_OF_DAY.checkValidValue(l); 
      updateCheckConflict(ChronoField.NANO_OF_DAY, ChronoField.HOUR_OF_DAY, Long.valueOf(l / 3600000000000L));
      updateCheckConflict(ChronoField.NANO_OF_DAY, ChronoField.MINUTE_OF_HOUR, Long.valueOf(l / 60000000000L % 60L));
      updateCheckConflict(ChronoField.NANO_OF_DAY, ChronoField.SECOND_OF_MINUTE, Long.valueOf(l / 1000000000L % 60L));
      updateCheckConflict(ChronoField.NANO_OF_DAY, ChronoField.NANO_OF_SECOND, Long.valueOf(l % 1000000000L));
    } 
    if (this.fieldValues.containsKey(ChronoField.MICRO_OF_DAY)) {
      long l = ((Long)this.fieldValues.remove(ChronoField.MICRO_OF_DAY)).longValue();
      if (this.resolverStyle != ResolverStyle.LENIENT)
        ChronoField.MICRO_OF_DAY.checkValidValue(l); 
      updateCheckConflict(ChronoField.MICRO_OF_DAY, ChronoField.SECOND_OF_DAY, Long.valueOf(l / 1000000L));
      updateCheckConflict(ChronoField.MICRO_OF_DAY, ChronoField.MICRO_OF_SECOND, Long.valueOf(l % 1000000L));
    } 
    if (this.fieldValues.containsKey(ChronoField.MILLI_OF_DAY)) {
      long l = ((Long)this.fieldValues.remove(ChronoField.MILLI_OF_DAY)).longValue();
      if (this.resolverStyle != ResolverStyle.LENIENT)
        ChronoField.MILLI_OF_DAY.checkValidValue(l); 
      updateCheckConflict(ChronoField.MILLI_OF_DAY, ChronoField.SECOND_OF_DAY, Long.valueOf(l / 1000L));
      updateCheckConflict(ChronoField.MILLI_OF_DAY, ChronoField.MILLI_OF_SECOND, Long.valueOf(l % 1000L));
    } 
    if (this.fieldValues.containsKey(ChronoField.SECOND_OF_DAY)) {
      long l = ((Long)this.fieldValues.remove(ChronoField.SECOND_OF_DAY)).longValue();
      if (this.resolverStyle != ResolverStyle.LENIENT)
        ChronoField.SECOND_OF_DAY.checkValidValue(l); 
      updateCheckConflict(ChronoField.SECOND_OF_DAY, ChronoField.HOUR_OF_DAY, Long.valueOf(l / 3600L));
      updateCheckConflict(ChronoField.SECOND_OF_DAY, ChronoField.MINUTE_OF_HOUR, Long.valueOf(l / 60L % 60L));
      updateCheckConflict(ChronoField.SECOND_OF_DAY, ChronoField.SECOND_OF_MINUTE, Long.valueOf(l % 60L));
    } 
    if (this.fieldValues.containsKey(ChronoField.MINUTE_OF_DAY)) {
      long l = ((Long)this.fieldValues.remove(ChronoField.MINUTE_OF_DAY)).longValue();
      if (this.resolverStyle != ResolverStyle.LENIENT)
        ChronoField.MINUTE_OF_DAY.checkValidValue(l); 
      updateCheckConflict(ChronoField.MINUTE_OF_DAY, ChronoField.HOUR_OF_DAY, Long.valueOf(l / 60L));
      updateCheckConflict(ChronoField.MINUTE_OF_DAY, ChronoField.MINUTE_OF_HOUR, Long.valueOf(l % 60L));
    } 
    if (this.fieldValues.containsKey(ChronoField.NANO_OF_SECOND)) {
      long l = ((Long)this.fieldValues.get(ChronoField.NANO_OF_SECOND)).longValue();
      if (this.resolverStyle != ResolverStyle.LENIENT)
        ChronoField.NANO_OF_SECOND.checkValidValue(l); 
      if (this.fieldValues.containsKey(ChronoField.MICRO_OF_SECOND)) {
        long l1 = ((Long)this.fieldValues.remove(ChronoField.MICRO_OF_SECOND)).longValue();
        if (this.resolverStyle != ResolverStyle.LENIENT)
          ChronoField.MICRO_OF_SECOND.checkValidValue(l1); 
        l = l1 * 1000L + l % 1000L;
        updateCheckConflict(ChronoField.MICRO_OF_SECOND, ChronoField.NANO_OF_SECOND, Long.valueOf(l));
      } 
      if (this.fieldValues.containsKey(ChronoField.MILLI_OF_SECOND)) {
        long l1 = ((Long)this.fieldValues.remove(ChronoField.MILLI_OF_SECOND)).longValue();
        if (this.resolverStyle != ResolverStyle.LENIENT)
          ChronoField.MILLI_OF_SECOND.checkValidValue(l1); 
        updateCheckConflict(ChronoField.MILLI_OF_SECOND, ChronoField.NANO_OF_SECOND, Long.valueOf(l1 * 1000000L + l % 1000000L));
      } 
    } 
    if (this.fieldValues.containsKey(ChronoField.HOUR_OF_DAY) && this.fieldValues.containsKey(ChronoField.MINUTE_OF_HOUR) && this.fieldValues.containsKey(ChronoField.SECOND_OF_MINUTE) && this.fieldValues.containsKey(ChronoField.NANO_OF_SECOND)) {
      long l1 = ((Long)this.fieldValues.remove(ChronoField.HOUR_OF_DAY)).longValue();
      long l2 = ((Long)this.fieldValues.remove(ChronoField.MINUTE_OF_HOUR)).longValue();
      long l3 = ((Long)this.fieldValues.remove(ChronoField.SECOND_OF_MINUTE)).longValue();
      long l4 = ((Long)this.fieldValues.remove(ChronoField.NANO_OF_SECOND)).longValue();
      resolveTime(l1, l2, l3, l4);
    } 
  }
  
  private void resolveTimeLenient() {
    if (this.time == null) {
      if (this.fieldValues.containsKey(ChronoField.MILLI_OF_SECOND)) {
        long l = ((Long)this.fieldValues.remove(ChronoField.MILLI_OF_SECOND)).longValue();
        if (this.fieldValues.containsKey(ChronoField.MICRO_OF_SECOND)) {
          long l1 = l * 1000L + ((Long)this.fieldValues.get(ChronoField.MICRO_OF_SECOND)).longValue() % 1000L;
          updateCheckConflict(ChronoField.MILLI_OF_SECOND, ChronoField.MICRO_OF_SECOND, Long.valueOf(l1));
          this.fieldValues.remove(ChronoField.MICRO_OF_SECOND);
          this.fieldValues.put(ChronoField.NANO_OF_SECOND, Long.valueOf(l1 * 1000L));
        } else {
          this.fieldValues.put(ChronoField.NANO_OF_SECOND, Long.valueOf(l * 1000000L));
        } 
      } else if (this.fieldValues.containsKey(ChronoField.MICRO_OF_SECOND)) {
        long l = ((Long)this.fieldValues.remove(ChronoField.MICRO_OF_SECOND)).longValue();
        this.fieldValues.put(ChronoField.NANO_OF_SECOND, Long.valueOf(l * 1000L));
      } 
      Long long = (Long)this.fieldValues.get(ChronoField.HOUR_OF_DAY);
      if (long != null) {
        Long long1 = (Long)this.fieldValues.get(ChronoField.MINUTE_OF_HOUR);
        Long long2 = (Long)this.fieldValues.get(ChronoField.SECOND_OF_MINUTE);
        Long long3 = (Long)this.fieldValues.get(ChronoField.NANO_OF_SECOND);
        if ((long1 == null && (long2 != null || long3 != null)) || (long1 != null && long2 == null && long3 != null))
          return; 
        long l1 = (long1 != null) ? long1.longValue() : 0L;
        long l2 = (long2 != null) ? long2.longValue() : 0L;
        long l3 = (long3 != null) ? long3.longValue() : 0L;
        resolveTime(long.longValue(), l1, l2, l3);
        this.fieldValues.remove(ChronoField.HOUR_OF_DAY);
        this.fieldValues.remove(ChronoField.MINUTE_OF_HOUR);
        this.fieldValues.remove(ChronoField.SECOND_OF_MINUTE);
        this.fieldValues.remove(ChronoField.NANO_OF_SECOND);
      } 
    } 
    if (this.resolverStyle != ResolverStyle.LENIENT && this.fieldValues.size() > 0)
      for (Map.Entry entry : this.fieldValues.entrySet()) {
        TemporalField temporalField = (TemporalField)entry.getKey();
        if (temporalField instanceof ChronoField && temporalField.isTimeBased())
          ((ChronoField)temporalField).checkValidValue(((Long)entry.getValue()).longValue()); 
      }  
  }
  
  private void resolveTime(long paramLong1, long paramLong2, long paramLong3, long paramLong4) {
    if (this.resolverStyle == ResolverStyle.LENIENT) {
      long l1 = Math.multiplyExact(paramLong1, 3600000000000L);
      l1 = Math.addExact(l1, Math.multiplyExact(paramLong2, 60000000000L));
      l1 = Math.addExact(l1, Math.multiplyExact(paramLong3, 1000000000L));
      l1 = Math.addExact(l1, paramLong4);
      int i = (int)Math.floorDiv(l1, 86400000000000L);
      long l2 = Math.floorMod(l1, 86400000000000L);
      updateCheckConflict(LocalTime.ofNanoOfDay(l2), Period.ofDays(i));
    } else {
      int i = ChronoField.MINUTE_OF_HOUR.checkValidIntValue(paramLong2);
      int j = ChronoField.NANO_OF_SECOND.checkValidIntValue(paramLong4);
      if (this.resolverStyle == ResolverStyle.SMART && paramLong1 == 24L && i == 0 && paramLong3 == 0L && j == 0) {
        updateCheckConflict(LocalTime.MIDNIGHT, Period.ofDays(1));
      } else {
        int k = ChronoField.HOUR_OF_DAY.checkValidIntValue(paramLong1);
        int m = ChronoField.SECOND_OF_MINUTE.checkValidIntValue(paramLong3);
        updateCheckConflict(LocalTime.of(k, i, m, j), Period.ZERO);
      } 
    } 
  }
  
  private void resolvePeriod() {
    if (this.date != null && this.time != null && !this.excessDays.isZero()) {
      this.date = this.date.plus(this.excessDays);
      this.excessDays = Period.ZERO;
    } 
  }
  
  private void resolveFractional() {
    if (this.time == null && (this.fieldValues.containsKey(ChronoField.INSTANT_SECONDS) || this.fieldValues.containsKey(ChronoField.SECOND_OF_DAY) || this.fieldValues.containsKey(ChronoField.SECOND_OF_MINUTE)))
      if (this.fieldValues.containsKey(ChronoField.NANO_OF_SECOND)) {
        long l = ((Long)this.fieldValues.get(ChronoField.NANO_OF_SECOND)).longValue();
        this.fieldValues.put(ChronoField.MICRO_OF_SECOND, Long.valueOf(l / 1000L));
        this.fieldValues.put(ChronoField.MILLI_OF_SECOND, Long.valueOf(l / 1000000L));
      } else {
        this.fieldValues.put(ChronoField.NANO_OF_SECOND, Long.valueOf(0L));
        this.fieldValues.put(ChronoField.MICRO_OF_SECOND, Long.valueOf(0L));
        this.fieldValues.put(ChronoField.MILLI_OF_SECOND, Long.valueOf(0L));
      }  
  }
  
  private void resolveInstant() {
    if (this.date != null && this.time != null)
      if (this.zone != null) {
        long l = this.date.atTime(this.time).atZone(this.zone).getLong(ChronoField.INSTANT_SECONDS);
        this.fieldValues.put(ChronoField.INSTANT_SECONDS, Long.valueOf(l));
      } else {
        Long long = (Long)this.fieldValues.get(ChronoField.OFFSET_SECONDS);
        if (long != null) {
          ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(long.intValue());
          long l = this.date.atTime(this.time).atZone(zoneOffset).getLong(ChronoField.INSTANT_SECONDS);
          this.fieldValues.put(ChronoField.INSTANT_SECONDS, Long.valueOf(l));
        } 
      }  
  }
  
  private void updateCheckConflict(LocalTime paramLocalTime, Period paramPeriod) {
    if (this.time != null) {
      if (!this.time.equals(paramLocalTime))
        throw new DateTimeException("Conflict found: Fields resolved to different times: " + this.time + " " + paramLocalTime); 
      if (!this.excessDays.isZero() && !paramPeriod.isZero() && !this.excessDays.equals(paramPeriod))
        throw new DateTimeException("Conflict found: Fields resolved to different excess periods: " + this.excessDays + " " + paramPeriod); 
      this.excessDays = paramPeriod;
    } else {
      this.time = paramLocalTime;
      this.excessDays = paramPeriod;
    } 
  }
  
  private void crossCheck() {
    if (this.date != null)
      crossCheck(this.date); 
    if (this.time != null) {
      crossCheck(this.time);
      if (this.date != null && this.fieldValues.size() > 0)
        crossCheck(this.date.atTime(this.time)); 
    } 
  }
  
  private void crossCheck(TemporalAccessor paramTemporalAccessor) {
    Iterator iterator = this.fieldValues.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry entry = (Map.Entry)iterator.next();
      TemporalField temporalField = (TemporalField)entry.getKey();
      if (paramTemporalAccessor.isSupported(temporalField)) {
        long l1;
        try {
          l1 = paramTemporalAccessor.getLong(temporalField);
        } catch (RuntimeException runtimeException) {
          continue;
        } 
        long l2 = ((Long)entry.getValue()).longValue();
        if (l1 != l2)
          throw new DateTimeException("Conflict found: Field " + temporalField + " " + l1 + " differs from " + temporalField + " " + l2 + " derived from " + paramTemporalAccessor); 
        iterator.remove();
      } 
    } 
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(64);
    stringBuilder.append(this.fieldValues).append(',').append(this.chrono);
    if (this.zone != null)
      stringBuilder.append(',').append(this.zone); 
    if (this.date != null || this.time != null) {
      stringBuilder.append(" resolved to ");
      if (this.date != null) {
        stringBuilder.append(this.date);
        if (this.time != null)
          stringBuilder.append('T').append(this.time); 
      } else {
        stringBuilder.append(this.time);
      } 
    } 
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\format\Parsed.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */