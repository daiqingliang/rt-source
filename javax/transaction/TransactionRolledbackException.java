package javax.transaction;

import java.rmi.RemoteException;

public class TransactionRolledbackException extends RemoteException {
  public TransactionRolledbackException() {}
  
  public TransactionRolledbackException(String paramString) { super(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\transaction\TransactionRolledbackException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */