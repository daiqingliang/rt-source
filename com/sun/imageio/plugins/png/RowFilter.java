package com.sun.imageio.plugins.png;

public class RowFilter {
  private static final int abs(int paramInt) { return (paramInt < 0) ? -paramInt : paramInt; }
  
  protected static int subFilter(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2) {
    int i = 0;
    for (int j = paramInt1; j < paramInt2 + paramInt1; j++) {
      byte b1 = paramArrayOfByte1[j] & 0xFF;
      byte b2 = paramArrayOfByte1[j - paramInt1] & 0xFF;
      byte b3 = b1 - b2;
      paramArrayOfByte2[j] = (byte)b3;
      i += abs(b3);
    } 
    return i;
  }
  
  protected static int upFilter(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt1, int paramInt2) {
    int i = 0;
    for (int j = paramInt1; j < paramInt2 + paramInt1; j++) {
      byte b1 = paramArrayOfByte1[j] & 0xFF;
      byte b2 = paramArrayOfByte2[j] & 0xFF;
      byte b3 = b1 - b2;
      paramArrayOfByte3[j] = (byte)b3;
      i += abs(b3);
    } 
    return i;
  }
  
  protected final int paethPredictor(int paramInt1, int paramInt2, int paramInt3) {
    int i = paramInt1 + paramInt2 - paramInt3;
    int j = abs(i - paramInt1);
    int k = abs(i - paramInt2);
    int m = abs(i - paramInt3);
    return (j <= k && j <= m) ? paramInt1 : ((k <= m) ? paramInt2 : paramInt3);
  }
  
  public int filterRow(int paramInt1, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[][] paramArrayOfByte, int paramInt2, int paramInt3) {
    if (paramInt1 != 3) {
      System.arraycopy(paramArrayOfByte1, paramInt3, paramArrayOfByte[0], paramInt3, paramInt2);
      return 0;
    } 
    int[] arrayOfInt = new int[5];
    byte b;
    for (b = 0; b < 5; b++)
      arrayOfInt[b] = Integer.MAX_VALUE; 
    b = 0;
    int j;
    for (j = paramInt3; j < paramInt2 + paramInt3; j++) {
      byte b1 = paramArrayOfByte1[j] & 0xFF;
      b += b1;
    } 
    arrayOfInt[0] = b;
    byte[] arrayOfByte = paramArrayOfByte[1];
    j = subFilter(paramArrayOfByte1, arrayOfByte, paramInt3, paramInt2);
    arrayOfInt[1] = j;
    arrayOfByte = paramArrayOfByte[2];
    j = upFilter(paramArrayOfByte1, paramArrayOfByte2, arrayOfByte, paramInt3, paramInt2);
    arrayOfInt[2] = j;
    arrayOfByte = paramArrayOfByte[3];
    j = 0;
    int k;
    for (k = paramInt3; k < paramInt2 + paramInt3; k++) {
      byte b1 = paramArrayOfByte1[k] & 0xFF;
      byte b2 = paramArrayOfByte1[k - paramInt3] & 0xFF;
      byte b3 = paramArrayOfByte2[k] & 0xFF;
      byte b4 = b1 - (b2 + b3) / 2;
      arrayOfByte[k] = (byte)b4;
      j += abs(b4);
    } 
    arrayOfInt[3] = j;
    arrayOfByte = paramArrayOfByte[4];
    j = 0;
    for (k = paramInt3; k < paramInt2 + paramInt3; k++) {
      byte b1 = paramArrayOfByte1[k] & 0xFF;
      byte b2 = paramArrayOfByte1[k - paramInt3] & 0xFF;
      byte b3 = paramArrayOfByte2[k] & 0xFF;
      byte b4 = paramArrayOfByte2[k - paramInt3] & 0xFF;
      int m = paethPredictor(b2, b3, b4);
      byte b5 = b1 - m;
      arrayOfByte[k] = (byte)b5;
      j += abs(b5);
    } 
    arrayOfInt[4] = j;
    int i = arrayOfInt[0];
    j = 0;
    for (k = 1; k < 5; k++) {
      if (arrayOfInt[k] < i) {
        i = arrayOfInt[k];
        j = k;
      } 
    } 
    if (j == 0)
      System.arraycopy(paramArrayOfByte1, paramInt3, paramArrayOfByte[0], paramInt3, paramInt2); 
    return j;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\png\RowFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */