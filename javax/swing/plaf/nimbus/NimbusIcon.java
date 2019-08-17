package javax.swing.plaf.nimbus;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.Painter;
import javax.swing.UIManager;
import javax.swing.plaf.synth.SynthContext;
import sun.swing.plaf.synth.SynthIcon;

class NimbusIcon extends SynthIcon {
  private int width;
  
  private int height;
  
  private String prefix;
  
  private String key;
  
  NimbusIcon(String paramString1, String paramString2, int paramInt1, int paramInt2) {
    this.width = paramInt1;
    this.height = paramInt2;
    this.prefix = paramString1;
    this.key = paramString2;
  }
  
  public void paintIcon(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    Painter painter = null;
    if (paramSynthContext != null)
      painter = (Painter)paramSynthContext.getStyle().get(paramSynthContext, this.key); 
    if (painter == null)
      painter = (Painter)UIManager.get(this.prefix + "[Enabled]." + this.key); 
    if (painter != null && paramSynthContext != null) {
      JComponent jComponent = paramSynthContext.getComponent();
      boolean bool1 = false;
      boolean bool2 = false;
      byte b1 = 0;
      byte b2 = 0;
      if (jComponent instanceof JToolBar) {
        JToolBar jToolBar = (JToolBar)jComponent;
        bool1 = (jToolBar.getOrientation() == 1) ? 1 : 0;
        bool2 = !jToolBar.getComponentOrientation().isLeftToRight() ? 1 : 0;
        Object object = NimbusLookAndFeel.resolveToolbarConstraint(jToolBar);
        if (jToolBar.getBorder() instanceof javax.swing.plaf.UIResource)
          if (object == "South") {
            b2 = 1;
          } else if (object == "East") {
            b1 = 1;
          }  
      } else if (jComponent instanceof javax.swing.JMenu) {
        bool2 = !jComponent.getComponentOrientation().isLeftToRight() ? 1 : 0;
      } 
      if (paramGraphics instanceof Graphics2D) {
        Graphics2D graphics2D = (Graphics2D)paramGraphics;
        graphics2D.translate(paramInt1, paramInt2);
        graphics2D.translate(b1, b2);
        if (bool1) {
          graphics2D.rotate(Math.toRadians(90.0D));
          graphics2D.translate(0, -paramInt3);
          painter.paint(graphics2D, paramSynthContext.getComponent(), paramInt4, paramInt3);
          graphics2D.translate(0, paramInt3);
          graphics2D.rotate(Math.toRadians(-90.0D));
        } else if (bool2) {
          graphics2D.scale(-1.0D, 1.0D);
          graphics2D.translate(-paramInt3, 0);
          painter.paint(graphics2D, paramSynthContext.getComponent(), paramInt3, paramInt4);
          graphics2D.translate(paramInt3, 0);
          graphics2D.scale(-1.0D, 1.0D);
        } else {
          painter.paint(graphics2D, paramSynthContext.getComponent(), paramInt3, paramInt4);
        } 
        graphics2D.translate(-b1, -b2);
        graphics2D.translate(-paramInt1, -paramInt2);
      } else {
        BufferedImage bufferedImage = new BufferedImage(paramInt3, paramInt4, 2);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        if (bool1) {
          graphics2D.rotate(Math.toRadians(90.0D));
          graphics2D.translate(0, -paramInt3);
          painter.paint(graphics2D, paramSynthContext.getComponent(), paramInt4, paramInt3);
        } else if (bool2) {
          graphics2D.scale(-1.0D, 1.0D);
          graphics2D.translate(-paramInt3, 0);
          painter.paint(graphics2D, paramSynthContext.getComponent(), paramInt3, paramInt4);
        } else {
          painter.paint(graphics2D, paramSynthContext.getComponent(), paramInt3, paramInt4);
        } 
        graphics2D.dispose();
        paramGraphics.drawImage(bufferedImage, paramInt1, paramInt2, null);
        bufferedImage = null;
      } 
    } 
  }
  
  public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2) {
    Painter painter = (Painter)UIManager.get(this.prefix + "[Enabled]." + this.key);
    if (painter != null) {
      JComponent jComponent = (paramComponent instanceof JComponent) ? (JComponent)paramComponent : null;
      Graphics2D graphics2D = (Graphics2D)paramGraphics;
      graphics2D.translate(paramInt1, paramInt2);
      painter.paint(graphics2D, jComponent, this.width, this.height);
      graphics2D.translate(-paramInt1, -paramInt2);
    } 
  }
  
  public int getIconWidth(SynthContext paramSynthContext) {
    if (paramSynthContext == null)
      return this.width; 
    JComponent jComponent = paramSynthContext.getComponent();
    return (jComponent instanceof JToolBar && ((JToolBar)jComponent).getOrientation() == 1) ? ((jComponent.getBorder() instanceof javax.swing.plaf.UIResource) ? (jComponent.getWidth() - 1) : jComponent.getWidth()) : scale(paramSynthContext, this.width);
  }
  
  public int getIconHeight(SynthContext paramSynthContext) {
    if (paramSynthContext == null)
      return this.height; 
    JComponent jComponent = paramSynthContext.getComponent();
    if (jComponent instanceof JToolBar) {
      JToolBar jToolBar = (JToolBar)jComponent;
      return (jToolBar.getOrientation() == 0) ? ((jToolBar.getBorder() instanceof javax.swing.plaf.UIResource) ? (jComponent.getHeight() - 1) : jComponent.getHeight()) : scale(paramSynthContext, this.width);
    } 
    return scale(paramSynthContext, this.height);
  }
  
  private int scale(SynthContext paramSynthContext, int paramInt) {
    if (paramSynthContext == null || paramSynthContext.getComponent() == null)
      return paramInt; 
    String str = (String)paramSynthContext.getComponent().getClientProperty("JComponent.sizeVariant");
    if (str != null)
      if ("large".equals(str)) {
        paramInt = (int)(paramInt * 1.15D);
      } else if ("small".equals(str)) {
        paramInt = (int)(paramInt * 0.857D);
      } else if ("mini".equals(str)) {
        paramInt = (int)(paramInt * 0.784D);
      }  
    return paramInt;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\NimbusIcon.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */