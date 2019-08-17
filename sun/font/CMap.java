package sun.font;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;

abstract class CMap {
  static final short ShiftJISEncoding = 2;
  
  static final short GBKEncoding = 3;
  
  static final short Big5Encoding = 4;
  
  static final short WansungEncoding = 5;
  
  static final short JohabEncoding = 6;
  
  static final short MSUnicodeSurrogateEncoding = 10;
  
  static final char noSuchChar = '�';
  
  static final int SHORTMASK = 65535;
  
  static final int INTMASK = -1;
  
  static final char[][] converterMaps = new char[7][];
  
  char[] xlat;
  
  public static final NullCMapClass theNullCmap = new NullCMapClass();
  
  static CMap initialize(TrueTypeFont paramTrueTypeFont) {
    CMap cMap = null;
    short s = -1;
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    boolean bool = false;
    ByteBuffer byteBuffer = paramTrueTypeFont.getTableBuffer(1668112752);
    int i4 = paramTrueTypeFont.getTableSize(1668112752);
    short s1 = byteBuffer.getShort(2);
    for (byte b = 0; b < s1; b++) {
      byteBuffer.position(b * 8 + 4);
      short s2 = byteBuffer.getShort();
      if (s2 == 3) {
        bool = true;
        s = byteBuffer.getShort();
        int i5 = byteBuffer.getInt();
        switch (s) {
          case 0:
            i = i5;
            break;
          case 1:
            j = i5;
            break;
          case 2:
            k = i5;
            break;
          case 3:
            m = i5;
            break;
          case 4:
            n = i5;
            break;
          case 5:
            i1 = i5;
            break;
          case 6:
            i2 = i5;
            break;
          case 10:
            i3 = i5;
            break;
        } 
      } 
    } 
    if (bool) {
      if (i3 != 0) {
        cMap = createCMap(byteBuffer, i3, null);
      } else if (i != 0) {
        cMap = createCMap(byteBuffer, i, null);
      } else if (j != 0) {
        cMap = createCMap(byteBuffer, j, null);
      } else if (k != 0) {
        cMap = createCMap(byteBuffer, k, getConverterMap((short)2));
      } else if (m != 0) {
        cMap = createCMap(byteBuffer, m, getConverterMap((short)3));
      } else if (n != 0) {
        if (FontUtilities.isSolaris && paramTrueTypeFont.platName != null && (paramTrueTypeFont.platName.startsWith("/usr/openwin/lib/locale/zh_CN.EUC/X11/fonts/TrueType") || paramTrueTypeFont.platName.startsWith("/usr/openwin/lib/locale/zh_CN/X11/fonts/TrueType") || paramTrueTypeFont.platName.startsWith("/usr/openwin/lib/locale/zh/X11/fonts/TrueType"))) {
          cMap = createCMap(byteBuffer, n, getConverterMap((short)3));
        } else {
          cMap = createCMap(byteBuffer, n, getConverterMap((short)4));
        } 
      } else if (i1 != 0) {
        cMap = createCMap(byteBuffer, i1, getConverterMap((short)5));
      } else if (i2 != 0) {
        cMap = createCMap(byteBuffer, i2, getConverterMap((short)6));
      } 
    } else {
      cMap = createCMap(byteBuffer, byteBuffer.getInt(8), null);
    } 
    return cMap;
  }
  
  static char[] getConverter(short paramShort) {
    String str;
    char c1 = '耀';
    char c2 = '￿';
    switch (paramShort) {
      case 2:
        c1 = '腀';
        c2 = 'ﳼ';
        str = "SJIS";
        break;
      case 3:
        c1 = '腀';
        c2 = 'ﺠ';
        str = "GBK";
        break;
      case 4:
        c1 = 'ꅀ';
        c2 = '﻾';
        str = "Big5";
        break;
      case 5:
        c1 = 'ꆡ';
        c2 = 'ﻞ';
        str = "EUC_KR";
        break;
      case 6:
        c1 = '腁';
        c2 = '﷾';
        str = "Johab";
        break;
      default:
        return null;
    } 
    try {
      char[] arrayOfChar1 = new char[65536];
      for (byte b1 = 0; b1 < 65536; b1++)
        arrayOfChar1[b1] = '�'; 
      byte[] arrayOfByte = new byte[(c2 - c1 + '\001') * '\002'];
      char[] arrayOfChar2 = new char[c2 - c1 + '\001'];
      byte b2 = 0;
      if (paramShort == 2) {
        for (char c3 = c1; c3 <= c2; c3++) {
          char c4 = c3 >> '\b' & 0xFF;
          if (c4 >= '¡' && c4 <= 'ß') {
            arrayOfByte[b2++] = -1;
            arrayOfByte[b2++] = -1;
          } else {
            arrayOfByte[b2++] = (byte)c4;
            arrayOfByte[b2++] = (byte)(c3 & 0xFF);
          } 
        } 
      } else {
        for (char c3 = c1; c3 <= c2; c3++) {
          arrayOfByte[b2++] = (byte)(c3 >> '\b' & 0xFF);
          arrayOfByte[b2++] = (byte)(c3 & 0xFF);
        } 
      } 
      Charset.forName(str).newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).replaceWith("\000").decode(ByteBuffer.wrap(arrayOfByte, 0, arrayOfByte.length), CharBuffer.wrap(arrayOfChar2, 0, arrayOfChar2.length), true);
      char c;
      for (c = ' '; c <= '~'; c++)
        arrayOfChar1[c] = (char)c; 
      if (paramShort == 2)
        for (c = '¡'; c <= 'ß'; c++)
          arrayOfChar1[c] = (char)(c - '¡' + '｡');  
      System.arraycopy(arrayOfChar2, 0, arrayOfChar1, c1, arrayOfChar2.length);
      char[] arrayOfChar3 = new char[65536];
      for (byte b3 = 0; b3 < 65536; b3++) {
        if (arrayOfChar1[b3] != '�')
          arrayOfChar3[arrayOfChar1[b3]] = (char)b3; 
      } 
      return arrayOfChar3;
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    } 
  }
  
  static char[] getConverterMap(short paramShort) {
    if (converterMaps[paramShort] == null)
      converterMaps[paramShort] = getConverter(paramShort); 
    return converterMaps[paramShort];
  }
  
  static CMap createCMap(ByteBuffer paramByteBuffer, int paramInt, char[] paramArrayOfChar) {
    long l;
    char c = paramByteBuffer.getChar(paramInt);
    if (c < '\b') {
      l = paramByteBuffer.getChar(paramInt + 2);
    } else {
      l = (paramByteBuffer.getInt(paramInt + 4) & 0xFFFFFFFF);
    } 
    if (paramInt + l > paramByteBuffer.capacity() && FontUtilities.isLogging())
      FontUtilities.getLogger().warning("Cmap subtable overflows buffer."); 
    switch (c) {
      case '\000':
        return new CMapFormat0(paramByteBuffer, paramInt);
      case '\002':
        return new CMapFormat2(paramByteBuffer, paramInt, paramArrayOfChar);
      case '\004':
        return new CMapFormat4(paramByteBuffer, paramInt, paramArrayOfChar);
      case '\006':
        return new CMapFormat6(paramByteBuffer, paramInt, paramArrayOfChar);
      case '\b':
        return new CMapFormat8(paramByteBuffer, paramInt, paramArrayOfChar);
      case '\n':
        return new CMapFormat10(paramByteBuffer, paramInt, paramArrayOfChar);
      case '\f':
        return new CMapFormat12(paramByteBuffer, paramInt, paramArrayOfChar);
    } 
    throw new RuntimeException("Cmap format unimplemented: " + paramByteBuffer.getChar(paramInt));
  }
  
  abstract char getGlyph(int paramInt);
  
  final int getControlCodeGlyph(int paramInt, boolean paramBoolean) {
    if (paramInt < 16) {
      switch (paramInt) {
        case 9:
        case 10:
        case 13:
          return 65535;
      } 
    } else if (paramInt >= 8204) {
      if (paramInt <= 8207 || (paramInt >= 8232 && paramInt <= 8238) || (paramInt >= 8298 && paramInt <= 8303))
        return 65535; 
      if (paramBoolean && paramInt >= 65535)
        return 0; 
    } 
    return -1;
  }
  
  static class CMapFormat0 extends CMap {
    byte[] cmap;
    
    CMapFormat0(ByteBuffer param1ByteBuffer, int param1Int) {
      char c = param1ByteBuffer.getChar(param1Int + 2);
      this.cmap = new byte[c - '\006'];
      param1ByteBuffer.position(param1Int + 6);
      param1ByteBuffer.get(this.cmap);
    }
    
    char getGlyph(int param1Int) {
      if (param1Int < 256) {
        if (param1Int < 16)
          switch (param1Int) {
            case 9:
            case 10:
            case 13:
              return Character.MAX_VALUE;
          }  
        return (char)(0xFF & this.cmap[param1Int]);
      } 
      return Character.MIN_VALUE;
    }
  }
  
  static class CMapFormat10 extends CMap {
    long firstCode;
    
    int entryCount;
    
    char[] glyphIdArray;
    
    CMapFormat10(ByteBuffer param1ByteBuffer, int param1Int, char[] param1ArrayOfChar) {
      this.firstCode = (param1ByteBuffer.getInt() & 0xFFFFFFFF);
      this.entryCount = param1ByteBuffer.getInt() & 0xFFFFFFFF;
      param1ByteBuffer.position(param1Int + 20);
      CharBuffer charBuffer = param1ByteBuffer.asCharBuffer();
      this.glyphIdArray = new char[this.entryCount];
      for (byte b = 0; b < this.entryCount; b++)
        this.glyphIdArray[b] = charBuffer.get(); 
    }
    
    char getGlyph(int param1Int) {
      if (this.xlat != null)
        throw new RuntimeException("xlat array for cmap fmt=10"); 
      int i = (int)(param1Int - this.firstCode);
      return (i < 0 || i >= this.entryCount) ? Character.MIN_VALUE : this.glyphIdArray[i];
    }
  }
  
  static class CMapFormat12 extends CMap {
    int numGroups;
    
    int highBit = 0;
    
    int power;
    
    int extra;
    
    long[] startCharCode;
    
    long[] endCharCode;
    
    int[] startGlyphID;
    
    CMapFormat12(ByteBuffer param1ByteBuffer, int param1Int, char[] param1ArrayOfChar) {
      if (param1ArrayOfChar != null)
        throw new RuntimeException("xlat array for cmap fmt=12"); 
      this.numGroups = param1ByteBuffer.getInt(param1Int + 12);
      this.startCharCode = new long[this.numGroups];
      this.endCharCode = new long[this.numGroups];
      this.startGlyphID = new int[this.numGroups];
      param1ByteBuffer.position(param1Int + 16);
      param1ByteBuffer = param1ByteBuffer.slice();
      IntBuffer intBuffer = param1ByteBuffer.asIntBuffer();
      int i;
      for (i = 0; i < this.numGroups; i++) {
        this.startCharCode[i] = (intBuffer.get() & 0xFFFFFFFF);
        this.endCharCode[i] = (intBuffer.get() & 0xFFFFFFFF);
        this.startGlyphID[i] = intBuffer.get() & 0xFFFFFFFF;
      } 
      i = this.numGroups;
      if (i >= 65536) {
        i >>= 16;
        this.highBit += 16;
      } 
      if (i >= 256) {
        i >>= 8;
        this.highBit += 8;
      } 
      if (i >= 16) {
        i >>= 4;
        this.highBit += 4;
      } 
      if (i >= 4) {
        i >>= 2;
        this.highBit += 2;
      } 
      if (i >= 2) {
        i >>= 1;
        this.highBit++;
      } 
      this.power = 1 << this.highBit;
      this.extra = this.numGroups - this.power;
    }
    
    char getGlyph(int param1Int) {
      int i = getControlCodeGlyph(param1Int, false);
      if (i >= 0)
        return (char)i; 
      int j = this.power;
      int k = 0;
      if (this.startCharCode[this.extra] <= param1Int)
        k = this.extra; 
      while (j > 1) {
        j >>= 1;
        if (this.startCharCode[k + j] <= param1Int)
          k += j; 
      } 
      return (this.startCharCode[k] <= param1Int && this.endCharCode[k] >= param1Int) ? (char)(int)(this.startGlyphID[k] + param1Int - this.startCharCode[k]) : 0;
    }
  }
  
  static class CMapFormat2 extends CMap {
    char[] subHeaderKey = new char[256];
    
    char[] firstCodeArray;
    
    char[] entryCountArray;
    
    short[] idDeltaArray;
    
    char[] idRangeOffSetArray;
    
    char[] glyphIndexArray;
    
    CMapFormat2(ByteBuffer param1ByteBuffer, int param1Int, char[] param1ArrayOfChar) {
      this.xlat = param1ArrayOfChar;
      char c = param1ByteBuffer.getChar(param1Int + 2);
      param1ByteBuffer.position(param1Int + 6);
      CharBuffer charBuffer = param1ByteBuffer.asCharBuffer();
      char c1 = Character.MIN_VALUE;
      char c2;
      for (c2 = Character.MIN_VALUE; c2 < 'Ā'; c2++) {
        this.subHeaderKey[c2] = charBuffer.get();
        if (this.subHeaderKey[c2] > c1)
          c1 = this.subHeaderKey[c2]; 
      } 
      c2 = (c1 >> '\003') + '\001';
      this.firstCodeArray = new char[c2];
      this.entryCountArray = new char[c2];
      this.idDeltaArray = new short[c2];
      this.idRangeOffSetArray = new char[c2];
      char c3;
      for (c3 = Character.MIN_VALUE; c3 < c2; c3++) {
        this.firstCodeArray[c3] = charBuffer.get();
        this.entryCountArray[c3] = charBuffer.get();
        this.idDeltaArray[c3] = (short)charBuffer.get();
        this.idRangeOffSetArray[c3] = charBuffer.get();
      } 
      c3 = (c - 'Ȇ' - c2 * '\b') / '\002';
      this.glyphIndexArray = new char[c3];
      for (byte b = 0; b < c3; b++)
        this.glyphIndexArray[b] = charBuffer.get(); 
    }
    
    char getGlyph(int param1Int) {
      int i = getControlCodeGlyph(param1Int, true);
      if (i >= 0)
        return (char)i; 
      if (this.xlat != null)
        param1Int = this.xlat[param1Int]; 
      char c1 = (char)(param1Int >> 8);
      char c2 = (char)(param1Int & 0xFF);
      char c3 = this.subHeaderKey[c1] >> '\003';
      if (c3 != '\000') {
        c = c2;
      } else {
        c = c1;
        if (c == Character.MIN_VALUE)
          c = c2; 
      } 
      char c4 = this.firstCodeArray[c3];
      if (c < c4)
        return Character.MIN_VALUE; 
      char c = (char)(c - c4);
      if (c < this.entryCountArray[c3]) {
        int j = (this.idRangeOffSetArray.length - c3) * 8 - 6;
        char c5 = (this.idRangeOffSetArray[c3] - j) / '\002';
        char c6 = this.glyphIndexArray[c5 + c];
        if (c6 != '\000')
          return (char)(c6 + this.idDeltaArray[c3]); 
      } 
      return Character.MIN_VALUE;
    }
  }
  
  static class CMapFormat4 extends CMap {
    int segCount;
    
    int entrySelector;
    
    int rangeShift;
    
    char[] endCount;
    
    char[] startCount;
    
    short[] idDelta;
    
    char[] idRangeOffset;
    
    char[] glyphIds;
    
    CMapFormat4(ByteBuffer param1ByteBuffer, int param1Int, char[] param1ArrayOfChar) {
      this.xlat = param1ArrayOfChar;
      param1ByteBuffer.position(param1Int);
      CharBuffer charBuffer = param1ByteBuffer.asCharBuffer();
      charBuffer.get();
      int i = charBuffer.get();
      if (param1Int + i > param1ByteBuffer.capacity())
        i = param1ByteBuffer.capacity() - param1Int; 
      charBuffer.get();
      this.segCount = charBuffer.get() / '\002';
      char c = charBuffer.get();
      this.entrySelector = charBuffer.get();
      this.rangeShift = charBuffer.get() / '\002';
      this.startCount = new char[this.segCount];
      this.endCount = new char[this.segCount];
      this.idDelta = new short[this.segCount];
      this.idRangeOffset = new char[this.segCount];
      int j;
      for (j = 0; j < this.segCount; j++)
        this.endCount[j] = charBuffer.get(); 
      charBuffer.get();
      for (j = 0; j < this.segCount; j++)
        this.startCount[j] = charBuffer.get(); 
      for (j = 0; j < this.segCount; j++)
        this.idDelta[j] = (short)charBuffer.get(); 
      for (j = 0; j < this.segCount; j++) {
        char c1 = charBuffer.get();
        this.idRangeOffset[j] = (char)(c1 >> '\001' & 0xFFFF);
      } 
      j = (this.segCount * 8 + 16) / 2;
      charBuffer.position(j);
      int k = i / 2 - j;
      this.glyphIds = new char[k];
      for (byte b = 0; b < k; b++)
        this.glyphIds[b] = charBuffer.get(); 
    }
    
    char getGlyph(int param1Int) {
      int i = 0;
      int j = 0;
      int k = getControlCodeGlyph(param1Int, true);
      if (k >= 0)
        return (char)k; 
      if (this.xlat != null)
        param1Int = this.xlat[param1Int]; 
      int m = 0;
      int n = this.startCount.length;
      for (i = this.startCount.length >> 1; m < n; i = m + n >> 1) {
        if (this.endCount[i] < param1Int) {
          m = i + 1;
        } else {
          n = i;
        } 
      } 
      if (param1Int >= this.startCount[i] && param1Int <= this.endCount[i]) {
        char c = this.idRangeOffset[i];
        if (c == '\000') {
          j = (char)(param1Int + this.idDelta[i]);
        } else {
          char c1 = c - this.segCount + i + param1Int - this.startCount[i];
          j = this.glyphIds[c1];
          if (j != 0)
            j = (char)(j + this.idDelta[i]); 
        } 
      } 
      if (j != 0);
      return j;
    }
  }
  
  static class CMapFormat6 extends CMap {
    char firstCode;
    
    char entryCount;
    
    char[] glyphIdArray;
    
    CMapFormat6(ByteBuffer param1ByteBuffer, int param1Int, char[] param1ArrayOfChar) {
      param1ByteBuffer.position(param1Int + 6);
      CharBuffer charBuffer = param1ByteBuffer.asCharBuffer();
      this.firstCode = charBuffer.get();
      this.entryCount = charBuffer.get();
      this.glyphIdArray = new char[this.entryCount];
      for (byte b = 0; b < this.entryCount; b++)
        this.glyphIdArray[b] = charBuffer.get(); 
    }
    
    char getGlyph(int param1Int) {
      int i = getControlCodeGlyph(param1Int, true);
      if (i >= 0)
        return (char)i; 
      if (this.xlat != null)
        param1Int = this.xlat[param1Int]; 
      param1Int -= this.firstCode;
      return (param1Int < 0 || param1Int >= this.entryCount) ? Character.MIN_VALUE : this.glyphIdArray[param1Int];
    }
  }
  
  static class CMapFormat8 extends CMap {
    byte[] is32 = new byte[8192];
    
    int nGroups;
    
    int[] startCharCode;
    
    int[] endCharCode;
    
    int[] startGlyphID;
    
    CMapFormat8(ByteBuffer param1ByteBuffer, int param1Int, char[] param1ArrayOfChar) {
      param1ByteBuffer.position(12);
      param1ByteBuffer.get(this.is32);
      this.nGroups = param1ByteBuffer.getInt();
      this.startCharCode = new int[this.nGroups];
      this.endCharCode = new int[this.nGroups];
      this.startGlyphID = new int[this.nGroups];
    }
    
    char getGlyph(int param1Int) {
      if (this.xlat != null)
        throw new RuntimeException("xlat array for cmap fmt=8"); 
      return Character.MIN_VALUE;
    }
  }
  
  static class NullCMapClass extends CMap {
    char getGlyph(int param1Int) { return Character.MIN_VALUE; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\CMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */