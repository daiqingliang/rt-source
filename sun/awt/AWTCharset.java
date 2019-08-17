package sun.awt;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

public class AWTCharset extends Charset {
  protected Charset awtCs;
  
  protected Charset javaCs;
  
  public AWTCharset(String paramString, Charset paramCharset) {
    super(paramString, null);
    this.javaCs = paramCharset;
    this.awtCs = this;
  }
  
  public boolean contains(Charset paramCharset) { return (this.javaCs == null) ? false : this.javaCs.contains(paramCharset); }
  
  public CharsetEncoder newEncoder() {
    if (this.javaCs == null)
      throw new Error("Encoder is not supported by this Charset"); 
    return new Encoder(this.javaCs.newEncoder());
  }
  
  public CharsetDecoder newDecoder() {
    if (this.javaCs == null)
      throw new Error("Decoder is not supported by this Charset"); 
    return new Decoder(this.javaCs.newDecoder());
  }
  
  public class Decoder extends CharsetDecoder {
    protected CharsetDecoder dec;
    
    private String nr;
    
    ByteBuffer fbb = ByteBuffer.allocate(0);
    
    protected Decoder(AWTCharset this$0) { this(this$0.javaCs.newDecoder()); }
    
    protected Decoder(CharsetDecoder param1CharsetDecoder) {
      super(AWTCharset.this.awtCs, param1CharsetDecoder.averageCharsPerByte(), param1CharsetDecoder.maxCharsPerByte());
      this.dec = param1CharsetDecoder;
    }
    
    protected CoderResult decodeLoop(ByteBuffer param1ByteBuffer, CharBuffer param1CharBuffer) { return this.dec.decode(param1ByteBuffer, param1CharBuffer, true); }
    
    protected CoderResult implFlush(CharBuffer param1CharBuffer) {
      this.dec.decode(this.fbb, param1CharBuffer, true);
      return this.dec.flush(param1CharBuffer);
    }
    
    protected void implReset() { this.dec.reset(); }
    
    protected void implReplaceWith(String param1String) {
      if (this.dec != null)
        this.dec.replaceWith(param1String); 
    }
    
    protected void implOnMalformedInput(CodingErrorAction param1CodingErrorAction) { this.dec.onMalformedInput(param1CodingErrorAction); }
    
    protected void implOnUnmappableCharacter(CodingErrorAction param1CodingErrorAction) { this.dec.onUnmappableCharacter(param1CodingErrorAction); }
  }
  
  public class Encoder extends CharsetEncoder {
    protected CharsetEncoder enc;
    
    protected Encoder(AWTCharset this$0) { this(this$0.javaCs.newEncoder()); }
    
    protected Encoder(CharsetEncoder param1CharsetEncoder) {
      super(AWTCharset.this.awtCs, param1CharsetEncoder.averageBytesPerChar(), param1CharsetEncoder.maxBytesPerChar());
      this.enc = param1CharsetEncoder;
    }
    
    public boolean canEncode(char param1Char) { return this.enc.canEncode(param1Char); }
    
    public boolean canEncode(CharSequence param1CharSequence) { return this.enc.canEncode(param1CharSequence); }
    
    protected CoderResult encodeLoop(CharBuffer param1CharBuffer, ByteBuffer param1ByteBuffer) { return this.enc.encode(param1CharBuffer, param1ByteBuffer, true); }
    
    protected CoderResult implFlush(ByteBuffer param1ByteBuffer) { return this.enc.flush(param1ByteBuffer); }
    
    protected void implReset() { this.enc.reset(); }
    
    protected void implReplaceWith(byte[] param1ArrayOfByte) {
      if (this.enc != null)
        this.enc.replaceWith(param1ArrayOfByte); 
    }
    
    protected void implOnMalformedInput(CodingErrorAction param1CodingErrorAction) { this.enc.onMalformedInput(param1CodingErrorAction); }
    
    protected void implOnUnmappableCharacter(CodingErrorAction param1CodingErrorAction) { this.enc.onUnmappableCharacter(param1CodingErrorAction); }
    
    public boolean isLegalReplacement(byte[] param1ArrayOfByte) { return true; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\AWTCharset.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */