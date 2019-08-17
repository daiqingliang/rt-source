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

public final class MinguoDate extends ChronoLocalDateImpl<MinguoDate> implements ChronoLocalDate, Serializable {
  private static final long serialVersionUID = 1300372329181994526L;
  
  private final LocalDate isoDate;
  
  public static MinguoDate now() { return now(Clock.systemDefaultZone()); }
  
  public static MinguoDate now(ZoneId paramZoneId) { return now(Clock.system(paramZoneId)); }
  
  public static MinguoDate now(Clock paramClock) { return new MinguoDate(LocalDate.now(paramClock)); }
  
  public static MinguoDate of(int paramInt1, int paramInt2, int paramInt3) { return new MinguoDate(LocalDate.of(paramInt1 + 1911, paramInt2, paramInt3)); }
  
  public static MinguoDate from(TemporalAccessor paramTemporalAccessor) { return MinguoChronology.INSTANCE.date(paramTemporalAccessor); }
  
  MinguoDate(LocalDate paramLocalDate) {
    Objects.requireNonNull(paramLocalDate, "isoDate");
    this.isoDate = paramLocalDate;
  }
  
  public MinguoChronology getChronology() { return MinguoChronology.INSTANCE; }
  
  public MinguoEra getEra() { return (getProlepticYear() >= 1) ? MinguoEra.ROC : MinguoEra.BEFORE_ROC; }
  
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
            l = (getProlepticYear() <= 0) ? (-valueRange.getMinimum() + 1L + 1911L) : (valueRange.getMaximum() - 1911L);
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
  
  private int getProlepticYear() { return this.isoDate.getYear() - 1911; }
  
  public MinguoDate with(TemporalField paramTemporalField, long paramLong) {
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
              return with(this.isoDate.withYear((getProlepticYear() >= 1) ? (i + 1911) : (1 - i + 1911)));
            case YEAR:
              return with(this.isoDate.withYear(i + 1911));
            case ERA:
              return with(this.isoDate.withYear(1 - getProlepticYear() + 1911));
          } 
          break;
      } 
      return with(this.isoDate.with(paramTemporalField, paramLong));
    } 
    return (MinguoDate)super.with(paramTemporalField, paramLong);
  }
  
  public MinguoDate with(TemporalAdjuster paramTemporalAdjuster) { return (MinguoDate)super.with(paramTemporalAdjuster); }
  
  public MinguoDate plus(TemporalAmount paramTemporalAmount) { return (MinguoDate)super.plus(paramTemporalAmount); }
  
  public MinguoDate minus(TemporalAmount paramTemporalAmount) { return (MinguoDate)super.minus(paramTemporalAmount); }
  
  MinguoDate plusYears(long paramLong) { return with(this.isoDate.plusYears(paramLong)); }
  
  MinguoDate plusMonths(long paramLong) { return with(this.isoDate.plusMonths(paramLong)); }
  
  MinguoDate plusWeeks(long paramLong) { return (MinguoDate)super.plusWeeks(paramLong); }
  
  MinguoDate plusDays(long paramLong) { return with(this.isoDate.plusDays(paramLong)); }
  
  public MinguoDate plus(long paramLong, TemporalUnit paramTemporalUnit) { return (MinguoDate)super.plus(paramLong, paramTemporalUnit); }
  
  public MinguoDate minus(long paramLong, TemporalUnit paramTemporalUnit) { return (MinguoDate)super.minus(paramLong, paramTemporalUnit); }
  
  MinguoDate minusYears(long paramLong) { return (MinguoDate)super.minusYears(paramLong); }
  
  MinguoDate minusMonths(long paramLong) { return (MinguoDate)super.minusMonths(paramLong); }
  
  MinguoDate minusWeeks(long paramLong) { return (MinguoDate)super.minusWeeks(paramLong); }
  
  MinguoDate minusDays(long paramLong) { return (MinguoDate)super.minusDays(paramLong); }
  
  private MinguoDate with(LocalDate paramLocalDate) { return paramLocalDate.equals(this.isoDate) ? this : new MinguoDate(paramLocalDate); }
  
  public final ChronoLocalDateTime<MinguoDate> atTime(LocalTime paramLocalTime) { return super.atTime(paramLocalTime); }
  
  public ChronoPeriod until(ChronoLocalDate paramChronoLocalDate) {
    Period period = this.isoDate.until(paramChronoLocalDate);
    return getChronology().period(period.getYears(), period.getMonths(), period.getDays());
  }
  
  public long toEpochDay() { return this.isoDate.toEpochDay(); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof MinguoDate) {
      MinguoDate minguoDate = (MinguoDate)paramObject;
      return this.isoDate.equals(minguoDate.isoDate);
    } 
    return false;
  }
  
  public int hashCode() { return getChronology().getId().hashCode() ^ this.isoDate.hashCode(); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Deserialization via serialization delegate"); }
  
  private Object writeReplace() { return new Ser((byte)7, this); }
  
  void writeExternal(DataOutput paramDataOutput) throws IOException {
    paramDataOutput.writeInt(get(ChronoField.YEAR));
    paramDataOutput.writeByte(get(ChronoField.MONTH_OF_YEAR));
    paramDataOutput.writeByte(get(ChronoField.DAY_OF_MONTH));
  }
  
  static MinguoDate readExternal(DataInput paramDataInput) throws IOException {
    int i = paramDataInput.readInt();
    byte b1 = paramDataInput.readByte();
    byte b2 = paramDataInput.readByte();
    return MinguoChronology.INSTANCE.date(i, b1, b2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\chrono\MinguoDate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */