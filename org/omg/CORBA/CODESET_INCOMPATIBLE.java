package org.omg.CORBA;

public final class CODESET_INCOMPATIBLE extends SystemException {
  public CODESET_INCOMPATIBLE() { this(""); }
  
  public CODESET_INCOMPATIBLE(String paramString) { this(paramString, 0, CompletionStatus.COMPLETED_NO); }
  
  public CODESET_INCOMPATIBLE(int paramInt, CompletionStatus paramCompletionStatus) { this("", paramInt, paramCompletionStatus); }
  
  public CODESET_INCOMPATIBLE(String paramString, int paramInt, CompletionStatus paramCompletionStatus) { super(paramString, paramInt, paramCompletionStatus); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\CODESET_INCOMPATIBLE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */