package sun.awt.windows;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import sun.awt.AWTCharset;

final class WDefaultFontCharset extends AWTCharset {
  private String fontName;
  
  WDefaultFontCharset(String paramString) {
    super("WDefaultFontCharset", Charset.forName("windows-1252"));
    this.fontName = paramString;
  }
  
  public CharsetEncoder newEncoder() { return new Encoder(null); }
  
  private native boolean canConvert(char paramChar);
  
  private static native void initIDs();
  
  static  {
    initIDs();
  }
  
  private class Encoder extends AWTCharset.Encoder {
    private Encoder() { super(WDefaultFontCharset.this); }
    
    public boolean canEncode(char param1Char) { return WDefaultFontCharset.this.canConvert(param1Char); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WDefaultFontCharset.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */