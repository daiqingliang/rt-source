package java.time;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Duration extends Object implements TemporalAmount, Comparable<Duration>, Serializable {
  public static final Duration ZERO = new Duration(0L, 0);
  
  private static final long serialVersionUID = 3078945930695997490L;
  
  private static final BigInteger BI_NANOS_PER_SECOND = BigInteger.valueOf(1000000000L);
  
  private static final Pattern PATTERN = Pattern.compile("([-+]?)P(?:([-+]?[0-9]+)D)?(T(?:([-+]?[0-9]+)H)?(?:([-+]?[0-9]+)M)?(?:([-+]?[0-9]+)(?:[.,]([0-9]{0,9}))?S)?)?", 2);
  
  private final long seconds;
  
  private final int nanos;
  
  public static Duration ofDays(long paramLong) { return create(Math.multiplyExact(paramLong, 86400L), 0); }
  
  public static Duration ofHours(long paramLong) { return create(Math.multiplyExact(paramLong, 3600L), 0); }
  
  public static Duration ofMinutes(long paramLong) { return create(Math.multiplyExact(paramLong, 60L), 0); }
  
  public static Duration ofSeconds(long paramLong) { return create(paramLong, 0); }
  
  public static Duration ofSeconds(long paramLong1, long paramLong2) {
    long l = Math.addExact(paramLong1, Math.floorDiv(paramLong2, 1000000000L));
    int i = (int)Math.floorMod(paramLong2, 1000000000L);
    return create(l, i);
  }
  
  public static Duration ofMillis(long paramLong) {
    long l = paramLong / 1000L;
    int i = (int)(paramLong % 1000L);
    if (i < 0) {
      i += 1000;
      l--;
    } 
    return create(l, i * 1000000);
  }
  
  public static Duration ofNanos(long paramLong) {
    long l = paramLong / 1000000000L;
    int i = (int)(paramLong % 1000000000L);
    if (i < 0) {
      i = (int)(i + 1000000000L);
      l--;
    } 
    return create(l, i);
  }
  
  public static Duration of(long paramLong, TemporalUnit paramTemporalUnit) { return ZERO.plus(paramLong, paramTemporalUnit); }
  
  public static Duration from(TemporalAmount paramTemporalAmount) {
    Objects.requireNonNull(paramTemporalAmount, "amount");
    Duration duration = ZERO;
    for (TemporalUnit temporalUnit : paramTemporalAmount.getUnits())
      duration = duration.plus(paramTemporalAmount.get(temporalUnit), temporalUnit); 
    return duration;
  }
  
  public static Duration parse(CharSequence paramCharSequence) {
    Objects.requireNonNull(paramCharSequence, "text");
    Matcher matcher = PATTERN.matcher(paramCharSequence);
    if (matcher.matches() && !"T".equals(matcher.group(3))) {
      boolean bool = "-".equals(matcher.group(1));
      String str1 = matcher.group(2);
      String str2 = matcher.group(4);
      String str3 = matcher.group(5);
      String str4 = matcher.group(6);
      String str5 = matcher.group(7);
      if (str1 != null || str2 != null || str3 != null || str4 != null) {
        long l1 = parseNumber(paramCharSequence, str1, 86400, "days");
        long l2 = parseNumber(paramCharSequence, str2, 3600, "hours");
        long l3 = parseNumber(paramCharSequence, str3, 60, "minutes");
        long l4 = parseNumber(paramCharSequence, str4, 1, "seconds");
        int i = parseFraction(paramCharSequence, str5, (l4 < 0L) ? -1 : 1);
        try {
          return create(bool, l1, l2, l3, l4, i);
        } catch (ArithmeticException arithmeticException) {
          throw (DateTimeParseException)(new DateTimeParseException("Text cannot be parsed to a Duration: overflow", paramCharSequence, 0)).initCause(arithmeticException);
        } 
      } 
    } 
    throw new DateTimeParseException("Text cannot be parsed to a Duration", paramCharSequence, 0);
  }
  
  private static long parseNumber(CharSequence paramCharSequence, String paramString1, int paramInt, String paramString2) {
    if (paramString1 == null)
      return 0L; 
    try {
      long l = Long.parseLong(paramString1);
      return Math.multiplyExact(l, paramInt);
    } catch (NumberFormatException|ArithmeticException numberFormatException) {
      throw (DateTimeParseException)(new DateTimeParseException("Text cannot be parsed to a Duration: " + paramString2, paramCharSequence, 0)).initCause(numberFormatException);
    } 
  }
  
  private static int parseFraction(CharSequence paramCharSequence, String paramString, int paramInt) {
    if (paramString == null || paramString.length() == 0)
      return 0; 
    try {
      paramString = (paramString + "000000000").substring(0, 9);
      return Integer.parseInt(paramString) * paramInt;
    } catch (NumberFormatException|ArithmeticException numberFormatException) {
      throw (DateTimeParseException)(new DateTimeParseException("Text cannot be parsed to a Duration: fraction", paramCharSequence, 0)).initCause(numberFormatException);
    } 
  }
  
  private static Duration create(boolean paramBoolean, long paramLong1, long paramLong2, long paramLong3, long paramLong4, int paramInt) {
    long l = Math.addExact(paramLong1, Math.addExact(paramLong2, Math.addExact(paramLong3, paramLong4)));
    return paramBoolean ? ofSeconds(l, paramInt).negated() : ofSeconds(l, paramInt);
  }
  
  public static Duration between(Temporal paramTemporal1, Temporal paramTemporal2) {
    try {
      return ofNanos(paramTemporal1.until(paramTemporal2, ChronoUnit.NANOS));
    } catch (DateTimeException|ArithmeticException dateTimeException) {
      long l2;
      long l1 = paramTemporal1.until(paramTemporal2, ChronoUnit.SECONDS);
      try {
        l2 = paramTemporal2.getLong(ChronoField.NANO_OF_SECOND) - paramTemporal1.getLong(ChronoField.NANO_OF_SECOND);
        if (l1 > 0L && l2 < 0L) {
          l1++;
        } else if (l1 < 0L && l2 > 0L) {
          l1--;
        } 
      } catch (DateTimeException dateTimeException1) {
        l2 = 0L;
      } 
      return ofSeconds(l1, l2);
    } 
  }
  
  private static Duration create(long paramLong, int paramInt) { return ((paramLong | paramInt) == 0L) ? ZERO : new Duration(paramLong, paramInt); }
  
  private Duration(long paramLong, int paramInt) {
    this.seconds = paramLong;
    this.nanos = paramInt;
  }
  
  public long get(TemporalUnit paramTemporalUnit) {
    if (paramTemporalUnit == ChronoUnit.SECONDS)
      return this.seconds; 
    if (paramTemporalUnit == ChronoUnit.NANOS)
      return this.nanos; 
    throw new UnsupportedTemporalTypeException("Unsupported unit: " + paramTemporalUnit);
  }
  
  public List<TemporalUnit> getUnits() { return DurationUnits.UNITS; }
  
  public boolean isZero() { return ((this.seconds | this.nanos) == 0L); }
  
  public boolean isNegative() { return (this.seconds < 0L); }
  
  public long getSeconds() { return this.seconds; }
  
  public int getNano() { return this.nanos; }
  
  public Duration withSeconds(long paramLong) { return create(paramLong, this.nanos); }
  
  public Duration withNanos(int paramInt) {
    ChronoField.NANO_OF_SECOND.checkValidIntValue(paramInt);
    return create(this.seconds, paramInt);
  }
  
  public Duration plus(Duration paramDuration) { return plus(paramDuration.getSeconds(), paramDuration.getNano()); }
  
  public Duration plus(long paramLong, TemporalUnit paramTemporalUnit) {
    Objects.requireNonNull(paramTemporalUnit, "unit");
    if (paramTemporalUnit == ChronoUnit.DAYS)
      return plus(Math.multiplyExact(paramLong, 86400L), 0L); 
    if (paramTemporalUnit.isDurationEstimated())
      throw new UnsupportedTemporalTypeException("Unit must not have an estimated duration"); 
    if (paramLong == 0L)
      return this; 
    if (paramTemporalUnit instanceof ChronoUnit) {
      switch ((ChronoUnit)paramTemporalUnit) {
        case NANOS:
          return plusNanos(paramLong);
        case MICROS:
          return plusSeconds(paramLong / 1000000000L * 1000L).plusNanos(paramLong % 1000000000L * 1000L);
        case MILLIS:
          return plusMillis(paramLong);
        case SECONDS:
          return plusSeconds(paramLong);
      } 
      return plusSeconds(Math.multiplyExact((paramTemporalUnit.getDuration()).seconds, paramLong));
    } 
    Duration duration = paramTemporalUnit.getDuration().multipliedBy(paramLong);
    return plusSeconds(duration.getSeconds()).plusNanos(duration.getNano());
  }
  
  public Duration plusDays(long paramLong) { return plus(Math.multiplyExact(paramLong, 86400L), 0L); }
  
  public Duration plusHours(long paramLong) { return plus(Math.multiplyExact(paramLong, 3600L), 0L); }
  
  public Duration plusMinutes(long paramLong) { return plus(Math.multiplyExact(paramLong, 60L), 0L); }
  
  public Duration plusSeconds(long paramLong) { return plus(paramLong, 0L); }
  
  public Duration plusMillis(long paramLong) { return plus(paramLong / 1000L, paramLong % 1000L * 1000000L); }
  
  public Duration plusNanos(long paramLong) { return plus(0L, paramLong); }
  
  private Duration plus(long paramLong1, long paramLong2) {
    if ((paramLong1 | paramLong2) == 0L)
      return this; 
    long l1 = Math.addExact(this.seconds, paramLong1);
    l1 = Math.addExact(l1, paramLong2 / 1000000000L);
    paramLong2 %= 1000000000L;
    long l2 = this.nanos + paramLong2;
    return ofSeconds(l1, l2);
  }
  
  public Duration minus(Duration paramDuration) {
    long l = paramDuration.getSeconds();
    int i = paramDuration.getNano();
    return (l == Float.MIN_VALUE) ? plus(Float.MAX_VALUE, -i).plus(1L, 0L) : plus(-l, -i);
  }
  
  public Duration minus(long paramLong, TemporalUnit paramTemporalUnit) { return (paramLong == Float.MIN_VALUE) ? plus(Float.MAX_VALUE, paramTemporalUnit).plus(1L, paramTemporalUnit) : plus(-paramLong, paramTemporalUnit); }
  
  public Duration minusDays(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusDays(Float.MAX_VALUE).plusDays(1L) : plusDays(-paramLong); }
  
  public Duration minusHours(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusHours(Float.MAX_VALUE).plusHours(1L) : plusHours(-paramLong); }
  
  public Duration minusMinutes(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusMinutes(Float.MAX_VALUE).plusMinutes(1L) : plusMinutes(-paramLong); }
  
  public Duration minusSeconds(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusSeconds(Float.MAX_VALUE).plusSeconds(1L) : plusSeconds(-paramLong); }
  
  public Duration minusMillis(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusMillis(Float.MAX_VALUE).plusMillis(1L) : plusMillis(-paramLong); }
  
  public Duration minusNanos(long paramLong) { return (paramLong == Float.MIN_VALUE) ? plusNanos(Float.MAX_VALUE).plusNanos(1L) : plusNanos(-paramLong); }
  
  public Duration multipliedBy(long paramLong) { return (paramLong == 0L) ? ZERO : ((paramLong == 1L) ? this : create(toSeconds().multiply(BigDecimal.valueOf(paramLong)))); }
  
  public Duration dividedBy(long paramLong) {
    if (paramLong == 0L)
      throw new ArithmeticException("Cannot divide by zero"); 
    return (paramLong == 1L) ? this : create(toSeconds().divide(BigDecimal.valueOf(paramLong), RoundingMode.DOWN));
  }
  
  private BigDecimal toSeconds() { return BigDecimal.valueOf(this.seconds).add(BigDecimal.valueOf(this.nanos, 9)); }
  
  private static Duration create(BigDecimal paramBigDecimal) {
    BigInteger bigInteger = paramBigDecimal.movePointRight(9).toBigIntegerExact();
    BigInteger[] arrayOfBigInteger = bigInteger.divideAndRemainder(BI_NANOS_PER_SECOND);
    if (arrayOfBigInteger[0].bitLength() > 63)
      throw new ArithmeticException("Exceeds capacity of Duration: " + bigInteger); 
    return ofSeconds(arrayOfBigInteger[0].longValue(), arrayOfBigInteger[1].intValue());
  }
  
  public Duration negated() { return multipliedBy(-1L); }
  
  public Duration abs() { return isNegative() ? negated() : this; }
  
  public Temporal addTo(Temporal paramTemporal) {
    if (this.seconds != 0L)
      paramTemporal = paramTemporal.plus(this.seconds, ChronoUnit.SECONDS); 
    if (this.nanos != 0)
      paramTemporal = paramTemporal.plus(this.nanos, ChronoUnit.NANOS); 
    return paramTemporal;
  }
  
  public Temporal subtractFrom(Temporal paramTemporal) {
    if (this.seconds != 0L)
      paramTemporal = paramTemporal.minus(this.seconds, ChronoUnit.SECONDS); 
    if (this.nanos != 0)
      paramTemporal = paramTemporal.minus(this.nanos, ChronoUnit.NANOS); 
    return paramTemporal;
  }
  
  public long toDays() { return this.seconds / 86400L; }
  
  public long toHours() { return this.seconds / 3600L; }
  
  public long toMinutes() { return this.seconds / 60L; }
  
  public long toMillis() {
    null = Math.multiplyExact(this.seconds, 1000L);
    return Math.addExact(null, (this.nanos / 1000000));
  }
  
  public long toNanos() {
    null = Math.multiplyExact(this.seconds, 1000000000L);
    return Math.addExact(null, this.nanos);
  }
  
  public int compareTo(Duration paramDuration) {
    int i = Long.compare(this.seconds, paramDuration.seconds);
    return (i != 0) ? i : (this.nanos - paramDuration.nanos);
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof Duration) {
      Duration duration = (Duration)paramObject;
      return (this.seconds == duration.seconds && this.nanos == duration.nanos);
    } 
    return false;
  }
  
  public int hashCode() { return (int)(this.seconds ^ this.seconds >>> 32) + 51 * this.nanos; }
  
  public String toString() {
    if (this == ZERO)
      return "PT0S"; 
    long l = this.seconds / 3600L;
    int i = (int)(this.seconds % 3600L / 60L);
    int j = (int)(this.seconds % 60L);
    StringBuilder stringBuilder = new StringBuilder(24);
    stringBuilder.append("PT");
    if (l != 0L)
      stringBuilder.append(l).append('H'); 
    if (i != 0)
      stringBuilder.append(i).append('M'); 
    if (j == 0 && this.nanos == 0 && stringBuilder.length() > 2)
      return stringBuilder.toString(); 
    if (j < 0 && this.nanos > 0) {
      if (j == -1) {
        stringBuilder.append("-0");
      } else {
        stringBuilder.append(j + 1);
      } 
    } else {
      stringBuilder.append(j);
    } 
    if (this.nanos > 0) {
      int k = stringBuilder.length();
      if (j < 0) {
        stringBuilder.append(2000000000L - this.nanos);
      } else {
        stringBuilder.append(this.nanos + 1000000000L);
      } 
      while (stringBuilder.charAt(stringBuilder.length() - 1) == '0')
        stringBuilder.setLength(stringBuilder.length() - 1); 
      stringBuilder.setCharAt(k, '.');
    } 
    stringBuilder.append('S');
    return stringBuilder.toString();
  }
  
  private Object writeReplace() { return new Ser((byte)1, this); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Deserialization via serialization delegate"); }
  
  void writeExternal(DataOutput paramDataOutput) throws IOException {
    paramDataOutput.writeLong(this.seconds);
    paramDataOutput.writeInt(this.nanos);
  }
  
  static Duration readExternal(DataInput paramDataInput) throws IOException {
    long l = paramDataInput.readLong();
    int i = paramDataInput.readInt();
    return ofSeconds(l, i);
  }
  
  private static class DurationUnits {
    static final List<TemporalUnit> UNITS = Collections.unmodifiableList(Arrays.asList(new TemporalUnit[] { ChronoUnit.SECONDS, ChronoUnit.NANOS }));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\Duration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */