package org.omg.CORBA;

public final class BAD_CONTEXT extends SystemException {
  public BAD_CONTEXT() { this(""); }
  
  public BAD_CONTEXT(String paramString) { this(paramString, 0, CompletionStatus.COMPLETED_NO); }
  
  public BAD_CONTEXT(int paramInt, CompletionStatus paramCompletionStatus) { this("", paramInt, paramCompletionStatus); }
  
  public BAD_CONTEXT(String paramString, int paramInt, CompletionStatus paramCompletionStatus) { super(paramString, paramInt, paramCompletionStatus); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\BAD_CONTEXT.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */