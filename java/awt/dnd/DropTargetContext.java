package java.awt.dnd;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.peer.DropTargetContextPeer;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import sun.awt.datatransfer.TransferableProxy;

public class DropTargetContext implements Serializable {
  private static final long serialVersionUID = -634158968993743371L;
  
  private DropTarget dropTarget;
  
  private DropTargetContextPeer dropTargetContextPeer;
  
  private Transferable transferable;
  
  DropTargetContext(DropTarget paramDropTarget) { this.dropTarget = paramDropTarget; }
  
  public DropTarget getDropTarget() { return this.dropTarget; }
  
  public Component getComponent() { return this.dropTarget.getComponent(); }
  
  public void addNotify(DropTargetContextPeer paramDropTargetContextPeer) { this.dropTargetContextPeer = paramDropTargetContextPeer; }
  
  public void removeNotify() {
    this.dropTargetContextPeer = null;
    this.transferable = null;
  }
  
  protected void setTargetActions(int paramInt) {
    DropTargetContextPeer dropTargetContextPeer1 = getDropTargetContextPeer();
    if (dropTargetContextPeer1 != null) {
      synchronized (dropTargetContextPeer1) {
        dropTargetContextPeer1.setTargetActions(paramInt);
        getDropTarget().doSetDefaultActions(paramInt);
      } 
    } else {
      getDropTarget().doSetDefaultActions(paramInt);
    } 
  }
  
  protected int getTargetActions() {
    DropTargetContextPeer dropTargetContextPeer1 = getDropTargetContextPeer();
    return (dropTargetContextPeer1 != null) ? dropTargetContextPeer1.getTargetActions() : this.dropTarget.getDefaultActions();
  }
  
  public void dropComplete(boolean paramBoolean) throws InvalidDnDOperationException {
    DropTargetContextPeer dropTargetContextPeer1 = getDropTargetContextPeer();
    if (dropTargetContextPeer1 != null)
      dropTargetContextPeer1.dropComplete(paramBoolean); 
  }
  
  protected void acceptDrag(int paramInt) {
    DropTargetContextPeer dropTargetContextPeer1 = getDropTargetContextPeer();
    if (dropTargetContextPeer1 != null)
      dropTargetContextPeer1.acceptDrag(paramInt); 
  }
  
  protected void rejectDrag() {
    DropTargetContextPeer dropTargetContextPeer1 = getDropTargetContextPeer();
    if (dropTargetContextPeer1 != null)
      dropTargetContextPeer1.rejectDrag(); 
  }
  
  protected void acceptDrop(int paramInt) {
    DropTargetContextPeer dropTargetContextPeer1 = getDropTargetContextPeer();
    if (dropTargetContextPeer1 != null)
      dropTargetContextPeer1.acceptDrop(paramInt); 
  }
  
  protected void rejectDrop() {
    DropTargetContextPeer dropTargetContextPeer1 = getDropTargetContextPeer();
    if (dropTargetContextPeer1 != null)
      dropTargetContextPeer1.rejectDrop(); 
  }
  
  protected DataFlavor[] getCurrentDataFlavors() {
    DropTargetContextPeer dropTargetContextPeer1 = getDropTargetContextPeer();
    return (dropTargetContextPeer1 != null) ? dropTargetContextPeer1.getTransferDataFlavors() : new DataFlavor[0];
  }
  
  protected List<DataFlavor> getCurrentDataFlavorsAsList() { return Arrays.asList(getCurrentDataFlavors()); }
  
  protected boolean isDataFlavorSupported(DataFlavor paramDataFlavor) { return getCurrentDataFlavorsAsList().contains(paramDataFlavor); }
  
  protected Transferable getTransferable() throws InvalidDnDOperationException {
    DropTargetContextPeer dropTargetContextPeer1 = getDropTargetContextPeer();
    if (dropTargetContextPeer1 == null)
      throw new InvalidDnDOperationException(); 
    if (this.transferable == null) {
      Transferable transferable1 = dropTargetContextPeer1.getTransferable();
      boolean bool = dropTargetContextPeer1.isTransferableJVMLocal();
      synchronized (this) {
        if (this.transferable == null)
          this.transferable = createTransferableProxy(transferable1, bool); 
      } 
    } 
    return this.transferable;
  }
  
  DropTargetContextPeer getDropTargetContextPeer() { return this.dropTargetContextPeer; }
  
  protected Transferable createTransferableProxy(Transferable paramTransferable, boolean paramBoolean) { return new TransferableProxy(paramTransferable, paramBoolean); }
  
  protected class TransferableProxy implements Transferable {
    protected Transferable transferable;
    
    protected boolean isLocal;
    
    private TransferableProxy proxy;
    
    TransferableProxy(Transferable param1Transferable, boolean param1Boolean) {
      this.proxy = new TransferableProxy(param1Transferable, param1Boolean);
      this.transferable = param1Transferable;
      this.isLocal = param1Boolean;
    }
    
    public DataFlavor[] getTransferDataFlavors() { return this.proxy.getTransferDataFlavors(); }
    
    public boolean isDataFlavorSupported(DataFlavor param1DataFlavor) { return this.proxy.isDataFlavorSupported(param1DataFlavor); }
    
    public Object getTransferData(DataFlavor param1DataFlavor) throws UnsupportedFlavorException, IOException { return this.proxy.getTransferData(param1DataFlavor); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\dnd\DropTargetContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */