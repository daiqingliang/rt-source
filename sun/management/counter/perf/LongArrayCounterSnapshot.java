package sun.management.counter.perf;

import sun.management.counter.AbstractCounter;
import sun.management.counter.LongArrayCounter;
import sun.management.counter.Units;
import sun.management.counter.Variability;

class LongArrayCounterSnapshot extends AbstractCounter implements LongArrayCounter {
  long[] value;
  
  private static final long serialVersionUID = 3585870271405924292L;
  
  LongArrayCounterSnapshot(String paramString, Units paramUnits, Variability paramVariability, int paramInt1, int paramInt2, long[] paramArrayOfLong) {
    super(paramString, paramUnits, paramVariability, paramInt1, paramInt2);
    this.value = paramArrayOfLong;
  }
  
  public Object getValue() { return this.value; }
  
  public long[] longArrayValue() { return this.value; }
  
  public long longAt(int paramInt) { return this.value[paramInt]; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\counter\perf\LongArrayCounterSnapshot.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */