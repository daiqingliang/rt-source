package org.omg.CORBA;

public final class OBJ_ADAPTER extends SystemException {
  public OBJ_ADAPTER() { this(""); }
  
  public OBJ_ADAPTER(String paramString) { this(paramString, 0, CompletionStatus.COMPLETED_NO); }
  
  public OBJ_ADAPTER(int paramInt, CompletionStatus paramCompletionStatus) { this("", paramInt, paramCompletionStatus); }
  
  public OBJ_ADAPTER(String paramString, int paramInt, CompletionStatus paramCompletionStatus) { super(paramString, paramInt, paramCompletionStatus); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\OBJ_ADAPTER.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */