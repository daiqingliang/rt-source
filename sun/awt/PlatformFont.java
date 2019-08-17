package sun.awt;

import java.awt.peer.FontPeer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Locale;
import java.util.Vector;
import sun.font.SunFontManager;

public abstract class PlatformFont implements FontPeer {
  protected FontDescriptor[] componentFonts;
  
  protected char defaultChar;
  
  protected FontConfiguration fontConfig;
  
  protected FontDescriptor defaultFont;
  
  protected String familyName;
  
  private Object[] fontCache;
  
  protected static int FONTCACHESIZE;
  
  protected static int FONTCACHEMASK;
  
  protected static String osVersion;
  
  public PlatformFont(String paramString, int paramInt) {
    SunFontManager sunFontManager = SunFontManager.getInstance();
    if (sunFontManager instanceof sun.java2d.FontSupport)
      this.fontConfig = sunFontManager.getFontConfiguration(); 
    if (this.fontConfig == null)
      return; 
    this.familyName = paramString.toLowerCase(Locale.ENGLISH);
    if (!FontConfiguration.isLogicalFontFamilyName(this.familyName))
      this.familyName = this.fontConfig.getFallbackFamilyName(this.familyName, "sansserif"); 
    this.componentFonts = this.fontConfig.getFontDescriptors(this.familyName, paramInt);
    char c = getMissingGlyphCharacter();
    this.defaultChar = '?';
    if (this.componentFonts.length > 0)
      this.defaultFont = this.componentFonts[0]; 
    for (byte b = 0; b < this.componentFonts.length; b++) {
      if (!this.componentFonts[b].isExcluded(c) && (this.componentFonts[b]).encoder.canEncode(c)) {
        this.defaultFont = this.componentFonts[b];
        this.defaultChar = c;
        break;
      } 
    } 
  }
  
  protected abstract char getMissingGlyphCharacter();
  
  public CharsetString[] makeMultiCharsetString(String paramString) { return makeMultiCharsetString(paramString.toCharArray(), 0, paramString.length(), true); }
  
  public CharsetString[] makeMultiCharsetString(String paramString, boolean paramBoolean) { return makeMultiCharsetString(paramString.toCharArray(), 0, paramString.length(), paramBoolean); }
  
  public CharsetString[] makeMultiCharsetString(char[] paramArrayOfChar, int paramInt1, int paramInt2) { return makeMultiCharsetString(paramArrayOfChar, paramInt1, paramInt2, true); }
  
  public CharsetString[] makeMultiCharsetString(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean) {
    CharsetString[] arrayOfCharsetString;
    if (paramInt2 < 1)
      return new CharsetString[0]; 
    Vector vector = null;
    char[] arrayOfChar = new char[paramInt2];
    char c = this.defaultChar;
    boolean bool = false;
    FontDescriptor fontDescriptor = this.defaultFont;
    int i;
    for (i = 0; i < this.componentFonts.length; i++) {
      if (!this.componentFonts[i].isExcluded(paramArrayOfChar[paramInt1]) && (this.componentFonts[i]).encoder.canEncode(paramArrayOfChar[paramInt1])) {
        fontDescriptor = this.componentFonts[i];
        c = paramArrayOfChar[paramInt1];
        bool = true;
        break;
      } 
    } 
    if (!paramBoolean && !bool)
      return null; 
    arrayOfChar[0] = c;
    i = 0;
    for (int j = 1; j < paramInt2; j++) {
      char c1 = paramArrayOfChar[paramInt1 + j];
      FontDescriptor fontDescriptor1 = this.defaultFont;
      c = this.defaultChar;
      bool = false;
      for (byte b = 0; b < this.componentFonts.length; b++) {
        if (!this.componentFonts[b].isExcluded(c1) && (this.componentFonts[b]).encoder.canEncode(c1)) {
          fontDescriptor1 = this.componentFonts[b];
          c = c1;
          bool = true;
          break;
        } 
      } 
      if (!paramBoolean && !bool)
        return null; 
      arrayOfChar[j] = c;
      if (fontDescriptor != fontDescriptor1) {
        if (vector == null)
          vector = new Vector(3); 
        vector.addElement(new CharsetString(arrayOfChar, i, j - i, fontDescriptor));
        fontDescriptor = fontDescriptor1;
        fontDescriptor1 = this.defaultFont;
        i = j;
      } 
    } 
    CharsetString charsetString = new CharsetString(arrayOfChar, i, paramInt2 - i, fontDescriptor);
    if (vector == null) {
      arrayOfCharsetString = new CharsetString[1];
      arrayOfCharsetString[0] = charsetString;
    } else {
      vector.addElement(charsetString);
      arrayOfCharsetString = new CharsetString[vector.size()];
      for (byte b = 0; b < vector.size(); b++)
        arrayOfCharsetString[b] = (CharsetString)vector.elementAt(b); 
    } 
    return arrayOfCharsetString;
  }
  
  public boolean mightHaveMultiFontMetrics() { return (this.fontConfig != null); }
  
  public Object[] makeConvertedMultiFontString(String paramString) { return makeConvertedMultiFontChars(paramString.toCharArray(), 0, paramString.length()); }
  
  public Object[] makeConvertedMultiFontChars(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    Object[] arrayOfObject = new Object[2];
    byte[] arrayOfByte = null;
    int i = paramInt1;
    byte b1 = 0;
    byte b2 = 0;
    FontDescriptor fontDescriptor1 = null;
    FontDescriptor fontDescriptor2 = null;
    int j = paramInt1 + paramInt2;
    if (paramInt1 < 0 || j > paramArrayOfChar.length)
      throw new ArrayIndexOutOfBoundsException(); 
    if (i >= j)
      return null; 
    while (i < j) {
      char c2 = paramArrayOfChar[i];
      this;
      char c1 = c2 & FONTCACHEMASK;
      PlatformFontCache platformFontCache = (PlatformFontCache)getFontCache()[c1];
      if (platformFontCache == null || platformFontCache.uniChar != c2) {
        fontDescriptor1 = this.defaultFont;
        c2 = this.defaultChar;
        char c = paramArrayOfChar[i];
        int m = this.componentFonts.length;
        for (b = 0; b < m; b++) {
          FontDescriptor fontDescriptor = this.componentFonts[b];
          fontDescriptor.encoder.reset();
          if (!fontDescriptor.isExcluded(c) && fontDescriptor.encoder.canEncode(c)) {
            fontDescriptor1 = fontDescriptor;
            c2 = c;
            break;
          } 
        } 
        try {
          char[] arrayOfChar = new char[1];
          arrayOfChar[0] = c2;
          platformFontCache = new PlatformFontCache();
          if (fontDescriptor1.useUnicode()) {
            fontDescriptor1;
            if (FontDescriptor.isLE) {
              platformFontCache.bb.put((byte)(arrayOfChar[0] & 0xFF));
              platformFontCache.bb.put((byte)(arrayOfChar[0] >> '\b'));
            } else {
              platformFontCache.bb.put((byte)(arrayOfChar[0] >> '\b'));
              platformFontCache.bb.put((byte)(arrayOfChar[0] & 0xFF));
            } 
          } else {
            fontDescriptor1.encoder.encode(CharBuffer.wrap(arrayOfChar), platformFontCache.bb, true);
          } 
          platformFontCache.fontDescriptor = fontDescriptor1;
          platformFontCache.uniChar = paramArrayOfChar[i];
          getFontCache()[c1] = platformFontCache;
        } catch (Exception b) {
          Exception exception;
          System.err.println(exception);
          exception.printStackTrace();
          return null;
        } 
      } 
      if (fontDescriptor2 != platformFontCache.fontDescriptor) {
        if (fontDescriptor2 != null) {
          arrayOfObject[b2++] = fontDescriptor2;
          arrayOfObject[b2++] = arrayOfByte;
          if (arrayOfByte != null) {
            b1 -= true;
            arrayOfByte[0] = (byte)(b1 >> 24);
            arrayOfByte[1] = (byte)(b1 >> 16);
            arrayOfByte[2] = (byte)(b1 >> 8);
            arrayOfByte[3] = (byte)b1;
          } 
          if (b2 >= arrayOfObject.length) {
            Object[] arrayOfObject1 = new Object[arrayOfObject.length * 2];
            System.arraycopy(arrayOfObject, 0, arrayOfObject1, 0, arrayOfObject.length);
            arrayOfObject = arrayOfObject1;
          } 
        } 
        if (platformFontCache.fontDescriptor.useUnicode()) {
          arrayOfByte = new byte[(j - i + 1) * (int)platformFontCache.fontDescriptor.unicodeEncoder.maxBytesPerChar() + 4];
        } else {
          arrayOfByte = new byte[(j - i + 1) * (int)platformFontCache.fontDescriptor.encoder.maxBytesPerChar() + 4];
        } 
        b1 = 4;
        fontDescriptor2 = platformFontCache.fontDescriptor;
      } 
      byte[] arrayOfByte1 = platformFontCache.bb.array();
      int k = platformFontCache.bb.position();
      if (k == 1) {
        arrayOfByte[b1++] = arrayOfByte1[0];
      } else if (k == 2) {
        arrayOfByte[b1++] = arrayOfByte1[0];
        arrayOfByte[b1++] = arrayOfByte1[1];
      } else if (k == 3) {
        arrayOfByte[b1++] = arrayOfByte1[0];
        arrayOfByte[b1++] = arrayOfByte1[1];
        arrayOfByte[b1++] = arrayOfByte1[2];
      } else if (k == 4) {
        arrayOfByte[b1++] = arrayOfByte1[0];
        arrayOfByte[b1++] = arrayOfByte1[1];
        arrayOfByte[b1++] = arrayOfByte1[2];
        arrayOfByte[b1++] = arrayOfByte1[3];
      } 
      i++;
    } 
    arrayOfObject[b2++] = fontDescriptor2;
    arrayOfObject[b2] = arrayOfByte;
    if (arrayOfByte != null) {
      b1 -= 4;
      arrayOfByte[0] = (byte)(b1 >> 24);
      arrayOfByte[1] = (byte)(b1 >> 16);
      arrayOfByte[2] = (byte)(b1 >> 8);
      arrayOfByte[3] = (byte)b1;
    } 
    return arrayOfObject;
  }
  
  protected final Object[] getFontCache() {
    if (this.fontCache == null) {
      this;
      this.fontCache = new Object[FONTCACHESIZE];
    } 
    return this.fontCache;
  }
  
  private static native void initIDs();
  
  static  {
    NativeLibLoader.loadLibraries();
    initIDs();
    FONTCACHESIZE = 256;
    FONTCACHEMASK = FONTCACHESIZE - 1;
  }
  
  class PlatformFontCache {
    char uniChar;
    
    FontDescriptor fontDescriptor;
    
    ByteBuffer bb = ByteBuffer.allocate(4);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\PlatformFont.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */