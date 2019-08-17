package java.util.concurrent;

import sun.misc.Contended;
import sun.misc.Unsafe;

public class Exchanger<V> extends Object {
  private static final int ASHIFT = 7;
  
  private static final int MMASK = 255;
  
  private static final int SEQ = 256;
  
  private static final int NCPU;
  
  static final int FULL;
  
  private static final int SPINS = 1024;
  
  private static final Object NULL_ITEM;
  
  private static final Object TIMED_OUT;
  
  private final Participant participant = new Participant();
  
  private static final Unsafe U;
  
  private static final long BOUND;
  
  private static final long SLOT;
  
  private static final long MATCH;
  
  private static final long BLOCKER;
  
  private static final int ABASE;
  
  private final Object arenaExchange(Object paramObject, boolean paramBoolean, long paramLong) {
    Node[] arrayOfNode = this.arena;
    Node node = (Node)this.participant.get();
    int i = node.index;
    while (true) {
      long l;
      Node node1 = (Node)U.getObjectVolatile(arrayOfNode, l = ((i << 7) + ABASE));
      if (node1 != null && U.compareAndSwapObject(arrayOfNode, l, node1, null)) {
        Object object = node1.item;
        node1.match = paramObject;
        Thread thread = node1.parked;
        if (thread != null)
          U.unpark(thread); 
        return object;
      } 
      int j;
      int k;
      if (i <= (k = (j = this.bound) & 0xFF) && node1 == null) {
        node.item = paramObject;
        if (U.compareAndSwapObject(arrayOfNode, l, null, node)) {
          long l1 = (paramBoolean && k == 0) ? (System.nanoTime() + paramLong) : 0L;
          Thread thread = Thread.currentThread();
          int m = node.hash;
          char c = 'Ѐ';
          while (true) {
            Object object = node.match;
            if (object != null) {
              U.putOrderedObject(node, MATCH, null);
              node.item = null;
              node.hash = m;
              return object;
            } 
            if (c > '\000') {
              m ^= m << 1;
              m ^= m >>> 3;
              m ^= m << 10;
              if (m == 0) {
                m = 0x400 | (int)thread.getId();
                continue;
              } 
              if (m < 0 && (--c & 0x1FF) == '\000')
                Thread.yield(); 
              continue;
            } 
            if (U.getObjectVolatile(arrayOfNode, l) != node) {
              c = 'Ѐ';
              continue;
            } 
            if (!thread.isInterrupted() && k == 0 && (!paramBoolean || (paramLong = l1 - System.nanoTime()) > 0L)) {
              U.putObject(thread, BLOCKER, this);
              node.parked = thread;
              if (U.getObjectVolatile(arrayOfNode, l) == node)
                U.park(false, paramLong); 
              node.parked = null;
              U.putObject(thread, BLOCKER, null);
              continue;
            } 
            if (U.getObjectVolatile(arrayOfNode, l) == node && U.compareAndSwapObject(arrayOfNode, l, node, null))
              break; 
          } 
          if (k != 0)
            U.compareAndSwapInt(this, BOUND, j, j + 256 - 1); 
          node.item = null;
          node.hash = m;
          i = node.index >>>= 1;
          if (Thread.interrupted())
            return null; 
          if (paramBoolean && k == 0 && paramLong <= 0L)
            return TIMED_OUT; 
          continue;
        } 
        node.item = null;
        continue;
      } 
      if (node.bound != j) {
        node.bound = j;
        node.collides = 0;
        i = (i != k || k == 0) ? k : (k - 1);
      } else {
        int m;
        if ((m = node.collides) < k || k == FULL || !U.compareAndSwapInt(this, BOUND, j, j + 256 + 1)) {
          node.collides = m + 1;
          i = (i == 0) ? k : (i - 1);
        } else {
          i = k + 1;
        } 
      } 
      node.index = i;
    } 
  }
  
  private final Object slotExchange(Object paramObject, boolean paramBoolean, long paramLong) {
    Node node = (Node)this.participant.get();
    Thread thread = Thread.currentThread();
    if (thread.isInterrupted())
      return null; 
    while (true) {
      Node node1;
      while ((node1 = this.slot) != null) {
        if (U.compareAndSwapObject(this, SLOT, node1, null)) {
          Object object1 = node1.item;
          node1.match = paramObject;
          Thread thread1 = node1.parked;
          if (thread1 != null)
            U.unpark(thread1); 
          return object1;
        } 
        if (NCPU > 1 && this.bound == 0 && U.compareAndSwapInt(this, BOUND, 0, 256))
          this.arena = new Node[FULL + 2 << 7]; 
      } 
      if (this.arena != null)
        return null; 
      node.item = paramObject;
      if (U.compareAndSwapObject(this, SLOT, null, node))
        break; 
      node.item = null;
    } 
    int i = node.hash;
    long l = paramBoolean ? (System.nanoTime() + paramLong) : 0L;
    char c = (NCPU > 1) ? 1024 : 1;
    Object object;
    while ((object = node.match) == null) {
      if (c > '\000') {
        i ^= i << 1;
        i ^= i >>> 3;
        i ^= i << 10;
        if (i == 0) {
          i = 0x400 | (int)thread.getId();
          continue;
        } 
        if (i < 0 && (--c & 0x1FF) == '\000')
          Thread.yield(); 
        continue;
      } 
      if (this.slot != node) {
        c = 'Ѐ';
        continue;
      } 
      if (!thread.isInterrupted() && this.arena == null && (!paramBoolean || (paramLong = l - System.nanoTime()) > 0L)) {
        U.putObject(thread, BLOCKER, this);
        node.parked = thread;
        if (this.slot == node)
          U.park(false, paramLong); 
        node.parked = null;
        U.putObject(thread, BLOCKER, null);
        continue;
      } 
      if (U.compareAndSwapObject(this, SLOT, node, null)) {
        object = (paramBoolean && paramLong <= 0L && !thread.isInterrupted()) ? TIMED_OUT : null;
        break;
      } 
    } 
    U.putOrderedObject(node, MATCH, null);
    node.item = null;
    node.hash = i;
    return object;
  }
  
  public V exchange(V paramV) throws InterruptedException {
    Object object2 = (paramV == null) ? NULL_ITEM : paramV;
    Object object1;
    if ((this.arena != null || (object1 = slotExchange(object2, false, 0L)) == null) && (Thread.interrupted() || (object1 = arenaExchange(object2, false, 0L)) == null))
      throw new InterruptedException(); 
    return (V)((object1 == NULL_ITEM) ? null : object1);
  }
  
  public V exchange(V paramV, long paramLong, TimeUnit paramTimeUnit) throws InterruptedException, TimeoutException {
    Object object2 = (paramV == null) ? NULL_ITEM : paramV;
    long l = paramTimeUnit.toNanos(paramLong);
    Object object1;
    if ((this.arena != null || (object1 = slotExchange(object2, true, l)) == null) && (Thread.interrupted() || (object1 = arenaExchange(object2, true, l)) == null))
      throw new InterruptedException(); 
    if (object1 == TIMED_OUT)
      throw new TimeoutException(); 
    return (V)((object1 == NULL_ITEM) ? null : object1);
  }
  
  static  {
    int i;
    NCPU = Runtime.getRuntime().availableProcessors();
    FULL = (NCPU >= 510) ? 255 : (NCPU >>> 1);
    NULL_ITEM = new Object();
    TIMED_OUT = new Object();
    try {
      U = Unsafe.getUnsafe();
      Class clazz1 = Exchanger.class;
      Class clazz2 = Node.class;
      Class clazz3 = Node[].class;
      Class clazz4 = Thread.class;
      BOUND = U.objectFieldOffset(clazz1.getDeclaredField("bound"));
      SLOT = U.objectFieldOffset(clazz1.getDeclaredField("slot"));
      MATCH = U.objectFieldOffset(clazz2.getDeclaredField("match"));
      BLOCKER = U.objectFieldOffset(clazz4.getDeclaredField("parkBlocker"));
      i = U.arrayIndexScale(clazz3);
      ABASE = U.arrayBaseOffset(clazz3) + 128;
    } catch (Exception exception) {
      throw new Error(exception);
    } 
    if ((i & i - 1) != 0 || i > 128)
      throw new Error("Unsupported array scale"); 
  }
  
  @Contended
  static final class Node {
    int index;
    
    int bound;
    
    int collides;
    
    int hash;
    
    Object item;
  }
  
  static final class Participant extends ThreadLocal<Node> {
    public Exchanger.Node initialValue() { return new Exchanger.Node(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\Exchanger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */