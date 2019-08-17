package java.time.chrono;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Objects;

public interface ChronoPeriod extends TemporalAmount {
  static ChronoPeriod between(ChronoLocalDate paramChronoLocalDate1, ChronoLocalDate paramChronoLocalDate2) {
    Objects.requireNonNull(paramChronoLocalDate1, "startDateInclusive");
    Objects.requireNonNull(paramChronoLocalDate2, "endDateExclusive");
    return paramChronoLocalDate1.until(paramChronoLocalDate2);
  }
  
  long get(TemporalUnit paramTemporalUnit);
  
  List<TemporalUnit> getUnits();
  
  Chronology getChronology();
  
  default boolean isZero() {
    for (TemporalUnit temporalUnit : getUnits()) {
      if (get(temporalUnit) != 0L)
        return false; 
    } 
    return true;
  }
  
  default boolean isNegative() {
    for (TemporalUnit temporalUnit : getUnits()) {
      if (get(temporalUnit) < 0L)
        return true; 
    } 
    return false;
  }
  
  ChronoPeriod plus(TemporalAmount paramTemporalAmount);
  
  ChronoPeriod minus(TemporalAmount paramTemporalAmount);
  
  ChronoPeriod multipliedBy(int paramInt);
  
  default ChronoPeriod negated() { return multipliedBy(-1); }
  
  ChronoPeriod normalized();
  
  Temporal addTo(Temporal paramTemporal);
  
  Temporal subtractFrom(Temporal paramTemporal);
  
  boolean equals(Object paramObject);
  
  int hashCode();
  
  String toString();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\chrono\ChronoPeriod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */