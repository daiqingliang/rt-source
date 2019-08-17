package org.omg.CORBA;

public final class INVALID_TRANSACTION extends SystemException {
  public INVALID_TRANSACTION() { this(""); }
  
  public INVALID_TRANSACTION(String paramString) { this(paramString, 0, CompletionStatus.COMPLETED_NO); }
  
  public INVALID_TRANSACTION(int paramInt, CompletionStatus paramCompletionStatus) { this("", paramInt, paramCompletionStatus); }
  
  public INVALID_TRANSACTION(String paramString, int paramInt, CompletionStatus paramCompletionStatus) { super(paramString, paramInt, paramCompletionStatus); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\INVALID_TRANSACTION.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */