package sun.awt.datatransfer;

import java.awt.EventQueue;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.FlavorTable;
import java.awt.datatransfer.SystemFlavorMap;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import sun.awt.AppContext;
import sun.awt.EventListenerAggregate;
import sun.awt.PeerEvent;
import sun.awt.SunToolkit;

public abstract class SunClipboard extends Clipboard implements PropertyChangeListener {
  private AppContext contentsContext = null;
  
  private final Object CLIPBOARD_FLAVOR_LISTENER_KEY;
  
  public SunClipboard(String paramString) {
    super(paramString);
    this.CLIPBOARD_FLAVOR_LISTENER_KEY = new StringBuffer(paramString + "_CLIPBOARD_FLAVOR_LISTENER_KEY");
  }
  
  public void setContents(Transferable paramTransferable, ClipboardOwner paramClipboardOwner) {
    if (paramTransferable == null)
      throw new NullPointerException("contents"); 
    initContext();
    clipboardOwner = this.owner;
    transferable = this.contents;
    try {
      this.owner = paramClipboardOwner;
      this.contents = new TransferableProxy(paramTransferable, true);
      setContentsNative(paramTransferable);
    } finally {
      if (clipboardOwner != null && clipboardOwner != paramClipboardOwner)
        EventQueue.invokeLater(new Runnable() {
              public void run() { oldOwner.lostOwnership(SunClipboard.this, oldContents); }
            }); 
    } 
  }
  
  private void initContext() {
    AppContext appContext = AppContext.getAppContext();
    if (this.contentsContext != appContext) {
      synchronized (appContext) {
        if (appContext.isDisposed())
          throw new IllegalStateException("Can't set contents from disposed AppContext"); 
        appContext.addPropertyChangeListener("disposed", this);
      } 
      if (this.contentsContext != null)
        this.contentsContext.removePropertyChangeListener("disposed", this); 
      this.contentsContext = appContext;
    } 
  }
  
  public Transferable getContents(Object paramObject) { return (this.contents != null) ? this.contents : new ClipboardTransferable(this); }
  
  protected Transferable getContextContents() {
    AppContext appContext = AppContext.getAppContext();
    return (appContext == this.contentsContext) ? this.contents : null;
  }
  
  public DataFlavor[] getAvailableDataFlavors() {
    Transferable transferable = getContextContents();
    if (transferable != null)
      return transferable.getTransferDataFlavors(); 
    long[] arrayOfLong = getClipboardFormatsOpenClose();
    return DataTransferer.getInstance().getFlavorsForFormatsAsArray(arrayOfLong, getDefaultFlavorTable());
  }
  
  public boolean isDataFlavorAvailable(DataFlavor paramDataFlavor) {
    if (paramDataFlavor == null)
      throw new NullPointerException("flavor"); 
    Transferable transferable = getContextContents();
    if (transferable != null)
      return transferable.isDataFlavorSupported(paramDataFlavor); 
    long[] arrayOfLong = getClipboardFormatsOpenClose();
    return formatArrayAsDataFlavorSet(arrayOfLong).contains(paramDataFlavor);
  }
  
  public Object getData(DataFlavor paramDataFlavor) throws UnsupportedFlavorException, IOException {
    if (paramDataFlavor == null)
      throw new NullPointerException("flavor"); 
    Transferable transferable1 = getContextContents();
    if (transferable1 != null)
      return transferable1.getTransferData(paramDataFlavor); 
    long l = 0L;
    byte[] arrayOfByte = null;
    Transferable transferable2 = null;
    try {
      openClipboard(null);
      long[] arrayOfLong = getClipboardFormats();
      Long long = (Long)DataTransferer.getInstance().getFlavorsForFormats(arrayOfLong, getDefaultFlavorTable()).get(paramDataFlavor);
      if (long == null)
        throw new UnsupportedFlavorException(paramDataFlavor); 
      l = long.longValue();
      arrayOfByte = getClipboardData(l);
      if (DataTransferer.getInstance().isLocaleDependentTextFormat(l))
        transferable2 = createLocaleTransferable(arrayOfLong); 
    } finally {
      closeClipboard();
    } 
    return DataTransferer.getInstance().translateBytes(arrayOfByte, paramDataFlavor, l, transferable2);
  }
  
  protected Transferable createLocaleTransferable(long[] paramArrayOfLong) throws IOException { return null; }
  
  public void openClipboard(SunClipboard paramSunClipboard) {}
  
  public void closeClipboard() {}
  
  public abstract long getID();
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if ("disposed".equals(paramPropertyChangeEvent.getPropertyName()) && Boolean.TRUE.equals(paramPropertyChangeEvent.getNewValue())) {
      AppContext appContext = (AppContext)paramPropertyChangeEvent.getSource();
      lostOwnershipLater(appContext);
    } 
  }
  
  protected void lostOwnershipImpl() { lostOwnershipLater(null); }
  
  protected void lostOwnershipLater(AppContext paramAppContext) {
    AppContext appContext = this.contentsContext;
    if (appContext == null)
      return; 
    SunToolkit.postEvent(appContext, new PeerEvent(this, () -> lostOwnershipNow(paramAppContext), 1L));
  }
  
  protected void lostOwnershipNow(AppContext paramAppContext) {
    SunClipboard sunClipboard = this;
    ClipboardOwner clipboardOwner = null;
    Transferable transferable = null;
    synchronized (sunClipboard) {
      AppContext appContext = sunClipboard.contentsContext;
      if (appContext == null)
        return; 
      if (paramAppContext == null || appContext == paramAppContext) {
        clipboardOwner = sunClipboard.owner;
        transferable = sunClipboard.contents;
        sunClipboard.contentsContext = null;
        sunClipboard.owner = null;
        sunClipboard.contents = null;
        sunClipboard.clearNativeContext();
        appContext.removePropertyChangeListener("disposed", sunClipboard);
      } else {
        return;
      } 
    } 
    if (clipboardOwner != null)
      clipboardOwner.lostOwnership(sunClipboard, transferable); 
  }
  
  protected abstract void clearNativeContext();
  
  protected abstract void setContentsNative(Transferable paramTransferable);
  
  protected long[] getClipboardFormatsOpenClose() {
    try {
      openClipboard(null);
      return getClipboardFormats();
    } finally {
      closeClipboard();
    } 
  }
  
  protected abstract long[] getClipboardFormats();
  
  protected abstract byte[] getClipboardData(long paramLong) throws IOException;
  
  private static Set formatArrayAsDataFlavorSet(long[] paramArrayOfLong) { return (paramArrayOfLong == null) ? null : DataTransferer.getInstance().getFlavorsForFormatsAsSet(paramArrayOfLong, getDefaultFlavorTable()); }
  
  public void addFlavorListener(FlavorListener paramFlavorListener) {
    if (paramFlavorListener == null)
      return; 
    AppContext appContext = AppContext.getAppContext();
    EventListenerAggregate eventListenerAggregate = (EventListenerAggregate)appContext.get(this.CLIPBOARD_FLAVOR_LISTENER_KEY);
    if (eventListenerAggregate == null) {
      eventListenerAggregate = new EventListenerAggregate(FlavorListener.class);
      appContext.put(this.CLIPBOARD_FLAVOR_LISTENER_KEY, eventListenerAggregate);
    } 
    eventListenerAggregate.add(paramFlavorListener);
    if (this.numberOfFlavorListeners++ == 0) {
      long[] arrayOfLong = null;
      try {
        openClipboard(null);
        arrayOfLong = getClipboardFormats();
      } catch (IllegalStateException illegalStateException) {
      
      } finally {
        closeClipboard();
      } 
      this.currentFormats = arrayOfLong;
      registerClipboardViewerChecked();
    } 
  }
  
  public void removeFlavorListener(FlavorListener paramFlavorListener) {
    if (paramFlavorListener == null)
      return; 
    AppContext appContext = AppContext.getAppContext();
    EventListenerAggregate eventListenerAggregate = (EventListenerAggregate)appContext.get(this.CLIPBOARD_FLAVOR_LISTENER_KEY);
    if (eventListenerAggregate == null)
      return; 
    if (eventListenerAggregate.remove(paramFlavorListener) && --this.numberOfFlavorListeners == 0) {
      unregisterClipboardViewerChecked();
      this.currentFormats = null;
    } 
  }
  
  public FlavorListener[] getFlavorListeners() {
    EventListenerAggregate eventListenerAggregate = (EventListenerAggregate)AppContext.getAppContext().get(this.CLIPBOARD_FLAVOR_LISTENER_KEY);
    return (eventListenerAggregate == null) ? new FlavorListener[0] : (FlavorListener[])eventListenerAggregate.getListenersCopy();
  }
  
  public boolean areFlavorListenersRegistered() { return (this.numberOfFlavorListeners > 0); }
  
  protected abstract void registerClipboardViewerChecked();
  
  protected abstract void unregisterClipboardViewerChecked();
  
  protected final void checkChange(long[] paramArrayOfLong) {
    if (Arrays.equals(paramArrayOfLong, this.currentFormats))
      return; 
    this.currentFormats = paramArrayOfLong;
    for (AppContext appContext : AppContext.getAppContexts()) {
      if (appContext == null || appContext.isDisposed())
        continue; 
      EventListenerAggregate eventListenerAggregate = (EventListenerAggregate)appContext.get(this.CLIPBOARD_FLAVOR_LISTENER_KEY);
      if (eventListenerAggregate != null) {
        FlavorListener[] arrayOfFlavorListener = (FlavorListener[])eventListenerAggregate.getListenersInternal();
        for (byte b = 0; b < arrayOfFlavorListener.length; b++)
          SunToolkit.postEvent(appContext, new PeerEvent(this, new SunFlavorChangeNotifier(this, arrayOfFlavorListener[b]), 1L)); 
      } 
    } 
  }
  
  public static FlavorTable getDefaultFlavorTable() { return (FlavorTable)SystemFlavorMap.getDefaultFlavorMap(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\datatransfer\SunClipboard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */