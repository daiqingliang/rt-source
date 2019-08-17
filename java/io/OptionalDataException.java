package java.io;

public class OptionalDataException extends ObjectStreamException {
  private static final long serialVersionUID = -8011121865681257820L;
  
  public int length;
  
  public boolean eof;
  
  OptionalDataException(int paramInt) {
    this.eof = false;
    this.length = paramInt;
  }
  
  OptionalDataException(boolean paramBoolean) {
    this.length = 0;
    this.eof = paramBoolean;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\OptionalDataException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */