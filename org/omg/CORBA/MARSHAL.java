package org.omg.CORBA;

public final class MARSHAL extends SystemException {
  public MARSHAL() { this(""); }
  
  public MARSHAL(String paramString) { this(paramString, 0, CompletionStatus.COMPLETED_NO); }
  
  public MARSHAL(int paramInt, CompletionStatus paramCompletionStatus) { this("", paramInt, paramCompletionStatus); }
  
  public MARSHAL(String paramString, int paramInt, CompletionStatus paramCompletionStatus) { super(paramString, paramInt, paramCompletionStatus); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\MARSHAL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */