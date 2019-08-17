package org.omg.CORBA;

public final class UNKNOWN extends SystemException {
  public UNKNOWN() { this(""); }
  
  public UNKNOWN(String paramString) { this(paramString, 0, CompletionStatus.COMPLETED_NO); }
  
  public UNKNOWN(int paramInt, CompletionStatus paramCompletionStatus) { this("", paramInt, paramCompletionStatus); }
  
  public UNKNOWN(String paramString, int paramInt, CompletionStatus paramCompletionStatus) { super(paramString, paramInt, paramCompletionStatus); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\UNKNOWN.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */