package com.sun.security.auth.module;

import java.io.UnsupportedEncodingException;

class Crypt {
  private static final byte[] IP = { 
      58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 
      44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 
      30, 22, 14, 6, 64, 56, 48, 40, 32, 24, 
      16, 8, 57, 49, 41, 33, 25, 17, 9, 1, 
      59, 51, 43, 35, 27, 19, 11, 3, 61, 53, 
      45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 
      31, 23, 15, 7 };
  
  private static final byte[] FP = { 
      40, 8, 48, 16, 56, 24, 64, 32, 39, 7, 
      47, 15, 55, 23, 63, 31, 38, 6, 46, 14, 
      54, 22, 62, 30, 37, 5, 45, 13, 53, 21, 
      61, 29, 36, 4, 44, 12, 52, 20, 60, 28, 
      35, 3, 43, 11, 51, 19, 59, 27, 34, 2, 
      42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 
      49, 17, 57, 25 };
  
  private static final byte[] PC1_C = { 
      57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 
      42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 
      27, 19, 11, 3, 60, 52, 44, 36 };
  
  private static final byte[] PC1_D = { 
      63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 
      46, 38, 30, 22, 14, 6, 61, 53, 45, 37, 
      29, 21, 13, 5, 28, 20, 12, 4 };
  
  private static final byte[] shifts = { 
      1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 
      2, 2, 2, 2, 2, 1 };
  
  private static final byte[] PC2_C = { 
      14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 
      21, 10, 23, 19, 12, 4, 26, 8, 16, 7, 
      27, 20, 13, 2 };
  
  private static final byte[] PC2_D = { 
      41, 52, 31, 37, 47, 55, 30, 40, 51, 45, 
      33, 48, 44, 49, 39, 56, 34, 53, 46, 42, 
      50, 36, 29, 32 };
  
  private byte[] C = new byte[28];
  
  private byte[] D = new byte[28];
  
  private byte[] KS;
  
  private byte[] E = new byte[48];
  
  private static final byte[] e2 = { 
      32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 
      8, 9, 8, 9, 10, 11, 12, 13, 12, 13, 
      14, 15, 16, 17, 16, 17, 18, 19, 20, 21, 
      20, 21, 22, 23, 24, 25, 24, 25, 26, 27, 
      28, 29, 28, 29, 30, 31, 32, 1 };
  
  private static final byte[][] S = { { 
        14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 
        6, 12, 5, 9, 0, 7, 0, 15, 7, 4, 
        14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 
        3, 8, 4, 1, 14, 8, 13, 6, 2, 11, 
        15, 12, 9, 7, 3, 10, 5, 0, 15, 12, 
        8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 
        10, 0, 6, 13 }, { 
        15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 
        2, 13, 12, 0, 5, 10, 3, 13, 4, 7, 
        15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 
        11, 5, 0, 14, 7, 11, 10, 4, 13, 1, 
        5, 8, 12, 6, 9, 3, 2, 15, 13, 8, 
        10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 
        0, 5, 14, 9 }, { 
        10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 
        12, 7, 11, 4, 2, 8, 13, 7, 0, 9, 
        3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 
        15, 1, 13, 6, 4, 9, 8, 15, 3, 0, 
        11, 1, 2, 12, 5, 10, 14, 7, 1, 10, 
        13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 
        11, 5, 2, 12 }, { 
        7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 
        8, 5, 11, 12, 4, 15, 13, 8, 11, 5, 
        6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 
        14, 9, 10, 6, 9, 0, 12, 11, 7, 13, 
        15, 1, 3, 14, 5, 2, 8, 4, 3, 15, 
        0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 
        12, 7, 2, 14 }, { 
        2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 
        3, 15, 13, 0, 14, 9, 14, 11, 2, 12, 
        4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 
        8, 6, 4, 2, 1, 11, 10, 13, 7, 8, 
        15, 9, 12, 5, 6, 3, 0, 14, 11, 8, 
        12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 
        10, 4, 5, 3 }, { 
        12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 
        3, 4, 14, 7, 5, 11, 10, 15, 4, 2, 
        7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 
        3, 8, 9, 14, 15, 5, 2, 8, 12, 3, 
        7, 0, 4, 10, 1, 13, 11, 6, 4, 3, 
        2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 
        6, 0, 8, 13 }, { 
        4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 
        9, 7, 5, 10, 6, 1, 13, 0, 11, 7, 
        4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 
        8, 6, 1, 4, 11, 13, 12, 3, 7, 14, 
        10, 15, 6, 8, 0, 5, 9, 2, 6, 11, 
        13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 
        14, 2, 3, 12 }, { 
        13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 
        3, 14, 5, 0, 12, 7, 1, 15, 13, 8, 
        10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 
        9, 2, 7, 11, 4, 1, 9, 12, 14, 2, 
        0, 6, 10, 13, 15, 3, 5, 8, 2, 1, 
        14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 
        3, 5, 6, 11 } };
  
  private static final byte[] P = { 
      16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 
      23, 26, 5, 18, 31, 10, 2, 8, 24, 14, 
      32, 27, 3, 9, 19, 13, 30, 6, 22, 11, 
      4, 25 };
  
  private byte[] L = new byte[64];
  
  private byte[] tempL = new byte[32];
  
  private byte[] f = new byte[32];
  
  private byte[] preS = new byte[48];
  
  private void setkey(byte[] paramArrayOfByte) {
    if (this.KS == null)
      this.KS = new byte[768]; 
    byte b;
    for (b = 0; b < 28; b++) {
      this.C[b] = paramArrayOfByte[PC1_C[b] - 1];
      this.D[b] = paramArrayOfByte[PC1_D[b] - 1];
    } 
    for (b = 0; b < 16; b++) {
      for (byte b2 = 0; b2 < shifts[b]; b2++) {
        byte b4 = this.C[0];
        byte b3;
        for (b3 = 0; b3 < 27; b3++)
          this.C[b3] = this.C[b3 + true]; 
        this.C[27] = b4;
        b4 = this.D[0];
        for (b3 = 0; b3 < 27; b3++)
          this.D[b3] = this.D[b3 + 1]; 
        this.D[27] = b4;
      } 
      for (byte b1 = 0; b1 < 24; b1++) {
        byte b3 = b * 48;
        this.KS[b3 + b1] = this.C[PC2_C[b1] - 1];
        this.KS[b3 + b1 + 24] = this.D[PC2_D[b1] - 28 - 1];
      } 
    } 
    for (b = 0; b < 48; b++)
      this.E[b] = e2[b]; 
  }
  
  private void encrypt(byte[] paramArrayOfByte, int paramInt) {
    byte b2 = 32;
    if (this.KS == null)
      this.KS = new byte[768]; 
    byte b1;
    for (b1 = 0; b1 < 64; b1++)
      this.L[b1] = paramArrayOfByte[IP[b1] - 1]; 
    for (byte b = 0; b < 16; b++) {
      byte b3 = b * 48;
      for (b1 = 0; b1 < 32; b1++)
        this.tempL[b1] = this.L[b2 + b1]; 
      for (b1 = 0; b1 < 48; b1++)
        this.preS[b1] = (byte)(this.L[b2 + this.E[b1] - 1] ^ this.KS[b3 + b1]); 
      for (b1 = 0; b1 < 8; b1++) {
        byte b4 = 6 * b1;
        byte b5 = S[b1][(this.preS[b4 + 0] << 5) + (this.preS[b4 + 1] << 3) + (this.preS[b4 + 2] << 2) + (this.preS[b4 + 3] << 1) + (this.preS[b4 + 4] << 0) + (this.preS[b4 + 5] << 4)];
        b4 = 4 * b1;
        this.f[b4 + 0] = (byte)(b5 >> 3 & true);
        this.f[b4 + 1] = (byte)(b5 >> 2 & true);
        this.f[b4 + 2] = (byte)(b5 >> 1 & true);
        this.f[b4 + 3] = (byte)(b5 >> 0 & true);
      } 
      for (b1 = 0; b1 < 32; b1++)
        this.L[b2 + b1] = (byte)(this.L[b1] ^ this.f[P[b1] - 1]); 
      for (b1 = 0; b1 < 32; b1++)
        this.L[b1] = this.tempL[b1]; 
    } 
    for (b1 = 0; b1 < 32; b1++) {
      byte b3 = this.L[b1];
      this.L[b1] = this.L[b2 + b1];
      this.L[b2 + b1] = (byte)b3;
    } 
    for (b1 = 0; b1 < 64; b1++)
      paramArrayOfByte[b1] = this.L[FP[b1] - 1]; 
  }
  
  public byte[] crypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
    byte[] arrayOfByte1 = new byte[66];
    byte[] arrayOfByte2 = new byte[13];
    byte b2 = 0;
    byte b1 = 0;
    while (b2 < paramArrayOfByte1.length && b1 < 64) {
      byte b = paramArrayOfByte1[b2];
      byte b3 = 0;
      while (b3 < 7) {
        arrayOfByte1[b1] = (byte)(b >> 6 - b3 & true);
        b3++;
        b1++;
      } 
      b1++;
      b2++;
    } 
    setkey(arrayOfByte1);
    for (b1 = 0; b1 < 66; b1++)
      arrayOfByte1[b1] = 0; 
    for (b1 = 0; b1 < 2; b1++) {
      byte b = paramArrayOfByte2[b1];
      arrayOfByte2[b1] = (byte)b;
      if (b > 90)
        b -= 6; 
      if (b > 57)
        b -= 7; 
      b -= 46;
      for (byte b3 = 0; b3 < 6; b3++) {
        if ((b >> b3 & true) != 0) {
          byte b4 = this.E[6 * b1 + b3];
          this.E[6 * b1 + b3] = this.E[6 * b1 + b3 + 24];
          this.E[6 * b1 + b3 + 24] = b4;
        } 
      } 
    } 
    for (b1 = 0; b1 < 25; b1++)
      encrypt(arrayOfByte1, 0); 
    for (b1 = 0; b1 < 11; b1++) {
      byte b = 0;
      for (byte b3 = 0; b3 < 6; b3++) {
        b <<= true;
        b |= arrayOfByte1[6 * b1 + b3];
      } 
      b += 46;
      if (b > 57)
        b += 7; 
      if (b > 90)
        b += 6; 
      arrayOfByte2[b1 + 2] = (byte)b;
    } 
    if (arrayOfByte2[1] == 0)
      arrayOfByte2[1] = arrayOfByte2[0]; 
    return arrayOfByte2;
  }
  
  public static void main(String[] paramArrayOfString) {
    if (paramArrayOfString.length != 2) {
      System.err.println("usage: Crypt password salt");
      System.exit(1);
    } 
    Crypt crypt = new Crypt();
    try {
      byte[] arrayOfByte = crypt.crypt(paramArrayOfString[0].getBytes("ISO-8859-1"), paramArrayOfString[1].getBytes("ISO-8859-1"));
      for (byte b = 0; b < arrayOfByte.length; b++)
        System.out.println(" " + b + " " + (char)arrayOfByte[b]); 
    } catch (UnsupportedEncodingException unsupportedEncodingException) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\auth\module\Crypt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */