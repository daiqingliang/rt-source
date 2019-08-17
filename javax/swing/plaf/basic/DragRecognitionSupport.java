package javax.swing.plaf.basic;

import java.awt.dnd.DragSource;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import sun.awt.AppContext;
import sun.awt.dnd.SunDragSourceContextPeer;

class DragRecognitionSupport {
  private int motionThreshold;
  
  private MouseEvent dndArmedEvent;
  
  private JComponent component;
  
  private static DragRecognitionSupport getDragRecognitionSupport() {
    DragRecognitionSupport dragRecognitionSupport = (DragRecognitionSupport)AppContext.getAppContext().get(DragRecognitionSupport.class);
    if (dragRecognitionSupport == null) {
      dragRecognitionSupport = new DragRecognitionSupport();
      AppContext.getAppContext().put(DragRecognitionSupport.class, dragRecognitionSupport);
    } 
    return dragRecognitionSupport;
  }
  
  public static boolean mousePressed(MouseEvent paramMouseEvent) { return getDragRecognitionSupport().mousePressedImpl(paramMouseEvent); }
  
  public static MouseEvent mouseReleased(MouseEvent paramMouseEvent) { return getDragRecognitionSupport().mouseReleasedImpl(paramMouseEvent); }
  
  public static boolean mouseDragged(MouseEvent paramMouseEvent, BeforeDrag paramBeforeDrag) { return getDragRecognitionSupport().mouseDraggedImpl(paramMouseEvent, paramBeforeDrag); }
  
  private void clearState() {
    this.dndArmedEvent = null;
    this.component = null;
  }
  
  private int mapDragOperationFromModifiers(MouseEvent paramMouseEvent, TransferHandler paramTransferHandler) { return (paramTransferHandler == null || !SwingUtilities.isLeftMouseButton(paramMouseEvent)) ? 0 : SunDragSourceContextPeer.convertModifiersToDropAction(paramMouseEvent.getModifiersEx(), paramTransferHandler.getSourceActions(this.component)); }
  
  private boolean mousePressedImpl(MouseEvent paramMouseEvent) {
    this.component = (JComponent)paramMouseEvent.getSource();
    if (mapDragOperationFromModifiers(paramMouseEvent, this.component.getTransferHandler()) != 0) {
      this.motionThreshold = DragSource.getDragThreshold();
      this.dndArmedEvent = paramMouseEvent;
      return true;
    } 
    clearState();
    return false;
  }
  
  private MouseEvent mouseReleasedImpl(MouseEvent paramMouseEvent) {
    if (this.dndArmedEvent == null)
      return null; 
    MouseEvent mouseEvent = null;
    if (paramMouseEvent.getSource() == this.component)
      mouseEvent = this.dndArmedEvent; 
    clearState();
    return mouseEvent;
  }
  
  private boolean mouseDraggedImpl(MouseEvent paramMouseEvent, BeforeDrag paramBeforeDrag) {
    if (this.dndArmedEvent == null)
      return false; 
    if (paramMouseEvent.getSource() != this.component) {
      clearState();
      return false;
    } 
    int i = Math.abs(paramMouseEvent.getX() - this.dndArmedEvent.getX());
    int j = Math.abs(paramMouseEvent.getY() - this.dndArmedEvent.getY());
    if (i > this.motionThreshold || j > this.motionThreshold) {
      TransferHandler transferHandler = this.component.getTransferHandler();
      int k = mapDragOperationFromModifiers(paramMouseEvent, transferHandler);
      if (k != 0) {
        if (paramBeforeDrag != null)
          paramBeforeDrag.dragStarting(this.dndArmedEvent); 
        transferHandler.exportAsDrag(this.component, this.dndArmedEvent, k);
        clearState();
      } 
    } 
    return true;
  }
  
  public static interface BeforeDrag {
    void dragStarting(MouseEvent param1MouseEvent);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\DragRecognitionSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */