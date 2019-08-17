package sun.nio.cs;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class UTF_32BE_BOM extends Unicode {
  public UTF_32BE_BOM() { super("X-UTF-32BE-BOM", StandardCharsets.aliases_UTF_32BE_BOM); }
  
  public String historicalName() { return "X-UTF-32BE-BOM"; }
  
  public CharsetDecoder newDecoder() { return new UTF_32Coder.Decoder(this, 1); }
  
  public CharsetEncoder newEncoder() { return new UTF_32Coder.Encoder(this, 1, true); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\cs\UTF_32BE_BOM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */