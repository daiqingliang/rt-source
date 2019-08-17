package javax.activation;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.OutputStream;

public interface DataContentHandler {
  DataFlavor[] getTransferDataFlavors();
  
  Object getTransferData(DataFlavor paramDataFlavor, DataSource paramDataSource) throws UnsupportedFlavorException, IOException;
  
  Object getContent(DataSource paramDataSource) throws IOException;
  
  void writeTo(Object paramObject, String paramString, OutputStream paramOutputStream) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\activation\DataContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */