package java.nio.file;

public class InvalidPathException extends IllegalArgumentException {
  static final long serialVersionUID = 4355821422286746137L;
  
  private String input;
  
  private int index;
  
  public InvalidPathException(String paramString1, String paramString2, int paramInt) {
    super(paramString2);
    if (paramString1 == null || paramString2 == null)
      throw new NullPointerException(); 
    if (paramInt < -1)
      throw new IllegalArgumentException(); 
    this.input = paramString1;
    this.index = paramInt;
  }
  
  public InvalidPathException(String paramString1, String paramString2) { this(paramString1, paramString2, -1); }
  
  public String getInput() { return this.input; }
  
  public String getReason() { return super.getMessage(); }
  
  public int getIndex() { return this.index; }
  
  public String getMessage() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(getReason());
    if (this.index > -1) {
      stringBuffer.append(" at index ");
      stringBuffer.append(this.index);
    } 
    stringBuffer.append(": ");
    stringBuffer.append(this.input);
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\InvalidPathException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */