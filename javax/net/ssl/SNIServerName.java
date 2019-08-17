package javax.net.ssl;

import java.util.Arrays;

public abstract class SNIServerName {
  private final int type;
  
  private final byte[] encoded;
  
  private static final char[] HEXES = "0123456789ABCDEF".toCharArray();
  
  protected SNIServerName(int paramInt, byte[] paramArrayOfByte) {
    if (paramInt < 0)
      throw new IllegalArgumentException("Server name type cannot be less than zero"); 
    if (paramInt > 255)
      throw new IllegalArgumentException("Server name type cannot be greater than 255"); 
    this.type = paramInt;
    if (paramArrayOfByte == null)
      throw new NullPointerException("Server name encoded value cannot be null"); 
    this.encoded = (byte[])paramArrayOfByte.clone();
  }
  
  public final int getType() { return this.type; }
  
  public final byte[] getEncoded() { return (byte[])this.encoded.clone(); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (getClass() != paramObject.getClass())
      return false; 
    SNIServerName sNIServerName = (SNIServerName)paramObject;
    return (this.type == sNIServerName.type && Arrays.equals(this.encoded, sNIServerName.encoded));
  }
  
  public int hashCode() {
    null = 17;
    null = 31 * null + this.type;
    return 31 * null + Arrays.hashCode(this.encoded);
  }
  
  public String toString() { return (this.type == 0) ? ("type=host_name (0), value=" + toHexString(this.encoded)) : ("type=(" + this.type + "), value=" + toHexString(this.encoded)); }
  
  private static String toHexString(byte[] paramArrayOfByte) {
    if (paramArrayOfByte.length == 0)
      return "(empty)"; 
    StringBuilder stringBuilder = new StringBuilder(paramArrayOfByte.length * 3 - 1);
    boolean bool = true;
    for (byte b1 : paramArrayOfByte) {
      if (bool) {
        bool = false;
      } else {
        stringBuilder.append(':');
      } 
      byte b2 = b1 & 0xFF;
      stringBuilder.append(HEXES[b2 >>> 4]);
      stringBuilder.append(HEXES[b2 & 0xF]);
    } 
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\ssl\SNIServerName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */