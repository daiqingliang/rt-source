package sun.text;

public final class UCompactIntArray implements Cloneable {
  private static final int PLANEMASK = 196608;
  
  private static final int PLANESHIFT = 16;
  
  private static final int PLANECOUNT = 16;
  
  private static final int CODEPOINTMASK = 65535;
  
  private static final int UNICODECOUNT = 65536;
  
  private static final int BLOCKSHIFT = 7;
  
  private static final int BLOCKCOUNT = 128;
  
  private static final int INDEXSHIFT = 9;
  
  private static final int INDEXCOUNT = 512;
  
  private static final int BLOCKMASK = 127;
  
  private int defaultValue;
  
  private int[][] values = new int[16][];
  
  private short[][] indices = new short[16][];
  
  private boolean isCompact;
  
  private boolean[][] blockTouched = new boolean[16][];
  
  private boolean[] planeTouched = new boolean[16];
  
  public UCompactIntArray() {}
  
  public UCompactIntArray(int paramInt) {
    this();
    this.defaultValue = paramInt;
  }
  
  public int elementAt(int paramInt) {
    int i = (paramInt & 0x30000) >> 16;
    if (!this.planeTouched[i])
      return this.defaultValue; 
    paramInt &= 0xFFFF;
    return this.values[i][(this.indices[i][paramInt >> 7] & 0xFFFF) + (paramInt & 0x7F)];
  }
  
  public void setElementAt(int paramInt1, int paramInt2) {
    if (this.isCompact)
      expand(); 
    int i = (paramInt1 & 0x30000) >> 16;
    if (!this.planeTouched[i])
      initPlane(i); 
    paramInt1 &= 0xFFFF;
    this.values[i][paramInt1] = paramInt2;
    this.blockTouched[i][paramInt1 >> 7] = true;
  }
  
  public void compact() {
    if (this.isCompact)
      return; 
    for (byte b = 0; b < 16; b++) {
      if (this.planeTouched[b]) {
        byte b1 = 0;
        byte b2 = 0;
        short s = -1;
        char c = Character.MIN_VALUE;
        while (c < this.indices[b].length) {
          this.indices[b][c] = -1;
          if (!this.blockTouched[b][c] && s != -1) {
            this.indices[b][c] = s;
          } else {
            char c1 = b1 * '';
            if (c > b1)
              System.arraycopy(this.values[b], b2, this.values[b], c1, 128); 
            if (!this.blockTouched[b][c])
              s = (short)c1; 
            this.indices[b][c] = (short)c1;
            b1++;
          } 
          c++;
          b2 += 128;
        } 
        c = b1 * '';
        int[] arrayOfInt = new int[c];
        System.arraycopy(this.values[b], 0, arrayOfInt, 0, c);
        this.values[b] = arrayOfInt;
        this.blockTouched[b] = null;
      } 
    } 
    this.isCompact = true;
  }
  
  private void expand() {
    if (this.isCompact) {
      for (byte b = 0; b < 16; b++) {
        if (this.planeTouched[b]) {
          this.blockTouched[b] = new boolean[512];
          int[] arrayOfInt = new int[65536];
          byte b1;
          for (b1 = 0; b1 < 65536; b1++) {
            arrayOfInt[b1] = this.values[b][this.indices[b][b1 >> 7] & '￿' + (b1 & 0x7F)];
            this.blockTouched[b][b1 >> 7] = true;
          } 
          for (b1 = 0; b1 < 'Ȁ'; b1++)
            this.indices[b][b1] = (short)(b1 << 7); 
          this.values[b] = arrayOfInt;
        } 
      } 
      this.isCompact = false;
    } 
  }
  
  private void initPlane(int paramInt) {
    this.values[paramInt] = new int[65536];
    this.indices[paramInt] = new short[512];
    this.blockTouched[paramInt] = new boolean[512];
    this.planeTouched[paramInt] = true;
    if (this.planeTouched[0] && paramInt != 0) {
      System.arraycopy(this.indices[0], 0, this.indices[paramInt], 0, 512);
    } else {
      for (byte b1 = 0; b1 < 'Ȁ'; b1++)
        this.indices[paramInt][b1] = (short)(b1 << 7); 
    } 
    for (byte b = 0; b < 65536; b++)
      this.values[paramInt][b] = this.defaultValue; 
  }
  
  public int getKSize() {
    int i = 0;
    for (byte b = 0; b < 16; b++) {
      if (this.planeTouched[b])
        i += this.values[b].length * 4 + this.indices[b].length * 2; 
    } 
    return i / 1024;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\text\UCompactIntArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */