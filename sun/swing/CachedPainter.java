package sun.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.util.HashMap;
import java.util.Map;

public abstract class CachedPainter {
  private static final Map<Object, ImageCache> cacheMap = new HashMap();
  
  private static ImageCache getCache(Object paramObject) {
    synchronized (CachedPainter.class) {
      ImageCache imageCache = (ImageCache)cacheMap.get(paramObject);
      if (imageCache == null) {
        imageCache = new ImageCache(1);
        cacheMap.put(paramObject, imageCache);
      } 
      return imageCache;
    } 
  }
  
  public CachedPainter(int paramInt) { getCache(getClass()).setMaxCount(paramInt); }
  
  public void paint(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object... paramVarArgs) {
    if (paramInt3 <= 0 || paramInt4 <= 0)
      return; 
    synchronized (CachedPainter.class) {
      paint0(paramComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramVarArgs);
    } 
  }
  
  private void paint0(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object... paramVarArgs) {
    Class clazz = getClass();
    GraphicsConfiguration graphicsConfiguration = getGraphicsConfiguration(paramComponent);
    ImageCache imageCache = getCache(clazz);
    Image image = imageCache.getImage(clazz, graphicsConfiguration, paramInt3, paramInt4, paramVarArgs);
    byte b = 0;
    do {
      boolean bool = false;
      if (image instanceof VolatileImage)
        switch (((VolatileImage)image).validate(graphicsConfiguration)) {
          case 2:
            ((VolatileImage)image).flush();
            image = null;
            break;
          case 1:
            bool = true;
            break;
        }  
      if (image == null) {
        image = createImage(paramComponent, paramInt3, paramInt4, graphicsConfiguration, paramVarArgs);
        imageCache.setImage(clazz, graphicsConfiguration, paramInt3, paramInt4, paramVarArgs, image);
        bool = true;
      } 
      if (bool) {
        Graphics graphics = image.getGraphics();
        paintToImage(paramComponent, image, graphics, paramInt3, paramInt4, paramVarArgs);
        graphics.dispose();
      } 
      paintImage(paramComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, image, paramVarArgs);
    } while (image instanceof VolatileImage && ((VolatileImage)image).contentsLost() && ++b < 3);
  }
  
  protected abstract void paintToImage(Component paramComponent, Image paramImage, Graphics paramGraphics, int paramInt1, int paramInt2, Object[] paramArrayOfObject);
  
  protected void paintImage(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Image paramImage, Object[] paramArrayOfObject) { paramGraphics.drawImage(paramImage, paramInt1, paramInt2, null); }
  
  protected Image createImage(Component paramComponent, int paramInt1, int paramInt2, GraphicsConfiguration paramGraphicsConfiguration, Object[] paramArrayOfObject) { return (paramGraphicsConfiguration == null) ? new BufferedImage(paramInt1, paramInt2, 1) : paramGraphicsConfiguration.createCompatibleVolatileImage(paramInt1, paramInt2); }
  
  protected void flush() {
    synchronized (CachedPainter.class) {
      getCache(getClass()).flush();
    } 
  }
  
  private GraphicsConfiguration getGraphicsConfiguration(Component paramComponent) { return (paramComponent == null) ? null : paramComponent.getGraphicsConfiguration(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\swing\CachedPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */