package java.util.concurrent;

import sun.misc.Unsafe;

public abstract class CountedCompleter<T> extends ForkJoinTask<T> {
  private static final long serialVersionUID = 5232453752276485070L;
  
  final CountedCompleter<?> completer;
  
  private static final Unsafe U;
  
  private static final long PENDING;
  
  protected CountedCompleter(CountedCompleter<?> paramCountedCompleter, int paramInt) {
    this.completer = paramCountedCompleter;
    this.pending = paramInt;
  }
  
  protected CountedCompleter(CountedCompleter<?> paramCountedCompleter) { this.completer = paramCountedCompleter; }
  
  protected CountedCompleter() { this.completer = null; }
  
  public abstract void compute();
  
  public void onCompletion(CountedCompleter<?> paramCountedCompleter) {}
  
  public boolean onExceptionalCompletion(Throwable paramThrowable, CountedCompleter<?> paramCountedCompleter) { return true; }
  
  public final CountedCompleter<?> getCompleter() { return this.completer; }
  
  public final int getPendingCount() { return this.pending; }
  
  public final void setPendingCount(int paramInt) { this.pending = paramInt; }
  
  public final void addToPendingCount(int paramInt) { U.getAndAddInt(this, PENDING, paramInt); }
  
  public final boolean compareAndSetPendingCount(int paramInt1, int paramInt2) { return U.compareAndSwapInt(this, PENDING, paramInt1, paramInt2); }
  
  public final int decrementPendingCountUnlessZero() {
    int i;
    do {
    
    } while ((i = this.pending) != 0 && !U.compareAndSwapInt(this, PENDING, i, i - 1));
    return i;
  }
  
  public final CountedCompleter<?> getRoot() {
    CountedCompleter countedCompleter1;
    CountedCompleter countedCompleter2;
    for (countedCompleter1 = this; (countedCompleter2 = countedCompleter1.completer) != null; countedCompleter1 = countedCompleter2);
    return countedCompleter1;
  }
  
  public final void tryComplete() {
    int i;
    CountedCompleter countedCompleter1 = this;
    CountedCompleter countedCompleter2 = countedCompleter1;
    do {
      while ((i = countedCompleter1.pending) == 0) {
        countedCompleter1.onCompletion(countedCompleter2);
        if ((countedCompleter1 = (countedCompleter2 = countedCompleter1).completer) == null) {
          countedCompleter2.quietlyComplete();
          return;
        } 
      } 
    } while (!U.compareAndSwapInt(countedCompleter1, PENDING, i, i - 1));
  }
  
  public final void propagateCompletion() {
    int i;
    CountedCompleter countedCompleter1 = this;
    CountedCompleter countedCompleter2 = countedCompleter1;
    do {
      while ((i = countedCompleter1.pending) == 0) {
        if ((countedCompleter1 = (countedCompleter2 = countedCompleter1).completer) == null) {
          countedCompleter2.quietlyComplete();
          return;
        } 
      } 
    } while (!U.compareAndSwapInt(countedCompleter1, PENDING, i, i - 1));
  }
  
  public void complete(T paramT) {
    setRawResult(paramT);
    onCompletion(this);
    quietlyComplete();
    CountedCompleter countedCompleter;
    if ((countedCompleter = this.completer) != null)
      countedCompleter.tryComplete(); 
  }
  
  public final CountedCompleter<?> firstComplete() {
    int i;
    do {
      if ((i = this.pending) == 0)
        return this; 
    } while (!U.compareAndSwapInt(this, PENDING, i, i - 1));
    return null;
  }
  
  public final CountedCompleter<?> nextComplete() {
    CountedCompleter countedCompleter;
    if ((countedCompleter = this.completer) != null)
      return countedCompleter.firstComplete(); 
    quietlyComplete();
    return null;
  }
  
  public final void quietlyCompleteRoot() {
    for (CountedCompleter countedCompleter = this;; countedCompleter = countedCompleter1) {
      CountedCompleter countedCompleter1;
      if ((countedCompleter1 = countedCompleter.completer) == null) {
        countedCompleter.quietlyComplete();
        return;
      } 
    } 
  }
  
  public final void helpComplete(int paramInt) {
    if (paramInt > 0 && this.status >= 0) {
      Thread thread;
      if (thread = Thread.currentThread() instanceof ForkJoinWorkerThread) {
        ForkJoinWorkerThread forkJoinWorkerThread;
        (forkJoinWorkerThread = (ForkJoinWorkerThread)thread).pool.helpComplete(forkJoinWorkerThread.workQueue, this, paramInt);
      } else {
        ForkJoinPool.common.externalHelpComplete(this, paramInt);
      } 
    } 
  }
  
  void internalPropagateException(Throwable paramThrowable) {
    CountedCompleter countedCompleter1 = this;
    CountedCompleter countedCompleter2 = countedCompleter1;
    while (countedCompleter1.onExceptionalCompletion(paramThrowable, countedCompleter2) && (countedCompleter1 = (countedCompleter2 = countedCompleter1).completer) != null && countedCompleter1.status >= 0 && countedCompleter1.recordExceptionalCompletion(paramThrowable) == Integer.MIN_VALUE);
  }
  
  protected final boolean exec() {
    compute();
    return false;
  }
  
  public T getRawResult() { return null; }
  
  protected void setRawResult(T paramT) {}
  
  static  {
    try {
      U = Unsafe.getUnsafe();
      PENDING = U.objectFieldOffset(CountedCompleter.class.getDeclaredField("pending"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\CountedCompleter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */