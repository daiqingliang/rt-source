package javax.transaction.xa;

public interface Xid {
  public static final int MAXGTRIDSIZE = 64;
  
  public static final int MAXBQUALSIZE = 64;
  
  int getFormatId();
  
  byte[] getGlobalTransactionId();
  
  byte[] getBranchQualifier();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\transaction\xa\Xid.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */