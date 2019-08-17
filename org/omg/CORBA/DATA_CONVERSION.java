package org.omg.CORBA;

public final class DATA_CONVERSION extends SystemException {
  public DATA_CONVERSION() { this(""); }
  
  public DATA_CONVERSION(String paramString) { this(paramString, 0, CompletionStatus.COMPLETED_NO); }
  
  public DATA_CONVERSION(int paramInt, CompletionStatus paramCompletionStatus) { this("", paramInt, paramCompletionStatus); }
  
  public DATA_CONVERSION(String paramString, int paramInt, CompletionStatus paramCompletionStatus) { super(paramString, paramInt, paramCompletionStatus); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\DATA_CONVERSION.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */