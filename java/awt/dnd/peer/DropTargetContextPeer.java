package java.awt.dnd.peer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.InvalidDnDOperationException;

public interface DropTargetContextPeer {
  void setTargetActions(int paramInt);
  
  int getTargetActions();
  
  DropTarget getDropTarget();
  
  DataFlavor[] getTransferDataFlavors();
  
  Transferable getTransferable() throws InvalidDnDOperationException;
  
  boolean isTransferableJVMLocal();
  
  void acceptDrag(int paramInt);
  
  void rejectDrag();
  
  void acceptDrop(int paramInt);
  
  void rejectDrop();
  
  void dropComplete(boolean paramBoolean);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\dnd\peer\DropTargetContextPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */