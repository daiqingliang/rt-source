package sun.awt;

public class CharsetString {
  public char[] charsetChars;
  
  public int offset;
  
  public int length;
  
  public FontDescriptor fontDescriptor;
  
  public CharsetString(char[] paramArrayOfChar, int paramInt1, int paramInt2, FontDescriptor paramFontDescriptor) {
    this.charsetChars = paramArrayOfChar;
    this.offset = paramInt1;
    this.length = paramInt2;
    this.fontDescriptor = paramFontDescriptor;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\CharsetString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */