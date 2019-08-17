package sun.security.util;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class BitArray {
  private byte[] repn;
  
  private int length;
  
  private static final int BITS_PER_UNIT = 8;
  
  private static final byte[][] NYBBLE = { 
      { 48, 48, 48, 48 }, { 48, 48, 48, 49 }, { 48, 48, 49, 48 }, { 48, 48, 49, 49 }, { 48, 49, 48, 48 }, { 48, 49, 48, 49 }, { 48, 49, 49, 48 }, { 48, 49, 49, 49 }, { 49, 48, 48, 48 }, { 49, 48, 48, 49 }, 
      { 49, 48, 49, 48 }, { 49, 48, 49, 49 }, { 49, 49, 48, 48 }, { 49, 49, 48, 49 }, { 49, 49, 49, 48 }, { 49, 49, 49, 49 } };
  
  private static final int BYTES_PER_LINE = 8;
  
  private static int subscript(int paramInt) { return paramInt / 8; }
  
  private static int position(int paramInt) { return 1 << 7 - paramInt % 8; }
  
  public BitArray(int paramInt) throws IllegalArgumentException {
    if (paramInt < 0)
      throw new IllegalArgumentException("Negative length for BitArray"); 
    this.length = paramInt;
    this.repn = new byte[(paramInt + 8 - 1) / 8];
  }
  
  public BitArray(int paramInt, byte[] paramArrayOfByte) throws IllegalArgumentException {
    if (paramInt < 0)
      throw new IllegalArgumentException("Negative length for BitArray"); 
    if (paramArrayOfByte.length * 8 < paramInt)
      throw new IllegalArgumentException("Byte array too short to represent bit array of given length"); 
    this.length = paramInt;
    int i = (paramInt + 8 - 1) / 8;
    int j = i * 8 - paramInt;
    byte b = (byte)(255 << j);
    this.repn = new byte[i];
    System.arraycopy(paramArrayOfByte, 0, this.repn, 0, i);
    if (i > 0)
      this.repn[i - 1] = (byte)(this.repn[i - 1] & b); 
  }
  
  public BitArray(boolean[] paramArrayOfBoolean) {
    this.length = paramArrayOfBoolean.length;
    this.repn = new byte[(this.length + 7) / 8];
    for (byte b = 0; b < this.length; b++)
      set(b, paramArrayOfBoolean[b]); 
  }
  
  private BitArray(BitArray paramBitArray) {
    this.length = paramBitArray.length;
    this.repn = (byte[])paramBitArray.repn.clone();
  }
  
  public boolean get(int paramInt) throws ArrayIndexOutOfBoundsException {
    if (paramInt < 0 || paramInt >= this.length)
      throw new ArrayIndexOutOfBoundsException(Integer.toString(paramInt)); 
    return ((this.repn[subscript(paramInt)] & position(paramInt)) != 0);
  }
  
  public void set(int paramInt, boolean paramBoolean) throws ArrayIndexOutOfBoundsException {
    if (paramInt < 0 || paramInt >= this.length)
      throw new ArrayIndexOutOfBoundsException(Integer.toString(paramInt)); 
    int i = subscript(paramInt);
    int j = position(paramInt);
    if (paramBoolean) {
      this.repn[i] = (byte)(this.repn[i] | j);
    } else {
      this.repn[i] = (byte)(this.repn[i] & (j ^ 0xFFFFFFFF));
    } 
  }
  
  public int length() { return this.length; }
  
  public byte[] toByteArray() { return (byte[])this.repn.clone(); }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (paramObject == null || !(paramObject instanceof BitArray))
      return false; 
    BitArray bitArray = (BitArray)paramObject;
    if (bitArray.length != this.length)
      return false; 
    for (byte b = 0; b < this.repn.length; b++) {
      if (this.repn[b] != bitArray.repn[b])
        return false; 
    } 
    return true;
  }
  
  public boolean[] toBooleanArray() {
    boolean[] arrayOfBoolean = new boolean[this.length];
    for (byte b = 0; b < this.length; b++)
      arrayOfBoolean[b] = get(b); 
    return arrayOfBoolean;
  }
  
  public int hashCode() {
    int i = 0;
    for (byte b = 0; b < this.repn.length; b++)
      i = 31 * i + this.repn[b]; 
    return i ^ this.length;
  }
  
  public Object clone() { return new BitArray(this); }
  
  public String toString() {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    int i;
    for (i = 0; i < this.repn.length - 1; i++) {
      byteArrayOutputStream.write(NYBBLE[this.repn[i] >> 4 & 0xF], 0, 4);
      byteArrayOutputStream.write(NYBBLE[this.repn[i] & 0xF], 0, 4);
      if (i % 8 == 7) {
        byteArrayOutputStream.write(10);
      } else {
        byteArrayOutputStream.write(32);
      } 
    } 
    for (i = 8 * (this.repn.length - 1); i < this.length; i++)
      byteArrayOutputStream.write(get(i) ? 49 : 48); 
    return new String(byteArrayOutputStream.toByteArray());
  }
  
  public BitArray truncate() {
    for (int i = this.length - 1; i >= 0; i--) {
      if (get(i))
        return new BitArray(i + 1, Arrays.copyOf(this.repn, (i + 8) / 8)); 
    } 
    return new BitArray(1);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\securit\\util\BitArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */