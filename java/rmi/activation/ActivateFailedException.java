package java.rmi.activation;

import java.rmi.RemoteException;

public class ActivateFailedException extends RemoteException {
  private static final long serialVersionUID = 4863550261346652506L;
  
  public ActivateFailedException(String paramString) { super(paramString); }
  
  public ActivateFailedException(String paramString, Exception paramException) { super(paramString, paramException); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\activation\ActivateFailedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */