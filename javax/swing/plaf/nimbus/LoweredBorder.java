package javax.swing.plaf.nimbus;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.border.Border;

class LoweredBorder extends AbstractRegionPainter implements Border {
  private static final int IMG_SIZE = 30;
  
  private static final int RADIUS = 13;
  
  private static final Insets INSETS = new Insets(10, 10, 10, 10);
  
  private static final AbstractRegionPainter.PaintContext PAINT_CONTEXT = new AbstractRegionPainter.PaintContext(INSETS, new Dimension(30, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.147483647E9D, 2.147483647E9D);
  
  protected Object[] getExtendedCacheKeys(JComponent paramJComponent) {
    new Object[1][0] = paramJComponent.getBackground();
    return (paramJComponent != null) ? new Object[1] : null;
  }
  
  protected void doPaint(Graphics2D paramGraphics2D, JComponent paramJComponent, int paramInt1, int paramInt2, Object[] paramArrayOfObject) {
    Color color = (paramJComponent == null) ? Color.BLACK : paramJComponent.getBackground();
    BufferedImage bufferedImage1 = new BufferedImage(30, 30, 2);
    BufferedImage bufferedImage2 = new BufferedImage(30, 30, 2);
    Graphics2D graphics2D = (Graphics2D)bufferedImage1.getGraphics();
    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    graphics2D.setColor(color);
    graphics2D.fillRoundRect(2, 0, 26, 26, 13, 13);
    graphics2D.dispose();
    InnerShadowEffect innerShadowEffect = new InnerShadowEffect();
    innerShadowEffect.setDistance(1);
    innerShadowEffect.setSize(3);
    innerShadowEffect.setColor(getLighter(color, 2.1F));
    innerShadowEffect.setAngle(90);
    innerShadowEffect.applyEffect(bufferedImage1, bufferedImage2, 30, 30);
    graphics2D = (Graphics2D)bufferedImage2.getGraphics();
    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    graphics2D.setClip(0, 28, 30, 1);
    graphics2D.setColor(getLighter(color, 0.9F));
    graphics2D.drawRoundRect(2, 1, 25, 25, 13, 13);
    graphics2D.dispose();
    if (paramInt1 != 30 || paramInt2 != 30) {
      ImageScalingHelper.paint(paramGraphics2D, 0, 0, paramInt1, paramInt2, bufferedImage2, INSETS, INSETS, ImageScalingHelper.PaintType.PAINT9_STRETCH, 512);
    } else {
      paramGraphics2D.drawImage(bufferedImage2, 0, 0, paramJComponent);
    } 
    bufferedImage1 = null;
    bufferedImage2 = null;
  }
  
  protected AbstractRegionPainter.PaintContext getPaintContext() { return PAINT_CONTEXT; }
  
  public Insets getBorderInsets(Component paramComponent) { return (Insets)INSETS.clone(); }
  
  public boolean isBorderOpaque() { return false; }
  
  public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    JComponent jComponent = (paramComponent instanceof JComponent) ? (JComponent)paramComponent : null;
    if (paramGraphics instanceof Graphics2D) {
      Graphics2D graphics2D = (Graphics2D)paramGraphics;
      graphics2D.translate(paramInt1, paramInt2);
      paint(graphics2D, jComponent, paramInt3, paramInt4);
      graphics2D.translate(-paramInt1, -paramInt2);
    } else {
      BufferedImage bufferedImage = new BufferedImage(30, 30, 2);
      Graphics2D graphics2D = (Graphics2D)bufferedImage.getGraphics();
      paint(graphics2D, jComponent, paramInt3, paramInt4);
      graphics2D.dispose();
      ImageScalingHelper.paint(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, bufferedImage, INSETS, INSETS, ImageScalingHelper.PaintType.PAINT9_STRETCH, 512);
    } 
  }
  
  private Color getLighter(Color paramColor, float paramFloat) { return new Color(Math.min((int)(paramColor.getRed() / paramFloat), 255), Math.min((int)(paramColor.getGreen() / paramFloat), 255), Math.min((int)(paramColor.getBlue() / paramFloat), 255)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\LoweredBorder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */