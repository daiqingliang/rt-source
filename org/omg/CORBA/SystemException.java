package org.omg.CORBA;

public abstract class SystemException extends RuntimeException {
  public int minor;
  
  public CompletionStatus completed;
  
  protected SystemException(String paramString, int paramInt, CompletionStatus paramCompletionStatus) {
    super(paramString);
    this.minor = paramInt;
    this.completed = paramCompletionStatus;
  }
  
  public String toString() {
    null = super.toString();
    int i = this.minor & 0xFFFFF000;
    switch (i) {
      case 1330446336:
        null = null + "  vmcid: OMG";
        break;
      case 1398079488:
        null = null + "  vmcid: SUN";
        break;
      default:
        null = null + "  vmcid: 0x" + Integer.toHexString(i);
        break;
    } 
    int j = this.minor & 0xFFF;
    null = null + "  minor code: " + j;
    switch (this.completed.value()) {
      case 0:
        return null + "  completed: Yes";
      case 1:
        return SYNTHETIC_LOCAL_VARIABLE_1 + "  completed: No";
    } 
    return SYNTHETIC_LOCAL_VARIABLE_1 + " completed: Maybe";
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\SystemException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */