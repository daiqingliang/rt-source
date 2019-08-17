package sun.nio.cs;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.IllegalCharsetNameException;

public class StreamEncoder extends Writer {
  private static final int DEFAULT_BYTE_BUFFER_SIZE = 8192;
  
  private Charset cs;
  
  private CharsetEncoder encoder;
  
  private ByteBuffer bb;
  
  private final OutputStream out;
  
  private WritableByteChannel ch;
  
  private boolean haveLeftoverChar = false;
  
  private char leftoverChar;
  
  private CharBuffer lcb = null;
  
  private void ensureOpen() throws IOException {
    if (!this.isOpen)
      throw new IOException("Stream closed"); 
  }
  
  public static StreamEncoder forOutputStreamWriter(OutputStream paramOutputStream, Object paramObject, String paramString) throws UnsupportedEncodingException {
    String str = paramString;
    if (str == null)
      str = Charset.defaultCharset().name(); 
    try {
      if (Charset.isSupported(str))
        return new StreamEncoder(paramOutputStream, paramObject, Charset.forName(str)); 
    } catch (IllegalCharsetNameException illegalCharsetNameException) {}
    throw new UnsupportedEncodingException(str);
  }
  
  public static StreamEncoder forOutputStreamWriter(OutputStream paramOutputStream, Object paramObject, Charset paramCharset) { return new StreamEncoder(paramOutputStream, paramObject, paramCharset); }
  
  public static StreamEncoder forOutputStreamWriter(OutputStream paramOutputStream, Object paramObject, CharsetEncoder paramCharsetEncoder) { return new StreamEncoder(paramOutputStream, paramObject, paramCharsetEncoder); }
  
  public static StreamEncoder forEncoder(WritableByteChannel paramWritableByteChannel, CharsetEncoder paramCharsetEncoder, int paramInt) { return new StreamEncoder(paramWritableByteChannel, paramCharsetEncoder, paramInt); }
  
  public String getEncoding() { return isOpen() ? encodingName() : null; }
  
  public void flushBuffer() throws IOException {
    synchronized (this.lock) {
      if (isOpen()) {
        implFlushBuffer();
      } else {
        throw new IOException("Stream closed");
      } 
    } 
  }
  
  public void write(int paramInt) throws IOException {
    char[] arrayOfChar = new char[1];
    arrayOfChar[0] = (char)paramInt;
    write(arrayOfChar, 0, 1);
  }
  
  public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    synchronized (this.lock) {
      ensureOpen();
      if (paramInt1 < 0 || paramInt1 > paramArrayOfChar.length || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfChar.length || paramInt1 + paramInt2 < 0)
        throw new IndexOutOfBoundsException(); 
      if (paramInt2 == 0)
        return; 
      implWrite(paramArrayOfChar, paramInt1, paramInt2);
    } 
  }
  
  public void write(String paramString, int paramInt1, int paramInt2) throws IOException {
    if (paramInt2 < 0)
      throw new IndexOutOfBoundsException(); 
    char[] arrayOfChar = new char[paramInt2];
    paramString.getChars(paramInt1, paramInt1 + paramInt2, arrayOfChar, 0);
    write(arrayOfChar, 0, paramInt2);
  }
  
  public void flush() throws IOException {
    synchronized (this.lock) {
      ensureOpen();
      implFlush();
    } 
  }
  
  public void close() throws IOException {
    synchronized (this.lock) {
      if (!this.isOpen)
        return; 
      implClose();
      this.isOpen = false;
    } 
  }
  
  private boolean isOpen() { return this.isOpen; }
  
  private StreamEncoder(OutputStream paramOutputStream, Object paramObject, Charset paramCharset) { this(paramOutputStream, paramObject, paramCharset.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE)); }
  
  private StreamEncoder(OutputStream paramOutputStream, Object paramObject, CharsetEncoder paramCharsetEncoder) {
    super(paramObject);
    this.out = paramOutputStream;
    this.ch = null;
    this.cs = paramCharsetEncoder.charset();
    this.encoder = paramCharsetEncoder;
    if (this.ch == null)
      this.bb = ByteBuffer.allocate(8192); 
  }
  
  private StreamEncoder(WritableByteChannel paramWritableByteChannel, CharsetEncoder paramCharsetEncoder, int paramInt) {
    this.out = null;
    this.ch = paramWritableByteChannel;
    this.cs = paramCharsetEncoder.charset();
    this.encoder = paramCharsetEncoder;
    this.bb = ByteBuffer.allocate((paramInt < 0) ? 8192 : paramInt);
  }
  
  private void writeBytes() throws IOException {
    this.bb.flip();
    int i = this.bb.limit();
    int j = this.bb.position();
    assert j <= i;
    int k = (j <= i) ? (i - j) : 0;
    if (k > 0)
      if (this.ch != null) {
        if (this.ch.write(this.bb) != k && !$assertionsDisabled)
          throw new AssertionError(k); 
      } else {
        this.out.write(this.bb.array(), this.bb.arrayOffset() + j, k);
      }  
    this.bb.clear();
  }
  
  private void flushLeftoverChar(CharBuffer paramCharBuffer, boolean paramBoolean) throws IOException {
    if (!this.haveLeftoverChar && !paramBoolean)
      return; 
    if (this.lcb == null) {
      this.lcb = CharBuffer.allocate(2);
    } else {
      this.lcb.clear();
    } 
    if (this.haveLeftoverChar)
      this.lcb.put(this.leftoverChar); 
    if (paramCharBuffer != null && paramCharBuffer.hasRemaining())
      this.lcb.put(paramCharBuffer.get()); 
    this.lcb.flip();
    while (this.lcb.hasRemaining() || paramBoolean) {
      CoderResult coderResult = this.encoder.encode(this.lcb, this.bb, paramBoolean);
      if (coderResult.isUnderflow()) {
        if (this.lcb.hasRemaining()) {
          this.leftoverChar = this.lcb.get();
          if (paramCharBuffer != null && paramCharBuffer.hasRemaining())
            flushLeftoverChar(paramCharBuffer, paramBoolean); 
          return;
        } 
        break;
      } 
      if (coderResult.isOverflow()) {
        assert this.bb.position() > 0;
        writeBytes();
        continue;
      } 
      coderResult.throwException();
    } 
    this.haveLeftoverChar = false;
  }
  
  void implWrite(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    CharBuffer charBuffer = CharBuffer.wrap(paramArrayOfChar, paramInt1, paramInt2);
    if (this.haveLeftoverChar)
      flushLeftoverChar(charBuffer, false); 
    while (charBuffer.hasRemaining()) {
      CoderResult coderResult = this.encoder.encode(charBuffer, this.bb, false);
      if (coderResult.isUnderflow()) {
        assert charBuffer.remaining() <= 1 : charBuffer.remaining();
        if (charBuffer.remaining() == 1) {
          this.haveLeftoverChar = true;
          this.leftoverChar = charBuffer.get();
        } 
        break;
      } 
      if (coderResult.isOverflow()) {
        assert this.bb.position() > 0;
        writeBytes();
        continue;
      } 
      coderResult.throwException();
    } 
  }
  
  void implFlushBuffer() throws IOException {
    if (this.bb.position() > 0)
      writeBytes(); 
  }
  
  void implFlush() throws IOException {
    implFlushBuffer();
    if (this.out != null)
      this.out.flush(); 
  }
  
  void implClose() throws IOException {
    flushLeftoverChar(null, true);
    try {
      while (true) {
        CoderResult coderResult = this.encoder.flush(this.bb);
        if (coderResult.isUnderflow())
          break; 
        if (coderResult.isOverflow()) {
          assert this.bb.position() > 0;
          writeBytes();
          continue;
        } 
        coderResult.throwException();
      } 
      if (this.bb.position() > 0)
        writeBytes(); 
      if (this.ch != null) {
        this.ch.close();
      } else {
        this.out.close();
      } 
    } catch (IOException iOException) {
      this.encoder.reset();
      throw iOException;
    } 
  }
  
  String encodingName() { return (this.cs instanceof HistoricallyNamedCharset) ? ((HistoricallyNamedCharset)this.cs).historicalName() : this.cs.name(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\cs\StreamEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */