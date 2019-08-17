package com.sun.jmx.snmp;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class SnmpOid extends SnmpValue {
  protected long[] components = null;
  
  protected int componentCount = 0;
  
  static final String name = "Object Identifier";
  
  private static SnmpOidTable meta = null;
  
  static final long serialVersionUID = 8956237235607885096L;
  
  public SnmpOid() {
    this.components = new long[15];
    this.componentCount = 0;
  }
  
  public SnmpOid(long[] paramArrayOfLong) {
    this.components = (long[])paramArrayOfLong.clone();
    this.componentCount = this.components.length;
  }
  
  public SnmpOid(long paramLong) {
    this.components = new long[1];
    this.components[0] = paramLong;
    this.componentCount = this.components.length;
  }
  
  public SnmpOid(long paramLong1, long paramLong2, long paramLong3, long paramLong4) {
    this.components = new long[4];
    this.components[0] = paramLong1;
    this.components[1] = paramLong2;
    this.components[2] = paramLong3;
    this.components[3] = paramLong4;
    this.componentCount = this.components.length;
  }
  
  public SnmpOid(String paramString) throws IllegalArgumentException {
    String str = paramString;
    if (!paramString.startsWith("."))
      try {
        str = resolveVarName(paramString);
      } catch (SnmpStatusException snmpStatusException) {
        throw new IllegalArgumentException(snmpStatusException.getMessage());
      }  
    StringTokenizer stringTokenizer = new StringTokenizer(str, ".", false);
    this.componentCount = stringTokenizer.countTokens();
    if (this.componentCount == 0) {
      this.components = new long[15];
    } else {
      this.components = new long[this.componentCount];
      try {
        for (byte b = 0; b < this.componentCount; b++) {
          try {
            this.components[b] = Long.parseLong(stringTokenizer.nextToken());
          } catch (NoSuchElementException noSuchElementException) {}
        } 
      } catch (NumberFormatException numberFormatException) {
        throw new IllegalArgumentException(paramString);
      } 
    } 
  }
  
  public int getLength() { return this.componentCount; }
  
  public long[] longValue() {
    long[] arrayOfLong = new long[this.componentCount];
    System.arraycopy(this.components, 0, arrayOfLong, 0, this.componentCount);
    return arrayOfLong;
  }
  
  public final long[] longValue(boolean paramBoolean) { return longValue(); }
  
  public final long getOidArc(int paramInt) throws SnmpStatusException {
    try {
      return this.components[paramInt];
    } catch (Exception exception) {
      throw new SnmpStatusException(6);
    } 
  }
  
  public Long toLong() {
    if (this.componentCount != 1)
      throw new IllegalArgumentException(); 
    return new Long(this.components[0]);
  }
  
  public Integer toInteger() {
    if (this.componentCount != 1 || this.components[0] > 2147483647L)
      throw new IllegalArgumentException(); 
    return new Integer((int)this.components[0]);
  }
  
  public String toString() {
    String str = "";
    if (this.componentCount >= 1) {
      for (byte b = 0; b < this.componentCount - 1; b++)
        str = str + this.components[b] + "."; 
      str = str + this.components[this.componentCount - 1];
    } 
    return str;
  }
  
  public Boolean toBoolean() {
    if (this.componentCount != 1 && this.components[0] != 1L && this.components[0] != 2L)
      throw new IllegalArgumentException(); 
    return Boolean.valueOf((this.components[0] == 1L));
  }
  
  public Byte[] toByte() {
    Byte[] arrayOfByte = new Byte[this.componentCount];
    for (byte b = 0; b < this.componentCount; b++) {
      if (this.components[0] > 255L)
        throw new IllegalArgumentException(); 
      arrayOfByte[b] = new Byte((byte)(int)this.components[b]);
    } 
    return arrayOfByte;
  }
  
  public SnmpOid toOid() {
    long[] arrayOfLong = new long[this.componentCount];
    for (byte b = 0; b < this.componentCount; b++)
      arrayOfLong[b] = this.components[b]; 
    return new SnmpOid(arrayOfLong);
  }
  
  public static SnmpOid toOid(long[] paramArrayOfLong, int paramInt) throws SnmpStatusException {
    try {
      if (paramArrayOfLong[paramInt] > 2147483647L)
        throw new SnmpStatusException(2); 
      int i = (int)paramArrayOfLong[paramInt++];
      long[] arrayOfLong = new long[i];
      for (int j = 0; j < i; j++)
        arrayOfLong[j] = paramArrayOfLong[paramInt + j]; 
      return new SnmpOid(arrayOfLong);
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
      throw new SnmpStatusException(2);
    } 
  }
  
  public static int nextOid(long[] paramArrayOfLong, int paramInt) throws SnmpStatusException {
    try {
      if (paramArrayOfLong[paramInt] > 2147483647L)
        throw new SnmpStatusException(2); 
      int i = (int)paramArrayOfLong[paramInt++];
      paramInt += i;
      if (paramInt <= paramArrayOfLong.length)
        return paramInt; 
      throw new SnmpStatusException(2);
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
      throw new SnmpStatusException(2);
    } 
  }
  
  public static void appendToOid(SnmpOid paramSnmpOid1, SnmpOid paramSnmpOid2) {
    paramSnmpOid2.append(paramSnmpOid1.getLength());
    paramSnmpOid2.append(paramSnmpOid1);
  }
  
  public final SnmpValue duplicate() { return (SnmpValue)clone(); }
  
  public Object clone() {
    try {
      SnmpOid snmpOid = (SnmpOid)super.clone();
      snmpOid.components = new long[this.componentCount];
      System.arraycopy(this.components, 0, snmpOid.components, 0, this.componentCount);
      return snmpOid;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError();
    } 
  }
  
  public void insert(long paramLong) {
    enlargeIfNeeded(1);
    for (int i = this.componentCount - 1; i >= 0; i--)
      this.components[i + 1] = this.components[i]; 
    this.components[0] = paramLong;
    this.componentCount++;
  }
  
  public void insert(int paramInt) { insert(paramInt); }
  
  public void append(SnmpOid paramSnmpOid) {
    enlargeIfNeeded(paramSnmpOid.componentCount);
    for (int i = 0; i < paramSnmpOid.componentCount; i++)
      this.components[this.componentCount + i] = paramSnmpOid.components[i]; 
    this.componentCount += paramSnmpOid.componentCount;
  }
  
  public void append(long paramLong) {
    enlargeIfNeeded(1);
    this.components[this.componentCount] = paramLong;
    this.componentCount++;
  }
  
  public void addToOid(String paramString) throws IllegalArgumentException {
    SnmpOid snmpOid = new SnmpOid(paramString);
    append(snmpOid);
  }
  
  public void addToOid(long[] paramArrayOfLong) {
    SnmpOid snmpOid = new SnmpOid(paramArrayOfLong);
    append(snmpOid);
  }
  
  public boolean isValid() { return (this.componentCount >= 2 && 0L <= this.components[0] && this.components[0] < 3L && 0L <= this.components[1] && this.components[1] < 40L); }
  
  public boolean equals(Object paramObject) {
    boolean bool = false;
    if (paramObject instanceof SnmpOid) {
      SnmpOid snmpOid = (SnmpOid)paramObject;
      if (snmpOid.componentCount == this.componentCount) {
        byte b = 0;
        long[] arrayOfLong = snmpOid.components;
        while (b < this.componentCount && this.components[b] == arrayOfLong[b])
          b++; 
        bool = (b == this.componentCount);
      } 
    } 
    return bool;
  }
  
  public int hashCode() {
    long l = 0L;
    for (byte b = 0; b < this.componentCount; b++)
      l = l * 31L + this.components[b]; 
    return (int)l;
  }
  
  public int compareTo(SnmpOid paramSnmpOid) {
    byte b = 0;
    byte b1 = 0;
    int i = Math.min(this.componentCount, paramSnmpOid.componentCount);
    long[] arrayOfLong = paramSnmpOid.components;
    for (b1 = 0; b1 < i && this.components[b1] == arrayOfLong[b1]; b1++);
    if (b1 == this.componentCount && b1 == paramSnmpOid.componentCount) {
      b = 0;
    } else if (b1 == this.componentCount) {
      b = -1;
    } else if (b1 == paramSnmpOid.componentCount) {
      b = 1;
    } else {
      b = (this.components[b1] < arrayOfLong[b1]) ? -1 : 1;
    } 
    return b;
  }
  
  public String resolveVarName(String paramString) throws SnmpStatusException {
    int i = paramString.indexOf('.');
    try {
      return handleLong(paramString, i);
    } catch (NumberFormatException numberFormatException) {
      SnmpOidTable snmpOidTable = getSnmpOidTable();
      if (snmpOidTable == null)
        throw new SnmpStatusException(2); 
      if (i <= 0) {
        SnmpOidRecord snmpOidRecord1 = snmpOidTable.resolveVarName(paramString);
        return snmpOidRecord1.getOid();
      } 
      SnmpOidRecord snmpOidRecord = snmpOidTable.resolveVarName(paramString.substring(0, i));
      return snmpOidRecord.getOid() + paramString.substring(i);
    } 
  }
  
  public String getTypeName() { return "Object Identifier"; }
  
  public static SnmpOidTable getSnmpOidTable() { return meta; }
  
  public static void setSnmpOidTable(SnmpOidTable paramSnmpOidTable) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new SnmpPermission("setSnmpOidTable")); 
    meta = paramSnmpOidTable;
  }
  
  public String toOctetString() { return new String(tobyte()); }
  
  private byte[] tobyte() {
    byte[] arrayOfByte = new byte[this.componentCount];
    for (byte b = 0; b < this.componentCount; b++) {
      if (this.components[0] > 255L)
        throw new IllegalArgumentException(); 
      arrayOfByte[b] = (byte)(int)this.components[b];
    } 
    return arrayOfByte;
  }
  
  private void enlargeIfNeeded(int paramInt) {
    int i;
    for (i = this.components.length; this.componentCount + paramInt > i; i *= 2);
    if (i > this.components.length) {
      long[] arrayOfLong = new long[i];
      for (byte b = 0; b < this.components.length; b++)
        arrayOfLong[b] = this.components[b]; 
      this.components = arrayOfLong;
    } 
  }
  
  private String handleLong(String paramString, int paramInt) throws NumberFormatException, SnmpStatusException {
    String str;
    if (paramInt > 0) {
      str = paramString.substring(0, paramInt);
    } else {
      str = paramString;
    } 
    Long.parseLong(str);
    return paramString;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpOid.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */