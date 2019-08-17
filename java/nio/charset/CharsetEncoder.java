package java.nio.charset;

import java.lang.ref.WeakReference;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;

public abstract class CharsetEncoder {
  private final Charset charset;
  
  private final float averageBytesPerChar;
  
  private final float maxBytesPerChar;
  
  private byte[] replacement;
  
  private CodingErrorAction malformedInputAction = CodingErrorAction.REPORT;
  
  private CodingErrorAction unmappableCharacterAction = CodingErrorAction.REPORT;
  
  private static final int ST_RESET = 0;
  
  private static final int ST_CODING = 1;
  
  private static final int ST_END = 2;
  
  private static final int ST_FLUSHED = 3;
  
  private int state = 0;
  
  private static String[] stateNames = { "RESET", "CODING", "CODING_END", "FLUSHED" };
  
  private WeakReference<CharsetDecoder> cachedDecoder = null;
  
  protected CharsetEncoder(Charset paramCharset, float paramFloat1, float paramFloat2, byte[] paramArrayOfByte) {
    this.charset = paramCharset;
    if (paramFloat1 <= 0.0F)
      throw new IllegalArgumentException("Non-positive averageBytesPerChar"); 
    if (paramFloat2 <= 0.0F)
      throw new IllegalArgumentException("Non-positive maxBytesPerChar"); 
    if (!Charset.atBugLevel("1.4") && paramFloat1 > paramFloat2)
      throw new IllegalArgumentException("averageBytesPerChar exceeds maxBytesPerChar"); 
    this.replacement = paramArrayOfByte;
    this.averageBytesPerChar = paramFloat1;
    this.maxBytesPerChar = paramFloat2;
    replaceWith(paramArrayOfByte);
  }
  
  protected CharsetEncoder(Charset paramCharset, float paramFloat1, float paramFloat2) { this(paramCharset, paramFloat1, paramFloat2, new byte[] { 63 }); }
  
  public final Charset charset() { return this.charset; }
  
  public final byte[] replacement() { return Arrays.copyOf(this.replacement, this.replacement.length); }
  
  public final CharsetEncoder replaceWith(byte[] paramArrayOfByte) {
    if (paramArrayOfByte == null)
      throw new IllegalArgumentException("Null replacement"); 
    int i = paramArrayOfByte.length;
    if (i == 0)
      throw new IllegalArgumentException("Empty replacement"); 
    if (i > this.maxBytesPerChar)
      throw new IllegalArgumentException("Replacement too long"); 
    if (!isLegalReplacement(paramArrayOfByte))
      throw new IllegalArgumentException("Illegal replacement"); 
    this.replacement = Arrays.copyOf(paramArrayOfByte, paramArrayOfByte.length);
    implReplaceWith(this.replacement);
    return this;
  }
  
  protected void implReplaceWith(byte[] paramArrayOfByte) {}
  
  public boolean isLegalReplacement(byte[] paramArrayOfByte) {
    WeakReference weakReference = this.cachedDecoder;
    CharsetDecoder charsetDecoder = null;
    if (weakReference == null || (charsetDecoder = (CharsetDecoder)weakReference.get()) == null) {
      charsetDecoder = charset().newDecoder();
      charsetDecoder.onMalformedInput(CodingErrorAction.REPORT);
      charsetDecoder.onUnmappableCharacter(CodingErrorAction.REPORT);
      this.cachedDecoder = new WeakReference(charsetDecoder);
    } else {
      charsetDecoder.reset();
    } 
    ByteBuffer byteBuffer = ByteBuffer.wrap(paramArrayOfByte);
    CharBuffer charBuffer = CharBuffer.allocate((int)(byteBuffer.remaining() * charsetDecoder.maxCharsPerByte()));
    CoderResult coderResult = charsetDecoder.decode(byteBuffer, charBuffer, true);
    return !coderResult.isError();
  }
  
  public CodingErrorAction malformedInputAction() { return this.malformedInputAction; }
  
  public final CharsetEncoder onMalformedInput(CodingErrorAction paramCodingErrorAction) {
    if (paramCodingErrorAction == null)
      throw new IllegalArgumentException("Null action"); 
    this.malformedInputAction = paramCodingErrorAction;
    implOnMalformedInput(paramCodingErrorAction);
    return this;
  }
  
  protected void implOnMalformedInput(CodingErrorAction paramCodingErrorAction) {}
  
  public CodingErrorAction unmappableCharacterAction() { return this.unmappableCharacterAction; }
  
  public final CharsetEncoder onUnmappableCharacter(CodingErrorAction paramCodingErrorAction) {
    if (paramCodingErrorAction == null)
      throw new IllegalArgumentException("Null action"); 
    this.unmappableCharacterAction = paramCodingErrorAction;
    implOnUnmappableCharacter(paramCodingErrorAction);
    return this;
  }
  
  protected void implOnUnmappableCharacter(CodingErrorAction paramCodingErrorAction) {}
  
  public final float averageBytesPerChar() { return this.averageBytesPerChar; }
  
  public final float maxBytesPerChar() { return this.maxBytesPerChar; }
  
  public final CoderResult encode(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer, boolean paramBoolean) {
    byte b = paramBoolean ? 2 : 1;
    if (this.state != 0 && this.state != 1 && (!paramBoolean || this.state != 2))
      throwIllegalStateException(this.state, b); 
    this.state = b;
    while (true) {
      CoderResult coderResult;
      try {
        coderResult = encodeLoop(paramCharBuffer, paramByteBuffer);
      } catch (BufferUnderflowException bufferUnderflowException) {
        throw new CoderMalfunctionError(bufferUnderflowException);
      } catch (BufferOverflowException bufferOverflowException) {
        throw new CoderMalfunctionError(bufferOverflowException);
      } 
      if (coderResult.isOverflow())
        return coderResult; 
      if (coderResult.isUnderflow())
        if (paramBoolean && paramCharBuffer.hasRemaining()) {
          coderResult = CoderResult.malformedForLength(paramCharBuffer.remaining());
        } else {
          return coderResult;
        }  
      CodingErrorAction codingErrorAction = null;
      if (coderResult.isMalformed()) {
        codingErrorAction = this.malformedInputAction;
      } else if (coderResult.isUnmappable()) {
        codingErrorAction = this.unmappableCharacterAction;
      } else {
        assert false : coderResult.toString();
      } 
      if (codingErrorAction == CodingErrorAction.REPORT)
        return coderResult; 
      if (codingErrorAction == CodingErrorAction.REPLACE) {
        if (paramByteBuffer.remaining() < this.replacement.length)
          return CoderResult.OVERFLOW; 
        paramByteBuffer.put(this.replacement);
      } 
      if (codingErrorAction == CodingErrorAction.IGNORE || codingErrorAction == CodingErrorAction.REPLACE) {
        paramCharBuffer.position(paramCharBuffer.position() + coderResult.length());
        continue;
      } 
      assert false;
    } 
    throw new AssertionError();
  }
  
  public final CoderResult flush(ByteBuffer paramByteBuffer) {
    if (this.state == 2) {
      CoderResult coderResult = implFlush(paramByteBuffer);
      if (coderResult.isUnderflow())
        this.state = 3; 
      return coderResult;
    } 
    if (this.state != 3)
      throwIllegalStateException(this.state, 3); 
    return CoderResult.UNDERFLOW;
  }
  
  protected CoderResult implFlush(ByteBuffer paramByteBuffer) { return CoderResult.UNDERFLOW; }
  
  public final CharsetEncoder reset() {
    implReset();
    this.state = 0;
    return this;
  }
  
  protected void implReset() {}
  
  protected abstract CoderResult encodeLoop(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer);
  
  public final ByteBuffer encode(CharBuffer paramCharBuffer) throws CharacterCodingException {
    int i = (int)(paramCharBuffer.remaining() * averageBytesPerChar());
    ByteBuffer byteBuffer = ByteBuffer.allocate(i);
    if (i == 0 && paramCharBuffer.remaining() == 0)
      return byteBuffer; 
    reset();
    while (true) {
      CoderResult coderResult = paramCharBuffer.hasRemaining() ? encode(paramCharBuffer, byteBuffer, true) : CoderResult.UNDERFLOW;
      if (coderResult.isUnderflow())
        coderResult = flush(byteBuffer); 
      if (coderResult.isUnderflow())
        break; 
      if (coderResult.isOverflow()) {
        i = 2 * i + 1;
        ByteBuffer byteBuffer1 = ByteBuffer.allocate(i);
        byteBuffer.flip();
        byteBuffer1.put(byteBuffer);
        byteBuffer = byteBuffer1;
        continue;
      } 
      coderResult.throwException();
    } 
    byteBuffer.flip();
    return byteBuffer;
  }
  
  private boolean canEncode(CharBuffer paramCharBuffer) {
    if (this.state == 3) {
      reset();
    } else if (this.state != 0) {
      throwIllegalStateException(this.state, 1);
    } 
    codingErrorAction1 = malformedInputAction();
    codingErrorAction2 = unmappableCharacterAction();
    try {
      onMalformedInput(CodingErrorAction.REPORT);
      onUnmappableCharacter(CodingErrorAction.REPORT);
      encode(paramCharBuffer);
    } catch (CharacterCodingException characterCodingException) {
      return false;
    } finally {
      onMalformedInput(codingErrorAction1);
      onUnmappableCharacter(codingErrorAction2);
      reset();
    } 
    return true;
  }
  
  public boolean canEncode(char paramChar) {
    CharBuffer charBuffer = CharBuffer.allocate(1);
    charBuffer.put(paramChar);
    charBuffer.flip();
    return canEncode(charBuffer);
  }
  
  public boolean canEncode(CharSequence paramCharSequence) {
    CharBuffer charBuffer;
    if (paramCharSequence instanceof CharBuffer) {
      charBuffer = ((CharBuffer)paramCharSequence).duplicate();
    } else {
      charBuffer = CharBuffer.wrap(paramCharSequence.toString());
    } 
    return canEncode(charBuffer);
  }
  
  private void throwIllegalStateException(int paramInt1, int paramInt2) { throw new IllegalStateException("Current state = " + stateNames[paramInt1] + ", new state = " + stateNames[paramInt2]); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\charset\CharsetEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */