package java.time.chrono;

import java.io.Serializable;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Objects;

abstract class ChronoLocalDateImpl<D extends ChronoLocalDate> extends Object implements ChronoLocalDate, Temporal, TemporalAdjuster, Serializable {
  private static final long serialVersionUID = 6282433883239719096L;
  
  static <D extends ChronoLocalDate> D ensureValid(Chronology paramChronology, Temporal paramTemporal) {
    ChronoLocalDate chronoLocalDate = (ChronoLocalDate)paramTemporal;
    if (!paramChronology.equals(chronoLocalDate.getChronology()))
      throw new ClassCastException("Chronology mismatch, expected: " + paramChronology.getId() + ", actual: " + chronoLocalDate.getChronology().getId()); 
    return (D)chronoLocalDate;
  }
  
  public D with(TemporalAdjuster paramTemporalAdjuster) { return (D)super.with(paramTemporalAdjuster); }
  
  public D with(TemporalField paramTemporalField, long paramLong) { return (D)super.with(paramTemporalField, paramLong); }
  
  public D plus(TemporalAmount paramTemporalAmount) { return (D)super.plus(paramTemporalAmount); }
  
  public D plus(long paramLong, TemporalUnit paramTemporalUnit) {
    if (paramTemporalUnit instanceof ChronoUnit) {
      ChronoUnit chronoUnit = (ChronoUnit)paramTemporalUnit;
      switch (chronoUnit) {
        case DAYS:
          return (D)plusDays(paramLong);
        case WEEKS:
          return (D)plusDays(Math.multiplyExact(paramLong, 7L));
        case MONTHS:
          return (D)plusMonths(paramLong);
        case YEARS:
          return (D)plusYears(paramLong);
        case DECADES:
          return (D)plusYears(Math.multiplyExact(paramLong, 10L));
        case CENTURIES:
          return (D)plusYears(Math.multiplyExact(paramLong, 100L));
        case MILLENNIA:
          return (D)plusYears(Math.multiplyExact(paramLong, 1000L));
        case ERAS:
          return (D)with(ChronoField.ERA, Math.addExact(getLong(ChronoField.ERA), paramLong));
      } 
      throw new UnsupportedTemporalTypeException("Unsupported unit: " + paramTemporalUnit);
    } 
    return (D)super.plus(paramLong, paramTemporalUnit);
  }
  
  public D minus(TemporalAmount paramTemporalAmount) { return (D)super.minus(paramTemporalAmount); }
  
  public D minus(long paramLong, TemporalUnit paramTemporalUnit) { return (D)super.minus(paramLong, paramTemporalUnit); }
  
  abstract D plusYears(long paramLong);
  
  abstract D plusMonths(long paramLong);
  
  D plusWeeks(long paramLong) { return (D)plusDays(Math.multiplyExact(paramLong, 7L)); }
  
  abstract D plusDays(long paramLong);
  
  D minusYears(long paramLong) { return (D)((paramLong == Float.MIN_VALUE) ? ((ChronoLocalDateImpl)plusYears(Float.MAX_VALUE)).plusYears(1L) : plusYears(-paramLong)); }
  
  D minusMonths(long paramLong) { return (D)((paramLong == Float.MIN_VALUE) ? ((ChronoLocalDateImpl)plusMonths(Float.MAX_VALUE)).plusMonths(1L) : plusMonths(-paramLong)); }
  
  D minusWeeks(long paramLong) { return (D)((paramLong == Float.MIN_VALUE) ? ((ChronoLocalDateImpl)plusWeeks(Float.MAX_VALUE)).plusWeeks(1L) : plusWeeks(-paramLong)); }
  
  D minusDays(long paramLong) { return (D)((paramLong == Float.MIN_VALUE) ? ((ChronoLocalDateImpl)plusDays(Float.MAX_VALUE)).plusDays(1L) : plusDays(-paramLong)); }
  
  public long until(Temporal paramTemporal, TemporalUnit paramTemporalUnit) {
    Objects.requireNonNull(paramTemporal, "endExclusive");
    ChronoLocalDate chronoLocalDate = getChronology().date(paramTemporal);
    if (paramTemporalUnit instanceof ChronoUnit) {
      switch ((ChronoUnit)paramTemporalUnit) {
        case DAYS:
          return daysUntil(chronoLocalDate);
        case WEEKS:
          return daysUntil(chronoLocalDate) / 7L;
        case MONTHS:
          return monthsUntil(chronoLocalDate);
        case YEARS:
          return monthsUntil(chronoLocalDate) / 12L;
        case DECADES:
          return monthsUntil(chronoLocalDate) / 120L;
        case CENTURIES:
          return monthsUntil(chronoLocalDate) / 1200L;
        case MILLENNIA:
          return monthsUntil(chronoLocalDate) / 12000L;
        case ERAS:
          return chronoLocalDate.getLong(ChronoField.ERA) - getLong(ChronoField.ERA);
      } 
      throw new UnsupportedTemporalTypeException("Unsupported unit: " + paramTemporalUnit);
    } 
    Objects.requireNonNull(paramTemporalUnit, "unit");
    return paramTemporalUnit.between(this, chronoLocalDate);
  }
  
  private long daysUntil(ChronoLocalDate paramChronoLocalDate) { return paramChronoLocalDate.toEpochDay() - toEpochDay(); }
  
  private long monthsUntil(ChronoLocalDate paramChronoLocalDate) {
    ValueRange valueRange = getChronology().range(ChronoField.MONTH_OF_YEAR);
    if (valueRange.getMaximum() != 12L)
      throw new IllegalStateException("ChronoLocalDateImpl only supports Chronologies with 12 months per year"); 
    long l1 = getLong(ChronoField.PROLEPTIC_MONTH) * 32L + get(ChronoField.DAY_OF_MONTH);
    long l2 = paramChronoLocalDate.getLong(ChronoField.PROLEPTIC_MONTH) * 32L + paramChronoLocalDate.get(ChronoField.DAY_OF_MONTH);
    return (l2 - l1) / 32L;
  }
  
  public boolean equals(Object paramObject) { return (this == paramObject) ? true : ((paramObject instanceof ChronoLocalDate) ? ((compareTo((ChronoLocalDate)paramObject) == 0)) : false); }
  
  public int hashCode() {
    long l = toEpochDay();
    return getChronology().hashCode() ^ (int)(l ^ l >>> 32);
  }
  
  public String toString() {
    long l1 = getLong(ChronoField.YEAR_OF_ERA);
    long l2 = getLong(ChronoField.MONTH_OF_YEAR);
    long l3 = getLong(ChronoField.DAY_OF_MONTH);
    StringBuilder stringBuilder = new StringBuilder(30);
    stringBuilder.append(getChronology().toString()).append(" ").append(getEra()).append(" ").append(l1).append((l2 < 10L) ? "-0" : "-").append(l2).append((l3 < 10L) ? "-0" : "-").append(l3);
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\chrono\ChronoLocalDateImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */