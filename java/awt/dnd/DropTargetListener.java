package java.awt.dnd;

import java.util.EventListener;

public interface DropTargetListener extends EventListener {
  void dragEnter(DropTargetDragEvent paramDropTargetDragEvent);
  
  void dragOver(DropTargetDragEvent paramDropTargetDragEvent);
  
  void dropActionChanged(DropTargetDragEvent paramDropTargetDragEvent);
  
  void dragExit(DropTargetEvent paramDropTargetEvent);
  
  void drop(DropTargetDropEvent paramDropTargetDropEvent);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\dnd\DropTargetListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */