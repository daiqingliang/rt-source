package java.io;

public class DataInputStream extends FilterInputStream implements DataInput {
  private byte[] bytearr = new byte[80];
  
  private char[] chararr = new char[80];
  
  private byte[] readBuffer = new byte[8];
  
  private char[] lineBuffer;
  
  public DataInputStream(InputStream paramInputStream) { super(paramInputStream); }
  
  public final int read(byte[] paramArrayOfByte) throws IOException { return this.in.read(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public final int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException { return this.in.read(paramArrayOfByte, paramInt1, paramInt2); }
  
  public final void readFully(byte[] paramArrayOfByte) throws IOException { readFully(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public final void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (paramInt2 < 0)
      throw new IndexOutOfBoundsException(); 
    int i;
    for (i = 0; i < paramInt2; i += j) {
      int j = this.in.read(paramArrayOfByte, paramInt1 + i, paramInt2 - i);
      if (j < 0)
        throw new EOFException(); 
    } 
  }
  
  public final int skipBytes(int paramInt) throws IOException {
    int i = 0;
    int j = 0;
    while (i < paramInt && (j = (int)this.in.skip((paramInt - i))) > 0)
      i += j; 
    return i;
  }
  
  public final boolean readBoolean() throws IOException {
    int i = this.in.read();
    if (i < 0)
      throw new EOFException(); 
    return (i != 0);
  }
  
  public final byte readByte() throws IOException {
    int i = this.in.read();
    if (i < 0)
      throw new EOFException(); 
    return (byte)i;
  }
  
  public final int readUnsignedByte() throws IOException {
    int i = this.in.read();
    if (i < 0)
      throw new EOFException(); 
    return i;
  }
  
  public final short readShort() throws IOException {
    int i = this.in.read();
    int j = this.in.read();
    if ((i | j) < 0)
      throw new EOFException(); 
    return (short)((i << 8) + (j << 0));
  }
  
  public final int readUnsignedShort() throws IOException {
    int i = this.in.read();
    int j = this.in.read();
    if ((i | j) < 0)
      throw new EOFException(); 
    return (i << 8) + (j << 0);
  }
  
  public final char readChar() throws IOException {
    int i = this.in.read();
    int j = this.in.read();
    if ((i | j) < 0)
      throw new EOFException(); 
    return (char)((i << 8) + (j << 0));
  }
  
  public final int readInt() throws IOException {
    int i = this.in.read();
    int j = this.in.read();
    int k = this.in.read();
    int m = this.in.read();
    if ((i | j | k | m) < 0)
      throw new EOFException(); 
    return (i << 24) + (j << 16) + (k << 8) + (m << 0);
  }
  
  public final long readLong() throws IOException {
    readFully(this.readBuffer, 0, 8);
    return (this.readBuffer[0] << 56) + ((this.readBuffer[1] & 0xFF) << 48) + ((this.readBuffer[2] & 0xFF) << 40) + ((this.readBuffer[3] & 0xFF) << 32) + ((this.readBuffer[4] & 0xFF) << 24) + ((this.readBuffer[5] & 0xFF) << 16) + ((this.readBuffer[6] & 0xFF) << 8) + ((this.readBuffer[7] & 0xFF) << 0);
  }
  
  public final float readFloat() throws IOException { return Float.intBitsToFloat(readInt()); }
  
  public final double readDouble() throws IOException { return Double.longBitsToDouble(readLong()); }
  
  @Deprecated
  public final String readLine() throws IOException {
    int k;
    char[] arrayOfChar = this.lineBuffer;
    if (arrayOfChar == null)
      arrayOfChar = this.lineBuffer = new char[128]; 
    int i = arrayOfChar.length;
    int j = 0;
    while (true) {
      int m;
      switch (k = this.in.read()) {
        case -1:
        case 10:
          break;
        case 13:
          m = this.in.read();
          if (m != 10 && m != -1) {
            if (!(this.in instanceof PushbackInputStream))
              this.in = new PushbackInputStream(this.in); 
            ((PushbackInputStream)this.in).unread(m);
          } 
          break;
      } 
      if (--i < 0) {
        arrayOfChar = new char[j + ''];
        i = arrayOfChar.length - j - 1;
        System.arraycopy(this.lineBuffer, 0, arrayOfChar, 0, j);
        this.lineBuffer = arrayOfChar;
      } 
      arrayOfChar[j++] = (char)k;
    } 
    return (k == -1 && j == 0) ? null : String.copyValueOf(arrayOfChar, 0, j);
  }
  
  public final String readUTF() throws IOException { return readUTF(this); }
  
  public static final String readUTF(DataInput paramDataInput) throws IOException {
    int i = paramDataInput.readUnsignedShort();
    byte[] arrayOfByte = null;
    char[] arrayOfChar = null;
    if (paramDataInput instanceof DataInputStream) {
      DataInputStream dataInputStream = (DataInputStream)paramDataInput;
      if (dataInputStream.bytearr.length < i) {
        dataInputStream.bytearr = new byte[i * 2];
        dataInputStream.chararr = new char[i * 2];
      } 
      arrayOfChar = dataInputStream.chararr;
      arrayOfByte = dataInputStream.bytearr;
    } else {
      arrayOfByte = new byte[i];
      arrayOfChar = new char[i];
    } 
    byte b1 = 0;
    byte b2 = 0;
    paramDataInput.readFully(arrayOfByte, 0, i);
    while (b1 < i) {
      byte b = arrayOfByte[b1] & 0xFF;
      if (b > Byte.MAX_VALUE)
        break; 
      b1++;
      arrayOfChar[b2++] = (char)b;
    } 
    while (b1 < i) {
      byte b5;
      byte b4;
      byte b3 = arrayOfByte[b1] & 0xFF;
      switch (b3 >> 4) {
        case 0:
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
          b1++;
          arrayOfChar[b2++] = (char)b3;
          continue;
        case 12:
        case 13:
          b1 += 2;
          if (b1 > i)
            throw new UTFDataFormatException("malformed input: partial character at end"); 
          b4 = arrayOfByte[b1 - 1];
          if ((b4 & 0xC0) != 128)
            throw new UTFDataFormatException("malformed input around byte " + b1); 
          arrayOfChar[b2++] = (char)((b3 & 0x1F) << 6 | b4 & 0x3F);
          continue;
        case 14:
          b1 += 3;
          if (b1 > i)
            throw new UTFDataFormatException("malformed input: partial character at end"); 
          b4 = arrayOfByte[b1 - 2];
          b5 = arrayOfByte[b1 - 1];
          if ((b4 & 0xC0) != 128 || (b5 & 0xC0) != 128)
            throw new UTFDataFormatException("malformed input around byte " + (b1 - 1)); 
          arrayOfChar[b2++] = (char)((b3 & 0xF) << 12 | (b4 & 0x3F) << 6 | (b5 & 0x3F) << 0);
          continue;
      } 
      throw new UTFDataFormatException("malformed input around byte " + b1);
    } 
    return new String(arrayOfChar, 0, b2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\DataInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */