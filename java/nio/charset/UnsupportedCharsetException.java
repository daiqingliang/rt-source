package java.nio.charset;

public class UnsupportedCharsetException extends IllegalArgumentException {
  private static final long serialVersionUID = 1490765524727386367L;
  
  private String charsetName;
  
  public UnsupportedCharsetException(String paramString) {
    super(String.valueOf(paramString));
    this.charsetName = paramString;
  }
  
  public String getCharsetName() { return this.charsetName; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\charset\UnsupportedCharsetException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */