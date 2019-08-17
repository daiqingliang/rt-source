package com.sun.jmx.snmp.internal;

import com.sun.jmx.snmp.SnmpDefinitions;

public class SnmpTools implements SnmpDefinitions {
  public static String binary2ascii(byte[] paramArrayOfByte, int paramInt) {
    if (paramArrayOfByte == null)
      return null; 
    int i = paramInt * 2 + 2;
    byte[] arrayOfByte = new byte[i];
    arrayOfByte[0] = 48;
    arrayOfByte[1] = 120;
    for (byte b = 0; b < paramInt; b++) {
      byte b1 = b * 2;
      byte b2 = paramArrayOfByte[b] & 0xF0;
      b2 >>= 4;
      if (b2 < 10) {
        arrayOfByte[b1 + 2] = (byte)(48 + b2);
      } else {
        arrayOfByte[b1 + 2] = (byte)(65 + b2 - 10);
      } 
      b2 = paramArrayOfByte[b] & 0xF;
      if (b2 < 10) {
        arrayOfByte[b1 + 1 + 2] = (byte)(48 + b2);
      } else {
        arrayOfByte[b1 + 1 + 2] = (byte)(65 + b2 - 10);
      } 
    } 
    return new String(arrayOfByte);
  }
  
  public static String binary2ascii(byte[] paramArrayOfByte) { return binary2ascii(paramArrayOfByte, paramArrayOfByte.length); }
  
  public static byte[] ascii2binary(String paramString) {
    if (paramString == null)
      return null; 
    String str = paramString.substring(2);
    int i = str.length();
    byte[] arrayOfByte1 = new byte[i / 2];
    byte[] arrayOfByte2 = str.getBytes();
    for (byte b = 0; b < i / 2; b++) {
      byte b1 = b * 2;
      byte b2 = 0;
      if (arrayOfByte2[b1] >= 48 && arrayOfByte2[b1] <= 57) {
        b2 = (byte)(arrayOfByte2[b1] - 48 << 4);
      } else if (arrayOfByte2[b1] >= 97 && arrayOfByte2[b1] <= 102) {
        b2 = (byte)(arrayOfByte2[b1] - 97 + 10 << 4);
      } else if (arrayOfByte2[b1] >= 65 && arrayOfByte2[b1] <= 70) {
        b2 = (byte)(arrayOfByte2[b1] - 65 + 10 << 4);
      } else {
        throw new Error("BAD format :" + paramString);
      } 
      if (arrayOfByte2[b1 + 1] >= 48 && arrayOfByte2[b1 + 1] <= 57) {
        b2 = (byte)(b2 + arrayOfByte2[b1 + 1] - 48);
      } else if (arrayOfByte2[b1 + 1] >= 97 && arrayOfByte2[b1 + 1] <= 102) {
        b2 = (byte)(b2 + arrayOfByte2[b1 + 1] - 97 + 10);
      } else if (arrayOfByte2[b1 + 1] >= 65 && arrayOfByte2[b1 + 1] <= 70) {
        b2 = (byte)(b2 + arrayOfByte2[b1 + 1] - 65 + 10);
      } else {
        throw new Error("BAD format :" + paramString);
      } 
      arrayOfByte1[b] = b2;
    } 
    return arrayOfByte1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\internal\SnmpTools.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */