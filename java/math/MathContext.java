package java.math;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;

public final class MathContext implements Serializable {
  private static final int DEFAULT_DIGITS = 9;
  
  private static final RoundingMode DEFAULT_ROUNDINGMODE = RoundingMode.HALF_UP;
  
  private static final int MIN_DIGITS = 0;
  
  private static final long serialVersionUID = 5579720004786848255L;
  
  public static final MathContext UNLIMITED = new MathContext(0, RoundingMode.HALF_UP);
  
  public static final MathContext DECIMAL32 = new MathContext(7, RoundingMode.HALF_EVEN);
  
  public static final MathContext DECIMAL64 = new MathContext(16, RoundingMode.HALF_EVEN);
  
  public static final MathContext DECIMAL128 = new MathContext(34, RoundingMode.HALF_EVEN);
  
  final int precision;
  
  final RoundingMode roundingMode;
  
  public MathContext(int paramInt) { this(paramInt, DEFAULT_ROUNDINGMODE); }
  
  public MathContext(int paramInt, RoundingMode paramRoundingMode) {
    if (paramInt < 0)
      throw new IllegalArgumentException("Digits < 0"); 
    if (paramRoundingMode == null)
      throw new NullPointerException("null RoundingMode"); 
    this.precision = paramInt;
    this.roundingMode = paramRoundingMode;
  }
  
  public MathContext(String paramString) {
    boolean bool = false;
    if (paramString == null)
      throw new NullPointerException("null String"); 
    try {
      if (!paramString.startsWith("precision="))
        throw new RuntimeException(); 
      int j = paramString.indexOf(' ');
      int k = 10;
      i = Integer.parseInt(paramString.substring(10, j));
      if (!paramString.startsWith("roundingMode=", j + 1))
        throw new RuntimeException(); 
      k = j + 1 + 13;
      String str = paramString.substring(k, paramString.length());
      this.roundingMode = RoundingMode.valueOf(str);
    } catch (RuntimeException runtimeException) {
      throw new IllegalArgumentException("bad string format");
    } 
    if (i < 0)
      throw new IllegalArgumentException("Digits < 0"); 
    this.precision = i;
  }
  
  public int getPrecision() { return this.precision; }
  
  public RoundingMode getRoundingMode() { return this.roundingMode; }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof MathContext))
      return false; 
    MathContext mathContext = (MathContext)paramObject;
    return (mathContext.precision == this.precision && mathContext.roundingMode == this.roundingMode);
  }
  
  public int hashCode() { return this.precision + this.roundingMode.hashCode() * 59; }
  
  public String toString() { return "precision=" + this.precision + " roundingMode=" + this.roundingMode.toString(); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    if (this.precision < 0) {
      String str = "MathContext: invalid digits in stream";
      throw new StreamCorruptedException(str);
    } 
    if (this.roundingMode == null) {
      String str = "MathContext: null roundingMode in stream";
      throw new StreamCorruptedException(str);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\math\MathContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */