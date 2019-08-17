package java.util.concurrent.atomic;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.function.DoubleBinaryOperator;

public class DoubleAccumulator extends Striped64 implements Serializable {
  private static final long serialVersionUID = 7249069246863182397L;
  
  private final DoubleBinaryOperator function;
  
  private final long identity;
  
  public DoubleAccumulator(DoubleBinaryOperator paramDoubleBinaryOperator, double paramDouble) {
    this.function = paramDoubleBinaryOperator;
    this.base = this.identity = Double.doubleToRawLongBits(paramDouble);
  }
  
  public void accumulate(double paramDouble) {
    boolean bool;
    Striped64.Cell[] arrayOfCell;
    long l1;
    long l2;
    if ((arrayOfCell = this.cells) != null || ((l2 = Double.doubleToRawLongBits(this.function.applyAsDouble(Double.longBitsToDouble(l1 = this.base), paramDouble))) != l1 && !casBase(l1, l2))) {
      bool = true;
      int i;
      Striped64.Cell cell;
      if (arrayOfCell != null && (i = arrayOfCell.length - 1) >= 0 && (cell = arrayOfCell[getProbe() & i]) != null) {
        long l;
        if (!(bool = ((l2 = Double.doubleToRawLongBits(this.function.applyAsDouble(Double.longBitsToDouble(l = cell.value), paramDouble))) == l || cell.cas(l, l2)))) {
          doubleAccumulate(paramDouble, this.function, bool);
          return;
        } 
        return;
      } 
    } else {
      return;
    } 
    doubleAccumulate(paramDouble, this.function, bool);
  }
  
  public double get() {
    Striped64.Cell[] arrayOfCell = this.cells;
    double d = Double.longBitsToDouble(this.base);
    if (arrayOfCell != null)
      for (byte b = 0; b < arrayOfCell.length; b++) {
        Striped64.Cell cell;
        if ((cell = arrayOfCell[b]) != null)
          d = this.function.applyAsDouble(d, Double.longBitsToDouble(cell.value)); 
      }  
    return d;
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
  
  public double getThenReset() {
    Striped64.Cell[] arrayOfCell = this.cells;
    double d = Double.longBitsToDouble(this.base);
    this.base = this.identity;
    if (arrayOfCell != null)
      for (byte b = 0; b < arrayOfCell.length; b++) {
        Striped64.Cell cell;
        if ((cell = arrayOfCell[b]) != null) {
          double d1 = Double.longBitsToDouble(cell.value);
          cell.value = this.identity;
          d = this.function.applyAsDouble(d, d1);
        } 
      }  
    return d;
  }
  
  public String toString() { return Double.toString(get()); }
  
  public double doubleValue() { return get(); }
  
  public long longValue() { return (long)get(); }
  
  public int intValue() { return (int)get(); }
  
  public float floatValue() { return (float)get(); }
  
  private Object writeReplace() { return new SerializationProxy(this); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Proxy required"); }
  
  private static class SerializationProxy implements Serializable {
    private static final long serialVersionUID = 7249069246863182397L;
    
    private final double value;
    
    private final DoubleBinaryOperator function;
    
    private final long identity;
    
    SerializationProxy(DoubleAccumulator param1DoubleAccumulator) {
      this.function = param1DoubleAccumulator.function;
      this.identity = param1DoubleAccumulator.identity;
      this.value = param1DoubleAccumulator.get();
    }
    
    private Object readResolve() {
      double d = Double.longBitsToDouble(this.identity);
      DoubleAccumulator doubleAccumulator = new DoubleAccumulator(this.function, d);
      doubleAccumulator.base = Double.doubleToRawLongBits(this.value);
      return doubleAccumulator;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\atomic\DoubleAccumulator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */