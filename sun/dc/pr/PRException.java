package sun.dc.pr;

public class PRException extends Exception {
  public static final String BAD_COORD_setOutputArea = "setOutputArea: alpha coordinate out of bounds";
  
  public static final String ALPHA_ARRAY_SHORT = "writeAlpha: alpha destination array too short";
  
  public static final String DUMMY = "";
  
  public PRException() {}
  
  public PRException(String paramString) { super(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\dc\pr\PRException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */