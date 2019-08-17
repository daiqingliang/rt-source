package sun.font;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

public class FileFontStrike extends PhysicalStrike {
  static final int INVISIBLE_GLYPHS = 65534;
  
  private FileFont fileFont;
  
  private static final int UNINITIALISED = 0;
  
  private static final int INTARRAY = 1;
  
  private static final int LONGARRAY = 2;
  
  private static final int SEGINTARRAY = 3;
  
  private static final int SEGLONGARRAY = 4;
  
  private static final int SEGSHIFT = 5;
  
  private static final int SEGSIZE = 32;
  
  private boolean segmentedCache;
  
  private int[][] segIntGlyphImages;
  
  private long[][] segLongGlyphImages;
  
  private float[] horizontalAdvances;
  
  private float[][] segHorizontalAdvances;
  
  ConcurrentHashMap<Integer, Rectangle2D.Float> boundsMap;
  
  SoftReference<ConcurrentHashMap<Integer, Point2D.Float>> glyphMetricsMapRef;
  
  AffineTransform invertDevTx;
  
  boolean useNatives;
  
  NativeStrike[] nativeStrikes;
  
  private int intPtSize;
  
  private static boolean isXPorLater = false;
  
  private WeakReference<ConcurrentHashMap<Integer, GeneralPath>> outlineMapRef;
  
  private static native boolean initNative();
  
  FileFontStrike(FileFont paramFileFont, FontStrikeDesc paramFontStrikeDesc) {
    super(paramFileFont, paramFontStrikeDesc);
    this.fileFont = paramFileFont;
    if (paramFontStrikeDesc.style != paramFileFont.style) {
      if ((paramFontStrikeDesc.style & 0x2) == 2 && (paramFileFont.style & 0x2) == 0) {
        this.algoStyle = true;
        this.italic = 0.7F;
      } 
      if ((paramFontStrikeDesc.style & true) == 1 && (paramFileFont.style & true) == 0) {
        this.algoStyle = true;
        this.boldness = 1.33F;
      } 
    } 
    double[] arrayOfDouble = new double[4];
    AffineTransform affineTransform = paramFontStrikeDesc.glyphTx;
    affineTransform.getMatrix(arrayOfDouble);
    if (!paramFontStrikeDesc.devTx.isIdentity() && paramFontStrikeDesc.devTx.getType() != 1)
      try {
        this.invertDevTx = paramFontStrikeDesc.devTx.createInverse();
      } catch (NoninvertibleTransformException noninvertibleTransformException) {} 
    boolean bool = (paramFontStrikeDesc.aaHint != 1 && paramFileFont.familyName.startsWith("Amble"));
    if (Double.isNaN(arrayOfDouble[0]) || Double.isNaN(arrayOfDouble[1]) || Double.isNaN(arrayOfDouble[2]) || Double.isNaN(arrayOfDouble[3]) || paramFileFont.getScaler() == null) {
      this.pScalerContext = NullFontScaler.getNullScalerContext();
    } else {
      this.pScalerContext = paramFileFont.getScaler().createScalerContext(arrayOfDouble, paramFontStrikeDesc.aaHint, paramFontStrikeDesc.fmHint, this.boldness, this.italic, bool);
    } 
    this.mapper = paramFileFont.getMapper();
    int i = this.mapper.getNumGlyphs();
    float f = (float)arrayOfDouble[3];
    int j = this.intPtSize = (int)f;
    boolean bool1 = ((affineTransform.getType() & 0x7C) == 0) ? 1 : 0;
    this.segmentedCache = (i > 256 || (i > 64 && (!bool1 || f != j || j < 6 || j > 36)));
    if (this.pScalerContext == 0L) {
      this.disposer = new FontStrikeDisposer(paramFileFont, paramFontStrikeDesc);
      initGlyphCache();
      this.pScalerContext = NullFontScaler.getNullScalerContext();
      SunFontManager.getInstance().deRegisterBadFont(paramFileFont);
      return;
    } 
    if (FontUtilities.isWindows && isXPorLater && !FontUtilities.useT2K && !GraphicsEnvironment.isHeadless() && !paramFileFont.useJavaRasterizer && (paramFontStrikeDesc.aaHint == 4 || paramFontStrikeDesc.aaHint == 5) && arrayOfDouble[1] == 0.0D && arrayOfDouble[2] == 0.0D && arrayOfDouble[0] == arrayOfDouble[3] && arrayOfDouble[0] >= 3.0D && arrayOfDouble[0] <= 100.0D && !((TrueTypeFont)paramFileFont).useEmbeddedBitmapsForSize(this.intPtSize)) {
      this.useNatives = true;
    } else if (paramFileFont.checkUseNatives() && paramFontStrikeDesc.aaHint == 0 && !this.algoStyle && arrayOfDouble[1] == 0.0D && arrayOfDouble[2] == 0.0D && arrayOfDouble[0] >= 6.0D && arrayOfDouble[0] <= 36.0D && arrayOfDouble[0] == arrayOfDouble[3]) {
      this.useNatives = true;
      int k = paramFileFont.nativeFonts.length;
      this.nativeStrikes = new NativeStrike[k];
      for (byte b = 0; b < k; b++)
        this.nativeStrikes[b] = new NativeStrike(paramFileFont.nativeFonts[b], paramFontStrikeDesc, false); 
    } 
    if (FontUtilities.isLogging() && FontUtilities.isWindows)
      FontUtilities.getLogger().info("Strike for " + paramFileFont + " at size = " + this.intPtSize + " use natives = " + this.useNatives + " useJavaRasteriser = " + paramFileFont.useJavaRasterizer + " AAHint = " + paramFontStrikeDesc.aaHint + " Has Embedded bitmaps = " + ((TrueTypeFont)paramFileFont).useEmbeddedBitmapsForSize(this.intPtSize)); 
    this.disposer = new FontStrikeDisposer(paramFileFont, paramFontStrikeDesc, this.pScalerContext);
    double d = 48.0D;
    this.getImageWithAdvance = (Math.abs(affineTransform.getScaleX()) <= d && Math.abs(affineTransform.getScaleY()) <= d && Math.abs(affineTransform.getShearX()) <= d && Math.abs(affineTransform.getShearY()) <= d);
    if (!this.getImageWithAdvance)
      if (!this.segmentedCache) {
        this.horizontalAdvances = new float[i];
        for (byte b = 0; b < i; b++)
          this.horizontalAdvances[b] = Float.MAX_VALUE; 
      } else {
        int k = (i + 32 - 1) / 32;
        this.segHorizontalAdvances = new float[k][];
      }  
  }
  
  public int getNumGlyphs() { return this.fileFont.getNumGlyphs(); }
  
  long getGlyphImageFromNative(int paramInt) { return FontUtilities.isWindows ? getGlyphImageFromWindows(paramInt) : getGlyphImageFromX11(paramInt); }
  
  private native long _getGlyphImageFromWindows(String paramString, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean);
  
  long getGlyphImageFromWindows(int paramInt) {
    String str = this.fileFont.getFamilyName(null);
    int i = this.desc.style & true | this.desc.style & 0x2 | this.fileFont.getStyle();
    int j = this.intPtSize;
    long l = _getGlyphImageFromWindows(str, i, j, paramInt, (this.desc.fmHint == 2));
    if (l != 0L) {
      float f = getGlyphAdvance(paramInt, false);
      StrikeCache.unsafe.putFloat(l + StrikeCache.xAdvanceOffset, f);
      return l;
    } 
    return this.fileFont.getGlyphImage(this.pScalerContext, paramInt);
  }
  
  long getGlyphImageFromX11(int paramInt) {
    char c = this.fileFont.glyphToCharMap[paramInt];
    for (byte b = 0; b < this.nativeStrikes.length; b++) {
      CharToGlyphMapper charToGlyphMapper = this.fileFont.nativeFonts[b].getMapper();
      int i = charToGlyphMapper.charToGlyph(c) & 0xFFFF;
      if (i != charToGlyphMapper.getMissingGlyphCode()) {
        long l = this.nativeStrikes[b].getGlyphImagePtrNoCache(i);
        if (l != 0L)
          return l; 
      } 
    } 
    return this.fileFont.getGlyphImage(this.pScalerContext, paramInt);
  }
  
  long getGlyphImagePtr(int paramInt) {
    if (paramInt >= 65534)
      return StrikeCache.invisibleGlyphPtr; 
    long l = 0L;
    if ((l = getCachedGlyphPtr(paramInt)) != 0L)
      return l; 
    if (this.useNatives) {
      l = getGlyphImageFromNative(paramInt);
      if (l == 0L && FontUtilities.isLogging())
        FontUtilities.getLogger().info("Strike for " + this.fileFont + " at size = " + this.intPtSize + " couldn't get native glyph for code = " + paramInt); 
    } 
    if (l == 0L)
      l = this.fileFont.getGlyphImage(this.pScalerContext, paramInt); 
    return setCachedGlyphPtr(paramInt, l);
  }
  
  void getGlyphImagePtrs(int[] paramArrayOfInt, long[] paramArrayOfLong, int paramInt) {
    for (byte b = 0; b < paramInt; b++) {
      int i = paramArrayOfInt[b];
      if (i >= 65534) {
        paramArrayOfLong[b] = StrikeCache.invisibleGlyphPtr;
      } else {
        paramArrayOfLong[b] = getCachedGlyphPtr(i);
        if (getCachedGlyphPtr(i) == 0L) {
          long l = 0L;
          if (this.useNatives)
            l = getGlyphImageFromNative(i); 
          if (l == 0L)
            l = this.fileFont.getGlyphImage(this.pScalerContext, i); 
          paramArrayOfLong[b] = setCachedGlyphPtr(i, l);
        } 
      } 
    } 
  }
  
  int getSlot0GlyphImagePtrs(int[] paramArrayOfInt, long[] paramArrayOfLong, int paramInt) {
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInt; b2++) {
      int i = paramArrayOfInt[b2];
      if (i >>> 24 != 0)
        return b1; 
      b1++;
      if (i >= 65534) {
        paramArrayOfLong[b2] = StrikeCache.invisibleGlyphPtr;
      } else {
        paramArrayOfLong[b2] = getCachedGlyphPtr(i);
        if (getCachedGlyphPtr(i) == 0L) {
          long l = 0L;
          if (this.useNatives)
            l = getGlyphImageFromNative(i); 
          if (l == 0L)
            l = this.fileFont.getGlyphImage(this.pScalerContext, i); 
          paramArrayOfLong[b2] = setCachedGlyphPtr(i, l);
        } 
      } 
    } 
    return b1;
  }
  
  long getCachedGlyphPtr(int paramInt) {
    try {
      return getCachedGlyphPtrInternal(paramInt);
    } catch (Exception exception) {
      NullFontScaler nullFontScaler;
      long l = (nullFontScaler = (NullFontScaler)FontScaler.getNullScaler()).getNullScalerContext();
      return nullFontScaler.getGlyphImage(l, paramInt);
    } 
  }
  
  private long getCachedGlyphPtrInternal(int paramInt) {
    int i;
    switch (this.glyphCacheFormat) {
      case 1:
        return this.intGlyphImages[paramInt] & 0xFFFFFFFFL;
      case 3:
        i = paramInt >> 5;
        if (this.segIntGlyphImages[i] != null) {
          int j = paramInt % 32;
          return this.segIntGlyphImages[i][j] & 0xFFFFFFFFL;
        } 
        return 0L;
      case 2:
        return this.longGlyphImages[paramInt];
      case 4:
        i = paramInt >> 5;
        if (this.segLongGlyphImages[i] != null) {
          int j = paramInt % 32;
          return this.segLongGlyphImages[i][j];
        } 
        return 0L;
    } 
    return 0L;
  }
  
  private long setCachedGlyphPtr(int paramInt, long paramLong) {
    try {
      return setCachedGlyphPtrInternal(paramInt, paramLong);
    } catch (Exception exception) {
      switch (this.glyphCacheFormat) {
        case 1:
        case 3:
          StrikeCache.freeIntPointer((int)paramLong);
          break;
        case 2:
        case 4:
          StrikeCache.freeLongPointer(paramLong);
          break;
      } 
      NullFontScaler nullFontScaler;
      long l = (nullFontScaler = (NullFontScaler)FontScaler.getNullScaler()).getNullScalerContext();
      return nullFontScaler.getGlyphImage(l, paramInt);
    } 
  }
  
  private long setCachedGlyphPtrInternal(int paramInt, long paramLong) {
    int j;
    int i;
    switch (this.glyphCacheFormat) {
      case 1:
        if (this.intGlyphImages[paramInt] == 0) {
          this.intGlyphImages[paramInt] = (int)paramLong;
          return paramLong;
        } 
        StrikeCache.freeIntPointer((int)paramLong);
        return this.intGlyphImages[paramInt] & 0xFFFFFFFFL;
      case 3:
        i = paramInt >> 5;
        j = paramInt % 32;
        if (this.segIntGlyphImages[i] == null)
          this.segIntGlyphImages[i] = new int[32]; 
        if (this.segIntGlyphImages[i][j] == 0) {
          this.segIntGlyphImages[i][j] = (int)paramLong;
          return paramLong;
        } 
        StrikeCache.freeIntPointer((int)paramLong);
        return this.segIntGlyphImages[i][j] & 0xFFFFFFFFL;
      case 2:
        if (this.longGlyphImages[paramInt] == 0L) {
          this.longGlyphImages[paramInt] = paramLong;
          return paramLong;
        } 
        StrikeCache.freeLongPointer(paramLong);
        return this.longGlyphImages[paramInt];
      case 4:
        i = paramInt >> 5;
        j = paramInt % 32;
        if (this.segLongGlyphImages[i] == null)
          this.segLongGlyphImages[i] = new long[32]; 
        if (this.segLongGlyphImages[i][j] == 0L) {
          this.segLongGlyphImages[i][j] = paramLong;
          return paramLong;
        } 
        StrikeCache.freeLongPointer(paramLong);
        return this.segLongGlyphImages[i][j];
    } 
    initGlyphCache();
    return setCachedGlyphPtr(paramInt, paramLong);
  }
  
  private void initGlyphCache() {
    int i = this.mapper.getNumGlyphs();
    byte b = 0;
    if (this.segmentedCache) {
      int j = (i + 32 - 1) / 32;
      if (longAddresses) {
        b = 4;
        this.segLongGlyphImages = new long[j][];
        this.disposer.segLongGlyphImages = this.segLongGlyphImages;
      } else {
        b = 3;
        this.segIntGlyphImages = new int[j][];
        this.disposer.segIntGlyphImages = this.segIntGlyphImages;
      } 
    } else if (longAddresses) {
      b = 2;
      this.longGlyphImages = new long[i];
      this.disposer.longGlyphImages = this.longGlyphImages;
    } else {
      b = 1;
      this.intGlyphImages = new int[i];
      this.disposer.intGlyphImages = this.intGlyphImages;
    } 
    this.glyphCacheFormat = b;
  }
  
  float getGlyphAdvance(int paramInt) { return getGlyphAdvance(paramInt, true); }
  
  private float getGlyphAdvance(int paramInt, boolean paramBoolean) {
    float f;
    if (paramInt >= 65534)
      return 0.0F; 
    if (this.horizontalAdvances != null) {
      f = this.horizontalAdvances[paramInt];
      if (f != Float.MAX_VALUE) {
        if (!paramBoolean && this.invertDevTx != null) {
          Point2D.Float float = new Point2D.Float(f, 0.0F);
          this.desc.devTx.deltaTransform(float, float);
          return float.x;
        } 
        return f;
      } 
    } else if (this.segmentedCache && this.segHorizontalAdvances != null) {
      int i = paramInt >> 5;
      float[] arrayOfFloat = this.segHorizontalAdvances[i];
      if (arrayOfFloat != null) {
        f = arrayOfFloat[paramInt % 32];
        if (f != Float.MAX_VALUE) {
          if (!paramBoolean && this.invertDevTx != null) {
            Point2D.Float float = new Point2D.Float(f, 0.0F);
            this.desc.devTx.deltaTransform(float, float);
            return float.x;
          } 
          return f;
        } 
      } 
    } 
    if (!paramBoolean && this.invertDevTx != null) {
      Point2D.Float float = new Point2D.Float();
      this.fileFont.getGlyphMetrics(this.pScalerContext, paramInt, float);
      return float.x;
    } 
    if (this.invertDevTx != null || !paramBoolean) {
      f = (getGlyphMetrics(paramInt, paramBoolean)).x;
    } else {
      long l;
      if (this.getImageWithAdvance) {
        l = getGlyphImagePtr(paramInt);
      } else {
        l = getCachedGlyphPtr(paramInt);
      } 
      if (l != 0L) {
        f = StrikeCache.unsafe.getFloat(l + StrikeCache.xAdvanceOffset);
      } else {
        f = this.fileFont.getGlyphAdvance(this.pScalerContext, paramInt);
      } 
    } 
    if (this.horizontalAdvances != null) {
      this.horizontalAdvances[paramInt] = f;
    } else if (this.segmentedCache && this.segHorizontalAdvances != null) {
      int i = paramInt >> 5;
      int j = paramInt % 32;
      if (this.segHorizontalAdvances[i] == null) {
        this.segHorizontalAdvances[i] = new float[32];
        for (byte b = 0; b < 32; b++)
          this.segHorizontalAdvances[i][b] = Float.MAX_VALUE; 
      } 
      this.segHorizontalAdvances[i][j] = f;
    } 
    return f;
  }
  
  float getCodePointAdvance(int paramInt) { return getGlyphAdvance(this.mapper.charToGlyph(paramInt)); }
  
  void getGlyphImageBounds(int paramInt, Point2D.Float paramFloat, Rectangle paramRectangle) {
    long l = getGlyphImagePtr(paramInt);
    if (l == 0L) {
      paramRectangle.x = (int)Math.floor(paramFloat.x);
      paramRectangle.y = (int)Math.floor(paramFloat.y);
      paramRectangle.width = paramRectangle.height = 0;
      return;
    } 
    float f1 = StrikeCache.unsafe.getFloat(l + StrikeCache.topLeftXOffset);
    float f2 = StrikeCache.unsafe.getFloat(l + StrikeCache.topLeftYOffset);
    paramRectangle.x = (int)Math.floor((paramFloat.x + f1));
    paramRectangle.y = (int)Math.floor((paramFloat.y + f2));
    paramRectangle.width = StrikeCache.unsafe.getShort(l + StrikeCache.widthOffset) & 0xFFFF;
    paramRectangle.height = StrikeCache.unsafe.getShort(l + StrikeCache.heightOffset) & 0xFFFF;
    if ((this.desc.aaHint == 4 || this.desc.aaHint == 5) && f1 <= -2.0F) {
      int i = getGlyphImageMinX(l, paramRectangle.x);
      if (i > paramRectangle.x) {
        paramRectangle.x++;
        paramRectangle.width--;
      } 
    } 
  }
  
  private int getGlyphImageMinX(long paramLong, int paramInt) {
    char c1 = StrikeCache.unsafe.getChar(paramLong + StrikeCache.widthOffset);
    char c2 = StrikeCache.unsafe.getChar(paramLong + StrikeCache.heightOffset);
    int i = StrikeCache.unsafe.getChar(paramLong + StrikeCache.rowBytesOffset);
    if (i == c1)
      return paramInt; 
    long l = StrikeCache.unsafe.getAddress(paramLong + StrikeCache.pixelDataOffset);
    if (l == 0L)
      return paramInt; 
    for (byte b = 0; b < c2; b++) {
      for (byte b1 = 0; b1 < 3; b1++) {
        if (StrikeCache.unsafe.getByte(l + (b * i) + b1) != 0)
          return paramInt; 
      } 
    } 
    return paramInt + 1;
  }
  
  StrikeMetrics getFontMetrics() {
    if (this.strikeMetrics == null) {
      this.strikeMetrics = this.fileFont.getFontMetrics(this.pScalerContext);
      if (this.invertDevTx != null)
        this.strikeMetrics.convertToUserSpace(this.invertDevTx); 
    } 
    return this.strikeMetrics;
  }
  
  Point2D.Float getGlyphMetrics(int paramInt) { return getGlyphMetrics(paramInt, true); }
  
  private Point2D.Float getGlyphMetrics(int paramInt, boolean paramBoolean) {
    long l;
    Point2D.Float float = new Point2D.Float();
    if (paramInt >= 65534)
      return float; 
    if (this.getImageWithAdvance && paramBoolean) {
      l = getGlyphImagePtr(paramInt);
    } else {
      l = getCachedGlyphPtr(paramInt);
    } 
    if (l != 0L) {
      float = new Point2D.Float();
      float.x = StrikeCache.unsafe.getFloat(l + StrikeCache.xAdvanceOffset);
      float.y = StrikeCache.unsafe.getFloat(l + StrikeCache.yAdvanceOffset);
      if (this.invertDevTx != null)
        this.invertDevTx.deltaTransform(float, float); 
    } else {
      Integer integer = Integer.valueOf(paramInt);
      Point2D.Float float1 = null;
      ConcurrentHashMap concurrentHashMap = null;
      if (this.glyphMetricsMapRef != null)
        concurrentHashMap = (ConcurrentHashMap)this.glyphMetricsMapRef.get(); 
      if (concurrentHashMap != null) {
        float1 = (Point2D.Float)concurrentHashMap.get(integer);
        if (float1 != null) {
          float.x = float1.x;
          float.y = float1.y;
          return float;
        } 
      } 
      if (float1 == null) {
        this.fileFont.getGlyphMetrics(this.pScalerContext, paramInt, float);
        if (this.invertDevTx != null)
          this.invertDevTx.deltaTransform(float, float); 
        float1 = new Point2D.Float(float.x, float.y);
        if (concurrentHashMap == null) {
          concurrentHashMap = new ConcurrentHashMap();
          this.glyphMetricsMapRef = new SoftReference(concurrentHashMap);
        } 
        concurrentHashMap.put(integer, float1);
      } 
    } 
    return float;
  }
  
  Point2D.Float getCharMetrics(char paramChar) { return getGlyphMetrics(this.mapper.charToGlyph(paramChar)); }
  
  Rectangle2D.Float getGlyphOutlineBounds(int paramInt) {
    if (this.boundsMap == null)
      this.boundsMap = new ConcurrentHashMap(); 
    Integer integer = Integer.valueOf(paramInt);
    Rectangle2D.Float float = (Rectangle2D.Float)this.boundsMap.get(integer);
    if (float == null) {
      float = this.fileFont.getGlyphOutlineBounds(this.pScalerContext, paramInt);
      this.boundsMap.put(integer, float);
    } 
    return float;
  }
  
  public Rectangle2D getOutlineBounds(int paramInt) { return this.fileFont.getGlyphOutlineBounds(this.pScalerContext, paramInt); }
  
  GeneralPath getGlyphOutline(int paramInt, float paramFloat1, float paramFloat2) {
    GeneralPath generalPath = null;
    ConcurrentHashMap concurrentHashMap = null;
    if (this.outlineMapRef != null) {
      concurrentHashMap = (ConcurrentHashMap)this.outlineMapRef.get();
      if (concurrentHashMap != null)
        generalPath = (GeneralPath)concurrentHashMap.get(Integer.valueOf(paramInt)); 
    } 
    if (generalPath == null) {
      generalPath = this.fileFont.getGlyphOutline(this.pScalerContext, paramInt, 0.0F, 0.0F);
      if (concurrentHashMap == null) {
        concurrentHashMap = new ConcurrentHashMap();
        this.outlineMapRef = new WeakReference(concurrentHashMap);
      } 
      concurrentHashMap.put(Integer.valueOf(paramInt), generalPath);
    } 
    generalPath = (GeneralPath)generalPath.clone();
    if (paramFloat1 != 0.0F || paramFloat2 != 0.0F)
      generalPath.transform(AffineTransform.getTranslateInstance(paramFloat1, paramFloat2)); 
    return generalPath;
  }
  
  GeneralPath getGlyphVectorOutline(int[] paramArrayOfInt, float paramFloat1, float paramFloat2) { return this.fileFont.getGlyphVectorOutline(this.pScalerContext, paramArrayOfInt, paramArrayOfInt.length, paramFloat1, paramFloat2); }
  
  protected void adjustPoint(Point2D.Float paramFloat) {
    if (this.invertDevTx != null)
      this.invertDevTx.deltaTransform(paramFloat, paramFloat); 
  }
  
  static  {
    if (FontUtilities.isWindows && !FontUtilities.useT2K && !GraphicsEnvironment.isHeadless())
      isXPorLater = initNative(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\FileFontStrike.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */