package org.omg.CORBA;

public final class TIMEOUT extends SystemException {
  public TIMEOUT() { this(""); }
  
  public TIMEOUT(String paramString) { this(paramString, 0, CompletionStatus.COMPLETED_NO); }
  
  public TIMEOUT(int paramInt, CompletionStatus paramCompletionStatus) { this("", paramInt, paramCompletionStatus); }
  
  public TIMEOUT(String paramString, int paramInt, CompletionStatus paramCompletionStatus) { super(paramString, paramInt, paramCompletionStatus); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\TIMEOUT.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */