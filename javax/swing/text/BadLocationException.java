package javax.swing.text;

public class BadLocationException extends Exception {
  private int offs;
  
  public BadLocationException(String paramString, int paramInt) {
    super(paramString);
    this.offs = paramInt;
  }
  
  public int offsetRequested() { return this.offs; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\BadLocationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */