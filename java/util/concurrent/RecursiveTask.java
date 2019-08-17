package java.util.concurrent;

public abstract class RecursiveTask<V> extends ForkJoinTask<V> {
  private static final long serialVersionUID = 5232453952276485270L;
  
  V result;
  
  protected abstract V compute();
  
  public final V getRawResult() { return (V)this.result; }
  
  protected final void setRawResult(V paramV) { this.result = paramV; }
  
  protected final boolean exec() {
    this.result = compute();
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\RecursiveTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */