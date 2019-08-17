package java.io;

public class InvalidClassException extends ObjectStreamException {
  private static final long serialVersionUID = -4333316296251054416L;
  
  public String classname;
  
  public InvalidClassException(String paramString) { super(paramString); }
  
  public InvalidClassException(String paramString1, String paramString2) {
    super(paramString2);
    this.classname = paramString1;
  }
  
  public String getMessage() { return (this.classname == null) ? super.getMessage() : (this.classname + "; " + super.getMessage()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\InvalidClassException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */