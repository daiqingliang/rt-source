package javax.activity;

import java.rmi.RemoteException;

public class ActivityCompletedException extends RemoteException {
  public ActivityCompletedException() {}
  
  public ActivityCompletedException(String paramString) { super(paramString); }
  
  public ActivityCompletedException(Throwable paramThrowable) { this("", paramThrowable); }
  
  public ActivityCompletedException(String paramString, Throwable paramThrowable) { super(paramString, paramThrowable); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\activity\ActivityCompletedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */