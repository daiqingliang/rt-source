package java.util.stream;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.function.UnaryOperator;

public interface Stream<T> extends BaseStream<T, Stream<T>> {
  Stream<T> filter(Predicate<? super T> paramPredicate);
  
  <R> Stream<R> map(Function<? super T, ? extends R> paramFunction);
  
  IntStream mapToInt(ToIntFunction<? super T> paramToIntFunction);
  
  LongStream mapToLong(ToLongFunction<? super T> paramToLongFunction);
  
  DoubleStream mapToDouble(ToDoubleFunction<? super T> paramToDoubleFunction);
  
  <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> paramFunction);
  
  IntStream flatMapToInt(Function<? super T, ? extends IntStream> paramFunction);
  
  LongStream flatMapToLong(Function<? super T, ? extends LongStream> paramFunction);
  
  DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> paramFunction);
  
  Stream<T> distinct();
  
  Stream<T> sorted();
  
  Stream<T> sorted(Comparator<? super T> paramComparator);
  
  Stream<T> peek(Consumer<? super T> paramConsumer);
  
  Stream<T> limit(long paramLong);
  
  Stream<T> skip(long paramLong);
  
  void forEach(Consumer<? super T> paramConsumer);
  
  void forEachOrdered(Consumer<? super T> paramConsumer);
  
  Object[] toArray();
  
  <A> A[] toArray(IntFunction<A[]> paramIntFunction);
  
  T reduce(T paramT, BinaryOperator<T> paramBinaryOperator);
  
  Optional<T> reduce(BinaryOperator<T> paramBinaryOperator);
  
  <U> U reduce(U paramU, BiFunction<U, ? super T, U> paramBiFunction, BinaryOperator<U> paramBinaryOperator);
  
  <R> R collect(Supplier<R> paramSupplier, BiConsumer<R, ? super T> paramBiConsumer1, BiConsumer<R, R> paramBiConsumer2);
  
  <R, A> R collect(Collector<? super T, A, R> paramCollector);
  
  Optional<T> min(Comparator<? super T> paramComparator);
  
  Optional<T> max(Comparator<? super T> paramComparator);
  
  long count();
  
  boolean anyMatch(Predicate<? super T> paramPredicate);
  
  boolean allMatch(Predicate<? super T> paramPredicate);
  
  boolean noneMatch(Predicate<? super T> paramPredicate);
  
  Optional<T> findFirst();
  
  Optional<T> findAny();
  
  static <T> Builder<T> builder() { return new Streams.StreamBuilderImpl(); }
  
  static <T> Stream<T> empty() { return StreamSupport.stream(Spliterators.emptySpliterator(), false); }
  
  static <T> Stream<T> of(T paramT) { return StreamSupport.stream(new Streams.StreamBuilderImpl(paramT), false); }
  
  @SafeVarargs
  static <T> Stream<T> of(T... paramVarArgs) { return Arrays.stream(paramVarArgs); }
  
  static <T> Stream<T> iterate(final T seed, final UnaryOperator<T> f) {
    Objects.requireNonNull(paramUnaryOperator);
    Iterator<T> iterator = new Iterator<T>() {
        T t = Streams.NONE;
        
        public boolean hasNext() { return true; }
        
        public T next() { return (T)(this.t = (this.t == Streams.NONE) ? seed : f.apply(this.t)); }
      };
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 1040), false);
  }
  
  static <T> Stream<T> generate(Supplier<T> paramSupplier) {
    Objects.requireNonNull(paramSupplier);
    return StreamSupport.stream(new StreamSpliterators.InfiniteSupplyingSpliterator.OfRef(Float.MAX_VALUE, paramSupplier), false);
  }
  
  static <T> Stream<T> concat(Stream<? extends T> paramStream1, Stream<? extends T> paramStream2) {
    Objects.requireNonNull(paramStream1);
    Objects.requireNonNull(paramStream2);
    Streams.ConcatSpliterator.OfRef ofRef = new Streams.ConcatSpliterator.OfRef(paramStream1.spliterator(), paramStream2.spliterator());
    Stream stream = StreamSupport.stream(ofRef, (paramStream1.isParallel() || paramStream2.isParallel()));
    return (Stream)stream.onClose(Streams.composedClose(paramStream1, paramStream2));
  }
  
  public static interface Builder<T> extends Consumer<T> {
    void accept(T param1T);
    
    default Builder<T> add(T param1T) {
      accept(param1T);
      return this;
    }
    
    Stream<T> build();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\stream\Stream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */