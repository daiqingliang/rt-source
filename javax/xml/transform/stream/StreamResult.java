package javax.xml.transform.stream;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.transform.Result;

public class StreamResult implements Result {
  public static final String FEATURE = "http://javax.xml.transform.stream.StreamResult/feature";
  
  private String systemId;
  
  private OutputStream outputStream;
  
  private Writer writer;
  
  public StreamResult() {}
  
  public StreamResult(OutputStream paramOutputStream) { setOutputStream(paramOutputStream); }
  
  public StreamResult(Writer paramWriter) { setWriter(paramWriter); }
  
  public StreamResult(String paramString) { this.systemId = paramString; }
  
  public StreamResult(File paramFile) { setSystemId(paramFile.toURI().toASCIIString()); }
  
  public void setOutputStream(OutputStream paramOutputStream) { this.outputStream = paramOutputStream; }
  
  public OutputStream getOutputStream() { return this.outputStream; }
  
  public void setWriter(Writer paramWriter) { this.writer = paramWriter; }
  
  public Writer getWriter() { return this.writer; }
  
  public void setSystemId(String paramString) { this.systemId = paramString; }
  
  public void setSystemId(File paramFile) { this.systemId = paramFile.toURI().toASCIIString(); }
  
  public String getSystemId() { return this.systemId; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\transform\stream\StreamResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */