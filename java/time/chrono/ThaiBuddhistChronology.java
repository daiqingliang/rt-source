package java.time.chrono;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ThaiBuddhistChronology extends AbstractChronology implements Serializable {
  public static final ThaiBuddhistChronology INSTANCE = new ThaiBuddhistChronology();
  
  private static final long serialVersionUID = 2775954514031616474L;
  
  static final int YEARS_DIFFERENCE = 543;
  
  private static final HashMap<String, String[]> ERA_NARROW_NAMES = new HashMap();
  
  private static final HashMap<String, String[]> ERA_SHORT_NAMES = new HashMap();
  
  private static final HashMap<String, String[]> ERA_FULL_NAMES = new HashMap();
  
  private static final String FALLBACK_LANGUAGE = "en";
  
  private static final String TARGET_LANGUAGE = "th";
  
  public String getId() { return "ThaiBuddhist"; }
  
  public String getCalendarType() { return "buddhist"; }
  
  public ThaiBuddhistDate date(Era paramEra, int paramInt1, int paramInt2, int paramInt3) { return date(prolepticYear(paramEra, paramInt1), paramInt2, paramInt3); }
  
  public ThaiBuddhistDate date(int paramInt1, int paramInt2, int paramInt3) { return new ThaiBuddhistDate(LocalDate.of(paramInt1 - 543, paramInt2, paramInt3)); }
  
  public ThaiBuddhistDate dateYearDay(Era paramEra, int paramInt1, int paramInt2) { return dateYearDay(prolepticYear(paramEra, paramInt1), paramInt2); }
  
  public ThaiBuddhistDate dateYearDay(int paramInt1, int paramInt2) { return new ThaiBuddhistDate(LocalDate.ofYearDay(paramInt1 - 543, paramInt2)); }
  
  public ThaiBuddhistDate dateEpochDay(long paramLong) { return new ThaiBuddhistDate(LocalDate.ofEpochDay(paramLong)); }
  
  public ThaiBuddhistDate dateNow() { return dateNow(Clock.systemDefaultZone()); }
  
  public ThaiBuddhistDate dateNow(ZoneId paramZoneId) { return dateNow(Clock.system(paramZoneId)); }
  
  public ThaiBuddhistDate dateNow(Clock paramClock) { return date(LocalDate.now(paramClock)); }
  
  public ThaiBuddhistDate date(TemporalAccessor paramTemporalAccessor) { return (paramTemporalAccessor instanceof ThaiBuddhistDate) ? (ThaiBuddhistDate)paramTemporalAccessor : new ThaiBuddhistDate(LocalDate.from(paramTemporalAccessor)); }
  
  public ChronoLocalDateTime<ThaiBuddhistDate> localDateTime(TemporalAccessor paramTemporalAccessor) { return super.localDateTime(paramTemporalAccessor); }
  
  public ChronoZonedDateTime<ThaiBuddhistDate> zonedDateTime(TemporalAccessor paramTemporalAccessor) { return super.zonedDateTime(paramTemporalAccessor); }
  
  public ChronoZonedDateTime<ThaiBuddhistDate> zonedDateTime(Instant paramInstant, ZoneId paramZoneId) { return super.zonedDateTime(paramInstant, paramZoneId); }
  
  public boolean isLeapYear(long paramLong) { return IsoChronology.INSTANCE.isLeapYear(paramLong - 543L); }
  
  public int prolepticYear(Era paramEra, int paramInt) {
    if (!(paramEra instanceof ThaiBuddhistEra))
      throw new ClassCastException("Era must be BuddhistEra"); 
    return (paramEra == ThaiBuddhistEra.BE) ? paramInt : (1 - paramInt);
  }
  
  public ThaiBuddhistEra eraOf(int paramInt) { return ThaiBuddhistEra.of(paramInt); }
  
  public List<Era> eras() { return Arrays.asList(ThaiBuddhistEra.values()); }
  
  public ValueRange range(ChronoField paramChronoField) {
    ValueRange valueRange;
    switch (paramChronoField) {
      case PROLEPTIC_MONTH:
        return (valueRange = ChronoField.PROLEPTIC_MONTH.range()).of(valueRange.getMinimum() + 6516L, valueRange.getMaximum() + 6516L);
      case YEAR_OF_ERA:
        return (valueRange = ChronoField.YEAR.range()).of(1L, -(valueRange.getMinimum() + 543L) + 1L, valueRange.getMaximum() + 543L);
      case YEAR:
        return (valueRange = ChronoField.YEAR.range()).of(valueRange.getMinimum() + 543L, valueRange.getMaximum() + 543L);
    } 
    return paramChronoField.range();
  }
  
  public ThaiBuddhistDate resolveDate(Map<TemporalField, Long> paramMap, ResolverStyle paramResolverStyle) { return (ThaiBuddhistDate)super.resolveDate(paramMap, paramResolverStyle); }
  
  Object writeReplace() { return super.writeReplace(); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Deserialization via serialization delegate"); }
  
  static  {
    ERA_NARROW_NAMES.put("en", new String[] { "BB", "BE" });
    ERA_NARROW_NAMES.put("th", new String[] { "BB", "BE" });
    ERA_SHORT_NAMES.put("en", new String[] { "B.B.", "B.E." });
    ERA_SHORT_NAMES.put("th", new String[] { "พ.ศ.", "ปีก่อนคริสต์กาลที่" });
    ERA_FULL_NAMES.put("en", new String[] { "Before Buddhist", "Budhhist Era" });
    ERA_FULL_NAMES.put("th", new String[] { "พุทธศักราช", "ปีก่อนคริสต์กาลที่" });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\chrono\ThaiBuddhistChronology.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */