package java.nio.charset;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

public abstract class CharsetDecoder {
  private final Charset charset;
  
  private final float averageCharsPerByte;
  
  private final float maxCharsPerByte;
  
  private String replacement;
  
  private CodingErrorAction malformedInputAction = CodingErrorAction.REPORT;
  
  private CodingErrorAction unmappableCharacterAction = CodingErrorAction.REPORT;
  
  private static final int ST_RESET = 0;
  
  private static final int ST_CODING = 1;
  
  private static final int ST_END = 2;
  
  private static final int ST_FLUSHED = 3;
  
  private int state = 0;
  
  private static String[] stateNames = { "RESET", "CODING", "CODING_END", "FLUSHED" };
  
  private CharsetDecoder(Charset paramCharset, float paramFloat1, float paramFloat2, String paramString) {
    this.charset = paramCharset;
    if (paramFloat1 <= 0.0F)
      throw new IllegalArgumentException("Non-positive averageCharsPerByte"); 
    if (paramFloat2 <= 0.0F)
      throw new IllegalArgumentException("Non-positive maxCharsPerByte"); 
    if (!Charset.atBugLevel("1.4") && paramFloat1 > paramFloat2)
      throw new IllegalArgumentException("averageCharsPerByte exceeds maxCharsPerByte"); 
    this.replacement = paramString;
    this.averageCharsPerByte = paramFloat1;
    this.maxCharsPerByte = paramFloat2;
    replaceWith(paramString);
  }
  
  protected CharsetDecoder(Charset paramCharset, float paramFloat1, float paramFloat2) { this(paramCharset, paramFloat1, paramFloat2, "�"); }
  
  public final Charset charset() { return this.charset; }
  
  public final String replacement() { return this.replacement; }
  
  public final CharsetDecoder replaceWith(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("Null replacement"); 
    int i = paramString.length();
    if (i == 0)
      throw new IllegalArgumentException("Empty replacement"); 
    if (i > this.maxCharsPerByte)
      throw new IllegalArgumentException("Replacement too long"); 
    this.replacement = paramString;
    implReplaceWith(this.replacement);
    return this;
  }
  
  protected void implReplaceWith(String paramString) {}
  
  public CodingErrorAction malformedInputAction() { return this.malformedInputAction; }
  
  public final CharsetDecoder onMalformedInput(CodingErrorAction paramCodingErrorAction) {
    if (paramCodingErrorAction == null)
      throw new IllegalArgumentException("Null action"); 
    this.malformedInputAction = paramCodingErrorAction;
    implOnMalformedInput(paramCodingErrorAction);
    return this;
  }
  
  protected void implOnMalformedInput(CodingErrorAction paramCodingErrorAction) {}
  
  public CodingErrorAction unmappableCharacterAction() { return this.unmappableCharacterAction; }
  
  public final CharsetDecoder onUnmappableCharacter(CodingErrorAction paramCodingErrorAction) {
    if (paramCodingErrorAction == null)
      throw new IllegalArgumentException("Null action"); 
    this.unmappableCharacterAction = paramCodingErrorAction;
    implOnUnmappableCharacter(paramCodingErrorAction);
    return this;
  }
  
  protected void implOnUnmappableCharacter(CodingErrorAction paramCodingErrorAction) {}
  
  public final float averageCharsPerByte() { return this.averageCharsPerByte; }
  
  public final float maxCharsPerByte() { return this.maxCharsPerByte; }
  
  public final CoderResult decode(ByteBuffer paramByteBuffer, CharBuffer paramCharBuffer, boolean paramBoolean) {
    byte b = paramBoolean ? 2 : 1;
    if (this.state != 0 && this.state != 1 && (!paramBoolean || this.state != 2))
      throwIllegalStateException(this.state, b); 
    this.state = b;
    while (true) {
      CoderResult coderResult;
      try {
        coderResult = decodeLoop(paramByteBuffer, paramCharBuffer);
      } catch (BufferUnderflowException bufferUnderflowException) {
        throw new CoderMalfunctionError(bufferUnderflowException);
      } catch (BufferOverflowException bufferOverflowException) {
        throw new CoderMalfunctionError(bufferOverflowException);
      } 
      if (coderResult.isOverflow())
        return coderResult; 
      if (coderResult.isUnderflow())
        if (paramBoolean && paramByteBuffer.hasRemaining()) {
          coderResult = CoderResult.malformedForLength(paramByteBuffer.remaining());
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
        if (paramCharBuffer.remaining() < this.replacement.length())
          return CoderResult.OVERFLOW; 
        paramCharBuffer.put(this.replacement);
      } 
      if (codingErrorAction == CodingErrorAction.IGNORE || codingErrorAction == CodingErrorAction.REPLACE) {
        paramByteBuffer.position(paramByteBuffer.position() + coderResult.length());
        continue;
      } 
      assert false;
    } 
    throw new AssertionError();
  }
  
  public final CoderResult flush(CharBuffer paramCharBuffer) {
    if (this.state == 2) {
      CoderResult coderResult = implFlush(paramCharBuffer);
      if (coderResult.isUnderflow())
        this.state = 3; 
      return coderResult;
    } 
    if (this.state != 3)
      throwIllegalStateException(this.state, 3); 
    return CoderResult.UNDERFLOW;
  }
  
  protected CoderResult implFlush(CharBuffer paramCharBuffer) { return CoderResult.UNDERFLOW; }
  
  public final CharsetDecoder reset() {
    implReset();
    this.state = 0;
    return this;
  }
  
  protected void implReset() {}
  
  protected abstract CoderResult decodeLoop(ByteBuffer paramByteBuffer, CharBuffer paramCharBuffer);
  
  public final CharBuffer decode(ByteBuffer paramByteBuffer) throws CharacterCodingException {
    int i = (int)(paramByteBuffer.remaining() * averageCharsPerByte());
    CharBuffer charBuffer = CharBuffer.allocate(i);
    if (i == 0 && paramByteBuffer.remaining() == 0)
      return charBuffer; 
    reset();
    while (true) {
      CoderResult coderResult = paramByteBuffer.hasRemaining() ? decode(paramByteBuffer, charBuffer, true) : CoderResult.UNDERFLOW;
      if (coderResult.isUnderflow())
        coderResult = flush(charBuffer); 
      if (coderResult.isUnderflow())
        break; 
      if (coderResult.isOverflow()) {
        i = 2 * i + 1;
        CharBuffer charBuffer1 = CharBuffer.allocate(i);
        charBuffer.flip();
        charBuffer1.put(charBuffer);
        charBuffer = charBuffer1;
        continue;
      } 
      coderResult.throwException();
    } 
    charBuffer.flip();
    return charBuffer;
  }
  
  public boolean isAutoDetecting() { return false; }
  
  public boolean isCharsetDetected() { throw new UnsupportedOperationException(); }
  
  public Charset detectedCharset() { throw new UnsupportedOperationException(); }
  
  private void throwIllegalStateException(int paramInt1, int paramInt2) { throw new IllegalStateException("Current state = " + stateNames[paramInt1] + ", new state = " + stateNames[paramInt2]); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\charset\CharsetDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */