package java.util;

public class IllformedLocaleException extends RuntimeException {
  private static final long serialVersionUID = -5245986824925681401L;
  
  private int _errIdx = -1;
  
  public IllformedLocaleException() {}
  
  public IllformedLocaleException(String paramString) { super(paramString); }
  
  public IllformedLocaleException(String paramString, int paramInt) {
    super(paramString + ((paramInt < 0) ? "" : (" [at index " + paramInt + "]")));
    this._errIdx = paramInt;
  }
  
  public int getErrorIndex() { return this._errIdx; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\IllformedLocaleException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */