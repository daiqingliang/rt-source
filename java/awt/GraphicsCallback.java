package java.awt;

import sun.awt.SunGraphicsCallback;

abstract class GraphicsCallback extends SunGraphicsCallback {
  static final class PaintAllCallback extends GraphicsCallback {
    private static PaintAllCallback instance = new PaintAllCallback();
    
    public void run(Component param1Component, Graphics param1Graphics) { param1Component.paintAll(param1Graphics); }
    
    static PaintAllCallback getInstance() { return instance; }
  }
  
  static final class PaintCallback extends GraphicsCallback {
    private static PaintCallback instance = new PaintCallback();
    
    public void run(Component param1Component, Graphics param1Graphics) { param1Component.paint(param1Graphics); }
    
    static PaintCallback getInstance() { return instance; }
  }
  
  static final class PaintHeavyweightComponentsCallback extends GraphicsCallback {
    private static PaintHeavyweightComponentsCallback instance = new PaintHeavyweightComponentsCallback();
    
    public void run(Component param1Component, Graphics param1Graphics) {
      if (param1Component.peer instanceof java.awt.peer.LightweightPeer) {
        param1Component.paintHeavyweightComponents(param1Graphics);
      } else {
        param1Component.paintAll(param1Graphics);
      } 
    }
    
    static PaintHeavyweightComponentsCallback getInstance() { return instance; }
  }
  
  static final class PeerPaintCallback extends GraphicsCallback {
    private static PeerPaintCallback instance = new PeerPaintCallback();
    
    public void run(Component param1Component, Graphics param1Graphics) {
      param1Component.validate();
      if (param1Component.peer instanceof java.awt.peer.LightweightPeer) {
        param1Component.lightweightPaint(param1Graphics);
      } else {
        param1Component.peer.paint(param1Graphics);
      } 
    }
    
    static PeerPaintCallback getInstance() { return instance; }
  }
  
  static final class PeerPrintCallback extends GraphicsCallback {
    private static PeerPrintCallback instance = new PeerPrintCallback();
    
    public void run(Component param1Component, Graphics param1Graphics) {
      param1Component.validate();
      if (param1Component.peer instanceof java.awt.peer.LightweightPeer) {
        param1Component.lightweightPrint(param1Graphics);
      } else {
        param1Component.peer.print(param1Graphics);
      } 
    }
    
    static PeerPrintCallback getInstance() { return instance; }
  }
  
  static final class PrintAllCallback extends GraphicsCallback {
    private static PrintAllCallback instance = new PrintAllCallback();
    
    public void run(Component param1Component, Graphics param1Graphics) { param1Component.printAll(param1Graphics); }
    
    static PrintAllCallback getInstance() { return instance; }
  }
  
  static final class PrintCallback extends GraphicsCallback {
    private static PrintCallback instance = new PrintCallback();
    
    public void run(Component param1Component, Graphics param1Graphics) { param1Component.print(param1Graphics); }
    
    static PrintCallback getInstance() { return instance; }
  }
  
  static final class PrintHeavyweightComponentsCallback extends GraphicsCallback {
    private static PrintHeavyweightComponentsCallback instance = new PrintHeavyweightComponentsCallback();
    
    public void run(Component param1Component, Graphics param1Graphics) {
      if (param1Component.peer instanceof java.awt.peer.LightweightPeer) {
        param1Component.printHeavyweightComponents(param1Graphics);
      } else {
        param1Component.printAll(param1Graphics);
      } 
    }
    
    static PrintHeavyweightComponentsCallback getInstance() { return instance; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\GraphicsCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */