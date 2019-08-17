package java.util;

public class IllegalFormatPrecisionException extends IllegalFormatException {
  private static final long serialVersionUID = 18711008L;
  
  private int p;
  
  public IllegalFormatPrecisionException(int paramInt) { this.p = paramInt; }
  
  public int getPrecision() { return this.p; }
  
  public String getMessage() { return Integer.toString(this.p); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\IllegalFormatPrecisionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */