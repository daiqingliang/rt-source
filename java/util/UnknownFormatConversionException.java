package java.util;

public class UnknownFormatConversionException extends IllegalFormatException {
  private static final long serialVersionUID = 19060418L;
  
  private String s;
  
  public UnknownFormatConversionException(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    this.s = paramString;
  }
  
  public String getConversion() { return this.s; }
  
  public String getMessage() { return String.format("Conversion = '%s'", new Object[] { this.s }); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\UnknownFormatConversionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */