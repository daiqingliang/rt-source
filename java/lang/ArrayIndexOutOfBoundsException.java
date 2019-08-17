package java.lang;

public class ArrayIndexOutOfBoundsException extends IndexOutOfBoundsException {
  private static final long serialVersionUID = -5116101128118950844L;
  
  public ArrayIndexOutOfBoundsException() {}
  
  public ArrayIndexOutOfBoundsException(int paramInt) { super("Array index out of range: " + paramInt); }
  
  public ArrayIndexOutOfBoundsException(String paramString) { super(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\ArrayIndexOutOfBoundsException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */