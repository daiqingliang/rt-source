package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.function.Consumer;
import sun.misc.SharedSecrets;

public class ArrayDeque<E> extends AbstractCollection<E> implements Deque<E>, Cloneable, Serializable {
  Object[] elements;
  
  int head;
  
  int tail;
  
  private static final int MIN_INITIAL_CAPACITY = 8;
  
  private static final long serialVersionUID = 2340985798034038923L;
  
  private static int calculateSize(int paramInt) {
    int i = 8;
    if (paramInt >= i) {
      i = paramInt;
      i |= i >>> 1;
      i |= i >>> 2;
      i |= i >>> 4;
      i |= i >>> 8;
      i |= i >>> 16;
      if (++i < 0)
        i >>>= 1; 
    } 
    return i;
  }
  
  private void allocateElements(int paramInt) { this.elements = new Object[calculateSize(paramInt)]; }
  
  private void doubleCapacity() {
    assert this.head == this.tail;
    int i = this.head;
    int j = this.elements.length;
    int k = j - i;
    int m = j << 1;
    if (m < 0)
      throw new IllegalStateException("Sorry, deque too big"); 
    Object[] arrayOfObject = new Object[m];
    System.arraycopy(this.elements, i, arrayOfObject, 0, k);
    System.arraycopy(this.elements, 0, arrayOfObject, k, i);
    this.elements = arrayOfObject;
    this.head = 0;
    this.tail = j;
  }
  
  private <T> T[] copyElements(T[] paramArrayOfT) {
    if (this.head < this.tail) {
      System.arraycopy(this.elements, this.head, paramArrayOfT, 0, size());
    } else if (this.head > this.tail) {
      int i = this.elements.length - this.head;
      System.arraycopy(this.elements, this.head, paramArrayOfT, 0, i);
      System.arraycopy(this.elements, 0, paramArrayOfT, i, this.tail);
    } 
    return paramArrayOfT;
  }
  
  public ArrayDeque() { this.elements = new Object[16]; }
  
  public ArrayDeque(int paramInt) { allocateElements(paramInt); }
  
  public ArrayDeque(Collection<? extends E> paramCollection) {
    allocateElements(paramCollection.size());
    addAll(paramCollection);
  }
  
  public void addFirst(E paramE) {
    if (paramE == null)
      throw new NullPointerException(); 
    this.elements[this.head = this.head - 1 & this.elements.length - 1] = paramE;
    if (this.head == this.tail)
      doubleCapacity(); 
  }
  
  public void addLast(E paramE) {
    if (paramE == null)
      throw new NullPointerException(); 
    this.elements[this.tail] = paramE;
    if ((this.tail = this.tail + 1 & this.elements.length - 1) == this.head)
      doubleCapacity(); 
  }
  
  public boolean offerFirst(E paramE) {
    addFirst(paramE);
    return true;
  }
  
  public boolean offerLast(E paramE) {
    addLast(paramE);
    return true;
  }
  
  public E removeFirst() {
    Object object = pollFirst();
    if (object == null)
      throw new NoSuchElementException(); 
    return (E)object;
  }
  
  public E removeLast() {
    Object object = pollLast();
    if (object == null)
      throw new NoSuchElementException(); 
    return (E)object;
  }
  
  public E pollFirst() {
    int i = this.head;
    Object object = this.elements[i];
    if (object == null)
      return null; 
    this.elements[i] = null;
    this.head = i + 1 & this.elements.length - 1;
    return (E)object;
  }
  
  public E pollLast() {
    int i = this.tail - 1 & this.elements.length - 1;
    Object object = this.elements[i];
    if (object == null)
      return null; 
    this.elements[i] = null;
    this.tail = i;
    return (E)object;
  }
  
  public E getFirst() {
    Object object = this.elements[this.head];
    if (object == null)
      throw new NoSuchElementException(); 
    return (E)object;
  }
  
  public E getLast() {
    Object object = this.elements[this.tail - 1 & this.elements.length - 1];
    if (object == null)
      throw new NoSuchElementException(); 
    return (E)object;
  }
  
  public E peekFirst() { return (E)this.elements[this.head]; }
  
  public E peekLast() { return (E)this.elements[this.tail - 1 & this.elements.length - 1]; }
  
  public boolean removeFirstOccurrence(Object paramObject) {
    if (paramObject == null)
      return false; 
    int i = this.elements.length - 1;
    Object object;
    for (int j = this.head; (object = this.elements[j]) != null; j = j + 1 & i) {
      if (paramObject.equals(object)) {
        delete(j);
        return true;
      } 
    } 
    return false;
  }
  
  public boolean removeLastOccurrence(Object paramObject) {
    if (paramObject == null)
      return false; 
    int i = this.elements.length - 1;
    Object object;
    for (int j = this.tail - 1 & i; (object = this.elements[j]) != null; j = j - 1 & i) {
      if (paramObject.equals(object)) {
        delete(j);
        return true;
      } 
    } 
    return false;
  }
  
  public boolean add(E paramE) {
    addLast(paramE);
    return true;
  }
  
  public boolean offer(E paramE) { return offerLast(paramE); }
  
  public E remove() { return (E)removeFirst(); }
  
  public E poll() { return (E)pollFirst(); }
  
  public E element() { return (E)getFirst(); }
  
  public E peek() { return (E)peekFirst(); }
  
  public void push(E paramE) { addFirst(paramE); }
  
  public E pop() { return (E)removeFirst(); }
  
  private void checkInvariants() {
    assert this.elements[this.tail] == null;
    assert false;
    throw new AssertionError();
  }
  
  private boolean delete(int paramInt) {
    checkInvariants();
    Object[] arrayOfObject = this.elements;
    int i = arrayOfObject.length - 1;
    int j = this.head;
    int k = this.tail;
    int m = paramInt - j & i;
    int n = k - paramInt & i;
    if (m >= (k - j & i))
      throw new ConcurrentModificationException(); 
    if (m < n) {
      if (j <= paramInt) {
        System.arraycopy(arrayOfObject, j, arrayOfObject, j + 1, m);
      } else {
        System.arraycopy(arrayOfObject, 0, arrayOfObject, 1, paramInt);
        arrayOfObject[0] = arrayOfObject[i];
        System.arraycopy(arrayOfObject, j, arrayOfObject, j + 1, i - j);
      } 
      arrayOfObject[j] = null;
      this.head = j + 1 & i;
      return false;
    } 
    if (paramInt < k) {
      System.arraycopy(arrayOfObject, paramInt + 1, arrayOfObject, paramInt, n);
      this.tail = k - 1;
    } else {
      System.arraycopy(arrayOfObject, paramInt + 1, arrayOfObject, paramInt, i - paramInt);
      arrayOfObject[i] = arrayOfObject[0];
      System.arraycopy(arrayOfObject, 1, arrayOfObject, 0, k);
      this.tail = k - 1 & i;
    } 
    return true;
  }
  
  public int size() { return this.tail - this.head & this.elements.length - 1; }
  
  public boolean isEmpty() { return (this.head == this.tail); }
  
  public Iterator<E> iterator() { return new DeqIterator(null); }
  
  public Iterator<E> descendingIterator() { return new DescendingIterator(null); }
  
  public boolean contains(Object paramObject) {
    if (paramObject == null)
      return false; 
    int i = this.elements.length - 1;
    Object object;
    for (int j = this.head; (object = this.elements[j]) != null; j = j + 1 & i) {
      if (paramObject.equals(object))
        return true; 
    } 
    return false;
  }
  
  public boolean remove(Object paramObject) { return removeFirstOccurrence(paramObject); }
  
  public void clear() {
    int i = this.head;
    int j = this.tail;
    if (i != j) {
      this.head = this.tail = 0;
      int k = i;
      int m = this.elements.length - 1;
      do {
        this.elements[k] = null;
        k = k + 1 & m;
      } while (k != j);
    } 
  }
  
  public Object[] toArray() { return copyElements(new Object[size()]); }
  
  public <T> T[] toArray(T[] paramArrayOfT) {
    int i = size();
    if (paramArrayOfT.length < i)
      paramArrayOfT = (T[])(Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), i); 
    copyElements(paramArrayOfT);
    if (paramArrayOfT.length > i)
      paramArrayOfT[i] = null; 
    return paramArrayOfT;
  }
  
  public ArrayDeque<E> clone() {
    try {
      ArrayDeque arrayDeque = (ArrayDeque)super.clone();
      arrayDeque.elements = Arrays.copyOf(this.elements, this.elements.length);
      return arrayDeque;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new AssertionError();
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(size());
    int i = this.elements.length - 1;
    for (int j = this.head; j != this.tail; j = j + 1 & i)
      paramObjectOutputStream.writeObject(this.elements[j]); 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    int i = paramObjectInputStream.readInt();
    int j = calculateSize(i);
    SharedSecrets.getJavaOISAccess().checkArray(paramObjectInputStream, Object[].class, j);
    allocateElements(i);
    this.head = 0;
    this.tail = i;
    for (byte b = 0; b < i; b++)
      this.elements[b] = paramObjectInputStream.readObject(); 
  }
  
  public Spliterator<E> spliterator() { return new DeqSpliterator(this, -1, -1); }
  
  private class DeqIterator extends Object implements Iterator<E> {
    private int cursor = ArrayDeque.this.head;
    
    private int fence = ArrayDeque.this.tail;
    
    private int lastRet = -1;
    
    private DeqIterator() {}
    
    public boolean hasNext() { return (this.cursor != this.fence); }
    
    public E next() {
      if (this.cursor == this.fence)
        throw new NoSuchElementException(); 
      Object object = ArrayDeque.this.elements[this.cursor];
      if (ArrayDeque.this.tail != this.fence || object == null)
        throw new ConcurrentModificationException(); 
      this.lastRet = this.cursor;
      this.cursor = this.cursor + 1 & ArrayDeque.this.elements.length - 1;
      return (E)object;
    }
    
    public void remove() {
      if (this.lastRet < 0)
        throw new IllegalStateException(); 
      if (ArrayDeque.this.delete(this.lastRet)) {
        this.cursor = this.cursor - 1 & ArrayDeque.this.elements.length - 1;
        this.fence = ArrayDeque.this.tail;
      } 
      this.lastRet = -1;
    }
    
    public void forEachRemaining(Consumer<? super E> param1Consumer) {
      Objects.requireNonNull(param1Consumer);
      Object[] arrayOfObject = ArrayDeque.this.elements;
      int i = arrayOfObject.length - 1;
      int j = this.fence;
      int k = this.cursor;
      this.cursor = j;
      while (k != j) {
        Object object = arrayOfObject[k];
        k = k + 1 & i;
        if (object == null)
          throw new ConcurrentModificationException(); 
        param1Consumer.accept(object);
      } 
    }
  }
  
  static final class DeqSpliterator<E> extends Object implements Spliterator<E> {
    private final ArrayDeque<E> deq;
    
    private int fence;
    
    private int index;
    
    DeqSpliterator(ArrayDeque<E> param1ArrayDeque, int param1Int1, int param1Int2) {
      this.deq = param1ArrayDeque;
      this.index = param1Int1;
      this.fence = param1Int2;
    }
    
    private int getFence() {
      int i;
      if ((i = this.fence) < 0) {
        i = this.fence = this.deq.tail;
        this.index = this.deq.head;
      } 
      return i;
    }
    
    public DeqSpliterator<E> trySplit() {
      int i = getFence();
      int j = this.index;
      int k = this.deq.elements.length;
      if (j != i && (j + 1 & k - 1) != i) {
        if (j > i)
          i += k; 
        int m = j + i >>> 1 & k - 1;
        return new DeqSpliterator(this.deq, j, this.index = m);
      } 
      return null;
    }
    
    public void forEachRemaining(Consumer<? super E> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      Object[] arrayOfObject = this.deq.elements;
      int i = arrayOfObject.length - 1;
      int j = getFence();
      int k = this.index;
      this.index = j;
      while (k != j) {
        Object object = arrayOfObject[k];
        k = k + 1 & i;
        if (object == null)
          throw new ConcurrentModificationException(); 
        param1Consumer.accept(object);
      } 
    }
    
    public boolean tryAdvance(Consumer<? super E> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      Object[] arrayOfObject = this.deq.elements;
      int i = arrayOfObject.length - 1;
      int j = getFence();
      int k = this.index;
      if (k != this.fence) {
        Object object = arrayOfObject[k];
        this.index = k + 1 & i;
        if (object == null)
          throw new ConcurrentModificationException(); 
        param1Consumer.accept(object);
        return true;
      } 
      return false;
    }
    
    public long estimateSize() {
      int i = getFence() - this.index;
      if (i < 0)
        i += this.deq.elements.length; 
      return i;
    }
    
    public int characteristics() { return 16720; }
  }
  
  private class DescendingIterator extends Object implements Iterator<E> {
    private int cursor = ArrayDeque.this.tail;
    
    private int fence = ArrayDeque.this.head;
    
    private int lastRet = -1;
    
    private DescendingIterator() {}
    
    public boolean hasNext() { return (this.cursor != this.fence); }
    
    public E next() {
      if (this.cursor == this.fence)
        throw new NoSuchElementException(); 
      this.cursor = this.cursor - 1 & ArrayDeque.this.elements.length - 1;
      Object object = ArrayDeque.this.elements[this.cursor];
      if (ArrayDeque.this.head != this.fence || object == null)
        throw new ConcurrentModificationException(); 
      this.lastRet = this.cursor;
      return (E)object;
    }
    
    public void remove() {
      if (this.lastRet < 0)
        throw new IllegalStateException(); 
      if (!ArrayDeque.this.delete(this.lastRet)) {
        this.cursor = this.cursor + 1 & ArrayDeque.this.elements.length - 1;
        this.fence = ArrayDeque.this.head;
      } 
      this.lastRet = -1;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\ArrayDeque.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */