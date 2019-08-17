package com.sun.jmx.snmp;

public class BerEncoder {
  public static final int BooleanTag = 1;
  
  public static final int IntegerTag = 2;
  
  public static final int OctetStringTag = 4;
  
  public static final int NullTag = 5;
  
  public static final int OidTag = 6;
  
  public static final int SequenceTag = 48;
  
  protected final byte[] bytes;
  
  protected int start = -1;
  
  protected final int[] stackBuf = new int[200];
  
  protected int stackTop = 0;
  
  public BerEncoder(byte[] paramArrayOfByte) {
    this.bytes = paramArrayOfByte;
    this.start = paramArrayOfByte.length;
    this.stackTop = 0;
  }
  
  public int trim() {
    int i = this.bytes.length - this.start;
    if (i > 0)
      System.arraycopy(this.bytes, this.start, this.bytes, 0, i); 
    this.start = this.bytes.length;
    this.stackTop = 0;
    return i;
  }
  
  public void putInteger(int paramInt) { putInteger(paramInt, 2); }
  
  public void putInteger(int paramInt1, int paramInt2) {
    putIntegerValue(paramInt1);
    putTag(paramInt2);
  }
  
  public void putInteger(long paramLong) { putInteger(paramLong, 2); }
  
  public void putInteger(long paramLong, int paramInt) {
    putIntegerValue(paramLong);
    putTag(paramInt);
  }
  
  public void putOctetString(byte[] paramArrayOfByte) { putOctetString(paramArrayOfByte, 4); }
  
  public void putOctetString(byte[] paramArrayOfByte, int paramInt) {
    putStringValue(paramArrayOfByte);
    putTag(paramInt);
  }
  
  public void putOid(long[] paramArrayOfLong) { putOid(paramArrayOfLong, 6); }
  
  public void putOid(long[] paramArrayOfLong, int paramInt) {
    putOidValue(paramArrayOfLong);
    putTag(paramInt);
  }
  
  public void putNull() { putNull(5); }
  
  public void putNull(int paramInt) {
    putLength(0);
    putTag(paramInt);
  }
  
  public void putAny(byte[] paramArrayOfByte) { putAny(paramArrayOfByte, paramArrayOfByte.length); }
  
  public void putAny(byte[] paramArrayOfByte, int paramInt) {
    System.arraycopy(paramArrayOfByte, 0, this.bytes, this.start - paramInt, paramInt);
    this.start -= paramInt;
  }
  
  public void openSequence() { this.stackBuf[this.stackTop++] = this.start; }
  
  public void closeSequence() { closeSequence(48); }
  
  public void closeSequence(int paramInt) {
    int i = this.stackBuf[--this.stackTop];
    putLength(i - this.start);
    putTag(paramInt);
  }
  
  protected final void putTag(int paramInt) {
    if (paramInt < 256) {
      this.bytes[--this.start] = (byte)paramInt;
    } else {
      while (paramInt != 0) {
        this.bytes[--this.start] = (byte)(paramInt & 0x7F);
        paramInt <<= 7;
      } 
    } 
  }
  
  protected final void putLength(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException(); 
    if (paramInt < 128) {
      this.bytes[--this.start] = (byte)paramInt;
    } else if (paramInt < 256) {
      this.bytes[--this.start] = (byte)paramInt;
      this.bytes[--this.start] = -127;
    } else if (paramInt < 65536) {
      this.bytes[--this.start] = (byte)paramInt;
      this.bytes[--this.start] = (byte)(paramInt >> 8);
      this.bytes[--this.start] = -126;
    } else if (paramInt < 16777126) {
      this.bytes[--this.start] = (byte)paramInt;
      this.bytes[--this.start] = (byte)(paramInt >> 8);
      this.bytes[--this.start] = (byte)(paramInt >> 16);
      this.bytes[--this.start] = -125;
    } else {
      this.bytes[--this.start] = (byte)paramInt;
      this.bytes[--this.start] = (byte)(paramInt >> 8);
      this.bytes[--this.start] = (byte)(paramInt >> 16);
      this.bytes[--this.start] = (byte)(paramInt >> 24);
      this.bytes[--this.start] = -124;
    } 
  }
  
  protected final void putIntegerValue(int paramInt) {
    int i = this.start;
    int j = 2139095040;
    byte b1 = 4;
    if (paramInt < 0) {
      while ((j & paramInt) == j && b1 > 1) {
        j >>= 8;
        b1--;
      } 
    } else {
      while ((j & paramInt) == 0 && b1 > 1) {
        j >>= 8;
        b1--;
      } 
    } 
    for (byte b2 = 0; b2 < b1; b2++) {
      this.bytes[--this.start] = (byte)paramInt;
      paramInt >>= 8;
    } 
    putLength(i - this.start);
  }
  
  protected final void putIntegerValue(long paramLong) {
    int i = this.start;
    long l = 9187343239835811840L;
    byte b1 = 8;
    if (paramLong < 0L) {
      while ((l & paramLong) == l && b1 > 1) {
        l >>= 8;
        b1--;
      } 
    } else {
      while ((l & paramLong) == 0L && b1 > 1) {
        l >>= 8;
        b1--;
      } 
    } 
    for (byte b2 = 0; b2 < b1; b2++) {
      this.bytes[--this.start] = (byte)(int)paramLong;
      paramLong >>= 8;
    } 
    putLength(i - this.start);
  }
  
  protected final void putStringValue(byte[] paramArrayOfByte) {
    int i = paramArrayOfByte.length;
    System.arraycopy(paramArrayOfByte, 0, this.bytes, this.start - i, i);
    this.start -= i;
    putLength(i);
  }
  
  protected final void putOidValue(long[] paramArrayOfLong) {
    int i = this.start;
    int j = paramArrayOfLong.length;
    if (j < 2 || paramArrayOfLong[0] > 2L || paramArrayOfLong[1] >= 40L)
      throw new IllegalArgumentException(); 
    for (int k = j - 1; k >= 2; k--) {
      long l = paramArrayOfLong[k];
      if (l < 0L)
        throw new IllegalArgumentException(); 
      if (l < 128L) {
        this.bytes[--this.start] = (byte)(int)l;
      } else {
        this.bytes[--this.start] = (byte)(int)(l & 0x7FL);
        for (l >>= 7; l != 0L; l >>= 7)
          this.bytes[--this.start] = (byte)(int)(l | 0x80L); 
      } 
    } 
    this.bytes[--this.start] = (byte)(int)(paramArrayOfLong[0] * 40L + paramArrayOfLong[1]);
    putLength(i - this.start);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\BerEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */