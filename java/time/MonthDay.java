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
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Objects;

public final class MonthDay extends Object implements TemporalAccessor, TemporalAdjuster, Comparable<MonthDay>, Serializable {
  private static final long serialVersionUID = -939150713474957432L;
  
  private static final DateTimeFormatter PARSER = (new DateTimeFormatterBuilder()).appendLiteral("--").appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).toFormatter();
  
  private final int month;
  
  private final int day;
  
  public static MonthDay now() { return now(Clock.systemDefaultZone()); }
  
  public static MonthDay now(ZoneId paramZoneId) { return now(Clock.system(paramZoneId)); }
  
  public static MonthDay now(Clock paramClock) {
    LocalDate localDate = LocalDate.now(paramClock);
    return of(localDate.getMonth(), localDate.getDayOfMonth());
  }
  
  public static MonthDay of(Month paramMonth, int paramInt) {
    Objects.requireNonNull(paramMonth, "month");
    ChronoField.DAY_OF_MONTH.checkValidValue(paramInt);
    if (paramInt > paramMonth.maxLength())
      throw new DateTimeException("Illegal value for DayOfMonth field, value " + paramInt + " is not valid for month " + paramMonth.name()); 
    return new MonthDay(paramMonth.getValue(), paramInt);
  }
  
  public static MonthDay of(int paramInt1, int paramInt2) { return of(Month.of(paramInt1), paramInt2); }
  
  public static MonthDay from(TemporalAccessor paramTemporalAccessor) {
    if (paramTemporalAccessor instanceof MonthDay)
      return (MonthDay)paramTemporalAccessor; 
    try {
      if (!IsoChronology.INSTANCE.equals(Chronology.from(paramTemporalAccessor)))
        paramTemporalAccessor = LocalDate.from(paramTemporalAccessor); 
      return of(paramTemporalAccessor.get(ChronoField.MONTH_OF_YEAR), paramTemporalAccessor.get(ChronoField.DAY_OF_MONTH));
    } catch (DateTimeException dateTimeException) {
      throw new DateTimeException("Unable to obtain MonthDay from TemporalAccessor: " + paramTemporalAccessor + " of type " + paramTemporalAccessor.getClass().getName(), dateTimeException);
    } 
  }
  
  public static MonthDay parse(CharSequence paramCharSequence) { return parse(paramCharSequence, PARSER); }
  
  public static MonthDay parse(CharSequence paramCharSequence, DateTimeFormatter paramDateTimeFormatter) {
    Objects.requireNonNull(paramDateTimeFormatter, "formatter");
    return (MonthDay)paramDateTimeFormatter.parse(paramCharSequence, MonthDay::from);
  }
  
  private MonthDay(int paramInt1, int paramInt2) {
    this.month = paramInt1;
    this.day = paramInt2;
  }
  
  public boolean isSupported(TemporalField paramTemporalField) { return (paramTemporalField instanceof ChronoField) ? ((paramTemporalField == ChronoField.MONTH_OF_YEAR || paramTemporalField == ChronoField.DAY_OF_MONTH)) : ((paramTemporalField != null && paramTemporalField.isSupportedBy(this))); }
  
  public ValueRange range(TemporalField paramTemporalField) { return (paramTemporalField == ChronoField.MONTH_OF_YEAR) ? paramTemporalField.range() : ((paramTemporalField == ChronoField.DAY_OF_MONTH) ? ValueRange.of(1L, getMonth().minLength(), getMonth().maxLength()) : super.range(paramTemporalField)); }
  
  public int get(TemporalField paramTemporalField) { return range(paramTemporalField).checkValidIntValue(getLong(paramTemporalField), paramTemporalField); }
  
  public long getLong(TemporalField paramTemporalField) {
    if (paramTemporalField instanceof ChronoField) {
      switch ((ChronoField)paramTemporalField) {
        case DAY_OF_MONTH:
          return this.day;
        case MONTH_OF_YEAR:
          return this.month;
      } 
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
    } 
    return paramTemporalField.getFrom(this);
  }
  
  public int getMonthValue() { return this.month; }
  
  public Month getMonth() { return Month.of(this.month); }
  
  public int getDayOfMonth() { return this.day; }
  
  public boolean isValidYear(int paramInt) { return !((this.day == 29 && this.month == 2 && !Year.isLeap(paramInt)) ? 1 : 0); }
  
  public MonthDay withMonth(int paramInt) { return with(Month.of(paramInt)); }
  
  public MonthDay with(Month paramMonth) {
    Objects.requireNonNull(paramMonth, "month");
    if (paramMonth.getValue() == this.month)
      return this; 
    int i = Math.min(this.day, paramMonth.maxLength());
    return new MonthDay(paramMonth.getValue(), i);
  }
  
  public MonthDay withDayOfMonth(int paramInt) { return (paramInt == this.day) ? this : of(this.month, paramInt); }
  
  public <R> R query(TemporalQuery<R> paramTemporalQuery) { return (paramTemporalQuery == TemporalQueries.chronology()) ? (R)IsoChronology.INSTANCE : (R)super.query(paramTemporalQuery); }
  
  public Temporal adjustInto(Temporal paramTemporal) {
    if (!Chronology.from(paramTemporal).equals(IsoChronology.INSTANCE))
      throw new DateTimeException("Adjustment only supported on ISO date-time"); 
    paramTemporal = paramTemporal.with(ChronoField.MONTH_OF_YEAR, this.month);
    return paramTemporal.with(ChronoField.DAY_OF_MONTH, Math.min(paramTemporal.range(ChronoField.DAY_OF_MONTH).getMaximum(), this.day));
  }
  
  public String format(DateTimeFormatter paramDateTimeFormatter) {
    Objects.requireNonNull(paramDateTimeFormatter, "formatter");
    return paramDateTimeFormatter.format(this);
  }
  
  public LocalDate atYear(int paramInt) { return LocalDate.of(paramInt, this.month, isValidYear(paramInt) ? this.day : 28); }
  
  public int compareTo(MonthDay paramMonthDay) {
    int i = this.month - paramMonthDay.month;
    if (i == 0)
      i = this.day - paramMonthDay.day; 
    return i;
  }
  
  public boolean isAfter(MonthDay paramMonthDay) { return (compareTo(paramMonthDay) > 0); }
  
  public boolean isBefore(MonthDay paramMonthDay) { return (compareTo(paramMonthDay) < 0); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof MonthDay) {
      MonthDay monthDay = (MonthDay)paramObject;
      return (this.month == monthDay.month && this.day == monthDay.day);
    } 
    return false;
  }
  
  public int hashCode() { return (this.month << 6) + this.day; }
  
  public String toString() { return 10.toString(); }
  
  private Object writeReplace() { return new Ser((byte)13, this); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Deserialization via serialization delegate"); }
  
  void writeExternal(DataOutput paramDataOutput) throws IOException {
    paramDataOutput.writeByte(this.month);
    paramDataOutput.writeByte(this.day);
  }
  
  static MonthDay readExternal(DataInput paramDataInput) throws IOException {
    byte b1 = paramDataInput.readByte();
    byte b2 = paramDataInput.readByte();
    return of(b1, b2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\MonthDay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */