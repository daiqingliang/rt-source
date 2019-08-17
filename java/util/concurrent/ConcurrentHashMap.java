package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.LongBinaryOperator;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongBiFunction;
import java.util.function.ToLongFunction;
import sun.misc.Contended;
import sun.misc.Unsafe;

public class ConcurrentHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable {
  private static final long serialVersionUID = 7249069246763182397L;
  
  private static final int MAXIMUM_CAPACITY = 1073741824;
  
  private static final int DEFAULT_CAPACITY = 16;
  
  static final int MAX_ARRAY_SIZE = 2147483639;
  
  private static final int DEFAULT_CONCURRENCY_LEVEL = 16;
  
  private static final float LOAD_FACTOR = 0.75F;
  
  static final int TREEIFY_THRESHOLD = 8;
  
  static final int UNTREEIFY_THRESHOLD = 6;
  
  static final int MIN_TREEIFY_CAPACITY = 64;
  
  private static final int MIN_TRANSFER_STRIDE = 16;
  
  private static int RESIZE_STAMP_BITS = 16;
  
  private static final int MAX_RESIZERS = (1 << 32 - RESIZE_STAMP_BITS) - 1;
  
  private static final int RESIZE_STAMP_SHIFT = 32 - RESIZE_STAMP_BITS;
  
  static final int MOVED = -1;
  
  static final int TREEBIN = -2;
  
  static final int RESERVED = -3;
  
  static final int HASH_BITS = 2147483647;
  
  static final int NCPU = Runtime.getRuntime().availableProcessors();
  
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("segments", Segment[].class), new ObjectStreamField("segmentMask", int.class), new ObjectStreamField("segmentShift", int.class) };
  
  private KeySetView<K, V> keySet;
  
  private ValuesView<K, V> values;
  
  private EntrySetView<K, V> entrySet;
  
  private static final Unsafe U;
  
  private static final long SIZECTL;
  
  private static final long TRANSFERINDEX;
  
  private static final long BASECOUNT;
  
  private static final long CELLSBUSY;
  
  private static final long CELLVALUE;
  
  private static final long ABASE;
  
  private static final int ASHIFT;
  
  static final int spread(int paramInt) { return (paramInt ^ paramInt >>> 16) & 0x7FFFFFFF; }
  
  private static final int tableSizeFor(int paramInt) {
    int i = paramInt - 1;
    i |= i >>> 1;
    i |= i >>> 2;
    i |= i >>> 4;
    i |= i >>> 8;
    i |= i >>> 16;
    return (i < 0) ? 1 : ((i >= 1073741824) ? 1073741824 : (i + 1));
  }
  
  static Class<?> comparableClassFor(Object paramObject) {
    if (paramObject instanceof Comparable) {
      Class clazz;
      if ((clazz = paramObject.getClass()) == String.class)
        return clazz; 
      Type[] arrayOfType;
      if ((arrayOfType = clazz.getGenericInterfaces()) != null)
        for (byte b = 0; b < arrayOfType.length; b++) {
          Type[] arrayOfType1;
          Type type;
          ParameterizedType parameterizedType;
          if (type = arrayOfType[b] instanceof ParameterizedType && (parameterizedType = (ParameterizedType)type).getRawType() == Comparable.class && (arrayOfType1 = parameterizedType.getActualTypeArguments()) != null && arrayOfType1.length == 1 && arrayOfType1[false] == clazz)
            return clazz; 
        }  
    } 
    return null;
  }
  
  static int compareComparables(Class<?> paramClass, Object paramObject1, Object paramObject2) { return (paramObject2 == null || paramObject2.getClass() != paramClass) ? 0 : ((Comparable)paramObject1).compareTo(paramObject2); }
  
  static final <K, V> Node<K, V> tabAt(Node<K, V>[] paramArrayOfNode, int paramInt) { return (Node)U.getObjectVolatile(paramArrayOfNode, (paramInt << ASHIFT) + ABASE); }
  
  static final <K, V> boolean casTabAt(Node<K, V>[] paramArrayOfNode, int paramInt, Node<K, V> paramNode1, Node<K, V> paramNode2) { return U.compareAndSwapObject(paramArrayOfNode, (paramInt << ASHIFT) + ABASE, paramNode1, paramNode2); }
  
  static final <K, V> void setTabAt(Node<K, V>[] paramArrayOfNode, int paramInt, Node<K, V> paramNode) { U.putObjectVolatile(paramArrayOfNode, (paramInt << ASHIFT) + ABASE, paramNode); }
  
  public ConcurrentHashMap() {}
  
  public ConcurrentHashMap(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException(); 
    int i = (paramInt >= 536870912) ? 1073741824 : tableSizeFor(paramInt + (paramInt >>> 1) + 1);
    this.sizeCtl = i;
  }
  
  public ConcurrentHashMap(Map<? extends K, ? extends V> paramMap) {
    this.sizeCtl = 16;
    putAll(paramMap);
  }
  
  public ConcurrentHashMap(int paramInt, float paramFloat) { this(paramInt, paramFloat, 1); }
  
  public ConcurrentHashMap(int paramInt1, float paramFloat, int paramInt2) {
    if (paramFloat <= 0.0F || paramInt1 < 0 || paramInt2 <= 0)
      throw new IllegalArgumentException(); 
    if (paramInt1 < paramInt2)
      paramInt1 = paramInt2; 
    long l = (long)(1.0D + ((float)paramInt1 / paramFloat));
    int i = (l >= 1073741824L) ? 1073741824 : tableSizeFor((int)l);
    this.sizeCtl = i;
  }
  
  public int size() {
    long l = sumCount();
    return (l < 0L) ? 0 : ((l > 2147483647L) ? Integer.MAX_VALUE : (int)l);
  }
  
  public boolean isEmpty() { return (sumCount() <= 0L); }
  
  public V get(Object paramObject) {
    int j = spread(paramObject.hashCode());
    Node[] arrayOfNode;
    Node node;
    int i;
    if ((arrayOfNode = this.table) != null && (i = arrayOfNode.length) > 0 && (node = tabAt(arrayOfNode, i - true & j)) != null) {
      int k;
      if ((k = node.hash) == j) {
        Object object;
        if ((object = node.key) == paramObject || (object != null && paramObject.equals(object)))
          return (V)node.val; 
      } else if (k < 0) {
        Node node1;
        return (V)(((node1 = node.find(j, paramObject)) != null) ? node1.val : null);
      } 
      while ((node = node.next) != null) {
        Object object;
        if (node.hash == j && ((object = node.key) == paramObject || (object != null && paramObject.equals(object))))
          return (V)node.val; 
      } 
    } 
    return null;
  }
  
  public boolean containsKey(Object paramObject) { return (get(paramObject) != null); }
  
  public boolean containsValue(Object paramObject) {
    if (paramObject == null)
      throw new NullPointerException(); 
    Node[] arrayOfNode;
    if ((arrayOfNode = this.table) != null) {
      Traverser traverser = new Traverser(arrayOfNode, arrayOfNode.length, 0, arrayOfNode.length);
      Node node;
      while ((node = traverser.advance()) != null) {
        Object object;
        if ((object = node.val) == paramObject || (object != null && paramObject.equals(object)))
          return true; 
      } 
    } 
    return false;
  }
  
  public V put(K paramK, V paramV) { return (V)putVal(paramK, paramV, false); }
  
  final V putVal(K paramK, V paramV, boolean paramBoolean) {
    if (paramK == null || paramV == null)
      throw new NullPointerException(); 
    int i = spread(paramK.hashCode());
    byte b = 0;
    Node[] arrayOfNode = this.table;
    while (true) {
      int j;
      if (arrayOfNode == null || (j = arrayOfNode.length) == 0) {
        arrayOfNode = initTable();
        continue;
      } 
      Node node;
      int k;
      if ((node = tabAt(arrayOfNode, k = j - true & i)) == null) {
        if (casTabAt(arrayOfNode, k, null, new Node(i, paramK, paramV, null)))
          break; 
        continue;
      } 
      int m;
      if ((m = node.hash) == -1) {
        arrayOfNode = helpTransfer(arrayOfNode, node);
        continue;
      } 
      Object object = null;
      synchronized (node) {
        if (tabAt(arrayOfNode, k) == node)
          if (m >= 0) {
            b = 1;
            Node node1 = node;
            while (true) {
              Object object1;
              if (node1.hash == i && ((object1 = node1.key) == paramK || (object1 != null && paramK.equals(object1)))) {
                object = node1.val;
                if (!paramBoolean)
                  node1.val = paramV; 
                break;
              } 
              Node node2 = node1;
              if ((node1 = node1.next) == null) {
                node2.next = new Node(i, paramK, paramV, null);
                break;
              } 
              b++;
            } 
          } else {
            b = 2;
            TreeNode treeNode;
            if (node instanceof TreeBin && (treeNode = ((TreeBin)node).putTreeVal(i, paramK, paramV)) != null) {
              object = treeNode.val;
              if (!paramBoolean)
                treeNode.val = paramV; 
            } 
          }  
      } 
      if (b != 0) {
        if (b >= 8)
          treeifyBin(arrayOfNode, k); 
        if (object != null)
          return (V)object; 
        break;
      } 
    } 
    addCount(1L, b);
    return null;
  }
  
  public void putAll(Map<? extends K, ? extends V> paramMap) {
    tryPresize(paramMap.size());
    for (Map.Entry entry : paramMap.entrySet())
      putVal(entry.getKey(), entry.getValue(), false); 
  }
  
  public V remove(Object paramObject) { return (V)replaceNode(paramObject, null, null); }
  
  final V replaceNode(Object paramObject1, V paramV, Object paramObject2) {
    int i = spread(paramObject1.hashCode());
    Node[] arrayOfNode = this.table;
    Node node;
    int j;
    int k;
    while (arrayOfNode != null && (j = arrayOfNode.length) != 0 && (node = tabAt(arrayOfNode, k = j - true & i)) != null) {
      int m;
      if ((m = node.hash) == -1) {
        arrayOfNode = helpTransfer(arrayOfNode, node);
        continue;
      } 
      Object object = null;
      boolean bool = false;
      synchronized (node) {
        if (tabAt(arrayOfNode, k) == node)
          if (m >= 0) {
            bool = true;
            Node node1 = node;
            Node node2 = null;
            do {
              Object object1;
              if (node1.hash == i && ((object1 = node1.key) == paramObject1 || (object1 != null && paramObject1.equals(object1)))) {
                Object object2 = node1.val;
                if (paramObject2 == null || paramObject2 == object2 || (object2 != null && paramObject2.equals(object2))) {
                  object = object2;
                  if (paramV != null) {
                    node1.val = paramV;
                    break;
                  } 
                  if (node2 != null) {
                    node2.next = node1.next;
                    break;
                  } 
                  setTabAt(arrayOfNode, k, node1.next);
                } 
                break;
              } 
              node2 = node1;
            } while ((node1 = node1.next) != null);
          } else {
            bool = true;
            TreeBin treeBin = (TreeBin)node;
            TreeNode treeNode1;
            TreeNode treeNode2;
            if (node instanceof TreeBin && (treeNode1 = treeBin.root) != null && (treeNode2 = treeNode1.findTreeNode(i, paramObject1, null)) != null) {
              Object object1 = treeNode2.val;
              if (paramObject2 == null || paramObject2 == object1 || (object1 != null && paramObject2.equals(object1))) {
                object = object1;
                if (paramV != null) {
                  treeNode2.val = paramV;
                } else if (treeBin.removeTreeNode(treeNode2)) {
                  setTabAt(arrayOfNode, k, untreeify(treeBin.first));
                } 
              } 
            } 
          }  
      } 
      if (bool) {
        if (object != null) {
          if (paramV == null)
            addCount(-1L, -1); 
          return (V)object;
        } 
        break;
      } 
    } 
    return null;
  }
  
  public void clear() {
    long l = 0L;
    byte b = 0;
    Node[] arrayOfNode = this.table;
    while (arrayOfNode != null && b < arrayOfNode.length) {
      Node node = tabAt(arrayOfNode, b);
      if (node == null) {
        b++;
        continue;
      } 
      int i;
      if ((i = node.hash) == -1) {
        arrayOfNode = helpTransfer(arrayOfNode, node);
        b = 0;
        continue;
      } 
      synchronized (node) {
        if (tabAt(arrayOfNode, b) == node) {
          for (Node node1 = (i >= 0) ? node : ((node instanceof TreeBin) ? ((TreeBin)node).first : null); node1 != null; node1 = node1.next)
            l--; 
          setTabAt(arrayOfNode, b++, null);
        } 
      } 
    } 
    if (l != 0L)
      addCount(l, -1); 
  }
  
  public KeySetView<K, V> keySet() {
    KeySetView keySetView;
    return ((keySetView = this.keySet) != null) ? keySetView : (this.keySet = new KeySetView(this, null));
  }
  
  public Collection<V> values() {
    ValuesView valuesView;
    return ((valuesView = this.values) != null) ? valuesView : (this.values = new ValuesView(this));
  }
  
  public Set<Map.Entry<K, V>> entrySet() {
    EntrySetView entrySetView;
    return ((entrySetView = this.entrySet) != null) ? entrySetView : (this.entrySet = new EntrySetView(this));
  }
  
  public int hashCode() {
    int i = 0;
    Node[] arrayOfNode;
    if ((arrayOfNode = this.table) != null) {
      Traverser traverser = new Traverser(arrayOfNode, arrayOfNode.length, 0, arrayOfNode.length);
      Node node;
      while ((node = traverser.advance()) != null)
        i += (node.key.hashCode() ^ node.val.hashCode()); 
    } 
    return i;
  }
  
  public String toString() {
    Node[] arrayOfNode;
    byte b = ((arrayOfNode = this.table) == null) ? 0 : arrayOfNode.length;
    Traverser traverser = new Traverser(arrayOfNode, b, 0, b);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append('{');
    Node node;
    if ((node = traverser.advance()) != null)
      while (true) {
        Object object1 = node.key;
        Object object2 = node.val;
        stringBuilder.append((object1 == this) ? "(this Map)" : object1);
        stringBuilder.append('=');
        stringBuilder.append((object2 == this) ? "(this Map)" : object2);
        if ((node = traverser.advance()) == null)
          break; 
        stringBuilder.append(',').append(' ');
      }  
    return stringBuilder.append('}').toString();
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject != this) {
      if (!(paramObject instanceof Map))
        return false; 
      Map map = (Map)paramObject;
      Node[] arrayOfNode;
      byte b = ((arrayOfNode = this.table) == null) ? 0 : arrayOfNode.length;
      Traverser traverser = new Traverser(arrayOfNode, b, 0, b);
      Node node;
      while ((node = traverser.advance()) != null) {
        Object object1 = node.val;
        Object object2 = map.get(node.key);
        if (object2 == null || (object2 != object1 && !object2.equals(object1)))
          return false; 
      } 
      for (Map.Entry entry : map.entrySet()) {
        Object object1;
        Object object2;
        Object object3;
        if ((object1 = entry.getKey()) == null || (object2 = entry.getValue()) == null || (object3 = get(object1)) == null || (object2 != object3 && !object2.equals(object3)))
          return false; 
      } 
    } 
    return true;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    byte b1 = 0;
    boolean bool;
    for (bool = true; bool < 16; bool <<= true)
      b1++; 
    byte b2 = 32 - b1;
    byte b3 = bool - true;
    Segment[] arrayOfSegment = (Segment[])new Segment[16];
    for (byte b4 = 0; b4 < arrayOfSegment.length; b4++)
      arrayOfSegment[b4] = new Segment(0.75F); 
    paramObjectOutputStream.putFields().put("segments", arrayOfSegment);
    paramObjectOutputStream.putFields().put("segmentShift", b2);
    paramObjectOutputStream.putFields().put("segmentMask", b3);
    paramObjectOutputStream.writeFields();
    Node[] arrayOfNode;
    if ((arrayOfNode = this.table) != null) {
      Traverser traverser = new Traverser(arrayOfNode, arrayOfNode.length, 0, arrayOfNode.length);
      Node node;
      while ((node = traverser.advance()) != null) {
        paramObjectOutputStream.writeObject(node.key);
        paramObjectOutputStream.writeObject(node.val);
      } 
    } 
    paramObjectOutputStream.writeObject(null);
    paramObjectOutputStream.writeObject(null);
    arrayOfSegment = null;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    this.sizeCtl = -1;
    paramObjectInputStream.defaultReadObject();
    long l = 0L;
    Node node = null;
    while (true) {
      Object object1 = paramObjectInputStream.readObject();
      Object object2 = paramObjectInputStream.readObject();
      if (object1 != null && object2 != null) {
        node = new Node(spread(object1.hashCode()), object1, object2, node);
        l++;
        continue;
      } 
      break;
    } 
    if (l == 0L) {
      this.sizeCtl = 0;
    } else {
      int i;
      if (l >= 536870912L) {
        i = 1073741824;
      } else {
        int k = (int)l;
        i = tableSizeFor(k + (k >>> 1) + 1);
      } 
      Node[] arrayOfNode = (Node[])new Node[i];
      int j = i - 1;
      long l1 = 0L;
      while (node != null) {
        boolean bool;
        Node node1 = node.next;
        int k = node.hash;
        int m = k & j;
        Node node2;
        if ((node2 = tabAt(arrayOfNode, m)) == null) {
          bool = true;
        } else {
          Object object = node.key;
          if (node2.hash < 0) {
            TreeBin treeBin = (TreeBin)node2;
            if (treeBin.putTreeVal(k, object, node.val) == null)
              l1++; 
            bool = false;
          } else {
            byte b = 0;
            bool = true;
            Node node3;
            for (node3 = node2; node3 != null; node3 = node3.next) {
              Object object1;
              if (node3.hash == k && ((object1 = node3.key) == object || (object1 != null && object.equals(object1)))) {
                bool = false;
                break;
              } 
              b++;
            } 
            if (bool && b >= 8) {
              bool = false;
              l1++;
              node.next = node2;
              TreeNode treeNode1 = null;
              TreeNode treeNode2 = null;
              for (node3 = node; node3 != null; node3 = node3.next) {
                TreeNode treeNode = new TreeNode(node3.hash, node3.key, node3.val, null, null);
                if ((treeNode.prev = treeNode2) == null) {
                  treeNode1 = treeNode;
                } else {
                  treeNode2.next = treeNode;
                } 
                treeNode2 = treeNode;
              } 
              setTabAt(arrayOfNode, m, new TreeBin(treeNode1));
            } 
          } 
        } 
        if (bool) {
          l1++;
          node.next = node2;
          setTabAt(arrayOfNode, m, node);
        } 
        node = node1;
      } 
      this.table = arrayOfNode;
      this.sizeCtl = i - (i >>> 2);
      this.baseCount = l1;
    } 
  }
  
  public V putIfAbsent(K paramK, V paramV) { return (V)putVal(paramK, paramV, true); }
  
  public boolean remove(Object paramObject1, Object paramObject2) {
    if (paramObject1 == null)
      throw new NullPointerException(); 
    return (paramObject2 != null && replaceNode(paramObject1, null, paramObject2) != null);
  }
  
  public boolean replace(K paramK, V paramV1, V paramV2) {
    if (paramK == null || paramV1 == null || paramV2 == null)
      throw new NullPointerException(); 
    return (replaceNode(paramK, paramV2, paramV1) != null);
  }
  
  public V replace(K paramK, V paramV) {
    if (paramK == null || paramV == null)
      throw new NullPointerException(); 
    return (V)replaceNode(paramK, paramV, null);
  }
  
  public V getOrDefault(Object paramObject, V paramV) {
    Object object;
    return ((object = get(paramObject)) == null) ? paramV : object;
  }
  
  public void forEach(BiConsumer<? super K, ? super V> paramBiConsumer) {
    if (paramBiConsumer == null)
      throw new NullPointerException(); 
    Node[] arrayOfNode;
    if ((arrayOfNode = this.table) != null) {
      Traverser traverser = new Traverser(arrayOfNode, arrayOfNode.length, 0, arrayOfNode.length);
      Node node;
      while ((node = traverser.advance()) != null)
        paramBiConsumer.accept(node.key, node.val); 
    } 
  }
  
  public void replaceAll(BiFunction<? super K, ? super V, ? extends V> paramBiFunction) {
    if (paramBiFunction == null)
      throw new NullPointerException(); 
    Node[] arrayOfNode;
    if ((arrayOfNode = this.table) != null) {
      Traverser traverser = new Traverser(arrayOfNode, arrayOfNode.length, 0, arrayOfNode.length);
      Node node;
      label22: while ((node = traverser.advance()) != null) {
        Object object1 = node.val;
        Object object2 = node.key;
        while (true) {
          Object object = paramBiFunction.apply(object2, object1);
          if (object == null)
            throw new NullPointerException(); 
          if (replaceNode(object2, object, object1) == null) {
            if ((object1 = get(object2)) == null)
              continue label22; 
            continue;
          } 
          continue label22;
        } 
      } 
    } 
  }
  
  public V computeIfAbsent(K paramK, Function<? super K, ? extends V> paramFunction) {
    if (paramK == null || paramFunction == null)
      throw new NullPointerException(); 
    int i = spread(paramK.hashCode());
    Object object = null;
    byte b = 0;
    arrayOfNode = this.table;
    while (true) {
      int j;
      if (arrayOfNode == null || (j = arrayOfNode.length) == 0) {
        arrayOfNode = initTable();
        continue;
      } 
      Node node;
      if ((node = tabAt(arrayOfNode, k = j - true & i)) == null) {
        ReservationNode reservationNode = new ReservationNode();
        synchronized (reservationNode) {
          if (casTabAt(arrayOfNode, k, null, reservationNode)) {
            b = 1;
            node1 = null;
            try {
              if ((object = paramFunction.apply(paramK)) != null)
                node1 = new Node(i, paramK, object, null); 
            } finally {
              setTabAt(arrayOfNode, k, node1);
            } 
          } 
        } 
        if (b)
          break; 
        continue;
      } 
      int m;
      if ((m = node.hash) == -1) {
        arrayOfNode = helpTransfer(arrayOfNode, node);
        continue;
      } 
      boolean bool = false;
      synchronized (node) {
        if (tabAt(arrayOfNode, k) == node)
          if (m >= 0) {
            b = 1;
            Node node1 = node;
            while (true) {
              Object object1;
              if (node1.hash == i && ((object1 = node1.key) == paramK || (object1 != null && paramK.equals(object1)))) {
                object = node1.val;
                break;
              } 
              Node node2 = node1;
              if ((node1 = node1.next) == null) {
                if ((object = paramFunction.apply(paramK)) != null) {
                  bool = true;
                  node2.next = new Node(i, paramK, object, null);
                } 
                break;
              } 
              b++;
            } 
          } else if (node instanceof TreeBin) {
            b = 2;
            TreeBin treeBin = (TreeBin)node;
            TreeNode treeNode1;
            TreeNode treeNode2;
            if ((treeNode1 = treeBin.root) != null && (treeNode2 = treeNode1.findTreeNode(i, paramK, null)) != null) {
              object = treeNode2.val;
            } else if ((object = paramFunction.apply(paramK)) != null) {
              bool = true;
              treeBin.putTreeVal(i, paramK, object);
            } 
          }  
      } 
      if (b != 0) {
        if (b >= 8)
          treeifyBin(arrayOfNode, k); 
        if (!bool)
          return (V)object; 
        break;
      } 
    } 
    if (object != null)
      addCount(1L, b); 
    return (V)object;
  }
  
  public V computeIfPresent(K paramK, BiFunction<? super K, ? super V, ? extends V> paramBiFunction) {
    if (paramK == null || paramBiFunction == null)
      throw new NullPointerException(); 
    int i = spread(paramK.hashCode());
    Object object = null;
    byte b = 0;
    byte b1 = 0;
    Node[] arrayOfNode = this.table;
    while (true) {
      int j;
      if (arrayOfNode == null || (j = arrayOfNode.length) == 0) {
        arrayOfNode = initTable();
        continue;
      } 
      Node node;
      int k;
      if ((node = tabAt(arrayOfNode, k = j - true & i)) == null)
        break; 
      int m;
      if ((m = node.hash) == -1) {
        arrayOfNode = helpTransfer(arrayOfNode, node);
        continue;
      } 
      synchronized (node) {
        if (tabAt(arrayOfNode, k) == node)
          if (m >= 0) {
            b1 = 1;
            Node node1 = node;
            Node node2 = null;
            while (true) {
              Object object1;
              if (node1.hash == i && ((object1 = node1.key) == paramK || (object1 != null && paramK.equals(object1)))) {
                object = paramBiFunction.apply(paramK, node1.val);
                if (object != null) {
                  node1.val = object;
                  break;
                } 
                b = -1;
                Node node3 = node1.next;
                if (node2 != null) {
                  node2.next = node3;
                  break;
                } 
                setTabAt(arrayOfNode, k, node3);
                break;
              } 
              node2 = node1;
              if ((node1 = node1.next) == null)
                break; 
              b1++;
            } 
          } else {
            b1 = 2;
            TreeBin treeBin = (TreeBin)node;
            TreeNode treeNode1;
            TreeNode treeNode2;
            if (node instanceof TreeBin && (treeNode1 = treeBin.root) != null && (treeNode2 = treeNode1.findTreeNode(i, paramK, null)) != null) {
              object = paramBiFunction.apply(paramK, treeNode2.val);
              if (object != null) {
                treeNode2.val = object;
              } else {
                b = -1;
                if (treeBin.removeTreeNode(treeNode2))
                  setTabAt(arrayOfNode, k, untreeify(treeBin.first)); 
              } 
            } 
          }  
      } 
      if (b1 != 0)
        break; 
    } 
    if (b != 0)
      addCount(b, b1); 
    return (V)object;
  }
  
  public V compute(K paramK, BiFunction<? super K, ? super V, ? extends V> paramBiFunction) {
    if (paramK == null || paramBiFunction == null)
      throw new NullPointerException(); 
    int i = spread(paramK.hashCode());
    Object object = null;
    byte b = 0;
    byte b1 = 0;
    arrayOfNode = this.table;
    while (true) {
      int j;
      if (arrayOfNode == null || (j = arrayOfNode.length) == 0) {
        arrayOfNode = initTable();
        continue;
      } 
      Node node;
      if ((node = tabAt(arrayOfNode, k = j - true & i)) == null) {
        ReservationNode reservationNode = new ReservationNode();
        synchronized (reservationNode) {
          if (casTabAt(arrayOfNode, k, null, reservationNode)) {
            b1 = 1;
            node1 = null;
            try {
              if ((object = paramBiFunction.apply(paramK, null)) != null) {
                b = 1;
                node1 = new Node(i, paramK, object, null);
              } 
            } finally {
              setTabAt(arrayOfNode, k, node1);
            } 
          } 
        } 
        if (b1)
          break; 
        continue;
      } 
      int m;
      if ((m = node.hash) == -1) {
        arrayOfNode = helpTransfer(arrayOfNode, node);
        continue;
      } 
      synchronized (node) {
        if (tabAt(arrayOfNode, k) == node)
          if (m >= 0) {
            b1 = 1;
            Node node1 = node;
            Node node2 = null;
            while (true) {
              Object object1;
              if (node1.hash == i && ((object1 = node1.key) == paramK || (object1 != null && paramK.equals(object1)))) {
                object = paramBiFunction.apply(paramK, node1.val);
                if (object != null) {
                  node1.val = object;
                  break;
                } 
                b = -1;
                Node node3 = node1.next;
                if (node2 != null) {
                  node2.next = node3;
                  break;
                } 
                setTabAt(arrayOfNode, k, node3);
                break;
              } 
              node2 = node1;
              if ((node1 = node1.next) == null) {
                object = paramBiFunction.apply(paramK, null);
                if (object != null) {
                  b = 1;
                  node2.next = new Node(i, paramK, object, null);
                } 
                break;
              } 
              b1++;
            } 
          } else if (node instanceof TreeBin) {
            TreeNode treeNode2;
            b1 = 1;
            TreeBin treeBin = (TreeBin)node;
            TreeNode treeNode1;
            if ((treeNode1 = treeBin.root) != null) {
              treeNode2 = treeNode1.findTreeNode(i, paramK, null);
            } else {
              treeNode2 = null;
            } 
            Object object1 = (treeNode2 == null) ? null : treeNode2.val;
            object = paramBiFunction.apply(paramK, object1);
            if (object != null) {
              if (treeNode2 != null) {
                treeNode2.val = object;
              } else {
                b = 1;
                treeBin.putTreeVal(i, paramK, object);
              } 
            } else if (treeNode2 != null) {
              b = -1;
              if (treeBin.removeTreeNode(treeNode2))
                setTabAt(arrayOfNode, k, untreeify(treeBin.first)); 
            } 
          }  
      } 
      if (b1 != 0) {
        if (b1 >= 8)
          treeifyBin(arrayOfNode, k); 
        break;
      } 
    } 
    if (b != 0)
      addCount(b, b1); 
    return (V)object;
  }
  
  public V merge(K paramK, V paramV, BiFunction<? super V, ? super V, ? extends V> paramBiFunction) {
    V v;
    if (paramK == null || paramV == null || paramBiFunction == null)
      throw new NullPointerException(); 
    int i = spread(paramK.hashCode());
    Object object = null;
    byte b = 0;
    byte b1 = 0;
    Node[] arrayOfNode = this.table;
    while (true) {
      int j;
      if (arrayOfNode == null || (j = arrayOfNode.length) == 0) {
        arrayOfNode = initTable();
        continue;
      } 
      Node node;
      int k;
      if ((node = tabAt(arrayOfNode, k = j - true & i)) == null) {
        if (casTabAt(arrayOfNode, k, null, new Node(i, paramK, paramV, null))) {
          b = 1;
          v = paramV;
          break;
        } 
        continue;
      } 
      int m;
      if ((m = node.hash) == -1) {
        arrayOfNode = helpTransfer(arrayOfNode, node);
        continue;
      } 
      synchronized (node) {
        if (tabAt(arrayOfNode, k) == node)
          if (m >= 0) {
            b1 = 1;
            Node node1 = node;
            Node node2 = null;
            while (true) {
              Object object1;
              if (node1.hash == i && ((object1 = node1.key) == paramK || (object1 != null && paramK.equals(object1)))) {
                object = paramBiFunction.apply(node1.val, paramV);
                if (object != null) {
                  node1.val = object;
                  break;
                } 
                b = -1;
                Node node3 = node1.next;
                if (node2 != null) {
                  node2.next = node3;
                  break;
                } 
                setTabAt(arrayOfNode, k, node3);
                break;
              } 
              node2 = node1;
              if ((node1 = node1.next) == null) {
                b = 1;
                v = paramV;
                node2.next = new Node(i, paramK, v, null);
                break;
              } 
              b1++;
            } 
          } else if (node instanceof TreeBin) {
            b1 = 2;
            TreeBin treeBin = (TreeBin)node;
            TreeNode treeNode1 = treeBin.root;
            TreeNode treeNode2 = (treeNode1 == null) ? null : treeNode1.findTreeNode(i, paramK, null);
            v = (treeNode2 == null) ? paramV : paramBiFunction.apply(treeNode2.val, paramV);
            if (v != null) {
              if (treeNode2 != null) {
                treeNode2.val = v;
              } else {
                b = 1;
                treeBin.putTreeVal(i, paramK, v);
              } 
            } else if (treeNode2 != null) {
              b = -1;
              if (treeBin.removeTreeNode(treeNode2))
                setTabAt(arrayOfNode, k, untreeify(treeBin.first)); 
            } 
          }  
      } 
      if (b1 != 0) {
        if (b1 >= 8)
          treeifyBin(arrayOfNode, k); 
        break;
      } 
    } 
    if (b != 0)
      addCount(b, b1); 
    return v;
  }
  
  public boolean contains(Object paramObject) { return containsValue(paramObject); }
  
  public Enumeration<K> keys() {
    Node[] arrayOfNode;
    byte b = ((arrayOfNode = this.table) == null) ? 0 : arrayOfNode.length;
    return new KeyIterator(arrayOfNode, b, 0, b, this);
  }
  
  public Enumeration<V> elements() {
    Node[] arrayOfNode;
    byte b = ((arrayOfNode = this.table) == null) ? 0 : arrayOfNode.length;
    return new ValueIterator(arrayOfNode, b, 0, b, this);
  }
  
  public long mappingCount() {
    long l = sumCount();
    return (l < 0L) ? 0L : l;
  }
  
  public static <K> KeySetView<K, Boolean> newKeySet() { return new KeySetView(new ConcurrentHashMap(), Boolean.TRUE); }
  
  public static <K> KeySetView<K, Boolean> newKeySet(int paramInt) { return new KeySetView(new ConcurrentHashMap(paramInt), Boolean.TRUE); }
  
  public KeySetView<K, V> keySet(V paramV) {
    if (paramV == null)
      throw new NullPointerException(); 
    return new KeySetView(this, paramV);
  }
  
  static final int resizeStamp(int paramInt) { return Integer.numberOfLeadingZeros(paramInt) | 1 << RESIZE_STAMP_BITS - 1; }
  
  private final Node<K, V>[] initTable() {
    Node[] arrayOfNode;
    while ((arrayOfNode = this.table) == null || arrayOfNode.length == 0) {
      int i;
      if ((i = this.sizeCtl) < 0) {
        Thread.yield();
        continue;
      } 
      if (U.compareAndSwapInt(this, SIZECTL, i, -1))
        try {
          if ((arrayOfNode = this.table) == null || arrayOfNode.length == 0) {
            int j = (i > 0) ? i : 16;
            Node[] arrayOfNode1 = (Node[])new Node[j];
            this.table = arrayOfNode = arrayOfNode1;
            i = j - (j >>> 2);
          } 
          this.sizeCtl = i;
        } finally {
          this.sizeCtl = i;
        }  
    } 
    return arrayOfNode;
  }
  
  private final void addCount(long paramLong, int paramInt) {
    CounterCell[] arrayOfCounterCell;
    long l1;
    long l2;
    if ((arrayOfCounterCell = this.counterCells) != null || !U.compareAndSwapLong(this, BASECOUNT, l1 = this.baseCount, l2 = l1 + paramLong)) {
      boolean bool = true;
      CounterCell counterCell;
      long l;
      int i;
      if (arrayOfCounterCell == null || (i = arrayOfCounterCell.length - 1) < 0 || (counterCell = arrayOfCounterCell[ThreadLocalRandom.getProbe() & i]) == null || !(bool = U.compareAndSwapLong(counterCell, CELLVALUE, l = counterCell.value, l + paramLong))) {
        fullAddCount(paramLong, bool);
        return;
      } 
      if (paramInt <= 1)
        return; 
      l2 = sumCount();
    } 
    if (paramInt >= 0) {
      Node[] arrayOfNode;
      int i;
      int j;
      while (l2 >= (j = this.sizeCtl) && (arrayOfNode = this.table) != null && (i = arrayOfNode.length) < 1073741824) {
        int k = resizeStamp(i);
        if (j < 0) {
          Node[] arrayOfNode1;
          if (j >>> RESIZE_STAMP_SHIFT != k || j == k + 1 || j == k + MAX_RESIZERS || (arrayOfNode1 = this.nextTable) == null || this.transferIndex <= 0)
            break; 
          if (U.compareAndSwapInt(this, SIZECTL, j, j + 1))
            transfer(arrayOfNode, arrayOfNode1); 
        } else if (U.compareAndSwapInt(this, SIZECTL, j, (k << RESIZE_STAMP_SHIFT) + 2)) {
          transfer(arrayOfNode, null);
        } 
        l2 = sumCount();
      } 
    } 
  }
  
  final Node<K, V>[] helpTransfer(Node<K, V>[] paramArrayOfNode, Node<K, V> paramNode) {
    Node[] arrayOfNode;
    if (paramArrayOfNode != null && paramNode instanceof ForwardingNode && (arrayOfNode = ((ForwardingNode)paramNode).nextTable) != null) {
      int j = resizeStamp(paramArrayOfNode.length);
      int i;
      while (arrayOfNode == this.nextTable && this.table == paramArrayOfNode && (i = this.sizeCtl) < 0 && i >>> RESIZE_STAMP_SHIFT == j && i != j + 1 && i != j + MAX_RESIZERS && this.transferIndex > 0) {
        if (U.compareAndSwapInt(this, SIZECTL, i, i + 1)) {
          transfer(paramArrayOfNode, arrayOfNode);
          break;
        } 
      } 
      return arrayOfNode;
    } 
    return this.table;
  }
  
  private final void tryPresize(int paramInt) {
    int i = (paramInt >= 536870912) ? 1073741824 : tableSizeFor(paramInt + (paramInt >>> 1) + 1);
    int j;
    while ((j = this.sizeCtl) >= 0) {
      Node[] arrayOfNode = this.table;
      int k;
      if (arrayOfNode == null || (k = arrayOfNode.length) == 0) {
        k = (j > i) ? j : i;
        if (U.compareAndSwapInt(this, SIZECTL, j, -1))
          try {
            if (this.table == arrayOfNode) {
              Node[] arrayOfNode1 = (Node[])new Node[k];
              this.table = arrayOfNode1;
              j = k - (k >>> 2);
            } 
            this.sizeCtl = j;
          } finally {
            this.sizeCtl = j;
          }  
        continue;
      } 
      if (i <= j || k >= 1073741824)
        break; 
      if (arrayOfNode == this.table) {
        int m = resizeStamp(k);
        if (j < 0) {
          Node[] arrayOfNode1;
          if (j >>> RESIZE_STAMP_SHIFT != m || j == m + 1 || j == m + MAX_RESIZERS || (arrayOfNode1 = this.nextTable) == null || this.transferIndex <= 0)
            break; 
          if (U.compareAndSwapInt(this, SIZECTL, j, j + 1))
            transfer(arrayOfNode, arrayOfNode1); 
          continue;
        } 
        if (U.compareAndSwapInt(this, SIZECTL, j, (m << RESIZE_STAMP_SHIFT) + 2))
          transfer(arrayOfNode, null); 
      } 
    } 
  }
  
  private final void transfer(Node<K, V>[] paramArrayOfNode1, Node<K, V>[] paramArrayOfNode2) {
    Node[] arrayOfNode;
    int i = paramArrayOfNode1.length;
    int j;
    if ((j = (NCPU > 1) ? ((i >>> 3) / NCPU) : i) < 16)
      j = 16; 
    if (paramArrayOfNode2 == null) {
      try {
        Node[] arrayOfNode1 = (Node[])new Node[i << 1];
        arrayOfNode = arrayOfNode1;
      } catch (Throwable throwable) {
        this.sizeCtl = Integer.MAX_VALUE;
        return;
      } 
      this.nextTable = arrayOfNode;
      this.transferIndex = i;
    } 
    int k = arrayOfNode.length;
    ForwardingNode forwardingNode = new ForwardingNode(arrayOfNode);
    boolean bool = true;
    boolean bool1 = false;
    int m = 0;
    int n = 0;
    while (true) {
      while (bool) {
        if (--m >= n || bool1) {
          bool = false;
          continue;
        } 
        int i2;
        if ((i2 = this.transferIndex) <= 0) {
          m = -1;
          bool = false;
          continue;
        } 
        int i3;
        if (U.compareAndSwapInt(this, TRANSFERINDEX, i2, i3 = (i2 > j) ? (i2 - j) : 0)) {
          n = i3;
          m = i2 - 1;
          bool = false;
        } 
      } 
      if (m < 0 || m >= i || m + i >= k) {
        if (bool1) {
          this.nextTable = null;
          this.table = arrayOfNode;
          this.sizeCtl = (i << 1) - (i >>> 1);
          return;
        } 
        int i2;
        if (U.compareAndSwapInt(this, SIZECTL, i2 = this.sizeCtl, i2 - 1)) {
          if (i2 - 2 != resizeStamp(i) << RESIZE_STAMP_SHIFT)
            return; 
          bool1 = bool = true;
          m = i;
        } 
        continue;
      } 
      Node node;
      if ((node = tabAt(paramArrayOfNode1, m)) == null) {
        bool = casTabAt(paramArrayOfNode1, m, null, forwardingNode);
        continue;
      } 
      int i1;
      if ((i1 = node.hash) == -1) {
        bool = true;
        continue;
      } 
      synchronized (node) {
        if (tabAt(paramArrayOfNode1, m) == node)
          if (i1 >= 0) {
            Node node2;
            Node node1;
            int i2 = i1 & i;
            Node node3 = node;
            Node node4;
            for (node4 = node.next; node4 != null; node4 = node4.next) {
              int i3 = node4.hash & i;
              if (i3 != i2) {
                i2 = i3;
                node3 = node4;
              } 
            } 
            if (i2 == 0) {
              node1 = node3;
              node2 = null;
            } else {
              node2 = node3;
              node1 = null;
            } 
            for (node4 = node; node4 != node3; node4 = node4.next) {
              int i3 = node4.hash;
              Object object1 = node4.key;
              Object object2 = node4.val;
              if ((i3 & i) == 0) {
                node1 = new Node(i3, object1, object2, node1);
              } else {
                node2 = new Node(i3, object1, object2, node2);
              } 
            } 
            setTabAt(arrayOfNode, m, node1);
            setTabAt(arrayOfNode, m + i, node2);
            setTabAt(paramArrayOfNode1, m, forwardingNode);
            bool = true;
          } else if (node instanceof TreeBin) {
            TreeBin treeBin = (TreeBin)node;
            TreeNode treeNode1 = null;
            TreeNode treeNode2 = null;
            TreeNode treeNode3 = null;
            TreeNode treeNode4 = null;
            byte b1 = 0;
            byte b2 = 0;
            TreeNode treeNode5 = treeBin.first;
            while (treeNode5 != null) {
              int i2 = treeNode5.hash;
              TreeNode treeNode = new TreeNode(i2, treeNode5.key, treeNode5.val, null, null);
              if ((i2 & i) == 0) {
                if ((treeNode.prev = treeNode2) == null) {
                  treeNode1 = treeNode;
                } else {
                  treeNode2.next = treeNode;
                } 
                treeNode2 = treeNode;
                b1++;
              } else {
                if ((treeNode.prev = treeNode4) == null) {
                  treeNode3 = treeNode;
                } else {
                  treeNode4.next = treeNode;
                } 
                treeNode4 = treeNode;
                b2++;
              } 
              Node node3 = treeNode5.next;
            } 
            Node node1 = (b1 <= 6) ? untreeify(treeNode1) : ((b2 != 0) ? new TreeBin(treeNode1) : treeBin);
            Node node2 = (b2 <= 6) ? untreeify(treeNode3) : ((b1 != 0) ? new TreeBin(treeNode3) : treeBin);
            setTabAt(arrayOfNode, m, node1);
            setTabAt(arrayOfNode, m + i, node2);
            setTabAt(paramArrayOfNode1, m, forwardingNode);
            bool = true;
          }  
      } 
    } 
  }
  
  final long sumCount() {
    CounterCell[] arrayOfCounterCell = this.counterCells;
    long l = this.baseCount;
    if (arrayOfCounterCell != null)
      for (byte b = 0; b < arrayOfCounterCell.length; b++) {
        CounterCell counterCell;
        if ((counterCell = arrayOfCounterCell[b]) != null)
          l += counterCell.value; 
      }  
    return l;
  }
  
  private final void fullAddCount(long paramLong, boolean paramBoolean) {
    int i;
    if ((i = ThreadLocalRandom.getProbe()) == 0) {
      ThreadLocalRandom.localInit();
      i = ThreadLocalRandom.getProbe();
      paramBoolean = true;
    } 
    boolean bool = false;
    while (true) {
      CounterCell[] arrayOfCounterCell;
      int j;
      if ((arrayOfCounterCell = this.counterCells) != null && (j = arrayOfCounterCell.length) > 0) {
        CounterCell counterCell;
        if ((counterCell = arrayOfCounterCell[j - true & i]) == null) {
          if (this.cellsBusy == 0) {
            CounterCell counterCell1 = new CounterCell(paramLong);
            if (this.cellsBusy == 0 && U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
              boolean bool1 = false;
              try {
                CounterCell[] arrayOfCounterCell1;
                int k;
                int m;
                if ((arrayOfCounterCell1 = this.counterCells) != null && (k = arrayOfCounterCell1.length) > 0 && arrayOfCounterCell1[m = k - true & i] == null) {
                  arrayOfCounterCell1[m] = counterCell1;
                  bool1 = true;
                } 
              } finally {
                this.cellsBusy = 0;
              } 
              if (bool1)
                break; 
              continue;
            } 
          } 
          bool = false;
        } else if (!paramBoolean) {
          paramBoolean = true;
        } else {
          long l1;
          if (U.compareAndSwapLong(counterCell, CELLVALUE, l1 = counterCell.value, l1 + paramLong))
            break; 
          if (this.counterCells != arrayOfCounterCell || j >= NCPU) {
            bool = false;
          } else if (!bool) {
            bool = true;
          } else if (this.cellsBusy == 0 && U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
            try {
              if (this.counterCells == arrayOfCounterCell) {
                CounterCell[] arrayOfCounterCell1 = new CounterCell[j << 1];
                for (byte b = 0; b < j; b++)
                  arrayOfCounterCell1[b] = arrayOfCounterCell[b]; 
                this.counterCells = arrayOfCounterCell1;
              } 
            } finally {
              this.cellsBusy = 0;
            } 
            bool = false;
            continue;
          } 
        } 
        i = ThreadLocalRandom.advanceProbe(i);
        continue;
      } 
      if (this.cellsBusy == 0 && this.counterCells == arrayOfCounterCell && U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
        boolean bool1 = false;
        try {
          if (this.counterCells == arrayOfCounterCell) {
            CounterCell[] arrayOfCounterCell1 = new CounterCell[2];
            arrayOfCounterCell1[i & true] = new CounterCell(paramLong);
            this.counterCells = arrayOfCounterCell1;
            bool1 = true;
          } 
        } finally {
          this.cellsBusy = 0;
        } 
        if (bool1)
          break; 
        continue;
      } 
      long l;
      if (U.compareAndSwapLong(this, BASECOUNT, l = this.baseCount, l + paramLong))
        break; 
    } 
  }
  
  private final void treeifyBin(Node<K, V>[] paramArrayOfNode, int paramInt) {
    if (paramArrayOfNode != null) {
      int i;
      if ((i = paramArrayOfNode.length) < 64) {
        tryPresize(i << 1);
      } else {
        Node node;
        if ((node = tabAt(paramArrayOfNode, paramInt)) != null && node.hash >= 0)
          synchronized (node) {
            if (tabAt(paramArrayOfNode, paramInt) == node) {
              TreeNode treeNode1 = null;
              TreeNode treeNode2 = null;
              for (Node node1 = node; node1 != null; node1 = node1.next) {
                TreeNode treeNode = new TreeNode(node1.hash, node1.key, node1.val, null, null);
                if ((treeNode.prev = treeNode2) == null) {
                  treeNode1 = treeNode;
                } else {
                  treeNode2.next = treeNode;
                } 
                treeNode2 = treeNode;
              } 
              setTabAt(paramArrayOfNode, paramInt, new TreeBin(treeNode1));
            } 
          }  
      } 
    } 
  }
  
  static <K, V> Node<K, V> untreeify(Node<K, V> paramNode) {
    Node node1 = null;
    Node node2 = null;
    for (Node<K, V> node3 = paramNode; node3 != null; node3 = node3.next) {
      Node node = new Node(node3.hash, node3.key, node3.val, null);
      if (node2 == null) {
        node1 = node;
      } else {
        node2.next = node;
      } 
      node2 = node;
    } 
    return node1;
  }
  
  final int batchFor(long paramLong) {
    long l;
    if (paramLong == Float.MAX_VALUE || (l = sumCount()) <= 1L || l < paramLong)
      return 0; 
    int i = ForkJoinPool.getCommonPoolParallelism() << 2;
    return (paramLong <= 0L || l /= paramLong >= i) ? i : (int)l;
  }
  
  public void forEach(long paramLong, BiConsumer<? super K, ? super V> paramBiConsumer) {
    if (paramBiConsumer == null)
      throw new NullPointerException(); 
    (new ForEachMappingTask(null, batchFor(paramLong), 0, 0, this.table, paramBiConsumer)).invoke();
  }
  
  public <U> void forEach(long paramLong, BiFunction<? super K, ? super V, ? extends U> paramBiFunction, Consumer<? super U> paramConsumer) {
    if (paramBiFunction == null || paramConsumer == null)
      throw new NullPointerException(); 
    (new ForEachTransformedMappingTask(null, batchFor(paramLong), 0, 0, this.table, paramBiFunction, paramConsumer)).invoke();
  }
  
  public <U> U search(long paramLong, BiFunction<? super K, ? super V, ? extends U> paramBiFunction) {
    if (paramBiFunction == null)
      throw new NullPointerException(); 
    return (U)(new SearchMappingsTask(null, batchFor(paramLong), 0, 0, this.table, paramBiFunction, new AtomicReference())).invoke();
  }
  
  public <U> U reduce(long paramLong, BiFunction<? super K, ? super V, ? extends U> paramBiFunction1, BiFunction<? super U, ? super U, ? extends U> paramBiFunction2) {
    if (paramBiFunction1 == null || paramBiFunction2 == null)
      throw new NullPointerException(); 
    return (U)(new MapReduceMappingsTask(null, batchFor(paramLong), 0, 0, this.table, null, paramBiFunction1, paramBiFunction2)).invoke();
  }
  
  public double reduceToDouble(long paramLong, ToDoubleBiFunction<? super K, ? super V> paramToDoubleBiFunction, double paramDouble, DoubleBinaryOperator paramDoubleBinaryOperator) {
    if (paramToDoubleBiFunction == null || paramDoubleBinaryOperator == null)
      throw new NullPointerException(); 
    return ((Double)(new MapReduceMappingsToDoubleTask(null, batchFor(paramLong), 0, 0, this.table, null, paramToDoubleBiFunction, paramDouble, paramDoubleBinaryOperator)).invoke()).doubleValue();
  }
  
  public long reduceToLong(long paramLong1, ToLongBiFunction<? super K, ? super V> paramToLongBiFunction, long paramLong2, LongBinaryOperator paramLongBinaryOperator) {
    if (paramToLongBiFunction == null || paramLongBinaryOperator == null)
      throw new NullPointerException(); 
    return ((Long)(new MapReduceMappingsToLongTask(null, batchFor(paramLong1), 0, 0, this.table, null, paramToLongBiFunction, paramLong2, paramLongBinaryOperator)).invoke()).longValue();
  }
  
  public int reduceToInt(long paramLong, ToIntBiFunction<? super K, ? super V> paramToIntBiFunction, int paramInt, IntBinaryOperator paramIntBinaryOperator) {
    if (paramToIntBiFunction == null || paramIntBinaryOperator == null)
      throw new NullPointerException(); 
    return ((Integer)(new MapReduceMappingsToIntTask(null, batchFor(paramLong), 0, 0, this.table, null, paramToIntBiFunction, paramInt, paramIntBinaryOperator)).invoke()).intValue();
  }
  
  public void forEachKey(long paramLong, Consumer<? super K> paramConsumer) {
    if (paramConsumer == null)
      throw new NullPointerException(); 
    (new ForEachKeyTask(null, batchFor(paramLong), 0, 0, this.table, paramConsumer)).invoke();
  }
  
  public <U> void forEachKey(long paramLong, Function<? super K, ? extends U> paramFunction, Consumer<? super U> paramConsumer) {
    if (paramFunction == null || paramConsumer == null)
      throw new NullPointerException(); 
    (new ForEachTransformedKeyTask(null, batchFor(paramLong), 0, 0, this.table, paramFunction, paramConsumer)).invoke();
  }
  
  public <U> U searchKeys(long paramLong, Function<? super K, ? extends U> paramFunction) {
    if (paramFunction == null)
      throw new NullPointerException(); 
    return (U)(new SearchKeysTask(null, batchFor(paramLong), 0, 0, this.table, paramFunction, new AtomicReference())).invoke();
  }
  
  public K reduceKeys(long paramLong, BiFunction<? super K, ? super K, ? extends K> paramBiFunction) {
    if (paramBiFunction == null)
      throw new NullPointerException(); 
    return (K)(new ReduceKeysTask(null, batchFor(paramLong), 0, 0, this.table, null, paramBiFunction)).invoke();
  }
  
  public <U> U reduceKeys(long paramLong, Function<? super K, ? extends U> paramFunction, BiFunction<? super U, ? super U, ? extends U> paramBiFunction) {
    if (paramFunction == null || paramBiFunction == null)
      throw new NullPointerException(); 
    return (U)(new MapReduceKeysTask(null, batchFor(paramLong), 0, 0, this.table, null, paramFunction, paramBiFunction)).invoke();
  }
  
  public double reduceKeysToDouble(long paramLong, ToDoubleFunction<? super K> paramToDoubleFunction, double paramDouble, DoubleBinaryOperator paramDoubleBinaryOperator) {
    if (paramToDoubleFunction == null || paramDoubleBinaryOperator == null)
      throw new NullPointerException(); 
    return ((Double)(new MapReduceKeysToDoubleTask(null, batchFor(paramLong), 0, 0, this.table, null, paramToDoubleFunction, paramDouble, paramDoubleBinaryOperator)).invoke()).doubleValue();
  }
  
  public long reduceKeysToLong(long paramLong1, ToLongFunction<? super K> paramToLongFunction, long paramLong2, LongBinaryOperator paramLongBinaryOperator) {
    if (paramToLongFunction == null || paramLongBinaryOperator == null)
      throw new NullPointerException(); 
    return ((Long)(new MapReduceKeysToLongTask(null, batchFor(paramLong1), 0, 0, this.table, null, paramToLongFunction, paramLong2, paramLongBinaryOperator)).invoke()).longValue();
  }
  
  public int reduceKeysToInt(long paramLong, ToIntFunction<? super K> paramToIntFunction, int paramInt, IntBinaryOperator paramIntBinaryOperator) {
    if (paramToIntFunction == null || paramIntBinaryOperator == null)
      throw new NullPointerException(); 
    return ((Integer)(new MapReduceKeysToIntTask(null, batchFor(paramLong), 0, 0, this.table, null, paramToIntFunction, paramInt, paramIntBinaryOperator)).invoke()).intValue();
  }
  
  public void forEachValue(long paramLong, Consumer<? super V> paramConsumer) {
    if (paramConsumer == null)
      throw new NullPointerException(); 
    (new ForEachValueTask(null, batchFor(paramLong), 0, 0, this.table, paramConsumer)).invoke();
  }
  
  public <U> void forEachValue(long paramLong, Function<? super V, ? extends U> paramFunction, Consumer<? super U> paramConsumer) {
    if (paramFunction == null || paramConsumer == null)
      throw new NullPointerException(); 
    (new ForEachTransformedValueTask(null, batchFor(paramLong), 0, 0, this.table, paramFunction, paramConsumer)).invoke();
  }
  
  public <U> U searchValues(long paramLong, Function<? super V, ? extends U> paramFunction) {
    if (paramFunction == null)
      throw new NullPointerException(); 
    return (U)(new SearchValuesTask(null, batchFor(paramLong), 0, 0, this.table, paramFunction, new AtomicReference())).invoke();
  }
  
  public V reduceValues(long paramLong, BiFunction<? super V, ? super V, ? extends V> paramBiFunction) {
    if (paramBiFunction == null)
      throw new NullPointerException(); 
    return (V)(new ReduceValuesTask(null, batchFor(paramLong), 0, 0, this.table, null, paramBiFunction)).invoke();
  }
  
  public <U> U reduceValues(long paramLong, Function<? super V, ? extends U> paramFunction, BiFunction<? super U, ? super U, ? extends U> paramBiFunction) {
    if (paramFunction == null || paramBiFunction == null)
      throw new NullPointerException(); 
    return (U)(new MapReduceValuesTask(null, batchFor(paramLong), 0, 0, this.table, null, paramFunction, paramBiFunction)).invoke();
  }
  
  public double reduceValuesToDouble(long paramLong, ToDoubleFunction<? super V> paramToDoubleFunction, double paramDouble, DoubleBinaryOperator paramDoubleBinaryOperator) {
    if (paramToDoubleFunction == null || paramDoubleBinaryOperator == null)
      throw new NullPointerException(); 
    return ((Double)(new MapReduceValuesToDoubleTask(null, batchFor(paramLong), 0, 0, this.table, null, paramToDoubleFunction, paramDouble, paramDoubleBinaryOperator)).invoke()).doubleValue();
  }
  
  public long reduceValuesToLong(long paramLong1, ToLongFunction<? super V> paramToLongFunction, long paramLong2, LongBinaryOperator paramLongBinaryOperator) {
    if (paramToLongFunction == null || paramLongBinaryOperator == null)
      throw new NullPointerException(); 
    return ((Long)(new MapReduceValuesToLongTask(null, batchFor(paramLong1), 0, 0, this.table, null, paramToLongFunction, paramLong2, paramLongBinaryOperator)).invoke()).longValue();
  }
  
  public int reduceValuesToInt(long paramLong, ToIntFunction<? super V> paramToIntFunction, int paramInt, IntBinaryOperator paramIntBinaryOperator) {
    if (paramToIntFunction == null || paramIntBinaryOperator == null)
      throw new NullPointerException(); 
    return ((Integer)(new MapReduceValuesToIntTask(null, batchFor(paramLong), 0, 0, this.table, null, paramToIntFunction, paramInt, paramIntBinaryOperator)).invoke()).intValue();
  }
  
  public void forEachEntry(long paramLong, Consumer<? super Map.Entry<K, V>> paramConsumer) {
    if (paramConsumer == null)
      throw new NullPointerException(); 
    (new ForEachEntryTask(null, batchFor(paramLong), 0, 0, this.table, paramConsumer)).invoke();
  }
  
  public <U> void forEachEntry(long paramLong, Function<Map.Entry<K, V>, ? extends U> paramFunction, Consumer<? super U> paramConsumer) {
    if (paramFunction == null || paramConsumer == null)
      throw new NullPointerException(); 
    (new ForEachTransformedEntryTask(null, batchFor(paramLong), 0, 0, this.table, paramFunction, paramConsumer)).invoke();
  }
  
  public <U> U searchEntries(long paramLong, Function<Map.Entry<K, V>, ? extends U> paramFunction) {
    if (paramFunction == null)
      throw new NullPointerException(); 
    return (U)(new SearchEntriesTask(null, batchFor(paramLong), 0, 0, this.table, paramFunction, new AtomicReference())).invoke();
  }
  
  public Map.Entry<K, V> reduceEntries(long paramLong, BiFunction<Map.Entry<K, V>, Map.Entry<K, V>, ? extends Map.Entry<K, V>> paramBiFunction) {
    if (paramBiFunction == null)
      throw new NullPointerException(); 
    return (Map.Entry)(new ReduceEntriesTask(null, batchFor(paramLong), 0, 0, this.table, null, paramBiFunction)).invoke();
  }
  
  public <U> U reduceEntries(long paramLong, Function<Map.Entry<K, V>, ? extends U> paramFunction, BiFunction<? super U, ? super U, ? extends U> paramBiFunction) {
    if (paramFunction == null || paramBiFunction == null)
      throw new NullPointerException(); 
    return (U)(new MapReduceEntriesTask(null, batchFor(paramLong), 0, 0, this.table, null, paramFunction, paramBiFunction)).invoke();
  }
  
  public double reduceEntriesToDouble(long paramLong, ToDoubleFunction<Map.Entry<K, V>> paramToDoubleFunction, double paramDouble, DoubleBinaryOperator paramDoubleBinaryOperator) {
    if (paramToDoubleFunction == null || paramDoubleBinaryOperator == null)
      throw new NullPointerException(); 
    return ((Double)(new MapReduceEntriesToDoubleTask(null, batchFor(paramLong), 0, 0, this.table, null, paramToDoubleFunction, paramDouble, paramDoubleBinaryOperator)).invoke()).doubleValue();
  }
  
  public long reduceEntriesToLong(long paramLong1, ToLongFunction<Map.Entry<K, V>> paramToLongFunction, long paramLong2, LongBinaryOperator paramLongBinaryOperator) {
    if (paramToLongFunction == null || paramLongBinaryOperator == null)
      throw new NullPointerException(); 
    return ((Long)(new MapReduceEntriesToLongTask(null, batchFor(paramLong1), 0, 0, this.table, null, paramToLongFunction, paramLong2, paramLongBinaryOperator)).invoke()).longValue();
  }
  
  public int reduceEntriesToInt(long paramLong, ToIntFunction<Map.Entry<K, V>> paramToIntFunction, int paramInt, IntBinaryOperator paramIntBinaryOperator) {
    if (paramToIntFunction == null || paramIntBinaryOperator == null)
      throw new NullPointerException(); 
    return ((Integer)(new MapReduceEntriesToIntTask(null, batchFor(paramLong), 0, 0, this.table, null, paramToIntFunction, paramInt, paramIntBinaryOperator)).invoke()).intValue();
  }
  
  static  {
    try {
      U = Unsafe.getUnsafe();
      Class clazz1 = ConcurrentHashMap.class;
      SIZECTL = U.objectFieldOffset(clazz1.getDeclaredField("sizeCtl"));
      TRANSFERINDEX = U.objectFieldOffset(clazz1.getDeclaredField("transferIndex"));
      BASECOUNT = U.objectFieldOffset(clazz1.getDeclaredField("baseCount"));
      CELLSBUSY = U.objectFieldOffset(clazz1.getDeclaredField("cellsBusy"));
      Class clazz2 = CounterCell.class;
      CELLVALUE = U.objectFieldOffset(clazz2.getDeclaredField("value"));
      Class clazz3 = Node[].class;
      ABASE = U.arrayBaseOffset(clazz3);
      int i = U.arrayIndexScale(clazz3);
      if ((i & i - 1) != 0)
        throw new Error("data type scale not a power of two"); 
      ASHIFT = 31 - Integer.numberOfLeadingZeros(i);
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
  
  static class BaseIterator<K, V> extends Traverser<K, V> {
    final ConcurrentHashMap<K, V> map;
    
    ConcurrentHashMap.Node<K, V> lastReturned;
    
    BaseIterator(ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap<K, V> param1ConcurrentHashMap) {
      super(param1ArrayOfNode, param1Int1, param1Int2, param1Int3);
      this.map = param1ConcurrentHashMap;
      advance();
    }
    
    public final boolean hasNext() { return (this.next != null); }
    
    public final boolean hasMoreElements() { return (this.next != null); }
    
    public final void remove() {
      ConcurrentHashMap.Node node;
      if ((node = this.lastReturned) == null)
        throw new IllegalStateException(); 
      this.lastReturned = null;
      this.map.replaceNode(node.key, null, null);
    }
  }
  
  static abstract class BulkTask<K, V, R> extends CountedCompleter<R> {
    ConcurrentHashMap.Node<K, V>[] tab;
    
    ConcurrentHashMap.Node<K, V> next;
    
    ConcurrentHashMap.TableStack<K, V> stack;
    
    ConcurrentHashMap.TableStack<K, V> spare;
    
    int index;
    
    int baseIndex;
    
    int baseLimit;
    
    final int baseSize;
    
    int batch;
    
    BulkTask(BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode) {
      super(param1BulkTask);
      this.batch = param1Int1;
      this.index = this.baseIndex = param1Int2;
      if ((this.tab = param1ArrayOfNode) == null) {
        this.baseSize = this.baseLimit = 0;
      } else if (param1BulkTask == null) {
        this.baseSize = this.baseLimit = param1ArrayOfNode.length;
      } else {
        this.baseLimit = param1Int3;
        this.baseSize = param1BulkTask.baseSize;
      } 
    }
    
    final ConcurrentHashMap.Node<K, V> advance() {
      ConcurrentHashMap.Node node;
      if ((node = this.next) != null)
        node = node.next; 
      while (true) {
        if (node != null)
          return this.next = node; 
        ConcurrentHashMap.Node[] arrayOfNode;
        int i;
        int j;
        if (this.baseIndex >= this.baseLimit || (arrayOfNode = this.tab) == null || (j = arrayOfNode.length) <= (i = this.index) || i < 0)
          return this.next = null; 
        if ((node = ConcurrentHashMap.tabAt(arrayOfNode, i)) != null && node.hash < 0) {
          if (node instanceof ConcurrentHashMap.ForwardingNode) {
            this.tab = ((ConcurrentHashMap.ForwardingNode)node).nextTable;
            node = null;
            pushState(arrayOfNode, i, j);
            continue;
          } 
          if (node instanceof ConcurrentHashMap.TreeBin) {
            node = ((ConcurrentHashMap.TreeBin)node).first;
          } else {
            node = null;
          } 
        } 
        if (this.stack != null) {
          recoverState(j);
          continue;
        } 
        if ((this.index = i + this.baseSize) >= j)
          this.index = ++this.baseIndex; 
      } 
    }
    
    private void pushState(ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, int param1Int1, int param1Int2) {
      ConcurrentHashMap.TableStack tableStack = this.spare;
      if (tableStack != null) {
        this.spare = tableStack.next;
      } else {
        tableStack = new ConcurrentHashMap.TableStack();
      } 
      tableStack.tab = param1ArrayOfNode;
      tableStack.length = param1Int2;
      tableStack.index = param1Int1;
      tableStack.next = this.stack;
      this.stack = tableStack;
    }
    
    private void recoverState(int param1Int) {
      ConcurrentHashMap.TableStack tableStack;
      int i;
      while ((tableStack = this.stack) != null && this.index += (i = tableStack.length) >= param1Int) {
        param1Int = i;
        this.index = tableStack.index;
        this.tab = tableStack.tab;
        tableStack.tab = null;
        ConcurrentHashMap.TableStack tableStack1 = tableStack.next;
        tableStack.next = this.spare;
        this.stack = tableStack1;
        this.spare = tableStack;
      } 
      if (tableStack == null && this.index += this.baseSize >= param1Int)
        this.index = ++this.baseIndex; 
    }
  }
  
  static abstract class CollectionView<K, V, E> extends Object implements Collection<E>, Serializable {
    private static final long serialVersionUID = 7249069246763182397L;
    
    final ConcurrentHashMap<K, V> map;
    
    private static final String oomeMsg = "Required array size too large";
    
    CollectionView(ConcurrentHashMap<K, V> param1ConcurrentHashMap) { this.map = param1ConcurrentHashMap; }
    
    public ConcurrentHashMap<K, V> getMap() { return this.map; }
    
    public final void clear() { this.map.clear(); }
    
    public final int size() { return this.map.size(); }
    
    public final boolean isEmpty() { return this.map.isEmpty(); }
    
    public abstract Iterator<E> iterator();
    
    public abstract boolean contains(Object param1Object);
    
    public abstract boolean remove(Object param1Object);
    
    public final Object[] toArray() {
      long l = this.map.mappingCount();
      if (l > 2147483639L)
        throw new OutOfMemoryError("Required array size too large"); 
      int i = (int)l;
      Object[] arrayOfObject = new Object[i];
      byte b = 0;
      for (Object object : this) {
        if (b == i) {
          if (i >= 2147483639)
            throw new OutOfMemoryError("Required array size too large"); 
          if (i >= 1073741819) {
            i = 2147483639;
          } else {
            i += (i >>> 1) + 1;
          } 
          arrayOfObject = Arrays.copyOf(arrayOfObject, i);
        } 
        arrayOfObject[b++] = object;
      } 
      return (b == i) ? arrayOfObject : Arrays.copyOf(arrayOfObject, b);
    }
    
    public final <T> T[] toArray(T[] param1ArrayOfT) {
      long l = this.map.mappingCount();
      if (l > 2147483639L)
        throw new OutOfMemoryError("Required array size too large"); 
      int i = (int)l;
      T[] arrayOfT = (param1ArrayOfT.length >= i) ? param1ArrayOfT : (Object[])Array.newInstance(param1ArrayOfT.getClass().getComponentType(), i);
      int j = arrayOfT.length;
      byte b = 0;
      for (Object object : this) {
        if (b == j) {
          if (j >= 2147483639)
            throw new OutOfMemoryError("Required array size too large"); 
          if (j >= 1073741819) {
            j = 2147483639;
          } else {
            j += (j >>> 1) + 1;
          } 
          arrayOfT = (T[])Arrays.copyOf(arrayOfT, j);
        } 
        arrayOfT[b++] = object;
      } 
      if (param1ArrayOfT == arrayOfT && b < j) {
        arrayOfT[b] = null;
        return arrayOfT;
      } 
      return (b == j) ? arrayOfT : Arrays.copyOf(arrayOfT, b);
    }
    
    public final String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append('[');
      Iterator iterator = iterator();
      if (iterator.hasNext())
        while (true) {
          Object object = iterator.next();
          stringBuilder.append((object == this) ? "(this Collection)" : object);
          if (!iterator.hasNext())
            break; 
          stringBuilder.append(',').append(' ');
        }  
      return stringBuilder.append(']').toString();
    }
    
    public final boolean containsAll(Collection<?> param1Collection) {
      if (param1Collection != this)
        for (Object object : param1Collection) {
          if (object == null || !contains(object))
            return false; 
        }  
      return true;
    }
    
    public final boolean removeAll(Collection<?> param1Collection) {
      if (param1Collection == null)
        throw new NullPointerException(); 
      boolean bool = false;
      Iterator iterator = iterator();
      while (iterator.hasNext()) {
        if (param1Collection.contains(iterator.next())) {
          iterator.remove();
          bool = true;
        } 
      } 
      return bool;
    }
    
    public final boolean retainAll(Collection<?> param1Collection) {
      if (param1Collection == null)
        throw new NullPointerException(); 
      boolean bool = false;
      Iterator iterator = iterator();
      while (iterator.hasNext()) {
        if (!param1Collection.contains(iterator.next())) {
          iterator.remove();
          bool = true;
        } 
      } 
      return bool;
    }
  }
  
  @Contended
  static final class CounterCell {
    CounterCell(long param1Long) { this.value = param1Long; }
  }
  
  static final class EntryIterator<K, V> extends BaseIterator<K, V> implements Iterator<Map.Entry<K, V>> {
    EntryIterator(ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap<K, V> param1ConcurrentHashMap) { super(param1ArrayOfNode, param1Int1, param1Int2, param1Int3, param1ConcurrentHashMap); }
    
    public final Map.Entry<K, V> next() {
      ConcurrentHashMap.Node node;
      if ((node = this.next) == null)
        throw new NoSuchElementException(); 
      Object object1 = node.key;
      Object object2 = node.val;
      this.lastReturned = node;
      advance();
      return new ConcurrentHashMap.MapEntry(object1, object2, this.map);
    }
  }
  
  static final class EntrySetView<K, V> extends CollectionView<K, V, Map.Entry<K, V>> implements Set<Map.Entry<K, V>>, Serializable {
    private static final long serialVersionUID = 2249069246763182397L;
    
    EntrySetView(ConcurrentHashMap<K, V> param1ConcurrentHashMap) { super(param1ConcurrentHashMap); }
    
    public boolean contains(Object param1Object) {
      Object object1;
      Object object2;
      Object object3;
      Map.Entry entry;
      return (param1Object instanceof Map.Entry && (object1 = (entry = (Map.Entry)param1Object).getKey()) != null && (object3 = this.map.get(object1)) != null && (object2 = entry.getValue()) != null && (object2 == object3 || object2.equals(object3)));
    }
    
    public boolean remove(Object param1Object) {
      Object object1;
      Object object2;
      Map.Entry entry;
      return (param1Object instanceof Map.Entry && (object1 = (entry = (Map.Entry)param1Object).getKey()) != null && (object2 = entry.getValue()) != null && this.map.remove(object1, object2));
    }
    
    public Iterator<Map.Entry<K, V>> iterator() {
      ConcurrentHashMap concurrentHashMap = this.map;
      ConcurrentHashMap.Node[] arrayOfNode;
      byte b = ((arrayOfNode = concurrentHashMap.table) == null) ? 0 : arrayOfNode.length;
      return new ConcurrentHashMap.EntryIterator(arrayOfNode, b, 0, b, concurrentHashMap);
    }
    
    public boolean add(Map.Entry<K, V> param1Entry) { return (this.map.putVal(param1Entry.getKey(), param1Entry.getValue(), false) == null); }
    
    public boolean addAll(Collection<? extends Map.Entry<K, V>> param1Collection) {
      boolean bool = false;
      for (Map.Entry entry : param1Collection) {
        if (add(entry))
          bool = true; 
      } 
      return bool;
    }
    
    public final int hashCode() {
      int i = 0;
      ConcurrentHashMap.Node[] arrayOfNode;
      if ((arrayOfNode = this.map.table) != null) {
        ConcurrentHashMap.Traverser traverser = new ConcurrentHashMap.Traverser(arrayOfNode, arrayOfNode.length, 0, arrayOfNode.length);
        ConcurrentHashMap.Node node;
        while ((node = traverser.advance()) != null)
          i += node.hashCode(); 
      } 
      return i;
    }
    
    public final boolean equals(Object param1Object) {
      Set set;
      return (param1Object instanceof Set && ((set = (Set)param1Object) == this || (containsAll(set) && set.containsAll(this))));
    }
    
    public Spliterator<Map.Entry<K, V>> spliterator() {
      ConcurrentHashMap concurrentHashMap = this.map;
      long l = concurrentHashMap.sumCount();
      ConcurrentHashMap.Node[] arrayOfNode;
      byte b = ((arrayOfNode = concurrentHashMap.table) == null) ? 0 : arrayOfNode.length;
      return new ConcurrentHashMap.EntrySpliterator(arrayOfNode, b, 0, b, (l < 0L) ? 0L : l, concurrentHashMap);
    }
    
    public void forEach(Consumer<? super Map.Entry<K, V>> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      ConcurrentHashMap.Node[] arrayOfNode;
      if ((arrayOfNode = this.map.table) != null) {
        ConcurrentHashMap.Traverser traverser = new ConcurrentHashMap.Traverser(arrayOfNode, arrayOfNode.length, 0, arrayOfNode.length);
        ConcurrentHashMap.Node node;
        while ((node = traverser.advance()) != null)
          param1Consumer.accept(new ConcurrentHashMap.MapEntry(node.key, node.val, this.map)); 
      } 
    }
  }
  
  static final class EntrySpliterator<K, V> extends Traverser<K, V> implements Spliterator<Map.Entry<K, V>> {
    final ConcurrentHashMap<K, V> map;
    
    long est;
    
    EntrySpliterator(ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, int param1Int1, int param1Int2, int param1Int3, long param1Long, ConcurrentHashMap<K, V> param1ConcurrentHashMap) {
      super(param1ArrayOfNode, param1Int1, param1Int2, param1Int3);
      this.map = param1ConcurrentHashMap;
      this.est = param1Long;
    }
    
    public Spliterator<Map.Entry<K, V>> trySplit() {
      int i;
      int j;
      int k;
      return ((k = (i = this.baseIndex) + (j = this.baseLimit) >>> 1) <= i) ? null : new EntrySpliterator(this.tab, this.baseSize, this.baseLimit = k, j, this.est >>>= true, this.map);
    }
    
    public void forEachRemaining(Consumer<? super Map.Entry<K, V>> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      ConcurrentHashMap.Node node;
      while ((node = advance()) != null)
        param1Consumer.accept(new ConcurrentHashMap.MapEntry(node.key, node.val, this.map)); 
    }
    
    public boolean tryAdvance(Consumer<? super Map.Entry<K, V>> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      ConcurrentHashMap.Node node;
      if ((node = advance()) == null)
        return false; 
      param1Consumer.accept(new ConcurrentHashMap.MapEntry(node.key, node.val, this.map));
      return true;
    }
    
    public long estimateSize() { return this.est; }
    
    public int characteristics() { return 4353; }
  }
  
  static final class ForEachEntryTask<K, V> extends BulkTask<K, V, Void> {
    final Consumer<? super Map.Entry<K, V>> action;
    
    ForEachEntryTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, Consumer<? super Map.Entry<K, V>> param1Consumer) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.action = param1Consumer;
    }
    
    public final void compute() {
      Consumer consumer;
      if ((consumer = this.action) != null) {
        int i = this.baseIndex;
        int j;
        int k;
        while (this.batch > 0 && (k = (j = this.baseLimit) + i >>> 1) > i) {
          addToPendingCount(1);
          (new ForEachEntryTask(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, consumer)).fork();
        } 
        ConcurrentHashMap.Node node;
        while ((node = advance()) != null)
          consumer.accept(node); 
        propagateCompletion();
      } 
    }
  }
  
  static final class ForEachKeyTask<K, V> extends BulkTask<K, V, Void> {
    final Consumer<? super K> action;
    
    ForEachKeyTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, Consumer<? super K> param1Consumer) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.action = param1Consumer;
    }
    
    public final void compute() {
      Consumer consumer;
      if ((consumer = this.action) != null) {
        int i = this.baseIndex;
        int j;
        int k;
        while (this.batch > 0 && (k = (j = this.baseLimit) + i >>> 1) > i) {
          addToPendingCount(1);
          (new ForEachKeyTask(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, consumer)).fork();
        } 
        ConcurrentHashMap.Node node;
        while ((node = advance()) != null)
          consumer.accept(node.key); 
        propagateCompletion();
      } 
    }
  }
  
  static final class ForEachMappingTask<K, V> extends BulkTask<K, V, Void> {
    final BiConsumer<? super K, ? super V> action;
    
    ForEachMappingTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, BiConsumer<? super K, ? super V> param1BiConsumer) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.action = param1BiConsumer;
    }
    
    public final void compute() {
      BiConsumer biConsumer;
      if ((biConsumer = this.action) != null) {
        int i = this.baseIndex;
        int j;
        int k;
        while (this.batch > 0 && (k = (j = this.baseLimit) + i >>> 1) > i) {
          addToPendingCount(1);
          (new ForEachMappingTask(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, biConsumer)).fork();
        } 
        ConcurrentHashMap.Node node;
        while ((node = advance()) != null)
          biConsumer.accept(node.key, node.val); 
        propagateCompletion();
      } 
    }
  }
  
  static final class ForEachTransformedEntryTask<K, V, U> extends BulkTask<K, V, Void> {
    final Function<Map.Entry<K, V>, ? extends U> transformer;
    
    final Consumer<? super U> action;
    
    ForEachTransformedEntryTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, Function<Map.Entry<K, V>, ? extends U> param1Function, Consumer<? super U> param1Consumer) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.transformer = param1Function;
      this.action = param1Consumer;
    }
    
    public final void compute() {
      Function function;
      Consumer consumer;
      if ((function = this.transformer) != null && (consumer = this.action) != null) {
        int i = this.baseIndex;
        int j;
        int k;
        while (this.batch > 0 && (k = (j = this.baseLimit) + i >>> 1) > i) {
          addToPendingCount(1);
          (new ForEachTransformedEntryTask(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, function, consumer)).fork();
        } 
        ConcurrentHashMap.Node node;
        while ((node = advance()) != null) {
          Object object;
          if ((object = function.apply(node)) != null)
            consumer.accept(object); 
        } 
        propagateCompletion();
      } 
    }
  }
  
  static final class ForEachTransformedKeyTask<K, V, U> extends BulkTask<K, V, Void> {
    final Function<? super K, ? extends U> transformer;
    
    final Consumer<? super U> action;
    
    ForEachTransformedKeyTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, Function<? super K, ? extends U> param1Function, Consumer<? super U> param1Consumer) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.transformer = param1Function;
      this.action = param1Consumer;
    }
    
    public final void compute() {
      Function function;
      Consumer consumer;
      if ((function = this.transformer) != null && (consumer = this.action) != null) {
        int i = this.baseIndex;
        int j;
        int k;
        while (this.batch > 0 && (k = (j = this.baseLimit) + i >>> 1) > i) {
          addToPendingCount(1);
          (new ForEachTransformedKeyTask(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, function, consumer)).fork();
        } 
        ConcurrentHashMap.Node node;
        while ((node = advance()) != null) {
          Object object;
          if ((object = function.apply(node.key)) != null)
            consumer.accept(object); 
        } 
        propagateCompletion();
      } 
    }
  }
  
  static final class ForEachTransformedMappingTask<K, V, U> extends BulkTask<K, V, Void> {
    final BiFunction<? super K, ? super V, ? extends U> transformer;
    
    final Consumer<? super U> action;
    
    ForEachTransformedMappingTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, BiFunction<? super K, ? super V, ? extends U> param1BiFunction, Consumer<? super U> param1Consumer) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.transformer = param1BiFunction;
      this.action = param1Consumer;
    }
    
    public final void compute() {
      BiFunction biFunction;
      Consumer consumer;
      if ((biFunction = this.transformer) != null && (consumer = this.action) != null) {
        int i = this.baseIndex;
        int j;
        int k;
        while (this.batch > 0 && (k = (j = this.baseLimit) + i >>> 1) > i) {
          addToPendingCount(1);
          (new ForEachTransformedMappingTask(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, biFunction, consumer)).fork();
        } 
        ConcurrentHashMap.Node node;
        while ((node = advance()) != null) {
          Object object;
          if ((object = biFunction.apply(node.key, node.val)) != null)
            consumer.accept(object); 
        } 
        propagateCompletion();
      } 
    }
  }
  
  static final class ForEachTransformedValueTask<K, V, U> extends BulkTask<K, V, Void> {
    final Function<? super V, ? extends U> transformer;
    
    final Consumer<? super U> action;
    
    ForEachTransformedValueTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, Function<? super V, ? extends U> param1Function, Consumer<? super U> param1Consumer) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.transformer = param1Function;
      this.action = param1Consumer;
    }
    
    public final void compute() {
      Function function;
      Consumer consumer;
      if ((function = this.transformer) != null && (consumer = this.action) != null) {
        int i = this.baseIndex;
        int j;
        int k;
        while (this.batch > 0 && (k = (j = this.baseLimit) + i >>> 1) > i) {
          addToPendingCount(1);
          (new ForEachTransformedValueTask(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, function, consumer)).fork();
        } 
        ConcurrentHashMap.Node node;
        while ((node = advance()) != null) {
          Object object;
          if ((object = function.apply(node.val)) != null)
            consumer.accept(object); 
        } 
        propagateCompletion();
      } 
    }
  }
  
  static final class ForEachValueTask<K, V> extends BulkTask<K, V, Void> {
    final Consumer<? super V> action;
    
    ForEachValueTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, Consumer<? super V> param1Consumer) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.action = param1Consumer;
    }
    
    public final void compute() {
      Consumer consumer;
      if ((consumer = this.action) != null) {
        int i = this.baseIndex;
        int j;
        int k;
        while (this.batch > 0 && (k = (j = this.baseLimit) + i >>> 1) > i) {
          addToPendingCount(1);
          (new ForEachValueTask(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, consumer)).fork();
        } 
        ConcurrentHashMap.Node node;
        while ((node = advance()) != null)
          consumer.accept(node.val); 
        propagateCompletion();
      } 
    }
  }
  
  static final class ForwardingNode<K, V> extends Node<K, V> {
    final ConcurrentHashMap.Node<K, V>[] nextTable;
    
    ForwardingNode(ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode) {
      super(-1, null, null, null);
      this.nextTable = param1ArrayOfNode;
    }
    
    ConcurrentHashMap.Node<K, V> find(int param1Int, Object param1Object) {
      ConcurrentHashMap.Node[] arrayOfNode = this.nextTable;
      label27: while (true) {
        ConcurrentHashMap.Node node;
        int i;
        if (param1Object == null || arrayOfNode == null || (i = arrayOfNode.length) == 0 || (node = ConcurrentHashMap.tabAt(arrayOfNode, i - true & param1Int)) == null)
          return null; 
        do {
          int j;
          Object object;
          if ((j = node.hash) == param1Int && ((object = node.key) == param1Object || (object != null && param1Object.equals(object))))
            return node; 
          if (j < 0) {
            if (node instanceof ForwardingNode) {
              arrayOfNode = ((ForwardingNode)node).nextTable;
              continue label27;
            } 
            return node.find(param1Int, param1Object);
          } 
        } while ((node = node.next) != null);
        break;
      } 
      return null;
    }
  }
  
  static final class KeyIterator<K, V> extends BaseIterator<K, V> implements Iterator<K>, Enumeration<K> {
    KeyIterator(ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap<K, V> param1ConcurrentHashMap) { super(param1ArrayOfNode, param1Int1, param1Int2, param1Int3, param1ConcurrentHashMap); }
    
    public final K next() {
      ConcurrentHashMap.Node node;
      if ((node = this.next) == null)
        throw new NoSuchElementException(); 
      Object object = node.key;
      this.lastReturned = node;
      advance();
      return (K)object;
    }
    
    public final K nextElement() { return (K)next(); }
  }
  
  public static class KeySetView<K, V> extends CollectionView<K, V, K> implements Set<K>, Serializable {
    private static final long serialVersionUID = 7249069246763182397L;
    
    private final V value;
    
    KeySetView(ConcurrentHashMap<K, V> param1ConcurrentHashMap, V param1V) {
      super(param1ConcurrentHashMap);
      this.value = param1V;
    }
    
    public V getMappedValue() { return (V)this.value; }
    
    public boolean contains(Object param1Object) { return this.map.containsKey(param1Object); }
    
    public boolean remove(Object param1Object) { return (this.map.remove(param1Object) != null); }
    
    public Iterator<K> iterator() {
      ConcurrentHashMap concurrentHashMap = this.map;
      ConcurrentHashMap.Node[] arrayOfNode;
      byte b = ((arrayOfNode = concurrentHashMap.table) == null) ? 0 : arrayOfNode.length;
      return new ConcurrentHashMap.KeyIterator(arrayOfNode, b, 0, b, concurrentHashMap);
    }
    
    public boolean add(K param1K) {
      Object object;
      if ((object = this.value) == null)
        throw new UnsupportedOperationException(); 
      return (this.map.putVal(param1K, object, true) == null);
    }
    
    public boolean addAll(Collection<? extends K> param1Collection) {
      boolean bool = false;
      Object object;
      if ((object = this.value) == null)
        throw new UnsupportedOperationException(); 
      for (Object object1 : param1Collection) {
        if (this.map.putVal(object1, object, true) == null)
          bool = true; 
      } 
      return bool;
    }
    
    public int hashCode() {
      int i = 0;
      for (Object object : this)
        i += object.hashCode(); 
      return i;
    }
    
    public boolean equals(Object param1Object) {
      Set set;
      return (param1Object instanceof Set && ((set = (Set)param1Object) == this || (containsAll(set) && set.containsAll(this))));
    }
    
    public Spliterator<K> spliterator() {
      ConcurrentHashMap concurrentHashMap = this.map;
      long l = concurrentHashMap.sumCount();
      ConcurrentHashMap.Node[] arrayOfNode;
      byte b = ((arrayOfNode = concurrentHashMap.table) == null) ? 0 : arrayOfNode.length;
      return new ConcurrentHashMap.KeySpliterator(arrayOfNode, b, 0, b, (l < 0L) ? 0L : l);
    }
    
    public void forEach(Consumer<? super K> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      ConcurrentHashMap.Node[] arrayOfNode;
      if ((arrayOfNode = this.map.table) != null) {
        ConcurrentHashMap.Traverser traverser = new ConcurrentHashMap.Traverser(arrayOfNode, arrayOfNode.length, 0, arrayOfNode.length);
        ConcurrentHashMap.Node node;
        while ((node = traverser.advance()) != null)
          param1Consumer.accept(node.key); 
      } 
    }
  }
  
  static final class KeySpliterator<K, V> extends Traverser<K, V> implements Spliterator<K> {
    long est;
    
    KeySpliterator(ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, int param1Int1, int param1Int2, int param1Int3, long param1Long) {
      super(param1ArrayOfNode, param1Int1, param1Int2, param1Int3);
      this.est = param1Long;
    }
    
    public Spliterator<K> trySplit() {
      int i;
      int j;
      int k;
      return ((k = (i = this.baseIndex) + (j = this.baseLimit) >>> 1) <= i) ? null : new KeySpliterator(this.tab, this.baseSize, this.baseLimit = k, j, this.est >>>= true);
    }
    
    public void forEachRemaining(Consumer<? super K> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      ConcurrentHashMap.Node node;
      while ((node = advance()) != null)
        param1Consumer.accept(node.key); 
    }
    
    public boolean tryAdvance(Consumer<? super K> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      ConcurrentHashMap.Node node;
      if ((node = advance()) == null)
        return false; 
      param1Consumer.accept(node.key);
      return true;
    }
    
    public long estimateSize() { return this.est; }
    
    public int characteristics() { return 4353; }
  }
  
  static final class MapEntry<K, V> extends Object implements Map.Entry<K, V> {
    final K key;
    
    V val;
    
    final ConcurrentHashMap<K, V> map;
    
    MapEntry(K param1K, V param1V, ConcurrentHashMap<K, V> param1ConcurrentHashMap) {
      this.key = param1K;
      this.val = param1V;
      this.map = param1ConcurrentHashMap;
    }
    
    public K getKey() { return (K)this.key; }
    
    public V getValue() { return (V)this.val; }
    
    public int hashCode() { return this.key.hashCode() ^ this.val.hashCode(); }
    
    public String toString() { return this.key + "=" + this.val; }
    
    public boolean equals(Object param1Object) {
      Object object1;
      Object object2;
      Map.Entry entry;
      return (param1Object instanceof Map.Entry && (object1 = (entry = (Map.Entry)param1Object).getKey()) != null && (object2 = entry.getValue()) != null && (object1 == this.key || object1.equals(this.key)) && (object2 == this.val || object2.equals(this.val)));
    }
    
    public V setValue(V param1V) {
      if (param1V == null)
        throw new NullPointerException(); 
      Object object = this.val;
      this.val = param1V;
      this.map.put(this.key, param1V);
      return (V)object;
    }
  }
  
  static final class MapReduceEntriesTask<K, V, U> extends BulkTask<K, V, U> {
    final Function<Map.Entry<K, V>, ? extends U> transformer;
    
    final BiFunction<? super U, ? super U, ? extends U> reducer;
    
    U result;
    
    MapReduceEntriesTask<K, V, U> rights;
    
    MapReduceEntriesTask<K, V, U> nextRight;
    
    MapReduceEntriesTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, MapReduceEntriesTask<K, V, U> param1MapReduceEntriesTask, Function<Map.Entry<K, V>, ? extends U> param1Function, BiFunction<? super U, ? super U, ? extends U> param1BiFunction) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.nextRight = param1MapReduceEntriesTask;
      this.transformer = param1Function;
      this.reducer = param1BiFunction;
    }
    
    public final U getRawResult() { return (U)this.result; }
    
    public final void compute() {
      Function function;
      BiFunction biFunction;
      if ((function = this.transformer) != null && (biFunction = this.reducer) != null) {
        int i = this.baseIndex;
        int j;
        int k;
        while (this.batch > 0 && (k = (j = this.baseLimit) + i >>> 1) > i) {
          addToPendingCount(1);
          (this.rights = new MapReduceEntriesTask(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, this.rights, function, biFunction)).fork();
        } 
        Object object = null;
        ConcurrentHashMap.Node node;
        while ((node = advance()) != null) {
          Object object1;
          if ((object1 = function.apply(node)) != null)
            object = (object == null) ? object1 : biFunction.apply(object, object1); 
        } 
        this.result = object;
        for (CountedCompleter countedCompleter = firstComplete(); countedCompleter != null; countedCompleter = countedCompleter.nextComplete()) {
          MapReduceEntriesTask mapReduceEntriesTask1 = (MapReduceEntriesTask)countedCompleter;
          for (MapReduceEntriesTask mapReduceEntriesTask2 = mapReduceEntriesTask1.rights; mapReduceEntriesTask2 != null; mapReduceEntriesTask2 = mapReduceEntriesTask1.rights = mapReduceEntriesTask2.nextRight) {
            Object object1;
            if ((object1 = mapReduceEntriesTask2.result) != null) {
              Object object2;
              mapReduceEntriesTask1.result = ((object2 = mapReduceEntriesTask1.result) == null) ? object1 : biFunction.apply(object2, object1);
            } 
          } 
        } 
      } 
    }
  }
  
  static final class MapReduceEntriesToDoubleTask<K, V> extends BulkTask<K, V, Double> {
    final ToDoubleFunction<Map.Entry<K, V>> transformer;
    
    final DoubleBinaryOperator reducer;
    
    final double basis;
    
    double result;
    
    MapReduceEntriesToDoubleTask<K, V> rights;
    
    MapReduceEntriesToDoubleTask<K, V> nextRight;
    
    MapReduceEntriesToDoubleTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, MapReduceEntriesToDoubleTask<K, V> param1MapReduceEntriesToDoubleTask, ToDoubleFunction<Map.Entry<K, V>> param1ToDoubleFunction, double param1Double, DoubleBinaryOperator param1DoubleBinaryOperator) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.nextRight = param1MapReduceEntriesToDoubleTask;
      this.transformer = param1ToDoubleFunction;
      this.basis = param1Double;
      this.reducer = param1DoubleBinaryOperator;
    }
    
    public final Double getRawResult() { return Double.valueOf(this.result); }
    
    public final void compute() {
      ToDoubleFunction toDoubleFunction;
      DoubleBinaryOperator doubleBinaryOperator;
      if ((toDoubleFunction = this.transformer) != null && (doubleBinaryOperator = this.reducer) != null) {
        double d = this.basis;
        int i = this.baseIndex;
        int j;
        int k;
        while (this.batch > 0 && (k = (j = this.baseLimit) + i >>> 1) > i) {
          addToPendingCount(1);
          (this.rights = new MapReduceEntriesToDoubleTask(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, this.rights, toDoubleFunction, d, doubleBinaryOperator)).fork();
        } 
        ConcurrentHashMap.Node node;
        while ((node = advance()) != null)
          d = doubleBinaryOperator.applyAsDouble(d, toDoubleFunction.applyAsDouble(node)); 
        this.result = d;
        for (CountedCompleter countedCompleter = firstComplete(); countedCompleter != null; countedCompleter = countedCompleter.nextComplete()) {
          MapReduceEntriesToDoubleTask mapReduceEntriesToDoubleTask1 = (MapReduceEntriesToDoubleTask)countedCompleter;
          for (MapReduceEntriesToDoubleTask mapReduceEntriesToDoubleTask2 = mapReduceEntriesToDoubleTask1.rights; mapReduceEntriesToDoubleTask2 != null; mapReduceEntriesToDoubleTask2 = mapReduceEntriesToDoubleTask1.rights = mapReduceEntriesToDoubleTask2.nextRight)
            mapReduceEntriesToDoubleTask1.result = doubleBinaryOperator.applyAsDouble(mapReduceEntriesToDoubleTask1.result, mapReduceEntriesToDoubleTask2.result); 
        } 
      } 
    }
  }
  
  static final class MapReduceEntriesToIntTask<K, V> extends BulkTask<K, V, Integer> {
    final ToIntFunction<Map.Entry<K, V>> transformer;
    
    final IntBinaryOperator reducer;
    
    final int basis;
    
    int result;
    
    MapReduceEntriesToIntTask<K, V> rights;
    
    MapReduceEntriesToIntTask<K, V> nextRight;
    
    MapReduceEntriesToIntTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, MapReduceEntriesToIntTask<K, V> param1MapReduceEntriesToIntTask, ToIntFunction<Map.Entry<K, V>> param1ToIntFunction, int param1Int4, IntBinaryOperator param1IntBinaryOperator) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.nextRight = param1MapReduceEntriesToIntTask;
      this.transformer = param1ToIntFunction;
      this.basis = param1Int4;
      this.reducer = param1IntBinaryOperator;
    }
    
    public final Integer getRawResult() { return Integer.valueOf(this.result); }
    
    public final void compute() {
      ToIntFunction toIntFunction;
      IntBinaryOperator intBinaryOperator;
      if ((toIntFunction = this.transformer) != null && (intBinaryOperator = this.reducer) != null) {
        int i = this.basis;
        int j = this.baseIndex;
        int k;
        int m;
        while (this.batch > 0 && (m = (k = this.baseLimit) + j >>> 1) > j) {
          addToPendingCount(1);
          (this.rights = new MapReduceEntriesToIntTask(this, this.batch >>>= 1, this.baseLimit = m, k, this.tab, this.rights, toIntFunction, i, intBinaryOperator)).fork();
        } 
        ConcurrentHashMap.Node node;
        while ((node = advance()) != null)
          i = intBinaryOperator.applyAsInt(i, toIntFunction.applyAsInt(node)); 
        this.result = i;
        for (CountedCompleter countedCompleter = firstComplete(); countedCompleter != null; countedCompleter = countedCompleter.nextComplete()) {
          MapReduceEntriesToIntTask mapReduceEntriesToIntTask1 = (MapReduceEntriesToIntTask)countedCompleter;
          for (MapReduceEntriesToIntTask mapReduceEntriesToIntTask2 = mapReduceEntriesToIntTask1.rights; mapReduceEntriesToIntTask2 != null; mapReduceEntriesToIntTask2 = mapReduceEntriesToIntTask1.rights = mapReduceEntriesToIntTask2.nextRight)
            mapReduceEntriesToIntTask1.result = intBinaryOperator.applyAsInt(mapReduceEntriesToIntTask1.result, mapReduceEntriesToIntTask2.result); 
        } 
      } 
    }
  }
  
  static final class MapReduceEntriesToLongTask<K, V> extends BulkTask<K, V, Long> {
    final ToLongFunction<Map.Entry<K, V>> transformer;
    
    final LongBinaryOperator reducer;
    
    final long basis;
    
    long result;
    
    MapReduceEntriesToLongTask<K, V> rights;
    
    MapReduceEntriesToLongTask<K, V> nextRight;
    
    MapReduceEntriesToLongTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, MapReduceEntriesToLongTask<K, V> param1MapReduceEntriesToLongTask, ToLongFunction<Map.Entry<K, V>> param1ToLongFunction, long param1Long, LongBinaryOperator param1LongBinaryOperator) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.nextRight = param1MapReduceEntriesToLongTask;
      this.transformer = param1ToLongFunction;
      this.basis = param1Long;
      this.reducer = param1LongBinaryOperator;
    }
    
    public final Long getRawResult() { return Long.valueOf(this.result); }
    
    public final void compute() {
      ToLongFunction toLongFunction;
      LongBinaryOperator longBinaryOperator;
      if ((toLongFunction = this.transformer) != null && (longBinaryOperator = this.reducer) != null) {
        long l = this.basis;
        int i = this.baseIndex;
        int j;
        int k;
        while (this.batch > 0 && (k = (j = this.baseLimit) + i >>> 1) > i) {
          addToPendingCount(1);
          (this.rights = new MapReduceEntriesToLongTask(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, this.rights, toLongFunction, l, longBinaryOperator)).fork();
        } 
        ConcurrentHashMap.Node node;
        while ((node = advance()) != null)
          l = longBinaryOperator.applyAsLong(l, toLongFunction.applyAsLong(node)); 
        this.result = l;
        for (CountedCompleter countedCompleter = firstComplete(); countedCompleter != null; countedCompleter = countedCompleter.nextComplete()) {
          MapReduceEntriesToLongTask mapReduceEntriesToLongTask1 = (MapReduceEntriesToLongTask)countedCompleter;
          for (MapReduceEntriesToLongTask mapReduceEntriesToLongTask2 = mapReduceEntriesToLongTask1.rights; mapReduceEntriesToLongTask2 != null; mapReduceEntriesToLongTask2 = mapReduceEntriesToLongTask1.rights = mapReduceEntriesToLongTask2.nextRight)
            mapReduceEntriesToLongTask1.result = longBinaryOperator.applyAsLong(mapReduceEntriesToLongTask1.result, mapReduceEntriesToLongTask2.result); 
        } 
      } 
    }
  }
  
  static final class MapReduceKeysTask<K, V, U> extends BulkTask<K, V, U> {
    final Function<? super K, ? extends U> transformer;
    
    final BiFunction<? super U, ? super U, ? extends U> reducer;
    
    U result;
    
    MapReduceKeysTask<K, V, U> rights;
    
    MapReduceKeysTask<K, V, U> nextRight;
    
    MapReduceKeysTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, MapReduceKeysTask<K, V, U> param1MapReduceKeysTask, Function<? super K, ? extends U> param1Function, BiFunction<? super U, ? super U, ? extends U> param1BiFunction) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.nextRight = param1MapReduceKeysTask;
      this.transformer = param1Function;
      this.reducer = param1BiFunction;
    }
    
    public final U getRawResult() { return (U)this.result; }
    
    public final void compute() {
      Function function;
      BiFunction biFunction;
      if ((function = this.transformer) != null && (biFunction = this.reducer) != null) {
        int i = this.baseIndex;
        int j;
        int k;
        while (this.batch > 0 && (k = (j = this.baseLimit) + i >>> 1) > i) {
          addToPendingCount(1);
          (this.rights = new MapReduceKeysTask(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, this.rights, function, biFunction)).fork();
        } 
        Object object = null;
        ConcurrentHashMap.Node node;
        while ((node = advance()) != null) {
          Object object1;
          if ((object1 = function.apply(node.key)) != null)
            object = (object == null) ? object1 : biFunction.apply(object, object1); 
        } 
        this.result = object;
        for (CountedCompleter countedCompleter = firstComplete(); countedCompleter != null; countedCompleter = countedCompleter.nextComplete()) {
          MapReduceKeysTask mapReduceKeysTask1 = (MapReduceKeysTask)countedCompleter;
          for (MapReduceKeysTask mapReduceKeysTask2 = mapReduceKeysTask1.rights; mapReduceKeysTask2 != null; mapReduceKeysTask2 = mapReduceKeysTask1.rights = mapReduceKeysTask2.nextRight) {
            Object object1;
            if ((object1 = mapReduceKeysTask2.result) != null) {
              Object object2;
              mapReduceKeysTask1.result = ((object2 = mapReduceKeysTask1.result) == null) ? object1 : biFunction.apply(object2, object1);
            } 
          } 
        } 
      } 
    }
  }
  
  static final class MapReduceKeysToDoubleTask<K, V> extends BulkTask<K, V, Double> {
    final ToDoubleFunction<? super K> transformer;
    
    final DoubleBinaryOperator reducer;
    
    final double basis;
    
    double result;
    
    MapReduceKeysToDoubleTask<K, V> rights;
    
    MapReduceKeysToDoubleTask<K, V> nextRight;
    
    MapReduceKeysToDoubleTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, MapReduceKeysToDoubleTask<K, V> param1MapReduceKeysToDoubleTask, ToDoubleFunction<? super K> param1ToDoubleFunction, double param1Double, DoubleBinaryOperator param1DoubleBinaryOperator) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.nextRight = param1MapReduceKeysToDoubleTask;
      this.transformer = param1ToDoubleFunction;
      this.basis = param1Double;
      this.reducer = param1DoubleBinaryOperator;
    }
    
    public final Double getRawResult() { return Double.valueOf(this.result); }
    
    public final void compute() {
      ToDoubleFunction toDoubleFunction;
      DoubleBinaryOperator doubleBinaryOperator;
      if ((toDoubleFunction = this.transformer) != null && (doubleBinaryOperator = this.reducer) != null) {
        double d = this.basis;
        int i = this.baseIndex;
        int j;
        int k;
        while (this.batch > 0 && (k = (j = this.baseLimit) + i >>> 1) > i) {
          addToPendingCount(1);
          (this.rights = new MapReduceKeysToDoubleTask(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, this.rights, toDoubleFunction, d, doubleBinaryOperator)).fork();
        } 
        ConcurrentHashMap.Node node;
        while ((node = advance()) != null)
          d = doubleBinaryOperator.applyAsDouble(d, toDoubleFunction.applyAsDouble(node.key)); 
        this.result = d;
        for (CountedCompleter countedCompleter = firstComplete(); countedCompleter != null; countedCompleter = countedCompleter.nextComplete()) {
          MapReduceKeysToDoubleTask mapReduceKeysToDoubleTask1 = (MapReduceKeysToDoubleTask)countedCompleter;
          for (MapReduceKeysToDoubleTask mapReduceKeysToDoubleTask2 = mapReduceKeysToDoubleTask1.rights; mapReduceKeysToDoubleTask2 != null; mapReduceKeysToDoubleTask2 = mapReduceKeysToDoubleTask1.rights = mapReduceKeysToDoubleTask2.nextRight)
            mapReduceKeysToDoubleTask1.result = doubleBinaryOperator.applyAsDouble(mapReduceKeysToDoubleTask1.result, mapReduceKeysToDoubleTask2.result); 
        } 
      } 
    }
  }
  
  static final class MapReduceKeysToIntTask<K, V> extends BulkTask<K, V, Integer> {
    final ToIntFunction<? super K> transformer;
    
    final IntBinaryOperator reducer;
    
    final int basis;
    
    int result;
    
    MapReduceKeysToIntTask<K, V> rights;
    
    MapReduceKeysToIntTask<K, V> nextRight;
    
    MapReduceKeysToIntTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, MapReduceKeysToIntTask<K, V> param1MapReduceKeysToIntTask, ToIntFunction<? super K> param1ToIntFunction, int param1Int4, IntBinaryOperator param1IntBinaryOperator) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.nextRight = param1MapReduceKeysToIntTask;
      this.transformer = param1ToIntFunction;
      this.basis = param1Int4;
      this.reducer = param1IntBinaryOperator;
    }
    
    public final Integer getRawResult() { return Integer.valueOf(this.result); }
    
    public final void compute() {
      ToIntFunction toIntFunction;
      IntBinaryOperator intBinaryOperator;
      if ((toIntFunction = this.transformer) != null && (intBinaryOperator = this.reducer) != null) {
        int i = this.basis;
        int j = this.baseIndex;
        int k;
        int m;
        while (this.batch > 0 && (m = (k = this.baseLimit) + j >>> 1) > j) {
          addToPendingCount(1);
          (this.rights = new MapReduceKeysToIntTask(this, this.batch >>>= 1, this.baseLimit = m, k, this.tab, this.rights, toIntFunction, i, intBinaryOperator)).fork();
        } 
        ConcurrentHashMap.Node node;
        while ((node = advance()) != null)
          i = intBinaryOperator.applyAsInt(i, toIntFunction.applyAsInt(node.key)); 
        this.result = i;
        for (CountedCompleter countedCompleter = firstComplete(); countedCompleter != null; countedCompleter = countedCompleter.nextComplete()) {
          MapReduceKeysToIntTask mapReduceKeysToIntTask1 = (MapReduceKeysToIntTask)countedCompleter;
          for (MapReduceKeysToIntTask mapReduceKeysToIntTask2 = mapReduceKeysToIntTask1.rights; mapReduceKeysToIntTask2 != null; mapReduceKeysToIntTask2 = mapReduceKeysToIntTask1.rights = mapReduceKeysToIntTask2.nextRight)
            mapReduceKeysToIntTask1.result = intBinaryOperator.applyAsInt(mapReduceKeysToIntTask1.result, mapReduceKeysToIntTask2.result); 
        } 
      } 
    }
  }
  
  static final class MapReduceKeysToLongTask<K, V> extends BulkTask<K, V, Long> {
    final ToLongFunction<? super K> transformer;
    
    final LongBinaryOperator reducer;
    
    final long basis;
    
    long result;
    
    MapReduceKeysToLongTask<K, V> rights;
    
    MapReduceKeysToLongTask<K, V> nextRight;
    
    MapReduceKeysToLongTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, MapReduceKeysToLongTask<K, V> param1MapReduceKeysToLongTask, ToLongFunction<? super K> param1ToLongFunction, long param1Long, LongBinaryOperator param1LongBinaryOperator) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.nextRight = param1MapReduceKeysToLongTask;
      this.transformer = param1ToLongFunction;
      this.basis = param1Long;
      this.reducer = param1LongBinaryOperator;
    }
    
    public final Long getRawResult() { return Long.valueOf(this.result); }
    
    public final void compute() {
      ToLongFunction toLongFunction;
      LongBinaryOperator longBinaryOperator;
      if ((toLongFunction = this.transformer) != null && (longBinaryOperator = this.reducer) != null) {
        long l = this.basis;
        int i = this.baseIndex;
        int j;
        int k;
        while (this.batch > 0 && (k = (j = this.baseLimit) + i >>> 1) > i) {
          addToPendingCount(1);
          (this.rights = new MapReduceKeysToLongTask(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, this.rights, toLongFunction, l, longBinaryOperator)).fork();
        } 
        ConcurrentHashMap.Node node;
        while ((node = advance()) != null)
          l = longBinaryOperator.applyAsLong(l, toLongFunction.applyAsLong(node.key)); 
        this.result = l;
        for (CountedCompleter countedCompleter = firstComplete(); countedCompleter != null; countedCompleter = countedCompleter.nextComplete()) {
          MapReduceKeysToLongTask mapReduceKeysToLongTask1 = (MapReduceKeysToLongTask)countedCompleter;
          for (MapReduceKeysToLongTask mapReduceKeysToLongTask2 = mapReduceKeysToLongTask1.rights; mapReduceKeysToLongTask2 != null; mapReduceKeysToLongTask2 = mapReduceKeysToLongTask1.rights = mapReduceKeysToLongTask2.nextRight)
            mapReduceKeysToLongTask1.result = longBinaryOperator.applyAsLong(mapReduceKeysToLongTask1.result, mapReduceKeysToLongTask2.result); 
        } 
      } 
    }
  }
  
  static final class MapReduceMappingsTask<K, V, U> extends BulkTask<K, V, U> {
    final BiFunction<? super K, ? super V, ? extends U> transformer;
    
    final BiFunction<? super U, ? super U, ? extends U> reducer;
    
    U result;
    
    MapReduceMappingsTask<K, V, U> rights;
    
    MapReduceMappingsTask<K, V, U> nextRight;
    
    MapReduceMappingsTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, MapReduceMappingsTask<K, V, U> param1MapReduceMappingsTask, BiFunction<? super K, ? super V, ? extends U> param1BiFunction1, BiFunction<? super U, ? super U, ? extends U> param1BiFunction2) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.nextRight = param1MapReduceMappingsTask;
      this.transformer = param1BiFunction1;
      this.reducer = param1BiFunction2;
    }
    
    public final U getRawResult() { return (U)this.result; }
    
    public final void compute() {
      BiFunction biFunction1;
      BiFunction biFunction2;
      if ((biFunction1 = this.transformer) != null && (biFunction2 = this.reducer) != null) {
        int i = this.baseIndex;
        int j;
        int k;
        while (this.batch > 0 && (k = (j = this.baseLimit) + i >>> 1) > i) {
          addToPendingCount(1);
          (this.rights = new MapReduceMappingsTask(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, this.rights, biFunction1, biFunction2)).fork();
        } 
        Object object = null;
        ConcurrentHashMap.Node node;
        while ((node = advance()) != null) {
          Object object1;
          if ((object1 = biFunction1.apply(node.key, node.val)) != null)
            object = (object == null) ? object1 : biFunction2.apply(object, object1); 
        } 
        this.result = object;
        for (CountedCompleter countedCompleter = firstComplete(); countedCompleter != null; countedCompleter = countedCompleter.nextComplete()) {
          MapReduceMappingsTask mapReduceMappingsTask1 = (MapReduceMappingsTask)countedCompleter;
          for (MapReduceMappingsTask mapReduceMappingsTask2 = mapReduceMappingsTask1.rights; mapReduceMappingsTask2 != null; mapReduceMappingsTask2 = mapReduceMappingsTask1.rights = mapReduceMappingsTask2.nextRight) {
            Object object1;
            if ((object1 = mapReduceMappingsTask2.result) != null) {
              Object object2;
              mapReduceMappingsTask1.result = ((object2 = mapReduceMappingsTask1.result) == null) ? object1 : biFunction2.apply(object2, object1);
            } 
          } 
        } 
      } 
    }
  }
  
  static final class MapReduceMappingsToDoubleTask<K, V> extends BulkTask<K, V, Double> {
    final ToDoubleBiFunction<? super K, ? super V> transformer;
    
    final DoubleBinaryOperator reducer;
    
    final double basis;
    
    double result;
    
    MapReduceMappingsToDoubleTask<K, V> rights;
    
    MapReduceMappingsToDoubleTask<K, V> nextRight;
    
    MapReduceMappingsToDoubleTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, MapReduceMappingsToDoubleTask<K, V> param1MapReduceMappingsToDoubleTask, ToDoubleBiFunction<? super K, ? super V> param1ToDoubleBiFunction, double param1Double, DoubleBinaryOperator param1DoubleBinaryOperator) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.nextRight = param1MapReduceMappingsToDoubleTask;
      this.transformer = param1ToDoubleBiFunction;
      this.basis = param1Double;
      this.reducer = param1DoubleBinaryOperator;
    }
    
    public final Double getRawResult() { return Double.valueOf(this.result); }
    
    public final void compute() {
      ToDoubleBiFunction toDoubleBiFunction;
      DoubleBinaryOperator doubleBinaryOperator;
      if ((toDoubleBiFunction = this.transformer) != null && (doubleBinaryOperator = this.reducer) != null) {
        double d = this.basis;
        int i = this.baseIndex;
        int j;
        int k;
        while (this.batch > 0 && (k = (j = this.baseLimit) + i >>> 1) > i) {
          addToPendingCount(1);
          (this.rights = new MapReduceMappingsToDoubleTask(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, this.rights, toDoubleBiFunction, d, doubleBinaryOperator)).fork();
        } 
        ConcurrentHashMap.Node node;
        while ((node = advance()) != null)
          d = doubleBinaryOperator.applyAsDouble(d, toDoubleBiFunction.applyAsDouble(node.key, node.val)); 
        this.result = d;
        for (CountedCompleter countedCompleter = firstComplete(); countedCompleter != null; countedCompleter = countedCompleter.nextComplete()) {
          MapReduceMappingsToDoubleTask mapReduceMappingsToDoubleTask1 = (MapReduceMappingsToDoubleTask)countedCompleter;
          for (MapReduceMappingsToDoubleTask mapReduceMappingsToDoubleTask2 = mapReduceMappingsToDoubleTask1.rights; mapReduceMappingsToDoubleTask2 != null; mapReduceMappingsToDoubleTask2 = mapReduceMappingsToDoubleTask1.rights = mapReduceMappingsToDoubleTask2.nextRight)
            mapReduceMappingsToDoubleTask1.result = doubleBinaryOperator.applyAsDouble(mapReduceMappingsToDoubleTask1.result, mapReduceMappingsToDoubleTask2.result); 
        } 
      } 
    }
  }
  
  static final class MapReduceMappingsToIntTask<K, V> extends BulkTask<K, V, Integer> {
    final ToIntBiFunction<? super K, ? super V> transformer;
    
    final IntBinaryOperator reducer;
    
    final int basis;
    
    int result;
    
    MapReduceMappingsToIntTask<K, V> rights;
    
    MapReduceMappingsToIntTask<K, V> nextRight;
    
    MapReduceMappingsToIntTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, MapReduceMappingsToIntTask<K, V> param1MapReduceMappingsToIntTask, ToIntBiFunction<? super K, ? super V> param1ToIntBiFunction, int param1Int4, IntBinaryOperator param1IntBinaryOperator) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.nextRight = param1MapReduceMappingsToIntTask;
      this.transformer = param1ToIntBiFunction;
      this.basis = param1Int4;
      this.reducer = param1IntBinaryOperator;
    }
    
    public final Integer getRawResult() { return Integer.valueOf(this.result); }
    
    public final void compute() {
      ToIntBiFunction toIntBiFunction;
      IntBinaryOperator intBinaryOperator;
      if ((toIntBiFunction = this.transformer) != null && (intBinaryOperator = this.reducer) != null) {
        int i = this.basis;
        int j = this.baseIndex;
        int k;
        int m;
        while (this.batch > 0 && (m = (k = this.baseLimit) + j >>> 1) > j) {
          addToPendingCount(1);
          (this.rights = new MapReduceMappingsToIntTask(this, this.batch >>>= 1, this.baseLimit = m, k, this.tab, this.rights, toIntBiFunction, i, intBinaryOperator)).fork();
        } 
        ConcurrentHashMap.Node node;
        while ((node = advance()) != null)
          i = intBinaryOperator.applyAsInt(i, toIntBiFunction.applyAsInt(node.key, node.val)); 
        this.result = i;
        for (CountedCompleter countedCompleter = firstComplete(); countedCompleter != null; countedCompleter = countedCompleter.nextComplete()) {
          MapReduceMappingsToIntTask mapReduceMappingsToIntTask1 = (MapReduceMappingsToIntTask)countedCompleter;
          for (MapReduceMappingsToIntTask mapReduceMappingsToIntTask2 = mapReduceMappingsToIntTask1.rights; mapReduceMappingsToIntTask2 != null; mapReduceMappingsToIntTask2 = mapReduceMappingsToIntTask1.rights = mapReduceMappingsToIntTask2.nextRight)
            mapReduceMappingsToIntTask1.result = intBinaryOperator.applyAsInt(mapReduceMappingsToIntTask1.result, mapReduceMappingsToIntTask2.result); 
        } 
      } 
    }
  }
  
  static final class MapReduceMappingsToLongTask<K, V> extends BulkTask<K, V, Long> {
    final ToLongBiFunction<? super K, ? super V> transformer;
    
    final LongBinaryOperator reducer;
    
    final long basis;
    
    long result;
    
    MapReduceMappingsToLongTask<K, V> rights;
    
    MapReduceMappingsToLongTask<K, V> nextRight;
    
    MapReduceMappingsToLongTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, MapReduceMappingsToLongTask<K, V> param1MapReduceMappingsToLongTask, ToLongBiFunction<? super K, ? super V> param1ToLongBiFunction, long param1Long, LongBinaryOperator param1LongBinaryOperator) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.nextRight = param1MapReduceMappingsToLongTask;
      this.transformer = param1ToLongBiFunction;
      this.basis = param1Long;
      this.reducer = param1LongBinaryOperator;
    }
    
    public final Long getRawResult() { return Long.valueOf(this.result); }
    
    public final void compute() {
      ToLongBiFunction toLongBiFunction;
      LongBinaryOperator longBinaryOperator;
      if ((toLongBiFunction = this.transformer) != null && (longBinaryOperator = this.reducer) != null) {
        long l = this.basis;
        int i = this.baseIndex;
        int j;
        int k;
        while (this.batch > 0 && (k = (j = this.baseLimit) + i >>> 1) > i) {
          addToPendingCount(1);
          (this.rights = new MapReduceMappingsToLongTask(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, this.rights, toLongBiFunction, l, longBinaryOperator)).fork();
        } 
        ConcurrentHashMap.Node node;
        while ((node = advance()) != null)
          l = longBinaryOperator.applyAsLong(l, toLongBiFunction.applyAsLong(node.key, node.val)); 
        this.result = l;
        for (CountedCompleter countedCompleter = firstComplete(); countedCompleter != null; countedCompleter = countedCompleter.nextComplete()) {
          MapReduceMappingsToLongTask mapReduceMappingsToLongTask1 = (MapReduceMappingsToLongTask)countedCompleter;
          for (MapReduceMappingsToLongTask mapReduceMappingsToLongTask2 = mapReduceMappingsToLongTask1.rights; mapReduceMappingsToLongTask2 != null; mapReduceMappingsToLongTask2 = mapReduceMappingsToLongTask1.rights = mapReduceMappingsToLongTask2.nextRight)
            mapReduceMappingsToLongTask1.result = longBinaryOperator.applyAsLong(mapReduceMappingsToLongTask1.result, mapReduceMappingsToLongTask2.result); 
        } 
      } 
    }
  }
  
  static final class MapReduceValuesTask<K, V, U> extends BulkTask<K, V, U> {
    final Function<? super V, ? extends U> transformer;
    
    final BiFunction<? super U, ? super U, ? extends U> reducer;
    
    U result;
    
    MapReduceValuesTask<K, V, U> rights;
    
    MapReduceValuesTask<K, V, U> nextRight;
    
    MapReduceValuesTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, MapReduceValuesTask<K, V, U> param1MapReduceValuesTask, Function<? super V, ? extends U> param1Function, BiFunction<? super U, ? super U, ? extends U> param1BiFunction) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.nextRight = param1MapReduceValuesTask;
      this.transformer = param1Function;
      this.reducer = param1BiFunction;
    }
    
    public final U getRawResult() { return (U)this.result; }
    
    public final void compute() {
      Function function;
      BiFunction biFunction;
      if ((function = this.transformer) != null && (biFunction = this.reducer) != null) {
        int i = this.baseIndex;
        int j;
        int k;
        while (this.batch > 0 && (k = (j = this.baseLimit) + i >>> 1) > i) {
          addToPendingCount(1);
          (this.rights = new MapReduceValuesTask(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, this.rights, function, biFunction)).fork();
        } 
        Object object = null;
        ConcurrentHashMap.Node node;
        while ((node = advance()) != null) {
          Object object1;
          if ((object1 = function.apply(node.val)) != null)
            object = (object == null) ? object1 : biFunction.apply(object, object1); 
        } 
        this.result = object;
        for (CountedCompleter countedCompleter = firstComplete(); countedCompleter != null; countedCompleter = countedCompleter.nextComplete()) {
          MapReduceValuesTask mapReduceValuesTask1 = (MapReduceValuesTask)countedCompleter;
          for (MapReduceValuesTask mapReduceValuesTask2 = mapReduceValuesTask1.rights; mapReduceValuesTask2 != null; mapReduceValuesTask2 = mapReduceValuesTask1.rights = mapReduceValuesTask2.nextRight) {
            Object object1;
            if ((object1 = mapReduceValuesTask2.result) != null) {
              Object object2;
              mapReduceValuesTask1.result = ((object2 = mapReduceValuesTask1.result) == null) ? object1 : biFunction.apply(object2, object1);
            } 
          } 
        } 
      } 
    }
  }
  
  static final class MapReduceValuesToDoubleTask<K, V> extends BulkTask<K, V, Double> {
    final ToDoubleFunction<? super V> transformer;
    
    final DoubleBinaryOperator reducer;
    
    final double basis;
    
    double result;
    
    MapReduceValuesToDoubleTask<K, V> rights;
    
    MapReduceValuesToDoubleTask<K, V> nextRight;
    
    MapReduceValuesToDoubleTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, MapReduceValuesToDoubleTask<K, V> param1MapReduceValuesToDoubleTask, ToDoubleFunction<? super V> param1ToDoubleFunction, double param1Double, DoubleBinaryOperator param1DoubleBinaryOperator) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.nextRight = param1MapReduceValuesToDoubleTask;
      this.transformer = param1ToDoubleFunction;
      this.basis = param1Double;
      this.reducer = param1DoubleBinaryOperator;
    }
    
    public final Double getRawResult() { return Double.valueOf(this.result); }
    
    public final void compute() {
      ToDoubleFunction toDoubleFunction;
      DoubleBinaryOperator doubleBinaryOperator;
      if ((toDoubleFunction = this.transformer) != null && (doubleBinaryOperator = this.reducer) != null) {
        double d = this.basis;
        int i = this.baseIndex;
        int j;
        int k;
        while (this.batch > 0 && (k = (j = this.baseLimit) + i >>> 1) > i) {
          addToPendingCount(1);
          (this.rights = new MapReduceValuesToDoubleTask(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, this.rights, toDoubleFunction, d, doubleBinaryOperator)).fork();
        } 
        ConcurrentHashMap.Node node;
        while ((node = advance()) != null)
          d = doubleBinaryOperator.applyAsDouble(d, toDoubleFunction.applyAsDouble(node.val)); 
        this.result = d;
        for (CountedCompleter countedCompleter = firstComplete(); countedCompleter != null; countedCompleter = countedCompleter.nextComplete()) {
          MapReduceValuesToDoubleTask mapReduceValuesToDoubleTask1 = (MapReduceValuesToDoubleTask)countedCompleter;
          for (MapReduceValuesToDoubleTask mapReduceValuesToDoubleTask2 = mapReduceValuesToDoubleTask1.rights; mapReduceValuesToDoubleTask2 != null; mapReduceValuesToDoubleTask2 = mapReduceValuesToDoubleTask1.rights = mapReduceValuesToDoubleTask2.nextRight)
            mapReduceValuesToDoubleTask1.result = doubleBinaryOperator.applyAsDouble(mapReduceValuesToDoubleTask1.result, mapReduceValuesToDoubleTask2.result); 
        } 
      } 
    }
  }
  
  static final class MapReduceValuesToIntTask<K, V> extends BulkTask<K, V, Integer> {
    final ToIntFunction<? super V> transformer;
    
    final IntBinaryOperator reducer;
    
    final int basis;
    
    int result;
    
    MapReduceValuesToIntTask<K, V> rights;
    
    MapReduceValuesToIntTask<K, V> nextRight;
    
    MapReduceValuesToIntTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, MapReduceValuesToIntTask<K, V> param1MapReduceValuesToIntTask, ToIntFunction<? super V> param1ToIntFunction, int param1Int4, IntBinaryOperator param1IntBinaryOperator) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.nextRight = param1MapReduceValuesToIntTask;
      this.transformer = param1ToIntFunction;
      this.basis = param1Int4;
      this.reducer = param1IntBinaryOperator;
    }
    
    public final Integer getRawResult() { return Integer.valueOf(this.result); }
    
    public final void compute() {
      ToIntFunction toIntFunction;
      IntBinaryOperator intBinaryOperator;
      if ((toIntFunction = this.transformer) != null && (intBinaryOperator = this.reducer) != null) {
        int i = this.basis;
        int j = this.baseIndex;
        int k;
        int m;
        while (this.batch > 0 && (m = (k = this.baseLimit) + j >>> 1) > j) {
          addToPendingCount(1);
          (this.rights = new MapReduceValuesToIntTask(this, this.batch >>>= 1, this.baseLimit = m, k, this.tab, this.rights, toIntFunction, i, intBinaryOperator)).fork();
        } 
        ConcurrentHashMap.Node node;
        while ((node = advance()) != null)
          i = intBinaryOperator.applyAsInt(i, toIntFunction.applyAsInt(node.val)); 
        this.result = i;
        for (CountedCompleter countedCompleter = firstComplete(); countedCompleter != null; countedCompleter = countedCompleter.nextComplete()) {
          MapReduceValuesToIntTask mapReduceValuesToIntTask1 = (MapReduceValuesToIntTask)countedCompleter;
          for (MapReduceValuesToIntTask mapReduceValuesToIntTask2 = mapReduceValuesToIntTask1.rights; mapReduceValuesToIntTask2 != null; mapReduceValuesToIntTask2 = mapReduceValuesToIntTask1.rights = mapReduceValuesToIntTask2.nextRight)
            mapReduceValuesToIntTask1.result = intBinaryOperator.applyAsInt(mapReduceValuesToIntTask1.result, mapReduceValuesToIntTask2.result); 
        } 
      } 
    }
  }
  
  static final class MapReduceValuesToLongTask<K, V> extends BulkTask<K, V, Long> {
    final ToLongFunction<? super V> transformer;
    
    final LongBinaryOperator reducer;
    
    final long basis;
    
    long result;
    
    MapReduceValuesToLongTask<K, V> rights;
    
    MapReduceValuesToLongTask<K, V> nextRight;
    
    MapReduceValuesToLongTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, MapReduceValuesToLongTask<K, V> param1MapReduceValuesToLongTask, ToLongFunction<? super V> param1ToLongFunction, long param1Long, LongBinaryOperator param1LongBinaryOperator) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.nextRight = param1MapReduceValuesToLongTask;
      this.transformer = param1ToLongFunction;
      this.basis = param1Long;
      this.reducer = param1LongBinaryOperator;
    }
    
    public final Long getRawResult() { return Long.valueOf(this.result); }
    
    public final void compute() {
      ToLongFunction toLongFunction;
      LongBinaryOperator longBinaryOperator;
      if ((toLongFunction = this.transformer) != null && (longBinaryOperator = this.reducer) != null) {
        long l = this.basis;
        int i = this.baseIndex;
        int j;
        int k;
        while (this.batch > 0 && (k = (j = this.baseLimit) + i >>> 1) > i) {
          addToPendingCount(1);
          (this.rights = new MapReduceValuesToLongTask(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, this.rights, toLongFunction, l, longBinaryOperator)).fork();
        } 
        ConcurrentHashMap.Node node;
        while ((node = advance()) != null)
          l = longBinaryOperator.applyAsLong(l, toLongFunction.applyAsLong(node.val)); 
        this.result = l;
        for (CountedCompleter countedCompleter = firstComplete(); countedCompleter != null; countedCompleter = countedCompleter.nextComplete()) {
          MapReduceValuesToLongTask mapReduceValuesToLongTask1 = (MapReduceValuesToLongTask)countedCompleter;
          for (MapReduceValuesToLongTask mapReduceValuesToLongTask2 = mapReduceValuesToLongTask1.rights; mapReduceValuesToLongTask2 != null; mapReduceValuesToLongTask2 = mapReduceValuesToLongTask1.rights = mapReduceValuesToLongTask2.nextRight)
            mapReduceValuesToLongTask1.result = longBinaryOperator.applyAsLong(mapReduceValuesToLongTask1.result, mapReduceValuesToLongTask2.result); 
        } 
      } 
    }
  }
  
  static class Node<K, V> extends Object implements Map.Entry<K, V> {
    final int hash;
    
    final K key;
    
    Node(int param1Int, K param1K, V param1V, Node<K, V> param1Node) {
      this.hash = param1Int;
      this.key = param1K;
      this.val = param1V;
      this.next = param1Node;
    }
    
    public final K getKey() { return (K)this.key; }
    
    public final V getValue() { return (V)this.val; }
    
    public final int hashCode() { return this.key.hashCode() ^ this.val.hashCode(); }
    
    public final String toString() { return this.key + "=" + this.val; }
    
    public final V setValue(V param1V) { throw new UnsupportedOperationException(); }
    
    public final boolean equals(Object param1Object) {
      Object object1;
      Object object2;
      Object object3;
      Map.Entry entry;
      return (param1Object instanceof Map.Entry && (object1 = (entry = (Map.Entry)param1Object).getKey()) != null && (object2 = entry.getValue()) != null && (object1 == this.key || object1.equals(this.key)) && (object2 == (object3 = this.val) || object2.equals(object3)));
    }
    
    Node<K, V> find(int param1Int, Object param1Object) {
      Node node = this;
      if (param1Object != null)
        do {
          Object object;
          if (node.hash == param1Int && ((object = node.key) == param1Object || (object != null && param1Object.equals(object))))
            return node; 
        } while ((node = node.next) != null); 
      return null;
    }
  }
  
  static final class ReduceEntriesTask<K, V> extends BulkTask<K, V, Map.Entry<K, V>> {
    final BiFunction<Map.Entry<K, V>, Map.Entry<K, V>, ? extends Map.Entry<K, V>> reducer;
    
    Map.Entry<K, V> result;
    
    ReduceEntriesTask<K, V> rights;
    
    ReduceEntriesTask<K, V> nextRight;
    
    ReduceEntriesTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, ReduceEntriesTask<K, V> param1ReduceEntriesTask, BiFunction<Map.Entry<K, V>, Map.Entry<K, V>, ? extends Map.Entry<K, V>> param1BiFunction) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.nextRight = param1ReduceEntriesTask;
      this.reducer = param1BiFunction;
    }
    
    public final Map.Entry<K, V> getRawResult() { return this.result; }
    
    public final void compute() {
      BiFunction biFunction;
      if ((biFunction = this.reducer) != null) {
        int i = this.baseIndex;
        int j;
        int k;
        while (this.batch > 0 && (k = (j = this.baseLimit) + i >>> 1) > i) {
          addToPendingCount(1);
          (this.rights = new ReduceEntriesTask(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, this.rights, biFunction)).fork();
        } 
        ConcurrentHashMap.Node node1;
        ConcurrentHashMap.Node node2;
        for (node1 = null; (node2 = advance()) != null; node1 = (node1 == null) ? node2 : (Map.Entry)biFunction.apply(node1, node2));
        this.result = node1;
        for (CountedCompleter countedCompleter = firstComplete(); countedCompleter != null; countedCompleter = countedCompleter.nextComplete()) {
          ReduceEntriesTask reduceEntriesTask1 = (ReduceEntriesTask)countedCompleter;
          for (ReduceEntriesTask reduceEntriesTask2 = reduceEntriesTask1.rights; reduceEntriesTask2 != null; reduceEntriesTask2 = reduceEntriesTask1.rights = reduceEntriesTask2.nextRight) {
            Map.Entry entry;
            if ((entry = reduceEntriesTask2.result) != null) {
              Map.Entry entry1;
              reduceEntriesTask1.result = ((entry1 = reduceEntriesTask1.result) == null) ? entry : (Map.Entry)biFunction.apply(entry1, entry);
            } 
          } 
        } 
      } 
    }
  }
  
  static final class ReduceKeysTask<K, V> extends BulkTask<K, V, K> {
    final BiFunction<? super K, ? super K, ? extends K> reducer;
    
    K result;
    
    ReduceKeysTask<K, V> rights;
    
    ReduceKeysTask<K, V> nextRight;
    
    ReduceKeysTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, ReduceKeysTask<K, V> param1ReduceKeysTask, BiFunction<? super K, ? super K, ? extends K> param1BiFunction) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.nextRight = param1ReduceKeysTask;
      this.reducer = param1BiFunction;
    }
    
    public final K getRawResult() { return (K)this.result; }
    
    public final void compute() {
      BiFunction biFunction;
      if ((biFunction = this.reducer) != null) {
        int i = this.baseIndex;
        int j;
        int k;
        while (this.batch > 0 && (k = (j = this.baseLimit) + i >>> 1) > i) {
          addToPendingCount(1);
          (this.rights = new ReduceKeysTask(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, this.rights, biFunction)).fork();
        } 
        Object object;
        ConcurrentHashMap.Node node;
        for (object = null; (node = advance()) != null; object = (object == null) ? object1 : ((object1 == null) ? object : biFunction.apply(object, object1)))
          Object object1 = node.key; 
        this.result = object;
        for (CountedCompleter countedCompleter = firstComplete(); countedCompleter != null; countedCompleter = countedCompleter.nextComplete()) {
          ReduceKeysTask reduceKeysTask1 = (ReduceKeysTask)countedCompleter;
          for (ReduceKeysTask reduceKeysTask2 = reduceKeysTask1.rights; reduceKeysTask2 != null; reduceKeysTask2 = reduceKeysTask1.rights = reduceKeysTask2.nextRight) {
            Object object1;
            if ((object1 = reduceKeysTask2.result) != null) {
              Object object2;
              reduceKeysTask1.result = ((object2 = reduceKeysTask1.result) == null) ? object1 : biFunction.apply(object2, object1);
            } 
          } 
        } 
      } 
    }
  }
  
  static final class ReduceValuesTask<K, V> extends BulkTask<K, V, V> {
    final BiFunction<? super V, ? super V, ? extends V> reducer;
    
    V result;
    
    ReduceValuesTask<K, V> rights;
    
    ReduceValuesTask<K, V> nextRight;
    
    ReduceValuesTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, ReduceValuesTask<K, V> param1ReduceValuesTask, BiFunction<? super V, ? super V, ? extends V> param1BiFunction) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.nextRight = param1ReduceValuesTask;
      this.reducer = param1BiFunction;
    }
    
    public final V getRawResult() { return (V)this.result; }
    
    public final void compute() {
      BiFunction biFunction;
      if ((biFunction = this.reducer) != null) {
        int i = this.baseIndex;
        int j;
        int k;
        while (this.batch > 0 && (k = (j = this.baseLimit) + i >>> 1) > i) {
          addToPendingCount(1);
          (this.rights = new ReduceValuesTask(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, this.rights, biFunction)).fork();
        } 
        Object object;
        ConcurrentHashMap.Node node;
        for (object = null; (node = advance()) != null; object = (object == null) ? object1 : biFunction.apply(object, object1))
          Object object1 = node.val; 
        this.result = object;
        for (CountedCompleter countedCompleter = firstComplete(); countedCompleter != null; countedCompleter = countedCompleter.nextComplete()) {
          ReduceValuesTask reduceValuesTask1 = (ReduceValuesTask)countedCompleter;
          for (ReduceValuesTask reduceValuesTask2 = reduceValuesTask1.rights; reduceValuesTask2 != null; reduceValuesTask2 = reduceValuesTask1.rights = reduceValuesTask2.nextRight) {
            Object object1;
            if ((object1 = reduceValuesTask2.result) != null) {
              Object object2;
              reduceValuesTask1.result = ((object2 = reduceValuesTask1.result) == null) ? object1 : biFunction.apply(object2, object1);
            } 
          } 
        } 
      } 
    }
  }
  
  static final class ReservationNode<K, V> extends Node<K, V> {
    ReservationNode() { super(-3, null, null, null); }
    
    ConcurrentHashMap.Node<K, V> find(int param1Int, Object param1Object) { return null; }
  }
  
  static final class SearchEntriesTask<K, V, U> extends BulkTask<K, V, U> {
    final Function<Map.Entry<K, V>, ? extends U> searchFunction;
    
    final AtomicReference<U> result;
    
    SearchEntriesTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, Function<Map.Entry<K, V>, ? extends U> param1Function, AtomicReference<U> param1AtomicReference) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.searchFunction = param1Function;
      this.result = param1AtomicReference;
    }
    
    public final U getRawResult() { return (U)this.result.get(); }
    
    public final void compute() {
      Function function;
      AtomicReference atomicReference;
      if ((function = this.searchFunction) != null && (atomicReference = this.result) != null) {
        int i = this.baseIndex;
        int j;
        int k;
        while (this.batch > 0 && (k = (j = this.baseLimit) + i >>> 1) > i) {
          if (atomicReference.get() != null)
            return; 
          addToPendingCount(1);
          (new SearchEntriesTask(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, function, atomicReference)).fork();
        } 
        while (atomicReference.get() == null) {
          ConcurrentHashMap.Node node;
          if ((node = advance()) == null) {
            propagateCompletion();
            break;
          } 
          Object object;
          if ((object = function.apply(node)) != null) {
            if (atomicReference.compareAndSet(null, object))
              quietlyCompleteRoot(); 
            return;
          } 
        } 
      } 
    }
  }
  
  static final class SearchKeysTask<K, V, U> extends BulkTask<K, V, U> {
    final Function<? super K, ? extends U> searchFunction;
    
    final AtomicReference<U> result;
    
    SearchKeysTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, Function<? super K, ? extends U> param1Function, AtomicReference<U> param1AtomicReference) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.searchFunction = param1Function;
      this.result = param1AtomicReference;
    }
    
    public final U getRawResult() { return (U)this.result.get(); }
    
    public final void compute() {
      Function function;
      AtomicReference atomicReference;
      if ((function = this.searchFunction) != null && (atomicReference = this.result) != null) {
        int i = this.baseIndex;
        int j;
        int k;
        while (this.batch > 0 && (k = (j = this.baseLimit) + i >>> 1) > i) {
          if (atomicReference.get() != null)
            return; 
          addToPendingCount(1);
          (new SearchKeysTask(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, function, atomicReference)).fork();
        } 
        while (atomicReference.get() == null) {
          ConcurrentHashMap.Node node;
          if ((node = advance()) == null) {
            propagateCompletion();
            break;
          } 
          Object object;
          if ((object = function.apply(node.key)) != null) {
            if (atomicReference.compareAndSet(null, object))
              quietlyCompleteRoot(); 
            break;
          } 
        } 
      } 
    }
  }
  
  static final class SearchMappingsTask<K, V, U> extends BulkTask<K, V, U> {
    final BiFunction<? super K, ? super V, ? extends U> searchFunction;
    
    final AtomicReference<U> result;
    
    SearchMappingsTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, BiFunction<? super K, ? super V, ? extends U> param1BiFunction, AtomicReference<U> param1AtomicReference) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.searchFunction = param1BiFunction;
      this.result = param1AtomicReference;
    }
    
    public final U getRawResult() { return (U)this.result.get(); }
    
    public final void compute() {
      BiFunction biFunction;
      AtomicReference atomicReference;
      if ((biFunction = this.searchFunction) != null && (atomicReference = this.result) != null) {
        int i = this.baseIndex;
        int j;
        int k;
        while (this.batch > 0 && (k = (j = this.baseLimit) + i >>> 1) > i) {
          if (atomicReference.get() != null)
            return; 
          addToPendingCount(1);
          (new SearchMappingsTask(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, biFunction, atomicReference)).fork();
        } 
        while (atomicReference.get() == null) {
          ConcurrentHashMap.Node node;
          if ((node = advance()) == null) {
            propagateCompletion();
            break;
          } 
          Object object;
          if ((object = biFunction.apply(node.key, node.val)) != null) {
            if (atomicReference.compareAndSet(null, object))
              quietlyCompleteRoot(); 
            break;
          } 
        } 
      } 
    }
  }
  
  static final class SearchValuesTask<K, V, U> extends BulkTask<K, V, U> {
    final Function<? super V, ? extends U> searchFunction;
    
    final AtomicReference<U> result;
    
    SearchValuesTask(ConcurrentHashMap.BulkTask<K, V, ?> param1BulkTask, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, Function<? super V, ? extends U> param1Function, AtomicReference<U> param1AtomicReference) {
      super(param1BulkTask, param1Int1, param1Int2, param1Int3, param1ArrayOfNode);
      this.searchFunction = param1Function;
      this.result = param1AtomicReference;
    }
    
    public final U getRawResult() { return (U)this.result.get(); }
    
    public final void compute() {
      Function function;
      AtomicReference atomicReference;
      if ((function = this.searchFunction) != null && (atomicReference = this.result) != null) {
        int i = this.baseIndex;
        int j;
        int k;
        while (this.batch > 0 && (k = (j = this.baseLimit) + i >>> 1) > i) {
          if (atomicReference.get() != null)
            return; 
          addToPendingCount(1);
          (new SearchValuesTask(this, this.batch >>>= 1, this.baseLimit = k, j, this.tab, function, atomicReference)).fork();
        } 
        while (atomicReference.get() == null) {
          ConcurrentHashMap.Node node;
          if ((node = advance()) == null) {
            propagateCompletion();
            break;
          } 
          Object object;
          if ((object = function.apply(node.val)) != null) {
            if (atomicReference.compareAndSet(null, object))
              quietlyCompleteRoot(); 
            break;
          } 
        } 
      } 
    }
  }
  
  static class Segment<K, V> extends ReentrantLock implements Serializable {
    private static final long serialVersionUID = 2249069246763182397L;
    
    final float loadFactor;
    
    Segment(float param1Float) { this.loadFactor = param1Float; }
  }
  
  static final class TableStack<K, V> extends Object {
    int length;
    
    int index;
    
    ConcurrentHashMap.Node<K, V>[] tab;
    
    TableStack<K, V> next;
  }
  
  static class Traverser<K, V> extends Object {
    ConcurrentHashMap.Node<K, V>[] tab;
    
    ConcurrentHashMap.Node<K, V> next;
    
    ConcurrentHashMap.TableStack<K, V> stack;
    
    ConcurrentHashMap.TableStack<K, V> spare;
    
    int index;
    
    int baseIndex;
    
    int baseLimit;
    
    final int baseSize;
    
    Traverser(ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, int param1Int1, int param1Int2, int param1Int3) {
      this.tab = param1ArrayOfNode;
      this.baseSize = param1Int1;
      this.baseIndex = this.index = param1Int2;
      this.baseLimit = param1Int3;
      this.next = null;
    }
    
    final ConcurrentHashMap.Node<K, V> advance() {
      ConcurrentHashMap.Node node;
      if ((node = this.next) != null)
        node = node.next; 
      while (true) {
        if (node != null)
          return this.next = node; 
        ConcurrentHashMap.Node[] arrayOfNode;
        int i;
        int j;
        if (this.baseIndex >= this.baseLimit || (arrayOfNode = this.tab) == null || (j = arrayOfNode.length) <= (i = this.index) || i < 0)
          return this.next = null; 
        if ((node = ConcurrentHashMap.tabAt(arrayOfNode, i)) != null && node.hash < 0) {
          if (node instanceof ConcurrentHashMap.ForwardingNode) {
            this.tab = ((ConcurrentHashMap.ForwardingNode)node).nextTable;
            node = null;
            pushState(arrayOfNode, i, j);
            continue;
          } 
          if (node instanceof ConcurrentHashMap.TreeBin) {
            node = ((ConcurrentHashMap.TreeBin)node).first;
          } else {
            node = null;
          } 
        } 
        if (this.stack != null) {
          recoverState(j);
          continue;
        } 
        if ((this.index = i + this.baseSize) >= j)
          this.index = ++this.baseIndex; 
      } 
    }
    
    private void pushState(ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, int param1Int1, int param1Int2) {
      ConcurrentHashMap.TableStack tableStack = this.spare;
      if (tableStack != null) {
        this.spare = tableStack.next;
      } else {
        tableStack = new ConcurrentHashMap.TableStack();
      } 
      tableStack.tab = param1ArrayOfNode;
      tableStack.length = param1Int2;
      tableStack.index = param1Int1;
      tableStack.next = this.stack;
      this.stack = tableStack;
    }
    
    private void recoverState(int param1Int) {
      ConcurrentHashMap.TableStack tableStack;
      int i;
      while ((tableStack = this.stack) != null && this.index += (i = tableStack.length) >= param1Int) {
        param1Int = i;
        this.index = tableStack.index;
        this.tab = tableStack.tab;
        tableStack.tab = null;
        ConcurrentHashMap.TableStack tableStack1 = tableStack.next;
        tableStack.next = this.spare;
        this.stack = tableStack1;
        this.spare = tableStack;
      } 
      if (tableStack == null && this.index += this.baseSize >= param1Int)
        this.index = ++this.baseIndex; 
    }
  }
  
  static final class TreeBin<K, V> extends Node<K, V> {
    ConcurrentHashMap.TreeNode<K, V> root;
    
    static final int WRITER = 1;
    
    static final int WAITER = 2;
    
    static final int READER = 4;
    
    private static final Unsafe U;
    
    private static final long LOCKSTATE;
    
    static int tieBreakOrder(Object param1Object1, Object param1Object2) {
      int i;
      if (param1Object1 == null || param1Object2 == null || (i = param1Object1.getClass().getName().compareTo(param1Object2.getClass().getName())) == 0)
        i = (System.identityHashCode(param1Object1) <= System.identityHashCode(param1Object2)) ? -1 : 1; 
      return i;
    }
    
    TreeBin(ConcurrentHashMap.TreeNode<K, V> param1TreeNode) {
      super(-2, null, null, null);
      this.first = param1TreeNode;
      ConcurrentHashMap.TreeNode<K, V> treeNode1 = null;
      for (ConcurrentHashMap.TreeNode<K, V> treeNode2 = param1TreeNode; treeNode2 != null; treeNode2 = treeNode) {
        ConcurrentHashMap.TreeNode treeNode = (ConcurrentHashMap.TreeNode)treeNode2.next;
        treeNode2.left = treeNode2.right = null;
        if (treeNode1 == null) {
          treeNode2.parent = null;
          treeNode2.red = false;
          treeNode1 = treeNode2;
        } else {
          ConcurrentHashMap.TreeNode<K, V> treeNode4;
          int j;
          Object object = treeNode2.key;
          int i = treeNode2.hash;
          Class clazz = null;
          ConcurrentHashMap.TreeNode<K, V> treeNode3 = treeNode1;
          do {
            Object object1 = treeNode3.key;
            int k;
            if ((k = treeNode3.hash) > i) {
              j = -1;
            } else if (k < i) {
              j = 1;
            } else if ((clazz == null && (clazz = ConcurrentHashMap.comparableClassFor(object)) == null) || (j = ConcurrentHashMap.compareComparables(clazz, object, object1)) == 0) {
              j = tieBreakOrder(object, object1);
            } 
            treeNode4 = treeNode3;
          } while ((treeNode3 = (j <= 0) ? treeNode3.left : treeNode3.right) != null);
          treeNode2.parent = treeNode4;
          if (j <= 0) {
            treeNode4.left = treeNode2;
          } else {
            treeNode4.right = treeNode2;
          } 
          treeNode1 = balanceInsertion(treeNode1, treeNode2);
        } 
      } 
      this.root = treeNode1;
      assert checkInvariants(this.root);
    }
    
    private final void lockRoot() {
      if (!U.compareAndSwapInt(this, LOCKSTATE, 0, 1))
        contendedLock(); 
    }
    
    private final void unlockRoot() { this.lockState = 0; }
    
    private final void contendedLock() {
      boolean bool = false;
      while (true) {
        int i;
        while (((i = this.lockState) & 0xFFFFFFFD) == 0) {
          if (U.compareAndSwapInt(this, LOCKSTATE, i, 1)) {
            if (bool)
              this.waiter = null; 
            return;
          } 
        } 
        if ((i & 0x2) == 0) {
          if (U.compareAndSwapInt(this, LOCKSTATE, i, i | 0x2)) {
            bool = true;
            this.waiter = Thread.currentThread();
          } 
          continue;
        } 
        if (bool)
          LockSupport.park(this); 
      } 
    }
    
    final ConcurrentHashMap.Node<K, V> find(int param1Int, Object param1Object) {
      if (param1Object != null) {
        ConcurrentHashMap.TreeNode treeNode = this.first;
        while (treeNode != null) {
          int i;
          if (((i = this.lockState) & 0x3) != 0) {
            Object object;
            if (treeNode.hash == param1Int && ((object = treeNode.key) == param1Object || (object != null && param1Object.equals(object))))
              return treeNode; 
            ConcurrentHashMap.Node node = treeNode.next;
            continue;
          } 
          if (U.compareAndSwapInt(this, LOCKSTATE, i, i + 4)) {
            ConcurrentHashMap.TreeNode treeNode1;
            try {
              ConcurrentHashMap.TreeNode treeNode2;
              treeNode1 = ((treeNode2 = this.root) == null) ? null : treeNode2.findTreeNode(param1Int, param1Object, null);
            } finally {
              Thread thread;
              if (U.getAndAddInt(this, LOCKSTATE, -4) == 6 && (thread = this.waiter) != null)
                LockSupport.unpark(thread); 
            } 
            return treeNode1;
          } 
        } 
      } 
      return null;
    }
    
    final ConcurrentHashMap.TreeNode<K, V> putTreeVal(int param1Int, K param1K, V param1V) {
      Class clazz = null;
      boolean bool = false;
      ConcurrentHashMap.TreeNode treeNode = this.root;
      while (true) {
        int i;
        if (treeNode == null) {
          this.first = this.root = new ConcurrentHashMap.TreeNode(param1Int, param1K, param1V, null, null);
          break;
        } 
        int j;
        if ((j = treeNode.hash) > param1Int) {
          i = -1;
        } else if (j < param1Int) {
          i = 1;
        } else {
          Object object;
          if ((object = treeNode.key) == param1K || (object != null && param1K.equals(object)))
            return treeNode; 
          if ((clazz == null && (clazz = ConcurrentHashMap.comparableClassFor(param1K)) == null) || (i = ConcurrentHashMap.compareComparables(clazz, param1K, object)) == 0) {
            bool = true;
            ConcurrentHashMap.TreeNode treeNode2;
            ConcurrentHashMap.TreeNode treeNode3;
            if (!bool && (((treeNode3 = treeNode.left) != null && (treeNode2 = treeNode3.findTreeNode(param1Int, param1K, clazz)) != null) || ((treeNode3 = treeNode.right) != null && (treeNode2 = treeNode3.findTreeNode(param1Int, param1K, clazz)) != null)))
              return treeNode2; 
            i = tieBreakOrder(param1K, object);
          } 
        } 
        ConcurrentHashMap.TreeNode treeNode1 = treeNode;
        if ((treeNode = (i <= 0) ? treeNode.left : treeNode.right) == null) {
          ConcurrentHashMap.TreeNode treeNode3 = this.first;
          ConcurrentHashMap.TreeNode treeNode2 = new ConcurrentHashMap.TreeNode(param1Int, param1K, param1V, treeNode3, treeNode1);
          this.first = treeNode2;
          if (treeNode3 != null)
            treeNode3.prev = treeNode2; 
          if (i <= 0) {
            treeNode1.left = treeNode2;
          } else {
            treeNode1.right = treeNode2;
          } 
          if (!treeNode1.red) {
            treeNode2.red = true;
            break;
          } 
          lockRoot();
          try {
            this.root = balanceInsertion(this.root, treeNode2);
            unlockRoot();
          } finally {
            unlockRoot();
          } 
        } 
      } 
      assert checkInvariants(this.root);
      return null;
    }
    
    final boolean removeTreeNode(ConcurrentHashMap.TreeNode<K, V> param1TreeNode) {
      ConcurrentHashMap.TreeNode treeNode1 = (ConcurrentHashMap.TreeNode)param1TreeNode.next;
      ConcurrentHashMap.TreeNode treeNode2 = param1TreeNode.prev;
      if (treeNode2 == null) {
        this.first = treeNode1;
      } else {
        treeNode2.next = treeNode1;
      } 
      if (treeNode1 != null)
        treeNode1.prev = treeNode2; 
      if (this.first == null) {
        this.root = null;
        return true;
      } 
      ConcurrentHashMap.TreeNode treeNode3;
      ConcurrentHashMap.TreeNode treeNode4;
      if ((treeNode3 = this.root) == null || treeNode3.right == null || (treeNode4 = treeNode3.left) == null || treeNode4.left == null)
        return true; 
      lockRoot();
      try {
        ConcurrentHashMap.TreeNode<K, V> treeNode5;
        ConcurrentHashMap.TreeNode treeNode6 = param1TreeNode.left;
        ConcurrentHashMap.TreeNode treeNode7 = param1TreeNode.right;
        if (treeNode6 != null && treeNode7 != null) {
          ConcurrentHashMap.TreeNode treeNode9;
          ConcurrentHashMap.TreeNode treeNode10;
          for (treeNode9 = treeNode7; (treeNode10 = treeNode9.left) != null; treeNode9 = treeNode10);
          boolean bool = treeNode9.red;
          treeNode9.red = param1TreeNode.red;
          param1TreeNode.red = bool;
          ConcurrentHashMap.TreeNode treeNode11 = treeNode9.right;
          ConcurrentHashMap.TreeNode treeNode12 = param1TreeNode.parent;
          if (treeNode9 == treeNode7) {
            param1TreeNode.parent = treeNode9;
            treeNode9.right = param1TreeNode;
          } else {
            ConcurrentHashMap.TreeNode treeNode = treeNode9.parent;
            if ((param1TreeNode.parent = treeNode) != null)
              if (treeNode9 == treeNode.left) {
                treeNode.left = param1TreeNode;
              } else {
                treeNode.right = param1TreeNode;
              }  
            if ((treeNode9.right = treeNode7) != null)
              treeNode7.parent = treeNode9; 
          } 
          param1TreeNode.left = null;
          if ((param1TreeNode.right = treeNode11) != null)
            treeNode11.parent = param1TreeNode; 
          if ((treeNode9.left = treeNode6) != null)
            treeNode6.parent = treeNode9; 
          if ((treeNode9.parent = treeNode12) == null) {
            treeNode3 = treeNode9;
          } else if (param1TreeNode == treeNode12.left) {
            treeNode12.left = treeNode9;
          } else {
            treeNode12.right = treeNode9;
          } 
          if (treeNode11 != null) {
            treeNode5 = treeNode11;
          } else {
            treeNode5 = param1TreeNode;
          } 
        } else if (treeNode6 != null) {
          treeNode5 = treeNode6;
        } else if (treeNode7 != null) {
          treeNode5 = treeNode7;
        } else {
          treeNode5 = param1TreeNode;
        } 
        if (treeNode5 != param1TreeNode) {
          ConcurrentHashMap.TreeNode treeNode = treeNode5.parent = param1TreeNode.parent;
          if (treeNode == null) {
            treeNode3 = treeNode5;
          } else if (param1TreeNode == treeNode.left) {
            treeNode.left = treeNode5;
          } else {
            treeNode.right = treeNode5;
          } 
          param1TreeNode.left = param1TreeNode.right = param1TreeNode.parent = null;
        } 
        this.root = param1TreeNode.red ? treeNode3 : balanceDeletion(treeNode3, treeNode5);
        ConcurrentHashMap.TreeNode treeNode8;
        if (param1TreeNode == treeNode5 && (treeNode8 = param1TreeNode.parent) != null) {
          if (param1TreeNode == treeNode8.left) {
            treeNode8.left = null;
          } else if (param1TreeNode == treeNode8.right) {
            treeNode8.right = null;
          } 
          param1TreeNode.parent = null;
        } 
      } finally {
        unlockRoot();
      } 
      assert checkInvariants(this.root);
      return false;
    }
    
    static <K, V> ConcurrentHashMap.TreeNode<K, V> rotateLeft(ConcurrentHashMap.TreeNode<K, V> param1TreeNode1, ConcurrentHashMap.TreeNode<K, V> param1TreeNode2) {
      ConcurrentHashMap.TreeNode treeNode;
      if (param1TreeNode2 != null && (treeNode = param1TreeNode2.right) != null) {
        ConcurrentHashMap.TreeNode treeNode2;
        if ((treeNode2 = param1TreeNode2.right = treeNode.left) != null)
          treeNode2.parent = param1TreeNode2; 
        ConcurrentHashMap.TreeNode treeNode1;
        if ((treeNode1 = treeNode.parent = param1TreeNode2.parent) == null) {
          (param1TreeNode1 = treeNode).red = false;
        } else if (treeNode1.left == param1TreeNode2) {
          treeNode1.left = treeNode;
        } else {
          treeNode1.right = treeNode;
        } 
        treeNode.left = param1TreeNode2;
        param1TreeNode2.parent = treeNode;
      } 
      return param1TreeNode1;
    }
    
    static <K, V> ConcurrentHashMap.TreeNode<K, V> rotateRight(ConcurrentHashMap.TreeNode<K, V> param1TreeNode1, ConcurrentHashMap.TreeNode<K, V> param1TreeNode2) {
      ConcurrentHashMap.TreeNode treeNode;
      if (param1TreeNode2 != null && (treeNode = param1TreeNode2.left) != null) {
        ConcurrentHashMap.TreeNode treeNode2;
        if ((treeNode2 = param1TreeNode2.left = treeNode.right) != null)
          treeNode2.parent = param1TreeNode2; 
        ConcurrentHashMap.TreeNode treeNode1;
        if ((treeNode1 = treeNode.parent = param1TreeNode2.parent) == null) {
          (param1TreeNode1 = treeNode).red = false;
        } else if (treeNode1.right == param1TreeNode2) {
          treeNode1.right = treeNode;
        } else {
          treeNode1.left = treeNode;
        } 
        treeNode.right = param1TreeNode2;
        param1TreeNode2.parent = treeNode;
      } 
      return param1TreeNode1;
    }
    
    static <K, V> ConcurrentHashMap.TreeNode<K, V> balanceInsertion(ConcurrentHashMap.TreeNode<K, V> param1TreeNode1, ConcurrentHashMap.TreeNode<K, V> param1TreeNode2) {
      param1TreeNode2.red = true;
      while (true) {
        ConcurrentHashMap.TreeNode treeNode1;
        if ((treeNode1 = param1TreeNode2.parent) == null) {
          param1TreeNode2.red = false;
          return param1TreeNode2;
        } 
        ConcurrentHashMap.TreeNode treeNode2;
        if (!treeNode1.red || (treeNode2 = treeNode1.parent) == null)
          return param1TreeNode1; 
        ConcurrentHashMap.TreeNode treeNode3;
        if (treeNode1 == (treeNode3 = treeNode2.left)) {
          ConcurrentHashMap.TreeNode treeNode;
          if ((treeNode = treeNode2.right) != null && treeNode.red) {
            treeNode.red = false;
            treeNode1.red = false;
            treeNode2.red = true;
            param1TreeNode2 = treeNode2;
            continue;
          } 
          if (param1TreeNode2 == treeNode1.right) {
            param1TreeNode1 = rotateLeft(param1TreeNode1, param1TreeNode2 = treeNode1);
            treeNode2 = ((treeNode1 = param1TreeNode2.parent) == null) ? null : treeNode1.parent;
          } 
          if (treeNode1 != null) {
            treeNode1.red = false;
            if (treeNode2 != null) {
              treeNode2.red = true;
              param1TreeNode1 = rotateRight(param1TreeNode1, treeNode2);
            } 
          } 
          continue;
        } 
        if (treeNode3 != null && treeNode3.red) {
          treeNode3.red = false;
          treeNode1.red = false;
          treeNode2.red = true;
          param1TreeNode2 = treeNode2;
          continue;
        } 
        if (param1TreeNode2 == treeNode1.left) {
          param1TreeNode1 = rotateRight(param1TreeNode1, param1TreeNode2 = treeNode1);
          treeNode2 = ((treeNode1 = param1TreeNode2.parent) == null) ? null : treeNode1.parent;
        } 
        if (treeNode1 != null) {
          treeNode1.red = false;
          if (treeNode2 != null) {
            treeNode2.red = true;
            param1TreeNode1 = rotateLeft(param1TreeNode1, treeNode2);
          } 
        } 
      } 
    }
    
    static <K, V> ConcurrentHashMap.TreeNode<K, V> balanceDeletion(ConcurrentHashMap.TreeNode<K, V> param1TreeNode1, ConcurrentHashMap.TreeNode<K, V> param1TreeNode2) {
      while (true) {
        if (param1TreeNode2 == null || param1TreeNode2 == param1TreeNode1)
          return param1TreeNode1; 
        ConcurrentHashMap.TreeNode treeNode1;
        if ((treeNode1 = param1TreeNode2.parent) == null) {
          param1TreeNode2.red = false;
          return param1TreeNode2;
        } 
        if (param1TreeNode2.red) {
          param1TreeNode2.red = false;
          return param1TreeNode1;
        } 
        ConcurrentHashMap.TreeNode treeNode2;
        if ((treeNode2 = treeNode1.left) == param1TreeNode2) {
          ConcurrentHashMap.TreeNode treeNode5;
          if ((treeNode5 = treeNode1.right) != null && treeNode5.red) {
            treeNode5.red = false;
            treeNode1.red = true;
            param1TreeNode1 = rotateLeft(param1TreeNode1, treeNode1);
            treeNode5 = ((treeNode1 = param1TreeNode2.parent) == null) ? null : treeNode1.right;
          } 
          if (treeNode5 == null) {
            param1TreeNode2 = treeNode1;
            continue;
          } 
          ConcurrentHashMap.TreeNode treeNode6 = treeNode5.left;
          ConcurrentHashMap.TreeNode treeNode7 = treeNode5.right;
          if ((treeNode7 == null || !treeNode7.red) && (treeNode6 == null || !treeNode6.red)) {
            treeNode5.red = true;
            param1TreeNode2 = treeNode1;
            continue;
          } 
          if (treeNode7 == null || !treeNode7.red) {
            if (treeNode6 != null)
              treeNode6.red = false; 
            treeNode5.red = true;
            param1TreeNode1 = rotateRight(param1TreeNode1, treeNode5);
            treeNode5 = ((treeNode1 = param1TreeNode2.parent) == null) ? null : treeNode1.right;
          } 
          if (treeNode5 != null) {
            treeNode5.red = (treeNode1 == null) ? false : treeNode1.red;
            if ((treeNode7 = treeNode5.right) != null)
              treeNode7.red = false; 
          } 
          if (treeNode1 != null) {
            treeNode1.red = false;
            param1TreeNode1 = rotateLeft(param1TreeNode1, treeNode1);
          } 
          param1TreeNode2 = param1TreeNode1;
          continue;
        } 
        if (treeNode2 != null && treeNode2.red) {
          treeNode2.red = false;
          treeNode1.red = true;
          param1TreeNode1 = rotateRight(param1TreeNode1, treeNode1);
          treeNode2 = ((treeNode1 = param1TreeNode2.parent) == null) ? null : treeNode1.left;
        } 
        if (treeNode2 == null) {
          param1TreeNode2 = treeNode1;
          continue;
        } 
        ConcurrentHashMap.TreeNode treeNode3 = treeNode2.left;
        ConcurrentHashMap.TreeNode treeNode4 = treeNode2.right;
        if ((treeNode3 == null || !treeNode3.red) && (treeNode4 == null || !treeNode4.red)) {
          treeNode2.red = true;
          param1TreeNode2 = treeNode1;
          continue;
        } 
        if (treeNode3 == null || !treeNode3.red) {
          if (treeNode4 != null)
            treeNode4.red = false; 
          treeNode2.red = true;
          param1TreeNode1 = rotateLeft(param1TreeNode1, treeNode2);
          treeNode2 = ((treeNode1 = param1TreeNode2.parent) == null) ? null : treeNode1.left;
        } 
        if (treeNode2 != null) {
          treeNode2.red = (treeNode1 == null) ? false : treeNode1.red;
          if ((treeNode3 = treeNode2.left) != null)
            treeNode3.red = false; 
        } 
        if (treeNode1 != null) {
          treeNode1.red = false;
          param1TreeNode1 = rotateRight(param1TreeNode1, treeNode1);
        } 
        param1TreeNode2 = param1TreeNode1;
      } 
    }
    
    static <K, V> boolean checkInvariants(ConcurrentHashMap.TreeNode<K, V> param1TreeNode) {
      ConcurrentHashMap.TreeNode treeNode1 = param1TreeNode.parent;
      ConcurrentHashMap.TreeNode treeNode2 = param1TreeNode.left;
      ConcurrentHashMap.TreeNode treeNode3 = param1TreeNode.right;
      ConcurrentHashMap.TreeNode treeNode4 = param1TreeNode.prev;
      ConcurrentHashMap.TreeNode treeNode5 = (ConcurrentHashMap.TreeNode)param1TreeNode.next;
      return (treeNode4 != null && treeNode4.next != param1TreeNode) ? false : ((treeNode5 != null && treeNode5.prev != param1TreeNode) ? false : ((treeNode1 != null && param1TreeNode != treeNode1.left && param1TreeNode != treeNode1.right) ? false : ((treeNode2 != null && (treeNode2.parent != param1TreeNode || treeNode2.hash > param1TreeNode.hash)) ? false : ((treeNode3 != null && (treeNode3.parent != param1TreeNode || treeNode3.hash < param1TreeNode.hash)) ? false : ((param1TreeNode.red && treeNode2 != null && treeNode2.red && treeNode3 != null && treeNode3.red) ? false : ((treeNode2 != null && !checkInvariants(treeNode2)) ? false : (!(treeNode3 != null && !checkInvariants(treeNode3)))))))));
    }
    
    static  {
      try {
        U = Unsafe.getUnsafe();
        Class clazz = TreeBin.class;
        LOCKSTATE = U.objectFieldOffset(clazz.getDeclaredField("lockState"));
      } catch (Exception exception) {
        throw new Error(exception);
      } 
    }
  }
  
  static final class TreeNode<K, V> extends Node<K, V> {
    TreeNode<K, V> parent;
    
    TreeNode<K, V> left;
    
    TreeNode<K, V> right;
    
    TreeNode<K, V> prev;
    
    boolean red;
    
    TreeNode(int param1Int, K param1K, V param1V, ConcurrentHashMap.Node<K, V> param1Node, TreeNode<K, V> param1TreeNode) {
      super(param1Int, param1K, param1V, param1Node);
      this.parent = param1TreeNode;
    }
    
    ConcurrentHashMap.Node<K, V> find(int param1Int, Object param1Object) { return findTreeNode(param1Int, param1Object, null); }
    
    final TreeNode<K, V> findTreeNode(int param1Int, Object param1Object, Class<?> param1Class) {
      if (param1Object != null) {
        TreeNode treeNode = this;
        do {
          TreeNode treeNode1 = treeNode.left;
          TreeNode treeNode2 = treeNode.right;
          int i;
          if ((i = treeNode.hash) > param1Int) {
            treeNode = treeNode1;
          } else if (i < param1Int) {
            treeNode = treeNode2;
          } else {
            Object object;
            if ((object = treeNode.key) == param1Object || (object != null && param1Object.equals(object)))
              return treeNode; 
            if (treeNode1 == null) {
              treeNode = treeNode2;
            } else if (treeNode2 == null) {
              treeNode = treeNode1;
            } else {
              int j;
              if ((param1Class != null || (param1Class = ConcurrentHashMap.comparableClassFor(param1Object)) != null) && (j = ConcurrentHashMap.compareComparables(param1Class, param1Object, object)) != 0) {
                treeNode = (j < 0) ? treeNode1 : treeNode2;
              } else {
                TreeNode treeNode3;
                if ((treeNode3 = treeNode2.findTreeNode(param1Int, param1Object, param1Class)) != null)
                  return treeNode3; 
                treeNode = treeNode1;
              } 
            } 
          } 
        } while (treeNode != null);
      } 
      return null;
    }
  }
  
  static final class ValueIterator<K, V> extends BaseIterator<K, V> implements Iterator<V>, Enumeration<V> {
    ValueIterator(ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, int param1Int1, int param1Int2, int param1Int3, ConcurrentHashMap<K, V> param1ConcurrentHashMap) { super(param1ArrayOfNode, param1Int1, param1Int2, param1Int3, param1ConcurrentHashMap); }
    
    public final V next() {
      ConcurrentHashMap.Node node;
      if ((node = this.next) == null)
        throw new NoSuchElementException(); 
      Object object = node.val;
      this.lastReturned = node;
      advance();
      return (V)object;
    }
    
    public final V nextElement() { return (V)next(); }
  }
  
  static final class ValueSpliterator<K, V> extends Traverser<K, V> implements Spliterator<V> {
    long est;
    
    ValueSpliterator(ConcurrentHashMap.Node<K, V>[] param1ArrayOfNode, int param1Int1, int param1Int2, int param1Int3, long param1Long) {
      super(param1ArrayOfNode, param1Int1, param1Int2, param1Int3);
      this.est = param1Long;
    }
    
    public Spliterator<V> trySplit() {
      int i;
      int j;
      int k;
      return ((k = (i = this.baseIndex) + (j = this.baseLimit) >>> 1) <= i) ? null : new ValueSpliterator(this.tab, this.baseSize, this.baseLimit = k, j, this.est >>>= true);
    }
    
    public void forEachRemaining(Consumer<? super V> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      ConcurrentHashMap.Node node;
      while ((node = advance()) != null)
        param1Consumer.accept(node.val); 
    }
    
    public boolean tryAdvance(Consumer<? super V> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      ConcurrentHashMap.Node node;
      if ((node = advance()) == null)
        return false; 
      param1Consumer.accept(node.val);
      return true;
    }
    
    public long estimateSize() { return this.est; }
    
    public int characteristics() { return 4352; }
  }
  
  static final class ValuesView<K, V> extends CollectionView<K, V, V> implements Collection<V>, Serializable {
    private static final long serialVersionUID = 2249069246763182397L;
    
    ValuesView(ConcurrentHashMap<K, V> param1ConcurrentHashMap) { super(param1ConcurrentHashMap); }
    
    public final boolean contains(Object param1Object) { return this.map.containsValue(param1Object); }
    
    public final boolean remove(Object param1Object) {
      if (param1Object != null) {
        Iterator iterator = iterator();
        while (iterator.hasNext()) {
          if (param1Object.equals(iterator.next())) {
            iterator.remove();
            return true;
          } 
        } 
      } 
      return false;
    }
    
    public final Iterator<V> iterator() {
      ConcurrentHashMap concurrentHashMap = this.map;
      ConcurrentHashMap.Node[] arrayOfNode;
      byte b = ((arrayOfNode = concurrentHashMap.table) == null) ? 0 : arrayOfNode.length;
      return new ConcurrentHashMap.ValueIterator(arrayOfNode, b, 0, b, concurrentHashMap);
    }
    
    public final boolean add(V param1V) { throw new UnsupportedOperationException(); }
    
    public final boolean addAll(Collection<? extends V> param1Collection) { throw new UnsupportedOperationException(); }
    
    public Spliterator<V> spliterator() {
      ConcurrentHashMap concurrentHashMap = this.map;
      long l = concurrentHashMap.sumCount();
      ConcurrentHashMap.Node[] arrayOfNode;
      byte b = ((arrayOfNode = concurrentHashMap.table) == null) ? 0 : arrayOfNode.length;
      return new ConcurrentHashMap.ValueSpliterator(arrayOfNode, b, 0, b, (l < 0L) ? 0L : l);
    }
    
    public void forEach(Consumer<? super V> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      ConcurrentHashMap.Node[] arrayOfNode;
      if ((arrayOfNode = this.map.table) != null) {
        ConcurrentHashMap.Traverser traverser = new ConcurrentHashMap.Traverser(arrayOfNode, arrayOfNode.length, 0, arrayOfNode.length);
        ConcurrentHashMap.Node node;
        while ((node = traverser.advance()) != null)
          param1Consumer.accept(node.val); 
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\ConcurrentHashMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */