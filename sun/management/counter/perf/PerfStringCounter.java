package sun.management.counter.perf;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import sun.management.counter.StringCounter;
import sun.management.counter.Units;
import sun.management.counter.Variability;

public class PerfStringCounter extends PerfByteArrayCounter implements StringCounter {
  private static Charset defaultCharset = Charset.defaultCharset();
  
  private static final long serialVersionUID = 6802913433363692452L;
  
  PerfStringCounter(String paramString, Variability paramVariability, int paramInt, ByteBuffer paramByteBuffer) { this(paramString, paramVariability, paramInt, paramByteBuffer.limit(), paramByteBuffer); }
  
  PerfStringCounter(String paramString, Variability paramVariability, int paramInt1, int paramInt2, ByteBuffer paramByteBuffer) { super(paramString, Units.STRING, paramVariability, paramInt1, paramInt2, paramByteBuffer); }
  
  public boolean isVector() { return false; }
  
  public int getVectorLength() { return 0; }
  
  public Object getValue() { return stringValue(); }
  
  public String stringValue() {
    String str = "";
    byte[] arrayOfByte = byteArrayValue();
    if (arrayOfByte == null || arrayOfByte.length <= 1)
      return str; 
    byte b;
    for (b = 0; b < arrayOfByte.length && arrayOfByte[b] != 0; b++);
    return new String(arrayOfByte, 0, b, defaultCharset);
  }
  
  protected Object writeReplace() { return new StringCounterSnapshot(getName(), getUnits(), getVariability(), getFlags(), stringValue()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\counter\perf\PerfStringCounter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */