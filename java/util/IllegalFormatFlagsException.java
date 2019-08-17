package java.util;

public class IllegalFormatFlagsException extends IllegalFormatException {
  private static final long serialVersionUID = 790824L;
  
  private String flags;
  
  public IllegalFormatFlagsException(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    this.flags = paramString;
  }
  
  public String getFlags() { return this.flags; }
  
  public String getMessage() { return "Flags = '" + this.flags + "'"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\IllegalFormatFlagsException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */