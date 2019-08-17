package java.lang;

import java.io.PrintStream;
import java.util.Arrays;
import sun.misc.VM;

public class ThreadGroup implements Thread.UncaughtExceptionHandler {
  private final ThreadGroup parent;
  
  String name = "system";
  
  int maxPriority = 10;
  
  boolean destroyed;
  
  boolean daemon;
  
  boolean vmAllowSuspension;
  
  int nUnstartedThreads = 0;
  
  int nthreads;
  
  Thread[] threads;
  
  int ngroups;
  
  ThreadGroup[] groups;
  
  private ThreadGroup() { this.parent = null; }
  
  public ThreadGroup(String paramString) { this(Thread.currentThread().getThreadGroup(), paramString); }
  
  public ThreadGroup(ThreadGroup paramThreadGroup, String paramString) { this(checkParentAccess(paramThreadGroup), paramThreadGroup, paramString); }
  
  private ThreadGroup(Void paramVoid, ThreadGroup paramThreadGroup, String paramString) {
    this.daemon = paramThreadGroup.daemon;
    this.vmAllowSuspension = paramThreadGroup.vmAllowSuspension;
    this.parent = paramThreadGroup;
    paramThreadGroup.add(this);
  }
  
  private static Void checkParentAccess(ThreadGroup paramThreadGroup) {
    paramThreadGroup.checkAccess();
    return null;
  }
  
  public final String getName() { return this.name; }
  
  public final ThreadGroup getParent() {
    if (this.parent != null)
      this.parent.checkAccess(); 
    return this.parent;
  }
  
  public final int getMaxPriority() { return this.maxPriority; }
  
  public final boolean isDaemon() { return this.daemon; }
  
  public boolean isDestroyed() { return this.destroyed; }
  
  public final void setDaemon(boolean paramBoolean) {
    checkAccess();
    this.daemon = paramBoolean;
  }
  
  public final void setMaxPriority(int paramInt) {
    Object object;
    int i;
    synchronized (this) {
      checkAccess();
      if (paramInt < 1 || paramInt > 10)
        return; 
      this.maxPriority = (this.parent != null) ? Math.min(paramInt, this.parent.maxPriority) : paramInt;
      i = this.ngroups;
      if (this.groups != null) {
        object = (ThreadGroup[])Arrays.copyOf(this.groups, i);
      } else {
        object = null;
      } 
    } 
    for (byte b = 0; b < i; b++)
      object[b].setMaxPriority(paramInt); 
  }
  
  public final boolean parentOf(ThreadGroup paramThreadGroup) {
    while (paramThreadGroup != null) {
      if (paramThreadGroup == this)
        return true; 
      paramThreadGroup = paramThreadGroup.parent;
    } 
    return false;
  }
  
  public final void checkAccess() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkAccess(this); 
  }
  
  public int activeCount() {
    Object object;
    int j;
    int i;
    synchronized (this) {
      if (this.destroyed)
        return 0; 
      i = this.nthreads;
      j = this.ngroups;
      if (this.groups != null) {
        object = (ThreadGroup[])Arrays.copyOf(this.groups, j);
      } else {
        object = null;
      } 
    } 
    for (byte b = 0; b < j; b++)
      i += object[b].activeCount(); 
    return i;
  }
  
  public int enumerate(Thread[] paramArrayOfThread) {
    checkAccess();
    return enumerate(paramArrayOfThread, 0, true);
  }
  
  public int enumerate(Thread[] paramArrayOfThread, boolean paramBoolean) {
    checkAccess();
    return enumerate(paramArrayOfThread, 0, paramBoolean);
  }
  
  private int enumerate(Thread[] paramArrayOfThread, int paramInt, boolean paramBoolean) {
    int i = 0;
    ThreadGroup[] arrayOfThreadGroup = null;
    synchronized (this) {
      if (this.destroyed)
        return 0; 
      int j = this.nthreads;
      if (j > paramArrayOfThread.length - paramInt)
        j = paramArrayOfThread.length - paramInt; 
      for (byte b = 0; b < j; b++) {
        if (this.threads[b].isAlive())
          paramArrayOfThread[paramInt++] = this.threads[b]; 
      } 
      if (paramBoolean) {
        i = this.ngroups;
        if (this.groups != null) {
          arrayOfThreadGroup = (ThreadGroup[])Arrays.copyOf(this.groups, i);
        } else {
          arrayOfThreadGroup = null;
        } 
      } 
    } 
    if (paramBoolean)
      for (byte b = 0; b < i; b++)
        paramInt = arrayOfThreadGroup[b].enumerate(paramArrayOfThread, paramInt, true);  
    return paramInt;
  }
  
  public int activeGroupCount() {
    Object object;
    int i;
    synchronized (this) {
      if (this.destroyed)
        return 0; 
      i = this.ngroups;
      if (this.groups != null) {
        object = (ThreadGroup[])Arrays.copyOf(this.groups, i);
      } else {
        object = null;
      } 
    } 
    int j = i;
    for (byte b = 0; b < i; b++)
      j += object[b].activeGroupCount(); 
    return j;
  }
  
  public int enumerate(ThreadGroup[] paramArrayOfThreadGroup) {
    checkAccess();
    return enumerate(paramArrayOfThreadGroup, 0, true);
  }
  
  public int enumerate(ThreadGroup[] paramArrayOfThreadGroup, boolean paramBoolean) {
    checkAccess();
    return enumerate(paramArrayOfThreadGroup, 0, paramBoolean);
  }
  
  private int enumerate(ThreadGroup[] paramArrayOfThreadGroup, int paramInt, boolean paramBoolean) {
    int i = 0;
    ThreadGroup[] arrayOfThreadGroup = null;
    synchronized (this) {
      if (this.destroyed)
        return 0; 
      int j = this.ngroups;
      if (j > paramArrayOfThreadGroup.length - paramInt)
        j = paramArrayOfThreadGroup.length - paramInt; 
      if (j > 0) {
        System.arraycopy(this.groups, 0, paramArrayOfThreadGroup, paramInt, j);
        paramInt += j;
      } 
      if (paramBoolean) {
        i = this.ngroups;
        if (this.groups != null) {
          arrayOfThreadGroup = (ThreadGroup[])Arrays.copyOf(this.groups, i);
        } else {
          arrayOfThreadGroup = null;
        } 
      } 
    } 
    if (paramBoolean)
      for (byte b = 0; b < i; b++)
        paramInt = arrayOfThreadGroup[b].enumerate(paramArrayOfThreadGroup, paramInt, true);  
    return paramInt;
  }
  
  @Deprecated
  public final void stop() {
    if (stopOrSuspend(false))
      Thread.currentThread().stop(); 
  }
  
  public final void interrupt() {
    Object object;
    int i;
    synchronized (this) {
      checkAccess();
      for (byte b1 = 0; b1 < this.nthreads; b1++)
        this.threads[b1].interrupt(); 
      i = this.ngroups;
      if (this.groups != null) {
        object = (ThreadGroup[])Arrays.copyOf(this.groups, i);
      } else {
        object = null;
      } 
    } 
    for (byte b = 0; b < i; b++)
      object[b].interrupt(); 
  }
  
  @Deprecated
  public final void suspend() {
    if (stopOrSuspend(true))
      Thread.currentThread().suspend(); 
  }
  
  private boolean stopOrSuspend(boolean paramBoolean) {
    int i;
    boolean bool = false;
    Thread thread = Thread.currentThread();
    ThreadGroup[] arrayOfThreadGroup = null;
    synchronized (this) {
      checkAccess();
      for (byte b1 = 0; b1 < this.nthreads; b1++) {
        if (this.threads[b1] == thread) {
          bool = true;
        } else if (paramBoolean) {
          this.threads[b1].suspend();
        } else {
          this.threads[b1].stop();
        } 
      } 
      i = this.ngroups;
      if (this.groups != null)
        arrayOfThreadGroup = (ThreadGroup[])Arrays.copyOf(this.groups, i); 
    } 
    for (byte b = 0; b < i; b++)
      bool = (arrayOfThreadGroup[b].stopOrSuspend(paramBoolean) || bool); 
    return bool;
  }
  
  @Deprecated
  public final void resume() {
    Object object;
    int i;
    synchronized (this) {
      checkAccess();
      for (byte b1 = 0; b1 < this.nthreads; b1++)
        this.threads[b1].resume(); 
      i = this.ngroups;
      if (this.groups != null) {
        object = (ThreadGroup[])Arrays.copyOf(this.groups, i);
      } else {
        object = null;
      } 
    } 
    for (byte b = 0; b < i; b++)
      object[b].resume(); 
  }
  
  public final void destroy() {
    Object object;
    int i;
    synchronized (this) {
      checkAccess();
      if (this.destroyed || this.nthreads > 0)
        throw new IllegalThreadStateException(); 
      i = this.ngroups;
      if (this.groups != null) {
        object = (ThreadGroup[])Arrays.copyOf(this.groups, i);
      } else {
        object = null;
      } 
      if (this.parent != null) {
        this.destroyed = true;
        this.ngroups = 0;
        this.groups = null;
        this.nthreads = 0;
        this.threads = null;
      } 
    } 
    for (byte b = 0; b < i; b++)
      object[b].destroy(); 
    if (this.parent != null)
      this.parent.remove(this); 
  }
  
  private final void add(ThreadGroup paramThreadGroup) {
    synchronized (this) {
      if (this.destroyed)
        throw new IllegalThreadStateException(); 
      if (this.groups == null) {
        this.groups = new ThreadGroup[4];
      } else if (this.ngroups == this.groups.length) {
        this.groups = (ThreadGroup[])Arrays.copyOf(this.groups, this.ngroups * 2);
      } 
      this.groups[this.ngroups] = paramThreadGroup;
      this.ngroups++;
    } 
  }
  
  private void remove(ThreadGroup paramThreadGroup) {
    synchronized (this) {
      if (this.destroyed)
        return; 
      for (int i = 0; i < this.ngroups; i++) {
        if (this.groups[i] == paramThreadGroup) {
          this.ngroups--;
          System.arraycopy(this.groups, i + true, this.groups, i, this.ngroups - i);
          this.groups[this.ngroups] = null;
          break;
        } 
      } 
      if (this.nthreads == 0)
        notifyAll(); 
      if (this.daemon && this.nthreads == 0 && this.nUnstartedThreads == 0 && this.ngroups == 0)
        destroy(); 
    } 
  }
  
  void addUnstarted() {
    synchronized (this) {
      if (this.destroyed)
        throw new IllegalThreadStateException(); 
      this.nUnstartedThreads++;
    } 
  }
  
  void add(Thread paramThread) {
    synchronized (this) {
      if (this.destroyed)
        throw new IllegalThreadStateException(); 
      if (this.threads == null) {
        this.threads = new Thread[4];
      } else if (this.nthreads == this.threads.length) {
        this.threads = (Thread[])Arrays.copyOf(this.threads, this.nthreads * 2);
      } 
      this.threads[this.nthreads] = paramThread;
      this.nthreads++;
      this.nUnstartedThreads--;
    } 
  }
  
  void threadStartFailed(Thread paramThread) {
    synchronized (this) {
      remove(paramThread);
      this.nUnstartedThreads++;
    } 
  }
  
  void threadTerminated(Thread paramThread) {
    synchronized (this) {
      remove(paramThread);
      if (this.nthreads == 0)
        notifyAll(); 
      if (this.daemon && this.nthreads == 0 && this.nUnstartedThreads == 0 && this.ngroups == 0)
        destroy(); 
    } 
  }
  
  private void remove(Thread paramThread) {
    synchronized (this) {
      if (this.destroyed)
        return; 
      for (int i = 0; i < this.nthreads; i++) {
        if (this.threads[i] == paramThread) {
          System.arraycopy(this.threads, i + true, this.threads, i, --this.nthreads - i);
          this.threads[this.nthreads] = null;
          break;
        } 
      } 
    } 
  }
  
  public void list() { list(System.out, 0); }
  
  void list(PrintStream paramPrintStream, int paramInt) {
    Object object;
    int i;
    synchronized (this) {
      byte b1;
      for (b1 = 0; b1 < paramInt; b1++)
        paramPrintStream.print(" "); 
      paramPrintStream.println(this);
      paramInt += 4;
      for (b1 = 0; b1 < this.nthreads; b1++) {
        for (byte b2 = 0; b2 < paramInt; b2++)
          paramPrintStream.print(" "); 
        paramPrintStream.println(this.threads[b1]);
      } 
      i = this.ngroups;
      if (this.groups != null) {
        object = (ThreadGroup[])Arrays.copyOf(this.groups, i);
      } else {
        object = null;
      } 
    } 
    for (byte b = 0; b < i; b++)
      object[b].list(paramPrintStream, paramInt); 
  }
  
  public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
    if (this.parent != null) {
      this.parent.uncaughtException(paramThread, paramThrowable);
    } else {
      Thread.UncaughtExceptionHandler uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
      if (uncaughtExceptionHandler != null) {
        uncaughtExceptionHandler.uncaughtException(paramThread, paramThrowable);
      } else if (!(paramThrowable instanceof ThreadDeath)) {
        System.err.print("Exception in thread \"" + paramThread.getName() + "\" ");
        paramThrowable.printStackTrace(System.err);
      } 
    } 
  }
  
  @Deprecated
  public boolean allowThreadSuspension(boolean paramBoolean) {
    this.vmAllowSuspension = paramBoolean;
    if (!paramBoolean)
      VM.unsuspendSomeThreads(); 
    return true;
  }
  
  public String toString() { return getClass().getName() + "[name=" + getName() + ",maxpri=" + this.maxPriority + "]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\ThreadGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */