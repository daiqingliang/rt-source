package java.awt;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import sun.awt.SunHints;

public class RenderingHints extends Object implements Map<Object, Object>, Cloneable {
  HashMap<Object, Object> hintmap = new HashMap(7);
  
  public static final Key KEY_ANTIALIASING = SunHints.KEY_ANTIALIASING;
  
  public static final Object VALUE_ANTIALIAS_ON = SunHints.VALUE_ANTIALIAS_ON;
  
  public static final Object VALUE_ANTIALIAS_OFF = SunHints.VALUE_ANTIALIAS_OFF;
  
  public static final Object VALUE_ANTIALIAS_DEFAULT = SunHints.VALUE_ANTIALIAS_DEFAULT;
  
  public static final Key KEY_RENDERING = SunHints.KEY_RENDERING;
  
  public static final Object VALUE_RENDER_SPEED = SunHints.VALUE_RENDER_SPEED;
  
  public static final Object VALUE_RENDER_QUALITY = SunHints.VALUE_RENDER_QUALITY;
  
  public static final Object VALUE_RENDER_DEFAULT = SunHints.VALUE_RENDER_DEFAULT;
  
  public static final Key KEY_DITHERING = SunHints.KEY_DITHERING;
  
  public static final Object VALUE_DITHER_DISABLE = SunHints.VALUE_DITHER_DISABLE;
  
  public static final Object VALUE_DITHER_ENABLE = SunHints.VALUE_DITHER_ENABLE;
  
  public static final Object VALUE_DITHER_DEFAULT = SunHints.VALUE_DITHER_DEFAULT;
  
  public static final Key KEY_TEXT_ANTIALIASING = SunHints.KEY_TEXT_ANTIALIASING;
  
  public static final Object VALUE_TEXT_ANTIALIAS_ON = SunHints.VALUE_TEXT_ANTIALIAS_ON;
  
  public static final Object VALUE_TEXT_ANTIALIAS_OFF = SunHints.VALUE_TEXT_ANTIALIAS_OFF;
  
  public static final Object VALUE_TEXT_ANTIALIAS_DEFAULT = SunHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
  
  public static final Object VALUE_TEXT_ANTIALIAS_GASP = SunHints.VALUE_TEXT_ANTIALIAS_GASP;
  
  public static final Object VALUE_TEXT_ANTIALIAS_LCD_HRGB = SunHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB;
  
  public static final Object VALUE_TEXT_ANTIALIAS_LCD_HBGR = SunHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR;
  
  public static final Object VALUE_TEXT_ANTIALIAS_LCD_VRGB = SunHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB;
  
  public static final Object VALUE_TEXT_ANTIALIAS_LCD_VBGR = SunHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR;
  
  public static final Key KEY_TEXT_LCD_CONTRAST = SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST;
  
  public static final Key KEY_FRACTIONALMETRICS = SunHints.KEY_FRACTIONALMETRICS;
  
  public static final Object VALUE_FRACTIONALMETRICS_OFF = SunHints.VALUE_FRACTIONALMETRICS_OFF;
  
  public static final Object VALUE_FRACTIONALMETRICS_ON = SunHints.VALUE_FRACTIONALMETRICS_ON;
  
  public static final Object VALUE_FRACTIONALMETRICS_DEFAULT = SunHints.VALUE_FRACTIONALMETRICS_DEFAULT;
  
  public static final Key KEY_INTERPOLATION = SunHints.KEY_INTERPOLATION;
  
  public static final Object VALUE_INTERPOLATION_NEAREST_NEIGHBOR = SunHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
  
  public static final Object VALUE_INTERPOLATION_BILINEAR = SunHints.VALUE_INTERPOLATION_BILINEAR;
  
  public static final Object VALUE_INTERPOLATION_BICUBIC = SunHints.VALUE_INTERPOLATION_BICUBIC;
  
  public static final Key KEY_ALPHA_INTERPOLATION = SunHints.KEY_ALPHA_INTERPOLATION;
  
  public static final Object VALUE_ALPHA_INTERPOLATION_SPEED = SunHints.VALUE_ALPHA_INTERPOLATION_SPEED;
  
  public static final Object VALUE_ALPHA_INTERPOLATION_QUALITY = SunHints.VALUE_ALPHA_INTERPOLATION_QUALITY;
  
  public static final Object VALUE_ALPHA_INTERPOLATION_DEFAULT = SunHints.VALUE_ALPHA_INTERPOLATION_DEFAULT;
  
  public static final Key KEY_COLOR_RENDERING = SunHints.KEY_COLOR_RENDERING;
  
  public static final Object VALUE_COLOR_RENDER_SPEED = SunHints.VALUE_COLOR_RENDER_SPEED;
  
  public static final Object VALUE_COLOR_RENDER_QUALITY = SunHints.VALUE_COLOR_RENDER_QUALITY;
  
  public static final Object VALUE_COLOR_RENDER_DEFAULT = SunHints.VALUE_COLOR_RENDER_DEFAULT;
  
  public static final Key KEY_STROKE_CONTROL = SunHints.KEY_STROKE_CONTROL;
  
  public static final Object VALUE_STROKE_DEFAULT = SunHints.VALUE_STROKE_DEFAULT;
  
  public static final Object VALUE_STROKE_NORMALIZE = SunHints.VALUE_STROKE_NORMALIZE;
  
  public static final Object VALUE_STROKE_PURE = SunHints.VALUE_STROKE_PURE;
  
  public RenderingHints(Map<Key, ?> paramMap) {
    if (paramMap != null)
      this.hintmap.putAll(paramMap); 
  }
  
  public RenderingHints(Key paramKey, Object paramObject) { this.hintmap.put(paramKey, paramObject); }
  
  public int size() { return this.hintmap.size(); }
  
  public boolean isEmpty() { return this.hintmap.isEmpty(); }
  
  public boolean containsKey(Object paramObject) { return this.hintmap.containsKey((Key)paramObject); }
  
  public boolean containsValue(Object paramObject) { return this.hintmap.containsValue(paramObject); }
  
  public Object get(Object paramObject) { return this.hintmap.get((Key)paramObject); }
  
  public Object put(Object paramObject1, Object paramObject2) {
    if (!((Key)paramObject1).isCompatibleValue(paramObject2))
      throw new IllegalArgumentException(paramObject2 + " incompatible with " + paramObject1); 
    return this.hintmap.put((Key)paramObject1, paramObject2);
  }
  
  public void add(RenderingHints paramRenderingHints) { this.hintmap.putAll(paramRenderingHints.hintmap); }
  
  public void clear() { this.hintmap.clear(); }
  
  public Object remove(Object paramObject) { return this.hintmap.remove((Key)paramObject); }
  
  public void putAll(Map<?, ?> paramMap) {
    if (RenderingHints.class.isInstance(paramMap)) {
      for (Map.Entry entry : paramMap.entrySet())
        this.hintmap.put(entry.getKey(), entry.getValue()); 
    } else {
      for (Map.Entry entry : paramMap.entrySet())
        put(entry.getKey(), entry.getValue()); 
    } 
  }
  
  public Set<Object> keySet() { return this.hintmap.keySet(); }
  
  public Collection<Object> values() { return this.hintmap.values(); }
  
  public Set<Map.Entry<Object, Object>> entrySet() { return Collections.unmodifiableMap(this.hintmap).entrySet(); }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof RenderingHints) ? this.hintmap.equals(((RenderingHints)paramObject).hintmap) : ((paramObject instanceof Map) ? this.hintmap.equals(paramObject) : 0); }
  
  public int hashCode() { return this.hintmap.hashCode(); }
  
  public Object clone() {
    RenderingHints renderingHints;
    try {
      renderingHints = (RenderingHints)super.clone();
      if (this.hintmap != null)
        renderingHints.hintmap = (HashMap)this.hintmap.clone(); 
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
    return renderingHints;
  }
  
  public String toString() { return (this.hintmap == null) ? (getClass().getName() + "@" + Integer.toHexString(hashCode()) + " (0 hints)") : this.hintmap.toString(); }
  
  public static abstract class Key {
    private static HashMap<Object, Object> identitymap = new HashMap(17);
    
    private int privatekey;
    
    private String getIdentity() { return getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(getClass())) + ":" + Integer.toHexString(this.privatekey); }
    
    private static void recordIdentity(Key param1Key) {
      String str = param1Key.getIdentity();
      Object object = identitymap.get(str);
      if (object != null) {
        Key key = (Key)((WeakReference)object).get();
        if (key != null && key.getClass() == param1Key.getClass())
          throw new IllegalArgumentException(str + " already registered"); 
      } 
      identitymap.put(str, new WeakReference(param1Key));
    }
    
    protected Key(int param1Int) {
      this.privatekey = param1Int;
      recordIdentity(this);
    }
    
    public abstract boolean isCompatibleValue(Object param1Object);
    
    protected final int intKey() { return this.privatekey; }
    
    public final int hashCode() { return super.hashCode(); }
    
    public final boolean equals(Object param1Object) { return (this == param1Object); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\RenderingHints.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */