package java.util.stream;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Spliterator;
import java.util.concurrent.CountedCompleter;
import java.util.function.Predicate;
import java.util.function.Supplier;

final class FindOps {
  public static <T> TerminalOp<T, Optional<T>> makeRef(boolean paramBoolean) { return new FindOp(paramBoolean, StreamShape.REFERENCE, Optional.empty(), Optional::isPresent, OfRef::new); }
  
  public static TerminalOp<Integer, OptionalInt> makeInt(boolean paramBoolean) { return new FindOp(paramBoolean, StreamShape.INT_VALUE, OptionalInt.empty(), OptionalInt::isPresent, OfInt::new); }
  
  public static TerminalOp<Long, OptionalLong> makeLong(boolean paramBoolean) { return new FindOp(paramBoolean, StreamShape.LONG_VALUE, OptionalLong.empty(), OptionalLong::isPresent, OfLong::new); }
  
  public static TerminalOp<Double, OptionalDouble> makeDouble(boolean paramBoolean) { return new FindOp(paramBoolean, StreamShape.DOUBLE_VALUE, OptionalDouble.empty(), OptionalDouble::isPresent, OfDouble::new); }
  
  private static final class FindOp<T, O> extends Object implements TerminalOp<T, O> {
    private final StreamShape shape;
    
    final boolean mustFindFirst;
    
    final O emptyValue;
    
    final Predicate<O> presentPredicate;
    
    final Supplier<TerminalSink<T, O>> sinkSupplier;
    
    FindOp(boolean param1Boolean, StreamShape param1StreamShape, O param1O, Predicate<O> param1Predicate, Supplier<TerminalSink<T, O>> param1Supplier) {
      this.mustFindFirst = param1Boolean;
      this.shape = param1StreamShape;
      this.emptyValue = param1O;
      this.presentPredicate = param1Predicate;
      this.sinkSupplier = param1Supplier;
    }
    
    public int getOpFlags() { return StreamOpFlag.IS_SHORT_CIRCUIT | (this.mustFindFirst ? 0 : StreamOpFlag.NOT_ORDERED); }
    
    public StreamShape inputShape() { return this.shape; }
    
    public <S> O evaluateSequential(PipelineHelper<T> param1PipelineHelper, Spliterator<S> param1Spliterator) {
      Object object = ((TerminalSink)param1PipelineHelper.wrapAndCopyInto((Sink)this.sinkSupplier.get(), param1Spliterator)).get();
      return (O)((object != null) ? object : this.emptyValue);
    }
    
    public <P_IN> O evaluateParallel(PipelineHelper<T> param1PipelineHelper, Spliterator<P_IN> param1Spliterator) { return (O)(new FindOps.FindTask(this, param1PipelineHelper, param1Spliterator)).invoke(); }
  }
  
  private static abstract class FindSink<T, O> extends Object implements TerminalSink<T, O> {
    boolean hasValue;
    
    T value;
    
    public void accept(T param1T) {
      if (!this.hasValue) {
        this.hasValue = true;
        this.value = param1T;
      } 
    }
    
    public boolean cancellationRequested() { return this.hasValue; }
    
    static final class OfDouble extends FindSink<Double, OptionalDouble> implements Sink.OfDouble {
      public void accept(double param2Double) { accept(Double.valueOf(param2Double)); }
      
      public OptionalDouble get() { return this.hasValue ? OptionalDouble.of(((Double)this.value).doubleValue()) : null; }
    }
    
    static final class OfInt extends FindSink<Integer, OptionalInt> implements Sink.OfInt {
      public void accept(int param2Int) { accept(Integer.valueOf(param2Int)); }
      
      public OptionalInt get() { return this.hasValue ? OptionalInt.of(((Integer)this.value).intValue()) : null; }
    }
    
    static final class OfLong extends FindSink<Long, OptionalLong> implements Sink.OfLong {
      public void accept(long param2Long) { accept(Long.valueOf(param2Long)); }
      
      public OptionalLong get() { return this.hasValue ? OptionalLong.of(((Long)this.value).longValue()) : null; }
    }
    
    static final class OfRef<T> extends FindSink<T, Optional<T>> {
      public Optional<T> get() { return this.hasValue ? Optional.of(this.value) : null; }
    }
  }
  
  static final class OfDouble extends FindSink<Double, OptionalDouble> implements Sink.OfDouble {
    public void accept(double param1Double) { accept(Double.valueOf(param1Double)); }
    
    public OptionalDouble get() { return this.hasValue ? OptionalDouble.of(((Double)this.value).doubleValue()) : null; }
  }
  
  static final class OfInt extends FindSink<Integer, OptionalInt> implements Sink.OfInt {
    public void accept(int param1Int) { accept(Integer.valueOf(param1Int)); }
    
    public OptionalInt get() { return this.hasValue ? OptionalInt.of(((Integer)this.value).intValue()) : null; }
  }
  
  static final class OfLong extends FindSink<Long, OptionalLong> implements Sink.OfLong {
    public void accept(long param1Long) { accept(Long.valueOf(param1Long)); }
    
    public OptionalLong get() { return this.hasValue ? OptionalLong.of(((Long)this.value).longValue()) : null; }
  }
  
  static final class OfRef<T> extends FindSink<T, Optional<T>> {
    public Optional<T> get() { return this.hasValue ? Optional.of(this.value) : null; }
  }
  
  private static final class FindTask<P_IN, P_OUT, O> extends AbstractShortCircuitTask<P_IN, P_OUT, O, FindTask<P_IN, P_OUT, O>> {
    private final FindOps.FindOp<P_OUT, O> op;
    
    FindTask(FindOps.FindOp<P_OUT, O> param1FindOp, PipelineHelper<P_OUT> param1PipelineHelper, Spliterator<P_IN> param1Spliterator) {
      super(param1PipelineHelper, param1Spliterator);
      this.op = param1FindOp;
    }
    
    FindTask(FindTask<P_IN, P_OUT, O> param1FindTask, Spliterator<P_IN> param1Spliterator) {
      super(param1FindTask, param1Spliterator);
      this.op = param1FindTask.op;
    }
    
    protected FindTask<P_IN, P_OUT, O> makeChild(Spliterator<P_IN> param1Spliterator) { return new FindTask(this, param1Spliterator); }
    
    protected O getEmptyResult() { return (O)this.op.emptyValue; }
    
    private void foundResult(O param1O) {
      if (isLeftmostNode()) {
        shortCircuit(param1O);
      } else {
        cancelLaterNodes();
      } 
    }
    
    protected O doLeaf() {
      Object object = ((TerminalSink)this.helper.wrapAndCopyInto((Sink)this.op.sinkSupplier.get(), this.spliterator)).get();
      if (!this.op.mustFindFirst) {
        if (object != null)
          shortCircuit(object); 
        return null;
      } 
      if (object != null) {
        foundResult(object);
        return (O)object;
      } 
      return null;
    }
    
    public void onCompletion(CountedCompleter<?> param1CountedCompleter) {
      if (this.op.mustFindFirst) {
        FindTask findTask1 = (FindTask)this.leftChild;
        FindTask findTask2 = null;
        while (findTask1 != findTask2) {
          Object object = findTask1.getLocalResult();
          if (object != null && this.op.presentPredicate.test(object)) {
            setLocalResult(object);
            foundResult(object);
            break;
          } 
          findTask2 = findTask1;
          findTask1 = (FindTask)this.rightChild;
        } 
      } 
      super.onCompletion(param1CountedCompleter);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\stream\FindOps.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */