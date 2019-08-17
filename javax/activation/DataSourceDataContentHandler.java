package javax.activation;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.OutputStream;

class DataSourceDataContentHandler implements DataContentHandler {
  private DataSource ds = null;
  
  private DataFlavor[] transferFlavors = null;
  
  private DataContentHandler dch = null;
  
  public DataSourceDataContentHandler(DataContentHandler paramDataContentHandler, DataSource paramDataSource) {
    this.ds = paramDataSource;
    this.dch = paramDataContentHandler;
  }
  
  public DataFlavor[] getTransferDataFlavors() {
    if (this.transferFlavors == null)
      if (this.dch != null) {
        this.transferFlavors = this.dch.getTransferDataFlavors();
      } else {
        this.transferFlavors = new DataFlavor[1];
        this.transferFlavors[0] = new ActivationDataFlavor(this.ds.getContentType(), this.ds.getContentType());
      }  
    return this.transferFlavors;
  }
  
  public Object getTransferData(DataFlavor paramDataFlavor, DataSource paramDataSource) throws UnsupportedFlavorException, IOException {
    if (this.dch != null)
      return this.dch.getTransferData(paramDataFlavor, paramDataSource); 
    if (paramDataFlavor.equals(getTransferDataFlavors()[0]))
      return paramDataSource.getInputStream(); 
    throw new UnsupportedFlavorException(paramDataFlavor);
  }
  
  public Object getContent(DataSource paramDataSource) throws IOException { return (this.dch != null) ? this.dch.getContent(paramDataSource) : paramDataSource.getInputStream(); }
  
  public void writeTo(Object paramObject, String paramString, OutputStream paramOutputStream) throws IOException {
    if (this.dch != null) {
      this.dch.writeTo(paramObject, paramString, paramOutputStream);
    } else {
      throw new UnsupportedDataTypeException("no DCH for content type " + this.ds.getContentType());
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\activation\DataSourceDataContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */