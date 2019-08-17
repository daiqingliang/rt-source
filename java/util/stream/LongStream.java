package java.util.stream;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LongSummaryStatistics;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongSupplier;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;

public interface LongStream extends BaseStream<Long, LongStream> {
  LongStream filter(LongPredicate paramLongPredicate);
  
  LongStream map(LongUnaryOperator paramLongUnaryOperator);
  
  <U> Stream<U> mapToObj(LongFunction<? extends U> paramLongFunction);
  
  IntStream mapToInt(LongToIntFunction paramLongToIntFunction);
  
  DoubleStream mapToDouble(LongToDoubleFunction paramLongToDoubleFunction);
  
  LongStream flatMap(LongFunction<? extends LongStream> paramLongFunction);
  
  LongStream distinct();
  
  LongStream sorted();
  
  LongStream peek(LongConsumer paramLongConsumer);
  
  LongStream limit(long paramLong);
  
  LongStream skip(long paramLong);
  
  void forEach(LongConsumer paramLongConsumer);
  
  void forEachOrdered(LongConsumer paramLongConsumer);
  
  long[] toArray();
  
  long reduce(long paramLong, LongBinaryOperator paramLongBinaryOperator);
  
  OptionalLong reduce(LongBinaryOperator paramLongBinaryOperator);
  
  <R> R collect(Supplier<R> paramSupplier, ObjLongConsumer<R> paramObjLongConsumer, BiConsumer<R, R> paramBiConsumer);
  
  long sum();
  
  OptionalLong min();
  
  OptionalLong max();
  
  long count();
  
  OptionalDouble average();
  
  LongSummaryStatistics summaryStatistics();
  
  boolean anyMatch(LongPredicate paramLongPredicate);
  
  boolean allMatch(LongPredicate paramLongPredicate);
  
  boolean noneMatch(LongPredicate paramLongPredicate);
  
  OptionalLong findFirst();
  
  OptionalLong findAny();
  
  DoubleStream asDoubleStream();
  
  Stream<Long> boxed();
  
  LongStream sequential();
  
  LongStream parallel();
  
  PrimitiveIterator.OfLong iterator();
  
  Spliterator.OfLong spliterator();
  
  static Builder builder() { return new Streams.LongStreamBuilderImpl(); }
  
  static LongStream empty() { return StreamSupport.longStream(Spliterators.emptyLongSpliterator(), false); }
  
  static LongStream of(long paramLong) { return StreamSupport.longStream(new Streams.LongStreamBuilderImpl(paramLong), false); }
  
  static LongStream of(long... paramVarArgs) { return Arrays.stream(paramVarArgs); }
  
  static LongStream iterate(final long seed, final LongUnaryOperator f) {
    Objects.requireNonNull(paramLongUnaryOperator);
    PrimitiveIterator.OfLong ofLong = new PrimitiveIterator.OfLong() {
        long t = seed;
        
        public boolean hasNext() { return true; }
        
        public long nextLong() {
          long l = this.t;
          this.t = f.applyAsLong(this.t);
          return l;
        }
      };
    return StreamSupport.longStream(Spliterators.spliteratorUnknownSize(ofLong, 1296), false);
  }
  
  static LongStream generate(LongSupplier paramLongSupplier) {
    Objects.requireNonNull(paramLongSupplier);
    return StreamSupport.longStream(new StreamSpliterators.InfiniteSupplyingSpliterator.OfLong(Float.MAX_VALUE, paramLongSupplier), false);
  }
  
  static LongStream range(long paramLong1, long paramLong2) {
    if (paramLong1 >= paramLong2)
      return empty(); 
    if (paramLong2 - paramLong1 < 0L) {
      long l = paramLong1 + Long.divideUnsigned(paramLong2 - paramLong1, 2L) + 1L;
      return concat(range(paramLong1, l), range(l, paramLong2));
    } 
    return StreamSupport.longStream(new Streams.RangeLongSpliterator(paramLong1, paramLong2, false), false);
  }
  
  static LongStream rangeClosed(long paramLong1, long paramLong2) {
    if (paramLong1 > paramLong2)
      return empty(); 
    if (paramLong2 - paramLong1 + 1L <= 0L) {
      long l = paramLong1 + Long.divideUnsigned(paramLong2 - paramLong1, 2L) + 1L;
      return concat(range(paramLong1, l), rangeClosed(l, paramLong2));
    } 
    return StreamSupport.longStream(new Streams.RangeLongSpliterator(paramLong1, paramLong2, true), false);
  }
  
  static LongStream concat(LongStream paramLongStream1, LongStream paramLongStream2) {
    Objects.requireNonNull(paramLongStream1);
    Objects.requireNonNull(paramLongStream2);
    Streams.ConcatSpliterator.OfLong ofLong = new Streams.ConcatSpliterator.OfLong(paramLongStream1.spliterator(), paramLongStream2.spliterator());
    LongStream longStream = StreamSupport.longStream(ofLong, (paramLongStream1.isParallel() || paramLongStream2.isParallel()));
    return (LongStream)longStream.onClose(Streams.composedClose(paramLongStream1, paramLongStream2));
  }
  
  public static interface Builder extends LongConsumer {
    void accept(long param1Long);
    
    default Builder add(long param1Long) {
      accept(param1Long);
      return this;
    }
    
    LongStream build();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\stream\LongStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */