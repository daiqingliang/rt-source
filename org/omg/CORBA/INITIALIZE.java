package org.omg.CORBA;

public final class INITIALIZE extends SystemException {
  public INITIALIZE() { this(""); }
  
  public INITIALIZE(String paramString) { this(paramString, 0, CompletionStatus.COMPLETED_NO); }
  
  public INITIALIZE(int paramInt, CompletionStatus paramCompletionStatus) { this("", paramInt, paramCompletionStatus); }
  
  public INITIALIZE(String paramString, int paramInt, CompletionStatus paramCompletionStatus) { super(paramString, paramInt, paramCompletionStatus); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\INITIALIZE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */