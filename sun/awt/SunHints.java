package sun.awt;

import java.awt.RenderingHints;

public class SunHints {
  private static final int NUM_KEYS = 10;
  
  private static final int VALS_PER_KEY = 8;
  
  public static final int INTKEY_RENDERING = 0;
  
  public static final int INTVAL_RENDER_DEFAULT = 0;
  
  public static final int INTVAL_RENDER_SPEED = 1;
  
  public static final int INTVAL_RENDER_QUALITY = 2;
  
  public static final int INTKEY_ANTIALIASING = 1;
  
  public static final int INTVAL_ANTIALIAS_DEFAULT = 0;
  
  public static final int INTVAL_ANTIALIAS_OFF = 1;
  
  public static final int INTVAL_ANTIALIAS_ON = 2;
  
  public static final int INTKEY_TEXT_ANTIALIASING = 2;
  
  public static final int INTVAL_TEXT_ANTIALIAS_DEFAULT = 0;
  
  public static final int INTVAL_TEXT_ANTIALIAS_OFF = 1;
  
  public static final int INTVAL_TEXT_ANTIALIAS_ON = 2;
  
  public static final int INTVAL_TEXT_ANTIALIAS_GASP = 3;
  
  public static final int INTVAL_TEXT_ANTIALIAS_LCD_HRGB = 4;
  
  public static final int INTVAL_TEXT_ANTIALIAS_LCD_HBGR = 5;
  
  public static final int INTVAL_TEXT_ANTIALIAS_LCD_VRGB = 6;
  
  public static final int INTVAL_TEXT_ANTIALIAS_LCD_VBGR = 7;
  
  public static final int INTKEY_FRACTIONALMETRICS = 3;
  
  public static final int INTVAL_FRACTIONALMETRICS_DEFAULT = 0;
  
  public static final int INTVAL_FRACTIONALMETRICS_OFF = 1;
  
  public static final int INTVAL_FRACTIONALMETRICS_ON = 2;
  
  public static final int INTKEY_DITHERING = 4;
  
  public static final int INTVAL_DITHER_DEFAULT = 0;
  
  public static final int INTVAL_DITHER_DISABLE = 1;
  
  public static final int INTVAL_DITHER_ENABLE = 2;
  
  public static final int INTKEY_INTERPOLATION = 5;
  
  public static final int INTVAL_INTERPOLATION_NEAREST_NEIGHBOR = 0;
  
  public static final int INTVAL_INTERPOLATION_BILINEAR = 1;
  
  public static final int INTVAL_INTERPOLATION_BICUBIC = 2;
  
  public static final int INTKEY_ALPHA_INTERPOLATION = 6;
  
  public static final int INTVAL_ALPHA_INTERPOLATION_DEFAULT = 0;
  
  public static final int INTVAL_ALPHA_INTERPOLATION_SPEED = 1;
  
  public static final int INTVAL_ALPHA_INTERPOLATION_QUALITY = 2;
  
  public static final int INTKEY_COLOR_RENDERING = 7;
  
  public static final int INTVAL_COLOR_RENDER_DEFAULT = 0;
  
  public static final int INTVAL_COLOR_RENDER_SPEED = 1;
  
  public static final int INTVAL_COLOR_RENDER_QUALITY = 2;
  
  public static final int INTKEY_STROKE_CONTROL = 8;
  
  public static final int INTVAL_STROKE_DEFAULT = 0;
  
  public static final int INTVAL_STROKE_NORMALIZE = 1;
  
  public static final int INTVAL_STROKE_PURE = 2;
  
  public static final int INTKEY_RESOLUTION_VARIANT = 9;
  
  public static final int INTVAL_RESOLUTION_VARIANT_DEFAULT = 0;
  
  public static final int INTVAL_RESOLUTION_VARIANT_OFF = 1;
  
  public static final int INTVAL_RESOLUTION_VARIANT_ON = 2;
  
  public static final int INTKEY_AATEXT_LCD_CONTRAST = 100;
  
  public static final Key KEY_RENDERING = new Key(0, "Global rendering quality key");
  
  public static final Object VALUE_RENDER_SPEED = new Value(KEY_RENDERING, 1, "Fastest rendering methods");
  
  public static final Object VALUE_RENDER_QUALITY = new Value(KEY_RENDERING, 2, "Highest quality rendering methods");
  
  public static final Object VALUE_RENDER_DEFAULT = new Value(KEY_RENDERING, 0, "Default rendering methods");
  
  public static final Key KEY_ANTIALIASING = new Key(1, "Global antialiasing enable key");
  
  public static final Object VALUE_ANTIALIAS_ON = new Value(KEY_ANTIALIASING, 2, "Antialiased rendering mode");
  
  public static final Object VALUE_ANTIALIAS_OFF = new Value(KEY_ANTIALIASING, 1, "Nonantialiased rendering mode");
  
  public static final Object VALUE_ANTIALIAS_DEFAULT = new Value(KEY_ANTIALIASING, 0, "Default antialiasing rendering mode");
  
  public static final Key KEY_TEXT_ANTIALIASING = new Key(2, "Text-specific antialiasing enable key");
  
  public static final Object VALUE_TEXT_ANTIALIAS_ON = new Value(KEY_TEXT_ANTIALIASING, 2, "Antialiased text mode");
  
  public static final Object VALUE_TEXT_ANTIALIAS_OFF = new Value(KEY_TEXT_ANTIALIASING, 1, "Nonantialiased text mode");
  
  public static final Object VALUE_TEXT_ANTIALIAS_DEFAULT = new Value(KEY_TEXT_ANTIALIASING, 0, "Default antialiasing text mode");
  
  public static final Object VALUE_TEXT_ANTIALIAS_GASP = new Value(KEY_TEXT_ANTIALIASING, 3, "gasp antialiasing text mode");
  
  public static final Object VALUE_TEXT_ANTIALIAS_LCD_HRGB = new Value(KEY_TEXT_ANTIALIASING, 4, "LCD HRGB antialiasing text mode");
  
  public static final Object VALUE_TEXT_ANTIALIAS_LCD_HBGR = new Value(KEY_TEXT_ANTIALIASING, 5, "LCD HBGR antialiasing text mode");
  
  public static final Object VALUE_TEXT_ANTIALIAS_LCD_VRGB = new Value(KEY_TEXT_ANTIALIASING, 6, "LCD VRGB antialiasing text mode");
  
  public static final Object VALUE_TEXT_ANTIALIAS_LCD_VBGR = new Value(KEY_TEXT_ANTIALIASING, 7, "LCD VBGR antialiasing text mode");
  
  public static final Key KEY_FRACTIONALMETRICS = new Key(3, "Fractional metrics enable key");
  
  public static final Object VALUE_FRACTIONALMETRICS_ON = new Value(KEY_FRACTIONALMETRICS, 2, "Fractional text metrics mode");
  
  public static final Object VALUE_FRACTIONALMETRICS_OFF = new Value(KEY_FRACTIONALMETRICS, 1, "Integer text metrics mode");
  
  public static final Object VALUE_FRACTIONALMETRICS_DEFAULT = new Value(KEY_FRACTIONALMETRICS, 0, "Default fractional text metrics mode");
  
  public static final Key KEY_DITHERING = new Key(4, "Dithering quality key");
  
  public static final Object VALUE_DITHER_ENABLE = new Value(KEY_DITHERING, 2, "Dithered rendering mode");
  
  public static final Object VALUE_DITHER_DISABLE = new Value(KEY_DITHERING, 1, "Nondithered rendering mode");
  
  public static final Object VALUE_DITHER_DEFAULT = new Value(KEY_DITHERING, 0, "Default dithering mode");
  
  public static final Key KEY_INTERPOLATION = new Key(5, "Image interpolation method key");
  
  public static final Object VALUE_INTERPOLATION_NEAREST_NEIGHBOR = new Value(KEY_INTERPOLATION, 0, "Nearest Neighbor image interpolation mode");
  
  public static final Object VALUE_INTERPOLATION_BILINEAR = new Value(KEY_INTERPOLATION, 1, "Bilinear image interpolation mode");
  
  public static final Object VALUE_INTERPOLATION_BICUBIC = new Value(KEY_INTERPOLATION, 2, "Bicubic image interpolation mode");
  
  public static final Key KEY_ALPHA_INTERPOLATION = new Key(6, "Alpha blending interpolation method key");
  
  public static final Object VALUE_ALPHA_INTERPOLATION_SPEED = new Value(KEY_ALPHA_INTERPOLATION, 1, "Fastest alpha blending methods");
  
  public static final Object VALUE_ALPHA_INTERPOLATION_QUALITY = new Value(KEY_ALPHA_INTERPOLATION, 2, "Highest quality alpha blending methods");
  
  public static final Object VALUE_ALPHA_INTERPOLATION_DEFAULT = new Value(KEY_ALPHA_INTERPOLATION, 0, "Default alpha blending methods");
  
  public static final Key KEY_COLOR_RENDERING = new Key(7, "Color rendering quality key");
  
  public static final Object VALUE_COLOR_RENDER_SPEED = new Value(KEY_COLOR_RENDERING, 1, "Fastest color rendering mode");
  
  public static final Object VALUE_COLOR_RENDER_QUALITY = new Value(KEY_COLOR_RENDERING, 2, "Highest quality color rendering mode");
  
  public static final Object VALUE_COLOR_RENDER_DEFAULT = new Value(KEY_COLOR_RENDERING, 0, "Default color rendering mode");
  
  public static final Key KEY_STROKE_CONTROL = new Key(8, "Stroke normalization control key");
  
  public static final Object VALUE_STROKE_DEFAULT = new Value(KEY_STROKE_CONTROL, 0, "Default stroke normalization");
  
  public static final Object VALUE_STROKE_NORMALIZE = new Value(KEY_STROKE_CONTROL, 1, "Normalize strokes for consistent rendering");
  
  public static final Object VALUE_STROKE_PURE = new Value(KEY_STROKE_CONTROL, 2, "Pure stroke conversion for accurate paths");
  
  public static final Key KEY_RESOLUTION_VARIANT = new Key(9, "Global image resolution variant key");
  
  public static final Object VALUE_RESOLUTION_VARIANT_DEFAULT = new Value(KEY_RESOLUTION_VARIANT, 0, "Choose image resolutions based on a default heuristic");
  
  public static final Object VALUE_RESOLUTION_VARIANT_OFF = new Value(KEY_RESOLUTION_VARIANT, 1, "Use only the standard resolution of an image");
  
  public static final Object VALUE_RESOLUTION_VARIANT_ON = new Value(KEY_RESOLUTION_VARIANT, 2, "Always use resolution-specific variants of images");
  
  public static final RenderingHints.Key KEY_TEXT_ANTIALIAS_LCD_CONTRAST = new LCDContrastKey(100, "Text-specific LCD contrast key");
  
  public static class Key extends RenderingHints.Key {
    String description;
    
    public Key(int param1Int, String param1String) {
      super(param1Int);
      this.description = param1String;
    }
    
    public final int getIndex() { return intKey(); }
    
    public final String toString() { return this.description; }
    
    public boolean isCompatibleValue(Object param1Object) { return (param1Object instanceof SunHints.Value) ? ((SunHints.Value)param1Object).isCompatibleKey(this) : 0; }
  }
  
  public static class LCDContrastKey extends Key {
    public LCDContrastKey(int param1Int, String param1String) { super(param1Int, param1String); }
    
    public final boolean isCompatibleValue(Object param1Object) {
      if (param1Object instanceof Integer) {
        int i = ((Integer)param1Object).intValue();
        return (i >= 100 && i <= 250);
      } 
      return false;
    }
  }
  
  public static class Value {
    private SunHints.Key myKey;
    
    private int index;
    
    private String description;
    
    private static Value[][] ValueObjects = new Value[10][8];
    
    private static void register(SunHints.Key param1Key, Value param1Value) {
      int i = param1Key.getIndex();
      int j = param1Value.getIndex();
      if (ValueObjects[i][j] != null)
        throw new InternalError("duplicate index: " + j); 
      ValueObjects[i][j] = param1Value;
    }
    
    public static Value get(int param1Int1, int param1Int2) { return ValueObjects[param1Int1][param1Int2]; }
    
    public Value(SunHints.Key param1Key, int param1Int, String param1String) {
      this.myKey = param1Key;
      this.index = param1Int;
      this.description = param1String;
      register(param1Key, this);
    }
    
    public final int getIndex() { return this.index; }
    
    public final String toString() { return this.description; }
    
    public final boolean isCompatibleKey(SunHints.Key param1Key) { return (this.myKey == param1Key); }
    
    public final int hashCode() { return System.identityHashCode(this); }
    
    public final boolean equals(Object param1Object) { return (this == param1Object); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\SunHints.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */