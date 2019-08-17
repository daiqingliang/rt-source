package java.awt.event;

import java.awt.AWTEvent;
import java.awt.ActiveEvent;
import sun.awt.AWTAccessor;

public class InvocationEvent extends AWTEvent implements ActiveEvent {
  public static final int INVOCATION_FIRST = 1200;
  
  public static final int INVOCATION_DEFAULT = 1200;
  
  public static final int INVOCATION_LAST = 1200;
  
  protected Runnable runnable;
  
  private final Runnable listener;
  
  protected boolean catchExceptions;
  
  private Exception exception = null;
  
  private Throwable throwable = null;
  
  private long when;
  
  private static final long serialVersionUID = 436056344909459450L;
  
  public InvocationEvent(Object paramObject, Runnable paramRunnable) { this(paramObject, 1200, paramRunnable, null, null, false); }
  
  public InvocationEvent(Object paramObject1, Runnable paramRunnable, Object paramObject2, boolean paramBoolean) { this(paramObject1, 1200, paramRunnable, paramObject2, null, paramBoolean); }
  
  public InvocationEvent(Object paramObject, Runnable paramRunnable1, Runnable paramRunnable2, boolean paramBoolean) { this(paramObject, 1200, paramRunnable1, null, paramRunnable2, paramBoolean); }
  
  protected InvocationEvent(Object paramObject1, int paramInt, Runnable paramRunnable, Object paramObject2, boolean paramBoolean) { this(paramObject1, paramInt, paramRunnable, paramObject2, null, paramBoolean); }
  
  private InvocationEvent(Object paramObject1, int paramInt, Runnable paramRunnable1, Object paramObject2, Runnable paramRunnable2, boolean paramBoolean) {
    super(paramObject1, paramInt);
    this.runnable = paramRunnable1;
    this.notifier = paramObject2;
    this.listener = paramRunnable2;
    this.catchExceptions = paramBoolean;
    this.when = System.currentTimeMillis();
  }
  
  public void dispatch() {
    try {
      if (this.catchExceptions) {
        try {
          this.runnable.run();
        } catch (Throwable throwable1) {
          if (throwable1 instanceof Exception)
            this.exception = (Exception)throwable1; 
          this.throwable = throwable1;
        } 
      } else {
        this.runnable.run();
      } 
    } finally {
      finishedDispatching(true);
    } 
  }
  
  public Exception getException() { return this.catchExceptions ? this.exception : null; }
  
  public Throwable getThrowable() { return this.catchExceptions ? this.throwable : null; }
  
  public long getWhen() { return this.when; }
  
  public boolean isDispatched() { return this.dispatched; }
  
  private void finishedDispatching(boolean paramBoolean) {
    this.dispatched = paramBoolean;
    if (this.notifier != null)
      synchronized (this.notifier) {
        this.notifier.notifyAll();
      }  
    if (this.listener != null)
      this.listener.run(); 
  }
  
  public String paramString() {
    switch (this.id) {
      case 1200:
        str = "INVOCATION_DEFAULT";
        return str + ",runnable=" + this.runnable + ",notifier=" + this.notifier + ",catchExceptions=" + this.catchExceptions + ",when=" + this.when;
    } 
    String str = "unknown type";
    return str + ",runnable=" + this.runnable + ",notifier=" + this.notifier + ",catchExceptions=" + this.catchExceptions + ",when=" + this.when;
  }
  
  static  {
    AWTAccessor.setInvocationEventAccessor(new AWTAccessor.InvocationEventAccessor() {
          public void dispose(InvocationEvent param1InvocationEvent) { param1InvocationEvent.finishedDispatching(false); }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\event\InvocationEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */