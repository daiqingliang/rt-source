package java.time.chrono;

import java.time.DateTimeException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;

public static enum HijrahEra implements Era {
  AH;
  
  public static HijrahEra of(int paramInt) {
    if (paramInt == 1)
      return AH; 
    throw new DateTimeException("Invalid era: " + paramInt);
  }
  
  public int getValue() { return 1; }
  
  public ValueRange range(TemporalField paramTemporalField) { return (paramTemporalField == ChronoField.ERA) ? ValueRange.of(1L, 1L) : super.range(paramTemporalField); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\chrono\HijrahEra.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */