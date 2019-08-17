package org.omg.CORBA;

public final class ACTIVITY_COMPLETED extends SystemException {
  public ACTIVITY_COMPLETED() { this(""); }
  
  public ACTIVITY_COMPLETED(String paramString) { this(paramString, 0, CompletionStatus.COMPLETED_NO); }
  
  public ACTIVITY_COMPLETED(int paramInt, CompletionStatus paramCompletionStatus) { this("", paramInt, paramCompletionStatus); }
  
  public ACTIVITY_COMPLETED(String paramString, int paramInt, CompletionStatus paramCompletionStatus) { super(paramString, paramInt, paramCompletionStatus); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\ACTIVITY_COMPLETED.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */