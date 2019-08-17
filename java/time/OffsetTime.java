package java.time;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;
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
import java.util.Objects;

public final class OffsetTime extends Object implements Temporal, TemporalAdjuster, Comparable<OffsetTime>, Serializable {
  public static final OffsetTime MIN = LocalTime.MIN.atOffset(ZoneOffset.MAX);
  
  public static final OffsetTime MAX = LocalTime.MAX.atOffset(ZoneOffset.MIN);
  
  private static final long serialVersionUID = 7264499704384272492L;
  
  private final LocalTime time;
  
  private final ZoneOffset offset;
  
  public static OffsetTime now() { return now(Clock.systemDefaultZone()); }
  
  public static OffsetTime now(ZoneId paramZoneId) { return now(Clock.system(paramZoneId)); }
  
  public static OffsetTime now(Clock paramClock) {
    Objects.requireNonNull(paramClock, "clock");
    Instant instant = paramClock.instant();
    return ofInstant(instant, paramClock.getZone().getRules().getOffset(instant));
  }
  
  public static OffsetTime of(LocalTime paramLocalTime, ZoneOffset paramZoneOffset) { return new OffsetTime(paramLocalTime, paramZoneOffset); }
  
  public static OffsetTime of(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ZoneOffset paramZoneOffset) { return new OffsetTime(LocalTime.of(paramInt1, paramInt2, paramInt3, paramInt4), paramZoneOffset); }
  
  public static OffsetTime ofInstant(Instant paramInstant, ZoneId paramZoneId) {
    Objects.requireNonNull(paramInstant, "instant");
    Objects.requireNonNull(paramZoneId, "zone");
    ZoneRules zoneRules = paramZoneId.getRules();
    ZoneOffset zoneOffset = zoneRules.getOffset(paramInstant);
    long l = paramInstant.getEpochSecond() + zoneOffset.getTotalSeconds();
    int i = (int)Math.floorMod(l, 86400L);
    LocalTime localTime = LocalTime.ofNanoOfDay(i * 1000000000L + paramInstant.getNano());
    return new OffsetTime(localTime, zoneOffset);
  }
  
  public static OffsetTime from(TemporalAccessor paramTemporalAccessor) {
    if (paramTemporalAccessor instanceof OffsetTime)
      return (OffsetTime)paramTemporalAccessor; 
    try {
      LocalTime localTime = LocalTime.from(paramTemporalAccessor);
      ZoneOffset zoneOffset = ZoneOffset.from(paramTemporalAccessor);
      return new OffsetTime(localTime, zoneOffset);
    } catch (DateTimeException dateTimeException) {
      throw new DateTimeException("Unable to obtain OffsetTime from TemporalAccessor: " + paramTemporalAccessor + " of type " + paramTemporalAccessor.getClass().getName(), dateTimeException);
    } 
  }
  
  public static OffsetTime parse(CharSequence paramCharSequence) { return parse(paramCharSequence, DateTimeFormatter.ISO_OFFSET_TIME); }
  
  public static OffsetTime parse(CharSequence paramCharSequence, DateTimeFormatter paramDateTimeFormatter) {
    Objects.requireNonNull(paramDateTimeFormatter, "formatter");
    return (OffsetTime)paramDateTimeFormatter.parse(paramCharSequence, OffsetTime::from);
  }
  
  private OffsetTime(LocalTime paramLocalTime, ZoneOffset paramZoneOffset) {
    this.time = (LocalTime)Objects.requireNonNull(paramLocalTime, "time");
    this.offset = (ZoneOffset)Objects.requireNonNull(paramZoneOffset, "offset");
  }
  
  private OffsetTime with(LocalTime paramLocalTime, ZoneOffset paramZoneOffset) { return (this.time == paramLocalTime && this.offset.equals(paramZoneOffset)) ? this : new OffsetTime(paramLocalTime, paramZoneOffset); }
  
  public boolean isSupported(TemporalField paramTemporalField) { return (paramTemporalField instanceof ChronoField) ? ((paramTemporalField.isTimeBased() || paramTemporalField == ChronoField.OFFSET_SECONDS)) : ((paramTemporalField != null && paramTemporalField.isSupportedBy(this))); }
  
  public boolean isSupported(TemporalUnit paramTemporalUnit) { return (paramTemporalUnit instanceof ChronoUnit) ? paramTemporalUnit.isTimeBased() : ((paramTemporalUnit != null && paramTemporalUnit.isSupportedBy(this)) ? 1 : 0); }
  
  public ValueRange range(TemporalField paramTemporalField) { return (paramTemporalField instanceof ChronoField) ? ((paramTemporalField == ChronoField.OFFSET_SECONDS) ? paramTemporalField.range() : this.time.range(paramTemporalField)) : paramTemporalField.rangeRefinedBy(this); }
  
  public int get(TemporalField paramTemporalField) { return super.get(paramTemporalField); }
  
  public long getLong(TemporalField paramTemporalField) { return (paramTemporalField instanceof ChronoField) ? ((paramTemporalField == ChronoField.OFFSET_SECONDS) ? this.offset.getTotalSeconds() : this.time.getLong(paramTemporalField)) : paramTemporalField.getFrom(this); }
  
  public ZoneOffset getOffset() { return this.offset; }
  
  public OffsetTime withOffsetSameLocal(ZoneOffset paramZoneOffset) { return (paramZoneOffset != null && paramZoneOffset.equals(this.offset)) ? this : new OffsetTime(this.time, paramZoneOffset); }
  
  public OffsetTime withOffsetSameInstant(ZoneOffset paramZoneOffset) {
    if (paramZoneOffset.equals(this.offset))
      return this; 
    int i = paramZoneOffset.getTotalSeconds() - this.offset.getTotalSeconds();
    LocalTime localTime = this.time.plusSeconds(i);
    return new OffsetTime(localTime, paramZoneOffset);
  }
  
  public LocalTime toLocalTime() { return this.time; }
  
  public int getHour() { return this.time.getHour(); }
  
  public int getMinute() { return this.time.getMinute(); }
  
  public int getSecond() { return this.time.getSecond(); }
  
  public int getNano() { return this.time.getNano(); }
  
  public OffsetTime with(TemporalAdjuster paramTemporalAdjuster) { return (paramTemporalAdjuster instanceof LocalTime) ? with((LocalTime)paramTemporalAdjuster, this.offset) : ((paramTemporalAdjuster instanceof ZoneOffset) ? with(this.time, (ZoneOffset)paramTemporalAdjuster) : ((paramTemporalAdjuster instanceof OffsetTime) ? (OffsetTime)paramTemporalAdjuster : (OffsetTime)paramTemporalAdjuster.adjustInto(this))); }
  
  public OffsetTime with(TemporalField paramTemporalField, long paramLong) {
    if (paramTemporalField instanceof ChronoField) {
      if (paramTemporalField == ChronoField.OFFSET_SECONDS) {
        ChronoField chronoField = (ChronoField)paramTemporalField;
        return with(this.time, ZoneOffset.ofTotalSeconds(chronoField.checkValidIntValue(paramLong)));
      } 
      return with(this.time.with(paramTemporalField, paramLong), this.offset);
    } 
    return (OffsetTime)paramTemporalField.adjustInto(this, paramLong);
  }
  
  public OffsetTime withHour(int paramInt) { return with(this.time.withHour(paramInt), this.offset); }
  
  public OffsetTime withMinute(int paramInt) { return with(this.time.withMinute(paramInt), this.offset); }
  
  public OffsetTime withSecond(int paramInt) { return with(this.time.withSecond(paramInt), this.offset); }
  
  public OffsetTime withNano(int paramInt) { return with(this.time.withNano(paramInt), this.offset); }
  
  public OffsetTime truncatedTo(TemporalUnit paramTemporalUnit) { return with(this.time.truncatedTo(paramTemporalUnit), this.offset); }
  
  public OffsetTime plus(TemporalAmount paramTemporalAmount) { return (OffsetTime)paramTemporalAmount.addTo(this); }
  
  public OffsetTime plus(long paramLong, TemporalUnit paramTemporalUnit) { return (paramTemporalUnit instanceof ChronoUnit) ? with(this.time.plus(paramLong, paramTemporalUnit), this.offset) : (OffsetTime)paramTemporalUnit.addTo(this, paramLong); }
  
  public OffsetTime plusHours(long paramLong) { return with(this.time.plusHours(paramLong), this.offset); }
  
  public OffsetTime plusMinutes(long paramLong) { return with(this.time.plusMinutes(paramLong), this.offset); }
  
  public OffsetTime plusSeconds(long paramLong) { return with(this.time.plusSeconds(paramLong), this.offset); }
  
  public OffsetTime plusNanos(long paramLong) { return with(this.time.plusNanos(paramLong), this.offset); }
  
  public OffsetTime minus(TemporalAmount paramTemporalAmount) { return (OffsetTime)paramTemporalAmount.subtractFrom(this); }
  
  public OffsetTime minus(long paramLong, TemporalUnit paramTemporalUnit) { return (paramLong == Float.MIN_VALUE) ? plus(Float.MAX_VALUE, paramTemporalUnit).plus(1L, paramTemporalUnit) : plus(-paramLong, paramTemporalUnit); }
  
  public OffsetTime minusHours(long paramLong) { return with(this.time.minusHours(paramLong), this.offset); }
  
  public OffsetTime minusMinutes(long paramLong) { return with(this.time.minusMinutes(paramLong), this.offset); }
  
  public OffsetTime minusSeconds(long paramLong) { return with(this.time.minusSeconds(paramLong), this.offset); }
  
  public OffsetTime minusNanos(long paramLong) { return with(this.time.minusNanos(paramLong), this.offset); }
  
  public <R> R query(TemporalQuery<R> paramTemporalQuery) { return (paramTemporalQuery == TemporalQueries.offset() || paramTemporalQuery == TemporalQueries.zone()) ? (R)this.offset : ((((paramTemporalQuery == TemporalQueries.zoneId()) ? 1 : 0) | ((paramTemporalQuery == TemporalQueries.chronology()) ? 1 : 0) || paramTemporalQuery == TemporalQueries.localDate()) ? null : ((paramTemporalQuery == TemporalQueries.localTime()) ? (R)this.time : ((paramTemporalQuery == TemporalQueries.precision()) ? (R)ChronoUnit.NANOS : (R)paramTemporalQuery.queryFrom(this)))); }
  
  public Temporal adjustInto(Temporal paramTemporal) { return paramTemporal.with(ChronoField.NANO_OF_DAY, this.time.toNanoOfDay()).with(ChronoField.OFFSET_SECONDS, this.offset.getTotalSeconds()); }
  
  public long until(Temporal paramTemporal, TemporalUnit paramTemporalUnit) {
    OffsetTime offsetTime = from(paramTemporal);
    if (paramTemporalUnit instanceof ChronoUnit) {
      long l = offsetTime.toEpochNano() - toEpochNano();
      switch ((ChronoUnit)paramTemporalUnit) {
        case NANOS:
          return l;
        case MICROS:
          return l / 1000L;
        case MILLIS:
          return l / 1000000L;
        case SECONDS:
          return l / 1000000000L;
        case MINUTES:
          return l / 60000000000L;
        case HOURS:
          return l / 3600000000000L;
        case HALF_DAYS:
          return l / 43200000000000L;
      } 
      throw new UnsupportedTemporalTypeException("Unsupported unit: " + paramTemporalUnit);
    } 
    return paramTemporalUnit.between(this, offsetTime);
  }
  
  public String format(DateTimeFormatter paramDateTimeFormatter) {
    Objects.requireNonNull(paramDateTimeFormatter, "formatter");
    return paramDateTimeFormatter.format(this);
  }
  
  public OffsetDateTime atDate(LocalDate paramLocalDate) { return OffsetDateTime.of(paramLocalDate, this.time, this.offset); }
  
  private long toEpochNano() {
    long l1 = this.time.toNanoOfDay();
    long l2 = this.offset.getTotalSeconds() * 1000000000L;
    return l1 - l2;
  }
  
  public int compareTo(OffsetTime paramOffsetTime) {
    if (this.offset.equals(paramOffsetTime.offset))
      return this.time.compareTo(paramOffsetTime.time); 
    int i = Long.compare(toEpochNano(), paramOffsetTime.toEpochNano());
    if (i == 0)
      i = this.time.compareTo(paramOffsetTime.time); 
    return i;
  }
  
  public boolean isAfter(OffsetTime paramOffsetTime) { return (toEpochNano() > paramOffsetTime.toEpochNano()); }
  
  public boolean isBefore(OffsetTime paramOffsetTime) { return (toEpochNano() < paramOffsetTime.toEpochNano()); }
  
  public boolean isEqual(OffsetTime paramOffsetTime) { return (toEpochNano() == paramOffsetTime.toEpochNano()); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof OffsetTime) {
      OffsetTime offsetTime = (OffsetTime)paramObject;
      return (this.time.equals(offsetTime.time) && this.offset.equals(offsetTime.offset));
    } 
    return false;
  }
  
  public int hashCode() { return this.time.hashCode() ^ this.offset.hashCode(); }
  
  public String toString() { return this.time.toString() + this.offset.toString(); }
  
  private Object writeReplace() { return new Ser((byte)9, this); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Deserialization via serialization delegate"); }
  
  void writeExternal(ObjectOutput paramObjectOutput) throws IOException {
    this.time.writeExternal(paramObjectOutput);
    this.offset.writeExternal(paramObjectOutput);
  }
  
  static OffsetTime readExternal(ObjectInput paramObjectInput) throws IOException, ClassNotFoundException {
    LocalTime localTime = LocalTime.readExternal(paramObjectInput);
    ZoneOffset zoneOffset = ZoneOffset.readExternal(paramObjectInput);
    return of(localTime, zoneOffset);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\OffsetTime.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */