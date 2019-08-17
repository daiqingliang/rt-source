package org.omg.CORBA;

public final class PERSIST_STORE extends SystemException {
  public PERSIST_STORE() { this(""); }
  
  public PERSIST_STORE(String paramString) { this(paramString, 0, CompletionStatus.COMPLETED_NO); }
  
  public PERSIST_STORE(int paramInt, CompletionStatus paramCompletionStatus) { this("", paramInt, paramCompletionStatus); }
  
  public PERSIST_STORE(String paramString, int paramInt, CompletionStatus paramCompletionStatus) { super(paramString, paramInt, paramCompletionStatus); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\PERSIST_STORE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */