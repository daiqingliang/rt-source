package java.time.temporal;

import java.time.Duration;

public interface TemporalUnit {
  Duration getDuration();
  
  boolean isDurationEstimated();
  
  boolean isDateBased();
  
  boolean isTimeBased();
  
  default boolean isSupportedBy(Temporal paramTemporal) {
    if (paramTemporal instanceof java.time.LocalTime)
      return isTimeBased(); 
    if (paramTemporal instanceof java.time.chrono.ChronoLocalDate)
      return isDateBased(); 
    if (paramTemporal instanceof java.time.chrono.ChronoLocalDateTime || paramTemporal instanceof java.time.chrono.ChronoZonedDateTime)
      return true; 
    try {
      paramTemporal.plus(1L, this);
      return true;
    } catch (UnsupportedTemporalTypeException unsupportedTemporalTypeException) {
      return false;
    } catch (RuntimeException runtimeException) {
      try {
        paramTemporal.plus(-1L, this);
        return true;
      } catch (RuntimeException runtimeException1) {
        return false;
      } 
    } 
  }
  
  <R extends Temporal> R addTo(R paramR, long paramLong);
  
  long between(Temporal paramTemporal1, Temporal paramTemporal2);
  
  String toString();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\temporal\TemporalUnit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */