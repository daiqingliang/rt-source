package java.io;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import sun.nio.cs.StreamEncoder;

public class OutputStreamWriter extends Writer {
  private final StreamEncoder se;
  
  public OutputStreamWriter(OutputStream paramOutputStream, String paramString) throws UnsupportedEncodingException {
    super(paramOutputStream);
    if (paramString == null)
      throw new NullPointerException("charsetName"); 
    this.se = StreamEncoder.forOutputStreamWriter(paramOutputStream, this, paramString);
  }
  
  public OutputStreamWriter(OutputStream paramOutputStream) {
    super(paramOutputStream);
    try {
      this.se = StreamEncoder.forOutputStreamWriter(paramOutputStream, this, (String)null);
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new Error(unsupportedEncodingException);
    } 
  }
  
  public OutputStreamWriter(OutputStream paramOutputStream, Charset paramCharset) {
    super(paramOutputStream);
    if (paramCharset == null)
      throw new NullPointerException("charset"); 
    this.se = StreamEncoder.forOutputStreamWriter(paramOutputStream, this, paramCharset);
  }
  
  public OutputStreamWriter(OutputStream paramOutputStream, CharsetEncoder paramCharsetEncoder) {
    super(paramOutputStream);
    if (paramCharsetEncoder == null)
      throw new NullPointerException("charset encoder"); 
    this.se = StreamEncoder.forOutputStreamWriter(paramOutputStream, this, paramCharsetEncoder);
  }
  
  public String getEncoding() { return this.se.getEncoding(); }
  
  void flushBuffer() throws IOException { this.se.flushBuffer(); }
  
  public void write(int paramInt) throws IOException { this.se.write(paramInt); }
  
  public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException { this.se.write(paramArrayOfChar, paramInt1, paramInt2); }
  
  public void write(String paramString, int paramInt1, int paramInt2) throws IOException { this.se.write(paramString, paramInt1, paramInt2); }
  
  public void flush() throws IOException { this.se.flush(); }
  
  public void close() throws IOException { this.se.close(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\OutputStreamWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */