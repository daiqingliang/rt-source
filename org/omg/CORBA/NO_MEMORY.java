package org.omg.CORBA;

public final class NO_MEMORY extends SystemException {
  public NO_MEMORY() { this(""); }
  
  public NO_MEMORY(String paramString) { this(paramString, 0, CompletionStatus.COMPLETED_NO); }
  
  public NO_MEMORY(int paramInt, CompletionStatus paramCompletionStatus) { this("", paramInt, paramCompletionStatus); }
  
  public NO_MEMORY(String paramString, int paramInt, CompletionStatus paramCompletionStatus) { super(paramString, paramInt, paramCompletionStatus); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\NO_MEMORY.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */