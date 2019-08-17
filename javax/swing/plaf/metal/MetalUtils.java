package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import sun.swing.CachedPainter;
import sun.swing.ImageIconUIResource;

class MetalUtils {
  static void drawFlush3DBorder(Graphics paramGraphics, Rectangle paramRectangle) { drawFlush3DBorder(paramGraphics, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height); }
  
  static void drawFlush3DBorder(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    paramGraphics.translate(paramInt1, paramInt2);
    paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
    paramGraphics.drawRect(0, 0, paramInt3 - 2, paramInt4 - 2);
    paramGraphics.setColor(MetalLookAndFeel.getControlHighlight());
    paramGraphics.drawRect(1, 1, paramInt3 - 2, paramInt4 - 2);
    paramGraphics.setColor(MetalLookAndFeel.getControl());
    paramGraphics.drawLine(0, paramInt4 - 1, 1, paramInt4 - 2);
    paramGraphics.drawLine(paramInt3 - 1, 0, paramInt3 - 2, 1);
    paramGraphics.translate(-paramInt1, -paramInt2);
  }
  
  static void drawPressed3DBorder(Graphics paramGraphics, Rectangle paramRectangle) { drawPressed3DBorder(paramGraphics, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height); }
  
  static void drawDisabledBorder(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    paramGraphics.translate(paramInt1, paramInt2);
    paramGraphics.setColor(MetalLookAndFeel.getControlShadow());
    paramGraphics.drawRect(0, 0, paramInt3 - 1, paramInt4 - 1);
    paramGraphics.translate(-paramInt1, -paramInt2);
  }
  
  static void drawPressed3DBorder(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    paramGraphics.translate(paramInt1, paramInt2);
    drawFlush3DBorder(paramGraphics, 0, 0, paramInt3, paramInt4);
    paramGraphics.setColor(MetalLookAndFeel.getControlShadow());
    paramGraphics.drawLine(1, 1, 1, paramInt4 - 2);
    paramGraphics.drawLine(1, 1, paramInt3 - 2, 1);
    paramGraphics.translate(-paramInt1, -paramInt2);
  }
  
  static void drawDark3DBorder(Graphics paramGraphics, Rectangle paramRectangle) { drawDark3DBorder(paramGraphics, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height); }
  
  static void drawDark3DBorder(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    paramGraphics.translate(paramInt1, paramInt2);
    drawFlush3DBorder(paramGraphics, 0, 0, paramInt3, paramInt4);
    paramGraphics.setColor(MetalLookAndFeel.getControl());
    paramGraphics.drawLine(1, 1, 1, paramInt4 - 2);
    paramGraphics.drawLine(1, 1, paramInt3 - 2, 1);
    paramGraphics.setColor(MetalLookAndFeel.getControlShadow());
    paramGraphics.drawLine(1, paramInt4 - 2, 1, paramInt4 - 2);
    paramGraphics.drawLine(paramInt3 - 2, 1, paramInt3 - 2, 1);
    paramGraphics.translate(-paramInt1, -paramInt2);
  }
  
  static void drawButtonBorder(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean) {
    if (paramBoolean) {
      drawActiveButtonBorder(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    } else {
      drawFlush3DBorder(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    } 
  }
  
  static void drawActiveButtonBorder(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    drawFlush3DBorder(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    paramGraphics.setColor(MetalLookAndFeel.getPrimaryControl());
    paramGraphics.drawLine(paramInt1 + 1, paramInt2 + 1, paramInt1 + 1, paramInt4 - 3);
    paramGraphics.drawLine(paramInt1 + 1, paramInt2 + 1, paramInt3 - 3, paramInt1 + 1);
    paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
    paramGraphics.drawLine(paramInt1 + 2, paramInt4 - 2, paramInt3 - 2, paramInt4 - 2);
    paramGraphics.drawLine(paramInt3 - 2, paramInt2 + 2, paramInt3 - 2, paramInt4 - 2);
  }
  
  static void drawDefaultButtonBorder(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean) {
    drawButtonBorder(paramGraphics, paramInt1 + 1, paramInt2 + 1, paramInt3 - 1, paramInt4 - 1, paramBoolean);
    paramGraphics.translate(paramInt1, paramInt2);
    paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
    paramGraphics.drawRect(0, 0, paramInt3 - 3, paramInt4 - 3);
    paramGraphics.drawLine(paramInt3 - 2, 0, paramInt3 - 2, 0);
    paramGraphics.drawLine(0, paramInt4 - 2, 0, paramInt4 - 2);
    paramGraphics.translate(-paramInt1, -paramInt2);
  }
  
  static void drawDefaultButtonPressedBorder(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    drawPressed3DBorder(paramGraphics, paramInt1 + 1, paramInt2 + 1, paramInt3 - 1, paramInt4 - 1);
    paramGraphics.translate(paramInt1, paramInt2);
    paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
    paramGraphics.drawRect(0, 0, paramInt3 - 3, paramInt4 - 3);
    paramGraphics.drawLine(paramInt3 - 2, 0, paramInt3 - 2, 0);
    paramGraphics.drawLine(0, paramInt4 - 2, 0, paramInt4 - 2);
    paramGraphics.setColor(MetalLookAndFeel.getControl());
    paramGraphics.drawLine(paramInt3 - 1, 0, paramInt3 - 1, 0);
    paramGraphics.drawLine(0, paramInt4 - 1, 0, paramInt4 - 1);
    paramGraphics.translate(-paramInt1, -paramInt2);
  }
  
  static boolean isLeftToRight(Component paramComponent) { return paramComponent.getComponentOrientation().isLeftToRight(); }
  
  static int getInt(Object paramObject, int paramInt) {
    Object object = UIManager.get(paramObject);
    if (object instanceof Integer)
      return ((Integer)object).intValue(); 
    if (object instanceof String)
      try {
        return Integer.parseInt((String)object);
      } catch (NumberFormatException numberFormatException) {} 
    return paramInt;
  }
  
  static boolean drawGradient(Component paramComponent, Graphics paramGraphics, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean) {
    List list = (List)UIManager.get(paramString);
    if (list == null || !(paramGraphics instanceof Graphics2D))
      return false; 
    if (paramInt3 <= 0 || paramInt4 <= 0)
      return true; 
    GradientPainter.INSTANCE.paint(paramComponent, (Graphics2D)paramGraphics, list, paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean);
    return true;
  }
  
  static boolean isToolBarButton(JComponent paramJComponent) { return paramJComponent.getParent() instanceof javax.swing.JToolBar; }
  
  static Icon getOceanToolBarIcon(Image paramImage) {
    FilteredImageSource filteredImageSource = new FilteredImageSource(paramImage.getSource(), new OceanToolBarImageFilter());
    return new ImageIconUIResource(Toolkit.getDefaultToolkit().createImage(filteredImageSource));
  }
  
  static Icon getOceanDisabledButtonIcon(Image paramImage) {
    Object[] arrayOfObject = (Object[])UIManager.get("Button.disabledGrayRange");
    int i = 180;
    int j = 215;
    if (arrayOfObject != null) {
      i = ((Integer)arrayOfObject[0]).intValue();
      j = ((Integer)arrayOfObject[1]).intValue();
    } 
    FilteredImageSource filteredImageSource = new FilteredImageSource(paramImage.getSource(), new OceanDisabledButtonImageFilter(i, j));
    return new ImageIconUIResource(Toolkit.getDefaultToolkit().createImage(filteredImageSource));
  }
  
  private static class GradientPainter extends CachedPainter {
    public static final GradientPainter INSTANCE = new GradientPainter(8);
    
    private static final int IMAGE_SIZE = 64;
    
    private int w;
    
    private int h;
    
    GradientPainter(int param1Int) { super(param1Int); }
    
    public void paint(Component param1Component, Graphics2D param1Graphics2D, List param1List, int param1Int1, int param1Int2, int param1Int3, int param1Int4, boolean param1Boolean) {
      byte b;
      int i;
      if (param1Boolean) {
        i = 64;
        b = param1Int4;
      } else {
        i = param1Int3;
        b = 64;
      } 
      synchronized (param1Component.getTreeLock()) {
        this.w = param1Int3;
        this.h = param1Int4;
        paint(param1Component, param1Graphics2D, param1Int1, param1Int2, i, b, new Object[] { param1List, Boolean.valueOf(param1Boolean) });
      } 
    }
    
    protected void paintToImage(Component param1Component, Image param1Image, Graphics param1Graphics, int param1Int1, int param1Int2, Object[] param1ArrayOfObject) {
      Graphics2D graphics2D = (Graphics2D)param1Graphics;
      List list = (List)param1ArrayOfObject[0];
      boolean bool = ((Boolean)param1ArrayOfObject[1]).booleanValue();
      if (bool) {
        drawVerticalGradient(graphics2D, ((Number)list.get(0)).floatValue(), ((Number)list.get(1)).floatValue(), (Color)list.get(2), (Color)list.get(3), (Color)list.get(4), param1Int1, param1Int2);
      } else {
        drawHorizontalGradient(graphics2D, ((Number)list.get(0)).floatValue(), ((Number)list.get(1)).floatValue(), (Color)list.get(2), (Color)list.get(3), (Color)list.get(4), param1Int1, param1Int2);
      } 
    }
    
    protected void paintImage(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, Image param1Image, Object[] param1ArrayOfObject) {
      boolean bool = ((Boolean)param1ArrayOfObject[1]).booleanValue();
      param1Graphics.translate(param1Int1, param1Int2);
      if (bool) {
        for (int i = 0; i < this.w; i += 64) {
          int j = Math.min(64, this.w - i);
          param1Graphics.drawImage(param1Image, i, 0, i + j, this.h, 0, 0, j, this.h, null);
        } 
      } else {
        for (int i = 0; i < this.h; i += 64) {
          int j = Math.min(64, this.h - i);
          param1Graphics.drawImage(param1Image, 0, i, this.w, i + j, 0, 0, this.w, j, null);
        } 
      } 
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    private void drawVerticalGradient(Graphics2D param1Graphics2D, float param1Float1, float param1Float2, Color param1Color1, Color param1Color2, Color param1Color3, int param1Int1, int param1Int2) {
      int i = (int)(param1Float1 * param1Int2);
      int j = (int)(param1Float2 * param1Int2);
      if (i > 0) {
        param1Graphics2D.setPaint(getGradient(0.0F, 0.0F, param1Color1, 0.0F, i, param1Color2));
        param1Graphics2D.fillRect(0, 0, param1Int1, i);
      } 
      if (j > 0) {
        param1Graphics2D.setColor(param1Color2);
        param1Graphics2D.fillRect(0, i, param1Int1, j);
      } 
      if (i > 0) {
        param1Graphics2D.setPaint(getGradient(0.0F, i + j, param1Color2, 0.0F, i * 2.0F + j, param1Color1));
        param1Graphics2D.fillRect(0, i + j, param1Int1, i);
      } 
      if (param1Int2 - i * 2 - j > 0) {
        param1Graphics2D.setPaint(getGradient(0.0F, i * 2.0F + j, param1Color1, 0.0F, param1Int2, param1Color3));
        param1Graphics2D.fillRect(0, i * 2 + j, param1Int1, param1Int2 - i * 2 - j);
      } 
    }
    
    private void drawHorizontalGradient(Graphics2D param1Graphics2D, float param1Float1, float param1Float2, Color param1Color1, Color param1Color2, Color param1Color3, int param1Int1, int param1Int2) {
      int i = (int)(param1Float1 * param1Int1);
      int j = (int)(param1Float2 * param1Int1);
      if (i > 0) {
        param1Graphics2D.setPaint(getGradient(0.0F, 0.0F, param1Color1, i, 0.0F, param1Color2));
        param1Graphics2D.fillRect(0, 0, i, param1Int2);
      } 
      if (j > 0) {
        param1Graphics2D.setColor(param1Color2);
        param1Graphics2D.fillRect(i, 0, j, param1Int2);
      } 
      if (i > 0) {
        param1Graphics2D.setPaint(getGradient(i + j, 0.0F, param1Color2, i * 2.0F + j, 0.0F, param1Color1));
        param1Graphics2D.fillRect(i + j, 0, i, param1Int2);
      } 
      if (param1Int1 - i * 2 - j > 0) {
        param1Graphics2D.setPaint(getGradient(i * 2.0F + j, 0.0F, param1Color1, param1Int1, 0.0F, param1Color3));
        param1Graphics2D.fillRect(i * 2 + j, 0, param1Int1 - i * 2 - j, param1Int2);
      } 
    }
    
    private GradientPaint getGradient(float param1Float1, float param1Float2, Color param1Color1, float param1Float3, float param1Float4, Color param1Color2) { return new GradientPaint(param1Float1, param1Float2, param1Color1, param1Float3, param1Float4, param1Color2, true); }
  }
  
  private static class OceanDisabledButtonImageFilter extends RGBImageFilter {
    private float min;
    
    private float factor;
    
    OceanDisabledButtonImageFilter(int param1Int1, int param1Int2) {
      this.min = param1Int1;
      this.factor = (param1Int2 - param1Int1) / 255.0F;
    }
    
    public int filterRGB(int param1Int1, int param1Int2, int param1Int3) {
      int i = Math.min(255, (int)((0.2125F * (param1Int3 >> 16 & 0xFF) + 0.7154F * (param1Int3 >> 8 & 0xFF) + 0.0721F * (param1Int3 & 0xFF) + 0.5F) * this.factor + this.min));
      return param1Int3 & 0xFF000000 | i << 16 | i << 8 | i << 0;
    }
  }
  
  private static class OceanToolBarImageFilter extends RGBImageFilter {
    public int filterRGB(int param1Int1, int param1Int2, int param1Int3) {
      int i = param1Int3 >> 16 & 0xFF;
      int j = param1Int3 >> 8 & 0xFF;
      int k = param1Int3 & 0xFF;
      int m = Math.max(Math.max(i, j), k);
      return param1Int3 & 0xFF000000 | m << 16 | m << 8 | m << 0;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */