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

public final class LocalTime extends Object implements Temporal, TemporalAdjuster, Comparable<LocalTime>, Serializable {
  public static final LocalTime MIN;
  
  public static final LocalTime MAX;
  
  public static final LocalTime MIDNIGHT;
  
  public static final LocalTime NOON;
  
  private static final LocalTime[] HOURS = new LocalTime[24];
  
  static final int HOURS_PER_DAY = 24;
  
  static final int MINUTES_PER_HOUR = 60;
  
  static final int MINUTES_PER_DAY = 1440;
  
  static final int SECONDS_PER_MINUTE = 60;
  
  static final int SECONDS_PER_HOUR = 3600;
  
  static final int SECONDS_PER_DAY = 86400;
  
  static final long MILLIS_PER_DAY = 86400000L;
  
  static final long MICROS_PER_DAY = 86400000000L;
  
  static final long NANOS_PER_SECOND = 1000000000L;
  
  static final long NANOS_PER_MINUTE = 60000000000L;
  
  static final long NANOS_PER_HOUR = 3600000000000L;
  
  static final long NANOS_PER_DAY = 86400000000000L;
  
  private static final long serialVersionUID = 6414437269572265201L;
  
  private final byte hour;
  
  private final byte minute;
  
  private final byte second;
  
  private final int nano;
  
  public static LocalTime now() { return now(Clock.systemDefaultZone()); }
  
  public static LocalTime now(ZoneId paramZoneId) { return now(Clock.system(paramZoneId)); }
  
  public static LocalTime now(Clock paramClock) {
    Objects.requireNonNull(paramClock, "clock");
    Instant instant = paramClock.instant();
    ZoneOffset zoneOffset = paramClock.getZone().getRules().getOffset(instant);
    long l = instant.getEpochSecond() + zoneOffset.getTotalSeconds();
    int i = (int)Math.floorMod(l, 86400L);
    return ofNanoOfDay(i * 1000000000L + instant.getNano());
  }
  
  public static LocalTime of(int paramInt1, int paramInt2) {
    ChronoField.HOUR_OF_DAY.checkValidValue(paramInt1);
    if (paramInt2 == 0)
      return HOURS[paramInt1]; 
    ChronoField.MINUTE_OF_HOUR.checkValidValue(paramInt2);
    return new LocalTime(paramInt1, paramInt2, 0, 0);
  }
  
  public static LocalTime of(int paramInt1, int paramInt2, int paramInt3) {
    ChronoField.HOUR_OF_DAY.checkValidValue(paramInt1);
    if ((paramInt2 | paramInt3) == 0)
      return HOURS[paramInt1]; 
    ChronoField.MINUTE_OF_HOUR.checkValidValue(paramInt2);
    ChronoField.SECOND_OF_MINUTE.checkValidValue(paramInt3);
    return new LocalTime(paramInt1, paramInt2, paramInt3, 0);
  }
  
  public static LocalTime of(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    ChronoField.HOUR_OF_DAY.checkValidValue(paramInt1);
    ChronoField.MINUTE_OF_HOUR.checkValidValue(paramInt2);
    ChronoField.SECOND_OF_MINUTE.checkValidValue(paramInt3);
    ChronoField.NANO_OF_SECOND.checkValidValue(paramInt4);
    return create(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public static LocalTime ofSecondOfDay(long paramLong) {
    ChronoField.SECOND_OF_DAY.checkValidValue(paramLong);
    int i = (int)(paramLong / 3600L);
    paramLong -= (i * 3600);
    int j = (int)(paramLong / 60L);
    paramLong -= (j * 60);
    return create(i, j, (int)paramLong, 0);
  }
  
  public static LocalTime ofNanoOfDay(long paramLong) {
    ChronoField.NANO_OF_DAY.checkValidValue(paramLong);
    int i = (int)(paramLong / 3600000000000L);
    paramLong -= i * 3600000000000L;
    int j = (int)(paramLong / 60000000000L);
    paramLong -= j * 60000000000L;
    int k = (int)(paramLong / 1000000000L);
    paramLong -= k * 1000000000L;
    return create(i, j, k, (int)paramLong);
  }
  
  public static LocalTime from(TemporalAccessor paramTemporalAccessor) {
    Objects.requireNonNull(paramTemporalAccessor, "temporal");
    LocalTime localTime = (LocalTime)paramTemporalAccessor.query(TemporalQueries.localTime());
    if (localTime == null)
      throw new DateTimeException("Unable to obtain LocalTime from TemporalAccessor: " + paramTemporalAccessor + " of type " + paramTemporalAccessor.getClass().getName()); 
    return localTime;
  }
  
  public static LocalTime parse(CharSequence paramCharSequence) { return parse(paramCharSequence, DateTimeFormatter.ISO_LOCAL_TIME); }
  
  public static LocalTime parse(CharSequence paramCharSequence, DateTimeFormatter paramDateTimeFormatter) {
    Objects.requireNonNull(paramDateTimeFormatter, "formatter");
    return (LocalTime)paramDateTimeFormatter.parse(paramCharSequence, LocalTime::from);
  }
  
  private static LocalTime create(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return ((paramInt2 | paramInt3 | paramInt4) == 0) ? HOURS[paramInt1] : new LocalTime(paramInt1, paramInt2, paramInt3, paramInt4); }
  
  private LocalTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.hour = (byte)paramInt1;
    this.minute = (byte)paramInt2;
    this.second = (byte)paramInt3;
    this.nano = paramInt4;
  }
  
  public boolean isSupported(TemporalField paramTemporalField) { return (paramTemporalField instanceof ChronoField) ? paramTemporalField.isTimeBased() : ((paramTemporalField != null && paramTemporalField.isSupportedBy(this)) ? 1 : 0); }
  
  public boolean isSupported(TemporalUnit paramTemporalUnit) { return (paramTemporalUnit instanceof ChronoUnit) ? paramTemporalUnit.isTimeBased() : ((paramTemporalUnit != null && paramTemporalUnit.isSupportedBy(this)) ? 1 : 0); }
  
  public ValueRange range(TemporalField paramTemporalField) { return super.range(paramTemporalField); }
  
  public int get(TemporalField paramTemporalField) { return (paramTemporalField instanceof ChronoField) ? get0(paramTemporalField) : super.get(paramTemporalField); }
  
  public long getLong(TemporalField paramTemporalField) { return (paramTemporalField instanceof ChronoField) ? ((paramTemporalField == ChronoField.NANO_OF_DAY) ? toNanoOfDay() : ((paramTemporalField == ChronoField.MICRO_OF_DAY) ? (toNanoOfDay() / 1000L) : get0(paramTemporalField))) : paramTemporalField.getFrom(this); }
  
  private int get0(TemporalField paramTemporalField) {
    byte b;
    switch ((ChronoField)paramTemporalField) {
      case NANOS:
        return this.nano;
      case MICROS:
        throw new UnsupportedTemporalTypeException("Invalid field 'NanoOfDay' for get() method, use getLong() instead");
      case MILLIS:
        return this.nano / 1000;
      case SECONDS:
        throw new UnsupportedTemporalTypeException("Invalid field 'MicroOfDay' for get() method, use getLong() instead");
      case MINUTES:
        return this.nano / 1000000;
      case HOURS:
        return (int)(toNanoOfDay() / 1000000L);
      case HALF_DAYS:
        return this.second;
      case null:
        return toSecondOfDay();
      case null:
        return this.minute;
      case null:
        return this.hour * 60 + this.minute;
      case null:
        return this.hour % 12;
      case null:
        b = this.hour % 12;
        return (b % 12 == 0) ? 12 : b;
      case null:
        return this.hour;
      case null:
        return (this.hour == 0) ? 24 : this.hour;
      case null:
        return this.hour / 12;
    } 
    throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
  }
  
  public int getHour() { return this.hour; }
  
  public int getMinute() { return this.minute; }
  
  public int getSecond() { return this.second; }
  
  public int getNano() { return this.nano; }
  
  public LocalTime with(TemporalAdjuster paramTemporalAdjuster) { return (paramTemporalAdjuster instanceof LocalTime) ? (LocalTime)paramTemporalAdjuster : (LocalTime)paramTemporalAdjuster.adjustInto(this); }
  
  public LocalTime with(TemporalField paramTemporalField, long paramLong) {
    if (paramTemporalField instanceof ChronoField) {
      ChronoField chronoField = (ChronoField)paramTemporalField;
      chronoField.checkValidValue(paramLong);
      switch (chronoField) {
        case NANOS:
          return withNano((int)paramLong);
        case MICROS:
          return ofNanoOfDay(paramLong);
        case MILLIS:
          return withNano((int)paramLong * 1000);
        case SECONDS:
          return ofNanoOfDay(paramLong * 1000L);
        case MINUTES:
          return withNano((int)paramLong * 1000000);
        case HOURS:
          return ofNanoOfDay(paramLong * 1000000L);
        case HALF_DAYS:
          return withSecond((int)paramLong);
        case null:
          return plusSeconds(paramLong - toSecondOfDay());
        case null:
          return withMinute((int)paramLong);
        case null:
          return plusMinutes(paramLong - (this.hour * 60 + this.minute));
        case null:
          return plusHours(paramLong - (this.hour % 12));
        case null:
          return plusHours(((paramLong == 12L) ? 0L : paramLong) - (this.hour % 12));
        case null:
          return withHour((int)paramLong);
        case null:
          return withHour((int)((paramLong == 24L) ? 0L : paramLong));
        case null:
          return plusHours((paramLong - (this.hour / 12)) * 12L);
      } 
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
    } 
    return (LocalTime)paramTemporalField.adjustInto(this, paramLong);
  }
  
  public LocalTime withHour(int paramInt) {
    if (this.hour == paramInt)
      return this; 
    ChronoField.HOUR_OF_DAY.checkValidValue(paramInt);
    return create(paramInt, this.minute, this.second, this.nano);
  }
  
  public LocalTime withMinute(int paramInt) {
    if (this.minute == paramInt)
      return this; 
    ChronoField.MINUTE_OF_HOUR.checkValidValue(paramInt);
    return create(this.hour, paramInt, this.second, this.nano);
  }
  
  public LocalTime withSecond(int paramInt) {
    if (this.second == paramInt)
      return this; 
    ChronoField.SECOND_OF_MINUTE.checkValidValue(paramInt);
    return create(this.hour, this.minute, paramInt, this.nano);
  }
  
  public LocalTime withNano(int paramInt) {
    if (this.nano == paramInt)
      return this; 
    ChronoField.NANO_OF_SECOND.checkValidValue(paramInt);
    return create(this.hour, this.minute, this.second, paramInt);
  }
  
  public LocalTime truncatedTo(TemporalUnit paramTemporalUnit) {
    if (paramTemporalUnit == ChronoUnit.NANOS)
      return this; 
    Duration duration = paramTemporalUnit.getDuration();
    if (duration.getSeconds() > 86400L)
      throw new UnsupportedTemporalTypeException("Unit is too large to be used for truncation"); 
    long l1 = duration.toNanos();
    if (86400000000000L % l1 != 0L)
      throw new UnsupportedTemporalTypeException("Unit must divide into a standard day without remainder"); 
    long l2 = toNanoOfDay();
    return ofNanoOfDay(l2 / l1 * l1);
  }
  
  public LocalTime plus(TemporalAmount paramTemporalAmount) { return (LocalTime)paramTemporalAmount.addTo(this); }
  
  public LocalTime plus(long paramLong, TemporalUnit paramTemporalUnit) {
    if (paramTemporalUnit instanceof ChronoUnit) {
      switch ((ChronoUnit)paramTemporalUnit) {
        case NANOS:
          return plusNanos(paramLong);
        case MICROS:
          return plusNanos(paramLong % 86400000000L * 1000L);
        case MILLIS:
          return plusNanos(paramLong % 86400000L * 1000000L);
        case SECONDS:
          return plusSeconds(paramLong);
        case MINUTES:
          return plusMinutes(paramLong);
        case HOURS:
          return plusHours(paramLong);
        case HALF_DAYS:
          return plusHours(paramLong % 2L * 12L);
      } 
      throw new UnsupportedTemporalTypeException("Unsupported unit: " + paramTemporalUnit);
    } 
    return (LocalTime)paramTemporalUnit.addTo(this, paramLong);
  }
  
  public LocalTime plusHours(long paramLong) {
    if (paramLong == 0L)
      return this; 
    int i = ((int)(paramLong % 24L) + this.hour + 24) % 24;
    return create(i, this.minute, this.second, this.nano);
  }
  
  public LocalTime plusMinutes(long paramLong) {
    if (paramLong == 0L)
      return this; 
    byte b = this.hour * 60 + this.minute;
    int i = ((int)(paramLong % 1440L) + b + 1440) % 1440;
    if (b == i)
      return this; 
    int j = i / 60;
    int k = i % 60;
    return create(j, k, this.second, this.nano);
  }
  
  public LocalTime plusSeconds(long paramLong) {
    if (paramLong == 0L)
      return this; 
    byte b = this.hour * 3600 + this.minute * 60 + this.second;
    int i = ((int)(paramLong % 86400L) + b + 86400) % 86400;
    if (b == i)
      return this; 
    int j = i / 3600;
    int k = i / 60 % 60;
    int m = i % 60;
    return create(j, k, m, this.nano);
  }
  
  public LocalTime plusNanos(long paramLong) {
    if (paramLong == 0L)
      return this; 
    long l1 = toNanoOfDay();
    long l2 = (paramLong % 86400000000000L + l1 + 86400000000000L) % 86400000000000L;
    if (l1 == l2)
      return this; 
    int i = (int)(l2 / 3600000000000L);
    int j = (int)(l2 / 60000000000L % 60L);
    int k = (int)(l2 / 1000000000L % 60L);
    int m = (int)(l2 % 1000000000L);
    return create(i, j, k, m);
  }
  
  public LocalTime minus(TemporalAmount paramTemporalAmount) { return (LocalTime)paramTemporalAmount.subtractFrom(this); }
  
  public LocalTime minus(long paramLong, TemporalUnit paramTemporalUnit) { return (paramLong == Float.MIN_VALUE) ? plus(Float.MAX_VALUE, paramTemporalUnit).plus(1L, paramTemporalUnit) : plus(-paramLong, paramTemporalUnit); }
  
  public LocalTime minusHours(long paramLong) { return plusHours(-(paramLong % 24L)); }
  
  public LocalTime minusMinutes(long paramLong) { return plusMinutes(-(paramLong % 1440L)); }
  
  public LocalTime minusSeconds(long paramLong) { return plusSeconds(-(paramLong % 86400L)); }
  
  public LocalTime minusNanos(long paramLong) { return plusNanos(-(paramLong % 86400000000000L)); }
  
  public <R> R query(TemporalQuery<R> paramTemporalQuery) { return (paramTemporalQuery == TemporalQueries.chronology() || paramTemporalQuery == TemporalQueries.zoneId() || paramTemporalQuery == TemporalQueries.zone() || paramTemporalQuery == TemporalQueries.offset()) ? null : ((paramTemporalQuery == TemporalQueries.localTime()) ? (R)this : ((paramTemporalQuery == TemporalQueries.localDate()) ? null : ((paramTemporalQuery == TemporalQueries.precision()) ? (R)ChronoUnit.NANOS : (R)paramTemporalQuery.queryFrom(this)))); }
  
  public Temporal adjustInto(Temporal paramTemporal) { return paramTemporal.with(ChronoField.NANO_OF_DAY, toNanoOfDay()); }
  
  public long until(Temporal paramTemporal, TemporalUnit paramTemporalUnit) {
    LocalTime localTime = from(paramTemporal);
    if (paramTemporalUnit instanceof ChronoUnit) {
      long l = localTime.toNanoOfDay() - toNanoOfDay();
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
    return paramTemporalUnit.between(this, localTime);
  }
  
  public String format(DateTimeFormatter paramDateTimeFormatter) {
    Objects.requireNonNull(paramDateTimeFormatter, "formatter");
    return paramDateTimeFormatter.format(this);
  }
  
  public LocalDateTime atDate(LocalDate paramLocalDate) { return LocalDateTime.of(paramLocalDate, this); }
  
  public OffsetTime atOffset(ZoneOffset paramZoneOffset) { return OffsetTime.of(this, paramZoneOffset); }
  
  public int toSecondOfDay() {
    null = this.hour * 3600;
    null += this.minute * 60;
    return this.second;
  }
  
  public long toNanoOfDay() {
    null = this.hour * 3600000000000L;
    null += this.minute * 60000000000L;
    null += this.second * 1000000000L;
    return this.nano;
  }
  
  public int compareTo(LocalTime paramLocalTime) {
    int i = Integer.compare(this.hour, paramLocalTime.hour);
    if (i == 0) {
      i = Integer.compare(this.minute, paramLocalTime.minute);
      if (i == 0) {
        i = Integer.compare(this.second, paramLocalTime.second);
        if (i == 0)
          i = Integer.compare(this.nano, paramLocalTime.nano); 
      } 
    } 
    return i;
  }
  
  public boolean isAfter(LocalTime paramLocalTime) { return (compareTo(paramLocalTime) > 0); }
  
  public boolean isBefore(LocalTime paramLocalTime) { return (compareTo(paramLocalTime) < 0); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof LocalTime) {
      LocalTime localTime = (LocalTime)paramObject;
      return (this.hour == localTime.hour && this.minute == localTime.minute && this.second == localTime.second && this.nano == localTime.nano);
    } 
    return false;
  }
  
  public int hashCode() {
    long l = toNanoOfDay();
    return (int)(l ^ l >>> 32);
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(18);
    byte b1 = this.hour;
    byte b2 = this.minute;
    byte b3 = this.second;
    int i = this.nano;
    stringBuilder.append((b1 < 10) ? "0" : "").append(b1).append((b2 < 10) ? ":0" : ":").append(b2);
    if (b3 > 0 || i > 0) {
      stringBuilder.append((b3 < 10) ? ":0" : ":").append(b3);
      if (i > 0) {
        stringBuilder.append('.');
        if (i % 1000000 == 0) {
          stringBuilder.append(Integer.toString(i / 1000000 + 1000).substring(1));
        } else if (i % 1000 == 0) {
          stringBuilder.append(Integer.toString(i / 1000 + 1000000).substring(1));
        } else {
          stringBuilder.append(Integer.toString(i + 1000000000).substring(1));
        } 
      } 
    } 
    return stringBuilder.toString();
  }
  
  private Object writeReplace() { return new Ser((byte)4, this); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Deserialization via serialization delegate"); }
  
  void writeExternal(DataOutput paramDataOutput) throws IOException {
    if (this.nano == 0) {
      if (this.second == 0) {
        if (this.minute == 0) {
          paramDataOutput.writeByte(this.hour ^ 0xFFFFFFFF);
        } else {
          paramDataOutput.writeByte(this.hour);
          paramDataOutput.writeByte(this.minute ^ 0xFFFFFFFF);
        } 
      } else {
        paramDataOutput.writeByte(this.hour);
        paramDataOutput.writeByte(this.minute);
        paramDataOutput.writeByte(this.second ^ 0xFFFFFFFF);
      } 
    } else {
      paramDataOutput.writeByte(this.hour);
      paramDataOutput.writeByte(this.minute);
      paramDataOutput.writeByte(this.second);
      paramDataOutput.writeInt(this.nano);
    } 
  }
  
  static LocalTime readExternal(DataInput paramDataInput) throws IOException {
    byte b = paramDataInput.readByte();
    byte b1 = 0;
    byte b2 = 0;
    int i = 0;
    if (b < 0) {
      b ^= 0xFFFFFFFF;
    } else {
      b1 = paramDataInput.readByte();
      if (b1 < 0) {
        b1 ^= 0xFFFFFFFF;
      } else {
        b2 = paramDataInput.readByte();
        if (b2 < 0) {
          b2 ^= 0xFFFFFFFF;
        } else {
          i = paramDataInput.readInt();
        } 
      } 
    } 
    return of(b, b1, b2, i);
  }
  
  static  {
    for (byte b = 0; b < HOURS.length; b++)
      HOURS[b] = new LocalTime(b, 0, 0, 0); 
    MIDNIGHT = HOURS[0];
    NOON = HOURS[12];
    MIN = HOURS[0];
    MAX = new LocalTime(23, 59, 59, 999999999);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\LocalTime.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */