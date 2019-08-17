package java.rmi;

public class MarshalException extends RemoteException {
  private static final long serialVersionUID = 6223554758134037936L;
  
  public MarshalException(String paramString) { super(paramString); }
  
  public MarshalException(String paramString, Exception paramException) { super(paramString, paramException); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\MarshalException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */