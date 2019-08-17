package sun.awt.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.peer.PanelPeer;
import sun.awt.SunGraphicsCallback;

class WPanelPeer extends WCanvasPeer implements PanelPeer {
  Insets insets_;
  
  public void paint(Graphics paramGraphics) {
    super.paint(paramGraphics);
    SunGraphicsCallback.PaintHeavyweightComponentsCallback.getInstance().runComponents(((Container)this.target).getComponents(), paramGraphics, 3);
  }
  
  public void print(Graphics paramGraphics) {
    super.print(paramGraphics);
    SunGraphicsCallback.PrintHeavyweightComponentsCallback.getInstance().runComponents(((Container)this.target).getComponents(), paramGraphics, 3);
  }
  
  public Insets getInsets() { return this.insets_; }
  
  private static native void initIDs();
  
  WPanelPeer(Component paramComponent) { super(paramComponent); }
  
  void initialize() {
    super.initialize();
    this.insets_ = new Insets(0, 0, 0, 0);
    Color color = ((Component)this.target).getBackground();
    if (color == null) {
      color = WColor.getDefaultColor(1);
      ((Component)this.target).setBackground(color);
      setBackground(color);
    } 
    color = ((Component)this.target).getForeground();
    if (color == null) {
      color = WColor.getDefaultColor(2);
      ((Component)this.target).setForeground(color);
      setForeground(color);
    } 
  }
  
  public Insets insets() { return getInsets(); }
  
  static  {
    initIDs();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WPanelPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */