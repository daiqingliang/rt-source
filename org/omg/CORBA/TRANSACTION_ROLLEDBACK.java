package org.omg.CORBA;

public final class TRANSACTION_ROLLEDBACK extends SystemException {
  public TRANSACTION_ROLLEDBACK() { this(""); }
  
  public TRANSACTION_ROLLEDBACK(String paramString) { this(paramString, 0, CompletionStatus.COMPLETED_NO); }
  
  public TRANSACTION_ROLLEDBACK(int paramInt, CompletionStatus paramCompletionStatus) { this("", paramInt, paramCompletionStatus); }
  
  public TRANSACTION_ROLLEDBACK(String paramString, int paramInt, CompletionStatus paramCompletionStatus) { super(paramString, paramInt, paramCompletionStatus); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\TRANSACTION_ROLLEDBACK.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */