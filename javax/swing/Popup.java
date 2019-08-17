package javax.swing;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import sun.awt.ModalExclude;

public class Popup {
  private Component component;
  
  protected Popup(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2) {
    this();
    if (paramComponent2 == null)
      throw new IllegalArgumentException("Contents must be non-null"); 
    reset(paramComponent1, paramComponent2, paramInt1, paramInt2);
  }
  
  protected Popup() {}
  
  public void show() {
    Component component1 = getComponent();
    if (component1 != null)
      component1.show(); 
  }
  
  public void hide() {
    Component component1 = getComponent();
    if (component1 instanceof JWindow) {
      component1.hide();
      ((JWindow)component1).getContentPane().removeAll();
    } 
    dispose();
  }
  
  void dispose() {
    Component component1 = getComponent();
    Window window = SwingUtilities.getWindowAncestor(component1);
    if (component1 instanceof JWindow) {
      ((Window)component1).dispose();
      component1 = null;
    } 
    if (window instanceof DefaultFrame)
      window.dispose(); 
  }
  
  void reset(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2) {
    if (getComponent() == null)
      this.component = createComponent(paramComponent1); 
    Component component1 = getComponent();
    if (component1 instanceof JWindow) {
      JWindow jWindow = (JWindow)getComponent();
      jWindow.setLocation(paramInt1, paramInt2);
      jWindow.getContentPane().add(paramComponent2, "Center");
      jWindow.invalidate();
      jWindow.validate();
      if (jWindow.isVisible())
        pack(); 
    } 
  }
  
  void pack() {
    Component component1 = getComponent();
    if (component1 instanceof Window)
      ((Window)component1).pack(); 
  }
  
  private Window getParentWindow(Component paramComponent) {
    Window window = null;
    if (paramComponent instanceof Window) {
      window = (Window)paramComponent;
    } else if (paramComponent != null) {
      window = SwingUtilities.getWindowAncestor(paramComponent);
    } 
    if (window == null)
      window = new DefaultFrame(); 
    return window;
  }
  
  Component createComponent(Component paramComponent) { return GraphicsEnvironment.isHeadless() ? null : new HeavyWeightWindow(getParentWindow(paramComponent)); }
  
  Component getComponent() { return this.component; }
  
  static class DefaultFrame extends Frame {}
  
  static class HeavyWeightWindow extends JWindow implements ModalExclude {
    HeavyWeightWindow(Window param1Window) {
      super(param1Window);
      setFocusableWindowState(false);
      setType(Window.Type.POPUP);
      getRootPane().setUseTrueDoubleBuffering(false);
      try {
        setAlwaysOnTop(true);
      } catch (SecurityException securityException) {}
    }
    
    public void update(Graphics param1Graphics) { paint(param1Graphics); }
    
    public void show() {
      pack();
      if (getWidth() > 0 && getHeight() > 0)
        super.show(); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\Popup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */