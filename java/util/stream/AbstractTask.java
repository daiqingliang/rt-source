package java.util.stream;

import java.util.Spliterator;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinPool;

abstract class AbstractTask<P_IN, P_OUT, R, K extends AbstractTask<P_IN, P_OUT, R, K>> extends CountedCompleter<R> {
  static final int LEAF_TARGET = ForkJoinPool.getCommonPoolParallelism() << 2;
  
  protected final PipelineHelper<P_OUT> helper;
  
  protected Spliterator<P_IN> spliterator;
  
  protected long targetSize;
  
  protected K leftChild;
  
  protected K rightChild;
  
  private R localResult;
  
  protected AbstractTask(PipelineHelper<P_OUT> paramPipelineHelper, Spliterator<P_IN> paramSpliterator) {
    super(null);
    this.helper = paramPipelineHelper;
    this.spliterator = paramSpliterator;
    this.targetSize = 0L;
  }
  
  protected AbstractTask(K paramK, Spliterator<P_IN> paramSpliterator) {
    super(paramK);
    this.spliterator = paramSpliterator;
    this.helper = paramK.helper;
    this.targetSize = paramK.targetSize;
  }
  
  protected abstract K makeChild(Spliterator<P_IN> paramSpliterator);
  
  protected abstract R doLeaf();
  
  public static long suggestTargetSize(long paramLong) {
    long l = paramLong / LEAF_TARGET;
    return (l > 0L) ? l : 1L;
  }
  
  protected final long getTargetSize(long paramLong) {
    long l;
    return ((l = this.targetSize) != 0L) ? l : (this.targetSize = suggestTargetSize(paramLong));
  }
  
  public R getRawResult() { return (R)this.localResult; }
  
  protected void setRawResult(R paramR) {
    if (paramR != null)
      throw new IllegalStateException(); 
  }
  
  protected R getLocalResult() { return (R)this.localResult; }
  
  protected void setLocalResult(R paramR) { this.localResult = paramR; }
  
  protected boolean isLeaf() { return (this.leftChild == null); }
  
  protected boolean isRoot() { return (getParent() == null); }
  
  protected K getParent() { return (K)(AbstractTask)getCompleter(); }
  
  public void compute() {
    Spliterator spliterator1 = this.spliterator;
    long l1 = spliterator1.estimateSize();
    long l2 = getTargetSize(l1);
    boolean bool = false;
    AbstractTask abstractTask = this;
    Spliterator spliterator2;
    while (l1 > l2 && (spliterator2 = spliterator1.trySplit()) != null) {
      AbstractTask abstractTask3;
      AbstractTask abstractTask1 = abstractTask.makeChild(spliterator2);
      abstractTask.leftChild = abstractTask1;
      AbstractTask abstractTask2 = abstractTask.makeChild(spliterator1);
      abstractTask.rightChild = abstractTask2;
      abstractTask.setPendingCount(1);
      if (bool) {
        bool = false;
        spliterator1 = spliterator2;
        abstractTask = abstractTask1;
        abstractTask3 = abstractTask2;
      } else {
        bool = true;
        abstractTask = abstractTask2;
        abstractTask3 = abstractTask1;
      } 
      abstractTask3.fork();
      l1 = spliterator1.estimateSize();
    } 
    abstractTask.setLocalResult(abstractTask.doLeaf());
    abstractTask.tryComplete();
  }
  
  public void onCompletion(CountedCompleter<?> paramCountedCompleter) {
    this.spliterator = null;
    this.leftChild = this.rightChild = null;
  }
  
  protected boolean isLeftmostNode() {
    for (AbstractTask abstractTask = this; abstractTask != null; abstractTask = abstractTask1) {
      AbstractTask abstractTask1 = abstractTask.getParent();
      if (abstractTask1 != null && abstractTask1.leftChild != abstractTask)
        return false; 
    } 
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\stream\AbstractTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */