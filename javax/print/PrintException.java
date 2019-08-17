package javax.print;

public class PrintException extends Exception {
  public PrintException() {}
  
  public PrintException(String paramString) { super(paramString); }
  
  public PrintException(Exception paramException) { super(paramException); }
  
  public PrintException(String paramString, Exception paramException) { super(paramString, paramException); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\PrintException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */