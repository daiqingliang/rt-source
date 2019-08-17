package java.util.concurrent.atomic;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class LongAdder extends Striped64 implements Serializable {
  private static final long serialVersionUID = 7249069246863182397L;
  
  public void add(long paramLong) {
    Striped64.Cell[] arrayOfCell;
    long l;
    if ((arrayOfCell = this.cells) != null || !casBase(l = this.base, l + paramLong)) {
      boolean bool = true;
      long l1;
      int i;
      Striped64.Cell cell;
      if (arrayOfCell == null || (i = arrayOfCell.length - 1) < 0 || (cell = arrayOfCell[getProbe() & i]) == null || !(bool = cell.cas(l1 = cell.value, l1 + paramLong)))
        longAccumulate(paramLong, null, bool); 
    } 
  }
  
  public void increment() { add(1L); }
  
  public void decrement() { add(-1L); }
  
  public long sum() {
    Striped64.Cell[] arrayOfCell = this.cells;
    long l = this.base;
    if (arrayOfCell != null)
      for (byte b = 0; b < arrayOfCell.length; b++) {
        Striped64.Cell cell;
        if ((cell = arrayOfCell[b]) != null)
          l += cell.value; 
      }  
    return l;
  }
  
  public void reset() {
    Striped64.Cell[] arrayOfCell = this.cells;
    this.base = 0L;
    if (arrayOfCell != null)
      for (byte b = 0; b < arrayOfCell.length; b++) {
        Striped64.Cell cell;
        if ((cell = arrayOfCell[b]) != null)
          cell.value = 0L; 
      }  
  }
  
  public long sumThenReset() {
    Striped64.Cell[] arrayOfCell = this.cells;
    long l = this.base;
    this.base = 0L;
    if (arrayOfCell != null)
      for (byte b = 0; b < arrayOfCell.length; b++) {
        Striped64.Cell cell;
        if ((cell = arrayOfCell[b]) != null) {
          l += cell.value;
          cell.value = 0L;
        } 
      }  
    return l;
  }
  
  public String toString() { return Long.toString(sum()); }
  
  public long longValue() { return sum(); }
  
  public int intValue() { return (int)sum(); }
  
  public float floatValue() { return (float)sum(); }
  
  public double doubleValue() { return sum(); }
  
  private Object writeReplace() { return new SerializationProxy(this); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Proxy required"); }
  
  private static class SerializationProxy implements Serializable {
    private static final long serialVersionUID = 7249069246863182397L;
    
    private final long value;
    
    SerializationProxy(LongAdder param1LongAdder) { this.value = param1LongAdder.sum(); }
    
    private Object readResolve() {
      LongAdder longAdder = new LongAdder();
      longAdder.base = this.value;
      return longAdder;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\atomic\LongAdder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */