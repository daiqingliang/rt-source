package java.time;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.time.chrono.IsoChronology;
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
import java.time.zone.ZoneRules;
import java.util.Comparator;
import java.util.Objects;

public final class OffsetDateTime extends Object implements Temporal, TemporalAdjuster, Comparable<OffsetDateTime>, Serializable {
  public static final OffsetDateTime MIN = LocalDateTime.MIN.atOffset(ZoneOffset.MAX);
  
  public static final OffsetDateTime MAX = LocalDateTime.MAX.atOffset(ZoneOffset.MIN);
  
  private static final long serialVersionUID = 2287754244819255394L;
  
  private final LocalDateTime dateTime;
  
  private final ZoneOffset offset;
  
  public static Comparator<OffsetDateTime> timeLineOrder() { return OffsetDateTime::compareInstant; }
  
  private static int compareInstant(OffsetDateTime paramOffsetDateTime1, OffsetDateTime paramOffsetDateTime2) {
    if (paramOffsetDateTime1.getOffset().equals(paramOffsetDateTime2.getOffset()))
      return paramOffsetDateTime1.toLocalDateTime().compareTo(paramOffsetDateTime2.toLocalDateTime()); 
    int i = Long.compare(paramOffsetDateTime1.toEpochSecond(), paramOffsetDateTime2.toEpochSecond());
    if (i == 0)
      i = paramOffsetDateTime1.toLocalTime().getNano() - paramOffsetDateTime2.toLocalTime().getNano(); 
    return i;
  }
  
  public static OffsetDateTime now() { return now(Clock.systemDefaultZone()); }
  
  public static OffsetDateTime now(ZoneId paramZoneId) { return now(Clock.system(paramZoneId)); }
  
  public static OffsetDateTime now(Clock paramClock) {
    Objects.requireNonNull(paramClock, "clock");
    Instant instant = paramClock.instant();
    return ofInstant(instant, paramClock.getZone().getRules().getOffset(instant));
  }
  
  public static OffsetDateTime of(LocalDate paramLocalDate, LocalTime paramLocalTime, ZoneOffset paramZoneOffset) {
    LocalDateTime localDateTime = LocalDateTime.of(paramLocalDate, paramLocalTime);
    return new OffsetDateTime(localDateTime, paramZoneOffset);
  }
  
  public static OffsetDateTime of(LocalDateTime paramLocalDateTime, ZoneOffset paramZoneOffset) { return new OffsetDateTime(paramLocalDateTime, paramZoneOffset); }
  
  public static OffsetDateTime of(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, ZoneOffset paramZoneOffset) {
    LocalDateTime localDateTime = LocalDateTime.of(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7);
    return new OffsetDateTime(localDateTime, paramZoneOffset);
  }
  
  public static OffsetDateTime ofInstant(Instant paramInstant, ZoneId paramZoneId) {
    Objects.requireNonNull(paramInstant, "instant");
    Objects.requireNonNull(paramZoneId, "zone");
    ZoneRules zoneRules = paramZoneId.getRules();
    ZoneOffset zoneOffset = zoneRules.getOffset(paramInstant);
    LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(paramInstant.getEpochSecond(), paramInstant.getNano(), zoneOffset);
    return new OffsetDateTime(localDateTime, zoneOffset);
  }
  
  public static OffsetDateTime from(TemporalAccessor paramTemporalAccessor) {
    if (paramTemporalAccessor instanceof OffsetDateTime)
      return (OffsetDateTime)paramTemporalAccessor; 
    try {
      ZoneOffset zoneOffset = ZoneOffset.from(paramTemporalAccessor);
      LocalDate localDate = (LocalDate)paramTemporalAccessor.query(TemporalQueries.localDate());
      LocalTime localTime = (LocalTime)paramTemporalAccessor.query(TemporalQueries.localTime());
      if (localDate != null && localTime != null)
        return of(localDate, localTime, zoneOffset); 
      Instant instant = Instant.from(paramTemporalAccessor);
      return ofInstant(instant, zoneOffset);
    } catch (DateTimeException dateTimeException) {
      throw new DateTimeException("Unable to obtain OffsetDateTime from TemporalAccessor: " + paramTemporalAccessor + " of type " + paramTemporalAccessor.getClass().getName(), dateTimeException);
    } 
  }
  
  public static OffsetDateTime parse(CharSequence paramCharSequence) { return parse(paramCharSequence, DateTimeFormatter.ISO_OFFSET_DATE_TIME); }
  
  public static OffsetDateTime parse(CharSequence paramCharSequence, DateTimeFormatter paramDateTimeFormatter) {
    Objects.requireNonNull(paramDateTimeFormatter, "formatter");
    return (OffsetDateTime)paramDateTimeFormatter.parse(paramCharSequence, OffsetDateTime::from);
  }
  
  private OffsetDateTime(LocalDateTime paramLocalDateTime, ZoneOffset paramZoneOffset) {
    this.dateTime = (LocalDateTime)Objects.requireNonNull(paramLocalDateTime, "dateTime");
    this.offset = (ZoneOffset)Objects.requireNonNull(paramZoneOffset, "offset");
  }
  
  private OffsetDateTime with(LocalDateTime paramLocalDateTime, ZoneOffset paramZoneOffset) { return (this.dateTime == paramLocalDateTime && this.offset.equals(paramZoneOffset)) ? this : new OffsetDateTime(paramLocalDateTime, paramZoneOffset); }
  
  public boolean isSupported(TemporalField paramTemporalField) { return (paramTemporalField instanceof ChronoField || (paramTemporalField != null && paramTemporalField.isSupportedBy(this))); }
  
  public boolean isSupported(TemporalUnit paramTemporalUnit) { return (paramTemporalUnit instanceof ChronoUnit) ? ((paramTemporalUnit != ChronoUnit.FOREVER)) : ((paramTemporalUnit != null && paramTemporalUnit.isSupportedBy(this))); }
  
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
  
  public OffsetDateTime withOffsetSameLocal(ZoneOffset paramZoneOffset) { return with(this.dateTime, paramZoneOffset); }
  
  public OffsetDateTime withOffsetSameInstant(ZoneOffset paramZoneOffset) {
    if (paramZoneOffset.equals(this.offset))
      return this; 
    int i = paramZoneOffset.getTotalSeconds() - this.offset.getTotalSeconds();
    LocalDateTime localDateTime = this.dateTime.plusSeconds(i);
    return new OffsetDateTime(localDateTime, paramZoneOffset);
  }
  
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
  
  public OffsetDateTime with(TemporalAdjuster paramTemporalAdjuster) { return (paramTemporalAdjuster instanceof LocalDate || paramTemporalAdjuster instanceof LocalTime || paramTemporalAdjuster instanceof LocalDateTime) ? with(this.dateTime.with(paramTemporalAdjuster), this.offset) : ((paramTemporalAdjuster instanceof Instant) ? ofInstant((Instant)paramTemporalAdjuster, this.offset) : ((paramTemporalAdjuster instanceof ZoneOffset) ? with(this.dateTime, (ZoneOffset)paramTemporalAdjuster) : ((paramTemporalAdjuster instanceof OffsetDateTime) ? (OffsetDateTime)paramTemporalAdjuster : (OffsetDateTime)paramTemporalAdjuster.adjustInto(this)))); }
  
  public OffsetDateTime with(TemporalField paramTemporalField, long paramLong) {
    if (paramTemporalField instanceof ChronoField) {
      ChronoField chronoField = (ChronoField)paramTemporalField;
      switch (chronoField) {
        case INSTANT_SECONDS:
          return ofInstant(Instant.ofEpochSecond(paramLong, getNano()), this.offset);
        case OFFSET_SECONDS:
          return with(this.dateTime, ZoneOffset.ofTotalSeconds(chronoField.checkValidIntValue(paramLong)));
      } 
      return with(this.dateTime.with(paramTemporalField, paramLong), this.offset);
    } 
    return (OffsetDateTime)paramTemporalField.adjustInto(this, paramLong);
  }
  
  public OffsetDateTime withYear(int paramInt) { return with(this.dateTime.withYear(paramInt), this.offset); }
  
  public OffsetDateTime withMonth(int paramInt) { return with(this.dateTime.withMonth(paramInt), this.offset); }
  
  public OffsetDateTime withDayOfMonth(int paramInt) { return with(this.dateTime.withDayOfMonth(paramInt), this.offset); }
  
  public OffsetDateTime withDayOfYear(int paramInt) { return with(this.dateTime.withDayOfYear(paramInt), this.offset); }
  
  public OffsetDateTime withHour(int paramInt) { return with(this.dateTime.withHour(paramInt), this.offset); }
  
  public OffsetDateTime withMinute(int paramInt) { return with(this.dateTime.withMinute(paramInt), this.offset); }
  
  public OffsetDateTime withSecond(int paramInt) { return with(this.dateTime.withSecond(paramInt), this.offset); }
  
  public OffsetDateTime withNano(int paramInt) { return with(this.dateTime.withNano(paramInt), this.offset); }
  
  public OffsetDateTime truncatedTo(TemporalUnit paramTemporalUnit) { return with(this.dateTime.truncatedTo(paramTemporalUnit), this.offset); }
  
  public OffsetDateTime plus(TemporalAmount paramTemporalAmount) { return (OffsetDateTime)paramTemporalAmount.addTo(this); }
  
  public OffsetDateTime plus(long paramLong, TemporalUnit paramTemporalUnit) { return (paramTemporalUnit instanceof ChronoUnit) ? with(this.dateTime.plus(paramLong, paramTemporalUnit), this.offset) : (OffsetDateTime)paramTemporalUnit.addTo(this, paramLong); }
  
  public OffsetDateTime plusYears(long paramLong) { return with(this.dateTime.plusYears(paramLong), this.offset); }
  
  public OffsetDateTime plusMonths(long paramLong) { return with(this.dateTime.plusMonths(paramLong), this.offset); }
  
  public OffsetDateTime plusWeeks(long paramLong) { return with(this.dateTime.plusWeeks(paramLong), this.offset); }
  
  public OffsetDateTime plusDays(long paramLong) { return with(this.dateTime.plusDays(paramLong), this.offset); }
  
  public OffsetDateTime plusHours(long paramLong) { return with(this.dateTime.plusHours(paramLong), this.offset); }
  
  public OffsetDateTime plusMinutes(long paramLong) { return with(this.dateTime.plusMinutes(paramLong), this.offset); }
  
  public OffsetDateTime plusSeconds(long paramLong) { return with(this.dateTime.plusSeconds(paramLong), this.offset); }
  
  public OffsetDateTime plusNanos(long paramLong) { return with(this.dateTime.plusNanos(paramLong), this.offset); }
  
  public OffsetDateTime minus(TemporalAmount paramTemporalAmount) { return (OffsetDateTime)paramTemporalAmount.subtractFrom(this); }
  
  public OffsetDateTime minus(long paramLong, TemporalUnit paramTemporalUnit) { return (paramLong == Float.MIN_VALUE) ? plus(Float.MAX_VALUE, paramTemporalUnit).plus(1L, paramTemporalUnit) : plus(-paramLong, paramTemporalUnit); }
  
  public OffsetDateTime minusYears(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusYears(Float.MAX_VALUE).plusYears(1L) : plusYears(-paramLong); }
  
  public OffsetDateTime minusMonths(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusMonths(Float.MAX_VALUE).plusMonths(1L) : plusMonths(-paramLong); }
  
  public OffsetDateTime minusWeeks(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusWeeks(Float.MAX_VALUE).plusWeeks(1L) : plusWeeks(-paramLong); }
  
  public OffsetDateTime minusDays(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusDays(Float.MAX_VALUE).plusDays(1L) : plusDays(-paramLong); }
  
  public OffsetDateTime minusHours(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusHours(Float.MAX_VALUE).plusHours(1L) : plusHours(-paramLong); }
  
  public OffsetDateTime minusMinutes(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusMinutes(Float.MAX_VALUE).plusMinutes(1L) : plusMinutes(-paramLong); }
  
  public OffsetDateTime minusSeconds(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusSeconds(Float.MAX_VALUE).plusSeconds(1L) : plusSeconds(-paramLong); }
  
  public OffsetDateTime minusNanos(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusNanos(Float.MAX_VALUE).plusNanos(1L) : plusNanos(-paramLong); }
  
  public <R> R query(TemporalQuery<R> paramTemporalQuery) { return (paramTemporalQuery == TemporalQueries.offset() || paramTemporalQuery == TemporalQueries.zone()) ? (R)getOffset() : ((paramTemporalQuery == TemporalQueries.zoneId()) ? null : ((paramTemporalQuery == TemporalQueries.localDate()) ? (R)toLocalDate() : ((paramTemporalQuery == TemporalQueries.localTime()) ? (R)toLocalTime() : ((paramTemporalQuery == TemporalQueries.chronology()) ? (R)IsoChronology.INSTANCE : ((paramTemporalQuery == TemporalQueries.precision()) ? (R)ChronoUnit.NANOS : (R)paramTemporalQuery.queryFrom(this)))))); }
  
  public Temporal adjustInto(Temporal paramTemporal) { return paramTemporal.with(ChronoField.EPOCH_DAY, toLocalDate().toEpochDay()).with(ChronoField.NANO_OF_DAY, toLocalTime().toNanoOfDay()).with(ChronoField.OFFSET_SECONDS, getOffset().getTotalSeconds()); }
  
  public long until(Temporal paramTemporal, TemporalUnit paramTemporalUnit) {
    OffsetDateTime offsetDateTime = from(paramTemporal);
    if (paramTemporalUnit instanceof ChronoUnit) {
      offsetDateTime = offsetDateTime.withOffsetSameInstant(this.offset);
      return this.dateTime.until(offsetDateTime.dateTime, paramTemporalUnit);
    } 
    return paramTemporalUnit.between(this, offsetDateTime);
  }
  
  public String format(DateTimeFormatter paramDateTimeFormatter) {
    Objects.requireNonNull(paramDateTimeFormatter, "formatter");
    return paramDateTimeFormatter.format(this);
  }
  
  public ZonedDateTime atZoneSameInstant(ZoneId paramZoneId) { return ZonedDateTime.ofInstant(this.dateTime, this.offset, paramZoneId); }
  
  public ZonedDateTime atZoneSimilarLocal(ZoneId paramZoneId) { return ZonedDateTime.ofLocal(this.dateTime, paramZoneId, this.offset); }
  
  public OffsetTime toOffsetTime() { return OffsetTime.of(this.dateTime.toLocalTime(), this.offset); }
  
  public ZonedDateTime toZonedDateTime() { return ZonedDateTime.of(this.dateTime, this.offset); }
  
  public Instant toInstant() { return this.dateTime.toInstant(this.offset); }
  
  public long toEpochSecond() { return this.dateTime.toEpochSecond(this.offset); }
  
  public int compareTo(OffsetDateTime paramOffsetDateTime) {
    int i = compareInstant(this, paramOffsetDateTime);
    if (i == 0)
      i = toLocalDateTime().compareTo(paramOffsetDateTime.toLocalDateTime()); 
    return i;
  }
  
  public boolean isAfter(OffsetDateTime paramOffsetDateTime) {
    long l1 = toEpochSecond();
    long l2 = paramOffsetDateTime.toEpochSecond();
    return (l1 > l2 || (l1 == l2 && toLocalTime().getNano() > paramOffsetDateTime.toLocalTime().getNano()));
  }
  
  public boolean isBefore(OffsetDateTime paramOffsetDateTime) {
    long l1 = toEpochSecond();
    long l2 = paramOffsetDateTime.toEpochSecond();
    return (l1 < l2 || (l1 == l2 && toLocalTime().getNano() < paramOffsetDateTime.toLocalTime().getNano()));
  }
  
  public boolean isEqual(OffsetDateTime paramOffsetDateTime) { return (toEpochSecond() == paramOffsetDateTime.toEpochSecond() && toLocalTime().getNano() == paramOffsetDateTime.toLocalTime().getNano()); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof OffsetDateTime) {
      OffsetDateTime offsetDateTime = (OffsetDateTime)paramObject;
      return (this.dateTime.equals(offsetDateTime.dateTime) && this.offset.equals(offsetDateTime.offset));
    } 
    return false;
  }
  
  public int hashCode() { return this.dateTime.hashCode() ^ this.offset.hashCode(); }
  
  public String toString() { return this.dateTime.toString() + this.offset.toString(); }
  
  private Object writeReplace() { return new Ser((byte)10, this); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Deserialization via serialization delegate"); }
  
  void writeExternal(ObjectOutput paramObjectOutput) throws IOException {
    this.dateTime.writeExternal(paramObjectOutput);
    this.offset.writeExternal(paramObjectOutput);
  }
  
  static OffsetDateTime readExternal(ObjectInput paramObjectInput) throws IOException, ClassNotFoundException {
    LocalDateTime localDateTime = LocalDateTime.readExternal(paramObjectInput);
    ZoneOffset zoneOffset = ZoneOffset.readExternal(paramObjectInput);
    return of(localDateTime, zoneOffset);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\OffsetDateTime.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */