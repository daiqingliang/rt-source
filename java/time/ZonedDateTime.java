package java.time;

import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
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
import java.time.zone.ZoneOffsetTransition;
import java.time.zone.ZoneRules;
import java.util.List;
import java.util.Objects;

public final class ZonedDateTime extends Object implements Temporal, ChronoZonedDateTime<LocalDate>, Serializable {
  private static final long serialVersionUID = -6260982410461394882L;
  
  private final LocalDateTime dateTime;
  
  private final ZoneOffset offset;
  
  private final ZoneId zone;
  
  public static ZonedDateTime now() { return now(Clock.systemDefaultZone()); }
  
  public static ZonedDateTime now(ZoneId paramZoneId) { return now(Clock.system(paramZoneId)); }
  
  public static ZonedDateTime now(Clock paramClock) {
    Objects.requireNonNull(paramClock, "clock");
    Instant instant = paramClock.instant();
    return ofInstant(instant, paramClock.getZone());
  }
  
  public static ZonedDateTime of(LocalDate paramLocalDate, LocalTime paramLocalTime, ZoneId paramZoneId) { return of(LocalDateTime.of(paramLocalDate, paramLocalTime), paramZoneId); }
  
  public static ZonedDateTime of(LocalDateTime paramLocalDateTime, ZoneId paramZoneId) { return ofLocal(paramLocalDateTime, paramZoneId, null); }
  
  public static ZonedDateTime of(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, ZoneId paramZoneId) {
    LocalDateTime localDateTime = LocalDateTime.of(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7);
    return ofLocal(localDateTime, paramZoneId, null);
  }
  
  public static ZonedDateTime ofLocal(LocalDateTime paramLocalDateTime, ZoneId paramZoneId, ZoneOffset paramZoneOffset) {
    ZoneOffset zoneOffset;
    Objects.requireNonNull(paramLocalDateTime, "localDateTime");
    Objects.requireNonNull(paramZoneId, "zone");
    if (paramZoneId instanceof ZoneOffset)
      return new ZonedDateTime(paramLocalDateTime, (ZoneOffset)paramZoneId, paramZoneId); 
    ZoneRules zoneRules = paramZoneId.getRules();
    List list = zoneRules.getValidOffsets(paramLocalDateTime);
    if (list.size() == 1) {
      zoneOffset = (ZoneOffset)list.get(0);
    } else if (list.size() == 0) {
      ZoneOffsetTransition zoneOffsetTransition = zoneRules.getTransition(paramLocalDateTime);
      paramLocalDateTime = paramLocalDateTime.plusSeconds(zoneOffsetTransition.getDuration().getSeconds());
      zoneOffset = zoneOffsetTransition.getOffsetAfter();
    } else if (paramZoneOffset != null && list.contains(paramZoneOffset)) {
      zoneOffset = paramZoneOffset;
    } else {
      zoneOffset = (ZoneOffset)Objects.requireNonNull(list.get(0), "offset");
    } 
    return new ZonedDateTime(paramLocalDateTime, zoneOffset, paramZoneId);
  }
  
  public static ZonedDateTime ofInstant(Instant paramInstant, ZoneId paramZoneId) {
    Objects.requireNonNull(paramInstant, "instant");
    Objects.requireNonNull(paramZoneId, "zone");
    return create(paramInstant.getEpochSecond(), paramInstant.getNano(), paramZoneId);
  }
  
  public static ZonedDateTime ofInstant(LocalDateTime paramLocalDateTime, ZoneOffset paramZoneOffset, ZoneId paramZoneId) {
    Objects.requireNonNull(paramLocalDateTime, "localDateTime");
    Objects.requireNonNull(paramZoneOffset, "offset");
    Objects.requireNonNull(paramZoneId, "zone");
    return paramZoneId.getRules().isValidOffset(paramLocalDateTime, paramZoneOffset) ? new ZonedDateTime(paramLocalDateTime, paramZoneOffset, paramZoneId) : create(paramLocalDateTime.toEpochSecond(paramZoneOffset), paramLocalDateTime.getNano(), paramZoneId);
  }
  
  private static ZonedDateTime create(long paramLong, int paramInt, ZoneId paramZoneId) {
    ZoneRules zoneRules = paramZoneId.getRules();
    Instant instant = Instant.ofEpochSecond(paramLong, paramInt);
    ZoneOffset zoneOffset = zoneRules.getOffset(instant);
    LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(paramLong, paramInt, zoneOffset);
    return new ZonedDateTime(localDateTime, zoneOffset, paramZoneId);
  }
  
  public static ZonedDateTime ofStrict(LocalDateTime paramLocalDateTime, ZoneOffset paramZoneOffset, ZoneId paramZoneId) {
    Objects.requireNonNull(paramLocalDateTime, "localDateTime");
    Objects.requireNonNull(paramZoneOffset, "offset");
    Objects.requireNonNull(paramZoneId, "zone");
    ZoneRules zoneRules = paramZoneId.getRules();
    if (!zoneRules.isValidOffset(paramLocalDateTime, paramZoneOffset)) {
      ZoneOffsetTransition zoneOffsetTransition = zoneRules.getTransition(paramLocalDateTime);
      if (zoneOffsetTransition != null && zoneOffsetTransition.isGap())
        throw new DateTimeException("LocalDateTime '" + paramLocalDateTime + "' does not exist in zone '" + paramZoneId + "' due to a gap in the local time-line, typically caused by daylight savings"); 
      throw new DateTimeException("ZoneOffset '" + paramZoneOffset + "' is not valid for LocalDateTime '" + paramLocalDateTime + "' in zone '" + paramZoneId + "'");
    } 
    return new ZonedDateTime(paramLocalDateTime, paramZoneOffset, paramZoneId);
  }
  
  private static ZonedDateTime ofLenient(LocalDateTime paramLocalDateTime, ZoneOffset paramZoneOffset, ZoneId paramZoneId) {
    Objects.requireNonNull(paramLocalDateTime, "localDateTime");
    Objects.requireNonNull(paramZoneOffset, "offset");
    Objects.requireNonNull(paramZoneId, "zone");
    if (paramZoneId instanceof ZoneOffset && !paramZoneOffset.equals(paramZoneId))
      throw new IllegalArgumentException("ZoneId must match ZoneOffset"); 
    return new ZonedDateTime(paramLocalDateTime, paramZoneOffset, paramZoneId);
  }
  
  public static ZonedDateTime from(TemporalAccessor paramTemporalAccessor) {
    if (paramTemporalAccessor instanceof ZonedDateTime)
      return (ZonedDateTime)paramTemporalAccessor; 
    try {
      ZoneId zoneId = ZoneId.from(paramTemporalAccessor);
      if (paramTemporalAccessor.isSupported(ChronoField.INSTANT_SECONDS)) {
        long l = paramTemporalAccessor.getLong(ChronoField.INSTANT_SECONDS);
        int i = paramTemporalAccessor.get(ChronoField.NANO_OF_SECOND);
        return create(l, i, zoneId);
      } 
      LocalDate localDate = LocalDate.from(paramTemporalAccessor);
      LocalTime localTime = LocalTime.from(paramTemporalAccessor);
      return of(localDate, localTime, zoneId);
    } catch (DateTimeException dateTimeException) {
      throw new DateTimeException("Unable to obtain ZonedDateTime from TemporalAccessor: " + paramTemporalAccessor + " of type " + paramTemporalAccessor.getClass().getName(), dateTimeException);
    } 
  }
  
  public static ZonedDateTime parse(CharSequence paramCharSequence) { return parse(paramCharSequence, DateTimeFormatter.ISO_ZONED_DATE_TIME); }
  
  public static ZonedDateTime parse(CharSequence paramCharSequence, DateTimeFormatter paramDateTimeFormatter) {
    Objects.requireNonNull(paramDateTimeFormatter, "formatter");
    return (ZonedDateTime)paramDateTimeFormatter.parse(paramCharSequence, ZonedDateTime::from);
  }
  
  private ZonedDateTime(LocalDateTime paramLocalDateTime, ZoneOffset paramZoneOffset, ZoneId paramZoneId) {
    this.dateTime = paramLocalDateTime;
    this.offset = paramZoneOffset;
    this.zone = paramZoneId;
  }
  
  private ZonedDateTime resolveLocal(LocalDateTime paramLocalDateTime) { return ofLocal(paramLocalDateTime, this.zone, this.offset); }
  
  private ZonedDateTime resolveInstant(LocalDateTime paramLocalDateTime) { return ofInstant(paramLocalDateTime, this.offset, this.zone); }
  
  private ZonedDateTime resolveOffset(ZoneOffset paramZoneOffset) { return (!paramZoneOffset.equals(this.offset) && this.zone.getRules().isValidOffset(this.dateTime, paramZoneOffset)) ? new ZonedDateTime(this.dateTime, paramZoneOffset, this.zone) : this; }
  
  public boolean isSupported(TemporalField paramTemporalField) { return (paramTemporalField instanceof ChronoField || (paramTemporalField != null && paramTemporalField.isSupportedBy(this))); }
  
  public boolean isSupported(TemporalUnit paramTemporalUnit) { return super.isSupported(paramTemporalUnit); }
  
  public ValueRange range(TemporalField paramTemporalField) { return (paramTemporalField instanceof ChronoField) ? ((paramTemporalField == ChronoField.INSTANT_SECONDS || paramTemporalField == ChronoField.OFFSET_SECONDS) ? paramTemporalField.range() : this.dateTime.range(paramTemporalField)) : paramTemporalField.rangeRefinedBy(this); }
  
  public int get(TemporalField paramTemporalField) {
    if (paramTemporalField instanceof ChronoField) {
      switch ((ChronoField)paramTemporalField) {
        case INSTANT_SECONDS:
          throw new UnsupportedTemporalTypeException("Invalid field 'InstantSeconds' for get() method, use getLong() instead");
        case OFFSET_SECONDS:
          return getOffset().getTotalSeconds();
      } 
      return this.dateTime.get(paramTemporalField);
    } 
    return super.get(paramTemporalField);
  }
  
  public long getLong(TemporalField paramTemporalField) {
    if (paramTemporalField instanceof ChronoField) {
      switch ((ChronoField)paramTemporalField) {
        case INSTANT_SECONDS:
          return toEpochSecond();
        case OFFSET_SECONDS:
          return getOffset().getTotalSeconds();
      } 
      return this.dateTime.getLong(paramTemporalField);
    } 
    return paramTemporalField.getFrom(this);
  }
  
  public ZoneOffset getOffset() { return this.offset; }
  
  public ZonedDateTime withEarlierOffsetAtOverlap() {
    ZoneOffsetTransition zoneOffsetTransition = getZone().getRules().getTransition(this.dateTime);
    if (zoneOffsetTransition != null && zoneOffsetTransition.isOverlap()) {
      ZoneOffset zoneOffset = zoneOffsetTransition.getOffsetBefore();
      if (!zoneOffset.equals(this.offset))
        return new ZonedDateTime(this.dateTime, zoneOffset, this.zone); 
    } 
    return this;
  }
  
  public ZonedDateTime withLaterOffsetAtOverlap() {
    ZoneOffsetTransition zoneOffsetTransition = getZone().getRules().getTransition(toLocalDateTime());
    if (zoneOffsetTransition != null) {
      ZoneOffset zoneOffset = zoneOffsetTransition.getOffsetAfter();
      if (!zoneOffset.equals(this.offset))
        return new ZonedDateTime(this.dateTime, zoneOffset, this.zone); 
    } 
    return this;
  }
  
  public ZoneId getZone() { return this.zone; }
  
  public ZonedDateTime withZoneSameLocal(ZoneId paramZoneId) {
    Objects.requireNonNull(paramZoneId, "zone");
    return this.zone.equals(paramZoneId) ? this : ofLocal(this.dateTime, paramZoneId, this.offset);
  }
  
  public ZonedDateTime withZoneSameInstant(ZoneId paramZoneId) {
    Objects.requireNonNull(paramZoneId, "zone");
    return this.zone.equals(paramZoneId) ? this : create(this.dateTime.toEpochSecond(this.offset), this.dateTime.getNano(), paramZoneId);
  }
  
  public ZonedDateTime withFixedOffsetZone() { return this.zone.equals(this.offset) ? this : new ZonedDateTime(this.dateTime, this.offset, this.offset); }
  
  public LocalDateTime toLocalDateTime() { return this.dateTime; }
  
  public LocalDate toLocalDate() { return this.dateTime.toLocalDate(); }
  
  public int getYear() { return this.dateTime.getYear(); }
  
  public int getMonthValue() { return this.dateTime.getMonthValue(); }
  
  public Month getMonth() { return this.dateTime.getMonth(); }
  
  public int getDayOfMonth() { return this.dateTime.getDayOfMonth(); }
  
  public int getDayOfYear() { return this.dateTime.getDayOfYear(); }
  
  public DayOfWeek getDayOfWeek() { return this.dateTime.getDayOfWeek(); }
  
  public LocalTime toLocalTime() { return this.dateTime.toLocalTime(); }
  
  public int getHour() { return this.dateTime.getHour(); }
  
  public int getMinute() { return this.dateTime.getMinute(); }
  
  public int getSecond() { return this.dateTime.getSecond(); }
  
  public int getNano() { return this.dateTime.getNano(); }
  
  public ZonedDateTime with(TemporalAdjuster paramTemporalAdjuster) {
    if (paramTemporalAdjuster instanceof LocalDate)
      return resolveLocal(LocalDateTime.of((LocalDate)paramTemporalAdjuster, this.dateTime.toLocalTime())); 
    if (paramTemporalAdjuster instanceof LocalTime)
      return resolveLocal(LocalDateTime.of(this.dateTime.toLocalDate(), (LocalTime)paramTemporalAdjuster)); 
    if (paramTemporalAdjuster instanceof LocalDateTime)
      return resolveLocal((LocalDateTime)paramTemporalAdjuster); 
    if (paramTemporalAdjuster instanceof OffsetDateTime) {
      OffsetDateTime offsetDateTime = (OffsetDateTime)paramTemporalAdjuster;
      return ofLocal(offsetDateTime.toLocalDateTime(), this.zone, offsetDateTime.getOffset());
    } 
    if (paramTemporalAdjuster instanceof Instant) {
      Instant instant = (Instant)paramTemporalAdjuster;
      return create(instant.getEpochSecond(), instant.getNano(), this.zone);
    } 
    return (paramTemporalAdjuster instanceof ZoneOffset) ? resolveOffset((ZoneOffset)paramTemporalAdjuster) : (ZonedDateTime)paramTemporalAdjuster.adjustInto(this);
  }
  
  public ZonedDateTime with(TemporalField paramTemporalField, long paramLong) {
    if (paramTemporalField instanceof ChronoField) {
      ZoneOffset zoneOffset;
      ChronoField chronoField = (ChronoField)paramTemporalField;
      switch (chronoField) {
        case INSTANT_SECONDS:
          return create(paramLong, getNano(), this.zone);
        case OFFSET_SECONDS:
          zoneOffset = ZoneOffset.ofTotalSeconds(chronoField.checkValidIntValue(paramLong));
          return resolveOffset(zoneOffset);
      } 
      return resolveLocal(this.dateTime.with(paramTemporalField, paramLong));
    } 
    return (ZonedDateTime)paramTemporalField.adjustInto(this, paramLong);
  }
  
  public ZonedDateTime withYear(int paramInt) { return resolveLocal(this.dateTime.withYear(paramInt)); }
  
  public ZonedDateTime withMonth(int paramInt) { return resolveLocal(this.dateTime.withMonth(paramInt)); }
  
  public ZonedDateTime withDayOfMonth(int paramInt) { return resolveLocal(this.dateTime.withDayOfMonth(paramInt)); }
  
  public ZonedDateTime withDayOfYear(int paramInt) { return resolveLocal(this.dateTime.withDayOfYear(paramInt)); }
  
  public ZonedDateTime withHour(int paramInt) { return resolveLocal(this.dateTime.withHour(paramInt)); }
  
  public ZonedDateTime withMinute(int paramInt) { return resolveLocal(this.dateTime.withMinute(paramInt)); }
  
  public ZonedDateTime withSecond(int paramInt) { return resolveLocal(this.dateTime.withSecond(paramInt)); }
  
  public ZonedDateTime withNano(int paramInt) { return resolveLocal(this.dateTime.withNano(paramInt)); }
  
  public ZonedDateTime truncatedTo(TemporalUnit paramTemporalUnit) { return resolveLocal(this.dateTime.truncatedTo(paramTemporalUnit)); }
  
  public ZonedDateTime plus(TemporalAmount paramTemporalAmount) {
    if (paramTemporalAmount instanceof Period) {
      Period period = (Period)paramTemporalAmount;
      return resolveLocal(this.dateTime.plus(period));
    } 
    Objects.requireNonNull(paramTemporalAmount, "amountToAdd");
    return (ZonedDateTime)paramTemporalAmount.addTo(this);
  }
  
  public ZonedDateTime plus(long paramLong, TemporalUnit paramTemporalUnit) { return (paramTemporalUnit instanceof java.time.temporal.ChronoUnit) ? (paramTemporalUnit.isDateBased() ? resolveLocal(this.dateTime.plus(paramLong, paramTemporalUnit)) : resolveInstant(this.dateTime.plus(paramLong, paramTemporalUnit))) : (ZonedDateTime)paramTemporalUnit.addTo(this, paramLong); }
  
  public ZonedDateTime plusYears(long paramLong) { return resolveLocal(this.dateTime.plusYears(paramLong)); }
  
  public ZonedDateTime plusMonths(long paramLong) { return resolveLocal(this.dateTime.plusMonths(paramLong)); }
  
  public ZonedDateTime plusWeeks(long paramLong) { return resolveLocal(this.dateTime.plusWeeks(paramLong)); }
  
  public ZonedDateTime plusDays(long paramLong) { return resolveLocal(this.dateTime.plusDays(paramLong)); }
  
  public ZonedDateTime plusHours(long paramLong) { return resolveInstant(this.dateTime.plusHours(paramLong)); }
  
  public ZonedDateTime plusMinutes(long paramLong) { return resolveInstant(this.dateTime.plusMinutes(paramLong)); }
  
  public ZonedDateTime plusSeconds(long paramLong) { return resolveInstant(this.dateTime.plusSeconds(paramLong)); }
  
  public ZonedDateTime plusNanos(long paramLong) { return resolveInstant(this.dateTime.plusNanos(paramLong)); }
  
  public ZonedDateTime minus(TemporalAmount paramTemporalAmount) {
    if (paramTemporalAmount instanceof Period) {
      Period period = (Period)paramTemporalAmount;
      return resolveLocal(this.dateTime.minus(period));
    } 
    Objects.requireNonNull(paramTemporalAmount, "amountToSubtract");
    return (ZonedDateTime)paramTemporalAmount.subtractFrom(this);
  }
  
  public ZonedDateTime minus(long paramLong, TemporalUnit paramTemporalUnit) { return (paramLong == Float.MIN_VALUE) ? plus(Float.MAX_VALUE, paramTemporalUnit).plus(1L, paramTemporalUnit) : plus(-paramLong, paramTemporalUnit); }
  
  public ZonedDateTime minusYears(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusYears(Float.MAX_VALUE).plusYears(1L) : plusYears(-paramLong); }
  
  public ZonedDateTime minusMonths(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusMonths(Float.MAX_VALUE).plusMonths(1L) : plusMonths(-paramLong); }
  
  public ZonedDateTime minusWeeks(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusWeeks(Float.MAX_VALUE).plusWeeks(1L) : plusWeeks(-paramLong); }
  
  public ZonedDateTime minusDays(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusDays(Float.MAX_VALUE).plusDays(1L) : plusDays(-paramLong); }
  
  public ZonedDateTime minusHours(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusHours(Float.MAX_VALUE).plusHours(1L) : plusHours(-paramLong); }
  
  public ZonedDateTime minusMinutes(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusMinutes(Float.MAX_VALUE).plusMinutes(1L) : plusMinutes(-paramLong); }
  
  public ZonedDateTime minusSeconds(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusSeconds(Float.MAX_VALUE).plusSeconds(1L) : plusSeconds(-paramLong); }
  
  public ZonedDateTime minusNanos(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusNanos(Float.MAX_VALUE).plusNanos(1L) : plusNanos(-paramLong); }
  
  public <R> R query(TemporalQuery<R> paramTemporalQuery) { return (paramTemporalQuery == TemporalQueries.localDate()) ? (R)toLocalDate() : (R)super.query(paramTemporalQuery); }
  
  public long until(Temporal paramTemporal, TemporalUnit paramTemporalUnit) {
    ZonedDateTime zonedDateTime = from(paramTemporal);
    if (paramTemporalUnit instanceof java.time.temporal.ChronoUnit) {
      zonedDateTime = zonedDateTime.withZoneSameInstant(this.zone);
      return paramTemporalUnit.isDateBased() ? this.dateTime.until(zonedDateTime.dateTime, paramTemporalUnit) : toOffsetDateTime().until(zonedDateTime.toOffsetDateTime(), paramTemporalUnit);
    } 
    return paramTemporalUnit.between(this, zonedDateTime);
  }
  
  public String format(DateTimeFormatter paramDateTimeFormatter) {
    Objects.requireNonNull(paramDateTimeFormatter, "formatter");
    return paramDateTimeFormatter.format(this);
  }
  
  public OffsetDateTime toOffsetDateTime() { return OffsetDateTime.of(this.dateTime, this.offset); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof ZonedDateTime) {
      ZonedDateTime zonedDateTime = (ZonedDateTime)paramObject;
      return (this.dateTime.equals(zonedDateTime.dateTime) && this.offset.equals(zonedDateTime.offset) && this.zone.equals(zonedDateTime.zone));
    } 
    return false;
  }
  
  public int hashCode() { return this.dateTime.hashCode() ^ this.offset.hashCode() ^ Integer.rotateLeft(this.zone.hashCode(), 3); }
  
  public String toString() {
    String str = this.dateTime.toString() + this.offset.toString();
    if (this.offset != this.zone)
      str = str + '[' + this.zone.toString() + ']'; 
    return str;
  }
  
  private Object writeReplace() { return new Ser((byte)6, this); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Deserialization via serialization delegate"); }
  
  void writeExternal(DataOutput paramDataOutput) throws IOException {
    this.dateTime.writeExternal(paramDataOutput);
    this.offset.writeExternal(paramDataOutput);
    this.zone.write(paramDataOutput);
  }
  
  static ZonedDateTime readExternal(ObjectInput paramObjectInput) throws IOException, ClassNotFoundException {
    LocalDateTime localDateTime = LocalDateTime.readExternal(paramObjectInput);
    ZoneOffset zoneOffset = ZoneOffset.readExternal(paramObjectInput);
    ZoneId zoneId = (ZoneId)Ser.read(paramObjectInput);
    return ofLenient(localDateTime, zoneOffset, zoneId);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\ZonedDateTime.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */