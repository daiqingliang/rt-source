package sun.management.counter.perf;

import sun.management.counter.AbstractCounter;
import sun.management.counter.LongCounter;
import sun.management.counter.Units;
import sun.management.counter.Variability;

class LongCounterSnapshot extends AbstractCounter implements LongCounter {
  long value;
  
  private static final long serialVersionUID = 2054263861474565758L;
  
  LongCounterSnapshot(String paramString, Units paramUnits, Variability paramVariability, int paramInt, long paramLong) {
    super(paramString, paramUnits, paramVariability, paramInt);
    this.value = paramLong;
  }
  
  public Object getValue() { return new Long(this.value); }
  
  public long longValue() { return this.value; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\counter\perf\LongCounterSnapshot.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */