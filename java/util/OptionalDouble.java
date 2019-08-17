package java.util;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public final class OptionalDouble {
  private static final OptionalDouble EMPTY = new OptionalDouble();
  
  private final boolean isPresent = false;
  
  private final double value = NaND;
  
  private OptionalDouble() {}
  
  public static OptionalDouble empty() { return EMPTY; }
  
  private OptionalDouble(double paramDouble) {}
  
  public static OptionalDouble of(double paramDouble) { return new OptionalDouble(paramDouble); }
  
  public double getAsDouble() {
    if (!this.isPresent)
      throw new NoSuchElementException("No value present"); 
    return this.value;
  }
  
  public boolean isPresent() { return this.isPresent; }
  
  public void ifPresent(DoubleConsumer paramDoubleConsumer) {
    if (this.isPresent)
      paramDoubleConsumer.accept(this.value); 
  }
  
  public double orElse(double paramDouble) { return this.isPresent ? this.value : paramDouble; }
  
  public double orElseGet(DoubleSupplier paramDoubleSupplier) { return this.isPresent ? this.value : paramDoubleSupplier.getAsDouble(); }
  
  public <X extends Throwable> double orElseThrow(Supplier<X> paramSupplier) throws X {
    if (this.isPresent)
      return this.value; 
    throw (Throwable)paramSupplier.get();
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof OptionalDouble))
      return false; 
    OptionalDouble optionalDouble = (OptionalDouble)paramObject;
    return (this.isPresent && optionalDouble.isPresent) ? ((Double.compare(this.value, optionalDouble.value) == 0)) : ((this.isPresent == optionalDouble.isPresent));
  }
  
  public int hashCode() { return this.isPresent ? Double.hashCode(this.value) : 0; }
  
  public String toString() { return this.isPresent ? String.format("OptionalDouble[%s]", new Object[] { Double.valueOf(this.value) }) : "OptionalDouble.empty"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\OptionalDouble.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */