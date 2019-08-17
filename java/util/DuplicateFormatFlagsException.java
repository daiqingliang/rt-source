package java.util;

public class DuplicateFormatFlagsException extends IllegalFormatException {
  private static final long serialVersionUID = 18890531L;
  
  private String flags;
  
  public DuplicateFormatFlagsException(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    this.flags = paramString;
  }
  
  public String getFlags() { return this.flags; }
  
  public String getMessage() { return String.format("Flags = '%s'", new Object[] { this.flags }); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\DuplicateFormatFlagsException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */