package org.omg.CORBA;

public final class REBIND extends SystemException {
  public REBIND() { this(""); }
  
  public REBIND(String paramString) { this(paramString, 0, CompletionStatus.COMPLETED_NO); }
  
  public REBIND(int paramInt, CompletionStatus paramCompletionStatus) { this("", paramInt, paramCompletionStatus); }
  
  public REBIND(String paramString, int paramInt, CompletionStatus paramCompletionStatus) { super(paramString, paramInt, paramCompletionStatus); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\REBIND.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */