package java.io;

public class InterruptedIOException extends IOException {
  private static final long serialVersionUID = 4020568460727500567L;
  
  public int bytesTransferred = 0;
  
  public InterruptedIOException() {}
  
  public InterruptedIOException(String paramString) { super(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\InterruptedIOException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */