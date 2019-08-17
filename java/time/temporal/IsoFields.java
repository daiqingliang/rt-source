package java.time.temporal;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleResources;

public final class IsoFields {
  public static final TemporalField DAY_OF_QUARTER = Field.DAY_OF_QUARTER;
  
  public static final TemporalField QUARTER_OF_YEAR = Field.QUARTER_OF_YEAR;
  
  public static final TemporalField WEEK_OF_WEEK_BASED_YEAR = Field.WEEK_OF_WEEK_BASED_YEAR;
  
  public static final TemporalField WEEK_BASED_YEAR = Field.WEEK_BASED_YEAR;
  
  public static final TemporalUnit WEEK_BASED_YEARS = Unit.WEEK_BASED_YEARS;
  
  public static final TemporalUnit QUARTER_YEARS = Unit.QUARTER_YEARS;
  
  private IsoFields() { throw new AssertionError("Not instantiable"); }
  
  private final abstract enum Field implements TemporalField {
    DAY_OF_QUARTER, QUARTER_OF_YEAR, WEEK_OF_WEEK_BASED_YEAR, WEEK_BASED_YEAR;
    
    private static final int[] QUARTER_DAYS;
    
    public boolean isDateBased() { return true; }
    
    public boolean isTimeBased() { return false; }
    
    public ValueRange rangeRefinedBy(TemporalAccessor param1TemporalAccessor) { return range(); }
    
    private static boolean isIso(TemporalAccessor param1TemporalAccessor) { return Chronology.from(param1TemporalAccessor).equals(IsoChronology.INSTANCE); }
    
    private static void ensureIso(TemporalAccessor param1TemporalAccessor) {
      if (!isIso(param1TemporalAccessor))
        throw new DateTimeException("Resolve requires IsoChronology"); 
    }
    
    private static ValueRange getWeekRange(LocalDate param1LocalDate) {
      int i = getWeekBasedYear(param1LocalDate);
      return ValueRange.of(1L, getWeekRange(i));
    }
    
    private static int getWeekRange(int param1Int) {
      LocalDate localDate = LocalDate.of(param1Int, 1, 1);
      return (localDate.getDayOfWeek() == DayOfWeek.THURSDAY || (localDate.getDayOfWeek() == DayOfWeek.WEDNESDAY && localDate.isLeapYear())) ? 53 : 52;
    }
    
    private static int getWeek(LocalDate param1LocalDate) {
      int i = param1LocalDate.getDayOfWeek().ordinal();
      int j = param1LocalDate.getDayOfYear() - 1;
      int k = j + 3 - i;
      int m = k / 7;
      int n = k - m * 7;
      int i1 = n - 3;
      if (i1 < -3)
        i1 += 7; 
      if (j < i1)
        return (int)getWeekRange(param1LocalDate.withDayOfYear(180).minusYears(1L)).getMaximum(); 
      int i2 = (j - i1) / 7 + 1;
      if (i2 == 53 && !((i1 == -3 || (i1 == -2 && param1LocalDate.isLeapYear())) ? 1 : 0))
        i2 = 1; 
      return i2;
    }
    
    private static int getWeekBasedYear(LocalDate param1LocalDate) {
      int i = param1LocalDate.getYear();
      int j = param1LocalDate.getDayOfYear();
      if (j <= 3) {
        int k = param1LocalDate.getDayOfWeek().ordinal();
        if (j - k < -2)
          i--; 
      } else if (j >= 363) {
        int k = param1LocalDate.getDayOfWeek().ordinal();
        j = j - 363 - (param1LocalDate.isLeapYear() ? 1 : 0);
        if (j - k >= 0)
          i++; 
      } 
      return i;
    }
    
    static  {
      // Byte code:
      //   0: new java/time/temporal/IsoFields$Field$1
      //   3: dup
      //   4: ldc 'DAY_OF_QUARTER'
      //   6: iconst_0
      //   7: invokespecial <init> : (Ljava/lang/String;I)V
      //   10: putstatic java/time/temporal/IsoFields$Field.DAY_OF_QUARTER : Ljava/time/temporal/IsoFields$Field;
      //   13: new java/time/temporal/IsoFields$Field$2
      //   16: dup
      //   17: ldc 'QUARTER_OF_YEAR'
      //   19: iconst_1
      //   20: invokespecial <init> : (Ljava/lang/String;I)V
      //   23: putstatic java/time/temporal/IsoFields$Field.QUARTER_OF_YEAR : Ljava/time/temporal/IsoFields$Field;
      //   26: new java/time/temporal/IsoFields$Field$3
      //   29: dup
      //   30: ldc 'WEEK_OF_WEEK_BASED_YEAR'
      //   32: iconst_2
      //   33: invokespecial <init> : (Ljava/lang/String;I)V
      //   36: putstatic java/time/temporal/IsoFields$Field.WEEK_OF_WEEK_BASED_YEAR : Ljava/time/temporal/IsoFields$Field;
      //   39: new java/time/temporal/IsoFields$Field$4
      //   42: dup
      //   43: ldc 'WEEK_BASED_YEAR'
      //   45: iconst_3
      //   46: invokespecial <init> : (Ljava/lang/String;I)V
      //   49: putstatic java/time/temporal/IsoFields$Field.WEEK_BASED_YEAR : Ljava/time/temporal/IsoFields$Field;
      //   52: iconst_4
      //   53: anewarray java/time/temporal/IsoFields$Field
      //   56: dup
      //   57: iconst_0
      //   58: getstatic java/time/temporal/IsoFields$Field.DAY_OF_QUARTER : Ljava/time/temporal/IsoFields$Field;
      //   61: aastore
      //   62: dup
      //   63: iconst_1
      //   64: getstatic java/time/temporal/IsoFields$Field.QUARTER_OF_YEAR : Ljava/time/temporal/IsoFields$Field;
      //   67: aastore
      //   68: dup
      //   69: iconst_2
      //   70: getstatic java/time/temporal/IsoFields$Field.WEEK_OF_WEEK_BASED_YEAR : Ljava/time/temporal/IsoFields$Field;
      //   73: aastore
      //   74: dup
      //   75: iconst_3
      //   76: getstatic java/time/temporal/IsoFields$Field.WEEK_BASED_YEAR : Ljava/time/temporal/IsoFields$Field;
      //   79: aastore
      //   80: putstatic java/time/temporal/IsoFields$Field.$VALUES : [Ljava/time/temporal/IsoFields$Field;
      //   83: bipush #8
      //   85: newarray int
      //   87: dup
      //   88: iconst_0
      //   89: iconst_0
      //   90: iastore
      //   91: dup
      //   92: iconst_1
      //   93: bipush #90
      //   95: iastore
      //   96: dup
      //   97: iconst_2
      //   98: sipush #181
      //   101: iastore
      //   102: dup
      //   103: iconst_3
      //   104: sipush #273
      //   107: iastore
      //   108: dup
      //   109: iconst_4
      //   110: iconst_0
      //   111: iastore
      //   112: dup
      //   113: iconst_5
      //   114: bipush #91
      //   116: iastore
      //   117: dup
      //   118: bipush #6
      //   120: sipush #182
      //   123: iastore
      //   124: dup
      //   125: bipush #7
      //   127: sipush #274
      //   130: iastore
      //   131: putstatic java/time/temporal/IsoFields$Field.QUARTER_DAYS : [I
      //   134: return
    }
  }
  
  private enum Unit implements TemporalUnit {
    WEEK_BASED_YEARS("WeekBasedYears", Duration.ofSeconds(31556952L)),
    QUARTER_YEARS("QuarterYears", Duration.ofSeconds(7889238L));
    
    private final String name;
    
    private final Duration duration;
    
    Unit(Duration param1Duration1, Duration param1Duration2) {
      this.name = param1Duration1;
      this.duration = param1Duration2;
    }
    
    public Duration getDuration() { return this.duration; }
    
    public boolean isDurationEstimated() { return true; }
    
    public boolean isDateBased() { return true; }
    
    public boolean isTimeBased() { return false; }
    
    public boolean isSupportedBy(Temporal param1Temporal) { return param1Temporal.isSupported(ChronoField.EPOCH_DAY); }
    
    public <R extends Temporal> R addTo(R param1R, long param1Long) {
      switch (IsoFields.null.$SwitchMap$java$time$temporal$IsoFields$Unit[ordinal()]) {
        case 1:
          return (R)param1R.with(IsoFields.WEEK_BASED_YEAR, Math.addExact(param1R.get(IsoFields.WEEK_BASED_YEAR), param1Long));
        case 2:
          return (R)param1R.plus(param1Long / 256L, ChronoUnit.YEARS).plus(param1Long % 256L * 3L, ChronoUnit.MONTHS);
      } 
      throw new IllegalStateException("Unreachable");
    }
    
    public long between(Temporal param1Temporal1, Temporal param1Temporal2) {
      if (param1Temporal1.getClass() != param1Temporal2.getClass())
        return param1Temporal1.until(param1Temporal2, this); 
      switch (IsoFields.null.$SwitchMap$java$time$temporal$IsoFields$Unit[ordinal()]) {
        case 1:
          return Math.subtractExact(param1Temporal2.getLong(IsoFields.WEEK_BASED_YEAR), param1Temporal1.getLong(IsoFields.WEEK_BASED_YEAR));
        case 2:
          return param1Temporal1.until(param1Temporal2, ChronoUnit.MONTHS) / 3L;
      } 
      throw new IllegalStateException("Unreachable");
    }
    
    public String toString() { return this.name; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\temporal\IsoFields.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */