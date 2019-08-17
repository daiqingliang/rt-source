package javax.swing.plaf.nimbus;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.image.VolatileImage;
import java.lang.reflect.Method;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.Painter;
import javax.swing.UIManager;
import sun.reflect.misc.MethodUtil;

public abstract class AbstractRegionPainter extends Object implements Painter<JComponent> {
  private PaintContext ctx;
  
  private float f;
  
  private float leftWidth;
  
  private float topHeight;
  
  private float centerWidth;
  
  private float centerHeight;
  
  private float rightWidth;
  
  private float bottomHeight;
  
  private float leftScale;
  
  private float topScale;
  
  private float centerHScale;
  
  private float centerVScale;
  
  private float rightScale;
  
  private float bottomScale;
  
  public final void paint(Graphics2D paramGraphics2D, JComponent paramJComponent, int paramInt1, int paramInt2) {
    if (paramInt1 <= 0 || paramInt2 <= 0)
      return; 
    Object[] arrayOfObject = getExtendedCacheKeys(paramJComponent);
    PaintContext.CacheMode cacheMode = (this.ctx == null) ? PaintContext.CacheMode.NO_CACHING : this.ctx.cacheMode;
    if (cacheMode == PaintContext.CacheMode.NO_CACHING || !ImageCache.getInstance().isImageCachable(paramInt1, paramInt2) || paramGraphics2D instanceof java.awt.print.PrinterGraphics) {
      paint0(paramGraphics2D, paramJComponent, paramInt1, paramInt2, arrayOfObject);
    } else if (cacheMode == PaintContext.CacheMode.FIXED_SIZES) {
      paintWithFixedSizeCaching(paramGraphics2D, paramJComponent, paramInt1, paramInt2, arrayOfObject);
    } else {
      paintWith9SquareCaching(paramGraphics2D, this.ctx, paramJComponent, paramInt1, paramInt2, arrayOfObject);
    } 
  }
  
  protected Object[] getExtendedCacheKeys(JComponent paramJComponent) { return null; }
  
  protected abstract PaintContext getPaintContext();
  
  protected void configureGraphics(Graphics2D paramGraphics2D) { paramGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); }
  
  protected abstract void doPaint(Graphics2D paramGraphics2D, JComponent paramJComponent, int paramInt1, int paramInt2, Object[] paramArrayOfObject);
  
  protected final float decodeX(float paramFloat) {
    if (paramFloat >= 0.0F && paramFloat <= 1.0F)
      return paramFloat * this.leftWidth; 
    if (paramFloat > 1.0F && paramFloat < 2.0F)
      return (paramFloat - 1.0F) * this.centerWidth + this.leftWidth; 
    if (paramFloat >= 2.0F && paramFloat <= 3.0F)
      return (paramFloat - 2.0F) * this.rightWidth + this.leftWidth + this.centerWidth; 
    throw new IllegalArgumentException("Invalid x");
  }
  
  protected final float decodeY(float paramFloat) {
    if (paramFloat >= 0.0F && paramFloat <= 1.0F)
      return paramFloat * this.topHeight; 
    if (paramFloat > 1.0F && paramFloat < 2.0F)
      return (paramFloat - 1.0F) * this.centerHeight + this.topHeight; 
    if (paramFloat >= 2.0F && paramFloat <= 3.0F)
      return (paramFloat - 2.0F) * this.bottomHeight + this.topHeight + this.centerHeight; 
    throw new IllegalArgumentException("Invalid y");
  }
  
  protected final float decodeAnchorX(float paramFloat1, float paramFloat2) {
    if (paramFloat1 >= 0.0F && paramFloat1 <= 1.0F)
      return decodeX(paramFloat1) + paramFloat2 * this.leftScale; 
    if (paramFloat1 > 1.0F && paramFloat1 < 2.0F)
      return decodeX(paramFloat1) + paramFloat2 * this.centerHScale; 
    if (paramFloat1 >= 2.0F && paramFloat1 <= 3.0F)
      return decodeX(paramFloat1) + paramFloat2 * this.rightScale; 
    throw new IllegalArgumentException("Invalid x");
  }
  
  protected final float decodeAnchorY(float paramFloat1, float paramFloat2) {
    if (paramFloat1 >= 0.0F && paramFloat1 <= 1.0F)
      return decodeY(paramFloat1) + paramFloat2 * this.topScale; 
    if (paramFloat1 > 1.0F && paramFloat1 < 2.0F)
      return decodeY(paramFloat1) + paramFloat2 * this.centerVScale; 
    if (paramFloat1 >= 2.0F && paramFloat1 <= 3.0F)
      return decodeY(paramFloat1) + paramFloat2 * this.bottomScale; 
    throw new IllegalArgumentException("Invalid y");
  }
  
  protected final Color decodeColor(String paramString, float paramFloat1, float paramFloat2, float paramFloat3, int paramInt) {
    if (UIManager.getLookAndFeel() instanceof NimbusLookAndFeel) {
      NimbusLookAndFeel nimbusLookAndFeel = (NimbusLookAndFeel)UIManager.getLookAndFeel();
      return nimbusLookAndFeel.getDerivedColor(paramString, paramFloat1, paramFloat2, paramFloat3, paramInt, true);
    } 
    return Color.getHSBColor(paramFloat1, paramFloat2, paramFloat3);
  }
  
  protected final Color decodeColor(Color paramColor1, Color paramColor2, float paramFloat) { return new Color(NimbusLookAndFeel.deriveARGB(paramColor1, paramColor2, paramFloat)); }
  
  protected final LinearGradientPaint decodeGradient(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float[] paramArrayOfFloat, Color[] paramArrayOfColor) {
    if (paramFloat1 == paramFloat3 && paramFloat2 == paramFloat4)
      paramFloat4 += 1.0E-5F; 
    return new LinearGradientPaint(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramArrayOfFloat, paramArrayOfColor);
  }
  
  protected final RadialGradientPaint decodeRadialGradient(float paramFloat1, float paramFloat2, float paramFloat3, float[] paramArrayOfFloat, Color[] paramArrayOfColor) {
    if (paramFloat3 == 0.0F)
      paramFloat3 = 1.0E-5F; 
    return new RadialGradientPaint(paramFloat1, paramFloat2, paramFloat3, paramArrayOfFloat, paramArrayOfColor);
  }
  
  protected final Color getComponentColor(JComponent paramJComponent, String paramString, Color paramColor, float paramFloat1, float paramFloat2, int paramInt) {
    Color color = null;
    if (paramJComponent != null)
      if ("background".equals(paramString)) {
        color = paramJComponent.getBackground();
      } else if ("foreground".equals(paramString)) {
        color = paramJComponent.getForeground();
      } else if (paramJComponent instanceof JList && "selectionForeground".equals(paramString)) {
        color = ((JList)paramJComponent).getSelectionForeground();
      } else if (paramJComponent instanceof JList && "selectionBackground".equals(paramString)) {
        color = ((JList)paramJComponent).getSelectionBackground();
      } else if (paramJComponent instanceof JTable && "selectionForeground".equals(paramString)) {
        color = ((JTable)paramJComponent).getSelectionForeground();
      } else if (paramJComponent instanceof JTable && "selectionBackground".equals(paramString)) {
        color = ((JTable)paramJComponent).getSelectionBackground();
      } else {
        String str = "get" + Character.toUpperCase(paramString.charAt(0)) + paramString.substring(1);
        try {
          Method method = MethodUtil.getMethod(paramJComponent.getClass(), str, null);
          color = (Color)MethodUtil.invoke(method, paramJComponent, null);
        } catch (Exception exception) {}
        if (color == null) {
          Object object = paramJComponent.getClientProperty(paramString);
          if (object instanceof Color)
            color = (Color)object; 
        } 
      }  
    if (color == null || color instanceof javax.swing.plaf.UIResource)
      return paramColor; 
    if (paramFloat1 != 0.0F || paramFloat2 != 0.0F || paramInt != 0) {
      float[] arrayOfFloat = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
      arrayOfFloat[1] = clamp(arrayOfFloat[1] + paramFloat1);
      arrayOfFloat[2] = clamp(arrayOfFloat[2] + paramFloat2);
      int i = clamp(color.getAlpha() + paramInt);
      return new Color(Color.HSBtoRGB(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2]) & 0xFFFFFF | i << 24);
    } 
    return color;
  }
  
  private void prepare(float paramFloat1, float paramFloat2) {
    if (this.ctx == null || this.ctx.canvasSize == null) {
      this.f = 1.0F;
      this.leftWidth = this.centerWidth = this.rightWidth = 0.0F;
      this.topHeight = this.centerHeight = this.bottomHeight = 0.0F;
      this.leftScale = this.centerHScale = this.rightScale = 0.0F;
      this.topScale = this.centerVScale = this.bottomScale = 0.0F;
      return;
    } 
    Number number = (Number)UIManager.get("scale");
    this.f = (number == null) ? 1.0F : number.floatValue();
    if (this.ctx.inverted) {
      this.centerWidth = (this.ctx.b - this.ctx.a) * this.f;
      float f1 = paramFloat1 - this.centerWidth;
      this.leftWidth = f1 * this.ctx.aPercent;
      this.rightWidth = f1 * this.ctx.bPercent;
      this.centerHeight = (this.ctx.d - this.ctx.c) * this.f;
      f1 = paramFloat2 - this.centerHeight;
      this.topHeight = f1 * this.ctx.cPercent;
      this.bottomHeight = f1 * this.ctx.dPercent;
    } else {
      this.leftWidth = this.ctx.a * this.f;
      this.rightWidth = (float)(this.ctx.canvasSize.getWidth() - this.ctx.b) * this.f;
      this.centerWidth = paramFloat1 - this.leftWidth - this.rightWidth;
      this.topHeight = this.ctx.c * this.f;
      this.bottomHeight = (float)(this.ctx.canvasSize.getHeight() - this.ctx.d) * this.f;
      this.centerHeight = paramFloat2 - this.topHeight - this.bottomHeight;
    } 
    this.leftScale = (this.ctx.a == 0.0F) ? 0.0F : (this.leftWidth / this.ctx.a);
    this.centerHScale = (this.ctx.b - this.ctx.a == 0.0F) ? 0.0F : (this.centerWidth / (this.ctx.b - this.ctx.a));
    this.rightScale = (this.ctx.canvasSize.width - this.ctx.b == 0.0F) ? 0.0F : (this.rightWidth / (this.ctx.canvasSize.width - this.ctx.b));
    this.topScale = (this.ctx.c == 0.0F) ? 0.0F : (this.topHeight / this.ctx.c);
    this.centerVScale = (this.ctx.d - this.ctx.c == 0.0F) ? 0.0F : (this.centerHeight / (this.ctx.d - this.ctx.c));
    this.bottomScale = (this.ctx.canvasSize.height - this.ctx.d == 0.0F) ? 0.0F : (this.bottomHeight / (this.ctx.canvasSize.height - this.ctx.d));
  }
  
  private void paintWith9SquareCaching(Graphics2D paramGraphics2D, PaintContext paramPaintContext, JComponent paramJComponent, int paramInt1, int paramInt2, Object[] paramArrayOfObject) {
    Dimension dimension = paramPaintContext.canvasSize;
    Insets insets = paramPaintContext.stretchingInsets;
    if (paramInt1 <= dimension.width * paramPaintContext.maxHorizontalScaleFactor && paramInt2 <= dimension.height * paramPaintContext.maxVerticalScaleFactor) {
      VolatileImage volatileImage = getImage(paramGraphics2D.getDeviceConfiguration(), paramJComponent, dimension.width, dimension.height, paramArrayOfObject);
      if (volatileImage != null) {
        Insets insets1;
        if (paramPaintContext.inverted) {
          int i = (paramInt1 - dimension.width - insets.left + insets.right) / 2;
          int j = (paramInt2 - dimension.height - insets.top + insets.bottom) / 2;
          insets1 = new Insets(j, i, j, i);
        } else {
          insets1 = insets;
        } 
        Object object = paramGraphics2D.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
        paramGraphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        ImageScalingHelper.paint(paramGraphics2D, 0, 0, paramInt1, paramInt2, volatileImage, insets, insets1, ImageScalingHelper.PaintType.PAINT9_STRETCH, 512);
        paramGraphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, (object != null) ? object : RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
      } else {
        paint0(paramGraphics2D, paramJComponent, paramInt1, paramInt2, paramArrayOfObject);
      } 
    } else {
      paint0(paramGraphics2D, paramJComponent, paramInt1, paramInt2, paramArrayOfObject);
    } 
  }
  
  private void paintWithFixedSizeCaching(Graphics2D paramGraphics2D, JComponent paramJComponent, int paramInt1, int paramInt2, Object[] paramArrayOfObject) {
    VolatileImage volatileImage = getImage(paramGraphics2D.getDeviceConfiguration(), paramJComponent, paramInt1, paramInt2, paramArrayOfObject);
    if (volatileImage != null) {
      paramGraphics2D.drawImage(volatileImage, 0, 0, null);
    } else {
      paint0(paramGraphics2D, paramJComponent, paramInt1, paramInt2, paramArrayOfObject);
    } 
  }
  
  private VolatileImage getImage(GraphicsConfiguration paramGraphicsConfiguration, JComponent paramJComponent, int paramInt1, int paramInt2, Object[] paramArrayOfObject) {
    ImageCache imageCache = ImageCache.getInstance();
    VolatileImage volatileImage = (VolatileImage)imageCache.getImage(paramGraphicsConfiguration, paramInt1, paramInt2, new Object[] { this, paramArrayOfObject });
    byte b = 0;
    do {
      int i = 2;
      if (volatileImage != null)
        i = volatileImage.validate(paramGraphicsConfiguration); 
      if (i != 2 && i != 1)
        continue; 
      if (volatileImage == null || volatileImage.getWidth() != paramInt1 || volatileImage.getHeight() != paramInt2 || i == 2) {
        if (volatileImage != null) {
          volatileImage.flush();
          volatileImage = null;
        } 
        volatileImage = paramGraphicsConfiguration.createCompatibleVolatileImage(paramInt1, paramInt2, 3);
        imageCache.setImage(volatileImage, paramGraphicsConfiguration, paramInt1, paramInt2, new Object[] { this, paramArrayOfObject });
      } 
      Graphics2D graphics2D = volatileImage.createGraphics();
      graphics2D.setComposite(AlphaComposite.Clear);
      graphics2D.fillRect(0, 0, paramInt1, paramInt2);
      graphics2D.setComposite(AlphaComposite.SrcOver);
      configureGraphics(graphics2D);
      paint0(graphics2D, paramJComponent, paramInt1, paramInt2, paramArrayOfObject);
      graphics2D.dispose();
    } while (volatileImage.contentsLost() && b++ < 3);
    return (b == 3) ? null : volatileImage;
  }
  
  private void paint0(Graphics2D paramGraphics2D, JComponent paramJComponent, int paramInt1, int paramInt2, Object[] paramArrayOfObject) {
    prepare(paramInt1, paramInt2);
    paramGraphics2D = (Graphics2D)paramGraphics2D.create();
    configureGraphics(paramGraphics2D);
    doPaint(paramGraphics2D, paramJComponent, paramInt1, paramInt2, paramArrayOfObject);
    paramGraphics2D.dispose();
  }
  
  private float clamp(float paramFloat) {
    if (paramFloat < 0.0F) {
      paramFloat = 0.0F;
    } else if (paramFloat > 1.0F) {
      paramFloat = 1.0F;
    } 
    return paramFloat;
  }
  
  private int clamp(int paramInt) {
    if (paramInt < 0) {
      paramInt = 0;
    } else if (paramInt > 255) {
      paramInt = 255;
    } 
    return paramInt;
  }
  
  protected static class PaintContext {
    private static Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);
    
    private Insets stretchingInsets;
    
    private Dimension canvasSize;
    
    private boolean inverted;
    
    private CacheMode cacheMode;
    
    private double maxHorizontalScaleFactor;
    
    private double maxVerticalScaleFactor;
    
    private float a;
    
    private float b;
    
    private float c;
    
    private float d;
    
    private float aPercent;
    
    private float bPercent;
    
    private float cPercent;
    
    private float dPercent;
    
    public PaintContext(Insets param1Insets, Dimension param1Dimension, boolean param1Boolean) { this(param1Insets, param1Dimension, param1Boolean, null, 1.0D, 1.0D); }
    
    public PaintContext(Insets param1Insets, Dimension param1Dimension, boolean param1Boolean, CacheMode param1CacheMode, double param1Double1, double param1Double2) {
      if (param1Double1 < 1.0D || param1Double1 < 1.0D)
        throw new IllegalArgumentException("Both maxH and maxV must be >= 1"); 
      this.stretchingInsets = (param1Insets == null) ? EMPTY_INSETS : param1Insets;
      this.canvasSize = param1Dimension;
      this.inverted = param1Boolean;
      this.cacheMode = (param1CacheMode == null) ? CacheMode.NO_CACHING : param1CacheMode;
      this.maxHorizontalScaleFactor = param1Double1;
      this.maxVerticalScaleFactor = param1Double2;
      if (param1Dimension != null) {
        this.a = this.stretchingInsets.left;
        this.b = (param1Dimension.width - this.stretchingInsets.right);
        this.c = this.stretchingInsets.top;
        this.d = (param1Dimension.height - this.stretchingInsets.bottom);
        this.canvasSize = param1Dimension;
        this.inverted = param1Boolean;
        if (param1Boolean) {
          float f = param1Dimension.width - this.b - this.a;
          this.aPercent = (f > 0.0F) ? (this.a / f) : 0.0F;
          this.bPercent = (f > 0.0F) ? (this.b / f) : 0.0F;
          f = param1Dimension.height - this.d - this.c;
          this.cPercent = (f > 0.0F) ? (this.c / f) : 0.0F;
          this.dPercent = (f > 0.0F) ? (this.d / f) : 0.0F;
        } 
      } 
    }
    
    protected enum CacheMode {
      NO_CACHING, FIXED_SIZES, NINE_SQUARE_SCALE;
    }
  }
  
  protected enum CacheMode {
    NO_CACHING, FIXED_SIZES, NINE_SQUARE_SCALE;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\AbstractRegionPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */