package java.util.stream;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.LongConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

abstract class ReferencePipeline<P_IN, P_OUT> extends AbstractPipeline<P_IN, P_OUT, Stream<P_OUT>> implements Stream<P_OUT> {
  ReferencePipeline(Supplier<? extends Spliterator<?>> paramSupplier, int paramInt, boolean paramBoolean) { super(paramSupplier, paramInt, paramBoolean); }
  
  ReferencePipeline(Spliterator<?> paramSpliterator, int paramInt, boolean paramBoolean) { super(paramSpliterator, paramInt, paramBoolean); }
  
  ReferencePipeline(AbstractPipeline<?, P_IN, ?> paramAbstractPipeline, int paramInt) { super(paramAbstractPipeline, paramInt); }
  
  final StreamShape getOutputShape() { return StreamShape.REFERENCE; }
  
  final <P_IN> Node<P_OUT> evaluateToNode(PipelineHelper<P_OUT> paramPipelineHelper, Spliterator<P_IN> paramSpliterator, boolean paramBoolean, IntFunction<P_OUT[]> paramIntFunction) { return Nodes.collect(paramPipelineHelper, paramSpliterator, paramBoolean, paramIntFunction); }
  
  final <P_IN> Spliterator<P_OUT> wrap(PipelineHelper<P_OUT> paramPipelineHelper, Supplier<Spliterator<P_IN>> paramSupplier, boolean paramBoolean) { return new StreamSpliterators.WrappingSpliterator(paramPipelineHelper, paramSupplier, paramBoolean); }
  
  final Spliterator<P_OUT> lazySpliterator(Supplier<? extends Spliterator<P_OUT>> paramSupplier) { return new StreamSpliterators.DelegatingSpliterator(paramSupplier); }
  
  final void forEachWithCancel(Spliterator<P_OUT> paramSpliterator, Sink<P_OUT> paramSink) {
    do {
    
    } while (!paramSink.cancellationRequested() && paramSpliterator.tryAdvance(paramSink));
  }
  
  final Node.Builder<P_OUT> makeNodeBuilder(long paramLong, IntFunction<P_OUT[]> paramIntFunction) { return Nodes.builder(paramLong, paramIntFunction); }
  
  public final Iterator<P_OUT> iterator() { return Spliterators.iterator(spliterator()); }
  
  public Stream<P_OUT> unordered() { return !isOrdered() ? this : new StatelessOp<P_OUT, P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_ORDERED) {
        Sink<P_OUT> opWrapSink(int param1Int, Sink<P_OUT> param1Sink) { return param1Sink; }
      }; }
  
  public final Stream<P_OUT> filter(final Predicate<? super P_OUT> predicate) {
    Objects.requireNonNull(paramPredicate);
    return new StatelessOp<P_OUT, P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SIZED) {
        Sink<P_OUT> opWrapSink(int param1Int, Sink<P_OUT> param1Sink) { return new Sink.ChainedReference<P_OUT, P_OUT>(param1Sink) {
              public void begin(long param2Long) { this.downstream.begin(-1L); }
              
              public void accept(P_OUT param2P_OUT) {
                if (predicate.test(param2P_OUT))
                  this.downstream.accept(param2P_OUT); 
              }
            }; }
      };
  }
  
  public final <R> Stream<R> map(final Function<? super P_OUT, ? extends R> mapper) {
    Objects.requireNonNull(paramFunction);
    return new StatelessOp<P_OUT, R>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
        Sink<P_OUT> opWrapSink(int param1Int, Sink<R> param1Sink) { return new Sink.ChainedReference<P_OUT, R>(param1Sink) {
              public void accept(P_OUT param2P_OUT) { this.downstream.accept(mapper.apply(param2P_OUT)); }
            }; }
      };
  }
  
  public final IntStream mapToInt(final ToIntFunction<? super P_OUT> mapper) {
    Objects.requireNonNull(paramToIntFunction);
    return new IntPipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
        Sink<P_OUT> opWrapSink(int param1Int, Sink<Integer> param1Sink) { return new Sink.ChainedReference<P_OUT, Integer>(param1Sink) {
              public void accept(P_OUT param2P_OUT) { this.downstream.accept(mapper.applyAsInt(param2P_OUT)); }
            }; }
      };
  }
  
  public final LongStream mapToLong(final ToLongFunction<? super P_OUT> mapper) {
    Objects.requireNonNull(paramToLongFunction);
    return new LongPipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
        Sink<P_OUT> opWrapSink(int param1Int, Sink<Long> param1Sink) { return new Sink.ChainedReference<P_OUT, Long>(param1Sink) {
              public void accept(P_OUT param2P_OUT) { this.downstream.accept(mapper.applyAsLong(param2P_OUT)); }
            }; }
      };
  }
  
  public final DoubleStream mapToDouble(final ToDoubleFunction<? super P_OUT> mapper) {
    Objects.requireNonNull(paramToDoubleFunction);
    return new DoublePipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
        Sink<P_OUT> opWrapSink(int param1Int, Sink<Double> param1Sink) { return new Sink.ChainedReference<P_OUT, Double>(param1Sink) {
              public void accept(P_OUT param2P_OUT) { this.downstream.accept(mapper.applyAsDouble(param2P_OUT)); }
            }; }
      };
  }
  
  public final <R> Stream<R> flatMap(final Function<? super P_OUT, ? extends Stream<? extends R>> mapper) {
    Objects.requireNonNull(paramFunction);
    return new StatelessOp<P_OUT, R>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) {
        Sink<P_OUT> opWrapSink(int param1Int, Sink<R> param1Sink) { return new Sink.ChainedReference<P_OUT, R>(param1Sink) {
              public void begin(long param2Long) { this.downstream.begin(-1L); }
              
              public void accept(P_OUT param2P_OUT) {
                try (Stream null = (Stream)mapper.apply(param2P_OUT)) {
                  if (stream != null)
                    ((Stream)stream.sequential()).forEach(this.downstream); 
                } 
              }
            }; }
      };
  }
  
  public final IntStream flatMapToInt(final Function<? super P_OUT, ? extends IntStream> mapper) {
    Objects.requireNonNull(paramFunction);
    return new IntPipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) {
        Sink<P_OUT> opWrapSink(int param1Int, Sink<Integer> param1Sink) { return new Sink.ChainedReference<P_OUT, Integer>(param1Sink) {
              IntConsumer downstreamAsInt = this.downstream::accept;
              
              public void begin(long param2Long) { this.downstream.begin(-1L); }
              
              public void accept(P_OUT param2P_OUT) {
                try (IntStream null = (IntStream)mapper.apply(param2P_OUT)) {
                  if (intStream != null)
                    intStream.sequential().forEach(this.downstreamAsInt); 
                } 
              }
            }; }
      };
  }
  
  public final DoubleStream flatMapToDouble(final Function<? super P_OUT, ? extends DoubleStream> mapper) {
    Objects.requireNonNull(paramFunction);
    return new DoublePipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) {
        Sink<P_OUT> opWrapSink(int param1Int, Sink<Double> param1Sink) { return new Sink.ChainedReference<P_OUT, Double>(param1Sink) {
              DoubleConsumer downstreamAsDouble = this.downstream::accept;
              
              public void begin(long param2Long) { this.downstream.begin(-1L); }
              
              public void accept(P_OUT param2P_OUT) {
                try (DoubleStream null = (DoubleStream)mapper.apply(param2P_OUT)) {
                  if (doubleStream != null)
                    doubleStream.sequential().forEach(this.downstreamAsDouble); 
                } 
              }
            }; }
      };
  }
  
  public final LongStream flatMapToLong(final Function<? super P_OUT, ? extends LongStream> mapper) {
    Objects.requireNonNull(paramFunction);
    return new LongPipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) {
        Sink<P_OUT> opWrapSink(int param1Int, Sink<Long> param1Sink) { return new Sink.ChainedReference<P_OUT, Long>(param1Sink) {
              LongConsumer downstreamAsLong = this.downstream::accept;
              
              public void begin(long param2Long) { this.downstream.begin(-1L); }
              
              public void accept(P_OUT param2P_OUT) {
                try (LongStream null = (LongStream)mapper.apply(param2P_OUT)) {
                  if (longStream != null)
                    longStream.sequential().forEach(this.downstreamAsLong); 
                } 
              }
            }; }
      };
  }
  
  public final Stream<P_OUT> peek(final Consumer<? super P_OUT> action) {
    Objects.requireNonNull(paramConsumer);
    return new StatelessOp<P_OUT, P_OUT>(this, StreamShape.REFERENCE, 0) {
        Sink<P_OUT> opWrapSink(int param1Int, Sink<P_OUT> param1Sink) { return new Sink.ChainedReference<P_OUT, P_OUT>(param1Sink) {
              public void accept(P_OUT param2P_OUT) {
                action.accept(param2P_OUT);
                this.downstream.accept(param2P_OUT);
              }
            }; }
      };
  }
  
  public final Stream<P_OUT> distinct() { return DistinctOps.makeRef(this); }
  
  public final Stream<P_OUT> sorted() { return SortedOps.makeRef(this); }
  
  public final Stream<P_OUT> sorted(Comparator<? super P_OUT> paramComparator) { return SortedOps.makeRef(this, paramComparator); }
  
  public final Stream<P_OUT> limit(long paramLong) {
    if (paramLong < 0L)
      throw new IllegalArgumentException(Long.toString(paramLong)); 
    return SliceOps.makeRef(this, 0L, paramLong);
  }
  
  public final Stream<P_OUT> skip(long paramLong) {
    if (paramLong < 0L)
      throw new IllegalArgumentException(Long.toString(paramLong)); 
    return (paramLong == 0L) ? this : SliceOps.makeRef(this, paramLong, -1L);
  }
  
  public void forEach(Consumer<? super P_OUT> paramConsumer) { evaluate(ForEachOps.makeRef(paramConsumer, false)); }
  
  public void forEachOrdered(Consumer<? super P_OUT> paramConsumer) { evaluate(ForEachOps.makeRef(paramConsumer, true)); }
  
  public final <A> A[] toArray(IntFunction<A[]> paramIntFunction) {
    IntFunction<A[]> intFunction = paramIntFunction;
    return (A[])(Object[])Nodes.flatten(evaluateToArrayNode(intFunction), intFunction).asArray(intFunction);
  }
  
  public final Object[] toArray() { return toArray(paramInt -> new Object[paramInt]); }
  
  public final boolean anyMatch(Predicate<? super P_OUT> paramPredicate) { return ((Boolean)evaluate(MatchOps.makeRef(paramPredicate, MatchOps.MatchKind.ANY))).booleanValue(); }
  
  public final boolean allMatch(Predicate<? super P_OUT> paramPredicate) { return ((Boolean)evaluate(MatchOps.makeRef(paramPredicate, MatchOps.MatchKind.ALL))).booleanValue(); }
  
  public final boolean noneMatch(Predicate<? super P_OUT> paramPredicate) { return ((Boolean)evaluate(MatchOps.makeRef(paramPredicate, MatchOps.MatchKind.NONE))).booleanValue(); }
  
  public final Optional<P_OUT> findFirst() { return (Optional)evaluate(FindOps.makeRef(true)); }
  
  public final Optional<P_OUT> findAny() { return (Optional)evaluate(FindOps.makeRef(false)); }
  
  public final P_OUT reduce(P_OUT paramP_OUT, BinaryOperator<P_OUT> paramBinaryOperator) { return (P_OUT)evaluate(ReduceOps.makeRef(paramP_OUT, paramBinaryOperator, paramBinaryOperator)); }
  
  public final Optional<P_OUT> reduce(BinaryOperator<P_OUT> paramBinaryOperator) { return (Optional)evaluate(ReduceOps.makeRef(paramBinaryOperator)); }
  
  public final <R> R reduce(R paramR, BiFunction<R, ? super P_OUT, R> paramBiFunction, BinaryOperator<R> paramBinaryOperator) { return (R)evaluate(ReduceOps.makeRef(paramR, paramBiFunction, paramBinaryOperator)); }
  
  public final <R, A> R collect(Collector<? super P_OUT, A, R> paramCollector) {
    Object object;
    if (isParallel() && paramCollector.characteristics().contains(Collector.Characteristics.CONCURRENT) && (!isOrdered() || paramCollector.characteristics().contains(Collector.Characteristics.UNORDERED))) {
      object = paramCollector.supplier().get();
      BiConsumer biConsumer = paramCollector.accumulator();
      forEach(paramObject2 -> paramBiConsumer.accept(paramObject1, paramObject2));
    } else {
      object = evaluate(ReduceOps.makeRef(paramCollector));
    } 
    return (R)(paramCollector.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH) ? object : paramCollector.finisher().apply(object));
  }
  
  public final <R> R collect(Supplier<R> paramSupplier, BiConsumer<R, ? super P_OUT> paramBiConsumer1, BiConsumer<R, R> paramBiConsumer2) { return (R)evaluate(ReduceOps.makeRef(paramSupplier, paramBiConsumer1, paramBiConsumer2)); }
  
  public final Optional<P_OUT> max(Comparator<? super P_OUT> paramComparator) { return reduce(BinaryOperator.maxBy(paramComparator)); }
  
  public final Optional<P_OUT> min(Comparator<? super P_OUT> paramComparator) { return reduce(BinaryOperator.minBy(paramComparator)); }
  
  public final long count() { return mapToLong(paramObject -> 1L).sum(); }
  
  static class Head<E_IN, E_OUT> extends ReferencePipeline<E_IN, E_OUT> {
    Head(Supplier<? extends Spliterator<?>> param1Supplier, int param1Int, boolean param1Boolean) { super(param1Supplier, param1Int, param1Boolean); }
    
    Head(Spliterator<?> param1Spliterator, int param1Int, boolean param1Boolean) { super(param1Spliterator, param1Int, param1Boolean); }
    
    final boolean opIsStateful() { throw new UnsupportedOperationException(); }
    
    final Sink<E_IN> opWrapSink(int param1Int, Sink<E_OUT> param1Sink) { throw new UnsupportedOperationException(); }
    
    public void forEach(Consumer<? super E_OUT> param1Consumer) {
      if (!isParallel()) {
        sourceStageSpliterator().forEachRemaining(param1Consumer);
      } else {
        super.forEach(param1Consumer);
      } 
    }
    
    public void forEachOrdered(Consumer<? super E_OUT> param1Consumer) {
      if (!isParallel()) {
        sourceStageSpliterator().forEachRemaining(param1Consumer);
      } else {
        super.forEachOrdered(param1Consumer);
      } 
    }
  }
  
  static abstract class StatefulOp<E_IN, E_OUT> extends ReferencePipeline<E_IN, E_OUT> {
    StatefulOp(AbstractPipeline<?, E_IN, ?> param1AbstractPipeline, StreamShape param1StreamShape, int param1Int) {
      super(param1AbstractPipeline, param1Int);
      assert param1AbstractPipeline.getOutputShape() == param1StreamShape;
    }
    
    final boolean opIsStateful() { return true; }
    
    abstract <P_IN> Node<E_OUT> opEvaluateParallel(PipelineHelper<E_OUT> param1PipelineHelper, Spliterator<P_IN> param1Spliterator, IntFunction<E_OUT[]> param1IntFunction);
  }
  
  static abstract class StatelessOp<E_IN, E_OUT> extends ReferencePipeline<E_IN, E_OUT> {
    StatelessOp(AbstractPipeline<?, E_IN, ?> param1AbstractPipeline, StreamShape param1StreamShape, int param1Int) {
      super(param1AbstractPipeline, param1Int);
      assert param1AbstractPipeline.getOutputShape() == param1StreamShape;
    }
    
    final boolean opIsStateful() { return false; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\stream\ReferencePipeline.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */