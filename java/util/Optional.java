package java.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class Optional<T> extends Object {
  private static final Optional<?> EMPTY = new Optional();
  
  private final T value = null;
  
  private Optional() {}
  
  public static <T> Optional<T> empty() { return EMPTY; }
  
  private Optional(T paramT) {}
  
  public static <T> Optional<T> of(T paramT) { return new Optional(paramT); }
  
  public static <T> Optional<T> ofNullable(T paramT) { return (paramT == null) ? empty() : of(paramT); }
  
  public T get() {
    if (this.value == null)
      throw new NoSuchElementException("No value present"); 
    return (T)this.value;
  }
  
  public boolean isPresent() { return (this.value != null); }
  
  public void ifPresent(Consumer<? super T> paramConsumer) {
    if (this.value != null)
      paramConsumer.accept(this.value); 
  }
  
  public Optional<T> filter(Predicate<? super T> paramPredicate) {
    Objects.requireNonNull(paramPredicate);
    return !isPresent() ? this : (paramPredicate.test(this.value) ? this : empty());
  }
  
  public <U> Optional<U> map(Function<? super T, ? extends U> paramFunction) {
    Objects.requireNonNull(paramFunction);
    return !isPresent() ? empty() : ofNullable(paramFunction.apply(this.value));
  }
  
  public <U> Optional<U> flatMap(Function<? super T, Optional<U>> paramFunction) {
    Objects.requireNonNull(paramFunction);
    return !isPresent() ? empty() : (Optional)Objects.requireNonNull(paramFunction.apply(this.value));
  }
  
  public T orElse(T paramT) { return (T)((this.value != null) ? this.value : paramT); }
  
  public T orElseGet(Supplier<? extends T> paramSupplier) { return (T)((this.value != null) ? this.value : paramSupplier.get()); }
  
  public <X extends Throwable> T orElseThrow(Supplier<? extends X> paramSupplier) throws X {
    if (this.value != null)
      return (T)this.value; 
    throw (Throwable)paramSupplier.get();
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof Optional))
      return false; 
    Optional optional = (Optional)paramObject;
    return Objects.equals(this.value, optional.value);
  }
  
  public int hashCode() { return Objects.hashCode(this.value); }
  
  public String toString() { return (this.value != null) ? String.format("Optional[%s]", new Object[] { this.value }) : "Optional.empty"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\Optional.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */