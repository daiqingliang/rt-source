package sun.nio.cs;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

class UTF_16LE extends Unicode {
  public UTF_16LE() { super("UTF-16LE", StandardCharsets.aliases_UTF_16LE); }
  
  public String historicalName() { return "UnicodeLittleUnmarked"; }
  
  public CharsetDecoder newDecoder() { return new Decoder(this); }
  
  public CharsetEncoder newEncoder() { return new Encoder(this); }
  
  private static class Decoder extends UnicodeDecoder {
    public Decoder(Charset param1Charset) { super(param1Charset, 2); }
  }
  
  private static class Encoder extends UnicodeEncoder {
    public Encoder(Charset param1Charset) { super(param1Charset, 1, false); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\cs\UTF_16LE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */