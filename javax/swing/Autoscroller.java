package javax.swing;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import sun.awt.AWTAccessor;

class Autoscroller implements ActionListener {
  private static Autoscroller sharedInstance = new Autoscroller();
  
  private static MouseEvent event;
  
  private static Timer timer;
  
  private static JComponent component;
  
  public static void stop(JComponent paramJComponent) { sharedInstance._stop(paramJComponent); }
  
  public static boolean isRunning(JComponent paramJComponent) { return sharedInstance._isRunning(paramJComponent); }
  
  public static void processMouseDragged(MouseEvent paramMouseEvent) { sharedInstance._processMouseDragged(paramMouseEvent); }
  
  private void start(JComponent paramJComponent, MouseEvent paramMouseEvent) {
    Point point = paramJComponent.getLocationOnScreen();
    if (component != paramJComponent)
      _stop(component); 
    component = paramJComponent;
    event = new MouseEvent(component, paramMouseEvent.getID(), paramMouseEvent.getWhen(), paramMouseEvent.getModifiers(), paramMouseEvent.getX() + point.x, paramMouseEvent.getY() + point.y, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), 0);
    AWTAccessor.MouseEventAccessor mouseEventAccessor = AWTAccessor.getMouseEventAccessor();
    mouseEventAccessor.setCausedByTouchEvent(event, mouseEventAccessor.isCausedByTouchEvent(paramMouseEvent));
    if (timer == null)
      timer = new Timer(100, this); 
    if (!timer.isRunning())
      timer.start(); 
  }
  
  private void _stop(JComponent paramJComponent) {
    if (component == paramJComponent) {
      if (timer != null)
        timer.stop(); 
      timer = null;
      event = null;
      component = null;
    } 
  }
  
  private boolean _isRunning(JComponent paramJComponent) { return (paramJComponent == component && timer != null && timer.isRunning()); }
  
  private void _processMouseDragged(MouseEvent paramMouseEvent) {
    JComponent jComponent = (JComponent)paramMouseEvent.getComponent();
    boolean bool = true;
    if (jComponent.isShowing()) {
      Rectangle rectangle = jComponent.getVisibleRect();
      bool = rectangle.contains(paramMouseEvent.getX(), paramMouseEvent.getY());
    } 
    if (bool) {
      _stop(jComponent);
    } else {
      start(jComponent, paramMouseEvent);
    } 
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    JComponent jComponent = component;
    if (jComponent == null || !jComponent.isShowing() || event == null) {
      _stop(jComponent);
      return;
    } 
    Point point = jComponent.getLocationOnScreen();
    MouseEvent mouseEvent = new MouseEvent(jComponent, event.getID(), event.getWhen(), event.getModifiers(), event.getX() - point.x, event.getY() - point.y, event.getXOnScreen(), event.getYOnScreen(), event.getClickCount(), event.isPopupTrigger(), 0);
    AWTAccessor.MouseEventAccessor mouseEventAccessor = AWTAccessor.getMouseEventAccessor();
    mouseEventAccessor.setCausedByTouchEvent(mouseEvent, mouseEventAccessor.isCausedByTouchEvent(event));
    jComponent.superProcessMouseMotionEvent(mouseEvent);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\Autoscroller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */