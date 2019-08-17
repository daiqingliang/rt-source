package java.util;

public class IllegalFormatCodePointException extends IllegalFormatException {
  private static final long serialVersionUID = 19080630L;
  
  private int c;
  
  public IllegalFormatCodePointException(int paramInt) { this.c = paramInt; }
  
  public int getCodePoint() { return this.c; }
  
  public String getMessage() { return String.format("Code point = %#x", new Object[] { Integer.valueOf(this.c) }); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\IllegalFormatCodePointException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */