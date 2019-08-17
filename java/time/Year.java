package java.time;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
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

public final class Year extends Object implements Temporal, TemporalAdjuster, Comparable<Year>, Serializable {
  public static final int MIN_VALUE = -999999999;
  
  public static final int MAX_VALUE = 999999999;
  
  private static final long serialVersionUID = -23038383694477807L;
  
  private static final DateTimeFormatter PARSER = (new DateTimeFormatterBuilder()).appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).toFormatter();
  
  private final int year;
  
  public static Year now() { return now(Clock.systemDefaultZone()); }
  
  public static Year now(ZoneId paramZoneId) { return now(Clock.system(paramZoneId)); }
  
  public static Year now(Clock paramClock) {
    LocalDate localDate = LocalDate.now(paramClock);
    return of(localDate.getYear());
  }
  
  public static Year of(int paramInt) {
    ChronoField.YEAR.checkValidValue(paramInt);
    return new Year(paramInt);
  }
  
  public static Year from(TemporalAccessor paramTemporalAccessor) {
    if (paramTemporalAccessor instanceof Year)
      return (Year)paramTemporalAccessor; 
    Objects.requireNonNull(paramTemporalAccessor, "temporal");
    try {
      if (!IsoChronology.INSTANCE.equals(Chronology.from(paramTemporalAccessor)))
        paramTemporalAccessor = LocalDate.from(paramTemporalAccessor); 
      return of(paramTemporalAccessor.get(ChronoField.YEAR));
    } catch (DateTimeException dateTimeException) {
      throw new DateTimeException("Unable to obtain Year from TemporalAccessor: " + paramTemporalAccessor + " of type " + paramTemporalAccessor.getClass().getName(), dateTimeException);
    } 
  }
  
  public static Year parse(CharSequence paramCharSequence) { return parse(paramCharSequence, PARSER); }
  
  public static Year parse(CharSequence paramCharSequence, DateTimeFormatter paramDateTimeFormatter) {
    Objects.requireNonNull(paramDateTimeFormatter, "formatter");
    return (Year)paramDateTimeFormatter.parse(paramCharSequence, Year::from);
  }
  
  public static boolean isLeap(long paramLong) { return ((paramLong & 0x3L) == 0L && (paramLong % 100L != 0L || paramLong % 400L == 0L)); }
  
  private Year(int paramInt) { this.year = paramInt; }
  
  public int getValue() { return this.year; }
  
  public boolean isSupported(TemporalField paramTemporalField) { return (paramTemporalField instanceof ChronoField) ? ((paramTemporalField == ChronoField.YEAR || paramTemporalField == ChronoField.YEAR_OF_ERA || paramTemporalField == ChronoField.ERA)) : ((paramTemporalField != null && paramTemporalField.isSupportedBy(this))); }
  
  public boolean isSupported(TemporalUnit paramTemporalUnit) { return (paramTemporalUnit instanceof ChronoUnit) ? ((paramTemporalUnit == ChronoUnit.YEARS || paramTemporalUnit == ChronoUnit.DECADES || paramTemporalUnit == ChronoUnit.CENTURIES || paramTemporalUnit == ChronoUnit.MILLENNIA || paramTemporalUnit == ChronoUnit.ERAS)) : ((paramTemporalUnit != null && paramTemporalUnit.isSupportedBy(this))); }
  
  public ValueRange range(TemporalField paramTemporalField) { return (paramTemporalField == ChronoField.YEAR_OF_ERA) ? ((this.year <= 0) ? ValueRange.of(1L, 1000000000L) : ValueRange.of(1L, 999999999L)) : super.range(paramTemporalField); }
  
  public int get(TemporalField paramTemporalField) { return range(paramTemporalField).checkValidIntValue(getLong(paramTemporalField), paramTemporalField); }
  
  public long getLong(TemporalField paramTemporalField) {
    if (paramTemporalField instanceof ChronoField) {
      switch ((ChronoField)paramTemporalField) {
        case YEARS:
          return ((this.year < 1) ? (1 - this.year) : this.year);
        case DECADES:
          return this.year;
        case CENTURIES:
          return ((this.year < 1) ? false : true);
      } 
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
    } 
    return paramTemporalField.getFrom(this);
  }
  
  public boolean isLeap() { return isLeap(this.year); }
  
  public boolean isValidMonthDay(MonthDay paramMonthDay) { return (paramMonthDay != null && paramMonthDay.isValidYear(this.year)); }
  
  public int length() { return isLeap() ? 366 : 365; }
  
  public Year with(TemporalAdjuster paramTemporalAdjuster) { return (Year)paramTemporalAdjuster.adjustInto(this); }
  
  public Year with(TemporalField paramTemporalField, long paramLong) {
    if (paramTemporalField instanceof ChronoField) {
      ChronoField chronoField = (ChronoField)paramTemporalField;
      chronoField.checkValidValue(paramLong);
      switch (chronoField) {
        case YEARS:
          return of((int)((this.year < 1) ? (1L - paramLong) : paramLong));
        case DECADES:
          return of((int)paramLong);
        case CENTURIES:
          return (getLong(ChronoField.ERA) == paramLong) ? this : of(1 - this.year);
      } 
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
    } 
    return (Year)paramTemporalField.adjustInto(this, paramLong);
  }
  
  public Year plus(TemporalAmount paramTemporalAmount) { return (Year)paramTemporalAmount.addTo(this); }
  
  public Year plus(long paramLong, TemporalUnit paramTemporalUnit) {
    if (paramTemporalUnit instanceof ChronoUnit) {
      switch ((ChronoUnit)paramTemporalUnit) {
        case YEARS:
          return plusYears(paramLong);
        case DECADES:
          return plusYears(Math.multiplyExact(paramLong, 10L));
        case CENTURIES:
          return plusYears(Math.multiplyExact(paramLong, 100L));
        case MILLENNIA:
          return plusYears(Math.multiplyExact(paramLong, 1000L));
        case ERAS:
          return with(ChronoField.ERA, Math.addExact(getLong(ChronoField.ERA), paramLong));
      } 
      throw new UnsupportedTemporalTypeException("Unsupported unit: " + paramTemporalUnit);
    } 
    return (Year)paramTemporalUnit.addTo(this, paramLong);
  }
  
  public Year plusYears(long paramLong) { return (paramLong == 0L) ? this : of(ChronoField.YEAR.checkValidIntValue(this.year + paramLong)); }
  
  public Year minus(TemporalAmount paramTemporalAmount) { return (Year)paramTemporalAmount.subtractFrom(this); }
  
  public Year minus(long paramLong, TemporalUnit paramTemporalUnit) { return (paramLong == Float.MIN_VALUE) ? plus(Float.MAX_VALUE, paramTemporalUnit).plus(1L, paramTemporalUnit) : plus(-paramLong, paramTemporalUnit); }
  
  public Year minusYears(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusYears(Float.MAX_VALUE).plusYears(1L) : plusYears(-paramLong); }
  
  public <R> R query(TemporalQuery<R> paramTemporalQuery) { return (paramTemporalQuery == TemporalQueries.chronology()) ? (R)IsoChronology.INSTANCE : ((paramTemporalQuery == TemporalQueries.precision()) ? (R)ChronoUnit.YEARS : (R)super.query(paramTemporalQuery)); }
  
  public Temporal adjustInto(Temporal paramTemporal) {
    if (!Chronology.from(paramTemporal).equals(IsoChronology.INSTANCE))
      throw new DateTimeException("Adjustment only supported on ISO date-time"); 
    return paramTemporal.with(ChronoField.YEAR, this.year);
  }
  
  public long until(Temporal paramTemporal, TemporalUnit paramTemporalUnit) {
    Year year1 = from(paramTemporal);
    if (paramTemporalUnit instanceof ChronoUnit) {
      long l = year1.year - this.year;
      switch ((ChronoUnit)paramTemporalUnit) {
        case YEARS:
          return l;
        case DECADES:
          return l / 10L;
        case CENTURIES:
          return l / 100L;
        case MILLENNIA:
          return l / 1000L;
        case ERAS:
          return year1.getLong(ChronoField.ERA) - getLong(ChronoField.ERA);
      } 
      throw new UnsupportedTemporalTypeException("Unsupported unit: " + paramTemporalUnit);
    } 
    return paramTemporalUnit.between(this, year1);
  }
  
  public String format(DateTimeFormatter paramDateTimeFormatter) {
    Objects.requireNonNull(paramDateTimeFormatter, "formatter");
    return paramDateTimeFormatter.format(this);
  }
  
  public LocalDate atDay(int paramInt) { return LocalDate.ofYearDay(this.year, paramInt); }
  
  public YearMonth atMonth(Month paramMonth) { return YearMonth.of(this.year, paramMonth); }
  
  public YearMonth atMonth(int paramInt) { return YearMonth.of(this.year, paramInt); }
  
  public LocalDate atMonthDay(MonthDay paramMonthDay) { return paramMonthDay.atYear(this.year); }
  
  public int compareTo(Year paramYear) { return this.year - paramYear.year; }
  
  public boolean isAfter(Year paramYear) { return (this.year > paramYear.year); }
  
  public boolean isBefore(Year paramYear) { return (this.year < paramYear.year); }
  
  public boolean equals(Object paramObject) { return (this == paramObject) ? true : ((paramObject instanceof Year) ? ((this.year == ((Year)paramObject).year)) : false); }
  
  public int hashCode() { return this.year; }
  
  public String toString() { return Integer.toString(this.year); }
  
  private Object writeReplace() { return new Ser((byte)11, this); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Deserialization via serialization delegate"); }
  
  void writeExternal(DataOutput paramDataOutput) throws IOException { paramDataOutput.writeInt(this.year); }
  
  static Year readExternal(DataInput paramDataInput) throws IOException { return of(paramDataInput.readInt()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\Year.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */