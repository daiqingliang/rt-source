package sun.management.counter.perf;

import java.nio.LongBuffer;
import sun.management.counter.AbstractCounter;
import sun.management.counter.LongArrayCounter;
import sun.management.counter.Units;
import sun.management.counter.Variability;

public class PerfLongArrayCounter extends AbstractCounter implements LongArrayCounter {
  LongBuffer lb;
  
  private static final long serialVersionUID = -2733617913045487126L;
  
  PerfLongArrayCounter(String paramString, Units paramUnits, Variability paramVariability, int paramInt1, int paramInt2, LongBuffer paramLongBuffer) {
    super(paramString, paramUnits, paramVariability, paramInt1, paramInt2);
    this.lb = paramLongBuffer;
  }
  
  public Object getValue() { return longArrayValue(); }
  
  public long[] longArrayValue() {
    this.lb.position(0);
    long[] arrayOfLong = new long[this.lb.limit()];
    this.lb.get(arrayOfLong);
    return arrayOfLong;
  }
  
  public long longAt(int paramInt) {
    this.lb.position(paramInt);
    return this.lb.get();
  }
  
  protected Object writeReplace() { return new LongArrayCounterSnapshot(getName(), getUnits(), getVariability(), getFlags(), getVectorLength(), longArrayValue()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\counter\perf\PerfLongArrayCounter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */