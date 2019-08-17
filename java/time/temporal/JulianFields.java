package java.time.temporal;

import java.time.DateTimeException;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.format.ResolverStyle;
import java.util.Map;

public final class JulianFields {
  private static final long JULIAN_DAY_OFFSET = 2440588L;
  
  public static final TemporalField JULIAN_DAY = Field.JULIAN_DAY;
  
  public static final TemporalField MODIFIED_JULIAN_DAY = Field.MODIFIED_JULIAN_DAY;
  
  public static final TemporalField RATA_DIE = Field.RATA_DIE;
  
  private JulianFields() { throw new AssertionError("Not instantiable"); }
  
  private enum Field implements TemporalField {
    JULIAN_DAY("JulianDay", ChronoUnit.DAYS, ChronoUnit.FOREVER, 2440588L),
    MODIFIED_JULIAN_DAY("ModifiedJulianDay", ChronoUnit.DAYS, ChronoUnit.FOREVER, 40587L),
    RATA_DIE("RataDie", ChronoUnit.DAYS, ChronoUnit.FOREVER, 719163L);
    
    private static final long serialVersionUID = -7501623920830201812L;
    
    private final String name;
    
    private final TemporalUnit baseUnit;
    
    private final TemporalUnit rangeUnit;
    
    private final ValueRange range;
    
    private final long offset;
    
    Field(TemporalUnit param1TemporalUnit1, TemporalUnit param1TemporalUnit2, long param1Long1, long param1Long2) {
      this.name = param1TemporalUnit1;
      this.baseUnit = param1TemporalUnit2;
      this.rangeUnit = param1Long1;
      this.range = ValueRange.of(-365243219162L + SYNTHETIC_LOCAL_VARIABLE_6, 365241780471L + SYNTHETIC_LOCAL_VARIABLE_6);
      this.offset = SYNTHETIC_LOCAL_VARIABLE_6;
    }
    
    public TemporalUnit getBaseUnit() { return this.baseUnit; }
    
    public TemporalUnit getRangeUnit() { return this.rangeUnit; }
    
    public boolean isDateBased() { return true; }
    
    public boolean isTimeBased() { return false; }
    
    public ValueRange range() { return this.range; }
    
    public boolean isSupportedBy(TemporalAccessor param1TemporalAccessor) { return param1TemporalAccessor.isSupported(ChronoField.EPOCH_DAY); }
    
    public ValueRange rangeRefinedBy(TemporalAccessor param1TemporalAccessor) {
      if (!isSupportedBy(param1TemporalAccessor))
        throw new DateTimeException("Unsupported field: " + this); 
      return range();
    }
    
    public long getFrom(TemporalAccessor param1TemporalAccessor) { return param1TemporalAccessor.getLong(ChronoField.EPOCH_DAY) + this.offset; }
    
    public <R extends Temporal> R adjustInto(R param1R, long param1Long) {
      if (!range().isValidValue(param1Long))
        throw new DateTimeException("Invalid value: " + this.name + " " + param1Long); 
      return (R)param1R.with(ChronoField.EPOCH_DAY, Math.subtractExact(param1Long, this.offset));
    }
    
    public ChronoLocalDate resolve(Map<TemporalField, Long> param1Map, TemporalAccessor param1TemporalAccessor, ResolverStyle param1ResolverStyle) {
      long l = ((Long)param1Map.remove(this)).longValue();
      Chronology chronology = Chronology.from(param1TemporalAccessor);
      if (param1ResolverStyle == ResolverStyle.LENIENT)
        return chronology.dateEpochDay(Math.subtractExact(l, this.offset)); 
      range().checkValidValue(l, this);
      return chronology.dateEpochDay(l - this.offset);
    }
    
    public String toString() { return this.name; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\temporal\JulianFields.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */