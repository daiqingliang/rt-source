package java.io;

public interface DataInput {
  void readFully(byte[] paramArrayOfByte) throws IOException;
  
  void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException;
  
  int skipBytes(int paramInt) throws IOException;
  
  boolean readBoolean() throws IOException;
  
  byte readByte() throws IOException;
  
  int readUnsignedByte() throws IOException;
  
  short readShort() throws IOException;
  
  int readUnsignedShort() throws IOException;
  
  char readChar() throws IOException;
  
  int readInt() throws IOException;
  
  long readLong() throws IOException;
  
  float readFloat() throws IOException;
  
  double readDouble() throws IOException;
  
  String readLine() throws IOException;
  
  String readUTF() throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\DataInput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */