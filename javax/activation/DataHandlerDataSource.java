package javax.activation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class DataHandlerDataSource implements DataSource {
  DataHandler dataHandler = null;
  
  public DataHandlerDataSource(DataHandler paramDataHandler) { this.dataHandler = paramDataHandler; }
  
  public InputStream getInputStream() throws IOException { return this.dataHandler.getInputStream(); }
  
  public OutputStream getOutputStream() throws IOException { return this.dataHandler.getOutputStream(); }
  
  public String getContentType() { return this.dataHandler.getContentType(); }
  
  public String getName() { return this.dataHandler.getName(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\activation\DataHandlerDataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */