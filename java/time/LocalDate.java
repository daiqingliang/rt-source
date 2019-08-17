package java.time;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoPeriod;
import java.time.chrono.Chronology;
import java.time.chrono.Era;
import java.time.chrono.IsoChronology;
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
import java.time.zone.ZoneOffsetTransition;
import java.time.zone.ZoneRules;
import java.util.Objects;

public final class LocalDate implements Temporal, TemporalAdjuster, ChronoLocalDate, Serializable {
  public static final LocalDate MIN;
  
  public static final LocalDate MAX = (MIN = of(-999999999, 1, 1)).of(999999999, 12, 31);
  
  private static final long serialVersionUID = 2942565459149668126L;
  
  private static final int DAYS_PER_CYCLE = 146097;
  
  static final long DAYS_0000_TO_1970 = 719528L;
  
  private final int year;
  
  private final short month;
  
  private final short day;
  
  public static LocalDate now() { return now(Clock.systemDefaultZone()); }
  
  public static LocalDate now(ZoneId paramZoneId) { return now(Clock.system(paramZoneId)); }
  
  public static LocalDate now(Clock paramClock) {
    Objects.requireNonNull(paramClock, "clock");
    Instant instant = paramClock.instant();
    ZoneOffset zoneOffset = paramClock.getZone().getRules().getOffset(instant);
    long l1 = instant.getEpochSecond() + zoneOffset.getTotalSeconds();
    long l2 = Math.floorDiv(l1, 86400L);
    return ofEpochDay(l2);
  }
  
  public static LocalDate of(int paramInt1, Month paramMonth, int paramInt2) {
    ChronoField.YEAR.checkValidValue(paramInt1);
    Objects.requireNonNull(paramMonth, "month");
    ChronoField.DAY_OF_MONTH.checkValidValue(paramInt2);
    return create(paramInt1, paramMonth.getValue(), paramInt2);
  }
  
  public static LocalDate of(int paramInt1, int paramInt2, int paramInt3) {
    ChronoField.YEAR.checkValidValue(paramInt1);
    ChronoField.MONTH_OF_YEAR.checkValidValue(paramInt2);
    ChronoField.DAY_OF_MONTH.checkValidValue(paramInt3);
    return create(paramInt1, paramInt2, paramInt3);
  }
  
  public static LocalDate ofYearDay(int paramInt1, int paramInt2) {
    ChronoField.YEAR.checkValidValue(paramInt1);
    ChronoField.DAY_OF_YEAR.checkValidValue(paramInt2);
    boolean bool = IsoChronology.INSTANCE.isLeapYear(paramInt1);
    if (paramInt2 == 366 && !bool)
      throw new DateTimeException("Invalid date 'DayOfYear 366' as '" + paramInt1 + "' is not a leap year"); 
    Month month1 = Month.of((paramInt2 - 1) / 31 + 1);
    int i = month1.firstDayOfYear(bool) + month1.length(bool) - 1;
    if (paramInt2 > i)
      month1 = month1.plus(1L); 
    int j = paramInt2 - month1.firstDayOfYear(bool) + 1;
    return new LocalDate(paramInt1, month1.getValue(), j);
  }
  
  public static LocalDate ofEpochDay(long paramLong) {
    long l1 = paramLong + 719528L;
    l1 -= 60L;
    long l2 = 0L;
    if (l1 < 0L) {
      long l = (l1 + 1L) / 146097L - 1L;
      l2 = l * 400L;
      l1 += -l * 146097L;
    } 
    long l3 = (400L * l1 + 591L) / 146097L;
    long l4 = l1 - 365L * l3 + l3 / 4L - l3 / 100L + l3 / 400L;
    if (l4 < 0L) {
      l3--;
      l4 = l1 - 365L * l3 + l3 / 4L - l3 / 100L + l3 / 400L;
    } 
    l3 += l2;
    int i = (int)l4;
    int j = (i * 5 + 2) / 153;
    int k = (j + 2) % 12 + 1;
    int m = i - (j * 306 + 5) / 10 + 1;
    l3 += (j / 10);
    int n = ChronoField.YEAR.checkValidIntValue(l3);
    return new LocalDate(n, k, m);
  }
  
  public static LocalDate from(TemporalAccessor paramTemporalAccessor) {
    Objects.requireNonNull(paramTemporalAccessor, "temporal");
    LocalDate localDate = (LocalDate)paramTemporalAccessor.query(TemporalQueries.localDate());
    if (localDate == null)
      throw new DateTimeException("Unable to obtain LocalDate from TemporalAccessor: " + paramTemporalAccessor + " of type " + paramTemporalAccessor.getClass().getName()); 
    return localDate;
  }
  
  public static LocalDate parse(CharSequence paramCharSequence) { return parse(paramCharSequence, DateTimeFormatter.ISO_LOCAL_DATE); }
  
  public static LocalDate parse(CharSequence paramCharSequence, DateTimeFormatter paramDateTimeFormatter) {
    Objects.requireNonNull(paramDateTimeFormatter, "formatter");
    return (LocalDate)paramDateTimeFormatter.parse(paramCharSequence, LocalDate::from);
  }
  
  private static LocalDate create(int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt3 > 28) {
      byte b = 31;
      switch (paramInt2) {
        case 2:
          b = IsoChronology.INSTANCE.isLeapYear(paramInt1) ? 29 : 28;
          break;
        case 4:
        case 6:
        case 9:
        case 11:
          b = 30;
          break;
      } 
      if (paramInt3 > b) {
        if (paramInt3 == 29)
          throw new DateTimeException("Invalid date 'February 29' as '" + paramInt1 + "' is not a leap year"); 
        throw new DateTimeException("Invalid date '" + Month.of(paramInt2).name() + " " + paramInt3 + "'");
      } 
    } 
    return new LocalDate(paramInt1, paramInt2, paramInt3);
  }
  
  private static LocalDate resolvePreviousValid(int paramInt1, int paramInt2, int paramInt3) {
    switch (paramInt2) {
      case 2:
        paramInt3 = Math.min(paramInt3, IsoChronology.INSTANCE.isLeapYear(paramInt1) ? 29 : 28);
        break;
      case 4:
      case 6:
      case 9:
      case 11:
        paramInt3 = Math.min(paramInt3, 30);
        break;
    } 
    return new LocalDate(paramInt1, paramInt2, paramInt3);
  }
  
  private LocalDate(int paramInt1, int paramInt2, int paramInt3) {
    this.year = paramInt1;
    this.month = (short)paramInt2;
    this.day = (short)paramInt3;
  }
  
  public boolean isSupported(TemporalField paramTemporalField) { return super.isSupported(paramTemporalField); }
  
  public boolean isSupported(TemporalUnit paramTemporalUnit) { return super.isSupported(paramTemporalUnit); }
  
  public ValueRange range(TemporalField paramTemporalField) {
    if (paramTemporalField instanceof ChronoField) {
      ChronoField chronoField = (ChronoField)paramTemporalField;
      if (chronoField.isDateBased()) {
        switch (chronoField) {
          case DAYS:
            return ValueRange.of(1L, lengthOfMonth());
          case WEEKS:
            return ValueRange.of(1L, lengthOfYear());
          case MONTHS:
            return ValueRange.of(1L, (getMonth() == Month.FEBRUARY && !isLeapYear()) ? 4L : 5L);
          case YEARS:
            return (getYear() <= 0) ? ValueRange.of(1L, 1000000000L) : ValueRange.of(1L, 999999999L);
        } 
        return paramTemporalField.range();
      } 
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
    } 
    return paramTemporalField.rangeRefinedBy(this);
  }
  
  public int get(TemporalField paramTemporalField) { return (paramTemporalField instanceof ChronoField) ? get0(paramTemporalField) : super.get(paramTemporalField); }
  
  public long getLong(TemporalField paramTemporalField) { return (paramTemporalField instanceof ChronoField) ? ((paramTemporalField == ChronoField.EPOCH_DAY) ? toEpochDay() : ((paramTemporalField == ChronoField.PROLEPTIC_MONTH) ? getProlepticMonth() : get0(paramTemporalField))) : paramTemporalField.getFrom(this); }
  
  private int get0(TemporalField paramTemporalField) {
    switch ((ChronoField)paramTemporalField) {
      case DECADES:
        return getDayOfWeek().getValue();
      case CENTURIES:
        return (this.day - 1) % 7 + 1;
      case MILLENNIA:
        return (getDayOfYear() - 1) % 7 + 1;
      case DAYS:
        return this.day;
      case WEEKS:
        return getDayOfYear();
      case ERAS:
        throw new UnsupportedTemporalTypeException("Invalid field 'EpochDay' for get() method, use getLong() instead");
      case MONTHS:
        return (this.day - 1) / 7 + 1;
      case null:
        return (getDayOfYear() - 1) / 7 + 1;
      case null:
        return this.month;
      case null:
        throw new UnsupportedTemporalTypeException("Invalid field 'ProlepticMonth' for get() method, use getLong() instead");
      case YEARS:
        return (this.year >= 1) ? this.year : (1 - this.year);
      case null:
        return this.year;
      case null:
        return (this.year >= 1) ? 1 : 0;
    } 
    throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
  }
  
  private long getProlepticMonth() { return this.year * 12L + this.month - 1L; }
  
  public IsoChronology getChronology() { return IsoChronology.INSTANCE; }
  
  public Era getEra() { return super.getEra(); }
  
  public int getYear() { return this.year; }
  
  public int getMonthValue() { return this.month; }
  
  public Month getMonth() { return Month.of(this.month); }
  
  public int getDayOfMonth() { return this.day; }
  
  public int getDayOfYear() { return getMonth().firstDayOfYear(isLeapYear()) + this.day - 1; }
  
  public DayOfWeek getDayOfWeek() {
    int i = (int)Math.floorMod(toEpochDay() + 3L, 7L);
    return DayOfWeek.of(i + 1);
  }
  
  public boolean isLeapYear() { return IsoChronology.INSTANCE.isLeapYear(this.year); }
  
  public int lengthOfMonth() {
    switch (this.month) {
      case 2:
        return isLeapYear() ? 29 : 28;
      case 4:
      case 6:
      case 9:
      case 11:
        return 30;
    } 
    return 31;
  }
  
  public int lengthOfYear() { return isLeapYear() ? 366 : 365; }
  
  public LocalDate with(TemporalAdjuster paramTemporalAdjuster) { return (paramTemporalAdjuster instanceof LocalDate) ? (LocalDate)paramTemporalAdjuster : (LocalDate)paramTemporalAdjuster.adjustInto(this); }
  
  public LocalDate with(TemporalField paramTemporalField, long paramLong) {
    if (paramTemporalField instanceof ChronoField) {
      ChronoField chronoField = (ChronoField)paramTemporalField;
      chronoField.checkValidValue(paramLong);
      switch (chronoField) {
        case DECADES:
          return plusDays(paramLong - getDayOfWeek().getValue());
        case CENTURIES:
          return plusDays(paramLong - getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH));
        case MILLENNIA:
          return plusDays(paramLong - getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR));
        case DAYS:
          return withDayOfMonth((int)paramLong);
        case WEEKS:
          return withDayOfYear((int)paramLong);
        case ERAS:
          return ofEpochDay(paramLong);
        case MONTHS:
          return plusWeeks(paramLong - getLong(ChronoField.ALIGNED_WEEK_OF_MONTH));
        case null:
          return plusWeeks(paramLong - getLong(ChronoField.ALIGNED_WEEK_OF_YEAR));
        case null:
          return withMonth((int)paramLong);
        case null:
          return plusMonths(paramLong - getProlepticMonth());
        case YEARS:
          return withYear((int)((this.year >= 1) ? paramLong : (1L - paramLong)));
        case null:
          return withYear((int)paramLong);
        case null:
          return (getLong(ChronoField.ERA) == paramLong) ? this : withYear(1 - this.year);
      } 
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
    } 
    return (LocalDate)paramTemporalField.adjustInto(this, paramLong);
  }
  
  public LocalDate withYear(int paramInt) {
    if (this.year == paramInt)
      return this; 
    ChronoField.YEAR.checkValidValue(paramInt);
    return resolvePreviousValid(paramInt, this.month, this.day);
  }
  
  public LocalDate withMonth(int paramInt) {
    if (this.month == paramInt)
      return this; 
    ChronoField.MONTH_OF_YEAR.checkValidValue(paramInt);
    return resolvePreviousValid(this.year, paramInt, this.day);
  }
  
  public LocalDate withDayOfMonth(int paramInt) { return (this.day == paramInt) ? this : of(this.year, this.month, paramInt); }
  
  public LocalDate withDayOfYear(int paramInt) { return (getDayOfYear() == paramInt) ? this : ofYearDay(this.year, paramInt); }
  
  public LocalDate plus(TemporalAmount paramTemporalAmount) {
    if (paramTemporalAmount instanceof Period) {
      Period period = (Period)paramTemporalAmount;
      return plusMonths(period.toTotalMonths()).plusDays(period.getDays());
    } 
    Objects.requireNonNull(paramTemporalAmount, "amountToAdd");
    return (LocalDate)paramTemporalAmount.addTo(this);
  }
  
  public LocalDate plus(long paramLong, TemporalUnit paramTemporalUnit) {
    if (paramTemporalUnit instanceof ChronoUnit) {
      ChronoUnit chronoUnit = (ChronoUnit)paramTemporalUnit;
      switch (chronoUnit) {
        case DAYS:
          return plusDays(paramLong);
        case WEEKS:
          return plusWeeks(paramLong);
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
    return (LocalDate)paramTemporalUnit.addTo(this, paramLong);
  }
  
  public LocalDate plusYears(long paramLong) {
    if (paramLong == 0L)
      return this; 
    int i = ChronoField.YEAR.checkValidIntValue(this.year + paramLong);
    return resolvePreviousValid(i, this.month, this.day);
  }
  
  public LocalDate plusMonths(long paramLong) {
    if (paramLong == 0L)
      return this; 
    long l1 = this.year * 12L + (this.month - 1);
    long l2 = l1 + paramLong;
    int i = ChronoField.YEAR.checkValidIntValue(Math.floorDiv(l2, 12L));
    int j = (int)Math.floorMod(l2, 12L) + 1;
    return resolvePreviousValid(i, j, this.day);
  }
  
  public LocalDate plusWeeks(long paramLong) { return plusDays(Math.multiplyExact(paramLong, 7L)); }
  
  public LocalDate plusDays(long paramLong) {
    if (paramLong == 0L)
      return this; 
    long l = Math.addExact(toEpochDay(), paramLong);
    return ofEpochDay(l);
  }
  
  public LocalDate minus(TemporalAmount paramTemporalAmount) {
    if (paramTemporalAmount instanceof Period) {
      Period period = (Period)paramTemporalAmount;
      return minusMonths(period.toTotalMonths()).minusDays(period.getDays());
    } 
    Objects.requireNonNull(paramTemporalAmount, "amountToSubtract");
    return (LocalDate)paramTemporalAmount.subtractFrom(this);
  }
  
  public LocalDate minus(long paramLong, TemporalUnit paramTemporalUnit) { return (paramLong == Float.MIN_VALUE) ? plus(Float.MAX_VALUE, paramTemporalUnit).plus(1L, paramTemporalUnit) : plus(-paramLong, paramTemporalUnit); }
  
  public LocalDate minusYears(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusYears(Float.MAX_VALUE).plusYears(1L) : plusYears(-paramLong); }
  
  public LocalDate minusMonths(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusMonths(Float.MAX_VALUE).plusMonths(1L) : plusMonths(-paramLong); }
  
  public LocalDate minusWeeks(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusWeeks(Float.MAX_VALUE).plusWeeks(1L) : plusWeeks(-paramLong); }
  
  public LocalDate minusDays(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusDays(Float.MAX_VALUE).plusDays(1L) : plusDays(-paramLong); }
  
  public <R> R query(TemporalQuery<R> paramTemporalQuery) { return (paramTemporalQuery == TemporalQueries.localDate()) ? (R)this : (R)super.query(paramTemporalQuery); }
  
  public Temporal adjustInto(Temporal paramTemporal) { return super.adjustInto(paramTemporal); }
  
  public long until(Temporal paramTemporal, TemporalUnit paramTemporalUnit) {
    LocalDate localDate = from(paramTemporal);
    if (paramTemporalUnit instanceof ChronoUnit) {
      switch ((ChronoUnit)paramTemporalUnit) {
        case DAYS:
          return daysUntil(localDate);
        case WEEKS:
          return daysUntil(localDate) / 7L;
        case MONTHS:
          return monthsUntil(localDate);
        case YEARS:
          return monthsUntil(localDate) / 12L;
        case DECADES:
          return monthsUntil(localDate) / 120L;
        case CENTURIES:
          return monthsUntil(localDate) / 1200L;
        case MILLENNIA:
          return monthsUntil(localDate) / 12000L;
        case ERAS:
          return localDate.getLong(ChronoField.ERA) - getLong(ChronoField.ERA);
      } 
      throw new UnsupportedTemporalTypeException("Unsupported unit: " + paramTemporalUnit);
    } 
    return paramTemporalUnit.between(this, localDate);
  }
  
  long daysUntil(LocalDate paramLocalDate) { return paramLocalDate.toEpochDay() - toEpochDay(); }
  
  private long monthsUntil(LocalDate paramLocalDate) {
    long l1 = getProlepticMonth() * 32L + getDayOfMonth();
    long l2 = paramLocalDate.getProlepticMonth() * 32L + paramLocalDate.getDayOfMonth();
    return (l2 - l1) / 32L;
  }
  
  public Period until(ChronoLocalDate paramChronoLocalDate) {
    LocalDate localDate = from(paramChronoLocalDate);
    long l1 = localDate.getProlepticMonth() - getProlepticMonth();
    int i = localDate.day - this.day;
    if (l1 > 0L && i < 0) {
      l1--;
      LocalDate localDate1 = plusMonths(l1);
      i = (int)(localDate.toEpochDay() - localDate1.toEpochDay());
    } else if (l1 < 0L && i > 0) {
      l1++;
      i -= localDate.lengthOfMonth();
    } 
    long l2 = l1 / 12L;
    int j = (int)(l1 % 12L);
    return Period.of(Math.toIntExact(l2), j, i);
  }
  
  public String format(DateTimeFormatter paramDateTimeFormatter) {
    Objects.requireNonNull(paramDateTimeFormatter, "formatter");
    return paramDateTimeFormatter.format(this);
  }
  
  public LocalDateTime atTime(LocalTime paramLocalTime) { return LocalDateTime.of(this, paramLocalTime); }
  
  public LocalDateTime atTime(int paramInt1, int paramInt2) { return atTime(LocalTime.of(paramInt1, paramInt2)); }
  
  public LocalDateTime atTime(int paramInt1, int paramInt2, int paramInt3) { return atTime(LocalTime.of(paramInt1, paramInt2, paramInt3)); }
  
  public LocalDateTime atTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return atTime(LocalTime.of(paramInt1, paramInt2, paramInt3, paramInt4)); }
  
  public OffsetDateTime atTime(OffsetTime paramOffsetTime) { return OffsetDateTime.of(LocalDateTime.of(this, paramOffsetTime.toLocalTime()), paramOffsetTime.getOffset()); }
  
  public LocalDateTime atStartOfDay() { return LocalDateTime.of(this, LocalTime.MIDNIGHT); }
  
  public ZonedDateTime atStartOfDay(ZoneId paramZoneId) {
    Objects.requireNonNull(paramZoneId, "zone");
    LocalDateTime localDateTime = atTime(LocalTime.MIDNIGHT);
    if (!(paramZoneId instanceof ZoneOffset)) {
      ZoneRules zoneRules = paramZoneId.getRules();
      ZoneOffsetTransition zoneOffsetTransition = zoneRules.getTransition(localDateTime);
      if (zoneOffsetTransition != null && zoneOffsetTransition.isGap())
        localDateTime = zoneOffsetTransition.getDateTimeAfter(); 
    } 
    return ZonedDateTime.of(localDateTime, paramZoneId);
  }
  
  public long toEpochDay() {
    long l1 = this.year;
    long l2 = this.month;
    long l3 = 0L;
    l3 += 365L * l1;
    if (l1 >= 0L) {
      l3 += (l1 + 3L) / 4L - (l1 + 99L) / 100L + (l1 + 399L) / 400L;
    } else {
      l3 -= l1 / -4L - l1 / -100L + l1 / -400L;
    } 
    l3 += (367L * l2 - 362L) / 12L;
    l3 += (this.day - 1);
    if (l2 > 2L) {
      l3--;
      if (!isLeapYear())
        l3--; 
    } 
    return l3 - 719528L;
  }
  
  public int compareTo(ChronoLocalDate paramChronoLocalDate) { return (paramChronoLocalDate instanceof LocalDate) ? compareTo0((LocalDate)paramChronoLocalDate) : super.compareTo(paramChronoLocalDate); }
  
  int compareTo0(LocalDate paramLocalDate) {
    int i = this.year - paramLocalDate.year;
    if (i == 0) {
      i = this.month - paramLocalDate.month;
      if (i == 0)
        i = this.day - paramLocalDate.day; 
    } 
    return i;
  }
  
  public boolean isAfter(ChronoLocalDate paramChronoLocalDate) { return (paramChronoLocalDate instanceof LocalDate) ? ((compareTo0((LocalDate)paramChronoLocalDate) > 0)) : super.isAfter(paramChronoLocalDate); }
  
  public boolean isBefore(ChronoLocalDate paramChronoLocalDate) { return (paramChronoLocalDate instanceof LocalDate) ? ((compareTo0((LocalDate)paramChronoLocalDate) < 0)) : super.isBefore(paramChronoLocalDate); }
  
  public boolean isEqual(ChronoLocalDate paramChronoLocalDate) { return (paramChronoLocalDate instanceof LocalDate) ? ((compareTo0((LocalDate)paramChronoLocalDate) == 0)) : super.isEqual(paramChronoLocalDate); }
  
  public boolean equals(Object paramObject) { return (this == paramObject) ? true : ((paramObject instanceof LocalDate) ? ((compareTo0((LocalDate)paramObject) == 0)) : false); }
  
  public int hashCode() {
    int i = this.year;
    short s1 = this.month;
    short s2 = this.day;
    return i & 0xFFFFF800 ^ (i << 11) + (s1 << 6) + s2;
  }
  
  public String toString() {
    int i = this.year;
    short s1 = this.month;
    short s2 = this.day;
    int j = Math.abs(i);
    StringBuilder stringBuilder = new StringBuilder(10);
    if (j < 1000) {
      if (i < 0) {
        stringBuilder.append(i - 10000).deleteCharAt(1);
      } else {
        stringBuilder.append(i + 10000).deleteCharAt(0);
      } 
    } else {
      if (i > 9999)
        stringBuilder.append('+'); 
      stringBuilder.append(i);
    } 
    return stringBuilder.append((s1 < 10) ? "-0" : "-").append(s1).append((s2 < 10) ? "-0" : "-").append(s2).toString();
  }
  
  private Object writeReplace() { return new Ser((byte)3, this); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Deserialization via serialization delegate"); }
  
  void writeExternal(DataOutput paramDataOutput) throws IOException {
    paramDataOutput.writeInt(this.year);
    paramDataOutput.writeByte(this.month);
    paramDataOutput.writeByte(this.day);
  }
  
  static LocalDate readExternal(DataInput paramDataInput) throws IOException {
    int i = paramDataInput.readInt();
    byte b1 = paramDataInput.readByte();
    byte b2 = paramDataInput.readByte();
    return of(i, b1, b2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\LocalDate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */