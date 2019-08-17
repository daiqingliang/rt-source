package javax.transaction;

import java.rmi.RemoteException;

public class InvalidTransactionException extends RemoteException {
  public InvalidTransactionException() {}
  
  public InvalidTransactionException(String paramString) { super(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\transaction\InvalidTransactionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */