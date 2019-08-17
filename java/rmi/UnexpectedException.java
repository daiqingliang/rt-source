package java.rmi;

public class UnexpectedException extends RemoteException {
  private static final long serialVersionUID = 1800467484195073863L;
  
  public UnexpectedException(String paramString) { super(paramString); }
  
  public UnexpectedException(String paramString, Exception paramException) { super(paramString, paramException); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\UnexpectedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */