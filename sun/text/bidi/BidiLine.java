package sun.text.bidi;

import java.text.Bidi;
import java.util.Arrays;

public final class BidiLine {
  static void setTrailingWSStart(BidiBase paramBidiBase) {
    byte[] arrayOfByte1 = paramBidiBase.dirProps;
    byte[] arrayOfByte2 = paramBidiBase.levels;
    int i = paramBidiBase.length;
    byte b = paramBidiBase.paraLevel;
    if (BidiBase.NoContextRTL(arrayOfByte1[i - 1]) == 7) {
      paramBidiBase.trailingWSStart = i;
      return;
    } 
    while (i > 0 && (BidiBase.DirPropFlagNC(arrayOfByte1[i - 1]) & BidiBase.MASK_WS) != 0)
      i--; 
    while (i > 0 && arrayOfByte2[i - 1] == b)
      i--; 
    paramBidiBase.trailingWSStart = i;
  }
  
  public static Bidi setLine(Bidi paramBidi1, BidiBase paramBidiBase1, Bidi paramBidi2, BidiBase paramBidiBase2, int paramInt1, int paramInt2) {
    BidiBase bidiBase = paramBidiBase2;
    int i = bidiBase.length = bidiBase.originalLength = bidiBase.resultLength = paramInt2 - paramInt1;
    bidiBase.text = new char[i];
    System.arraycopy(paramBidiBase1.text, paramInt1, bidiBase.text, 0, i);
    bidiBase.paraLevel = paramBidiBase1.GetParaLevelAt(paramInt1);
    bidiBase.paraCount = paramBidiBase1.paraCount;
    bidiBase.runs = new BidiRun[0];
    if (paramBidiBase1.controlCount > 0) {
      for (int j = paramInt1; j < paramInt2; j++) {
        if (BidiBase.IsBidiControlChar(paramBidiBase1.text[j]))
          bidiBase.controlCount++; 
      } 
      bidiBase.resultLength -= bidiBase.controlCount;
    } 
    bidiBase.getDirPropsMemory(i);
    bidiBase.dirProps = bidiBase.dirPropsMemory;
    System.arraycopy(paramBidiBase1.dirProps, paramInt1, bidiBase.dirProps, 0, i);
    bidiBase.getLevelsMemory(i);
    bidiBase.levels = bidiBase.levelsMemory;
    System.arraycopy(paramBidiBase1.levels, paramInt1, bidiBase.levels, 0, i);
    bidiBase.runCount = -1;
    if (paramBidiBase1.direction != 2) {
      bidiBase.direction = paramBidiBase1.direction;
      if (paramBidiBase1.trailingWSStart <= paramInt1) {
        bidiBase.trailingWSStart = 0;
      } else if (paramBidiBase1.trailingWSStart < paramInt2) {
        paramBidiBase1.trailingWSStart -= paramInt1;
      } else {
        bidiBase.trailingWSStart = i;
      } 
    } else {
      byte[] arrayOfByte = bidiBase.levels;
      setTrailingWSStart(bidiBase);
      int j = bidiBase.trailingWSStart;
      if (j == 0) {
        bidiBase.direction = (byte)(bidiBase.paraLevel & true);
      } else {
        byte b = (byte)(arrayOfByte[0] & true);
        if (j < i && (bidiBase.paraLevel & true) != b) {
          bidiBase.direction = 2;
        } else {
          for (byte b1 = 1;; b1++) {
            if (b1 == j) {
              bidiBase.direction = b;
              break;
            } 
            if ((arrayOfByte[b1] & true) != b) {
              bidiBase.direction = 2;
              break;
            } 
          } 
        } 
      } 
      switch (bidiBase.direction) {
        case 0:
          bidiBase.paraLevel = (byte)(bidiBase.paraLevel + 1 & 0xFFFFFFFE);
          bidiBase.trailingWSStart = 0;
          break;
        case 1:
          bidiBase.paraLevel = (byte)(bidiBase.paraLevel | true);
          bidiBase.trailingWSStart = 0;
          break;
      } 
    } 
    paramBidiBase2.paraBidi = paramBidiBase1;
    return paramBidi2;
  }
  
  static byte getLevelAt(BidiBase paramBidiBase, int paramInt) { return (paramBidiBase.direction != 2 || paramInt >= paramBidiBase.trailingWSStart) ? paramBidiBase.GetParaLevelAt(paramInt) : paramBidiBase.levels[paramInt]; }
  
  static byte[] getLevels(BidiBase paramBidiBase) {
    int i = paramBidiBase.trailingWSStart;
    int j = paramBidiBase.length;
    if (i != j) {
      Arrays.fill(paramBidiBase.levels, i, j, paramBidiBase.paraLevel);
      paramBidiBase.trailingWSStart = j;
    } 
    if (j < paramBidiBase.levels.length) {
      byte[] arrayOfByte = new byte[j];
      System.arraycopy(paramBidiBase.levels, 0, arrayOfByte, 0, j);
      return arrayOfByte;
    } 
    return paramBidiBase.levels;
  }
  
  static BidiRun getLogicalRun(BidiBase paramBidiBase, int paramInt) {
    BidiRun bidiRun1 = new BidiRun();
    getRuns(paramBidiBase);
    int i = paramBidiBase.runCount;
    int j = 0;
    int k = 0;
    BidiRun bidiRun2 = paramBidiBase.runs[0];
    for (byte b = 0; b < i; b++) {
      bidiRun2 = paramBidiBase.runs[b];
      k = bidiRun2.start + bidiRun2.limit - j;
      if (paramInt >= bidiRun2.start && paramInt < k)
        break; 
      j = bidiRun2.limit;
    } 
    bidiRun1.start = bidiRun2.start;
    bidiRun1.limit = k;
    bidiRun1.level = bidiRun2.level;
    return bidiRun1;
  }
  
  private static void getSingleRun(BidiBase paramBidiBase, byte paramByte) {
    paramBidiBase.runs = paramBidiBase.simpleRuns;
    paramBidiBase.runCount = 1;
    paramBidiBase.runs[0] = new BidiRun(0, paramBidiBase.length, paramByte);
  }
  
  private static void reorderLine(BidiBase paramBidiBase, byte paramByte1, byte paramByte2) {
    if (paramByte2 <= (paramByte1 | true))
      return; 
    paramByte1 = (byte)(paramByte1 + 1);
    BidiRun[] arrayOfBidiRun = paramBidiBase.runs;
    byte[] arrayOfByte = paramBidiBase.levels;
    int i = paramBidiBase.runCount;
    if (paramBidiBase.trailingWSStart < paramBidiBase.length)
      i--; 
    label41: while (true) {
      paramByte2 = (byte)(paramByte2 - 1);
      if (paramByte2 >= paramByte1) {
        byte b;
        for (b = 0;; b = b2 + 1) {
          if (b < i && arrayOfByte[(arrayOfBidiRun[b]).start] < paramByte2) {
            b++;
            continue;
          } 
          if (b >= i)
            continue label41; 
          byte b2 = b;
          while (++b2 < i && arrayOfByte[(arrayOfBidiRun[b2]).start] >= paramByte2);
          for (byte b1 = b2 - 1; b < b1; b1--) {
            BidiRun bidiRun = arrayOfBidiRun[b];
            arrayOfBidiRun[b] = arrayOfBidiRun[b1];
            arrayOfBidiRun[b1] = bidiRun;
            b++;
          } 
          if (b2 == i)
            continue label41; 
        } 
      } 
      break;
    } 
    if ((paramByte1 & true) == 0) {
      byte b = 0;
      if (paramBidiBase.trailingWSStart == paramBidiBase.length)
        i--; 
      while (b < i) {
        BidiRun bidiRun = arrayOfBidiRun[b];
        arrayOfBidiRun[b] = arrayOfBidiRun[i];
        arrayOfBidiRun[i] = bidiRun;
        b++;
        i--;
      } 
    } 
  }
  
  static int getRunFromLogicalIndex(BidiBase paramBidiBase, int paramInt) {
    BidiRun[] arrayOfBidiRun = paramBidiBase.runs;
    int i = paramBidiBase.runCount;
    int j = 0;
    for (byte b = 0; b < i; b++) {
      int k = (arrayOfBidiRun[b]).limit - j;
      int m = (arrayOfBidiRun[b]).start;
      if (paramInt >= m && paramInt < m + k)
        return b; 
      j += k;
    } 
    throw new IllegalStateException("Internal ICU error in getRunFromLogicalIndex");
  }
  
  static void getRuns(BidiBase paramBidiBase) {
    if (paramBidiBase.runCount >= 0)
      return; 
    if (paramBidiBase.direction != 2) {
      getSingleRun(paramBidiBase, paramBidiBase.paraLevel);
    } else {
      int i = paramBidiBase.length;
      byte[] arrayOfByte = paramBidiBase.levels;
      byte b = 126;
      int j = paramBidiBase.trailingWSStart;
      byte b2 = 0;
      byte b1;
      for (b1 = 0; b1 < j; b1++) {
        if (arrayOfByte[b1] != b) {
          b2++;
          b = arrayOfByte[b1];
        } 
      } 
      if (b2 == 1 && j == i) {
        getSingleRun(paramBidiBase, arrayOfByte[0]);
      } else {
        byte b4 = 62;
        byte b5 = 0;
        if (j < i)
          b2++; 
        paramBidiBase.getRunsMemory(b2);
        BidiRun[] arrayOfBidiRun = paramBidiBase.runsMemory;
        byte b3 = 0;
        b1 = 0;
        do {
          byte b6 = b1;
          b = arrayOfByte[b1];
          if (b < b4)
            b4 = b; 
          if (b > b5)
            b5 = b; 
          while (++b1 < j && arrayOfByte[b1] == b);
          arrayOfBidiRun[b3] = new BidiRun(b6, b1 - b6, b);
          b3++;
        } while (b1 < j);
        if (j < i) {
          arrayOfBidiRun[b3] = new BidiRun(j, i - j, paramBidiBase.paraLevel);
          if (paramBidiBase.paraLevel < b4)
            b4 = paramBidiBase.paraLevel; 
        } 
        paramBidiBase.runs = arrayOfBidiRun;
        paramBidiBase.runCount = b2;
        reorderLine(paramBidiBase, b4, b5);
        j = 0;
        for (b1 = 0; b1 < b2; b1++) {
          (arrayOfBidiRun[b1]).level = arrayOfByte[(arrayOfBidiRun[b1]).start];
          j = (arrayOfBidiRun[b1]).limit += j;
        } 
        if (b3 < b2) {
          boolean bool = ((paramBidiBase.paraLevel & true) != 0) ? 0 : b3;
          (arrayOfBidiRun[bool]).level = paramBidiBase.paraLevel;
        } 
      } 
    } 
    if (paramBidiBase.insertPoints.size > 0)
      for (byte b = 0; b < paramBidiBase.insertPoints.size; b++) {
        BidiBase.Point point = paramBidiBase.insertPoints.points[b];
        int i = getRunFromLogicalIndex(paramBidiBase, point.pos);
        (paramBidiBase.runs[i]).insertRemove |= point.flag;
      }  
    if (paramBidiBase.controlCount > 0)
      for (byte b = 0; b < paramBidiBase.length; b++) {
        char c = paramBidiBase.text[b];
        if (BidiBase.IsBidiControlChar(c)) {
          int i = getRunFromLogicalIndex(paramBidiBase, b);
          (paramBidiBase.runs[i]).insertRemove--;
        } 
      }  
  }
  
  static int[] prepareReorder(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3) {
    if (paramArrayOfByte1 == null || paramArrayOfByte1.length <= 0)
      return null; 
    byte b1 = 62;
    byte b2 = 0;
    int i = paramArrayOfByte1.length;
    while (i > 0) {
      byte b = paramArrayOfByte1[--i];
      if (b > 62)
        return null; 
      if (b < b1)
        b1 = b; 
      if (b > b2)
        b2 = b; 
    } 
    paramArrayOfByte2[0] = b1;
    paramArrayOfByte3[0] = b2;
    int[] arrayOfInt = new int[paramArrayOfByte1.length];
    i = paramArrayOfByte1.length;
    while (i > 0)
      arrayOfInt[--i] = i; 
    return arrayOfInt;
  }
  
  static int[] reorderVisual(byte[] paramArrayOfByte) {
    byte[] arrayOfByte1 = new byte[1];
    byte[] arrayOfByte2 = new byte[1];
    int[] arrayOfInt = prepareReorder(paramArrayOfByte, arrayOfByte1, arrayOfByte2);
    if (arrayOfInt == null)
      return null; 
    byte b1 = arrayOfByte1[0];
    byte b2 = arrayOfByte2[0];
    if (b1 == b2 && (b1 & true) == 0)
      return arrayOfInt; 
    b1 = (byte)(b1 | true);
    do {
      for (byte b = 0;; b = b4 + 1) {
        if (b < paramArrayOfByte.length && paramArrayOfByte[b] < b2) {
          b++;
          continue;
        } 
        if (b >= paramArrayOfByte.length)
          break; 
        byte b4 = b;
        while (++b4 < paramArrayOfByte.length && paramArrayOfByte[b4] >= b2);
        for (byte b3 = b4 - 1; b < b3; b3--) {
          int i = arrayOfInt[b];
          arrayOfInt[b] = arrayOfInt[b3];
          arrayOfInt[b3] = i;
          b++;
        } 
        if (b4 == paramArrayOfByte.length)
          break; 
      } 
      b2 = (byte)(b2 - 1);
    } while (b2 >= b1);
    return arrayOfInt;
  }
  
  static int[] getVisualMap(BidiBase paramBidiBase) {
    BidiRun[] arrayOfBidiRun = paramBidiBase.runs;
    int j = (paramBidiBase.length > paramBidiBase.resultLength) ? paramBidiBase.length : paramBidiBase.resultLength;
    int[] arrayOfInt1 = new int[j];
    int i = 0;
    byte b = 0;
    int k;
    for (k = 0; k < paramBidiBase.runCount; k++) {
      int m = (arrayOfBidiRun[k]).start;
      int n = (arrayOfBidiRun[k]).limit;
      if (arrayOfBidiRun[k].isEvenRun()) {
        do {
          arrayOfInt1[b++] = m++;
        } while (++i < n);
      } else {
        m += n - i;
        do {
          arrayOfInt1[b++] = --m;
        } while (++i < n);
      } 
    } 
    if (paramBidiBase.insertPoints.size > 0) {
      k = 0;
      int m = paramBidiBase.runCount;
      arrayOfBidiRun = paramBidiBase.runs;
      int n;
      for (n = 0; n < m; n++) {
        int i2 = (arrayOfBidiRun[n]).insertRemove;
        if ((i2 & 0x5) > 0)
          k++; 
        if ((i2 & 0xA) > 0)
          k++; 
      } 
      int i1 = paramBidiBase.resultLength;
      for (n = m - 1; n >= 0 && k > 0; n--) {
        int i2 = (arrayOfBidiRun[n]).insertRemove;
        if ((i2 & 0xA) > 0) {
          arrayOfInt1[--i1] = -1;
          k--;
        } 
        i = (n > 0) ? (arrayOfBidiRun[n - 1]).limit : 0;
        for (int i3 = (arrayOfBidiRun[n]).limit - 1; i3 >= i && k > 0; i3--)
          arrayOfInt1[--i1] = arrayOfInt1[i3]; 
        if ((i2 & 0x5) > 0) {
          arrayOfInt1[--i1] = -1;
          k--;
        } 
      } 
    } else if (paramBidiBase.controlCount > 0) {
      k = paramBidiBase.runCount;
      arrayOfBidiRun = paramBidiBase.runs;
      i = 0;
      int m = 0;
      byte b1 = 0;
      while (b1 < k) {
        int i1 = (arrayOfBidiRun[b1]).limit - i;
        int n = (arrayOfBidiRun[b1]).insertRemove;
        if (n == 0 && m == i) {
          m += i1;
        } else if (n == 0) {
          int i2 = (arrayOfBidiRun[b1]).limit;
          for (int i3 = i; i3 < i2; i3++)
            arrayOfInt1[m++] = arrayOfInt1[i3]; 
        } else {
          int i2 = (arrayOfBidiRun[b1]).start;
          boolean bool = arrayOfBidiRun[b1].isEvenRun();
          int i3 = i2 + i1 - 1;
          for (int i4 = 0; i4 < i1; i4++) {
            int i5 = bool ? (i2 + i4) : (i3 - i4);
            char c = paramBidiBase.text[i5];
            if (!BidiBase.IsBidiControlChar(c))
              arrayOfInt1[m++] = i5; 
          } 
        } 
        b1++;
        i += i1;
      } 
    } 
    if (j == paramBidiBase.resultLength)
      return arrayOfInt1; 
    int[] arrayOfInt2 = new int[paramBidiBase.resultLength];
    System.arraycopy(arrayOfInt1, 0, arrayOfInt2, 0, paramBidiBase.resultLength);
    return arrayOfInt2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\text\bidi\BidiLine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */