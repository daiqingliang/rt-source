package java.util.concurrent;

public abstract class RecursiveAction extends ForkJoinTask<Void> {
  private static final long serialVersionUID = 5232453952276485070L;
  
  protected abstract void compute();
  
  public final Void getRawResult() { return null; }
  
  protected final void setRawResult(Void paramVoid) {}
  
  protected final boolean exec() {
    compute();
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\RecursiveAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */