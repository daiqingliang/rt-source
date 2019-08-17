package sun.nio.cs;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

class UTF_16 extends Unicode {
  public UTF_16() { super("UTF-16", StandardCharsets.aliases_UTF_16); }
  
  public String historicalName() { return "UTF-16"; }
  
  public CharsetDecoder newDecoder() { return new Decoder(this); }
  
  public CharsetEncoder newEncoder() { return new Encoder(this); }
  
  private static class Decoder extends UnicodeDecoder {
    public Decoder(Charset param1Charset) { super(param1Charset, 0); }
  }
  
  private static class Encoder extends UnicodeEncoder {
    public Encoder(Charset param1Charset) { super(param1Charset, 0, true); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\cs\UTF_16.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */