package sun.text;

public final class CompactByteArray implements Cloneable {
  public static final int UNICODECOUNT = 65536;
  
  private static final int BLOCKSHIFT = 7;
  
  private static final int BLOCKCOUNT = 128;
  
  private static final int INDEXSHIFT = 9;
  
  private static final int INDEXCOUNT = 512;
  
  private static final int BLOCKMASK = 127;
  
  private byte[] values;
  
  private short[] indices;
  
  private boolean isCompact;
  
  private int[] hashes;
  
  public CompactByteArray(byte paramByte) {
    this.values = new byte[65536];
    this.indices = new short[512];
    this.hashes = new int[512];
    byte b;
    for (b = 0; b < 65536; b++)
      this.values[b] = paramByte; 
    for (b = 0; b < 'Ȁ'; b++) {
      this.indices[b] = (short)(b << 7);
      this.hashes[b] = 0;
    } 
    this.isCompact = false;
  }
  
  public CompactByteArray(short[] paramArrayOfShort, byte[] paramArrayOfByte) {
    if (paramArrayOfShort.length != 512)
      throw new IllegalArgumentException("Index out of bounds!"); 
    for (byte b = 0; b < 'Ȁ'; b++) {
      short s = paramArrayOfShort[b];
      if (s < 0 || s >= paramArrayOfByte.length + 128)
        throw new IllegalArgumentException("Index out of bounds!"); 
    } 
    this.indices = paramArrayOfShort;
    this.values = paramArrayOfByte;
    this.isCompact = true;
  }
  
  public byte elementAt(char paramChar) { return this.values[(this.indices[paramChar >> '\007'] & 0xFFFF) + (paramChar & 0x7F)]; }
  
  public void setElementAt(char paramChar, byte paramByte) {
    if (this.isCompact)
      expand(); 
    this.values[paramChar] = paramByte;
    touchBlock(paramChar >> '\007', paramByte);
  }
  
  public void setElementAt(char paramChar1, char paramChar2, byte paramByte) {
    if (this.isCompact)
      expand(); 
    for (char c = paramChar1; c <= paramChar2; c++) {
      this.values[c] = paramByte;
      touchBlock(c >> '\007', paramByte);
    } 
  }
  
  public void compact() {
    if (!this.isCompact) {
      byte b1 = 0;
      byte b2 = 0;
      short s = -1;
      char c = Character.MIN_VALUE;
      while (c < this.indices.length) {
        this.indices[c] = -1;
        boolean bool = blockTouched(c);
        if (!bool && s != -1) {
          this.indices[c] = s;
        } else {
          byte b3 = 0;
          byte b4 = 0;
          b4 = 0;
          while (b4 < b1) {
            if (this.hashes[c] == this.hashes[b4] && arrayRegionMatches(this.values, b2, this.values, b3, 128)) {
              this.indices[c] = (short)b3;
              break;
            } 
            b4++;
            b3 += 128;
          } 
          if (this.indices[c] == -1) {
            System.arraycopy(this.values, b2, this.values, b3, 128);
            this.indices[c] = (short)b3;
            this.hashes[b4] = this.hashes[c];
            b1++;
            if (!bool)
              s = (short)b3; 
          } 
        } 
        c++;
        b2 += 128;
      } 
      c = b1 * '';
      byte[] arrayOfByte = new byte[c];
      System.arraycopy(this.values, 0, arrayOfByte, 0, c);
      this.values = arrayOfByte;
      this.isCompact = true;
      this.hashes = null;
    } 
  }
  
  static final boolean arrayRegionMatches(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2, int paramInt3) {
    int i = paramInt1 + paramInt3;
    int j = paramInt2 - paramInt1;
    for (int k = paramInt1; k < i; k++) {
      if (paramArrayOfByte1[k] != paramArrayOfByte2[k + j])
        return false; 
    } 
    return true;
  }
  
  private final void touchBlock(int paramInt1, int paramInt2) { this.hashes[paramInt1] = this.hashes[paramInt1] + (paramInt2 << 1) | true; }
  
  private final boolean blockTouched(int paramInt) { return (this.hashes[paramInt] != 0); }
  
  public short[] getIndexArray() { return this.indices; }
  
  public byte[] getStringArray() { return this.values; }
  
  public Object clone() {
    try {
      CompactByteArray compactByteArray = (CompactByteArray)super.clone();
      compactByteArray.values = (byte[])this.values.clone();
      compactByteArray.indices = (short[])this.indices.clone();
      if (this.hashes != null)
        compactByteArray.hashes = (int[])this.hashes.clone(); 
      return compactByteArray;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (this == paramObject)
      return true; 
    if (getClass() != paramObject.getClass())
      return false; 
    CompactByteArray compactByteArray = (CompactByteArray)paramObject;
    for (byte b = 0; b < 65536; b++) {
      if (elementAt((char)b) != compactByteArray.elementAt((char)b))
        return false; 
    } 
    return true;
  }
  
  public int hashCode() {
    byte b = 0;
    int i = Math.min(3, this.values.length / 16);
    for (int j = 0; j < this.values.length; j += i)
      b = b * 37 + this.values[j]; 
    return b;
  }
  
  private void expand() {
    if (this.isCompact) {
      this.hashes = new int[512];
      byte[] arrayOfByte = new byte[65536];
      byte b;
      for (b = 0; b < 65536; b++) {
        byte b1 = elementAt((char)b);
        arrayOfByte[b] = b1;
        touchBlock(b >> 7, b1);
      } 
      for (b = 0; b < 'Ȁ'; b++)
        this.indices[b] = (short)(b << 7); 
      this.values = null;
      this.values = arrayOfByte;
      this.isCompact = false;
    } 
  }
  
  private byte[] getArray() { return this.values; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\text\CompactByteArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */