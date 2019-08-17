package java.util.stream;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.Iterator;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;

public interface DoubleStream extends BaseStream<Double, DoubleStream> {
  DoubleStream filter(DoublePredicate paramDoublePredicate);
  
  DoubleStream map(DoubleUnaryOperator paramDoubleUnaryOperator);
  
  <U> Stream<U> mapToObj(DoubleFunction<? extends U> paramDoubleFunction);
  
  IntStream mapToInt(DoubleToIntFunction paramDoubleToIntFunction);
  
  LongStream mapToLong(DoubleToLongFunction paramDoubleToLongFunction);
  
  DoubleStream flatMap(DoubleFunction<? extends DoubleStream> paramDoubleFunction);
  
  DoubleStream distinct();
  
  DoubleStream sorted();
  
  DoubleStream peek(DoubleConsumer paramDoubleConsumer);
  
  DoubleStream limit(long paramLong);
  
  DoubleStream skip(long paramLong);
  
  void forEach(DoubleConsumer paramDoubleConsumer);
  
  void forEachOrdered(DoubleConsumer paramDoubleConsumer);
  
  double[] toArray();
  
  double reduce(double paramDouble, DoubleBinaryOperator paramDoubleBinaryOperator);
  
  OptionalDouble reduce(DoubleBinaryOperator paramDoubleBinaryOperator);
  
  <R> R collect(Supplier<R> paramSupplier, ObjDoubleConsumer<R> paramObjDoubleConsumer, BiConsumer<R, R> paramBiConsumer);
  
  double sum();
  
  OptionalDouble min();
  
  OptionalDouble max();
  
  long count();
  
  OptionalDouble average();
  
  DoubleSummaryStatistics summaryStatistics();
  
  boolean anyMatch(DoublePredicate paramDoublePredicate);
  
  boolean allMatch(DoublePredicate paramDoublePredicate);
  
  boolean noneMatch(DoublePredicate paramDoublePredicate);
  
  OptionalDouble findFirst();
  
  OptionalDouble findAny();
  
  Stream<Double> boxed();
  
  DoubleStream sequential();
  
  DoubleStream parallel();
  
  PrimitiveIterator.OfDouble iterator();
  
  Spliterator.OfDouble spliterator();
  
  static Builder builder() { return new Streams.DoubleStreamBuilderImpl(); }
  
  static DoubleStream empty() { return StreamSupport.doubleStream(Spliterators.emptyDoubleSpliterator(), false); }
  
  static DoubleStream of(double paramDouble) { return StreamSupport.doubleStream(new Streams.DoubleStreamBuilderImpl(paramDouble), false); }
  
  static DoubleStream of(double... paramVarArgs) { return Arrays.stream(paramVarArgs); }
  
  static DoubleStream iterate(final double seed, final DoubleUnaryOperator f) {
    Objects.requireNonNull(paramDoubleUnaryOperator);
    PrimitiveIterator.OfDouble ofDouble = new PrimitiveIterator.OfDouble() {
        double t = seed;
        
        public boolean hasNext() { return true; }
        
        public double nextDouble() {
          double d = this.t;
          this.t = f.applyAsDouble(this.t);
          return d;
        }
      };
    return StreamSupport.doubleStream(Spliterators.spliteratorUnknownSize(ofDouble, 1296), false);
  }
  
  static DoubleStream generate(DoubleSupplier paramDoubleSupplier) {
    Objects.requireNonNull(paramDoubleSupplier);
    return StreamSupport.doubleStream(new StreamSpliterators.InfiniteSupplyingSpliterator.OfDouble(Float.MAX_VALUE, paramDoubleSupplier), false);
  }
  
  static DoubleStream concat(DoubleStream paramDoubleStream1, DoubleStream paramDoubleStream2) {
    Objects.requireNonNull(paramDoubleStream1);
    Objects.requireNonNull(paramDoubleStream2);
    Streams.ConcatSpliterator.OfDouble ofDouble = new Streams.ConcatSpliterator.OfDouble(paramDoubleStream1.spliterator(), paramDoubleStream2.spliterator());
    DoubleStream doubleStream = StreamSupport.doubleStream(ofDouble, (paramDoubleStream1.isParallel() || paramDoubleStream2.isParallel()));
    return (DoubleStream)doubleStream.onClose(Streams.composedClose(paramDoubleStream1, paramDoubleStream2));
  }
  
  public static interface Builder extends DoubleConsumer {
    void accept(double param1Double);
    
    default Builder add(double param1Double) {
      accept(param1Double);
      return this;
    }
    
    DoubleStream build();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\stream\DoubleStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */