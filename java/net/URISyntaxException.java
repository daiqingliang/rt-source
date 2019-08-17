package java.net;

public class URISyntaxException extends Exception {
  private static final long serialVersionUID = 2137979680897488891L;
  
  private String input;
  
  private int index;
  
  public URISyntaxException(String paramString1, String paramString2, int paramInt) {
    super(paramString2);
    if (paramString1 == null || paramString2 == null)
      throw new NullPointerException(); 
    if (paramInt < -1)
      throw new IllegalArgumentException(); 
    this.input = paramString1;
    this.index = paramInt;
  }
  
  public URISyntaxException(String paramString1, String paramString2) { this(paramString1, paramString2, -1); }
  
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\URISyntaxException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */