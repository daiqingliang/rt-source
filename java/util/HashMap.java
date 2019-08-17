package java.util;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import sun.misc.SharedSecrets;

public class HashMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Cloneable, Serializable {
  private static final long serialVersionUID = 362498820763181265L;
  
  static final int DEFAULT_INITIAL_CAPACITY = 16;
  
  static final int MAXIMUM_CAPACITY = 1073741824;
  
  static final float DEFAULT_LOAD_FACTOR = 0.75F;
  
  static final int TREEIFY_THRESHOLD = 8;
  
  static final int UNTREEIFY_THRESHOLD = 6;
  
  static final int MIN_TREEIFY_CAPACITY = 64;
  
  Node<K, V>[] table;
  
  Set<Map.Entry<K, V>> entrySet;
  
  int size;
  
  int modCount;
  
  int threshold;
  
  final float loadFactor;
  
  static final int hash(Object paramObject) {
    int i;
    return (paramObject == null) ? 0 : ((i = paramObject.hashCode()) ^ i >>> 16);
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
  
  static final int tableSizeFor(int paramInt) {
    int i = paramInt - 1;
    i |= i >>> 1;
    i |= i >>> 2;
    i |= i >>> 4;
    i |= i >>> 8;
    i |= i >>> 16;
    return (i < 0) ? 1 : ((i >= 1073741824) ? 1073741824 : (i + 1));
  }
  
  public HashMap(int paramInt, float paramFloat) {
    if (paramInt < 0)
      throw new IllegalArgumentException("Illegal initial capacity: " + paramInt); 
    if (paramInt > 1073741824)
      paramInt = 1073741824; 
    if (paramFloat <= 0.0F || Float.isNaN(paramFloat))
      throw new IllegalArgumentException("Illegal load factor: " + paramFloat); 
    this.loadFactor = paramFloat;
    this.threshold = tableSizeFor(paramInt);
  }
  
  public HashMap(int paramInt) { this(paramInt, 0.75F); }
  
  public HashMap() { this.loadFactor = 0.75F; }
  
  public HashMap(Map<? extends K, ? extends V> paramMap) {
    this.loadFactor = 0.75F;
    putMapEntries(paramMap, false);
  }
  
  final void putMapEntries(Map<? extends K, ? extends V> paramMap, boolean paramBoolean) {
    int i = paramMap.size();
    if (i > 0) {
      if (this.table == null) {
        float f = i / this.loadFactor + 1.0F;
        int j = (f < 1.07374182E9F) ? (int)f : 1073741824;
        if (j > this.threshold)
          this.threshold = tableSizeFor(j); 
      } else if (i > this.threshold) {
        resize();
      } 
      for (Map.Entry entry : paramMap.entrySet()) {
        Object object1 = entry.getKey();
        Object object2 = entry.getValue();
        putVal(hash(object1), object1, object2, false, paramBoolean);
      } 
    } 
  }
  
  public int size() { return this.size; }
  
  public boolean isEmpty() { return (this.size == 0); }
  
  public V get(Object paramObject) {
    Node node;
    return (V)(((node = getNode(hash(paramObject), paramObject)) == null) ? null : node.value);
  }
  
  final Node<K, V> getNode(int paramInt, Object paramObject) {
    Node[] arrayOfNode;
    Node node;
    int i;
    if ((arrayOfNode = this.table) != null && (i = arrayOfNode.length) > 0 && (node = arrayOfNode[i - true & paramInt]) != null) {
      Object object;
      if (node.hash == paramInt && ((object = node.key) == paramObject || (paramObject != null && paramObject.equals(object))))
        return node; 
      Node node1;
      if ((node1 = node.next) != null) {
        if (node instanceof TreeNode)
          return ((TreeNode)node).getTreeNode(paramInt, paramObject); 
        do {
          if (node1.hash == paramInt && ((object = node1.key) == paramObject || (paramObject != null && paramObject.equals(object))))
            return node1; 
        } while ((node1 = node1.next) != null);
      } 
    } 
    return null;
  }
  
  public boolean containsKey(Object paramObject) { return (getNode(hash(paramObject), paramObject) != null); }
  
  public V put(K paramK, V paramV) { return (V)putVal(hash(paramK), paramK, paramV, false, true); }
  
  final V putVal(int paramInt, K paramK, V paramV, boolean paramBoolean1, boolean paramBoolean2) {
    Node[] arrayOfNode;
    int i;
    if ((arrayOfNode = this.table) == null || (i = arrayOfNode.length) == 0)
      i = arrayOfNode = resize().length; 
    Node node;
    int j;
    if ((node = arrayOfNode[j = i - true & paramInt]) == null) {
      arrayOfNode[j] = newNode(paramInt, paramK, paramV, null);
    } else {
      Node node1;
      Object object;
      if (node.hash == paramInt && ((object = node.key) == paramK || (paramK != null && paramK.equals(object)))) {
        node1 = node;
      } else if (node instanceof TreeNode) {
        node1 = ((TreeNode)node).putTreeVal(this, arrayOfNode, paramInt, paramK, paramV);
      } else {
        for (byte b = 0;; b++) {
          if ((node1 = node.next) == null) {
            node.next = newNode(paramInt, paramK, paramV, null);
            if (b >= 7)
              treeifyBin(arrayOfNode, paramInt); 
            break;
          } 
          if (node1.hash == paramInt && ((object = node1.key) == paramK || (paramK != null && paramK.equals(object))))
            break; 
          node = node1;
        } 
      } 
      if (node1 != null) {
        Object object1 = node1.value;
        if (!paramBoolean1 || object1 == null)
          node1.value = paramV; 
        afterNodeAccess(node1);
        return (V)object1;
      } 
    } 
    this.modCount++;
    if (++this.size > this.threshold)
      resize(); 
    afterNodeInsertion(paramBoolean2);
    return null;
  }
  
  final Node<K, V>[] resize() {
    byte b;
    Node[] arrayOfNode = this.table;
    int i = (arrayOfNode == null) ? 0 : arrayOfNode.length;
    int j = this.threshold;
    int k = 0;
    if (i) {
      if (i >= 1073741824) {
        this.threshold = Integer.MAX_VALUE;
        return arrayOfNode;
      } 
      if ((b = i << true) < 1073741824 && i >= 16)
        k = j << 1; 
    } else if (j > 0) {
      b = j;
    } else {
      b = 16;
      k = 12;
    } 
    if (k == 0) {
      float f = b * this.loadFactor;
      k = (b < 1073741824 && f < 1.07374182E9F) ? (int)f : Integer.MAX_VALUE;
    } 
    this.threshold = k;
    Node[] arrayOfNode1 = (Node[])new Node[b];
    this.table = arrayOfNode1;
    if (arrayOfNode != null)
      for (int m = 0; m < i; m++) {
        Node node;
        if ((node = arrayOfNode[m]) != null) {
          arrayOfNode[m] = null;
          if (node.next == null) {
            arrayOfNode1[node.hash & b - 1] = node;
          } else if (node instanceof TreeNode) {
            ((TreeNode)node).split(this, arrayOfNode1, m, i);
          } else {
            Node node5;
            Node node1 = null;
            Node node2 = null;
            Node node3 = null;
            Node node4 = null;
            do {
              node5 = node.next;
              if ((node.hash & i) == 0) {
                if (node2 == null) {
                  node1 = node;
                } else {
                  node2.next = node;
                } 
                node2 = node;
              } else {
                if (node4 == null) {
                  node3 = node;
                } else {
                  node4.next = node;
                } 
                node4 = node;
              } 
            } while ((node = node5) != null);
            if (node2 != null) {
              node2.next = null;
              arrayOfNode1[m] = node1;
            } 
            if (node4 != null) {
              node4.next = null;
              arrayOfNode1[m + i] = node3;
            } 
          } 
        } 
      }  
    return arrayOfNode1;
  }
  
  final void treeifyBin(Node<K, V>[] paramArrayOfNode, int paramInt) {
    int i;
    if (paramArrayOfNode == null || (i = paramArrayOfNode.length) < 64) {
      resize();
    } else {
      int j;
      Node<K, V> node;
      if ((node = paramArrayOfNode[j = i - true & paramInt]) != null) {
        TreeNode treeNode3;
        TreeNode treeNode1 = null;
        TreeNode treeNode2 = null;
        do {
          treeNode3 = replacementTreeNode(node, null);
          if (treeNode2 == null) {
            treeNode1 = treeNode3;
          } else {
            treeNode3.prev = treeNode2;
            treeNode2.next = treeNode3;
          } 
          treeNode2 = treeNode3;
        } while ((node = node.next) != null);
        paramArrayOfNode[j] = treeNode1;
        if (treeNode1 != null)
          treeNode1.treeify(paramArrayOfNode); 
      } 
    } 
  }
  
  public void putAll(Map<? extends K, ? extends V> paramMap) { putMapEntries(paramMap, true); }
  
  public V remove(Object paramObject) {
    Node node;
    return (V)(((node = removeNode(hash(paramObject), paramObject, null, false, true)) == null) ? null : node.value);
  }
  
  final Node<K, V> removeNode(int paramInt, Object paramObject1, Object paramObject2, boolean paramBoolean1, boolean paramBoolean2) {
    Node[] arrayOfNode;
    Node node;
    int i;
    int j;
    if ((arrayOfNode = this.table) != null && (i = arrayOfNode.length) > 0 && (node = arrayOfNode[j = i - true & paramInt]) != null) {
      Node node1 = null;
      Object object1;
      if (node.hash == paramInt && ((object1 = node.key) == paramObject1 || (paramObject1 != null && paramObject1.equals(object1)))) {
        node1 = node;
      } else {
        Node node2;
        if ((node2 = node.next) != null)
          if (node instanceof TreeNode) {
            node1 = ((TreeNode)node).getTreeNode(paramInt, paramObject1);
          } else {
            do {
              if (node2.hash == paramInt && ((object1 = node2.key) == paramObject1 || (paramObject1 != null && paramObject1.equals(object1)))) {
                node1 = node2;
                break;
              } 
              node = node2;
            } while ((node2 = node2.next) != null);
          }  
      } 
      Object object2;
      if (node1 != null && (!paramBoolean1 || (object2 = node1.value) == paramObject2 || (paramObject2 != null && paramObject2.equals(object2)))) {
        if (node1 instanceof TreeNode) {
          ((TreeNode)node1).removeTreeNode(this, arrayOfNode, paramBoolean2);
        } else if (node1 == node) {
          arrayOfNode[j] = node1.next;
        } else {
          node.next = node1.next;
        } 
        this.modCount++;
        this.size--;
        afterNodeRemoval(node1);
        return node1;
      } 
    } 
    return null;
  }
  
  public void clear() {
    this.modCount++;
    Node[] arrayOfNode;
    if ((arrayOfNode = this.table) != null && this.size > 0) {
      this.size = 0;
      for (byte b = 0; b < arrayOfNode.length; b++)
        arrayOfNode[b] = null; 
    } 
  }
  
  public boolean containsValue(Object paramObject) {
    Node[] arrayOfNode;
    if ((arrayOfNode = this.table) != null && this.size > 0)
      for (byte b = 0; b < arrayOfNode.length; b++) {
        for (Node node = arrayOfNode[b]; node != null; node = node.next) {
          Object object;
          if ((object = node.value) == paramObject || (paramObject != null && paramObject.equals(object)))
            return true; 
        } 
      }  
    return false;
  }
  
  public Set<K> keySet() {
    Set set = this.keySet;
    if (set == null) {
      set = new KeySet();
      this.keySet = set;
    } 
    return set;
  }
  
  public Collection<V> values() {
    Collection collection = this.values;
    if (collection == null) {
      collection = new Values();
      this.values = collection;
    } 
    return collection;
  }
  
  public Set<Map.Entry<K, V>> entrySet() {
    Set set;
    return ((set = this.entrySet) == null) ? (this.entrySet = new EntrySet()) : set;
  }
  
  public V getOrDefault(Object paramObject, V paramV) {
    Node node;
    return ((node = getNode(hash(paramObject), paramObject)) == null) ? paramV : node.value;
  }
  
  public V putIfAbsent(K paramK, V paramV) { return (V)putVal(hash(paramK), paramK, paramV, true, true); }
  
  public boolean remove(Object paramObject1, Object paramObject2) { return (removeNode(hash(paramObject1), paramObject1, paramObject2, true, true) != null); }
  
  public boolean replace(K paramK, V paramV1, V paramV2) {
    Node node;
    Object object;
    if ((node = getNode(hash(paramK), paramK)) != null && ((object = node.value) == paramV1 || (object != null && object.equals(paramV1)))) {
      node.value = paramV2;
      afterNodeAccess(node);
      return true;
    } 
    return false;
  }
  
  public V replace(K paramK, V paramV) {
    Node node;
    if ((node = getNode(hash(paramK), paramK)) != null) {
      Object object = node.value;
      node.value = paramV;
      afterNodeAccess(node);
      return (V)object;
    } 
    return null;
  }
  
  public V computeIfAbsent(K paramK, Function<? super K, ? extends V> paramFunction) {
    if (paramFunction == null)
      throw new NullPointerException(); 
    int i = hash(paramK);
    byte b = 0;
    TreeNode treeNode = null;
    Node node2 = null;
    Node[] arrayOfNode;
    int j;
    if (this.size > this.threshold || (arrayOfNode = this.table) == null || (j = arrayOfNode.length) == 0)
      j = arrayOfNode = resize().length; 
    Node node1;
    int k;
    if ((node1 = arrayOfNode[k = j - true & i]) != null) {
      if (node1 instanceof TreeNode) {
        node2 = (treeNode = (TreeNode)node1).getTreeNode(i, paramK);
      } else {
        Node node = node1;
        do {
          Object object2;
          if (node.hash == i && ((object2 = node.key) == paramK || (paramK != null && paramK.equals(object2)))) {
            node2 = node;
            break;
          } 
          ++b;
        } while ((node = node.next) != null);
      } 
      Object object1;
      if (node2 != null && (object1 = node2.value) != null) {
        afterNodeAccess(node2);
        return (V)object1;
      } 
    } 
    Object object = paramFunction.apply(paramK);
    if (object == null)
      return null; 
    if (node2 != null) {
      node2.value = object;
      afterNodeAccess(node2);
      return (V)object;
    } 
    if (treeNode != null) {
      treeNode.putTreeVal(this, arrayOfNode, i, paramK, object);
    } else {
      arrayOfNode[k] = newNode(i, paramK, object, node1);
      if (b >= 7)
        treeifyBin(arrayOfNode, i); 
    } 
    this.modCount++;
    this.size++;
    afterNodeInsertion(true);
    return (V)object;
  }
  
  public V computeIfPresent(K paramK, BiFunction<? super K, ? super V, ? extends V> paramBiFunction) {
    if (paramBiFunction == null)
      throw new NullPointerException(); 
    int i = hash(paramK);
    Node node;
    Object object;
    if ((node = getNode(i, paramK)) != null && (object = node.value) != null) {
      Object object1 = paramBiFunction.apply(paramK, object);
      if (object1 != null) {
        node.value = object1;
        afterNodeAccess(node);
        return (V)object1;
      } 
      removeNode(i, paramK, null, false, true);
    } 
    return null;
  }
  
  public V compute(K paramK, BiFunction<? super K, ? super V, ? extends V> paramBiFunction) {
    if (paramBiFunction == null)
      throw new NullPointerException(); 
    int i = hash(paramK);
    byte b = 0;
    TreeNode treeNode = null;
    Node node2 = null;
    Node[] arrayOfNode;
    int j;
    if (this.size > this.threshold || (arrayOfNode = this.table) == null || (j = arrayOfNode.length) == 0)
      j = arrayOfNode = resize().length; 
    Node node1;
    int k;
    if ((node1 = arrayOfNode[k = j - true & i]) != null)
      if (node1 instanceof TreeNode) {
        node2 = (treeNode = (TreeNode)node1).getTreeNode(i, paramK);
      } else {
        Node node = node1;
        do {
          Object object;
          if (node.hash == i && ((object = node.key) == paramK || (paramK != null && paramK.equals(object)))) {
            node2 = node;
            break;
          } 
          ++b;
        } while ((node = node.next) != null);
      }  
    Object object1 = (node2 == null) ? null : node2.value;
    Object object2 = paramBiFunction.apply(paramK, object1);
    if (node2 != null) {
      if (object2 != null) {
        node2.value = object2;
        afterNodeAccess(node2);
      } else {
        removeNode(i, paramK, null, false, true);
      } 
    } else if (object2 != null) {
      if (treeNode != null) {
        treeNode.putTreeVal(this, arrayOfNode, i, paramK, object2);
      } else {
        arrayOfNode[k] = newNode(i, paramK, object2, node1);
        if (b >= 7)
          treeifyBin(arrayOfNode, i); 
      } 
      this.modCount++;
      this.size++;
      afterNodeInsertion(true);
    } 
    return (V)object2;
  }
  
  public V merge(K paramK, V paramV, BiFunction<? super V, ? super V, ? extends V> paramBiFunction) {
    if (paramV == null)
      throw new NullPointerException(); 
    if (paramBiFunction == null)
      throw new NullPointerException(); 
    int i = hash(paramK);
    byte b = 0;
    TreeNode treeNode = null;
    Node node2 = null;
    Node[] arrayOfNode;
    int j;
    if (this.size > this.threshold || (arrayOfNode = this.table) == null || (j = arrayOfNode.length) == 0)
      j = arrayOfNode = resize().length; 
    Node node1;
    int k;
    if ((node1 = arrayOfNode[k = j - true & i]) != null)
      if (node1 instanceof TreeNode) {
        node2 = (treeNode = (TreeNode)node1).getTreeNode(i, paramK);
      } else {
        Node node = node1;
        do {
          Object object;
          if (node.hash == i && ((object = node.key) == paramK || (paramK != null && paramK.equals(object)))) {
            node2 = node;
            break;
          } 
          ++b;
        } while ((node = node.next) != null);
      }  
    if (node2 != null) {
      V v;
      if (node2.value != null) {
        Object object = paramBiFunction.apply(node2.value, paramV);
      } else {
        v = paramV;
      } 
      if (v != null) {
        node2.value = v;
        afterNodeAccess(node2);
      } else {
        removeNode(i, paramK, null, false, true);
      } 
      return v;
    } 
    if (paramV != null) {
      if (treeNode != null) {
        treeNode.putTreeVal(this, arrayOfNode, i, paramK, paramV);
      } else {
        arrayOfNode[k] = newNode(i, paramK, paramV, node1);
        if (b >= 7)
          treeifyBin(arrayOfNode, i); 
      } 
      this.modCount++;
      this.size++;
      afterNodeInsertion(true);
    } 
    return paramV;
  }
  
  public void forEach(BiConsumer<? super K, ? super V> paramBiConsumer) {
    if (paramBiConsumer == null)
      throw new NullPointerException(); 
    Node[] arrayOfNode;
    if (this.size > 0 && (arrayOfNode = this.table) != null) {
      int i = this.modCount;
      for (byte b = 0; b < arrayOfNode.length; b++) {
        for (Node node = arrayOfNode[b]; node != null; node = node.next)
          paramBiConsumer.accept(node.key, node.value); 
      } 
      if (this.modCount != i)
        throw new ConcurrentModificationException(); 
    } 
  }
  
  public void replaceAll(BiFunction<? super K, ? super V, ? extends V> paramBiFunction) {
    if (paramBiFunction == null)
      throw new NullPointerException(); 
    Node[] arrayOfNode;
    if (this.size > 0 && (arrayOfNode = this.table) != null) {
      int i = this.modCount;
      for (byte b = 0; b < arrayOfNode.length; b++) {
        for (Node node = arrayOfNode[b]; node != null; node = node.next)
          node.value = paramBiFunction.apply(node.key, node.value); 
      } 
      if (this.modCount != i)
        throw new ConcurrentModificationException(); 
    } 
  }
  
  public Object clone() {
    HashMap hashMap;
    try {
      hashMap = (HashMap)super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
    hashMap.reinitialize();
    hashMap.putMapEntries(this, false);
    return hashMap;
  }
  
  final float loadFactor() { return this.loadFactor; }
  
  final int capacity() { return (this.table != null) ? this.table.length : ((this.threshold > 0) ? this.threshold : 16); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    int i = capacity();
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(i);
    paramObjectOutputStream.writeInt(this.size);
    internalWriteEntries(paramObjectOutputStream);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    reinitialize();
    if (this.loadFactor <= 0.0F || Float.isNaN(this.loadFactor))
      throw new InvalidObjectException("Illegal load factor: " + this.loadFactor); 
    paramObjectInputStream.readInt();
    int i = paramObjectInputStream.readInt();
    if (i < 0)
      throw new InvalidObjectException("Illegal mappings count: " + i); 
    if (i > 0) {
      float f1 = Math.min(Math.max(0.25F, this.loadFactor), 4.0F);
      float f2 = i / f1 + 1.0F;
      byte b1 = (f2 < 16.0F) ? 16 : ((f2 >= 1.07374182E9F) ? 1073741824 : tableSizeFor((int)f2));
      float f3 = b1 * f1;
      this.threshold = (b1 < 1073741824 && f3 < 1.07374182E9F) ? (int)f3 : Integer.MAX_VALUE;
      SharedSecrets.getJavaOISAccess().checkArray(paramObjectInputStream, Entry[].class, b1);
      Node[] arrayOfNode = (Node[])new Node[b1];
      this.table = arrayOfNode;
      for (byte b2 = 0; b2 < i; b2++) {
        Object object1 = paramObjectInputStream.readObject();
        Object object2 = paramObjectInputStream.readObject();
        putVal(hash(object1), object1, object2, false, false);
      } 
    } 
  }
  
  Node<K, V> newNode(int paramInt, K paramK, V paramV, Node<K, V> paramNode) { return new Node(paramInt, paramK, paramV, paramNode); }
  
  Node<K, V> replacementNode(Node<K, V> paramNode1, Node<K, V> paramNode2) { return new Node(paramNode1.hash, paramNode1.key, paramNode1.value, paramNode2); }
  
  TreeNode<K, V> newTreeNode(int paramInt, K paramK, V paramV, Node<K, V> paramNode) { return new TreeNode(paramInt, paramK, paramV, paramNode); }
  
  TreeNode<K, V> replacementTreeNode(Node<K, V> paramNode1, Node<K, V> paramNode2) { return new TreeNode(paramNode1.hash, paramNode1.key, paramNode1.value, paramNode2); }
  
  void reinitialize() {
    this.table = null;
    this.entrySet = null;
    this.keySet = null;
    this.values = null;
    this.modCount = 0;
    this.threshold = 0;
    this.size = 0;
  }
  
  void afterNodeAccess(Node<K, V> paramNode) {}
  
  void afterNodeInsertion(boolean paramBoolean) {}
  
  void afterNodeRemoval(Node<K, V> paramNode) {}
  
  void internalWriteEntries(ObjectOutputStream paramObjectOutputStream) throws IOException {
    Node[] arrayOfNode;
    if (this.size > 0 && (arrayOfNode = this.table) != null)
      for (byte b = 0; b < arrayOfNode.length; b++) {
        for (Node node = arrayOfNode[b]; node != null; node = node.next) {
          paramObjectOutputStream.writeObject(node.key);
          paramObjectOutputStream.writeObject(node.value);
        } 
      }  
  }
  
  final class EntryIterator extends HashIterator implements Iterator<Map.Entry<K, V>> {
    EntryIterator() { super(HashMap.this); }
    
    public final Map.Entry<K, V> next() { return nextNode(); }
  }
  
  final class EntrySet extends AbstractSet<Map.Entry<K, V>> {
    public final int size() { return HashMap.this.size; }
    
    public final void clear() { HashMap.this.clear(); }
    
    public final Iterator<Map.Entry<K, V>> iterator() { return new HashMap.EntryIterator(HashMap.this); }
    
    public final boolean contains(Object param1Object) {
      if (!(param1Object instanceof Map.Entry))
        return false; 
      Map.Entry entry = (Map.Entry)param1Object;
      Object object = entry.getKey();
      HashMap.Node node = HashMap.this.getNode(HashMap.hash(object), object);
      return (node != null && node.equals(entry));
    }
    
    public final boolean remove(Object param1Object) {
      if (param1Object instanceof Map.Entry) {
        Map.Entry entry = (Map.Entry)param1Object;
        Object object1 = entry.getKey();
        Object object2 = entry.getValue();
        return (HashMap.this.removeNode(HashMap.hash(object1), object1, object2, true, true) != null);
      } 
      return false;
    }
    
    public final Spliterator<Map.Entry<K, V>> spliterator() { return new HashMap.EntrySpliterator(HashMap.this, 0, -1, 0, 0); }
    
    public final void forEach(Consumer<? super Map.Entry<K, V>> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      HashMap.Node[] arrayOfNode;
      if (HashMap.this.size > 0 && (arrayOfNode = HashMap.this.table) != null) {
        int i = HashMap.this.modCount;
        for (byte b = 0; b < arrayOfNode.length; b++) {
          for (HashMap.Node node = arrayOfNode[b]; node != null; node = node.next)
            param1Consumer.accept(node); 
        } 
        if (HashMap.this.modCount != i)
          throw new ConcurrentModificationException(); 
      } 
    }
  }
  
  static final class EntrySpliterator<K, V> extends HashMapSpliterator<K, V> implements Spliterator<Map.Entry<K, V>> {
    EntrySpliterator(HashMap<K, V> param1HashMap, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { super(param1HashMap, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public EntrySpliterator<K, V> trySplit() {
      int i = getFence();
      int j = this.index;
      int k = j + i >>> 1;
      return (j >= k || this.current != null) ? null : new EntrySpliterator(this.map, j, this.index = k, this.est >>>= 1, this.expectedModCount);
    }
    
    public void forEachRemaining(Consumer<? super Map.Entry<K, V>> param1Consumer) {
      int k;
      if (param1Consumer == null)
        throw new NullPointerException(); 
      HashMap hashMap = this.map;
      HashMap.Node[] arrayOfNode = hashMap.table;
      int j;
      if ((j = this.fence) < 0) {
        k = this.expectedModCount = hashMap.modCount;
        j = this.fence = (arrayOfNode == null) ? 0 : arrayOfNode.length;
      } else {
        k = this.expectedModCount;
      } 
      int i;
      if (arrayOfNode != null && arrayOfNode.length >= j && (i = this.index) >= 0 && (i < (this.index = j) || this.current != null)) {
        HashMap.Node node = this.current;
        this.current = null;
        do {
          if (node == null) {
            node = arrayOfNode[i++];
          } else {
            param1Consumer.accept(node);
            node = node.next;
          } 
        } while (node != null || i < j);
        if (hashMap.modCount != k)
          throw new ConcurrentModificationException(); 
      } 
    }
    
    public boolean tryAdvance(Consumer<? super Map.Entry<K, V>> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      HashMap.Node[] arrayOfNode = this.map.table;
      int i;
      if (arrayOfNode != null && arrayOfNode.length >= (i = getFence()) && this.index >= 0)
        while (this.current != null || this.index < i) {
          if (this.current == null) {
            this.current = arrayOfNode[this.index++];
            continue;
          } 
          HashMap.Node node = this.current;
          this.current = this.current.next;
          param1Consumer.accept(node);
          if (this.map.modCount != this.expectedModCount)
            throw new ConcurrentModificationException(); 
          return true;
        }  
      return false;
    }
    
    public int characteristics() { return ((this.fence < 0 || this.est == this.map.size) ? 64 : 0) | true; }
  }
  
  abstract class HashIterator {
    HashMap.Node<K, V> next;
    
    HashMap.Node<K, V> current;
    
    int expectedModCount = HashMap.this.modCount;
    
    int index;
    
    HashIterator() {
      HashMap.Node[] arrayOfNode = HashMap.this.table;
      this.current = this.next = null;
      this.index = 0;
      if (arrayOfNode != null && HashMap.this.size > 0)
        do {
        
        } while (this.index < arrayOfNode.length && (this.next = arrayOfNode[this.index++]) == null); 
    }
    
    public final boolean hasNext() { return (this.next != null); }
    
    final HashMap.Node<K, V> nextNode() {
      HashMap.Node node = this.next;
      if (HashMap.this.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
      if (node == null)
        throw new NoSuchElementException(); 
      HashMap.Node[] arrayOfNode;
      if ((this.next = (this.current = node).next) == null && (arrayOfNode = HashMap.this.table) != null)
        do {
        
        } while (this.index < arrayOfNode.length && (this.next = arrayOfNode[this.index++]) == null); 
      return node;
    }
    
    public final void remove() {
      HashMap.Node node = this.current;
      if (node == null)
        throw new IllegalStateException(); 
      if (HashMap.this.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
      this.current = null;
      Object object = node.key;
      HashMap.this.removeNode(HashMap.hash(object), object, null, false, false);
      this.expectedModCount = HashMap.this.modCount;
    }
  }
  
  static class HashMapSpliterator<K, V> extends Object {
    final HashMap<K, V> map;
    
    HashMap.Node<K, V> current;
    
    int index;
    
    int fence;
    
    int est;
    
    int expectedModCount;
    
    HashMapSpliterator(HashMap<K, V> param1HashMap, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      this.map = param1HashMap;
      this.index = param1Int1;
      this.fence = param1Int2;
      this.est = param1Int3;
      this.expectedModCount = param1Int4;
    }
    
    final int getFence() {
      int i;
      if ((i = this.fence) < 0) {
        HashMap hashMap = this.map;
        this.est = hashMap.size;
        this.expectedModCount = hashMap.modCount;
        HashMap.Node[] arrayOfNode = hashMap.table;
        i = this.fence = (arrayOfNode == null) ? 0 : arrayOfNode.length;
      } 
      return i;
    }
    
    public final long estimateSize() {
      getFence();
      return this.est;
    }
  }
  
  final class KeyIterator extends HashIterator implements Iterator<K> {
    KeyIterator() { super(HashMap.this); }
    
    public final K next() { return (K)(nextNode()).key; }
  }
  
  final class KeySet extends AbstractSet<K> {
    public final int size() { return HashMap.this.size; }
    
    public final void clear() { HashMap.this.clear(); }
    
    public final Iterator<K> iterator() { return new HashMap.KeyIterator(HashMap.this); }
    
    public final boolean contains(Object param1Object) { return HashMap.this.containsKey(param1Object); }
    
    public final boolean remove(Object param1Object) { return (HashMap.this.removeNode(HashMap.hash(param1Object), param1Object, null, false, true) != null); }
    
    public final Spliterator<K> spliterator() { return new HashMap.KeySpliterator(HashMap.this, 0, -1, 0, 0); }
    
    public final void forEach(Consumer<? super K> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      HashMap.Node[] arrayOfNode;
      if (HashMap.this.size > 0 && (arrayOfNode = HashMap.this.table) != null) {
        int i = HashMap.this.modCount;
        for (byte b = 0; b < arrayOfNode.length; b++) {
          for (HashMap.Node node = arrayOfNode[b]; node != null; node = node.next)
            param1Consumer.accept(node.key); 
        } 
        if (HashMap.this.modCount != i)
          throw new ConcurrentModificationException(); 
      } 
    }
  }
  
  static final class KeySpliterator<K, V> extends HashMapSpliterator<K, V> implements Spliterator<K> {
    KeySpliterator(HashMap<K, V> param1HashMap, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { super(param1HashMap, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public KeySpliterator<K, V> trySplit() {
      int i = getFence();
      int j = this.index;
      int k = j + i >>> 1;
      return (j >= k || this.current != null) ? null : new KeySpliterator(this.map, j, this.index = k, this.est >>>= 1, this.expectedModCount);
    }
    
    public void forEachRemaining(Consumer<? super K> param1Consumer) {
      int k;
      if (param1Consumer == null)
        throw new NullPointerException(); 
      HashMap hashMap = this.map;
      HashMap.Node[] arrayOfNode = hashMap.table;
      int j;
      if ((j = this.fence) < 0) {
        k = this.expectedModCount = hashMap.modCount;
        j = this.fence = (arrayOfNode == null) ? 0 : arrayOfNode.length;
      } else {
        k = this.expectedModCount;
      } 
      int i;
      if (arrayOfNode != null && arrayOfNode.length >= j && (i = this.index) >= 0 && (i < (this.index = j) || this.current != null)) {
        HashMap.Node node = this.current;
        this.current = null;
        do {
          if (node == null) {
            node = arrayOfNode[i++];
          } else {
            param1Consumer.accept(node.key);
            node = node.next;
          } 
        } while (node != null || i < j);
        if (hashMap.modCount != k)
          throw new ConcurrentModificationException(); 
      } 
    }
    
    public boolean tryAdvance(Consumer<? super K> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      HashMap.Node[] arrayOfNode = this.map.table;
      int i;
      if (arrayOfNode != null && arrayOfNode.length >= (i = getFence()) && this.index >= 0)
        while (this.current != null || this.index < i) {
          if (this.current == null) {
            this.current = arrayOfNode[this.index++];
            continue;
          } 
          Object object = this.current.key;
          this.current = this.current.next;
          param1Consumer.accept(object);
          if (this.map.modCount != this.expectedModCount)
            throw new ConcurrentModificationException(); 
          return true;
        }  
      return false;
    }
    
    public int characteristics() { return ((this.fence < 0 || this.est == this.map.size) ? 64 : 0) | true; }
  }
  
  static class Node<K, V> extends Object implements Map.Entry<K, V> {
    final int hash;
    
    final K key;
    
    V value;
    
    Node<K, V> next;
    
    Node(int param1Int, K param1K, V param1V, Node<K, V> param1Node) {
      this.hash = param1Int;
      this.key = param1K;
      this.value = param1V;
      this.next = param1Node;
    }
    
    public final K getKey() { return (K)this.key; }
    
    public final V getValue() { return (V)this.value; }
    
    public final String toString() { return this.key + "=" + this.value; }
    
    public final int hashCode() { return Objects.hashCode(this.key) ^ Objects.hashCode(this.value); }
    
    public final V setValue(V param1V) {
      Object object = this.value;
      this.value = param1V;
      return (V)object;
    }
    
    public final boolean equals(Object param1Object) {
      if (param1Object == this)
        return true; 
      if (param1Object instanceof Map.Entry) {
        Map.Entry entry = (Map.Entry)param1Object;
        if (Objects.equals(this.key, entry.getKey()) && Objects.equals(this.value, entry.getValue()))
          return true; 
      } 
      return false;
    }
  }
  
  static final class TreeNode<K, V> extends LinkedHashMap.Entry<K, V> {
    TreeNode<K, V> parent;
    
    TreeNode<K, V> left;
    
    TreeNode<K, V> right;
    
    TreeNode<K, V> prev;
    
    boolean red;
    
    TreeNode(int param1Int, K param1K, V param1V, HashMap.Node<K, V> param1Node) { super(param1Int, param1K, param1V, param1Node); }
    
    final TreeNode<K, V> root() {
      for (TreeNode treeNode = this;; treeNode = treeNode1) {
        TreeNode treeNode1;
        if ((treeNode1 = treeNode.parent) == null)
          return treeNode; 
      } 
    }
    
    static <K, V> void moveRootToFront(HashMap.Node<K, V>[] param1ArrayOfNode, TreeNode<K, V> param1TreeNode) {
      int i;
      if (param1TreeNode != null && param1ArrayOfNode != null && (i = param1ArrayOfNode.length) > 0) {
        int j = i - 1 & param1TreeNode.hash;
        TreeNode treeNode = (TreeNode)param1ArrayOfNode[j];
        if (param1TreeNode != treeNode) {
          param1ArrayOfNode[j] = param1TreeNode;
          TreeNode treeNode1 = param1TreeNode.prev;
          HashMap.Node node;
          if ((node = param1TreeNode.next) != null)
            ((TreeNode)node).prev = treeNode1; 
          if (treeNode1 != null)
            treeNode1.next = node; 
          if (treeNode != null)
            treeNode.prev = param1TreeNode; 
          param1TreeNode.next = treeNode;
          param1TreeNode.prev = null;
        } 
        assert checkInvariants(param1TreeNode);
      } 
    }
    
    final TreeNode<K, V> find(int param1Int, Object param1Object, Class<?> param1Class) {
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
          if ((object = treeNode.key) == param1Object || (param1Object != null && param1Object.equals(object)))
            return treeNode; 
          if (treeNode1 == null) {
            treeNode = treeNode2;
          } else if (treeNode2 == null) {
            treeNode = treeNode1;
          } else {
            int j;
            if ((param1Class != null || (param1Class = HashMap.comparableClassFor(param1Object)) != null) && (j = HashMap.compareComparables(param1Class, param1Object, object)) != 0) {
              treeNode = (j < 0) ? treeNode1 : treeNode2;
            } else {
              TreeNode treeNode3;
              if ((treeNode3 = treeNode2.find(param1Int, param1Object, param1Class)) != null)
                return treeNode3; 
              treeNode = treeNode1;
            } 
          } 
        } 
      } while (treeNode != null);
      return null;
    }
    
    final TreeNode<K, V> getTreeNode(int param1Int, Object param1Object) { return ((this.parent != null) ? root() : this).find(param1Int, param1Object, null); }
    
    static int tieBreakOrder(Object param1Object1, Object param1Object2) {
      int i;
      if (param1Object1 == null || param1Object2 == null || (i = param1Object1.getClass().getName().compareTo(param1Object2.getClass().getName())) == 0)
        i = (System.identityHashCode(param1Object1) <= System.identityHashCode(param1Object2)) ? -1 : 1; 
      return i;
    }
    
    final void treeify(HashMap.Node<K, V>[] param1ArrayOfNode) {
      TreeNode treeNode1 = null;
      for (TreeNode treeNode2 = this; treeNode2 != null; treeNode2 = treeNode) {
        TreeNode treeNode = (TreeNode)treeNode2.next;
        treeNode2.left = treeNode2.right = null;
        if (treeNode1 == null) {
          treeNode2.parent = null;
          treeNode2.red = false;
          treeNode1 = treeNode2;
        } else {
          TreeNode treeNode4;
          int j;
          Object object = treeNode2.key;
          int i = treeNode2.hash;
          Class clazz = null;
          TreeNode treeNode3 = treeNode1;
          do {
            Object object1 = treeNode3.key;
            int k;
            if ((k = treeNode3.hash) > i) {
              j = -1;
            } else if (k < i) {
              j = 1;
            } else if ((clazz == null && (clazz = HashMap.comparableClassFor(object)) == null) || (j = HashMap.compareComparables(clazz, object, object1)) == 0) {
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
      moveRootToFront(param1ArrayOfNode, treeNode1);
    }
    
    final HashMap.Node<K, V> untreeify(HashMap<K, V> param1HashMap) {
      HashMap.Node node1 = null;
      HashMap.Node node2 = null;
      TreeNode treeNode = this;
      while (treeNode != null) {
        HashMap.Node node4 = param1HashMap.replacementNode(treeNode, null);
        if (node2 == null) {
          node1 = node4;
        } else {
          node2.next = node4;
        } 
        node2 = node4;
        HashMap.Node node3 = treeNode.next;
      } 
      return node1;
    }
    
    final TreeNode<K, V> putTreeVal(HashMap<K, V> param1HashMap, HashMap.Node<K, V>[] param1ArrayOfNode, int param1Int, K param1K, V param1V) {
      TreeNode treeNode3;
      int i;
      Class clazz = null;
      boolean bool = false;
      TreeNode treeNode1 = (this.parent != null) ? root() : this;
      TreeNode treeNode2 = treeNode1;
      do {
        int j;
        if ((j = treeNode2.hash) > param1Int) {
          i = -1;
        } else if (j < param1Int) {
          i = 1;
        } else {
          Object object;
          if ((object = treeNode2.key) == param1K || (param1K != null && param1K.equals(object)))
            return treeNode2; 
          if ((clazz == null && (clazz = HashMap.comparableClassFor(param1K)) == null) || (i = HashMap.compareComparables(clazz, param1K, object)) == 0) {
            bool = true;
            TreeNode treeNode5;
            TreeNode treeNode6;
            if (!bool && (((treeNode6 = treeNode2.left) != null && (treeNode5 = treeNode6.find(param1Int, param1K, clazz)) != null) || ((treeNode6 = treeNode2.right) != null && (treeNode5 = treeNode6.find(param1Int, param1K, clazz)) != null)))
              return treeNode5; 
            i = tieBreakOrder(param1K, object);
          } 
        } 
        treeNode3 = treeNode2;
      } while ((treeNode2 = (i <= 0) ? treeNode2.left : treeNode2.right) != null);
      HashMap.Node node = treeNode3.next;
      TreeNode treeNode4 = param1HashMap.newTreeNode(param1Int, param1K, param1V, node);
      if (i <= 0) {
        treeNode3.left = treeNode4;
      } else {
        treeNode3.right = treeNode4;
      } 
      treeNode3.next = treeNode4;
      treeNode4.parent = treeNode4.prev = treeNode3;
      if (node != null)
        ((TreeNode)node).prev = treeNode4; 
      moveRootToFront(param1ArrayOfNode, balanceInsertion(treeNode1, treeNode4));
      return null;
    }
    
    final void removeTreeNode(HashMap<K, V> param1HashMap, HashMap.Node<K, V>[] param1ArrayOfNode, boolean param1Boolean) {
      TreeNode treeNode9;
      int i;
      if (param1ArrayOfNode == null || (i = param1ArrayOfNode.length) == 0)
        return; 
      int j = i - 1 & this.hash;
      TreeNode treeNode1 = (TreeNode)param1ArrayOfNode[j];
      TreeNode treeNode2 = treeNode1;
      TreeNode treeNode4 = (TreeNode)this.next;
      TreeNode treeNode5 = this.prev;
      if (treeNode5 == null) {
        param1ArrayOfNode[j] = treeNode1 = treeNode4;
      } else {
        treeNode5.next = treeNode4;
      } 
      if (treeNode4 != null)
        treeNode4.prev = treeNode5; 
      if (treeNode1 == null)
        return; 
      if (treeNode2.parent != null)
        treeNode2 = treeNode2.root(); 
      TreeNode treeNode3;
      if (treeNode2 == null || treeNode2.right == null || (treeNode3 = treeNode2.left) == null || treeNode3.left == null) {
        param1ArrayOfNode[j] = treeNode1.untreeify(param1HashMap);
        return;
      } 
      TreeNode treeNode6 = this;
      TreeNode treeNode7 = this.left;
      TreeNode treeNode8 = this.right;
      if (treeNode7 != null && treeNode8 != null) {
        TreeNode treeNode11;
        TreeNode treeNode12;
        for (treeNode11 = treeNode8; (treeNode12 = treeNode11.left) != null; treeNode11 = treeNode12);
        boolean bool = treeNode11.red;
        treeNode11.red = treeNode6.red;
        treeNode6.red = bool;
        TreeNode treeNode13 = treeNode11.right;
        TreeNode treeNode14 = treeNode6.parent;
        if (treeNode11 == treeNode8) {
          treeNode6.parent = treeNode11;
          treeNode11.right = treeNode6;
        } else {
          TreeNode treeNode = treeNode11.parent;
          if ((treeNode6.parent = treeNode) != null)
            if (treeNode11 == treeNode.left) {
              treeNode.left = treeNode6;
            } else {
              treeNode.right = treeNode6;
            }  
          if ((treeNode11.right = treeNode8) != null)
            treeNode8.parent = treeNode11; 
        } 
        treeNode6.left = null;
        if ((treeNode6.right = treeNode13) != null)
          treeNode13.parent = treeNode6; 
        if ((treeNode11.left = treeNode7) != null)
          treeNode7.parent = treeNode11; 
        if ((treeNode11.parent = treeNode14) == null) {
          treeNode2 = treeNode11;
        } else if (treeNode6 == treeNode14.left) {
          treeNode14.left = treeNode11;
        } else {
          treeNode14.right = treeNode11;
        } 
        if (treeNode13 != null) {
          treeNode9 = treeNode13;
        } else {
          treeNode9 = treeNode6;
        } 
      } else if (treeNode7 != null) {
        treeNode9 = treeNode7;
      } else if (treeNode8 != null) {
        treeNode9 = treeNode8;
      } else {
        treeNode9 = treeNode6;
      } 
      if (treeNode9 != treeNode6) {
        TreeNode treeNode = treeNode9.parent = treeNode6.parent;
        if (treeNode == null) {
          treeNode2 = treeNode9;
        } else if (treeNode6 == treeNode.left) {
          treeNode.left = treeNode9;
        } else {
          treeNode.right = treeNode9;
        } 
        treeNode6.left = treeNode6.right = treeNode6.parent = null;
      } 
      TreeNode treeNode10 = treeNode6.red ? treeNode2 : balanceDeletion(treeNode2, treeNode9);
      if (treeNode9 == treeNode6) {
        TreeNode treeNode = treeNode6.parent;
        treeNode6.parent = null;
        if (treeNode != null)
          if (treeNode6 == treeNode.left) {
            treeNode.left = null;
          } else if (treeNode6 == treeNode.right) {
            treeNode.right = null;
          }  
      } 
      if (param1Boolean)
        moveRootToFront(param1ArrayOfNode, treeNode10); 
    }
    
    final void split(HashMap<K, V> param1HashMap, HashMap.Node<K, V>[] param1ArrayOfNode, int param1Int1, int param1Int2) {
      TreeNode treeNode1 = this;
      TreeNode treeNode2 = null;
      TreeNode treeNode3 = null;
      TreeNode treeNode4 = null;
      TreeNode treeNode5 = null;
      byte b1 = 0;
      byte b2 = 0;
      for (TreeNode treeNode6 = treeNode1; treeNode6 != null; treeNode6 = treeNode) {
        TreeNode treeNode = (TreeNode)treeNode6.next;
        treeNode6.next = null;
        if ((treeNode6.hash & param1Int2) == 0) {
          if ((treeNode6.prev = treeNode3) == null) {
            treeNode2 = treeNode6;
          } else {
            treeNode3.next = treeNode6;
          } 
          treeNode3 = treeNode6;
          b1++;
        } else {
          if ((treeNode6.prev = treeNode5) == null) {
            treeNode4 = treeNode6;
          } else {
            treeNode5.next = treeNode6;
          } 
          treeNode5 = treeNode6;
          b2++;
        } 
      } 
      if (treeNode2 != null)
        if (b1 <= 6) {
          param1ArrayOfNode[param1Int1] = treeNode2.untreeify(param1HashMap);
        } else {
          param1ArrayOfNode[param1Int1] = treeNode2;
          if (treeNode4 != null)
            treeNode2.treeify(param1ArrayOfNode); 
        }  
      if (treeNode4 != null)
        if (b2 <= 6) {
          param1ArrayOfNode[param1Int1 + param1Int2] = treeNode4.untreeify(param1HashMap);
        } else {
          param1ArrayOfNode[param1Int1 + param1Int2] = treeNode4;
          if (treeNode2 != null)
            treeNode4.treeify(param1ArrayOfNode); 
        }  
    }
    
    static <K, V> TreeNode<K, V> rotateLeft(TreeNode<K, V> param1TreeNode1, TreeNode<K, V> param1TreeNode2) {
      TreeNode treeNode;
      if (param1TreeNode2 != null && (treeNode = param1TreeNode2.right) != null) {
        TreeNode treeNode2;
        if ((treeNode2 = param1TreeNode2.right = treeNode.left) != null)
          treeNode2.parent = param1TreeNode2; 
        TreeNode treeNode1;
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
    
    static <K, V> TreeNode<K, V> rotateRight(TreeNode<K, V> param1TreeNode1, TreeNode<K, V> param1TreeNode2) {
      TreeNode treeNode;
      if (param1TreeNode2 != null && (treeNode = param1TreeNode2.left) != null) {
        TreeNode treeNode2;
        if ((treeNode2 = param1TreeNode2.left = treeNode.right) != null)
          treeNode2.parent = param1TreeNode2; 
        TreeNode treeNode1;
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
    
    static <K, V> TreeNode<K, V> balanceInsertion(TreeNode<K, V> param1TreeNode1, TreeNode<K, V> param1TreeNode2) {
      param1TreeNode2.red = true;
      while (true) {
        TreeNode treeNode1;
        if ((treeNode1 = param1TreeNode2.parent) == null) {
          param1TreeNode2.red = false;
          return param1TreeNode2;
        } 
        TreeNode treeNode2;
        if (!treeNode1.red || (treeNode2 = treeNode1.parent) == null)
          return param1TreeNode1; 
        TreeNode treeNode3;
        if (treeNode1 == (treeNode3 = treeNode2.left)) {
          TreeNode treeNode;
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
    
    static <K, V> TreeNode<K, V> balanceDeletion(TreeNode<K, V> param1TreeNode1, TreeNode<K, V> param1TreeNode2) {
      while (true) {
        if (param1TreeNode2 == null || param1TreeNode2 == param1TreeNode1)
          return param1TreeNode1; 
        TreeNode treeNode1;
        if ((treeNode1 = param1TreeNode2.parent) == null) {
          param1TreeNode2.red = false;
          return param1TreeNode2;
        } 
        if (param1TreeNode2.red) {
          param1TreeNode2.red = false;
          return param1TreeNode1;
        } 
        TreeNode treeNode2;
        if ((treeNode2 = treeNode1.left) == param1TreeNode2) {
          TreeNode treeNode5;
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
          TreeNode treeNode6 = treeNode5.left;
          TreeNode treeNode7 = treeNode5.right;
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
        TreeNode treeNode3 = treeNode2.left;
        TreeNode treeNode4 = treeNode2.right;
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
    
    static <K, V> boolean checkInvariants(TreeNode<K, V> param1TreeNode) {
      TreeNode treeNode1 = param1TreeNode.parent;
      TreeNode treeNode2 = param1TreeNode.left;
      TreeNode treeNode3 = param1TreeNode.right;
      TreeNode treeNode4 = param1TreeNode.prev;
      TreeNode treeNode5 = (TreeNode)param1TreeNode.next;
      return (treeNode4 != null && treeNode4.next != param1TreeNode) ? false : ((treeNode5 != null && treeNode5.prev != param1TreeNode) ? false : ((treeNode1 != null && param1TreeNode != treeNode1.left && param1TreeNode != treeNode1.right) ? false : ((treeNode2 != null && (treeNode2.parent != param1TreeNode || treeNode2.hash > param1TreeNode.hash)) ? false : ((treeNode3 != null && (treeNode3.parent != param1TreeNode || treeNode3.hash < param1TreeNode.hash)) ? false : ((param1TreeNode.red && treeNode2 != null && treeNode2.red && treeNode3 != null && treeNode3.red) ? false : ((treeNode2 != null && !checkInvariants(treeNode2)) ? false : (!(treeNode3 != null && !checkInvariants(treeNode3)))))))));
    }
  }
  
  final class ValueIterator extends HashIterator implements Iterator<V> {
    ValueIterator() { super(HashMap.this); }
    
    public final V next() { return (V)(nextNode()).value; }
  }
  
  static final class ValueSpliterator<K, V> extends HashMapSpliterator<K, V> implements Spliterator<V> {
    ValueSpliterator(HashMap<K, V> param1HashMap, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { super(param1HashMap, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public ValueSpliterator<K, V> trySplit() {
      int i = getFence();
      int j = this.index;
      int k = j + i >>> 1;
      return (j >= k || this.current != null) ? null : new ValueSpliterator(this.map, j, this.index = k, this.est >>>= 1, this.expectedModCount);
    }
    
    public void forEachRemaining(Consumer<? super V> param1Consumer) {
      int k;
      if (param1Consumer == null)
        throw new NullPointerException(); 
      HashMap hashMap = this.map;
      HashMap.Node[] arrayOfNode = hashMap.table;
      int j;
      if ((j = this.fence) < 0) {
        k = this.expectedModCount = hashMap.modCount;
        j = this.fence = (arrayOfNode == null) ? 0 : arrayOfNode.length;
      } else {
        k = this.expectedModCount;
      } 
      int i;
      if (arrayOfNode != null && arrayOfNode.length >= j && (i = this.index) >= 0 && (i < (this.index = j) || this.current != null)) {
        HashMap.Node node = this.current;
        this.current = null;
        do {
          if (node == null) {
            node = arrayOfNode[i++];
          } else {
            param1Consumer.accept(node.value);
            node = node.next;
          } 
        } while (node != null || i < j);
        if (hashMap.modCount != k)
          throw new ConcurrentModificationException(); 
      } 
    }
    
    public boolean tryAdvance(Consumer<? super V> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      HashMap.Node[] arrayOfNode = this.map.table;
      int i;
      if (arrayOfNode != null && arrayOfNode.length >= (i = getFence()) && this.index >= 0)
        while (this.current != null || this.index < i) {
          if (this.current == null) {
            this.current = arrayOfNode[this.index++];
            continue;
          } 
          Object object = this.current.value;
          this.current = this.current.next;
          param1Consumer.accept(object);
          if (this.map.modCount != this.expectedModCount)
            throw new ConcurrentModificationException(); 
          return true;
        }  
      return false;
    }
    
    public int characteristics() { return (this.fence < 0 || this.est == this.map.size) ? 64 : 0; }
  }
  
  final class Values extends AbstractCollection<V> {
    public final int size() { return HashMap.this.size; }
    
    public final void clear() { HashMap.this.clear(); }
    
    public final Iterator<V> iterator() { return new HashMap.ValueIterator(HashMap.this); }
    
    public final boolean contains(Object param1Object) { return HashMap.this.containsValue(param1Object); }
    
    public final Spliterator<V> spliterator() { return new HashMap.ValueSpliterator(HashMap.this, 0, -1, 0, 0); }
    
    public final void forEach(Consumer<? super V> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      HashMap.Node[] arrayOfNode;
      if (HashMap.this.size > 0 && (arrayOfNode = HashMap.this.table) != null) {
        int i = HashMap.this.modCount;
        for (byte b = 0; b < arrayOfNode.length; b++) {
          for (HashMap.Node node = arrayOfNode[b]; node != null; node = node.next)
            param1Consumer.accept(node.value); 
        } 
        if (HashMap.this.modCount != i)
          throw new ConcurrentModificationException(); 
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\HashMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */