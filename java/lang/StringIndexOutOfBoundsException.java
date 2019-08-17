package java.lang;

public class StringIndexOutOfBoundsException extends IndexOutOfBoundsException {
  private static final long serialVersionUID = -6762910422159637258L;
  
  public StringIndexOutOfBoundsException() {}
  
  public StringIndexOutOfBoundsException(String paramString) { super(paramString); }
  
  public StringIndexOutOfBoundsException(int paramInt) { super("String index out of range: " + paramInt); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\StringIndexOutOfBoundsException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */