package java.lang;

import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;
import sun.misc.MessageUtils;
import sun.nio.cs.ArrayDecoder;
import sun.nio.cs.ArrayEncoder;
import sun.nio.cs.HistoricallyNamedCharset;

class StringCoding {
  private static final ThreadLocal<SoftReference<StringDecoder>> decoder = new ThreadLocal();
  
  private static final ThreadLocal<SoftReference<StringEncoder>> encoder = new ThreadLocal();
  
  private static boolean warnUnsupportedCharset = true;
  
  private static <T> T deref(ThreadLocal<SoftReference<T>> paramThreadLocal) {
    SoftReference softReference = (SoftReference)paramThreadLocal.get();
    return (softReference == null) ? null : (T)softReference.get();
  }
  
  private static <T> void set(ThreadLocal<SoftReference<T>> paramThreadLocal, T paramT) { paramThreadLocal.set(new SoftReference(paramT)); }
  
  private static byte[] safeTrim(byte[] paramArrayOfByte, int paramInt, Charset paramCharset, boolean paramBoolean) { return (paramInt == paramArrayOfByte.length && (paramBoolean || System.getSecurityManager() == null)) ? paramArrayOfByte : Arrays.copyOf(paramArrayOfByte, paramInt); }
  
  private static char[] safeTrim(char[] paramArrayOfChar, int paramInt, Charset paramCharset, boolean paramBoolean) { return (paramInt == paramArrayOfChar.length && (paramBoolean || System.getSecurityManager() == null)) ? paramArrayOfChar : Arrays.copyOf(paramArrayOfChar, paramInt); }
  
  private static int scale(int paramInt, float paramFloat) { return (int)(paramInt * paramFloat); }
  
  private static Charset lookupCharset(String paramString) {
    if (Charset.isSupported(paramString))
      try {
        return Charset.forName(paramString);
      } catch (UnsupportedCharsetException unsupportedCharsetException) {
        throw new Error(unsupportedCharsetException);
      }  
    return null;
  }
  
  private static void warnUnsupportedCharset(String paramString) {
    if (warnUnsupportedCharset) {
      MessageUtils.err("WARNING: Default charset " + paramString + " not supported, using ISO-8859-1 instead");
      warnUnsupportedCharset = false;
    } 
  }
  
  static char[] decode(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws UnsupportedEncodingException {
    StringDecoder stringDecoder = (StringDecoder)deref(decoder);
    String str = (paramString == null) ? "ISO-8859-1" : paramString;
    if (stringDecoder == null || (!str.equals(stringDecoder.requestedCharsetName()) && !str.equals(stringDecoder.charsetName()))) {
      stringDecoder = null;
      try {
        Charset charset = lookupCharset(str);
        if (charset != null)
          stringDecoder = new StringDecoder(charset, str, null); 
      } catch (IllegalCharsetNameException illegalCharsetNameException) {}
      if (stringDecoder == null)
        throw new UnsupportedEncodingException(str); 
      set(decoder, stringDecoder);
    } 
    return stringDecoder.decode(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  static char[] decode(Charset paramCharset, byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    CharsetDecoder charsetDecoder = paramCharset.newDecoder();
    int i = scale(paramInt2, charsetDecoder.maxCharsPerByte());
    char[] arrayOfChar = new char[i];
    if (paramInt2 == 0)
      return arrayOfChar; 
    boolean bool = false;
    if (System.getSecurityManager() != null)
      if (!(bool = (paramCharset.getClass().getClassLoader0() == null))) {
        paramArrayOfByte = Arrays.copyOfRange(paramArrayOfByte, paramInt1, paramInt1 + paramInt2);
        paramInt1 = 0;
      }  
    charsetDecoder.onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).reset();
    if (charsetDecoder instanceof ArrayDecoder) {
      int j = ((ArrayDecoder)charsetDecoder).decode(paramArrayOfByte, paramInt1, paramInt2, arrayOfChar);
      return safeTrim(arrayOfChar, j, paramCharset, bool);
    } 
    ByteBuffer byteBuffer = ByteBuffer.wrap(paramArrayOfByte, paramInt1, paramInt2);
    CharBuffer charBuffer = CharBuffer.wrap(arrayOfChar);
    try {
      CoderResult coderResult = charsetDecoder.decode(byteBuffer, charBuffer, true);
      if (!coderResult.isUnderflow())
        coderResult.throwException(); 
      coderResult = charsetDecoder.flush(charBuffer);
      if (!coderResult.isUnderflow())
        coderResult.throwException(); 
    } catch (CharacterCodingException characterCodingException) {
      throw new Error(characterCodingException);
    } 
    return safeTrim(arrayOfChar, charBuffer.position(), paramCharset, bool);
  }
  
  static char[] decode(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    String str = Charset.defaultCharset().name();
    try {
      return decode(str, paramArrayOfByte, paramInt1, paramInt2);
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      warnUnsupportedCharset(str);
      try {
        return decode("ISO-8859-1", paramArrayOfByte, paramInt1, paramInt2);
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
        MessageUtils.err("ISO-8859-1 charset not available: " + unsupportedEncodingException.toString());
        System.exit(1);
        return null;
      } 
    } 
  }
  
  static byte[] encode(String paramString, char[] paramArrayOfChar, int paramInt1, int paramInt2) throws UnsupportedEncodingException {
    StringEncoder stringEncoder = (StringEncoder)deref(encoder);
    String str = (paramString == null) ? "ISO-8859-1" : paramString;
    if (stringEncoder == null || (!str.equals(stringEncoder.requestedCharsetName()) && !str.equals(stringEncoder.charsetName()))) {
      stringEncoder = null;
      try {
        Charset charset = lookupCharset(str);
        if (charset != null)
          stringEncoder = new StringEncoder(charset, str, null); 
      } catch (IllegalCharsetNameException illegalCharsetNameException) {}
      if (stringEncoder == null)
        throw new UnsupportedEncodingException(str); 
      set(encoder, stringEncoder);
    } 
    return stringEncoder.encode(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  static byte[] encode(Charset paramCharset, char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    CharsetEncoder charsetEncoder = paramCharset.newEncoder();
    int i = scale(paramInt2, charsetEncoder.maxBytesPerChar());
    byte[] arrayOfByte = new byte[i];
    if (paramInt2 == 0)
      return arrayOfByte; 
    boolean bool = false;
    if (System.getSecurityManager() != null)
      if (!(bool = (paramCharset.getClass().getClassLoader0() == null))) {
        paramArrayOfChar = Arrays.copyOfRange(paramArrayOfChar, paramInt1, paramInt1 + paramInt2);
        paramInt1 = 0;
      }  
    charsetEncoder.onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).reset();
    if (charsetEncoder instanceof ArrayEncoder) {
      int j = ((ArrayEncoder)charsetEncoder).encode(paramArrayOfChar, paramInt1, paramInt2, arrayOfByte);
      return safeTrim(arrayOfByte, j, paramCharset, bool);
    } 
    ByteBuffer byteBuffer = ByteBuffer.wrap(arrayOfByte);
    CharBuffer charBuffer = CharBuffer.wrap(paramArrayOfChar, paramInt1, paramInt2);
    try {
      CoderResult coderResult = charsetEncoder.encode(charBuffer, byteBuffer, true);
      if (!coderResult.isUnderflow())
        coderResult.throwException(); 
      coderResult = charsetEncoder.flush(byteBuffer);
      if (!coderResult.isUnderflow())
        coderResult.throwException(); 
    } catch (CharacterCodingException characterCodingException) {
      throw new Error(characterCodingException);
    } 
    return safeTrim(arrayOfByte, byteBuffer.position(), paramCharset, bool);
  }
  
  static byte[] encode(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    String str = Charset.defaultCharset().name();
    try {
      return encode(str, paramArrayOfChar, paramInt1, paramInt2);
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      warnUnsupportedCharset(str);
      try {
        return encode("ISO-8859-1", paramArrayOfChar, paramInt1, paramInt2);
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
        MessageUtils.err("ISO-8859-1 charset not available: " + unsupportedEncodingException.toString());
        System.exit(1);
        return null;
      } 
    } 
  }
  
  private static class StringDecoder {
    private final String requestedCharsetName;
    
    private final Charset cs;
    
    private final CharsetDecoder cd;
    
    private final boolean isTrusted;
    
    private StringDecoder(Charset param1Charset, String param1String) {
      this.requestedCharsetName = param1String;
      this.cs = param1Charset;
      this.cd = param1Charset.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
      this.isTrusted = (param1Charset.getClass().getClassLoader0() == null);
    }
    
    String charsetName() { return (this.cs instanceof HistoricallyNamedCharset) ? ((HistoricallyNamedCharset)this.cs).historicalName() : this.cs.name(); }
    
    final String requestedCharsetName() { return this.requestedCharsetName; }
    
    char[] decode(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) {
      int i = StringCoding.scale(param1Int2, this.cd.maxCharsPerByte());
      char[] arrayOfChar = new char[i];
      if (param1Int2 == 0)
        return arrayOfChar; 
      if (this.cd instanceof ArrayDecoder) {
        int j = ((ArrayDecoder)this.cd).decode(param1ArrayOfByte, param1Int1, param1Int2, arrayOfChar);
        return StringCoding.safeTrim(arrayOfChar, j, this.cs, this.isTrusted);
      } 
      this.cd.reset();
      ByteBuffer byteBuffer = ByteBuffer.wrap(param1ArrayOfByte, param1Int1, param1Int2);
      CharBuffer charBuffer = CharBuffer.wrap(arrayOfChar);
      try {
        CoderResult coderResult = this.cd.decode(byteBuffer, charBuffer, true);
        if (!coderResult.isUnderflow())
          coderResult.throwException(); 
        coderResult = this.cd.flush(charBuffer);
        if (!coderResult.isUnderflow())
          coderResult.throwException(); 
      } catch (CharacterCodingException characterCodingException) {
        throw new Error(characterCodingException);
      } 
      return StringCoding.safeTrim(arrayOfChar, charBuffer.position(), this.cs, this.isTrusted);
    }
  }
  
  private static class StringEncoder {
    private Charset cs;
    
    private CharsetEncoder ce;
    
    private final String requestedCharsetName;
    
    private final boolean isTrusted;
    
    private StringEncoder(Charset param1Charset, String param1String) {
      this.requestedCharsetName = param1String;
      this.cs = param1Charset;
      this.ce = param1Charset.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
      this.isTrusted = (param1Charset.getClass().getClassLoader0() == null);
    }
    
    String charsetName() { return (this.cs instanceof HistoricallyNamedCharset) ? ((HistoricallyNamedCharset)this.cs).historicalName() : this.cs.name(); }
    
    final String requestedCharsetName() { return this.requestedCharsetName; }
    
    byte[] encode(char[] param1ArrayOfChar, int param1Int1, int param1Int2) {
      int i = StringCoding.scale(param1Int2, this.ce.maxBytesPerChar());
      byte[] arrayOfByte = new byte[i];
      if (param1Int2 == 0)
        return arrayOfByte; 
      if (this.ce instanceof ArrayEncoder) {
        int j = ((ArrayEncoder)this.ce).encode(param1ArrayOfChar, param1Int1, param1Int2, arrayOfByte);
        return StringCoding.safeTrim(arrayOfByte, j, this.cs, this.isTrusted);
      } 
      this.ce.reset();
      ByteBuffer byteBuffer = ByteBuffer.wrap(arrayOfByte);
      CharBuffer charBuffer = CharBuffer.wrap(param1ArrayOfChar, param1Int1, param1Int2);
      try {
        CoderResult coderResult = this.ce.encode(charBuffer, byteBuffer, true);
        if (!coderResult.isUnderflow())
          coderResult.throwException(); 
        coderResult = this.ce.flush(byteBuffer);
        if (!coderResult.isUnderflow())
          coderResult.throwException(); 
      } catch (CharacterCodingException characterCodingException) {
        throw new Error(characterCodingException);
      } 
      return StringCoding.safeTrim(arrayOfByte, byteBuffer.position(), this.cs, this.isTrusted);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\StringCoding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */