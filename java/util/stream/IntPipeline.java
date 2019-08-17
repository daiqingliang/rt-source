package java.util.stream;

import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;

abstract class IntPipeline<E_IN> extends AbstractPipeline<E_IN, Integer, IntStream> implements IntStream {
  IntPipeline(Supplier<? extends Spliterator<Integer>> paramSupplier, int paramInt, boolean paramBoolean) { super(paramSupplier, paramInt, paramBoolean); }
  
  IntPipeline(Spliterator<Integer> paramSpliterator, int paramInt, boolean paramBoolean) { super(paramSpliterator, paramInt, paramBoolean); }
  
  IntPipeline(AbstractPipeline<?, E_IN, ?> paramAbstractPipeline, int paramInt) { super(paramAbstractPipeline, paramInt); }
  
  private static IntConsumer adapt(Sink<Integer> paramSink) {
    if (paramSink instanceof IntConsumer)
      return (IntConsumer)paramSink; 
    if (Tripwire.ENABLED)
      Tripwire.trip(AbstractPipeline.class, "using IntStream.adapt(Sink<Integer> s)"); 
    return paramSink::accept;
  }
  
  private static Spliterator.OfInt adapt(Spliterator<Integer> paramSpliterator) {
    if (paramSpliterator instanceof Spliterator.OfInt)
      return (Spliterator.OfInt)paramSpliterator; 
    if (Tripwire.ENABLED)
      Tripwire.trip(AbstractPipeline.class, "using IntStream.adapt(Spliterator<Integer> s)"); 
    throw new UnsupportedOperationException("IntStream.adapt(Spliterator<Integer> s)");
  }
  
  final StreamShape getOutputShape() { return StreamShape.INT_VALUE; }
  
  final <P_IN> Node<Integer> evaluateToNode(PipelineHelper<Integer> paramPipelineHelper, Spliterator<P_IN> paramSpliterator, boolean paramBoolean, IntFunction<Integer[]> paramIntFunction) { return Nodes.collectInt(paramPipelineHelper, paramSpliterator, paramBoolean); }
  
  final <P_IN> Spliterator<Integer> wrap(PipelineHelper<Integer> paramPipelineHelper, Supplier<Spliterator<P_IN>> paramSupplier, boolean paramBoolean) { return new StreamSpliterators.IntWrappingSpliterator(paramPipelineHelper, paramSupplier, paramBoolean); }
  
  final Spliterator.OfInt lazySpliterator(Supplier<? extends Spliterator<Integer>> paramSupplier) { return new StreamSpliterators.DelegatingSpliterator.OfInt(paramSupplier); }
  
  final void forEachWithCancel(Spliterator<Integer> paramSpliterator, Sink<Integer> paramSink) {
    Spliterator.OfInt ofInt = adapt(paramSpliterator);
    IntConsumer intConsumer = adapt(paramSink);
    do {
    
    } while (!paramSink.cancellationRequested() && ofInt.tryAdvance(intConsumer));
  }
  
  final Node.Builder<Integer> makeNodeBuilder(long paramLong, IntFunction<Integer[]> paramIntFunction) { return Nodes.intBuilder(paramLong); }
  
  public final PrimitiveIterator.OfInt iterator() { return Spliterators.iterator(spliterator()); }
  
  public final Spliterator.OfInt spliterator() { return adapt(super.spliterator()); }
  
  public final LongStream asLongStream() { return new LongPipeline.StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
        Sink<Integer> opWrapSink(int param1Int, Sink<Long> param1Sink) { return new Sink.ChainedInt<Long>(param1Sink) {
              public void accept(int param2Int) { this.downstream.accept(param2Int); }
            }; }
      }; }
  
  public final DoubleStream asDoubleStream() { return new DoublePipeline.StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
        Sink<Integer> opWrapSink(int param1Int, Sink<Double> param1Sink) { return new Sink.ChainedInt<Double>(param1Sink) {
              public void accept(int param2Int) { this.downstream.accept(param2Int); }
            }; }
      }; }
  
  public final Stream<Integer> boxed() { return mapToObj(Integer::valueOf); }
  
  public final IntStream map(final IntUnaryOperator mapper) {
    Objects.requireNonNull(paramIntUnaryOperator);
    return new StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
        Sink<Integer> opWrapSink(int param1Int, Sink<Integer> param1Sink) { return new Sink.ChainedInt<Integer>(param1Sink) {
              public void accept(int param2Int) { this.downstream.accept(mapper.applyAsInt(param2Int)); }
            }; }
      };
  }
  
  public final <U> Stream<U> mapToObj(final IntFunction<? extends U> mapper) {
    Objects.requireNonNull(paramIntFunction);
    return new ReferencePipeline.StatelessOp<Integer, U>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
        Sink<Integer> opWrapSink(int param1Int, Sink<U> param1Sink) { return new Sink.ChainedInt<U>(param1Sink) {
              public void accept(int param2Int) { this.downstream.accept(mapper.apply(param2Int)); }
            }; }
      };
  }
  
  public final LongStream mapToLong(final IntToLongFunction mapper) {
    Objects.requireNonNull(paramIntToLongFunction);
    return new LongPipeline.StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
        Sink<Integer> opWrapSink(int param1Int, Sink<Long> param1Sink) { return new Sink.ChainedInt<Long>(param1Sink) {
              public void accept(int param2Int) { this.downstream.accept(mapper.applyAsLong(param2Int)); }
            }; }
      };
  }
  
  public final DoubleStream mapToDouble(final IntToDoubleFunction mapper) {
    Objects.requireNonNull(paramIntToDoubleFunction);
    return new DoublePipeline.StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
        Sink<Integer> opWrapSink(int param1Int, Sink<Double> param1Sink) { return new Sink.ChainedInt<Double>(param1Sink) {
              public void accept(int param2Int) { this.downstream.accept(mapper.applyAsDouble(param2Int)); }
            }; }
      };
  }
  
  public final IntStream flatMap(final IntFunction<? extends IntStream> mapper) { return new StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) {
        Sink<Integer> opWrapSink(int param1Int, Sink<Integer> param1Sink) { return new Sink.ChainedInt<Integer>(param1Sink) {
              public void begin(long param2Long) { this.downstream.begin(-1L); }
              
              public void accept(int param2Int) {
                try (IntStream null = (IntStream)mapper.apply(param2Int)) {
                  if (intStream != null)
                    intStream.sequential().forEach(param2Int -> this.downstream.accept(param2Int)); 
                } 
              }
            }; }
      }; }
  
  public IntStream unordered() { return !isOrdered() ? this : new StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_ORDERED) {
        Sink<Integer> opWrapSink(int param1Int, Sink<Integer> param1Sink) { return param1Sink; }
      }; }
  
  public final IntStream filter(final IntPredicate predicate) {
    Objects.requireNonNull(paramIntPredicate);
    return new StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SIZED) {
        Sink<Integer> opWrapSink(int param1Int, Sink<Integer> param1Sink) { return new Sink.ChainedInt<Integer>(param1Sink) {
              public void begin(long param2Long) { this.downstream.begin(-1L); }
              
              public void accept(int param2Int) {
                if (predicate.test(param2Int))
                  this.downstream.accept(param2Int); 
              }
            }; }
      };
  }
  
  public final IntStream peek(final IntConsumer action) {
    Objects.requireNonNull(paramIntConsumer);
    return new StatelessOp<Integer>(this, StreamShape.INT_VALUE, 0) {
        Sink<Integer> opWrapSink(int param1Int, Sink<Integer> param1Sink) { return new Sink.ChainedInt<Integer>(param1Sink) {
              public void accept(int param2Int) {
                action.accept(param2Int);
                this.downstream.accept(param2Int);
              }
            }; }
      };
  }
  
  public final IntStream limit(long paramLong) {
    if (paramLong < 0L)
      throw new IllegalArgumentException(Long.toString(paramLong)); 
    return SliceOps.makeInt(this, 0L, paramLong);
  }
  
  public final IntStream skip(long paramLong) {
    if (paramLong < 0L)
      throw new IllegalArgumentException(Long.toString(paramLong)); 
    return (paramLong == 0L) ? this : SliceOps.makeInt(this, paramLong, -1L);
  }
  
  public final IntStream sorted() { return SortedOps.makeInt(this); }
  
  public final IntStream distinct() { return boxed().distinct().mapToInt(paramInteger -> paramInteger.intValue()); }
  
  public void forEach(IntConsumer paramIntConsumer) { evaluate(ForEachOps.makeInt(paramIntConsumer, false)); }
  
  public void forEachOrdered(IntConsumer paramIntConsumer) { evaluate(ForEachOps.makeInt(paramIntConsumer, true)); }
  
  public final int sum() { return reduce(0, Integer::sum); }
  
  public final OptionalInt min() { return reduce(Math::min); }
  
  public final OptionalInt max() { return reduce(Math::max); }
  
  public final long count() { return mapToLong(paramInt -> 1L).sum(); }
  
  public final OptionalDouble average() {
    long[] arrayOfLong = (long[])collect(() -> new long[2], (paramArrayOfLong, paramInt) -> {
          paramArrayOfLong[0] = paramArrayOfLong[0] + 1L;
          paramArrayOfLong[1] = paramArrayOfLong[1] + paramInt;
        }(paramArrayOfLong1, paramArrayOfLong2) -> {
          paramArrayOfLong1[0] = paramArrayOfLong1[0] + paramArrayOfLong2[0];
          paramArrayOfLong1[1] = paramArrayOfLong1[1] + paramArrayOfLong2[1];
        });
    return (arrayOfLong[0] > 0L) ? OptionalDouble.of(arrayOfLong[1] / arrayOfLong[0]) : OptionalDouble.empty();
  }
  
  public final IntSummaryStatistics summaryStatistics() { return (IntSummaryStatistics)collect(IntSummaryStatistics::new, IntSummaryStatistics::accept, IntSummaryStatistics::combine); }
  
  public final int reduce(int paramInt, IntBinaryOperator paramIntBinaryOperator) { return ((Integer)evaluate(ReduceOps.makeInt(paramInt, paramIntBinaryOperator))).intValue(); }
  
  public final OptionalInt reduce(IntBinaryOperator paramIntBinaryOperator) { return (OptionalInt)evaluate(ReduceOps.makeInt(paramIntBinaryOperator)); }
  
  public final <R> R collect(Supplier<R> paramSupplier, ObjIntConsumer<R> paramObjIntConsumer, BiConsumer<R, R> paramBiConsumer) {
    BinaryOperator binaryOperator = (paramObject1, paramObject2) -> {
        paramBiConsumer.accept(paramObject1, paramObject2);
        return paramObject1;
      };
    return (R)evaluate(ReduceOps.makeInt(paramSupplier, paramObjIntConsumer, binaryOperator));
  }
  
  public final boolean anyMatch(IntPredicate paramIntPredicate) { return ((Boolean)evaluate(MatchOps.makeInt(paramIntPredicate, MatchOps.MatchKind.ANY))).booleanValue(); }
  
  public final boolean allMatch(IntPredicate paramIntPredicate) { return ((Boolean)evaluate(MatchOps.makeInt(paramIntPredicate, MatchOps.MatchKind.ALL))).booleanValue(); }
  
  public final boolean noneMatch(IntPredicate paramIntPredicate) { return ((Boolean)evaluate(MatchOps.makeInt(paramIntPredicate, MatchOps.MatchKind.NONE))).booleanValue(); }
  
  public final OptionalInt findFirst() { return (OptionalInt)evaluate(FindOps.makeInt(true)); }
  
  public final OptionalInt findAny() { return (OptionalInt)evaluate(FindOps.makeInt(false)); }
  
  public final int[] toArray() { return (int[])Nodes.flattenInt((Node.OfInt)evaluateToArrayNode(paramInt -> new Integer[paramInt])).asPrimitiveArray(); }
  
  static class Head<E_IN> extends IntPipeline<E_IN> {
    Head(Supplier<? extends Spliterator<Integer>> param1Supplier, int param1Int, boolean param1Boolean) { super(param1Supplier, param1Int, param1Boolean); }
    
    Head(Spliterator<Integer> param1Spliterator, int param1Int, boolean param1Boolean) { super(param1Spliterator, param1Int, param1Boolean); }
    
    final boolean opIsStateful() { throw new UnsupportedOperationException(); }
    
    final Sink<E_IN> opWrapSink(int param1Int, Sink<Integer> param1Sink) { throw new UnsupportedOperationException(); }
    
    public void forEach(IntConsumer param1IntConsumer) {
      if (!isParallel()) {
        IntPipeline.adapt(sourceStageSpliterator()).forEachRemaining(param1IntConsumer);
      } else {
        super.forEach(param1IntConsumer);
      } 
    }
    
    public void forEachOrdered(IntConsumer param1IntConsumer) {
      if (!isParallel()) {
        IntPipeline.adapt(sourceStageSpliterator()).forEachRemaining(param1IntConsumer);
      } else {
        super.forEachOrdered(param1IntConsumer);
      } 
    }
  }
  
  static abstract class StatefulOp<E_IN> extends IntPipeline<E_IN> {
    StatefulOp(AbstractPipeline<?, E_IN, ?> param1AbstractPipeline, StreamShape param1StreamShape, int param1Int) {
      super(param1AbstractPipeline, param1Int);
      assert param1AbstractPipeline.getOutputShape() == param1StreamShape;
    }
    
    final boolean opIsStateful() { return true; }
    
    abstract <P_IN> Node<Integer> opEvaluateParallel(PipelineHelper<Integer> param1PipelineHelper, Spliterator<P_IN> param1Spliterator, IntFunction<Integer[]> param1IntFunction);
  }
  
  static abstract class StatelessOp<E_IN> extends IntPipeline<E_IN> {
    StatelessOp(AbstractPipeline<?, E_IN, ?> param1AbstractPipeline, StreamShape param1StreamShape, int param1Int) {
      super(param1AbstractPipeline, param1Int);
      assert param1AbstractPipeline.getOutputShape() == param1StreamShape;
    }
    
    final boolean opIsStateful() { return false; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\stream\IntPipeline.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */