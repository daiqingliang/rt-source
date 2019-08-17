package java.time;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.chrono.ChronoPeriod;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Period implements ChronoPeriod, Serializable {
  public static final Period ZERO = new Period(0, 0, 0);
  
  private static final long serialVersionUID = -3587258372562876L;
  
  private static final Pattern PATTERN = Pattern.compile("([-+]?)P(?:([-+]?[0-9]+)Y)?(?:([-+]?[0-9]+)M)?(?:([-+]?[0-9]+)W)?(?:([-+]?[0-9]+)D)?", 2);
  
  private static final List<TemporalUnit> SUPPORTED_UNITS = Collections.unmodifiableList(Arrays.asList(new TemporalUnit[] { ChronoUnit.YEARS, ChronoUnit.MONTHS, ChronoUnit.DAYS }));
  
  private final int years;
  
  private final int months;
  
  private final int days;
  
  public static Period ofYears(int paramInt) { return create(paramInt, 0, 0); }
  
  public static Period ofMonths(int paramInt) { return create(0, paramInt, 0); }
  
  public static Period ofWeeks(int paramInt) { return create(0, 0, Math.multiplyExact(paramInt, 7)); }
  
  public static Period ofDays(int paramInt) { return create(0, 0, paramInt); }
  
  public static Period of(int paramInt1, int paramInt2, int paramInt3) { return create(paramInt1, paramInt2, paramInt3); }
  
  public static Period from(TemporalAmount paramTemporalAmount) {
    if (paramTemporalAmount instanceof Period)
      return (Period)paramTemporalAmount; 
    if (paramTemporalAmount instanceof ChronoPeriod && !IsoChronology.INSTANCE.equals(((ChronoPeriod)paramTemporalAmount).getChronology()))
      throw new DateTimeException("Period requires ISO chronology: " + paramTemporalAmount); 
    Objects.requireNonNull(paramTemporalAmount, "amount");
    int i = 0;
    int j = 0;
    int k = 0;
    for (TemporalUnit temporalUnit : paramTemporalAmount.getUnits()) {
      long l = paramTemporalAmount.get(temporalUnit);
      if (temporalUnit == ChronoUnit.YEARS) {
        i = Math.toIntExact(l);
        continue;
      } 
      if (temporalUnit == ChronoUnit.MONTHS) {
        j = Math.toIntExact(l);
        continue;
      } 
      if (temporalUnit == ChronoUnit.DAYS) {
        k = Math.toIntExact(l);
        continue;
      } 
      throw new DateTimeException("Unit must be Years, Months or Days, but was " + temporalUnit);
    } 
    return create(i, j, k);
  }
  
  public static Period parse(CharSequence paramCharSequence) {
    Objects.requireNonNull(paramCharSequence, "text");
    Matcher matcher = PATTERN.matcher(paramCharSequence);
    if (matcher.matches()) {
      byte b = "-".equals(matcher.group(1)) ? -1 : 1;
      String str1 = matcher.group(2);
      String str2 = matcher.group(3);
      String str3 = matcher.group(4);
      String str4 = matcher.group(5);
      if (str1 != null || str2 != null || str4 != null || str3 != null)
        try {
          int i = parseNumber(paramCharSequence, str1, b);
          int j = parseNumber(paramCharSequence, str2, b);
          int k = parseNumber(paramCharSequence, str3, b);
          int m = parseNumber(paramCharSequence, str4, b);
          m = Math.addExact(m, Math.multiplyExact(k, 7));
          return create(i, j, m);
        } catch (NumberFormatException numberFormatException) {
          throw new DateTimeParseException("Text cannot be parsed to a Period", paramCharSequence, 0, numberFormatException);
        }  
    } 
    throw new DateTimeParseException("Text cannot be parsed to a Period", paramCharSequence, 0);
  }
  
  private static int parseNumber(CharSequence paramCharSequence, String paramString, int paramInt) {
    if (paramString == null)
      return 0; 
    int i = Integer.parseInt(paramString);
    try {
      return Math.multiplyExact(i, paramInt);
    } catch (ArithmeticException arithmeticException) {
      throw new DateTimeParseException("Text cannot be parsed to a Period", paramCharSequence, 0, arithmeticException);
    } 
  }
  
  public static Period between(LocalDate paramLocalDate1, LocalDate paramLocalDate2) { return paramLocalDate1.until(paramLocalDate2); }
  
  private static Period create(int paramInt1, int paramInt2, int paramInt3) { return ((paramInt1 | paramInt2 | paramInt3) == 0) ? ZERO : new Period(paramInt1, paramInt2, paramInt3); }
  
  private Period(int paramInt1, int paramInt2, int paramInt3) {
    this.years = paramInt1;
    this.months = paramInt2;
    this.days = paramInt3;
  }
  
  public long get(TemporalUnit paramTemporalUnit) {
    if (paramTemporalUnit == ChronoUnit.YEARS)
      return getYears(); 
    if (paramTemporalUnit == ChronoUnit.MONTHS)
      return getMonths(); 
    if (paramTemporalUnit == ChronoUnit.DAYS)
      return getDays(); 
    throw new UnsupportedTemporalTypeException("Unsupported unit: " + paramTemporalUnit);
  }
  
  public List<TemporalUnit> getUnits() { return SUPPORTED_UNITS; }
  
  public IsoChronology getChronology() { return IsoChronology.INSTANCE; }
  
  public boolean isZero() { return (this == ZERO); }
  
  public boolean isNegative() { return (this.years < 0 || this.months < 0 || this.days < 0); }
  
  public int getYears() { return this.years; }
  
  public int getMonths() { return this.months; }
  
  public int getDays() { return this.days; }
  
  public Period withYears(int paramInt) { return (paramInt == this.years) ? this : create(paramInt, this.months, this.days); }
  
  public Period withMonths(int paramInt) { return (paramInt == this.months) ? this : create(this.years, paramInt, this.days); }
  
  public Period withDays(int paramInt) { return (paramInt == this.days) ? this : create(this.years, this.months, paramInt); }
  
  public Period plus(TemporalAmount paramTemporalAmount) {
    Period period;
    return (period = from(paramTemporalAmount)).create(Math.addExact(this.years, period.years), Math.addExact(this.months, period.months), Math.addExact(this.days, period.days));
  }
  
  public Period plusYears(long paramLong) { return (paramLong == 0L) ? this : create(Math.toIntExact(Math.addExact(this.years, paramLong)), this.months, this.days); }
  
  public Period plusMonths(long paramLong) { return (paramLong == 0L) ? this : create(this.years, Math.toIntExact(Math.addExact(this.months, paramLong)), this.days); }
  
  public Period plusDays(long paramLong) { return (paramLong == 0L) ? this : create(this.years, this.months, Math.toIntExact(Math.addExact(this.days, paramLong))); }
  
  public Period minus(TemporalAmount paramTemporalAmount) {
    Period period;
    return (period = from(paramTemporalAmount)).create(Math.subtractExact(this.years, period.years), Math.subtractExact(this.months, period.months), Math.subtractExact(this.days, period.days));
  }
  
  public Period minusYears(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusYears(Float.MAX_VALUE).plusYears(1L) : plusYears(-paramLong); }
  
  public Period minusMonths(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusMonths(Float.MAX_VALUE).plusMonths(1L) : plusMonths(-paramLong); }
  
  public Period minusDays(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusDays(Float.MAX_VALUE).plusDays(1L) : plusDays(-paramLong); }
  
  public Period multipliedBy(int paramInt) { return (this == ZERO || paramInt == 1) ? this : create(Math.multiplyExact(this.years, paramInt), Math.multiplyExact(this.months, paramInt), Math.multiplyExact(this.days, paramInt)); }
  
  public Period negated() { return multipliedBy(-1); }
  
  public Period normalized() {
    long l1 = toTotalMonths();
    long l2 = l1 / 12L;
    int i = (int)(l1 % 12L);
    return (l2 == this.years && i == this.months) ? this : create(Math.toIntExact(l2), i, this.days);
  }
  
  public long toTotalMonths() { return this.years * 12L + this.months; }
  
  public Temporal addTo(Temporal paramTemporal) {
    validateChrono(paramTemporal);
    if (this.months == 0) {
      if (this.years != 0)
        paramTemporal = paramTemporal.plus(this.years, ChronoUnit.YEARS); 
    } else {
      long l = toTotalMonths();
      if (l != 0L)
        paramTemporal = paramTemporal.plus(l, ChronoUnit.MONTHS); 
    } 
    if (this.days != 0)
      paramTemporal = paramTemporal.plus(this.days, ChronoUnit.DAYS); 
    return paramTemporal;
  }
  
  public Temporal subtractFrom(Temporal paramTemporal) {
    validateChrono(paramTemporal);
    if (this.months == 0) {
      if (this.years != 0)
        paramTemporal = paramTemporal.minus(this.years, ChronoUnit.YEARS); 
    } else {
      long l = toTotalMonths();
      if (l != 0L)
        paramTemporal = paramTemporal.minus(l, ChronoUnit.MONTHS); 
    } 
    if (this.days != 0)
      paramTemporal = paramTemporal.minus(this.days, ChronoUnit.DAYS); 
    return paramTemporal;
  }
  
  private void validateChrono(TemporalAccessor paramTemporalAccessor) {
    Objects.requireNonNull(paramTemporalAccessor, "temporal");
    Chronology chronology = (Chronology)paramTemporalAccessor.query(TemporalQueries.chronology());
    if (chronology != null && !IsoChronology.INSTANCE.equals(chronology))
      throw new DateTimeException("Chronology mismatch, expected: ISO, actual: " + chronology.getId()); 
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof Period) {
      Period period = (Period)paramObject;
      return (this.years == period.years && this.months == period.months && this.days == period.days);
    } 
    return false;
  }
  
  public int hashCode() { return this.years + Integer.rotateLeft(this.months, 8) + Integer.rotateLeft(this.days, 16); }
  
  public String toString() {
    if (this == ZERO)
      return "P0D"; 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append('P');
    if (this.years != 0)
      stringBuilder.append(this.years).append('Y'); 
    if (this.months != 0)
      stringBuilder.append(this.months).append('M'); 
    if (this.days != 0)
      stringBuilder.append(this.days).append('D'); 
    return stringBuilder.toString();
  }
  
  private Object writeReplace() { return new Ser((byte)14, this); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Deserialization via serialization delegate"); }
  
  void writeExternal(DataOutput paramDataOutput) throws IOException {
    paramDataOutput.writeInt(this.years);
    paramDataOutput.writeInt(this.months);
    paramDataOutput.writeInt(this.days);
  }
  
  static Period readExternal(DataInput paramDataInput) throws IOException {
    int i = paramDataInput.readInt();
    int j = paramDataInput.readInt();
    int k = paramDataInput.readInt();
    return of(i, j, k);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\Period.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */