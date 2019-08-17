package java.util;

final class DualPivotQuicksort {
  private static final int MAX_RUN_COUNT = 67;
  
  private static final int MAX_RUN_LENGTH = 33;
  
  private static final int QUICKSORT_THRESHOLD = 286;
  
  private static final int INSERTION_SORT_THRESHOLD = 47;
  
  private static final int COUNTING_SORT_THRESHOLD_FOR_BYTE = 29;
  
  private static final int COUNTING_SORT_THRESHOLD_FOR_SHORT_OR_CHAR = 3200;
  
  private static final int NUM_SHORT_VALUES = 65536;
  
  private static final int NUM_CHAR_VALUES = 65536;
  
  private static final int NUM_BYTE_VALUES = 256;
  
  static void sort(int[] paramArrayOfInt1, int paramInt1, int paramInt2, int[] paramArrayOfInt2, int paramInt3, int paramInt4) {
    int k;
    int j;
    int[] arrayOfInt2;
    if (paramInt2 - paramInt1 < 286) {
      sort(paramArrayOfInt1, paramInt1, paramInt2, true);
      return;
    } 
    int[] arrayOfInt1 = new int[68];
    byte b = 0;
    arrayOfInt1[0] = paramInt1;
    int i = paramInt1;
    while (i < paramInt2) {
      if (paramArrayOfInt1[i] < paramArrayOfInt1[i + 1]) {
        while (++i <= paramInt2 && paramArrayOfInt1[i - 1] <= paramArrayOfInt1[i]);
      } else if (paramArrayOfInt1[i] > paramArrayOfInt1[i + 1]) {
        while (++i <= paramInt2 && paramArrayOfInt1[i - 1] >= paramArrayOfInt1[i]);
        int n = arrayOfInt1[b] - 1;
        j = i;
        while (++n < --j) {
          k = paramArrayOfInt1[n];
          paramArrayOfInt1[n] = paramArrayOfInt1[j];
          paramArrayOfInt1[j] = k;
        } 
      } else {
        byte b1 = 33;
        while (++i <= paramInt2 && paramArrayOfInt1[i - 1] == paramArrayOfInt1[i]) {
          if (--b1 == 0) {
            sort(paramArrayOfInt1, paramInt1, paramInt2, true);
            return;
          } 
        } 
      } 
      if (++b == 67) {
        sort(paramArrayOfInt1, paramInt1, paramInt2, true);
        return;
      } 
      arrayOfInt1[b] = i;
    } 
    if (arrayOfInt1[b] == paramInt2++) {
      arrayOfInt1[++b] = paramInt2;
    } else if (b == 1) {
      return;
    } 
    i = 0;
    boolean bool = true;
    while (bool <<= true < b)
      i = (byte)(i ^ true); 
    int m = paramInt2 - paramInt1;
    if (paramArrayOfInt2 == null || paramInt4 < m || paramInt3 + m > paramArrayOfInt2.length) {
      paramArrayOfInt2 = new int[m];
      paramInt3 = 0;
    } 
    if (i == 0) {
      System.arraycopy(paramArrayOfInt1, paramInt1, paramArrayOfInt2, paramInt3, m);
      arrayOfInt2 = paramArrayOfInt1;
      k = 0;
      paramArrayOfInt1 = paramArrayOfInt2;
      j = paramInt3 - paramInt1;
    } else {
      arrayOfInt2 = paramArrayOfInt2;
      j = 0;
      k = paramInt3 - paramInt1;
    } 
    while (b > 1) {
      byte b1;
      int n;
      for (n = (b1 = 0) + 2; n <= b; n += 2) {
        int i2 = arrayOfInt1[n];
        int i3 = arrayOfInt1[n - 1];
        int i4 = arrayOfInt1[n - 2];
        int i5 = i4;
        int i6 = i3;
        while (i4 < i2) {
          if (i6 >= i2 || (i5 < i3 && paramArrayOfInt1[i5 + j] <= paramArrayOfInt1[i6 + j])) {
            arrayOfInt2[i4 + k] = paramArrayOfInt1[i5++ + j];
          } else {
            arrayOfInt2[i4 + k] = paramArrayOfInt1[i6++ + j];
          } 
          i4++;
        } 
        arrayOfInt1[++b1] = i2;
      } 
      if ((b & true) != 0) {
        n = paramInt2;
        int i2 = arrayOfInt1[b - 1];
        while (--n >= i2)
          arrayOfInt2[n + k] = paramArrayOfInt1[n + j]; 
        arrayOfInt1[++b1] = paramInt2;
      } 
      int[] arrayOfInt = paramArrayOfInt1;
      paramArrayOfInt1 = arrayOfInt2;
      arrayOfInt2 = arrayOfInt;
      int i1 = j;
      j = k;
      k = i1;
      b = b1;
    } 
  }
  
  private static void sort(int[] paramArrayOfInt, int paramInt1, int paramInt2, boolean paramBoolean) {
    int i = paramInt2 - paramInt1 + 1;
    if (i < 47) {
      if (paramBoolean) {
        int i5 = paramInt1;
        int i6;
        for (i6 = i5; i5 < paramInt2; i6 = ++i5) {
          int i7 = paramArrayOfInt[i5 + 1];
          while (i7 < paramArrayOfInt[i6]) {
            paramArrayOfInt[i6 + 1] = paramArrayOfInt[i6];
            if (i6-- == paramInt1)
              break; 
          } 
          paramArrayOfInt[i6 + 1] = i7;
        } 
      } else {
        do {
          if (paramInt1 >= paramInt2)
            return; 
        } while (paramArrayOfInt[++paramInt1] >= paramArrayOfInt[paramInt1 - 1]);
        int i5;
        for (i5 = paramInt1; ++paramInt1 <= paramInt2; i5 = ++paramInt1) {
          int i6 = paramArrayOfInt[i5];
          int i7 = paramArrayOfInt[paramInt1];
          if (i6 < i7) {
            i7 = i6;
            i6 = paramArrayOfInt[paramInt1];
          } 
          while (i6 < paramArrayOfInt[--i5])
            paramArrayOfInt[i5 + 2] = paramArrayOfInt[i5]; 
          paramArrayOfInt[++i5 + 1] = i6;
          while (i7 < paramArrayOfInt[--i5])
            paramArrayOfInt[i5 + 1] = paramArrayOfInt[i5]; 
          paramArrayOfInt[i5 + 1] = i7;
        } 
        i5 = paramArrayOfInt[paramInt2];
        while (i5 < paramArrayOfInt[--paramInt2])
          paramArrayOfInt[paramInt2 + 1] = paramArrayOfInt[paramInt2]; 
        paramArrayOfInt[paramInt2 + 1] = i5;
      } 
      return;
    } 
    int j = (i >> 3) + (i >> 6) + 1;
    int k = paramInt1 + paramInt2 >>> 1;
    int m = k - j;
    int n = m - j;
    int i1 = k + j;
    int i2 = i1 + j;
    if (paramArrayOfInt[m] < paramArrayOfInt[n]) {
      int i5 = paramArrayOfInt[m];
      paramArrayOfInt[m] = paramArrayOfInt[n];
      paramArrayOfInt[n] = i5;
    } 
    if (paramArrayOfInt[k] < paramArrayOfInt[m]) {
      int i5 = paramArrayOfInt[k];
      paramArrayOfInt[k] = paramArrayOfInt[m];
      paramArrayOfInt[m] = i5;
      if (i5 < paramArrayOfInt[n]) {
        paramArrayOfInt[m] = paramArrayOfInt[n];
        paramArrayOfInt[n] = i5;
      } 
    } 
    if (paramArrayOfInt[i1] < paramArrayOfInt[k]) {
      int i5 = paramArrayOfInt[i1];
      paramArrayOfInt[i1] = paramArrayOfInt[k];
      paramArrayOfInt[k] = i5;
      if (i5 < paramArrayOfInt[m]) {
        paramArrayOfInt[k] = paramArrayOfInt[m];
        paramArrayOfInt[m] = i5;
        if (i5 < paramArrayOfInt[n]) {
          paramArrayOfInt[m] = paramArrayOfInt[n];
          paramArrayOfInt[n] = i5;
        } 
      } 
    } 
    if (paramArrayOfInt[i2] < paramArrayOfInt[i1]) {
      int i5 = paramArrayOfInt[i2];
      paramArrayOfInt[i2] = paramArrayOfInt[i1];
      paramArrayOfInt[i1] = i5;
      if (i5 < paramArrayOfInt[k]) {
        paramArrayOfInt[i1] = paramArrayOfInt[k];
        paramArrayOfInt[k] = i5;
        if (i5 < paramArrayOfInt[m]) {
          paramArrayOfInt[k] = paramArrayOfInt[m];
          paramArrayOfInt[m] = i5;
          if (i5 < paramArrayOfInt[n]) {
            paramArrayOfInt[m] = paramArrayOfInt[n];
            paramArrayOfInt[n] = i5;
          } 
        } 
      } 
    } 
    int i3 = paramInt1;
    int i4 = paramInt2;
    if (paramArrayOfInt[n] != paramArrayOfInt[m] && paramArrayOfInt[m] != paramArrayOfInt[k] && paramArrayOfInt[k] != paramArrayOfInt[i1] && paramArrayOfInt[i1] != paramArrayOfInt[i2]) {
      int i5 = paramArrayOfInt[m];
      int i6 = paramArrayOfInt[i1];
      paramArrayOfInt[m] = paramArrayOfInt[paramInt1];
      paramArrayOfInt[i1] = paramArrayOfInt[paramInt2];
      while (paramArrayOfInt[++i3] < i5);
      while (paramArrayOfInt[--i4] > i6);
      int i7 = i3 - 1;
      label147: while (++i7 <= i4) {
        int i8 = paramArrayOfInt[i7];
        if (i8 < i5) {
          paramArrayOfInt[i7] = paramArrayOfInt[i3];
          paramArrayOfInt[i3] = i8;
          i3++;
          continue;
        } 
        if (i8 > i6) {
          while (paramArrayOfInt[i4] > i6) {
            if (i4-- == i7)
              break label147; 
          } 
          if (paramArrayOfInt[i4] < i5) {
            paramArrayOfInt[i7] = paramArrayOfInt[i3];
            paramArrayOfInt[i3] = paramArrayOfInt[i4];
            i3++;
          } else {
            paramArrayOfInt[i7] = paramArrayOfInt[i4];
          } 
          paramArrayOfInt[i4] = i8;
          i4--;
        } 
      } 
      paramArrayOfInt[paramInt1] = paramArrayOfInt[i3 - 1];
      paramArrayOfInt[i3 - 1] = i5;
      paramArrayOfInt[paramInt2] = paramArrayOfInt[i4 + 1];
      paramArrayOfInt[i4 + 1] = i6;
      sort(paramArrayOfInt, paramInt1, i3 - 2, paramBoolean);
      sort(paramArrayOfInt, i4 + 2, paramInt2, false);
      if (i3 < n && i2 < i4) {
        while (paramArrayOfInt[i3] == i5)
          i3++; 
        while (paramArrayOfInt[i4] == i6)
          i4--; 
        i7 = i3 - 1;
        while (++i7 <= i4) {
          int i8 = paramArrayOfInt[i7];
          if (i8 == i5) {
            paramArrayOfInt[i7] = paramArrayOfInt[i3];
            paramArrayOfInt[i3] = i8;
            i3++;
            continue;
          } 
          if (i8 == i6) {
            while (paramArrayOfInt[i4] == i6) {
              if (i4-- == i7)
                // Byte code: goto -> 1033 
            } 
            if (paramArrayOfInt[i4] == i5) {
              paramArrayOfInt[i7] = paramArrayOfInt[i3];
              paramArrayOfInt[i3] = i5;
              i3++;
            } else {
              paramArrayOfInt[i7] = paramArrayOfInt[i4];
            } 
            paramArrayOfInt[i4] = i8;
            i4--;
          } 
        } 
      } 
      sort(paramArrayOfInt, i3, i4, false);
    } else {
      int i5 = paramArrayOfInt[k];
      for (int i6 = i3; i6 <= i4; i6++) {
        if (paramArrayOfInt[i6] != i5) {
          int i7 = paramArrayOfInt[i6];
          if (i7 < i5) {
            paramArrayOfInt[i6] = paramArrayOfInt[i3];
            paramArrayOfInt[i3] = i7;
            i3++;
          } else {
            while (paramArrayOfInt[i4] > i5)
              i4--; 
            if (paramArrayOfInt[i4] < i5) {
              paramArrayOfInt[i6] = paramArrayOfInt[i3];
              paramArrayOfInt[i3] = paramArrayOfInt[i4];
              i3++;
            } else {
              paramArrayOfInt[i6] = i5;
            } 
            paramArrayOfInt[i4] = i7;
            i4--;
          } 
        } 
      } 
      sort(paramArrayOfInt, paramInt1, i3 - 1, paramBoolean);
      sort(paramArrayOfInt, i4 + 1, paramInt2, false);
    } 
  }
  
  static void sort(long[] paramArrayOfLong1, int paramInt1, int paramInt2, long[] paramArrayOfLong2, int paramInt3, int paramInt4) {
    int k;
    int j;
    long[] arrayOfLong;
    if (paramInt2 - paramInt1 < 286) {
      sort(paramArrayOfLong1, paramInt1, paramInt2, true);
      return;
    } 
    int[] arrayOfInt = new int[68];
    byte b = 0;
    arrayOfInt[0] = paramInt1;
    int i = paramInt1;
    while (i < paramInt2) {
      if (paramArrayOfLong1[i] < paramArrayOfLong1[i + 1]) {
        while (++i <= paramInt2 && paramArrayOfLong1[i - 1] <= paramArrayOfLong1[i]);
      } else if (paramArrayOfLong1[i] > paramArrayOfLong1[i + 1]) {
        while (++i <= paramInt2 && paramArrayOfLong1[i - 1] >= paramArrayOfLong1[i]);
        int n = arrayOfInt[b] - 1;
        j = i;
        while (++n < --j) {
          k = paramArrayOfLong1[n];
          paramArrayOfLong1[n] = paramArrayOfLong1[j];
          paramArrayOfLong1[j] = k;
        } 
      } else {
        byte b1 = 33;
        while (++i <= paramInt2 && paramArrayOfLong1[i - 1] == paramArrayOfLong1[i]) {
          if (--b1 == 0) {
            sort(paramArrayOfLong1, paramInt1, paramInt2, true);
            return;
          } 
        } 
      } 
      if (++b == 67) {
        sort(paramArrayOfLong1, paramInt1, paramInt2, true);
        return;
      } 
      arrayOfInt[b] = i;
    } 
    if (arrayOfInt[b] == paramInt2++) {
      arrayOfInt[++b] = paramInt2;
    } else if (b == 1) {
      return;
    } 
    i = 0;
    boolean bool = true;
    while (bool <<= true < b)
      i = (byte)(i ^ true); 
    int m = paramInt2 - paramInt1;
    if (paramArrayOfLong2 == null || paramInt4 < m || paramInt3 + m > paramArrayOfLong2.length) {
      paramArrayOfLong2 = new long[m];
      paramInt3 = 0;
    } 
    if (i == 0) {
      System.arraycopy(paramArrayOfLong1, paramInt1, paramArrayOfLong2, paramInt3, m);
      arrayOfLong = paramArrayOfLong1;
      k = 0;
      paramArrayOfLong1 = paramArrayOfLong2;
      j = paramInt3 - paramInt1;
    } else {
      arrayOfLong = paramArrayOfLong2;
      j = 0;
      k = paramInt3 - paramInt1;
    } 
    while (b > 1) {
      byte b1;
      int n;
      for (n = (b1 = 0) + 2; n <= b; n += 2) {
        int i2 = arrayOfInt[n];
        int i3 = arrayOfInt[n - 1];
        int i4 = arrayOfInt[n - 2];
        int i5 = i4;
        int i6 = i3;
        while (i4 < i2) {
          if (i6 >= i2 || (i5 < i3 && paramArrayOfLong1[i5 + j] <= paramArrayOfLong1[i6 + j])) {
            arrayOfLong[i4 + k] = paramArrayOfLong1[i5++ + j];
          } else {
            arrayOfLong[i4 + k] = paramArrayOfLong1[i6++ + j];
          } 
          i4++;
        } 
        arrayOfInt[++b1] = i2;
      } 
      if ((b & true) != 0) {
        n = paramInt2;
        int i2 = arrayOfInt[b - 1];
        while (--n >= i2)
          arrayOfLong[n + k] = paramArrayOfLong1[n + j]; 
        arrayOfInt[++b1] = paramInt2;
      } 
      long[] arrayOfLong1 = paramArrayOfLong1;
      paramArrayOfLong1 = arrayOfLong;
      arrayOfLong = arrayOfLong1;
      int i1 = j;
      j = k;
      k = i1;
      b = b1;
    } 
  }
  
  private static void sort(long[] paramArrayOfLong, int paramInt1, int paramInt2, boolean paramBoolean) {
    int i = paramInt2 - paramInt1 + 1;
    if (i < 47) {
      if (paramBoolean) {
        int i5 = paramInt1;
        int i6;
        for (i6 = i5; i5 < paramInt2; i6 = ++i5) {
          long l = paramArrayOfLong[i5 + 1];
          while (l < paramArrayOfLong[i6]) {
            paramArrayOfLong[i6 + 1] = paramArrayOfLong[i6];
            if (i6-- == paramInt1)
              break; 
          } 
          paramArrayOfLong[i6 + 1] = l;
        } 
      } else {
        do {
          if (paramInt1 >= paramInt2)
            return; 
        } while (paramArrayOfLong[++paramInt1] >= paramArrayOfLong[paramInt1 - 1]);
        int i5;
        for (i5 = paramInt1; ++paramInt1 <= paramInt2; i5 = ++paramInt1) {
          long l1 = paramArrayOfLong[i5];
          long l2 = paramArrayOfLong[paramInt1];
          if (l1 < l2) {
            l2 = l1;
            l1 = paramArrayOfLong[paramInt1];
          } 
          while (l1 < paramArrayOfLong[--i5])
            paramArrayOfLong[i5 + 2] = paramArrayOfLong[i5]; 
          paramArrayOfLong[++i5 + 1] = l1;
          while (l2 < paramArrayOfLong[--i5])
            paramArrayOfLong[i5 + 1] = paramArrayOfLong[i5]; 
          paramArrayOfLong[i5 + 1] = l2;
        } 
        long l = paramArrayOfLong[paramInt2];
        while (l < paramArrayOfLong[--paramInt2])
          paramArrayOfLong[paramInt2 + 1] = paramArrayOfLong[paramInt2]; 
        paramArrayOfLong[paramInt2 + 1] = l;
      } 
      return;
    } 
    int j = (i >> 3) + (i >> 6) + 1;
    int k = paramInt1 + paramInt2 >>> 1;
    int m = k - j;
    int n = m - j;
    int i1 = k + j;
    int i2 = i1 + j;
    if (paramArrayOfLong[m] < paramArrayOfLong[n]) {
      long l = paramArrayOfLong[m];
      paramArrayOfLong[m] = paramArrayOfLong[n];
      paramArrayOfLong[n] = l;
    } 
    if (paramArrayOfLong[k] < paramArrayOfLong[m]) {
      long l = paramArrayOfLong[k];
      paramArrayOfLong[k] = paramArrayOfLong[m];
      paramArrayOfLong[m] = l;
      if (l < paramArrayOfLong[n]) {
        paramArrayOfLong[m] = paramArrayOfLong[n];
        paramArrayOfLong[n] = l;
      } 
    } 
    if (paramArrayOfLong[i1] < paramArrayOfLong[k]) {
      long l = paramArrayOfLong[i1];
      paramArrayOfLong[i1] = paramArrayOfLong[k];
      paramArrayOfLong[k] = l;
      if (l < paramArrayOfLong[m]) {
        paramArrayOfLong[k] = paramArrayOfLong[m];
        paramArrayOfLong[m] = l;
        if (l < paramArrayOfLong[n]) {
          paramArrayOfLong[m] = paramArrayOfLong[n];
          paramArrayOfLong[n] = l;
        } 
      } 
    } 
    if (paramArrayOfLong[i2] < paramArrayOfLong[i1]) {
      long l = paramArrayOfLong[i2];
      paramArrayOfLong[i2] = paramArrayOfLong[i1];
      paramArrayOfLong[i1] = l;
      if (l < paramArrayOfLong[k]) {
        paramArrayOfLong[i1] = paramArrayOfLong[k];
        paramArrayOfLong[k] = l;
        if (l < paramArrayOfLong[m]) {
          paramArrayOfLong[k] = paramArrayOfLong[m];
          paramArrayOfLong[m] = l;
          if (l < paramArrayOfLong[n]) {
            paramArrayOfLong[m] = paramArrayOfLong[n];
            paramArrayOfLong[n] = l;
          } 
        } 
      } 
    } 
    int i3 = paramInt1;
    int i4 = paramInt2;
    if (paramArrayOfLong[n] != paramArrayOfLong[m] && paramArrayOfLong[m] != paramArrayOfLong[k] && paramArrayOfLong[k] != paramArrayOfLong[i1] && paramArrayOfLong[i1] != paramArrayOfLong[i2]) {
      long l1 = paramArrayOfLong[m];
      long l2 = paramArrayOfLong[i1];
      paramArrayOfLong[m] = paramArrayOfLong[paramInt1];
      paramArrayOfLong[i1] = paramArrayOfLong[paramInt2];
      while (paramArrayOfLong[++i3] < l1);
      while (paramArrayOfLong[--i4] > l2);
      int i5 = i3 - 1;
      label147: while (++i5 <= i4) {
        long l = paramArrayOfLong[i5];
        if (l < l1) {
          paramArrayOfLong[i5] = paramArrayOfLong[i3];
          paramArrayOfLong[i3] = l;
          i3++;
          continue;
        } 
        if (l > l2) {
          while (paramArrayOfLong[i4] > l2) {
            if (i4-- == i5)
              break label147; 
          } 
          if (paramArrayOfLong[i4] < l1) {
            paramArrayOfLong[i5] = paramArrayOfLong[i3];
            paramArrayOfLong[i3] = paramArrayOfLong[i4];
            i3++;
          } else {
            paramArrayOfLong[i5] = paramArrayOfLong[i4];
          } 
          paramArrayOfLong[i4] = l;
          i4--;
        } 
      } 
      paramArrayOfLong[paramInt1] = paramArrayOfLong[i3 - 1];
      paramArrayOfLong[i3 - 1] = l1;
      paramArrayOfLong[paramInt2] = paramArrayOfLong[i4 + 1];
      paramArrayOfLong[i4 + 1] = l2;
      sort(paramArrayOfLong, paramInt1, i3 - 2, paramBoolean);
      sort(paramArrayOfLong, i4 + 2, paramInt2, false);
      if (i3 < n && i2 < i4) {
        while (paramArrayOfLong[i3] == l1)
          i3++; 
        while (paramArrayOfLong[i4] == l2)
          i4--; 
        i5 = i3 - 1;
        while (++i5 <= i4) {
          long l = paramArrayOfLong[i5];
          if (l == l1) {
            paramArrayOfLong[i5] = paramArrayOfLong[i3];
            paramArrayOfLong[i3] = l;
            i3++;
            continue;
          } 
          if (l == l2) {
            while (paramArrayOfLong[i4] == l2) {
              if (i4-- == i5)
                // Byte code: goto -> 1065 
            } 
            if (paramArrayOfLong[i4] == l1) {
              paramArrayOfLong[i5] = paramArrayOfLong[i3];
              paramArrayOfLong[i3] = l1;
              i3++;
            } else {
              paramArrayOfLong[i5] = paramArrayOfLong[i4];
            } 
            paramArrayOfLong[i4] = l;
            i4--;
          } 
        } 
      } 
      sort(paramArrayOfLong, i3, i4, false);
    } else {
      long l = paramArrayOfLong[k];
      for (int i5 = i3; i5 <= i4; i5++) {
        if (paramArrayOfLong[i5] != l) {
          long l1 = paramArrayOfLong[i5];
          if (l1 < l) {
            paramArrayOfLong[i5] = paramArrayOfLong[i3];
            paramArrayOfLong[i3] = l1;
            i3++;
          } else {
            while (paramArrayOfLong[i4] > l)
              i4--; 
            if (paramArrayOfLong[i4] < l) {
              paramArrayOfLong[i5] = paramArrayOfLong[i3];
              paramArrayOfLong[i3] = paramArrayOfLong[i4];
              i3++;
            } else {
              paramArrayOfLong[i5] = l;
            } 
            paramArrayOfLong[i4] = l1;
            i4--;
          } 
        } 
      } 
      sort(paramArrayOfLong, paramInt1, i3 - 1, paramBoolean);
      sort(paramArrayOfLong, i4 + 1, paramInt2, false);
    } 
  }
  
  static void sort(short[] paramArrayOfShort1, int paramInt1, int paramInt2, short[] paramArrayOfShort2, int paramInt3, int paramInt4) {
    if (paramInt2 - paramInt1 > 3200) {
      int[] arrayOfInt = new int[65536];
      int i = paramInt1 - 1;
      while (++i <= paramInt2)
        arrayOfInt[paramArrayOfShort1[i] - Short.MIN_VALUE] = arrayOfInt[paramArrayOfShort1[i] - Short.MIN_VALUE] + 1; 
      i = 65536;
      int j = paramInt2 + 1;
      label21: while (j > paramInt1) {
        while (arrayOfInt[--i] == 0);
        short s = (short)(i + -32768);
        int k = arrayOfInt[i];
        while (true) {
          paramArrayOfShort1[--j] = s;
          if (--k <= 0)
            continue label21; 
        } 
      } 
    } else {
      doSort(paramArrayOfShort1, paramInt1, paramInt2, paramArrayOfShort2, paramInt3, paramInt4);
    } 
  }
  
  private static void doSort(short[] paramArrayOfShort1, int paramInt1, int paramInt2, short[] paramArrayOfShort2, int paramInt3, int paramInt4) {
    int k;
    int j;
    short[] arrayOfShort;
    if (paramInt2 - paramInt1 < 286) {
      sort(paramArrayOfShort1, paramInt1, paramInt2, true);
      return;
    } 
    int[] arrayOfInt = new int[68];
    byte b = 0;
    arrayOfInt[0] = paramInt1;
    int i = paramInt1;
    while (i < paramInt2) {
      if (paramArrayOfShort1[i] < paramArrayOfShort1[i + 1]) {
        while (++i <= paramInt2 && paramArrayOfShort1[i - 1] <= paramArrayOfShort1[i]);
      } else if (paramArrayOfShort1[i] > paramArrayOfShort1[i + 1]) {
        while (++i <= paramInt2 && paramArrayOfShort1[i - 1] >= paramArrayOfShort1[i]);
        int n = arrayOfInt[b] - 1;
        j = i;
        while (++n < --j) {
          k = paramArrayOfShort1[n];
          paramArrayOfShort1[n] = paramArrayOfShort1[j];
          paramArrayOfShort1[j] = k;
        } 
      } else {
        byte b1 = 33;
        while (++i <= paramInt2 && paramArrayOfShort1[i - 1] == paramArrayOfShort1[i]) {
          if (--b1 == 0) {
            sort(paramArrayOfShort1, paramInt1, paramInt2, true);
            return;
          } 
        } 
      } 
      if (++b == 67) {
        sort(paramArrayOfShort1, paramInt1, paramInt2, true);
        return;
      } 
      arrayOfInt[b] = i;
    } 
    if (arrayOfInt[b] == paramInt2++) {
      arrayOfInt[++b] = paramInt2;
    } else if (b == 1) {
      return;
    } 
    i = 0;
    boolean bool = true;
    while (bool <<= true < b)
      i = (byte)(i ^ true); 
    int m = paramInt2 - paramInt1;
    if (paramArrayOfShort2 == null || paramInt4 < m || paramInt3 + m > paramArrayOfShort2.length) {
      paramArrayOfShort2 = new short[m];
      paramInt3 = 0;
    } 
    if (i == 0) {
      System.arraycopy(paramArrayOfShort1, paramInt1, paramArrayOfShort2, paramInt3, m);
      arrayOfShort = paramArrayOfShort1;
      k = 0;
      paramArrayOfShort1 = paramArrayOfShort2;
      j = paramInt3 - paramInt1;
    } else {
      arrayOfShort = paramArrayOfShort2;
      j = 0;
      k = paramInt3 - paramInt1;
    } 
    while (b > 1) {
      byte b1;
      int n;
      for (n = (b1 = 0) + 2; n <= b; n += 2) {
        int i2 = arrayOfInt[n];
        int i3 = arrayOfInt[n - 1];
        int i4 = arrayOfInt[n - 2];
        int i5 = i4;
        int i6 = i3;
        while (i4 < i2) {
          if (i6 >= i2 || (i5 < i3 && paramArrayOfShort1[i5 + j] <= paramArrayOfShort1[i6 + j])) {
            arrayOfShort[i4 + k] = paramArrayOfShort1[i5++ + j];
          } else {
            arrayOfShort[i4 + k] = paramArrayOfShort1[i6++ + j];
          } 
          i4++;
        } 
        arrayOfInt[++b1] = i2;
      } 
      if ((b & true) != 0) {
        n = paramInt2;
        int i2 = arrayOfInt[b - 1];
        while (--n >= i2)
          arrayOfShort[n + k] = paramArrayOfShort1[n + j]; 
        arrayOfInt[++b1] = paramInt2;
      } 
      short[] arrayOfShort1 = paramArrayOfShort1;
      paramArrayOfShort1 = arrayOfShort;
      arrayOfShort = arrayOfShort1;
      int i1 = j;
      j = k;
      k = i1;
      b = b1;
    } 
  }
  
  private static void sort(short[] paramArrayOfShort, int paramInt1, int paramInt2, boolean paramBoolean) {
    int i = paramInt2 - paramInt1 + 1;
    if (i < 47) {
      if (paramBoolean) {
        int i5 = paramInt1;
        int i6;
        for (i6 = i5; i5 < paramInt2; i6 = ++i5) {
          short s = paramArrayOfShort[i5 + 1];
          while (s < paramArrayOfShort[i6]) {
            paramArrayOfShort[i6 + 1] = paramArrayOfShort[i6];
            if (i6-- == paramInt1)
              break; 
          } 
          paramArrayOfShort[i6 + 1] = s;
        } 
      } else {
        do {
          if (paramInt1 >= paramInt2)
            return; 
        } while (paramArrayOfShort[++paramInt1] >= paramArrayOfShort[paramInt1 - 1]);
        int i5;
        for (i5 = paramInt1; ++paramInt1 <= paramInt2; i5 = ++paramInt1) {
          short s1 = paramArrayOfShort[i5];
          short s2 = paramArrayOfShort[paramInt1];
          if (s1 < s2) {
            s2 = s1;
            s1 = paramArrayOfShort[paramInt1];
          } 
          while (s1 < paramArrayOfShort[--i5])
            paramArrayOfShort[i5 + 2] = paramArrayOfShort[i5]; 
          paramArrayOfShort[++i5 + 1] = s1;
          while (s2 < paramArrayOfShort[--i5])
            paramArrayOfShort[i5 + 1] = paramArrayOfShort[i5]; 
          paramArrayOfShort[i5 + 1] = s2;
        } 
        i5 = paramArrayOfShort[paramInt2];
        while (i5 < paramArrayOfShort[--paramInt2])
          paramArrayOfShort[paramInt2 + 1] = paramArrayOfShort[paramInt2]; 
        paramArrayOfShort[paramInt2 + 1] = i5;
      } 
      return;
    } 
    int j = (i >> 3) + (i >> 6) + 1;
    int k = paramInt1 + paramInt2 >>> 1;
    int m = k - j;
    int n = m - j;
    int i1 = k + j;
    int i2 = i1 + j;
    if (paramArrayOfShort[m] < paramArrayOfShort[n]) {
      short s = paramArrayOfShort[m];
      paramArrayOfShort[m] = paramArrayOfShort[n];
      paramArrayOfShort[n] = s;
    } 
    if (paramArrayOfShort[k] < paramArrayOfShort[m]) {
      short s = paramArrayOfShort[k];
      paramArrayOfShort[k] = paramArrayOfShort[m];
      paramArrayOfShort[m] = s;
      if (s < paramArrayOfShort[n]) {
        paramArrayOfShort[m] = paramArrayOfShort[n];
        paramArrayOfShort[n] = s;
      } 
    } 
    if (paramArrayOfShort[i1] < paramArrayOfShort[k]) {
      short s = paramArrayOfShort[i1];
      paramArrayOfShort[i1] = paramArrayOfShort[k];
      paramArrayOfShort[k] = s;
      if (s < paramArrayOfShort[m]) {
        paramArrayOfShort[k] = paramArrayOfShort[m];
        paramArrayOfShort[m] = s;
        if (s < paramArrayOfShort[n]) {
          paramArrayOfShort[m] = paramArrayOfShort[n];
          paramArrayOfShort[n] = s;
        } 
      } 
    } 
    if (paramArrayOfShort[i2] < paramArrayOfShort[i1]) {
      short s = paramArrayOfShort[i2];
      paramArrayOfShort[i2] = paramArrayOfShort[i1];
      paramArrayOfShort[i1] = s;
      if (s < paramArrayOfShort[k]) {
        paramArrayOfShort[i1] = paramArrayOfShort[k];
        paramArrayOfShort[k] = s;
        if (s < paramArrayOfShort[m]) {
          paramArrayOfShort[k] = paramArrayOfShort[m];
          paramArrayOfShort[m] = s;
          if (s < paramArrayOfShort[n]) {
            paramArrayOfShort[m] = paramArrayOfShort[n];
            paramArrayOfShort[n] = s;
          } 
        } 
      } 
    } 
    int i3 = paramInt1;
    int i4 = paramInt2;
    if (paramArrayOfShort[n] != paramArrayOfShort[m] && paramArrayOfShort[m] != paramArrayOfShort[k] && paramArrayOfShort[k] != paramArrayOfShort[i1] && paramArrayOfShort[i1] != paramArrayOfShort[i2]) {
      short s1 = paramArrayOfShort[m];
      short s2 = paramArrayOfShort[i1];
      paramArrayOfShort[m] = paramArrayOfShort[paramInt1];
      paramArrayOfShort[i1] = paramArrayOfShort[paramInt2];
      while (paramArrayOfShort[++i3] < s1);
      while (paramArrayOfShort[--i4] > s2);
      int i5 = i3 - 1;
      label147: while (++i5 <= i4) {
        short s = paramArrayOfShort[i5];
        if (s < s1) {
          paramArrayOfShort[i5] = paramArrayOfShort[i3];
          paramArrayOfShort[i3] = s;
          i3++;
          continue;
        } 
        if (s > s2) {
          while (paramArrayOfShort[i4] > s2) {
            if (i4-- == i5)
              break label147; 
          } 
          if (paramArrayOfShort[i4] < s1) {
            paramArrayOfShort[i5] = paramArrayOfShort[i3];
            paramArrayOfShort[i3] = paramArrayOfShort[i4];
            i3++;
          } else {
            paramArrayOfShort[i5] = paramArrayOfShort[i4];
          } 
          paramArrayOfShort[i4] = s;
          i4--;
        } 
      } 
      paramArrayOfShort[paramInt1] = paramArrayOfShort[i3 - 1];
      paramArrayOfShort[i3 - 1] = s1;
      paramArrayOfShort[paramInt2] = paramArrayOfShort[i4 + 1];
      paramArrayOfShort[i4 + 1] = s2;
      sort(paramArrayOfShort, paramInt1, i3 - 2, paramBoolean);
      sort(paramArrayOfShort, i4 + 2, paramInt2, false);
      if (i3 < n && i2 < i4) {
        while (paramArrayOfShort[i3] == s1)
          i3++; 
        while (paramArrayOfShort[i4] == s2)
          i4--; 
        i5 = i3 - 1;
        while (++i5 <= i4) {
          short s = paramArrayOfShort[i5];
          if (s == s1) {
            paramArrayOfShort[i5] = paramArrayOfShort[i3];
            paramArrayOfShort[i3] = s;
            i3++;
            continue;
          } 
          if (s == s2) {
            while (paramArrayOfShort[i4] == s2) {
              if (i4-- == i5)
                // Byte code: goto -> 1033 
            } 
            if (paramArrayOfShort[i4] == s1) {
              paramArrayOfShort[i5] = paramArrayOfShort[i3];
              paramArrayOfShort[i3] = s1;
              i3++;
            } else {
              paramArrayOfShort[i5] = paramArrayOfShort[i4];
            } 
            paramArrayOfShort[i4] = s;
            i4--;
          } 
        } 
      } 
      sort(paramArrayOfShort, i3, i4, false);
    } else {
      short s = paramArrayOfShort[k];
      for (int i5 = i3; i5 <= i4; i5++) {
        if (paramArrayOfShort[i5] != s) {
          short s1 = paramArrayOfShort[i5];
          if (s1 < s) {
            paramArrayOfShort[i5] = paramArrayOfShort[i3];
            paramArrayOfShort[i3] = s1;
            i3++;
          } else {
            while (paramArrayOfShort[i4] > s)
              i4--; 
            if (paramArrayOfShort[i4] < s) {
              paramArrayOfShort[i5] = paramArrayOfShort[i3];
              paramArrayOfShort[i3] = paramArrayOfShort[i4];
              i3++;
            } else {
              paramArrayOfShort[i5] = s;
            } 
            paramArrayOfShort[i4] = s1;
            i4--;
          } 
        } 
      } 
      sort(paramArrayOfShort, paramInt1, i3 - 1, paramBoolean);
      sort(paramArrayOfShort, i4 + 1, paramInt2, false);
    } 
  }
  
  static void sort(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2, int paramInt3, int paramInt4) {
    if (paramInt2 - paramInt1 > 3200) {
      int[] arrayOfInt = new int[65536];
      int i = paramInt1 - 1;
      while (++i <= paramInt2)
        arrayOfInt[paramArrayOfChar1[i]] = arrayOfInt[paramArrayOfChar1[i]] + 1; 
      i = 65536;
      int j = paramInt2 + 1;
      label21: while (j > paramInt1) {
        while (arrayOfInt[--i] == 0);
        char c = (char)i;
        int k = arrayOfInt[i];
        while (true) {
          paramArrayOfChar1[--j] = c;
          if (--k <= 0)
            continue label21; 
        } 
      } 
    } else {
      doSort(paramArrayOfChar1, paramInt1, paramInt2, paramArrayOfChar2, paramInt3, paramInt4);
    } 
  }
  
  private static void doSort(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2, int paramInt3, int paramInt4) {
    int k;
    int j;
    char[] arrayOfChar;
    if (paramInt2 - paramInt1 < 286) {
      sort(paramArrayOfChar1, paramInt1, paramInt2, true);
      return;
    } 
    int[] arrayOfInt = new int[68];
    byte b = 0;
    arrayOfInt[0] = paramInt1;
    int i = paramInt1;
    while (i < paramInt2) {
      if (paramArrayOfChar1[i] < paramArrayOfChar1[i + 1]) {
        while (++i <= paramInt2 && paramArrayOfChar1[i - 1] <= paramArrayOfChar1[i]);
      } else if (paramArrayOfChar1[i] > paramArrayOfChar1[i + 1]) {
        while (++i <= paramInt2 && paramArrayOfChar1[i - 1] >= paramArrayOfChar1[i]);
        int n = arrayOfInt[b] - 1;
        j = i;
        while (++n < --j) {
          k = paramArrayOfChar1[n];
          paramArrayOfChar1[n] = paramArrayOfChar1[j];
          paramArrayOfChar1[j] = k;
        } 
      } else {
        byte b1 = 33;
        while (++i <= paramInt2 && paramArrayOfChar1[i - 1] == paramArrayOfChar1[i]) {
          if (--b1 == 0) {
            sort(paramArrayOfChar1, paramInt1, paramInt2, true);
            return;
          } 
        } 
      } 
      if (++b == 67) {
        sort(paramArrayOfChar1, paramInt1, paramInt2, true);
        return;
      } 
      arrayOfInt[b] = i;
    } 
    if (arrayOfInt[b] == paramInt2++) {
      arrayOfInt[++b] = paramInt2;
    } else if (b == 1) {
      return;
    } 
    i = 0;
    boolean bool = true;
    while (bool <<= true < b)
      i = (byte)(i ^ true); 
    int m = paramInt2 - paramInt1;
    if (paramArrayOfChar2 == null || paramInt4 < m || paramInt3 + m > paramArrayOfChar2.length) {
      paramArrayOfChar2 = new char[m];
      paramInt3 = 0;
    } 
    if (i == 0) {
      System.arraycopy(paramArrayOfChar1, paramInt1, paramArrayOfChar2, paramInt3, m);
      arrayOfChar = paramArrayOfChar1;
      k = 0;
      paramArrayOfChar1 = paramArrayOfChar2;
      j = paramInt3 - paramInt1;
    } else {
      arrayOfChar = paramArrayOfChar2;
      j = 0;
      k = paramInt3 - paramInt1;
    } 
    while (b > 1) {
      byte b1;
      int n;
      for (n = (b1 = 0) + 2; n <= b; n += 2) {
        int i2 = arrayOfInt[n];
        int i3 = arrayOfInt[n - 1];
        int i4 = arrayOfInt[n - 2];
        int i5 = i4;
        int i6 = i3;
        while (i4 < i2) {
          if (i6 >= i2 || (i5 < i3 && paramArrayOfChar1[i5 + j] <= paramArrayOfChar1[i6 + j])) {
            arrayOfChar[i4 + k] = paramArrayOfChar1[i5++ + j];
          } else {
            arrayOfChar[i4 + k] = paramArrayOfChar1[i6++ + j];
          } 
          i4++;
        } 
        arrayOfInt[++b1] = i2;
      } 
      if ((b & true) != 0) {
        n = paramInt2;
        int i2 = arrayOfInt[b - 1];
        while (--n >= i2)
          arrayOfChar[n + k] = paramArrayOfChar1[n + j]; 
        arrayOfInt[++b1] = paramInt2;
      } 
      char[] arrayOfChar1 = paramArrayOfChar1;
      paramArrayOfChar1 = arrayOfChar;
      arrayOfChar = arrayOfChar1;
      int i1 = j;
      j = k;
      k = i1;
      b = b1;
    } 
  }
  
  private static void sort(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean) {
    int i = paramInt2 - paramInt1 + 1;
    if (i < 47) {
      if (paramBoolean) {
        int i5 = paramInt1;
        int i6;
        for (i6 = i5; i5 < paramInt2; i6 = ++i5) {
          char c = paramArrayOfChar[i5 + 1];
          while (c < paramArrayOfChar[i6]) {
            paramArrayOfChar[i6 + 1] = paramArrayOfChar[i6];
            if (i6-- == paramInt1)
              break; 
          } 
          paramArrayOfChar[i6 + 1] = c;
        } 
      } else {
        do {
          if (paramInt1 >= paramInt2)
            return; 
        } while (paramArrayOfChar[++paramInt1] >= paramArrayOfChar[paramInt1 - 1]);
        int i5;
        for (i5 = paramInt1; ++paramInt1 <= paramInt2; i5 = ++paramInt1) {
          char c1 = paramArrayOfChar[i5];
          char c2 = paramArrayOfChar[paramInt1];
          if (c1 < c2) {
            c2 = c1;
            c1 = paramArrayOfChar[paramInt1];
          } 
          while (c1 < paramArrayOfChar[--i5])
            paramArrayOfChar[i5 + 2] = paramArrayOfChar[i5]; 
          paramArrayOfChar[++i5 + 1] = c1;
          while (c2 < paramArrayOfChar[--i5])
            paramArrayOfChar[i5 + 1] = paramArrayOfChar[i5]; 
          paramArrayOfChar[i5 + 1] = c2;
        } 
        i5 = paramArrayOfChar[paramInt2];
        while (i5 < paramArrayOfChar[--paramInt2])
          paramArrayOfChar[paramInt2 + 1] = paramArrayOfChar[paramInt2]; 
        paramArrayOfChar[paramInt2 + 1] = i5;
      } 
      return;
    } 
    int j = (i >> 3) + (i >> 6) + 1;
    int k = paramInt1 + paramInt2 >>> 1;
    int m = k - j;
    int n = m - j;
    int i1 = k + j;
    int i2 = i1 + j;
    if (paramArrayOfChar[m] < paramArrayOfChar[n]) {
      char c = paramArrayOfChar[m];
      paramArrayOfChar[m] = paramArrayOfChar[n];
      paramArrayOfChar[n] = c;
    } 
    if (paramArrayOfChar[k] < paramArrayOfChar[m]) {
      char c = paramArrayOfChar[k];
      paramArrayOfChar[k] = paramArrayOfChar[m];
      paramArrayOfChar[m] = c;
      if (c < paramArrayOfChar[n]) {
        paramArrayOfChar[m] = paramArrayOfChar[n];
        paramArrayOfChar[n] = c;
      } 
    } 
    if (paramArrayOfChar[i1] < paramArrayOfChar[k]) {
      char c = paramArrayOfChar[i1];
      paramArrayOfChar[i1] = paramArrayOfChar[k];
      paramArrayOfChar[k] = c;
      if (c < paramArrayOfChar[m]) {
        paramArrayOfChar[k] = paramArrayOfChar[m];
        paramArrayOfChar[m] = c;
        if (c < paramArrayOfChar[n]) {
          paramArrayOfChar[m] = paramArrayOfChar[n];
          paramArrayOfChar[n] = c;
        } 
      } 
    } 
    if (paramArrayOfChar[i2] < paramArrayOfChar[i1]) {
      char c = paramArrayOfChar[i2];
      paramArrayOfChar[i2] = paramArrayOfChar[i1];
      paramArrayOfChar[i1] = c;
      if (c < paramArrayOfChar[k]) {
        paramArrayOfChar[i1] = paramArrayOfChar[k];
        paramArrayOfChar[k] = c;
        if (c < paramArrayOfChar[m]) {
          paramArrayOfChar[k] = paramArrayOfChar[m];
          paramArrayOfChar[m] = c;
          if (c < paramArrayOfChar[n]) {
            paramArrayOfChar[m] = paramArrayOfChar[n];
            paramArrayOfChar[n] = c;
          } 
        } 
      } 
    } 
    int i3 = paramInt1;
    int i4 = paramInt2;
    if (paramArrayOfChar[n] != paramArrayOfChar[m] && paramArrayOfChar[m] != paramArrayOfChar[k] && paramArrayOfChar[k] != paramArrayOfChar[i1] && paramArrayOfChar[i1] != paramArrayOfChar[i2]) {
      char c1 = paramArrayOfChar[m];
      char c2 = paramArrayOfChar[i1];
      paramArrayOfChar[m] = paramArrayOfChar[paramInt1];
      paramArrayOfChar[i1] = paramArrayOfChar[paramInt2];
      while (paramArrayOfChar[++i3] < c1);
      while (paramArrayOfChar[--i4] > c2);
      int i5 = i3 - 1;
      label147: while (++i5 <= i4) {
        char c = paramArrayOfChar[i5];
        if (c < c1) {
          paramArrayOfChar[i5] = paramArrayOfChar[i3];
          paramArrayOfChar[i3] = c;
          i3++;
          continue;
        } 
        if (c > c2) {
          while (paramArrayOfChar[i4] > c2) {
            if (i4-- == i5)
              break label147; 
          } 
          if (paramArrayOfChar[i4] < c1) {
            paramArrayOfChar[i5] = paramArrayOfChar[i3];
            paramArrayOfChar[i3] = paramArrayOfChar[i4];
            i3++;
          } else {
            paramArrayOfChar[i5] = paramArrayOfChar[i4];
          } 
          paramArrayOfChar[i4] = c;
          i4--;
        } 
      } 
      paramArrayOfChar[paramInt1] = paramArrayOfChar[i3 - 1];
      paramArrayOfChar[i3 - 1] = c1;
      paramArrayOfChar[paramInt2] = paramArrayOfChar[i4 + 1];
      paramArrayOfChar[i4 + 1] = c2;
      sort(paramArrayOfChar, paramInt1, i3 - 2, paramBoolean);
      sort(paramArrayOfChar, i4 + 2, paramInt2, false);
      if (i3 < n && i2 < i4) {
        while (paramArrayOfChar[i3] == c1)
          i3++; 
        while (paramArrayOfChar[i4] == c2)
          i4--; 
        i5 = i3 - 1;
        while (++i5 <= i4) {
          char c = paramArrayOfChar[i5];
          if (c == c1) {
            paramArrayOfChar[i5] = paramArrayOfChar[i3];
            paramArrayOfChar[i3] = c;
            i3++;
            continue;
          } 
          if (c == c2) {
            while (paramArrayOfChar[i4] == c2) {
              if (i4-- == i5)
                // Byte code: goto -> 1033 
            } 
            if (paramArrayOfChar[i4] == c1) {
              paramArrayOfChar[i5] = paramArrayOfChar[i3];
              paramArrayOfChar[i3] = c1;
              i3++;
            } else {
              paramArrayOfChar[i5] = paramArrayOfChar[i4];
            } 
            paramArrayOfChar[i4] = c;
            i4--;
          } 
        } 
      } 
      sort(paramArrayOfChar, i3, i4, false);
    } else {
      char c = paramArrayOfChar[k];
      for (int i5 = i3; i5 <= i4; i5++) {
        if (paramArrayOfChar[i5] != c) {
          char c1 = paramArrayOfChar[i5];
          if (c1 < c) {
            paramArrayOfChar[i5] = paramArrayOfChar[i3];
            paramArrayOfChar[i3] = c1;
            i3++;
          } else {
            while (paramArrayOfChar[i4] > c)
              i4--; 
            if (paramArrayOfChar[i4] < c) {
              paramArrayOfChar[i5] = paramArrayOfChar[i3];
              paramArrayOfChar[i3] = paramArrayOfChar[i4];
              i3++;
            } else {
              paramArrayOfChar[i5] = c;
            } 
            paramArrayOfChar[i4] = c1;
            i4--;
          } 
        } 
      } 
      sort(paramArrayOfChar, paramInt1, i3 - 1, paramBoolean);
      sort(paramArrayOfChar, i4 + 1, paramInt2, false);
    } 
  }
  
  static void sort(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (paramInt2 - paramInt1 > 29) {
      int[] arrayOfInt = new int[256];
      int i = paramInt1 - 1;
      while (++i <= paramInt2)
        arrayOfInt[paramArrayOfByte[i] - Byte.MIN_VALUE] = arrayOfInt[paramArrayOfByte[i] - Byte.MIN_VALUE] + 1; 
      i = 256;
      int j = paramInt2 + 1;
      label30: while (j > paramInt1) {
        while (arrayOfInt[--i] == 0);
        byte b = (byte)(i + -128);
        int k = arrayOfInt[i];
        while (true) {
          paramArrayOfByte[--j] = b;
          if (--k <= 0)
            continue label30; 
        } 
      } 
    } else {
      int i = paramInt1;
      int j;
      for (j = i; i < paramInt2; j = ++i) {
        byte b = paramArrayOfByte[i + 1];
        while (b < paramArrayOfByte[j]) {
          paramArrayOfByte[j + 1] = paramArrayOfByte[j];
          if (j-- == paramInt1)
            break; 
        } 
        paramArrayOfByte[j + 1] = b;
      } 
    } 
  }
  
  static void sort(float[] paramArrayOfFloat1, int paramInt1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3, int paramInt4) {
    while (paramInt1 <= paramInt2 && Float.isNaN(paramArrayOfFloat1[paramInt2]))
      paramInt2--; 
    int i = paramInt2;
    while (--i >= paramInt1) {
      float f = paramArrayOfFloat1[i];
      if (f != f) {
        paramArrayOfFloat1[i] = paramArrayOfFloat1[paramInt2];
        paramArrayOfFloat1[paramInt2] = f;
        paramInt2--;
      } 
    } 
    doSort(paramArrayOfFloat1, paramInt1, paramInt2, paramArrayOfFloat2, paramInt3, paramInt4);
    for (i = paramInt2; paramInt1 < i; i = m) {
      int m = paramInt1 + i >>> 1;
      float f = paramArrayOfFloat1[m];
      if (f < 0.0F) {
        paramInt1 = m + 1;
        continue;
      } 
    } 
    while (paramInt1 <= paramInt2 && Float.floatToRawIntBits(paramArrayOfFloat1[paramInt1]) < 0)
      paramInt1++; 
    int j = paramInt1;
    int k = paramInt1 - 1;
    while (++j <= paramInt2) {
      float f = paramArrayOfFloat1[j];
      if (f != 0.0F)
        break; 
      if (Float.floatToRawIntBits(f) < 0) {
        paramArrayOfFloat1[j] = 0.0F;
        paramArrayOfFloat1[++k] = -0.0F;
      } 
    } 
  }
  
  private static void doSort(float[] paramArrayOfFloat1, int paramInt1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3, int paramInt4) {
    int k;
    int j;
    float[] arrayOfFloat;
    if (paramInt2 - paramInt1 < 286) {
      sort(paramArrayOfFloat1, paramInt1, paramInt2, true);
      return;
    } 
    int[] arrayOfInt = new int[68];
    byte b = 0;
    arrayOfInt[0] = paramInt1;
    int i = paramInt1;
    while (i < paramInt2) {
      if (paramArrayOfFloat1[i] < paramArrayOfFloat1[i + 1]) {
        while (++i <= paramInt2 && paramArrayOfFloat1[i - 1] <= paramArrayOfFloat1[i]);
      } else if (paramArrayOfFloat1[i] > paramArrayOfFloat1[i + 1]) {
        while (++i <= paramInt2 && paramArrayOfFloat1[i - 1] >= paramArrayOfFloat1[i]);
        int n = arrayOfInt[b] - 1;
        j = i;
        while (++n < --j) {
          k = paramArrayOfFloat1[n];
          paramArrayOfFloat1[n] = paramArrayOfFloat1[j];
          paramArrayOfFloat1[j] = k;
        } 
      } else {
        byte b1 = 33;
        while (++i <= paramInt2 && paramArrayOfFloat1[i - 1] == paramArrayOfFloat1[i]) {
          if (--b1 == 0) {
            sort(paramArrayOfFloat1, paramInt1, paramInt2, true);
            return;
          } 
        } 
      } 
      if (++b == 67) {
        sort(paramArrayOfFloat1, paramInt1, paramInt2, true);
        return;
      } 
      arrayOfInt[b] = i;
    } 
    if (arrayOfInt[b] == paramInt2++) {
      arrayOfInt[++b] = paramInt2;
    } else if (b == 1) {
      return;
    } 
    i = 0;
    boolean bool = true;
    while (bool <<= true < b)
      i = (byte)(i ^ true); 
    int m = paramInt2 - paramInt1;
    if (paramArrayOfFloat2 == null || paramInt4 < m || paramInt3 + m > paramArrayOfFloat2.length) {
      paramArrayOfFloat2 = new float[m];
      paramInt3 = 0;
    } 
    if (i == 0) {
      System.arraycopy(paramArrayOfFloat1, paramInt1, paramArrayOfFloat2, paramInt3, m);
      arrayOfFloat = paramArrayOfFloat1;
      k = 0;
      paramArrayOfFloat1 = paramArrayOfFloat2;
      j = paramInt3 - paramInt1;
    } else {
      arrayOfFloat = paramArrayOfFloat2;
      j = 0;
      k = paramInt3 - paramInt1;
    } 
    while (b > 1) {
      byte b1;
      int n;
      for (n = (b1 = 0) + 2; n <= b; n += 2) {
        int i2 = arrayOfInt[n];
        int i3 = arrayOfInt[n - 1];
        int i4 = arrayOfInt[n - 2];
        int i5 = i4;
        int i6 = i3;
        while (i4 < i2) {
          if (i6 >= i2 || (i5 < i3 && paramArrayOfFloat1[i5 + j] <= paramArrayOfFloat1[i6 + j])) {
            arrayOfFloat[i4 + k] = paramArrayOfFloat1[i5++ + j];
          } else {
            arrayOfFloat[i4 + k] = paramArrayOfFloat1[i6++ + j];
          } 
          i4++;
        } 
        arrayOfInt[++b1] = i2;
      } 
      if ((b & true) != 0) {
        n = paramInt2;
        int i2 = arrayOfInt[b - 1];
        while (--n >= i2)
          arrayOfFloat[n + k] = paramArrayOfFloat1[n + j]; 
        arrayOfInt[++b1] = paramInt2;
      } 
      float[] arrayOfFloat1 = paramArrayOfFloat1;
      paramArrayOfFloat1 = arrayOfFloat;
      arrayOfFloat = arrayOfFloat1;
      int i1 = j;
      j = k;
      k = i1;
      b = b1;
    } 
  }
  
  private static void sort(float[] paramArrayOfFloat, int paramInt1, int paramInt2, boolean paramBoolean) {
    int i = paramInt2 - paramInt1 + 1;
    if (i < 47) {
      if (paramBoolean) {
        int i5 = paramInt1;
        int i6;
        for (i6 = i5; i5 < paramInt2; i6 = ++i5) {
          float f = paramArrayOfFloat[i5 + 1];
          while (f < paramArrayOfFloat[i6]) {
            paramArrayOfFloat[i6 + 1] = paramArrayOfFloat[i6];
            if (i6-- == paramInt1)
              break; 
          } 
          paramArrayOfFloat[i6 + 1] = f;
        } 
      } else {
        do {
          if (paramInt1 >= paramInt2)
            return; 
        } while (paramArrayOfFloat[++paramInt1] >= paramArrayOfFloat[paramInt1 - 1]);
        int i5;
        for (i5 = paramInt1; ++paramInt1 <= paramInt2; i5 = ++paramInt1) {
          float f1 = paramArrayOfFloat[i5];
          float f2 = paramArrayOfFloat[paramInt1];
          if (f1 < f2) {
            f2 = f1;
            f1 = paramArrayOfFloat[paramInt1];
          } 
          while (f1 < paramArrayOfFloat[--i5])
            paramArrayOfFloat[i5 + 2] = paramArrayOfFloat[i5]; 
          paramArrayOfFloat[++i5 + 1] = f1;
          while (f2 < paramArrayOfFloat[--i5])
            paramArrayOfFloat[i5 + 1] = paramArrayOfFloat[i5]; 
          paramArrayOfFloat[i5 + 1] = f2;
        } 
        float f = paramArrayOfFloat[paramInt2];
        while (f < paramArrayOfFloat[--paramInt2])
          paramArrayOfFloat[paramInt2 + 1] = paramArrayOfFloat[paramInt2]; 
        paramArrayOfFloat[paramInt2 + 1] = f;
      } 
      return;
    } 
    int j = (i >> 3) + (i >> 6) + 1;
    int k = paramInt1 + paramInt2 >>> 1;
    int m = k - j;
    int n = m - j;
    int i1 = k + j;
    int i2 = i1 + j;
    if (paramArrayOfFloat[m] < paramArrayOfFloat[n]) {
      float f = paramArrayOfFloat[m];
      paramArrayOfFloat[m] = paramArrayOfFloat[n];
      paramArrayOfFloat[n] = f;
    } 
    if (paramArrayOfFloat[k] < paramArrayOfFloat[m]) {
      float f = paramArrayOfFloat[k];
      paramArrayOfFloat[k] = paramArrayOfFloat[m];
      paramArrayOfFloat[m] = f;
      if (f < paramArrayOfFloat[n]) {
        paramArrayOfFloat[m] = paramArrayOfFloat[n];
        paramArrayOfFloat[n] = f;
      } 
    } 
    if (paramArrayOfFloat[i1] < paramArrayOfFloat[k]) {
      float f = paramArrayOfFloat[i1];
      paramArrayOfFloat[i1] = paramArrayOfFloat[k];
      paramArrayOfFloat[k] = f;
      if (f < paramArrayOfFloat[m]) {
        paramArrayOfFloat[k] = paramArrayOfFloat[m];
        paramArrayOfFloat[m] = f;
        if (f < paramArrayOfFloat[n]) {
          paramArrayOfFloat[m] = paramArrayOfFloat[n];
          paramArrayOfFloat[n] = f;
        } 
      } 
    } 
    if (paramArrayOfFloat[i2] < paramArrayOfFloat[i1]) {
      float f = paramArrayOfFloat[i2];
      paramArrayOfFloat[i2] = paramArrayOfFloat[i1];
      paramArrayOfFloat[i1] = f;
      if (f < paramArrayOfFloat[k]) {
        paramArrayOfFloat[i1] = paramArrayOfFloat[k];
        paramArrayOfFloat[k] = f;
        if (f < paramArrayOfFloat[m]) {
          paramArrayOfFloat[k] = paramArrayOfFloat[m];
          paramArrayOfFloat[m] = f;
          if (f < paramArrayOfFloat[n]) {
            paramArrayOfFloat[m] = paramArrayOfFloat[n];
            paramArrayOfFloat[n] = f;
          } 
        } 
      } 
    } 
    int i3 = paramInt1;
    int i4 = paramInt2;
    if (paramArrayOfFloat[n] != paramArrayOfFloat[m] && paramArrayOfFloat[m] != paramArrayOfFloat[k] && paramArrayOfFloat[k] != paramArrayOfFloat[i1] && paramArrayOfFloat[i1] != paramArrayOfFloat[i2]) {
      float f1 = paramArrayOfFloat[m];
      float f2 = paramArrayOfFloat[i1];
      paramArrayOfFloat[m] = paramArrayOfFloat[paramInt1];
      paramArrayOfFloat[i1] = paramArrayOfFloat[paramInt2];
      while (paramArrayOfFloat[++i3] < f1);
      while (paramArrayOfFloat[--i4] > f2);
      int i5 = i3 - 1;
      label147: while (++i5 <= i4) {
        float f = paramArrayOfFloat[i5];
        if (f < f1) {
          paramArrayOfFloat[i5] = paramArrayOfFloat[i3];
          paramArrayOfFloat[i3] = f;
          i3++;
          continue;
        } 
        if (f > f2) {
          while (paramArrayOfFloat[i4] > f2) {
            if (i4-- == i5)
              break label147; 
          } 
          if (paramArrayOfFloat[i4] < f1) {
            paramArrayOfFloat[i5] = paramArrayOfFloat[i3];
            paramArrayOfFloat[i3] = paramArrayOfFloat[i4];
            i3++;
          } else {
            paramArrayOfFloat[i5] = paramArrayOfFloat[i4];
          } 
          paramArrayOfFloat[i4] = f;
          i4--;
        } 
      } 
      paramArrayOfFloat[paramInt1] = paramArrayOfFloat[i3 - 1];
      paramArrayOfFloat[i3 - 1] = f1;
      paramArrayOfFloat[paramInt2] = paramArrayOfFloat[i4 + 1];
      paramArrayOfFloat[i4 + 1] = f2;
      sort(paramArrayOfFloat, paramInt1, i3 - 2, paramBoolean);
      sort(paramArrayOfFloat, i4 + 2, paramInt2, false);
      if (i3 < n && i2 < i4) {
        while (paramArrayOfFloat[i3] == f1)
          i3++; 
        while (paramArrayOfFloat[i4] == f2)
          i4--; 
        i5 = i3 - 1;
        while (++i5 <= i4) {
          float f = paramArrayOfFloat[i5];
          if (f == f1) {
            paramArrayOfFloat[i5] = paramArrayOfFloat[i3];
            paramArrayOfFloat[i3] = f;
            i3++;
            continue;
          } 
          if (f == f2) {
            while (paramArrayOfFloat[i4] == f2) {
              if (i4-- == i5)
                // Byte code: goto -> 1067 
            } 
            if (paramArrayOfFloat[i4] == f1) {
              paramArrayOfFloat[i5] = paramArrayOfFloat[i3];
              paramArrayOfFloat[i3] = paramArrayOfFloat[i4];
              i3++;
            } else {
              paramArrayOfFloat[i5] = paramArrayOfFloat[i4];
            } 
            paramArrayOfFloat[i4] = f;
            i4--;
          } 
        } 
      } 
      sort(paramArrayOfFloat, i3, i4, false);
    } else {
      float f = paramArrayOfFloat[k];
      for (int i5 = i3; i5 <= i4; i5++) {
        if (paramArrayOfFloat[i5] != f) {
          float f1 = paramArrayOfFloat[i5];
          if (f1 < f) {
            paramArrayOfFloat[i5] = paramArrayOfFloat[i3];
            paramArrayOfFloat[i3] = f1;
            i3++;
          } else {
            while (paramArrayOfFloat[i4] > f)
              i4--; 
            if (paramArrayOfFloat[i4] < f) {
              paramArrayOfFloat[i5] = paramArrayOfFloat[i3];
              paramArrayOfFloat[i3] = paramArrayOfFloat[i4];
              i3++;
            } else {
              paramArrayOfFloat[i5] = paramArrayOfFloat[i4];
            } 
            paramArrayOfFloat[i4] = f1;
            i4--;
          } 
        } 
      } 
      sort(paramArrayOfFloat, paramInt1, i3 - 1, paramBoolean);
      sort(paramArrayOfFloat, i4 + 1, paramInt2, false);
    } 
  }
  
  static void sort(double[] paramArrayOfDouble1, int paramInt1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3, int paramInt4) {
    while (paramInt1 <= paramInt2 && Double.isNaN(paramArrayOfDouble1[paramInt2]))
      paramInt2--; 
    int i = paramInt2;
    while (--i >= paramInt1) {
      double d = paramArrayOfDouble1[i];
      if (d != d) {
        paramArrayOfDouble1[i] = paramArrayOfDouble1[paramInt2];
        paramArrayOfDouble1[paramInt2] = d;
        paramInt2--;
      } 
    } 
    doSort(paramArrayOfDouble1, paramInt1, paramInt2, paramArrayOfDouble2, paramInt3, paramInt4);
    for (i = paramInt2; paramInt1 < i; i = m) {
      int m = paramInt1 + i >>> 1;
      double d = paramArrayOfDouble1[m];
      if (d < 0.0D) {
        paramInt1 = m + 1;
        continue;
      } 
    } 
    while (paramInt1 <= paramInt2 && Double.doubleToRawLongBits(paramArrayOfDouble1[paramInt1]) < 0L)
      paramInt1++; 
    int j = paramInt1;
    int k = paramInt1 - 1;
    while (++j <= paramInt2) {
      double d = paramArrayOfDouble1[j];
      if (d != 0.0D)
        break; 
      if (Double.doubleToRawLongBits(d) < 0L) {
        paramArrayOfDouble1[j] = 0.0D;
        paramArrayOfDouble1[++k] = -0.0D;
      } 
    } 
  }
  
  private static void doSort(double[] paramArrayOfDouble1, int paramInt1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3, int paramInt4) {
    int k;
    int j;
    double[] arrayOfDouble;
    if (paramInt2 - paramInt1 < 286) {
      sort(paramArrayOfDouble1, paramInt1, paramInt2, true);
      return;
    } 
    int[] arrayOfInt = new int[68];
    byte b = 0;
    arrayOfInt[0] = paramInt1;
    int i = paramInt1;
    while (i < paramInt2) {
      if (paramArrayOfDouble1[i] < paramArrayOfDouble1[i + 1]) {
        while (++i <= paramInt2 && paramArrayOfDouble1[i - 1] <= paramArrayOfDouble1[i]);
      } else if (paramArrayOfDouble1[i] > paramArrayOfDouble1[i + 1]) {
        while (++i <= paramInt2 && paramArrayOfDouble1[i - 1] >= paramArrayOfDouble1[i]);
        int n = arrayOfInt[b] - 1;
        j = i;
        while (++n < --j) {
          k = paramArrayOfDouble1[n];
          paramArrayOfDouble1[n] = paramArrayOfDouble1[j];
          paramArrayOfDouble1[j] = k;
        } 
      } else {
        byte b1 = 33;
        while (++i <= paramInt2 && paramArrayOfDouble1[i - 1] == paramArrayOfDouble1[i]) {
          if (--b1 == 0) {
            sort(paramArrayOfDouble1, paramInt1, paramInt2, true);
            return;
          } 
        } 
      } 
      if (++b == 67) {
        sort(paramArrayOfDouble1, paramInt1, paramInt2, true);
        return;
      } 
      arrayOfInt[b] = i;
    } 
    if (arrayOfInt[b] == paramInt2++) {
      arrayOfInt[++b] = paramInt2;
    } else if (b == 1) {
      return;
    } 
    i = 0;
    boolean bool = true;
    while (bool <<= true < b)
      i = (byte)(i ^ true); 
    int m = paramInt2 - paramInt1;
    if (paramArrayOfDouble2 == null || paramInt4 < m || paramInt3 + m > paramArrayOfDouble2.length) {
      paramArrayOfDouble2 = new double[m];
      paramInt3 = 0;
    } 
    if (i == 0) {
      System.arraycopy(paramArrayOfDouble1, paramInt1, paramArrayOfDouble2, paramInt3, m);
      arrayOfDouble = paramArrayOfDouble1;
      k = 0;
      paramArrayOfDouble1 = paramArrayOfDouble2;
      j = paramInt3 - paramInt1;
    } else {
      arrayOfDouble = paramArrayOfDouble2;
      j = 0;
      k = paramInt3 - paramInt1;
    } 
    while (b > 1) {
      byte b1;
      int n;
      for (n = (b1 = 0) + 2; n <= b; n += 2) {
        int i2 = arrayOfInt[n];
        int i3 = arrayOfInt[n - 1];
        int i4 = arrayOfInt[n - 2];
        int i5 = i4;
        int i6 = i3;
        while (i4 < i2) {
          if (i6 >= i2 || (i5 < i3 && paramArrayOfDouble1[i5 + j] <= paramArrayOfDouble1[i6 + j])) {
            arrayOfDouble[i4 + k] = paramArrayOfDouble1[i5++ + j];
          } else {
            arrayOfDouble[i4 + k] = paramArrayOfDouble1[i6++ + j];
          } 
          i4++;
        } 
        arrayOfInt[++b1] = i2;
      } 
      if ((b & true) != 0) {
        n = paramInt2;
        int i2 = arrayOfInt[b - 1];
        while (--n >= i2)
          arrayOfDouble[n + k] = paramArrayOfDouble1[n + j]; 
        arrayOfInt[++b1] = paramInt2;
      } 
      double[] arrayOfDouble1 = paramArrayOfDouble1;
      paramArrayOfDouble1 = arrayOfDouble;
      arrayOfDouble = arrayOfDouble1;
      int i1 = j;
      j = k;
      k = i1;
      b = b1;
    } 
  }
  
  private static void sort(double[] paramArrayOfDouble, int paramInt1, int paramInt2, boolean paramBoolean) {
    int i = paramInt2 - paramInt1 + 1;
    if (i < 47) {
      if (paramBoolean) {
        int i5 = paramInt1;
        int i6;
        for (i6 = i5; i5 < paramInt2; i6 = ++i5) {
          double d = paramArrayOfDouble[i5 + 1];
          while (d < paramArrayOfDouble[i6]) {
            paramArrayOfDouble[i6 + 1] = paramArrayOfDouble[i6];
            if (i6-- == paramInt1)
              break; 
          } 
          paramArrayOfDouble[i6 + 1] = d;
        } 
      } else {
        do {
          if (paramInt1 >= paramInt2)
            return; 
        } while (paramArrayOfDouble[++paramInt1] >= paramArrayOfDouble[paramInt1 - 1]);
        int i5;
        for (i5 = paramInt1; ++paramInt1 <= paramInt2; i5 = ++paramInt1) {
          double d1 = paramArrayOfDouble[i5];
          double d2 = paramArrayOfDouble[paramInt1];
          if (d1 < d2) {
            d2 = d1;
            d1 = paramArrayOfDouble[paramInt1];
          } 
          while (d1 < paramArrayOfDouble[--i5])
            paramArrayOfDouble[i5 + 2] = paramArrayOfDouble[i5]; 
          paramArrayOfDouble[++i5 + 1] = d1;
          while (d2 < paramArrayOfDouble[--i5])
            paramArrayOfDouble[i5 + 1] = paramArrayOfDouble[i5]; 
          paramArrayOfDouble[i5 + 1] = d2;
        } 
        double d = paramArrayOfDouble[paramInt2];
        while (d < paramArrayOfDouble[--paramInt2])
          paramArrayOfDouble[paramInt2 + 1] = paramArrayOfDouble[paramInt2]; 
        paramArrayOfDouble[paramInt2 + 1] = d;
      } 
      return;
    } 
    int j = (i >> 3) + (i >> 6) + 1;
    int k = paramInt1 + paramInt2 >>> 1;
    int m = k - j;
    int n = m - j;
    int i1 = k + j;
    int i2 = i1 + j;
    if (paramArrayOfDouble[m] < paramArrayOfDouble[n]) {
      double d = paramArrayOfDouble[m];
      paramArrayOfDouble[m] = paramArrayOfDouble[n];
      paramArrayOfDouble[n] = d;
    } 
    if (paramArrayOfDouble[k] < paramArrayOfDouble[m]) {
      double d = paramArrayOfDouble[k];
      paramArrayOfDouble[k] = paramArrayOfDouble[m];
      paramArrayOfDouble[m] = d;
      if (d < paramArrayOfDouble[n]) {
        paramArrayOfDouble[m] = paramArrayOfDouble[n];
        paramArrayOfDouble[n] = d;
      } 
    } 
    if (paramArrayOfDouble[i1] < paramArrayOfDouble[k]) {
      double d = paramArrayOfDouble[i1];
      paramArrayOfDouble[i1] = paramArrayOfDouble[k];
      paramArrayOfDouble[k] = d;
      if (d < paramArrayOfDouble[m]) {
        paramArrayOfDouble[k] = paramArrayOfDouble[m];
        paramArrayOfDouble[m] = d;
        if (d < paramArrayOfDouble[n]) {
          paramArrayOfDouble[m] = paramArrayOfDouble[n];
          paramArrayOfDouble[n] = d;
        } 
      } 
    } 
    if (paramArrayOfDouble[i2] < paramArrayOfDouble[i1]) {
      double d = paramArrayOfDouble[i2];
      paramArrayOfDouble[i2] = paramArrayOfDouble[i1];
      paramArrayOfDouble[i1] = d;
      if (d < paramArrayOfDouble[k]) {
        paramArrayOfDouble[i1] = paramArrayOfDouble[k];
        paramArrayOfDouble[k] = d;
        if (d < paramArrayOfDouble[m]) {
          paramArrayOfDouble[k] = paramArrayOfDouble[m];
          paramArrayOfDouble[m] = d;
          if (d < paramArrayOfDouble[n]) {
            paramArrayOfDouble[m] = paramArrayOfDouble[n];
            paramArrayOfDouble[n] = d;
          } 
        } 
      } 
    } 
    int i3 = paramInt1;
    int i4 = paramInt2;
    if (paramArrayOfDouble[n] != paramArrayOfDouble[m] && paramArrayOfDouble[m] != paramArrayOfDouble[k] && paramArrayOfDouble[k] != paramArrayOfDouble[i1] && paramArrayOfDouble[i1] != paramArrayOfDouble[i2]) {
      double d1 = paramArrayOfDouble[m];
      double d2 = paramArrayOfDouble[i1];
      paramArrayOfDouble[m] = paramArrayOfDouble[paramInt1];
      paramArrayOfDouble[i1] = paramArrayOfDouble[paramInt2];
      while (paramArrayOfDouble[++i3] < d1);
      while (paramArrayOfDouble[--i4] > d2);
      int i5 = i3 - 1;
      label147: while (++i5 <= i4) {
        double d = paramArrayOfDouble[i5];
        if (d < d1) {
          paramArrayOfDouble[i5] = paramArrayOfDouble[i3];
          paramArrayOfDouble[i3] = d;
          i3++;
          continue;
        } 
        if (d > d2) {
          while (paramArrayOfDouble[i4] > d2) {
            if (i4-- == i5)
              break label147; 
          } 
          if (paramArrayOfDouble[i4] < d1) {
            paramArrayOfDouble[i5] = paramArrayOfDouble[i3];
            paramArrayOfDouble[i3] = paramArrayOfDouble[i4];
            i3++;
          } else {
            paramArrayOfDouble[i5] = paramArrayOfDouble[i4];
          } 
          paramArrayOfDouble[i4] = d;
          i4--;
        } 
      } 
      paramArrayOfDouble[paramInt1] = paramArrayOfDouble[i3 - 1];
      paramArrayOfDouble[i3 - 1] = d1;
      paramArrayOfDouble[paramInt2] = paramArrayOfDouble[i4 + 1];
      paramArrayOfDouble[i4 + 1] = d2;
      sort(paramArrayOfDouble, paramInt1, i3 - 2, paramBoolean);
      sort(paramArrayOfDouble, i4 + 2, paramInt2, false);
      if (i3 < n && i2 < i4) {
        while (paramArrayOfDouble[i3] == d1)
          i3++; 
        while (paramArrayOfDouble[i4] == d2)
          i4--; 
        i5 = i3 - 1;
        while (++i5 <= i4) {
          double d = paramArrayOfDouble[i5];
          if (d == d1) {
            paramArrayOfDouble[i5] = paramArrayOfDouble[i3];
            paramArrayOfDouble[i3] = d;
            i3++;
            continue;
          } 
          if (d == d2) {
            while (paramArrayOfDouble[i4] == d2) {
              if (i4-- == i5)
                // Byte code: goto -> 1067 
            } 
            if (paramArrayOfDouble[i4] == d1) {
              paramArrayOfDouble[i5] = paramArrayOfDouble[i3];
              paramArrayOfDouble[i3] = paramArrayOfDouble[i4];
              i3++;
            } else {
              paramArrayOfDouble[i5] = paramArrayOfDouble[i4];
            } 
            paramArrayOfDouble[i4] = d;
            i4--;
          } 
        } 
      } 
      sort(paramArrayOfDouble, i3, i4, false);
    } else {
      double d = paramArrayOfDouble[k];
      for (int i5 = i3; i5 <= i4; i5++) {
        if (paramArrayOfDouble[i5] != d) {
          double d1 = paramArrayOfDouble[i5];
          if (d1 < d) {
            paramArrayOfDouble[i5] = paramArrayOfDouble[i3];
            paramArrayOfDouble[i3] = d1;
            i3++;
          } else {
            while (paramArrayOfDouble[i4] > d)
              i4--; 
            if (paramArrayOfDouble[i4] < d) {
              paramArrayOfDouble[i5] = paramArrayOfDouble[i3];
              paramArrayOfDouble[i3] = paramArrayOfDouble[i4];
              i3++;
            } else {
              paramArrayOfDouble[i5] = paramArrayOfDouble[i4];
            } 
            paramArrayOfDouble[i4] = d1;
            i4--;
          } 
        } 
      } 
      sort(paramArrayOfDouble, paramInt1, i3 - 1, paramBoolean);
      sort(paramArrayOfDouble, i4 + 1, paramInt2, false);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\DualPivotQuicksort.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */