package java.time.temporal;

import java.util.List;

public interface TemporalAmount {
  long get(TemporalUnit paramTemporalUnit);
  
  List<TemporalUnit> getUnits();
  
  Temporal addTo(Temporal paramTemporal);
  
  Temporal subtractFrom(Temporal paramTemporal);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\temporal\TemporalAmount.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */