package java.util.stream;

import java.util.Spliterator;
import java.util.concurrent.CountedCompleter;
import java.util.function.IntFunction;

final class SliceOps {
  private static long calcSize(long paramLong1, long paramLong2, long paramLong3) { return (paramLong1 >= 0L) ? Math.max(-1L, Math.min(paramLong1 - paramLong2, paramLong3)) : -1L; }
  
  private static long calcSliceFence(long paramLong1, long paramLong2) {
    long l = (paramLong2 >= 0L) ? (paramLong1 + paramLong2) : Float.MAX_VALUE;
    return (l >= 0L) ? l : Float.MAX_VALUE;
  }
  
  private static <P_IN> Spliterator<P_IN> sliceSpliterator(StreamShape paramStreamShape, Spliterator<P_IN> paramSpliterator, long paramLong1, long paramLong2) {
    assert paramSpliterator.hasCharacteristics(16384);
    long l = calcSliceFence(paramLong1, paramLong2);
    switch (paramStreamShape) {
      case REFERENCE:
        return new StreamSpliterators.SliceSpliterator.OfRef(paramSpliterator, paramLong1, l);
      case INT_VALUE:
        return new StreamSpliterators.SliceSpliterator.OfInt((Spliterator.OfInt)paramSpliterator, paramLong1, l);
      case LONG_VALUE:
        return new StreamSpliterators.SliceSpliterator.OfLong((Spliterator.OfLong)paramSpliterator, paramLong1, l);
      case DOUBLE_VALUE:
        return new StreamSpliterators.SliceSpliterator.OfDouble((Spliterator.OfDouble)paramSpliterator, paramLong1, l);
    } 
    throw new IllegalStateException("Unknown shape " + paramStreamShape);
  }
  
  private static <T> IntFunction<T[]> castingArray() { return paramInt -> (Object[])new Object[paramInt]; }
  
  public static <T> Stream<T> makeRef(AbstractPipeline<?, T, ?> paramAbstractPipeline, final long skip, final long limit) {
    if (paramLong1 < 0L)
      throw new IllegalArgumentException("Skip must be non-negative: " + paramLong1); 
    return new ReferencePipeline.StatefulOp<T, T>(paramAbstractPipeline, StreamShape.REFERENCE, flags(paramLong2)) {
        Spliterator<T> unorderedSkipLimitSpliterator(Spliterator<T> param1Spliterator, long param1Long1, long param1Long2, long param1Long3) {
          if (param1Long1 <= param1Long3) {
            param1Long2 = (param1Long2 >= 0L) ? Math.min(param1Long2, param1Long3 - param1Long1) : (param1Long3 - param1Long1);
            param1Long1 = 0L;
          } 
          return new StreamSpliterators.UnorderedSliceSpliterator.OfRef(param1Spliterator, param1Long1, param1Long2);
        }
        
        <P_IN> Spliterator<T> opEvaluateParallelLazy(PipelineHelper<T> param1PipelineHelper, Spliterator<P_IN> param1Spliterator) {
          long l = param1PipelineHelper.exactOutputSizeIfKnown(param1Spliterator);
          return (l > 0L && param1Spliterator.hasCharacteristics(16384)) ? new StreamSpliterators.SliceSpliterator.OfRef(param1PipelineHelper.wrapSpliterator(param1Spliterator), skip, SliceOps.calcSliceFence(skip, limit)) : (!StreamOpFlag.ORDERED.isKnown(param1PipelineHelper.getStreamAndOpFlags()) ? unorderedSkipLimitSpliterator(param1PipelineHelper.wrapSpliterator(param1Spliterator), skip, limit, l) : ((Node)(new SliceOps.SliceTask(this, param1PipelineHelper, param1Spliterator, SliceOps.castingArray(), skip, limit)).invoke()).spliterator());
        }
        
        <P_IN> Node<T> opEvaluateParallel(PipelineHelper<T> param1PipelineHelper, Spliterator<P_IN> param1Spliterator, IntFunction<T[]> param1IntFunction) {
          long l = param1PipelineHelper.exactOutputSizeIfKnown(param1Spliterator);
          if (l > 0L && param1Spliterator.hasCharacteristics(16384)) {
            Spliterator spliterator = SliceOps.sliceSpliterator(param1PipelineHelper.getSourceShape(), param1Spliterator, skip, limit);
            return Nodes.collect(param1PipelineHelper, spliterator, true, param1IntFunction);
          } 
          if (!StreamOpFlag.ORDERED.isKnown(param1PipelineHelper.getStreamAndOpFlags())) {
            Spliterator spliterator = unorderedSkipLimitSpliterator(param1PipelineHelper.wrapSpliterator(param1Spliterator), skip, limit, l);
            return Nodes.collect(this, spliterator, true, param1IntFunction);
          } 
          return (Node)(new SliceOps.SliceTask(this, param1PipelineHelper, param1Spliterator, param1IntFunction, skip, limit)).invoke();
        }
        
        Sink<T> opWrapSink(int param1Int, Sink<T> param1Sink) { return new Sink.ChainedReference<T, T>(param1Sink) {
              long n = SliceOps.null.this.val$skip;
              
              long m = (SliceOps.null.this.val$limit >= 0L) ? SliceOps.null.this.val$limit : Float.MAX_VALUE;
              
              public void begin(long param2Long) { this.downstream.begin(SliceOps.calcSize(param2Long, skip, this.m)); }
              
              public void accept(T param2T) {
                if (this.n == 0L) {
                  if (this.m > 0L) {
                    this.m--;
                    this.downstream.accept(param2T);
                  } 
                } else {
                  this.n--;
                } 
              }
              
              public boolean cancellationRequested() { return (this.m == 0L || this.downstream.cancellationRequested()); }
            }; }
      };
  }
  
  public static IntStream makeInt(AbstractPipeline<?, Integer, ?> paramAbstractPipeline, final long skip, final long limit) {
    if (paramLong1 < 0L)
      throw new IllegalArgumentException("Skip must be non-negative: " + paramLong1); 
    return new IntPipeline.StatefulOp<Integer>(paramAbstractPipeline, StreamShape.INT_VALUE, flags(paramLong2)) {
        Spliterator.OfInt unorderedSkipLimitSpliterator(Spliterator.OfInt param1OfInt, long param1Long1, long param1Long2, long param1Long3) {
          if (param1Long1 <= param1Long3) {
            param1Long2 = (param1Long2 >= 0L) ? Math.min(param1Long2, param1Long3 - param1Long1) : (param1Long3 - param1Long1);
            param1Long1 = 0L;
          } 
          return new StreamSpliterators.UnorderedSliceSpliterator.OfInt(param1OfInt, param1Long1, param1Long2);
        }
        
        <P_IN> Spliterator<Integer> opEvaluateParallelLazy(PipelineHelper<Integer> param1PipelineHelper, Spliterator<P_IN> param1Spliterator) {
          long l = param1PipelineHelper.exactOutputSizeIfKnown(param1Spliterator);
          return (l > 0L && param1Spliterator.hasCharacteristics(16384)) ? new StreamSpliterators.SliceSpliterator.OfInt((Spliterator.OfInt)param1PipelineHelper.wrapSpliterator(param1Spliterator), skip, SliceOps.calcSliceFence(skip, limit)) : (!StreamOpFlag.ORDERED.isKnown(param1PipelineHelper.getStreamAndOpFlags()) ? unorderedSkipLimitSpliterator((Spliterator.OfInt)param1PipelineHelper.wrapSpliterator(param1Spliterator), skip, limit, l) : ((Node)(new SliceOps.SliceTask(this, param1PipelineHelper, param1Spliterator, param1Int -> new Integer[param1Int], skip, limit)).invoke()).spliterator());
        }
        
        <P_IN> Node<Integer> opEvaluateParallel(PipelineHelper<Integer> param1PipelineHelper, Spliterator<P_IN> param1Spliterator, IntFunction<Integer[]> param1IntFunction) {
          long l = param1PipelineHelper.exactOutputSizeIfKnown(param1Spliterator);
          if (l > 0L && param1Spliterator.hasCharacteristics(16384)) {
            Spliterator spliterator = SliceOps.sliceSpliterator(param1PipelineHelper.getSourceShape(), param1Spliterator, skip, limit);
            return Nodes.collectInt(param1PipelineHelper, spliterator, true);
          } 
          if (!StreamOpFlag.ORDERED.isKnown(param1PipelineHelper.getStreamAndOpFlags())) {
            Spliterator.OfInt ofInt = unorderedSkipLimitSpliterator((Spliterator.OfInt)param1PipelineHelper.wrapSpliterator(param1Spliterator), skip, limit, l);
            return Nodes.collectInt(this, ofInt, true);
          } 
          return (Node)(new SliceOps.SliceTask(this, param1PipelineHelper, param1Spliterator, param1IntFunction, skip, limit)).invoke();
        }
        
        Sink<Integer> opWrapSink(int param1Int, Sink<Integer> param1Sink) { return new Sink.ChainedInt<Integer>(param1Sink) {
              long n = SliceOps.null.this.val$skip;
              
              long m = (SliceOps.null.this.val$limit >= 0L) ? SliceOps.null.this.val$limit : Float.MAX_VALUE;
              
              public void begin(long param2Long) { this.downstream.begin(SliceOps.calcSize(param2Long, skip, this.m)); }
              
              public void accept(int param2Int) {
                if (this.n == 0L) {
                  if (this.m > 0L) {
                    this.m--;
                    this.downstream.accept(param2Int);
                  } 
                } else {
                  this.n--;
                } 
              }
              
              public boolean cancellationRequested() { return (this.m == 0L || this.downstream.cancellationRequested()); }
            }; }
      };
  }
  
  public static LongStream makeLong(AbstractPipeline<?, Long, ?> paramAbstractPipeline, final long skip, final long limit) {
    if (paramLong1 < 0L)
      throw new IllegalArgumentException("Skip must be non-negative: " + paramLong1); 
    return new LongPipeline.StatefulOp<Long>(paramAbstractPipeline, StreamShape.LONG_VALUE, flags(paramLong2)) {
        Spliterator.OfLong unorderedSkipLimitSpliterator(Spliterator.OfLong param1OfLong, long param1Long1, long param1Long2, long param1Long3) {
          if (param1Long1 <= param1Long3) {
            param1Long2 = (param1Long2 >= 0L) ? Math.min(param1Long2, param1Long3 - param1Long1) : (param1Long3 - param1Long1);
            param1Long1 = 0L;
          } 
          return new StreamSpliterators.UnorderedSliceSpliterator.OfLong(param1OfLong, param1Long1, param1Long2);
        }
        
        <P_IN> Spliterator<Long> opEvaluateParallelLazy(PipelineHelper<Long> param1PipelineHelper, Spliterator<P_IN> param1Spliterator) {
          long l = param1PipelineHelper.exactOutputSizeIfKnown(param1Spliterator);
          return (l > 0L && param1Spliterator.hasCharacteristics(16384)) ? new StreamSpliterators.SliceSpliterator.OfLong((Spliterator.OfLong)param1PipelineHelper.wrapSpliterator(param1Spliterator), skip, SliceOps.calcSliceFence(skip, limit)) : (!StreamOpFlag.ORDERED.isKnown(param1PipelineHelper.getStreamAndOpFlags()) ? unorderedSkipLimitSpliterator((Spliterator.OfLong)param1PipelineHelper.wrapSpliterator(param1Spliterator), skip, limit, l) : ((Node)(new SliceOps.SliceTask(this, param1PipelineHelper, param1Spliterator, param1Int -> new Long[param1Int], skip, limit)).invoke()).spliterator());
        }
        
        <P_IN> Node<Long> opEvaluateParallel(PipelineHelper<Long> param1PipelineHelper, Spliterator<P_IN> param1Spliterator, IntFunction<Long[]> param1IntFunction) {
          long l = param1PipelineHelper.exactOutputSizeIfKnown(param1Spliterator);
          if (l > 0L && param1Spliterator.hasCharacteristics(16384)) {
            Spliterator spliterator = SliceOps.sliceSpliterator(param1PipelineHelper.getSourceShape(), param1Spliterator, skip, limit);
            return Nodes.collectLong(param1PipelineHelper, spliterator, true);
          } 
          if (!StreamOpFlag.ORDERED.isKnown(param1PipelineHelper.getStreamAndOpFlags())) {
            Spliterator.OfLong ofLong = unorderedSkipLimitSpliterator((Spliterator.OfLong)param1PipelineHelper.wrapSpliterator(param1Spliterator), skip, limit, l);
            return Nodes.collectLong(this, ofLong, true);
          } 
          return (Node)(new SliceOps.SliceTask(this, param1PipelineHelper, param1Spliterator, param1IntFunction, skip, limit)).invoke();
        }
        
        Sink<Long> opWrapSink(int param1Int, Sink<Long> param1Sink) { return new Sink.ChainedLong<Long>(param1Sink) {
              long n = SliceOps.null.this.val$skip;
              
              long m = (SliceOps.null.this.val$limit >= 0L) ? SliceOps.null.this.val$limit : Float.MAX_VALUE;
              
              public void begin(long param2Long) { this.downstream.begin(SliceOps.calcSize(param2Long, skip, this.m)); }
              
              public void accept(long param2Long) {
                if (this.n == 0L) {
                  if (this.m > 0L) {
                    this.m--;
                    this.downstream.accept(param2Long);
                  } 
                } else {
                  this.n--;
                } 
              }
              
              public boolean cancellationRequested() { return (this.m == 0L || this.downstream.cancellationRequested()); }
            }; }
      };
  }
  
  public static DoubleStream makeDouble(AbstractPipeline<?, Double, ?> paramAbstractPipeline, final long skip, final long limit) {
    if (paramLong1 < 0L)
      throw new IllegalArgumentException("Skip must be non-negative: " + paramLong1); 
    return new DoublePipeline.StatefulOp<Double>(paramAbstractPipeline, StreamShape.DOUBLE_VALUE, flags(paramLong2)) {
        Spliterator.OfDouble unorderedSkipLimitSpliterator(Spliterator.OfDouble param1OfDouble, long param1Long1, long param1Long2, long param1Long3) {
          if (param1Long1 <= param1Long3) {
            param1Long2 = (param1Long2 >= 0L) ? Math.min(param1Long2, param1Long3 - param1Long1) : (param1Long3 - param1Long1);
            param1Long1 = 0L;
          } 
          return new StreamSpliterators.UnorderedSliceSpliterator.OfDouble(param1OfDouble, param1Long1, param1Long2);
        }
        
        <P_IN> Spliterator<Double> opEvaluateParallelLazy(PipelineHelper<Double> param1PipelineHelper, Spliterator<P_IN> param1Spliterator) {
          long l = param1PipelineHelper.exactOutputSizeIfKnown(param1Spliterator);
          return (l > 0L && param1Spliterator.hasCharacteristics(16384)) ? new StreamSpliterators.SliceSpliterator.OfDouble((Spliterator.OfDouble)param1PipelineHelper.wrapSpliterator(param1Spliterator), skip, SliceOps.calcSliceFence(skip, limit)) : (!StreamOpFlag.ORDERED.isKnown(param1PipelineHelper.getStreamAndOpFlags()) ? unorderedSkipLimitSpliterator((Spliterator.OfDouble)param1PipelineHelper.wrapSpliterator(param1Spliterator), skip, limit, l) : ((Node)(new SliceOps.SliceTask(this, param1PipelineHelper, param1Spliterator, param1Int -> new Double[param1Int], skip, limit)).invoke()).spliterator());
        }
        
        <P_IN> Node<Double> opEvaluateParallel(PipelineHelper<Double> param1PipelineHelper, Spliterator<P_IN> param1Spliterator, IntFunction<Double[]> param1IntFunction) {
          long l = param1PipelineHelper.exactOutputSizeIfKnown(param1Spliterator);
          if (l > 0L && param1Spliterator.hasCharacteristics(16384)) {
            Spliterator spliterator = SliceOps.sliceSpliterator(param1PipelineHelper.getSourceShape(), param1Spliterator, skip, limit);
            return Nodes.collectDouble(param1PipelineHelper, spliterator, true);
          } 
          if (!StreamOpFlag.ORDERED.isKnown(param1PipelineHelper.getStreamAndOpFlags())) {
            Spliterator.OfDouble ofDouble = unorderedSkipLimitSpliterator((Spliterator.OfDouble)param1PipelineHelper.wrapSpliterator(param1Spliterator), skip, limit, l);
            return Nodes.collectDouble(this, ofDouble, true);
          } 
          return (Node)(new SliceOps.SliceTask(this, param1PipelineHelper, param1Spliterator, param1IntFunction, skip, limit)).invoke();
        }
        
        Sink<Double> opWrapSink(int param1Int, Sink<Double> param1Sink) { return new Sink.ChainedDouble<Double>(param1Sink) {
              long n = SliceOps.null.this.val$skip;
              
              long m = (SliceOps.null.this.val$limit >= 0L) ? SliceOps.null.this.val$limit : Float.MAX_VALUE;
              
              public void begin(long param2Long) { this.downstream.begin(SliceOps.calcSize(param2Long, skip, this.m)); }
              
              public void accept(double param2Double) {
                if (this.n == 0L) {
                  if (this.m > 0L) {
                    this.m--;
                    this.downstream.accept(param2Double);
                  } 
                } else {
                  this.n--;
                } 
              }
              
              public boolean cancellationRequested() { return (this.m == 0L || this.downstream.cancellationRequested()); }
            }; }
      };
  }
  
  private static int flags(long paramLong) { return StreamOpFlag.NOT_SIZED | ((paramLong != -1L) ? StreamOpFlag.IS_SHORT_CIRCUIT : 0); }
  
  private static final class SliceTask<P_IN, P_OUT> extends AbstractShortCircuitTask<P_IN, P_OUT, Node<P_OUT>, SliceTask<P_IN, P_OUT>> {
    private final AbstractPipeline<P_OUT, P_OUT, ?> op;
    
    private final IntFunction<P_OUT[]> generator;
    
    private final long targetOffset;
    
    private final long targetSize;
    
    private long thisNodeSize;
    
    SliceTask(AbstractPipeline<P_OUT, P_OUT, ?> param1AbstractPipeline, PipelineHelper<P_OUT> param1PipelineHelper, Spliterator<P_IN> param1Spliterator, IntFunction<P_OUT[]> param1IntFunction, long param1Long1, long param1Long2) {
      super(param1PipelineHelper, param1Spliterator);
      this.op = param1AbstractPipeline;
      this.generator = param1IntFunction;
      this.targetOffset = param1Long1;
      this.targetSize = param1Long2;
    }
    
    SliceTask(SliceTask<P_IN, P_OUT> param1SliceTask, Spliterator<P_IN> param1Spliterator) {
      super(param1SliceTask, param1Spliterator);
      this.op = param1SliceTask.op;
      this.generator = param1SliceTask.generator;
      this.targetOffset = param1SliceTask.targetOffset;
      this.targetSize = param1SliceTask.targetSize;
    }
    
    protected SliceTask<P_IN, P_OUT> makeChild(Spliterator<P_IN> param1Spliterator) { return new SliceTask(this, param1Spliterator); }
    
    protected final Node<P_OUT> getEmptyResult() { return Nodes.emptyNode(this.op.getOutputShape()); }
    
    protected final Node<P_OUT> doLeaf() {
      if (isRoot()) {
        long l = StreamOpFlag.SIZED.isPreserved(this.op.sourceOrOpFlags) ? this.op.exactOutputSizeIfKnown(this.spliterator) : -1L;
        Node.Builder builder = this.op.makeNodeBuilder(l, this.generator);
        Sink sink = this.op.opWrapSink(this.helper.getStreamAndOpFlags(), builder);
        this.helper.copyIntoWithCancel(this.helper.wrapSink(sink), this.spliterator);
        return builder.build();
      } 
      Node node = ((Node.Builder)this.helper.wrapAndCopyInto(this.helper.makeNodeBuilder(-1L, this.generator), this.spliterator)).build();
      this.thisNodeSize = node.count();
      this.completed = true;
      this.spliterator = null;
      return node;
    }
    
    public final void onCompletion(CountedCompleter<?> param1CountedCompleter) {
      if (!isLeaf()) {
        Node node;
        ((SliceTask)this.leftChild).thisNodeSize += ((SliceTask)this.rightChild).thisNodeSize;
        if (this.canceled) {
          this.thisNodeSize = 0L;
          node = getEmptyResult();
        } else if (this.thisNodeSize == 0L) {
          node = getEmptyResult();
        } else if (((SliceTask)this.leftChild).thisNodeSize == 0L) {
          node = (Node)((SliceTask)this.rightChild).getLocalResult();
        } else {
          node = Nodes.conc(this.op.getOutputShape(), (Node)((SliceTask)this.leftChild).getLocalResult(), (Node)((SliceTask)this.rightChild).getLocalResult());
        } 
        setLocalResult(isRoot() ? doTruncate(node) : node);
        this.completed = true;
      } 
      if (this.targetSize >= 0L && !isRoot() && isLeftCompleted(this.targetOffset + this.targetSize))
        cancelLaterNodes(); 
      super.onCompletion(param1CountedCompleter);
    }
    
    protected void cancel() {
      super.cancel();
      if (this.completed)
        setLocalResult(getEmptyResult()); 
    }
    
    private Node<P_OUT> doTruncate(Node<P_OUT> param1Node) {
      long l = (this.targetSize >= 0L) ? Math.min(param1Node.count(), this.targetOffset + this.targetSize) : this.thisNodeSize;
      return param1Node.truncate(this.targetOffset, l, this.generator);
    }
    
    private boolean isLeftCompleted(long param1Long) {
      long l = this.completed ? this.thisNodeSize : completedSize(param1Long);
      if (l >= param1Long)
        return true; 
      SliceTask sliceTask1 = (SliceTask)getParent();
      SliceTask sliceTask2 = this;
      while (sliceTask1 != null) {
        if (sliceTask2 == sliceTask1.rightChild) {
          SliceTask sliceTask = (SliceTask)sliceTask1.leftChild;
          if (sliceTask != null) {
            l += sliceTask.completedSize(param1Long);
            if (l >= param1Long)
              return true; 
          } 
        } 
        sliceTask2 = sliceTask1;
        sliceTask1 = (SliceTask)sliceTask1.getParent();
      } 
      return (l >= param1Long);
    }
    
    private long completedSize(long param1Long) {
      if (this.completed)
        return this.thisNodeSize; 
      SliceTask sliceTask1 = (SliceTask)this.leftChild;
      SliceTask sliceTask2 = (SliceTask)this.rightChild;
      if (sliceTask1 == null || sliceTask2 == null)
        return this.thisNodeSize; 
      long l = sliceTask1.completedSize(param1Long);
      return (l >= param1Long) ? l : (l + sliceTask2.completedSize(param1Long));
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\stream\SliceOps.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */