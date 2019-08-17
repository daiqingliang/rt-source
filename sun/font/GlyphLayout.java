package sun.font;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public final class GlyphLayout {
  private GVData _gvdata = new GVData();
  
  private LayoutEngineFactory _lef;
  
  private TextRecord _textRecord = new TextRecord();
  
  private ScriptRun _scriptRuns = new ScriptRun();
  
  private FontRunIterator _fontRuns = new FontRunIterator();
  
  private int _ercount;
  
  private ArrayList _erecords = new ArrayList(10);
  
  private Point2D.Float _pt = new Point2D.Float();
  
  private FontStrikeDesc _sd = new FontStrikeDesc();
  
  private float[] _mat = new float[4];
  
  private int _typo_flags;
  
  private int _offset;
  
  public static GlyphLayout get(LayoutEngineFactory paramLayoutEngineFactory) {
    if (paramLayoutEngineFactory == null)
      paramLayoutEngineFactory = SunLayoutEngine.instance(); 
    GlyphLayout glyphLayout = null;
    synchronized (GlyphLayout.class) {
      if (cache != null) {
        glyphLayout = cache;
        cache = null;
      } 
    } 
    if (glyphLayout == null)
      glyphLayout = new GlyphLayout(); 
    glyphLayout._lef = paramLayoutEngineFactory;
    return glyphLayout;
  }
  
  public static void done(GlyphLayout paramGlyphLayout) {
    paramGlyphLayout._lef = null;
    cache = paramGlyphLayout;
  }
  
  public StandardGlyphVector layout(Font paramFont, FontRenderContext paramFontRenderContext, char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, StandardGlyphVector paramStandardGlyphVector) {
    StandardGlyphVector standardGlyphVector;
    if (paramArrayOfChar == null || paramInt1 < 0 || paramInt2 < 0 || paramInt2 > paramArrayOfChar.length - paramInt1)
      throw new IllegalArgumentException(); 
    init(paramInt2);
    if (paramFont.hasLayoutAttributes()) {
      AttributeValues attributeValues = ((AttributeMap)paramFont.getAttributes()).getValues();
      if (attributeValues.getKerning() != 0)
        this._typo_flags |= 0x1; 
      if (attributeValues.getLigatures() != 0)
        this._typo_flags |= 0x2; 
    } 
    this._offset = paramInt1;
    SDCache sDCache = SDCache.get(paramFont, paramFontRenderContext);
    this._mat[0] = (float)sDCache.gtx.getScaleX();
    this._mat[1] = (float)sDCache.gtx.getShearY();
    this._mat[2] = (float)sDCache.gtx.getShearX();
    this._mat[3] = (float)sDCache.gtx.getScaleY();
    this._pt.setLocation(sDCache.delta);
    int i = paramInt1 + paramInt2;
    int j = 0;
    int k = paramArrayOfChar.length;
    if (paramInt3 != 0) {
      if ((paramInt3 & true) != 0)
        this._typo_flags |= Integer.MIN_VALUE; 
      if ((paramInt3 & 0x2) != 0)
        j = paramInt1; 
      if ((paramInt3 & 0x4) != 0)
        k = i; 
    } 
    byte b = -1;
    Font2D font2D = FontUtilities.getFont2D(paramFont);
    if (font2D instanceof FontSubstitution)
      font2D = ((FontSubstitution)font2D).getCompositeFont2D(); 
    this._textRecord.init(paramArrayOfChar, paramInt1, i, j, k);
    int m = paramInt1;
    if (font2D instanceof CompositeFont) {
      this._scriptRuns.init(paramArrayOfChar, paramInt1, paramInt2);
      this._fontRuns.init((CompositeFont)font2D, paramArrayOfChar, paramInt1, i);
      while (this._scriptRuns.next()) {
        int i3 = this._scriptRuns.getScriptLimit();
        int i4 = this._scriptRuns.getScriptCode();
        while (this._fontRuns.next(i4, i3)) {
          PhysicalFont physicalFont = this._fontRuns.getFont();
          if (physicalFont instanceof NativeFont)
            physicalFont = ((NativeFont)physicalFont).getDelegateFont(); 
          int i5 = this._fontRuns.getGlyphMask();
          int i6 = this._fontRuns.getPos();
          nextEngineRecord(m, i6, i4, b, physicalFont, i5);
          m = i6;
        } 
      } 
    } else {
      this._scriptRuns.init(paramArrayOfChar, paramInt1, paramInt2);
      while (this._scriptRuns.next()) {
        int i3 = this._scriptRuns.getScriptLimit();
        int i4 = this._scriptRuns.getScriptCode();
        nextEngineRecord(m, i3, i4, b, font2D, 0);
        m = i3;
      } 
    } 
    int n = 0;
    int i1 = this._ercount;
    int i2 = 1;
    if (this._typo_flags < 0) {
      n = i1 - 1;
      i1 = -1;
      i2 = -1;
    } 
    this._sd = sDCache.sd;
    while (n != i1) {
      standardGlyphVector = (EngineRecord)this._erecords.get(n);
      while (true) {
        try {
          standardGlyphVector.layout();
          break;
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
          if (this._gvdata._count >= 0)
            this._gvdata.grow(); 
        } 
      } 
      if (this._gvdata._count < 0)
        break; 
      n += i2;
    } 
    if (this._gvdata._count < 0) {
      standardGlyphVector = new StandardGlyphVector(paramFont, paramArrayOfChar, paramInt1, paramInt2, paramFontRenderContext);
      if (FontUtilities.debugFonts())
        FontUtilities.getLogger().warning("OpenType layout failed on font: " + paramFont); 
    } else {
      standardGlyphVector = this._gvdata.createGlyphVector(paramFont, paramFontRenderContext, paramStandardGlyphVector);
    } 
    return standardGlyphVector;
  }
  
  private void init(int paramInt) {
    this._typo_flags = 0;
    this._ercount = 0;
    this._gvdata.init(paramInt);
  }
  
  private void nextEngineRecord(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Font2D paramFont2D, int paramInt5) {
    EngineRecord engineRecord = null;
    if (this._ercount == this._erecords.size()) {
      engineRecord = new EngineRecord();
      this._erecords.add(engineRecord);
    } else {
      engineRecord = (EngineRecord)this._erecords.get(this._ercount);
    } 
    engineRecord.init(paramInt1, paramInt2, paramFont2D, paramInt3, paramInt4, paramInt5);
    this._ercount++;
  }
  
  private final class EngineRecord {
    private int start;
    
    private int limit;
    
    private int gmask;
    
    private int eflags;
    
    private GlyphLayout.LayoutEngineKey key = new GlyphLayout.LayoutEngineKey();
    
    private GlyphLayout.LayoutEngine engine;
    
    void init(int param1Int1, int param1Int2, Font2D param1Font2D, int param1Int3, int param1Int4, int param1Int5) {
      this.start = param1Int1;
      this.limit = param1Int2;
      this.gmask = param1Int5;
      this.key.init(param1Font2D, param1Int3, param1Int4);
      this.eflags = 0;
      for (int i = param1Int1; i < param1Int2; i++) {
        int j = this.this$0._textRecord.text[i];
        if (Character.isHighSurrogate((char)j) && i < param1Int2 - 1 && Character.isLowSurrogate(this.this$0._textRecord.text[i + 1]))
          j = Character.toCodePoint((char)j, this.this$0._textRecord.text[++i]); 
        int k = Character.getType(j);
        if (k == 6 || k == 7 || k == 8) {
          this.eflags = 4;
          break;
        } 
      } 
      this.engine = GlyphLayout.this._lef.getEngine(this.key);
    }
    
    void layout() {
      this.this$0._textRecord.start = this.start;
      this.this$0._textRecord.limit = this.limit;
      this.engine.layout(GlyphLayout.this._sd, GlyphLayout.this._mat, this.gmask, this.start - GlyphLayout.this._offset, GlyphLayout.this._textRecord, GlyphLayout.this._typo_flags | this.eflags, GlyphLayout.this._pt, GlyphLayout.this._gvdata);
    }
  }
  
  public static final class GVData {
    public int _count;
    
    public int _flags;
    
    public int[] _glyphs;
    
    public float[] _positions;
    
    public int[] _indices;
    
    private static final int UNINITIALIZED_FLAGS = -1;
    
    public void init(int param1Int) {
      this._count = 0;
      this._flags = -1;
      if (this._glyphs == null || this._glyphs.length < param1Int) {
        if (param1Int < 20)
          param1Int = 20; 
        this._glyphs = new int[param1Int];
        this._positions = new float[param1Int * 2 + 2];
        this._indices = new int[param1Int];
      } 
    }
    
    public void grow() { grow(this._glyphs.length / 4); }
    
    public void grow(int param1Int) {
      int i = this._glyphs.length + param1Int;
      int[] arrayOfInt1 = new int[i];
      System.arraycopy(this._glyphs, 0, arrayOfInt1, 0, this._count);
      this._glyphs = arrayOfInt1;
      float[] arrayOfFloat = new float[i * 2 + 2];
      System.arraycopy(this._positions, 0, arrayOfFloat, 0, this._count * 2 + 2);
      this._positions = arrayOfFloat;
      int[] arrayOfInt2 = new int[i];
      System.arraycopy(this._indices, 0, arrayOfInt2, 0, this._count);
      this._indices = arrayOfInt2;
    }
    
    public void adjustPositions(AffineTransform param1AffineTransform) { param1AffineTransform.transform(this._positions, 0, this._positions, 0, this._count); }
    
    public StandardGlyphVector createGlyphVector(Font param1Font, FontRenderContext param1FontRenderContext, StandardGlyphVector param1StandardGlyphVector) {
      if (this._flags == -1) {
        this._flags = 0;
        if (this._count > 1) {
          boolean bool1 = true;
          boolean bool2 = true;
          int i = this._count;
          for (byte b = 0; b < this._count && (bool1 || bool2); b++) {
            int j = this._indices[b];
            bool1 = (bool1 && j == b) ? 1 : 0;
            bool2 = (bool2 && j == --i) ? 1 : 0;
          } 
          if (bool2)
            this._flags |= 0x4; 
          if (!bool2 && !bool1)
            this._flags |= 0x8; 
        } 
        this._flags |= 0x2;
      } 
      int[] arrayOfInt1 = new int[this._count];
      System.arraycopy(this._glyphs, 0, arrayOfInt1, 0, this._count);
      float[] arrayOfFloat = null;
      if ((this._flags & 0x2) != 0) {
        arrayOfFloat = new float[this._count * 2 + 2];
        System.arraycopy(this._positions, 0, arrayOfFloat, 0, arrayOfFloat.length);
      } 
      int[] arrayOfInt2 = null;
      if ((this._flags & 0x8) != 0) {
        arrayOfInt2 = new int[this._count];
        System.arraycopy(this._indices, 0, arrayOfInt2, 0, this._count);
      } 
      if (param1StandardGlyphVector == null) {
        param1StandardGlyphVector = new StandardGlyphVector(param1Font, param1FontRenderContext, arrayOfInt1, arrayOfFloat, arrayOfInt2, this._flags);
      } else {
        param1StandardGlyphVector.initGlyphVector(param1Font, param1FontRenderContext, arrayOfInt1, arrayOfFloat, arrayOfInt2, this._flags);
      } 
      return param1StandardGlyphVector;
    }
  }
  
  public static interface LayoutEngine {
    void layout(FontStrikeDesc param1FontStrikeDesc, float[] param1ArrayOfFloat, int param1Int1, int param1Int2, TextRecord param1TextRecord, int param1Int3, Point2D.Float param1Float, GlyphLayout.GVData param1GVData);
  }
  
  public static interface LayoutEngineFactory {
    GlyphLayout.LayoutEngine getEngine(Font2D param1Font2D, int param1Int1, int param1Int2);
    
    GlyphLayout.LayoutEngine getEngine(GlyphLayout.LayoutEngineKey param1LayoutEngineKey);
  }
  
  public static final class LayoutEngineKey {
    private Font2D font;
    
    private int script;
    
    private int lang;
    
    LayoutEngineKey() {}
    
    LayoutEngineKey(Font2D param1Font2D, int param1Int1, int param1Int2) { init(param1Font2D, param1Int1, param1Int2); }
    
    void init(Font2D param1Font2D, int param1Int1, int param1Int2) {
      this.font = param1Font2D;
      this.script = param1Int1;
      this.lang = param1Int2;
    }
    
    LayoutEngineKey copy() { return new LayoutEngineKey(this.font, this.script, this.lang); }
    
    Font2D font() { return this.font; }
    
    int script() { return this.script; }
    
    int lang() { return this.lang; }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      if (param1Object == null)
        return false; 
      try {
        LayoutEngineKey layoutEngineKey = (LayoutEngineKey)param1Object;
        return (this.script == layoutEngineKey.script && this.lang == layoutEngineKey.lang && this.font.equals(layoutEngineKey.font));
      } catch (ClassCastException classCastException) {
        return false;
      } 
    }
    
    public int hashCode() { return this.script ^ this.lang ^ this.font.hashCode(); }
  }
  
  private static final class SDCache {
    public Font key_font;
    
    public FontRenderContext key_frc;
    
    public AffineTransform dtx;
    
    public AffineTransform invdtx;
    
    public AffineTransform gtx;
    
    public Point2D.Float delta;
    
    public FontStrikeDesc sd;
    
    private static final Point2D.Float ZERO_DELTA = new Point2D.Float();
    
    private static SoftReference<ConcurrentHashMap<SDKey, SDCache>> cacheRef;
    
    private SDCache(Font param1Font, FontRenderContext param1FontRenderContext) {
      this.key_font = param1Font;
      this.key_frc = param1FontRenderContext;
      this.dtx = param1FontRenderContext.getTransform();
      this.dtx.setTransform(this.dtx.getScaleX(), this.dtx.getShearY(), this.dtx.getShearX(), this.dtx.getScaleY(), 0.0D, 0.0D);
      if (!this.dtx.isIdentity())
        try {
          this.invdtx = this.dtx.createInverse();
        } catch (NoninvertibleTransformException noninvertibleTransformException) {
          throw new InternalError(noninvertibleTransformException);
        }  
      float f = param1Font.getSize2D();
      if (param1Font.isTransformed()) {
        this.gtx = param1Font.getTransform();
        this.gtx.scale(f, f);
        this.delta = new Point2D.Float((float)this.gtx.getTranslateX(), (float)this.gtx.getTranslateY());
        this.gtx.setTransform(this.gtx.getScaleX(), this.gtx.getShearY(), this.gtx.getShearX(), this.gtx.getScaleY(), 0.0D, 0.0D);
        this.gtx.preConcatenate(this.dtx);
      } else {
        this.delta = ZERO_DELTA;
        this.gtx = new AffineTransform(this.dtx);
        this.gtx.scale(f, f);
      } 
      int i = FontStrikeDesc.getAAHintIntVal(param1FontRenderContext.getAntiAliasingHint(), FontUtilities.getFont2D(param1Font), (int)Math.abs(f));
      int j = FontStrikeDesc.getFMHintIntVal(param1FontRenderContext.getFractionalMetricsHint());
      this.sd = new FontStrikeDesc(this.dtx, this.gtx, param1Font.getStyle(), i, j);
    }
    
    public static SDCache get(Font param1Font, FontRenderContext param1FontRenderContext) {
      if (param1FontRenderContext.isTransformed()) {
        AffineTransform affineTransform = param1FontRenderContext.getTransform();
        if (affineTransform.getTranslateX() != 0.0D || affineTransform.getTranslateY() != 0.0D) {
          affineTransform = new AffineTransform(affineTransform.getScaleX(), affineTransform.getShearY(), affineTransform.getShearX(), affineTransform.getScaleY(), 0.0D, 0.0D);
          param1FontRenderContext = new FontRenderContext(affineTransform, param1FontRenderContext.getAntiAliasingHint(), param1FontRenderContext.getFractionalMetricsHint());
        } 
      } 
      SDKey sDKey = new SDKey(param1Font, param1FontRenderContext);
      ConcurrentHashMap concurrentHashMap = null;
      SDCache sDCache = null;
      if (cacheRef != null) {
        concurrentHashMap = (ConcurrentHashMap)cacheRef.get();
        if (concurrentHashMap != null)
          sDCache = (SDCache)concurrentHashMap.get(sDKey); 
      } 
      if (sDCache == null) {
        sDCache = new SDCache(param1Font, param1FontRenderContext);
        if (concurrentHashMap == null) {
          concurrentHashMap = new ConcurrentHashMap(10);
          cacheRef = new SoftReference(concurrentHashMap);
        } else if (concurrentHashMap.size() >= 512) {
          concurrentHashMap.clear();
        } 
        concurrentHashMap.put(sDKey, sDCache);
      } 
      return sDCache;
    }
    
    private static final class SDKey {
      private final Font font;
      
      private final FontRenderContext frc;
      
      private final int hash;
      
      SDKey(Font param2Font, FontRenderContext param2FontRenderContext) {
        this.font = param2Font;
        this.frc = param2FontRenderContext;
        this.hash = param2Font.hashCode() ^ param2FontRenderContext.hashCode();
      }
      
      public int hashCode() { return this.hash; }
      
      public boolean equals(Object param2Object) {
        try {
          SDKey sDKey = (SDKey)param2Object;
          return (this.hash == sDKey.hash && this.font.equals(sDKey.font) && this.frc.equals(sDKey.frc));
        } catch (ClassCastException classCastException) {
          return false;
        } 
      }
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\GlyphLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */