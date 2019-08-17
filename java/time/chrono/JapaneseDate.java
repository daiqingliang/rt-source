package java.time.chrono;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
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
import java.util.Calendar;
import java.util.Objects;
import sun.util.calendar.CalendarDate;
import sun.util.calendar.Era;
import sun.util.calendar.LocalGregorianCalendar;

public final class JapaneseDate extends ChronoLocalDateImpl<JapaneseDate> implements ChronoLocalDate, Serializable {
  private static final long serialVersionUID = -305327627230580483L;
  
  private final LocalDate isoDate;
  
  private JapaneseEra era;
  
  private int yearOfEra;
  
  static final LocalDate MEIJI_6_ISODATE = LocalDate.of(1873, 1, 1);
  
  public static JapaneseDate now() { return now(Clock.systemDefaultZone()); }
  
  public static JapaneseDate now(ZoneId paramZoneId) { return now(Clock.system(paramZoneId)); }
  
  public static JapaneseDate now(Clock paramClock) { return new JapaneseDate(LocalDate.now(paramClock)); }
  
  public static JapaneseDate of(JapaneseEra paramJapaneseEra, int paramInt1, int paramInt2, int paramInt3) {
    Objects.requireNonNull(paramJapaneseEra, "era");
    LocalGregorianCalendar.Date date = JapaneseChronology.JCAL.newCalendarDate(null);
    date.setEra(paramJapaneseEra.getPrivateEra()).setDate(paramInt1, paramInt2, paramInt3);
    if (!JapaneseChronology.JCAL.validate(date))
      throw new DateTimeException("year, month, and day not valid for Era"); 
    LocalDate localDate = LocalDate.of(date.getNormalizedYear(), paramInt2, paramInt3);
    return new JapaneseDate(paramJapaneseEra, paramInt1, localDate);
  }
  
  public static JapaneseDate of(int paramInt1, int paramInt2, int paramInt3) { return new JapaneseDate(LocalDate.of(paramInt1, paramInt2, paramInt3)); }
  
  static JapaneseDate ofYearDay(JapaneseEra paramJapaneseEra, int paramInt1, int paramInt2) {
    Objects.requireNonNull(paramJapaneseEra, "era");
    CalendarDate calendarDate = paramJapaneseEra.getPrivateEra().getSinceDate();
    LocalGregorianCalendar.Date date = JapaneseChronology.JCAL.newCalendarDate(null);
    date.setEra(paramJapaneseEra.getPrivateEra());
    if (paramInt1 == 1) {
      date.setDate(paramInt1, calendarDate.getMonth(), calendarDate.getDayOfMonth() + paramInt2 - 1);
    } else {
      date.setDate(paramInt1, 1, paramInt2);
    } 
    JapaneseChronology.JCAL.normalize(date);
    if (paramJapaneseEra.getPrivateEra() != date.getEra() || paramInt1 != date.getYear())
      throw new DateTimeException("Invalid parameters"); 
    LocalDate localDate = LocalDate.of(date.getNormalizedYear(), date.getMonth(), date.getDayOfMonth());
    return new JapaneseDate(paramJapaneseEra, paramInt1, localDate);
  }
  
  public static JapaneseDate from(TemporalAccessor paramTemporalAccessor) { return JapaneseChronology.INSTANCE.date(paramTemporalAccessor); }
  
  JapaneseDate(LocalDate paramLocalDate) {
    if (paramLocalDate.isBefore(MEIJI_6_ISODATE))
      throw new DateTimeException("JapaneseDate before Meiji 6 is not supported"); 
    LocalGregorianCalendar.Date date = toPrivateJapaneseDate(paramLocalDate);
    this.era = JapaneseEra.toJapaneseEra(date.getEra());
    this.yearOfEra = date.getYear();
    this.isoDate = paramLocalDate;
  }
  
  JapaneseDate(JapaneseEra paramJapaneseEra, int paramInt, LocalDate paramLocalDate) {
    if (paramLocalDate.isBefore(MEIJI_6_ISODATE))
      throw new DateTimeException("JapaneseDate before Meiji 6 is not supported"); 
    this.era = paramJapaneseEra;
    this.yearOfEra = paramInt;
    this.isoDate = paramLocalDate;
  }
  
  public JapaneseChronology getChronology() { return JapaneseChronology.INSTANCE; }
  
  public JapaneseEra getEra() { return this.era; }
  
  public int lengthOfMonth() { return this.isoDate.lengthOfMonth(); }
  
  public int lengthOfYear() {
    Calendar calendar = Calendar.getInstance(JapaneseChronology.LOCALE);
    calendar.set(0, this.era.getValue() + 2);
    calendar.set(this.yearOfEra, this.isoDate.getMonthValue() - 1, this.isoDate.getDayOfMonth());
    return calendar.getActualMaximum(6);
  }
  
  public boolean isSupported(TemporalField paramTemporalField) { return (paramTemporalField == ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH || paramTemporalField == ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR || paramTemporalField == ChronoField.ALIGNED_WEEK_OF_MONTH || paramTemporalField == ChronoField.ALIGNED_WEEK_OF_YEAR) ? false : super.isSupported(paramTemporalField); }
  
  public ValueRange range(TemporalField paramTemporalField) {
    if (paramTemporalField instanceof ChronoField) {
      if (isSupported(paramTemporalField)) {
        Calendar calendar;
        ChronoField chronoField = (ChronoField)paramTemporalField;
        switch (chronoField) {
          case DAY_OF_MONTH:
            return ValueRange.of(1L, lengthOfMonth());
          case DAY_OF_YEAR:
            return ValueRange.of(1L, lengthOfYear());
          case YEAR_OF_ERA:
            calendar = Calendar.getInstance(JapaneseChronology.LOCALE);
            calendar.set(0, this.era.getValue() + 2);
            calendar.set(this.yearOfEra, this.isoDate.getMonthValue() - 1, this.isoDate.getDayOfMonth());
            return ValueRange.of(1L, calendar.getActualMaximum(1));
        } 
        return getChronology().range(chronoField);
      } 
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
    } 
    return paramTemporalField.rangeRefinedBy(this);
  }
  
  public long getLong(TemporalField paramTemporalField) {
    if (paramTemporalField instanceof ChronoField) {
      Calendar calendar;
      switch ((ChronoField)paramTemporalField) {
        case ALIGNED_DAY_OF_WEEK_IN_MONTH:
        case ALIGNED_DAY_OF_WEEK_IN_YEAR:
        case ALIGNED_WEEK_OF_MONTH:
        case ALIGNED_WEEK_OF_YEAR:
          throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
        case YEAR_OF_ERA:
          return this.yearOfEra;
        case ERA:
          return this.era.getValue();
        case DAY_OF_YEAR:
          calendar = Calendar.getInstance(JapaneseChronology.LOCALE);
          calendar.set(0, this.era.getValue() + 2);
          calendar.set(this.yearOfEra, this.isoDate.getMonthValue() - 1, this.isoDate.getDayOfMonth());
          return calendar.get(6);
      } 
      return this.isoDate.getLong(paramTemporalField);
    } 
    return paramTemporalField.getFrom(this);
  }
  
  private static LocalGregorianCalendar.Date toPrivateJapaneseDate(LocalDate paramLocalDate) {
    LocalGregorianCalendar.Date date = JapaneseChronology.JCAL.newCalendarDate(null);
    Era era1 = JapaneseEra.privateEraFrom(paramLocalDate);
    int i = paramLocalDate.getYear();
    if (era1 != null)
      i -= era1.getSinceDate().getYear() - 1; 
    date.setEra(era1).setYear(i).setMonth(paramLocalDate.getMonthValue()).setDayOfMonth(paramLocalDate.getDayOfMonth());
    JapaneseChronology.JCAL.normalize(date);
    return date;
  }
  
  public JapaneseDate with(TemporalField paramTemporalField, long paramLong) {
    if (paramTemporalField instanceof ChronoField) {
      int i;
      ChronoField chronoField = (ChronoField)paramTemporalField;
      if (getLong(chronoField) == paramLong)
        return this; 
      switch (chronoField) {
        case YEAR_OF_ERA:
        case ERA:
        case YEAR:
          i = getChronology().range(chronoField).checkValidIntValue(paramLong, chronoField);
          switch (chronoField) {
            case YEAR_OF_ERA:
              return withYear(i);
            case YEAR:
              return with(this.isoDate.withYear(i));
            case ERA:
              return withYear(JapaneseEra.of(i), this.yearOfEra);
          } 
          break;
      } 
      return with(this.isoDate.with(paramTemporalField, paramLong));
    } 
    return (JapaneseDate)super.with(paramTemporalField, paramLong);
  }
  
  public JapaneseDate with(TemporalAdjuster paramTemporalAdjuster) { return (JapaneseDate)super.with(paramTemporalAdjuster); }
  
  public JapaneseDate plus(TemporalAmount paramTemporalAmount) { return (JapaneseDate)super.plus(paramTemporalAmount); }
  
  public JapaneseDate minus(TemporalAmount paramTemporalAmount) { return (JapaneseDate)super.minus(paramTemporalAmount); }
  
  private JapaneseDate withYear(JapaneseEra paramJapaneseEra, int paramInt) {
    int i = JapaneseChronology.INSTANCE.prolepticYear(paramJapaneseEra, paramInt);
    return with(this.isoDate.withYear(i));
  }
  
  private JapaneseDate withYear(int paramInt) { return withYear(getEra(), paramInt); }
  
  JapaneseDate plusYears(long paramLong) { return with(this.isoDate.plusYears(paramLong)); }
  
  JapaneseDate plusMonths(long paramLong) { return with(this.isoDate.plusMonths(paramLong)); }
  
  JapaneseDate plusWeeks(long paramLong) { return with(this.isoDate.plusWeeks(paramLong)); }
  
  JapaneseDate plusDays(long paramLong) { return with(this.isoDate.plusDays(paramLong)); }
  
  public JapaneseDate plus(long paramLong, TemporalUnit paramTemporalUnit) { return (JapaneseDate)super.plus(paramLong, paramTemporalUnit); }
  
  public JapaneseDate minus(long paramLong, TemporalUnit paramTemporalUnit) { return (JapaneseDate)super.minus(paramLong, paramTemporalUnit); }
  
  JapaneseDate minusYears(long paramLong) { return (JapaneseDate)super.minusYears(paramLong); }
  
  JapaneseDate minusMonths(long paramLong) { return (JapaneseDate)super.minusMonths(paramLong); }
  
  JapaneseDate minusWeeks(long paramLong) { return (JapaneseDate)super.minusWeeks(paramLong); }
  
  JapaneseDate minusDays(long paramLong) { return (JapaneseDate)super.minusDays(paramLong); }
  
  private JapaneseDate with(LocalDate paramLocalDate) { return paramLocalDate.equals(this.isoDate) ? this : new JapaneseDate(paramLocalDate); }
  
  public final ChronoLocalDateTime<JapaneseDate> atTime(LocalTime paramLocalTime) { return super.atTime(paramLocalTime); }
  
  public ChronoPeriod until(ChronoLocalDate paramChronoLocalDate) {
    Period period = this.isoDate.until(paramChronoLocalDate);
    return getChronology().period(period.getYears(), period.getMonths(), period.getDays());
  }
  
  public long toEpochDay() { return this.isoDate.toEpochDay(); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof JapaneseDate) {
      JapaneseDate japaneseDate = (JapaneseDate)paramObject;
      return this.isoDate.equals(japaneseDate.isoDate);
    } 
    return false;
  }
  
  public int hashCode() { return getChronology().getId().hashCode() ^ this.isoDate.hashCode(); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Deserialization via serialization delegate"); }
  
  private Object writeReplace() { return new Ser((byte)4, this); }
  
  void writeExternal(DataOutput paramDataOutput) throws IOException {
    paramDataOutput.writeInt(get(ChronoField.YEAR));
    paramDataOutput.writeByte(get(ChronoField.MONTH_OF_YEAR));
    paramDataOutput.writeByte(get(ChronoField.DAY_OF_MONTH));
  }
  
  static JapaneseDate readExternal(DataInput paramDataInput) throws IOException {
    int i = paramDataInput.readInt();
    byte b1 = paramDataInput.readByte();
    byte b2 = paramDataInput.readByte();
    return JapaneseChronology.INSTANCE.date(i, b1, b2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\chrono\JapaneseDate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */