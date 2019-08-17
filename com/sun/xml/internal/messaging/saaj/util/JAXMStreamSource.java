package com.sun.xml.internal.messaging.saaj.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import javax.xml.transform.stream.StreamSource;

public class JAXMStreamSource extends StreamSource {
  InputStream in;
  
  Reader reader;
  
  private static final boolean lazyContentLength = SAAJUtil.getSystemBoolean("saaj.lazy.contentlength");
  
  public JAXMStreamSource(InputStream paramInputStream) throws IOException {
    if (lazyContentLength) {
      this.in = paramInputStream;
    } else if (paramInputStream instanceof ByteInputStream) {
      this.in = (ByteInputStream)paramInputStream;
    } else {
      ByteOutputStream byteOutputStream = new ByteOutputStream();
      byteOutputStream.write(paramInputStream);
      this.in = byteOutputStream.newInputStream();
    } 
  }
  
  public JAXMStreamSource(Reader paramReader) throws IOException {
    if (lazyContentLength) {
      this.reader = paramReader;
      return;
    } 
    CharWriter charWriter = new CharWriter();
    char[] arrayOfChar = new char[1024];
    int i;
    while (-1 != (i = paramReader.read(arrayOfChar)))
      charWriter.write(arrayOfChar, 0, i); 
    this.reader = new CharReader(charWriter.getChars(), charWriter.getCount());
  }
  
  public InputStream getInputStream() { return this.in; }
  
  public Reader getReader() { return this.reader; }
  
  public void reset() throws IOException {
    if (this.in != null)
      this.in.reset(); 
    if (this.reader != null)
      this.reader.reset(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saa\\util\JAXMStreamSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */