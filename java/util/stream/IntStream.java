package java.util.stream;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;

public interface IntStream extends BaseStream<Integer, IntStream> {
  IntStream filter(IntPredicate paramIntPredicate);
  
  IntStream map(IntUnaryOperator paramIntUnaryOperator);
  
  <U> Stream<U> mapToObj(IntFunction<? extends U> paramIntFunction);
  
  LongStream mapToLong(IntToLongFunction paramIntToLongFunction);
  
  DoubleStream mapToDouble(IntToDoubleFunction paramIntToDoubleFunction);
  
  IntStream flatMap(IntFunction<? extends IntStream> paramIntFunction);
  
  IntStream distinct();
  
  IntStream sorted();
  
  IntStream peek(IntConsumer paramIntConsumer);
  
  IntStream limit(long paramLong);
  
  IntStream skip(long paramLong);
  
  void forEach(IntConsumer paramIntConsumer);
  
  void forEachOrdered(IntConsumer paramIntConsumer);
  
  int[] toArray();
  
  int reduce(int paramInt, IntBinaryOperator paramIntBinaryOperator);
  
  OptionalInt reduce(IntBinaryOperator paramIntBinaryOperator);
  
  <R> R collect(Supplier<R> paramSupplier, ObjIntConsumer<R> paramObjIntConsumer, BiConsumer<R, R> paramBiConsumer);
  
  int sum();
  
  OptionalInt min();
  
  OptionalInt max();
  
  long count();
  
  OptionalDouble average();
  
  IntSummaryStatistics summaryStatistics();
  
  boolean anyMatch(IntPredicate paramIntPredicate);
  
  boolean allMatch(IntPredicate paramIntPredicate);
  
  boolean noneMatch(IntPredicate paramIntPredicate);
  
  OptionalInt findFirst();
  
  OptionalInt findAny();
  
  LongStream asLongStream();
  
  DoubleStream asDoubleStream();
  
  Stream<Integer> boxed();
  
  IntStream sequential();
  
  IntStream parallel();
  
  PrimitiveIterator.OfInt iterator();
  
  Spliterator.OfInt spliterator();
  
  static Builder builder() { return new Streams.IntStreamBuilderImpl(); }
  
  static IntStream empty() { return StreamSupport.intStream(Spliterators.emptyIntSpliterator(), false); }
  
  static IntStream of(int paramInt) { return StreamSupport.intStream(new Streams.IntStreamBuilderImpl(paramInt), false); }
  
  static IntStream of(int... paramVarArgs) { return Arrays.stream(paramVarArgs); }
  
  static IntStream iterate(final int seed, final IntUnaryOperator f) {
    Objects.requireNonNull(paramIntUnaryOperator);
    PrimitiveIterator.OfInt ofInt = new PrimitiveIterator.OfInt() {
        int t = seed;
        
        public boolean hasNext() { return true; }
        
        public int nextInt() {
          int i = this.t;
          this.t = f.applyAsInt(this.t);
          return i;
        }
      };
    return StreamSupport.intStream(Spliterators.spliteratorUnknownSize(ofInt, 1296), false);
  }
  
  static IntStream generate(IntSupplier paramIntSupplier) {
    Objects.requireNonNull(paramIntSupplier);
    return StreamSupport.intStream(new StreamSpliterators.InfiniteSupplyingSpliterator.OfInt(Float.MAX_VALUE, paramIntSupplier), false);
  }
  
  static IntStream range(int paramInt1, int paramInt2) { return (paramInt1 >= paramInt2) ? empty() : StreamSupport.intStream(new Streams.RangeIntSpliterator(paramInt1, paramInt2, false), false); }
  
  static IntStream rangeClosed(int paramInt1, int paramInt2) { return (paramInt1 > paramInt2) ? empty() : StreamSupport.intStream(new Streams.RangeIntSpliterator(paramInt1, paramInt2, true), false); }
  
  static IntStream concat(IntStream paramIntStream1, IntStream paramIntStream2) {
    Objects.requireNonNull(paramIntStream1);
    Objects.requireNonNull(paramIntStream2);
    Streams.ConcatSpliterator.OfInt ofInt = new Streams.ConcatSpliterator.OfInt(paramIntStream1.spliterator(), paramIntStream2.spliterator());
    IntStream intStream = StreamSupport.intStream(ofInt, (paramIntStream1.isParallel() || paramIntStream2.isParallel()));
    return (IntStream)intStream.onClose(Streams.composedClose(paramIntStream1, paramIntStream2));
  }
  
  public static interface Builder extends IntConsumer {
    void accept(int param1Int);
    
    default Builder add(int param1Int) {
      accept(param1Int);
      return this;
    }
    
    IntStream build();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\stream\IntStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */