package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import sun.awt.AppContext;

class MetalBumps implements Icon {
  static final Color ALPHA = new Color(0, 0, 0, 0);
  
  protected int xBumps;
  
  protected int yBumps;
  
  protected Color topColor;
  
  protected Color shadowColor;
  
  protected Color backColor;
  
  private static final Object METAL_BUMPS = new Object();
  
  protected BumpBuffer buffer;
  
  public MetalBumps(int paramInt1, int paramInt2, Color paramColor1, Color paramColor2, Color paramColor3) {
    setBumpArea(paramInt1, paramInt2);
    setBumpColors(paramColor1, paramColor2, paramColor3);
  }
  
  private static BumpBuffer createBuffer(GraphicsConfiguration paramGraphicsConfiguration, Color paramColor1, Color paramColor2, Color paramColor3) {
    AppContext appContext = AppContext.getAppContext();
    List list = (List)appContext.get(METAL_BUMPS);
    if (list == null) {
      list = new ArrayList();
      appContext.put(METAL_BUMPS, list);
    } 
    for (BumpBuffer bumpBuffer1 : list) {
      if (bumpBuffer1.hasSameConfiguration(paramGraphicsConfiguration, paramColor1, paramColor2, paramColor3))
        return bumpBuffer1; 
    } 
    BumpBuffer bumpBuffer = new BumpBuffer(paramGraphicsConfiguration, paramColor1, paramColor2, paramColor3);
    list.add(bumpBuffer);
    return bumpBuffer;
  }
  
  public void setBumpArea(Dimension paramDimension) { setBumpArea(paramDimension.width, paramDimension.height); }
  
  public void setBumpArea(int paramInt1, int paramInt2) {
    this.xBumps = paramInt1 / 2;
    this.yBumps = paramInt2 / 2;
  }
  
  public void setBumpColors(Color paramColor1, Color paramColor2, Color paramColor3) {
    this.topColor = paramColor1;
    this.shadowColor = paramColor2;
    if (paramColor3 == null) {
      this.backColor = ALPHA;
    } else {
      this.backColor = paramColor3;
    } 
  }
  
  public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2) {
    GraphicsConfiguration graphicsConfiguration = (paramGraphics instanceof Graphics2D) ? ((Graphics2D)paramGraphics).getDeviceConfiguration() : null;
    if (this.buffer == null || !this.buffer.hasSameConfiguration(graphicsConfiguration, this.topColor, this.shadowColor, this.backColor))
      this.buffer = createBuffer(graphicsConfiguration, this.topColor, this.shadowColor, this.backColor); 
    int i = 64;
    int j = 64;
    int k = getIconWidth();
    int m = getIconHeight();
    int n = paramInt1 + k;
    int i1 = paramInt2 + m;
    int i2 = paramInt1;
    while (paramInt2 < i1) {
      int i3 = Math.min(i1 - paramInt2, j);
      for (paramInt1 = i2; paramInt1 < n; paramInt1 += i) {
        int i4 = Math.min(n - paramInt1, i);
        paramGraphics.drawImage(this.buffer.getImage(), paramInt1, paramInt2, paramInt1 + i4, paramInt2 + i3, 0, 0, i4, i3, null);
      } 
      paramInt2 += j;
    } 
  }
  
  public int getIconWidth() { return this.xBumps * 2; }
  
  public int getIconHeight() { return this.yBumps * 2; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalBumps.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */