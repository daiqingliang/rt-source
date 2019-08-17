package java.awt.dnd;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.List;

public class DropTargetDragEvent extends DropTargetEvent {
  private static final long serialVersionUID = -8422265619058953682L;
  
  private Point location;
  
  private int actions;
  
  private int dropAction;
  
  public DropTargetDragEvent(DropTargetContext paramDropTargetContext, Point paramPoint, int paramInt1, int paramInt2) {
    super(paramDropTargetContext);
    if (paramPoint == null)
      throw new NullPointerException("cursorLocn"); 
    if (paramInt1 != 0 && paramInt1 != 1 && paramInt1 != 2 && paramInt1 != 1073741824)
      throw new IllegalArgumentException("dropAction" + paramInt1); 
    if ((paramInt2 & 0xBFFFFFFC) != 0)
      throw new IllegalArgumentException("srcActions"); 
    this.location = paramPoint;
    this.actions = paramInt2;
    this.dropAction = paramInt1;
  }
  
  public Point getLocation() { return this.location; }
  
  public DataFlavor[] getCurrentDataFlavors() { return getDropTargetContext().getCurrentDataFlavors(); }
  
  public List<DataFlavor> getCurrentDataFlavorsAsList() { return getDropTargetContext().getCurrentDataFlavorsAsList(); }
  
  public boolean isDataFlavorSupported(DataFlavor paramDataFlavor) { return getDropTargetContext().isDataFlavorSupported(paramDataFlavor); }
  
  public int getSourceActions() { return this.actions; }
  
  public int getDropAction() { return this.dropAction; }
  
  public Transferable getTransferable() { return getDropTargetContext().getTransferable(); }
  
  public void acceptDrag(int paramInt) { getDropTargetContext().acceptDrag(paramInt); }
  
  public void rejectDrag() { getDropTargetContext().rejectDrag(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\dnd\DropTargetDragEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */