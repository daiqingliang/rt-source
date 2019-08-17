package sun.awt.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.peer.CanvasPeer;
import sun.awt.PaintEventDispatcher;
import sun.awt.SunToolkit;

class WCanvasPeer extends WComponentPeer implements CanvasPeer {
  private boolean eraseBackground;
  
  WCanvasPeer(Component paramComponent) { super(paramComponent); }
  
  native void create(WComponentPeer paramWComponentPeer);
  
  void initialize() {
    this.eraseBackground = !SunToolkit.getSunAwtNoerasebackground();
    boolean bool = SunToolkit.getSunAwtErasebackgroundonresize();
    if (!PaintEventDispatcher.getPaintEventDispatcher().shouldDoNativeBackgroundErase((Component)this.target))
      this.eraseBackground = false; 
    setNativeBackgroundErase(this.eraseBackground, bool);
    super.initialize();
    Color color = ((Component)this.target).getBackground();
    if (color != null)
      setBackground(color); 
  }
  
  public void paint(Graphics paramGraphics) {
    Dimension dimension = ((Component)this.target).getSize();
    if (paramGraphics instanceof java.awt.Graphics2D || paramGraphics instanceof sun.awt.Graphics2Delegate) {
      paramGraphics.clearRect(0, 0, dimension.width, dimension.height);
    } else {
      paramGraphics.setColor(((Component)this.target).getBackground());
      paramGraphics.fillRect(0, 0, dimension.width, dimension.height);
      paramGraphics.setColor(((Component)this.target).getForeground());
    } 
    super.paint(paramGraphics);
  }
  
  public boolean shouldClearRectBeforePaint() { return this.eraseBackground; }
  
  void disableBackgroundErase() {
    this.eraseBackground = false;
    setNativeBackgroundErase(false, false);
  }
  
  private native void setNativeBackgroundErase(boolean paramBoolean1, boolean paramBoolean2);
  
  public GraphicsConfiguration getAppropriateGraphicsConfiguration(GraphicsConfiguration paramGraphicsConfiguration) { return paramGraphicsConfiguration; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WCanvasPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */