package java.util.concurrent.atomic;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class DoubleAdder extends Striped64 implements Serializable {
  private static final long serialVersionUID = 7249069246863182397L;
  
  public void add(double paramDouble) {
    Striped64.Cell[] arrayOfCell;
    long l;
    if ((arrayOfCell = this.cells) != null || !casBase(l = this.base, Double.doubleToRawLongBits(Double.longBitsToDouble(l) + paramDouble))) {
      boolean bool = true;
      long l1;
      int i;
      Striped64.Cell cell;
      if (arrayOfCell == null || (i = arrayOfCell.length - 1) < 0 || (cell = arrayOfCell[getProbe() & i]) == null || !(bool = cell.cas(l1 = cell.value, Double.doubleToRawLongBits(Double.longBitsToDouble(l1) + paramDouble))))
        doubleAccumulate(paramDouble, null, bool); 
    } 
  }
  
  public double sum() {
    Striped64.Cell[] arrayOfCell = this.cells;
    double d = Double.longBitsToDouble(this.base);
    if (arrayOfCell != null)
      for (byte b = 0; b < arrayOfCell.length; b++) {
        Striped64.Cell cell;
        if ((cell = arrayOfCell[b]) != null)
          d += Double.longBitsToDouble(cell.value); 
      }  
    return d;
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
  
  public double sumThenReset() {
    Striped64.Cell[] arrayOfCell = this.cells;
    double d = Double.longBitsToDouble(this.base);
    this.base = 0L;
    if (arrayOfCell != null)
      for (byte b = 0; b < arrayOfCell.length; b++) {
        Striped64.Cell cell;
        if ((cell = arrayOfCell[b]) != null) {
          long l = cell.value;
          cell.value = 0L;
          d += Double.longBitsToDouble(l);
        } 
      }  
    return d;
  }
  
  public String toString() { return Double.toString(sum()); }
  
  public double doubleValue() { return sum(); }
  
  public long longValue() { return (long)sum(); }
  
  public int intValue() { return (int)sum(); }
  
  public float floatValue() { return (float)sum(); }
  
  private Object writeReplace() { return new SerializationProxy(this); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Proxy required"); }
  
  private static class SerializationProxy implements Serializable {
    private static final long serialVersionUID = 7249069246863182397L;
    
    private final double value;
    
    SerializationProxy(DoubleAdder param1DoubleAdder) { this.value = param1DoubleAdder.sum(); }
    
    private Object readResolve() {
      DoubleAdder doubleAdder = new DoubleAdder();
      doubleAdder.base = Double.doubleToRawLongBits(this.value);
      return doubleAdder;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\atomic\DoubleAdder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */