package javax.activity;

import java.rmi.RemoteException;

public class ActivityRequiredException extends RemoteException {
  public ActivityRequiredException() {}
  
  public ActivityRequiredException(String paramString) { super(paramString); }
  
  public ActivityRequiredException(Throwable paramThrowable) { this("", paramThrowable); }
  
  public ActivityRequiredException(String paramString, Throwable paramThrowable) { super(paramString, paramThrowable); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\activity\ActivityRequiredException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */