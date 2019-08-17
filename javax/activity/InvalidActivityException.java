package javax.activity;

import java.rmi.RemoteException;

public class InvalidActivityException extends RemoteException {
  public InvalidActivityException() {}
  
  public InvalidActivityException(String paramString) { super(paramString); }
  
  public InvalidActivityException(Throwable paramThrowable) { this("", paramThrowable); }
  
  public InvalidActivityException(String paramString, Throwable paramThrowable) { super(paramString, paramThrowable); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\activity\InvalidActivityException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */