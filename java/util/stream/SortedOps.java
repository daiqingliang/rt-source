package java.util.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.IntFunction;

final class SortedOps {
  static <T> Stream<T> makeRef(AbstractPipeline<?, T, ?> paramAbstractPipeline) { return new OfRef(paramAbstractPipeline); }
  
  static <T> Stream<T> makeRef(AbstractPipeline<?, T, ?> paramAbstractPipeline, Comparator<? super T> paramComparator) { return new OfRef(paramAbstractPipeline, paramComparator); }
  
  static <T> IntStream makeInt(AbstractPipeline<?, Integer, ?> paramAbstractPipeline) { return new OfInt(paramAbstractPipeline); }
  
  static <T> LongStream makeLong(AbstractPipeline<?, Long, ?> paramAbstractPipeline) { return new OfLong(paramAbstractPipeline); }
  
  static <T> DoubleStream makeDouble(AbstractPipeline<?, Double, ?> paramAbstractPipeline) { return new OfDouble(paramAbstractPipeline); }
  
  private static abstract class AbstractDoubleSortingSink extends Sink.ChainedDouble<Double> {
    protected boolean cancellationWasRequested;
    
    AbstractDoubleSortingSink(Sink<? super Double> param1Sink) { super(param1Sink); }
    
    public final boolean cancellationRequested() {
      this.cancellationWasRequested = true;
      return false;
    }
  }
  
  private static abstract class AbstractIntSortingSink extends Sink.ChainedInt<Integer> {
    protected boolean cancellationWasRequested;
    
    AbstractIntSortingSink(Sink<? super Integer> param1Sink) { super(param1Sink); }
    
    public final boolean cancellationRequested() {
      this.cancellationWasRequested = true;
      return false;
    }
  }
  
  private static abstract class AbstractLongSortingSink extends Sink.ChainedLong<Long> {
    protected boolean cancellationWasRequested;
    
    AbstractLongSortingSink(Sink<? super Long> param1Sink) { super(param1Sink); }
    
    public final boolean cancellationRequested() {
      this.cancellationWasRequested = true;
      return false;
    }
  }
  
  private static abstract class AbstractRefSortingSink<T> extends Sink.ChainedReference<T, T> {
    protected final Comparator<? super T> comparator;
    
    protected boolean cancellationWasRequested;
    
    AbstractRefSortingSink(Sink<? super T> param1Sink, Comparator<? super T> param1Comparator) {
      super(param1Sink);
      this.comparator = param1Comparator;
    }
    
    public final boolean cancellationRequested() {
      this.cancellationWasRequested = true;
      return false;
    }
  }
  
  private static final class DoubleSortingSink extends AbstractDoubleSortingSink {
    private SpinedBuffer.OfDouble b;
    
    DoubleSortingSink(Sink<? super Double> param1Sink) { super(param1Sink); }
    
    public void begin(long param1Long) {
      if (param1Long >= 2147483639L)
        throw new IllegalArgumentException("Stream size exceeds max array size"); 
      this.b = (param1Long > 0L) ? new SpinedBuffer.OfDouble((int)param1Long) : new SpinedBuffer.OfDouble();
    }
    
    public void end() {
      double[] arrayOfDouble = (double[])this.b.asPrimitiveArray();
      Arrays.sort(arrayOfDouble);
      this.downstream.begin(arrayOfDouble.length);
      if (!this.cancellationWasRequested) {
        for (double d : arrayOfDouble)
          this.downstream.accept(d); 
      } else {
        for (double d : arrayOfDouble) {
          if (this.downstream.cancellationRequested())
            break; 
          this.downstream.accept(d);
        } 
      } 
      this.downstream.end();
    }
    
    public void accept(double param1Double) { this.b.accept(param1Double); }
  }
  
  private static final class IntSortingSink extends AbstractIntSortingSink {
    private SpinedBuffer.OfInt b;
    
    IntSortingSink(Sink<? super Integer> param1Sink) { super(param1Sink); }
    
    public void begin(long param1Long) {
      if (param1Long >= 2147483639L)
        throw new IllegalArgumentException("Stream size exceeds max array size"); 
      this.b = (param1Long > 0L) ? new SpinedBuffer.OfInt((int)param1Long) : new SpinedBuffer.OfInt();
    }
    
    public void end() {
      int[] arrayOfInt = (int[])this.b.asPrimitiveArray();
      Arrays.sort(arrayOfInt);
      this.downstream.begin(arrayOfInt.length);
      if (!this.cancellationWasRequested) {
        for (int i : arrayOfInt)
          this.downstream.accept(i); 
      } else {
        for (int i : arrayOfInt) {
          if (this.downstream.cancellationRequested())
            break; 
          this.downstream.accept(i);
        } 
      } 
      this.downstream.end();
    }
    
    public void accept(int param1Int) { this.b.accept(param1Int); }
  }
  
  private static final class LongSortingSink extends AbstractLongSortingSink {
    private SpinedBuffer.OfLong b;
    
    LongSortingSink(Sink<? super Long> param1Sink) { super(param1Sink); }
    
    public void begin(long param1Long) {
      if (param1Long >= 2147483639L)
        throw new IllegalArgumentException("Stream size exceeds max array size"); 
      this.b = (param1Long > 0L) ? new SpinedBuffer.OfLong((int)param1Long) : new SpinedBuffer.OfLong();
    }
    
    public void end() {
      long[] arrayOfLong = (long[])this.b.asPrimitiveArray();
      Arrays.sort(arrayOfLong);
      this.downstream.begin(arrayOfLong.length);
      if (!this.cancellationWasRequested) {
        for (long l : arrayOfLong)
          this.downstream.accept(l); 
      } else {
        for (long l : arrayOfLong) {
          if (this.downstream.cancellationRequested())
            break; 
          this.downstream.accept(l);
        } 
      } 
      this.downstream.end();
    }
    
    public void accept(long param1Long) { this.b.accept(param1Long); }
  }
  
  private static final class OfDouble extends DoublePipeline.StatefulOp<Double> {
    OfDouble(AbstractPipeline<?, Double, ?> param1AbstractPipeline) { super(param1AbstractPipeline, StreamShape.DOUBLE_VALUE, StreamOpFlag.IS_ORDERED | StreamOpFlag.IS_SORTED); }
    
    public Sink<Double> opWrapSink(int param1Int, Sink<Double> param1Sink) {
      Objects.requireNonNull(param1Sink);
      return StreamOpFlag.SORTED.isKnown(param1Int) ? param1Sink : (StreamOpFlag.SIZED.isKnown(param1Int) ? new SortedOps.SizedDoubleSortingSink(param1Sink) : new SortedOps.DoubleSortingSink(param1Sink));
    }
    
    public <P_IN> Node<Double> opEvaluateParallel(PipelineHelper<Double> param1PipelineHelper, Spliterator<P_IN> param1Spliterator, IntFunction<Double[]> param1IntFunction) {
      if (StreamOpFlag.SORTED.isKnown(param1PipelineHelper.getStreamAndOpFlags()))
        return param1PipelineHelper.evaluate(param1Spliterator, false, param1IntFunction); 
      Node.OfDouble ofDouble = (Node.OfDouble)param1PipelineHelper.evaluate(param1Spliterator, true, param1IntFunction);
      double[] arrayOfDouble = (double[])ofDouble.asPrimitiveArray();
      Arrays.parallelSort(arrayOfDouble);
      return Nodes.node(arrayOfDouble);
    }
  }
  
  private static final class OfInt extends IntPipeline.StatefulOp<Integer> {
    OfInt(AbstractPipeline<?, Integer, ?> param1AbstractPipeline) { super(param1AbstractPipeline, StreamShape.INT_VALUE, StreamOpFlag.IS_ORDERED | StreamOpFlag.IS_SORTED); }
    
    public Sink<Integer> opWrapSink(int param1Int, Sink<Integer> param1Sink) {
      Objects.requireNonNull(param1Sink);
      return StreamOpFlag.SORTED.isKnown(param1Int) ? param1Sink : (StreamOpFlag.SIZED.isKnown(param1Int) ? new SortedOps.SizedIntSortingSink(param1Sink) : new SortedOps.IntSortingSink(param1Sink));
    }
    
    public <P_IN> Node<Integer> opEvaluateParallel(PipelineHelper<Integer> param1PipelineHelper, Spliterator<P_IN> param1Spliterator, IntFunction<Integer[]> param1IntFunction) {
      if (StreamOpFlag.SORTED.isKnown(param1PipelineHelper.getStreamAndOpFlags()))
        return param1PipelineHelper.evaluate(param1Spliterator, false, param1IntFunction); 
      Node.OfInt ofInt = (Node.OfInt)param1PipelineHelper.evaluate(param1Spliterator, true, param1IntFunction);
      int[] arrayOfInt = (int[])ofInt.asPrimitiveArray();
      Arrays.parallelSort(arrayOfInt);
      return Nodes.node(arrayOfInt);
    }
  }
  
  private static final class OfLong extends LongPipeline.StatefulOp<Long> {
    OfLong(AbstractPipeline<?, Long, ?> param1AbstractPipeline) { super(param1AbstractPipeline, StreamShape.LONG_VALUE, StreamOpFlag.IS_ORDERED | StreamOpFlag.IS_SORTED); }
    
    public Sink<Long> opWrapSink(int param1Int, Sink<Long> param1Sink) {
      Objects.requireNonNull(param1Sink);
      return StreamOpFlag.SORTED.isKnown(param1Int) ? param1Sink : (StreamOpFlag.SIZED.isKnown(param1Int) ? new SortedOps.SizedLongSortingSink(param1Sink) : new SortedOps.LongSortingSink(param1Sink));
    }
    
    public <P_IN> Node<Long> opEvaluateParallel(PipelineHelper<Long> param1PipelineHelper, Spliterator<P_IN> param1Spliterator, IntFunction<Long[]> param1IntFunction) {
      if (StreamOpFlag.SORTED.isKnown(param1PipelineHelper.getStreamAndOpFlags()))
        return param1PipelineHelper.evaluate(param1Spliterator, false, param1IntFunction); 
      Node.OfLong ofLong = (Node.OfLong)param1PipelineHelper.evaluate(param1Spliterator, true, param1IntFunction);
      long[] arrayOfLong = (long[])ofLong.asPrimitiveArray();
      Arrays.parallelSort(arrayOfLong);
      return Nodes.node(arrayOfLong);
    }
  }
  
  private static final class OfRef<T> extends ReferencePipeline.StatefulOp<T, T> {
    private final boolean isNaturalSort = true;
    
    private final Comparator<? super T> comparator;
    
    OfRef(AbstractPipeline<?, T, ?> param1AbstractPipeline) {
      super(param1AbstractPipeline, StreamShape.REFERENCE, StreamOpFlag.IS_ORDERED | StreamOpFlag.IS_SORTED);
      Comparator comparator1 = Comparator.naturalOrder();
      this.comparator = comparator1;
    }
    
    OfRef(AbstractPipeline<?, T, ?> param1AbstractPipeline, Comparator<? super T> param1Comparator) {
      super(param1AbstractPipeline, StreamShape.REFERENCE, StreamOpFlag.IS_ORDERED | StreamOpFlag.NOT_SORTED);
      this.comparator = (Comparator)Objects.requireNonNull(param1Comparator);
    }
    
    public Sink<T> opWrapSink(int param1Int, Sink<T> param1Sink) {
      Objects.requireNonNull(param1Sink);
      return (StreamOpFlag.SORTED.isKnown(param1Int) && this.isNaturalSort) ? param1Sink : (StreamOpFlag.SIZED.isKnown(param1Int) ? new SortedOps.SizedRefSortingSink(param1Sink, this.comparator) : new SortedOps.RefSortingSink(param1Sink, this.comparator));
    }
    
    public <P_IN> Node<T> opEvaluateParallel(PipelineHelper<T> param1PipelineHelper, Spliterator<P_IN> param1Spliterator, IntFunction<T[]> param1IntFunction) {
      if (StreamOpFlag.SORTED.isKnown(param1PipelineHelper.getStreamAndOpFlags()) && this.isNaturalSort)
        return param1PipelineHelper.evaluate(param1Spliterator, false, param1IntFunction); 
      Object[] arrayOfObject = param1PipelineHelper.evaluate(param1Spliterator, true, param1IntFunction).asArray(param1IntFunction);
      Arrays.parallelSort(arrayOfObject, this.comparator);
      return Nodes.node(arrayOfObject);
    }
  }
  
  private static final class RefSortingSink<T> extends AbstractRefSortingSink<T> {
    private ArrayList<T> list;
    
    RefSortingSink(Sink<? super T> param1Sink, Comparator<? super T> param1Comparator) { super(param1Sink, param1Comparator); }
    
    public void begin(long param1Long) {
      if (param1Long >= 2147483639L)
        throw new IllegalArgumentException("Stream size exceeds max array size"); 
      this.list = (param1Long >= 0L) ? new ArrayList((int)param1Long) : new ArrayList();
    }
    
    public void end() {
      this.list.sort(this.comparator);
      this.downstream.begin(this.list.size());
      if (!this.cancellationWasRequested) {
        this.list.forEach(this.downstream::accept);
      } else {
        for (Object object : this.list) {
          if (this.downstream.cancellationRequested())
            break; 
          this.downstream.accept(object);
        } 
      } 
      this.downstream.end();
      this.list = null;
    }
    
    public void accept(T param1T) { this.list.add(param1T); }
  }
  
  private static final class SizedDoubleSortingSink extends AbstractDoubleSortingSink {
    private double[] array;
    
    private int offset;
    
    SizedDoubleSortingSink(Sink<? super Double> param1Sink) { super(param1Sink); }
    
    public void begin(long param1Long) {
      if (param1Long >= 2147483639L)
        throw new IllegalArgumentException("Stream size exceeds max array size"); 
      this.array = new double[(int)param1Long];
    }
    
    public void end() {
      Arrays.sort(this.array, 0, this.offset);
      this.downstream.begin(this.offset);
      if (!this.cancellationWasRequested) {
        for (byte b = 0; b < this.offset; b++)
          this.downstream.accept(this.array[b]); 
      } else {
        for (byte b = 0; b < this.offset && !this.downstream.cancellationRequested(); b++)
          this.downstream.accept(this.array[b]); 
      } 
      this.downstream.end();
      this.array = null;
    }
    
    public void accept(double param1Double) { this.array[this.offset++] = param1Double; }
  }
  
  private static final class SizedIntSortingSink extends AbstractIntSortingSink {
    private int[] array;
    
    private int offset;
    
    SizedIntSortingSink(Sink<? super Integer> param1Sink) { super(param1Sink); }
    
    public void begin(long param1Long) {
      if (param1Long >= 2147483639L)
        throw new IllegalArgumentException("Stream size exceeds max array size"); 
      this.array = new int[(int)param1Long];
    }
    
    public void end() {
      Arrays.sort(this.array, 0, this.offset);
      this.downstream.begin(this.offset);
      if (!this.cancellationWasRequested) {
        for (byte b = 0; b < this.offset; b++)
          this.downstream.accept(this.array[b]); 
      } else {
        for (byte b = 0; b < this.offset && !this.downstream.cancellationRequested(); b++)
          this.downstream.accept(this.array[b]); 
      } 
      this.downstream.end();
      this.array = null;
    }
    
    public void accept(int param1Int) { this.array[this.offset++] = param1Int; }
  }
  
  private static final class SizedLongSortingSink extends AbstractLongSortingSink {
    private long[] array;
    
    private int offset;
    
    SizedLongSortingSink(Sink<? super Long> param1Sink) { super(param1Sink); }
    
    public void begin(long param1Long) {
      if (param1Long >= 2147483639L)
        throw new IllegalArgumentException("Stream size exceeds max array size"); 
      this.array = new long[(int)param1Long];
    }
    
    public void end() {
      Arrays.sort(this.array, 0, this.offset);
      this.downstream.begin(this.offset);
      if (!this.cancellationWasRequested) {
        for (byte b = 0; b < this.offset; b++)
          this.downstream.accept(this.array[b]); 
      } else {
        for (byte b = 0; b < this.offset && !this.downstream.cancellationRequested(); b++)
          this.downstream.accept(this.array[b]); 
      } 
      this.downstream.end();
      this.array = null;
    }
    
    public void accept(long param1Long) { this.array[this.offset++] = param1Long; }
  }
  
  private static final class SizedRefSortingSink<T> extends AbstractRefSortingSink<T> {
    private T[] array;
    
    private int offset;
    
    SizedRefSortingSink(Sink<? super T> param1Sink, Comparator<? super T> param1Comparator) { super(param1Sink, param1Comparator); }
    
    public void begin(long param1Long) {
      if (param1Long >= 2147483639L)
        throw new IllegalArgumentException("Stream size exceeds max array size"); 
      this.array = (Object[])new Object[(int)param1Long];
    }
    
    public void end() {
      Arrays.sort(this.array, 0, this.offset, this.comparator);
      this.downstream.begin(this.offset);
      if (!this.cancellationWasRequested) {
        for (byte b = 0; b < this.offset; b++)
          this.downstream.accept(this.array[b]); 
      } else {
        for (byte b = 0; b < this.offset && !this.downstream.cancellationRequested(); b++)
          this.downstream.accept(this.array[b]); 
      } 
      this.downstream.end();
      this.array = null;
    }
    
    public void accept(T param1T) { this.array[this.offset++] = param1T; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\stream\SortedOps.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */