package java.nio.file.attribute;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class FileTime extends Object implements Comparable<FileTime> {
  private final TimeUnit unit;
  
  private final long value;
  
  private Instant instant;
  
  private String valueAsString;
  
  private static final long HOURS_PER_DAY = 24L;
  
  private static final long MINUTES_PER_HOUR = 60L;
  
  private static final long SECONDS_PER_MINUTE = 60L;
  
  private static final long SECONDS_PER_HOUR = 3600L;
  
  private static final long SECONDS_PER_DAY = 86400L;
  
  private static final long MILLIS_PER_SECOND = 1000L;
  
  private static final long MICROS_PER_SECOND = 1000000L;
  
  private static final long NANOS_PER_SECOND = 1000000000L;
  
  private static final int NANOS_PER_MILLI = 1000000;
  
  private static final int NANOS_PER_MICRO = 1000;
  
  private static final long MIN_SECOND = -31557014167219200L;
  
  private static final long MAX_SECOND = 31556889864403199L;
  
  private static final long DAYS_PER_10000_YEARS = 3652425L;
  
  private static final long SECONDS_PER_10000_YEARS = 315569520000L;
  
  private static final long SECONDS_0000_TO_1970 = 62167219200L;
  
  private FileTime(long paramLong, TimeUnit paramTimeUnit, Instant paramInstant) {
    this.value = paramLong;
    this.unit = paramTimeUnit;
    this.instant = paramInstant;
  }
  
  public static FileTime from(long paramLong, TimeUnit paramTimeUnit) {
    Objects.requireNonNull(paramTimeUnit, "unit");
    return new FileTime(paramLong, paramTimeUnit, null);
  }
  
  public static FileTime fromMillis(long paramLong) { return new FileTime(paramLong, TimeUnit.MILLISECONDS, null); }
  
  public static FileTime from(Instant paramInstant) {
    Objects.requireNonNull(paramInstant, "instant");
    return new FileTime(0L, null, paramInstant);
  }
  
  public long to(TimeUnit paramTimeUnit) {
    Objects.requireNonNull(paramTimeUnit, "unit");
    if (this.unit != null)
      return paramTimeUnit.convert(this.value, this.unit); 
    long l1 = paramTimeUnit.convert(this.instant.getEpochSecond(), TimeUnit.SECONDS);
    if (l1 == Float.MIN_VALUE || l1 == Float.MAX_VALUE)
      return l1; 
    long l2 = paramTimeUnit.convert(this.instant.getNano(), TimeUnit.NANOSECONDS);
    long l3 = l1 + l2;
    return (((l1 ^ l3) & (l2 ^ l3)) < 0L) ? ((l1 < 0L) ? Float.MIN_VALUE : Float.MAX_VALUE) : l3;
  }
  
  public long toMillis() {
    if (this.unit != null)
      return this.unit.toMillis(this.value); 
    long l1 = this.instant.getEpochSecond();
    int i = this.instant.getNano();
    long l2 = l1 * 1000L;
    long l3 = Math.abs(l1);
    return ((l3 | 0x3E8L) >>> 31 != 0L && l2 / 1000L != l1) ? ((l1 < 0L) ? Float.MIN_VALUE : Float.MAX_VALUE) : (l2 + (i / 1000000));
  }
  
  private static long scale(long paramLong1, long paramLong2, long paramLong3) { return (paramLong1 > paramLong3) ? Float.MAX_VALUE : ((paramLong1 < -paramLong3) ? Float.MIN_VALUE : (paramLong1 * paramLong2)); }
  
  public Instant toInstant() {
    if (this.instant == null) {
      long l = 0L;
      int i = 0;
      switch (this.unit) {
        case DAYS:
          l = scale(this.value, 86400L, 106751991167300L);
          break;
        case HOURS:
          l = scale(this.value, 3600L, 2562047788015215L);
          break;
        case MINUTES:
          l = scale(this.value, 60L, 153722867280912930L);
          break;
        case SECONDS:
          l = this.value;
          break;
        case MILLISECONDS:
          l = Math.floorDiv(this.value, 1000L);
          i = (int)Math.floorMod(this.value, 1000L) * 1000000;
          break;
        case MICROSECONDS:
          l = Math.floorDiv(this.value, 1000000L);
          i = (int)Math.floorMod(this.value, 1000000L) * 1000;
          break;
        case NANOSECONDS:
          l = Math.floorDiv(this.value, 1000000000L);
          i = (int)Math.floorMod(this.value, 1000000000L);
          break;
        default:
          throw new AssertionError("Unit not handled");
      } 
      if (l <= -31557014167219200L) {
        this.instant = Instant.MIN;
      } else if (l >= 31556889864403199L) {
        this.instant = Instant.MAX;
      } else {
        this.instant = Instant.ofEpochSecond(l, i);
      } 
    } 
    return this.instant;
  }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof FileTime) ? ((compareTo((FileTime)paramObject) == 0)) : false; }
  
  public int hashCode() { return toInstant().hashCode(); }
  
  private long toDays() { return (this.unit != null) ? this.unit.toDays(this.value) : TimeUnit.SECONDS.toDays(toInstant().getEpochSecond()); }
  
  private long toExcessNanos(long paramLong) { return (this.unit != null) ? this.unit.toNanos(this.value - this.unit.convert(paramLong, TimeUnit.DAYS)) : TimeUnit.SECONDS.toNanos(toInstant().getEpochSecond() - TimeUnit.DAYS.toSeconds(paramLong)); }
  
  public int compareTo(FileTime paramFileTime) {
    if (this.unit != null && this.unit == paramFileTime.unit)
      return Long.compare(this.value, paramFileTime.value); 
    long l1 = toInstant().getEpochSecond();
    long l2 = paramFileTime.toInstant().getEpochSecond();
    int i = Long.compare(l1, l2);
    if (i != 0)
      return i; 
    i = Long.compare(toInstant().getNano(), paramFileTime.toInstant().getNano());
    if (i != 0)
      return i; 
    if (l1 != 31556889864403199L && l1 != -31557014167219200L)
      return 0; 
    long l3 = toDays();
    long l4 = paramFileTime.toDays();
    return (l3 == l4) ? Long.compare(toExcessNanos(l3), paramFileTime.toExcessNanos(l4)) : Long.compare(l3, l4);
  }
  
  private StringBuilder append(StringBuilder paramStringBuilder, int paramInt1, int paramInt2) {
    while (paramInt1 > 0) {
      paramStringBuilder.append((char)(paramInt2 / paramInt1 + 48));
      paramInt2 %= paramInt1;
      paramInt1 /= 10;
    } 
    return paramStringBuilder;
  }
  
  public String toString() {
    if (this.valueAsString == null) {
      LocalDateTime localDateTime;
      long l = 0L;
      int i = 0;
      if (this.instant == null && this.unit.compareTo(TimeUnit.SECONDS) >= 0) {
        l = this.unit.toSeconds(this.value);
      } else {
        l = toInstant().getEpochSecond();
        i = toInstant().getNano();
      } 
      int j = 0;
      if (l >= -62167219200L) {
        long l1 = l - 315569520000L + 62167219200L;
        long l2 = Math.floorDiv(l1, 315569520000L) + 1L;
        long l3 = Math.floorMod(l1, 315569520000L);
        localDateTime = LocalDateTime.ofEpochSecond(l3 - 62167219200L, i, ZoneOffset.UTC);
        j = localDateTime.getYear() + (int)l2 * 10000;
      } else {
        long l1 = l + 62167219200L;
        long l2 = l1 / 315569520000L;
        long l3 = l1 % 315569520000L;
        localDateTime = LocalDateTime.ofEpochSecond(l3 - 62167219200L, i, ZoneOffset.UTC);
        j = localDateTime.getYear() + (int)l2 * 10000;
      } 
      if (j <= 0)
        j--; 
      int k = localDateTime.getNano();
      StringBuilder stringBuilder = new StringBuilder(64);
      stringBuilder.append((j < 0) ? "-" : "");
      j = Math.abs(j);
      if (j < 10000) {
        append(stringBuilder, 1000, Math.abs(j));
      } else {
        stringBuilder.append(String.valueOf(j));
      } 
      stringBuilder.append('-');
      append(stringBuilder, 10, localDateTime.getMonthValue());
      stringBuilder.append('-');
      append(stringBuilder, 10, localDateTime.getDayOfMonth());
      stringBuilder.append('T');
      append(stringBuilder, 10, localDateTime.getHour());
      stringBuilder.append(':');
      append(stringBuilder, 10, localDateTime.getMinute());
      stringBuilder.append(':');
      append(stringBuilder, 10, localDateTime.getSecond());
      if (k != 0) {
        stringBuilder.append('.');
        int m;
        for (m = 100000000; k % 10 == 0; m /= 10)
          k /= 10; 
        append(stringBuilder, m, k);
      } 
      stringBuilder.append('Z');
      this.valueAsString = stringBuilder.toString();
    } 
    return this.valueAsString;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\attribute\FileTime.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */