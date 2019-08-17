package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnmappableCharacterException;
import java.nio.charset.UnsupportedCharsetException;

public class CodeSetConversion {
  private static CodeSetConversion implementation;
  
  private static final int FALLBACK_CODESET = 0;
  
  private CodeSetCache cache = new CodeSetCache();
  
  public CTBConverter getCTBConverter(OSFCodeSetRegistry.Entry paramEntry) {
    byte b = !paramEntry.isFixedWidth() ? 1 : paramEntry.getMaxBytesPerChar();
    return new JavaCTBConverter(paramEntry, b);
  }
  
  public CTBConverter getCTBConverter(OSFCodeSetRegistry.Entry paramEntry, boolean paramBoolean1, boolean paramBoolean2) {
    if (paramEntry == OSFCodeSetRegistry.UCS_2)
      return new UTF16CTBConverter(paramBoolean1); 
    if (paramEntry == OSFCodeSetRegistry.UTF_16)
      return paramBoolean2 ? new UTF16CTBConverter() : new UTF16CTBConverter(paramBoolean1); 
    byte b = !paramEntry.isFixedWidth() ? 1 : paramEntry.getMaxBytesPerChar();
    return new JavaCTBConverter(paramEntry, b);
  }
  
  public BTCConverter getBTCConverter(OSFCodeSetRegistry.Entry paramEntry) { return new JavaBTCConverter(paramEntry); }
  
  public BTCConverter getBTCConverter(OSFCodeSetRegistry.Entry paramEntry, boolean paramBoolean) { return (paramEntry == OSFCodeSetRegistry.UTF_16 || paramEntry == OSFCodeSetRegistry.UCS_2) ? new UTF16BTCConverter(paramBoolean) : new JavaBTCConverter(paramEntry); }
  
  private int selectEncoding(CodeSetComponentInfo.CodeSetComponent paramCodeSetComponent1, CodeSetComponentInfo.CodeSetComponent paramCodeSetComponent2) {
    int i = paramCodeSetComponent2.nativeCodeSet;
    if (i == 0)
      if (paramCodeSetComponent2.conversionCodeSets.length > 0) {
        i = paramCodeSetComponent2.conversionCodeSets[0];
      } else {
        return 0;
      }  
    if (paramCodeSetComponent1.nativeCodeSet == i)
      return i; 
    byte b;
    for (b = 0; b < paramCodeSetComponent1.conversionCodeSets.length; b++) {
      if (i == paramCodeSetComponent1.conversionCodeSets[b])
        return i; 
    } 
    for (b = 0; b < paramCodeSetComponent2.conversionCodeSets.length; b++) {
      if (paramCodeSetComponent1.nativeCodeSet == paramCodeSetComponent2.conversionCodeSets[b])
        return paramCodeSetComponent1.nativeCodeSet; 
    } 
    for (b = 0; b < paramCodeSetComponent2.conversionCodeSets.length; b++) {
      for (byte b1 = 0; b1 < paramCodeSetComponent1.conversionCodeSets.length; b1++) {
        if (paramCodeSetComponent2.conversionCodeSets[b] == paramCodeSetComponent1.conversionCodeSets[b1])
          return paramCodeSetComponent2.conversionCodeSets[b]; 
      } 
    } 
    return 0;
  }
  
  public CodeSetComponentInfo.CodeSetContext negotiate(CodeSetComponentInfo paramCodeSetComponentInfo1, CodeSetComponentInfo paramCodeSetComponentInfo2) {
    int i = selectEncoding(paramCodeSetComponentInfo1.getCharComponent(), paramCodeSetComponentInfo2.getCharComponent());
    if (i == 0)
      i = OSFCodeSetRegistry.UTF_8.getNumber(); 
    int j = selectEncoding(paramCodeSetComponentInfo1.getWCharComponent(), paramCodeSetComponentInfo2.getWCharComponent());
    if (j == 0)
      j = OSFCodeSetRegistry.UTF_16.getNumber(); 
    return new CodeSetComponentInfo.CodeSetContext(i, j);
  }
  
  private CodeSetConversion() {}
  
  public static final CodeSetConversion impl() { return CodeSetConversionHolder.csc; }
  
  public static abstract class BTCConverter {
    public abstract boolean isFixedWidthEncoding();
    
    public abstract int getFixedCharWidth();
    
    public abstract int getNumChars();
    
    public abstract char[] getChars(byte[] param1ArrayOfByte, int param1Int1, int param1Int2);
  }
  
  public static abstract class CTBConverter {
    public abstract void convert(char param1Char);
    
    public abstract void convert(String param1String);
    
    public abstract int getNumBytes();
    
    public abstract float getMaxBytesPerChar();
    
    public abstract boolean isFixedWidthEncoding();
    
    public abstract int getAlignment();
    
    public abstract byte[] getBytes();
  }
  
  private static class CodeSetConversionHolder {
    static final CodeSetConversion csc = new CodeSetConversion(null);
  }
  
  private class JavaBTCConverter extends BTCConverter {
    private ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.encoding");
    
    private OMGSystemException omgWrapper = OMGSystemException.get("rpc.encoding");
    
    protected CharsetDecoder btc;
    
    private char[] buffer;
    
    private int resultingNumChars;
    
    private OSFCodeSetRegistry.Entry codeset;
    
    public JavaBTCConverter(OSFCodeSetRegistry.Entry param1Entry) {
      this.btc = getConverter(param1Entry.getName());
      this.codeset = param1Entry;
    }
    
    public final boolean isFixedWidthEncoding() { return this.codeset.isFixedWidth(); }
    
    public final int getFixedCharWidth() { return this.codeset.getMaxBytesPerChar(); }
    
    public final int getNumChars() { return this.resultingNumChars; }
    
    public char[] getChars(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) {
      try {
        ByteBuffer byteBuffer = ByteBuffer.wrap(param1ArrayOfByte, param1Int1, param1Int2);
        CharBuffer charBuffer = this.btc.decode(byteBuffer);
        this.resultingNumChars = charBuffer.limit();
        if (charBuffer.limit() == charBuffer.capacity()) {
          this.buffer = charBuffer.array();
        } else {
          this.buffer = new char[charBuffer.limit()];
          charBuffer.get(this.buffer, 0, charBuffer.limit()).position(0);
        } 
        return this.buffer;
      } catch (IllegalStateException illegalStateException) {
        throw this.wrapper.btcConverterFailure(illegalStateException);
      } catch (MalformedInputException malformedInputException) {
        throw this.wrapper.badUnicodePair(malformedInputException);
      } catch (UnmappableCharacterException unmappableCharacterException) {
        throw this.omgWrapper.charNotInCodeset(unmappableCharacterException);
      } catch (CharacterCodingException characterCodingException) {
        throw this.wrapper.btcConverterFailure(characterCodingException);
      } 
    }
    
    protected CharsetDecoder getConverter(String param1String) {
      CharsetDecoder charsetDecoder = null;
      try {
        charsetDecoder = CodeSetConversion.this.cache.getByteToCharConverter(param1String);
        if (charsetDecoder == null) {
          Charset charset = Charset.forName(param1String);
          charsetDecoder = charset.newDecoder();
          CodeSetConversion.this.cache.setConverter(param1String, charsetDecoder);
        } 
      } catch (IllegalCharsetNameException illegalCharsetNameException) {
        throw this.wrapper.invalidBtcConverterName(illegalCharsetNameException, param1String);
      } 
      return charsetDecoder;
    }
  }
  
  private class JavaCTBConverter extends CTBConverter {
    private ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.encoding");
    
    private OMGSystemException omgWrapper = OMGSystemException.get("rpc.encoding");
    
    private CharsetEncoder ctb;
    
    private int alignment;
    
    private char[] chars = null;
    
    private int numBytes = 0;
    
    private int numChars = 0;
    
    private ByteBuffer buffer;
    
    private OSFCodeSetRegistry.Entry codeset;
    
    public JavaCTBConverter(OSFCodeSetRegistry.Entry param1Entry, int param1Int) {
      try {
        this.ctb = this$0.cache.getCharToByteConverter(param1Entry.getName());
        if (this.ctb == null) {
          Charset charset = Charset.forName(param1Entry.getName());
          this.ctb = charset.newEncoder();
          this$0.cache.setConverter(param1Entry.getName(), this.ctb);
        } 
      } catch (IllegalCharsetNameException illegalCharsetNameException) {
        throw this.wrapper.invalidCtbConverterName(illegalCharsetNameException, param1Entry.getName());
      } catch (UnsupportedCharsetException unsupportedCharsetException) {
        throw this.wrapper.invalidCtbConverterName(unsupportedCharsetException, param1Entry.getName());
      } 
      this.codeset = param1Entry;
      this.alignment = param1Int;
    }
    
    public final float getMaxBytesPerChar() { return this.ctb.maxBytesPerChar(); }
    
    public void convert(char param1Char) {
      if (this.chars == null)
        this.chars = new char[1]; 
      this.chars[0] = param1Char;
      this.numChars = 1;
      convertCharArray();
    }
    
    public void convert(String param1String) {
      if (this.chars == null || this.chars.length < param1String.length())
        this.chars = new char[param1String.length()]; 
      this.numChars = param1String.length();
      param1String.getChars(0, this.numChars, this.chars, 0);
      convertCharArray();
    }
    
    public final int getNumBytes() { return this.numBytes; }
    
    public final int getAlignment() { return this.alignment; }
    
    public final boolean isFixedWidthEncoding() { return this.codeset.isFixedWidth(); }
    
    public byte[] getBytes() { return this.buffer.array(); }
    
    private void convertCharArray() {
      try {
        this.buffer = this.ctb.encode(CharBuffer.wrap(this.chars, 0, this.numChars));
        this.numBytes = this.buffer.limit();
      } catch (IllegalStateException illegalStateException) {
        throw this.wrapper.ctbConverterFailure(illegalStateException);
      } catch (MalformedInputException malformedInputException) {
        throw this.wrapper.badUnicodePair(malformedInputException);
      } catch (UnmappableCharacterException unmappableCharacterException) {
        throw this.omgWrapper.charNotInCodeset(unmappableCharacterException);
      } catch (CharacterCodingException characterCodingException) {
        throw this.wrapper.ctbConverterFailure(characterCodingException);
      } 
    }
  }
  
  private class UTF16BTCConverter extends JavaBTCConverter {
    private boolean defaultToLittleEndian;
    
    private boolean converterUsesBOM = true;
    
    private static final char UTF16_BE_MARKER = '﻿';
    
    private static final char UTF16_LE_MARKER = '￾';
    
    public UTF16BTCConverter(boolean param1Boolean) {
      super(CodeSetConversion.this, OSFCodeSetRegistry.UTF_16);
      this.defaultToLittleEndian = param1Boolean;
    }
    
    public char[] getChars(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) {
      if (hasUTF16ByteOrderMarker(param1ArrayOfByte, param1Int1, param1Int2)) {
        if (!this.converterUsesBOM)
          switchToConverter(OSFCodeSetRegistry.UTF_16); 
        this.converterUsesBOM = true;
        return super.getChars(param1ArrayOfByte, param1Int1, param1Int2);
      } 
      if (this.converterUsesBOM) {
        if (this.defaultToLittleEndian) {
          switchToConverter(OSFCodeSetRegistry.UTF_16LE);
        } else {
          switchToConverter(OSFCodeSetRegistry.UTF_16BE);
        } 
        this.converterUsesBOM = false;
      } 
      return super.getChars(param1ArrayOfByte, param1Int1, param1Int2);
    }
    
    private boolean hasUTF16ByteOrderMarker(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) {
      if (param1Int2 >= 4) {
        byte b1 = param1ArrayOfByte[param1Int1] & 0xFF;
        byte b2 = param1ArrayOfByte[param1Int1 + 1] & 0xFF;
        char c = (char)(b1 << 8 | b2 << 0);
        return (c == '﻿' || c == '￾');
      } 
      return false;
    }
    
    private void switchToConverter(OSFCodeSetRegistry.Entry param1Entry) { this.btc = getConverter(param1Entry.getName()); }
  }
  
  private class UTF16CTBConverter extends JavaCTBConverter {
    public UTF16CTBConverter() { super(CodeSetConversion.this, OSFCodeSetRegistry.UTF_16, 2); }
    
    public UTF16CTBConverter(boolean param1Boolean) { super(CodeSetConversion.this, param1Boolean ? OSFCodeSetRegistry.UTF_16LE : OSFCodeSetRegistry.UTF_16BE, 2); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\CodeSetConversion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */