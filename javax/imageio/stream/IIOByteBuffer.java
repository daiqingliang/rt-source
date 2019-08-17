package javax.imageio.stream;

public class IIOByteBuffer {
  private byte[] data;
  
  private int offset;
  
  private int length;
  
  public IIOByteBuffer(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    this.data = paramArrayOfByte;
    this.offset = paramInt1;
    this.length = paramInt2;
  }
  
  public byte[] getData() { return this.data; }
  
  public void setData(byte[] paramArrayOfByte) { this.data = paramArrayOfByte; }
  
  public int getOffset() { return this.offset; }
  
  public void setOffset(int paramInt) { this.offset = paramInt; }
  
  public int getLength() { return this.length; }
  
  public void setLength(int paramInt) { this.length = paramInt; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\stream\IIOByteBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */