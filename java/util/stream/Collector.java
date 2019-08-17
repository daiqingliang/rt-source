package java.util.stream;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Collector<T, A, R> {
  Supplier<A> supplier();
  
  BiConsumer<A, T> accumulator();
  
  BinaryOperator<A> combiner();
  
  Function<A, R> finisher();
  
  Set<Characteristics> characteristics();
  
  static <T, R> Collector<T, R, R> of(Supplier<R> paramSupplier, BiConsumer<R, T> paramBiConsumer, BinaryOperator<R> paramBinaryOperator, Characteristics... paramVarArgs) {
    Objects.requireNonNull(paramSupplier);
    Objects.requireNonNull(paramBiConsumer);
    Objects.requireNonNull(paramBinaryOperator);
    Objects.requireNonNull(paramVarArgs);
    Set set = (paramVarArgs.length == 0) ? Collectors.CH_ID : Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH, paramVarArgs));
    return new Collectors.CollectorImpl(paramSupplier, paramBiConsumer, paramBinaryOperator, set);
  }
  
  static <T, A, R> Collector<T, A, R> of(Supplier<A> paramSupplier, BiConsumer<A, T> paramBiConsumer, BinaryOperator<A> paramBinaryOperator, Function<A, R> paramFunction, Characteristics... paramVarArgs) {
    Objects.requireNonNull(paramSupplier);
    Objects.requireNonNull(paramBiConsumer);
    Objects.requireNonNull(paramBinaryOperator);
    Objects.requireNonNull(paramFunction);
    Objects.requireNonNull(paramVarArgs);
    Set set = Collectors.CH_NOID;
    if (paramVarArgs.length > 0) {
      set = EnumSet.noneOf(Characteristics.class);
      Collections.addAll(set, paramVarArgs);
      set = Collections.unmodifiableSet(set);
    } 
    return new Collectors.CollectorImpl(paramSupplier, paramBiConsumer, paramBinaryOperator, paramFunction, set);
  }
  
  public enum Characteristics {
    CONCURRENT, UNORDERED, IDENTITY_FINISH;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\stream\Collector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */