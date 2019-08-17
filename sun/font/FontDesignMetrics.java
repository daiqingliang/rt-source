package sun.font;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GraphicsEnvironment;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

public final class FontDesignMetrics extends FontMetrics {
  static final long serialVersionUID = 4480069578560887773L;
  
  private static final float UNKNOWN_WIDTH = -1.0F;
  
  private static final int CURRENT_VERSION = 1;
  
  private static float roundingUpValue = 0.95F;
  
  private Font font;
  
  private float ascent;
  
  private float descent;
  
  private float leading;
  
  private float maxAdvance;
  
  private double[] matrix;
  
  private int[] cache;
  
  private int serVersion = 0;
  
  private boolean isAntiAliased;
  
  private boolean usesFractionalMetrics;
  
  private AffineTransform frcTx;
  
  private float[] advCache;
  
  private int height = -1;
  
  private FontRenderContext frc;
  
  private double[] devmatrix = null;
  
  private FontStrike fontStrike;
  
  private static FontRenderContext DEFAULT_FRC = null;
  
  private static final ConcurrentHashMap<Object, KeyReference> metricsCache = new ConcurrentHashMap();
  
  private static final int MAXRECENT = 5;
  
  private static final FontDesignMetrics[] recentMetrics = new FontDesignMetrics[5];
  
  private static int recentIndex = 0;
  
  private static FontRenderContext getDefaultFrc() {
    if (DEFAULT_FRC == null) {
      AffineTransform affineTransform;
      if (GraphicsEnvironment.isHeadless()) {
        affineTransform = new AffineTransform();
      } else {
        affineTransform = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getDefaultTransform();
      } 
      DEFAULT_FRC = new FontRenderContext(affineTransform, false, false);
    } 
    return DEFAULT_FRC;
  }
  
  public static FontDesignMetrics getMetrics(Font paramFont) { return getMetrics(paramFont, getDefaultFrc()); }
  
  public static FontDesignMetrics getMetrics(Font paramFont, FontRenderContext paramFontRenderContext) {
    KeyReference keyReference;
    SunFontManager sunFontManager = SunFontManager.getInstance();
    if (sunFontManager.maybeUsingAlternateCompositeFonts() && FontUtilities.getFont2D(paramFont) instanceof CompositeFont)
      return new FontDesignMetrics(paramFont, paramFontRenderContext); 
    FontDesignMetrics fontDesignMetrics = null;
    boolean bool = paramFontRenderContext.equals(getDefaultFrc());
    if (bool) {
      keyReference = (KeyReference)metricsCache.get(paramFont);
    } else {
      synchronized (MetricsKey.class) {
        MetricsKey.key.init(paramFont, paramFontRenderContext);
        keyReference = (KeyReference)metricsCache.get(MetricsKey.key);
      } 
    } 
    if (keyReference != null)
      fontDesignMetrics = (FontDesignMetrics)keyReference.get(); 
    if (fontDesignMetrics == null) {
      fontDesignMetrics = new FontDesignMetrics(paramFont, paramFontRenderContext);
      if (bool) {
        metricsCache.put(paramFont, new KeyReference(paramFont, fontDesignMetrics));
      } else {
        MetricsKey metricsKey = new MetricsKey(paramFont, paramFontRenderContext);
        metricsCache.put(metricsKey, new KeyReference(metricsKey, fontDesignMetrics));
      } 
    } 
    for (byte b = 0; b < recentMetrics.length; b++) {
      if (recentMetrics[b] == fontDesignMetrics)
        return fontDesignMetrics; 
    } 
    synchronized (recentMetrics) {
      recentMetrics[recentIndex++] = fontDesignMetrics;
      if (recentIndex == 5)
        recentIndex = 0; 
    } 
    return fontDesignMetrics;
  }
  
  private FontDesignMetrics(Font paramFont) { this(paramFont, getDefaultFrc()); }
  
  private FontDesignMetrics(Font paramFont, FontRenderContext paramFontRenderContext) {
    super(paramFont);
    this.font = paramFont;
    this.frc = paramFontRenderContext;
    this.isAntiAliased = paramFontRenderContext.isAntiAliased();
    this.usesFractionalMetrics = paramFontRenderContext.usesFractionalMetrics();
    this.frcTx = paramFontRenderContext.getTransform();
    this.matrix = new double[4];
    initMatrixAndMetrics();
    initAdvCache();
  }
  
  private void initMatrixAndMetrics() {
    Font2D font2D = FontUtilities.getFont2D(this.font);
    this.fontStrike = font2D.getStrike(this.font, this.frc);
    StrikeMetrics strikeMetrics = this.fontStrike.getFontMetrics();
    this.ascent = strikeMetrics.getAscent();
    this.descent = strikeMetrics.getDescent();
    this.leading = strikeMetrics.getLeading();
    this.maxAdvance = strikeMetrics.getMaxAdvance();
    this.devmatrix = new double[4];
    this.frcTx.getMatrix(this.devmatrix);
  }
  
  private void initAdvCache() {
    this.advCache = new float[256];
    for (byte b = 0; b < 'Ā'; b++)
      this.advCache[b] = -1.0F; 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    if (this.serVersion != 1) {
      this.frc = getDefaultFrc();
      this.isAntiAliased = this.frc.isAntiAliased();
      this.usesFractionalMetrics = this.frc.usesFractionalMetrics();
      this.frcTx = this.frc.getTransform();
    } else {
      this.frc = new FontRenderContext(this.frcTx, this.isAntiAliased, this.usesFractionalMetrics);
    } 
    this.height = -1;
    this.cache = null;
    initMatrixAndMetrics();
    initAdvCache();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    this.cache = new int[256];
    for (byte b = 0; b < 'Ā'; b++)
      this.cache[b] = -1; 
    this.serVersion = 1;
    paramObjectOutputStream.defaultWriteObject();
    this.cache = null;
  }
  
  private float handleCharWidth(int paramInt) { return this.fontStrike.getCodePointAdvance(paramInt); }
  
  private float getLatinCharWidth(char paramChar) {
    float f = this.advCache[paramChar];
    if (f == -1.0F) {
      f = handleCharWidth(paramChar);
      this.advCache[paramChar] = f;
    } 
    return f;
  }
  
  public FontRenderContext getFontRenderContext() { return this.frc; }
  
  public int charWidth(char paramChar) {
    float f;
    if (paramChar < 'Ā') {
      f = getLatinCharWidth(paramChar);
    } else {
      f = handleCharWidth(paramChar);
    } 
    return (int)(0.5D + f);
  }
  
  public int charWidth(int paramInt) {
    if (!Character.isValidCodePoint(paramInt))
      paramInt = 65535; 
    float f = handleCharWidth(paramInt);
    return (int)(0.5D + f);
  }
  
  public int stringWidth(String paramString) {
    float f = 0.0F;
    if (this.font.hasLayoutAttributes()) {
      if (paramString == null)
        throw new NullPointerException("str is null"); 
      if (paramString.length() == 0)
        return 0; 
      f = (new TextLayout(paramString, this.font, this.frc)).getAdvance();
    } else {
      int i = paramString.length();
      for (byte b = 0; b < i; b++) {
        char c = paramString.charAt(b);
        if (c < 'Ā') {
          f += getLatinCharWidth(c);
        } else {
          if (FontUtilities.isNonSimpleChar(c)) {
            f = (new TextLayout(paramString, this.font, this.frc)).getAdvance();
            break;
          } 
          f += handleCharWidth(c);
        } 
      } 
    } 
    return (int)(0.5D + f);
  }
  
  public int charsWidth(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    float f = 0.0F;
    if (this.font.hasLayoutAttributes()) {
      if (paramInt2 == 0)
        return 0; 
      String str = new String(paramArrayOfChar, paramInt1, paramInt2);
      f = (new TextLayout(str, this.font, this.frc)).getAdvance();
    } else {
      if (paramInt2 < 0)
        throw new IndexOutOfBoundsException("len=" + paramInt2); 
      int i = paramInt1 + paramInt2;
      for (int j = paramInt1; j < i; j++) {
        char c = paramArrayOfChar[j];
        if (c < 'Ā') {
          f += getLatinCharWidth(c);
        } else {
          if (FontUtilities.isNonSimpleChar(c)) {
            String str = new String(paramArrayOfChar, paramInt1, paramInt2);
            f = (new TextLayout(str, this.font, this.frc)).getAdvance();
            break;
          } 
          f += handleCharWidth(c);
        } 
      } 
    } 
    return (int)(0.5D + f);
  }
  
  public int[] getWidths() {
    int[] arrayOfInt = new int[256];
    for (char c = Character.MIN_VALUE; c < 'Ā'; c = (char)(c + 1)) {
      float f = this.advCache[c];
      if (f == -1.0F)
        f = this.advCache[c] = handleCharWidth(c); 
      arrayOfInt[c] = (int)(0.5D + f);
    } 
    return arrayOfInt;
  }
  
  public int getMaxAdvance() { return (int)(0.99F + this.maxAdvance); }
  
  public int getAscent() { return (int)(roundingUpValue + this.ascent); }
  
  public int getDescent() { return (int)(roundingUpValue + this.descent); }
  
  public int getLeading() { return (int)(roundingUpValue + this.descent + this.leading) - (int)(roundingUpValue + this.descent); }
  
  public int getHeight() {
    if (this.height < 0)
      this.height = getAscent() + (int)(roundingUpValue + this.descent + this.leading); 
    return this.height;
  }
  
  private static class KeyReference extends SoftReference implements DisposerRecord, Disposer.PollDisposable {
    static ReferenceQueue queue = Disposer.getQueue();
    
    Object key;
    
    KeyReference(Object param1Object1, Object param1Object2) {
      super(param1Object2, queue);
      this.key = param1Object1;
      Disposer.addReference(this, this);
    }
    
    public void dispose() {
      if (metricsCache.get(this.key) == this)
        metricsCache.remove(this.key); 
    }
  }
  
  private static class MetricsKey {
    Font font;
    
    FontRenderContext frc;
    
    int hash;
    
    static final MetricsKey key = new MetricsKey();
    
    MetricsKey() {}
    
    MetricsKey(Font param1Font, FontRenderContext param1FontRenderContext) { init(param1Font, param1FontRenderContext); }
    
    void init(Font param1Font, FontRenderContext param1FontRenderContext) {
      this.font = param1Font;
      this.frc = param1FontRenderContext;
      this.hash = param1Font.hashCode() + param1FontRenderContext.hashCode();
    }
    
    public boolean equals(Object param1Object) { return !(param1Object instanceof MetricsKey) ? false : ((this.font.equals(((MetricsKey)param1Object).font) && this.frc.equals(((MetricsKey)param1Object).frc))); }
    
    public int hashCode() { return this.hash; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\FontDesignMetrics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */