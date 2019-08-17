package java.util;

public class MissingFormatWidthException extends IllegalFormatException {
  private static final long serialVersionUID = 15560123L;
  
  private String s;
  
  public MissingFormatWidthException(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    this.s = paramString;
  }
  
  public String getFormatSpecifier() { return this.s; }
  
  public String getMessage() { return this.s; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\MissingFormatWidthException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */