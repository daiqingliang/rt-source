package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class Vector<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, Serializable {
  protected Object[] elementData;
  
  protected int elementCount;
  
  protected int capacityIncrement;
  
  private static final long serialVersionUID = -2767605614048989439L;
  
  private static final int MAX_ARRAY_SIZE = 2147483639;
  
  public Vector(int paramInt1, int paramInt2) {
    if (paramInt1 < 0)
      throw new IllegalArgumentException("Illegal Capacity: " + paramInt1); 
    this.elementData = new Object[paramInt1];
    this.capacityIncrement = paramInt2;
  }
  
  public Vector(int paramInt) { this(paramInt, 0); }
  
  public Vector() { this(10); }
  
  public Vector(Collection<? extends E> paramCollection) {
    this.elementData = paramCollection.toArray();
    this.elementCount = this.elementData.length;
    if (this.elementData.getClass() != Object[].class)
      this.elementData = Arrays.copyOf(this.elementData, this.elementCount, Object[].class); 
  }
  
  public void copyInto(Object[] paramArrayOfObject) { System.arraycopy(this.elementData, 0, paramArrayOfObject, 0, this.elementCount); }
  
  public void trimToSize() {
    this.modCount++;
    int i = this.elementData.length;
    if (this.elementCount < i)
      this.elementData = Arrays.copyOf(this.elementData, this.elementCount); 
  }
  
  public void ensureCapacity(int paramInt) {
    if (paramInt > 0) {
      this.modCount++;
      ensureCapacityHelper(paramInt);
    } 
  }
  
  private void ensureCapacityHelper(int paramInt) {
    if (paramInt - this.elementData.length > 0)
      grow(paramInt); 
  }
  
  private void grow(int paramInt) {
    int i = this.elementData.length;
    int j = i + ((this.capacityIncrement > 0) ? this.capacityIncrement : i);
    if (j - paramInt < 0)
      j = paramInt; 
    if (j - 2147483639 > 0)
      j = hugeCapacity(paramInt); 
    this.elementData = Arrays.copyOf(this.elementData, j);
  }
  
  private static int hugeCapacity(int paramInt) {
    if (paramInt < 0)
      throw new OutOfMemoryError(); 
    return (paramInt > 2147483639) ? Integer.MAX_VALUE : 2147483639;
  }
  
  public void setSize(int paramInt) {
    this.modCount++;
    if (paramInt > this.elementCount) {
      ensureCapacityHelper(paramInt);
    } else {
      for (int i = paramInt; i < this.elementCount; i++)
        this.elementData[i] = null; 
    } 
    this.elementCount = paramInt;
  }
  
  public int capacity() { return this.elementData.length; }
  
  public int size() { return this.elementCount; }
  
  public boolean isEmpty() { return (this.elementCount == 0); }
  
  public Enumeration<E> elements() { return new Enumeration<E>() {
        int count = 0;
        
        public boolean hasMoreElements() { return (this.count < Vector.this.elementCount); }
        
        public E nextElement() {
          synchronized (Vector.this) {
            if (this.count < Vector.this.elementCount)
              return (E)Vector.this.elementData(this.count++); 
          } 
          throw new NoSuchElementException("Vector Enumeration");
        }
      }; }
  
  public boolean contains(Object paramObject) { return (indexOf(paramObject, 0) >= 0); }
  
  public int indexOf(Object paramObject) { return indexOf(paramObject, 0); }
  
  public int indexOf(Object paramObject, int paramInt) {
    if (paramObject == null) {
      for (int i = paramInt; i < this.elementCount; i++) {
        if (this.elementData[i] == null)
          return i; 
      } 
    } else {
      for (int i = paramInt; i < this.elementCount; i++) {
        if (paramObject.equals(this.elementData[i]))
          return i; 
      } 
    } 
    return -1;
  }
  
  public int lastIndexOf(Object paramObject) { return lastIndexOf(paramObject, this.elementCount - 1); }
  
  public int lastIndexOf(Object paramObject, int paramInt) {
    if (paramInt >= this.elementCount)
      throw new IndexOutOfBoundsException(paramInt + " >= " + this.elementCount); 
    if (paramObject == null) {
      for (int i = paramInt; i >= 0; i--) {
        if (this.elementData[i] == null)
          return i; 
      } 
    } else {
      for (int i = paramInt; i >= 0; i--) {
        if (paramObject.equals(this.elementData[i]))
          return i; 
      } 
    } 
    return -1;
  }
  
  public E elementAt(int paramInt) {
    if (paramInt >= this.elementCount)
      throw new ArrayIndexOutOfBoundsException(paramInt + " >= " + this.elementCount); 
    return (E)elementData(paramInt);
  }
  
  public E firstElement() {
    if (this.elementCount == 0)
      throw new NoSuchElementException(); 
    return (E)elementData(0);
  }
  
  public E lastElement() {
    if (this.elementCount == 0)
      throw new NoSuchElementException(); 
    return (E)elementData(this.elementCount - 1);
  }
  
  public void setElementAt(E paramE, int paramInt) {
    if (paramInt >= this.elementCount)
      throw new ArrayIndexOutOfBoundsException(paramInt + " >= " + this.elementCount); 
    this.elementData[paramInt] = paramE;
  }
  
  public void removeElementAt(int paramInt) {
    this.modCount++;
    if (paramInt >= this.elementCount)
      throw new ArrayIndexOutOfBoundsException(paramInt + " >= " + this.elementCount); 
    if (paramInt < 0)
      throw new ArrayIndexOutOfBoundsException(paramInt); 
    int i = this.elementCount - paramInt - 1;
    if (i > 0)
      System.arraycopy(this.elementData, paramInt + 1, this.elementData, paramInt, i); 
    this.elementCount--;
    this.elementData[this.elementCount] = null;
  }
  
  public void insertElementAt(E paramE, int paramInt) {
    this.modCount++;
    if (paramInt > this.elementCount)
      throw new ArrayIndexOutOfBoundsException(paramInt + " > " + this.elementCount); 
    ensureCapacityHelper(this.elementCount + 1);
    System.arraycopy(this.elementData, paramInt, this.elementData, paramInt + 1, this.elementCount - paramInt);
    this.elementData[paramInt] = paramE;
    this.elementCount++;
  }
  
  public void addElement(E paramE) {
    this.modCount++;
    ensureCapacityHelper(this.elementCount + 1);
    this.elementData[this.elementCount++] = paramE;
  }
  
  public boolean removeElement(Object paramObject) {
    this.modCount++;
    int i = indexOf(paramObject);
    if (i >= 0) {
      removeElementAt(i);
      return true;
    } 
    return false;
  }
  
  public void removeAllElements() {
    this.modCount++;
    for (byte b = 0; b < this.elementCount; b++)
      this.elementData[b] = null; 
    this.elementCount = 0;
  }
  
  public Object clone() {
    try {
      Vector vector = (Vector)super.clone();
      vector.elementData = Arrays.copyOf(this.elementData, this.elementCount);
      vector.modCount = 0;
      return vector;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
  
  public Object[] toArray() { return Arrays.copyOf(this.elementData, this.elementCount); }
  
  public <T> T[] toArray(T[] paramArrayOfT) {
    if (paramArrayOfT.length < this.elementCount)
      return (T[])(Object[])Arrays.copyOf(this.elementData, this.elementCount, paramArrayOfT.getClass()); 
    System.arraycopy(this.elementData, 0, paramArrayOfT, 0, this.elementCount);
    if (paramArrayOfT.length > this.elementCount)
      paramArrayOfT[this.elementCount] = null; 
    return paramArrayOfT;
  }
  
  E elementData(int paramInt) { return (E)this.elementData[paramInt]; }
  
  public E get(int paramInt) {
    if (paramInt >= this.elementCount)
      throw new ArrayIndexOutOfBoundsException(paramInt); 
    return (E)elementData(paramInt);
  }
  
  public E set(int paramInt, E paramE) {
    if (paramInt >= this.elementCount)
      throw new ArrayIndexOutOfBoundsException(paramInt); 
    Object object = elementData(paramInt);
    this.elementData[paramInt] = paramE;
    return (E)object;
  }
  
  public boolean add(E paramE) {
    this.modCount++;
    ensureCapacityHelper(this.elementCount + 1);
    this.elementData[this.elementCount++] = paramE;
    return true;
  }
  
  public boolean remove(Object paramObject) { return removeElement(paramObject); }
  
  public void add(int paramInt, E paramE) { insertElementAt(paramE, paramInt); }
  
  public E remove(int paramInt) {
    this.modCount++;
    if (paramInt >= this.elementCount)
      throw new ArrayIndexOutOfBoundsException(paramInt); 
    Object object = elementData(paramInt);
    int i = this.elementCount - paramInt - 1;
    if (i > 0)
      System.arraycopy(this.elementData, paramInt + 1, this.elementData, paramInt, i); 
    this.elementData[--this.elementCount] = null;
    return (E)object;
  }
  
  public void clear() { removeAllElements(); }
  
  public boolean containsAll(Collection<?> paramCollection) { return super.containsAll(paramCollection); }
  
  public boolean addAll(Collection<? extends E> paramCollection) {
    this.modCount++;
    Object[] arrayOfObject = paramCollection.toArray();
    int i = arrayOfObject.length;
    ensureCapacityHelper(this.elementCount + i);
    System.arraycopy(arrayOfObject, 0, this.elementData, this.elementCount, i);
    this.elementCount += i;
    return (i != 0);
  }
  
  public boolean removeAll(Collection<?> paramCollection) { return super.removeAll(paramCollection); }
  
  public boolean retainAll(Collection<?> paramCollection) { return super.retainAll(paramCollection); }
  
  public boolean addAll(int paramInt, Collection<? extends E> paramCollection) {
    this.modCount++;
    if (paramInt < 0 || paramInt > this.elementCount)
      throw new ArrayIndexOutOfBoundsException(paramInt); 
    Object[] arrayOfObject = paramCollection.toArray();
    int i = arrayOfObject.length;
    ensureCapacityHelper(this.elementCount + i);
    int j = this.elementCount - paramInt;
    if (j > 0)
      System.arraycopy(this.elementData, paramInt, this.elementData, paramInt + i, j); 
    System.arraycopy(arrayOfObject, 0, this.elementData, paramInt, i);
    this.elementCount += i;
    return (i != 0);
  }
  
  public boolean equals(Object paramObject) { return super.equals(paramObject); }
  
  public int hashCode() { return super.hashCode(); }
  
  public String toString() { return super.toString(); }
  
  public List<E> subList(int paramInt1, int paramInt2) { return Collections.synchronizedList(super.subList(paramInt1, paramInt2), this); }
  
  protected void removeRange(int paramInt1, int paramInt2) {
    this.modCount++;
    int i = this.elementCount - paramInt2;
    System.arraycopy(this.elementData, paramInt2, this.elementData, paramInt1, i);
    int j = this.elementCount - paramInt2 - paramInt1;
    while (this.elementCount != j)
      this.elementData[--this.elementCount] = null; 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    int i = getField.get("elementCount", 0);
    Object[] arrayOfObject = (Object[])getField.get("elementData", null);
    if (i < 0 || arrayOfObject == null || i > arrayOfObject.length)
      throw new StreamCorruptedException("Inconsistent vector internals"); 
    this.elementCount = i;
    this.elementData = (Object[])arrayOfObject.clone();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    Object[] arrayOfObject;
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    synchronized (this) {
      putField.put("capacityIncrement", this.capacityIncrement);
      putField.put("elementCount", this.elementCount);
      arrayOfObject = (Object[])this.elementData.clone();
    } 
    putField.put("elementData", arrayOfObject);
    paramObjectOutputStream.writeFields();
  }
  
  public ListIterator<E> listIterator(int paramInt) {
    if (paramInt < 0 || paramInt > this.elementCount)
      throw new IndexOutOfBoundsException("Index: " + paramInt); 
    return new ListItr(paramInt);
  }
  
  public ListIterator<E> listIterator() { return new ListItr(0); }
  
  public Iterator<E> iterator() { return new Itr(null); }
  
  public void forEach(Consumer<? super E> paramConsumer) {
    Objects.requireNonNull(paramConsumer);
    int i = this.modCount;
    Object[] arrayOfObject = (Object[])this.elementData;
    int j = this.elementCount;
    for (byte b = 0; this.modCount == i && b < j; b++)
      paramConsumer.accept(arrayOfObject[b]); 
    if (this.modCount != i)
      throw new ConcurrentModificationException(); 
  }
  
  public boolean removeIf(Predicate<? super E> paramPredicate) {
    Objects.requireNonNull(paramPredicate);
    int i = 0;
    int j = this.elementCount;
    BitSet bitSet = new BitSet(j);
    int k = this.modCount;
    int m;
    for (m = 0; this.modCount == k && m < j; m++) {
      Object object = this.elementData[m];
      if (paramPredicate.test(object)) {
        bitSet.set(m);
        i++;
      } 
    } 
    if (this.modCount != k)
      throw new ConcurrentModificationException(); 
    m = (i > 0) ? 1 : 0;
    if (m != 0) {
      int n = j - i;
      int i1 = 0;
      for (byte b = 0; i1 < j && b < n; b++) {
        i1 = bitSet.nextClearBit(i1);
        this.elementData[b] = this.elementData[i1];
        i1++;
      } 
      for (i1 = n; i1 < j; i1++)
        this.elementData[i1] = null; 
      this.elementCount = n;
      if (this.modCount != k)
        throw new ConcurrentModificationException(); 
      this.modCount++;
    } 
    return m;
  }
  
  public void replaceAll(UnaryOperator<E> paramUnaryOperator) {
    Objects.requireNonNull(paramUnaryOperator);
    int i = this.modCount;
    int j = this.elementCount;
    for (byte b = 0; this.modCount == i && b < j; b++)
      this.elementData[b] = paramUnaryOperator.apply(this.elementData[b]); 
    if (this.modCount != i)
      throw new ConcurrentModificationException(); 
    this.modCount++;
  }
  
  public void sort(Comparator<? super E> paramComparator) {
    int i = this.modCount;
    Arrays.sort((Object[])this.elementData, 0, this.elementCount, paramComparator);
    if (this.modCount != i)
      throw new ConcurrentModificationException(); 
    this.modCount++;
  }
  
  public Spliterator<E> spliterator() { return new VectorSpliterator(this, null, 0, -1, 0); }
  
  private class Itr extends Object implements Iterator<E> {
    int cursor;
    
    int lastRet = -1;
    
    int expectedModCount = Vector.this.modCount;
    
    private Itr() {}
    
    public boolean hasNext() { return (this.cursor != Vector.this.elementCount); }
    
    public E next() {
      synchronized (Vector.this) {
        checkForComodification();
        int i = this.cursor;
        if (i >= Vector.this.elementCount)
          throw new NoSuchElementException(); 
        this.cursor = i + 1;
        return (E)Vector.this.elementData(this.lastRet = i);
      } 
    }
    
    public void remove() {
      if (this.lastRet == -1)
        throw new IllegalStateException(); 
      synchronized (Vector.this) {
        checkForComodification();
        Vector.this.remove(this.lastRet);
        this.expectedModCount = Vector.this.modCount;
      } 
      this.cursor = this.lastRet;
      this.lastRet = -1;
    }
    
    public void forEachRemaining(Consumer<? super E> param1Consumer) {
      Objects.requireNonNull(param1Consumer);
      synchronized (Vector.this) {
        int i = Vector.this.elementCount;
        int j = this.cursor;
        if (j >= i)
          return; 
        Object[] arrayOfObject = (Object[])Vector.this.elementData;
        if (j >= arrayOfObject.length)
          throw new ConcurrentModificationException(); 
        while (j != i && Vector.this.modCount == this.expectedModCount)
          param1Consumer.accept(arrayOfObject[j++]); 
        this.cursor = j;
        this.lastRet = j - 1;
        checkForComodification();
      } 
    }
    
    final void checkForComodification() {
      if (Vector.this.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
    }
  }
  
  final class ListItr extends Itr implements ListIterator<E> {
    ListItr(int param1Int) {
      super(Vector.this, null);
      this.cursor = param1Int;
    }
    
    public boolean hasPrevious() { return (this.cursor != 0); }
    
    public int nextIndex() { return this.cursor; }
    
    public int previousIndex() { return this.cursor - 1; }
    
    public E previous() {
      synchronized (Vector.this) {
        checkForComodification();
        int i = this.cursor - 1;
        if (i < 0)
          throw new NoSuchElementException(); 
        this.cursor = i;
        return (E)Vector.this.elementData(this.lastRet = i);
      } 
    }
    
    public void set(E param1E) {
      if (this.lastRet == -1)
        throw new IllegalStateException(); 
      synchronized (Vector.this) {
        checkForComodification();
        Vector.this.set(this.lastRet, param1E);
      } 
    }
    
    public void add(E param1E) {
      int i = this.cursor;
      synchronized (Vector.this) {
        checkForComodification();
        Vector.this.add(i, param1E);
        this.expectedModCount = Vector.this.modCount;
      } 
      this.cursor = i + 1;
      this.lastRet = -1;
    }
  }
  
  static final class VectorSpliterator<E> extends Object implements Spliterator<E> {
    private final Vector<E> list;
    
    private Object[] array;
    
    private int index;
    
    private int fence;
    
    private int expectedModCount;
    
    VectorSpliterator(Vector<E> param1Vector, Object[] param1ArrayOfObject, int param1Int1, int param1Int2, int param1Int3) {
      this.list = param1Vector;
      this.array = param1ArrayOfObject;
      this.index = param1Int1;
      this.fence = param1Int2;
      this.expectedModCount = param1Int3;
    }
    
    private int getFence() {
      int i;
      if ((i = this.fence) < 0)
        synchronized (this.list) {
          this.array = this.list.elementData;
          this.expectedModCount = this.list.modCount;
          i = this.fence = this.list.elementCount;
        }  
      return i;
    }
    
    public Spliterator<E> trySplit() {
      int i = getFence();
      int j = this.index;
      int k = j + i >>> 1;
      return (j >= k) ? null : new VectorSpliterator(this.list, this.array, j, this.index = k, this.expectedModCount);
    }
    
    public boolean tryAdvance(Consumer<? super E> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      int i;
      if (getFence() > (i = this.index)) {
        this.index = i + 1;
        param1Consumer.accept(this.array[i]);
        if (this.list.modCount != this.expectedModCount)
          throw new ConcurrentModificationException(); 
        return true;
      } 
      return false;
    }
    
    public void forEachRemaining(Consumer<? super E> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      Vector vector;
      if ((vector = this.list) != null) {
        Object[] arrayOfObject;
        int j;
        if ((j = this.fence) < 0) {
          synchronized (vector) {
            this.expectedModCount = vector.modCount;
            arrayOfObject = this.array = vector.elementData;
            j = this.fence = vector.elementCount;
          } 
        } else {
          arrayOfObject = this.array;
        } 
        int i;
        if (arrayOfObject != null && (i = this.index) >= 0 && (this.index = j) <= arrayOfObject.length) {
          while (i < j)
            param1Consumer.accept(arrayOfObject[i++]); 
          if (vector.modCount == this.expectedModCount)
            return; 
        } 
      } 
      throw new ConcurrentModificationException();
    }
    
    public long estimateSize() { return (getFence() - this.index); }
    
    public int characteristics() { return 16464; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\Vector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */