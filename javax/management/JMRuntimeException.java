package javax.management;

public class JMRuntimeException extends RuntimeException {
  private static final long serialVersionUID = 6573344628407841861L;
  
  public JMRuntimeException() {}
  
  public JMRuntimeException(String paramString) { super(paramString); }
  
  JMRuntimeException(String paramString, Throwable paramThrowable) { super(paramString, paramThrowable); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\JMRuntimeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */