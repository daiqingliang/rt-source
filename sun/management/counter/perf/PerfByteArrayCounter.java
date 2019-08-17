package sun.management.counter.perf;

import java.nio.ByteBuffer;
import sun.management.counter.AbstractCounter;
import sun.management.counter.ByteArrayCounter;
import sun.management.counter.Units;
import sun.management.counter.Variability;

public class PerfByteArrayCounter extends AbstractCounter implements ByteArrayCounter {
  ByteBuffer bb;
  
  private static final long serialVersionUID = 2545474036937279921L;
  
  PerfByteArrayCounter(String paramString, Units paramUnits, Variability paramVariability, int paramInt1, int paramInt2, ByteBuffer paramByteBuffer) {
    super(paramString, paramUnits, paramVariability, paramInt1, paramInt2);
    this.bb = paramByteBuffer;
  }
  
  public Object getValue() { return byteArrayValue(); }
  
  public byte[] byteArrayValue() {
    this.bb.position(0);
    byte[] arrayOfByte = new byte[this.bb.limit()];
    this.bb.get(arrayOfByte);
    return arrayOfByte;
  }
  
  public byte byteAt(int paramInt) {
    this.bb.position(paramInt);
    return this.bb.get();
  }
  
  public String toString() {
    String str = getName() + ": " + new String(byteArrayValue()) + " " + getUnits();
    return isInternal() ? (str + " [INTERNAL]") : str;
  }
  
  protected Object writeReplace() { return new ByteArrayCounterSnapshot(getName(), getUnits(), getVariability(), getFlags(), getVectorLength(), byteArrayValue()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\counter\perf\PerfByteArrayCounter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */