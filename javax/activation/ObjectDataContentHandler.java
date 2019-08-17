package javax.activation;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

class ObjectDataContentHandler implements DataContentHandler {
  private DataFlavor[] transferFlavors = null;
  
  private Object obj;
  
  private String mimeType;
  
  private DataContentHandler dch = null;
  
  public ObjectDataContentHandler(DataContentHandler paramDataContentHandler, Object paramObject, String paramString) {
    this.obj = paramObject;
    this.mimeType = paramString;
    this.dch = paramDataContentHandler;
  }
  
  public DataContentHandler getDCH() { return this.dch; }
  
  public DataFlavor[] getTransferDataFlavors() {
    if (this.transferFlavors == null)
      if (this.dch != null) {
        this.transferFlavors = this.dch.getTransferDataFlavors();
      } else {
        this.transferFlavors = new DataFlavor[1];
        this.transferFlavors[0] = new ActivationDataFlavor(this.obj.getClass(), this.mimeType, this.mimeType);
      }  
    return this.transferFlavors;
  }
  
  public Object getTransferData(DataFlavor paramDataFlavor, DataSource paramDataSource) throws UnsupportedFlavorException, IOException {
    if (this.dch != null)
      return this.dch.getTransferData(paramDataFlavor, paramDataSource); 
    if (paramDataFlavor.equals(getTransferDataFlavors()[0]))
      return this.obj; 
    throw new UnsupportedFlavorException(paramDataFlavor);
  }
  
  public Object getContent(DataSource paramDataSource) { return this.obj; }
  
  public void writeTo(Object paramObject, String paramString, OutputStream paramOutputStream) throws IOException {
    if (this.dch != null) {
      this.dch.writeTo(paramObject, paramString, paramOutputStream);
    } else if (paramObject instanceof byte[]) {
      paramOutputStream.write((byte[])paramObject);
    } else if (paramObject instanceof String) {
      OutputStreamWriter outputStreamWriter = new OutputStreamWriter(paramOutputStream);
      outputStreamWriter.write((String)paramObject);
      outputStreamWriter.flush();
    } else {
      throw new UnsupportedDataTypeException("no object DCH for MIME type " + this.mimeType);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\activation\ObjectDataContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */