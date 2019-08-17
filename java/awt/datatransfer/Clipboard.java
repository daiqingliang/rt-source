package java.awt.datatransfer;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import sun.awt.EventListenerAggregate;

public class Clipboard {
  String name;
  
  protected ClipboardOwner owner;
  
  protected Transferable contents;
  
  private EventListenerAggregate flavorListeners;
  
  private Set<DataFlavor> currentDataFlavors;
  
  public Clipboard(String paramString) { this.name = paramString; }
  
  public String getName() { return this.name; }
  
  public void setContents(Transferable paramTransferable, ClipboardOwner paramClipboardOwner) {
    final ClipboardOwner oldOwner = this.owner;
    final Transferable oldContents = this.contents;
    this.owner = paramClipboardOwner;
    this.contents = paramTransferable;
    if (clipboardOwner != null && clipboardOwner != paramClipboardOwner)
      EventQueue.invokeLater(new Runnable() {
            public void run() { oldOwner.lostOwnership(Clipboard.this, oldContents); }
          }); 
    fireFlavorsChanged();
  }
  
  public Transferable getContents(Object paramObject) { return this.contents; }
  
  public DataFlavor[] getAvailableDataFlavors() {
    Transferable transferable = getContents(null);
    return (transferable == null) ? new DataFlavor[0] : transferable.getTransferDataFlavors();
  }
  
  public boolean isDataFlavorAvailable(DataFlavor paramDataFlavor) {
    if (paramDataFlavor == null)
      throw new NullPointerException("flavor"); 
    Transferable transferable = getContents(null);
    return (transferable == null) ? false : transferable.isDataFlavorSupported(paramDataFlavor);
  }
  
  public Object getData(DataFlavor paramDataFlavor) throws UnsupportedFlavorException, IOException {
    if (paramDataFlavor == null)
      throw new NullPointerException("flavor"); 
    Transferable transferable = getContents(null);
    if (transferable == null)
      throw new UnsupportedFlavorException(paramDataFlavor); 
    return transferable.getTransferData(paramDataFlavor);
  }
  
  public void addFlavorListener(FlavorListener paramFlavorListener) {
    if (paramFlavorListener == null)
      return; 
    if (this.flavorListeners == null) {
      this.currentDataFlavors = getAvailableDataFlavorSet();
      this.flavorListeners = new EventListenerAggregate(FlavorListener.class);
    } 
    this.flavorListeners.add(paramFlavorListener);
  }
  
  public void removeFlavorListener(FlavorListener paramFlavorListener) {
    if (paramFlavorListener == null || this.flavorListeners == null)
      return; 
    this.flavorListeners.remove(paramFlavorListener);
  }
  
  public FlavorListener[] getFlavorListeners() { return (this.flavorListeners == null) ? new FlavorListener[0] : (FlavorListener[])this.flavorListeners.getListenersCopy(); }
  
  private void fireFlavorsChanged() {
    if (this.flavorListeners == null)
      return; 
    Set set = this.currentDataFlavors;
    this.currentDataFlavors = getAvailableDataFlavorSet();
    if (set.equals(this.currentDataFlavors))
      return; 
    FlavorListener[] arrayOfFlavorListener = (FlavorListener[])this.flavorListeners.getListenersInternal();
    for (byte b = 0; b < arrayOfFlavorListener.length; b++) {
      final FlavorListener listener = arrayOfFlavorListener[b];
      EventQueue.invokeLater(new Runnable() {
            public void run() { listener.flavorsChanged(new FlavorEvent(Clipboard.this)); }
          });
    } 
  }
  
  private Set<DataFlavor> getAvailableDataFlavorSet() {
    HashSet hashSet = new HashSet();
    Transferable transferable = getContents(null);
    if (transferable != null) {
      DataFlavor[] arrayOfDataFlavor = transferable.getTransferDataFlavors();
      if (arrayOfDataFlavor != null)
        hashSet.addAll(Arrays.asList(arrayOfDataFlavor)); 
    } 
    return hashSet;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\datatransfer\Clipboard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */