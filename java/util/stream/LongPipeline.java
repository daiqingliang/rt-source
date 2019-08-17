package java.util.stream;

import java.util.Iterator;
import java.util.LongSummaryStatistics;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.IntFunction;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;

abstract class LongPipeline<E_IN> extends AbstractPipeline<E_IN, Long, LongStream> implements LongStream {
  LongPipeline(Supplier<? extends Spliterator<Long>> paramSupplier, int paramInt, boolean paramBoolean) { super(paramSupplier, paramInt, paramBoolean); }
  
  LongPipeline(Spliterator<Long> paramSpliterator, int paramInt, boolean paramBoolean) { super(paramSpliterator, paramInt, paramBoolean); }
  
  LongPipeline(AbstractPipeline<?, E_IN, ?> paramAbstractPipeline, int paramInt) { super(paramAbstractPipeline, paramInt); }
  
  private static LongConsumer adapt(Sink<Long> paramSink) {
    if (paramSink instanceof LongConsumer)
      return (LongConsumer)paramSink; 
    if (Tripwire.ENABLED)
      Tripwire.trip(AbstractPipeline.class, "using LongStream.adapt(Sink<Long> s)"); 
    return paramSink::accept;
  }
  
  private static Spliterator.OfLong adapt(Spliterator<Long> paramSpliterator) {
    if (paramSpliterator instanceof Spliterator.OfLong)
      return (Spliterator.OfLong)paramSpliterator; 
    if (Tripwire.ENABLED)
      Tripwire.trip(AbstractPipeline.class, "using LongStream.adapt(Spliterator<Long> s)"); 
    throw new UnsupportedOperationException("LongStream.adapt(Spliterator<Long> s)");
  }
  
  final StreamShape getOutputShape() { return StreamShape.LONG_VALUE; }
  
  final <P_IN> Node<Long> evaluateToNode(PipelineHelper<Long> paramPipelineHelper, Spliterator<P_IN> paramSpliterator, boolean paramBoolean, IntFunction<Long[]> paramIntFunction) { return Nodes.collectLong(paramPipelineHelper, paramSpliterator, paramBoolean); }
  
  final <P_IN> Spliterator<Long> wrap(PipelineHelper<Long> paramPipelineHelper, Supplier<Spliterator<P_IN>> paramSupplier, boolean paramBoolean) { return new StreamSpliterators.LongWrappingSpliterator(paramPipelineHelper, paramSupplier, paramBoolean); }
  
  final Spliterator.OfLong lazySpliterator(Supplier<? extends Spliterator<Long>> paramSupplier) { return new StreamSpliterators.DelegatingSpliterator.OfLong(paramSupplier); }
  
  final void forEachWithCancel(Spliterator<Long> paramSpliterator, Sink<Long> paramSink) {
    Spliterator.OfLong ofLong = adapt(paramSpliterator);
    LongConsumer longConsumer = adapt(paramSink);
    do {
    
    } while (!paramSink.cancellationRequested() && ofLong.tryAdvance(longConsumer));
  }
  
  final Node.Builder<Long> makeNodeBuilder(long paramLong, IntFunction<Long[]> paramIntFunction) { return Nodes.longBuilder(paramLong); }
  
  public final PrimitiveIterator.OfLong iterator() { return Spliterators.iterator(spliterator()); }
  
  public final Spliterator.OfLong spliterator() { return adapt(super.spliterator()); }
  
  public final DoubleStream asDoubleStream() { return new DoublePipeline.StatelessOp<Long>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
        Sink<Long> opWrapSink(int param1Int, Sink<Double> param1Sink) { return new Sink.ChainedLong<Double>(param1Sink) {
              public void accept(long param2Long) { this.downstream.accept(param2Long); }
            }; }
      }; }
  
  public final Stream<Long> boxed() { return mapToObj(Long::valueOf); }
  
  public final LongStream map(final LongUnaryOperator mapper) {
    Objects.requireNonNull(paramLongUnaryOperator);
    return new StatelessOp<Long>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
        Sink<Long> opWrapSink(int param1Int, Sink<Long> param1Sink) { return new Sink.ChainedLong<Long>(param1Sink) {
              public void accept(long param2Long) { this.downstream.accept(mapper.applyAsLong(param2Long)); }
            }; }
      };
  }
  
  public final <U> Stream<U> mapToObj(final LongFunction<? extends U> mapper) {
    Objects.requireNonNull(paramLongFunction);
    return new ReferencePipeline.StatelessOp<Long, U>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
        Sink<Long> opWrapSink(int param1Int, Sink<U> param1Sink) { return new Sink.ChainedLong<U>(param1Sink) {
              public void accept(long param2Long) { this.downstream.accept(mapper.apply(param2Long)); }
            }; }
      };
  }
  
  public final IntStream mapToInt(final LongToIntFunction mapper) {
    Objects.requireNonNull(paramLongToIntFunction);
    return new IntPipeline.StatelessOp<Long>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
        Sink<Long> opWrapSink(int param1Int, Sink<Integer> param1Sink) { return new Sink.ChainedLong<Integer>(param1Sink) {
              public void accept(long param2Long) { this.downstream.accept(mapper.applyAsInt(param2Long)); }
            }; }
      };
  }
  
  public final DoubleStream mapToDouble(final LongToDoubleFunction mapper) {
    Objects.requireNonNull(paramLongToDoubleFunction);
    return new DoublePipeline.StatelessOp<Long>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
        Sink<Long> opWrapSink(int param1Int, Sink<Double> param1Sink) { return new Sink.ChainedLong<Double>(param1Sink) {
              public void accept(long param2Long) { this.downstream.accept(mapper.applyAsDouble(param2Long)); }
            }; }
      };
  }
  
  public final LongStream flatMap(final LongFunction<? extends LongStream> mapper) { return new StatelessOp<Long>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) {
        Sink<Long> opWrapSink(int param1Int, Sink<Long> param1Sink) { return new Sink.ChainedLong<Long>(param1Sink) {
              public void begin(long param2Long) { this.downstream.begin(-1L); }
              
              public void accept(long param2Long) {
                try (LongStream null = (LongStream)mapper.apply(param2Long)) {
                  if (longStream != null)
                    longStream.sequential().forEach(param2Long -> this.downstream.accept(param2Long)); 
                } 
              }
            }; }
      }; }
  
  public LongStream unordered() { return !isOrdered() ? this : new StatelessOp<Long>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_ORDERED) {
        Sink<Long> opWrapSink(int param1Int, Sink<Long> param1Sink) { return param1Sink; }
      }; }
  
  public final LongStream filter(final LongPredicate predicate) {
    Objects.requireNonNull(paramLongPredicate);
    return new StatelessOp<Long>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_SIZED) {
        Sink<Long> opWrapSink(int param1Int, Sink<Long> param1Sink) { return new Sink.ChainedLong<Long>(param1Sink) {
              public void begin(long param2Long) { this.downstream.begin(-1L); }
              
              public void accept(long param2Long) {
                if (predicate.test(param2Long))
                  this.downstream.accept(param2Long); 
              }
            }; }
      };
  }
  
  public final LongStream peek(final LongConsumer action) {
    Objects.requireNonNull(paramLongConsumer);
    return new StatelessOp<Long>(this, StreamShape.LONG_VALUE, 0) {
        Sink<Long> opWrapSink(int param1Int, Sink<Long> param1Sink) { return new Sink.ChainedLong<Long>(param1Sink) {
              public void accept(long param2Long) {
                action.accept(param2Long);
                this.downstream.accept(param2Long);
              }
            }; }
      };
  }
  
  public final LongStream limit(long paramLong) {
    if (paramLong < 0L)
      throw new IllegalArgumentException(Long.toString(paramLong)); 
    return SliceOps.makeLong(this, 0L, paramLong);
  }
  
  public final LongStream skip(long paramLong) {
    if (paramLong < 0L)
      throw new IllegalArgumentException(Long.toString(paramLong)); 
    return (paramLong == 0L) ? this : SliceOps.makeLong(this, paramLong, -1L);
  }
  
  public final LongStream sorted() { return SortedOps.makeLong(this); }
  
  public final LongStream distinct() { return boxed().distinct().mapToLong(paramLong -> paramLong.longValue()); }
  
  public void forEach(LongConsumer paramLongConsumer) { evaluate(ForEachOps.makeLong(paramLongConsumer, false)); }
  
  public void forEachOrdered(LongConsumer paramLongConsumer) { evaluate(ForEachOps.makeLong(paramLongConsumer, true)); }
  
  public final long sum() { return reduce(0L, Long::sum); }
  
  public final OptionalLong min() { return reduce(Math::min); }
  
  public final OptionalLong max() { return reduce(Math::max); }
  
  public final OptionalDouble average() {
    long[] arrayOfLong = (long[])collect(() -> new long[2], (paramArrayOfLong, paramLong) -> {
          paramArrayOfLong[0] = paramArrayOfLong[0] + 1L;
          paramArrayOfLong[1] = paramArrayOfLong[1] + paramLong;
        }(paramArrayOfLong1, paramArrayOfLong2) -> {
          paramArrayOfLong1[0] = paramArrayOfLong1[0] + paramArrayOfLong2[0];
          paramArrayOfLong1[1] = paramArrayOfLong1[1] + paramArrayOfLong2[1];
        });
    return (arrayOfLong[0] > 0L) ? OptionalDouble.of(arrayOfLong[1] / arrayOfLong[0]) : OptionalDouble.empty();
  }
  
  public final long count() { return map(paramLong -> 1L).sum(); }
  
  public final LongSummaryStatistics summaryStatistics() { return (LongSummaryStatistics)collect(LongSummaryStatistics::new, LongSummaryStatistics::accept, LongSummaryStatistics::combine); }
  
  public final long reduce(long paramLong, LongBinaryOperator paramLongBinaryOperator) { return ((Long)evaluate(ReduceOps.makeLong(paramLong, paramLongBinaryOperator))).longValue(); }
  
  public final OptionalLong reduce(LongBinaryOperator paramLongBinaryOperator) { return (OptionalLong)evaluate(ReduceOps.makeLong(paramLongBinaryOperator)); }
  
  public final <R> R collect(Supplier<R> paramSupplier, ObjLongConsumer<R> paramObjLongConsumer, BiConsumer<R, R> paramBiConsumer) {
    BinaryOperator binaryOperator = (paramObject1, paramObject2) -> {
        paramBiConsumer.accept(paramObject1, paramObject2);
        return paramObject1;
      };
    return (R)evaluate(ReduceOps.makeLong(paramSupplier, paramObjLongConsumer, binaryOperator));
  }
  
  public final boolean anyMatch(LongPredicate paramLongPredicate) { return ((Boolean)evaluate(MatchOps.makeLong(paramLongPredicate, MatchOps.MatchKind.ANY))).booleanValue(); }
  
  public final boolean allMatch(LongPredicate paramLongPredicate) { return ((Boolean)evaluate(MatchOps.makeLong(paramLongPredicate, MatchOps.MatchKind.ALL))).booleanValue(); }
  
  public final boolean noneMatch(LongPredicate paramLongPredicate) { return ((Boolean)evaluate(MatchOps.makeLong(paramLongPredicate, MatchOps.MatchKind.NONE))).booleanValue(); }
  
  public final OptionalLong findFirst() { return (OptionalLong)evaluate(FindOps.makeLong(true)); }
  
  public final OptionalLong findAny() { return (OptionalLong)evaluate(FindOps.makeLong(false)); }
  
  public final long[] toArray() { return (long[])Nodes.flattenLong((Node.OfLong)evaluateToArrayNode(paramInt -> new Long[paramInt])).asPrimitiveArray(); }
  
  static class Head<E_IN> extends LongPipeline<E_IN> {
    Head(Supplier<? extends Spliterator<Long>> param1Supplier, int param1Int, boolean param1Boolean) { super(param1Supplier, param1Int, param1Boolean); }
    
    Head(Spliterator<Long> param1Spliterator, int param1Int, boolean param1Boolean) { super(param1Spliterator, param1Int, param1Boolean); }
    
    final boolean opIsStateful() { throw new UnsupportedOperationException(); }
    
    final Sink<E_IN> opWrapSink(int param1Int, Sink<Long> param1Sink) { throw new UnsupportedOperationException(); }
    
    public void forEach(LongConsumer param1LongConsumer) {
      if (!isParallel()) {
        LongPipeline.adapt(sourceStageSpliterator()).forEachRemaining(param1LongConsumer);
      } else {
        super.forEach(param1LongConsumer);
      } 
    }
    
    public void forEachOrdered(LongConsumer param1LongConsumer) {
      if (!isParallel()) {
        LongPipeline.adapt(sourceStageSpliterator()).forEachRemaining(param1LongConsumer);
      } else {
        super.forEachOrdered(param1LongConsumer);
      } 
    }
  }
  
  static abstract class StatefulOp<E_IN> extends LongPipeline<E_IN> {
    StatefulOp(AbstractPipeline<?, E_IN, ?> param1AbstractPipeline, StreamShape param1StreamShape, int param1Int) {
      super(param1AbstractPipeline, param1Int);
      assert param1AbstractPipeline.getOutputShape() == param1StreamShape;
    }
    
    final boolean opIsStateful() { return true; }
    
    abstract <P_IN> Node<Long> opEvaluateParallel(PipelineHelper<Long> param1PipelineHelper, Spliterator<P_IN> param1Spliterator, IntFunction<Long[]> param1IntFunction);
  }
  
  static abstract class StatelessOp<E_IN> extends LongPipeline<E_IN> {
    StatelessOp(AbstractPipeline<?, E_IN, ?> param1AbstractPipeline, StreamShape param1StreamShape, int param1Int) {
      super(param1AbstractPipeline, param1Int);
      assert param1AbstractPipeline.getOutputShape() == param1StreamShape;
    }
    
    final boolean opIsStateful() { return false; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\stream\LongPipeline.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */