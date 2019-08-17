package com.sun.xml.internal.bind.v2.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.xml.transform.stream.StreamSource;

public final class DataSourceSource extends StreamSource {
  private final DataSource source;
  
  private final String charset;
  
  private Reader r;
  
  private InputStream is;
  
  public DataSourceSource(DataHandler paramDataHandler) throws MimeTypeParseException { this(paramDataHandler.getDataSource()); }
  
  public DataSourceSource(DataSource paramDataSource) throws MimeTypeParseException {
    this.source = paramDataSource;
    String str = paramDataSource.getContentType();
    if (str == null) {
      this.charset = null;
    } else {
      MimeType mimeType = new MimeType(str);
      this.charset = mimeType.getParameter("charset");
    } 
  }
  
  public void setReader(Reader paramReader) { throw new UnsupportedOperationException(); }
  
  public void setInputStream(InputStream paramInputStream) { throw new UnsupportedOperationException(); }
  
  public Reader getReader() {
    try {
      if (this.charset == null)
        return null; 
      if (this.r == null)
        this.r = new InputStreamReader(this.source.getInputStream(), this.charset); 
      return this.r;
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
  
  public InputStream getInputStream() {
    try {
      if (this.charset != null)
        return null; 
      if (this.is == null)
        this.is = this.source.getInputStream(); 
      return this.is;
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
  
  public DataSource getDataSource() { return this.source; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v\\util\DataSourceSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */