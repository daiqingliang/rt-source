package java.util.concurrent.atomic;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleBinaryOperator;
import java.util.function.LongBinaryOperator;
import sun.misc.Contended;
import sun.misc.Unsafe;

abstract class Striped64 extends Number {
  static final int NCPU = Runtime.getRuntime().availableProcessors();
  
  private static final Unsafe UNSAFE;
  
  private static final long BASE;
  
  private static final long CELLSBUSY;
  
  private static final long PROBE;
  
  final boolean casBase(long paramLong1, long paramLong2) { return UNSAFE.compareAndSwapLong(this, BASE, paramLong1, paramLong2); }
  
  final boolean casCellsBusy() { return UNSAFE.compareAndSwapInt(this, CELLSBUSY, 0, 1); }
  
  static final int getProbe() { return UNSAFE.getInt(Thread.currentThread(), PROBE); }
  
  static final int advanceProbe(int paramInt) {
    paramInt ^= paramInt << 13;
    paramInt ^= paramInt >>> 17;
    paramInt ^= paramInt << 5;
    UNSAFE.putInt(Thread.currentThread(), PROBE, paramInt);
    return paramInt;
  }
  
  final void longAccumulate(long paramLong, LongBinaryOperator paramLongBinaryOperator, boolean paramBoolean) {
    int i;
    if ((i = getProbe()) == 0) {
      ThreadLocalRandom.current();
      i = getProbe();
      paramBoolean = true;
    } 
    boolean bool = false;
    while (true) {
      Cell[] arrayOfCell;
      int j;
      if ((arrayOfCell = this.cells) != null && (j = arrayOfCell.length) > 0) {
        Cell cell;
        if ((cell = arrayOfCell[j - true & i]) == null) {
          if (this.cellsBusy == 0) {
            Cell cell1 = new Cell(paramLong);
            if (this.cellsBusy == 0 && casCellsBusy()) {
              boolean bool1 = false;
              try {
                Cell[] arrayOfCell1;
                int k;
                int m;
                if ((arrayOfCell1 = this.cells) != null && (k = arrayOfCell1.length) > 0 && arrayOfCell1[m = k - true & i] == null) {
                  arrayOfCell1[m] = cell1;
                  bool1 = true;
                } 
              } finally {
                this.cellsBusy = 0;
              } 
              if (bool1)
                break; 
              continue;
            } 
          } 
          bool = false;
        } else if (!paramBoolean) {
          paramBoolean = true;
        } else {
          long l1;
          if (cell.cas(l1 = cell.value, (paramLongBinaryOperator == null) ? (l1 + paramLong) : paramLongBinaryOperator.applyAsLong(l1, paramLong)))
            break; 
          if (j >= NCPU || this.cells != arrayOfCell) {
            bool = false;
          } else if (!bool) {
            bool = true;
          } else if (this.cellsBusy == 0 && casCellsBusy()) {
            try {
              if (this.cells == arrayOfCell) {
                Cell[] arrayOfCell1 = new Cell[j << 1];
                for (byte b = 0; b < j; b++)
                  arrayOfCell1[b] = arrayOfCell[b]; 
                this.cells = arrayOfCell1;
              } 
            } finally {
              this.cellsBusy = 0;
            } 
            bool = false;
            continue;
          } 
        } 
        i = advanceProbe(i);
        continue;
      } 
      if (this.cellsBusy == 0 && this.cells == arrayOfCell && casCellsBusy()) {
        boolean bool1 = false;
        try {
          if (this.cells == arrayOfCell) {
            Cell[] arrayOfCell1 = new Cell[2];
            arrayOfCell1[i & true] = new Cell(paramLong);
            this.cells = arrayOfCell1;
            bool1 = true;
          } 
        } finally {
          this.cellsBusy = 0;
        } 
        if (bool1)
          break; 
        continue;
      } 
      long l;
      if (casBase(l = this.base, (paramLongBinaryOperator == null) ? (l + paramLong) : paramLongBinaryOperator.applyAsLong(l, paramLong)))
        break; 
    } 
  }
  
  final void doubleAccumulate(double paramDouble, DoubleBinaryOperator paramDoubleBinaryOperator, boolean paramBoolean) {
    int i;
    if ((i = getProbe()) == 0) {
      ThreadLocalRandom.current();
      i = getProbe();
      paramBoolean = true;
    } 
    boolean bool = false;
    while (true) {
      Cell[] arrayOfCell;
      int j;
      if ((arrayOfCell = this.cells) != null && (j = arrayOfCell.length) > 0) {
        Cell cell;
        if ((cell = arrayOfCell[j - true & i]) == null) {
          if (this.cellsBusy == 0) {
            Cell cell1 = new Cell(Double.doubleToRawLongBits(paramDouble));
            if (this.cellsBusy == 0 && casCellsBusy()) {
              boolean bool1 = false;
              try {
                Cell[] arrayOfCell1;
                int k;
                int m;
                if ((arrayOfCell1 = this.cells) != null && (k = arrayOfCell1.length) > 0 && arrayOfCell1[m = k - true & i] == null) {
                  arrayOfCell1[m] = cell1;
                  bool1 = true;
                } 
              } finally {
                this.cellsBusy = 0;
              } 
              if (bool1)
                break; 
              continue;
            } 
          } 
          bool = false;
        } else if (!paramBoolean) {
          paramBoolean = true;
        } else {
          long l1;
          if (cell.cas(l1 = cell.value, (paramDoubleBinaryOperator == null) ? Double.doubleToRawLongBits(Double.longBitsToDouble(l1) + paramDouble) : Double.doubleToRawLongBits(paramDoubleBinaryOperator.applyAsDouble(Double.longBitsToDouble(l1), paramDouble))))
            break; 
          if (j >= NCPU || this.cells != arrayOfCell) {
            bool = false;
          } else if (!bool) {
            bool = true;
          } else if (this.cellsBusy == 0 && casCellsBusy()) {
            try {
              if (this.cells == arrayOfCell) {
                Cell[] arrayOfCell1 = new Cell[j << 1];
                for (byte b = 0; b < j; b++)
                  arrayOfCell1[b] = arrayOfCell[b]; 
                this.cells = arrayOfCell1;
              } 
            } finally {
              this.cellsBusy = 0;
            } 
            bool = false;
            continue;
          } 
        } 
        i = advanceProbe(i);
        continue;
      } 
      if (this.cellsBusy == 0 && this.cells == arrayOfCell && casCellsBusy()) {
        boolean bool1 = false;
        try {
          if (this.cells == arrayOfCell) {
            Cell[] arrayOfCell1 = new Cell[2];
            arrayOfCell1[i & true] = new Cell(Double.doubleToRawLongBits(paramDouble));
            this.cells = arrayOfCell1;
            bool1 = true;
          } 
        } finally {
          this.cellsBusy = 0;
        } 
        if (bool1)
          break; 
        continue;
      } 
      long l;
      if (casBase(l = this.base, (paramDoubleBinaryOperator == null) ? Double.doubleToRawLongBits(Double.longBitsToDouble(l) + paramDouble) : Double.doubleToRawLongBits(paramDoubleBinaryOperator.applyAsDouble(Double.longBitsToDouble(l), paramDouble))))
        break; 
    } 
  }
  
  static  {
    try {
      UNSAFE = Unsafe.getUnsafe();
      Class clazz1 = Striped64.class;
      BASE = UNSAFE.objectFieldOffset(clazz1.getDeclaredField("base"));
      CELLSBUSY = UNSAFE.objectFieldOffset(clazz1.getDeclaredField("cellsBusy"));
      Class clazz2 = Thread.class;
      PROBE = UNSAFE.objectFieldOffset(clazz2.getDeclaredField("threadLocalRandomProbe"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
  
  @Contended
  static final class Cell {
    private static final Unsafe UNSAFE;
    
    private static final long valueOffset;
    
    Cell(long param1Long) { this.value = param1Long; }
    
    final boolean cas(long param1Long1, long param1Long2) { return UNSAFE.compareAndSwapLong(this, valueOffset, param1Long1, param1Long2); }
    
    static  {
      try {
        UNSAFE = Unsafe.getUnsafe();
        Class clazz = Cell.class;
        valueOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("value"));
      } catch (Exception exception) {
        throw new Error(exception);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\atomic\Striped64.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */