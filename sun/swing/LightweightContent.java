package sun.swing;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import javax.swing.JComponent;

public interface LightweightContent {
  JComponent getComponent();
  
  void paintLock();
  
  void paintUnlock();
  
  default void imageBufferReset(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) { imageBufferReset(paramArrayOfInt, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  default void imageBufferReset(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { imageBufferReset(paramArrayOfInt, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, 1); }
  
  void imageReshaped(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  void imageUpdated(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  void focusGrabbed();
  
  void focusUngrabbed();
  
  void preferredSizeChanged(int paramInt1, int paramInt2);
  
  void maximumSizeChanged(int paramInt1, int paramInt2);
  
  void minimumSizeChanged(int paramInt1, int paramInt2);
  
  default void setCursor(Cursor paramCursor) {}
  
  default <T extends java.awt.dnd.DragGestureRecognizer> T createDragGestureRecognizer(Class<T> paramClass, DragSource paramDragSource, Component paramComponent, int paramInt, DragGestureListener paramDragGestureListener) { return null; }
  
  default DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent paramDragGestureEvent) throws InvalidDnDOperationException { return null; }
  
  default void addDropTarget(DropTarget paramDropTarget) {}
  
  default void removeDropTarget(DropTarget paramDropTarget) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\swing\LightweightContent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */