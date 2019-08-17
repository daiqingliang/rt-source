package sun.awt;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import sun.nio.cs.HistoricallyNamedCharset;
import sun.security.action.GetPropertyAction;

public class FontDescriptor implements Cloneable {
  String nativeName;
  
  public CharsetEncoder encoder;
  
  String charsetName;
  
  private int[] exclusionRanges;
  
  public CharsetEncoder unicodeEncoder;
  
  boolean useUnicode;
  
  static boolean isLE;
  
  public FontDescriptor(String paramString, CharsetEncoder paramCharsetEncoder, int[] paramArrayOfInt) {
    this.nativeName = paramString;
    this.encoder = paramCharsetEncoder;
    this.exclusionRanges = paramArrayOfInt;
    this.useUnicode = false;
    Charset charset = paramCharsetEncoder.charset();
    if (charset instanceof HistoricallyNamedCharset) {
      this.charsetName = ((HistoricallyNamedCharset)charset).historicalName();
    } else {
      this.charsetName = charset.name();
    } 
  }
  
  public String getNativeName() { return this.nativeName; }
  
  public CharsetEncoder getFontCharsetEncoder() { return this.encoder; }
  
  public String getFontCharsetName() { return this.charsetName; }
  
  public int[] getExclusionRanges() { return this.exclusionRanges; }
  
  public boolean isExcluded(char paramChar) {
    byte b = 0;
    while (b < this.exclusionRanges.length) {
      int i = this.exclusionRanges[b++];
      int j = this.exclusionRanges[b++];
      if (paramChar >= i && paramChar <= j)
        return true; 
    } 
    return false;
  }
  
  public String toString() { return super.toString() + " [" + this.nativeName + "|" + this.encoder + "]"; }
  
  private static native void initIDs();
  
  public boolean useUnicode() {
    if (this.useUnicode && this.unicodeEncoder == null)
      try {
        this.unicodeEncoder = isLE ? StandardCharsets.UTF_16LE.newEncoder() : StandardCharsets.UTF_16BE.newEncoder();
      } catch (IllegalArgumentException illegalArgumentException) {} 
    return this.useUnicode;
  }
  
  static  {
    NativeLibLoader.loadLibraries();
    initIDs();
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.io.unicode.encoding", "UnicodeBig"));
    isLE = !"UnicodeBig".equals(str);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\FontDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */