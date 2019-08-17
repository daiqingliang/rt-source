package org.omg.CORBA;

public final class IMP_LIMIT extends SystemException {
  public IMP_LIMIT() { this(""); }
  
  public IMP_LIMIT(String paramString) { this(paramString, 0, CompletionStatus.COMPLETED_NO); }
  
  public IMP_LIMIT(int paramInt, CompletionStatus paramCompletionStatus) { this("", paramInt, paramCompletionStatus); }
  
  public IMP_LIMIT(String paramString, int paramInt, CompletionStatus paramCompletionStatus) { super(paramString, paramInt, paramCompletionStatus); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\IMP_LIMIT.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */