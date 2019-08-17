package javax.imageio.stream;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Stack;
import javax.imageio.IIOException;

public abstract class ImageInputStreamImpl implements ImageInputStream {
  private Stack markByteStack = new Stack();
  
  private Stack markBitStack = new Stack();
  
  private boolean isClosed = false;
  
  private static final int BYTE_BUF_LENGTH = 8192;
  
  byte[] byteBuf = new byte[8192];
  
  protected ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
  
  protected long streamPos;
  
  protected int bitOffset;
  
  protected long flushedPos = 0L;
  
  protected final void checkClosed() {
    if (this.isClosed)
      throw new IOException("closed"); 
  }
  
  public void setByteOrder(ByteOrder paramByteOrder) { this.byteOrder = paramByteOrder; }
  
  public ByteOrder getByteOrder() { return this.byteOrder; }
  
  public abstract int read() throws IOException;
  
  public int read(byte[] paramArrayOfByte) throws IOException { return read(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public abstract int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException;
  
  public void readBytes(IIOByteBuffer paramIIOByteBuffer, int paramInt) throws IOException {
    if (paramInt < 0)
      throw new IndexOutOfBoundsException("len < 0!"); 
    if (paramIIOByteBuffer == null)
      throw new NullPointerException("buf == null!"); 
    byte[] arrayOfByte = new byte[paramInt];
    paramInt = read(arrayOfByte, 0, paramInt);
    paramIIOByteBuffer.setData(arrayOfByte);
    paramIIOByteBuffer.setOffset(0);
    paramIIOByteBuffer.setLength(paramInt);
  }
  
  public boolean readBoolean() throws IOException {
    int i = read();
    if (i < 0)
      throw new EOFException(); 
    return (i != 0);
  }
  
  public byte readByte() throws IOException {
    int i = read();
    if (i < 0)
      throw new EOFException(); 
    return (byte)i;
  }
  
  public int readUnsignedByte() throws IOException {
    int i = read();
    if (i < 0)
      throw new EOFException(); 
    return i;
  }
  
  public short readShort() throws IOException {
    if (read(this.byteBuf, 0, 2) != 2)
      throw new EOFException(); 
    return (this.byteOrder == ByteOrder.BIG_ENDIAN) ? (short)((this.byteBuf[0] & 0xFF) << 8 | (this.byteBuf[1] & 0xFF) << 0) : (short)((this.byteBuf[1] & 0xFF) << 8 | (this.byteBuf[0] & 0xFF) << 0);
  }
  
  public int readUnsignedShort() throws IOException { return readShort() & 0xFFFF; }
  
  public char readChar() throws IOException { return (char)readShort(); }
  
  public int readInt() throws IOException {
    if (read(this.byteBuf, 0, 4) != 4)
      throw new EOFException(); 
    return (this.byteOrder == ByteOrder.BIG_ENDIAN) ? ((this.byteBuf[0] & 0xFF) << 24 | (this.byteBuf[1] & 0xFF) << 16 | (this.byteBuf[2] & 0xFF) << 8 | (this.byteBuf[3] & 0xFF) << 0) : ((this.byteBuf[3] & 0xFF) << 24 | (this.byteBuf[2] & 0xFF) << 16 | (this.byteBuf[1] & 0xFF) << 8 | (this.byteBuf[0] & 0xFF) << 0);
  }
  
  public long readUnsignedInt() throws IOException { return readInt() & 0xFFFFFFFFL; }
  
  public long readLong() throws IOException {
    int i = readInt();
    int j = readInt();
    return (this.byteOrder == ByteOrder.BIG_ENDIAN) ? ((i << 32) + (j & 0xFFFFFFFFL)) : ((j << 32) + (i & 0xFFFFFFFFL));
  }
  
  public float readFloat() throws IOException { return Float.intBitsToFloat(readInt()); }
  
  public double readDouble() throws IOException { return Double.longBitsToDouble(readLong()); }
  
  public String readLine() throws IOException {
    StringBuffer stringBuffer = new StringBuffer();
    int i = -1;
    boolean bool = false;
    while (!bool) {
      long l;
      switch (i = read()) {
        case -1:
        case 10:
          bool = true;
          continue;
        case 13:
          bool = true;
          l = getStreamPosition();
          if (read() != 10)
            seek(l); 
          continue;
      } 
      stringBuffer.append((char)i);
    } 
    return (i == -1 && stringBuffer.length() == 0) ? null : stringBuffer.toString();
  }
  
  public String readUTF() throws IOException {
    String str;
    this.bitOffset = 0;
    ByteOrder byteOrder1 = getByteOrder();
    setByteOrder(ByteOrder.BIG_ENDIAN);
    try {
      str = DataInputStream.readUTF(this);
    } catch (IOException iOException) {
      setByteOrder(byteOrder1);
      throw iOException;
    } 
    setByteOrder(byteOrder1);
    return str;
  }
  
  public void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfByte.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > b.length!"); 
    while (paramInt2 > 0) {
      int i = read(paramArrayOfByte, paramInt1, paramInt2);
      if (i == -1)
        throw new EOFException(); 
      paramInt1 += i;
      paramInt2 -= i;
    } 
  }
  
  public void readFully(byte[] paramArrayOfByte) throws IOException { readFully(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public void readFully(short[] paramArrayOfShort, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfShort.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > s.length!"); 
    while (paramInt2 > 0) {
      int i = Math.min(paramInt2, this.byteBuf.length / 2);
      readFully(this.byteBuf, 0, i * 2);
      toShorts(this.byteBuf, paramArrayOfShort, paramInt1, i);
      paramInt1 += i;
      paramInt2 -= i;
    } 
  }
  
  public void readFully(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfChar.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > c.length!"); 
    while (paramInt2 > 0) {
      int i = Math.min(paramInt2, this.byteBuf.length / 2);
      readFully(this.byteBuf, 0, i * 2);
      toChars(this.byteBuf, paramArrayOfChar, paramInt1, i);
      paramInt1 += i;
      paramInt2 -= i;
    } 
  }
  
  public void readFully(int[] paramArrayOfInt, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfInt.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > i.length!"); 
    while (paramInt2 > 0) {
      int i = Math.min(paramInt2, this.byteBuf.length / 4);
      readFully(this.byteBuf, 0, i * 4);
      toInts(this.byteBuf, paramArrayOfInt, paramInt1, i);
      paramInt1 += i;
      paramInt2 -= i;
    } 
  }
  
  public void readFully(long[] paramArrayOfLong, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfLong.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > l.length!"); 
    while (paramInt2 > 0) {
      int i = Math.min(paramInt2, this.byteBuf.length / 8);
      readFully(this.byteBuf, 0, i * 8);
      toLongs(this.byteBuf, paramArrayOfLong, paramInt1, i);
      paramInt1 += i;
      paramInt2 -= i;
    } 
  }
  
  public void readFully(float[] paramArrayOfFloat, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfFloat.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > f.length!"); 
    while (paramInt2 > 0) {
      int i = Math.min(paramInt2, this.byteBuf.length / 4);
      readFully(this.byteBuf, 0, i * 4);
      toFloats(this.byteBuf, paramArrayOfFloat, paramInt1, i);
      paramInt1 += i;
      paramInt2 -= i;
    } 
  }
  
  public void readFully(double[] paramArrayOfDouble, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfDouble.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > d.length!"); 
    while (paramInt2 > 0) {
      int i = Math.min(paramInt2, this.byteBuf.length / 8);
      readFully(this.byteBuf, 0, i * 8);
      toDoubles(this.byteBuf, paramArrayOfDouble, paramInt1, i);
      paramInt1 += i;
      paramInt2 -= i;
    } 
  }
  
  private void toShorts(byte[] paramArrayOfByte, short[] paramArrayOfShort, int paramInt1, int paramInt2) {
    boolean bool = false;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (int i = 0; i < paramInt2; i++) {
        byte b1 = paramArrayOfByte[bool];
        byte b2 = paramArrayOfByte[bool + true] & 0xFF;
        paramArrayOfShort[paramInt1 + i] = (short)(b1 << 8 | b2);
        bool += true;
      } 
    } else {
      for (int i = 0; i < paramInt2; i++) {
        byte b1 = paramArrayOfByte[bool + true];
        byte b2 = paramArrayOfByte[bool] & 0xFF;
        paramArrayOfShort[paramInt1 + i] = (short)(b1 << 8 | b2);
        bool += true;
      } 
    } 
  }
  
  private void toChars(byte[] paramArrayOfByte, char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    boolean bool = false;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (int i = 0; i < paramInt2; i++) {
        byte b1 = paramArrayOfByte[bool];
        byte b2 = paramArrayOfByte[bool + true] & 0xFF;
        paramArrayOfChar[paramInt1 + i] = (char)(b1 << 8 | b2);
        bool += true;
      } 
    } else {
      for (int i = 0; i < paramInt2; i++) {
        byte b1 = paramArrayOfByte[bool + true];
        byte b2 = paramArrayOfByte[bool] & 0xFF;
        paramArrayOfChar[paramInt1 + i] = (char)(b1 << 8 | b2);
        bool += true;
      } 
    } 
  }
  
  private void toInts(byte[] paramArrayOfByte, int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    boolean bool = false;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (int i = 0; i < paramInt2; i++) {
        byte b1 = paramArrayOfByte[bool];
        byte b2 = paramArrayOfByte[bool + true] & 0xFF;
        byte b3 = paramArrayOfByte[bool + 2] & 0xFF;
        byte b4 = paramArrayOfByte[bool + 3] & 0xFF;
        paramArrayOfInt[paramInt1 + i] = b1 << 24 | b2 << 16 | b3 << 8 | b4;
        bool += true;
      } 
    } else {
      for (int i = 0; i < paramInt2; i++) {
        byte b1 = paramArrayOfByte[bool + 3];
        byte b2 = paramArrayOfByte[bool + 2] & 0xFF;
        byte b3 = paramArrayOfByte[bool + true] & 0xFF;
        byte b4 = paramArrayOfByte[bool] & 0xFF;
        paramArrayOfInt[paramInt1 + i] = b1 << 24 | b2 << 16 | b3 << 8 | b4;
        bool += true;
      } 
    } 
  }
  
  private void toLongs(byte[] paramArrayOfByte, long[] paramArrayOfLong, int paramInt1, int paramInt2) {
    boolean bool = false;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (int i = 0; i < paramInt2; i++) {
        byte b1 = paramArrayOfByte[bool];
        byte b2 = paramArrayOfByte[bool + true] & 0xFF;
        byte b3 = paramArrayOfByte[bool + 2] & 0xFF;
        byte b4 = paramArrayOfByte[bool + 3] & 0xFF;
        byte b5 = paramArrayOfByte[bool + 4];
        byte b6 = paramArrayOfByte[bool + 5] & 0xFF;
        byte b7 = paramArrayOfByte[bool + 6] & 0xFF;
        byte b8 = paramArrayOfByte[bool + 7] & 0xFF;
        byte b9 = b1 << 24 | b2 << 16 | b3 << 8 | b4;
        byte b10 = b5 << 24 | b6 << 16 | b7 << 8 | b8;
        paramArrayOfLong[paramInt1 + i] = b9 << 32 | b10 & 0xFFFFFFFFL;
        bool += true;
      } 
    } else {
      for (int i = 0; i < paramInt2; i++) {
        byte b1 = paramArrayOfByte[bool + 7];
        byte b2 = paramArrayOfByte[bool + 6] & 0xFF;
        byte b3 = paramArrayOfByte[bool + 5] & 0xFF;
        byte b4 = paramArrayOfByte[bool + 4] & 0xFF;
        byte b5 = paramArrayOfByte[bool + 3];
        byte b6 = paramArrayOfByte[bool + 2] & 0xFF;
        byte b7 = paramArrayOfByte[bool + true] & 0xFF;
        byte b8 = paramArrayOfByte[bool] & 0xFF;
        byte b9 = b1 << 24 | b2 << 16 | b3 << 8 | b4;
        byte b10 = b5 << 24 | b6 << 16 | b7 << 8 | b8;
        paramArrayOfLong[paramInt1 + i] = b9 << 32 | b10 & 0xFFFFFFFFL;
        bool += true;
      } 
    } 
  }
  
  private void toFloats(byte[] paramArrayOfByte, float[] paramArrayOfFloat, int paramInt1, int paramInt2) {
    boolean bool = false;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (int i = 0; i < paramInt2; i++) {
        byte b1 = paramArrayOfByte[bool];
        byte b2 = paramArrayOfByte[bool + true] & 0xFF;
        byte b3 = paramArrayOfByte[bool + 2] & 0xFF;
        byte b4 = paramArrayOfByte[bool + 3] & 0xFF;
        byte b5 = b1 << 24 | b2 << 16 | b3 << 8 | b4;
        paramArrayOfFloat[paramInt1 + i] = Float.intBitsToFloat(b5);
        bool += true;
      } 
    } else {
      for (int i = 0; i < paramInt2; i++) {
        byte b1 = paramArrayOfByte[bool + 3];
        byte b2 = paramArrayOfByte[bool + 2] & 0xFF;
        byte b3 = paramArrayOfByte[bool + true] & 0xFF;
        byte b4 = paramArrayOfByte[bool + false] & 0xFF;
        byte b5 = b1 << 24 | b2 << 16 | b3 << 8 | b4;
        paramArrayOfFloat[paramInt1 + i] = Float.intBitsToFloat(b5);
        bool += true;
      } 
    } 
  }
  
  private void toDoubles(byte[] paramArrayOfByte, double[] paramArrayOfDouble, int paramInt1, int paramInt2) {
    boolean bool = false;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (int i = 0; i < paramInt2; i++) {
        byte b1 = paramArrayOfByte[bool];
        byte b2 = paramArrayOfByte[bool + true] & 0xFF;
        byte b3 = paramArrayOfByte[bool + 2] & 0xFF;
        byte b4 = paramArrayOfByte[bool + 3] & 0xFF;
        byte b5 = paramArrayOfByte[bool + 4];
        byte b6 = paramArrayOfByte[bool + 5] & 0xFF;
        byte b7 = paramArrayOfByte[bool + 6] & 0xFF;
        byte b8 = paramArrayOfByte[bool + 7] & 0xFF;
        byte b9 = b1 << 24 | b2 << 16 | b3 << 8 | b4;
        byte b10 = b5 << 24 | b6 << 16 | b7 << 8 | b8;
        long l = b9 << 32 | b10 & 0xFFFFFFFFL;
        paramArrayOfDouble[paramInt1 + i] = Double.longBitsToDouble(l);
        bool += true;
      } 
    } else {
      for (int i = 0; i < paramInt2; i++) {
        byte b1 = paramArrayOfByte[bool + 7];
        byte b2 = paramArrayOfByte[bool + 6] & 0xFF;
        byte b3 = paramArrayOfByte[bool + 5] & 0xFF;
        byte b4 = paramArrayOfByte[bool + 4] & 0xFF;
        byte b5 = paramArrayOfByte[bool + 3];
        byte b6 = paramArrayOfByte[bool + 2] & 0xFF;
        byte b7 = paramArrayOfByte[bool + true] & 0xFF;
        byte b8 = paramArrayOfByte[bool] & 0xFF;
        byte b9 = b1 << 24 | b2 << 16 | b3 << 8 | b4;
        byte b10 = b5 << 24 | b6 << 16 | b7 << 8 | b8;
        long l = b9 << 32 | b10 & 0xFFFFFFFFL;
        paramArrayOfDouble[paramInt1 + i] = Double.longBitsToDouble(l);
        bool += true;
      } 
    } 
  }
  
  public long getStreamPosition() throws IOException {
    checkClosed();
    return this.streamPos;
  }
  
  public int getBitOffset() throws IOException {
    checkClosed();
    return this.bitOffset;
  }
  
  public void setBitOffset(int paramInt) throws IOException {
    checkClosed();
    if (paramInt < 0 || paramInt > 7)
      throw new IllegalArgumentException("bitOffset must be betwwen 0 and 7!"); 
    this.bitOffset = paramInt;
  }
  
  public int readBit() throws IOException {
    checkClosed();
    int i = this.bitOffset + 1 & 0x7;
    int j = read();
    if (j == -1)
      throw new EOFException(); 
    if (i != 0) {
      seek(getStreamPosition() - 1L);
      j >>= 8 - i;
    } 
    this.bitOffset = i;
    return j & true;
  }
  
  public long readBits(int paramInt) throws IOException {
    checkClosed();
    if (paramInt < 0 || paramInt > 64)
      throw new IllegalArgumentException(); 
    if (paramInt == 0)
      return 0L; 
    int i = paramInt + this.bitOffset;
    int j = this.bitOffset + paramInt & 0x7;
    null = 0L;
    while (i > 0) {
      int k = read();
      if (k == -1)
        throw new EOFException(); 
      null <<= 8;
      null |= k;
      i -= 8;
    } 
    if (j != 0)
      seek(getStreamPosition() - 1L); 
    this.bitOffset = j;
    null >>>= -i;
    return -1L >>> 64 - paramInt;
  }
  
  public long length() throws IOException { return -1L; }
  
  public int skipBytes(int paramInt) throws IOException {
    long l = getStreamPosition();
    seek(l + paramInt);
    return (int)(getStreamPosition() - l);
  }
  
  public long skipBytes(long paramLong) throws IOException {
    long l = getStreamPosition();
    seek(l + paramLong);
    return getStreamPosition() - l;
  }
  
  public void seek(long paramLong) throws IOException {
    checkClosed();
    if (paramLong < this.flushedPos)
      throw new IndexOutOfBoundsException("pos < flushedPos!"); 
    this.streamPos = paramLong;
    this.bitOffset = 0;
  }
  
  public void mark() {
    try {
      this.markByteStack.push(Long.valueOf(getStreamPosition()));
      this.markBitStack.push(Integer.valueOf(getBitOffset()));
    } catch (IOException iOException) {}
  }
  
  public void reset() {
    if (this.markByteStack.empty())
      return; 
    long l = ((Long)this.markByteStack.pop()).longValue();
    if (l < this.flushedPos)
      throw new IIOException("Previous marked position has been discarded!"); 
    seek(l);
    int i = ((Integer)this.markBitStack.pop()).intValue();
    setBitOffset(i);
  }
  
  public void flushBefore(long paramLong) throws IOException {
    checkClosed();
    if (paramLong < this.flushedPos)
      throw new IndexOutOfBoundsException("pos < flushedPos!"); 
    if (paramLong > getStreamPosition())
      throw new IndexOutOfBoundsException("pos > getStreamPosition()!"); 
    this.flushedPos = paramLong;
  }
  
  public void flush() { flushBefore(getStreamPosition()); }
  
  public long getFlushedPosition() throws IOException { return this.flushedPos; }
  
  public boolean isCached() throws IOException { return false; }
  
  public boolean isCachedMemory() throws IOException { return false; }
  
  public boolean isCachedFile() throws IOException { return false; }
  
  public void close() {
    checkClosed();
    this.isClosed = true;
  }
  
  protected void finalize() {
    if (!this.isClosed)
      try {
        close();
      } catch (IOException iOException) {} 
    super.finalize();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\stream\ImageInputStreamImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */