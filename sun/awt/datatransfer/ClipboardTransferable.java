package sun.awt.datatransfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ClipboardTransferable implements Transferable {
  private final HashMap flavorsToData = new HashMap();
  
  private DataFlavor[] flavors = new DataFlavor[0];
  
  public ClipboardTransferable(SunClipboard paramSunClipboard) {
    paramSunClipboard.openClipboard(null);
    try {
      long[] arrayOfLong = paramSunClipboard.getClipboardFormats();
      if (arrayOfLong != null && arrayOfLong.length > 0) {
        HashMap hashMap = new HashMap(arrayOfLong.length, 1.0F);
        Map map = DataTransferer.getInstance().getFlavorsForFormats(arrayOfLong, SunClipboard.getDefaultFlavorTable());
        for (DataFlavor dataFlavor : map.keySet()) {
          Long long = (Long)map.get(dataFlavor);
          fetchOneFlavor(paramSunClipboard, dataFlavor, long, hashMap);
        } 
        this.flavors = DataTransferer.getInstance().setToSortedDataFlavorArray(this.flavorsToData.keySet());
      } 
    } finally {
      paramSunClipboard.closeClipboard();
    } 
  }
  
  private boolean fetchOneFlavor(SunClipboard paramSunClipboard, DataFlavor paramDataFlavor, Long paramLong, HashMap paramHashMap) {
    if (!this.flavorsToData.containsKey(paramDataFlavor)) {
      long l = paramLong.longValue();
      Object object = null;
      if (!paramHashMap.containsKey(paramLong)) {
        try {
          object = paramSunClipboard.getClipboardData(l);
        } catch (IOException iOException) {
          object = iOException;
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        } 
        paramHashMap.put(paramLong, object);
      } else {
        object = paramHashMap.get(paramLong);
      } 
      if (object instanceof IOException) {
        this.flavorsToData.put(paramDataFlavor, object);
        return false;
      } 
      if (object != null) {
        this.flavorsToData.put(paramDataFlavor, new DataFactory(l, (byte[])object));
        return true;
      } 
    } 
    return false;
  }
  
  public DataFlavor[] getTransferDataFlavors() { return (DataFlavor[])this.flavors.clone(); }
  
  public boolean isDataFlavorSupported(DataFlavor paramDataFlavor) { return this.flavorsToData.containsKey(paramDataFlavor); }
  
  public Object getTransferData(DataFlavor paramDataFlavor) throws UnsupportedFlavorException, IOException {
    if (!isDataFlavorSupported(paramDataFlavor))
      throw new UnsupportedFlavorException(paramDataFlavor); 
    Object object = this.flavorsToData.get(paramDataFlavor);
    if (object instanceof IOException)
      throw (IOException)object; 
    if (object instanceof DataFactory) {
      DataFactory dataFactory = (DataFactory)object;
      object = dataFactory.getTransferData(paramDataFlavor);
    } 
    return object;
  }
  
  private final class DataFactory {
    final long format;
    
    final byte[] data;
    
    DataFactory(long param1Long, byte[] param1ArrayOfByte) {
      this.format = param1Long;
      this.data = param1ArrayOfByte;
    }
    
    public Object getTransferData(DataFlavor param1DataFlavor) throws UnsupportedFlavorException, IOException { return DataTransferer.getInstance().translateBytes(this.data, param1DataFlavor, this.format, ClipboardTransferable.this); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\datatransfer\ClipboardTransferable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */