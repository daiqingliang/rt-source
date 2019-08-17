package java.nio.charset;

public class UnmappableCharacterException extends CharacterCodingException {
  private static final long serialVersionUID = -7026962371537706123L;
  
  private int inputLength;
  
  public UnmappableCharacterException(int paramInt) { this.inputLength = paramInt; }
  
  public int getInputLength() { return this.inputLength; }
  
  public String getMessage() { return "Input length = " + this.inputLength; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\charset\UnmappableCharacterException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */