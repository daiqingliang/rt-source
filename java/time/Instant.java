package java.time;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
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
import java.util.Objects;

public final class Instant extends Object implements Temporal, TemporalAdjuster, Comparable<Instant>, Serializable {
  public static final Instant EPOCH;
  
  private static final long MIN_SECOND = -31557014167219200L;
  
  private static final long MAX_SECOND = 31556889864403199L;
  
  public static final Instant MIN;
  
  public static final Instant MAX = (MIN = (EPOCH = new Instant(0L, 0)).ofEpochSecond(-31557014167219200L, 0L)).ofEpochSecond(31556889864403199L, 999999999L);
  
  private static final long serialVersionUID = -665713676816604388L;
  
  private final long seconds;
  
  private final int nanos;
  
  public static Instant now() { return Clock.systemUTC().instant(); }
  
  public static Instant now(Clock paramClock) {
    Objects.requireNonNull(paramClock, "clock");
    return paramClock.instant();
  }
  
  public static Instant ofEpochSecond(long paramLong) { return create(paramLong, 0); }
  
  public static Instant ofEpochSecond(long paramLong1, long paramLong2) {
    long l = Math.addExact(paramLong1, Math.floorDiv(paramLong2, 1000000000L));
    int i = (int)Math.floorMod(paramLong2, 1000000000L);
    return create(l, i);
  }
  
  public static Instant ofEpochMilli(long paramLong) {
    long l = Math.floorDiv(paramLong, 1000L);
    int i = (int)Math.floorMod(paramLong, 1000L);
    return create(l, i * 1000000);
  }
  
  public static Instant from(TemporalAccessor paramTemporalAccessor) {
    if (paramTemporalAccessor instanceof Instant)
      return (Instant)paramTemporalAccessor; 
    Objects.requireNonNull(paramTemporalAccessor, "temporal");
    try {
      long l = paramTemporalAccessor.getLong(ChronoField.INSTANT_SECONDS);
      int i = paramTemporalAccessor.get(ChronoField.NANO_OF_SECOND);
      return ofEpochSecond(l, i);
    } catch (DateTimeException dateTimeException) {
      throw new DateTimeException("Unable to obtain Instant from TemporalAccessor: " + paramTemporalAccessor + " of type " + paramTemporalAccessor.getClass().getName(), dateTimeException);
    } 
  }
  
  public static Instant parse(CharSequence paramCharSequence) { return (Instant)DateTimeFormatter.ISO_INSTANT.parse(paramCharSequence, Instant::from); }
  
  private static Instant create(long paramLong, int paramInt) {
    if ((paramLong | paramInt) == 0L)
      return EPOCH; 
    if (paramLong < -31557014167219200L || paramLong > 31556889864403199L)
      throw new DateTimeException("Instant exceeds minimum or maximum instant"); 
    return new Instant(paramLong, paramInt);
  }
  
  private Instant(long paramLong, int paramInt) {
    this.seconds = paramLong;
    this.nanos = paramInt;
  }
  
  public boolean isSupported(TemporalField paramTemporalField) { return (paramTemporalField instanceof ChronoField) ? ((paramTemporalField == ChronoField.INSTANT_SECONDS || paramTemporalField == ChronoField.NANO_OF_SECOND || paramTemporalField == ChronoField.MICRO_OF_SECOND || paramTemporalField == ChronoField.MILLI_OF_SECOND)) : ((paramTemporalField != null && paramTemporalField.isSupportedBy(this))); }
  
  public boolean isSupported(TemporalUnit paramTemporalUnit) { return (paramTemporalUnit instanceof ChronoUnit) ? ((paramTemporalUnit.isTimeBased() || paramTemporalUnit == ChronoUnit.DAYS)) : ((paramTemporalUnit != null && paramTemporalUnit.isSupportedBy(this))); }
  
  public ValueRange range(TemporalField paramTemporalField) { return super.range(paramTemporalField); }
  
  public int get(TemporalField paramTemporalField) {
    if (paramTemporalField instanceof ChronoField) {
      switch ((ChronoField)paramTemporalField) {
        case NANOS:
          return this.nanos;
        case MICROS:
          return this.nanos / 1000;
        case MILLIS:
          return this.nanos / 1000000;
        case SECONDS:
          ChronoField.INSTANT_SECONDS.checkValidIntValue(this.seconds);
          break;
      } 
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
    } 
    return range(paramTemporalField).checkValidIntValue(paramTemporalField.getFrom(this), paramTemporalField);
  }
  
  public long getLong(TemporalField paramTemporalField) {
    if (paramTemporalField instanceof ChronoField) {
      switch ((ChronoField)paramTemporalField) {
        case NANOS:
          return this.nanos;
        case MICROS:
          return (this.nanos / 1000);
        case MILLIS:
          return (this.nanos / 1000000);
        case SECONDS:
          return this.seconds;
      } 
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
    } 
    return paramTemporalField.getFrom(this);
  }
  
  public long getEpochSecond() { return this.seconds; }
  
  public int getNano() { return this.nanos; }
  
  public Instant with(TemporalAdjuster paramTemporalAdjuster) { return (Instant)paramTemporalAdjuster.adjustInto(this); }
  
  public Instant with(TemporalField paramTemporalField, long paramLong) {
    if (paramTemporalField instanceof ChronoField) {
      int i;
      ChronoField chronoField = (ChronoField)paramTemporalField;
      chronoField.checkValidValue(paramLong);
      switch (chronoField) {
        case MILLIS:
          i = (int)paramLong * 1000000;
          return (i != this.nanos) ? create(this.seconds, i) : this;
        case MICROS:
          i = (int)paramLong * 1000;
          return (i != this.nanos) ? create(this.seconds, i) : this;
        case NANOS:
          return (paramLong != this.nanos) ? create(this.seconds, (int)paramLong) : this;
        case SECONDS:
          return (paramLong != this.seconds) ? create(paramLong, this.nanos) : this;
      } 
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
    } 
    return (Instant)paramTemporalField.adjustInto(this, paramLong);
  }
  
  public Instant truncatedTo(TemporalUnit paramTemporalUnit) {
    if (paramTemporalUnit == ChronoUnit.NANOS)
      return this; 
    Duration duration = paramTemporalUnit.getDuration();
    if (duration.getSeconds() > 86400L)
      throw new UnsupportedTemporalTypeException("Unit is too large to be used for truncation"); 
    long l1 = duration.toNanos();
    if (86400000000000L % l1 != 0L)
      throw new UnsupportedTemporalTypeException("Unit must divide into a standard day without remainder"); 
    long l2 = this.seconds % 86400L * 1000000000L + this.nanos;
    long l3 = l2 / l1 * l1;
    return plusNanos(l3 - l2);
  }
  
  public Instant plus(TemporalAmount paramTemporalAmount) { return (Instant)paramTemporalAmount.addTo(this); }
  
  public Instant plus(long paramLong, TemporalUnit paramTemporalUnit) {
    if (paramTemporalUnit instanceof ChronoUnit) {
      switch ((ChronoUnit)paramTemporalUnit) {
        case NANOS:
          return plusNanos(paramLong);
        case MICROS:
          return plus(paramLong / 1000000L, paramLong % 1000000L * 1000L);
        case MILLIS:
          return plusMillis(paramLong);
        case SECONDS:
          return plusSeconds(paramLong);
        case MINUTES:
          return plusSeconds(Math.multiplyExact(paramLong, 60L));
        case HOURS:
          return plusSeconds(Math.multiplyExact(paramLong, 3600L));
        case HALF_DAYS:
          return plusSeconds(Math.multiplyExact(paramLong, 43200L));
        case DAYS:
          return plusSeconds(Math.multiplyExact(paramLong, 86400L));
      } 
      throw new UnsupportedTemporalTypeException("Unsupported unit: " + paramTemporalUnit);
    } 
    return (Instant)paramTemporalUnit.addTo(this, paramLong);
  }
  
  public Instant plusSeconds(long paramLong) { return plus(paramLong, 0L); }
  
  public Instant plusMillis(long paramLong) { return plus(paramLong / 1000L, paramLong % 1000L * 1000000L); }
  
  public Instant plusNanos(long paramLong) { return plus(0L, paramLong); }
  
  private Instant plus(long paramLong1, long paramLong2) {
    if ((paramLong1 | paramLong2) == 0L)
      return this; 
    long l1 = Math.addExact(this.seconds, paramLong1);
    l1 = Math.addExact(l1, paramLong2 / 1000000000L);
    paramLong2 %= 1000000000L;
    long l2 = this.nanos + paramLong2;
    return ofEpochSecond(l1, l2);
  }
  
  public Instant minus(TemporalAmount paramTemporalAmount) { return (Instant)paramTemporalAmount.subtractFrom(this); }
  
  public Instant minus(long paramLong, TemporalUnit paramTemporalUnit) { return (paramLong == Float.MIN_VALUE) ? plus(Float.MAX_VALUE, paramTemporalUnit).plus(1L, paramTemporalUnit) : plus(-paramLong, paramTemporalUnit); }
  
  public Instant minusSeconds(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusSeconds(Float.MAX_VALUE).plusSeconds(1L) : plusSeconds(-paramLong); }
  
  public Instant minusMillis(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusMillis(Float.MAX_VALUE).plusMillis(1L) : plusMillis(-paramLong); }
  
  public Instant minusNanos(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusNanos(Float.MAX_VALUE).plusNanos(1L) : plusNanos(-paramLong); }
  
  public <R> R query(TemporalQuery<R> paramTemporalQuery) { return (paramTemporalQuery == TemporalQueries.precision()) ? (R)ChronoUnit.NANOS : ((paramTemporalQuery == TemporalQueries.chronology() || paramTemporalQuery == TemporalQueries.zoneId() || paramTemporalQuery == TemporalQueries.zone() || paramTemporalQuery == TemporalQueries.offset() || paramTemporalQuery == TemporalQueries.localDate() || paramTemporalQuery == TemporalQueries.localTime()) ? null : (R)paramTemporalQuery.queryFrom(this)); }
  
  public Temporal adjustInto(Temporal paramTemporal) { return paramTemporal.with(ChronoField.INSTANT_SECONDS, this.seconds).with(ChronoField.NANO_OF_SECOND, this.nanos); }
  
  public long until(Temporal paramTemporal, TemporalUnit paramTemporalUnit) {
    Instant instant = from(paramTemporal);
    if (paramTemporalUnit instanceof ChronoUnit) {
      ChronoUnit chronoUnit = (ChronoUnit)paramTemporalUnit;
      switch (chronoUnit) {
        case NANOS:
          return nanosUntil(instant);
        case MICROS:
          return nanosUntil(instant) / 1000L;
        case MILLIS:
          return Math.subtractExact(instant.toEpochMilli(), toEpochMilli());
        case SECONDS:
          return secondsUntil(instant);
        case MINUTES:
          return secondsUntil(instant) / 60L;
        case HOURS:
          return secondsUntil(instant) / 3600L;
        case HALF_DAYS:
          return secondsUntil(instant) / 43200L;
        case DAYS:
          return secondsUntil(instant) / 86400L;
      } 
      throw new UnsupportedTemporalTypeException("Unsupported unit: " + paramTemporalUnit);
    } 
    return paramTemporalUnit.between(this, instant);
  }
  
  private long nanosUntil(Instant paramInstant) {
    long l1 = Math.subtractExact(paramInstant.seconds, this.seconds);
    long l2 = Math.multiplyExact(l1, 1000000000L);
    return Math.addExact(l2, (paramInstant.nanos - this.nanos));
  }
  
  private long secondsUntil(Instant paramInstant) {
    long l1 = Math.subtractExact(paramInstant.seconds, this.seconds);
    long l2 = (paramInstant.nanos - this.nanos);
    if (l1 > 0L && l2 < 0L) {
      l1--;
    } else if (l1 < 0L && l2 > 0L) {
      l1++;
    } 
    return l1;
  }
  
  public OffsetDateTime atOffset(ZoneOffset paramZoneOffset) { return OffsetDateTime.ofInstant(this, paramZoneOffset); }
  
  public ZonedDateTime atZone(ZoneId paramZoneId) { return ZonedDateTime.ofInstant(this, paramZoneId); }
  
  public long toEpochMilli() {
    if (this.seconds < 0L && this.nanos > 0) {
      long l1 = Math.multiplyExact(this.seconds + 1L, 1000L);
      long l2 = (this.nanos / 1000000 - 1000);
      return Math.addExact(l1, l2);
    } 
    long l = Math.multiplyExact(this.seconds, 1000L);
    return Math.addExact(l, (this.nanos / 1000000));
  }
  
  public int compareTo(Instant paramInstant) {
    int i = Long.compare(this.seconds, paramInstant.seconds);
    return (i != 0) ? i : (this.nanos - paramInstant.nanos);
  }
  
  public boolean isAfter(Instant paramInstant) { return (compareTo(paramInstant) > 0); }
  
  public boolean isBefore(Instant paramInstant) { return (compareTo(paramInstant) < 0); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof Instant) {
      Instant instant = (Instant)paramObject;
      return (this.seconds == instant.seconds && this.nanos == instant.nanos);
    } 
    return false;
  }
  
  public int hashCode() { return (int)(this.seconds ^ this.seconds >>> 32) + 51 * this.nanos; }
  
  public String toString() { return DateTimeFormatter.ISO_INSTANT.format(this); }
  
  private Object writeReplace() { return new Ser((byte)2, this); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Deserialization via serialization delegate"); }
  
  void writeExternal(DataOutput paramDataOutput) throws IOException {
    paramDataOutput.writeLong(this.seconds);
    paramDataOutput.writeInt(this.nanos);
  }
  
  static Instant readExternal(DataInput paramDataInput) throws IOException {
    long l = paramDataInput.readLong();
    int i = paramDataInput.readInt();
    return ofEpochSecond(l, i);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\Instant.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */