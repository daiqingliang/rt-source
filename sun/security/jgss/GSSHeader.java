package sun.security.jgss;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.ietf.jgss.GSSException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class GSSHeader {
  private ObjectIdentifier mechOid = null;
  
  private byte[] mechOidBytes = null;
  
  private int mechTokenLength = 0;
  
  public static final int TOKEN_ID = 96;
  
  public GSSHeader(ObjectIdentifier paramObjectIdentifier, int paramInt) throws IOException {
    this.mechOid = paramObjectIdentifier;
    DerOutputStream derOutputStream = new DerOutputStream();
    derOutputStream.putOID(paramObjectIdentifier);
    this.mechOidBytes = derOutputStream.toByteArray();
    this.mechTokenLength = paramInt;
  }
  
  public GSSHeader(InputStream paramInputStream) throws IOException, GSSException {
    int i = paramInputStream.read();
    if (i != 96)
      throw new GSSException(10, -1, "GSSHeader did not find the right tag"); 
    int j = getLength(paramInputStream);
    DerValue derValue = new DerValue(paramInputStream);
    this.mechOidBytes = derValue.toByteArray();
    this.mechOid = derValue.getOID();
    this.mechTokenLength = j - this.mechOidBytes.length;
  }
  
  public ObjectIdentifier getOid() { return this.mechOid; }
  
  public int getMechTokenLength() { return this.mechTokenLength; }
  
  public int getLength() {
    int i = this.mechOidBytes.length + this.mechTokenLength;
    return 1 + getLenFieldSize(i) + this.mechOidBytes.length;
  }
  
  public static int getMaxMechTokenSize(ObjectIdentifier paramObjectIdentifier, int paramInt) {
    int i = 0;
    try {
      DerOutputStream derOutputStream = new DerOutputStream();
      derOutputStream.putOID(paramObjectIdentifier);
      i = derOutputStream.toByteArray().length;
    } catch (IOException iOException) {}
    paramInt -= 1 + i;
    return 5;
  }
  
  private int getLenFieldSize(int paramInt) {
    byte b = 1;
    if (paramInt < 128) {
      b = 1;
    } else if (paramInt < 256) {
      b = 2;
    } else if (paramInt < 65536) {
      b = 3;
    } else if (paramInt < 16777216) {
      b = 4;
    } else {
      b = 5;
    } 
    return b;
  }
  
  public int encode(OutputStream paramOutputStream) throws IOException {
    int i = 1 + this.mechOidBytes.length;
    paramOutputStream.write(96);
    int j = this.mechOidBytes.length + this.mechTokenLength;
    i += putLength(j, paramOutputStream);
    paramOutputStream.write(this.mechOidBytes);
    return i;
  }
  
  private int getLength(InputStream paramInputStream) throws IOException { return getLength(paramInputStream.read(), paramInputStream); }
  
  private int getLength(int paramInt, InputStream paramInputStream) throws IOException {
    int i;
    int j = paramInt;
    if ((j & 0x80) == 0) {
      i = j;
    } else {
      j &= 0x7F;
      if (j == 0)
        return -1; 
      if (j < 0 || j > 4)
        throw new IOException("DerInputStream.getLength(): lengthTag=" + j + ", " + ((j < 0) ? "incorrect DER encoding." : "too big.")); 
      i = 0;
      while (j > 0) {
        i <<= 8;
        i += (0xFF & paramInputStream.read());
        j--;
      } 
      if (i < 0)
        throw new IOException("Invalid length bytes"); 
    } 
    return i;
  }
  
  private int putLength(int paramInt, OutputStream paramOutputStream) throws IOException {
    byte b = 0;
    if (paramInt < 128) {
      paramOutputStream.write((byte)paramInt);
      b = 1;
    } else if (paramInt < 256) {
      paramOutputStream.write(-127);
      paramOutputStream.write((byte)paramInt);
      b = 2;
    } else if (paramInt < 65536) {
      paramOutputStream.write(-126);
      paramOutputStream.write((byte)(paramInt >> 8));
      paramOutputStream.write((byte)paramInt);
      b = 3;
    } else if (paramInt < 16777216) {
      paramOutputStream.write(-125);
      paramOutputStream.write((byte)(paramInt >> 16));
      paramOutputStream.write((byte)(paramInt >> 8));
      paramOutputStream.write((byte)paramInt);
      b = 4;
    } else {
      paramOutputStream.write(-124);
      paramOutputStream.write((byte)(paramInt >> 24));
      paramOutputStream.write((byte)(paramInt >> 16));
      paramOutputStream.write((byte)(paramInt >> 8));
      paramOutputStream.write((byte)paramInt);
      b = 5;
    } 
    return b;
  }
  
  private void debug(String paramString) { System.err.print(paramString); }
  
  private String getHexBytes(byte[] paramArrayOfByte, int paramInt) throws IOException {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < paramInt; b++) {
      byte b1 = paramArrayOfByte[b] >> 4 & 0xF;
      byte b2 = paramArrayOfByte[b] & 0xF;
      stringBuffer.append(Integer.toHexString(b1));
      stringBuffer.append(Integer.toHexString(b2));
      stringBuffer.append(' ');
    } 
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\GSSHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */