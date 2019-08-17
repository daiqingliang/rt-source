package javax.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.event.EventListenerList;

public class Timer implements Serializable {
  protected EventListenerList listenerList = new EventListenerList();
  
  private final AtomicBoolean notify = new AtomicBoolean(false);
  
  private final Runnable doPostEvent;
  
  private final Lock lock = new ReentrantLock();
  
  TimerQueue.DelayedTimer delayedTimer = null;
  
  public Timer(int paramInt, ActionListener paramActionListener) {
    this.delay = paramInt;
    this.initialDelay = paramInt;
    this.doPostEvent = new DoPostEvent();
    if (paramActionListener != null)
      addActionListener(paramActionListener); 
  }
  
  final AccessControlContext getAccessControlContext() {
    if (this.acc == null)
      throw new SecurityException("Timer is missing AccessControlContext"); 
    return this.acc;
  }
  
  public void addActionListener(ActionListener paramActionListener) { this.listenerList.add(ActionListener.class, paramActionListener); }
  
  public void removeActionListener(ActionListener paramActionListener) { this.listenerList.remove(ActionListener.class, paramActionListener); }
  
  public ActionListener[] getActionListeners() { return (ActionListener[])this.listenerList.getListeners(ActionListener.class); }
  
  protected void fireActionPerformed(ActionEvent paramActionEvent) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ActionListener.class)
        ((ActionListener)arrayOfObject[i + 1]).actionPerformed(paramActionEvent); 
    } 
  }
  
  public <T extends java.util.EventListener> T[] getListeners(Class<T> paramClass) { return (T[])this.listenerList.getListeners(paramClass); }
  
  private TimerQueue timerQueue() { return TimerQueue.sharedInstance(); }
  
  public static void setLogTimers(boolean paramBoolean) { logTimers = paramBoolean; }
  
  public static boolean getLogTimers() { return logTimers; }
  
  public void setDelay(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("Invalid delay: " + paramInt); 
    this.delay = paramInt;
  }
  
  public int getDelay() { return this.delay; }
  
  public void setInitialDelay(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("Invalid initial delay: " + paramInt); 
    this.initialDelay = paramInt;
  }
  
  public int getInitialDelay() { return this.initialDelay; }
  
  public void setRepeats(boolean paramBoolean) { this.repeats = paramBoolean; }
  
  public boolean isRepeats() { return this.repeats; }
  
  public void setCoalesce(boolean paramBoolean) {
    boolean bool = this.coalesce;
    this.coalesce = paramBoolean;
    if (!bool && this.coalesce)
      cancelEvent(); 
  }
  
  public boolean isCoalesce() { return this.coalesce; }
  
  public void setActionCommand(String paramString) { this.actionCommand = paramString; }
  
  public String getActionCommand() { return this.actionCommand; }
  
  public void start() { timerQueue().addTimer(this, getInitialDelay()); }
  
  public boolean isRunning() { return timerQueue().containsTimer(this); }
  
  public void stop() {
    getLock().lock();
    try {
      cancelEvent();
      timerQueue().removeTimer(this);
    } finally {
      getLock().unlock();
    } 
  }
  
  public void restart() {
    getLock().lock();
    try {
      stop();
      start();
    } finally {
      getLock().unlock();
    } 
  }
  
  void cancelEvent() { this.notify.set(false); }
  
  void post() {
    if (this.notify.compareAndSet(false, true) || !this.coalesce)
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
              SwingUtilities.invokeLater(Timer.this.doPostEvent);
              return null;
            }
          },  getAccessControlContext()); 
  }
  
  Lock getLock() { return this.lock; }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException {
    this.acc = AccessController.getContext();
    paramObjectInputStream.defaultReadObject();
  }
  
  private Object readResolve() {
    Timer timer = new Timer(getDelay(), null);
    timer.listenerList = this.listenerList;
    timer.initialDelay = this.initialDelay;
    timer.delay = this.delay;
    timer.repeats = this.repeats;
    timer.coalesce = this.coalesce;
    timer.actionCommand = this.actionCommand;
    return timer;
  }
  
  class DoPostEvent implements Runnable {
    public void run() {
      if (logTimers)
        System.out.println("Timer ringing: " + Timer.this); 
      if (Timer.this.notify.get()) {
        Timer.this.fireActionPerformed(new ActionEvent(Timer.this, 0, Timer.this.getActionCommand(), System.currentTimeMillis(), 0));
        if (Timer.this.coalesce)
          Timer.this.cancelEvent(); 
      } 
    }
    
    Timer getTimer() { return Timer.this; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\Timer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */