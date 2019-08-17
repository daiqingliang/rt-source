package sun.reflect;

class ByteVectorImpl implements ByteVector {
  private byte[] data;
  
  private int pos;
  
  public ByteVectorImpl() { this(100); }
  
  public ByteVectorImpl(int paramInt) {
    this.data = new byte[paramInt];
    this.pos = -1;
  }
  
  public int getLength() { return this.pos + 1; }
  
  public byte get(int paramInt) {
    if (paramInt >= this.data.length) {
      resize(paramInt);
      this.pos = paramInt;
    } 
    return this.data[paramInt];
  }
  
  public void put(int paramInt, byte paramByte) {
    if (paramInt >= this.data.length) {
      resize(paramInt);
      this.pos = paramInt;
    } 
    this.data[paramInt] = paramByte;
  }
  
  public void add(byte paramByte) {
    if (++this.pos >= this.data.length)
      resize(this.pos); 
    this.data[this.pos] = paramByte;
  }
  
  public void trim() {
    if (this.pos != this.data.length - 1) {
      byte[] arrayOfByte = new byte[this.pos + 1];
      System.arraycopy(this.data, 0, arrayOfByte, 0, this.pos + 1);
      this.data = arrayOfByte;
    } 
  }
  
  public byte[] getData() { return this.data; }
  
  private void resize(int paramInt) {
    if (paramInt <= 2 * this.data.length)
      paramInt = 2 * this.data.length; 
    byte[] arrayOfByte = new byte[paramInt];
    System.arraycopy(this.data, 0, arrayOfByte, 0, this.data.length);
    this.data = arrayOfByte;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\ByteVectorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */