package java.rmi;

public class StubNotFoundException extends RemoteException {
  private static final long serialVersionUID = -7088199405468872373L;
  
  public StubNotFoundException(String paramString) { super(paramString); }
  
  public StubNotFoundException(String paramString, Exception paramException) { super(paramString, paramException); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\StubNotFoundException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */