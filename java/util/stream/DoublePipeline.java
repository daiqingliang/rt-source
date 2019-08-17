package java.util.stream;

import java.util.DoubleSummaryStatistics;
import java.util.Iterator;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntFunction;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;

abstract class DoublePipeline<E_IN> extends AbstractPipeline<E_IN, Double, DoubleStream> implements DoubleStream {
  DoublePipeline(Supplier<? extends Spliterator<Double>> paramSupplier, int paramInt, boolean paramBoolean) { super(paramSupplier, paramInt, paramBoolean); }
  
  DoublePipeline(Spliterator<Double> paramSpliterator, int paramInt, boolean paramBoolean) { super(paramSpliterator, paramInt, paramBoolean); }
  
  DoublePipeline(AbstractPipeline<?, E_IN, ?> paramAbstractPipeline, int paramInt) { super(paramAbstractPipeline, paramInt); }
  
  private static DoubleConsumer adapt(Sink<Double> paramSink) {
    if (paramSink instanceof DoubleConsumer)
      return (DoubleConsumer)paramSink; 
    if (Tripwire.ENABLED)
      Tripwire.trip(AbstractPipeline.class, "using DoubleStream.adapt(Sink<Double> s)"); 
    return paramSink::accept;
  }
  
  private static Spliterator.OfDouble adapt(Spliterator<Double> paramSpliterator) {
    if (paramSpliterator instanceof Spliterator.OfDouble)
      return (Spliterator.OfDouble)paramSpliterator; 
    if (Tripwire.ENABLED)
      Tripwire.trip(AbstractPipeline.class, "using DoubleStream.adapt(Spliterator<Double> s)"); 
    throw new UnsupportedOperationException("DoubleStream.adapt(Spliterator<Double> s)");
  }
  
  final StreamShape getOutputShape() { return StreamShape.DOUBLE_VALUE; }
  
  final <P_IN> Node<Double> evaluateToNode(PipelineHelper<Double> paramPipelineHelper, Spliterator<P_IN> paramSpliterator, boolean paramBoolean, IntFunction<Double[]> paramIntFunction) { return Nodes.collectDouble(paramPipelineHelper, paramSpliterator, paramBoolean); }
  
  final <P_IN> Spliterator<Double> wrap(PipelineHelper<Double> paramPipelineHelper, Supplier<Spliterator<P_IN>> paramSupplier, boolean paramBoolean) { return new StreamSpliterators.DoubleWrappingSpliterator(paramPipelineHelper, paramSupplier, paramBoolean); }
  
  final Spliterator.OfDouble lazySpliterator(Supplier<? extends Spliterator<Double>> paramSupplier) { return new StreamSpliterators.DelegatingSpliterator.OfDouble(paramSupplier); }
  
  final void forEachWithCancel(Spliterator<Double> paramSpliterator, Sink<Double> paramSink) {
    Spliterator.OfDouble ofDouble = adapt(paramSpliterator);
    DoubleConsumer doubleConsumer = adapt(paramSink);
    do {
    
    } while (!paramSink.cancellationRequested() && ofDouble.tryAdvance(doubleConsumer));
  }
  
  final Node.Builder<Double> makeNodeBuilder(long paramLong, IntFunction<Double[]> paramIntFunction) { return Nodes.doubleBuilder(paramLong); }
  
  public final PrimitiveIterator.OfDouble iterator() { return Spliterators.iterator(spliterator()); }
  
  public final Spliterator.OfDouble spliterator() { return adapt(super.spliterator()); }
  
  public final Stream<Double> boxed() { return mapToObj(Double::valueOf); }
  
  public final DoubleStream map(final DoubleUnaryOperator mapper) {
    Objects.requireNonNull(paramDoubleUnaryOperator);
    return new StatelessOp<Double>(this, StreamShape.DOUBLE_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
        Sink<Double> opWrapSink(int param1Int, Sink<Double> param1Sink) { return new Sink.ChainedDouble<Double>(param1Sink) {
              public void accept(double param2Double) { this.downstream.accept(mapper.applyAsDouble(param2Double)); }
            }; }
      };
  }
  
  public final <U> Stream<U> mapToObj(final DoubleFunction<? extends U> mapper) {
    Objects.requireNonNull(paramDoubleFunction);
    return new ReferencePipeline.StatelessOp<Double, U>(this, StreamShape.DOUBLE_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
        Sink<Double> opWrapSink(int param1Int, Sink<U> param1Sink) { return new Sink.ChainedDouble<U>(param1Sink) {
              public void accept(double param2Double) { this.downstream.accept(mapper.apply(param2Double)); }
            }; }
      };
  }
  
  public final IntStream mapToInt(final DoubleToIntFunction mapper) {
    Objects.requireNonNull(paramDoubleToIntFunction);
    return new IntPipeline.StatelessOp<Double>(this, StreamShape.DOUBLE_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
        Sink<Double> opWrapSink(int param1Int, Sink<Integer> param1Sink) { return new Sink.ChainedDouble<Integer>(param1Sink) {
              public void accept(double param2Double) { this.downstream.accept(mapper.applyAsInt(param2Double)); }
            }; }
      };
  }
  
  public final LongStream mapToLong(final DoubleToLongFunction mapper) {
    Objects.requireNonNull(paramDoubleToLongFunction);
    return new LongPipeline.StatelessOp<Double>(this, StreamShape.DOUBLE_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
        Sink<Double> opWrapSink(int param1Int, Sink<Long> param1Sink) { return new Sink.ChainedDouble<Long>(param1Sink) {
              public void accept(double param2Double) { this.downstream.accept(mapper.applyAsLong(param2Double)); }
            }; }
      };
  }
  
  public final DoubleStream flatMap(final DoubleFunction<? extends DoubleStream> mapper) { return new StatelessOp<Double>(this, StreamShape.DOUBLE_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) {
        Sink<Double> opWrapSink(int param1Int, Sink<Double> param1Sink) { return new Sink.ChainedDouble<Double>(param1Sink) {
              public void begin(long param2Long) { this.downstream.begin(-1L); }
              
              public void accept(double param2Double) {
                try (DoubleStream null = (DoubleStream)mapper.apply(param2Double)) {
                  if (doubleStream != null)
                    doubleStream.sequential().forEach(param2Double -> this.downstream.accept(param2Double)); 
                } 
              }
            }; }
      }; }
  
  public DoubleStream unordered() { return !isOrdered() ? this : new StatelessOp<Double>(this, StreamShape.DOUBLE_VALUE, StreamOpFlag.NOT_ORDERED) {
        Sink<Double> opWrapSink(int param1Int, Sink<Double> param1Sink) { return param1Sink; }
      }; }
  
  public final DoubleStream filter(final DoublePredicate predicate) {
    Objects.requireNonNull(paramDoublePredicate);
    return new StatelessOp<Double>(this, StreamShape.DOUBLE_VALUE, StreamOpFlag.NOT_SIZED) {
        Sink<Double> opWrapSink(int param1Int, Sink<Double> param1Sink) { return new Sink.ChainedDouble<Double>(param1Sink) {
              public void begin(long param2Long) { this.downstream.begin(-1L); }
              
              public void accept(double param2Double) {
                if (predicate.test(param2Double))
                  this.downstream.accept(param2Double); 
              }
            }; }
      };
  }
  
  public final DoubleStream peek(final DoubleConsumer action) {
    Objects.requireNonNull(paramDoubleConsumer);
    return new StatelessOp<Double>(this, StreamShape.DOUBLE_VALUE, 0) {
        Sink<Double> opWrapSink(int param1Int, Sink<Double> param1Sink) { return new Sink.ChainedDouble<Double>(param1Sink) {
              public void accept(double param2Double) {
                action.accept(param2Double);
                this.downstream.accept(param2Double);
              }
            }; }
      };
  }
  
  public final DoubleStream limit(long paramLong) {
    if (paramLong < 0L)
      throw new IllegalArgumentException(Long.toString(paramLong)); 
    return SliceOps.makeDouble(this, 0L, paramLong);
  }
  
  public final DoubleStream skip(long paramLong) {
    if (paramLong < 0L)
      throw new IllegalArgumentException(Long.toString(paramLong)); 
    if (paramLong == 0L)
      return this; 
    long l = -1L;
    return SliceOps.makeDouble(this, paramLong, l);
  }
  
  public final DoubleStream sorted() { return SortedOps.makeDouble(this); }
  
  public final DoubleStream distinct() { return boxed().distinct().mapToDouble(paramDouble -> paramDouble.doubleValue()); }
  
  public void forEach(DoubleConsumer paramDoubleConsumer) { evaluate(ForEachOps.makeDouble(paramDoubleConsumer, false)); }
  
  public void forEachOrdered(DoubleConsumer paramDoubleConsumer) { evaluate(ForEachOps.makeDouble(paramDoubleConsumer, true)); }
  
  public final double sum() {
    double[] arrayOfDouble = (double[])collect(() -> new double[3], (paramArrayOfDouble, paramDouble) -> {
          Collectors.sumWithCompensation(paramArrayOfDouble, paramDouble);
          paramArrayOfDouble[2] = paramArrayOfDouble[2] + paramDouble;
        }(paramArrayOfDouble1, paramArrayOfDouble2) -> {
          Collectors.sumWithCompensation(paramArrayOfDouble1, paramArrayOfDouble2[0]);
          Collectors.sumWithCompensation(paramArrayOfDouble1, paramArrayOfDouble2[1]);
          paramArrayOfDouble1[2] = paramArrayOfDouble1[2] + paramArrayOfDouble2[2];
        });
    return Collectors.computeFinalSum(arrayOfDouble);
  }
  
  public final OptionalDouble min() { return reduce(Math::min); }
  
  public final OptionalDouble max() { return reduce(Math::max); }
  
  public final OptionalDouble average() {
    double[] arrayOfDouble = (double[])collect(() -> new double[4], (paramArrayOfDouble, paramDouble) -> {
          paramArrayOfDouble[2] = paramArrayOfDouble[2] + 1.0D;
          Collectors.sumWithCompensation(paramArrayOfDouble, paramDouble);
          paramArrayOfDouble[3] = paramArrayOfDouble[3] + paramDouble;
        }(paramArrayOfDouble1, paramArrayOfDouble2) -> {
          Collectors.sumWithCompensation(paramArrayOfDouble1, paramArrayOfDouble2[0]);
          Collectors.sumWithCompensation(paramArrayOfDouble1, paramArrayOfDouble2[1]);
          paramArrayOfDouble1[2] = paramArrayOfDouble1[2] + paramArrayOfDouble2[2];
          paramArrayOfDouble1[3] = paramArrayOfDouble1[3] + paramArrayOfDouble2[3];
        });
    return (arrayOfDouble[2] > 0.0D) ? OptionalDouble.of(Collectors.computeFinalSum(arrayOfDouble) / arrayOfDouble[2]) : OptionalDouble.empty();
  }
  
  public final long count() { return mapToLong(paramDouble -> 1L).sum(); }
  
  public final DoubleSummaryStatistics summaryStatistics() { return (DoubleSummaryStatistics)collect(DoubleSummaryStatistics::new, DoubleSummaryStatistics::accept, DoubleSummaryStatistics::combine); }
  
  public final double reduce(double paramDouble, DoubleBinaryOperator paramDoubleBinaryOperator) { return ((Double)evaluate(ReduceOps.makeDouble(paramDouble, paramDoubleBinaryOperator))).doubleValue(); }
  
  public final OptionalDouble reduce(DoubleBinaryOperator paramDoubleBinaryOperator) { return (OptionalDouble)evaluate(ReduceOps.makeDouble(paramDoubleBinaryOperator)); }
  
  public final <R> R collect(Supplier<R> paramSupplier, ObjDoubleConsumer<R> paramObjDoubleConsumer, BiConsumer<R, R> paramBiConsumer) {
    BinaryOperator binaryOperator = (paramObject1, paramObject2) -> {
        paramBiConsumer.accept(paramObject1, paramObject2);
        return paramObject1;
      };
    return (R)evaluate(ReduceOps.makeDouble(paramSupplier, paramObjDoubleConsumer, binaryOperator));
  }
  
  public final boolean anyMatch(DoublePredicate paramDoublePredicate) { return ((Boolean)evaluate(MatchOps.makeDouble(paramDoublePredicate, MatchOps.MatchKind.ANY))).booleanValue(); }
  
  public final boolean allMatch(DoublePredicate paramDoublePredicate) { return ((Boolean)evaluate(MatchOps.makeDouble(paramDoublePredicate, MatchOps.MatchKind.ALL))).booleanValue(); }
  
  public final boolean noneMatch(DoublePredicate paramDoublePredicate) { return ((Boolean)evaluate(MatchOps.makeDouble(paramDoublePredicate, MatchOps.MatchKind.NONE))).booleanValue(); }
  
  public final OptionalDouble findFirst() { return (OptionalDouble)evaluate(FindOps.makeDouble(true)); }
  
  public final OptionalDouble findAny() { return (OptionalDouble)evaluate(FindOps.makeDouble(false)); }
  
  public final double[] toArray() { return (double[])Nodes.flattenDouble((Node.OfDouble)evaluateToArrayNode(paramInt -> new Double[paramInt])).asPrimitiveArray(); }
  
  static class Head<E_IN> extends DoublePipeline<E_IN> {
    Head(Supplier<? extends Spliterator<Double>> param1Supplier, int param1Int, boolean param1Boolean) { super(param1Supplier, param1Int, param1Boolean); }
    
    Head(Spliterator<Double> param1Spliterator, int param1Int, boolean param1Boolean) { super(param1Spliterator, param1Int, param1Boolean); }
    
    final boolean opIsStateful() { throw new UnsupportedOperationException(); }
    
    final Sink<E_IN> opWrapSink(int param1Int, Sink<Double> param1Sink) { throw new UnsupportedOperationException(); }
    
    public void forEach(DoubleConsumer param1DoubleConsumer) {
      if (!isParallel()) {
        DoublePipeline.adapt(sourceStageSpliterator()).forEachRemaining(param1DoubleConsumer);
      } else {
        super.forEach(param1DoubleConsumer);
      } 
    }
    
    public void forEachOrdered(DoubleConsumer param1DoubleConsumer) {
      if (!isParallel()) {
        DoublePipeline.adapt(sourceStageSpliterator()).forEachRemaining(param1DoubleConsumer);
      } else {
        super.forEachOrdered(param1DoubleConsumer);
      } 
    }
  }
  
  static abstract class StatefulOp<E_IN> extends DoublePipeline<E_IN> {
    StatefulOp(AbstractPipeline<?, E_IN, ?> param1AbstractPipeline, StreamShape param1StreamShape, int param1Int) {
      super(param1AbstractPipeline, param1Int);
      assert param1AbstractPipeline.getOutputShape() == param1StreamShape;
    }
    
    final boolean opIsStateful() { return true; }
    
    abstract <P_IN> Node<Double> opEvaluateParallel(PipelineHelper<Double> param1PipelineHelper, Spliterator<P_IN> param1Spliterator, IntFunction<Double[]> param1IntFunction);
  }
  
  static abstract class StatelessOp<E_IN> extends DoublePipeline<E_IN> {
    StatelessOp(AbstractPipeline<?, E_IN, ?> param1AbstractPipeline, StreamShape param1StreamShape, int param1Int) {
      super(param1AbstractPipeline, param1Int);
      assert param1AbstractPipeline.getOutputShape() == param1StreamShape;
    }
    
    final boolean opIsStateful() { return false; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\stream\DoublePipeline.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */