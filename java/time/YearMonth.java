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

public final class YearMonth extends Object implements Temporal, TemporalAdjuster, Comparable<YearMonth>, Serializable {
  private static final long serialVersionUID = 4183400860270640070L;
  
  private static final DateTimeFormatter PARSER = (new DateTimeFormatterBuilder()).appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).toFormatter();
  
  private final int year;
  
  private final int month;
  
  public static YearMonth now() { return now(Clock.systemDefaultZone()); }
  
  public static YearMonth now(ZoneId paramZoneId) { return now(Clock.system(paramZoneId)); }
  
  public static YearMonth now(Clock paramClock) {
    LocalDate localDate = LocalDate.now(paramClock);
    return of(localDate.getYear(), localDate.getMonth());
  }
  
  public static YearMonth of(int paramInt, Month paramMonth) {
    Objects.requireNonNull(paramMonth, "month");
    return of(paramInt, paramMonth.getValue());
  }
  
  public static YearMonth of(int paramInt1, int paramInt2) {
    ChronoField.YEAR.checkValidValue(paramInt1);
    ChronoField.MONTH_OF_YEAR.checkValidValue(paramInt2);
    return new YearMonth(paramInt1, paramInt2);
  }
  
  public static YearMonth from(TemporalAccessor paramTemporalAccessor) {
    if (paramTemporalAccessor instanceof YearMonth)
      return (YearMonth)paramTemporalAccessor; 
    Objects.requireNonNull(paramTemporalAccessor, "temporal");
    try {
      if (!IsoChronology.INSTANCE.equals(Chronology.from(paramTemporalAccessor)))
        paramTemporalAccessor = LocalDate.from(paramTemporalAccessor); 
      return of(paramTemporalAccessor.get(ChronoField.YEAR), paramTemporalAccessor.get(ChronoField.MONTH_OF_YEAR));
    } catch (DateTimeException dateTimeException) {
      throw new DateTimeException("Unable to obtain YearMonth from TemporalAccessor: " + paramTemporalAccessor + " of type " + paramTemporalAccessor.getClass().getName(), dateTimeException);
    } 
  }
  
  public static YearMonth parse(CharSequence paramCharSequence) { return parse(paramCharSequence, PARSER); }
  
  public static YearMonth parse(CharSequence paramCharSequence, DateTimeFormatter paramDateTimeFormatter) {
    Objects.requireNonNull(paramDateTimeFormatter, "formatter");
    return (YearMonth)paramDateTimeFormatter.parse(paramCharSequence, YearMonth::from);
  }
  
  private YearMonth(int paramInt1, int paramInt2) {
    this.year = paramInt1;
    this.month = paramInt2;
  }
  
  private YearMonth with(int paramInt1, int paramInt2) { return (this.year == paramInt1 && this.month == paramInt2) ? this : new YearMonth(paramInt1, paramInt2); }
  
  public boolean isSupported(TemporalField paramTemporalField) { return (paramTemporalField instanceof ChronoField) ? ((paramTemporalField == ChronoField.YEAR || paramTemporalField == ChronoField.MONTH_OF_YEAR || paramTemporalField == ChronoField.PROLEPTIC_MONTH || paramTemporalField == ChronoField.YEAR_OF_ERA || paramTemporalField == ChronoField.ERA)) : ((paramTemporalField != null && paramTemporalField.isSupportedBy(this))); }
  
  public boolean isSupported(TemporalUnit paramTemporalUnit) { return (paramTemporalUnit instanceof ChronoUnit) ? ((paramTemporalUnit == ChronoUnit.MONTHS || paramTemporalUnit == ChronoUnit.YEARS || paramTemporalUnit == ChronoUnit.DECADES || paramTemporalUnit == ChronoUnit.CENTURIES || paramTemporalUnit == ChronoUnit.MILLENNIA || paramTemporalUnit == ChronoUnit.ERAS)) : ((paramTemporalUnit != null && paramTemporalUnit.isSupportedBy(this))); }
  
  public ValueRange range(TemporalField paramTemporalField) { return (paramTemporalField == ChronoField.YEAR_OF_ERA) ? ((getYear() <= 0) ? ValueRange.of(1L, 1000000000L) : ValueRange.of(1L, 999999999L)) : super.range(paramTemporalField); }
  
  public int get(TemporalField paramTemporalField) { return range(paramTemporalField).checkValidIntValue(getLong(paramTemporalField), paramTemporalField); }
  
  public long getLong(TemporalField paramTemporalField) {
    if (paramTemporalField instanceof ChronoField) {
      switch ((ChronoField)paramTemporalField) {
        case MONTHS:
          return this.month;
        case YEARS:
          return getProlepticMonth();
        case DECADES:
          return ((this.year < 1) ? (1 - this.year) : this.year);
        case CENTURIES:
          return this.year;
        case MILLENNIA:
          return ((this.year < 1) ? false : true);
      } 
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
    } 
    return paramTemporalField.getFrom(this);
  }
  
  private long getProlepticMonth() { return this.year * 12L + this.month - 1L; }
  
  public int getYear() { return this.year; }
  
  public int getMonthValue() { return this.month; }
  
  public Month getMonth() { return Month.of(this.month); }
  
  public boolean isLeapYear() { return IsoChronology.INSTANCE.isLeapYear(this.year); }
  
  public boolean isValidDay(int paramInt) { return (paramInt >= 1 && paramInt <= lengthOfMonth()); }
  
  public int lengthOfMonth() { return getMonth().length(isLeapYear()); }
  
  public int lengthOfYear() { return isLeapYear() ? 366 : 365; }
  
  public YearMonth with(TemporalAdjuster paramTemporalAdjuster) { return (YearMonth)paramTemporalAdjuster.adjustInto(this); }
  
  public YearMonth with(TemporalField paramTemporalField, long paramLong) {
    if (paramTemporalField instanceof ChronoField) {
      ChronoField chronoField = (ChronoField)paramTemporalField;
      chronoField.checkValidValue(paramLong);
      switch (chronoField) {
        case MONTHS:
          return withMonth((int)paramLong);
        case YEARS:
          return plusMonths(paramLong - getProlepticMonth());
        case DECADES:
          return withYear((int)((this.year < 1) ? (1L - paramLong) : paramLong));
        case CENTURIES:
          return withYear((int)paramLong);
        case MILLENNIA:
          return (getLong(ChronoField.ERA) == paramLong) ? this : withYear(1 - this.year);
      } 
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
    } 
    return (YearMonth)paramTemporalField.adjustInto(this, paramLong);
  }
  
  public YearMonth withYear(int paramInt) {
    ChronoField.YEAR.checkValidValue(paramInt);
    return with(paramInt, this.month);
  }
  
  public YearMonth withMonth(int paramInt) {
    ChronoField.MONTH_OF_YEAR.checkValidValue(paramInt);
    return with(this.year, paramInt);
  }
  
  public YearMonth plus(TemporalAmount paramTemporalAmount) { return (YearMonth)paramTemporalAmount.addTo(this); }
  
  public YearMonth plus(long paramLong, TemporalUnit paramTemporalUnit) {
    if (paramTemporalUnit instanceof ChronoUnit) {
      switch ((ChronoUnit)paramTemporalUnit) {
        case MONTHS:
          return plusMonths(paramLong);
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
    return (YearMonth)paramTemporalUnit.addTo(this, paramLong);
  }
  
  public YearMonth plusYears(long paramLong) {
    if (paramLong == 0L)
      return this; 
    int i = ChronoField.YEAR.checkValidIntValue(this.year + paramLong);
    return with(i, this.month);
  }
  
  public YearMonth plusMonths(long paramLong) {
    if (paramLong == 0L)
      return this; 
    long l1 = this.year * 12L + (this.month - 1);
    long l2 = l1 + paramLong;
    int i = ChronoField.YEAR.checkValidIntValue(Math.floorDiv(l2, 12L));
    int j = (int)Math.floorMod(l2, 12L) + 1;
    return with(i, j);
  }
  
  public YearMonth minus(TemporalAmount paramTemporalAmount) { return (YearMonth)paramTemporalAmount.subtractFrom(this); }
  
  public YearMonth minus(long paramLong, TemporalUnit paramTemporalUnit) { return (paramLong == Float.MIN_VALUE) ? plus(Float.MAX_VALUE, paramTemporalUnit).plus(1L, paramTemporalUnit) : plus(-paramLong, paramTemporalUnit); }
  
  public YearMonth minusYears(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusYears(Float.MAX_VALUE).plusYears(1L) : plusYears(-paramLong); }
  
  public YearMonth minusMonths(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusMonths(Float.MAX_VALUE).plusMonths(1L) : plusMonths(-paramLong); }
  
  public <R> R query(TemporalQuery<R> paramTemporalQuery) { return (paramTemporalQuery == TemporalQueries.chronology()) ? (R)IsoChronology.INSTANCE : ((paramTemporalQuery == TemporalQueries.precision()) ? (R)ChronoUnit.MONTHS : (R)super.query(paramTemporalQuery)); }
  
  public Temporal adjustInto(Temporal paramTemporal) {
    if (!Chronology.from(paramTemporal).equals(IsoChronology.INSTANCE))
      throw new DateTimeException("Adjustment only supported on ISO date-time"); 
    return paramTemporal.with(ChronoField.PROLEPTIC_MONTH, getProlepticMonth());
  }
  
  public long until(Temporal paramTemporal, TemporalUnit paramTemporalUnit) {
    YearMonth yearMonth = from(paramTemporal);
    if (paramTemporalUnit instanceof ChronoUnit) {
      long l = yearMonth.getProlepticMonth() - getProlepticMonth();
      switch ((ChronoUnit)paramTemporalUnit) {
        case MONTHS:
          return l;
        case YEARS:
          return l / 12L;
        case DECADES:
          return l / 120L;
        case CENTURIES:
          return l / 1200L;
        case MILLENNIA:
          return l / 12000L;
        case ERAS:
          return yearMonth.getLong(ChronoField.ERA) - getLong(ChronoField.ERA);
      } 
      throw new UnsupportedTemporalTypeException("Unsupported unit: " + paramTemporalUnit);
    } 
    return paramTemporalUnit.between(this, yearMonth);
  }
  
  public String format(DateTimeFormatter paramDateTimeFormatter) {
    Objects.requireNonNull(paramDateTimeFormatter, "formatter");
    return paramDateTimeFormatter.format(this);
  }
  
  public LocalDate atDay(int paramInt) { return LocalDate.of(this.year, this.month, paramInt); }
  
  public LocalDate atEndOfMonth() { return LocalDate.of(this.year, this.month, lengthOfMonth()); }
  
  public int compareTo(YearMonth paramYearMonth) {
    int i = this.year - paramYearMonth.year;
    if (i == 0)
      i = this.month - paramYearMonth.month; 
    return i;
  }
  
  public boolean isAfter(YearMonth paramYearMonth) { return (compareTo(paramYearMonth) > 0); }
  
  public boolean isBefore(YearMonth paramYearMonth) { return (compareTo(paramYearMonth) < 0); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof YearMonth) {
      YearMonth yearMonth = (YearMonth)paramObject;
      return (this.year == yearMonth.year && this.month == yearMonth.month);
    } 
    return false;
  }
  
  public int hashCode() { return this.year ^ this.month << 27; }
  
  public String toString() {
    int i = Math.abs(this.year);
    StringBuilder stringBuilder = new StringBuilder(9);
    if (i < 1000) {
      if (this.year < 0) {
        stringBuilder.append(this.year - 10000).deleteCharAt(1);
      } else {
        stringBuilder.append(this.year + 10000).deleteCharAt(0);
      } 
    } else {
      stringBuilder.append(this.year);
    } 
    return stringBuilder.append((this.month < 10) ? "-0" : "-").append(this.month).toString();
  }
  
  private Object writeReplace() { return new Ser((byte)12, this); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Deserialization via serialization delegate"); }
  
  void writeExternal(DataOutput paramDataOutput) throws IOException {
    paramDataOutput.writeInt(this.year);
    paramDataOutput.writeByte(this.month);
  }
  
  static YearMonth readExternal(DataInput paramDataInput) throws IOException {
    int i = paramDataInput.readInt();
    byte b = paramDataInput.readByte();
    return of(i, b);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\YearMonth.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */