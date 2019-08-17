package javax.xml.transform.stream;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import javax.xml.transform.Source;

public class StreamSource implements Source {
  public static final String FEATURE = "http://javax.xml.transform.stream.StreamSource/feature";
  
  private String publicId;
  
  private String systemId;
  
  private InputStream inputStream;
  
  private Reader reader;
  
  public StreamSource() {}
  
  public StreamSource(InputStream paramInputStream) { setInputStream(paramInputStream); }
  
  public StreamSource(InputStream paramInputStream, String paramString) {
    setInputStream(paramInputStream);
    setSystemId(paramString);
  }
  
  public StreamSource(Reader paramReader) { setReader(paramReader); }
  
  public StreamSource(Reader paramReader, String paramString) {
    setReader(paramReader);
    setSystemId(paramString);
  }
  
  public StreamSource(String paramString) { this.systemId = paramString; }
  
  public StreamSource(File paramFile) { setSystemId(paramFile.toURI().toASCIIString()); }
  
  public void setInputStream(InputStream paramInputStream) { this.inputStream = paramInputStream; }
  
  public InputStream getInputStream() { return this.inputStream; }
  
  public void setReader(Reader paramReader) { this.reader = paramReader; }
  
  public Reader getReader() { return this.reader; }
  
  public void setPublicId(String paramString) { this.publicId = paramString; }
  
  public String getPublicId() { return this.publicId; }
  
  public void setSystemId(String paramString) { this.systemId = paramString; }
  
  public String getSystemId() { return this.systemId; }
  
  public void setSystemId(File paramFile) { this.systemId = paramFile.toURI().toASCIIString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\transform\stream\StreamSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */