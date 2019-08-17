package java.awt.dnd;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public abstract class MouseDragGestureRecognizer extends DragGestureRecognizer implements MouseListener, MouseMotionListener {
  private static final long serialVersionUID = 6220099344182281120L;
  
  protected MouseDragGestureRecognizer(DragSource paramDragSource, Component paramComponent, int paramInt, DragGestureListener paramDragGestureListener) { super(paramDragSource, paramComponent, paramInt, paramDragGestureListener); }
  
  protected MouseDragGestureRecognizer(DragSource paramDragSource, Component paramComponent, int paramInt) { this(paramDragSource, paramComponent, paramInt, null); }
  
  protected MouseDragGestureRecognizer(DragSource paramDragSource, Component paramComponent) { this(paramDragSource, paramComponent, 0); }
  
  protected MouseDragGestureRecognizer(DragSource paramDragSource) { this(paramDragSource, null); }
  
  protected void registerListeners() {
    this.component.addMouseListener(this);
    this.component.addMouseMotionListener(this);
  }
  
  protected void unregisterListeners() {
    this.component.removeMouseListener(this);
    this.component.removeMouseMotionListener(this);
  }
  
  public void mouseClicked(MouseEvent paramMouseEvent) {}
  
  public void mousePressed(MouseEvent paramMouseEvent) {}
  
  public void mouseReleased(MouseEvent paramMouseEvent) {}
  
  public void mouseEntered(MouseEvent paramMouseEvent) {}
  
  public void mouseExited(MouseEvent paramMouseEvent) {}
  
  public void mouseDragged(MouseEvent paramMouseEvent) {}
  
  public void mouseMoved(MouseEvent paramMouseEvent) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\dnd\MouseDragGestureRecognizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */