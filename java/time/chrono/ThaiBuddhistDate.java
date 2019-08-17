package java.time.chrono;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Objects;

public final class ThaiBuddhistDate extends ChronoLocalDateImpl<ThaiBuddhistDate> implements ChronoLocalDate, Serializable {
  private static final long serialVersionUID = -8722293800195731463L;
  
  private final LocalDate isoDate;
  
  public static ThaiBuddhistDate now() { return now(Clock.systemDefaultZone()); }
  
  public static ThaiBuddhistDate now(ZoneId paramZoneId) { return now(Clock.system(paramZoneId)); }
  
  public static ThaiBuddhistDate now(Clock paramClock) { return new ThaiBuddhistDate(LocalDate.now(paramClock)); }
  
  public static ThaiBuddhistDate of(int paramInt1, int paramInt2, int paramInt3) { return new ThaiBuddhistDate(LocalDate.of(paramInt1 - 543, paramInt2, paramInt3)); }
  
  public static ThaiBuddhistDate from(TemporalAccessor paramTemporalAccessor) { return ThaiBuddhistChronology.INSTANCE.date(paramTemporalAccessor); }
  
  ThaiBuddhistDate(LocalDate paramLocalDate) {
    Objects.requireNonNull(paramLocalDate, "isoDate");
    this.isoDate = paramLocalDate;
  }
  
  public ThaiBuddhistChronology getChronology() { return ThaiBuddhistChronology.INSTANCE; }
  
  public ThaiBuddhistEra getEra() { return (getProlepticYear() >= 1) ? ThaiBuddhistEra.BE : ThaiBuddhistEra.BEFORE_BE; }
  
  public int lengthOfMonth() { return this.isoDate.lengthOfMonth(); }
  
  public ValueRange range(TemporalField paramTemporalField) {
    if (paramTemporalField instanceof ChronoField) {
      if (isSupported(paramTemporalField)) {
        long l;
        ValueRange valueRange;
        ChronoField chronoField = (ChronoField)paramTemporalField;
        switch (chronoField) {
          case DAY_OF_MONTH:
          case DAY_OF_YEAR:
          case ALIGNED_WEEK_OF_MONTH:
            return this.isoDate.range(paramTemporalField);
          case YEAR_OF_ERA:
            valueRange = ChronoField.YEAR.range();
            l = (getProlepticYear() <= 0) ? (-(valueRange.getMinimum() + 543L) + 1L) : (valueRange.getMaximum() + 543L);
            return ValueRange.of(1L, l);
        } 
        return getChronology().range(chronoField);
      } 
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
    } 
    return paramTemporalField.rangeRefinedBy(this);
  }
  
  public long getLong(TemporalField paramTemporalField) {
    if (paramTemporalField instanceof ChronoField) {
      int i;
      switch ((ChronoField)paramTemporalField) {
        case PROLEPTIC_MONTH:
          return getProlepticMonth();
        case YEAR_OF_ERA:
          i = getProlepticYear();
          return ((i >= 1) ? i : (1 - i));
        case YEAR:
          return getProlepticYear();
        case ERA:
          return ((getProlepticYear() >= 1) ? true : false);
      } 
      return this.isoDate.getLong(paramTemporalField);
    } 
    return paramTemporalField.getFrom(this);
  }
  
  private long getProlepticMonth() { return getProlepticYear() * 12L + this.isoDate.getMonthValue() - 1L; }
  
  private int getProlepticYear() { return this.isoDate.getYear() + 543; }
  
  public ThaiBuddhistDate with(TemporalField paramTemporalField, long paramLong) {
    if (paramTemporalField instanceof ChronoField) {
      int i;
      ChronoField chronoField = (ChronoField)paramTemporalField;
      if (getLong(chronoField) == paramLong)
        return this; 
      switch (chronoField) {
        case PROLEPTIC_MONTH:
          getChronology().range(chronoField).checkValidValue(paramLong, chronoField);
          return plusMonths(paramLong - getProlepticMonth());
        case YEAR_OF_ERA:
        case YEAR:
        case ERA:
          i = getChronology().range(chronoField).checkValidIntValue(paramLong, chronoField);
          switch (chronoField) {
            case YEAR_OF_ERA:
              return with(this.isoDate.withYear(((getProlepticYear() >= 1) ? i : (1 - i)) - 543));
            case YEAR:
              return with(this.isoDate.withYear(i - 543));
            case ERA:
              return with(this.isoDate.withYear(1 - getProlepticYear() - 543));
          } 
          break;
      } 
      return with(this.isoDate.with(paramTemporalField, paramLong));
    } 
    return (ThaiBuddhistDate)super.with(paramTemporalField, paramLong);
  }
  
  public ThaiBuddhistDate with(TemporalAdjuster paramTemporalAdjuster) { return (ThaiBuddhistDate)super.with(paramTemporalAdjuster); }
  
  public ThaiBuddhistDate plus(TemporalAmount paramTemporalAmount) { return (ThaiBuddhistDate)super.plus(paramTemporalAmount); }
  
  public ThaiBuddhistDate minus(TemporalAmount paramTemporalAmount) { return (ThaiBuddhistDate)super.minus(paramTemporalAmount); }
  
  ThaiBuddhistDate plusYears(long paramLong) { return with(this.isoDate.plusYears(paramLong)); }
  
  ThaiBuddhistDate plusMonths(long paramLong) { return with(this.isoDate.plusMonths(paramLong)); }
  
  ThaiBuddhistDate plusWeeks(long paramLong) { return (ThaiBuddhistDate)super.plusWeeks(paramLong); }
  
  ThaiBuddhistDate plusDays(long paramLong) { return with(this.isoDate.plusDays(paramLong)); }
  
  public ThaiBuddhistDate plus(long paramLong, TemporalUnit paramTemporalUnit) { return (ThaiBuddhistDate)super.plus(paramLong, paramTemporalUnit); }
  
  public ThaiBuddhistDate minus(long paramLong, TemporalUnit paramTemporalUnit) { return (ThaiBuddhistDate)super.minus(paramLong, paramTemporalUnit); }
  
  ThaiBuddhistDate minusYears(long paramLong) { return (ThaiBuddhistDate)super.minusYears(paramLong); }
  
  ThaiBuddhistDate minusMonths(long paramLong) { return (ThaiBuddhistDate)super.minusMonths(paramLong); }
  
  ThaiBuddhistDate minusWeeks(long paramLong) { return (ThaiBuddhistDate)super.minusWeeks(paramLong); }
  
  ThaiBuddhistDate minusDays(long paramLong) { return (ThaiBuddhistDate)super.minusDays(paramLong); }
  
  private ThaiBuddhistDate with(LocalDate paramLocalDate) { return paramLocalDate.equals(this.isoDate) ? this : new ThaiBuddhistDate(paramLocalDate); }
  
  public final ChronoLocalDateTime<ThaiBuddhistDate> atTime(LocalTime paramLocalTime) { return super.atTime(paramLocalTime); }
  
  public ChronoPeriod until(ChronoLocalDate paramChronoLocalDate) {
    Period period = this.isoDate.until(paramChronoLocalDate);
    return getChronology().period(period.getYears(), period.getMonths(), period.getDays());
  }
  
  public long toEpochDay() { return this.isoDate.toEpochDay(); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof ThaiBuddhistDate) {
      ThaiBuddhistDate thaiBuddhistDate = (ThaiBuddhistDate)paramObject;
      return this.isoDate.equals(thaiBuddhistDate.isoDate);
    } 
    return false;
  }
  
  public int hashCode() { return getChronology().getId().hashCode() ^ this.isoDate.hashCode(); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Deserialization via serialization delegate"); }
  
  private Object writeReplace() { return new Ser((byte)8, this); }
  
  void writeExternal(DataOutput paramDataOutput) throws IOException {
    paramDataOutput.writeInt(get(ChronoField.YEAR));
    paramDataOutput.writeByte(get(ChronoField.MONTH_OF_YEAR));
    paramDataOutput.writeByte(get(ChronoField.DAY_OF_MONTH));
  }
  
  static ThaiBuddhistDate readExternal(DataInput paramDataInput) throws IOException {
    int i = paramDataInput.readInt();
    byte b1 = paramDataInput.readByte();
    byte b2 = paramDataInput.readByte();
    return ThaiBuddhistChronology.INSTANCE.date(i, b1, b2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\chrono\ThaiBuddhistDate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */