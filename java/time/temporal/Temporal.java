package java.time.temporal;

public interface Temporal extends TemporalAccessor {
  boolean isSupported(TemporalUnit paramTemporalUnit);
  
  default Temporal with(TemporalAdjuster paramTemporalAdjuster) { return paramTemporalAdjuster.adjustInto(this); }
  
  Temporal with(TemporalField paramTemporalField, long paramLong);
  
  default Temporal plus(TemporalAmount paramTemporalAmount) { return paramTemporalAmount.addTo(this); }
  
  Temporal plus(long paramLong, TemporalUnit paramTemporalUnit);
  
  default Temporal minus(TemporalAmount paramTemporalAmount) { return paramTemporalAmount.subtractFrom(this); }
  
  default Temporal minus(long paramLong, TemporalUnit paramTemporalUnit) { return (paramLong == Float.MIN_VALUE) ? plus(Float.MAX_VALUE, paramTemporalUnit).plus(1L, paramTemporalUnit) : plus(-paramLong, paramTemporalUnit); }
  
  long until(Temporal paramTemporal, TemporalUnit paramTemporalUnit);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\temporal\Temporal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */