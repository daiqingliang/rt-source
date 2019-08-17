package java.util;

public class FormatFlagsConversionMismatchException extends IllegalFormatException {
  private static final long serialVersionUID = 19120414L;
  
  private String f;
  
  private char c;
  
  public FormatFlagsConversionMismatchException(String paramString, char paramChar) {
    if (paramString == null)
      throw new NullPointerException(); 
    this.f = paramString;
    this.c = paramChar;
  }
  
  public String getFlags() { return this.f; }
  
  public char getConversion() { return this.c; }
  
  public String getMessage() { return "Conversion = " + this.c + ", Flags = " + this.f; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\FormatFlagsConversionMismatchException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */