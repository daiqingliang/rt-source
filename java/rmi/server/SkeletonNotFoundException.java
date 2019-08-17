package java.rmi.server;

import java.rmi.RemoteException;

@Deprecated
public class SkeletonNotFoundException extends RemoteException {
  private static final long serialVersionUID = -7860299673822761231L;
  
  public SkeletonNotFoundException(String paramString) { super(paramString); }
  
  public SkeletonNotFoundException(String paramString, Exception paramException) { super(paramString, paramException); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\server\SkeletonNotFoundException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */