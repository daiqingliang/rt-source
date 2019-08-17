package java.util.stream;

import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicReference;

abstract class AbstractShortCircuitTask<P_IN, P_OUT, R, K extends AbstractShortCircuitTask<P_IN, P_OUT, R, K>> extends AbstractTask<P_IN, P_OUT, R, K> {
  protected final AtomicReference<R> sharedResult = new AtomicReference(null);
  
  protected AbstractShortCircuitTask(PipelineHelper<P_OUT> paramPipelineHelper, Spliterator<P_IN> paramSpliterator) { super(paramPipelineHelper, paramSpliterator); }
  
  protected AbstractShortCircuitTask(K paramK, Spliterator<P_IN> paramSpliterator) { super(paramK, paramSpliterator); }
  
  protected abstract R getEmptyResult();
  
  public void compute() {
    Spliterator spliterator = this.spliterator;
    long l1 = spliterator.estimateSize();
    long l2 = getTargetSize(l1);
    boolean bool = false;
    AbstractShortCircuitTask abstractShortCircuitTask = this;
    AtomicReference atomicReference = this.sharedResult;
    Object object;
    while ((object = atomicReference.get()) == null) {
      AbstractShortCircuitTask abstractShortCircuitTask3;
      if (abstractShortCircuitTask.taskCanceled()) {
        object = abstractShortCircuitTask.getEmptyResult();
        break;
      } 
      Spliterator spliterator1;
      if (l1 <= l2 || (spliterator1 = spliterator.trySplit()) == null) {
        object = abstractShortCircuitTask.doLeaf();
        break;
      } 
      AbstractShortCircuitTask abstractShortCircuitTask1 = (AbstractShortCircuitTask)abstractShortCircuitTask.makeChild(spliterator1);
      abstractShortCircuitTask.leftChild = abstractShortCircuitTask1;
      AbstractShortCircuitTask abstractShortCircuitTask2 = (AbstractShortCircuitTask)abstractShortCircuitTask.makeChild(spliterator);
      abstractShortCircuitTask.rightChild = abstractShortCircuitTask2;
      abstractShortCircuitTask.setPendingCount(1);
      if (bool) {
        bool = false;
        spliterator = spliterator1;
        abstractShortCircuitTask = abstractShortCircuitTask1;
        abstractShortCircuitTask3 = abstractShortCircuitTask2;
      } else {
        bool = true;
        abstractShortCircuitTask = abstractShortCircuitTask2;
        abstractShortCircuitTask3 = abstractShortCircuitTask1;
      } 
      abstractShortCircuitTask3.fork();
      l1 = spliterator.estimateSize();
    } 
    abstractShortCircuitTask.setLocalResult(object);
    abstractShortCircuitTask.tryComplete();
  }
  
  protected void shortCircuit(R paramR) {
    if (paramR != null)
      this.sharedResult.compareAndSet(null, paramR); 
  }
  
  protected void setLocalResult(R paramR) {
    if (isRoot()) {
      if (paramR != null)
        this.sharedResult.compareAndSet(null, paramR); 
    } else {
      super.setLocalResult(paramR);
    } 
  }
  
  public R getRawResult() { return (R)getLocalResult(); }
  
  public R getLocalResult() {
    if (isRoot()) {
      Object object = this.sharedResult.get();
      return (R)((object == null) ? getEmptyResult() : object);
    } 
    return (R)super.getLocalResult();
  }
  
  protected void cancel() { this.canceled = true; }
  
  protected boolean taskCanceled() {
    boolean bool = this.canceled;
    if (!bool)
      for (AbstractShortCircuitTask abstractShortCircuitTask = (AbstractShortCircuitTask)getParent(); !bool && abstractShortCircuitTask != null; abstractShortCircuitTask = (AbstractShortCircuitTask)abstractShortCircuitTask.getParent())
        bool = abstractShortCircuitTask.canceled;  
    return bool;
  }
  
  protected void cancelLaterNodes() {
    AbstractShortCircuitTask abstractShortCircuitTask1 = (AbstractShortCircuitTask)getParent();
    AbstractShortCircuitTask abstractShortCircuitTask2 = this;
    while (abstractShortCircuitTask1 != null) {
      if (abstractShortCircuitTask1.leftChild == abstractShortCircuitTask2) {
        AbstractShortCircuitTask abstractShortCircuitTask = (AbstractShortCircuitTask)abstractShortCircuitTask1.rightChild;
        if (!abstractShortCircuitTask.canceled)
          abstractShortCircuitTask.cancel(); 
      } 
      abstractShortCircuitTask2 = abstractShortCircuitTask1;
      abstractShortCircuitTask1 = (AbstractShortCircuitTask)abstractShortCircuitTask1.getParent();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\stream\AbstractShortCircuitTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */