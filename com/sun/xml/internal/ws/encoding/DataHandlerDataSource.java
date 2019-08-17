package com.sun.xml.internal.ws.encoding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataHandler;
import javax.activation.DataSource;

public class DataHandlerDataSource implements DataSource {
  private final DataHandler dataHandler;
  
  public DataHandlerDataSource(DataHandler paramDataHandler) { this.dataHandler = paramDataHandler; }
  
  public InputStream getInputStream() throws IOException { return this.dataHandler.getInputStream(); }
  
  public OutputStream getOutputStream() throws IOException { return this.dataHandler.getOutputStream(); }
  
  public String getContentType() { return this.dataHandler.getContentType(); }
  
  public String getName() { return this.dataHandler.getName(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\DataHandlerDataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */