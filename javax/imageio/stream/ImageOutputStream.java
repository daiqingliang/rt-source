package javax.imageio.stream;

import java.io.DataOutput;
import java.io.IOException;

public interface ImageOutputStream extends ImageInputStream, DataOutput {
  void write(int paramInt) throws IOException;
  
  void write(byte[] paramArrayOfByte) throws IOException;
  
  void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException;
  
  void writeBoolean(boolean paramBoolean) throws IOException;
  
  void writeByte(int paramInt) throws IOException;
  
  void writeShort(int paramInt) throws IOException;
  
  void writeChar(int paramInt) throws IOException;
  
  void writeInt(int paramInt) throws IOException;
  
  void writeLong(long paramLong) throws IOException;
  
  void writeFloat(float paramFloat) throws IOException;
  
  void writeDouble(double paramDouble) throws IOException;
  
  void writeBytes(String paramString) throws IOException;
  
  void writeChars(String paramString) throws IOException;
  
  void writeUTF(String paramString) throws IOException;
  
  void writeShorts(short[] paramArrayOfShort, int paramInt1, int paramInt2) throws IOException;
  
  void writeChars(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException;
  
  void writeInts(int[] paramArrayOfInt, int paramInt1, int paramInt2) throws IOException;
  
  void writeLongs(long[] paramArrayOfLong, int paramInt1, int paramInt2) throws IOException;
  
  void writeFloats(float[] paramArrayOfFloat, int paramInt1, int paramInt2) throws IOException;
  
  void writeDoubles(double[] paramArrayOfDouble, int paramInt1, int paramInt2) throws IOException;
  
  void writeBit(int paramInt) throws IOException;
  
  void writeBits(long paramLong, int paramInt) throws IOException;
  
  void flushBefore(long paramLong) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\stream\ImageOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */