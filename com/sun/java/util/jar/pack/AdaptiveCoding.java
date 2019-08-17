package com.sun.java.util.jar.pack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class AdaptiveCoding implements CodingMethod {
  CodingMethod headCoding;
  
  int headLength;
  
  CodingMethod tailCoding;
  
  public static final int KX_MIN = 0;
  
  public static final int KX_MAX = 3;
  
  public static final int KX_LG2BASE = 4;
  
  public static final int KX_BASE = 16;
  
  public static final int KB_MIN = 0;
  
  public static final int KB_MAX = 255;
  
  public static final int KB_OFFSET = 1;
  
  public static final int KB_DEFAULT = 3;
  
  public AdaptiveCoding(int paramInt, CodingMethod paramCodingMethod1, CodingMethod paramCodingMethod2) {
    assert isCodableLength(paramInt);
    this.headLength = paramInt;
    this.headCoding = paramCodingMethod1;
    this.tailCoding = paramCodingMethod2;
  }
  
  public void setHeadCoding(CodingMethod paramCodingMethod) { this.headCoding = paramCodingMethod; }
  
  public void setHeadLength(int paramInt) {
    assert isCodableLength(paramInt);
    this.headLength = paramInt;
  }
  
  public void setTailCoding(CodingMethod paramCodingMethod) { this.tailCoding = paramCodingMethod; }
  
  public boolean isTrivial() { return (this.headCoding == this.tailCoding); }
  
  public void writeArrayTo(OutputStream paramOutputStream, int[] paramArrayOfInt, int paramInt1, int paramInt2) throws IOException { writeArray(this, paramOutputStream, paramArrayOfInt, paramInt1, paramInt2); }
  
  private static void writeArray(AdaptiveCoding paramAdaptiveCoding, OutputStream paramOutputStream, int[] paramArrayOfInt, int paramInt1, int paramInt2) throws IOException {
    while (true) {
      int i = paramInt1 + paramAdaptiveCoding.headLength;
      assert i <= paramInt2;
      paramAdaptiveCoding.headCoding.writeArrayTo(paramOutputStream, paramArrayOfInt, paramInt1, i);
      paramInt1 = i;
      if (paramAdaptiveCoding.tailCoding instanceof AdaptiveCoding) {
        paramAdaptiveCoding = (AdaptiveCoding)paramAdaptiveCoding.tailCoding;
        continue;
      } 
      break;
    } 
    paramAdaptiveCoding.tailCoding.writeArrayTo(paramOutputStream, paramArrayOfInt, paramInt1, paramInt2);
  }
  
  public void readArrayFrom(InputStream paramInputStream, int[] paramArrayOfInt, int paramInt1, int paramInt2) throws IOException { readArray(this, paramInputStream, paramArrayOfInt, paramInt1, paramInt2); }
  
  private static void readArray(AdaptiveCoding paramAdaptiveCoding, InputStream paramInputStream, int[] paramArrayOfInt, int paramInt1, int paramInt2) throws IOException {
    while (true) {
      int i = paramInt1 + paramAdaptiveCoding.headLength;
      assert i <= paramInt2;
      paramAdaptiveCoding.headCoding.readArrayFrom(paramInputStream, paramArrayOfInt, paramInt1, i);
      paramInt1 = i;
      if (paramAdaptiveCoding.tailCoding instanceof AdaptiveCoding) {
        paramAdaptiveCoding = (AdaptiveCoding)paramAdaptiveCoding.tailCoding;
        continue;
      } 
      break;
    } 
    paramAdaptiveCoding.tailCoding.readArrayFrom(paramInputStream, paramArrayOfInt, paramInt1, paramInt2);
  }
  
  static int getKXOf(int paramInt) {
    for (byte b = 0; b <= 3; b++) {
      if ((paramInt - 1 & 0xFFFFFF00) == 0)
        return b; 
      paramInt >>>= 4;
    } 
    return -1;
  }
  
  static int getKBOf(int paramInt) {
    int i = getKXOf(paramInt);
    if (i < 0)
      return -1; 
    paramInt >>>= i * 4;
    return paramInt - 1;
  }
  
  static int decodeK(int paramInt1, int paramInt2) {
    assert 0 <= paramInt1 && paramInt1 <= 3;
    assert 0 <= paramInt2 && paramInt2 <= 255;
    return paramInt2 + 1 << paramInt1 * 4;
  }
  
  static int getNextK(int paramInt) {
    if (paramInt <= 0)
      return 1; 
    int i = getKXOf(paramInt);
    if (i < 0)
      return Integer.MAX_VALUE; 
    int j = 1 << i * 4;
    int k = 255 << i * 4;
    int m = paramInt + j;
    m &= (j - 1 ^ 0xFFFFFFFF);
    if ((m - j & (k ^ 0xFFFFFFFF)) == 0) {
      assert getKXOf(m) == i;
      return m;
    } 
    if (i == 3)
      return Integer.MAX_VALUE; 
    int n = 255 << ++i * 4;
    m |= k & (n ^ 0xFFFFFFFF);
    m += j;
    assert getKXOf(m) == i;
    return m;
  }
  
  public static boolean isCodableLength(int paramInt) {
    int i = getKXOf(paramInt);
    if (i < 0)
      return false; 
    int j = 1 << i * 4;
    int k = 255 << i * 4;
    return ((paramInt - j & (k ^ 0xFFFFFFFF)) == 0);
  }
  
  public byte[] getMetaCoding(Coding paramCoding) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(10);
    try {
      makeMetaCoding(this, paramCoding, byteArrayOutputStream);
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
    return byteArrayOutputStream.toByteArray();
  }
  
  private static void makeMetaCoding(AdaptiveCoding paramAdaptiveCoding, Coding paramCoding, ByteArrayOutputStream paramByteArrayOutputStream) throws IOException {
    byte b;
    CodingMethod codingMethod;
    while (true) {
      CodingMethod codingMethod1 = paramAdaptiveCoding.headCoding;
      int i = paramAdaptiveCoding.headLength;
      codingMethod = paramAdaptiveCoding.tailCoding;
      int j = i;
      assert isCodableLength(j);
      byte b1 = (codingMethod1 == paramCoding) ? 1 : 0;
      b = (codingMethod == paramCoding) ? 1 : 0;
      if (b1 + b > true)
        b = 0; 
      byte b2 = 1 * b1 + 2 * b;
      assert b2 < 3;
      int k = getKXOf(j);
      int m = getKBOf(j);
      assert decodeK(k, m) == j;
      boolean bool = (m != 3) ? 1 : 0;
      paramByteArrayOutputStream.write(117 + k + 4 * bool + 8 * b2);
      if (bool)
        paramByteArrayOutputStream.write(m); 
      if (b1 == 0)
        paramByteArrayOutputStream.write(codingMethod1.getMetaCoding(paramCoding)); 
      if (codingMethod instanceof AdaptiveCoding) {
        paramAdaptiveCoding = (AdaptiveCoding)codingMethod;
        continue;
      } 
      break;
    } 
    if (b == 0)
      paramByteArrayOutputStream.write(codingMethod.getMetaCoding(paramCoding)); 
  }
  
  public static int parseMetaCoding(byte[] paramArrayOfByte, int paramInt, Coding paramCoding, CodingMethod[] paramArrayOfCodingMethod) {
    byte b = paramArrayOfByte[paramInt++] & 0xFF;
    if (b < 117 || b >= 141)
      return paramInt - 1; 
    AdaptiveCoding adaptiveCoding = null;
    boolean bool = true;
    while (bool) {
      bool = false;
      assert b >= 117;
      b -= 117;
      byte b1 = b % 4;
      byte b2 = b / 4 % 2;
      byte b3 = b / 8;
      assert b3 < 3;
      byte b4 = b3 & true;
      byte b5 = b3 & 0x2;
      CodingMethod[] arrayOfCodingMethod1 = { paramCoding };
      CodingMethod[] arrayOfCodingMethod2 = { paramCoding };
      byte b6 = 3;
      if (b2 != 0)
        b6 = paramArrayOfByte[paramInt++] & 0xFF; 
      if (b4 == 0)
        paramInt = BandStructure.parseMetaCoding(paramArrayOfByte, paramInt, paramCoding, arrayOfCodingMethod1); 
      if (b5 == 0 && (b = paramArrayOfByte[paramInt] & 0xFF) >= 117 && b < 141) {
        paramInt++;
        bool = true;
      } else if (b5 == 0) {
        paramInt = BandStructure.parseMetaCoding(paramArrayOfByte, paramInt, paramCoding, arrayOfCodingMethod2);
      } 
      AdaptiveCoding adaptiveCoding1 = new AdaptiveCoding(decodeK(b1, b6), arrayOfCodingMethod1[0], arrayOfCodingMethod2[0]);
      if (adaptiveCoding == null) {
        paramArrayOfCodingMethod[0] = adaptiveCoding1;
      } else {
        adaptiveCoding.tailCoding = adaptiveCoding1;
      } 
      adaptiveCoding = adaptiveCoding1;
    } 
    return paramInt;
  }
  
  private String keyString(CodingMethod paramCodingMethod) { return (paramCodingMethod instanceof Coding) ? ((Coding)paramCodingMethod).keyString() : paramCodingMethod.toString(); }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(20);
    AdaptiveCoding adaptiveCoding = this;
    stringBuilder.append("run(");
    while (true) {
      stringBuilder.append(adaptiveCoding.headLength).append("*");
      stringBuilder.append(keyString(adaptiveCoding.headCoding));
      if (adaptiveCoding.tailCoding instanceof AdaptiveCoding) {
        adaptiveCoding = (AdaptiveCoding)adaptiveCoding.tailCoding;
        stringBuilder.append(" ");
        continue;
      } 
      break;
    } 
    stringBuilder.append(" **").append(keyString(adaptiveCoding.tailCoding));
    stringBuilder.append(")");
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jav\\util\jar\pack\AdaptiveCoding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */