package sun.font;

import java.text.Bidi;

public final class BidiUtils {
  static final char NUMLEVELS = '>';
  
  public static void getLevels(Bidi paramBidi, byte[] paramArrayOfByte, int paramInt) {
    int i = paramInt + paramBidi.getLength();
    if (paramInt < 0 || i > paramArrayOfByte.length)
      throw new IndexOutOfBoundsException("levels.length = " + paramArrayOfByte.length + " start: " + paramInt + " limit: " + i); 
    int j = paramBidi.getRunCount();
    int k = paramInt;
    for (byte b = 0; b < j; b++) {
      int m = paramInt + paramBidi.getRunLimit(b);
      byte b1 = (byte)paramBidi.getRunLevel(b);
      while (k < m)
        paramArrayOfByte[k++] = b1; 
    } 
  }
  
  public static byte[] getLevels(Bidi paramBidi) {
    byte[] arrayOfByte = new byte[paramBidi.getLength()];
    getLevels(paramBidi, arrayOfByte, 0);
    return arrayOfByte;
  }
  
  public static int[] createVisualToLogicalMap(byte[] paramArrayOfByte) {
    int i = paramArrayOfByte.length;
    int[] arrayOfInt = new int[i];
    byte b1 = 63;
    byte b2 = 0;
    byte b;
    for (b = 0; b < i; b++) {
      arrayOfInt[b] = b;
      byte b3 = paramArrayOfByte[b];
      if (b3 > b2)
        b2 = b3; 
      if ((b3 & true) != 0 && b3 < b1)
        b1 = b3; 
    } 
    while (b2 >= b1) {
      b = 0;
      while (true) {
        if (b < i && paramArrayOfByte[b] < b2) {
          b++;
          continue;
        } 
        byte b3 = b++;
        if (b3 == paramArrayOfByte.length)
          break; 
        while (b < i && paramArrayOfByte[b] >= b2)
          b++; 
        for (byte b4 = b - 1; b3 < b4; b4--) {
          int j = arrayOfInt[b3];
          arrayOfInt[b3] = arrayOfInt[b4];
          arrayOfInt[b4] = j;
          b3++;
        } 
      } 
      b2 = (byte)(b2 - 1);
    } 
    return arrayOfInt;
  }
  
  public static int[] createInverseMap(int[] paramArrayOfInt) {
    if (paramArrayOfInt == null)
      return null; 
    int[] arrayOfInt = new int[paramArrayOfInt.length];
    for (byte b = 0; b < paramArrayOfInt.length; b++)
      arrayOfInt[paramArrayOfInt[b]] = b; 
    return arrayOfInt;
  }
  
  public static int[] createContiguousOrder(int[] paramArrayOfInt) { return (paramArrayOfInt != null) ? computeContiguousOrder(paramArrayOfInt, 0, paramArrayOfInt.length) : null; }
  
  private static int[] computeContiguousOrder(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    int[] arrayOfInt = new int[paramInt2 - paramInt1];
    int i;
    for (i = 0; i < arrayOfInt.length; i++)
      arrayOfInt[i] = i + paramInt1; 
    for (i = 0; i < arrayOfInt.length - 1; i++) {
      int j = i;
      int k = paramArrayOfInt[arrayOfInt[j]];
      int m;
      for (m = i; m < arrayOfInt.length; m++) {
        if (paramArrayOfInt[arrayOfInt[m]] < k) {
          j = m;
          k = paramArrayOfInt[arrayOfInt[j]];
        } 
      } 
      m = arrayOfInt[i];
      arrayOfInt[i] = arrayOfInt[j];
      arrayOfInt[j] = m;
    } 
    if (paramInt1 != 0)
      for (i = 0; i < arrayOfInt.length; i++)
        arrayOfInt[i] = arrayOfInt[i] - paramInt1;  
    for (i = 0; i < arrayOfInt.length && arrayOfInt[i] == i; i++);
    return (i == arrayOfInt.length) ? null : createInverseMap(arrayOfInt);
  }
  
  public static int[] createNormalizedMap(int[] paramArrayOfInt, byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (paramArrayOfInt != null) {
      if (paramInt1 != 0 || paramInt2 != paramArrayOfInt.length) {
        boolean bool3;
        boolean bool2;
        boolean bool1;
        if (paramArrayOfByte == null) {
          bool3 = false;
          bool1 = true;
          bool2 = true;
        } else if (paramArrayOfByte[paramInt1] == paramArrayOfByte[paramInt2 - 1]) {
          bool3 = paramArrayOfByte[paramInt1];
          bool2 = ((bool3 & true) == 0) ? 1 : 0;
          int i;
          for (i = paramInt1; i < paramInt2 && paramArrayOfByte[i] >= bool3; i++) {
            if (bool2)
              bool2 = (paramArrayOfByte[i] == bool3) ? 1 : 0; 
          } 
          bool1 = (i == paramInt2) ? 1 : 0;
        } else {
          bool1 = false;
          bool3 = false;
          bool2 = false;
        } 
        if (bool1) {
          int i;
          if (bool2)
            return null; 
          int[] arrayOfInt = new int[paramInt2 - paramInt1];
          if (bool3 & true) {
            i = paramArrayOfInt[paramInt2 - 1];
          } else {
            i = paramArrayOfInt[paramInt1];
          } 
          if (i == 0) {
            System.arraycopy(paramArrayOfInt, paramInt1, arrayOfInt, 0, paramInt2 - paramInt1);
          } else {
            for (int j = 0; j < arrayOfInt.length; j++)
              arrayOfInt[j] = paramArrayOfInt[j + paramInt1] - i; 
          } 
          return arrayOfInt;
        } 
        return computeContiguousOrder(paramArrayOfInt, paramInt1, paramInt2);
      } 
      return paramArrayOfInt;
    } 
    return null;
  }
  
  public static void reorderVisually(byte[] paramArrayOfByte, Object[] paramArrayOfObject) {
    int i = paramArrayOfByte.length;
    byte b1 = 63;
    byte b2 = 0;
    byte b;
    for (b = 0; b < i; b++) {
      byte b3 = paramArrayOfByte[b];
      if (b3 > b2)
        b2 = b3; 
      if ((b3 & true) != 0 && b3 < b1)
        b1 = b3; 
    } 
    while (b2 >= b1) {
      b = 0;
      while (true) {
        if (b < i && paramArrayOfByte[b] < b2) {
          b++;
          continue;
        } 
        byte b3 = b++;
        if (b3 == paramArrayOfByte.length)
          break; 
        while (b < i && paramArrayOfByte[b] >= b2)
          b++; 
        for (byte b4 = b - 1; b3 < b4; b4--) {
          Object object = paramArrayOfObject[b3];
          paramArrayOfObject[b3] = paramArrayOfObject[b4];
          paramArrayOfObject[b4] = object;
          b3++;
        } 
      } 
      b2 = (byte)(b2 - 1);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\BidiUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */