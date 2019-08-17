package org.omg.CORBA;

public final class FREE_MEM extends SystemException {
  public FREE_MEM() { this(""); }
  
  public FREE_MEM(String paramString) { this(paramString, 0, CompletionStatus.COMPLETED_NO); }
  
  public FREE_MEM(int paramInt, CompletionStatus paramCompletionStatus) { this("", paramInt, paramCompletionStatus); }
  
  public FREE_MEM(String paramString, int paramInt, CompletionStatus paramCompletionStatus) { super(paramString, paramInt, paramCompletionStatus); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\FREE_MEM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */