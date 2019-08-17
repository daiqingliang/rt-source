package sun.awt.windows;

import java.awt.Font;
import java.awt.FontMetrics;
import java.util.Hashtable;

final class WFontMetrics extends FontMetrics {
  int[] widths;
  
  int ascent;
  
  int descent;
  
  int leading;
  
  int height;
  
  int maxAscent;
  
  int maxDescent;
  
  int maxHeight;
  
  int maxAdvance;
  
  static Hashtable table;
  
  public WFontMetrics(Font paramFont) {
    super(paramFont);
    init();
  }
  
  public int getLeading() { return this.leading; }
  
  public int getAscent() { return this.ascent; }
  
  public int getDescent() { return this.descent; }
  
  public int getHeight() { return this.height; }
  
  public int getMaxAscent() { return this.maxAscent; }
  
  public int getMaxDescent() { return this.maxDescent; }
  
  public int getMaxAdvance() { return this.maxAdvance; }
  
  public native int stringWidth(String paramString);
  
  public native int charsWidth(char[] paramArrayOfChar, int paramInt1, int paramInt2);
  
  public native int bytesWidth(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  public int[] getWidths() { return this.widths; }
  
  native void init();
  
  static FontMetrics getFontMetrics(Font paramFont) {
    FontMetrics fontMetrics = (FontMetrics)table.get(paramFont);
    if (fontMetrics == null)
      table.put(paramFont, fontMetrics = new WFontMetrics(paramFont)); 
    return fontMetrics;
  }
  
  private static native void initIDs();
  
  static  {
    initIDs();
    table = new Hashtable();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WFontMetrics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */