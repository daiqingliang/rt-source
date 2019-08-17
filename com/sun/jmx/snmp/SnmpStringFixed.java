package com.sun.jmx.snmp;

public class SnmpStringFixed extends SnmpString {
  private static final long serialVersionUID = -9120939046874646063L;
  
  public SnmpStringFixed(byte[] paramArrayOfByte) { super(paramArrayOfByte); }
  
  public SnmpStringFixed(Byte[] paramArrayOfByte) { super(paramArrayOfByte); }
  
  public SnmpStringFixed(String paramString) { super(paramString); }
  
  public SnmpStringFixed(int paramInt, byte[] paramArrayOfByte) throws IllegalArgumentException {
    if (paramInt <= 0 || paramArrayOfByte == null)
      throw new IllegalArgumentException(); 
    int i = Math.min(paramInt, paramArrayOfByte.length);
    this.value = new byte[paramInt];
    int j;
    for (j = 0; j < i; j++)
      this.value[j] = paramArrayOfByte[j]; 
    for (j = i; j < paramInt; j++)
      this.value[j] = 0; 
  }
  
  public SnmpStringFixed(int paramInt, Byte[] paramArrayOfByte) throws IllegalArgumentException {
    if (paramInt <= 0 || paramArrayOfByte == null)
      throw new IllegalArgumentException(); 
    int i = Math.min(paramInt, paramArrayOfByte.length);
    this.value = new byte[paramInt];
    int j;
    for (j = 0; j < i; j++)
      this.value[j] = paramArrayOfByte[j].byteValue(); 
    for (j = i; j < paramInt; j++)
      this.value[j] = 0; 
  }
  
  public SnmpStringFixed(int paramInt, String paramString) throws IllegalArgumentException {
    if (paramInt <= 0 || paramString == null)
      throw new IllegalArgumentException(); 
    byte[] arrayOfByte = paramString.getBytes();
    int i = Math.min(paramInt, arrayOfByte.length);
    this.value = new byte[paramInt];
    int j;
    for (j = 0; j < i; j++)
      this.value[j] = arrayOfByte[j]; 
    for (j = i; j < paramInt; j++)
      this.value[j] = 0; 
  }
  
  public static SnmpOid toOid(int paramInt1, long[] paramArrayOfLong, int paramInt2) throws SnmpStatusException {
    try {
      long[] arrayOfLong = new long[paramInt1];
      for (int i = 0; i < paramInt1; i++)
        arrayOfLong[i] = paramArrayOfLong[paramInt2 + i]; 
      return new SnmpOid(arrayOfLong);
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
      throw new SnmpStatusException(2);
    } 
  }
  
  public static int nextOid(int paramInt1, long[] paramArrayOfLong, int paramInt2) throws SnmpStatusException {
    int i = paramInt2 + paramInt1;
    if (i > paramArrayOfLong.length)
      throw new SnmpStatusException(2); 
    return i;
  }
  
  public static void appendToOid(int paramInt, SnmpOid paramSnmpOid1, SnmpOid paramSnmpOid2) { paramSnmpOid2.append(paramSnmpOid1); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpStringFixed.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */