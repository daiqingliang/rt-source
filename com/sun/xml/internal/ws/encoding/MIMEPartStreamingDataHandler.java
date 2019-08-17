package com.sun.xml.internal.ws.encoding;

import com.sun.xml.internal.org.jvnet.mimepull.MIMEPart;
import com.sun.xml.internal.ws.developer.StreamingDataHandler;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;

public class MIMEPartStreamingDataHandler extends StreamingDataHandler {
  private final StreamingDataSource ds = (StreamingDataSource)getDataSource();
  
  public MIMEPartStreamingDataHandler(MIMEPart paramMIMEPart) { super(new StreamingDataSource(paramMIMEPart)); }
  
  public InputStream readOnce() throws IOException { return this.ds.readOnce(); }
  
  public void moveTo(File paramFile) throws IOException { this.ds.moveTo(paramFile); }
  
  public void close() throws IOException { this.ds.close(); }
  
  private static final class MyIOException extends IOException {
    private final Exception linkedException;
    
    MyIOException(Exception param1Exception) { this.linkedException = param1Exception; }
    
    public Throwable getCause() { return this.linkedException; }
  }
  
  private static final class StreamingDataSource implements DataSource {
    private final MIMEPart part;
    
    StreamingDataSource(MIMEPart param1MIMEPart) { this.part = param1MIMEPart; }
    
    public InputStream getInputStream() throws IOException { return this.part.read(); }
    
    InputStream readOnce() throws IOException {
      try {
        return this.part.readOnce();
      } catch (Exception exception) {
        throw new MIMEPartStreamingDataHandler.MyIOException(exception);
      } 
    }
    
    void moveTo(File param1File) throws IOException { this.part.moveTo(param1File); }
    
    public OutputStream getOutputStream() throws IOException { return null; }
    
    public String getContentType() { return this.part.getContentType(); }
    
    public String getName() { return ""; }
    
    public void close() throws IOException { this.part.close(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\MIMEPartStreamingDataHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */