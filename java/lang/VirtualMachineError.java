package java.lang;

public abstract class VirtualMachineError extends Error {
  private static final long serialVersionUID = 4161983926571568670L;
  
  public VirtualMachineError() {}
  
  public VirtualMachineError(String paramString) { super(paramString); }
  
  public VirtualMachineError(String paramString, Throwable paramThrowable) { super(paramString, paramThrowable); }
  
  public VirtualMachineError(Throwable paramThrowable) { super(paramThrowable); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\VirtualMachineError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */