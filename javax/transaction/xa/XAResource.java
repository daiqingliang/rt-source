package javax.transaction.xa;

public interface XAResource {
  public static final int TMENDRSCAN = 8388608;
  
  public static final int TMFAIL = 536870912;
  
  public static final int TMJOIN = 2097152;
  
  public static final int TMNOFLAGS = 0;
  
  public static final int TMONEPHASE = 1073741824;
  
  public static final int TMRESUME = 134217728;
  
  public static final int TMSTARTRSCAN = 16777216;
  
  public static final int TMSUCCESS = 67108864;
  
  public static final int TMSUSPEND = 33554432;
  
  public static final int XA_RDONLY = 3;
  
  public static final int XA_OK = 0;
  
  void commit(Xid paramXid, boolean paramBoolean) throws XAException;
  
  void end(Xid paramXid, int paramInt) throws XAException;
  
  void forget(Xid paramXid) throws XAException;
  
  int getTransactionTimeout() throws XAException;
  
  boolean isSameRM(XAResource paramXAResource) throws XAException;
  
  int prepare(Xid paramXid) throws XAException;
  
  Xid[] recover(int paramInt) throws XAException;
  
  void rollback(Xid paramXid) throws XAException;
  
  boolean setTransactionTimeout(int paramInt) throws XAException;
  
  void start(Xid paramXid, int paramInt) throws XAException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\transaction\xa\XAResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */