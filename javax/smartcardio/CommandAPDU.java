package javax.smartcardio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;

public final class CommandAPDU implements Serializable {
  private static final long serialVersionUID = 398698301286670877L;
  
  private static final int MAX_APDU_SIZE = 65544;
  
  private byte[] apdu;
  
  private int nc;
  
  private int ne;
  
  private int dataOffset;
  
  public CommandAPDU(byte[] paramArrayOfByte) {
    this.apdu = (byte[])paramArrayOfByte.clone();
    parse();
  }
  
  public CommandAPDU(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    checkArrayBounds(paramArrayOfByte, paramInt1, paramInt2);
    this.apdu = new byte[paramInt2];
    System.arraycopy(paramArrayOfByte, paramInt1, this.apdu, 0, paramInt2);
    parse();
  }
  
  private void checkArrayBounds(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (paramInt1 < 0 || paramInt2 < 0)
      throw new IllegalArgumentException("Offset and length must not be negative"); 
    if (paramArrayOfByte == null) {
      if (paramInt1 != 0 && paramInt2 != 0)
        throw new IllegalArgumentException("offset and length must be 0 if array is null"); 
    } else if (paramInt1 > paramArrayOfByte.length - paramInt2) {
      throw new IllegalArgumentException("Offset plus length exceed array size");
    } 
  }
  
  public CommandAPDU(ByteBuffer paramByteBuffer) {
    this.apdu = new byte[paramByteBuffer.remaining()];
    paramByteBuffer.get(this.apdu);
    parse();
  }
  
  public CommandAPDU(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { this(paramInt1, paramInt2, paramInt3, paramInt4, null, 0, 0, 0); }
  
  public CommandAPDU(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { this(paramInt1, paramInt2, paramInt3, paramInt4, null, 0, 0, paramInt5); }
  
  public CommandAPDU(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte) { this(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfByte, 0, arrayLength(paramArrayOfByte), 0); }
  
  public CommandAPDU(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, int paramInt5, int paramInt6) { this(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfByte, paramInt5, paramInt6, 0); }
  
  public CommandAPDU(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, int paramInt5) { this(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfByte, 0, arrayLength(paramArrayOfByte), paramInt5); }
  
  private static int arrayLength(byte[] paramArrayOfByte) { return (paramArrayOfByte != null) ? paramArrayOfByte.length : 0; }
  
  private void parse() {
    if (this.apdu.length < 4)
      throw new IllegalArgumentException("apdu must be at least 4 bytes long"); 
    if (this.apdu.length == 4)
      return; 
    byte b1 = this.apdu[4] & 0xFF;
    if (this.apdu.length == 5) {
      this.ne = (b1 == 0) ? 256 : b1;
      return;
    } 
    if (b1 != 0) {
      if (this.apdu.length == 5 + b1) {
        this.nc = b1;
        this.dataOffset = 5;
        return;
      } 
      if (this.apdu.length == 6 + b1) {
        this.nc = b1;
        this.dataOffset = 5;
        byte b = this.apdu[this.apdu.length - 1] & 0xFF;
        this.ne = (b == 0) ? 256 : b;
        return;
      } 
      throw new IllegalArgumentException("Invalid APDU: length=" + this.apdu.length + ", b1=" + b1);
    } 
    if (this.apdu.length < 7)
      throw new IllegalArgumentException("Invalid APDU: length=" + this.apdu.length + ", b1=" + b1); 
    byte b2 = (this.apdu[5] & 0xFF) << 8 | this.apdu[6] & 0xFF;
    if (this.apdu.length == 7) {
      this.ne = (b2 == 0) ? 65536 : b2;
      return;
    } 
    if (b2 == 0)
      throw new IllegalArgumentException("Invalid APDU: length=" + this.apdu.length + ", b1=" + b1 + ", b2||b3=" + b2); 
    if (this.apdu.length == 7 + b2) {
      this.nc = b2;
      this.dataOffset = 7;
      return;
    } 
    if (this.apdu.length == 9 + b2) {
      this.nc = b2;
      this.dataOffset = 7;
      int i = this.apdu.length - 2;
      byte b = (this.apdu[i] & 0xFF) << 8 | this.apdu[i + 1] & 0xFF;
      this.ne = (b == 0) ? 65536 : b;
    } else {
      throw new IllegalArgumentException("Invalid APDU: length=" + this.apdu.length + ", b1=" + b1 + ", b2||b3=" + b2);
    } 
  }
  
  public CommandAPDU(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, int paramInt5, int paramInt6, int paramInt7) {
    checkArrayBounds(paramArrayOfByte, paramInt5, paramInt6);
    if (paramInt6 > 65535)
      throw new IllegalArgumentException("dataLength is too large"); 
    if (paramInt7 < 0)
      throw new IllegalArgumentException("ne must not be negative"); 
    if (paramInt7 > 65536)
      throw new IllegalArgumentException("ne is too large"); 
    this.ne = paramInt7;
    this.nc = paramInt6;
    if (paramInt6 == 0) {
      if (paramInt7 == 0) {
        this.apdu = new byte[4];
        setHeader(paramInt1, paramInt2, paramInt3, paramInt4);
      } else if (paramInt7 <= 256) {
        byte b = (paramInt7 != 256) ? (byte)paramInt7 : 0;
        this.apdu = new byte[5];
        setHeader(paramInt1, paramInt2, paramInt3, paramInt4);
        this.apdu[4] = b;
      } else {
        byte b2;
        byte b1;
        if (paramInt7 == 65536) {
          b1 = 0;
          b2 = 0;
        } else {
          b1 = (byte)(paramInt7 >> 8);
          b2 = (byte)paramInt7;
        } 
        this.apdu = new byte[7];
        setHeader(paramInt1, paramInt2, paramInt3, paramInt4);
        this.apdu[5] = b1;
        this.apdu[6] = b2;
      } 
    } else if (paramInt7 == 0) {
      if (paramInt6 <= 255) {
        this.apdu = new byte[5 + paramInt6];
        setHeader(paramInt1, paramInt2, paramInt3, paramInt4);
        this.apdu[4] = (byte)paramInt6;
        this.dataOffset = 5;
        System.arraycopy(paramArrayOfByte, paramInt5, this.apdu, 5, paramInt6);
      } else {
        this.apdu = new byte[7 + paramInt6];
        setHeader(paramInt1, paramInt2, paramInt3, paramInt4);
        this.apdu[4] = 0;
        this.apdu[5] = (byte)(paramInt6 >> 8);
        this.apdu[6] = (byte)paramInt6;
        this.dataOffset = 7;
        System.arraycopy(paramArrayOfByte, paramInt5, this.apdu, 7, paramInt6);
      } 
    } else if (paramInt6 <= 255 && paramInt7 <= 256) {
      this.apdu = new byte[6 + paramInt6];
      setHeader(paramInt1, paramInt2, paramInt3, paramInt4);
      this.apdu[4] = (byte)paramInt6;
      this.dataOffset = 5;
      System.arraycopy(paramArrayOfByte, paramInt5, this.apdu, 5, paramInt6);
      this.apdu[this.apdu.length - 1] = (paramInt7 != 256) ? (byte)paramInt7 : 0;
    } else {
      this.apdu = new byte[9 + paramInt6];
      setHeader(paramInt1, paramInt2, paramInt3, paramInt4);
      this.apdu[4] = 0;
      this.apdu[5] = (byte)(paramInt6 >> 8);
      this.apdu[6] = (byte)paramInt6;
      this.dataOffset = 7;
      System.arraycopy(paramArrayOfByte, paramInt5, this.apdu, 7, paramInt6);
      if (paramInt7 != 65536) {
        int i = this.apdu.length - 2;
        this.apdu[i] = (byte)(paramInt7 >> 8);
        this.apdu[i + 1] = (byte)paramInt7;
      } 
    } 
  }
  
  private void setHeader(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.apdu[0] = (byte)paramInt1;
    this.apdu[1] = (byte)paramInt2;
    this.apdu[2] = (byte)paramInt3;
    this.apdu[3] = (byte)paramInt4;
  }
  
  public int getCLA() { return this.apdu[0] & 0xFF; }
  
  public int getINS() { return this.apdu[1] & 0xFF; }
  
  public int getP1() { return this.apdu[2] & 0xFF; }
  
  public int getP2() { return this.apdu[3] & 0xFF; }
  
  public int getNc() { return this.nc; }
  
  public byte[] getData() {
    byte[] arrayOfByte = new byte[this.nc];
    System.arraycopy(this.apdu, this.dataOffset, arrayOfByte, 0, this.nc);
    return arrayOfByte;
  }
  
  public int getNe() { return this.ne; }
  
  public byte[] getBytes() { return (byte[])this.apdu.clone(); }
  
  public String toString() { return "CommmandAPDU: " + this.apdu.length + " bytes, nc=" + this.nc + ", ne=" + this.ne; }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof CommandAPDU))
      return false; 
    CommandAPDU commandAPDU = (CommandAPDU)paramObject;
    return Arrays.equals(this.apdu, commandAPDU.apdu);
  }
  
  public int hashCode() { return Arrays.hashCode(this.apdu); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    this.apdu = (byte[])paramObjectInputStream.readUnshared();
    parse();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\smartcardio\CommandAPDU.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */