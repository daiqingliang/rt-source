package java.rmi;

public class ServerException extends RemoteException {
  private static final long serialVersionUID = -4775845313121906682L;
  
  public ServerException(String paramString) { super(paramString); }
  
  public ServerException(String paramString, Exception paramException) { super(paramString, paramException); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\ServerException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */