package java.rmi;

public class AccessException extends RemoteException {
  private static final long serialVersionUID = 6314925228044966088L;
  
  public AccessException(String paramString) { super(paramString); }
  
  public AccessException(String paramString, Exception paramException) { super(paramString, paramException); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\AccessException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */