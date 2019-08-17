package java.util;

public class MissingFormatArgumentException extends IllegalFormatException {
  private static final long serialVersionUID = 19190115L;
  
  private String s;
  
  public MissingFormatArgumentException(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    this.s = paramString;
  }
  
  public String getFormatSpecifier() { return this.s; }
  
  public String getMessage() { return "Format specifier '" + this.s + "'"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\MissingFormatArgumentException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */