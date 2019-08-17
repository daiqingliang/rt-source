package com.sun.imageio.plugins.common;

import java.io.PrintStream;

public class LZWStringTable {
  private static final int RES_CODES = 2;
  
  private static final short HASH_FREE = -1;
  
  private static final short NEXT_FIRST = -1;
  
  private static final int MAXBITS = 12;
  
  private static final int MAXSTR = 4096;
  
  private static final short HASHSIZE = 9973;
  
  private static final short HASHSTEP = 2039;
  
  byte[] strChr = new byte[4096];
  
  short[] strNxt = new short[4096];
  
  short[] strHsh = new short[9973];
  
  short numStrings;
  
  int[] strLen = new int[4096];
  
  public int addCharString(short paramShort, byte paramByte) {
    if (this.numStrings >= 4096)
      return 65535; 
    int i;
    for (i = hash(paramShort, paramByte); this.strHsh[i] != -1; i = (i + 2039) % 9973);
    this.strHsh[i] = this.numStrings;
    this.strChr[this.numStrings] = paramByte;
    if (paramShort == -1) {
      this.strNxt[this.numStrings] = -1;
      this.strLen[this.numStrings] = 1;
    } else {
      this.strNxt[this.numStrings] = paramShort;
      this.strLen[this.numStrings] = this.strLen[paramShort] + 1;
    } 
    this.numStrings = (short)(this.numStrings + 1);
    return this.numStrings;
  }
  
  public short findCharString(short paramShort, byte paramByte) {
    if (paramShort == -1)
      return (short)(paramByte & 0xFF); 
    short s;
    for (int i = hash(paramShort, paramByte); (s = this.strHsh[i]) != -1; i = (i + 2039) % 9973) {
      if (this.strNxt[s] == paramShort && this.strChr[s] == paramByte)
        return (short)s; 
    } 
    return -1;
  }
  
  public void clearTable(int paramInt) {
    this.numStrings = 0;
    int i;
    for (i = 0; i < 9973; i++)
      this.strHsh[i] = -1; 
    i = (1 << paramInt) + 2;
    for (byte b = 0; b < i; b++)
      addCharString((short)-1, (byte)b); 
  }
  
  public static int hash(short paramShort, byte paramByte) { return (((short)(paramByte << 8) ^ paramShort) & 0xFFFF) % 9973; }
  
  public int expandCode(byte[] paramArrayOfByte, int paramInt1, short paramShort, int paramInt2) {
    int i;
    if (paramInt1 == -2 && paramInt2 == 1)
      paramInt2 = 0; 
    if (paramShort == -1 || paramInt2 == this.strLen[paramShort])
      return 0; 
    int j = this.strLen[paramShort] - paramInt2;
    int k = paramArrayOfByte.length - paramInt1;
    if (k > j) {
      i = j;
    } else {
      i = k;
    } 
    int m = j - i;
    int n = paramInt1 + i;
    while (n > paramInt1 && paramShort != -1) {
      if (--m < 0)
        paramArrayOfByte[--n] = this.strChr[paramShort]; 
      paramShort = this.strNxt[paramShort];
    } 
    return (j > i) ? -i : i;
  }
  
  public void dump(PrintStream paramPrintStream) {
    for (char c = 'Ă'; c < this.numStrings; c++)
      paramPrintStream.println(" strNxt[" + c + "] = " + this.strNxt[c] + " strChr " + Integer.toHexString(this.strChr[c] & 0xFF) + " strLen " + Integer.toHexString(this.strLen[c])); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\common\LZWStringTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */