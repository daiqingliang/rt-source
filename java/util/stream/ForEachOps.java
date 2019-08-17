package java.util.stream;

import java.util.Objects;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountedCompleter;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.LongConsumer;

final class ForEachOps {
  public static <T> TerminalOp<T, Void> makeRef(Consumer<? super T> paramConsumer, boolean paramBoolean) {
    Objects.requireNonNull(paramConsumer);
    return new ForEachOp.OfRef(paramConsumer, paramBoolean);
  }
  
  public static TerminalOp<Integer, Void> makeInt(IntConsumer paramIntConsumer, boolean paramBoolean) {
    Objects.requireNonNull(paramIntConsumer);
    return new ForEachOp.OfInt(paramIntConsumer, paramBoolean);
  }
  
  public static TerminalOp<Long, Void> makeLong(LongConsumer paramLongConsumer, boolean paramBoolean) {
    Objects.requireNonNull(paramLongConsumer);
    return new ForEachOp.OfLong(paramLongConsumer, paramBoolean);
  }
  
  public static TerminalOp<Double, Void> makeDouble(DoubleConsumer paramDoubleConsumer, boolean paramBoolean) {
    Objects.requireNonNull(paramDoubleConsumer);
    return new ForEachOp.OfDouble(paramDoubleConsumer, paramBoolean);
  }
  
  static abstract class ForEachOp<T> extends Object implements TerminalOp<T, Void>, TerminalSink<T, Void> {
    private final boolean ordered;
    
    protected ForEachOp(boolean param1Boolean) { this.ordered = param1Boolean; }
    
    public int getOpFlags() { return this.ordered ? 0 : StreamOpFlag.NOT_ORDERED; }
    
    public <S> Void evaluateSequential(PipelineHelper<T> param1PipelineHelper, Spliterator<S> param1Spliterator) { return ((ForEachOp)param1PipelineHelper.wrapAndCopyInto(this, param1Spliterator)).get(); }
    
    public <S> Void evaluateParallel(PipelineHelper<T> param1PipelineHelper, Spliterator<S> param1Spliterator) {
      if (this.ordered) {
        (new ForEachOps.ForEachOrderedTask(param1PipelineHelper, param1Spliterator, this)).invoke();
      } else {
        (new ForEachOps.ForEachTask(param1PipelineHelper, param1Spliterator, param1PipelineHelper.wrapSink(this))).invoke();
      } 
      return null;
    }
    
    public Void get() { return null; }
    
    static final class OfDouble extends ForEachOp<Double> implements Sink.OfDouble {
      final DoubleConsumer consumer;
      
      OfDouble(DoubleConsumer param2DoubleConsumer, boolean param2Boolean) {
        super(param2Boolean);
        this.consumer = param2DoubleConsumer;
      }
      
      public StreamShape inputShape() { return StreamShape.DOUBLE_VALUE; }
      
      public void accept(double param2Double) { this.consumer.accept(param2Double); }
    }
    
    static final class OfInt extends ForEachOp<Integer> implements Sink.OfInt {
      final IntConsumer consumer;
      
      OfInt(IntConsumer param2IntConsumer, boolean param2Boolean) {
        super(param2Boolean);
        this.consumer = param2IntConsumer;
      }
      
      public StreamShape inputShape() { return StreamShape.INT_VALUE; }
      
      public void accept(int param2Int) { this.consumer.accept(param2Int); }
    }
    
    static final class OfLong extends ForEachOp<Long> implements Sink.OfLong {
      final LongConsumer consumer;
      
      OfLong(LongConsumer param2LongConsumer, boolean param2Boolean) {
        super(param2Boolean);
        this.consumer = param2LongConsumer;
      }
      
      public StreamShape inputShape() { return StreamShape.LONG_VALUE; }
      
      public void accept(long param2Long) { this.consumer.accept(param2Long); }
    }
    
    static final class OfRef<T> extends ForEachOp<T> {
      final Consumer<? super T> consumer;
      
      OfRef(Consumer<? super T> param2Consumer, boolean param2Boolean) {
        super(param2Boolean);
        this.consumer = param2Consumer;
      }
      
      public void accept(T param2T) { this.consumer.accept(param2T); }
    }
  }
  
  static final class OfDouble extends ForEachOp<Double> implements Sink.OfDouble {
    final DoubleConsumer consumer;
    
    OfDouble(DoubleConsumer param1DoubleConsumer, boolean param1Boolean) {
      super(param1Boolean);
      this.consumer = param1DoubleConsumer;
    }
    
    public StreamShape inputShape() { return StreamShape.DOUBLE_VALUE; }
    
    public void accept(double param1Double) { this.consumer.accept(param1Double); }
  }
  
  static final class OfInt extends ForEachOp<Integer> implements Sink.OfInt {
    final IntConsumer consumer;
    
    OfInt(IntConsumer param1IntConsumer, boolean param1Boolean) {
      super(param1Boolean);
      this.consumer = param1IntConsumer;
    }
    
    public StreamShape inputShape() { return StreamShape.INT_VALUE; }
    
    public void accept(int param1Int) { this.consumer.accept(param1Int); }
  }
  
  static final class OfLong extends ForEachOp<Long> implements Sink.OfLong {
    final LongConsumer consumer;
    
    OfLong(LongConsumer param1LongConsumer, boolean param1Boolean) {
      super(param1Boolean);
      this.consumer = param1LongConsumer;
    }
    
    public StreamShape inputShape() { return StreamShape.LONG_VALUE; }
    
    public void accept(long param1Long) { this.consumer.accept(param1Long); }
  }
  
  static final class OfRef<T> extends ForEachOp<T> {
    final Consumer<? super T> consumer;
    
    OfRef(Consumer<? super T> param1Consumer, boolean param1Boolean) {
      super(param1Boolean);
      this.consumer = param1Consumer;
    }
    
    public void accept(T param1T) { this.consumer.accept(param1T); }
  }
  
  static final class ForEachOrderedTask<S, T> extends CountedCompleter<Void> {
    private final PipelineHelper<T> helper;
    
    private Spliterator<S> spliterator;
    
    private final long targetSize;
    
    private final ConcurrentHashMap<ForEachOrderedTask<S, T>, ForEachOrderedTask<S, T>> completionMap;
    
    private final Sink<T> action;
    
    private final ForEachOrderedTask<S, T> leftPredecessor;
    
    private Node<T> node;
    
    protected ForEachOrderedTask(PipelineHelper<T> param1PipelineHelper, Spliterator<S> param1Spliterator, Sink<T> param1Sink) {
      super(null);
      this.helper = param1PipelineHelper;
      this.spliterator = param1Spliterator;
      this.targetSize = AbstractTask.suggestTargetSize(param1Spliterator.estimateSize());
      this.completionMap = new ConcurrentHashMap(Math.max(16, AbstractTask.LEAF_TARGET << 1));
      this.action = param1Sink;
      this.leftPredecessor = null;
    }
    
    ForEachOrderedTask(ForEachOrderedTask<S, T> param1ForEachOrderedTask1, Spliterator<S> param1Spliterator, ForEachOrderedTask<S, T> param1ForEachOrderedTask2) {
      super(param1ForEachOrderedTask1);
      this.helper = param1ForEachOrderedTask1.helper;
      this.spliterator = param1Spliterator;
      this.targetSize = param1ForEachOrderedTask1.targetSize;
      this.completionMap = param1ForEachOrderedTask1.completionMap;
      this.action = param1ForEachOrderedTask1.action;
      this.leftPredecessor = param1ForEachOrderedTask2;
    }
    
    public final void compute() { doCompute(this); }
    
    private static <S, T> void doCompute(ForEachOrderedTask<S, T> param1ForEachOrderedTask) {
      Spliterator spliterator1 = param1ForEachOrderedTask.spliterator;
      long l = param1ForEachOrderedTask.targetSize;
      boolean bool = false;
      Spliterator spliterator2;
      while (spliterator1.estimateSize() > l && (spliterator2 = spliterator1.trySplit()) != null) {
        ForEachOrderedTask forEachOrderedTask3;
        ForEachOrderedTask forEachOrderedTask1 = new ForEachOrderedTask(param1ForEachOrderedTask, spliterator2, param1ForEachOrderedTask.leftPredecessor);
        ForEachOrderedTask forEachOrderedTask2 = new ForEachOrderedTask(param1ForEachOrderedTask, spliterator1, forEachOrderedTask1);
        param1ForEachOrderedTask.addToPendingCount(1);
        forEachOrderedTask2.addToPendingCount(1);
        param1ForEachOrderedTask.completionMap.put(forEachOrderedTask1, forEachOrderedTask2);
        if (param1ForEachOrderedTask.leftPredecessor != null) {
          forEachOrderedTask1.addToPendingCount(1);
          if (param1ForEachOrderedTask.completionMap.replace(param1ForEachOrderedTask.leftPredecessor, param1ForEachOrderedTask, forEachOrderedTask1)) {
            param1ForEachOrderedTask.addToPendingCount(-1);
          } else {
            forEachOrderedTask1.addToPendingCount(-1);
          } 
        } 
        if (bool) {
          bool = false;
          spliterator1 = spliterator2;
          param1ForEachOrderedTask = forEachOrderedTask1;
          forEachOrderedTask3 = forEachOrderedTask2;
        } else {
          bool = true;
          param1ForEachOrderedTask = forEachOrderedTask2;
          forEachOrderedTask3 = forEachOrderedTask1;
        } 
        forEachOrderedTask3.fork();
      } 
      if (param1ForEachOrderedTask.getPendingCount() > 0) {
        IntFunction intFunction = param1Int -> (Object[])new Object[param1Int];
        Node.Builder builder = param1ForEachOrderedTask.helper.makeNodeBuilder(param1ForEachOrderedTask.helper.exactOutputSizeIfKnown(spliterator1), intFunction);
        param1ForEachOrderedTask.node = ((Node.Builder)param1ForEachOrderedTask.helper.wrapAndCopyInto(builder, spliterator1)).build();
        param1ForEachOrderedTask.spliterator = null;
      } 
      param1ForEachOrderedTask.tryComplete();
    }
    
    public void onCompletion(CountedCompleter<?> param1CountedCompleter) {
      if (this.node != null) {
        this.node.forEach(this.action);
        this.node = null;
      } else if (this.spliterator != null) {
        this.helper.wrapAndCopyInto(this.action, this.spliterator);
        this.spliterator = null;
      } 
      ForEachOrderedTask forEachOrderedTask = (ForEachOrderedTask)this.completionMap.remove(this);
      if (forEachOrderedTask != null)
        forEachOrderedTask.tryComplete(); 
    }
  }
  
  static final class ForEachTask<S, T> extends CountedCompleter<Void> {
    private Spliterator<S> spliterator;
    
    private final Sink<S> sink;
    
    private final PipelineHelper<T> helper;
    
    private long targetSize;
    
    ForEachTask(PipelineHelper<T> param1PipelineHelper, Spliterator<S> param1Spliterator, Sink<S> param1Sink) {
      super(null);
      this.sink = param1Sink;
      this.helper = param1PipelineHelper;
      this.spliterator = param1Spliterator;
      this.targetSize = 0L;
    }
    
    ForEachTask(ForEachTask<S, T> param1ForEachTask, Spliterator<S> param1Spliterator) {
      super(param1ForEachTask);
      this.spliterator = param1Spliterator;
      this.sink = param1ForEachTask.sink;
      this.targetSize = param1ForEachTask.targetSize;
      this.helper = param1ForEachTask.helper;
    }
    
    public void compute() {
      Spliterator spliterator1 = this.spliterator;
      long l1 = spliterator1.estimateSize();
      long l2;
      if ((l2 = this.targetSize) == 0L)
        this.targetSize = l2 = AbstractTask.suggestTargetSize(l1); 
      boolean bool = StreamOpFlag.SHORT_CIRCUIT.isKnown(this.helper.getStreamAndOpFlags());
      boolean bool1 = false;
      Sink sink1 = this.sink;
      ForEachTask forEachTask = this;
      while (!bool || !sink1.cancellationRequested()) {
        ForEachTask forEachTask2;
        Spliterator spliterator2;
        if (l1 <= l2 || (spliterator2 = spliterator1.trySplit()) == null) {
          forEachTask.helper.copyInto(sink1, spliterator1);
          break;
        } 
        ForEachTask forEachTask1 = new ForEachTask(forEachTask, spliterator2);
        forEachTask.addToPendingCount(1);
        if (bool1) {
          bool1 = false;
          spliterator1 = spliterator2;
          forEachTask2 = forEachTask;
          forEachTask = forEachTask1;
        } else {
          bool1 = true;
          forEachTask2 = forEachTask1;
        } 
        forEachTask2.fork();
        l1 = spliterator1.estimateSize();
      } 
      forEachTask.spliterator = null;
      forEachTask.propagateCompletion();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\stream\ForEachOps.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */