package javax.imageio.stream;

import java.io.Closeable;
import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteOrder;

public interface ImageInputStream extends DataInput, Closeable {
  void setByteOrder(ByteOrder paramByteOrder);
  
  ByteOrder getByteOrder();
  
  int read() throws IOException;
  
  int read(byte[] paramArrayOfByte) throws IOException;
  
  int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException;
  
  void readBytes(IIOByteBuffer paramIIOByteBuffer, int paramInt) throws IOException;
  
  boolean readBoolean() throws IOException;
  
  byte readByte() throws IOException;
  
  int readUnsignedByte() throws IOException;
  
  short readShort() throws IOException;
  
  int readUnsignedShort() throws IOException;
  
  char readChar() throws IOException;
  
  int readInt() throws IOException;
  
  long readUnsignedInt() throws IOException;
  
  long readLong() throws IOException;
  
  float readFloat() throws IOException;
  
  double readDouble() throws IOException;
  
  String readLine() throws IOException;
  
  String readUTF() throws IOException;
  
  void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException;
  
  void readFully(byte[] paramArrayOfByte) throws IOException;
  
  void readFully(short[] paramArrayOfShort, int paramInt1, int paramInt2) throws IOException;
  
  void readFully(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException;
  
  void readFully(int[] paramArrayOfInt, int paramInt1, int paramInt2) throws IOException;
  
  void readFully(long[] paramArrayOfLong, int paramInt1, int paramInt2) throws IOException;
  
  void readFully(float[] paramArrayOfFloat, int paramInt1, int paramInt2) throws IOException;
  
  void readFully(double[] paramArrayOfDouble, int paramInt1, int paramInt2) throws IOException;
  
  long getStreamPosition() throws IOException;
  
  int getBitOffset() throws IOException;
  
  void setBitOffset(int paramInt) throws IOException;
  
  int readBit() throws IOException;
  
  long readBits(int paramInt) throws IOException;
  
  long length() throws IOException;
  
  int skipBytes(int paramInt) throws IOException;
  
  long skipBytes(long paramLong) throws IOException;
  
  void seek(long paramLong) throws IOException;
  
  void mark();
  
  void reset();
  
  void flushBefore(long paramLong) throws IOException;
  
  void flush();
  
  long getFlushedPosition() throws IOException;
  
  boolean isCached() throws IOException;
  
  boolean isCachedMemory() throws IOException;
  
  boolean isCachedFile() throws IOException;
  
  void close();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\stream\ImageInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */