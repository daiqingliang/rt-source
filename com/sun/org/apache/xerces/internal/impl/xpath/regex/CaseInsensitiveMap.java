package com.sun.org.apache.xerces.internal.impl.xpath.regex;

public class CaseInsensitiveMap {
  private static int CHUNK_SHIFT = 10;
  
  private static int CHUNK_SIZE = 1 << CHUNK_SHIFT;
  
  private static int CHUNK_MASK = CHUNK_SIZE - 1;
  
  private static int INITIAL_CHUNK_COUNT = 64;
  
  private static int[][][] caseInsensitiveMap;
  
  private static Boolean mapBuilt = Boolean.FALSE;
  
  private static int LOWER_CASE_MATCH = 1;
  
  private static int UPPER_CASE_MATCH = 2;
  
  public static int[] get(int paramInt) {
    if (mapBuilt == Boolean.FALSE)
      synchronized (mapBuilt) {
        if (mapBuilt == Boolean.FALSE)
          buildCaseInsensitiveMap(); 
      }  
    return (paramInt < 65536) ? getMapping(paramInt) : null;
  }
  
  private static int[] getMapping(int paramInt) {
    int i = paramInt >>> CHUNK_SHIFT;
    int j = paramInt & CHUNK_MASK;
    return caseInsensitiveMap[i][j];
  }
  
  private static void buildCaseInsensitiveMap() {
    caseInsensitiveMap = new int[INITIAL_CHUNK_COUNT][][];
    int i;
    for (i = 0; i < INITIAL_CHUNK_COUNT; i++)
      caseInsensitiveMap[i] = new int[CHUNK_SIZE][]; 
    for (byte b = 0; b < 65536; b++) {
      i = Character.toLowerCase(b);
      int j = Character.toUpperCase(b);
      if (i != j || i != b) {
        int[] arrayOfInt = new int[2];
        byte b1 = 0;
        if (i != b) {
          arrayOfInt[b1++] = i;
          arrayOfInt[b1++] = LOWER_CASE_MATCH;
          int[] arrayOfInt1 = getMapping(i);
          if (arrayOfInt1 != null)
            arrayOfInt = updateMap(b, arrayOfInt, i, arrayOfInt1, LOWER_CASE_MATCH); 
        } 
        if (j != b) {
          if (b1 == arrayOfInt.length)
            arrayOfInt = expandMap(arrayOfInt, 2); 
          arrayOfInt[b1++] = j;
          arrayOfInt[b1++] = UPPER_CASE_MATCH;
          int[] arrayOfInt1 = getMapping(j);
          if (arrayOfInt1 != null)
            arrayOfInt = updateMap(b, arrayOfInt, j, arrayOfInt1, UPPER_CASE_MATCH); 
        } 
        set(b, arrayOfInt);
      } 
    } 
    mapBuilt = Boolean.TRUE;
  }
  
  private static int[] expandMap(int[] paramArrayOfInt, int paramInt) {
    int i = paramArrayOfInt.length;
    int[] arrayOfInt = new int[i + paramInt];
    System.arraycopy(paramArrayOfInt, 0, arrayOfInt, 0, i);
    return arrayOfInt;
  }
  
  private static void set(int paramInt, int[] paramArrayOfInt) {
    int i = paramInt >>> CHUNK_SHIFT;
    int j = paramInt & CHUNK_MASK;
    caseInsensitiveMap[i][j] = paramArrayOfInt;
  }
  
  private static int[] updateMap(int paramInt1, int[] paramArrayOfInt1, int paramInt2, int[] paramArrayOfInt2, int paramInt3) {
    for (boolean bool = false; bool < paramArrayOfInt2.length; bool += true) {
      int i = paramArrayOfInt2[bool];
      int[] arrayOfInt = getMapping(i);
      if (arrayOfInt != null && contains(arrayOfInt, paramInt2, paramInt3)) {
        if (!contains(arrayOfInt, paramInt1)) {
          arrayOfInt = expandAndAdd(arrayOfInt, paramInt1, paramInt3);
          set(i, arrayOfInt);
        } 
        if (!contains(paramArrayOfInt1, i))
          paramArrayOfInt1 = expandAndAdd(paramArrayOfInt1, i, paramInt3); 
      } 
    } 
    if (!contains(paramArrayOfInt2, paramInt1)) {
      paramArrayOfInt2 = expandAndAdd(paramArrayOfInt2, paramInt1, paramInt3);
      set(paramInt2, paramArrayOfInt2);
    } 
    return paramArrayOfInt1;
  }
  
  private static boolean contains(int[] paramArrayOfInt, int paramInt) {
    for (boolean bool = false; bool < paramArrayOfInt.length; bool += true) {
      if (paramArrayOfInt[bool] == paramInt)
        return true; 
    } 
    return false;
  }
  
  private static boolean contains(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    for (boolean bool = false; bool < paramArrayOfInt.length; bool += true) {
      if (paramArrayOfInt[bool] == paramInt1 && paramArrayOfInt[bool + true] == paramInt2)
        return true; 
    } 
    return false;
  }
  
  private static int[] expandAndAdd(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    int i = paramArrayOfInt.length;
    int[] arrayOfInt = new int[i + 2];
    System.arraycopy(paramArrayOfInt, 0, arrayOfInt, 0, i);
    arrayOfInt[i] = paramInt1;
    arrayOfInt[i + 1] = paramInt2;
    return arrayOfInt;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xpath\regex\CaseInsensitiveMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */