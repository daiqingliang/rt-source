package java.awt.dnd;

public class InvalidDnDOperationException extends IllegalStateException {
  private static final long serialVersionUID = -6062568741193956678L;
  
  private static String dft_msg = "The operation requested cannot be performed by the DnD system since it is not in the appropriate state";
  
  public InvalidDnDOperationException() { super(dft_msg); }
  
  public InvalidDnDOperationException(String paramString) { super(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\dnd\InvalidDnDOperationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */