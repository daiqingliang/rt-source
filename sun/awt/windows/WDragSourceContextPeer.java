package sun.awt.windows;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Map;
import sun.awt.dnd.SunDragSourceContextPeer;

final class WDragSourceContextPeer extends SunDragSourceContextPeer {
  private static final WDragSourceContextPeer theInstance = new WDragSourceContextPeer(null);
  
  public void startSecondaryEventLoop() { WToolkit.startSecondaryEventLoop(); }
  
  public void quitSecondaryEventLoop() { WToolkit.quitSecondaryEventLoop(); }
  
  private WDragSourceContextPeer(DragGestureEvent paramDragGestureEvent) { super(paramDragGestureEvent); }
  
  static WDragSourceContextPeer createDragSourceContextPeer(DragGestureEvent paramDragGestureEvent) throws InvalidDnDOperationException {
    theInstance.setTrigger(paramDragGestureEvent);
    return theInstance;
  }
  
  protected void startDrag(Transferable paramTransferable, long[] paramArrayOfLong, Map paramMap) {
    long l = 0L;
    l = createDragSource(getTrigger().getComponent(), paramTransferable, getTrigger().getTriggerEvent(), getTrigger().getSourceAsDragGestureRecognizer().getSourceActions(), paramArrayOfLong, paramMap);
    if (l == 0L)
      throw new InvalidDnDOperationException("failed to create native peer"); 
    int[] arrayOfInt = null;
    Point point = null;
    Image image = getDragImage();
    int i = -1;
    int j = -1;
    if (image != null)
      try {
        i = image.getWidth(null);
        j = image.getHeight(null);
        if (i < 0 || j < 0)
          throw new InvalidDnDOperationException("drag image is not ready"); 
        point = getDragImageOffset();
        BufferedImage bufferedImage = new BufferedImage(i, j, 2);
        bufferedImage.getGraphics().drawImage(image, 0, 0, null);
        arrayOfInt = ((DataBufferInt)bufferedImage.getData().getDataBuffer()).getData();
      } catch (Throwable throwable) {
        throw new InvalidDnDOperationException("drag image creation problem: " + throwable.getMessage());
      }  
    setNativeContext(l);
    WDropTargetContextPeer.setCurrentJVMLocalSourceTransferable(paramTransferable);
    if (arrayOfInt != null) {
      doDragDrop(getNativeContext(), getCursor(), arrayOfInt, i, j, point.x, point.y);
    } else {
      doDragDrop(getNativeContext(), getCursor(), null, -1, -1, 0, 0);
    } 
  }
  
  native long createDragSource(Component paramComponent, Transferable paramTransferable, InputEvent paramInputEvent, int paramInt, long[] paramArrayOfLong, Map paramMap);
  
  native void doDragDrop(long paramLong, Cursor paramCursor, int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  protected native void setNativeCursor(long paramLong, Cursor paramCursor, int paramInt);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WDragSourceContextPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */