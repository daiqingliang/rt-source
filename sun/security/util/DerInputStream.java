package sun.security.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.Vector;

public class DerInputStream {
  DerInputBuffer buffer;
  
  public byte tag;
  
  public DerInputStream(byte[] paramArrayOfByte) throws IOException { init(paramArrayOfByte, 0, paramArrayOfByte.length, true); }
  
  public DerInputStream(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean) throws IOException { init(paramArrayOfByte, paramInt1, paramInt2, paramBoolean); }
  
  public DerInputStream(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException { init(paramArrayOfByte, paramInt1, paramInt2, true); }
  
  private void init(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean) throws IOException {
    if (paramInt1 + 2 > paramArrayOfByte.length || paramInt1 + paramInt2 > paramArrayOfByte.length)
      throw new IOException("Encoding bytes too short"); 
    if (DerIndefLenConverter.isIndefinite(paramArrayOfByte[paramInt1 + 1])) {
      if (!paramBoolean)
        throw new IOException("Indefinite length BER encoding found"); 
      byte[] arrayOfByte = new byte[paramInt2];
      System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, paramInt2);
      DerIndefLenConverter derIndefLenConverter = new DerIndefLenConverter();
      this.buffer = new DerInputBuffer(derIndefLenConverter.convert(arrayOfByte), paramBoolean);
    } else {
      this.buffer = new DerInputBuffer(paramArrayOfByte, paramInt1, paramInt2, paramBoolean);
    } 
    this.buffer.mark(2147483647);
  }
  
  DerInputStream(DerInputBuffer paramDerInputBuffer) {
    this.buffer = paramDerInputBuffer;
    this.buffer.mark(2147483647);
  }
  
  public DerInputStream subStream(int paramInt, boolean paramBoolean) throws IOException {
    DerInputBuffer derInputBuffer = this.buffer.dup();
    derInputBuffer.truncate(paramInt);
    if (paramBoolean)
      this.buffer.skip(paramInt); 
    return new DerInputStream(derInputBuffer);
  }
  
  public byte[] toByteArray() { return this.buffer.toByteArray(); }
  
  public int getInteger() throws IOException {
    if (this.buffer.read() != 2)
      throw new IOException("DER input, Integer tag error"); 
    return this.buffer.getInteger(getLength(this.buffer));
  }
  
  public BigInteger getBigInteger() throws IOException {
    if (this.buffer.read() != 2)
      throw new IOException("DER input, Integer tag error"); 
    return this.buffer.getBigInteger(getLength(this.buffer), false);
  }
  
  public BigInteger getPositiveBigInteger() throws IOException {
    if (this.buffer.read() != 2)
      throw new IOException("DER input, Integer tag error"); 
    return this.buffer.getBigInteger(getLength(this.buffer), true);
  }
  
  public int getEnumerated() throws IOException {
    if (this.buffer.read() != 10)
      throw new IOException("DER input, Enumerated tag error"); 
    return this.buffer.getInteger(getLength(this.buffer));
  }
  
  public byte[] getBitString() {
    if (this.buffer.read() != 3)
      throw new IOException("DER input not an bit string"); 
    return this.buffer.getBitString(getLength(this.buffer));
  }
  
  public BitArray getUnalignedBitString() throws IOException {
    if (this.buffer.read() != 3)
      throw new IOException("DER input not a bit string"); 
    int i = getLength(this.buffer) - 1;
    int j = this.buffer.read();
    if (j < 0)
      throw new IOException("Unused bits of bit string invalid"); 
    int k = i * 8 - j;
    if (k < 0)
      throw new IOException("Valid bits of bit string invalid"); 
    byte[] arrayOfByte = new byte[i];
    if (i != 0 && this.buffer.read(arrayOfByte) != i)
      throw new IOException("Short read of DER bit string"); 
    return new BitArray(k, arrayOfByte);
  }
  
  public byte[] getOctetString() {
    if (this.buffer.read() != 4)
      throw new IOException("DER input not an octet string"); 
    int i = getLength(this.buffer);
    byte[] arrayOfByte = new byte[i];
    if (i != 0 && this.buffer.read(arrayOfByte) != i)
      throw new IOException("Short read of DER octet string"); 
    return arrayOfByte;
  }
  
  public void getBytes(byte[] paramArrayOfByte) throws IOException {
    if (paramArrayOfByte.length != 0 && this.buffer.read(paramArrayOfByte) != paramArrayOfByte.length)
      throw new IOException("Short read of DER octet string"); 
  }
  
  public void getNull() throws IOException {
    if (this.buffer.read() != 5 || this.buffer.read() != 0)
      throw new IOException("getNull, bad data"); 
  }
  
  public ObjectIdentifier getOID() throws IOException { return new ObjectIdentifier(this); }
  
  public DerValue[] getSequence(int paramInt) throws IOException {
    this.tag = (byte)this.buffer.read();
    if (this.tag != 48)
      throw new IOException("Sequence tag error"); 
    return readVector(paramInt);
  }
  
  public DerValue[] getSet(int paramInt) throws IOException {
    this.tag = (byte)this.buffer.read();
    if (this.tag != 49)
      throw new IOException("Set tag error"); 
    return readVector(paramInt);
  }
  
  public DerValue[] getSet(int paramInt, boolean paramBoolean) throws IOException {
    this.tag = (byte)this.buffer.read();
    if (!paramBoolean && this.tag != 49)
      throw new IOException("Set tag error"); 
    return readVector(paramInt);
  }
  
  protected DerValue[] readVector(int paramInt) throws IOException {
    DerInputStream derInputStream;
    byte b = (byte)this.buffer.read();
    int i = getLength(b, this.buffer);
    if (i == -1) {
      int k = this.buffer.available();
      int m = 2;
      byte[] arrayOfByte = new byte[k + m];
      arrayOfByte[0] = this.tag;
      arrayOfByte[1] = b;
      DataInputStream dataInputStream = new DataInputStream(this.buffer);
      dataInputStream.readFully(arrayOfByte, m, k);
      dataInputStream.close();
      DerIndefLenConverter derIndefLenConverter = new DerIndefLenConverter();
      this.buffer = new DerInputBuffer(derIndefLenConverter.convert(arrayOfByte), this.buffer.allowBER);
      if (this.tag != this.buffer.read())
        throw new IOException("Indefinite length encoding not supported"); 
      i = getLength(this.buffer);
    } 
    if (i == 0)
      return new DerValue[0]; 
    if (this.buffer.available() == i) {
      derInputStream = this;
    } else {
      derInputStream = subStream(i, true);
    } 
    Vector vector = new Vector(paramInt);
    do {
      DerValue derValue = new DerValue(derInputStream.buffer, this.buffer.allowBER);
      vector.addElement(derValue);
    } while (derInputStream.available() > 0);
    if (derInputStream.available() != 0)
      throw new IOException("Extra data at end of vector"); 
    int j = vector.size();
    DerValue[] arrayOfDerValue = new DerValue[j];
    for (byte b1 = 0; b1 < j; b1++)
      arrayOfDerValue[b1] = (DerValue)vector.elementAt(b1); 
    return arrayOfDerValue;
  }
  
  public DerValue getDerValue() throws IOException { return new DerValue(this.buffer); }
  
  public String getUTF8String() throws IOException { return readString((byte)12, "UTF-8", "UTF8"); }
  
  public String getPrintableString() throws IOException { return readString((byte)19, "Printable", "ASCII"); }
  
  public String getT61String() throws IOException { return readString((byte)20, "T61", "ISO-8859-1"); }
  
  public String getIA5String() throws IOException { return readString((byte)22, "IA5", "ASCII"); }
  
  public String getBMPString() throws IOException { return readString((byte)30, "BMP", "UnicodeBigUnmarked"); }
  
  public String getGeneralString() throws IOException { return readString((byte)27, "General", "ASCII"); }
  
  private String readString(byte paramByte, String paramString1, String paramString2) throws IOException {
    if (this.buffer.read() != paramByte)
      throw new IOException("DER input not a " + paramString1 + " string"); 
    int i = getLength(this.buffer);
    byte[] arrayOfByte = new byte[i];
    if (i != 0 && this.buffer.read(arrayOfByte) != i)
      throw new IOException("Short read of DER " + paramString1 + " string"); 
    return new String(arrayOfByte, paramString2);
  }
  
  public Date getUTCTime() throws IOException {
    if (this.buffer.read() != 23)
      throw new IOException("DER input, UTCtime tag invalid "); 
    return this.buffer.getUTCTime(getLength(this.buffer));
  }
  
  public Date getGeneralizedTime() throws IOException {
    if (this.buffer.read() != 24)
      throw new IOException("DER input, GeneralizedTime tag invalid "); 
    return this.buffer.getGeneralizedTime(getLength(this.buffer));
  }
  
  int getByte() throws IOException { return 0xFF & this.buffer.read(); }
  
  public int peekByte() throws IOException { return this.buffer.peek(); }
  
  int getLength() throws IOException { return getLength(this.buffer); }
  
  static int getLength(InputStream paramInputStream) throws IOException { return getLength(paramInputStream.read(), paramInputStream); }
  
  static int getLength(int paramInt, InputStream paramInputStream) throws IOException {
    int i;
    if (paramInt == -1)
      throw new IOException("Short read of DER length"); 
    String str = "DerInputStream.getLength(): ";
    int j = paramInt;
    if ((j & 0x80) == 0) {
      i = j;
    } else {
      j &= 0x7F;
      if (j == 0)
        return -1; 
      if (j < 0 || j > 4)
        throw new IOException(str + "lengthTag=" + j + ", " + ((j < 0) ? "incorrect DER encoding." : "too big.")); 
      i = 0xFF & paramInputStream.read();
      j--;
      if (i == 0)
        throw new IOException(str + "Redundant length bytes found"); 
      while (j-- > 0) {
        i <<= 8;
        i += (0xFF & paramInputStream.read());
      } 
      if (i < 0)
        throw new IOException(str + "Invalid length bytes"); 
      if (i <= 127)
        throw new IOException(str + "Should use short form for length"); 
    } 
    return i;
  }
  
  public void mark(int paramInt) { this.buffer.mark(paramInt); }
  
  public void reset() throws IOException { this.buffer.reset(); }
  
  public int available() throws IOException { return this.buffer.available(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\securit\\util\DerInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */