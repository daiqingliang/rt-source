package java.time.chrono;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ValueRange;
import java.util.Objects;

final class ChronoLocalDateTimeImpl<D extends ChronoLocalDate> extends Object implements ChronoLocalDateTime<D>, Temporal, TemporalAdjuster, Serializable {
  private static final long serialVersionUID = 4556003607393004514L;
  
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
  
  private final D date;
  
  private final LocalTime time;
  
  static <R extends ChronoLocalDate> ChronoLocalDateTimeImpl<R> of(R paramR, LocalTime paramLocalTime) { return new ChronoLocalDateTimeImpl(paramR, paramLocalTime); }
  
  static <R extends ChronoLocalDate> ChronoLocalDateTimeImpl<R> ensureValid(Chronology paramChronology, Temporal paramTemporal) {
    ChronoLocalDateTimeImpl chronoLocalDateTimeImpl = (ChronoLocalDateTimeImpl)paramTemporal;
    if (!paramChronology.equals(chronoLocalDateTimeImpl.getChronology()))
      throw new ClassCastException("Chronology mismatch, required: " + paramChronology.getId() + ", actual: " + chronoLocalDateTimeImpl.getChronology().getId()); 
    return chronoLocalDateTimeImpl;
  }
  
  private ChronoLocalDateTimeImpl(D paramD, LocalTime paramLocalTime) {
    Objects.requireNonNull(paramD, "date");
    Objects.requireNonNull(paramLocalTime, "time");
    this.date = paramD;
    this.time = paramLocalTime;
  }
  
  private ChronoLocalDateTimeImpl<D> with(Temporal paramTemporal, LocalTime paramLocalTime) {
    if (this.date == paramTemporal && this.time == paramLocalTime)
      return this; 
    ChronoLocalDate chronoLocalDate = ChronoLocalDateImpl.ensureValid(this.date.getChronology(), paramTemporal);
    return new ChronoLocalDateTimeImpl(chronoLocalDate, paramLocalTime);
  }
  
  public D toLocalDate() { return (D)this.date; }
  
  public LocalTime toLocalTime() { return this.time; }
  
  public boolean isSupported(TemporalField paramTemporalField) {
    if (paramTemporalField instanceof ChronoField) {
      ChronoField chronoField = (ChronoField)paramTemporalField;
      return (chronoField.isDateBased() || chronoField.isTimeBased());
    } 
    return (paramTemporalField != null && paramTemporalField.isSupportedBy(this));
  }
  
  public ValueRange range(TemporalField paramTemporalField) {
    if (paramTemporalField instanceof ChronoField) {
      ChronoField chronoField = (ChronoField)paramTemporalField;
      return chronoField.isTimeBased() ? this.time.range(paramTemporalField) : this.date.range(paramTemporalField);
    } 
    return paramTemporalField.rangeRefinedBy(this);
  }
  
  public int get(TemporalField paramTemporalField) {
    if (paramTemporalField instanceof ChronoField) {
      ChronoField chronoField = (ChronoField)paramTemporalField;
      return chronoField.isTimeBased() ? this.time.get(paramTemporalField) : this.date.get(paramTemporalField);
    } 
    return range(paramTemporalField).checkValidIntValue(getLong(paramTemporalField), paramTemporalField);
  }
  
  public long getLong(TemporalField paramTemporalField) {
    if (paramTemporalField instanceof ChronoField) {
      ChronoField chronoField = (ChronoField)paramTemporalField;
      return chronoField.isTimeBased() ? this.time.getLong(paramTemporalField) : this.date.getLong(paramTemporalField);
    } 
    return paramTemporalField.getFrom(this);
  }
  
  public ChronoLocalDateTimeImpl<D> with(TemporalAdjuster paramTemporalAdjuster) { return (paramTemporalAdjuster instanceof ChronoLocalDate) ? with((ChronoLocalDate)paramTemporalAdjuster, this.time) : ((paramTemporalAdjuster instanceof LocalTime) ? with(this.date, (LocalTime)paramTemporalAdjuster) : ((paramTemporalAdjuster instanceof ChronoLocalDateTimeImpl) ? ensureValid(this.date.getChronology(), (ChronoLocalDateTimeImpl)paramTemporalAdjuster) : ensureValid(this.date.getChronology(), (ChronoLocalDateTimeImpl)paramTemporalAdjuster.adjustInto(this)))); }
  
  public ChronoLocalDateTimeImpl<D> with(TemporalField paramTemporalField, long paramLong) {
    if (paramTemporalField instanceof ChronoField) {
      ChronoField chronoField = (ChronoField)paramTemporalField;
      return chronoField.isTimeBased() ? with(this.date, this.time.with(paramTemporalField, paramLong)) : with(this.date.with(paramTemporalField, paramLong), this.time);
    } 
    return ensureValid(this.date.getChronology(), paramTemporalField.adjustInto(this, paramLong));
  }
  
  public ChronoLocalDateTimeImpl<D> plus(long paramLong, TemporalUnit paramTemporalUnit) {
    if (paramTemporalUnit instanceof ChronoUnit) {
      ChronoUnit chronoUnit = (ChronoUnit)paramTemporalUnit;
      switch (chronoUnit) {
        case NANOS:
          return plusNanos(paramLong);
        case MICROS:
          return plusDays(paramLong / 86400000000L).plusNanos(paramLong % 86400000000L * 1000L);
        case MILLIS:
          return plusDays(paramLong / 86400000L).plusNanos(paramLong % 86400000L * 1000000L);
        case SECONDS:
          return plusSeconds(paramLong);
        case MINUTES:
          return plusMinutes(paramLong);
        case HOURS:
          return plusHours(paramLong);
        case HALF_DAYS:
          return plusDays(paramLong / 256L).plusHours(paramLong % 256L * 12L);
      } 
      return with(this.date.plus(paramLong, paramTemporalUnit), this.time);
    } 
    return ensureValid(this.date.getChronology(), paramTemporalUnit.addTo(this, paramLong));
  }
  
  private ChronoLocalDateTimeImpl<D> plusDays(long paramLong) { return with(this.date.plus(paramLong, ChronoUnit.DAYS), this.time); }
  
  private ChronoLocalDateTimeImpl<D> plusHours(long paramLong) { return plusWithOverflow(this.date, paramLong, 0L, 0L, 0L); }
  
  private ChronoLocalDateTimeImpl<D> plusMinutes(long paramLong) { return plusWithOverflow(this.date, 0L, paramLong, 0L, 0L); }
  
  ChronoLocalDateTimeImpl<D> plusSeconds(long paramLong) { return plusWithOverflow(this.date, 0L, 0L, paramLong, 0L); }
  
  private ChronoLocalDateTimeImpl<D> plusNanos(long paramLong) { return plusWithOverflow(this.date, 0L, 0L, 0L, paramLong); }
  
  private ChronoLocalDateTimeImpl<D> plusWithOverflow(D paramD, long paramLong1, long paramLong2, long paramLong3, long paramLong4) {
    if ((paramLong1 | paramLong2 | paramLong3 | paramLong4) == 0L)
      return with(paramD, this.time); 
    long l1 = paramLong4 / 86400000000000L + paramLong3 / 86400L + paramLong2 / 1440L + paramLong1 / 24L;
    long l2 = paramLong4 % 86400000000000L + paramLong3 % 86400L * 1000000000L + paramLong2 % 1440L * 60000000000L + paramLong1 % 24L * 3600000000000L;
    long l3 = this.time.toNanoOfDay();
    l2 += l3;
    l1 += Math.floorDiv(l2, 86400000000000L);
    long l4 = Math.floorMod(l2, 86400000000000L);
    LocalTime localTime = (l4 == l3) ? this.time : LocalTime.ofNanoOfDay(l4);
    return with(paramD.plus(l1, ChronoUnit.DAYS), localTime);
  }
  
  public ChronoZonedDateTime<D> atZone(ZoneId paramZoneId) { return ChronoZonedDateTimeImpl.ofBest(this, paramZoneId, null); }
  
  public long until(Temporal paramTemporal, TemporalUnit paramTemporalUnit) {
    Objects.requireNonNull(paramTemporal, "endExclusive");
    ChronoLocalDateTime chronoLocalDateTime = getChronology().localDateTime(paramTemporal);
    if (paramTemporalUnit instanceof ChronoUnit) {
      if (paramTemporalUnit.isTimeBased()) {
        long l = chronoLocalDateTime.getLong(ChronoField.EPOCH_DAY) - this.date.getLong(ChronoField.EPOCH_DAY);
        switch ((ChronoUnit)paramTemporalUnit) {
          case NANOS:
            l = Math.multiplyExact(l, 86400000000000L);
            break;
          case MICROS:
            l = Math.multiplyExact(l, 86400000000L);
            break;
          case MILLIS:
            l = Math.multiplyExact(l, 86400000L);
            break;
          case SECONDS:
            l = Math.multiplyExact(l, 86400L);
            break;
          case MINUTES:
            l = Math.multiplyExact(l, 1440L);
            break;
          case HOURS:
            l = Math.multiplyExact(l, 24L);
            break;
          case HALF_DAYS:
            l = Math.multiplyExact(l, 2L);
            break;
        } 
        return Math.addExact(l, this.time.until(chronoLocalDateTime.toLocalTime(), paramTemporalUnit));
      } 
      ChronoLocalDate chronoLocalDate = chronoLocalDateTime.toLocalDate();
      if (chronoLocalDateTime.toLocalTime().isBefore(this.time))
        chronoLocalDate = chronoLocalDate.minus(1L, ChronoUnit.DAYS); 
      return this.date.until(chronoLocalDate, paramTemporalUnit);
    } 
    Objects.requireNonNull(paramTemporalUnit, "unit");
    return paramTemporalUnit.between(this, chronoLocalDateTime);
  }
  
  private Object writeReplace() { return new Ser((byte)2, this); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Deserialization via serialization delegate"); }
  
  void writeExternal(ObjectOutput paramObjectOutput) throws IOException {
    paramObjectOutput.writeObject(this.date);
    paramObjectOutput.writeObject(this.time);
  }
  
  static ChronoLocalDateTime<?> readExternal(ObjectInput paramObjectInput) throws IOException, ClassNotFoundException {
    ChronoLocalDate chronoLocalDate = (ChronoLocalDate)paramObjectInput.readObject();
    LocalTime localTime = (LocalTime)paramObjectInput.readObject();
    return chronoLocalDate.atTime(localTime);
  }
  
  public boolean equals(Object paramObject) { return (this == paramObject) ? true : ((paramObject instanceof ChronoLocalDateTime) ? ((compareTo((ChronoLocalDateTime)paramObject) == 0)) : false); }
  
  public int hashCode() { return toLocalDate().hashCode() ^ toLocalTime().hashCode(); }
  
  public String toString() { return toLocalDate().toString() + 'T' + toLocalTime().toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\chrono\ChronoLocalDateTimeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */