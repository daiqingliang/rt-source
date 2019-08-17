package java.util;

import java.util.concurrent.CountedCompleter;

class ArraysParallelSortHelpers {
  static final class EmptyCompleter extends CountedCompleter<Void> {
    static final long serialVersionUID = 2446542900576103244L;
    
    EmptyCompleter(CountedCompleter<?> param1CountedCompleter) { super(param1CountedCompleter); }
    
    public final void compute() {}
  }
  
  static final class FJByte {
    static final class Merger extends CountedCompleter<Void> {
      static final long serialVersionUID = 2446542900576103244L;
      
      final byte[] a;
      
      final byte[] w;
      
      final int lbase;
      
      final int lsize;
      
      final int rbase;
      
      final int rsize;
      
      final int wbase;
      
      final int gran;
      
      Merger(CountedCompleter<?> param2CountedCompleter, byte[] param2ArrayOfByte1, byte[] param2ArrayOfByte2, int param2Int1, int param2Int2, int param2Int3, int param2Int4, int param2Int5, int param2Int6) {
        super(param2CountedCompleter);
        this.a = param2ArrayOfByte1;
        this.w = param2ArrayOfByte2;
        this.lbase = param2Int1;
        this.lsize = param2Int2;
        this.rbase = param2Int3;
        this.rsize = param2Int4;
        this.wbase = param2Int5;
        this.gran = param2Int6;
      }
      
      public final void compute() {
        byte[] arrayOfByte1 = this.a;
        byte[] arrayOfByte2 = this.w;
        int i = this.lbase;
        int j = this.lsize;
        int k = this.rbase;
        int m = this.rsize;
        int n = this.wbase;
        int i1 = this.gran;
        if (arrayOfByte1 == null || arrayOfByte2 == null || i < 0 || k < 0 || n < 0)
          throw new IllegalStateException(); 
        while (true) {
          int i5;
          int i4;
          if (j >= m) {
            if (j <= i1)
              break; 
            i5 = m;
            byte b = arrayOfByte1[(i4 = j >>> 1) + i];
            int i6;
            for (i6 = 0; i6 < i5; i6 = i7 + 1) {
              int i7 = i6 + i5 >>> 1;
              if (b <= arrayOfByte1[i7 + k]) {
                i5 = i7;
                continue;
              } 
            } 
          } else {
            if (m <= i1)
              break; 
            i4 = j;
            byte b = arrayOfByte1[(i5 = m >>> 1) + k];
            int i6;
            for (i6 = 0; i6 < i4; i6 = i7 + 1) {
              int i7 = i6 + i4 >>> 1;
              if (b <= arrayOfByte1[i7 + i]) {
                i4 = i7;
                continue;
              } 
            } 
          } 
          Merger merger = new Merger(this, arrayOfByte1, arrayOfByte2, i + i4, j - i4, k + i5, m - i5, n + i4 + i5, i1);
          m = i5;
          j = i4;
          addToPendingCount(1);
          merger.fork();
        } 
        int i2 = i + j;
        int i3 = k + m;
        while (i < i2 && k < i3) {
          byte b;
          byte b1;
          byte b2;
          if ((b1 = arrayOfByte1[i]) <= (b2 = arrayOfByte1[k])) {
            i++;
            b = b1;
          } else {
            k++;
            b = b2;
          } 
          arrayOfByte2[n++] = b;
        } 
        if (k < i3) {
          System.arraycopy(arrayOfByte1, k, arrayOfByte2, n, i3 - k);
        } else if (i < i2) {
          System.arraycopy(arrayOfByte1, i, arrayOfByte2, n, i2 - i);
        } 
        tryComplete();
      }
    }
    
    static final class Sorter extends CountedCompleter<Void> {
      static final long serialVersionUID = 2446542900576103244L;
      
      final byte[] a;
      
      final byte[] w;
      
      final int base;
      
      final int size;
      
      final int wbase;
      
      final int gran;
      
      Sorter(CountedCompleter<?> param2CountedCompleter, byte[] param2ArrayOfByte1, byte[] param2ArrayOfByte2, int param2Int1, int param2Int2, int param2Int3, int param2Int4) {
        super(param2CountedCompleter);
        this.a = param2ArrayOfByte1;
        this.w = param2ArrayOfByte2;
        this.base = param2Int1;
        this.size = param2Int2;
        this.wbase = param2Int3;
        this.gran = param2Int4;
      }
      
      public final void compute() {
        ArraysParallelSortHelpers.EmptyCompleter emptyCompleter = this;
        byte[] arrayOfByte1 = this.a;
        byte[] arrayOfByte2 = this.w;
        int i = this.base;
        int j = this.size;
        int k = this.wbase;
        int m = this.gran;
        while (j > m) {
          int n = j >>> 1;
          int i1 = n >>> 1;
          int i2 = n + i1;
          ArraysParallelSortHelpers.Relay relay1 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJByte.Merger(emptyCompleter, arrayOfByte2, arrayOfByte1, k, n, k + n, j - n, i, m));
          ArraysParallelSortHelpers.Relay relay2 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJByte.Merger(relay1, arrayOfByte1, arrayOfByte2, i + n, i1, i + i2, j - i2, k + n, m));
          (new Sorter(relay2, arrayOfByte1, arrayOfByte2, i + i2, j - i2, k + i2, m)).fork();
          (new Sorter(relay2, arrayOfByte1, arrayOfByte2, i + n, i1, k + n, m)).fork();
          ArraysParallelSortHelpers.Relay relay3 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJByte.Merger(relay1, arrayOfByte1, arrayOfByte2, i, i1, i + i1, n - i1, k, m));
          (new Sorter(relay3, arrayOfByte1, arrayOfByte2, i + i1, n - i1, k + i1, m)).fork();
          emptyCompleter = new ArraysParallelSortHelpers.EmptyCompleter(relay3);
          j = i1;
        } 
        DualPivotQuicksort.sort(arrayOfByte1, i, i + j - 1);
        emptyCompleter.tryComplete();
      }
    }
  }
  
  static final class FJChar {
    static final class Merger extends CountedCompleter<Void> {
      static final long serialVersionUID = 2446542900576103244L;
      
      final char[] a;
      
      final char[] w;
      
      final int lbase;
      
      final int lsize;
      
      final int rbase;
      
      final int rsize;
      
      final int wbase;
      
      final int gran;
      
      Merger(CountedCompleter<?> param2CountedCompleter, char[] param2ArrayOfChar1, char[] param2ArrayOfChar2, int param2Int1, int param2Int2, int param2Int3, int param2Int4, int param2Int5, int param2Int6) {
        super(param2CountedCompleter);
        this.a = param2ArrayOfChar1;
        this.w = param2ArrayOfChar2;
        this.lbase = param2Int1;
        this.lsize = param2Int2;
        this.rbase = param2Int3;
        this.rsize = param2Int4;
        this.wbase = param2Int5;
        this.gran = param2Int6;
      }
      
      public final void compute() {
        char[] arrayOfChar1 = this.a;
        char[] arrayOfChar2 = this.w;
        int i = this.lbase;
        int j = this.lsize;
        int k = this.rbase;
        int m = this.rsize;
        int n = this.wbase;
        int i1 = this.gran;
        if (arrayOfChar1 == null || arrayOfChar2 == null || i < 0 || k < 0 || n < 0)
          throw new IllegalStateException(); 
        while (true) {
          int i5;
          int i4;
          if (j >= m) {
            if (j <= i1)
              break; 
            i5 = m;
            char c = arrayOfChar1[(i4 = j >>> 1) + i];
            int i6;
            for (i6 = 0; i6 < i5; i6 = i7 + 1) {
              int i7 = i6 + i5 >>> 1;
              if (c <= arrayOfChar1[i7 + k]) {
                i5 = i7;
                continue;
              } 
            } 
          } else {
            if (m <= i1)
              break; 
            i4 = j;
            char c = arrayOfChar1[(i5 = m >>> 1) + k];
            int i6;
            for (i6 = 0; i6 < i4; i6 = i7 + 1) {
              int i7 = i6 + i4 >>> 1;
              if (c <= arrayOfChar1[i7 + i]) {
                i4 = i7;
                continue;
              } 
            } 
          } 
          Merger merger = new Merger(this, arrayOfChar1, arrayOfChar2, i + i4, j - i4, k + i5, m - i5, n + i4 + i5, i1);
          m = i5;
          j = i4;
          addToPendingCount(1);
          merger.fork();
        } 
        int i2 = i + j;
        int i3 = k + m;
        while (i < i2 && k < i3) {
          char c;
          char c1;
          char c2;
          if ((c1 = arrayOfChar1[i]) <= (c2 = arrayOfChar1[k])) {
            i++;
            c = c1;
          } else {
            k++;
            c = c2;
          } 
          arrayOfChar2[n++] = c;
        } 
        if (k < i3) {
          System.arraycopy(arrayOfChar1, k, arrayOfChar2, n, i3 - k);
        } else if (i < i2) {
          System.arraycopy(arrayOfChar1, i, arrayOfChar2, n, i2 - i);
        } 
        tryComplete();
      }
    }
    
    static final class Sorter extends CountedCompleter<Void> {
      static final long serialVersionUID = 2446542900576103244L;
      
      final char[] a;
      
      final char[] w;
      
      final int base;
      
      final int size;
      
      final int wbase;
      
      final int gran;
      
      Sorter(CountedCompleter<?> param2CountedCompleter, char[] param2ArrayOfChar1, char[] param2ArrayOfChar2, int param2Int1, int param2Int2, int param2Int3, int param2Int4) {
        super(param2CountedCompleter);
        this.a = param2ArrayOfChar1;
        this.w = param2ArrayOfChar2;
        this.base = param2Int1;
        this.size = param2Int2;
        this.wbase = param2Int3;
        this.gran = param2Int4;
      }
      
      public final void compute() {
        ArraysParallelSortHelpers.EmptyCompleter emptyCompleter = this;
        char[] arrayOfChar1 = this.a;
        char[] arrayOfChar2 = this.w;
        int i = this.base;
        int j = this.size;
        int k = this.wbase;
        int m = this.gran;
        while (j > m) {
          int n = j >>> 1;
          int i1 = n >>> 1;
          int i2 = n + i1;
          ArraysParallelSortHelpers.Relay relay1 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJChar.Merger(emptyCompleter, arrayOfChar2, arrayOfChar1, k, n, k + n, j - n, i, m));
          ArraysParallelSortHelpers.Relay relay2 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJChar.Merger(relay1, arrayOfChar1, arrayOfChar2, i + n, i1, i + i2, j - i2, k + n, m));
          (new Sorter(relay2, arrayOfChar1, arrayOfChar2, i + i2, j - i2, k + i2, m)).fork();
          (new Sorter(relay2, arrayOfChar1, arrayOfChar2, i + n, i1, k + n, m)).fork();
          ArraysParallelSortHelpers.Relay relay3 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJChar.Merger(relay1, arrayOfChar1, arrayOfChar2, i, i1, i + i1, n - i1, k, m));
          (new Sorter(relay3, arrayOfChar1, arrayOfChar2, i + i1, n - i1, k + i1, m)).fork();
          emptyCompleter = new ArraysParallelSortHelpers.EmptyCompleter(relay3);
          j = i1;
        } 
        DualPivotQuicksort.sort(arrayOfChar1, i, i + j - 1, arrayOfChar2, k, j);
        emptyCompleter.tryComplete();
      }
    }
  }
  
  static final class FJDouble {
    static final class Merger extends CountedCompleter<Void> {
      static final long serialVersionUID = 2446542900576103244L;
      
      final double[] a;
      
      final double[] w;
      
      final int lbase;
      
      final int lsize;
      
      final int rbase;
      
      final int rsize;
      
      final int wbase;
      
      final int gran;
      
      Merger(CountedCompleter<?> param2CountedCompleter, double[] param2ArrayOfDouble1, double[] param2ArrayOfDouble2, int param2Int1, int param2Int2, int param2Int3, int param2Int4, int param2Int5, int param2Int6) {
        super(param2CountedCompleter);
        this.a = param2ArrayOfDouble1;
        this.w = param2ArrayOfDouble2;
        this.lbase = param2Int1;
        this.lsize = param2Int2;
        this.rbase = param2Int3;
        this.rsize = param2Int4;
        this.wbase = param2Int5;
        this.gran = param2Int6;
      }
      
      public final void compute() {
        double[] arrayOfDouble1 = this.a;
        double[] arrayOfDouble2 = this.w;
        int i = this.lbase;
        int j = this.lsize;
        int k = this.rbase;
        int m = this.rsize;
        int n = this.wbase;
        int i1 = this.gran;
        if (arrayOfDouble1 == null || arrayOfDouble2 == null || i < 0 || k < 0 || n < 0)
          throw new IllegalStateException(); 
        while (true) {
          int i5;
          int i4;
          if (j >= m) {
            if (j <= i1)
              break; 
            i5 = m;
            double d = arrayOfDouble1[(i4 = j >>> 1) + i];
            int i6;
            for (i6 = 0; i6 < i5; i6 = i7 + 1) {
              int i7 = i6 + i5 >>> 1;
              if (d <= arrayOfDouble1[i7 + k]) {
                i5 = i7;
                continue;
              } 
            } 
          } else {
            if (m <= i1)
              break; 
            i4 = j;
            double d = arrayOfDouble1[(i5 = m >>> 1) + k];
            int i6;
            for (i6 = 0; i6 < i4; i6 = i7 + 1) {
              int i7 = i6 + i4 >>> 1;
              if (d <= arrayOfDouble1[i7 + i]) {
                i4 = i7;
                continue;
              } 
            } 
          } 
          Merger merger = new Merger(this, arrayOfDouble1, arrayOfDouble2, i + i4, j - i4, k + i5, m - i5, n + i4 + i5, i1);
          m = i5;
          j = i4;
          addToPendingCount(1);
          merger.fork();
        } 
        int i2 = i + j;
        int i3 = k + m;
        while (i < i2 && k < i3) {
          double d1;
          double d2;
          double d3;
          if ((d2 = arrayOfDouble1[i]) <= (d3 = arrayOfDouble1[k])) {
            i++;
            d1 = d2;
          } else {
            k++;
            d1 = d3;
          } 
          arrayOfDouble2[n++] = d1;
        } 
        if (k < i3) {
          System.arraycopy(arrayOfDouble1, k, arrayOfDouble2, n, i3 - k);
        } else if (i < i2) {
          System.arraycopy(arrayOfDouble1, i, arrayOfDouble2, n, i2 - i);
        } 
        tryComplete();
      }
    }
    
    static final class Sorter extends CountedCompleter<Void> {
      static final long serialVersionUID = 2446542900576103244L;
      
      final double[] a;
      
      final double[] w;
      
      final int base;
      
      final int size;
      
      final int wbase;
      
      final int gran;
      
      Sorter(CountedCompleter<?> param2CountedCompleter, double[] param2ArrayOfDouble1, double[] param2ArrayOfDouble2, int param2Int1, int param2Int2, int param2Int3, int param2Int4) {
        super(param2CountedCompleter);
        this.a = param2ArrayOfDouble1;
        this.w = param2ArrayOfDouble2;
        this.base = param2Int1;
        this.size = param2Int2;
        this.wbase = param2Int3;
        this.gran = param2Int4;
      }
      
      public final void compute() {
        ArraysParallelSortHelpers.EmptyCompleter emptyCompleter = this;
        double[] arrayOfDouble1 = this.a;
        double[] arrayOfDouble2 = this.w;
        int i = this.base;
        int j = this.size;
        int k = this.wbase;
        int m = this.gran;
        while (j > m) {
          int n = j >>> 1;
          int i1 = n >>> 1;
          int i2 = n + i1;
          ArraysParallelSortHelpers.Relay relay1 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJDouble.Merger(emptyCompleter, arrayOfDouble2, arrayOfDouble1, k, n, k + n, j - n, i, m));
          ArraysParallelSortHelpers.Relay relay2 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJDouble.Merger(relay1, arrayOfDouble1, arrayOfDouble2, i + n, i1, i + i2, j - i2, k + n, m));
          (new Sorter(relay2, arrayOfDouble1, arrayOfDouble2, i + i2, j - i2, k + i2, m)).fork();
          (new Sorter(relay2, arrayOfDouble1, arrayOfDouble2, i + n, i1, k + n, m)).fork();
          ArraysParallelSortHelpers.Relay relay3 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJDouble.Merger(relay1, arrayOfDouble1, arrayOfDouble2, i, i1, i + i1, n - i1, k, m));
          (new Sorter(relay3, arrayOfDouble1, arrayOfDouble2, i + i1, n - i1, k + i1, m)).fork();
          emptyCompleter = new ArraysParallelSortHelpers.EmptyCompleter(relay3);
          j = i1;
        } 
        DualPivotQuicksort.sort(arrayOfDouble1, i, i + j - 1, arrayOfDouble2, k, j);
        emptyCompleter.tryComplete();
      }
    }
  }
  
  static final class FJFloat {
    static final class Merger extends CountedCompleter<Void> {
      static final long serialVersionUID = 2446542900576103244L;
      
      final float[] a;
      
      final float[] w;
      
      final int lbase;
      
      final int lsize;
      
      final int rbase;
      
      final int rsize;
      
      final int wbase;
      
      final int gran;
      
      Merger(CountedCompleter<?> param2CountedCompleter, float[] param2ArrayOfFloat1, float[] param2ArrayOfFloat2, int param2Int1, int param2Int2, int param2Int3, int param2Int4, int param2Int5, int param2Int6) {
        super(param2CountedCompleter);
        this.a = param2ArrayOfFloat1;
        this.w = param2ArrayOfFloat2;
        this.lbase = param2Int1;
        this.lsize = param2Int2;
        this.rbase = param2Int3;
        this.rsize = param2Int4;
        this.wbase = param2Int5;
        this.gran = param2Int6;
      }
      
      public final void compute() {
        float[] arrayOfFloat1 = this.a;
        float[] arrayOfFloat2 = this.w;
        int i = this.lbase;
        int j = this.lsize;
        int k = this.rbase;
        int m = this.rsize;
        int n = this.wbase;
        int i1 = this.gran;
        if (arrayOfFloat1 == null || arrayOfFloat2 == null || i < 0 || k < 0 || n < 0)
          throw new IllegalStateException(); 
        while (true) {
          int i5;
          int i4;
          if (j >= m) {
            if (j <= i1)
              break; 
            i5 = m;
            float f = arrayOfFloat1[(i4 = j >>> 1) + i];
            int i6;
            for (i6 = 0; i6 < i5; i6 = i7 + 1) {
              int i7 = i6 + i5 >>> 1;
              if (f <= arrayOfFloat1[i7 + k]) {
                i5 = i7;
                continue;
              } 
            } 
          } else {
            if (m <= i1)
              break; 
            i4 = j;
            float f = arrayOfFloat1[(i5 = m >>> 1) + k];
            int i6;
            for (i6 = 0; i6 < i4; i6 = i7 + 1) {
              int i7 = i6 + i4 >>> 1;
              if (f <= arrayOfFloat1[i7 + i]) {
                i4 = i7;
                continue;
              } 
            } 
          } 
          Merger merger = new Merger(this, arrayOfFloat1, arrayOfFloat2, i + i4, j - i4, k + i5, m - i5, n + i4 + i5, i1);
          m = i5;
          j = i4;
          addToPendingCount(1);
          merger.fork();
        } 
        int i2 = i + j;
        int i3 = k + m;
        while (i < i2 && k < i3) {
          float f1;
          float f2;
          float f3;
          if ((f2 = arrayOfFloat1[i]) <= (f3 = arrayOfFloat1[k])) {
            i++;
            f1 = f2;
          } else {
            k++;
            f1 = f3;
          } 
          arrayOfFloat2[n++] = f1;
        } 
        if (k < i3) {
          System.arraycopy(arrayOfFloat1, k, arrayOfFloat2, n, i3 - k);
        } else if (i < i2) {
          System.arraycopy(arrayOfFloat1, i, arrayOfFloat2, n, i2 - i);
        } 
        tryComplete();
      }
    }
    
    static final class Sorter extends CountedCompleter<Void> {
      static final long serialVersionUID = 2446542900576103244L;
      
      final float[] a;
      
      final float[] w;
      
      final int base;
      
      final int size;
      
      final int wbase;
      
      final int gran;
      
      Sorter(CountedCompleter<?> param2CountedCompleter, float[] param2ArrayOfFloat1, float[] param2ArrayOfFloat2, int param2Int1, int param2Int2, int param2Int3, int param2Int4) {
        super(param2CountedCompleter);
        this.a = param2ArrayOfFloat1;
        this.w = param2ArrayOfFloat2;
        this.base = param2Int1;
        this.size = param2Int2;
        this.wbase = param2Int3;
        this.gran = param2Int4;
      }
      
      public final void compute() {
        ArraysParallelSortHelpers.EmptyCompleter emptyCompleter = this;
        float[] arrayOfFloat1 = this.a;
        float[] arrayOfFloat2 = this.w;
        int i = this.base;
        int j = this.size;
        int k = this.wbase;
        int m = this.gran;
        while (j > m) {
          int n = j >>> 1;
          int i1 = n >>> 1;
          int i2 = n + i1;
          ArraysParallelSortHelpers.Relay relay1 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJFloat.Merger(emptyCompleter, arrayOfFloat2, arrayOfFloat1, k, n, k + n, j - n, i, m));
          ArraysParallelSortHelpers.Relay relay2 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJFloat.Merger(relay1, arrayOfFloat1, arrayOfFloat2, i + n, i1, i + i2, j - i2, k + n, m));
          (new Sorter(relay2, arrayOfFloat1, arrayOfFloat2, i + i2, j - i2, k + i2, m)).fork();
          (new Sorter(relay2, arrayOfFloat1, arrayOfFloat2, i + n, i1, k + n, m)).fork();
          ArraysParallelSortHelpers.Relay relay3 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJFloat.Merger(relay1, arrayOfFloat1, arrayOfFloat2, i, i1, i + i1, n - i1, k, m));
          (new Sorter(relay3, arrayOfFloat1, arrayOfFloat2, i + i1, n - i1, k + i1, m)).fork();
          emptyCompleter = new ArraysParallelSortHelpers.EmptyCompleter(relay3);
          j = i1;
        } 
        DualPivotQuicksort.sort(arrayOfFloat1, i, i + j - 1, arrayOfFloat2, k, j);
        emptyCompleter.tryComplete();
      }
    }
  }
  
  static final class FJInt {
    static final class Merger extends CountedCompleter<Void> {
      static final long serialVersionUID = 2446542900576103244L;
      
      final int[] a;
      
      final int[] w;
      
      final int lbase;
      
      final int lsize;
      
      final int rbase;
      
      final int rsize;
      
      final int wbase;
      
      final int gran;
      
      Merger(CountedCompleter<?> param2CountedCompleter, int[] param2ArrayOfInt1, int[] param2ArrayOfInt2, int param2Int1, int param2Int2, int param2Int3, int param2Int4, int param2Int5, int param2Int6) {
        super(param2CountedCompleter);
        this.a = param2ArrayOfInt1;
        this.w = param2ArrayOfInt2;
        this.lbase = param2Int1;
        this.lsize = param2Int2;
        this.rbase = param2Int3;
        this.rsize = param2Int4;
        this.wbase = param2Int5;
        this.gran = param2Int6;
      }
      
      public final void compute() {
        int[] arrayOfInt1 = this.a;
        int[] arrayOfInt2 = this.w;
        int i = this.lbase;
        int j = this.lsize;
        int k = this.rbase;
        int m = this.rsize;
        int n = this.wbase;
        int i1 = this.gran;
        if (arrayOfInt1 == null || arrayOfInt2 == null || i < 0 || k < 0 || n < 0)
          throw new IllegalStateException(); 
        while (true) {
          int i5;
          int i4;
          if (j >= m) {
            if (j <= i1)
              break; 
            i5 = m;
            int i6 = arrayOfInt1[(i4 = j >>> 1) + i];
            int i7;
            for (i7 = 0; i7 < i5; i7 = i8 + 1) {
              int i8 = i7 + i5 >>> 1;
              if (i6 <= arrayOfInt1[i8 + k]) {
                i5 = i8;
                continue;
              } 
            } 
          } else {
            if (m <= i1)
              break; 
            i4 = j;
            int i6 = arrayOfInt1[(i5 = m >>> 1) + k];
            int i7;
            for (i7 = 0; i7 < i4; i7 = i8 + 1) {
              int i8 = i7 + i4 >>> 1;
              if (i6 <= arrayOfInt1[i8 + i]) {
                i4 = i8;
                continue;
              } 
            } 
          } 
          Merger merger = new Merger(this, arrayOfInt1, arrayOfInt2, i + i4, j - i4, k + i5, m - i5, n + i4 + i5, i1);
          m = i5;
          j = i4;
          addToPendingCount(1);
          merger.fork();
        } 
        int i2 = i + j;
        int i3 = k + m;
        while (i < i2 && k < i3) {
          int i4;
          int i5;
          int i6;
          if ((i5 = arrayOfInt1[i]) <= (i6 = arrayOfInt1[k])) {
            i++;
            i4 = i5;
          } else {
            k++;
            i4 = i6;
          } 
          arrayOfInt2[n++] = i4;
        } 
        if (k < i3) {
          System.arraycopy(arrayOfInt1, k, arrayOfInt2, n, i3 - k);
        } else if (i < i2) {
          System.arraycopy(arrayOfInt1, i, arrayOfInt2, n, i2 - i);
        } 
        tryComplete();
      }
    }
    
    static final class Sorter extends CountedCompleter<Void> {
      static final long serialVersionUID = 2446542900576103244L;
      
      final int[] a;
      
      final int[] w;
      
      final int base;
      
      final int size;
      
      final int wbase;
      
      final int gran;
      
      Sorter(CountedCompleter<?> param2CountedCompleter, int[] param2ArrayOfInt1, int[] param2ArrayOfInt2, int param2Int1, int param2Int2, int param2Int3, int param2Int4) {
        super(param2CountedCompleter);
        this.a = param2ArrayOfInt1;
        this.w = param2ArrayOfInt2;
        this.base = param2Int1;
        this.size = param2Int2;
        this.wbase = param2Int3;
        this.gran = param2Int4;
      }
      
      public final void compute() {
        ArraysParallelSortHelpers.EmptyCompleter emptyCompleter = this;
        int[] arrayOfInt1 = this.a;
        int[] arrayOfInt2 = this.w;
        int i = this.base;
        int j = this.size;
        int k = this.wbase;
        int m = this.gran;
        while (j > m) {
          int n = j >>> 1;
          int i1 = n >>> 1;
          int i2 = n + i1;
          ArraysParallelSortHelpers.Relay relay1 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJInt.Merger(emptyCompleter, arrayOfInt2, arrayOfInt1, k, n, k + n, j - n, i, m));
          ArraysParallelSortHelpers.Relay relay2 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJInt.Merger(relay1, arrayOfInt1, arrayOfInt2, i + n, i1, i + i2, j - i2, k + n, m));
          (new Sorter(relay2, arrayOfInt1, arrayOfInt2, i + i2, j - i2, k + i2, m)).fork();
          (new Sorter(relay2, arrayOfInt1, arrayOfInt2, i + n, i1, k + n, m)).fork();
          ArraysParallelSortHelpers.Relay relay3 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJInt.Merger(relay1, arrayOfInt1, arrayOfInt2, i, i1, i + i1, n - i1, k, m));
          (new Sorter(relay3, arrayOfInt1, arrayOfInt2, i + i1, n - i1, k + i1, m)).fork();
          emptyCompleter = new ArraysParallelSortHelpers.EmptyCompleter(relay3);
          j = i1;
        } 
        DualPivotQuicksort.sort(arrayOfInt1, i, i + j - 1, arrayOfInt2, k, j);
        emptyCompleter.tryComplete();
      }
    }
  }
  
  static final class FJLong {
    static final class Merger extends CountedCompleter<Void> {
      static final long serialVersionUID = 2446542900576103244L;
      
      final long[] a;
      
      final long[] w;
      
      final int lbase;
      
      final int lsize;
      
      final int rbase;
      
      final int rsize;
      
      final int wbase;
      
      final int gran;
      
      Merger(CountedCompleter<?> param2CountedCompleter, long[] param2ArrayOfLong1, long[] param2ArrayOfLong2, int param2Int1, int param2Int2, int param2Int3, int param2Int4, int param2Int5, int param2Int6) {
        super(param2CountedCompleter);
        this.a = param2ArrayOfLong1;
        this.w = param2ArrayOfLong2;
        this.lbase = param2Int1;
        this.lsize = param2Int2;
        this.rbase = param2Int3;
        this.rsize = param2Int4;
        this.wbase = param2Int5;
        this.gran = param2Int6;
      }
      
      public final void compute() {
        long[] arrayOfLong1 = this.a;
        long[] arrayOfLong2 = this.w;
        int i = this.lbase;
        int j = this.lsize;
        int k = this.rbase;
        int m = this.rsize;
        int n = this.wbase;
        int i1 = this.gran;
        if (arrayOfLong1 == null || arrayOfLong2 == null || i < 0 || k < 0 || n < 0)
          throw new IllegalStateException(); 
        while (true) {
          int i5;
          int i4;
          if (j >= m) {
            if (j <= i1)
              break; 
            i5 = m;
            long l = arrayOfLong1[(i4 = j >>> 1) + i];
            int i6;
            for (i6 = 0; i6 < i5; i6 = i7 + 1) {
              int i7 = i6 + i5 >>> 1;
              if (l <= arrayOfLong1[i7 + k]) {
                i5 = i7;
                continue;
              } 
            } 
          } else {
            if (m <= i1)
              break; 
            i4 = j;
            long l = arrayOfLong1[(i5 = m >>> 1) + k];
            int i6;
            for (i6 = 0; i6 < i4; i6 = i7 + 1) {
              int i7 = i6 + i4 >>> 1;
              if (l <= arrayOfLong1[i7 + i]) {
                i4 = i7;
                continue;
              } 
            } 
          } 
          Merger merger = new Merger(this, arrayOfLong1, arrayOfLong2, i + i4, j - i4, k + i5, m - i5, n + i4 + i5, i1);
          m = i5;
          j = i4;
          addToPendingCount(1);
          merger.fork();
        } 
        int i2 = i + j;
        int i3 = k + m;
        while (i < i2 && k < i3) {
          long l1;
          long l2;
          long l3;
          if ((l2 = arrayOfLong1[i]) <= (l3 = arrayOfLong1[k])) {
            i++;
            l1 = l2;
          } else {
            k++;
            l1 = l3;
          } 
          arrayOfLong2[n++] = l1;
        } 
        if (k < i3) {
          System.arraycopy(arrayOfLong1, k, arrayOfLong2, n, i3 - k);
        } else if (i < i2) {
          System.arraycopy(arrayOfLong1, i, arrayOfLong2, n, i2 - i);
        } 
        tryComplete();
      }
    }
    
    static final class Sorter extends CountedCompleter<Void> {
      static final long serialVersionUID = 2446542900576103244L;
      
      final long[] a;
      
      final long[] w;
      
      final int base;
      
      final int size;
      
      final int wbase;
      
      final int gran;
      
      Sorter(CountedCompleter<?> param2CountedCompleter, long[] param2ArrayOfLong1, long[] param2ArrayOfLong2, int param2Int1, int param2Int2, int param2Int3, int param2Int4) {
        super(param2CountedCompleter);
        this.a = param2ArrayOfLong1;
        this.w = param2ArrayOfLong2;
        this.base = param2Int1;
        this.size = param2Int2;
        this.wbase = param2Int3;
        this.gran = param2Int4;
      }
      
      public final void compute() {
        ArraysParallelSortHelpers.EmptyCompleter emptyCompleter = this;
        long[] arrayOfLong1 = this.a;
        long[] arrayOfLong2 = this.w;
        int i = this.base;
        int j = this.size;
        int k = this.wbase;
        int m = this.gran;
        while (j > m) {
          int n = j >>> 1;
          int i1 = n >>> 1;
          int i2 = n + i1;
          ArraysParallelSortHelpers.Relay relay1 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJLong.Merger(emptyCompleter, arrayOfLong2, arrayOfLong1, k, n, k + n, j - n, i, m));
          ArraysParallelSortHelpers.Relay relay2 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJLong.Merger(relay1, arrayOfLong1, arrayOfLong2, i + n, i1, i + i2, j - i2, k + n, m));
          (new Sorter(relay2, arrayOfLong1, arrayOfLong2, i + i2, j - i2, k + i2, m)).fork();
          (new Sorter(relay2, arrayOfLong1, arrayOfLong2, i + n, i1, k + n, m)).fork();
          ArraysParallelSortHelpers.Relay relay3 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJLong.Merger(relay1, arrayOfLong1, arrayOfLong2, i, i1, i + i1, n - i1, k, m));
          (new Sorter(relay3, arrayOfLong1, arrayOfLong2, i + i1, n - i1, k + i1, m)).fork();
          emptyCompleter = new ArraysParallelSortHelpers.EmptyCompleter(relay3);
          j = i1;
        } 
        DualPivotQuicksort.sort(arrayOfLong1, i, i + j - 1, arrayOfLong2, k, j);
        emptyCompleter.tryComplete();
      }
    }
  }
  
  static final class FJObject {
    static final class Merger<T> extends CountedCompleter<Void> {
      static final long serialVersionUID = 2446542900576103244L;
      
      final T[] a;
      
      final T[] w;
      
      final int lbase;
      
      final int lsize;
      
      final int rbase;
      
      final int rsize;
      
      final int wbase;
      
      final int gran;
      
      Comparator<? super T> comparator;
      
      Merger(CountedCompleter<?> param2CountedCompleter, T[] param2ArrayOfT1, T[] param2ArrayOfT2, int param2Int1, int param2Int2, int param2Int3, int param2Int4, int param2Int5, int param2Int6, Comparator<? super T> param2Comparator) {
        super(param2CountedCompleter);
        this.a = param2ArrayOfT1;
        this.w = param2ArrayOfT2;
        this.lbase = param2Int1;
        this.lsize = param2Int2;
        this.rbase = param2Int3;
        this.rsize = param2Int4;
        this.wbase = param2Int5;
        this.gran = param2Int6;
        this.comparator = param2Comparator;
      }
      
      public final void compute() {
        Comparator comparator1 = this.comparator;
        Object[] arrayOfObject1 = this.a;
        Object[] arrayOfObject2 = this.w;
        int i = this.lbase;
        int j = this.lsize;
        int k = this.rbase;
        int m = this.rsize;
        int n = this.wbase;
        int i1 = this.gran;
        if (arrayOfObject1 == null || arrayOfObject2 == null || i < 0 || k < 0 || n < 0 || comparator1 == null)
          throw new IllegalStateException(); 
        while (true) {
          int i5;
          int i4;
          if (j >= m) {
            if (j <= i1)
              break; 
            i5 = m;
            Object object = arrayOfObject1[(i4 = j >>> 1) + i];
            int i6;
            for (i6 = 0; i6 < i5; i6 = i7 + 1) {
              int i7 = i6 + i5 >>> 1;
              if (comparator1.compare(object, arrayOfObject1[i7 + k]) <= 0) {
                i5 = i7;
                continue;
              } 
            } 
          } else {
            if (m <= i1)
              break; 
            i4 = j;
            Object object = arrayOfObject1[(i5 = m >>> 1) + k];
            int i6;
            for (i6 = 0; i6 < i4; i6 = i7 + 1) {
              int i7 = i6 + i4 >>> 1;
              if (comparator1.compare(object, arrayOfObject1[i7 + i]) <= 0) {
                i4 = i7;
                continue;
              } 
            } 
          } 
          Merger merger = new Merger(this, arrayOfObject1, arrayOfObject2, i + i4, j - i4, k + i5, m - i5, n + i4 + i5, i1, comparator1);
          m = i5;
          j = i4;
          addToPendingCount(1);
          merger.fork();
        } 
        int i2 = i + j;
        int i3 = k + m;
        while (i < i2 && k < i3) {
          Object object1;
          Object object2;
          Object object3;
          if (comparator1.compare(object2 = arrayOfObject1[i], object3 = arrayOfObject1[k]) <= 0) {
            i++;
            object1 = object2;
          } else {
            k++;
            object1 = object3;
          } 
          arrayOfObject2[n++] = object1;
        } 
        if (k < i3) {
          System.arraycopy(arrayOfObject1, k, arrayOfObject2, n, i3 - k);
        } else if (i < i2) {
          System.arraycopy(arrayOfObject1, i, arrayOfObject2, n, i2 - i);
        } 
        tryComplete();
      }
    }
    
    static final class Sorter<T> extends CountedCompleter<Void> {
      static final long serialVersionUID = 2446542900576103244L;
      
      final T[] a;
      
      final T[] w;
      
      final int base;
      
      final int size;
      
      final int wbase;
      
      final int gran;
      
      Comparator<? super T> comparator;
      
      Sorter(CountedCompleter<?> param2CountedCompleter, T[] param2ArrayOfT1, T[] param2ArrayOfT2, int param2Int1, int param2Int2, int param2Int3, int param2Int4, Comparator<? super T> param2Comparator) {
        super(param2CountedCompleter);
        this.a = param2ArrayOfT1;
        this.w = param2ArrayOfT2;
        this.base = param2Int1;
        this.size = param2Int2;
        this.wbase = param2Int3;
        this.gran = param2Int4;
        this.comparator = param2Comparator;
      }
      
      public final void compute() {
        ArraysParallelSortHelpers.EmptyCompleter emptyCompleter = this;
        Comparator comparator1 = this.comparator;
        Object[] arrayOfObject1 = this.a;
        Object[] arrayOfObject2 = this.w;
        int i = this.base;
        int j = this.size;
        int k = this.wbase;
        int m = this.gran;
        while (j > m) {
          int n = j >>> 1;
          int i1 = n >>> 1;
          int i2 = n + i1;
          ArraysParallelSortHelpers.Relay relay1 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJObject.Merger(emptyCompleter, arrayOfObject2, arrayOfObject1, k, n, k + n, j - n, i, m, comparator1));
          ArraysParallelSortHelpers.Relay relay2 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJObject.Merger(relay1, arrayOfObject1, arrayOfObject2, i + n, i1, i + i2, j - i2, k + n, m, comparator1));
          (new Sorter(relay2, arrayOfObject1, arrayOfObject2, i + i2, j - i2, k + i2, m, comparator1)).fork();
          (new Sorter(relay2, arrayOfObject1, arrayOfObject2, i + n, i1, k + n, m, comparator1)).fork();
          ArraysParallelSortHelpers.Relay relay3 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJObject.Merger(relay1, arrayOfObject1, arrayOfObject2, i, i1, i + i1, n - i1, k, m, comparator1));
          (new Sorter(relay3, arrayOfObject1, arrayOfObject2, i + i1, n - i1, k + i1, m, comparator1)).fork();
          emptyCompleter = new ArraysParallelSortHelpers.EmptyCompleter(relay3);
          j = i1;
        } 
        TimSort.sort(arrayOfObject1, i, i + j, comparator1, arrayOfObject2, k, j);
        emptyCompleter.tryComplete();
      }
    }
  }
  
  static final class FJShort {
    static final class Merger extends CountedCompleter<Void> {
      static final long serialVersionUID = 2446542900576103244L;
      
      final short[] a;
      
      final short[] w;
      
      final int lbase;
      
      final int lsize;
      
      final int rbase;
      
      final int rsize;
      
      final int wbase;
      
      final int gran;
      
      Merger(CountedCompleter<?> param2CountedCompleter, short[] param2ArrayOfShort1, short[] param2ArrayOfShort2, int param2Int1, int param2Int2, int param2Int3, int param2Int4, int param2Int5, int param2Int6) {
        super(param2CountedCompleter);
        this.a = param2ArrayOfShort1;
        this.w = param2ArrayOfShort2;
        this.lbase = param2Int1;
        this.lsize = param2Int2;
        this.rbase = param2Int3;
        this.rsize = param2Int4;
        this.wbase = param2Int5;
        this.gran = param2Int6;
      }
      
      public final void compute() {
        short[] arrayOfShort1 = this.a;
        short[] arrayOfShort2 = this.w;
        int i = this.lbase;
        int j = this.lsize;
        int k = this.rbase;
        int m = this.rsize;
        int n = this.wbase;
        int i1 = this.gran;
        if (arrayOfShort1 == null || arrayOfShort2 == null || i < 0 || k < 0 || n < 0)
          throw new IllegalStateException(); 
        while (true) {
          int i5;
          int i4;
          if (j >= m) {
            if (j <= i1)
              break; 
            i5 = m;
            short s = arrayOfShort1[(i4 = j >>> 1) + i];
            int i6;
            for (i6 = 0; i6 < i5; i6 = i7 + 1) {
              int i7 = i6 + i5 >>> 1;
              if (s <= arrayOfShort1[i7 + k]) {
                i5 = i7;
                continue;
              } 
            } 
          } else {
            if (m <= i1)
              break; 
            i4 = j;
            short s = arrayOfShort1[(i5 = m >>> 1) + k];
            int i6;
            for (i6 = 0; i6 < i4; i6 = i7 + 1) {
              int i7 = i6 + i4 >>> 1;
              if (s <= arrayOfShort1[i7 + i]) {
                i4 = i7;
                continue;
              } 
            } 
          } 
          Merger merger = new Merger(this, arrayOfShort1, arrayOfShort2, i + i4, j - i4, k + i5, m - i5, n + i4 + i5, i1);
          m = i5;
          j = i4;
          addToPendingCount(1);
          merger.fork();
        } 
        int i2 = i + j;
        int i3 = k + m;
        while (i < i2 && k < i3) {
          short s;
          short s1;
          short s2;
          if ((s1 = arrayOfShort1[i]) <= (s2 = arrayOfShort1[k])) {
            i++;
            s = s1;
          } else {
            k++;
            s = s2;
          } 
          arrayOfShort2[n++] = s;
        } 
        if (k < i3) {
          System.arraycopy(arrayOfShort1, k, arrayOfShort2, n, i3 - k);
        } else if (i < i2) {
          System.arraycopy(arrayOfShort1, i, arrayOfShort2, n, i2 - i);
        } 
        tryComplete();
      }
    }
    
    static final class Sorter extends CountedCompleter<Void> {
      static final long serialVersionUID = 2446542900576103244L;
      
      final short[] a;
      
      final short[] w;
      
      final int base;
      
      final int size;
      
      final int wbase;
      
      final int gran;
      
      Sorter(CountedCompleter<?> param2CountedCompleter, short[] param2ArrayOfShort1, short[] param2ArrayOfShort2, int param2Int1, int param2Int2, int param2Int3, int param2Int4) {
        super(param2CountedCompleter);
        this.a = param2ArrayOfShort1;
        this.w = param2ArrayOfShort2;
        this.base = param2Int1;
        this.size = param2Int2;
        this.wbase = param2Int3;
        this.gran = param2Int4;
      }
      
      public final void compute() {
        ArraysParallelSortHelpers.EmptyCompleter emptyCompleter = this;
        short[] arrayOfShort1 = this.a;
        short[] arrayOfShort2 = this.w;
        int i = this.base;
        int j = this.size;
        int k = this.wbase;
        int m = this.gran;
        while (j > m) {
          int n = j >>> 1;
          int i1 = n >>> 1;
          int i2 = n + i1;
          ArraysParallelSortHelpers.Relay relay1 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJShort.Merger(emptyCompleter, arrayOfShort2, arrayOfShort1, k, n, k + n, j - n, i, m));
          ArraysParallelSortHelpers.Relay relay2 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJShort.Merger(relay1, arrayOfShort1, arrayOfShort2, i + n, i1, i + i2, j - i2, k + n, m));
          (new Sorter(relay2, arrayOfShort1, arrayOfShort2, i + i2, j - i2, k + i2, m)).fork();
          (new Sorter(relay2, arrayOfShort1, arrayOfShort2, i + n, i1, k + n, m)).fork();
          ArraysParallelSortHelpers.Relay relay3 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJShort.Merger(relay1, arrayOfShort1, arrayOfShort2, i, i1, i + i1, n - i1, k, m));
          (new Sorter(relay3, arrayOfShort1, arrayOfShort2, i + i1, n - i1, k + i1, m)).fork();
          emptyCompleter = new ArraysParallelSortHelpers.EmptyCompleter(relay3);
          j = i1;
        } 
        DualPivotQuicksort.sort(arrayOfShort1, i, i + j - 1, arrayOfShort2, k, j);
        emptyCompleter.tryComplete();
      }
    }
  }
  
  static final class Relay extends CountedCompleter<Void> {
    static final long serialVersionUID = 2446542900576103244L;
    
    final CountedCompleter<?> task;
    
    Relay(CountedCompleter<?> param1CountedCompleter) {
      super(null, 1);
      this.task = param1CountedCompleter;
    }
    
    public final void compute() {}
    
    public final void onCompletion(CountedCompleter<?> param1CountedCompleter) { this.task.compute(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\ArraysParallelSortHelpers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */