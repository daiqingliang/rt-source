package com.sun.jmx.snmp;

import com.sun.jmx.snmp.internal.SnmpTools;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class SnmpEngineId implements Serializable {
  private static final long serialVersionUID = 5434729655830763317L;
  
  byte[] engineId = null;
  
  String hexString = null;
  
  String humanString = null;
  
  SnmpEngineId(String paramString) {
    this.engineId = SnmpTools.ascii2binary(paramString);
    this.hexString = paramString.toLowerCase();
  }
  
  SnmpEngineId(byte[] paramArrayOfByte) {
    this.engineId = paramArrayOfByte;
    this.hexString = SnmpTools.binary2ascii(paramArrayOfByte).toLowerCase();
  }
  
  public String getReadableId() { return this.humanString; }
  
  public String toString() { return this.hexString; }
  
  public byte[] getBytes() { return this.engineId; }
  
  void setStringValue(String paramString) { this.humanString = paramString; }
  
  static void validateId(String paramString) {
    byte[] arrayOfByte = SnmpTools.ascii2binary(paramString);
    validateId(arrayOfByte);
  }
  
  static void validateId(byte[] paramArrayOfByte) {
    if (paramArrayOfByte.length < 5)
      throw new IllegalArgumentException("Id size lower than 5 bytes."); 
    if (paramArrayOfByte.length > 32)
      throw new IllegalArgumentException("Id size greater than 32 bytes."); 
    if ((paramArrayOfByte[0] & 0x80) == 0 && paramArrayOfByte.length != 12)
      throw new IllegalArgumentException("Very first bit = 0 and length != 12 octets"); 
    byte[] arrayOfByte1 = new byte[paramArrayOfByte.length];
    if (Arrays.equals(arrayOfByte1, paramArrayOfByte))
      throw new IllegalArgumentException("Zeroed Id."); 
    byte[] arrayOfByte2 = new byte[paramArrayOfByte.length];
    Arrays.fill(arrayOfByte2, (byte)-1);
    if (Arrays.equals(arrayOfByte2, paramArrayOfByte))
      throw new IllegalArgumentException("0xFF Id."); 
  }
  
  public static SnmpEngineId createEngineId(byte[] paramArrayOfByte) throws IllegalArgumentException {
    if (paramArrayOfByte == null || paramArrayOfByte.length == 0)
      return null; 
    validateId(paramArrayOfByte);
    return new SnmpEngineId(paramArrayOfByte);
  }
  
  public static SnmpEngineId createEngineId() {
    Object object = null;
    byte[] arrayOfByte = new byte[13];
    byte b = 42;
    long l1 = 255L;
    long l2 = System.currentTimeMillis();
    arrayOfByte[0] = (byte)((b & 0xFF000000) >> 24);
    arrayOfByte[0] = (byte)(arrayOfByte[0] | 0x80);
    arrayOfByte[1] = (byte)((b & 0xFF0000) >> 16);
    arrayOfByte[2] = (byte)((b & 0xFF00) >> '\b');
    arrayOfByte[3] = (byte)(b & 0xFF);
    arrayOfByte[4] = 5;
    arrayOfByte[5] = (byte)(int)((l2 & l1 << 56) >>> 56);
    arrayOfByte[6] = (byte)(int)((l2 & l1 << 48) >>> 48);
    arrayOfByte[7] = (byte)(int)((l2 & l1 << 40) >>> 40);
    arrayOfByte[8] = (byte)(int)((l2 & l1 << 32) >>> 32);
    arrayOfByte[9] = (byte)(int)((l2 & l1 << 24) >>> 24);
    arrayOfByte[10] = (byte)(int)((l2 & l1 << 16) >>> 16);
    arrayOfByte[11] = (byte)(int)((l2 & l1 << 8) >>> 8);
    arrayOfByte[12] = (byte)(int)(l2 & l1);
    return new SnmpEngineId(arrayOfByte);
  }
  
  public SnmpOid toOid() {
    long[] arrayOfLong = new long[this.engineId.length + 1];
    arrayOfLong[0] = this.engineId.length;
    for (byte b = 1; b <= this.engineId.length; b++)
      arrayOfLong[b] = (this.engineId[b - true] & 0xFF); 
    return new SnmpOid(arrayOfLong);
  }
  
  public static SnmpEngineId createEngineId(String paramString) throws IllegalArgumentException, UnknownHostException { return createEngineId(paramString, null); }
  
  public static SnmpEngineId createEngineId(String paramString1, String paramString2) throws IllegalArgumentException, UnknownHostException {
    if (paramString1 == null)
      return null; 
    if (paramString1.startsWith("0x") || paramString1.startsWith("0X")) {
      validateId(paramString1);
      return new SnmpEngineId(paramString1);
    } 
    paramString2 = (paramString2 == null) ? ":" : paramString2;
    StringTokenizer stringTokenizer = new StringTokenizer(paramString1, paramString2, true);
    String str1 = null;
    String str2 = null;
    String str3 = null;
    int i = 161;
    int j = 42;
    InetAddress inetAddress = null;
    SnmpEngineId snmpEngineId = null;
    try {
      try {
        str1 = stringTokenizer.nextToken();
      } catch (NoSuchElementException noSuchElementException) {
        throw new IllegalArgumentException("Passed string is invalid : [" + paramString1 + "]");
      } 
      if (!str1.equals(paramString2)) {
        inetAddress = InetAddress.getByName(str1);
        try {
          stringTokenizer.nextToken();
        } catch (NoSuchElementException noSuchElementException) {
          snmpEngineId = createEngineId(inetAddress, i, j);
          snmpEngineId.setStringValue(paramString1);
          return snmpEngineId;
        } 
      } else {
        inetAddress = InetAddress.getLocalHost();
      } 
      try {
        str2 = stringTokenizer.nextToken();
      } catch (NoSuchElementException noSuchElementException) {
        snmpEngineId = createEngineId(inetAddress, i, j);
        snmpEngineId.setStringValue(paramString1);
        return snmpEngineId;
      } 
      if (!str2.equals(paramString2)) {
        i = Integer.parseInt(str2);
        try {
          stringTokenizer.nextToken();
        } catch (NoSuchElementException noSuchElementException) {
          snmpEngineId = createEngineId(inetAddress, i, j);
          snmpEngineId.setStringValue(paramString1);
          return snmpEngineId;
        } 
      } 
      try {
        str3 = stringTokenizer.nextToken();
      } catch (NoSuchElementException noSuchElementException) {
        snmpEngineId = createEngineId(inetAddress, i, j);
        snmpEngineId.setStringValue(paramString1);
        return snmpEngineId;
      } 
      if (!str3.equals(paramString2))
        j = Integer.parseInt(str3); 
      snmpEngineId = createEngineId(inetAddress, i, j);
      snmpEngineId.setStringValue(paramString1);
      return snmpEngineId;
    } catch (Exception exception) {
      throw new IllegalArgumentException("Passed string is invalid : [" + paramString1 + "]. Check that the used separator [" + paramString2 + "] is compatible with IPv6 address format.");
    } 
  }
  
  public static SnmpEngineId createEngineId(int paramInt) throws UnknownHostException {
    byte b = 42;
    InetAddress inetAddress = null;
    inetAddress = InetAddress.getLocalHost();
    return createEngineId(inetAddress, paramInt, b);
  }
  
  public static SnmpEngineId createEngineId(InetAddress paramInetAddress, int paramInt) throws IllegalArgumentException {
    byte b = 42;
    if (paramInetAddress == null)
      throw new IllegalArgumentException("InetAddress is null."); 
    return createEngineId(paramInetAddress, paramInt, b);
  }
  
  public static SnmpEngineId createEngineId(int paramInt1, int paramInt2) throws UnknownHostException {
    InetAddress inetAddress = null;
    inetAddress = InetAddress.getLocalHost();
    return createEngineId(inetAddress, paramInt1, paramInt2);
  }
  
  public static SnmpEngineId createEngineId(InetAddress paramInetAddress, int paramInt1, int paramInt2) {
    if (paramInetAddress == null)
      throw new IllegalArgumentException("InetAddress is null."); 
    byte[] arrayOfByte1 = paramInetAddress.getAddress();
    byte[] arrayOfByte2 = new byte[9 + arrayOfByte1.length];
    arrayOfByte2[0] = (byte)((paramInt2 & 0xFF000000) >> 24);
    arrayOfByte2[0] = (byte)(arrayOfByte2[0] | 0x80);
    arrayOfByte2[1] = (byte)((paramInt2 & 0xFF0000) >> 16);
    arrayOfByte2[2] = (byte)((paramInt2 & 0xFF00) >> 8);
    arrayOfByte2[3] = (byte)(paramInt2 & 0xFF);
    arrayOfByte2[4] = 5;
    if (arrayOfByte1.length == 4)
      arrayOfByte2[4] = 1; 
    if (arrayOfByte1.length == 16)
      arrayOfByte2[4] = 2; 
    for (byte b = 0; b < arrayOfByte1.length; b++)
      arrayOfByte2[b + 5] = arrayOfByte1[b]; 
    arrayOfByte2[5 + arrayOfByte1.length] = (byte)((paramInt1 & 0xFF000000) >> 24);
    arrayOfByte2[6 + arrayOfByte1.length] = (byte)((paramInt1 & 0xFF0000) >> 16);
    arrayOfByte2[7 + arrayOfByte1.length] = (byte)((paramInt1 & 0xFF00) >> 8);
    arrayOfByte2[8 + arrayOfByte1.length] = (byte)(paramInt1 & 0xFF);
    return new SnmpEngineId(arrayOfByte2);
  }
  
  public static SnmpEngineId createEngineId(int paramInt, InetAddress paramInetAddress) {
    if (paramInetAddress == null)
      throw new IllegalArgumentException("InetAddress is null."); 
    byte[] arrayOfByte1 = paramInetAddress.getAddress();
    byte[] arrayOfByte2 = new byte[5 + arrayOfByte1.length];
    arrayOfByte2[0] = (byte)((paramInt & 0xFF000000) >> 24);
    arrayOfByte2[0] = (byte)(arrayOfByte2[0] | 0x80);
    arrayOfByte2[1] = (byte)((paramInt & 0xFF0000) >> 16);
    arrayOfByte2[2] = (byte)((paramInt & 0xFF00) >> 8);
    arrayOfByte2[3] = (byte)(paramInt & 0xFF);
    if (arrayOfByte1.length == 4)
      arrayOfByte2[4] = 1; 
    if (arrayOfByte1.length == 16)
      arrayOfByte2[4] = 2; 
    for (byte b = 0; b < arrayOfByte1.length; b++)
      arrayOfByte2[b + 5] = arrayOfByte1[b]; 
    return new SnmpEngineId(arrayOfByte2);
  }
  
  public static SnmpEngineId createEngineId(InetAddress paramInetAddress) { return createEngineId(42, paramInetAddress); }
  
  public boolean equals(Object paramObject) { return !(paramObject instanceof SnmpEngineId) ? false : this.hexString.equals(((SnmpEngineId)paramObject).toString()); }
  
  public int hashCode() { return this.hexString.hashCode(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpEngineId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */