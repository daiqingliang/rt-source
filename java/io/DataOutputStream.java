package java.io;

public class DataOutputStream extends FilterOutputStream implements DataOutput {
  protected int written;
  
  private byte[] bytearr = null;
  
  private byte[] writeBuffer = new byte[8];
  
  public DataOutputStream(OutputStream paramOutputStream) { super(paramOutputStream); }
  
  private void incCount(int paramInt) {
    int i = this.written + paramInt;
    if (i < 0)
      i = Integer.MAX_VALUE; 
    this.written = i;
  }
  
  public void write(int paramInt) {
    this.out.write(paramInt);
    incCount(1);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    this.out.write(paramArrayOfByte, paramInt1, paramInt2);
    incCount(paramInt2);
  }
  
  public void flush() throws IOException { this.out.flush(); }
  
  public final void writeBoolean(boolean paramBoolean) throws IOException {
    this.out.write(paramBoolean ? 1 : 0);
    incCount(1);
  }
  
  public final void writeByte(int paramInt) {
    this.out.write(paramInt);
    incCount(1);
  }
  
  public final void writeShort(int paramInt) {
    this.out.write(paramInt >>> 8 & 0xFF);
    this.out.write(paramInt >>> 0 & 0xFF);
    incCount(2);
  }
  
  public final void writeChar(int paramInt) {
    this.out.write(paramInt >>> 8 & 0xFF);
    this.out.write(paramInt >>> 0 & 0xFF);
    incCount(2);
  }
  
  public final void writeInt(int paramInt) {
    this.out.write(paramInt >>> 24 & 0xFF);
    this.out.write(paramInt >>> 16 & 0xFF);
    this.out.write(paramInt >>> 8 & 0xFF);
    this.out.write(paramInt >>> 0 & 0xFF);
    incCount(4);
  }
  
  public final void writeLong(long paramLong) throws IOException {
    this.writeBuffer[0] = (byte)(int)(paramLong >>> 56);
    this.writeBuffer[1] = (byte)(int)(paramLong >>> 48);
    this.writeBuffer[2] = (byte)(int)(paramLong >>> 40);
    this.writeBuffer[3] = (byte)(int)(paramLong >>> 32);
    this.writeBuffer[4] = (byte)(int)(paramLong >>> 24);
    this.writeBuffer[5] = (byte)(int)(paramLong >>> 16);
    this.writeBuffer[6] = (byte)(int)(paramLong >>> 8);
    this.writeBuffer[7] = (byte)(int)(paramLong >>> false);
    this.out.write(this.writeBuffer, 0, 8);
    incCount(8);
  }
  
  public final void writeFloat(float paramFloat) throws IOException { writeInt(Float.floatToIntBits(paramFloat)); }
  
  public final void writeDouble(double paramDouble) throws IOException { writeLong(Double.doubleToLongBits(paramDouble)); }
  
  public final void writeBytes(String paramString) throws IOException {
    int i = paramString.length();
    for (byte b = 0; b < i; b++)
      this.out.write((byte)paramString.charAt(b)); 
    incCount(i);
  }
  
  public final void writeChars(String paramString) throws IOException {
    int i = paramString.length();
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      this.out.write(c >>> '\b' & 0xFF);
      this.out.write(c >>> Character.MIN_VALUE & 0xFF);
    } 
    incCount(i * 2);
  }
  
  public final void writeUTF(String paramString) throws IOException { writeUTF(paramString, this); }
  
  static int writeUTF(String paramString, DataOutput paramDataOutput) throws IOException {
    int i = paramString.length();
    byte b1 = 0;
    byte b2 = 0;
    for (byte b3 = 0; b3 < i; b3++) {
      char c = paramString.charAt(b3);
      if (c >= '\001' && c <= '') {
        b1++;
      } else if (c > '߿') {
        b1 += 3;
      } else {
        b1 += 2;
      } 
    } 
    if (b1 > '￿')
      throw new UTFDataFormatException("encoded string too long: " + b1 + " bytes"); 
    byte[] arrayOfByte = null;
    if (paramDataOutput instanceof DataOutputStream) {
      DataOutputStream dataOutputStream = (DataOutputStream)paramDataOutput;
      if (dataOutputStream.bytearr == null || dataOutputStream.bytearr.length < b1 + 2)
        dataOutputStream.bytearr = new byte[b1 * 2 + 2]; 
      arrayOfByte = dataOutputStream.bytearr;
    } else {
      arrayOfByte = new byte[b1 + 2];
    } 
    arrayOfByte[b2++] = (byte)(b1 >>> 8 & 0xFF);
    arrayOfByte[b2++] = (byte)(b1 >>> 0 & 0xFF);
    byte b4 = 0;
    for (b4 = 0; b4 < i; b4++) {
      char c = paramString.charAt(b4);
      if (c < '\001' || c > '')
        break; 
      arrayOfByte[b2++] = (byte)c;
    } 
    while (b4 < i) {
      char c = paramString.charAt(b4);
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
      b4++;
    } 
    paramDataOutput.write(arrayOfByte, 0, b1 + 2);
    return b1 + 2;
  }
  
  public final int size() { return this.written; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\DataOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */