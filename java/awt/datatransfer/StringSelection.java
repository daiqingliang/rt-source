package java.awt.datatransfer;

import java.io.IOException;
import java.io.StringReader;

public class StringSelection implements Transferable, ClipboardOwner {
  private static final int STRING = 0;
  
  private static final int PLAIN_TEXT = 1;
  
  private static final DataFlavor[] flavors = { DataFlavor.stringFlavor, DataFlavor.plainTextFlavor };
  
  private String data;
  
  public StringSelection(String paramString) { this.data = paramString; }
  
  public DataFlavor[] getTransferDataFlavors() { return (DataFlavor[])flavors.clone(); }
  
  public boolean isDataFlavorSupported(DataFlavor paramDataFlavor) {
    for (byte b = 0; b < flavors.length; b++) {
      if (paramDataFlavor.equals(flavors[b]))
        return true; 
    } 
    return false;
  }
  
  public Object getTransferData(DataFlavor paramDataFlavor) throws UnsupportedFlavorException, IOException {
    if (paramDataFlavor.equals(flavors[0]))
      return this.data; 
    if (paramDataFlavor.equals(flavors[1]))
      return new StringReader((this.data == null) ? "" : this.data); 
    throw new UnsupportedFlavorException(paramDataFlavor);
  }
  
  public void lostOwnership(Clipboard paramClipboard, Transferable paramTransferable) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\datatransfer\StringSelection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */