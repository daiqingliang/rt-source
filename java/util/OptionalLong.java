package java.util;

import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public final class OptionalLong {
  private static final OptionalLong EMPTY = new OptionalLong();
  
  private final boolean isPresent = false;
  
  private final long value = 0L;
  
  private OptionalLong() {}
  
  public static OptionalLong empty() { return EMPTY; }
  
  private OptionalLong(long paramLong) {}
  
  public static OptionalLong of(long paramLong) { return new OptionalLong(paramLong); }
  
  public long getAsLong() {
    if (!this.isPresent)
      throw new NoSuchElementException("No value present"); 
    return this.value;
  }
  
  public boolean isPresent() { return this.isPresent; }
  
  public void ifPresent(LongConsumer paramLongConsumer) {
    if (this.isPresent)
      paramLongConsumer.accept(this.value); 
  }
  
  public long orElse(long paramLong) { return this.isPresent ? this.value : paramLong; }
  
  public long orElseGet(LongSupplier paramLongSupplier) { return this.isPresent ? this.value : paramLongSupplier.getAsLong(); }
  
  public <X extends Throwable> long orElseThrow(Supplier<X> paramSupplier) throws X {
    if (this.isPresent)
      return this.value; 
    throw (Throwable)paramSupplier.get();
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof OptionalLong))
      return false; 
    OptionalLong optionalLong = (OptionalLong)paramObject;
    return (this.isPresent && optionalLong.isPresent) ? ((this.value == optionalLong.value)) : ((this.isPresent == optionalLong.isPresent));
  }
  
  public int hashCode() { return this.isPresent ? Long.hashCode(this.value) : 0; }
  
  public String toString() { return this.isPresent ? String.format("OptionalLong[%s]", new Object[] { Long.valueOf(this.value) }) : "OptionalLong.empty"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\OptionalLong.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */