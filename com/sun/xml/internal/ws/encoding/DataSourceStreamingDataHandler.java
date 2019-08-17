package com.sun.xml.internal.ws.encoding;

import com.sun.xml.internal.ws.developer.StreamingDataHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.activation.DataSource;

public class DataSourceStreamingDataHandler extends StreamingDataHandler {
  public DataSourceStreamingDataHandler(DataSource paramDataSource) { super(paramDataSource); }
  
  public InputStream readOnce() throws IOException { return getInputStream(); }
  
  public void moveTo(File paramFile) throws IOException {
    InputStream inputStream = getInputStream();
    fileOutputStream = new FileOutputStream(paramFile);
    try {
      byte[] arrayOfByte = new byte[8192];
      int i;
      while ((i = inputStream.read(arrayOfByte)) != -1)
        fileOutputStream.write(arrayOfByte, 0, i); 
      inputStream.close();
    } finally {
      if (fileOutputStream != null)
        fileOutputStream.close(); 
    } 
  }
  
  public void close() throws IOException {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\DataSourceStreamingDataHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */