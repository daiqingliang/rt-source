package com.sun.java.util.jar.pack;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;

final class Histogram {
  protected final int[][] matrix;
  
  protected final int totalWeight;
  
  protected final int[] values;
  
  protected final int[] counts;
  
  private static final long LOW32 = 4294967295L;
  
  private static double log2 = Math.log(2.0D);
  
  private final BitMetric bitMetric = new BitMetric() {
      public double getBitLength(int param1Int) { return Histogram.this.getBitLength(param1Int); }
    };
  
  public Histogram(int[] paramArrayOfInt) {
    long[] arrayOfLong = computeHistogram2Col(maybeSort(paramArrayOfInt));
    int[][] arrayOfInt = makeTable(arrayOfLong);
    this.values = arrayOfInt[0];
    this.counts = arrayOfInt[1];
    this.matrix = makeMatrix(arrayOfLong);
    this.totalWeight = paramArrayOfInt.length;
    assert assertWellFormed(paramArrayOfInt);
  }
  
  public Histogram(int[] paramArrayOfInt, int paramInt1, int paramInt2) { this(sortedSlice(paramArrayOfInt, paramInt1, paramInt2)); }
  
  public Histogram(int[][] paramArrayOfInt) {
    paramArrayOfInt = normalizeMatrix(paramArrayOfInt);
    this.matrix = paramArrayOfInt;
    int i = 0;
    int j = 0;
    for (byte b1 = 0; b1 < paramArrayOfInt.length; b1++) {
      int k = paramArrayOfInt[b1].length - 1;
      i += k;
      j += paramArrayOfInt[b1][0] * k;
    } 
    this.totalWeight = j;
    long[] arrayOfLong = new long[i];
    byte b2 = 0;
    for (byte b3 = 0; b3 < paramArrayOfInt.length; b3++) {
      for (byte b = 1; b < paramArrayOfInt[b3].length; b++)
        arrayOfLong[b2++] = paramArrayOfInt[b3][b] << 32 | 0xFFFFFFFFL & paramArrayOfInt[b3][0]; 
    } 
    assert b2 == arrayOfLong.length;
    Arrays.sort(arrayOfLong);
    int[][] arrayOfInt = makeTable(arrayOfLong);
    this.values = arrayOfInt[1];
    this.counts = arrayOfInt[0];
    assert assertWellFormed(null);
  }
  
  public int[][] getMatrix() { return this.matrix; }
  
  public int getRowCount() { return this.matrix.length; }
  
  public int getRowFrequency(int paramInt) { return this.matrix[paramInt][0]; }
  
  public int getRowLength(int paramInt) { return this.matrix[paramInt].length - 1; }
  
  public int getRowValue(int paramInt1, int paramInt2) { return this.matrix[paramInt1][paramInt2 + 1]; }
  
  public int getRowWeight(int paramInt) { return getRowFrequency(paramInt) * getRowLength(paramInt); }
  
  public int getTotalWeight() { return this.totalWeight; }
  
  public int getTotalLength() { return this.values.length; }
  
  public int[] getAllValues() { return this.values; }
  
  public int[] getAllFrequencies() { return this.counts; }
  
  public int getFrequency(int paramInt) {
    int i = Arrays.binarySearch(this.values, paramInt);
    if (i < 0)
      return 0; 
    assert this.values[i] == paramInt;
    return this.counts[i];
  }
  
  public double getBitLength(int paramInt) {
    double d = getFrequency(paramInt) / getTotalWeight();
    return -Math.log(d) / log2;
  }
  
  public double getRowBitLength(int paramInt) {
    double d = getRowFrequency(paramInt) / getTotalWeight();
    return -Math.log(d) / log2;
  }
  
  public BitMetric getBitMetric() { return this.bitMetric; }
  
  public double getBitLength() {
    double d = 0.0D;
    for (byte b = 0; b < this.matrix.length; b++)
      d += getRowBitLength(b) * getRowWeight(b); 
    assert 0.1D > Math.abs(d - getBitLength(this.bitMetric));
    return d;
  }
  
  public double getBitLength(BitMetric paramBitMetric) {
    double d = 0.0D;
    for (byte b = 0; b < this.matrix.length; b++) {
      for (byte b1 = 1; b1 < this.matrix[b].length; b1++)
        d += this.matrix[b][0] * paramBitMetric.getBitLength(this.matrix[b][b1]); 
    } 
    return d;
  }
  
  private static double round(double paramDouble1, double paramDouble2) { return Math.round(paramDouble1 * paramDouble2) / paramDouble2; }
  
  public int[][] normalizeMatrix(int[][] paramArrayOfInt) {
    long[] arrayOfLong = new long[paramArrayOfInt.length];
    for (byte b1 = 0; b1 < paramArrayOfInt.length; b1++) {
      if (paramArrayOfInt[b1].length > 1) {
        int k = paramArrayOfInt[b1][0];
        if (k > 0)
          arrayOfLong[b1] = k << 32 | b1; 
      } 
    } 
    Arrays.sort(arrayOfLong);
    int[][] arrayOfInt = new int[paramArrayOfInt.length][];
    int i = -1;
    byte b2 = 0;
    byte b3 = 0;
    for (int j = 0;; j++) {
      int[] arrayOfInt1;
      if (j < paramArrayOfInt.length) {
        long l = arrayOfLong[arrayOfLong.length - j - 1];
        if (l == 0L)
          continue; 
        arrayOfInt1 = paramArrayOfInt[(int)l];
        assert l >>> 32 == arrayOfInt1[0];
      } else {
        arrayOfInt1 = new int[] { -1 };
      } 
      if (arrayOfInt1[0] != i && b3 > b2) {
        int k = 0;
        for (byte b4 = b2; b4 < b3; b4++) {
          int[] arrayOfInt3 = arrayOfInt[b4];
          assert arrayOfInt3[0] == i;
          k += arrayOfInt3.length - 1;
        } 
        int[] arrayOfInt2 = new int[1 + k];
        arrayOfInt2[0] = i;
        int m = 1;
        byte b5;
        for (b5 = b2; b5 < b3; b5++) {
          int[] arrayOfInt3 = arrayOfInt[b5];
          assert arrayOfInt3[0] == i;
          System.arraycopy(arrayOfInt3, 1, arrayOfInt2, m, arrayOfInt3.length - 1);
          m += arrayOfInt3.length - 1;
        } 
        if (!isSorted(arrayOfInt2, 1, true)) {
          Arrays.sort(arrayOfInt2, 1, arrayOfInt2.length);
          b5 = 2;
          for (byte b = 2; b < arrayOfInt2.length; b++) {
            if (arrayOfInt2[b] != arrayOfInt2[b - 1])
              arrayOfInt2[b5++] = arrayOfInt2[b]; 
          } 
          if (b5 < arrayOfInt2.length) {
            int[] arrayOfInt3 = new int[b5];
            System.arraycopy(arrayOfInt2, 0, arrayOfInt3, 0, b5);
            arrayOfInt2 = arrayOfInt3;
          } 
        } 
        arrayOfInt[b2++] = arrayOfInt2;
        b3 = b2;
      } 
      if (j == paramArrayOfInt.length)
        break; 
      i = arrayOfInt1[0];
      arrayOfInt[b3++] = arrayOfInt1;
      continue;
    } 
    assert b2 == b3;
    paramArrayOfInt = arrayOfInt;
    if (b2 < paramArrayOfInt.length) {
      arrayOfInt = new int[b2][];
      System.arraycopy(paramArrayOfInt, 0, arrayOfInt, 0, b2);
      paramArrayOfInt = arrayOfInt;
    } 
    return paramArrayOfInt;
  }
  
  public String[] getRowTitles(String paramString) {
    int i = getTotalLength();
    int j = getTotalWeight();
    String[] arrayOfString = new String[this.matrix.length];
    int k = 0;
    int m = 0;
    for (byte b = 0; b < this.matrix.length; b++) {
      int n = getRowFrequency(b);
      int i1 = getRowLength(b);
      int i2 = getRowWeight(b);
      k += i2;
      m += i1;
      long l1 = (k * 100L + (j / 2)) / j;
      long l2 = (m * 100L + (i / 2)) / i;
      double d = getRowBitLength(b);
      assert 0.1D > Math.abs(d - getBitLength(this.matrix[b][1]));
      arrayOfString[b] = paramString + "[" + b + "] len=" + round(d, 10.0D) + " (" + n + "*[" + i1 + "]) (" + k + ":" + l1 + "%) [" + m + ":" + l2 + "%]";
    } 
    return arrayOfString;
  }
  
  public void print(PrintStream paramPrintStream) { print("hist", paramPrintStream); }
  
  public void print(String paramString, PrintStream paramPrintStream) { print(paramString, getRowTitles(paramString), paramPrintStream); }
  
  public void print(String paramString, String[] paramArrayOfString, PrintStream paramPrintStream) {
    int i = getTotalLength();
    int j = getTotalWeight();
    double d1 = getBitLength();
    double d2 = d1 / j;
    double d3 = j / i;
    String str = paramString + " len=" + round(d1, 10.0D) + " avgLen=" + round(d2, 10.0D) + " weight(" + j + ") unique[" + i + "] avgWeight(" + round(d3, 100.0D) + ")";
    if (paramArrayOfString == null) {
      paramPrintStream.println(str);
    } else {
      paramPrintStream.println(str + " {");
      StringBuffer stringBuffer = new StringBuffer();
      for (byte b = 0; b < this.matrix.length; b++) {
        stringBuffer.setLength(0);
        stringBuffer.append("  ").append(paramArrayOfString[b]).append(" {");
        for (byte b1 = 1; b1 < this.matrix[b].length; b1++)
          stringBuffer.append(" ").append(this.matrix[b][b1]); 
        stringBuffer.append(" }");
        paramPrintStream.println(stringBuffer);
      } 
      paramPrintStream.println("}");
    } 
  }
  
  private static int[][] makeMatrix(long[] paramArrayOfLong) {
    Arrays.sort(paramArrayOfLong);
    int[] arrayOfInt = new int[paramArrayOfLong.length];
    for (byte b1 = 0; b1 < arrayOfInt.length; b1++)
      arrayOfInt[b1] = (int)(paramArrayOfLong[b1] >>> 32); 
    long[] arrayOfLong = computeHistogram2Col(arrayOfInt);
    int[][] arrayOfInt1 = new int[arrayOfLong.length][];
    byte b2 = 0;
    byte b3 = 0;
    int i = arrayOfInt1.length;
    while (--i >= 0) {
      long l = arrayOfLong[b3++];
      int j = (int)l;
      int k = (int)(l >>> 32);
      int[] arrayOfInt2 = new int[1 + k];
      arrayOfInt2[0] = j;
      for (byte b = 0; b < k; b++) {
        long l1 = paramArrayOfLong[b2++];
        assert l1 >>> 32 == j;
        arrayOfInt2[true + b] = (int)l1;
      } 
      arrayOfInt1[i] = arrayOfInt2;
    } 
    assert b2 == paramArrayOfLong.length;
    return arrayOfInt1;
  }
  
  private static int[][] makeTable(long[] paramArrayOfLong) {
    int[][] arrayOfInt = new int[2][paramArrayOfLong.length];
    for (byte b = 0; b < paramArrayOfLong.length; b++) {
      arrayOfInt[0][b] = (int)paramArrayOfLong[b];
      arrayOfInt[1][b] = (int)(paramArrayOfLong[b] >>> 32);
    } 
    return arrayOfInt;
  }
  
  private static long[] computeHistogram2Col(int[] paramArrayOfInt) {
    switch (paramArrayOfInt.length) {
      case 0:
        return new long[0];
      case 1:
        return new long[] { 0x100000000L | 0xFFFFFFFFL & paramArrayOfInt[0] };
    } 
    long[] arrayOfLong = null;
    boolean bool = true;
    while (true) {
      byte b = -1;
      int i = paramArrayOfInt[0] ^ 0xFFFFFFFF;
      byte b1 = 0;
      for (byte b2 = 0; b2 <= paramArrayOfInt.length; b2++) {
        int j;
        if (b2 < paramArrayOfInt.length) {
          j = paramArrayOfInt[b2];
        } else {
          j = i ^ 0xFFFFFFFF;
        } 
        if (j == i) {
          b1++;
        } else {
          if (!bool && b1 != 0)
            arrayOfLong[b] = b1 << 32 | 0xFFFFFFFFL & i; 
          i = j;
          b1 = 1;
          b++;
        } 
      } 
      if (bool) {
        arrayOfLong = new long[b];
        bool = false;
        continue;
      } 
      break;
    } 
    return arrayOfLong;
  }
  
  private static int[][] regroupHistogram(int[][] paramArrayOfInt, int[] paramArrayOfInt1) {
    long l1 = 0L;
    for (byte b1 = 0; b1 < paramArrayOfInt.length; b1++)
      l1 += (paramArrayOfInt[b1].length - 1); 
    long l2 = 0L;
    int i;
    for (i = 0; i < paramArrayOfInt1.length; i++)
      l2 += paramArrayOfInt1[i]; 
    if (l2 > l1) {
      i = paramArrayOfInt1.length;
      long l = l1;
      for (byte b = 0; b < paramArrayOfInt1.length; b++) {
        if (l < paramArrayOfInt1[b]) {
          int[] arrayOfInt1 = new int[b + true];
          System.arraycopy(paramArrayOfInt1, 0, arrayOfInt1, 0, b + true);
          paramArrayOfInt1 = arrayOfInt1;
          paramArrayOfInt1[b] = (int)l;
          l = 0L;
          break;
        } 
        l -= paramArrayOfInt1[b];
      } 
    } else {
      long l = l1 - l2;
      int[] arrayOfInt1 = new int[paramArrayOfInt1.length + 1];
      System.arraycopy(paramArrayOfInt1, 0, arrayOfInt1, 0, paramArrayOfInt1.length);
      arrayOfInt1[paramArrayOfInt1.length] = (int)l;
      paramArrayOfInt1 = arrayOfInt1;
    } 
    int[][] arrayOfInt = new int[paramArrayOfInt1.length][];
    byte b2 = 0;
    int j = 1;
    int k = paramArrayOfInt[b2].length;
    for (byte b3 = 0; b3 < paramArrayOfInt1.length; b3++) {
      int m = paramArrayOfInt1[b3];
      int[] arrayOfInt1 = new int[1 + m];
      long l = 0L;
      arrayOfInt[b3] = arrayOfInt1;
      int n;
      for (n = 1; n < arrayOfInt1.length; n += i1) {
        int i1 = arrayOfInt1.length - n;
        while (j == k) {
          j = 1;
          k = paramArrayOfInt[++b2].length;
        } 
        if (i1 > k - j)
          i1 = k - j; 
        l += paramArrayOfInt[b2][0] * i1;
        System.arraycopy(paramArrayOfInt[b2], k - i1, arrayOfInt1, n, i1);
        k -= i1;
      } 
      Arrays.sort(arrayOfInt1, 1, arrayOfInt1.length);
      arrayOfInt1[0] = (int)((l + (m / 2)) / m);
    } 
    assert j == k;
    assert b2 == paramArrayOfInt.length - 1;
    return arrayOfInt;
  }
  
  public static Histogram makeByteHistogram(InputStream paramInputStream) throws IOException {
    byte[] arrayOfByte = new byte[4096];
    int[] arrayOfInt = new int[256];
    int i;
    while ((i = paramInputStream.read(arrayOfByte)) > 0) {
      for (byte b1 = 0; b1 < i; b1++)
        arrayOfInt[arrayOfByte[b1] & 0xFF] = arrayOfInt[arrayOfByte[b1] & 0xFF] + 1; 
    } 
    int[][] arrayOfInt1 = new int[256][2];
    for (byte b = 0; b < arrayOfInt.length; b++) {
      arrayOfInt1[b][0] = arrayOfInt[b];
      arrayOfInt1[b][1] = b;
    } 
    return new Histogram(arrayOfInt1);
  }
  
  private static int[] sortedSlice(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    if (paramInt1 == 0 && paramInt2 == paramArrayOfInt.length && isSorted(paramArrayOfInt, 0, false))
      return paramArrayOfInt; 
    int[] arrayOfInt = new int[paramInt2 - paramInt1];
    System.arraycopy(paramArrayOfInt, paramInt1, arrayOfInt, 0, arrayOfInt.length);
    Arrays.sort(arrayOfInt);
    return arrayOfInt;
  }
  
  private static boolean isSorted(int[] paramArrayOfInt, int paramInt, boolean paramBoolean) {
    for (int i = paramInt + 1; i < paramArrayOfInt.length; i++) {
      if (paramBoolean ? (paramArrayOfInt[i - 1] >= paramArrayOfInt[i]) : (paramArrayOfInt[i - 1] > paramArrayOfInt[i]))
        return false; 
    } 
    return true;
  }
  
  private static int[] maybeSort(int[] paramArrayOfInt) {
    if (!isSorted(paramArrayOfInt, 0, false)) {
      paramArrayOfInt = (int[])paramArrayOfInt.clone();
      Arrays.sort(paramArrayOfInt);
    } 
    return paramArrayOfInt;
  }
  
  private boolean assertWellFormed(int[] paramArrayOfInt) { return true; }
  
  public static interface BitMetric {
    double getBitLength(int param1Int);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jav\\util\jar\pack\Histogram.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */