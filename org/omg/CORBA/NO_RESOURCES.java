package org.omg.CORBA;

public final class NO_RESOURCES extends SystemException {
  public NO_RESOURCES() { this(""); }
  
  public NO_RESOURCES(String paramString) { this(paramString, 0, CompletionStatus.COMPLETED_NO); }
  
  public NO_RESOURCES(int paramInt, CompletionStatus paramCompletionStatus) { this("", paramInt, paramCompletionStatus); }
  
  public NO_RESOURCES(String paramString, int paramInt, CompletionStatus paramCompletionStatus) { super(paramString, paramInt, paramCompletionStatus); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\NO_RESOURCES.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */