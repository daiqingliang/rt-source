package org.omg.CORBA;

public final class INVALID_ACTIVITY extends SystemException {
  public INVALID_ACTIVITY() { this(""); }
  
  public INVALID_ACTIVITY(String paramString) { this(paramString, 0, CompletionStatus.COMPLETED_NO); }
  
  public INVALID_ACTIVITY(int paramInt, CompletionStatus paramCompletionStatus) { this("", paramInt, paramCompletionStatus); }
  
  public INVALID_ACTIVITY(String paramString, int paramInt, CompletionStatus paramCompletionStatus) { super(paramString, paramInt, paramCompletionStatus); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\INVALID_ACTIVITY.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */