package java.util.concurrent.locks;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import sun.misc.Unsafe;

public class StampedLock implements Serializable {
  private static final long serialVersionUID = -6001602636862214147L;
  
  private static final int NCPU = Runtime.getRuntime().availableProcessors();
  
  private static final int SPINS = (NCPU > 1) ? 64 : 0;
  
  private static final int HEAD_SPINS = (NCPU > 1) ? 1024 : 0;
  
  private static final int MAX_HEAD_SPINS = (NCPU > 1) ? 65536 : 0;
  
  private static final int OVERFLOW_YIELD_RATE = 7;
  
  private static final int LG_READERS = 7;
  
  private static final long RUNIT = 1L;
  
  private static final long WBIT = 128L;
  
  private static final long RBITS = 127L;
  
  private static final long RFULL = 126L;
  
  private static final long ABITS = 255L;
  
  private static final long SBITS = -128L;
  
  private static final long ORIGIN = 256L;
  
  private static final long INTERRUPTED = 1L;
  
  private static final int WAITING = -1;
  
  private static final int CANCELLED = 1;
  
  private static final int RMODE = 0;
  
  private static final int WMODE = 1;
  
  ReadLockView readLockView;
  
  WriteLockView writeLockView;
  
  ReadWriteLockView readWriteLockView;
  
  private int readerOverflow;
  
  private static final Unsafe U;
  
  private static final long STATE;
  
  private static final long WHEAD;
  
  private static final long WTAIL;
  
  private static final long WNEXT;
  
  private static final long WSTATUS;
  
  private static final long WCOWAIT;
  
  private static final long PARKBLOCKER;
  
  public long writeLock() {
    long l1;
    long l2;
    return (((l1 = this.state) & 0xFFL) == 0L && U.compareAndSwapLong(this, STATE, l1, l2 = l1 + 128L)) ? l2 : acquireWrite(false, 0L);
  }
  
  public long tryWriteLock() {
    long l1;
    long l2;
    return (((l1 = this.state) & 0xFFL) == 0L && U.compareAndSwapLong(this, STATE, l1, l2 = l1 + 128L)) ? l2 : 0L;
  }
  
  public long tryWriteLock(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    long l = paramTimeUnit.toNanos(paramLong);
    if (!Thread.interrupted()) {
      long l1;
      if ((l1 = tryWriteLock()) != 0L)
        return l1; 
      if (l <= 0L)
        return 0L; 
      long l2;
      if ((l2 = System.nanoTime() + l) == 0L)
        l2 = 1L; 
      if ((l1 = acquireWrite(true, l2)) != 1L)
        return l1; 
    } 
    throw new InterruptedException();
  }
  
  public long writeLockInterruptibly() {
    long l;
    if (!Thread.interrupted() && (l = acquireWrite(true, 0L)) != 1L)
      return l; 
    throw new InterruptedException();
  }
  
  public long readLock() {
    long l1 = this.state;
    long l2;
    return (this.whead == this.wtail && (l1 & 0xFFL) < 126L && U.compareAndSwapLong(this, STATE, l1, l2 = l1 + 1L)) ? l2 : acquireRead(false, 0L);
  }
  
  public long tryReadLock() {
    long l;
    while (true) {
      long l1;
      long l2;
      if ((l2 = (l1 = this.state) & 0xFFL) == 128L)
        return 0L; 
      if (l2 < 126L) {
        long l3;
        if (U.compareAndSwapLong(this, STATE, l1, l3 = l1 + 1L))
          return l3; 
        continue;
      } 
      if ((l = tryIncReaderOverflow(l1)) != 0L)
        break; 
    } 
    return l;
  }
  
  public long tryReadLock(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    long l = paramTimeUnit.toNanos(paramLong);
    if (!Thread.interrupted()) {
      long l1;
      long l2;
      if ((l2 = (l1 = this.state) & 0xFFL) != 128L)
        if (l2 < 126L) {
          long l5;
          if (U.compareAndSwapLong(this, STATE, l1, l5 = l1 + 1L))
            return l5; 
        } else {
          long l5;
          if ((l5 = tryIncReaderOverflow(l1)) != 0L)
            return l5; 
        }  
      if (l <= 0L)
        return 0L; 
      long l4;
      if ((l4 = System.nanoTime() + l) == 0L)
        l4 = 1L; 
      long l3;
      if ((l3 = acquireRead(true, l4)) != 1L)
        return l3; 
    } 
    throw new InterruptedException();
  }
  
  public long readLockInterruptibly() {
    long l;
    if (!Thread.interrupted() && (l = acquireRead(true, 0L)) != 1L)
      return l; 
    throw new InterruptedException();
  }
  
  public long tryOptimisticRead() {
    long l;
    return (((l = this.state) & 0x80L) == 0L) ? (l & 0xFFFFFFFFFFFFFF80L) : 0L;
  }
  
  public boolean validate(long paramLong) {
    U.loadFence();
    return ((paramLong & 0xFFFFFFFFFFFFFF80L) == (this.state & 0xFFFFFFFFFFFFFF80L));
  }
  
  public void unlockWrite(long paramLong) {
    if (this.state != paramLong || (paramLong & 0x80L) == 0L)
      throw new IllegalMonitorStateException(); 
    this.state = (paramLong += 128L == 0L) ? 256L : paramLong;
    WNode wNode;
    if ((wNode = this.whead) != null && wNode.status != 0)
      release(wNode); 
  }
  
  public void unlockRead(long paramLong) {
    while (true) {
      long l1;
      long l2;
      if (((l1 = this.state) & 0xFFFFFFFFFFFFFF80L) != (paramLong & 0xFFFFFFFFFFFFFF80L) || (paramLong & 0xFFL) == 0L || (l2 = l1 & 0xFFL) == 0L || l2 == 128L)
        throw new IllegalMonitorStateException(); 
      if (l2 < 126L) {
        if (U.compareAndSwapLong(this, STATE, l1, l1 - 1L)) {
          WNode wNode;
          if (l2 == 1L && (wNode = this.whead) != null && wNode.status != 0)
            release(wNode); 
          break;
        } 
        continue;
      } 
      if (tryDecReaderOverflow(l1) != 0L)
        break; 
    } 
  }
  
  public void unlock(long paramLong) {
    long l1 = paramLong & 0xFFL;
    long l2;
    long l3;
    while (((l3 = this.state) & 0xFFFFFFFFFFFFFF80L) == (paramLong & 0xFFFFFFFFFFFFFF80L) && (l2 = l3 & 0xFFL) != 0L) {
      if (l2 == 128L) {
        if (l1 != l2)
          break; 
        this.state = (l3 += 128L == 0L) ? 256L : l3;
        WNode wNode;
        if ((wNode = this.whead) != null && wNode.status != 0)
          release(wNode); 
        return;
      } 
      if (l1 == 0L || l1 >= 128L)
        break; 
      if (l2 < 126L) {
        if (U.compareAndSwapLong(this, STATE, l3, l3 - 1L)) {
          WNode wNode;
          if (l2 == 1L && (wNode = this.whead) != null && wNode.status != 0)
            release(wNode); 
          return;
        } 
        continue;
      } 
      if (tryDecReaderOverflow(l3) != 0L)
        return; 
    } 
    throw new IllegalMonitorStateException();
  }
  
  public long tryConvertToWriteLock(long paramLong) {
    long l1 = paramLong & 0xFFL;
    long l2;
    while (((l2 = this.state) & 0xFFFFFFFFFFFFFF80L) == (paramLong & 0xFFFFFFFFFFFFFF80L)) {
      long l;
      if ((l = l2 & 0xFFL) == 0L) {
        if (l1 != 0L)
          break; 
        long l3;
        if (U.compareAndSwapLong(this, STATE, l2, l3 = l2 + 128L))
          return l3; 
        continue;
      } 
      if (l == 128L) {
        if (l1 != l)
          break; 
        return paramLong;
      } 
      if (l == 1L && l1 != 0L) {
        long l3;
        if (U.compareAndSwapLong(this, STATE, l2, l3 = l2 - 1L + 128L))
          return l3; 
      } 
    } 
    return 0L;
  }
  
  public long tryConvertToReadLock(long paramLong) {
    long l1 = paramLong & 0xFFL;
    long l2;
    while (((l2 = this.state) & 0xFFFFFFFFFFFFFF80L) == (paramLong & 0xFFFFFFFFFFFFFF80L)) {
      long l;
      if ((l = l2 & 0xFFL) == 0L) {
        if (l1 != 0L)
          break; 
        if (l < 126L) {
          long l4;
          if (U.compareAndSwapLong(this, STATE, l2, l4 = l2 + 1L))
            return l4; 
          continue;
        } 
        long l3;
        if ((l3 = tryIncReaderOverflow(l2)) != 0L)
          return l3; 
        continue;
      } 
      if (l == 128L) {
        if (l1 != l)
          break; 
        long l3 = l2 + 129L;
        this.state = l3;
        WNode wNode;
        if ((wNode = this.whead) != null && wNode.status != 0)
          release(wNode); 
        return l3;
      } 
      if (l1 != 0L && l1 < 128L)
        return paramLong; 
    } 
    return 0L;
  }
  
  public long tryConvertToOptimisticRead(long paramLong) {
    long l1 = paramLong & 0xFFL;
    U.loadFence();
    long l2;
    while (((l2 = this.state) & 0xFFFFFFFFFFFFFF80L) == (paramLong & 0xFFFFFFFFFFFFFF80L)) {
      long l3;
      if ((l3 = l2 & 0xFFL) == 0L) {
        if (l1 != 0L)
          break; 
        return l2;
      } 
      if (l3 == 128L) {
        if (l1 != l3)
          break; 
        long l = (l2 += 128L == 0L) ? 256L : l2;
        this.state = l;
        WNode wNode;
        if ((wNode = this.whead) != null && wNode.status != 0)
          release(wNode); 
        return l;
      } 
      if (l1 == 0L || l1 >= 128L)
        break; 
      if (l3 < 126L) {
        long l;
        if (U.compareAndSwapLong(this, STATE, l2, l = l2 - 1L)) {
          WNode wNode;
          if (l3 == 1L && (wNode = this.whead) != null && wNode.status != 0)
            release(wNode); 
          return l & 0xFFFFFFFFFFFFFF80L;
        } 
        continue;
      } 
      long l4;
      if ((l4 = tryDecReaderOverflow(l2)) != 0L)
        return l4 & 0xFFFFFFFFFFFFFF80L; 
    } 
    return 0L;
  }
  
  public boolean tryUnlockWrite() {
    long l;
    if (((l = this.state) & 0x80L) != 0L) {
      this.state = (l += 128L == 0L) ? 256L : l;
      WNode wNode;
      if ((wNode = this.whead) != null && wNode.status != 0)
        release(wNode); 
      return true;
    } 
    return false;
  }
  
  public boolean tryUnlockRead() {
    long l1;
    long l2;
    while ((l2 = (l1 = this.state) & 0xFFL) != 0L && l2 < 128L) {
      if (l2 < 126L) {
        if (U.compareAndSwapLong(this, STATE, l1, l1 - 1L)) {
          WNode wNode;
          if (l2 == 1L && (wNode = this.whead) != null && wNode.status != 0)
            release(wNode); 
          return true;
        } 
        continue;
      } 
      if (tryDecReaderOverflow(l1) != 0L)
        return true; 
    } 
    return false;
  }
  
  private int getReadLockCount(long paramLong) {
    long l;
    if ((l = paramLong & 0x7FL) >= 126L)
      l = 126L + this.readerOverflow; 
    return (int)l;
  }
  
  public boolean isWriteLocked() { return ((this.state & 0x80L) != 0L); }
  
  public boolean isReadLocked() { return ((this.state & 0x7FL) != 0L); }
  
  public int getReadLockCount() { return getReadLockCount(this.state); }
  
  public String toString() {
    long l = this.state;
    return super.toString() + (((l & 0xFFL) == 0L) ? "[Unlocked]" : (((l & 0x80L) != 0L) ? "[Write-locked]" : ("[Read-locks:" + getReadLockCount(l) + "]")));
  }
  
  public Lock asReadLock() {
    ReadLockView readLockView1;
    return ((readLockView1 = this.readLockView) != null) ? readLockView1 : (this.readLockView = new ReadLockView());
  }
  
  public Lock asWriteLock() {
    WriteLockView writeLockView1;
    return ((writeLockView1 = this.writeLockView) != null) ? writeLockView1 : (this.writeLockView = new WriteLockView());
  }
  
  public ReadWriteLock asReadWriteLock() {
    ReadWriteLockView readWriteLockView1;
    return ((readWriteLockView1 = this.readWriteLockView) != null) ? readWriteLockView1 : (this.readWriteLockView = new ReadWriteLockView());
  }
  
  final void unstampedUnlockWrite() {
    long l;
    if (((l = this.state) & 0x80L) == 0L)
      throw new IllegalMonitorStateException(); 
    this.state = (l += 128L == 0L) ? 256L : l;
    WNode wNode;
    if ((wNode = this.whead) != null && wNode.status != 0)
      release(wNode); 
  }
  
  final void unstampedUnlockRead() {
    while (true) {
      long l1;
      long l2;
      if ((l2 = (l1 = this.state) & 0xFFL) == 0L || l2 >= 128L)
        throw new IllegalMonitorStateException(); 
      if (l2 < 126L) {
        if (U.compareAndSwapLong(this, STATE, l1, l1 - 1L)) {
          WNode wNode;
          if (l2 == 1L && (wNode = this.whead) != null && wNode.status != 0)
            release(wNode); 
          break;
        } 
        continue;
      } 
      if (tryDecReaderOverflow(l1) != 0L)
        break; 
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    this.state = 256L;
  }
  
  private long tryIncReaderOverflow(long paramLong) {
    if ((paramLong & 0xFFL) == 126L) {
      if (U.compareAndSwapLong(this, STATE, paramLong, paramLong | 0x7FL)) {
        this.readerOverflow++;
        this.state = paramLong;
        return paramLong;
      } 
    } else if ((LockSupport.nextSecondarySeed() & 0x7) == 0) {
      Thread.yield();
    } 
    return 0L;
  }
  
  private long tryDecReaderOverflow(long paramLong) {
    if ((paramLong & 0xFFL) == 126L) {
      if (U.compareAndSwapLong(this, STATE, paramLong, paramLong | 0x7FL)) {
        long l;
        int i;
        if ((i = this.readerOverflow) > 0) {
          this.readerOverflow = i - 1;
          l = paramLong;
        } else {
          l = paramLong - 1L;
        } 
        this.state = l;
        return l;
      } 
    } else if ((LockSupport.nextSecondarySeed() & 0x7) == 0) {
      Thread.yield();
    } 
    return 0L;
  }
  
  private void release(WNode paramWNode) {
    if (paramWNode != null) {
      U.compareAndSwapInt(paramWNode, WSTATUS, -1, 0);
      WNode wNode;
      if ((wNode = paramWNode.next) == null || wNode.status == 1)
        for (WNode wNode1 = this.wtail; wNode1 != null && wNode1 != paramWNode; wNode1 = wNode1.prev) {
          if (wNode1.status <= 0)
            wNode = wNode1; 
        }  
      Thread thread;
      if (wNode != null && (thread = wNode.thread) != null)
        U.unpark(thread); 
    } 
  }
  
  private long acquireWrite(boolean paramBoolean, long paramLong) {
    WNode wNode2;
    WNode wNode1 = null;
    int i = -1;
    while (true) {
      long l1;
      long l2;
      while ((l1 = (l2 = this.state) & 0xFFL) == 0L) {
        long l;
        if (U.compareAndSwapLong(this, STATE, l2, l = l2 + 128L))
          return l; 
      } 
      if (i < 0) {
        i = (l1 == 128L && this.wtail == this.whead) ? SPINS : 0;
        continue;
      } 
      if (i > 0) {
        if (LockSupport.nextSecondarySeed() >= 0)
          i--; 
        continue;
      } 
      if ((wNode2 = this.wtail) == null) {
        WNode wNode = new WNode(1, null);
        if (U.compareAndSwapObject(this, WHEAD, null, wNode))
          this.wtail = wNode; 
        continue;
      } 
      if (wNode1 == null) {
        wNode1 = new WNode(1, wNode2);
        continue;
      } 
      if (wNode1.prev != wNode2) {
        wNode1.prev = wNode2;
        continue;
      } 
      if (U.compareAndSwapObject(this, WTAIL, wNode2, wNode1))
        break; 
    } 
    wNode2.next = wNode1;
    i = -1;
    while (true) {
      WNode wNode;
      if ((wNode = this.whead) == wNode2) {
        if (i < 0) {
          i = HEAD_SPINS;
        } else if (i < MAX_HEAD_SPINS) {
          i <<= 1;
        } 
        int j = i;
        do {
          long l;
          while (((l = this.state) & 0xFFL) == 0L) {
            long l1;
            if (U.compareAndSwapLong(this, STATE, l, l1 = l + 128L)) {
              this.whead = wNode1;
              wNode1.prev = null;
              return l1;
            } 
          } 
        } while (LockSupport.nextSecondarySeed() < 0 || --j > 0);
      } else if (wNode != null) {
        WNode wNode3;
        while ((wNode3 = wNode.cowait) != null) {
          Thread thread;
          if (U.compareAndSwapObject(wNode, WCOWAIT, wNode3, wNode3.cowait) && (thread = wNode3.thread) != null)
            U.unpark(thread); 
        } 
      } 
      if (this.whead == wNode) {
        long l;
        WNode wNode3;
        if ((wNode3 = wNode1.prev) != wNode2) {
          if (wNode3 != null)
            (wNode2 = wNode3).next = wNode1; 
          continue;
        } 
        int j;
        if ((j = wNode2.status) == 0) {
          U.compareAndSwapInt(wNode2, WSTATUS, 0, -1);
          continue;
        } 
        if (j == 1) {
          WNode wNode4;
          if ((wNode4 = wNode2.prev) != null) {
            wNode1.prev = wNode4;
            wNode4.next = wNode1;
          } 
          continue;
        } 
        if (paramLong == 0L) {
          l = 0L;
        } else if ((l = paramLong - System.nanoTime()) <= 0L) {
          return cancelWaiter(wNode1, wNode1, false);
        } 
        Thread thread = Thread.currentThread();
        U.putObject(thread, PARKBLOCKER, this);
        wNode1.thread = thread;
        if (wNode2.status < 0 && (wNode2 != wNode || (this.state & 0xFFL) != 0L) && this.whead == wNode && wNode1.prev == wNode2)
          U.park(false, l); 
        wNode1.thread = null;
        U.putObject(thread, PARKBLOCKER, null);
        if (paramBoolean && Thread.interrupted())
          break; 
      } 
    } 
    return cancelWaiter(wNode1, wNode1, true);
  }
  
  private long acquireRead(boolean paramBoolean, long paramLong) {
    WNode wNode2;
    WNode wNode1 = null;
    int i = -1;
    label175: while (true) {
      WNode wNode;
      if ((wNode = this.whead) == (wNode2 = this.wtail))
        while (true) {
          long l1;
          long l2;
          long l3;
          if (((l1 = (l2 = this.state) & 0xFFL) < 126L) ? U.compareAndSwapLong(this, STATE, l2, l3 = l2 + 1L) : (l1 < 128L && (l3 = tryIncReaderOverflow(l2)) != 0L))
            return l3; 
          if (l1 >= 128L) {
            if (i > 0) {
              if (LockSupport.nextSecondarySeed() >= 0)
                i--; 
              continue;
            } 
            if (i == 0) {
              WNode wNode3 = this.whead;
              WNode wNode4 = this.wtail;
              if ((wNode3 == wNode && wNode4 == wNode2) || (wNode = wNode3) != (wNode2 = wNode4))
                break; 
            } 
            i = SPINS;
          } 
        }  
      if (wNode2 == null) {
        WNode wNode3 = new WNode(1, null);
        if (U.compareAndSwapObject(this, WHEAD, null, wNode3))
          this.wtail = wNode3; 
        continue;
      } 
      if (wNode1 == null) {
        wNode1 = new WNode(0, wNode2);
        continue;
      } 
      if (wNode == wNode2 || wNode2.mode != 0) {
        if (wNode1.prev != wNode2) {
          wNode1.prev = wNode2;
          continue;
        } 
        if (U.compareAndSwapObject(this, WTAIL, wNode2, wNode1)) {
          wNode2.next = wNode1;
          break;
        } 
        continue;
      } 
      if (!U.compareAndSwapObject(wNode2, WCOWAIT, wNode1.cowait = wNode2.cowait, wNode1)) {
        wNode1.cowait = null;
        continue;
      } 
      while (true) {
        WNode wNode4;
        Thread thread;
        if ((wNode = this.whead) != null && (wNode4 = wNode.cowait) != null && U.compareAndSwapObject(wNode, WCOWAIT, wNode4, wNode4.cowait) && (thread = wNode4.thread) != null)
          U.unpark(thread); 
        WNode wNode3;
        if (wNode == (wNode3 = wNode2.prev) || wNode == wNode2 || wNode3 == null) {
          long l;
          do {
            long l1;
            long l2;
            if (((l = (l1 = this.state) & 0xFFL) < 126L) ? U.compareAndSwapLong(this, STATE, l1, l2 = l1 + 1L) : (l < 128L && (l2 = tryIncReaderOverflow(l1)) != 0L))
              return l2; 
          } while (l < 128L);
        } 
        if (this.whead == wNode && wNode2.prev == wNode3) {
          long l;
          if (wNode3 == null || wNode == wNode2 || wNode2.status > 0) {
            wNode1 = null;
            continue label175;
          } 
          if (paramLong == 0L) {
            l = 0L;
          } else if ((l = paramLong - System.nanoTime()) <= 0L) {
            return cancelWaiter(wNode1, wNode2, false);
          } 
          Thread thread1 = Thread.currentThread();
          U.putObject(thread1, PARKBLOCKER, this);
          wNode1.thread = thread1;
          if ((wNode != wNode3 || (this.state & 0xFFL) == 128L) && this.whead == wNode && wNode2.prev == wNode3)
            U.park(false, l); 
          wNode1.thread = null;
          U.putObject(thread1, PARKBLOCKER, null);
          if (paramBoolean && Thread.interrupted())
            break; 
        } 
      } 
      return cancelWaiter(wNode1, wNode2, true);
    } 
    i = -1;
    while (true) {
      WNode wNode;
      if ((wNode = this.whead) == wNode2) {
        long l;
        if (i < 0) {
          i = HEAD_SPINS;
        } else if (i < MAX_HEAD_SPINS) {
          i <<= 1;
        } 
        int j = i;
        do {
          long l1;
          long l2;
          if (((l = (l1 = this.state) & 0xFFL) < 126L) ? U.compareAndSwapLong(this, STATE, l1, l2 = l1 + 1L) : (l < 128L && (l2 = tryIncReaderOverflow(l1)) != 0L)) {
            this.whead = wNode1;
            wNode1.prev = null;
            WNode wNode3;
            while ((wNode3 = wNode1.cowait) != null) {
              Thread thread;
              if (U.compareAndSwapObject(wNode1, WCOWAIT, wNode3, wNode3.cowait) && (thread = wNode3.thread) != null)
                U.unpark(thread); 
            } 
            return l2;
          } 
        } while (l < 128L || LockSupport.nextSecondarySeed() < 0 || --j > 0);
      } else if (wNode != null) {
        WNode wNode3;
        while ((wNode3 = wNode.cowait) != null) {
          Thread thread;
          if (U.compareAndSwapObject(wNode, WCOWAIT, wNode3, wNode3.cowait) && (thread = wNode3.thread) != null)
            U.unpark(thread); 
        } 
      } 
      if (this.whead == wNode) {
        long l;
        WNode wNode3;
        if ((wNode3 = wNode1.prev) != wNode2) {
          if (wNode3 != null)
            (wNode2 = wNode3).next = wNode1; 
          continue;
        } 
        int j;
        if ((j = wNode2.status) == 0) {
          U.compareAndSwapInt(wNode2, WSTATUS, 0, -1);
          continue;
        } 
        if (j == 1) {
          WNode wNode4;
          if ((wNode4 = wNode2.prev) != null) {
            wNode1.prev = wNode4;
            wNode4.next = wNode1;
          } 
          continue;
        } 
        if (paramLong == 0L) {
          l = 0L;
        } else if ((l = paramLong - System.nanoTime()) <= 0L) {
          return cancelWaiter(wNode1, wNode1, false);
        } 
        Thread thread = Thread.currentThread();
        U.putObject(thread, PARKBLOCKER, this);
        wNode1.thread = thread;
        if (wNode2.status < 0 && (wNode2 != wNode || (this.state & 0xFFL) == 128L) && this.whead == wNode && wNode1.prev == wNode2)
          U.park(false, l); 
        wNode1.thread = null;
        U.putObject(thread, PARKBLOCKER, null);
        if (paramBoolean && Thread.interrupted())
          break; 
      } 
    } 
    return cancelWaiter(wNode1, wNode1, true);
  }
  
  private long cancelWaiter(WNode paramWNode1, WNode paramWNode2, boolean paramBoolean) {
    if (paramWNode1 != null && paramWNode2 != null) {
      paramWNode1.status = 1;
      WNode wNode1;
      WNode wNode2;
      for (wNode1 = paramWNode2; (wNode2 = wNode1.cowait) != null; wNode1 = wNode2) {
        if (wNode2.status == 1) {
          U.compareAndSwapObject(wNode1, WCOWAIT, wNode2, wNode2.cowait);
          wNode1 = paramWNode2;
          continue;
        } 
      } 
      if (paramWNode2 == paramWNode1) {
        for (wNode1 = paramWNode2.cowait; wNode1 != null; wNode1 = wNode1.cowait) {
          Thread thread;
          if ((thread = wNode1.thread) != null)
            U.unpark(thread); 
        } 
        for (wNode1 = paramWNode1.prev; wNode1 != null; wNode1 = wNode3) {
          while ((wNode2 = paramWNode1.next) == null || wNode2.status == 1) {
            WNode wNode4 = null;
            for (WNode wNode5 = this.wtail; wNode5 != null && wNode5 != paramWNode1; wNode5 = wNode5.prev) {
              if (wNode5.status != 1)
                wNode4 = wNode5; 
            } 
            if (wNode2 == wNode4 || U.compareAndSwapObject(paramWNode1, WNEXT, wNode2, wNode2 = wNode4)) {
              if (wNode2 == null && paramWNode1 == this.wtail)
                U.compareAndSwapObject(this, WTAIL, paramWNode1, wNode1); 
              break;
            } 
          } 
          if (wNode1.next == paramWNode1)
            U.compareAndSwapObject(wNode1, WNEXT, paramWNode1, wNode2); 
          Thread thread;
          if (wNode2 != null && (thread = wNode2.thread) != null) {
            wNode2.thread = null;
            U.unpark(thread);
          } 
          WNode wNode3;
          if (wNode1.status != 1 || (wNode3 = wNode1.prev) == null)
            break; 
          paramWNode1.prev = wNode3;
          U.compareAndSwapObject(wNode3, WNEXT, wNode1, wNode2);
        } 
      } 
    } 
    WNode wNode;
    while ((wNode = this.whead) != null) {
      WNode wNode1;
      if ((wNode1 = wNode.next) == null || wNode1.status == 1)
        for (WNode wNode2 = this.wtail; wNode2 != null && wNode2 != wNode; wNode2 = wNode2.prev) {
          if (wNode2.status <= 0)
            wNode1 = wNode2; 
        }  
      if (wNode == this.whead) {
        long l;
        if (wNode1 != null && wNode.status == 0 && ((l = this.state) & 0xFFL) != 128L && (l == 0L || wNode1.mode == 0))
          release(wNode); 
        break;
      } 
    } 
    return (paramBoolean || Thread.interrupted()) ? 1L : 0L;
  }
  
  static  {
    try {
      U = Unsafe.getUnsafe();
      Class clazz1 = StampedLock.class;
      Class clazz2 = WNode.class;
      STATE = U.objectFieldOffset(clazz1.getDeclaredField("state"));
      WHEAD = U.objectFieldOffset(clazz1.getDeclaredField("whead"));
      WTAIL = U.objectFieldOffset(clazz1.getDeclaredField("wtail"));
      WSTATUS = U.objectFieldOffset(clazz2.getDeclaredField("status"));
      WNEXT = U.objectFieldOffset(clazz2.getDeclaredField("next"));
      WCOWAIT = U.objectFieldOffset(clazz2.getDeclaredField("cowait"));
      Class clazz3 = Thread.class;
      PARKBLOCKER = U.objectFieldOffset(clazz3.getDeclaredField("parkBlocker"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
  
  final class ReadLockView implements Lock {
    public void lock() { StampedLock.this.readLock(); }
    
    public void lockInterruptibly() { StampedLock.this.readLockInterruptibly(); }
    
    public boolean tryLock() { return (StampedLock.this.tryReadLock() != 0L); }
    
    public boolean tryLock(long param1Long, TimeUnit param1TimeUnit) throws InterruptedException { return (StampedLock.this.tryReadLock(param1Long, param1TimeUnit) != 0L); }
    
    public void unlock() { StampedLock.this.unstampedUnlockRead(); }
    
    public Condition newCondition() { throw new UnsupportedOperationException(); }
  }
  
  final class ReadWriteLockView implements ReadWriteLock {
    public Lock readLock() { return StampedLock.this.asReadLock(); }
    
    public Lock writeLock() { return StampedLock.this.asWriteLock(); }
  }
  
  static final class WNode {
    final int mode;
    
    WNode(int param1Int, WNode param1WNode) {
      this.mode = param1Int;
      this.prev = param1WNode;
    }
  }
  
  final class WriteLockView implements Lock {
    public void lock() { StampedLock.this.writeLock(); }
    
    public void lockInterruptibly() { StampedLock.this.writeLockInterruptibly(); }
    
    public boolean tryLock() { return (StampedLock.this.tryWriteLock() != 0L); }
    
    public boolean tryLock(long param1Long, TimeUnit param1TimeUnit) throws InterruptedException { return (StampedLock.this.tryWriteLock(param1Long, param1TimeUnit) != 0L); }
    
    public void unlock() { StampedLock.this.unstampedUnlockWrite(); }
    
    public Condition newCondition() { throw new UnsupportedOperationException(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\locks\StampedLock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */