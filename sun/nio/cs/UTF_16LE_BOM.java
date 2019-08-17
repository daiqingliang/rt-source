package sun.nio.cs;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

class UTF_16LE_BOM extends Unicode {
  public UTF_16LE_BOM() { super("x-UTF-16LE-BOM", StandardCharsets.aliases_UTF_16LE_BOM); }
  
  public String historicalName() { return "UnicodeLittle"; }
  
  public CharsetDecoder newDecoder() { return new Decoder(this); }
  
  public CharsetEncoder newEncoder() { return new Encoder(this); }
  
  private static class Decoder extends UnicodeDecoder {
    public Decoder(Charset param1Charset) { super(param1Charset, 0, 2); }
  }
  
  private static class Encoder extends UnicodeEncoder {
    public Encoder(Charset param1Charset) { super(param1Charset, 1, true); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\cs\UTF_16LE_BOM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */