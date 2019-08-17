package java.awt;

public class HeadlessException extends UnsupportedOperationException {
  private static final long serialVersionUID = 167183644944358563L;
  
  public HeadlessException() {}
  
  public HeadlessException(String paramString) { super(paramString); }
  
  public String getMessage() {
    String str1 = super.getMessage();
    String str2 = GraphicsEnvironment.getHeadlessMessage();
    return (str1 == null) ? str2 : ((str2 == null) ? str1 : (str1 + str2));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\HeadlessException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */