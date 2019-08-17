package java.util.concurrent.atomic;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.function.LongBinaryOperator;

public class LongAccumulator extends Striped64 implements Serializable {
  private static final long serialVersionUID = 7249069246863182397L;
  
  private final LongBinaryOperator function;
  
  private final long identity;
  
  public LongAccumulator(LongBinaryOperator paramLongBinaryOperator, long paramLong) {
    this.function = paramLongBinaryOperator;
    this.base = this.identity = paramLong;
  }
  
  public void accumulate(long paramLong) {
    boolean bool;
    Striped64.Cell[] arrayOfCell;
    long l1;
    long l2;
    if ((arrayOfCell = this.cells) != null || ((l2 = this.function.applyAsLong(l1 = this.base, paramLong)) != l1 && !casBase(l1, l2))) {
      bool = true;
      int i;
      Striped64.Cell cell;
      if (arrayOfCell != null && (i = arrayOfCell.length - 1) >= 0 && (cell = arrayOfCell[getProbe() & i]) != null) {
        long l;
        if (!(bool = ((l2 = this.function.applyAsLong(l = cell.value, paramLong)) == l || cell.cas(l, l2)))) {
          longAccumulate(paramLong, this.function, bool);
          return;
        } 
        return;
      } 
    } else {
      return;
    } 
    longAccumulate(paramLong, this.function, bool);
  }
  
  public long get() {
    Striped64.Cell[] arrayOfCell = this.cells;
    long l = this.base;
    if (arrayOfCell != null)
      for (byte b = 0; b < arrayOfCell.length; b++) {
        Striped64.Cell cell;
        if ((cell = arrayOfCell[b]) != null)
          l = this.function.applyAsLong(l, cell.value); 
      }  
    return l;
  }
  
  public void reset() {
    Striped64.Cell[] arrayOfCell = this.cells;
    this.base = this.identity;
    if (arrayOfCell != null)
      for (byte b = 0; b < arrayOfCell.length; b++) {
        Striped64.Cell cell;
        if ((cell = arrayOfCell[b]) != null)
          cell.value = this.identity; 
      }  
  }
  
  public long getThenReset() {
    Striped64.Cell[] arrayOfCell = this.cells;
    long l = this.base;
    this.base = this.identity;
    if (arrayOfCell != null)
      for (byte b = 0; b < arrayOfCell.length; b++) {
        Striped64.Cell cell;
        if ((cell = arrayOfCell[b]) != null) {
          long l1 = cell.value;
          cell.value = this.identity;
          l = this.function.applyAsLong(l, l1);
        } 
      }  
    return l;
  }
  
  public String toString() { return Long.toString(get()); }
  
  public long longValue() { return get(); }
  
  public int intValue() { return (int)get(); }
  
  public float floatValue() { return (float)get(); }
  
  public double doubleValue() { return get(); }
  
  private Object writeReplace() { return new SerializationProxy(this); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Proxy required"); }
  
  private static class SerializationProxy implements Serializable {
    private static final long serialVersionUID = 7249069246863182397L;
    
    private final long value;
    
    private final LongBinaryOperator function;
    
    private final long identity;
    
    SerializationProxy(LongAccumulator param1LongAccumulator) {
      this.function = param1LongAccumulator.function;
      this.identity = param1LongAccumulator.identity;
      this.value = param1LongAccumulator.get();
    }
    
    private Object readResolve() {
      LongAccumulator longAccumulator = new LongAccumulator(this.function, this.identity);
      longAccumulator.base = this.value;
      return longAccumulator;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\atomic\LongAccumulator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */