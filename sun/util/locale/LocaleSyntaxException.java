package sun.util.locale;

public class LocaleSyntaxException extends Exception {
  private static final long serialVersionUID = 1L;
  
  private int index = -1;
  
  public LocaleSyntaxException(String paramString) { this(paramString, 0); }
  
  public LocaleSyntaxException(String paramString, int paramInt) {
    super(paramString);
    this.index = paramInt;
  }
  
  public int getErrorIndex() { return this.index; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\LocaleSyntaxException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */