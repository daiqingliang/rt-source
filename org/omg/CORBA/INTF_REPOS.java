package org.omg.CORBA;

public final class INTF_REPOS extends SystemException {
  public INTF_REPOS() { this(""); }
  
  public INTF_REPOS(String paramString) { this(paramString, 0, CompletionStatus.COMPLETED_NO); }
  
  public INTF_REPOS(int paramInt, CompletionStatus paramCompletionStatus) { this("", paramInt, paramCompletionStatus); }
  
  public INTF_REPOS(String paramString, int paramInt, CompletionStatus paramCompletionStatus) { super(paramString, paramInt, paramCompletionStatus); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\INTF_REPOS.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */