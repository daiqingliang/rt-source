package java.util.concurrent;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import sun.misc.Contended;
import sun.misc.Unsafe;

@Contended
public class ForkJoinPool extends AbstractExecutorService {
  static final int SMASK = 65535;
  
  static final int MAX_CAP = 32767;
  
  static final int EVENMASK = 65534;
  
  static final int SQMASK = 126;
  
  static final int SCANNING = 1;
  
  static final int INACTIVE = -2147483648;
  
  static final int SS_SEQ = 65536;
  
  static final int MODE_MASK = -65536;
  
  static final int LIFO_QUEUE = 0;
  
  static final int FIFO_QUEUE = 65536;
  
  static final int SHARED_QUEUE = -2147483648;
  
  public static final ForkJoinWorkerThreadFactory defaultForkJoinWorkerThreadFactory;
  
  private static final RuntimePermission modifyThreadPermission;
  
  static final ForkJoinPool common;
  
  static final int commonParallelism;
  
  private static int commonMaxSpares;
  
  private static int poolNumberSequence;
  
  private static final long IDLE_TIMEOUT = 2000000000L;
  
  private static final long TIMEOUT_SLOP = 20000000L;
  
  private static final int DEFAULT_COMMON_MAX_SPARES = 256;
  
  private static final int SPINS = 0;
  
  private static final int SEED_INCREMENT = -1640531527;
  
  private static final long SP_MASK = 4294967295L;
  
  private static final long UC_MASK = -4294967296L;
  
  private static final int AC_SHIFT = 48;
  
  private static final long AC_UNIT = 281474976710656L;
  
  private static final long AC_MASK = -281474976710656L;
  
  private static final int TC_SHIFT = 32;
  
  private static final long TC_UNIT = 4294967296L;
  
  private static final long TC_MASK = 281470681743360L;
  
  private static final long ADD_WORKER = 140737488355328L;
  
  private static final int RSLOCK = 1;
  
  private static final int RSIGNAL = 2;
  
  private static final int STARTED = 4;
  
  private static final int STOP = 536870912;
  
  private static final int TERMINATED = 1073741824;
  
  private static final int SHUTDOWN = -2147483648;
  
  final int config;
  
  int indexSeed;
  
  final ForkJoinWorkerThreadFactory factory;
  
  final Thread.UncaughtExceptionHandler ueh;
  
  final String workerNamePrefix;
  
  private static final Unsafe U;
  
  private static final int ABASE;
  
  private static final int ASHIFT;
  
  private static final long CTL;
  
  private static final long RUNSTATE;
  
  private static final long STEALCOUNTER;
  
  private static final long PARKBLOCKER;
  
  private static final long QTOP;
  
  private static final long QLOCK;
  
  private static final long QSCANSTATE;
  
  private static final long QPARKER;
  
  private static final long QCURRENTSTEAL;
  
  private static final long QCURRENTJOIN;
  
  private static void checkPermission() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(modifyThreadPermission); 
  }
  
  private static final int nextPoolId() { return ++poolNumberSequence; }
  
  private int lockRunState() {
    int i;
    return (((i = this.runState) & true) != 0 || !U.compareAndSwapInt(this, RUNSTATE, i, i |= 0x1)) ? awaitRunStateLock() : i;
  }
  
  private int awaitRunStateLock() {
    boolean bool = false;
    byte b = 0;
    int i = 0;
    while (true) {
      int j;
      while (((j = this.runState) & true) == 0) {
        int k;
        if (U.compareAndSwapInt(this, RUNSTATE, j, k = j | true)) {
          if (bool)
            try {
              Thread.currentThread().interrupt();
            } catch (SecurityException securityException) {
              continue;
            }  
          return k;
        } 
      } 
      if (!i) {
        i = ThreadLocalRandom.nextSecondarySeed();
        continue;
      } 
      if (b) {
        i ^= i << 6;
        i ^= i >>> 21;
        i ^= i << 7;
        if (i >= 0)
          b--; 
        continue;
      } 
      AtomicLong atomicLong;
      if ((j & 0x4) == 0 || (atomicLong = this.stealCounter) == null) {
        Thread.yield();
        continue;
      } 
      if (U.compareAndSwapInt(this, RUNSTATE, j, j | 0x2))
        synchronized (atomicLong) {
          if ((this.runState & 0x2) != 0) {
            try {
              atomicLong.wait();
            } catch (InterruptedException interruptedException) {
              if (!(Thread.currentThread() instanceof ForkJoinWorkerThread))
                bool = true; 
            } 
          } else {
            atomicLong.notifyAll();
          } 
        }  
    } 
  }
  
  private void unlockRunState(int paramInt1, int paramInt2) {
    if (!U.compareAndSwapInt(this, RUNSTATE, paramInt1, paramInt2)) {
      AtomicLong atomicLong = this.stealCounter;
      this.runState = paramInt2;
      if (atomicLong != null)
        synchronized (atomicLong) {
          atomicLong.notifyAll();
        }  
    } 
  }
  
  private boolean createWorker() {
    ForkJoinWorkerThreadFactory forkJoinWorkerThreadFactory = this.factory;
    Throwable throwable = null;
    ForkJoinWorkerThread forkJoinWorkerThread = null;
    try {
      if (forkJoinWorkerThreadFactory != null && (forkJoinWorkerThread = forkJoinWorkerThreadFactory.newThread(this)) != null) {
        forkJoinWorkerThread.start();
        return true;
      } 
    } catch (Throwable throwable1) {
      throwable = throwable1;
    } 
    deregisterWorker(forkJoinWorkerThread, throwable);
    return false;
  }
  
  private void tryAddWorker(long paramLong) {
    boolean bool = false;
    do {
      long l = 0xFFFF000000000000L & paramLong + 281474976710656L | 0xFFFF00000000L & paramLong + 4294967296L;
      if (this.ctl != paramLong)
        continue; 
      int i;
      int j;
      if ((j = (i = lockRunState()) & 0x20000000) == 0)
        bool = U.compareAndSwapLong(this, CTL, paramLong, l); 
      unlockRunState(i, i & 0xFFFFFFFE);
      if (j != 0)
        break; 
      if (bool) {
        createWorker();
        break;
      } 
    } while (((paramLong = this.ctl) & 0x800000000000L) != 0L && (int)paramLong == 0);
  }
  
  final WorkQueue registerWorker(ForkJoinWorkerThread paramForkJoinWorkerThread) {
    paramForkJoinWorkerThread.setDaemon(true);
    Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
    if ((uncaughtExceptionHandler = this.ueh) != null)
      paramForkJoinWorkerThread.setUncaughtExceptionHandler(uncaughtExceptionHandler); 
    WorkQueue workQueue = new WorkQueue(this, paramForkJoinWorkerThread);
    int i = 0;
    int j = this.config & 0xFFFF0000;
    k = lockRunState();
    try {
      WorkQueue[] arrayOfWorkQueue;
      int m;
      if ((arrayOfWorkQueue = this.workQueues) != null && (m = arrayOfWorkQueue.length) > 0) {
        WorkQueue[] arrayOfWorkQueue1;
        int n = this.indexSeed += -1640531527;
        int i1 = m - 1;
        i = (n << 1 | true) & i1;
        if (arrayOfWorkQueue[i] != null) {
          byte b = 0;
          int i2 = (m <= 4) ? 2 : ((m >>> 1 & 0xFFFE) + 2);
          while (arrayOfWorkQueue[i = i + i2 & i1] != null) {
            if (++b >= m) {
              this.workQueues = arrayOfWorkQueue1 = (WorkQueue[])Arrays.copyOf(arrayOfWorkQueue, m <<= 1);
              i1 = m - 1;
              b = 0;
            } 
          } 
        } 
        workQueue.hint = n;
        workQueue.config = i | j;
        workQueue.scanState = i;
        arrayOfWorkQueue1[i] = workQueue;
      } 
    } finally {
      unlockRunState(k, k & 0xFFFFFFFE);
    } 
    paramForkJoinWorkerThread.setName(this.workerNamePrefix.concat(Integer.toString(i >>> 1)));
    return workQueue;
  }
  
  final void deregisterWorker(ForkJoinWorkerThread paramForkJoinWorkerThread, Throwable paramThrowable) {
    WorkQueue workQueue = null;
    if (paramForkJoinWorkerThread != null && (workQueue = paramForkJoinWorkerThread.workQueue) != null) {
      int j = workQueue.config & 0xFFFF;
      int k = lockRunState();
      WorkQueue[] arrayOfWorkQueue1;
      if ((arrayOfWorkQueue1 = this.workQueues) != null && arrayOfWorkQueue1.length > j && arrayOfWorkQueue1[j] == workQueue)
        arrayOfWorkQueue1[j] = null; 
      unlockRunState(k, k & 0xFFFFFFFE);
    } 
    long l;
    do {
    
    } while (!U.compareAndSwapLong(this, CTL, l = this.ctl, 0xFFFF000000000000L & l - 281474976710656L | 0xFFFF00000000L & l - 4294967296L | 0xFFFFFFFFL & l));
    if (workQueue != null) {
      workQueue.qlock = -1;
      workQueue.transferStealCount(this);
      workQueue.cancelAll();
    } 
    WorkQueue[] arrayOfWorkQueue;
    int i;
    while (!tryTerminate(false, false) && workQueue != null && workQueue.array != null && (this.runState & 0x20000000) == 0 && (arrayOfWorkQueue = this.workQueues) != null && (i = arrayOfWorkQueue.length - 1) >= 0) {
      int j;
      if ((j = (int)(l = this.ctl)) != 0) {
        if (tryRelease(l, arrayOfWorkQueue[j & i], 281474976710656L))
          break; 
        continue;
      } 
      if (paramThrowable != null && (l & 0x800000000000L) != 0L)
        tryAddWorker(l); 
      break;
    } 
    if (paramThrowable == null) {
      ForkJoinTask.helpExpungeStaleExceptions();
    } else {
      ForkJoinTask.rethrow(paramThrowable);
    } 
  }
  
  final void signalWork(WorkQueue[] paramArrayOfWorkQueue, WorkQueue paramWorkQueue) {
    long l;
    while ((l = this.ctl) < 0L) {
      int i;
      if ((i = (int)l) == 0) {
        if ((l & 0x800000000000L) != 0L)
          tryAddWorker(l); 
        break;
      } 
      int j;
      WorkQueue workQueue;
      if (paramArrayOfWorkQueue == null || paramArrayOfWorkQueue.length <= (j = i & 0xFFFF) || (workQueue = paramArrayOfWorkQueue[j]) == null)
        break; 
      int k = i + 65536 & 0x7FFFFFFF;
      int m = i - workQueue.scanState;
      long l1 = 0xFFFFFFFF00000000L & l + 281474976710656L | 0xFFFFFFFFL & workQueue.stackPred;
      if (m == 0 && U.compareAndSwapLong(this, CTL, l, l1)) {
        workQueue.scanState = k;
        Thread thread;
        if ((thread = workQueue.parker) != null)
          U.unpark(thread); 
        break;
      } 
      if (paramWorkQueue != null && paramWorkQueue.base == paramWorkQueue.top)
        break; 
    } 
  }
  
  private boolean tryRelease(long paramLong1, WorkQueue paramWorkQueue, long paramLong2) {
    int i = (int)paramLong1;
    int j = i + 65536 & 0x7FFFFFFF;
    if (paramWorkQueue != null && paramWorkQueue.scanState == i) {
      long l = 0xFFFFFFFF00000000L & paramLong1 + paramLong2 | 0xFFFFFFFFL & paramWorkQueue.stackPred;
      if (U.compareAndSwapLong(this, CTL, paramLong1, l)) {
        paramWorkQueue.scanState = j;
        Thread thread;
        if ((thread = paramWorkQueue.parker) != null)
          U.unpark(thread); 
        return true;
      } 
    } 
    return false;
  }
  
  final void runWorker(WorkQueue paramWorkQueue) {
    paramWorkQueue.growArray();
    int i = paramWorkQueue.hint;
    for (byte b = (i == 0) ? 1 : i;; b ^= b << 5) {
      ForkJoinTask forkJoinTask;
      if ((forkJoinTask = scan(paramWorkQueue, b)) != null) {
        paramWorkQueue.runTask(forkJoinTask);
      } else if (!awaitWork(paramWorkQueue, b)) {
        break;
      } 
      b ^= b << 13;
      b ^= b >>> 17;
    } 
  }
  
  private ForkJoinTask<?> scan(WorkQueue paramWorkQueue, int paramInt) {
    WorkQueue[] arrayOfWorkQueue;
    int i;
    if ((arrayOfWorkQueue = this.workQueues) != null && (i = arrayOfWorkQueue.length - 1) > 0 && paramWorkQueue != null) {
      int j = paramWorkQueue.scanState;
      int k = paramInt & i;
      int m = k;
      int n = 0;
      int i1 = 0;
      while (true) {
        WorkQueue workQueue;
        if ((workQueue = arrayOfWorkQueue[m]) != null) {
          ForkJoinTask[] arrayOfForkJoinTask;
          int i2;
          int i3;
          if ((i3 = (i2 = workQueue.base) - workQueue.top) < 0 && (arrayOfForkJoinTask = workQueue.array) != null) {
            long l = (((arrayOfForkJoinTask.length - 1 & i2) << ASHIFT) + ABASE);
            ForkJoinTask forkJoinTask;
            if ((forkJoinTask = (ForkJoinTask)U.getObjectVolatile(arrayOfForkJoinTask, l)) != null && workQueue.base == i2)
              if (j >= 0) {
                if (U.compareAndSwapObject(arrayOfForkJoinTask, l, forkJoinTask, null)) {
                  workQueue.base = i2 + 1;
                  if (i3 < -1)
                    signalWork(arrayOfWorkQueue, workQueue); 
                  return forkJoinTask;
                } 
              } else if (!n && paramWorkQueue.scanState < 0) {
                long l1;
                tryRelease(l1 = this.ctl, arrayOfWorkQueue[i & (int)l1], 281474976710656L);
              }  
            if (j < 0)
              j = paramWorkQueue.scanState; 
            paramInt ^= paramInt << 1;
            paramInt ^= paramInt >>> 3;
            paramInt ^= paramInt << 10;
            k = m = paramInt & i;
            n = i1 = 0;
            continue;
          } 
          i1 += i2;
        } 
        if ((m = m + 1 & i) == k) {
          if ((j >= 0 || j == (j = paramWorkQueue.scanState)) && n == (n = i1)) {
            if (j < 0 || paramWorkQueue.qlock < 0)
              break; 
            int i2 = j | 0x80000000;
            long l1;
            long l2 = 0xFFFFFFFFL & i2 | 0xFFFFFFFF00000000L & (l1 = this.ctl) - 281474976710656L;
            paramWorkQueue.stackPred = (int)l1;
            U.putInt(paramWorkQueue, QSCANSTATE, i2);
            if (U.compareAndSwapLong(this, CTL, l1, l2)) {
              j = i2;
            } else {
              paramWorkQueue.scanState = j;
            } 
          } 
          i1 = 0;
        } 
      } 
    } 
    return null;
  }
  
  private boolean awaitWork(WorkQueue paramWorkQueue, int paramInt) {
    if (paramWorkQueue == null || paramWorkQueue.qlock < 0)
      return false; 
    int i = paramWorkQueue.stackPred;
    byte b = 0;
    int j;
    while ((j = paramWorkQueue.scanState) < 0) {
      if (b) {
        paramInt ^= paramInt << 6;
        paramInt ^= paramInt >>> 21;
        paramInt ^= paramInt << 7;
        WorkQueue workQueue;
        WorkQueue[] arrayOfWorkQueue;
        int k;
        if (paramInt >= 0 && --b == 0 && i != 0 && (arrayOfWorkQueue = this.workQueues) != null && (k = i & 0xFFFF) < arrayOfWorkQueue.length && (workQueue = arrayOfWorkQueue[k]) != null && (workQueue.parker == null || workQueue.scanState >= 0))
          b = 0; 
        continue;
      } 
      if (paramWorkQueue.qlock < 0)
        return false; 
      if (!Thread.interrupted()) {
        long l4;
        long l3;
        long l2;
        long l1;
        int k = (int)((l1 = this.ctl) >> 48) + (this.config & 0xFFFF);
        if ((k <= 0 && tryTerminate(false, false)) || (this.runState & 0x20000000) != 0)
          return false; 
        if (k <= 0 && j == (int)l1) {
          l2 = 0xFFFFFFFF00000000L & l1 + 281474976710656L | 0xFFFFFFFFL & i;
          short s = (short)(int)(l1 >>> 32);
          if (s > 2 && U.compareAndSwapLong(this, CTL, l1, l2))
            return false; 
          l3 = 2000000000L * ((s >= 0) ? true : (1 - s));
          l4 = System.nanoTime() + l3 - 20000000L;
        } else {
          l2 = l3 = l4 = 0L;
        } 
        Thread thread = Thread.currentThread();
        U.putObject(thread, PARKBLOCKER, this);
        paramWorkQueue.parker = thread;
        if (paramWorkQueue.scanState < 0 && this.ctl == l1)
          U.park(false, l3); 
        U.putOrderedObject(paramWorkQueue, QPARKER, null);
        U.putObject(thread, PARKBLOCKER, null);
        if (paramWorkQueue.scanState >= 0)
          break; 
        if (l3 != 0L && this.ctl == l1 && l4 - System.nanoTime() <= 0L && U.compareAndSwapLong(this, CTL, l1, l2))
          return false; 
      } 
    } 
    return true;
  }
  
  final int helpComplete(WorkQueue paramWorkQueue, CountedCompleter<?> paramCountedCompleter, int paramInt) {
    int i = 0;
    WorkQueue[] arrayOfWorkQueue;
    int j;
    if ((arrayOfWorkQueue = this.workQueues) != null && (j = arrayOfWorkQueue.length - 1) >= 0 && paramCountedCompleter != null && paramWorkQueue != null) {
      int k = paramWorkQueue.config;
      int m = paramWorkQueue.hint ^ paramWorkQueue.top;
      int n = m & j;
      int i1 = 1;
      int i2 = n;
      int i3 = 0;
      int i4 = 0;
      while ((i = paramCountedCompleter.status) >= 0) {
        CountedCompleter countedCompleter;
        if (i1 == 1 && (countedCompleter = paramWorkQueue.popCC(paramCountedCompleter, k)) != null) {
          countedCompleter.doExec();
          if (paramInt != 0 && --paramInt == 0)
            break; 
          n = i2;
          i3 = i4 = 0;
          continue;
        } 
        WorkQueue workQueue;
        if ((workQueue = arrayOfWorkQueue[i2]) == null) {
          i1 = 0;
        } else if ((i1 = workQueue.pollAndExecCC(paramCountedCompleter)) < 0) {
          i4 += i1;
        } 
        if (i1 > 0) {
          if (i1 == 1 && paramInt != 0 && --paramInt == 0)
            break; 
          m ^= m << 13;
          m ^= m >>> 17;
          m ^= m << 5;
          n = i2 = m & j;
          i3 = i4 = 0;
          continue;
        } 
        if ((i2 = i2 + 1 & j) == n) {
          if (i3 == (i3 = i4))
            break; 
          i4 = 0;
        } 
      } 
    } 
    return i;
  }
  
  private void helpStealer(WorkQueue paramWorkQueue, ForkJoinTask<?> paramForkJoinTask) {
    WorkQueue[] arrayOfWorkQueue = this.workQueues;
    int i = 0;
    int j;
    if (arrayOfWorkQueue != null && (j = arrayOfWorkQueue.length - 1) >= 0 && paramWorkQueue != null && paramForkJoinTask != null) {
      int k;
      do {
        k = 0;
        WorkQueue workQueue = paramWorkQueue;
        ForkJoinTask<?> forkJoinTask = paramForkJoinTask;
        label57: while (forkJoinTask.status >= 0) {
          int m = workQueue.hint | true;
          for (int n = 0; n <= j; n += 2) {
            WorkQueue workQueue1;
            int i1;
            if ((workQueue1 = arrayOfWorkQueue[i1 = m + n & j]) != null) {
              if (workQueue1.currentSteal == forkJoinTask) {
                workQueue.hint = i1;
                continue label57;
              } 
              k += workQueue1.base;
            } 
          } 
        } 
      } while (paramForkJoinTask.status >= 0 && i != (i = k));
    } 
  }
  
  private boolean tryCompensate(WorkQueue paramWorkQueue) {
    boolean bool;
    WorkQueue[] arrayOfWorkQueue;
    int i;
    int j;
    if (paramWorkQueue == null || paramWorkQueue.qlock < 0 || (arrayOfWorkQueue = this.workQueues) == null || (i = arrayOfWorkQueue.length - 1) <= 0 || (j = this.config & 0xFFFF) == 0) {
      bool = false;
    } else {
      long l;
      int k;
      if ((k = (int)(l = this.ctl)) != 0) {
        bool = tryRelease(l, arrayOfWorkQueue[k & i], 0L);
      } else {
        int m = (int)(l >> 48) + j;
        short s = (short)(int)(l >> 32) + j;
        byte b1 = 0;
        byte b2;
        for (b2 = 0; b2 <= i; b2++) {
          WorkQueue workQueue;
          if ((workQueue = arrayOfWorkQueue[(b2 << true | true) & i]) != null) {
            if ((workQueue.scanState & true) != 0)
              break; 
            b1++;
          } 
        } 
        if (b1 != s << 1 || this.ctl != l) {
          bool = false;
        } else if (s >= j && m > 1 && paramWorkQueue.isEmpty()) {
          long l1 = 0xFFFF000000000000L & l - 281474976710656L | 0xFFFFFFFFFFFFL & l;
          bool = U.compareAndSwapLong(this, CTL, l, l1);
        } else {
          boolean bool1;
          if (s >= Short.MAX_VALUE || (this == common && s >= j + commonMaxSpares))
            throw new RejectedExecutionException("Thread limit exceeded replacing blocked worker"); 
          b2 = 0;
          long l1 = 0xFFFF000000000000L & l | 0xFFFF00000000L & l + 4294967296L;
          int n;
          if (((n = lockRunState()) & 0x20000000) == 0)
            bool1 = U.compareAndSwapLong(this, CTL, l, l1); 
          unlockRunState(n, n & 0xFFFFFFFE);
          bool = (bool1 && createWorker());
        } 
      } 
    } 
    return bool;
  }
  
  final int awaitJoin(WorkQueue paramWorkQueue, ForkJoinTask<?> paramForkJoinTask, long paramLong) {
    int i = 0;
    if (paramForkJoinTask != null && paramWorkQueue != null) {
      ForkJoinTask forkJoinTask = paramWorkQueue.currentJoin;
      U.putOrderedObject(paramWorkQueue, QCURRENTJOIN, paramForkJoinTask);
      CountedCompleter countedCompleter = (paramForkJoinTask instanceof CountedCompleter) ? (CountedCompleter)paramForkJoinTask : null;
      while ((i = paramForkJoinTask.status) >= 0) {
        long l;
        if (countedCompleter != null) {
          helpComplete(paramWorkQueue, countedCompleter, 0);
        } else if (paramWorkQueue.base == paramWorkQueue.top || paramWorkQueue.tryRemoveAndExec(paramForkJoinTask)) {
          helpStealer(paramWorkQueue, paramForkJoinTask);
        } 
        if ((i = paramForkJoinTask.status) < 0)
          break; 
        if (paramLong == 0L) {
          l = 0L;
        } else {
          long l1;
          if ((l1 = paramLong - System.nanoTime()) <= 0L)
            break; 
          if ((l = TimeUnit.NANOSECONDS.toMillis(l1)) <= 0L)
            l = 1L; 
        } 
        if (tryCompensate(paramWorkQueue)) {
          paramForkJoinTask.internalWait(l);
          U.getAndAddLong(this, CTL, 281474976710656L);
        } 
      } 
      U.putOrderedObject(paramWorkQueue, QCURRENTJOIN, forkJoinTask);
    } 
    return i;
  }
  
  private WorkQueue findNonEmptyStealQueue() {
    int j = ThreadLocalRandom.nextSecondarySeed();
    WorkQueue[] arrayOfWorkQueue;
    int i;
    if ((arrayOfWorkQueue = this.workQueues) != null && (i = arrayOfWorkQueue.length - 1) >= 0) {
      int k = j & i;
      int m = k;
      int n = 0;
      int i1 = 0;
      while (true) {
        WorkQueue workQueue;
        if ((workQueue = arrayOfWorkQueue[m]) != null) {
          int i2;
          if ((i2 = workQueue.base) - workQueue.top < 0)
            return workQueue; 
          i1 += i2;
        } 
        if ((m = m + 1 & i) == k) {
          if (n == (n = i1))
            break; 
          i1 = 0;
        } 
      } 
    } 
    return null;
  }
  
  final void helpQuiescePool(WorkQueue paramWorkQueue) {
    ForkJoinTask forkJoinTask = paramWorkQueue.currentSteal;
    boolean bool = true;
    while (true) {
      paramWorkQueue.execLocalTasks();
      WorkQueue workQueue;
      while ((workQueue = findNonEmptyStealQueue()) != null) {
        if (!bool) {
          bool = true;
          U.getAndAddLong(this, CTL, 281474976710656L);
        } 
        U.putOrderedObject(paramWorkQueue, QCURRENTSTEAL, forkJoinTask1);
        forkJoinTask1.doExec();
        ForkJoinTask forkJoinTask1;
        int i;
        if ((i = workQueue.base) - workQueue.top < 0 && (forkJoinTask1 = workQueue.pollAt(i)) != null && ++paramWorkQueue.nsteals < 0)
          paramWorkQueue.transferStealCount(this); 
      } 
      if (bool) {
        long l1;
        long l2 = 0xFFFF000000000000L & (l1 = this.ctl) - 281474976710656L | 0xFFFFFFFFFFFFL & l1;
        if ((int)(l2 >> 48) + (this.config & 0xFFFF) <= 0)
          break; 
        if (U.compareAndSwapLong(this, CTL, l1, l2))
          bool = false; 
        continue;
      } 
      long l;
      if ((int)((l = this.ctl) >> 48) + (this.config & 0xFFFF) <= 0 && U.compareAndSwapLong(this, CTL, l, l + 281474976710656L))
        break; 
    } 
    U.putOrderedObject(paramWorkQueue, QCURRENTSTEAL, forkJoinTask);
  }
  
  final ForkJoinTask<?> nextTaskFor(WorkQueue paramWorkQueue) {
    WorkQueue workQueue;
    ForkJoinTask forkJoinTask;
    int i;
    do {
      ForkJoinTask forkJoinTask1;
      if ((forkJoinTask1 = paramWorkQueue.nextLocalTask()) != null)
        return forkJoinTask1; 
      if ((workQueue = findNonEmptyStealQueue()) == null)
        return null; 
    } while ((i = workQueue.base) - workQueue.top >= 0 || (forkJoinTask = workQueue.pollAt(i)) == null);
    return forkJoinTask;
  }
  
  static int getSurplusQueuedTaskCount() {
    Thread thread;
    if (thread = Thread.currentThread() instanceof ForkJoinWorkerThread) {
      ForkJoinWorkerThread forkJoinWorkerThread;
      ForkJoinPool forkJoinPool;
      int i = (forkJoinPool = (forkJoinWorkerThread = (ForkJoinWorkerThread)thread).pool).config & 0xFFFF;
      WorkQueue workQueue;
      int j = (workQueue = forkJoinWorkerThread.workQueue).top - workQueue.base;
      int k = (int)(forkJoinPool.ctl >> 48) + i;
      return j - ((k > i >>>= 1) ? 0 : ((k > i >>>= 1) ? 1 : ((k > i >>>= 1) ? 2 : ((k > i >>>= 1) ? 4 : 8))));
    } 
    return 0;
  }
  
  private boolean tryTerminate(boolean paramBoolean1, boolean paramBoolean2) {
    if (this == common)
      return false; 
    int i;
    if ((i = this.runState) >= 0) {
      if (!paramBoolean2)
        return false; 
      i = lockRunState();
      unlockRunState(i, i & 0xFFFFFFFE | 0x80000000);
    } 
    if ((i & 0x20000000) == 0) {
      if (!paramBoolean1) {
        long l2;
        long l1 = 0L;
        do {
          l2 = this.ctl;
          if ((int)(l2 >> 48) + (this.config & 0xFFFF) > 0)
            return false; 
          WorkQueue[] arrayOfWorkQueue;
          int j;
          if ((arrayOfWorkQueue = this.workQueues) == null || (j = arrayOfWorkQueue.length - 1) <= 0)
            break; 
          for (byte b1 = 0; b1 <= j; b1++) {
            WorkQueue workQueue;
            if ((workQueue = arrayOfWorkQueue[b1]) != null) {
              int k;
              if ((k = workQueue.base) != workQueue.top || workQueue.scanState >= 0 || workQueue.currentSteal != null) {
                long l3;
                tryRelease(l3 = this.ctl, arrayOfWorkQueue[j & (int)l3], 281474976710656L);
                return false;
              } 
              l2 += k;
              if (!(b1 & true))
                workQueue.qlock = -1; 
            } 
          } 
        } while (l1 != (l1 = l2));
      } 
      if ((this.runState & 0x20000000) == 0) {
        i = lockRunState();
        unlockRunState(i, i & 0xFFFFFFFE | 0x20000000);
      } 
    } 
    byte b = 0;
    long l = 0L;
    while (true) {
      long l1 = this.ctl;
      WorkQueue[] arrayOfWorkQueue;
      int j;
      if ((short)(int)(l1 >>> 32) + (this.config & 0xFFFF) <= 0 || (arrayOfWorkQueue = this.workQueues) == null || (j = arrayOfWorkQueue.length - 1) <= 0) {
        if ((this.runState & 0x40000000) == 0) {
          i = lockRunState();
          unlockRunState(i, i & 0xFFFFFFFE | 0x40000000);
          synchronized (this) {
            notifyAll();
            break;
          } 
        } 
        break;
      } 
      for (byte b1 = 0; b1 <= j; b1++) {
        WorkQueue workQueue;
        if ((workQueue = arrayOfWorkQueue[b1]) != null) {
          l1 += workQueue.base;
          workQueue.qlock = -1;
          if (b) {
            workQueue.cancelAll();
            ForkJoinWorkerThread forkJoinWorkerThread;
            if (b > 1 && (forkJoinWorkerThread = workQueue.owner) != null) {
              if (!forkJoinWorkerThread.isInterrupted())
                try {
                  forkJoinWorkerThread.interrupt();
                } catch (Throwable throwable) {} 
              if (workQueue.scanState < 0)
                U.unpark(forkJoinWorkerThread); 
            } 
          } 
        } 
      } 
      if (l1 != l) {
        l = l1;
        b = 0;
        continue;
      } 
      if (b > 3 && b > j)
        break; 
      if (++b > 1) {
        byte b2 = 0;
        long l2;
        int k;
        while (b2++ <= j && (k = (int)(l2 = this.ctl)) != 0)
          tryRelease(l2, arrayOfWorkQueue[k & j], 281474976710656L); 
      } 
    } 
    return true;
  }
  
  private void externalSubmit(ForkJoinTask<?> paramForkJoinTask) {
    int i;
    if ((i = ThreadLocalRandom.getProbe()) == 0) {
      ThreadLocalRandom.localInit();
      i = ThreadLocalRandom.getProbe();
    } 
    while (true) {
      boolean bool = false;
      if ((j = this.runState) < 0) {
        tryTerminate(false, false);
        throw new RejectedExecutionException();
      } 
      WorkQueue[] arrayOfWorkQueue;
      int k;
      if ((j & 0x4) == 0 || (arrayOfWorkQueue = this.workQueues) == null || (k = arrayOfWorkQueue.length - 1) < 0) {
        m = 0;
        j = lockRunState();
        try {
          if ((j & 0x4) == 0) {
            U.compareAndSwapObject(this, STEALCOUNTER, null, new AtomicLong());
            int n = this.config & 0xFFFF;
            int i1 = (n > 1) ? (n - 1) : 1;
            i1 |= i1 >>> 1;
            i1 |= i1 >>> 2;
            i1 |= i1 >>> 4;
            i1 |= i1 >>> 8;
            i1 |= i1 >>> 16;
            i1 = i1 + 1 << 1;
            this.workQueues = new WorkQueue[i1];
            m = 4;
          } 
        } finally {
          unlockRunState(j, j & 0xFFFFFFFE | m);
        } 
      } else {
        int m;
        if ((workQueue = arrayOfWorkQueue[m = i & k & 0x7E]) != null) {
          if (workQueue.qlock == 0 && U.compareAndSwapInt(workQueue, QLOCK, 0, 1)) {
            ForkJoinTask[] arrayOfForkJoinTask = workQueue.array;
            int n = workQueue.top;
            boolean bool1 = false;
            try {
              if ((arrayOfForkJoinTask != null && arrayOfForkJoinTask.length > n + 1 - workQueue.base) || (arrayOfForkJoinTask = workQueue.growArray()) != null) {
                int i1 = ((arrayOfForkJoinTask.length - 1 & n) << ASHIFT) + ABASE;
                U.putOrderedObject(arrayOfForkJoinTask, i1, paramForkJoinTask);
                U.putOrderedInt(workQueue, QTOP, n + 1);
                bool1 = true;
              } 
            } finally {
              U.compareAndSwapInt(workQueue, QLOCK, 1, 0);
            } 
            if (bool1) {
              signalWork(arrayOfWorkQueue, workQueue);
              return;
            } 
          } 
          bool = true;
        } else if (((j = this.runState) & true) == 0) {
          workQueue = new WorkQueue(this, null);
          workQueue.hint = i;
          workQueue.config = m | 0x80000000;
          workQueue.scanState = Integer.MIN_VALUE;
          j = lockRunState();
          if (j > 0 && (arrayOfWorkQueue = this.workQueues) != null && m < arrayOfWorkQueue.length && arrayOfWorkQueue[m] == null)
            arrayOfWorkQueue[m] = workQueue; 
          unlockRunState(j, j & 0xFFFFFFFE);
        } else {
          bool = true;
        } 
      } 
      if (bool)
        i = ThreadLocalRandom.advanceProbe(i); 
    } 
  }
  
  final void externalPush(ForkJoinTask<?> paramForkJoinTask) {
    int j = ThreadLocalRandom.getProbe();
    int k = this.runState;
    WorkQueue[] arrayOfWorkQueue;
    WorkQueue workQueue;
    int i;
    if ((arrayOfWorkQueue = this.workQueues) != null && (i = arrayOfWorkQueue.length - 1) >= 0 && (workQueue = arrayOfWorkQueue[i & j & 0x7E]) != null && j != 0 && k > 0 && U.compareAndSwapInt(workQueue, QLOCK, 0, 1)) {
      ForkJoinTask[] arrayOfForkJoinTask;
      int m;
      int n;
      int i1;
      if ((arrayOfForkJoinTask = workQueue.array) != null && (m = arrayOfForkJoinTask.length - 1) > (n = (i1 = workQueue.top) - workQueue.base)) {
        int i2 = ((m & i1) << ASHIFT) + ABASE;
        U.putOrderedObject(arrayOfForkJoinTask, i2, paramForkJoinTask);
        U.putOrderedInt(workQueue, QTOP, i1 + 1);
        U.putIntVolatile(workQueue, QLOCK, 0);
        if (n <= 1)
          signalWork(arrayOfWorkQueue, workQueue); 
        return;
      } 
      U.compareAndSwapInt(workQueue, QLOCK, 1, 0);
    } 
    externalSubmit(paramForkJoinTask);
  }
  
  static WorkQueue commonSubmitterQueue() {
    ForkJoinPool forkJoinPool = common;
    int i = ThreadLocalRandom.getProbe();
    WorkQueue[] arrayOfWorkQueue;
    int j;
    return (forkJoinPool != null && (arrayOfWorkQueue = forkJoinPool.workQueues) != null && (j = arrayOfWorkQueue.length - 1) >= 0) ? arrayOfWorkQueue[j & i & 0x7E] : null;
  }
  
  final boolean tryExternalUnpush(ForkJoinTask<?> paramForkJoinTask) {
    int k = ThreadLocalRandom.getProbe();
    WorkQueue[] arrayOfWorkQueue;
    WorkQueue workQueue;
    ForkJoinTask[] arrayOfForkJoinTask;
    int i;
    int j;
    if ((arrayOfWorkQueue = this.workQueues) != null && (i = arrayOfWorkQueue.length - 1) >= 0 && (workQueue = arrayOfWorkQueue[i & k & 0x7E]) != null && (arrayOfForkJoinTask = workQueue.array) != null && (j = workQueue.top) != workQueue.base) {
      long l = (((arrayOfForkJoinTask.length - 1 & j - 1) << ASHIFT) + ABASE);
      if (U.compareAndSwapInt(workQueue, QLOCK, 0, 1)) {
        if (workQueue.top == j && workQueue.array == arrayOfForkJoinTask && U.getObject(arrayOfForkJoinTask, l) == paramForkJoinTask && U.compareAndSwapObject(arrayOfForkJoinTask, l, paramForkJoinTask, null)) {
          U.putOrderedInt(workQueue, QTOP, j - 1);
          U.putOrderedInt(workQueue, QLOCK, 0);
          return true;
        } 
        U.compareAndSwapInt(workQueue, QLOCK, 1, 0);
      } 
    } 
    return false;
  }
  
  final int externalHelpComplete(CountedCompleter<?> paramCountedCompleter, int paramInt) {
    int j = ThreadLocalRandom.getProbe();
    WorkQueue[] arrayOfWorkQueue;
    int i;
    return ((arrayOfWorkQueue = this.workQueues) == null || (i = arrayOfWorkQueue.length) == 0) ? 0 : helpComplete(arrayOfWorkQueue[i - 1 & j & 0x7E], paramCountedCompleter, paramInt);
  }
  
  public ForkJoinPool() { this(Math.min(32767, Runtime.getRuntime().availableProcessors()), defaultForkJoinWorkerThreadFactory, null, false); }
  
  public ForkJoinPool(int paramInt) { this(paramInt, defaultForkJoinWorkerThreadFactory, null, false); }
  
  public ForkJoinPool(int paramInt, ForkJoinWorkerThreadFactory paramForkJoinWorkerThreadFactory, Thread.UncaughtExceptionHandler paramUncaughtExceptionHandler, boolean paramBoolean) {
    this(checkParallelism(paramInt), checkFactory(paramForkJoinWorkerThreadFactory), paramUncaughtExceptionHandler, paramBoolean ? 65536 : 0, "ForkJoinPool-" + nextPoolId() + "-worker-");
    checkPermission();
  }
  
  private static int checkParallelism(int paramInt) {
    if (paramInt <= 0 || paramInt > 32767)
      throw new IllegalArgumentException(); 
    return paramInt;
  }
  
  private static ForkJoinWorkerThreadFactory checkFactory(ForkJoinWorkerThreadFactory paramForkJoinWorkerThreadFactory) {
    if (paramForkJoinWorkerThreadFactory == null)
      throw new NullPointerException(); 
    return paramForkJoinWorkerThreadFactory;
  }
  
  private ForkJoinPool(int paramInt1, ForkJoinWorkerThreadFactory paramForkJoinWorkerThreadFactory, Thread.UncaughtExceptionHandler paramUncaughtExceptionHandler, int paramInt2, String paramString) {
    this.workerNamePrefix = paramString;
    this.factory = paramForkJoinWorkerThreadFactory;
    this.ueh = paramUncaughtExceptionHandler;
    this.config = paramInt1 & 0xFFFF | paramInt2;
    long l = -paramInt1;
    this.ctl = l << 48 & 0xFFFF000000000000L | l << 32 & 0xFFFF00000000L;
  }
  
  public static ForkJoinPool commonPool() { return common; }
  
  public <T> T invoke(ForkJoinTask<T> paramForkJoinTask) {
    if (paramForkJoinTask == null)
      throw new NullPointerException(); 
    externalPush(paramForkJoinTask);
    return (T)paramForkJoinTask.join();
  }
  
  public void execute(ForkJoinTask<?> paramForkJoinTask) {
    if (paramForkJoinTask == null)
      throw new NullPointerException(); 
    externalPush(paramForkJoinTask);
  }
  
  public void execute(Runnable paramRunnable) {
    ForkJoinTask.RunnableExecuteAction runnableExecuteAction;
    if (paramRunnable == null)
      throw new NullPointerException(); 
    if (paramRunnable instanceof ForkJoinTask) {
      runnableExecuteAction = (ForkJoinTask)paramRunnable;
    } else {
      runnableExecuteAction = new ForkJoinTask.RunnableExecuteAction(paramRunnable);
    } 
    externalPush(runnableExecuteAction);
  }
  
  public <T> ForkJoinTask<T> submit(ForkJoinTask<T> paramForkJoinTask) {
    if (paramForkJoinTask == null)
      throw new NullPointerException(); 
    externalPush(paramForkJoinTask);
    return paramForkJoinTask;
  }
  
  public <T> ForkJoinTask<T> submit(Callable<T> paramCallable) {
    ForkJoinTask.AdaptedCallable adaptedCallable = new ForkJoinTask.AdaptedCallable(paramCallable);
    externalPush(adaptedCallable);
    return adaptedCallable;
  }
  
  public <T> ForkJoinTask<T> submit(Runnable paramRunnable, T paramT) {
    ForkJoinTask.AdaptedRunnable adaptedRunnable = new ForkJoinTask.AdaptedRunnable(paramRunnable, paramT);
    externalPush(adaptedRunnable);
    return adaptedRunnable;
  }
  
  public ForkJoinTask<?> submit(Runnable paramRunnable) {
    ForkJoinTask.AdaptedRunnableAction adaptedRunnableAction;
    if (paramRunnable == null)
      throw new NullPointerException(); 
    if (paramRunnable instanceof ForkJoinTask) {
      adaptedRunnableAction = (ForkJoinTask)paramRunnable;
    } else {
      adaptedRunnableAction = new ForkJoinTask.AdaptedRunnableAction(paramRunnable);
    } 
    externalPush(adaptedRunnableAction);
    return adaptedRunnableAction;
  }
  
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> paramCollection) {
    arrayList = new ArrayList(paramCollection.size());
    bool = false;
    try {
      for (Callable callable : paramCollection) {
        ForkJoinTask.AdaptedCallable adaptedCallable = new ForkJoinTask.AdaptedCallable(callable);
        arrayList.add(adaptedCallable);
        externalPush(adaptedCallable);
      } 
      byte b = 0;
      i = arrayList.size();
      while (b < i) {
        ((ForkJoinTask)arrayList.get(b)).quietlyJoin();
        b++;
      } 
      bool = true;
      return arrayList;
    } finally {
      if (!bool) {
        byte b = 0;
        int i = arrayList.size();
        while (b < i) {
          ((Future)arrayList.get(b)).cancel(false);
          b++;
        } 
      } 
    } 
  }
  
  public ForkJoinWorkerThreadFactory getFactory() { return this.factory; }
  
  public Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() { return this.ueh; }
  
  public int getParallelism() {
    int i;
    return ((i = this.config & 0xFFFF) > 0) ? i : 1;
  }
  
  public static int getCommonPoolParallelism() { return commonParallelism; }
  
  public int getPoolSize() { return (this.config & 0xFFFF) + (short)(int)(this.ctl >>> 32); }
  
  public boolean getAsyncMode() { return ((this.config & 0x10000) != 0); }
  
  public int getRunningThreadCount() {
    byte b = 0;
    WorkQueue[] arrayOfWorkQueue;
    if ((arrayOfWorkQueue = this.workQueues) != null)
      for (boolean bool = true; bool < arrayOfWorkQueue.length; bool += true) {
        WorkQueue workQueue;
        if ((workQueue = arrayOfWorkQueue[bool]) != null && workQueue.isApparentlyUnblocked())
          b++; 
      }  
    return b;
  }
  
  public int getActiveThreadCount() {
    int i = (this.config & 0xFFFF) + (int)(this.ctl >> 48);
    return (i <= 0) ? 0 : i;
  }
  
  public boolean isQuiescent() { return ((this.config & 0xFFFF) + (int)(this.ctl >> 48) <= 0); }
  
  public long getStealCount() {
    AtomicLong atomicLong = this.stealCounter;
    long l = (atomicLong == null) ? 0L : atomicLong.get();
    WorkQueue[] arrayOfWorkQueue;
    if ((arrayOfWorkQueue = this.workQueues) != null)
      for (boolean bool = true; bool < arrayOfWorkQueue.length; bool += true) {
        WorkQueue workQueue;
        if ((workQueue = arrayOfWorkQueue[bool]) != null)
          l += workQueue.nsteals; 
      }  
    return l;
  }
  
  public long getQueuedTaskCount() {
    long l = 0L;
    WorkQueue[] arrayOfWorkQueue;
    if ((arrayOfWorkQueue = this.workQueues) != null)
      for (boolean bool = true; bool < arrayOfWorkQueue.length; bool += true) {
        WorkQueue workQueue;
        if ((workQueue = arrayOfWorkQueue[bool]) != null)
          l += workQueue.queueSize(); 
      }  
    return l;
  }
  
  public int getQueuedSubmissionCount() {
    int i = 0;
    WorkQueue[] arrayOfWorkQueue;
    if ((arrayOfWorkQueue = this.workQueues) != null)
      for (boolean bool = false; bool < arrayOfWorkQueue.length; bool += true) {
        WorkQueue workQueue;
        if ((workQueue = arrayOfWorkQueue[bool]) != null)
          i += workQueue.queueSize(); 
      }  
    return i;
  }
  
  public boolean hasQueuedSubmissions() {
    WorkQueue[] arrayOfWorkQueue;
    if ((arrayOfWorkQueue = this.workQueues) != null)
      for (boolean bool = false; bool < arrayOfWorkQueue.length; bool += true) {
        WorkQueue workQueue;
        if ((workQueue = arrayOfWorkQueue[bool]) != null && !workQueue.isEmpty())
          return true; 
      }  
    return false;
  }
  
  protected ForkJoinTask<?> pollSubmission() {
    WorkQueue[] arrayOfWorkQueue;
    if ((arrayOfWorkQueue = this.workQueues) != null)
      for (boolean bool = false; bool < arrayOfWorkQueue.length; bool += true) {
        WorkQueue workQueue;
        ForkJoinTask forkJoinTask;
        if ((workQueue = arrayOfWorkQueue[bool]) != null && (forkJoinTask = workQueue.poll()) != null)
          return forkJoinTask; 
      }  
    return null;
  }
  
  protected int drainTasksTo(Collection<? super ForkJoinTask<?>> paramCollection) {
    byte b = 0;
    WorkQueue[] arrayOfWorkQueue;
    if ((arrayOfWorkQueue = this.workQueues) != null)
      for (byte b1 = 0; b1 < arrayOfWorkQueue.length; b1++) {
        WorkQueue workQueue;
        if ((workQueue = arrayOfWorkQueue[b1]) != null) {
          ForkJoinTask forkJoinTask;
          while ((forkJoinTask = workQueue.poll()) != null) {
            paramCollection.add(forkJoinTask);
            b++;
          } 
        } 
      }  
    return b;
  }
  
  public String toString() {
    long l1 = 0L;
    long l2 = 0L;
    byte b = 0;
    AtomicLong atomicLong = this.stealCounter;
    long l3 = (atomicLong == null) ? 0L : atomicLong.get();
    long l4 = this.ctl;
    WorkQueue[] arrayOfWorkQueue;
    if ((arrayOfWorkQueue = this.workQueues) != null)
      for (byte b1 = 0; b1 < arrayOfWorkQueue.length; b1++) {
        WorkQueue workQueue;
        if ((workQueue = arrayOfWorkQueue[b1]) != null) {
          int n = workQueue.queueSize();
          if (!(b1 & true)) {
            l2 += n;
          } else {
            l1 += n;
            l3 += workQueue.nsteals;
            if (workQueue.isApparentlyUnblocked())
              b++; 
          } 
        } 
      }  
    int i = this.config & 0xFFFF;
    int j = i + (short)(int)(l4 >>> 32);
    int k = i + (int)(l4 >> 48);
    if (k < 0)
      k = 0; 
    int m = this.runState;
    String str = ((m & 0x40000000) != 0) ? "Terminated" : (((m & 0x20000000) != 0) ? "Terminating" : (((m & 0x80000000) != 0) ? "Shutting down" : "Running"));
    return super.toString() + "[" + str + ", parallelism = " + i + ", size = " + j + ", active = " + k + ", running = " + b + ", steals = " + l3 + ", tasks = " + l1 + ", submissions = " + l2 + "]";
  }
  
  public void shutdown() {
    checkPermission();
    tryTerminate(false, true);
  }
  
  public List<Runnable> shutdownNow() {
    checkPermission();
    tryTerminate(true, true);
    return Collections.emptyList();
  }
  
  public boolean isTerminated() { return ((this.runState & 0x40000000) != 0); }
  
  public boolean isTerminating() {
    int i = this.runState;
    return ((i & 0x20000000) != 0 && (i & 0x40000000) == 0);
  }
  
  public boolean isShutdown() { return ((this.runState & 0x80000000) != 0); }
  
  public boolean awaitTermination(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    if (Thread.interrupted())
      throw new InterruptedException(); 
    if (this == common) {
      awaitQuiescence(paramLong, paramTimeUnit);
      return false;
    } 
    long l1 = paramTimeUnit.toNanos(paramLong);
    if (isTerminated())
      return true; 
    if (l1 <= 0L)
      return false; 
    long l2 = System.nanoTime() + l1;
    synchronized (this) {
      while (true) {
        if (isTerminated())
          return true; 
        if (l1 <= 0L)
          return false; 
        long l = TimeUnit.NANOSECONDS.toMillis(l1);
        wait((l > 0L) ? l : 1L);
        l1 = l2 - System.nanoTime();
      } 
    } 
  }
  
  public boolean awaitQuiescence(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    long l1 = paramTimeUnit.toNanos(paramLong);
    Thread thread = Thread.currentThread();
    ForkJoinWorkerThread forkJoinWorkerThread;
    if (thread instanceof ForkJoinWorkerThread && (forkJoinWorkerThread = (ForkJoinWorkerThread)thread).pool == this) {
      helpQuiescePool(forkJoinWorkerThread.workQueue);
      return true;
    } 
    long l2 = System.nanoTime();
    byte b = 0;
    boolean bool = true;
    WorkQueue[] arrayOfWorkQueue;
    int i;
    while (!isQuiescent() && (arrayOfWorkQueue = this.workQueues) != null && (i = arrayOfWorkQueue.length - 1) >= 0) {
      if (!bool) {
        if (System.nanoTime() - l2 > l1)
          return false; 
        Thread.yield();
      } 
      bool = false;
      for (int j = i + 1 << 2; j >= 0; j--) {
        WorkQueue workQueue;
        int k;
        int m;
        if ((m = b++ & i) <= i && m >= 0 && (workQueue = arrayOfWorkQueue[m]) != null && (k = workQueue.base) - workQueue.top < 0) {
          bool = true;
          ForkJoinTask forkJoinTask;
          if ((forkJoinTask = workQueue.pollAt(k)) != null)
            forkJoinTask.doExec(); 
          break;
        } 
      } 
    } 
    return true;
  }
  
  static void quiesceCommonPool() { common.awaitQuiescence(Float.MAX_VALUE, TimeUnit.NANOSECONDS); }
  
  public static void managedBlock(ManagedBlocker paramManagedBlocker) throws InterruptedException {
    Thread thread = Thread.currentThread();
    ForkJoinPool forkJoinPool;
    ForkJoinWorkerThread forkJoinWorkerThread;
    if (thread instanceof ForkJoinWorkerThread && (forkJoinPool = (forkJoinWorkerThread = (ForkJoinWorkerThread)thread).pool) != null) {
      WorkQueue workQueue = forkJoinWorkerThread.workQueue;
      while (!paramManagedBlocker.isReleasable()) {
        if (forkJoinPool.tryCompensate(workQueue))
          try {
            do {
            
            } while (!paramManagedBlocker.isReleasable() && !paramManagedBlocker.block());
            U.getAndAddLong(forkJoinPool, CTL, 281474976710656L);
          } finally {
            U.getAndAddLong(forkJoinPool, CTL, 281474976710656L);
          }  
      } 
    } else {
      do {
      
      } while (!paramManagedBlocker.isReleasable() && !paramManagedBlocker.block());
    } 
  }
  
  protected <T> RunnableFuture<T> newTaskFor(Runnable paramRunnable, T paramT) { return new ForkJoinTask.AdaptedRunnable(paramRunnable, paramT); }
  
  protected <T> RunnableFuture<T> newTaskFor(Callable<T> paramCallable) { return new ForkJoinTask.AdaptedCallable(paramCallable); }
  
  private static ForkJoinPool makeCommonPool() {
    int i = -1;
    ForkJoinWorkerThreadFactory forkJoinWorkerThreadFactory = null;
    Thread.UncaughtExceptionHandler uncaughtExceptionHandler = null;
    try {
      String str1 = System.getProperty("java.util.concurrent.ForkJoinPool.common.parallelism");
      String str2 = System.getProperty("java.util.concurrent.ForkJoinPool.common.threadFactory");
      String str3 = System.getProperty("java.util.concurrent.ForkJoinPool.common.exceptionHandler");
      if (str1 != null)
        i = Integer.parseInt(str1); 
      if (str2 != null)
        forkJoinWorkerThreadFactory = (ForkJoinWorkerThreadFactory)ClassLoader.getSystemClassLoader().loadClass(str2).newInstance(); 
      if (str3 != null)
        uncaughtExceptionHandler = (Thread.UncaughtExceptionHandler)ClassLoader.getSystemClassLoader().loadClass(str3).newInstance(); 
    } catch (Exception exception) {}
    if (forkJoinWorkerThreadFactory == null)
      if (System.getSecurityManager() == null) {
        forkJoinWorkerThreadFactory = defaultForkJoinWorkerThreadFactory;
      } else {
        forkJoinWorkerThreadFactory = new InnocuousForkJoinWorkerThreadFactory();
      }  
    if (i < 0 && (i = Runtime.getRuntime().availableProcessors() - 1) <= 0)
      i = 1; 
    if (i > 32767)
      i = 32767; 
    return new ForkJoinPool(i, forkJoinWorkerThreadFactory, uncaughtExceptionHandler, 0, "ForkJoinPool.commonPool-worker-");
  }
  
  static  {
    try {
      U = Unsafe.getUnsafe();
      Class clazz1 = ForkJoinPool.class;
      CTL = U.objectFieldOffset(clazz1.getDeclaredField("ctl"));
      RUNSTATE = U.objectFieldOffset(clazz1.getDeclaredField("runState"));
      STEALCOUNTER = U.objectFieldOffset(clazz1.getDeclaredField("stealCounter"));
      Class clazz2 = Thread.class;
      PARKBLOCKER = U.objectFieldOffset(clazz2.getDeclaredField("parkBlocker"));
      Class clazz3 = WorkQueue.class;
      QTOP = U.objectFieldOffset(clazz3.getDeclaredField("top"));
      QLOCK = U.objectFieldOffset(clazz3.getDeclaredField("qlock"));
      QSCANSTATE = U.objectFieldOffset(clazz3.getDeclaredField("scanState"));
      QPARKER = U.objectFieldOffset(clazz3.getDeclaredField("parker"));
      QCURRENTSTEAL = U.objectFieldOffset(clazz3.getDeclaredField("currentSteal"));
      QCURRENTJOIN = U.objectFieldOffset(clazz3.getDeclaredField("currentJoin"));
      Class clazz4 = ForkJoinTask[].class;
      ABASE = U.arrayBaseOffset(clazz4);
      int j = U.arrayIndexScale(clazz4);
      if ((j & j - 1) != 0)
        throw new Error("data type scale not a power of two"); 
      ASHIFT = 31 - Integer.numberOfLeadingZeros(j);
    } catch (Exception exception) {
      throw new Error(exception);
    } 
    commonMaxSpares = 256;
    defaultForkJoinWorkerThreadFactory = new DefaultForkJoinWorkerThreadFactory();
    modifyThreadPermission = new RuntimePermission("modifyThread");
    common = (ForkJoinPool)AccessController.doPrivileged(new PrivilegedAction<ForkJoinPool>() {
          public ForkJoinPool run() { return ForkJoinPool.makeCommonPool(); }
        });
    int i = common.config & 0xFFFF;
    commonParallelism = (i > 0) ? i : 1;
  }
  
  static final class DefaultForkJoinWorkerThreadFactory implements ForkJoinWorkerThreadFactory {
    public final ForkJoinWorkerThread newThread(ForkJoinPool param1ForkJoinPool) { return new ForkJoinWorkerThread(param1ForkJoinPool); }
  }
  
  static final class EmptyTask extends ForkJoinTask<Void> {
    private static final long serialVersionUID = -7721805057305804111L;
    
    public final Void getRawResult() { return null; }
    
    public final void setRawResult(Void param1Void) {}
    
    public final boolean exec() { return true; }
  }
  
  public static interface ForkJoinWorkerThreadFactory {
    ForkJoinWorkerThread newThread(ForkJoinPool param1ForkJoinPool);
  }
  
  static final class InnocuousForkJoinWorkerThreadFactory implements ForkJoinWorkerThreadFactory {
    private static final AccessControlContext innocuousAcc;
    
    public final ForkJoinWorkerThread newThread(final ForkJoinPool pool) { return (ForkJoinWorkerThread.InnocuousForkJoinWorkerThread)AccessController.doPrivileged(new PrivilegedAction<ForkJoinWorkerThread>() {
            public ForkJoinWorkerThread run() { return new ForkJoinWorkerThread.InnocuousForkJoinWorkerThread(pool); }
          },  innocuousAcc); }
    
    static  {
      Permissions permissions = new Permissions();
      permissions.add(modifyThreadPermission);
      permissions.add(new RuntimePermission("enableContextClassLoaderOverride"));
      permissions.add(new RuntimePermission("modifyThreadGroup"));
      innocuousAcc = new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, permissions) });
    }
  }
  
  public static interface ManagedBlocker {
    boolean block();
    
    boolean isReleasable();
  }
  
  @Contended
  static final class WorkQueue {
    static final int INITIAL_QUEUE_CAPACITY = 8192;
    
    static final int MAXIMUM_QUEUE_CAPACITY = 67108864;
    
    int stackPred;
    
    int nsteals;
    
    int hint;
    
    int config;
    
    int top;
    
    ForkJoinTask<?>[] array;
    
    final ForkJoinPool pool;
    
    final ForkJoinWorkerThread owner;
    
    private static final Unsafe U;
    
    private static final int ABASE;
    
    private static final int ASHIFT;
    
    private static final long QTOP;
    
    private static final long QLOCK;
    
    private static final long QCURRENTSTEAL;
    
    WorkQueue(ForkJoinPool param1ForkJoinPool, ForkJoinWorkerThread param1ForkJoinWorkerThread) {
      this.pool = param1ForkJoinPool;
      this.owner = param1ForkJoinWorkerThread;
      this.base = this.top = 4096;
    }
    
    final int getPoolIndex() { return (this.config & 0xFFFF) >>> 1; }
    
    final int queueSize() {
      int i = this.base - this.top;
      return (i >= 0) ? 0 : -i;
    }
    
    final boolean isEmpty() {
      ForkJoinTask[] arrayOfForkJoinTask;
      int i;
      int j;
      int k;
      return ((i = this.base - (k = this.top)) >= 0 || (i == -1 && ((arrayOfForkJoinTask = this.array) == null || (j = arrayOfForkJoinTask.length - 1) < 0 || U.getObject(arrayOfForkJoinTask, ((j & k - true) << ASHIFT) + ABASE) == null)));
    }
    
    final void push(ForkJoinTask<?> param1ForkJoinTask) {
      int i = this.base;
      int j = this.top;
      ForkJoinTask[] arrayOfForkJoinTask;
      if ((arrayOfForkJoinTask = this.array) != null) {
        int m = arrayOfForkJoinTask.length - 1;
        U.putOrderedObject(arrayOfForkJoinTask, (((m & j) << ASHIFT) + ABASE), param1ForkJoinTask);
        U.putOrderedInt(this, QTOP, j + 1);
        int k;
        if ((k = j - i) <= 1) {
          ForkJoinPool forkJoinPool;
          if ((forkJoinPool = this.pool) != null)
            forkJoinPool.signalWork(forkJoinPool.workQueues, this); 
        } else if (k >= m) {
          growArray();
        } 
      } 
    }
    
    final ForkJoinTask<?>[] growArray() {
      ForkJoinTask[] arrayOfForkJoinTask1 = this.array;
      int i = (arrayOfForkJoinTask1 != null) ? (arrayOfForkJoinTask1.length << 1) : 8192;
      if (i > 67108864)
        throw new RejectedExecutionException("Queue capacity exceeded"); 
      ForkJoinTask[] arrayOfForkJoinTask2 = this.array = new ForkJoinTask[i];
      int j;
      int k;
      int m;
      if (arrayOfForkJoinTask1 != null && (j = arrayOfForkJoinTask1.length - 1) >= 0 && (k = this.top) - (m = this.base) > 0) {
        int n = i - 1;
        do {
          int i1 = ((m & j) << ASHIFT) + ABASE;
          int i2 = ((m & n) << ASHIFT) + ABASE;
          ForkJoinTask forkJoinTask = (ForkJoinTask)U.getObjectVolatile(arrayOfForkJoinTask1, i1);
          if (forkJoinTask == null || !U.compareAndSwapObject(arrayOfForkJoinTask1, i1, forkJoinTask, null))
            continue; 
          U.putObjectVolatile(arrayOfForkJoinTask2, i2, forkJoinTask);
        } while (++m != k);
      } 
      return arrayOfForkJoinTask2;
    }
    
    final ForkJoinTask<?> pop() {
      ForkJoinTask[] arrayOfForkJoinTask;
      int i;
      if ((arrayOfForkJoinTask = this.array) != null && (i = arrayOfForkJoinTask.length - 1) >= 0) {
        long l = (((i & j) << ASHIFT) + ABASE);
        ForkJoinTask forkJoinTask;
        int j;
        while ((j = this.top - 1) - this.base >= 0 && (forkJoinTask = (ForkJoinTask)U.getObject(arrayOfForkJoinTask, l)) != null) {
          if (U.compareAndSwapObject(arrayOfForkJoinTask, l, forkJoinTask, null)) {
            U.putOrderedInt(this, QTOP, j);
            return forkJoinTask;
          } 
        } 
      } 
      return null;
    }
    
    final ForkJoinTask<?> pollAt(int param1Int) {
      int i = ((arrayOfForkJoinTask.length - 1 & param1Int) << ASHIFT) + ABASE;
      ForkJoinTask forkJoinTask;
      ForkJoinTask[] arrayOfForkJoinTask;
      if ((arrayOfForkJoinTask = this.array) != null && (forkJoinTask = (ForkJoinTask)U.getObjectVolatile(arrayOfForkJoinTask, i)) != null && this.base == param1Int && U.compareAndSwapObject(arrayOfForkJoinTask, i, forkJoinTask, null)) {
        this.base = param1Int + 1;
        return forkJoinTask;
      } 
      return null;
    }
    
    final ForkJoinTask<?> poll() {
      ForkJoinTask[] arrayOfForkJoinTask;
      int i;
      while ((i = this.base) - this.top < 0 && (arrayOfForkJoinTask = this.array) != null) {
        int j = ((arrayOfForkJoinTask.length - 1 & i) << ASHIFT) + ABASE;
        ForkJoinTask forkJoinTask = (ForkJoinTask)U.getObjectVolatile(arrayOfForkJoinTask, j);
        if (this.base == i) {
          if (forkJoinTask != null) {
            if (U.compareAndSwapObject(arrayOfForkJoinTask, j, forkJoinTask, null)) {
              this.base = i + 1;
              return forkJoinTask;
            } 
            continue;
          } 
          if (i + 1 == this.top)
            break; 
        } 
      } 
      return null;
    }
    
    final ForkJoinTask<?> nextLocalTask() { return ((this.config & 0x10000) == 0) ? pop() : poll(); }
    
    final ForkJoinTask<?> peek() {
      ForkJoinTask[] arrayOfForkJoinTask = this.array;
      int i;
      if (arrayOfForkJoinTask == null || (i = arrayOfForkJoinTask.length - 1) < 0)
        return null; 
      int j = ((this.config & 0x10000) == 0) ? (this.top - 1) : this.base;
      int k = ((j & i) << ASHIFT) + ABASE;
      return (ForkJoinTask)U.getObjectVolatile(arrayOfForkJoinTask, k);
    }
    
    final boolean tryUnpush(ForkJoinTask<?> param1ForkJoinTask) {
      ForkJoinTask[] arrayOfForkJoinTask;
      int i;
      if ((arrayOfForkJoinTask = this.array) != null && (i = this.top) != this.base && U.compareAndSwapObject(arrayOfForkJoinTask, (((arrayOfForkJoinTask.length - 1 & --i) << ASHIFT) + ABASE), param1ForkJoinTask, null)) {
        U.putOrderedInt(this, QTOP, i);
        return true;
      } 
      return false;
    }
    
    final void cancelAll() {
      ForkJoinTask forkJoinTask;
      if ((forkJoinTask = this.currentJoin) != null)
        (this.currentJoin = null).cancelIgnoringExceptions(forkJoinTask); 
      if ((forkJoinTask = this.currentSteal) != null)
        (this.currentSteal = null).cancelIgnoringExceptions(forkJoinTask); 
      while ((forkJoinTask = poll()) != null)
        ForkJoinTask.cancelIgnoringExceptions(forkJoinTask); 
    }
    
    final void pollAndExecAll() {
      ForkJoinTask forkJoinTask;
      while ((forkJoinTask = poll()) != null)
        forkJoinTask.doExec(); 
    }
    
    final void execLocalTasks() {
      int i = this.base;
      ForkJoinTask[] arrayOfForkJoinTask = this.array;
      int j;
      int k;
      if (i - (k = this.top - 1) <= 0 && arrayOfForkJoinTask != null && (j = arrayOfForkJoinTask.length - 1) >= 0)
        if ((this.config & 0x10000) == 0) {
          ForkJoinTask forkJoinTask;
          do {
            U.putOrderedInt(this, QTOP, k);
            forkJoinTask.doExec();
          } while ((forkJoinTask = (ForkJoinTask)U.getAndSetObject(arrayOfForkJoinTask, (((j & k) << ASHIFT) + ABASE), null)) != null && this.base - (k = this.top - 1) <= 0);
        } else {
          pollAndExecAll();
        }  
    }
    
    final void runTask(ForkJoinTask<?> param1ForkJoinTask) {
      if (param1ForkJoinTask != null) {
        this.scanState &= 0xFFFFFFFE;
        (this.currentSteal = param1ForkJoinTask).doExec();
        U.putOrderedObject(this, QCURRENTSTEAL, null);
        execLocalTasks();
        ForkJoinWorkerThread forkJoinWorkerThread = this.owner;
        if (++this.nsteals < 0)
          transferStealCount(this.pool); 
        this.scanState |= 0x1;
        if (forkJoinWorkerThread != null)
          forkJoinWorkerThread.afterTopLevelExec(); 
      } 
    }
    
    final void transferStealCount(ForkJoinPool param1ForkJoinPool) {
      AtomicLong atomicLong;
      if (param1ForkJoinPool != null && (atomicLong = param1ForkJoinPool.stealCounter) != null) {
        int i = this.nsteals;
        this.nsteals = 0;
        atomicLong.getAndAdd(((i < 0) ? Integer.MAX_VALUE : i));
      } 
    }
    
    final boolean tryRemoveAndExec(ForkJoinTask<?> param1ForkJoinTask) {
      ForkJoinTask[] arrayOfForkJoinTask;
      int i;
      if ((arrayOfForkJoinTask = this.array) != null && (i = arrayOfForkJoinTask.length - 1) >= 0 && param1ForkJoinTask != null) {
        int j;
        int k;
        int m;
        while ((m = (j = this.top) - (k = this.base)) > 0) {
          while (true) {
            long l = (((--j & i) << ASHIFT) + ABASE);
            ForkJoinTask forkJoinTask;
            if ((forkJoinTask = (ForkJoinTask)U.getObject(arrayOfForkJoinTask, l)) == null)
              return (j + 1 == this.top); 
            if (forkJoinTask == param1ForkJoinTask) {
              boolean bool = false;
              if (j + 1 == this.top) {
                if (U.compareAndSwapObject(arrayOfForkJoinTask, l, param1ForkJoinTask, null)) {
                  U.putOrderedInt(this, QTOP, j);
                  bool = true;
                } 
              } else if (this.base == k) {
                bool = U.compareAndSwapObject(arrayOfForkJoinTask, l, param1ForkJoinTask, new ForkJoinPool.EmptyTask());
              } 
              if (bool)
                param1ForkJoinTask.doExec(); 
              break;
            } 
            if (forkJoinTask.status < 0 && j + 1 == this.top) {
              if (U.compareAndSwapObject(arrayOfForkJoinTask, l, forkJoinTask, null))
                U.putOrderedInt(this, QTOP, j); 
              break;
            } 
            if (--m == 0)
              return false; 
          } 
          if (param1ForkJoinTask.status < 0)
            return false; 
        } 
      } 
      return true;
    }
    
    final CountedCompleter<?> popCC(CountedCompleter<?> param1CountedCompleter, int param1Int) {
      long l = (((arrayOfForkJoinTask.length - 1 & i - 1) << ASHIFT) + ABASE);
      int i;
      ForkJoinTask[] arrayOfForkJoinTask;
      Object object;
      if (this.base - (i = this.top) < 0 && (arrayOfForkJoinTask = this.array) != null && (object = U.getObjectVolatile(arrayOfForkJoinTask, l)) != null && object instanceof CountedCompleter) {
        CountedCompleter countedCompleter1 = (CountedCompleter)object;
        CountedCompleter countedCompleter2 = countedCompleter1;
        do {
          if (countedCompleter2 == param1CountedCompleter) {
            if (param1Int < 0) {
              if (U.compareAndSwapInt(this, QLOCK, 0, 1)) {
                if (this.top == i && this.array == arrayOfForkJoinTask && U.compareAndSwapObject(arrayOfForkJoinTask, l, countedCompleter1, null)) {
                  U.putOrderedInt(this, QTOP, i - 1);
                  U.putOrderedInt(this, QLOCK, 0);
                  return countedCompleter1;
                } 
                U.compareAndSwapInt(this, QLOCK, 1, 0);
              } 
              break;
            } 
            if (U.compareAndSwapObject(arrayOfForkJoinTask, l, countedCompleter1, null)) {
              U.putOrderedInt(this, QTOP, i - 1);
              return countedCompleter1;
            } 
            break;
          } 
        } while ((countedCompleter2 = countedCompleter2.completer) != null);
      } 
      return null;
    }
    
    final int pollAndExecCC(CountedCompleter<?> param1CountedCompleter) {
      byte b;
      int i;
      ForkJoinTask[] arrayOfForkJoinTask;
      if ((i = this.base) - this.top >= 0 || (arrayOfForkJoinTask = this.array) == null) {
        b = i | 0x80000000;
      } else {
        long l = (((arrayOfForkJoinTask.length - 1 & i) << ASHIFT) + ABASE);
        Object object;
        if ((object = U.getObjectVolatile(arrayOfForkJoinTask, l)) == null) {
          b = 2;
        } else if (!(object instanceof CountedCompleter)) {
          b = -1;
        } else {
          CountedCompleter countedCompleter1 = (CountedCompleter)object;
          CountedCompleter countedCompleter2 = countedCompleter1;
          while (true) {
            if (countedCompleter2 == param1CountedCompleter) {
              if (this.base == i && U.compareAndSwapObject(arrayOfForkJoinTask, l, countedCompleter1, null)) {
                this.base = i + 1;
                countedCompleter1.doExec();
                boolean bool = true;
                break;
              } 
              b = 2;
              break;
            } 
            if ((countedCompleter2 = countedCompleter2.completer) == null) {
              b = -1;
              break;
            } 
          } 
        } 
      } 
      return b;
    }
    
    final boolean isApparentlyUnblocked() {
      ForkJoinWorkerThread forkJoinWorkerThread;
      Thread.State state;
      return (this.scanState >= 0 && (forkJoinWorkerThread = this.owner) != null && (state = forkJoinWorkerThread.getState()) != Thread.State.BLOCKED && state != Thread.State.WAITING && state != Thread.State.TIMED_WAITING);
    }
    
    static  {
      try {
        U = Unsafe.getUnsafe();
        Class clazz1 = WorkQueue.class;
        Class clazz2 = ForkJoinTask[].class;
        QTOP = U.objectFieldOffset(clazz1.getDeclaredField("top"));
        QLOCK = U.objectFieldOffset(clazz1.getDeclaredField("qlock"));
        QCURRENTSTEAL = U.objectFieldOffset(clazz1.getDeclaredField("currentSteal"));
        ABASE = U.arrayBaseOffset(clazz2);
        int i = U.arrayIndexScale(clazz2);
        if ((i & i - 1) != 0)
          throw new Error("data type scale not a power of two"); 
        ASHIFT = 31 - Integer.numberOfLeadingZeros(i);
      } catch (Exception exception) {
        throw new Error(exception);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\ForkJoinPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */