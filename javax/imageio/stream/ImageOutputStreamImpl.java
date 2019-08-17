package javax.imageio.stream;

import java.io.IOException;
import java.io.UTFDataFormatException;
import java.nio.ByteOrder;

public abstract class ImageOutputStreamImpl extends ImageInputStreamImpl implements ImageOutputStream {
  public abstract void write(int paramInt) throws IOException;
  
  public void write(byte[] paramArrayOfByte) throws IOException { write(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public abstract void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException;
  
  public void writeBoolean(boolean paramBoolean) throws IOException { write(paramBoolean ? 1 : 0); }
  
  public void writeByte(int paramInt) throws IOException { write(paramInt); }
  
  public void writeShort(int paramInt) throws IOException {
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      this.byteBuf[0] = (byte)(paramInt >>> 8);
      this.byteBuf[1] = (byte)(paramInt >>> 0);
    } else {
      this.byteBuf[0] = (byte)(paramInt >>> 0);
      this.byteBuf[1] = (byte)(paramInt >>> 8);
    } 
    write(this.byteBuf, 0, 2);
  }
  
  public void writeChar(int paramInt) throws IOException { writeShort(paramInt); }
  
  public void writeInt(int paramInt) throws IOException {
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      this.byteBuf[0] = (byte)(paramInt >>> 24);
      this.byteBuf[1] = (byte)(paramInt >>> 16);
      this.byteBuf[2] = (byte)(paramInt >>> 8);
      this.byteBuf[3] = (byte)(paramInt >>> 0);
    } else {
      this.byteBuf[0] = (byte)(paramInt >>> 0);
      this.byteBuf[1] = (byte)(paramInt >>> 8);
      this.byteBuf[2] = (byte)(paramInt >>> 16);
      this.byteBuf[3] = (byte)(paramInt >>> 24);
    } 
    write(this.byteBuf, 0, 4);
  }
  
  public void writeLong(long paramLong) throws IOException {
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      this.byteBuf[0] = (byte)(int)(paramLong >>> 56);
      this.byteBuf[1] = (byte)(int)(paramLong >>> 48);
      this.byteBuf[2] = (byte)(int)(paramLong >>> 40);
      this.byteBuf[3] = (byte)(int)(paramLong >>> 32);
      this.byteBuf[4] = (byte)(int)(paramLong >>> 24);
      this.byteBuf[5] = (byte)(int)(paramLong >>> 16);
      this.byteBuf[6] = (byte)(int)(paramLong >>> 8);
      this.byteBuf[7] = (byte)(int)(paramLong >>> false);
    } else {
      this.byteBuf[0] = (byte)(int)(paramLong >>> false);
      this.byteBuf[1] = (byte)(int)(paramLong >>> 8);
      this.byteBuf[2] = (byte)(int)(paramLong >>> 16);
      this.byteBuf[3] = (byte)(int)(paramLong >>> 24);
      this.byteBuf[4] = (byte)(int)(paramLong >>> 32);
      this.byteBuf[5] = (byte)(int)(paramLong >>> 40);
      this.byteBuf[6] = (byte)(int)(paramLong >>> 48);
      this.byteBuf[7] = (byte)(int)(paramLong >>> 56);
    } 
    write(this.byteBuf, 0, 4);
    write(this.byteBuf, 4, 4);
  }
  
  public void writeFloat(float paramFloat) throws IOException { writeInt(Float.floatToIntBits(paramFloat)); }
  
  public void writeDouble(double paramDouble) throws IOException { writeLong(Double.doubleToLongBits(paramDouble)); }
  
  public void writeBytes(String paramString) throws IOException {
    int i = paramString.length();
    for (byte b = 0; b < i; b++)
      write((byte)paramString.charAt(b)); 
  }
  
  public void writeChars(String paramString) throws IOException {
    int i = paramString.length();
    byte[] arrayOfByte = new byte[i * 2];
    byte b = 0;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (byte b1 = 0; b1 < i; b1++) {
        char c = paramString.charAt(b1);
        arrayOfByte[b++] = (byte)(c >>> '\b');
        arrayOfByte[b++] = (byte)(c >>> Character.MIN_VALUE);
      } 
    } else {
      for (byte b1 = 0; b1 < i; b1++) {
        char c = paramString.charAt(b1);
        arrayOfByte[b++] = (byte)(c >>> Character.MIN_VALUE);
        arrayOfByte[b++] = (byte)(c >>> '\b');
      } 
    } 
    write(arrayOfByte, 0, i * 2);
  }
  
  public void writeUTF(String paramString) throws IOException {
    int i = paramString.length();
    byte b1 = 0;
    char[] arrayOfChar = new char[i];
    byte b2 = 0;
    paramString.getChars(0, i, arrayOfChar, 0);
    for (byte b3 = 0; b3 < i; b3++) {
      char c = arrayOfChar[b3];
      if (c >= '\001' && c <= '') {
        b1++;
      } else if (c > '߿') {
        b1 += 3;
      } else {
        b1 += 2;
      } 
    } 
    if (b1 > '￿')
      throw new UTFDataFormatException("utflen > 65536!"); 
    byte[] arrayOfByte = new byte[b1 + 2];
    arrayOfByte[b2++] = (byte)(b1 >>> 8 & 0xFF);
    arrayOfByte[b2++] = (byte)(b1 >>> 0 & 0xFF);
    for (byte b4 = 0; b4 < i; b4++) {
      char c = arrayOfChar[b4];
      if (c >= '\001' && c <= '') {
        arrayOfByte[b2++] = (byte)c;
      } else if (c > '߿') {
        arrayOfByte[b2++] = (byte)(0xE0 | c >> '\f' & 0xF);
        arrayOfByte[b2++] = (byte)(0x80 | c >> '\006' & 0x3F);
        arrayOfByte[b2++] = (byte)(0x80 | c >> Character.MIN_VALUE & 0x3F);
      } else {
        arrayOfByte[b2++] = (byte)(0xC0 | c >> '\006' & 0x1F);
        arrayOfByte[b2++] = (byte)(0x80 | c >> Character.MIN_VALUE & 0x3F);
      } 
    } 
    write(arrayOfByte, 0, b1 + 2);
  }
  
  public void writeShorts(short[] paramArrayOfShort, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfShort.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > s.length!"); 
    byte[] arrayOfByte = new byte[paramInt2 * 2];
    byte b = 0;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (int i = 0; i < paramInt2; i++) {
        short s = paramArrayOfShort[paramInt1 + i];
        arrayOfByte[b++] = (byte)(s >>> 8);
        arrayOfByte[b++] = (byte)(s >>> 0);
      } 
    } else {
      for (int i = 0; i < paramInt2; i++) {
        short s = paramArrayOfShort[paramInt1 + i];
        arrayOfByte[b++] = (byte)(s >>> 0);
        arrayOfByte[b++] = (byte)(s >>> 8);
      } 
    } 
    write(arrayOfByte, 0, paramInt2 * 2);
  }
  
  public void writeChars(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfChar.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > c.length!"); 
    byte[] arrayOfByte = new byte[paramInt2 * 2];
    byte b = 0;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (int i = 0; i < paramInt2; i++) {
        char c = paramArrayOfChar[paramInt1 + i];
        arrayOfByte[b++] = (byte)(c >>> '\b');
        arrayOfByte[b++] = (byte)(c >>> Character.MIN_VALUE);
      } 
    } else {
      for (int i = 0; i < paramInt2; i++) {
        char c = paramArrayOfChar[paramInt1 + i];
        arrayOfByte[b++] = (byte)(c >>> Character.MIN_VALUE);
        arrayOfByte[b++] = (byte)(c >>> '\b');
      } 
    } 
    write(arrayOfByte, 0, paramInt2 * 2);
  }
  
  public void writeInts(int[] paramArrayOfInt, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfInt.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > i.length!"); 
    byte[] arrayOfByte = new byte[paramInt2 * 4];
    byte b = 0;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (int i = 0; i < paramInt2; i++) {
        int j = paramArrayOfInt[paramInt1 + i];
        arrayOfByte[b++] = (byte)(j >>> 24);
        arrayOfByte[b++] = (byte)(j >>> 16);
        arrayOfByte[b++] = (byte)(j >>> 8);
        arrayOfByte[b++] = (byte)(j >>> 0);
      } 
    } else {
      for (int i = 0; i < paramInt2; i++) {
        int j = paramArrayOfInt[paramInt1 + i];
        arrayOfByte[b++] = (byte)(j >>> 0);
        arrayOfByte[b++] = (byte)(j >>> 8);
        arrayOfByte[b++] = (byte)(j >>> 16);
        arrayOfByte[b++] = (byte)(j >>> 24);
      } 
    } 
    write(arrayOfByte, 0, paramInt2 * 4);
  }
  
  public void writeLongs(long[] paramArrayOfLong, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfLong.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > l.length!"); 
    byte[] arrayOfByte = new byte[paramInt2 * 8];
    byte b = 0;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (int i = 0; i < paramInt2; i++) {
        long l = paramArrayOfLong[paramInt1 + i];
        arrayOfByte[b++] = (byte)(int)(l >>> 56);
        arrayOfByte[b++] = (byte)(int)(l >>> 48);
        arrayOfByte[b++] = (byte)(int)(l >>> 40);
        arrayOfByte[b++] = (byte)(int)(l >>> 32);
        arrayOfByte[b++] = (byte)(int)(l >>> 24);
        arrayOfByte[b++] = (byte)(int)(l >>> 16);
        arrayOfByte[b++] = (byte)(int)(l >>> 8);
        arrayOfByte[b++] = (byte)(int)(l >>> false);
      } 
    } else {
      for (int i = 0; i < paramInt2; i++) {
        long l = paramArrayOfLong[paramInt1 + i];
        arrayOfByte[b++] = (byte)(int)(l >>> false);
        arrayOfByte[b++] = (byte)(int)(l >>> 8);
        arrayOfByte[b++] = (byte)(int)(l >>> 16);
        arrayOfByte[b++] = (byte)(int)(l >>> 24);
        arrayOfByte[b++] = (byte)(int)(l >>> 32);
        arrayOfByte[b++] = (byte)(int)(l >>> 40);
        arrayOfByte[b++] = (byte)(int)(l >>> 48);
        arrayOfByte[b++] = (byte)(int)(l >>> 56);
      } 
    } 
    write(arrayOfByte, 0, paramInt2 * 8);
  }
  
  public void writeFloats(float[] paramArrayOfFloat, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfFloat.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > f.length!"); 
    byte[] arrayOfByte = new byte[paramInt2 * 4];
    byte b = 0;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (int i = 0; i < paramInt2; i++) {
        int j = Float.floatToIntBits(paramArrayOfFloat[paramInt1 + i]);
        arrayOfByte[b++] = (byte)(j >>> 24);
        arrayOfByte[b++] = (byte)(j >>> 16);
        arrayOfByte[b++] = (byte)(j >>> 8);
        arrayOfByte[b++] = (byte)(j >>> 0);
      } 
    } else {
      for (int i = 0; i < paramInt2; i++) {
        int j = Float.floatToIntBits(paramArrayOfFloat[paramInt1 + i]);
        arrayOfByte[b++] = (byte)(j >>> 0);
        arrayOfByte[b++] = (byte)(j >>> 8);
        arrayOfByte[b++] = (byte)(j >>> 16);
        arrayOfByte[b++] = (byte)(j >>> 24);
      } 
    } 
    write(arrayOfByte, 0, paramInt2 * 4);
  }
  
  public void writeDoubles(double[] paramArrayOfDouble, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfDouble.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > d.length!"); 
    byte[] arrayOfByte = new byte[paramInt2 * 8];
    byte b = 0;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (int i = 0; i < paramInt2; i++) {
        long l = Double.doubleToLongBits(paramArrayOfDouble[paramInt1 + i]);
        arrayOfByte[b++] = (byte)(int)(l >>> 56);
        arrayOfByte[b++] = (byte)(int)(l >>> 48);
        arrayOfByte[b++] = (byte)(int)(l >>> 40);
        arrayOfByte[b++] = (byte)(int)(l >>> 32);
        arrayOfByte[b++] = (byte)(int)(l >>> 24);
        arrayOfByte[b++] = (byte)(int)(l >>> 16);
        arrayOfByte[b++] = (byte)(int)(l >>> 8);
        arrayOfByte[b++] = (byte)(int)(l >>> false);
      } 
    } else {
      for (int i = 0; i < paramInt2; i++) {
        long l = Double.doubleToLongBits(paramArrayOfDouble[paramInt1 + i]);
        arrayOfByte[b++] = (byte)(int)(l >>> false);
        arrayOfByte[b++] = (byte)(int)(l >>> 8);
        arrayOfByte[b++] = (byte)(int)(l >>> 16);
        arrayOfByte[b++] = (byte)(int)(l >>> 24);
        arrayOfByte[b++] = (byte)(int)(l >>> 32);
        arrayOfByte[b++] = (byte)(int)(l >>> 40);
        arrayOfByte[b++] = (byte)(int)(l >>> 48);
        arrayOfByte[b++] = (byte)(int)(l >>> 56);
      } 
    } 
    write(arrayOfByte, 0, paramInt2 * 8);
  }
  
  public void writeBit(int paramInt) throws IOException { writeBits(0x1L & paramInt, 1); }
  
  public void writeBits(long paramLong, int paramInt) throws IOException {
    checkClosed();
    if (paramInt < 0 || paramInt > 64)
      throw new IllegalArgumentException("Bad value for numBits!"); 
    if (paramInt == 0)
      return; 
    if (getStreamPosition() > 0L || this.bitOffset > 0) {
      int i = this.bitOffset;
      int j = read();
      if (j != -1) {
        seek(getStreamPosition() - 1L);
      } else {
        j = 0;
      } 
      if (paramInt + i < 8) {
        int k = 8 - i + paramInt;
        int m = -1 >>> 32 - paramInt;
        j &= (m << k ^ 0xFFFFFFFF);
        j = (int)(j | (paramLong & m) << k);
        write(j);
        seek(getStreamPosition() - 1L);
        this.bitOffset = i + paramInt;
        paramInt = 0;
      } else {
        int k = 8 - i;
        int m = -1 >>> 32 - k;
        j &= (m ^ 0xFFFFFFFF);
        j = (int)(j | paramLong >> paramInt - k & m);
        write(j);
        paramInt -= k;
      } 
    } 
    if (paramInt > 7) {
      int i = paramInt % 8;
      for (int j = paramInt / 8; j > 0; j--) {
        int k = (j - 1) * 8 + i;
        int m = (int)((k == 0) ? (paramLong & 0xFFL) : (paramLong >> k & 0xFFL));
        write(m);
      } 
      paramInt = i;
    } 
    if (paramInt != 0) {
      int i = 0;
      i = read();
      if (i != -1) {
        seek(getStreamPosition() - 1L);
      } else {
        i = 0;
      } 
      int j = 8 - paramInt;
      int k = -1 >>> 32 - paramInt;
      i &= (k << j ^ 0xFFFFFFFF);
      i = (int)(i | (paramLong & k) << j);
      write(i);
      seek(getStreamPosition() - 1L);
      this.bitOffset = paramInt;
    } 
  }
  
  protected final void flushBits() {
    checkClosed();
    if (this.bitOffset != 0) {
      int i = this.bitOffset;
      int j = read();
      if (j < 0) {
        j = 0;
        this.bitOffset = 0;
      } else {
        seek(getStreamPosition() - 1L);
        j &= -1 << 8 - i;
      } 
      write(j);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\stream\ImageOutputStreamImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */