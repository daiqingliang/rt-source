package sun.dc.path;

public class PathException extends Exception {
  public static final String BAD_PATH_endPath = "endPath: bad path";
  
  public static final String BAD_PATH_useProxy = "useProxy: bad path";
  
  public static final String DUMMY = "";
  
  public PathException() {}
  
  public PathException(String paramString) { super(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\dc\path\PathException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */