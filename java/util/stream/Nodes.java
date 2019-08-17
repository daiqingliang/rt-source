package java.util.stream;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.CountedCompleter;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;

final class Nodes {
  static final long MAX_ARRAY_SIZE = 2147483639L;
  
  static final String BAD_SIZE = "Stream size exceeds max array size";
  
  private static final Node EMPTY_NODE = new EmptyNode.OfRef(null);
  
  private static final Node.OfInt EMPTY_INT_NODE = new EmptyNode.OfInt();
  
  private static final Node.OfLong EMPTY_LONG_NODE = new EmptyNode.OfLong();
  
  private static final Node.OfDouble EMPTY_DOUBLE_NODE = new EmptyNode.OfDouble();
  
  private static final int[] EMPTY_INT_ARRAY = new int[0];
  
  private static final long[] EMPTY_LONG_ARRAY = new long[0];
  
  private static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
  
  private Nodes() { throw new Error("no instances"); }
  
  static <T> Node<T> emptyNode(StreamShape paramStreamShape) {
    switch (paramStreamShape) {
      case REFERENCE:
        return EMPTY_NODE;
      case INT_VALUE:
        return EMPTY_INT_NODE;
      case LONG_VALUE:
        return EMPTY_LONG_NODE;
      case DOUBLE_VALUE:
        return EMPTY_DOUBLE_NODE;
    } 
    throw new IllegalStateException("Unknown shape " + paramStreamShape);
  }
  
  static <T> Node<T> conc(StreamShape paramStreamShape, Node<T> paramNode1, Node<T> paramNode2) {
    switch (paramStreamShape) {
      case REFERENCE:
        return new ConcNode(paramNode1, paramNode2);
      case INT_VALUE:
        return new ConcNode.OfInt((Node.OfInt)paramNode1, (Node.OfInt)paramNode2);
      case LONG_VALUE:
        return new ConcNode.OfLong((Node.OfLong)paramNode1, (Node.OfLong)paramNode2);
      case DOUBLE_VALUE:
        return new ConcNode.OfDouble((Node.OfDouble)paramNode1, (Node.OfDouble)paramNode2);
    } 
    throw new IllegalStateException("Unknown shape " + paramStreamShape);
  }
  
  static <T> Node<T> node(T[] paramArrayOfT) { return new ArrayNode(paramArrayOfT); }
  
  static <T> Node<T> node(Collection<T> paramCollection) { return new CollectionNode(paramCollection); }
  
  static <T> Node.Builder<T> builder(long paramLong, IntFunction<T[]> paramIntFunction) { return (paramLong >= 0L && paramLong < 2147483639L) ? new FixedNodeBuilder(paramLong, paramIntFunction) : builder(); }
  
  static <T> Node.Builder<T> builder() { return new SpinedNodeBuilder(); }
  
  static Node.OfInt node(int[] paramArrayOfInt) { return new IntArrayNode(paramArrayOfInt); }
  
  static Node.Builder.OfInt intBuilder(long paramLong) { return (paramLong >= 0L && paramLong < 2147483639L) ? new IntFixedNodeBuilder(paramLong) : intBuilder(); }
  
  static Node.Builder.OfInt intBuilder() { return new IntSpinedNodeBuilder(); }
  
  static Node.OfLong node(long[] paramArrayOfLong) { return new LongArrayNode(paramArrayOfLong); }
  
  static Node.Builder.OfLong longBuilder(long paramLong) { return (paramLong >= 0L && paramLong < 2147483639L) ? new LongFixedNodeBuilder(paramLong) : longBuilder(); }
  
  static Node.Builder.OfLong longBuilder() { return new LongSpinedNodeBuilder(); }
  
  static Node.OfDouble node(double[] paramArrayOfDouble) { return new DoubleArrayNode(paramArrayOfDouble); }
  
  static Node.Builder.OfDouble doubleBuilder(long paramLong) { return (paramLong >= 0L && paramLong < 2147483639L) ? new DoubleFixedNodeBuilder(paramLong) : doubleBuilder(); }
  
  static Node.Builder.OfDouble doubleBuilder() { return new DoubleSpinedNodeBuilder(); }
  
  public static <P_IN, P_OUT> Node<P_OUT> collect(PipelineHelper<P_OUT> paramPipelineHelper, Spliterator<P_IN> paramSpliterator, boolean paramBoolean, IntFunction<P_OUT[]> paramIntFunction) {
    long l = paramPipelineHelper.exactOutputSizeIfKnown(paramSpliterator);
    if (l >= 0L && paramSpliterator.hasCharacteristics(16384)) {
      if (l >= 2147483639L)
        throw new IllegalArgumentException("Stream size exceeds max array size"); 
      Object[] arrayOfObject = (Object[])paramIntFunction.apply((int)l);
      (new SizedCollectorTask.OfRef(paramSpliterator, paramPipelineHelper, arrayOfObject)).invoke();
      return node(arrayOfObject);
    } 
    Node node = (Node)(new CollectorTask.OfRef(paramPipelineHelper, paramIntFunction, paramSpliterator)).invoke();
    return paramBoolean ? flatten(node, paramIntFunction) : node;
  }
  
  public static <P_IN> Node.OfInt collectInt(PipelineHelper<Integer> paramPipelineHelper, Spliterator<P_IN> paramSpliterator, boolean paramBoolean) {
    long l = paramPipelineHelper.exactOutputSizeIfKnown(paramSpliterator);
    if (l >= 0L && paramSpliterator.hasCharacteristics(16384)) {
      if (l >= 2147483639L)
        throw new IllegalArgumentException("Stream size exceeds max array size"); 
      int[] arrayOfInt = new int[(int)l];
      (new SizedCollectorTask.OfInt(paramSpliterator, paramPipelineHelper, arrayOfInt)).invoke();
      return node(arrayOfInt);
    } 
    Node.OfInt ofInt = (Node.OfInt)(new CollectorTask.OfInt(paramPipelineHelper, paramSpliterator)).invoke();
    return paramBoolean ? flattenInt(ofInt) : ofInt;
  }
  
  public static <P_IN> Node.OfLong collectLong(PipelineHelper<Long> paramPipelineHelper, Spliterator<P_IN> paramSpliterator, boolean paramBoolean) {
    long l = paramPipelineHelper.exactOutputSizeIfKnown(paramSpliterator);
    if (l >= 0L && paramSpliterator.hasCharacteristics(16384)) {
      if (l >= 2147483639L)
        throw new IllegalArgumentException("Stream size exceeds max array size"); 
      long[] arrayOfLong = new long[(int)l];
      (new SizedCollectorTask.OfLong(paramSpliterator, paramPipelineHelper, arrayOfLong)).invoke();
      return node(arrayOfLong);
    } 
    Node.OfLong ofLong = (Node.OfLong)(new CollectorTask.OfLong(paramPipelineHelper, paramSpliterator)).invoke();
    return paramBoolean ? flattenLong(ofLong) : ofLong;
  }
  
  public static <P_IN> Node.OfDouble collectDouble(PipelineHelper<Double> paramPipelineHelper, Spliterator<P_IN> paramSpliterator, boolean paramBoolean) {
    long l = paramPipelineHelper.exactOutputSizeIfKnown(paramSpliterator);
    if (l >= 0L && paramSpliterator.hasCharacteristics(16384)) {
      if (l >= 2147483639L)
        throw new IllegalArgumentException("Stream size exceeds max array size"); 
      double[] arrayOfDouble = new double[(int)l];
      (new SizedCollectorTask.OfDouble(paramSpliterator, paramPipelineHelper, arrayOfDouble)).invoke();
      return node(arrayOfDouble);
    } 
    Node.OfDouble ofDouble = (Node.OfDouble)(new CollectorTask.OfDouble(paramPipelineHelper, paramSpliterator)).invoke();
    return paramBoolean ? flattenDouble(ofDouble) : ofDouble;
  }
  
  public static <T> Node<T> flatten(Node<T> paramNode, IntFunction<T[]> paramIntFunction) {
    if (paramNode.getChildCount() > 0) {
      long l = paramNode.count();
      if (l >= 2147483639L)
        throw new IllegalArgumentException("Stream size exceeds max array size"); 
      Object[] arrayOfObject = (Object[])paramIntFunction.apply((int)l);
      (new ToArrayTask.OfRef(paramNode, arrayOfObject, 0, null)).invoke();
      return node(arrayOfObject);
    } 
    return paramNode;
  }
  
  public static Node.OfInt flattenInt(Node.OfInt paramOfInt) {
    if (paramOfInt.getChildCount() > 0) {
      long l = paramOfInt.count();
      if (l >= 2147483639L)
        throw new IllegalArgumentException("Stream size exceeds max array size"); 
      int[] arrayOfInt = new int[(int)l];
      (new ToArrayTask.OfInt(paramOfInt, arrayOfInt, 0, null)).invoke();
      return node(arrayOfInt);
    } 
    return paramOfInt;
  }
  
  public static Node.OfLong flattenLong(Node.OfLong paramOfLong) {
    if (paramOfLong.getChildCount() > 0) {
      long l = paramOfLong.count();
      if (l >= 2147483639L)
        throw new IllegalArgumentException("Stream size exceeds max array size"); 
      long[] arrayOfLong = new long[(int)l];
      (new ToArrayTask.OfLong(paramOfLong, arrayOfLong, 0, null)).invoke();
      return node(arrayOfLong);
    } 
    return paramOfLong;
  }
  
  public static Node.OfDouble flattenDouble(Node.OfDouble paramOfDouble) {
    if (paramOfDouble.getChildCount() > 0) {
      long l = paramOfDouble.count();
      if (l >= 2147483639L)
        throw new IllegalArgumentException("Stream size exceeds max array size"); 
      double[] arrayOfDouble = new double[(int)l];
      (new ToArrayTask.OfDouble(paramOfDouble, arrayOfDouble, 0, null)).invoke();
      return node(arrayOfDouble);
    } 
    return paramOfDouble;
  }
  
  private static abstract class AbstractConcNode<T, T_NODE extends Node<T>> extends Object implements Node<T> {
    protected final T_NODE left;
    
    protected final T_NODE right;
    
    private final long size;
    
    AbstractConcNode(T_NODE param1T_NODE1, T_NODE param1T_NODE2) {
      this.left = param1T_NODE1;
      this.right = param1T_NODE2;
      this.size = param1T_NODE1.count() + param1T_NODE2.count();
    }
    
    public int getChildCount() { return 2; }
    
    public T_NODE getChild(int param1Int) {
      if (param1Int == 0)
        return (T_NODE)this.left; 
      if (param1Int == 1)
        return (T_NODE)this.right; 
      throw new IndexOutOfBoundsException();
    }
    
    public long count() { return this.size; }
  }
  
  private static class ArrayNode<T> extends Object implements Node<T> {
    final T[] array;
    
    int curSize;
    
    ArrayNode(long param1Long, IntFunction<T[]> param1IntFunction) {
      if (param1Long >= 2147483639L)
        throw new IllegalArgumentException("Stream size exceeds max array size"); 
      this.array = (Object[])param1IntFunction.apply((int)param1Long);
      this.curSize = 0;
    }
    
    ArrayNode(T[] param1ArrayOfT) {
      this.array = param1ArrayOfT;
      this.curSize = param1ArrayOfT.length;
    }
    
    public Spliterator<T> spliterator() { return Arrays.spliterator(this.array, 0, this.curSize); }
    
    public void copyInto(T[] param1ArrayOfT, int param1Int) { System.arraycopy(this.array, 0, param1ArrayOfT, param1Int, this.curSize); }
    
    public T[] asArray(IntFunction<T[]> param1IntFunction) {
      if (this.array.length == this.curSize)
        return (T[])this.array; 
      throw new IllegalStateException();
    }
    
    public long count() { return this.curSize; }
    
    public void forEach(Consumer<? super T> param1Consumer) {
      for (byte b = 0; b < this.curSize; b++)
        param1Consumer.accept(this.array[b]); 
    }
    
    public String toString() { return String.format("ArrayNode[%d][%s]", new Object[] { Integer.valueOf(this.array.length - this.curSize), Arrays.toString(this.array) }); }
  }
  
  private static final class CollectionNode<T> extends Object implements Node<T> {
    private final Collection<T> c;
    
    CollectionNode(Collection<T> param1Collection) { this.c = param1Collection; }
    
    public Spliterator<T> spliterator() { return this.c.stream().spliterator(); }
    
    public void copyInto(T[] param1ArrayOfT, int param1Int) {
      for (Object object : this.c)
        param1ArrayOfT[param1Int++] = object; 
    }
    
    public T[] asArray(IntFunction<T[]> param1IntFunction) { return (T[])this.c.toArray((Object[])param1IntFunction.apply(this.c.size())); }
    
    public long count() { return this.c.size(); }
    
    public void forEach(Consumer<? super T> param1Consumer) { this.c.forEach(param1Consumer); }
    
    public String toString() { return String.format("CollectionNode[%d][%s]", new Object[] { Integer.valueOf(this.c.size()), this.c }); }
  }
  
  private static class CollectorTask<P_IN, P_OUT, T_NODE extends Node<P_OUT>, T_BUILDER extends Node.Builder<P_OUT>> extends AbstractTask<P_IN, P_OUT, T_NODE, CollectorTask<P_IN, P_OUT, T_NODE, T_BUILDER>> {
    protected final PipelineHelper<P_OUT> helper;
    
    protected final LongFunction<T_BUILDER> builderFactory;
    
    protected final BinaryOperator<T_NODE> concFactory;
    
    CollectorTask(PipelineHelper<P_OUT> param1PipelineHelper, Spliterator<P_IN> param1Spliterator, LongFunction<T_BUILDER> param1LongFunction, BinaryOperator<T_NODE> param1BinaryOperator) {
      super(param1PipelineHelper, param1Spliterator);
      this.helper = param1PipelineHelper;
      this.builderFactory = param1LongFunction;
      this.concFactory = param1BinaryOperator;
    }
    
    CollectorTask(CollectorTask<P_IN, P_OUT, T_NODE, T_BUILDER> param1CollectorTask, Spliterator<P_IN> param1Spliterator) {
      super(param1CollectorTask, param1Spliterator);
      this.helper = param1CollectorTask.helper;
      this.builderFactory = param1CollectorTask.builderFactory;
      this.concFactory = param1CollectorTask.concFactory;
    }
    
    protected CollectorTask<P_IN, P_OUT, T_NODE, T_BUILDER> makeChild(Spliterator<P_IN> param1Spliterator) { return new CollectorTask(this, param1Spliterator); }
    
    protected T_NODE doLeaf() {
      Node.Builder builder = (Node.Builder)this.builderFactory.apply(this.helper.exactOutputSizeIfKnown(this.spliterator));
      return (T_NODE)((Node.Builder)this.helper.wrapAndCopyInto(builder, this.spliterator)).build();
    }
    
    public void onCompletion(CountedCompleter<?> param1CountedCompleter) {
      if (!isLeaf())
        setLocalResult(this.concFactory.apply(((CollectorTask)this.leftChild).getLocalResult(), ((CollectorTask)this.rightChild).getLocalResult())); 
      super.onCompletion(param1CountedCompleter);
    }
    
    private static final class OfDouble<P_IN> extends CollectorTask<P_IN, Double, Node.OfDouble, Node.Builder.OfDouble> {
      OfDouble(PipelineHelper<Double> param2PipelineHelper, Spliterator<P_IN> param2Spliterator) { super(param2PipelineHelper, param2Spliterator, Nodes::doubleBuilder, OfDouble::new); }
    }
    
    private static final class OfInt<P_IN> extends CollectorTask<P_IN, Integer, Node.OfInt, Node.Builder.OfInt> {
      OfInt(PipelineHelper<Integer> param2PipelineHelper, Spliterator<P_IN> param2Spliterator) { super(param2PipelineHelper, param2Spliterator, Nodes::intBuilder, OfInt::new); }
    }
    
    private static final class OfLong<P_IN> extends CollectorTask<P_IN, Long, Node.OfLong, Node.Builder.OfLong> {
      OfLong(PipelineHelper<Long> param2PipelineHelper, Spliterator<P_IN> param2Spliterator) { super(param2PipelineHelper, param2Spliterator, Nodes::longBuilder, OfLong::new); }
    }
    
    private static final class OfRef<P_IN, P_OUT> extends CollectorTask<P_IN, P_OUT, Node<P_OUT>, Node.Builder<P_OUT>> {
      OfRef(PipelineHelper<P_OUT> param2PipelineHelper, IntFunction<P_OUT[]> param2IntFunction, Spliterator<P_IN> param2Spliterator) { super(param2PipelineHelper, param2Spliterator, param2Long -> Nodes.builder(param2Long, param2IntFunction), ConcNode::new); }
    }
  }
  
  private static final class OfDouble<P_IN> extends CollectorTask<P_IN, Double, Node.OfDouble, Node.Builder.OfDouble> {
    OfDouble(PipelineHelper<Double> param1PipelineHelper, Spliterator<P_IN> param1Spliterator) { super(param1PipelineHelper, param1Spliterator, Nodes::doubleBuilder, OfDouble::new); }
  }
  
  private static final class OfInt<P_IN> extends CollectorTask<P_IN, Integer, Node.OfInt, Node.Builder.OfInt> {
    OfInt(PipelineHelper<Integer> param1PipelineHelper, Spliterator<P_IN> param1Spliterator) { super(param1PipelineHelper, param1Spliterator, Nodes::intBuilder, OfInt::new); }
  }
  
  private static final class OfLong<P_IN> extends CollectorTask<P_IN, Long, Node.OfLong, Node.Builder.OfLong> {
    OfLong(PipelineHelper<Long> param1PipelineHelper, Spliterator<P_IN> param1Spliterator) { super(param1PipelineHelper, param1Spliterator, Nodes::longBuilder, OfLong::new); }
  }
  
  private static final class OfRef<P_IN, P_OUT> extends CollectorTask<P_IN, P_OUT, Node<P_OUT>, Node.Builder<P_OUT>> {
    OfRef(PipelineHelper<P_OUT> param1PipelineHelper, IntFunction<P_OUT[]> param1IntFunction, Spliterator<P_IN> param1Spliterator) { super(param1PipelineHelper, param1Spliterator, param1Long -> Nodes.builder(param1Long, param1IntFunction), ConcNode::new); }
  }
  
  static final class ConcNode<T> extends AbstractConcNode<T, Node<T>> implements Node<T> {
    ConcNode(Node<T> param1Node1, Node<T> param1Node2) { super(param1Node1, param1Node2); }
    
    public Spliterator<T> spliterator() { return new Nodes.InternalNodeSpliterator.OfRef(this); }
    
    public void copyInto(T[] param1ArrayOfT, int param1Int) {
      Objects.requireNonNull(param1ArrayOfT);
      this.left.copyInto(param1ArrayOfT, param1Int);
      this.right.copyInto(param1ArrayOfT, param1Int + (int)this.left.count());
    }
    
    public T[] asArray(IntFunction<T[]> param1IntFunction) {
      long l = count();
      if (l >= 2147483639L)
        throw new IllegalArgumentException("Stream size exceeds max array size"); 
      Object[] arrayOfObject = (Object[])param1IntFunction.apply((int)l);
      copyInto(arrayOfObject, 0);
      return (T[])arrayOfObject;
    }
    
    public void forEach(Consumer<? super T> param1Consumer) {
      this.left.forEach(param1Consumer);
      this.right.forEach(param1Consumer);
    }
    
    public Node<T> truncate(long param1Long1, long param1Long2, IntFunction<T[]> param1IntFunction) {
      if (param1Long1 == 0L && param1Long2 == count())
        return this; 
      long l = this.left.count();
      return (param1Long1 >= l) ? this.right.truncate(param1Long1 - l, param1Long2 - l, param1IntFunction) : ((param1Long2 <= l) ? this.left.truncate(param1Long1, param1Long2, param1IntFunction) : Nodes.conc(getShape(), this.left.truncate(param1Long1, l, param1IntFunction), this.right.truncate(0L, param1Long2 - l, param1IntFunction)));
    }
    
    public String toString() { return (count() < 32L) ? String.format("ConcNode[%s.%s]", new Object[] { this.left, this.right }) : String.format("ConcNode[size=%d]", new Object[] { Long.valueOf(count()) }); }
    
    static final class OfDouble extends OfPrimitive<Double, DoubleConsumer, double[], Spliterator.OfDouble, Node.OfDouble> implements Node.OfDouble {
      OfDouble(Node.OfDouble param2OfDouble1, Node.OfDouble param2OfDouble2) { super(param2OfDouble1, param2OfDouble2); }
      
      public Spliterator.OfDouble spliterator() { return new Nodes.InternalNodeSpliterator.OfDouble(this); }
    }
    
    static final class OfInt extends OfPrimitive<Integer, IntConsumer, int[], Spliterator.OfInt, Node.OfInt> implements Node.OfInt {
      OfInt(Node.OfInt param2OfInt1, Node.OfInt param2OfInt2) { super(param2OfInt1, param2OfInt2); }
      
      public Spliterator.OfInt spliterator() { return new Nodes.InternalNodeSpliterator.OfInt(this); }
    }
    
    static final class OfLong extends OfPrimitive<Long, LongConsumer, long[], Spliterator.OfLong, Node.OfLong> implements Node.OfLong {
      OfLong(Node.OfLong param2OfLong1, Node.OfLong param2OfLong2) { super(param2OfLong1, param2OfLong2); }
      
      public Spliterator.OfLong spliterator() { return new Nodes.InternalNodeSpliterator.OfLong(this); }
    }
    
    private static abstract class OfPrimitive<E, T_CONS, T_ARR, T_SPLITR extends Spliterator.OfPrimitive<E, T_CONS, T_SPLITR>, T_NODE extends Node.OfPrimitive<E, T_CONS, T_ARR, T_SPLITR, T_NODE>> extends Nodes.AbstractConcNode<E, T_NODE> implements Node.OfPrimitive<E, T_CONS, T_ARR, T_SPLITR, T_NODE> {
      OfPrimitive(T_NODE param2T_NODE1, T_NODE param2T_NODE2) { super(param2T_NODE1, param2T_NODE2); }
      
      public void forEach(T_CONS param2T_CONS) {
        ((Node.OfPrimitive)this.left).forEach(param2T_CONS);
        ((Node.OfPrimitive)this.right).forEach(param2T_CONS);
      }
      
      public void copyInto(T_ARR param2T_ARR, int param2Int) {
        ((Node.OfPrimitive)this.left).copyInto(param2T_ARR, param2Int);
        ((Node.OfPrimitive)this.right).copyInto(param2T_ARR, param2Int + (int)((Node.OfPrimitive)this.left).count());
      }
      
      public T_ARR asPrimitiveArray() {
        long l = count();
        if (l >= 2147483639L)
          throw new IllegalArgumentException("Stream size exceeds max array size"); 
        Object object = newArray((int)l);
        copyInto(object, 0);
        return (T_ARR)object;
      }
      
      public String toString() { return (count() < 32L) ? String.format("%s[%s.%s]", new Object[] { getClass().getName(), this.left, this.right }) : String.format("%s[size=%d]", new Object[] { getClass().getName(), Long.valueOf(count()) }); }
    }
  }
  
  static final class OfDouble extends ConcNode.OfPrimitive<Double, DoubleConsumer, double[], Spliterator.OfDouble, Node.OfDouble> implements Node.OfDouble {
    OfDouble(Node.OfDouble param1OfDouble1, Node.OfDouble param1OfDouble2) { super(param1OfDouble1, param1OfDouble2); }
    
    public Spliterator.OfDouble spliterator() { return new Nodes.InternalNodeSpliterator.OfDouble(this); }
  }
  
  static final class OfInt extends ConcNode.OfPrimitive<Integer, IntConsumer, int[], Spliterator.OfInt, Node.OfInt> implements Node.OfInt {
    OfInt(Node.OfInt param1OfInt1, Node.OfInt param1OfInt2) { super(param1OfInt1, param1OfInt2); }
    
    public Spliterator.OfInt spliterator() { return new Nodes.InternalNodeSpliterator.OfInt(this); }
  }
  
  static final class OfLong extends ConcNode.OfPrimitive<Long, LongConsumer, long[], Spliterator.OfLong, Node.OfLong> implements Node.OfLong {
    OfLong(Node.OfLong param1OfLong1, Node.OfLong param1OfLong2) { super(param1OfLong1, param1OfLong2); }
    
    public Spliterator.OfLong spliterator() { return new Nodes.InternalNodeSpliterator.OfLong(this); }
  }
  
  private static class DoubleArrayNode implements Node.OfDouble {
    final double[] array;
    
    int curSize;
    
    DoubleArrayNode(long param1Long) {
      if (param1Long >= 2147483639L)
        throw new IllegalArgumentException("Stream size exceeds max array size"); 
      this.array = new double[(int)param1Long];
      this.curSize = 0;
    }
    
    DoubleArrayNode(double[] param1ArrayOfDouble) {
      this.array = param1ArrayOfDouble;
      this.curSize = param1ArrayOfDouble.length;
    }
    
    public Spliterator.OfDouble spliterator() { return Arrays.spliterator(this.array, 0, this.curSize); }
    
    public double[] asPrimitiveArray() { return (this.array.length == this.curSize) ? this.array : Arrays.copyOf(this.array, this.curSize); }
    
    public void copyInto(double[] param1ArrayOfDouble, int param1Int) { System.arraycopy(this.array, 0, param1ArrayOfDouble, param1Int, this.curSize); }
    
    public long count() { return this.curSize; }
    
    public void forEach(DoubleConsumer param1DoubleConsumer) {
      for (byte b = 0; b < this.curSize; b++)
        param1DoubleConsumer.accept(this.array[b]); 
    }
    
    public String toString() { return String.format("DoubleArrayNode[%d][%s]", new Object[] { Integer.valueOf(this.array.length - this.curSize), Arrays.toString(this.array) }); }
  }
  
  private static final class DoubleFixedNodeBuilder extends DoubleArrayNode implements Node.Builder.OfDouble {
    DoubleFixedNodeBuilder(long param1Long) {
      super(param1Long);
      assert param1Long < 2147483639L;
    }
    
    public Node.OfDouble build() {
      if (this.curSize < this.array.length)
        throw new IllegalStateException(String.format("Current size %d is less than fixed size %d", new Object[] { Integer.valueOf(this.curSize), Integer.valueOf(this.array.length) })); 
      return this;
    }
    
    public void begin(long param1Long) {
      if (param1Long != this.array.length)
        throw new IllegalStateException(String.format("Begin size %d is not equal to fixed size %d", new Object[] { Long.valueOf(param1Long), Integer.valueOf(this.array.length) })); 
      this.curSize = 0;
    }
    
    public void accept(double param1Double) {
      if (this.curSize < this.array.length) {
        this.array[this.curSize++] = param1Double;
      } else {
        throw new IllegalStateException(String.format("Accept exceeded fixed size of %d", new Object[] { Integer.valueOf(this.array.length) }));
      } 
    }
    
    public void end() {
      if (this.curSize < this.array.length)
        throw new IllegalStateException(String.format("End size %d is less than fixed size %d", new Object[] { Integer.valueOf(this.curSize), Integer.valueOf(this.array.length) })); 
    }
    
    public String toString() { return String.format("DoubleFixedNodeBuilder[%d][%s]", new Object[] { Integer.valueOf(this.array.length - this.curSize), Arrays.toString(this.array) }); }
  }
  
  private static final class DoubleSpinedNodeBuilder extends SpinedBuffer.OfDouble implements Node.OfDouble, Node.Builder.OfDouble {
    private boolean building = false;
    
    public Spliterator.OfDouble spliterator() {
      assert !this.building : "during building";
      return super.spliterator();
    }
    
    public void forEach(DoubleConsumer param1DoubleConsumer) {
      assert !this.building : "during building";
      super.forEach(param1DoubleConsumer);
    }
    
    public void begin(long param1Long) {
      assert !this.building : "was already building";
      this.building = true;
      clear();
      ensureCapacity(param1Long);
    }
    
    public void accept(double param1Double) {
      assert this.building : "not building";
      super.accept(param1Double);
    }
    
    public void end() {
      assert this.building : "was not building";
      this.building = false;
    }
    
    public void copyInto(double[] param1ArrayOfDouble, int param1Int) {
      assert !this.building : "during building";
      super.copyInto(param1ArrayOfDouble, param1Int);
    }
    
    public double[] asPrimitiveArray() {
      assert !this.building : "during building";
      return (double[])super.asPrimitiveArray();
    }
    
    public Node.OfDouble build() {
      assert !this.building : "during building";
      return this;
    }
  }
  
  private static abstract class EmptyNode<T, T_ARR, T_CONS> extends Object implements Node<T> {
    public T[] asArray(IntFunction<T[]> param1IntFunction) { return (T[])(Object[])param1IntFunction.apply(0); }
    
    public void copyInto(T_ARR param1T_ARR, int param1Int) {}
    
    public long count() { return 0L; }
    
    public void forEach(T_CONS param1T_CONS) {}
    
    private static final class OfDouble extends EmptyNode<Double, double[], DoubleConsumer> implements Node.OfDouble {
      public Spliterator.OfDouble spliterator() { return Spliterators.emptyDoubleSpliterator(); }
      
      public double[] asPrimitiveArray() { return EMPTY_DOUBLE_ARRAY; }
    }
    
    private static final class OfInt extends EmptyNode<Integer, int[], IntConsumer> implements Node.OfInt {
      public Spliterator.OfInt spliterator() { return Spliterators.emptyIntSpliterator(); }
      
      public int[] asPrimitiveArray() { return EMPTY_INT_ARRAY; }
    }
    
    private static final class OfLong extends EmptyNode<Long, long[], LongConsumer> implements Node.OfLong {
      public Spliterator.OfLong spliterator() { return Spliterators.emptyLongSpliterator(); }
      
      public long[] asPrimitiveArray() { return EMPTY_LONG_ARRAY; }
    }
    
    private static class OfRef<T> extends EmptyNode<T, T[], Consumer<? super T>> {
      private OfRef() {}
      
      public Spliterator<T> spliterator() { return Spliterators.emptySpliterator(); }
    }
  }
  
  private static final class OfDouble extends EmptyNode<Double, double[], DoubleConsumer> implements Node.OfDouble {
    public Spliterator.OfDouble spliterator() { return Spliterators.emptyDoubleSpliterator(); }
    
    public double[] asPrimitiveArray() { return EMPTY_DOUBLE_ARRAY; }
  }
  
  private static final class OfInt extends EmptyNode<Integer, int[], IntConsumer> implements Node.OfInt {
    public Spliterator.OfInt spliterator() { return Spliterators.emptyIntSpliterator(); }
    
    public int[] asPrimitiveArray() { return EMPTY_INT_ARRAY; }
  }
  
  private static final class OfLong extends EmptyNode<Long, long[], LongConsumer> implements Node.OfLong {
    public Spliterator.OfLong spliterator() { return Spliterators.emptyLongSpliterator(); }
    
    public long[] asPrimitiveArray() { return EMPTY_LONG_ARRAY; }
  }
  
  private static class OfRef<T> extends EmptyNode<T, T[], Consumer<? super T>> {
    private OfRef() {}
    
    public Spliterator<T> spliterator() { return Spliterators.emptySpliterator(); }
  }
  
  private static final class FixedNodeBuilder<T> extends ArrayNode<T> implements Node.Builder<T> {
    FixedNodeBuilder(long param1Long, IntFunction<T[]> param1IntFunction) {
      super(param1Long, param1IntFunction);
      assert param1Long < 2147483639L;
    }
    
    public Node<T> build() {
      if (this.curSize < this.array.length)
        throw new IllegalStateException(String.format("Current size %d is less than fixed size %d", new Object[] { Integer.valueOf(this.curSize), Integer.valueOf(this.array.length) })); 
      return this;
    }
    
    public void begin(long param1Long) {
      if (param1Long != this.array.length)
        throw new IllegalStateException(String.format("Begin size %d is not equal to fixed size %d", new Object[] { Long.valueOf(param1Long), Integer.valueOf(this.array.length) })); 
      this.curSize = 0;
    }
    
    public void accept(T param1T) {
      if (this.curSize < this.array.length) {
        this.array[this.curSize++] = param1T;
      } else {
        throw new IllegalStateException(String.format("Accept exceeded fixed size of %d", new Object[] { Integer.valueOf(this.array.length) }));
      } 
    }
    
    public void end() {
      if (this.curSize < this.array.length)
        throw new IllegalStateException(String.format("End size %d is less than fixed size %d", new Object[] { Integer.valueOf(this.curSize), Integer.valueOf(this.array.length) })); 
    }
    
    public String toString() { return String.format("FixedNodeBuilder[%d][%s]", new Object[] { Integer.valueOf(this.array.length - this.curSize), Arrays.toString(this.array) }); }
  }
  
  private static class IntArrayNode implements Node.OfInt {
    final int[] array;
    
    int curSize;
    
    IntArrayNode(long param1Long) {
      if (param1Long >= 2147483639L)
        throw new IllegalArgumentException("Stream size exceeds max array size"); 
      this.array = new int[(int)param1Long];
      this.curSize = 0;
    }
    
    IntArrayNode(int[] param1ArrayOfInt) {
      this.array = param1ArrayOfInt;
      this.curSize = param1ArrayOfInt.length;
    }
    
    public Spliterator.OfInt spliterator() { return Arrays.spliterator(this.array, 0, this.curSize); }
    
    public int[] asPrimitiveArray() { return (this.array.length == this.curSize) ? this.array : Arrays.copyOf(this.array, this.curSize); }
    
    public void copyInto(int[] param1ArrayOfInt, int param1Int) { System.arraycopy(this.array, 0, param1ArrayOfInt, param1Int, this.curSize); }
    
    public long count() { return this.curSize; }
    
    public void forEach(IntConsumer param1IntConsumer) {
      for (byte b = 0; b < this.curSize; b++)
        param1IntConsumer.accept(this.array[b]); 
    }
    
    public String toString() { return String.format("IntArrayNode[%d][%s]", new Object[] { Integer.valueOf(this.array.length - this.curSize), Arrays.toString(this.array) }); }
  }
  
  private static final class IntFixedNodeBuilder extends IntArrayNode implements Node.Builder.OfInt {
    IntFixedNodeBuilder(long param1Long) {
      super(param1Long);
      assert param1Long < 2147483639L;
    }
    
    public Node.OfInt build() {
      if (this.curSize < this.array.length)
        throw new IllegalStateException(String.format("Current size %d is less than fixed size %d", new Object[] { Integer.valueOf(this.curSize), Integer.valueOf(this.array.length) })); 
      return this;
    }
    
    public void begin(long param1Long) {
      if (param1Long != this.array.length)
        throw new IllegalStateException(String.format("Begin size %d is not equal to fixed size %d", new Object[] { Long.valueOf(param1Long), Integer.valueOf(this.array.length) })); 
      this.curSize = 0;
    }
    
    public void accept(int param1Int) {
      if (this.curSize < this.array.length) {
        this.array[this.curSize++] = param1Int;
      } else {
        throw new IllegalStateException(String.format("Accept exceeded fixed size of %d", new Object[] { Integer.valueOf(this.array.length) }));
      } 
    }
    
    public void end() {
      if (this.curSize < this.array.length)
        throw new IllegalStateException(String.format("End size %d is less than fixed size %d", new Object[] { Integer.valueOf(this.curSize), Integer.valueOf(this.array.length) })); 
    }
    
    public String toString() { return String.format("IntFixedNodeBuilder[%d][%s]", new Object[] { Integer.valueOf(this.array.length - this.curSize), Arrays.toString(this.array) }); }
  }
  
  private static final class IntSpinedNodeBuilder extends SpinedBuffer.OfInt implements Node.OfInt, Node.Builder.OfInt {
    private boolean building = false;
    
    public Spliterator.OfInt spliterator() {
      assert !this.building : "during building";
      return super.spliterator();
    }
    
    public void forEach(IntConsumer param1IntConsumer) {
      assert !this.building : "during building";
      super.forEach(param1IntConsumer);
    }
    
    public void begin(long param1Long) {
      assert !this.building : "was already building";
      this.building = true;
      clear();
      ensureCapacity(param1Long);
    }
    
    public void accept(int param1Int) {
      assert this.building : "not building";
      super.accept(param1Int);
    }
    
    public void end() {
      assert this.building : "was not building";
      this.building = false;
    }
    
    public void copyInto(int[] param1ArrayOfInt, int param1Int) {
      assert !this.building : "during building";
      super.copyInto(param1ArrayOfInt, param1Int);
    }
    
    public int[] asPrimitiveArray() {
      assert !this.building : "during building";
      return (int[])super.asPrimitiveArray();
    }
    
    public Node.OfInt build() {
      assert !this.building : "during building";
      return this;
    }
  }
  
  private static abstract class InternalNodeSpliterator<T, S extends Spliterator<T>, N extends Node<T>> extends Object implements Spliterator<T> {
    N curNode;
    
    int curChildIndex;
    
    S lastNodeSpliterator;
    
    S tryAdvanceSpliterator;
    
    Deque<N> tryAdvanceStack;
    
    InternalNodeSpliterator(N param1N) { this.curNode = param1N; }
    
    protected final Deque<N> initStack() {
      ArrayDeque arrayDeque = new ArrayDeque(8);
      for (int i = this.curNode.getChildCount() - 1; i >= this.curChildIndex; i--)
        arrayDeque.addFirst(this.curNode.getChild(i)); 
      return arrayDeque;
    }
    
    protected final N findNextLeafNode(Deque<N> param1Deque) {
      Node node = null;
      while ((node = (Node)param1Deque.pollFirst()) != null) {
        if (node.getChildCount() == 0) {
          if (node.count() > 0L)
            return (N)node; 
          continue;
        } 
        for (int i = node.getChildCount() - 1; i >= 0; i--)
          param1Deque.addFirst(node.getChild(i)); 
      } 
      return null;
    }
    
    protected final boolean initTryAdvance() {
      if (this.curNode == null)
        return false; 
      if (this.tryAdvanceSpliterator == null)
        if (this.lastNodeSpliterator == null) {
          this.tryAdvanceStack = initStack();
          Node node = findNextLeafNode(this.tryAdvanceStack);
          if (node != null) {
            this.tryAdvanceSpliterator = node.spliterator();
          } else {
            this.curNode = null;
            return false;
          } 
        } else {
          this.tryAdvanceSpliterator = this.lastNodeSpliterator;
        }  
      return true;
    }
    
    public final S trySplit() {
      if (this.curNode == null || this.tryAdvanceSpliterator != null)
        return null; 
      if (this.lastNodeSpliterator != null)
        return (S)this.lastNodeSpliterator.trySplit(); 
      if (this.curChildIndex < this.curNode.getChildCount() - 1)
        return (S)this.curNode.getChild(this.curChildIndex++).spliterator(); 
      this.curNode = this.curNode.getChild(this.curChildIndex);
      if (this.curNode.getChildCount() == 0) {
        this.lastNodeSpliterator = this.curNode.spliterator();
        return (S)this.lastNodeSpliterator.trySplit();
      } 
      this.curChildIndex = 0;
      return (S)this.curNode.getChild(this.curChildIndex++).spliterator();
    }
    
    public final long estimateSize() {
      if (this.curNode == null)
        return 0L; 
      if (this.lastNodeSpliterator != null)
        return this.lastNodeSpliterator.estimateSize(); 
      long l = 0L;
      for (int i = this.curChildIndex; i < this.curNode.getChildCount(); i++)
        l += this.curNode.getChild(i).count(); 
      return l;
    }
    
    public final int characteristics() { return 64; }
    
    private static final class OfDouble extends OfPrimitive<Double, DoubleConsumer, double[], Spliterator.OfDouble, Node.OfDouble> implements Spliterator.OfDouble {
      OfDouble(Node.OfDouble param2OfDouble) { super(param2OfDouble); }
    }
    
    private static final class OfInt extends OfPrimitive<Integer, IntConsumer, int[], Spliterator.OfInt, Node.OfInt> implements Spliterator.OfInt {
      OfInt(Node.OfInt param2OfInt) { super(param2OfInt); }
    }
    
    private static final class OfLong extends OfPrimitive<Long, LongConsumer, long[], Spliterator.OfLong, Node.OfLong> implements Spliterator.OfLong {
      OfLong(Node.OfLong param2OfLong) { super(param2OfLong); }
    }
    
    private static abstract class OfPrimitive<T, T_CONS, T_ARR, T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>, N extends Node.OfPrimitive<T, T_CONS, T_ARR, T_SPLITR, N>> extends InternalNodeSpliterator<T, T_SPLITR, N> implements Spliterator.OfPrimitive<T, T_CONS, T_SPLITR> {
      OfPrimitive(N param2N) { super(param2N); }
      
      public boolean tryAdvance(T_CONS param2T_CONS) {
        if (!initTryAdvance())
          return false; 
        boolean bool = ((Spliterator.OfPrimitive)this.tryAdvanceSpliterator).tryAdvance(param2T_CONS);
        if (!bool) {
          if (this.lastNodeSpliterator == null) {
            Node.OfPrimitive ofPrimitive = (Node.OfPrimitive)findNextLeafNode(this.tryAdvanceStack);
            if (ofPrimitive != null) {
              this.tryAdvanceSpliterator = ofPrimitive.spliterator();
              return ((Spliterator.OfPrimitive)this.tryAdvanceSpliterator).tryAdvance(param2T_CONS);
            } 
          } 
          this.curNode = null;
        } 
        return bool;
      }
      
      public void forEachRemaining(T_CONS param2T_CONS) {
        if (this.curNode == null)
          return; 
        if (this.tryAdvanceSpliterator == null) {
          if (this.lastNodeSpliterator == null) {
            Deque deque = initStack();
            Node.OfPrimitive ofPrimitive;
            while ((ofPrimitive = (Node.OfPrimitive)findNextLeafNode(deque)) != null)
              ofPrimitive.forEach(param2T_CONS); 
            this.curNode = null;
          } else {
            ((Spliterator.OfPrimitive)this.lastNodeSpliterator).forEachRemaining(param2T_CONS);
          } 
        } else {
          while (tryAdvance(param2T_CONS));
        } 
      }
    }
    
    private static final class OfRef<T> extends InternalNodeSpliterator<T, Spliterator<T>, Node<T>> {
      OfRef(Node<T> param2Node) { super(param2Node); }
      
      public boolean tryAdvance(Consumer<? super T> param2Consumer) {
        if (!initTryAdvance())
          return false; 
        boolean bool = this.tryAdvanceSpliterator.tryAdvance(param2Consumer);
        if (!bool) {
          if (this.lastNodeSpliterator == null) {
            Node node = findNextLeafNode(this.tryAdvanceStack);
            if (node != null) {
              this.tryAdvanceSpliterator = node.spliterator();
              return this.tryAdvanceSpliterator.tryAdvance(param2Consumer);
            } 
          } 
          this.curNode = null;
        } 
        return bool;
      }
      
      public void forEachRemaining(Consumer<? super T> param2Consumer) {
        if (this.curNode == null)
          return; 
        if (this.tryAdvanceSpliterator == null) {
          if (this.lastNodeSpliterator == null) {
            Deque deque = initStack();
            Node node;
            while ((node = findNextLeafNode(deque)) != null)
              node.forEach(param2Consumer); 
            this.curNode = null;
          } else {
            this.lastNodeSpliterator.forEachRemaining(param2Consumer);
          } 
        } else {
          while (tryAdvance(param2Consumer));
        } 
      }
    }
  }
  
  private static class LongArrayNode implements Node.OfLong {
    final long[] array;
    
    int curSize;
    
    LongArrayNode(long param1Long) {
      if (param1Long >= 2147483639L)
        throw new IllegalArgumentException("Stream size exceeds max array size"); 
      this.array = new long[(int)param1Long];
      this.curSize = 0;
    }
    
    LongArrayNode(long[] param1ArrayOfLong) {
      this.array = param1ArrayOfLong;
      this.curSize = param1ArrayOfLong.length;
    }
    
    public Spliterator.OfLong spliterator() { return Arrays.spliterator(this.array, 0, this.curSize); }
    
    public long[] asPrimitiveArray() { return (this.array.length == this.curSize) ? this.array : Arrays.copyOf(this.array, this.curSize); }
    
    public void copyInto(long[] param1ArrayOfLong, int param1Int) { System.arraycopy(this.array, 0, param1ArrayOfLong, param1Int, this.curSize); }
    
    public long count() { return this.curSize; }
    
    public void forEach(LongConsumer param1LongConsumer) {
      for (byte b = 0; b < this.curSize; b++)
        param1LongConsumer.accept(this.array[b]); 
    }
    
    public String toString() { return String.format("LongArrayNode[%d][%s]", new Object[] { Integer.valueOf(this.array.length - this.curSize), Arrays.toString(this.array) }); }
  }
  
  private static final class LongFixedNodeBuilder extends LongArrayNode implements Node.Builder.OfLong {
    LongFixedNodeBuilder(long param1Long) {
      super(param1Long);
      assert param1Long < 2147483639L;
    }
    
    public Node.OfLong build() {
      if (this.curSize < this.array.length)
        throw new IllegalStateException(String.format("Current size %d is less than fixed size %d", new Object[] { Integer.valueOf(this.curSize), Integer.valueOf(this.array.length) })); 
      return this;
    }
    
    public void begin(long param1Long) {
      if (param1Long != this.array.length)
        throw new IllegalStateException(String.format("Begin size %d is not equal to fixed size %d", new Object[] { Long.valueOf(param1Long), Integer.valueOf(this.array.length) })); 
      this.curSize = 0;
    }
    
    public void accept(long param1Long) {
      if (this.curSize < this.array.length) {
        this.array[this.curSize++] = param1Long;
      } else {
        throw new IllegalStateException(String.format("Accept exceeded fixed size of %d", new Object[] { Integer.valueOf(this.array.length) }));
      } 
    }
    
    public void end() {
      if (this.curSize < this.array.length)
        throw new IllegalStateException(String.format("End size %d is less than fixed size %d", new Object[] { Integer.valueOf(this.curSize), Integer.valueOf(this.array.length) })); 
    }
    
    public String toString() { return String.format("LongFixedNodeBuilder[%d][%s]", new Object[] { Integer.valueOf(this.array.length - this.curSize), Arrays.toString(this.array) }); }
  }
  
  private static final class LongSpinedNodeBuilder extends SpinedBuffer.OfLong implements Node.OfLong, Node.Builder.OfLong {
    private boolean building = false;
    
    public Spliterator.OfLong spliterator() {
      assert !this.building : "during building";
      return super.spliterator();
    }
    
    public void forEach(LongConsumer param1LongConsumer) {
      assert !this.building : "during building";
      super.forEach(param1LongConsumer);
    }
    
    public void begin(long param1Long) {
      assert !this.building : "was already building";
      this.building = true;
      clear();
      ensureCapacity(param1Long);
    }
    
    public void accept(long param1Long) {
      assert this.building : "not building";
      super.accept(param1Long);
    }
    
    public void end() {
      assert this.building : "was not building";
      this.building = false;
    }
    
    public void copyInto(long[] param1ArrayOfLong, int param1Int) {
      assert !this.building : "during building";
      super.copyInto(param1ArrayOfLong, param1Int);
    }
    
    public long[] asPrimitiveArray() {
      assert !this.building : "during building";
      return (long[])super.asPrimitiveArray();
    }
    
    public Node.OfLong build() {
      assert !this.building : "during building";
      return this;
    }
  }
  
  private static abstract class SizedCollectorTask<P_IN, P_OUT, T_SINK extends Sink<P_OUT>, K extends SizedCollectorTask<P_IN, P_OUT, T_SINK, K>> extends CountedCompleter<Void> implements Sink<P_OUT> {
    protected final Spliterator<P_IN> spliterator;
    
    protected final PipelineHelper<P_OUT> helper;
    
    protected final long targetSize;
    
    protected long offset;
    
    protected long length;
    
    protected int index;
    
    protected int fence;
    
    SizedCollectorTask(Spliterator<P_IN> param1Spliterator, PipelineHelper<P_OUT> param1PipelineHelper, int param1Int) {
      assert param1Spliterator.hasCharacteristics(16384);
      this.spliterator = param1Spliterator;
      this.helper = param1PipelineHelper;
      this.targetSize = AbstractTask.suggestTargetSize(param1Spliterator.estimateSize());
      this.offset = 0L;
      this.length = param1Int;
    }
    
    SizedCollectorTask(K param1K, Spliterator<P_IN> param1Spliterator, long param1Long1, long param1Long2, int param1Int) {
      super(param1K);
      assert param1Spliterator.hasCharacteristics(16384);
      this.spliterator = param1Spliterator;
      this.helper = param1K.helper;
      this.targetSize = param1K.targetSize;
      this.offset = param1Long1;
      this.length = param1Long2;
      if (param1Long1 < 0L || param1Long2 < 0L || param1Long1 + param1Long2 - 1L >= param1Int)
        throw new IllegalArgumentException(String.format("offset and length interval [%d, %d + %d) is not within array size interval [0, %d)", new Object[] { Long.valueOf(param1Long1), Long.valueOf(param1Long1), Long.valueOf(param1Long2), Integer.valueOf(param1Int) })); 
    }
    
    public void compute() {
      SizedCollectorTask sizedCollectorTask1 = this;
      Spliterator spliterator1 = this.spliterator;
      Spliterator spliterator2;
      while (spliterator1.estimateSize() > sizedCollectorTask1.targetSize && (spliterator2 = spliterator1.trySplit()) != null) {
        sizedCollectorTask1.setPendingCount(1);
        long l = spliterator2.estimateSize();
        sizedCollectorTask1.makeChild(spliterator2, sizedCollectorTask1.offset, l).fork();
        sizedCollectorTask1 = sizedCollectorTask1.makeChild(spliterator1, sizedCollectorTask1.offset + l, sizedCollectorTask1.length - l);
      } 
      assert sizedCollectorTask1.offset + sizedCollectorTask1.length < 2147483639L;
      SizedCollectorTask sizedCollectorTask2 = sizedCollectorTask1;
      sizedCollectorTask1.helper.wrapAndCopyInto(sizedCollectorTask2, spliterator1);
      sizedCollectorTask1.propagateCompletion();
    }
    
    abstract K makeChild(Spliterator<P_IN> param1Spliterator, long param1Long1, long param1Long2);
    
    public void begin(long param1Long) {
      if (param1Long > this.length)
        throw new IllegalStateException("size passed to Sink.begin exceeds array length"); 
      this.index = (int)this.offset;
      this.fence = this.index + (int)this.length;
    }
    
    static final class OfDouble<P_IN> extends SizedCollectorTask<P_IN, Double, Sink.OfDouble, OfDouble<P_IN>> implements Sink.OfDouble {
      private final double[] array;
      
      OfDouble(Spliterator<P_IN> param2Spliterator, PipelineHelper<Double> param2PipelineHelper, double[] param2ArrayOfDouble) {
        super(param2Spliterator, param2PipelineHelper, param2ArrayOfDouble.length);
        this.array = param2ArrayOfDouble;
      }
      
      OfDouble(OfDouble<P_IN> param2OfDouble, Spliterator<P_IN> param2Spliterator, long param2Long1, long param2Long2) {
        super(param2OfDouble, param2Spliterator, param2Long1, param2Long2, param2OfDouble.array.length);
        this.array = param2OfDouble.array;
      }
      
      OfDouble<P_IN> makeChild(Spliterator<P_IN> param2Spliterator, long param2Long1, long param2Long2) { return new OfDouble(this, param2Spliterator, param2Long1, param2Long2); }
      
      public void accept(double param2Double) {
        if (this.index >= this.fence)
          throw new IndexOutOfBoundsException(Integer.toString(this.index)); 
        this.array[this.index++] = param2Double;
      }
    }
    
    static final class OfInt<P_IN> extends SizedCollectorTask<P_IN, Integer, Sink.OfInt, OfInt<P_IN>> implements Sink.OfInt {
      private final int[] array;
      
      OfInt(Spliterator<P_IN> param2Spliterator, PipelineHelper<Integer> param2PipelineHelper, int[] param2ArrayOfInt) {
        super(param2Spliterator, param2PipelineHelper, param2ArrayOfInt.length);
        this.array = param2ArrayOfInt;
      }
      
      OfInt(OfInt<P_IN> param2OfInt, Spliterator<P_IN> param2Spliterator, long param2Long1, long param2Long2) {
        super(param2OfInt, param2Spliterator, param2Long1, param2Long2, param2OfInt.array.length);
        this.array = param2OfInt.array;
      }
      
      OfInt<P_IN> makeChild(Spliterator<P_IN> param2Spliterator, long param2Long1, long param2Long2) { return new OfInt(this, param2Spliterator, param2Long1, param2Long2); }
      
      public void accept(int param2Int) {
        if (this.index >= this.fence)
          throw new IndexOutOfBoundsException(Integer.toString(this.index)); 
        this.array[this.index++] = param2Int;
      }
    }
    
    static final class OfLong<P_IN> extends SizedCollectorTask<P_IN, Long, Sink.OfLong, OfLong<P_IN>> implements Sink.OfLong {
      private final long[] array;
      
      OfLong(Spliterator<P_IN> param2Spliterator, PipelineHelper<Long> param2PipelineHelper, long[] param2ArrayOfLong) {
        super(param2Spliterator, param2PipelineHelper, param2ArrayOfLong.length);
        this.array = param2ArrayOfLong;
      }
      
      OfLong(OfLong<P_IN> param2OfLong, Spliterator<P_IN> param2Spliterator, long param2Long1, long param2Long2) {
        super(param2OfLong, param2Spliterator, param2Long1, param2Long2, param2OfLong.array.length);
        this.array = param2OfLong.array;
      }
      
      OfLong<P_IN> makeChild(Spliterator<P_IN> param2Spliterator, long param2Long1, long param2Long2) { return new OfLong(this, param2Spliterator, param2Long1, param2Long2); }
      
      public void accept(long param2Long) {
        if (this.index >= this.fence)
          throw new IndexOutOfBoundsException(Integer.toString(this.index)); 
        this.array[this.index++] = param2Long;
      }
    }
    
    static final class OfRef<P_IN, P_OUT> extends SizedCollectorTask<P_IN, P_OUT, Sink<P_OUT>, OfRef<P_IN, P_OUT>> implements Sink<P_OUT> {
      private final P_OUT[] array;
      
      OfRef(Spliterator<P_IN> param2Spliterator, PipelineHelper<P_OUT> param2PipelineHelper, P_OUT[] param2ArrayOfP_OUT) {
        super(param2Spliterator, param2PipelineHelper, param2ArrayOfP_OUT.length);
        this.array = param2ArrayOfP_OUT;
      }
      
      OfRef(OfRef<P_IN, P_OUT> param2OfRef, Spliterator<P_IN> param2Spliterator, long param2Long1, long param2Long2) {
        super(param2OfRef, param2Spliterator, param2Long1, param2Long2, param2OfRef.array.length);
        this.array = param2OfRef.array;
      }
      
      OfRef<P_IN, P_OUT> makeChild(Spliterator<P_IN> param2Spliterator, long param2Long1, long param2Long2) { return new OfRef(this, param2Spliterator, param2Long1, param2Long2); }
      
      public void accept(P_OUT param2P_OUT) {
        if (this.index >= this.fence)
          throw new IndexOutOfBoundsException(Integer.toString(this.index)); 
        this.array[this.index++] = param2P_OUT;
      }
    }
  }
  
  static final class OfDouble<P_IN> extends SizedCollectorTask<P_IN, Double, Sink.OfDouble, SizedCollectorTask.OfDouble<P_IN>> implements Sink.OfDouble {
    private final double[] array;
    
    OfDouble(Spliterator<P_IN> param1Spliterator, PipelineHelper<Double> param1PipelineHelper, double[] param1ArrayOfDouble) {
      super(param1Spliterator, param1PipelineHelper, param1ArrayOfDouble.length);
      this.array = param1ArrayOfDouble;
    }
    
    OfDouble(OfDouble<P_IN> param1OfDouble, Spliterator<P_IN> param1Spliterator, long param1Long1, long param1Long2) {
      super(param1OfDouble, param1Spliterator, param1Long1, param1Long2, param1OfDouble.array.length);
      this.array = param1OfDouble.array;
    }
    
    OfDouble<P_IN> makeChild(Spliterator<P_IN> param1Spliterator, long param1Long1, long param1Long2) { return new OfDouble(this, param1Spliterator, param1Long1, param1Long2); }
    
    public void accept(double param1Double) {
      if (this.index >= this.fence)
        throw new IndexOutOfBoundsException(Integer.toString(this.index)); 
      this.array[this.index++] = param1Double;
    }
  }
  
  static final class OfInt<P_IN> extends SizedCollectorTask<P_IN, Integer, Sink.OfInt, SizedCollectorTask.OfInt<P_IN>> implements Sink.OfInt {
    private final int[] array;
    
    OfInt(Spliterator<P_IN> param1Spliterator, PipelineHelper<Integer> param1PipelineHelper, int[] param1ArrayOfInt) {
      super(param1Spliterator, param1PipelineHelper, param1ArrayOfInt.length);
      this.array = param1ArrayOfInt;
    }
    
    OfInt(OfInt<P_IN> param1OfInt, Spliterator<P_IN> param1Spliterator, long param1Long1, long param1Long2) {
      super(param1OfInt, param1Spliterator, param1Long1, param1Long2, param1OfInt.array.length);
      this.array = param1OfInt.array;
    }
    
    OfInt<P_IN> makeChild(Spliterator<P_IN> param1Spliterator, long param1Long1, long param1Long2) { return new OfInt(this, param1Spliterator, param1Long1, param1Long2); }
    
    public void accept(int param1Int) {
      if (this.index >= this.fence)
        throw new IndexOutOfBoundsException(Integer.toString(this.index)); 
      this.array[this.index++] = param1Int;
    }
  }
  
  static final class OfLong<P_IN> extends SizedCollectorTask<P_IN, Long, Sink.OfLong, SizedCollectorTask.OfLong<P_IN>> implements Sink.OfLong {
    private final long[] array;
    
    OfLong(Spliterator<P_IN> param1Spliterator, PipelineHelper<Long> param1PipelineHelper, long[] param1ArrayOfLong) {
      super(param1Spliterator, param1PipelineHelper, param1ArrayOfLong.length);
      this.array = param1ArrayOfLong;
    }
    
    OfLong(OfLong<P_IN> param1OfLong, Spliterator<P_IN> param1Spliterator, long param1Long1, long param1Long2) {
      super(param1OfLong, param1Spliterator, param1Long1, param1Long2, param1OfLong.array.length);
      this.array = param1OfLong.array;
    }
    
    OfLong<P_IN> makeChild(Spliterator<P_IN> param1Spliterator, long param1Long1, long param1Long2) { return new OfLong(this, param1Spliterator, param1Long1, param1Long2); }
    
    public void accept(long param1Long) {
      if (this.index >= this.fence)
        throw new IndexOutOfBoundsException(Integer.toString(this.index)); 
      this.array[this.index++] = param1Long;
    }
  }
  
  static final class OfRef<P_IN, P_OUT> extends SizedCollectorTask<P_IN, P_OUT, Sink<P_OUT>, SizedCollectorTask.OfRef<P_IN, P_OUT>> implements Sink<P_OUT> {
    private final P_OUT[] array;
    
    OfRef(Spliterator<P_IN> param1Spliterator, PipelineHelper<P_OUT> param1PipelineHelper, P_OUT[] param1ArrayOfP_OUT) {
      super(param1Spliterator, param1PipelineHelper, param1ArrayOfP_OUT.length);
      this.array = param1ArrayOfP_OUT;
    }
    
    OfRef(OfRef<P_IN, P_OUT> param1OfRef, Spliterator<P_IN> param1Spliterator, long param1Long1, long param1Long2) {
      super(param1OfRef, param1Spliterator, param1Long1, param1Long2, param1OfRef.array.length);
      this.array = param1OfRef.array;
    }
    
    OfRef<P_IN, P_OUT> makeChild(Spliterator<P_IN> param1Spliterator, long param1Long1, long param1Long2) { return new OfRef(this, param1Spliterator, param1Long1, param1Long2); }
    
    public void accept(P_OUT param1P_OUT) {
      if (this.index >= this.fence)
        throw new IndexOutOfBoundsException(Integer.toString(this.index)); 
      this.array[this.index++] = param1P_OUT;
    }
  }
  
  private static final class SpinedNodeBuilder<T> extends SpinedBuffer<T> implements Node<T>, Node.Builder<T> {
    private boolean building = false;
    
    public Spliterator<T> spliterator() {
      assert !this.building : "during building";
      return super.spliterator();
    }
    
    public void forEach(Consumer<? super T> param1Consumer) {
      assert !this.building : "during building";
      super.forEach(param1Consumer);
    }
    
    public void begin(long param1Long) {
      assert !this.building : "was already building";
      this.building = true;
      clear();
      ensureCapacity(param1Long);
    }
    
    public void accept(T param1T) {
      assert this.building : "not building";
      super.accept(param1T);
    }
    
    public void end() {
      assert this.building : "was not building";
      this.building = false;
    }
    
    public void copyInto(T[] param1ArrayOfT, int param1Int) {
      assert !this.building : "during building";
      super.copyInto(param1ArrayOfT, param1Int);
    }
    
    public T[] asArray(IntFunction<T[]> param1IntFunction) {
      assert !this.building : "during building";
      return (T[])super.asArray(param1IntFunction);
    }
    
    public Node<T> build() {
      assert !this.building : "during building";
      return this;
    }
  }
  
  private static abstract class ToArrayTask<T, T_NODE extends Node<T>, K extends ToArrayTask<T, T_NODE, K>> extends CountedCompleter<Void> {
    protected final T_NODE node;
    
    protected final int offset;
    
    ToArrayTask(T_NODE param1T_NODE, int param1Int) {
      this.node = param1T_NODE;
      this.offset = param1Int;
    }
    
    ToArrayTask(K param1K, T_NODE param1T_NODE, int param1Int) {
      super(param1K);
      this.node = param1T_NODE;
      this.offset = param1Int;
    }
    
    abstract void copyNodeToArray();
    
    abstract K makeChild(int param1Int1, int param1Int2);
    
    public void compute() {
      for (ToArrayTask toArrayTask = this;; toArrayTask = toArrayTask.makeChild(b, toArrayTask.offset + i)) {
        if (toArrayTask.node.getChildCount() == 0) {
          toArrayTask.copyNodeToArray();
          toArrayTask.propagateCompletion();
          return;
        } 
        toArrayTask.setPendingCount(toArrayTask.node.getChildCount() - 1);
        int i = 0;
        byte b;
        for (b = 0; b < toArrayTask.node.getChildCount() - 1; b++) {
          ToArrayTask toArrayTask1 = toArrayTask.makeChild(b, toArrayTask.offset + i);
          i = (int)(i + toArrayTask1.node.count());
          toArrayTask1.fork();
        } 
      } 
    }
    
    private static final class OfDouble extends OfPrimitive<Double, DoubleConsumer, double[], Spliterator.OfDouble, Node.OfDouble> {
      private OfDouble(Node.OfDouble param2OfDouble, double[] param2ArrayOfDouble, int param2Int) { super(param2OfDouble, param2ArrayOfDouble, param2Int, null); }
    }
    
    private static final class OfInt extends OfPrimitive<Integer, IntConsumer, int[], Spliterator.OfInt, Node.OfInt> {
      private OfInt(Node.OfInt param2OfInt, int[] param2ArrayOfInt, int param2Int) { super(param2OfInt, param2ArrayOfInt, param2Int, null); }
    }
    
    private static final class OfLong extends OfPrimitive<Long, LongConsumer, long[], Spliterator.OfLong, Node.OfLong> {
      private OfLong(Node.OfLong param2OfLong, long[] param2ArrayOfLong, int param2Int) { super(param2OfLong, param2ArrayOfLong, param2Int, null); }
    }
    
    private static class OfPrimitive<T, T_CONS, T_ARR, T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>, T_NODE extends Node.OfPrimitive<T, T_CONS, T_ARR, T_SPLITR, T_NODE>> extends ToArrayTask<T, T_NODE, OfPrimitive<T, T_CONS, T_ARR, T_SPLITR, T_NODE>> {
      private final T_ARR array;
      
      private OfPrimitive(T_NODE param2T_NODE, T_ARR param2T_ARR, int param2Int) {
        super(param2T_NODE, param2Int);
        this.array = param2T_ARR;
      }
      
      private OfPrimitive(OfPrimitive<T, T_CONS, T_ARR, T_SPLITR, T_NODE> param2OfPrimitive, T_NODE param2T_NODE, int param2Int) {
        super(param2OfPrimitive, param2T_NODE, param2Int);
        this.array = param2OfPrimitive.array;
      }
      
      OfPrimitive<T, T_CONS, T_ARR, T_SPLITR, T_NODE> makeChild(int param2Int1, int param2Int2) { return new OfPrimitive(this, ((Node.OfPrimitive)this.node).getChild(param2Int1), param2Int2); }
      
      void copyNodeToArray() { ((Node.OfPrimitive)this.node).copyInto(this.array, this.offset); }
    }
    
    private static final class OfRef<T> extends ToArrayTask<T, Node<T>, OfRef<T>> {
      private final T[] array;
      
      private OfRef(Node<T> param2Node, T[] param2ArrayOfT, int param2Int) {
        super(param2Node, param2Int);
        this.array = param2ArrayOfT;
      }
      
      private OfRef(OfRef<T> param2OfRef, Node<T> param2Node, int param2Int) {
        super(param2OfRef, param2Node, param2Int);
        this.array = param2OfRef.array;
      }
      
      OfRef<T> makeChild(int param2Int1, int param2Int2) { return new OfRef(this, this.node.getChild(param2Int1), param2Int2); }
      
      void copyNodeToArray() { this.node.copyInto(this.array, this.offset); }
    }
  }
  
  private static final class OfDouble extends ToArrayTask.OfPrimitive<Double, DoubleConsumer, double[], Spliterator.OfDouble, Node.OfDouble> {
    private OfDouble(Node.OfDouble param1OfDouble, double[] param1ArrayOfDouble, int param1Int) { super(param1OfDouble, param1ArrayOfDouble, param1Int, null); }
  }
  
  private static final class OfInt extends ToArrayTask.OfPrimitive<Integer, IntConsumer, int[], Spliterator.OfInt, Node.OfInt> {
    private OfInt(Node.OfInt param1OfInt, int[] param1ArrayOfInt, int param1Int) { super(param1OfInt, param1ArrayOfInt, param1Int, null); }
  }
  
  private static final class OfLong extends ToArrayTask.OfPrimitive<Long, LongConsumer, long[], Spliterator.OfLong, Node.OfLong> {
    private OfLong(Node.OfLong param1OfLong, long[] param1ArrayOfLong, int param1Int) { super(param1OfLong, param1ArrayOfLong, param1Int, null); }
  }
  
  private static final class OfRef<T> extends ToArrayTask<T, Node<T>, ToArrayTask.OfRef<T>> {
    private final T[] array;
    
    private OfRef(Node<T> param1Node, T[] param1ArrayOfT, int param1Int) {
      super(param1Node, param1Int);
      this.array = param1ArrayOfT;
    }
    
    private OfRef(OfRef<T> param1OfRef, Node<T> param1Node, int param1Int) {
      super(param1OfRef, param1Node, param1Int);
      this.array = param1OfRef.array;
    }
    
    OfRef<T> makeChild(int param1Int1, int param1Int2) { return new OfRef(this, this.node.getChild(param1Int1), param1Int2); }
    
    void copyNodeToArray() { this.node.copyInto(this.array, this.offset); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\stream\Nodes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */