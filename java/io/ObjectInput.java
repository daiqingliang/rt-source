package java.io;

public interface ObjectInput extends DataInput, AutoCloseable {
  Object readObject() throws ClassNotFoundException, IOException;
  
  int read() throws IOException;
  
  int read(byte[] paramArrayOfByte) throws IOException;
  
  int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException;
  
  long skip(long paramLong) throws IOException;
  
  int available() throws IOException;
  
  void close() throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\ObjectInput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */