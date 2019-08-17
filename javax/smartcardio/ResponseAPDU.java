package javax.smartcardio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;

public final class ResponseAPDU implements Serializable {
  private static final long serialVersionUID = 6962744978375594225L;
  
  private byte[] apdu;
  
  public ResponseAPDU(byte[] paramArrayOfByte) {
    paramArrayOfByte = (byte[])paramArrayOfByte.clone();
    check(paramArrayOfByte);
    this.apdu = paramArrayOfByte;
  }
  
  private static void check(byte[] paramArrayOfByte) {
    if (paramArrayOfByte.length < 2)
      throw new IllegalArgumentException("apdu must be at least 2 bytes long"); 
  }
  
  public int getNr() { return this.apdu.length - 2; }
  
  public byte[] getData() {
    byte[] arrayOfByte = new byte[this.apdu.length - 2];
    System.arraycopy(this.apdu, 0, arrayOfByte, 0, arrayOfByte.length);
    return arrayOfByte;
  }
  
  public int getSW1() { return this.apdu[this.apdu.length - 2] & 0xFF; }
  
  public int getSW2() { return this.apdu[this.apdu.length - 1] & 0xFF; }
  
  public int getSW() { return getSW1() << 8 | getSW2(); }
  
  public byte[] getBytes() { return (byte[])this.apdu.clone(); }
  
  public String toString() { return "ResponseAPDU: " + this.apdu.length + " bytes, SW=" + Integer.toHexString(getSW()); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof ResponseAPDU))
      return false; 
    ResponseAPDU responseAPDU = (ResponseAPDU)paramObject;
    return Arrays.equals(this.apdu, responseAPDU.apdu);
  }
  
  public int hashCode() { return Arrays.hashCode(this.apdu); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    this.apdu = (byte[])paramObjectInputStream.readUnshared();
    check(this.apdu);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\smartcardio\ResponseAPDU.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */