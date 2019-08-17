package javax.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import sun.awt.AppContext;
import sun.swing.AccumulativeRunnable;

public abstract class SwingWorker<T, V> extends Object implements RunnableFuture<T> {
  private static final int MAX_WORKER_THREADS = 10;
  
  private final FutureTask<T> future;
  
  private final PropertyChangeSupport propertyChangeSupport;
  
  private AccumulativeRunnable<V> doProcess;
  
  private AccumulativeRunnable<Integer> doNotifyProgressChange;
  
  private final AccumulativeRunnable<Runnable> doSubmit = getDoSubmit();
  
  private static final Object DO_SUBMIT_KEY = new StringBuilder("doSubmit");
  
  public SwingWorker() {
    Callable<T> callable = new Callable<T>() {
        public T call() throws Exception {
          SwingWorker.this.setState(SwingWorker.StateValue.STARTED);
          return (T)SwingWorker.this.doInBackground();
        }
      };
    this.future = new FutureTask<T>(callable) {
        protected void done() {
          SwingWorker.this.doneEDT();
          SwingWorker.this.setState(SwingWorker.StateValue.DONE);
        }
      };
    this.state = StateValue.PENDING;
    this.propertyChangeSupport = new SwingWorkerPropertyChangeSupport(this);
    this.doProcess = null;
    this.doNotifyProgressChange = null;
  }
  
  protected abstract T doInBackground() throws Exception;
  
  public final void run() { this.future.run(); }
  
  @SafeVarargs
  protected final void publish(V... paramVarArgs) {
    synchronized (this) {
      if (this.doProcess == null)
        this.doProcess = new AccumulativeRunnable<V>() {
            public void run(List<V> param1List) { SwingWorker.this.process(param1List); }
            
            protected void submit() { SwingWorker.this.doSubmit.add(new Runnable[] { this }); }
          }; 
    } 
    this.doProcess.add(paramVarArgs);
  }
  
  protected void process(List<V> paramList) {}
  
  protected void done() {}
  
  protected final void setProgress(int paramInt) {
    if (paramInt < 0 || paramInt > 100)
      throw new IllegalArgumentException("the value should be from 0 to 100"); 
    if (this.progress == paramInt)
      return; 
    int i = this.progress;
    this.progress = paramInt;
    if (!getPropertyChangeSupport().hasListeners("progress"))
      return; 
    synchronized (this) {
      if (this.doNotifyProgressChange == null)
        this.doNotifyProgressChange = new AccumulativeRunnable<Integer>() {
            public void run(List<Integer> param1List) { SwingWorker.this.firePropertyChange("progress", param1List.get(0), param1List.get(param1List.size() - 1)); }
            
            protected void submit() { SwingWorker.this.doSubmit.add(new Runnable[] { this }); }
          }; 
    } 
    this.doNotifyProgressChange.add(new Integer[] { null, (new Integer[2][0] = Integer.valueOf(i)).valueOf(paramInt) });
  }
  
  public final int getProgress() { return this.progress; }
  
  public final void execute() { getWorkersExecutorService().execute(this); }
  
  public final boolean cancel(boolean paramBoolean) { return this.future.cancel(paramBoolean); }
  
  public final boolean isCancelled() { return this.future.isCancelled(); }
  
  public final boolean isDone() { return this.future.isDone(); }
  
  public final T get() throws Exception { return (T)this.future.get(); }
  
  public final T get(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException, ExecutionException, TimeoutException { return (T)this.future.get(paramLong, paramTimeUnit); }
  
  public final void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) { getPropertyChangeSupport().addPropertyChangeListener(paramPropertyChangeListener); }
  
  public final void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) { getPropertyChangeSupport().removePropertyChangeListener(paramPropertyChangeListener); }
  
  public final void firePropertyChange(String paramString, Object paramObject1, Object paramObject2) { getPropertyChangeSupport().firePropertyChange(paramString, paramObject1, paramObject2); }
  
  public final PropertyChangeSupport getPropertyChangeSupport() { return this.propertyChangeSupport; }
  
  public final StateValue getState() { return isDone() ? StateValue.DONE : this.state; }
  
  private void setState(StateValue paramStateValue) {
    StateValue stateValue = this.state;
    this.state = paramStateValue;
    firePropertyChange("state", stateValue, paramStateValue);
  }
  
  private void doneEDT() {
    Runnable runnable = new Runnable() {
        public void run() { SwingWorker.this.done(); }
      };
    if (SwingUtilities.isEventDispatchThread()) {
      runnable.run();
    } else {
      this.doSubmit.add(new Runnable[] { runnable });
    } 
  }
  
  private static ExecutorService getWorkersExecutorService() {
    AppContext appContext = AppContext.getAppContext();
    ExecutorService executorService = (ExecutorService)appContext.get(SwingWorker.class);
    if (executorService == null) {
      ThreadFactory threadFactory = new ThreadFactory() {
          final ThreadFactory defaultFactory = Executors.defaultThreadFactory();
          
          public Thread newThread(Runnable param1Runnable) {
            Thread thread = this.defaultFactory.newThread(param1Runnable);
            thread.setName("SwingWorker-" + thread.getName());
            thread.setDaemon(true);
            return thread;
          }
        };
      executorService = new ThreadPoolExecutor(10, 10, 10L, TimeUnit.MINUTES, new LinkedBlockingQueue(), threadFactory);
      appContext.put(SwingWorker.class, executorService);
      final ExecutorService es = executorService;
      appContext.addPropertyChangeListener("disposed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
              boolean bool = ((Boolean)param1PropertyChangeEvent.getNewValue()).booleanValue();
              if (bool) {
                WeakReference weakReference = new WeakReference(es);
                final ExecutorService executorService = (ExecutorService)weakReference.get();
                if (executorService != null)
                  AccessController.doPrivileged(new PrivilegedAction<Void>() {
                        public Void run() {
                          executorService.shutdown();
                          return null;
                        }
                      }); 
              } 
            }
          });
    } 
    return executorService;
  }
  
  private static AccumulativeRunnable<Runnable> getDoSubmit() {
    synchronized (DO_SUBMIT_KEY) {
      AppContext appContext = AppContext.getAppContext();
      Object object = appContext.get(DO_SUBMIT_KEY);
      if (object == null) {
        object = new DoSubmitAccumulativeRunnable(null);
        appContext.put(DO_SUBMIT_KEY, object);
      } 
      return (AccumulativeRunnable)object;
    } 
  }
  
  private static class DoSubmitAccumulativeRunnable extends AccumulativeRunnable<Runnable> implements ActionListener {
    private static final int DELAY = 33;
    
    private DoSubmitAccumulativeRunnable() {}
    
    protected void run(List<Runnable> param1List) {
      for (Runnable runnable : param1List)
        runnable.run(); 
    }
    
    protected void submit() {
      Timer timer = new Timer(33, this);
      timer.setRepeats(false);
      timer.start();
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) { run(); }
  }
  
  public enum StateValue {
    PENDING, STARTED, DONE;
  }
  
  private class SwingWorkerPropertyChangeSupport extends PropertyChangeSupport {
    SwingWorkerPropertyChangeSupport(Object param1Object) { super(param1Object); }
    
    public void firePropertyChange(final PropertyChangeEvent evt) {
      if (SwingUtilities.isEventDispatchThread()) {
        super.firePropertyChange(param1PropertyChangeEvent);
      } else {
        SwingWorker.this.doSubmit.add(new Runnable[] { new Runnable() {
                public void run() { SwingWorker.SwingWorkerPropertyChangeSupport.this.firePropertyChange(evt); }
              } });
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\SwingWorker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */