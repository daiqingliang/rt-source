package com.sun.java.util.jar.pack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;

class PopulationCoding implements CodingMethod {
  Histogram vHist;
  
  int[] fValues;
  
  int fVlen;
  
  long[] symtab;
  
  CodingMethod favoredCoding;
  
  CodingMethod tokenCoding;
  
  CodingMethod unfavoredCoding;
  
  int L = -1;
  
  static final int[] LValuesCoded = { 
      -1, 4, 8, 16, 32, 64, 128, 192, 224, 240, 
      248, 252 };
  
  public void setFavoredValues(int[] paramArrayOfInt, int paramInt) {
    assert paramArrayOfInt[0] == 0;
    assert this.fValues == null;
    this.fValues = paramArrayOfInt;
    this.fVlen = paramInt;
    if (this.L >= 0)
      setL(this.L); 
  }
  
  public void setFavoredValues(int[] paramArrayOfInt) {
    int i = paramArrayOfInt.length - 1;
    setFavoredValues(paramArrayOfInt, i);
  }
  
  public void setHistogram(Histogram paramHistogram) { this.vHist = paramHistogram; }
  
  public void setL(int paramInt) {
    this.L = paramInt;
    if (paramInt >= 0 && this.fValues != null && this.tokenCoding == null) {
      this.tokenCoding = fitTokenCoding(this.fVlen, paramInt);
      assert this.tokenCoding != null;
    } 
  }
  
  public static Coding fitTokenCoding(int paramInt1, int paramInt2) {
    if (paramInt1 < 256)
      return BandStructure.BYTE1; 
    Coding coding1 = BandStructure.UNSIGNED5.setL(paramInt2);
    if (!coding1.canRepresentUnsigned(paramInt1))
      return null; 
    Coding coding2 = coding1;
    Coding coding3 = coding1;
    while (true) {
      coding3 = coding3.setB(coding3.B() - 1);
      if (coding3.umax() < paramInt1)
        break; 
      coding2 = coding3;
    } 
    return coding2;
  }
  
  public void setFavoredCoding(CodingMethod paramCodingMethod) { this.favoredCoding = paramCodingMethod; }
  
  public void setTokenCoding(CodingMethod paramCodingMethod) {
    this.tokenCoding = paramCodingMethod;
    this.L = -1;
    if (paramCodingMethod instanceof Coding && this.fValues != null) {
      Coding coding = (Coding)paramCodingMethod;
      if (coding == fitTokenCoding(this.fVlen, coding.L()))
        this.L = coding.L(); 
    } 
  }
  
  public void setUnfavoredCoding(CodingMethod paramCodingMethod) { this.unfavoredCoding = paramCodingMethod; }
  
  public int favoredValueMaxLength() { return (this.L == 0) ? Integer.MAX_VALUE : BandStructure.UNSIGNED5.setL(this.L).umax(); }
  
  public void resortFavoredValues() {
    Coding coding = (Coding)this.tokenCoding;
    this.fValues = BandStructure.realloc(this.fValues, 1 + this.fVlen);
    int i = 1;
    for (byte b = 1; b <= coding.B(); b++) {
      int j = coding.byteMax(b);
      if (j > this.fVlen)
        j = this.fVlen; 
      if (j < coding.byteMin(b))
        break; 
      int k = i;
      int m = j + 1;
      if (m != k) {
        assert m > k : m + "!>" + k;
        assert coding.getLength(k) == b : b + " != len(" + k + ") == " + coding.getLength(k);
        assert coding.getLength(m - 1) == b : b + " != len(" + (m - true) + ") == " + coding.getLength(m - true);
        int n = k + (m - k) / 2;
        int i1 = k;
        int i2 = -1;
        int i3 = k;
        for (int i4 = k; i4 < m; i4++) {
          int i5 = this.fValues[i4];
          int i6 = this.vHist.getFrequency(i5);
          if (i2 != i6) {
            if (b == 1) {
              Arrays.sort(this.fValues, i3, i4);
            } else if (Math.abs(i1 - n) > Math.abs(i4 - n)) {
              i1 = i4;
            } 
            i2 = i6;
            i3 = i4;
          } 
        } 
        if (b == 1) {
          Arrays.sort(this.fValues, i3, m);
        } else {
          Arrays.sort(this.fValues, k, i1);
          Arrays.sort(this.fValues, i1, m);
        } 
        assert coding.getLength(k) == coding.getLength(i1);
        assert coding.getLength(k) == coding.getLength(m - 1);
        i = j + 1;
      } 
    } 
    assert i == this.fValues.length;
    this.symtab = null;
  }
  
  public int getToken(int paramInt) {
    if (this.symtab == null)
      this.symtab = makeSymtab(); 
    int i = Arrays.binarySearch(this.symtab, paramInt << 32);
    if (i < 0)
      i = -i - 1; 
    return (i < this.symtab.length && paramInt == (int)(this.symtab[i] >>> 32)) ? (int)this.symtab[i] : 0;
  }
  
  public int[][] encodeValues(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    int[] arrayOfInt1 = new int[paramInt2 - paramInt1];
    byte b = 0;
    for (int i = 0; i < arrayOfInt1.length; i++) {
      int k = paramArrayOfInt[paramInt1 + i];
      int m = getToken(k);
      if (m != 0) {
        arrayOfInt1[i] = m;
      } else {
        b++;
      } 
    } 
    int[] arrayOfInt2 = new int[b];
    b = 0;
    for (int j = 0; j < arrayOfInt1.length; j++) {
      if (arrayOfInt1[j] == 0) {
        int k = paramArrayOfInt[paramInt1 + j];
        arrayOfInt2[b++] = k;
      } 
    } 
    assert b == arrayOfInt2.length;
    return new int[][] { arrayOfInt1, arrayOfInt2 };
  }
  
  private long[] makeSymtab() {
    long[] arrayOfLong = new long[this.fVlen];
    for (byte b = 1; b <= this.fVlen; b++)
      arrayOfLong[b - true] = this.fValues[b] << 32 | b; 
    Arrays.sort(arrayOfLong);
    return arrayOfLong;
  }
  
  private Coding getTailCoding(CodingMethod paramCodingMethod) {
    while (paramCodingMethod instanceof AdaptiveCoding)
      paramCodingMethod = ((AdaptiveCoding)paramCodingMethod).tailCoding; 
    return (Coding)paramCodingMethod;
  }
  
  public void writeArrayTo(OutputStream paramOutputStream, int[] paramArrayOfInt, int paramInt1, int paramInt2) throws IOException {
    int[][] arrayOfInt = encodeValues(paramArrayOfInt, paramInt1, paramInt2);
    writeSequencesTo(paramOutputStream, arrayOfInt[0], arrayOfInt[1]);
  }
  
  void writeSequencesTo(OutputStream paramOutputStream, int[] paramArrayOfInt1, int[] paramArrayOfInt2) throws IOException {
    this.favoredCoding.writeArrayTo(paramOutputStream, this.fValues, 1, 1 + this.fVlen);
    getTailCoding(this.favoredCoding).writeTo(paramOutputStream, computeSentinelValue());
    this.tokenCoding.writeArrayTo(paramOutputStream, paramArrayOfInt1, 0, paramArrayOfInt1.length);
    if (paramArrayOfInt2.length > 0)
      this.unfavoredCoding.writeArrayTo(paramOutputStream, paramArrayOfInt2, 0, paramArrayOfInt2.length); 
  }
  
  int computeSentinelValue() {
    Coding coding = getTailCoding(this.favoredCoding);
    if (coding.isDelta())
      return 0; 
    int i = this.fValues[1];
    int j = i;
    for (byte b = 2; b <= this.fVlen; b++) {
      j = this.fValues[b];
      i = moreCentral(i, j);
    } 
    return (coding.getLength(i) <= coding.getLength(j)) ? i : j;
  }
  
  public void readArrayFrom(InputStream paramInputStream, int[] paramArrayOfInt, int paramInt1, int paramInt2) throws IOException {
    setFavoredValues(readFavoredValuesFrom(paramInputStream, paramInt2 - paramInt1));
    this.tokenCoding.readArrayFrom(paramInputStream, paramArrayOfInt, paramInt1, paramInt2);
    int i = 0;
    int j = -1;
    byte b1 = 0;
    for (int k = paramInt1; k < paramInt2; k++) {
      int m = paramArrayOfInt[k];
      if (m == 0) {
        if (j < 0) {
          i = k;
        } else {
          paramArrayOfInt[j] = k;
        } 
        j = k;
        b1++;
      } else {
        paramArrayOfInt[k] = this.fValues[m];
      } 
    } 
    int[] arrayOfInt = new int[b1];
    if (b1 > 0)
      this.unfavoredCoding.readArrayFrom(paramInputStream, arrayOfInt, 0, b1); 
    for (byte b2 = 0; b2 < b1; b2++) {
      int m = paramArrayOfInt[i];
      paramArrayOfInt[i] = arrayOfInt[b2];
      i = m;
    } 
  }
  
  int[] readFavoredValuesFrom(InputStream paramInputStream, int paramInt) throws IOException {
    int[] arrayOfInt = new int[1000];
    HashSet hashSet = null;
    assert (hashSet = new HashSet()) != null;
    int i = 1;
    paramInt += i;
    int j = Integer.MIN_VALUE;
    int k = 0;
    CodingMethod codingMethod;
    for (codingMethod = this.favoredCoding; codingMethod instanceof AdaptiveCoding; codingMethod = adaptiveCoding.tailCoding) {
      AdaptiveCoding adaptiveCoding = (AdaptiveCoding)codingMethod;
      int m = adaptiveCoding.headLength;
      while (i + m > arrayOfInt.length)
        arrayOfInt = BandStructure.realloc(arrayOfInt); 
      int n = i + m;
      adaptiveCoding.headCoding.readArrayFrom(paramInputStream, arrayOfInt, i, n);
      while (i < n) {
        int i1 = arrayOfInt[i++];
        assert hashSet.add(Integer.valueOf(i1));
        assert i <= paramInt;
        k = i1;
        j = moreCentral(j, i1);
      } 
    } 
    Coding coding = (Coding)codingMethod;
    if (coding.isDelta()) {
      long l = 0L;
      while (true) {
        int m;
        l += coding.readFrom(paramInputStream);
        if (coding.isSubrange()) {
          m = coding.reduceToUnsignedRange(l);
        } else {
          m = (int)l;
        } 
        l = m;
        if (i > 1 && (m == k || m == j))
          break; 
        if (i == arrayOfInt.length)
          arrayOfInt = BandStructure.realloc(arrayOfInt); 
        arrayOfInt[i++] = m;
        assert hashSet.add(Integer.valueOf(m));
        assert i <= paramInt;
        k = m;
        j = moreCentral(j, m);
      } 
    } else {
      while (true) {
        int m = coding.readFrom(paramInputStream);
        if (i > 1 && (m == k || m == j))
          break; 
        if (i == arrayOfInt.length)
          arrayOfInt = BandStructure.realloc(arrayOfInt); 
        arrayOfInt[i++] = m;
        assert hashSet.add(Integer.valueOf(m));
        assert i <= paramInt;
        k = m;
        j = moreCentral(j, m);
      } 
    } 
    return BandStructure.realloc(arrayOfInt, i);
  }
  
  private static int moreCentral(int paramInt1, int paramInt2) {
    int i = paramInt1 >> 31 ^ paramInt1 << 1;
    int j = paramInt2 >> 31 ^ paramInt2 << 1;
    i -= Integer.MIN_VALUE;
    j -= Integer.MIN_VALUE;
    int k = (i < j) ? paramInt1 : paramInt2;
    assert k == moreCentralSlow(paramInt1, paramInt2);
    return k;
  }
  
  private static int moreCentralSlow(int paramInt1, int paramInt2) {
    int i = paramInt1;
    if (i < 0)
      i = -i; 
    if (i < 0)
      return paramInt2; 
    int j = paramInt2;
    if (j < 0)
      j = -j; 
    return (j < 0) ? paramInt1 : ((i < j) ? paramInt1 : ((i > j) ? paramInt2 : ((paramInt1 < paramInt2) ? paramInt1 : paramInt2)));
  }
  
  public byte[] getMetaCoding(Coding paramCoding) {
    int i = this.fVlen;
    byte b1 = 0;
    if (this.tokenCoding instanceof Coding) {
      Coding coding = (Coding)this.tokenCoding;
      if (coding.B() == 1) {
        b1 = 1;
      } else if (this.L >= 0) {
        assert this.L == coding.L();
        for (byte b = 1; b < LValuesCoded.length; b++) {
          if (LValuesCoded[b] == this.L) {
            b1 = b;
            break;
          } 
        } 
      } 
    } 
    CodingMethod codingMethod = null;
    if (b1 != 0 && this.tokenCoding == fitTokenCoding(this.fVlen, this.L))
      codingMethod = this.tokenCoding; 
    boolean bool1 = (this.favoredCoding == paramCoding) ? 1 : 0;
    boolean bool2 = (this.unfavoredCoding == paramCoding || this.unfavoredCoding == null) ? 1 : 0;
    boolean bool3 = (this.tokenCoding == codingMethod) ? 1 : 0;
    byte b2 = (bool3 == true) ? b1 : 0;
    assert bool3 == ((b2 > 0) ? 1 : 0);
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(10);
    byteArrayOutputStream.write('' + bool1 + 2 * bool2 + 4 * b2);
    try {
      if (!bool1)
        byteArrayOutputStream.write(this.favoredCoding.getMetaCoding(paramCoding)); 
      if (!bool3)
        byteArrayOutputStream.write(this.tokenCoding.getMetaCoding(paramCoding)); 
      if (!bool2)
        byteArrayOutputStream.write(this.unfavoredCoding.getMetaCoding(paramCoding)); 
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
    return byteArrayOutputStream.toByteArray();
  }
  
  public static int parseMetaCoding(byte[] paramArrayOfByte, int paramInt, Coding paramCoding, CodingMethod[] paramArrayOfCodingMethod) {
    byte b1 = paramArrayOfByte[paramInt++] & 0xFF;
    if (b1 < 141 || b1 >= 189)
      return paramInt - 1; 
    b1 -= 141;
    byte b2 = b1 % 2;
    byte b3 = b1 / 2 % 2;
    byte b4 = b1 / 4;
    boolean bool = (b4 > 0) ? 1 : 0;
    int i = LValuesCoded[b4];
    CodingMethod[] arrayOfCodingMethod1 = { paramCoding };
    CodingMethod[] arrayOfCodingMethod2 = { null };
    CodingMethod[] arrayOfCodingMethod3 = { paramCoding };
    if (b2 == 0)
      paramInt = BandStructure.parseMetaCoding(paramArrayOfByte, paramInt, paramCoding, arrayOfCodingMethod1); 
    if (!bool)
      paramInt = BandStructure.parseMetaCoding(paramArrayOfByte, paramInt, paramCoding, arrayOfCodingMethod2); 
    if (b3 == 0)
      paramInt = BandStructure.parseMetaCoding(paramArrayOfByte, paramInt, paramCoding, arrayOfCodingMethod3); 
    PopulationCoding populationCoding = new PopulationCoding();
    populationCoding.L = i;
    populationCoding.favoredCoding = arrayOfCodingMethod1[0];
    populationCoding.tokenCoding = arrayOfCodingMethod2[0];
    populationCoding.unfavoredCoding = arrayOfCodingMethod3[0];
    paramArrayOfCodingMethod[0] = populationCoding;
    return paramInt;
  }
  
  private String keyString(CodingMethod paramCodingMethod) { return (paramCodingMethod instanceof Coding) ? ((Coding)paramCodingMethod).keyString() : ((paramCodingMethod == null) ? "none" : paramCodingMethod.toString()); }
  
  public String toString() {
    PropMap propMap = Utils.currentPropMap();
    boolean bool = (propMap != null && propMap.getBoolean("com.sun.java.util.jar.pack.verbose.pop")) ? 1 : 0;
    StringBuilder stringBuilder = new StringBuilder(100);
    stringBuilder.append("pop(").append("fVlen=").append(this.fVlen);
    if (bool && this.fValues != null) {
      stringBuilder.append(" fV=[");
      for (byte b = 1; b <= this.fVlen; b++)
        stringBuilder.append((b == 1) ? "" : ",").append(this.fValues[b]); 
      stringBuilder.append(";").append(computeSentinelValue());
      stringBuilder.append("]");
    } 
    stringBuilder.append(" fc=").append(keyString(this.favoredCoding));
    stringBuilder.append(" tc=").append(keyString(this.tokenCoding));
    stringBuilder.append(" uc=").append(keyString(this.unfavoredCoding));
    stringBuilder.append(")");
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jav\\util\jar\pack\PopulationCoding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */