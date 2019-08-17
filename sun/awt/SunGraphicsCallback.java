package sun.awt;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import sun.util.logging.PlatformLogger;

public abstract class SunGraphicsCallback {
  public static final int HEAVYWEIGHTS = 1;
  
  public static final int LIGHTWEIGHTS = 2;
  
  public static final int TWO_PASSES = 4;
  
  private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.SunGraphicsCallback");
  
  public abstract void run(Component paramComponent, Graphics paramGraphics);
  
  protected void constrainGraphics(Graphics paramGraphics, Rectangle paramRectangle) {
    if (paramGraphics instanceof ConstrainableGraphics) {
      ((ConstrainableGraphics)paramGraphics).constrain(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
    } else {
      paramGraphics.translate(paramRectangle.x, paramRectangle.y);
    } 
    paramGraphics.clipRect(0, 0, paramRectangle.width, paramRectangle.height);
  }
  
  public final void runOneComponent(Component paramComponent, Rectangle paramRectangle, Graphics paramGraphics, Shape paramShape, int paramInt) {
    if (paramComponent == null || paramComponent.getPeer() == null || !paramComponent.isVisible())
      return; 
    boolean bool = paramComponent.isLightweight();
    if ((bool && (paramInt & 0x2) == 0) || (!bool && (paramInt & true) == 0))
      return; 
    if (paramRectangle == null)
      paramRectangle = paramComponent.getBounds(); 
    if (paramShape == null || paramShape.intersects(paramRectangle)) {
      graphics = paramGraphics.create();
      try {
        constrainGraphics(graphics, paramRectangle);
        graphics.setFont(paramComponent.getFont());
        graphics.setColor(paramComponent.getForeground());
        if (graphics instanceof Graphics2D) {
          ((Graphics2D)graphics).setBackground(paramComponent.getBackground());
        } else if (graphics instanceof Graphics2Delegate) {
          ((Graphics2Delegate)graphics).setBackground(paramComponent.getBackground());
        } 
        run(paramComponent, graphics);
      } finally {
        graphics.dispose();
      } 
    } 
  }
  
  public final void runComponents(Component[] paramArrayOfComponent, Graphics paramGraphics, int paramInt) {
    int i = paramArrayOfComponent.length;
    Shape shape = paramGraphics.getClip();
    if (log.isLoggable(PlatformLogger.Level.FINER) && shape != null) {
      Rectangle rectangle = shape.getBounds();
      log.finer("x = " + rectangle.x + ", y = " + rectangle.y + ", width = " + rectangle.width + ", height = " + rectangle.height);
    } 
    if ((paramInt & 0x4) != 0) {
      int j;
      for (j = i - 1; j >= 0; j--)
        runOneComponent(paramArrayOfComponent[j], null, paramGraphics, shape, 2); 
      for (j = i - 1; j >= 0; j--)
        runOneComponent(paramArrayOfComponent[j], null, paramGraphics, shape, 1); 
    } else {
      for (int j = i - 1; j >= 0; j--)
        runOneComponent(paramArrayOfComponent[j], null, paramGraphics, shape, paramInt); 
    } 
  }
  
  public static final class PaintHeavyweightComponentsCallback extends SunGraphicsCallback {
    private static PaintHeavyweightComponentsCallback instance = new PaintHeavyweightComponentsCallback();
    
    public void run(Component param1Component, Graphics param1Graphics) {
      if (!param1Component.isLightweight()) {
        param1Component.paintAll(param1Graphics);
      } else if (param1Component instanceof Container) {
        runComponents(((Container)param1Component).getComponents(), param1Graphics, 3);
      } 
    }
    
    public static PaintHeavyweightComponentsCallback getInstance() { return instance; }
  }
  
  public static final class PrintHeavyweightComponentsCallback extends SunGraphicsCallback {
    private static PrintHeavyweightComponentsCallback instance = new PrintHeavyweightComponentsCallback();
    
    public void run(Component param1Component, Graphics param1Graphics) {
      if (!param1Component.isLightweight()) {
        param1Component.printAll(param1Graphics);
      } else if (param1Component instanceof Container) {
        runComponents(((Container)param1Component).getComponents(), param1Graphics, 3);
      } 
    }
    
    public static PrintHeavyweightComponentsCallback getInstance() { return instance; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\SunGraphicsCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */