package java.util.logging;

public class ErrorManager {
  private boolean reported = false;
  
  public static final int GENERIC_FAILURE = 0;
  
  public static final int WRITE_FAILURE = 1;
  
  public static final int FLUSH_FAILURE = 2;
  
  public static final int CLOSE_FAILURE = 3;
  
  public static final int OPEN_FAILURE = 4;
  
  public static final int FORMAT_FAILURE = 5;
  
  public void error(String paramString, Exception paramException, int paramInt) {
    if (this.reported)
      return; 
    this.reported = true;
    String str = "java.util.logging.ErrorManager: " + paramInt;
    if (paramString != null)
      str = str + ": " + paramString; 
    System.err.println(str);
    if (paramException != null)
      paramException.printStackTrace(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\logging\ErrorManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */