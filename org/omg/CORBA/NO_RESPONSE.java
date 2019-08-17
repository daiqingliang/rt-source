package org.omg.CORBA;

public final class NO_RESPONSE extends SystemException {
  public NO_RESPONSE() { this(""); }
  
  public NO_RESPONSE(String paramString) { this(paramString, 0, CompletionStatus.COMPLETED_NO); }
  
  public NO_RESPONSE(int paramInt, CompletionStatus paramCompletionStatus) { this("", paramInt, paramCompletionStatus); }
  
  public NO_RESPONSE(String paramString, int paramInt, CompletionStatus paramCompletionStatus) { super(paramString, paramInt, paramCompletionStatus); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\NO_RESPONSE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */