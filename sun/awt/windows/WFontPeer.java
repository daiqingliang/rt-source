package sun.awt.windows;

import sun.awt.PlatformFont;

final class WFontPeer extends PlatformFont {
  private String textComponentFontName;
  
  public WFontPeer(String paramString, int paramInt) {
    super(paramString, paramInt);
    if (this.fontConfig != null)
      this.textComponentFontName = ((WFontConfiguration)this.fontConfig).getTextComponentFontName(this.familyName, paramInt); 
  }
  
  protected char getMissingGlyphCharacter() { return '❑'; }
  
  private static native void initIDs();
  
  static  {
    initIDs();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WFontPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */