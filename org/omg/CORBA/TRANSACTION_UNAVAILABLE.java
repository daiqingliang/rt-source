package org.omg.CORBA;

public final class TRANSACTION_UNAVAILABLE extends SystemException {
  public TRANSACTION_UNAVAILABLE() { this(""); }
  
  public TRANSACTION_UNAVAILABLE(String paramString) { this(paramString, 0, CompletionStatus.COMPLETED_NO); }
  
  public TRANSACTION_UNAVAILABLE(int paramInt, CompletionStatus paramCompletionStatus) { this("", paramInt, paramCompletionStatus); }
  
  public TRANSACTION_UNAVAILABLE(String paramString, int paramInt, CompletionStatus paramCompletionStatus) { super(paramString, paramInt, paramCompletionStatus); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\TRANSACTION_UNAVAILABLE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */