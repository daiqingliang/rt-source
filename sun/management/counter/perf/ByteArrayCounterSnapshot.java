package sun.management.counter.perf;

import sun.management.counter.AbstractCounter;
import sun.management.counter.ByteArrayCounter;
import sun.management.counter.Units;
import sun.management.counter.Variability;

class ByteArrayCounterSnapshot extends AbstractCounter implements ByteArrayCounter {
  byte[] value;
  
  private static final long serialVersionUID = 1444793459838438979L;
  
  ByteArrayCounterSnapshot(String paramString, Units paramUnits, Variability paramVariability, int paramInt1, int paramInt2, byte[] paramArrayOfByte) {
    super(paramString, paramUnits, paramVariability, paramInt1, paramInt2);
    this.value = paramArrayOfByte;
  }
  
  public Object getValue() { return this.value; }
  
  public byte[] byteArrayValue() { return this.value; }
  
  public byte byteAt(int paramInt) { return this.value[paramInt]; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\counter\perf\ByteArrayCounterSnapshot.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */