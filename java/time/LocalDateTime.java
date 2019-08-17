package java.time;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
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
import java.time.temporal.ValueRange;
import java.time.zone.ZoneRules;
import java.util.Objects;

public final class LocalDateTime extends Object implements Temporal, TemporalAdjuster, ChronoLocalDateTime<LocalDate>, Serializable {
  public static final LocalDateTime MIN;
  
  public static final LocalDateTime MAX = (MIN = of(LocalDate.MIN, LocalTime.MIN)).of(LocalDate.MAX, LocalTime.MAX);
  
  private static final long serialVersionUID = 6207766400415563566L;
  
  private final LocalDate date;
  
  private final LocalTime time;
  
  public static LocalDateTime now() { return now(Clock.systemDefaultZone()); }
  
  public static LocalDateTime now(ZoneId paramZoneId) { return now(Clock.system(paramZoneId)); }
  
  public static LocalDateTime now(Clock paramClock) {
    Objects.requireNonNull(paramClock, "clock");
    Instant instant = paramClock.instant();
    ZoneOffset zoneOffset = paramClock.getZone().getRules().getOffset(instant);
    return ofEpochSecond(instant.getEpochSecond(), instant.getNano(), zoneOffset);
  }
  
  public static LocalDateTime of(int paramInt1, Month paramMonth, int paramInt2, int paramInt3, int paramInt4) {
    LocalDate localDate = LocalDate.of(paramInt1, paramMonth, paramInt2);
    LocalTime localTime = LocalTime.of(paramInt3, paramInt4);
    return new LocalDateTime(localDate, localTime);
  }
  
  public static LocalDateTime of(int paramInt1, Month paramMonth, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    LocalDate localDate = LocalDate.of(paramInt1, paramMonth, paramInt2);
    LocalTime localTime = LocalTime.of(paramInt3, paramInt4, paramInt5);
    return new LocalDateTime(localDate, localTime);
  }
  
  public static LocalDateTime of(int paramInt1, Month paramMonth, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    LocalDate localDate = LocalDate.of(paramInt1, paramMonth, paramInt2);
    LocalTime localTime = LocalTime.of(paramInt3, paramInt4, paramInt5, paramInt6);
    return new LocalDateTime(localDate, localTime);
  }
  
  public static LocalDateTime of(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    LocalDate localDate = LocalDate.of(paramInt1, paramInt2, paramInt3);
    LocalTime localTime = LocalTime.of(paramInt4, paramInt5);
    return new LocalDateTime(localDate, localTime);
  }
  
  public static LocalDateTime of(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    LocalDate localDate = LocalDate.of(paramInt1, paramInt2, paramInt3);
    LocalTime localTime = LocalTime.of(paramInt4, paramInt5, paramInt6);
    return new LocalDateTime(localDate, localTime);
  }
  
  public static LocalDateTime of(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
    LocalDate localDate = LocalDate.of(paramInt1, paramInt2, paramInt3);
    LocalTime localTime = LocalTime.of(paramInt4, paramInt5, paramInt6, paramInt7);
    return new LocalDateTime(localDate, localTime);
  }
  
  public static LocalDateTime of(LocalDate paramLocalDate, LocalTime paramLocalTime) {
    Objects.requireNonNull(paramLocalDate, "date");
    Objects.requireNonNull(paramLocalTime, "time");
    return new LocalDateTime(paramLocalDate, paramLocalTime);
  }
  
  public static LocalDateTime ofInstant(Instant paramInstant, ZoneId paramZoneId) {
    Objects.requireNonNull(paramInstant, "instant");
    Objects.requireNonNull(paramZoneId, "zone");
    ZoneRules zoneRules = paramZoneId.getRules();
    ZoneOffset zoneOffset = zoneRules.getOffset(paramInstant);
    return ofEpochSecond(paramInstant.getEpochSecond(), paramInstant.getNano(), zoneOffset);
  }
  
  public static LocalDateTime ofEpochSecond(long paramLong, int paramInt, ZoneOffset paramZoneOffset) {
    Objects.requireNonNull(paramZoneOffset, "offset");
    ChronoField.NANO_OF_SECOND.checkValidValue(paramInt);
    long l1 = paramLong + paramZoneOffset.getTotalSeconds();
    long l2 = Math.floorDiv(l1, 86400L);
    int i = (int)Math.floorMod(l1, 86400L);
    LocalDate localDate = LocalDate.ofEpochDay(l2);
    LocalTime localTime = LocalTime.ofNanoOfDay(i * 1000000000L + paramInt);
    return new LocalDateTime(localDate, localTime);
  }
  
  public static LocalDateTime from(TemporalAccessor paramTemporalAccessor) {
    if (paramTemporalAccessor instanceof LocalDateTime)
      return (LocalDateTime)paramTemporalAccessor; 
    if (paramTemporalAccessor instanceof ZonedDateTime)
      return ((ZonedDateTime)paramTemporalAccessor).toLocalDateTime(); 
    if (paramTemporalAccessor instanceof OffsetDateTime)
      return ((OffsetDateTime)paramTemporalAccessor).toLocalDateTime(); 
    try {
      LocalDate localDate = LocalDate.from(paramTemporalAccessor);
      LocalTime localTime = LocalTime.from(paramTemporalAccessor);
      return new LocalDateTime(localDate, localTime);
    } catch (DateTimeException dateTimeException) {
      throw new DateTimeException("Unable to obtain LocalDateTime from TemporalAccessor: " + paramTemporalAccessor + " of type " + paramTemporalAccessor.getClass().getName(), dateTimeException);
    } 
  }
  
  public static LocalDateTime parse(CharSequence paramCharSequence) { return parse(paramCharSequence, DateTimeFormatter.ISO_LOCAL_DATE_TIME); }
  
  public static LocalDateTime parse(CharSequence paramCharSequence, DateTimeFormatter paramDateTimeFormatter) {
    Objects.requireNonNull(paramDateTimeFormatter, "formatter");
    return (LocalDateTime)paramDateTimeFormatter.parse(paramCharSequence, LocalDateTime::from);
  }
  
  private LocalDateTime(LocalDate paramLocalDate, LocalTime paramLocalTime) {
    this.date = paramLocalDate;
    this.time = paramLocalTime;
  }
  
  private LocalDateTime with(LocalDate paramLocalDate, LocalTime paramLocalTime) { return (this.date == paramLocalDate && this.time == paramLocalTime) ? this : new LocalDateTime(paramLocalDate, paramLocalTime); }
  
  public boolean isSupported(TemporalField paramTemporalField) {
    if (paramTemporalField instanceof ChronoField) {
      ChronoField chronoField = (ChronoField)paramTemporalField;
      return (chronoField.isDateBased() || chronoField.isTimeBased());
    } 
    return (paramTemporalField != null && paramTemporalField.isSupportedBy(this));
  }
  
  public boolean isSupported(TemporalUnit paramTemporalUnit) { return super.isSupported(paramTemporalUnit); }
  
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
    return super.get(paramTemporalField);
  }
  
  public long getLong(TemporalField paramTemporalField) {
    if (paramTemporalField instanceof ChronoField) {
      ChronoField chronoField = (ChronoField)paramTemporalField;
      return chronoField.isTimeBased() ? this.time.getLong(paramTemporalField) : this.date.getLong(paramTemporalField);
    } 
    return paramTemporalField.getFrom(this);
  }
  
  public LocalDate toLocalDate() { return this.date; }
  
  public int getYear() { return this.date.getYear(); }
  
  public int getMonthValue() { return this.date.getMonthValue(); }
  
  public Month getMonth() { return this.date.getMonth(); }
  
  public int getDayOfMonth() { return this.date.getDayOfMonth(); }
  
  public int getDayOfYear() { return this.date.getDayOfYear(); }
  
  public DayOfWeek getDayOfWeek() { return this.date.getDayOfWeek(); }
  
  public LocalTime toLocalTime() { return this.time; }
  
  public int getHour() { return this.time.getHour(); }
  
  public int getMinute() { return this.time.getMinute(); }
  
  public int getSecond() { return this.time.getSecond(); }
  
  public int getNano() { return this.time.getNano(); }
  
  public LocalDateTime with(TemporalAdjuster paramTemporalAdjuster) { return (paramTemporalAdjuster instanceof LocalDate) ? with((LocalDate)paramTemporalAdjuster, this.time) : ((paramTemporalAdjuster instanceof LocalTime) ? with(this.date, (LocalTime)paramTemporalAdjuster) : ((paramTemporalAdjuster instanceof LocalDateTime) ? (LocalDateTime)paramTemporalAdjuster : (LocalDateTime)paramTemporalAdjuster.adjustInto(this))); }
  
  public LocalDateTime with(TemporalField paramTemporalField, long paramLong) {
    if (paramTemporalField instanceof ChronoField) {
      ChronoField chronoField = (ChronoField)paramTemporalField;
      return chronoField.isTimeBased() ? with(this.date, this.time.with(paramTemporalField, paramLong)) : with(this.date.with(paramTemporalField, paramLong), this.time);
    } 
    return (LocalDateTime)paramTemporalField.adjustInto(this, paramLong);
  }
  
  public LocalDateTime withYear(int paramInt) { return with(this.date.withYear(paramInt), this.time); }
  
  public LocalDateTime withMonth(int paramInt) { return with(this.date.withMonth(paramInt), this.time); }
  
  public LocalDateTime withDayOfMonth(int paramInt) { return with(this.date.withDayOfMonth(paramInt), this.time); }
  
  public LocalDateTime withDayOfYear(int paramInt) { return with(this.date.withDayOfYear(paramInt), this.time); }
  
  public LocalDateTime withHour(int paramInt) {
    LocalTime localTime = this.time.withHour(paramInt);
    return with(this.date, localTime);
  }
  
  public LocalDateTime withMinute(int paramInt) {
    LocalTime localTime = this.time.withMinute(paramInt);
    return with(this.date, localTime);
  }
  
  public LocalDateTime withSecond(int paramInt) {
    LocalTime localTime = this.time.withSecond(paramInt);
    return with(this.date, localTime);
  }
  
  public LocalDateTime withNano(int paramInt) {
    LocalTime localTime = this.time.withNano(paramInt);
    return with(this.date, localTime);
  }
  
  public LocalDateTime truncatedTo(TemporalUnit paramTemporalUnit) { return with(this.date, this.time.truncatedTo(paramTemporalUnit)); }
  
  public LocalDateTime plus(TemporalAmount paramTemporalAmount) {
    if (paramTemporalAmount instanceof Period) {
      Period period = (Period)paramTemporalAmount;
      return with(this.date.plus(period), this.time);
    } 
    Objects.requireNonNull(paramTemporalAmount, "amountToAdd");
    return (LocalDateTime)paramTemporalAmount.addTo(this);
  }
  
  public LocalDateTime plus(long paramLong, TemporalUnit paramTemporalUnit) {
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
    return (LocalDateTime)paramTemporalUnit.addTo(this, paramLong);
  }
  
  public LocalDateTime plusYears(long paramLong) {
    LocalDate localDate = this.date.plusYears(paramLong);
    return with(localDate, this.time);
  }
  
  public LocalDateTime plusMonths(long paramLong) {
    LocalDate localDate = this.date.plusMonths(paramLong);
    return with(localDate, this.time);
  }
  
  public LocalDateTime plusWeeks(long paramLong) {
    LocalDate localDate = this.date.plusWeeks(paramLong);
    return with(localDate, this.time);
  }
  
  public LocalDateTime plusDays(long paramLong) {
    LocalDate localDate = this.date.plusDays(paramLong);
    return with(localDate, this.time);
  }
  
  public LocalDateTime plusHours(long paramLong) { return plusWithOverflow(this.date, paramLong, 0L, 0L, 0L, 1); }
  
  public LocalDateTime plusMinutes(long paramLong) { return plusWithOverflow(this.date, 0L, paramLong, 0L, 0L, 1); }
  
  public LocalDateTime plusSeconds(long paramLong) { return plusWithOverflow(this.date, 0L, 0L, paramLong, 0L, 1); }
  
  public LocalDateTime plusNanos(long paramLong) { return plusWithOverflow(this.date, 0L, 0L, 0L, paramLong, 1); }
  
  public LocalDateTime minus(TemporalAmount paramTemporalAmount) {
    if (paramTemporalAmount instanceof Period) {
      Period period = (Period)paramTemporalAmount;
      return with(this.date.minus(period), this.time);
    } 
    Objects.requireNonNull(paramTemporalAmount, "amountToSubtract");
    return (LocalDateTime)paramTemporalAmount.subtractFrom(this);
  }
  
  public LocalDateTime minus(long paramLong, TemporalUnit paramTemporalUnit) { return (paramLong == Float.MIN_VALUE) ? plus(Float.MAX_VALUE, paramTemporalUnit).plus(1L, paramTemporalUnit) : plus(-paramLong, paramTemporalUnit); }
  
  public LocalDateTime minusYears(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusYears(Float.MAX_VALUE).plusYears(1L) : plusYears(-paramLong); }
  
  public LocalDateTime minusMonths(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusMonths(Float.MAX_VALUE).plusMonths(1L) : plusMonths(-paramLong); }
  
  public LocalDateTime minusWeeks(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusWeeks(Float.MAX_VALUE).plusWeeks(1L) : plusWeeks(-paramLong); }
  
  public LocalDateTime minusDays(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusDays(Float.MAX_VALUE).plusDays(1L) : plusDays(-paramLong); }
  
  public LocalDateTime minusHours(long paramLong) { return plusWithOverflow(this.date, paramLong, 0L, 0L, 0L, -1); }
  
  public LocalDateTime minusMinutes(long paramLong) { return plusWithOverflow(this.date, 0L, paramLong, 0L, 0L, -1); }
  
  public LocalDateTime minusSeconds(long paramLong) { return plusWithOverflow(this.date, 0L, 0L, paramLong, 0L, -1); }
  
  public LocalDateTime minusNanos(long paramLong) { return plusWithOverflow(this.date, 0L, 0L, 0L, paramLong, -1); }
  
  private LocalDateTime plusWithOverflow(LocalDate paramLocalDate, long paramLong1, long paramLong2, long paramLong3, long paramLong4, int paramInt) {
    if ((paramLong1 | paramLong2 | paramLong3 | paramLong4) == 0L)
      return with(paramLocalDate, this.time); 
    long l1 = paramLong4 / 86400000000000L + paramLong3 / 86400L + paramLong2 / 1440L + paramLong1 / 24L;
    l1 *= paramInt;
    long l2 = paramLong4 % 86400000000000L + paramLong3 % 86400L * 1000000000L + paramLong2 % 1440L * 60000000000L + paramLong1 % 24L * 3600000000000L;
    long l3 = this.time.toNanoOfDay();
    l2 = l2 * paramInt + l3;
    l1 += Math.floorDiv(l2, 86400000000000L);
    long l4 = Math.floorMod(l2, 86400000000000L);
    LocalTime localTime = (l4 == l3) ? this.time : LocalTime.ofNanoOfDay(l4);
    return with(paramLocalDate.plusDays(l1), localTime);
  }
  
  public <R> R query(TemporalQuery<R> paramTemporalQuery) { return (paramTemporalQuery == TemporalQueries.localDate()) ? (R)this.date : (R)super.query(paramTemporalQuery); }
  
  public Temporal adjustInto(Temporal paramTemporal) { return super.adjustInto(paramTemporal); }
  
  public long until(Temporal paramTemporal, TemporalUnit paramTemporalUnit) {
    LocalDateTime localDateTime = from(paramTemporal);
    if (paramTemporalUnit instanceof ChronoUnit) {
      if (paramTemporalUnit.isTimeBased()) {
        long l1 = this.date.daysUntil(localDateTime.date);
        if (l1 == 0L)
          return this.time.until(localDateTime.time, paramTemporalUnit); 
        long l2 = localDateTime.time.toNanoOfDay() - this.time.toNanoOfDay();
        if (l1 > 0L) {
          l1--;
          l2 += 86400000000000L;
        } else {
          l1++;
          l2 -= 86400000000000L;
        } 
        switch ((ChronoUnit)paramTemporalUnit) {
          case NANOS:
            l1 = Math.multiplyExact(l1, 86400000000000L);
            break;
          case MICROS:
            l1 = Math.multiplyExact(l1, 86400000000L);
            l2 /= 1000L;
            break;
          case MILLIS:
            l1 = Math.multiplyExact(l1, 86400000L);
            l2 /= 1000000L;
            break;
          case SECONDS:
            l1 = Math.multiplyExact(l1, 86400L);
            l2 /= 1000000000L;
            break;
          case MINUTES:
            l1 = Math.multiplyExact(l1, 1440L);
            l2 /= 60000000000L;
            break;
          case HOURS:
            l1 = Math.multiplyExact(l1, 24L);
            l2 /= 3600000000000L;
            break;
          case HALF_DAYS:
            l1 = Math.multiplyExact(l1, 2L);
            l2 /= 43200000000000L;
            break;
        } 
        return Math.addExact(l1, l2);
      } 
      LocalDate localDate = localDateTime.date;
      if (localDate.isAfter(this.date) && localDateTime.time.isBefore(this.time)) {
        localDate = localDate.minusDays(1L);
      } else if (localDate.isBefore(this.date) && localDateTime.time.isAfter(this.time)) {
        localDate = localDate.plusDays(1L);
      } 
      return this.date.until(localDate, paramTemporalUnit);
    } 
    return paramTemporalUnit.between(this, localDateTime);
  }
  
  public String format(DateTimeFormatter paramDateTimeFormatter) {
    Objects.requireNonNull(paramDateTimeFormatter, "formatter");
    return paramDateTimeFormatter.format(this);
  }
  
  public OffsetDateTime atOffset(ZoneOffset paramZoneOffset) { return OffsetDateTime.of(this, paramZoneOffset); }
  
  public ZonedDateTime atZone(ZoneId paramZoneId) { return ZonedDateTime.of(this, paramZoneId); }
  
  public int compareTo(ChronoLocalDateTime<?> paramChronoLocalDateTime) { return (paramChronoLocalDateTime instanceof LocalDateTime) ? compareTo0((LocalDateTime)paramChronoLocalDateTime) : super.compareTo(paramChronoLocalDateTime); }
  
  private int compareTo0(LocalDateTime paramLocalDateTime) {
    int i = this.date.compareTo0(paramLocalDateTime.toLocalDate());
    if (i == 0)
      i = this.time.compareTo(paramLocalDateTime.toLocalTime()); 
    return i;
  }
  
  public boolean isAfter(ChronoLocalDateTime<?> paramChronoLocalDateTime) { return (paramChronoLocalDateTime instanceof LocalDateTime) ? ((compareTo0((LocalDateTime)paramChronoLocalDateTime) > 0)) : super.isAfter(paramChronoLocalDateTime); }
  
  public boolean isBefore(ChronoLocalDateTime<?> paramChronoLocalDateTime) { return (paramChronoLocalDateTime instanceof LocalDateTime) ? ((compareTo0((LocalDateTime)paramChronoLocalDateTime) < 0)) : super.isBefore(paramChronoLocalDateTime); }
  
  public boolean isEqual(ChronoLocalDateTime<?> paramChronoLocalDateTime) { return (paramChronoLocalDateTime instanceof LocalDateTime) ? ((compareTo0((LocalDateTime)paramChronoLocalDateTime) == 0)) : super.isEqual(paramChronoLocalDateTime); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof LocalDateTime) {
      LocalDateTime localDateTime = (LocalDateTime)paramObject;
      return (this.date.equals(localDateTime.date) && this.time.equals(localDateTime.time));
    } 
    return false;
  }
  
  public int hashCode() { return this.date.hashCode() ^ this.time.hashCode(); }
  
  public String toString() { return this.date.toString() + 'T' + this.time.toString(); }
  
  private Object writeReplace() { return new Ser((byte)5, this); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Deserialization via serialization delegate"); }
  
  void writeExternal(DataOutput paramDataOutput) throws IOException {
    this.date.writeExternal(paramDataOutput);
    this.time.writeExternal(paramDataOutput);
  }
  
  static LocalDateTime readExternal(DataInput paramDataInput) throws IOException {
    LocalDate localDate = LocalDate.readExternal(paramDataInput);
    LocalTime localTime = LocalTime.readExternal(paramDataInput);
    return of(localDate, localTime);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\LocalDateTime.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */