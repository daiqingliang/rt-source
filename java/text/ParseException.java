package java.text;

public class ParseException extends Exception {
  private static final long serialVersionUID = 2703218443322787634L;
  
  private int errorOffset;
  
  public ParseException(String paramString, int paramInt) {
    super(paramString);
    this.errorOffset = paramInt;
  }
  
  public int getErrorOffset() { return this.errorOffset; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\ParseException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */