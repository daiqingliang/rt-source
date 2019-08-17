package java.io;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.StreamDecoder;

public class InputStreamReader extends Reader {
  private final StreamDecoder sd;
  
  public InputStreamReader(InputStream paramInputStream) {
    super(paramInputStream);
    try {
      this.sd = StreamDecoder.forInputStreamReader(paramInputStream, this, (String)null);
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new Error(unsupportedEncodingException);
    } 
  }
  
  public InputStreamReader(InputStream paramInputStream, String paramString) throws UnsupportedEncodingException {
    super(paramInputStream);
    if (paramString == null)
      throw new NullPointerException("charsetName"); 
    this.sd = StreamDecoder.forInputStreamReader(paramInputStream, this, paramString);
  }
  
  public InputStreamReader(InputStream paramInputStream, Charset paramCharset) {
    super(paramInputStream);
    if (paramCharset == null)
      throw new NullPointerException("charset"); 
    this.sd = StreamDecoder.forInputStreamReader(paramInputStream, this, paramCharset);
  }
  
  public InputStreamReader(InputStream paramInputStream, CharsetDecoder paramCharsetDecoder) {
    super(paramInputStream);
    if (paramCharsetDecoder == null)
      throw new NullPointerException("charset decoder"); 
    this.sd = StreamDecoder.forInputStreamReader(paramInputStream, this, paramCharsetDecoder);
  }
  
  public String getEncoding() { return this.sd.getEncoding(); }
  
  public int read() throws IOException { return this.sd.read(); }
  
  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException { return this.sd.read(paramArrayOfChar, paramInt1, paramInt2); }
  
  public boolean ready() throws IOException { return this.sd.ready(); }
  
  public void close() throws IOException { this.sd.close(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\InputStreamReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */