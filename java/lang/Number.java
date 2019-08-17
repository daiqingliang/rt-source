package java.lang;

import java.io.Serializable;

public abstract class Number implements Serializable {
  private static final long serialVersionUID = -8742448824652078965L;
  
  public abstract int intValue();
  
  public abstract long longValue();
  
  public abstract float floatValue();
  
  public abstract double doubleValue();
  
  public byte byteValue() { return (byte)intValue(); }
  
  public short shortValue() { return (short)intValue(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\Number.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */