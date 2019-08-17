package java.util;

public class IllegalFormatWidthException extends IllegalFormatException {
  private static final long serialVersionUID = 16660902L;
  
  private int w;
  
  public IllegalFormatWidthException(int paramInt) { this.w = paramInt; }
  
  public int getWidth() { return this.w; }
  
  public String getMessage() { return Integer.toString(this.w); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\IllegalFormatWidthException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */