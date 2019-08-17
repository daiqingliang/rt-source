package com.sun.jmx.snmp;

public class SnmpIpAddress extends SnmpOid {
  private static final long serialVersionUID = 7204629998270874474L;
  
  static final String name = "IpAddress";
  
  public SnmpIpAddress(byte[] paramArrayOfByte) throws IllegalArgumentException { buildFromByteArray(paramArrayOfByte); }
  
  public SnmpIpAddress(long paramLong) {
    int i = (int)paramLong;
    byte[] arrayOfByte = new byte[4];
    arrayOfByte[0] = (byte)(i >>> 24 & 0xFF);
    arrayOfByte[1] = (byte)(i >>> 16 & 0xFF);
    arrayOfByte[2] = (byte)(i >>> 8 & 0xFF);
    arrayOfByte[3] = (byte)(i & 0xFF);
    buildFromByteArray(arrayOfByte);
  }
  
  public SnmpIpAddress(String paramString) throws IllegalArgumentException {
    super(paramString);
    if (this.componentCount > 4 || this.components[0] > 255L || this.components[1] > 255L || this.components[2] > 255L || this.components[3] > 255L)
      throw new IllegalArgumentException(paramString); 
  }
  
  public SnmpIpAddress(long paramLong1, long paramLong2, long paramLong3, long paramLong4) {
    super(paramLong1, paramLong2, paramLong3, paramLong4);
    if (this.components[0] > 255L || this.components[1] > 255L || this.components[2] > 255L || this.components[3] > 255L)
      throw new IllegalArgumentException(); 
  }
  
  public byte[] byteValue() {
    byte[] arrayOfByte = new byte[4];
    arrayOfByte[0] = (byte)(int)this.components[0];
    arrayOfByte[1] = (byte)(int)this.components[1];
    arrayOfByte[2] = (byte)(int)this.components[2];
    arrayOfByte[3] = (byte)(int)this.components[3];
    return arrayOfByte;
  }
  
  public String stringValue() { return toString(); }
  
  public static SnmpOid toOid(long[] paramArrayOfLong, int paramInt) throws SnmpStatusException {
    if (paramInt + 4 <= paramArrayOfLong.length)
      try {
        return new SnmpOid(paramArrayOfLong[paramInt], paramArrayOfLong[paramInt + 1], paramArrayOfLong[paramInt + 2], paramArrayOfLong[paramInt + 3]);
      } catch (IllegalArgumentException illegalArgumentException) {
        throw new SnmpStatusException(2);
      }  
    throw new SnmpStatusException(2);
  }
  
  public static int nextOid(long[] paramArrayOfLong, int paramInt) throws SnmpStatusException {
    if (paramInt + 4 <= paramArrayOfLong.length)
      return paramInt + 4; 
    throw new SnmpStatusException(2);
  }
  
  public static void appendToOid(SnmpOid paramSnmpOid1, SnmpOid paramSnmpOid2) {
    if (paramSnmpOid1.getLength() != 4)
      throw new IllegalArgumentException(); 
    paramSnmpOid2.append(paramSnmpOid1);
  }
  
  public final String getTypeName() { return "IpAddress"; }
  
  private void buildFromByteArray(byte[] paramArrayOfByte) throws IllegalArgumentException {
    if (paramArrayOfByte.length != 4)
      throw new IllegalArgumentException(); 
    this.components = new long[4];
    this.componentCount = 4;
    this.components[0] = (paramArrayOfByte[0] >= 0) ? paramArrayOfByte[0] : (paramArrayOfByte[0] + 256);
    this.components[1] = (paramArrayOfByte[1] >= 0) ? paramArrayOfByte[1] : (paramArrayOfByte[1] + 256);
    this.components[2] = (paramArrayOfByte[2] >= 0) ? paramArrayOfByte[2] : (paramArrayOfByte[2] + 256);
    this.components[3] = (paramArrayOfByte[3] >= 0) ? paramArrayOfByte[3] : (paramArrayOfByte[3] + 256);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpIpAddress.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */