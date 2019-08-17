package sun.awt.windows;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.SortedMap;
import sun.awt.datatransfer.DataTransferer;
import sun.awt.datatransfer.SunClipboard;

final class WClipboard extends SunClipboard {
  private boolean isClipboardViewerRegistered;
  
  WClipboard() { super("System"); }
  
  public long getID() { return 0L; }
  
  protected void setContentsNative(Transferable paramTransferable) {
    SortedMap sortedMap = WDataTransferer.getInstance().getFormatsForTransferable(paramTransferable, getDefaultFlavorTable());
    openClipboard(this);
    try {
      for (Long long : sortedMap.keySet()) {
        DataFlavor dataFlavor = (DataFlavor)sortedMap.get(long);
        try {
          byte[] arrayOfByte = WDataTransferer.getInstance().translateTransferable(paramTransferable, dataFlavor, long.longValue());
          publishClipboardData(long.longValue(), arrayOfByte);
        } catch (IOException iOException) {
          if (!dataFlavor.isMimeTypeEqual("application/x-java-jvm-local-objectref") || !(iOException instanceof java.io.NotSerializableException))
            iOException.printStackTrace(); 
        } 
      } 
    } finally {
      closeClipboard();
    } 
  }
  
  private void lostSelectionOwnershipImpl() { lostOwnershipImpl(); }
  
  protected void clearNativeContext() {}
  
  public native void openClipboard(SunClipboard paramSunClipboard) throws IllegalStateException;
  
  public native void closeClipboard();
  
  private native void publishClipboardData(long paramLong, byte[] paramArrayOfByte);
  
  private static native void init();
  
  protected native long[] getClipboardFormats();
  
  protected native byte[] getClipboardData(long paramLong) throws IOException;
  
  protected void registerClipboardViewerChecked() {
    if (!this.isClipboardViewerRegistered) {
      registerClipboardViewer();
      this.isClipboardViewerRegistered = true;
    } 
  }
  
  private native void registerClipboardViewer();
  
  protected void unregisterClipboardViewerChecked() {}
  
  private void handleContentsChanged() {
    if (!areFlavorListenersRegistered())
      return; 
    long[] arrayOfLong = null;
    try {
      openClipboard(null);
      arrayOfLong = getClipboardFormats();
    } catch (IllegalStateException illegalStateException) {
    
    } finally {
      closeClipboard();
    } 
    checkChange(arrayOfLong);
  }
  
  protected Transferable createLocaleTransferable(long[] paramArrayOfLong) throws IOException {
    boolean bool = false;
    for (byte b = 0; b < paramArrayOfLong.length; b++) {
      if (paramArrayOfLong[b] == 16L) {
        bool = true;
        break;
      } 
    } 
    if (!bool)
      return null; 
    byte[] arrayOfByte1 = null;
    try {
      arrayOfByte1 = getClipboardData(16L);
    } catch (IOException iOException) {
      return null;
    } 
    final byte[] localeDataFinal = arrayOfByte1;
    return new Transferable() {
        public DataFlavor[] getTransferDataFlavors() { return new DataFlavor[] { DataTransferer.javaTextEncodingFlavor }; }
        
        public boolean isDataFlavorSupported(DataFlavor param1DataFlavor) { return param1DataFlavor.equals(DataTransferer.javaTextEncodingFlavor); }
        
        public Object getTransferData(DataFlavor param1DataFlavor) throws UnsupportedFlavorException {
          if (isDataFlavorSupported(param1DataFlavor))
            return localeDataFinal; 
          throw new UnsupportedFlavorException(param1DataFlavor);
        }
      };
  }
  
  static  {
    init();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WClipboard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */