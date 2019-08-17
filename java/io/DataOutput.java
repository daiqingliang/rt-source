package java.io;

public interface DataOutput {
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
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\DataOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */