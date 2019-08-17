package com.sun.imageio.plugins.png;

class CRC {
  private static int[] crcTable = new int[256];
  
  private int crc = -1;
  
  public void reset() { this.crc = -1; }
  
  public void update(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    for (int i = 0; i < paramInt2; i++)
      this.crc = crcTable[(this.crc ^ paramArrayOfByte[paramInt1 + i]) & 0xFF] ^ this.crc >>> 8; 
  }
  
  public void update(int paramInt) { this.crc = crcTable[(this.crc ^ paramInt) & 0xFF] ^ this.crc >>> 8; }
  
  public int getValue() { return this.crc ^ 0xFFFFFFFF; }
  
  static  {
    for (byte b = 0; b < 'Ā'; b++) {
      int i = b;
      for (byte b1 = 0; b1 < 8; b1++) {
        if ((i & true) == true) {
          i = 0xEDB88320 ^ i >>> true;
        } else {
          i >>>= 1;
        } 
        crcTable[b] = i;
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\png\CRC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */