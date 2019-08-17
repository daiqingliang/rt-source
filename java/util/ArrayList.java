package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import sun.misc.SharedSecrets;

public class ArrayList<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, Serializable {
  private static final long serialVersionUID = 8683452581122892189L;
  
  private static final int DEFAULT_CAPACITY = 10;
  
  private static final Object[] EMPTY_ELEMENTDATA = new Object[0];
  
  private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = new Object[0];
  
  Object[] elementData;
  
  private int size;
  
  private static final int MAX_ARRAY_SIZE = 2147483639;
  
  public ArrayList(int paramInt) {
    if (paramInt > 0) {
      this.elementData = new Object[paramInt];
    } else if (paramInt == 0) {
      this.elementData = EMPTY_ELEMENTDATA;
    } else {
      throw new IllegalArgumentException("Illegal Capacity: " + paramInt);
    } 
  }
  
  public ArrayList() { this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA; }
  
  public ArrayList(Collection<? extends E> paramCollection) {
    this.elementData = paramCollection.toArray();
    if ((this.size = this.elementData.length) != 0) {
      if (this.elementData.getClass() != Object[].class)
        this.elementData = Arrays.copyOf(this.elementData, this.size, Object[].class); 
    } else {
      this.elementData = EMPTY_ELEMENTDATA;
    } 
  }
  
  public void trimToSize() {
    this.modCount++;
    if (this.size < this.elementData.length)
      this.elementData = (this.size == 0) ? EMPTY_ELEMENTDATA : Arrays.copyOf(this.elementData, this.size); 
  }
  
  public void ensureCapacity(int paramInt) {
    boolean bool = (this.elementData != DEFAULTCAPACITY_EMPTY_ELEMENTDATA) ? 0 : 10;
    if (paramInt > bool)
      ensureExplicitCapacity(paramInt); 
  }
  
  private static int calculateCapacity(Object[] paramArrayOfObject, int paramInt) { return (paramArrayOfObject == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) ? Math.max(10, paramInt) : paramInt; }
  
  private void ensureCapacityInternal(int paramInt) { ensureExplicitCapacity(calculateCapacity(this.elementData, paramInt)); }
  
  private void ensureExplicitCapacity(int paramInt) {
    this.modCount++;
    if (paramInt - this.elementData.length > 0)
      grow(paramInt); 
  }
  
  private void grow(int paramInt) {
    int i = this.elementData.length;
    int j = i + (i >> 1);
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
  
  public int size() { return this.size; }
  
  public boolean isEmpty() { return (this.size == 0); }
  
  public boolean contains(Object paramObject) { return (indexOf(paramObject) >= 0); }
  
  public int indexOf(Object paramObject) {
    if (paramObject == null) {
      for (byte b = 0; b < this.size; b++) {
        if (this.elementData[b] == null)
          return b; 
      } 
    } else {
      for (byte b = 0; b < this.size; b++) {
        if (paramObject.equals(this.elementData[b]))
          return b; 
      } 
    } 
    return -1;
  }
  
  public int lastIndexOf(Object paramObject) {
    if (paramObject == null) {
      for (int i = this.size - 1; i >= 0; i--) {
        if (this.elementData[i] == null)
          return i; 
      } 
    } else {
      for (int i = this.size - 1; i >= 0; i--) {
        if (paramObject.equals(this.elementData[i]))
          return i; 
      } 
    } 
    return -1;
  }
  
  public Object clone() {
    try {
      ArrayList arrayList = (ArrayList)super.clone();
      arrayList.elementData = Arrays.copyOf(this.elementData, this.size);
      arrayList.modCount = 0;
      return arrayList;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
  
  public Object[] toArray() { return Arrays.copyOf(this.elementData, this.size); }
  
  public <T> T[] toArray(T[] paramArrayOfT) {
    if (paramArrayOfT.length < this.size)
      return (T[])(Object[])Arrays.copyOf(this.elementData, this.size, paramArrayOfT.getClass()); 
    System.arraycopy(this.elementData, 0, paramArrayOfT, 0, this.size);
    if (paramArrayOfT.length > this.size)
      paramArrayOfT[this.size] = null; 
    return paramArrayOfT;
  }
  
  E elementData(int paramInt) { return (E)this.elementData[paramInt]; }
  
  public E get(int paramInt) {
    rangeCheck(paramInt);
    return (E)elementData(paramInt);
  }
  
  public E set(int paramInt, E paramE) {
    rangeCheck(paramInt);
    Object object = elementData(paramInt);
    this.elementData[paramInt] = paramE;
    return (E)object;
  }
  
  public boolean add(E paramE) {
    ensureCapacityInternal(this.size + 1);
    this.elementData[this.size++] = paramE;
    return true;
  }
  
  public void add(int paramInt, E paramE) {
    rangeCheckForAdd(paramInt);
    ensureCapacityInternal(this.size + 1);
    System.arraycopy(this.elementData, paramInt, this.elementData, paramInt + 1, this.size - paramInt);
    this.elementData[paramInt] = paramE;
    this.size++;
  }
  
  public E remove(int paramInt) {
    rangeCheck(paramInt);
    this.modCount++;
    Object object = elementData(paramInt);
    int i = this.size - paramInt - 1;
    if (i > 0)
      System.arraycopy(this.elementData, paramInt + 1, this.elementData, paramInt, i); 
    this.elementData[--this.size] = null;
    return (E)object;
  }
  
  public boolean remove(Object paramObject) {
    if (paramObject == null) {
      for (byte b = 0; b < this.size; b++) {
        if (this.elementData[b] == null) {
          fastRemove(b);
          return true;
        } 
      } 
    } else {
      for (byte b = 0; b < this.size; b++) {
        if (paramObject.equals(this.elementData[b])) {
          fastRemove(b);
          return true;
        } 
      } 
    } 
    return false;
  }
  
  private void fastRemove(int paramInt) {
    this.modCount++;
    int i = this.size - paramInt - 1;
    if (i > 0)
      System.arraycopy(this.elementData, paramInt + 1, this.elementData, paramInt, i); 
    this.elementData[--this.size] = null;
  }
  
  public void clear() {
    this.modCount++;
    for (byte b = 0; b < this.size; b++)
      this.elementData[b] = null; 
    this.size = 0;
  }
  
  public boolean addAll(Collection<? extends E> paramCollection) {
    Object[] arrayOfObject = paramCollection.toArray();
    int i = arrayOfObject.length;
    ensureCapacityInternal(this.size + i);
    System.arraycopy(arrayOfObject, 0, this.elementData, this.size, i);
    this.size += i;
    return (i != 0);
  }
  
  public boolean addAll(int paramInt, Collection<? extends E> paramCollection) {
    rangeCheckForAdd(paramInt);
    Object[] arrayOfObject = paramCollection.toArray();
    int i = arrayOfObject.length;
    ensureCapacityInternal(this.size + i);
    int j = this.size - paramInt;
    if (j > 0)
      System.arraycopy(this.elementData, paramInt, this.elementData, paramInt + i, j); 
    System.arraycopy(arrayOfObject, 0, this.elementData, paramInt, i);
    this.size += i;
    return (i != 0);
  }
  
  protected void removeRange(int paramInt1, int paramInt2) {
    this.modCount++;
    int i = this.size - paramInt2;
    System.arraycopy(this.elementData, paramInt2, this.elementData, paramInt1, i);
    int j = this.size - paramInt2 - paramInt1;
    for (int k = j; k < this.size; k++)
      this.elementData[k] = null; 
    this.size = j;
  }
  
  private void rangeCheck(int paramInt) {
    if (paramInt >= this.size)
      throw new IndexOutOfBoundsException(outOfBoundsMsg(paramInt)); 
  }
  
  private void rangeCheckForAdd(int paramInt) {
    if (paramInt > this.size || paramInt < 0)
      throw new IndexOutOfBoundsException(outOfBoundsMsg(paramInt)); 
  }
  
  private String outOfBoundsMsg(int paramInt) { return "Index: " + paramInt + ", Size: " + this.size; }
  
  public boolean removeAll(Collection<?> paramCollection) {
    Objects.requireNonNull(paramCollection);
    return batchRemove(paramCollection, false);
  }
  
  public boolean retainAll(Collection<?> paramCollection) {
    Objects.requireNonNull(paramCollection);
    return batchRemove(paramCollection, true);
  }
  
  private boolean batchRemove(Collection<?> paramCollection, boolean paramBoolean) {
    arrayOfObject = this.elementData;
    i = 0;
    j = 0;
    bool = false;
    try {
      while (i < this.size) {
        if (paramCollection.contains(arrayOfObject[i]) == paramBoolean)
          arrayOfObject[j++] = arrayOfObject[i]; 
        i++;
      } 
    } finally {
      if (i != this.size) {
        System.arraycopy(arrayOfObject, i, arrayOfObject, j, this.size - i);
        j += this.size - i;
      } 
      if (j != this.size) {
        for (int k = j; k < this.size; k++)
          arrayOfObject[k] = null; 
        this.modCount += this.size - j;
        this.size = j;
        bool = true;
      } 
    } 
    return bool;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    int i = this.modCount;
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(this.size);
    for (byte b = 0; b < this.size; b++)
      paramObjectOutputStream.writeObject(this.elementData[b]); 
    if (this.modCount != i)
      throw new ConcurrentModificationException(); 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    this.elementData = EMPTY_ELEMENTDATA;
    paramObjectInputStream.defaultReadObject();
    paramObjectInputStream.readInt();
    if (this.size > 0) {
      int i = calculateCapacity(this.elementData, this.size);
      SharedSecrets.getJavaOISAccess().checkArray(paramObjectInputStream, Object[].class, i);
      ensureCapacityInternal(this.size);
      Object[] arrayOfObject = this.elementData;
      for (byte b = 0; b < this.size; b++)
        arrayOfObject[b] = paramObjectInputStream.readObject(); 
    } 
  }
  
  public ListIterator<E> listIterator(int paramInt) {
    if (paramInt < 0 || paramInt > this.size)
      throw new IndexOutOfBoundsException("Index: " + paramInt); 
    return new ListItr(paramInt);
  }
  
  public ListIterator<E> listIterator() { return new ListItr(0); }
  
  public Iterator<E> iterator() { return new Itr(); }
  
  public List<E> subList(int paramInt1, int paramInt2) {
    subListRangeCheck(paramInt1, paramInt2, this.size);
    return new SubList(this, 0, paramInt1, paramInt2);
  }
  
  static void subListRangeCheck(int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt1 < 0)
      throw new IndexOutOfBoundsException("fromIndex = " + paramInt1); 
    if (paramInt2 > paramInt3)
      throw new IndexOutOfBoundsException("toIndex = " + paramInt2); 
    if (paramInt1 > paramInt2)
      throw new IllegalArgumentException("fromIndex(" + paramInt1 + ") > toIndex(" + paramInt2 + ")"); 
  }
  
  public void forEach(Consumer<? super E> paramConsumer) {
    Objects.requireNonNull(paramConsumer);
    int i = this.modCount;
    Object[] arrayOfObject = (Object[])this.elementData;
    int j = this.size;
    for (byte b = 0; this.modCount == i && b < j; b++)
      paramConsumer.accept(arrayOfObject[b]); 
    if (this.modCount != i)
      throw new ConcurrentModificationException(); 
  }
  
  public Spliterator<E> spliterator() { return new ArrayListSpliterator(this, 0, -1, 0); }
  
  public boolean removeIf(Predicate<? super E> paramPredicate) {
    Objects.requireNonNull(paramPredicate);
    int i = 0;
    BitSet bitSet = new BitSet(this.size);
    int j = this.modCount;
    int k = this.size;
    int m;
    for (m = 0; this.modCount == j && m < k; m++) {
      Object object = this.elementData[m];
      if (paramPredicate.test(object)) {
        bitSet.set(m);
        i++;
      } 
    } 
    if (this.modCount != j)
      throw new ConcurrentModificationException(); 
    m = (i > 0) ? 1 : 0;
    if (m != 0) {
      int n = k - i;
      int i1 = 0;
      for (byte b = 0; i1 < k && b < n; b++) {
        i1 = bitSet.nextClearBit(i1);
        this.elementData[b] = this.elementData[i1];
        i1++;
      } 
      for (i1 = n; i1 < k; i1++)
        this.elementData[i1] = null; 
      this.size = n;
      if (this.modCount != j)
        throw new ConcurrentModificationException(); 
      this.modCount++;
    } 
    return m;
  }
  
  public void replaceAll(UnaryOperator<E> paramUnaryOperator) {
    Objects.requireNonNull(paramUnaryOperator);
    int i = this.modCount;
    int j = this.size;
    for (byte b = 0; this.modCount == i && b < j; b++)
      this.elementData[b] = paramUnaryOperator.apply(this.elementData[b]); 
    if (this.modCount != i)
      throw new ConcurrentModificationException(); 
    this.modCount++;
  }
  
  public void sort(Comparator<? super E> paramComparator) {
    int i = this.modCount;
    Arrays.sort((Object[])this.elementData, 0, this.size, paramComparator);
    if (this.modCount != i)
      throw new ConcurrentModificationException(); 
    this.modCount++;
  }
  
  static final class ArrayListSpliterator<E> extends Object implements Spliterator<E> {
    private final ArrayList<E> list;
    
    private int index;
    
    private int fence;
    
    private int expectedModCount;
    
    ArrayListSpliterator(ArrayList<E> param1ArrayList, int param1Int1, int param1Int2, int param1Int3) {
      this.list = param1ArrayList;
      this.index = param1Int1;
      this.fence = param1Int2;
      this.expectedModCount = param1Int3;
    }
    
    private int getFence() {
      int i;
      if ((i = this.fence) < 0) {
        ArrayList arrayList;
        if ((arrayList = this.list) == null) {
          i = this.fence = 0;
        } else {
          this.expectedModCount = arrayList.modCount;
          i = this.fence = arrayList.size;
        } 
      } 
      return i;
    }
    
    public ArrayListSpliterator<E> trySplit() {
      int i = getFence();
      int j = this.index;
      int k = j + i >>> 1;
      return (j >= k) ? null : new ArrayListSpliterator(this.list, j, this.index = k, this.expectedModCount);
    }
    
    public boolean tryAdvance(Consumer<? super E> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      int i = getFence();
      int j = this.index;
      if (j < i) {
        this.index = j + 1;
        Object object = this.list.elementData[j];
        param1Consumer.accept(object);
        if (this.list.modCount != this.expectedModCount)
          throw new ConcurrentModificationException(); 
        return true;
      } 
      return false;
    }
    
    public void forEachRemaining(Consumer<? super E> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      ArrayList arrayList;
      Object[] arrayOfObject;
      if ((arrayList = this.list) != null && (arrayOfObject = arrayList.elementData) != null) {
        int k;
        int j;
        if ((j = this.fence) < 0) {
          k = arrayList.modCount;
          j = arrayList.size;
        } else {
          k = this.expectedModCount;
        } 
        int i;
        if ((i = this.index) >= 0 && (this.index = j) <= arrayOfObject.length) {
          while (i < j) {
            Object object = arrayOfObject[i];
            param1Consumer.accept(object);
            i++;
          } 
          if (arrayList.modCount == k)
            return; 
        } 
      } 
      throw new ConcurrentModificationException();
    }
    
    public long estimateSize() { return (getFence() - this.index); }
    
    public int characteristics() { return 16464; }
  }
  
  private class Itr extends Object implements Iterator<E> {
    int cursor;
    
    int lastRet = -1;
    
    int expectedModCount = ArrayList.this.modCount;
    
    public boolean hasNext() { return (this.cursor != ArrayList.this.size); }
    
    public E next() {
      checkForComodification();
      int i = this.cursor;
      if (i >= ArrayList.this.size)
        throw new NoSuchElementException(); 
      Object[] arrayOfObject = ArrayList.this.elementData;
      if (i >= arrayOfObject.length)
        throw new ConcurrentModificationException(); 
      this.cursor = i + 1;
      return (E)arrayOfObject[this.lastRet = i];
    }
    
    public void remove() {
      if (this.lastRet < 0)
        throw new IllegalStateException(); 
      checkForComodification();
      try {
        ArrayList.this.remove(this.lastRet);
        this.cursor = this.lastRet;
        this.lastRet = -1;
        this.expectedModCount = ArrayList.this.modCount;
      } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
        throw new ConcurrentModificationException();
      } 
    }
    
    public void forEachRemaining(Consumer<? super E> param1Consumer) {
      Objects.requireNonNull(param1Consumer);
      int i = ArrayList.this.size;
      int j = this.cursor;
      if (j >= i)
        return; 
      Object[] arrayOfObject = ArrayList.this.elementData;
      if (j >= arrayOfObject.length)
        throw new ConcurrentModificationException(); 
      while (j != i && ArrayList.this.modCount == this.expectedModCount)
        param1Consumer.accept(arrayOfObject[j++]); 
      this.cursor = j;
      this.lastRet = j - 1;
      checkForComodification();
    }
    
    final void checkForComodification() {
      if (ArrayList.this.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
    }
  }
  
  private class ListItr extends Itr implements ListIterator<E> {
    ListItr(int param1Int) {
      super(ArrayList.this);
      this.cursor = param1Int;
    }
    
    public boolean hasPrevious() { return (this.cursor != 0); }
    
    public int nextIndex() { return this.cursor; }
    
    public int previousIndex() { return this.cursor - 1; }
    
    public E previous() {
      checkForComodification();
      int i = this.cursor - 1;
      if (i < 0)
        throw new NoSuchElementException(); 
      Object[] arrayOfObject = ArrayList.this.elementData;
      if (i >= arrayOfObject.length)
        throw new ConcurrentModificationException(); 
      this.cursor = i;
      return (E)arrayOfObject[this.lastRet = i];
    }
    
    public void set(E param1E) {
      if (this.lastRet < 0)
        throw new IllegalStateException(); 
      checkForComodification();
      try {
        ArrayList.this.set(this.lastRet, param1E);
      } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
        throw new ConcurrentModificationException();
      } 
    }
    
    public void add(E param1E) {
      checkForComodification();
      try {
        int i = this.cursor;
        ArrayList.this.add(i, param1E);
        this.cursor = i + 1;
        this.lastRet = -1;
        this.expectedModCount = ArrayList.this.modCount;
      } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
        throw new ConcurrentModificationException();
      } 
    }
  }
  
  private class SubList extends AbstractList<E> implements RandomAccess {
    private final AbstractList<E> parent;
    
    private final int parentOffset;
    
    private final int offset;
    
    int size;
    
    SubList(AbstractList<E> param1AbstractList, int param1Int1, int param1Int2, int param1Int3) {
      this.parent = param1AbstractList;
      this.parentOffset = param1Int2;
      this.offset = param1Int1 + param1Int2;
      this.size = param1Int3 - param1Int2;
      this.modCount = ArrayList.this.modCount;
    }
    
    public E set(int param1Int, E param1E) {
      rangeCheck(param1Int);
      checkForComodification();
      Object object = ArrayList.this.elementData(this.offset + param1Int);
      ArrayList.this.elementData[this.offset + param1Int] = param1E;
      return (E)object;
    }
    
    public E get(int param1Int) {
      rangeCheck(param1Int);
      checkForComodification();
      return (E)ArrayList.this.elementData(this.offset + param1Int);
    }
    
    public int size() {
      checkForComodification();
      return this.size;
    }
    
    public void add(int param1Int, E param1E) {
      rangeCheckForAdd(param1Int);
      checkForComodification();
      this.parent.add(this.parentOffset + param1Int, param1E);
      this.modCount = this.parent.modCount;
      this.size++;
    }
    
    public E remove(int param1Int) {
      rangeCheck(param1Int);
      checkForComodification();
      Object object = this.parent.remove(this.parentOffset + param1Int);
      this.modCount = this.parent.modCount;
      this.size--;
      return (E)object;
    }
    
    protected void removeRange(int param1Int1, int param1Int2) {
      checkForComodification();
      this.parent.removeRange(this.parentOffset + param1Int1, this.parentOffset + param1Int2);
      this.modCount = this.parent.modCount;
      this.size -= param1Int2 - param1Int1;
    }
    
    public boolean addAll(Collection<? extends E> param1Collection) { return addAll(this.size, param1Collection); }
    
    public boolean addAll(int param1Int, Collection<? extends E> param1Collection) {
      rangeCheckForAdd(param1Int);
      int i = param1Collection.size();
      if (i == 0)
        return false; 
      checkForComodification();
      this.parent.addAll(this.parentOffset + param1Int, param1Collection);
      this.modCount = this.parent.modCount;
      this.size += i;
      return true;
    }
    
    public Iterator<E> iterator() { return listIterator(); }
    
    public ListIterator<E> listIterator(final int index) {
      checkForComodification();
      rangeCheckForAdd(param1Int);
      final int offset = this.offset;
      return new ListIterator<E>() {
          int cursor = index;
          
          int lastRet = -1;
          
          int expectedModCount = this.this$1.this$0.modCount;
          
          public boolean hasNext() { return (this.cursor != ArrayList.SubList.this.size); }
          
          public E next() {
            checkForComodification();
            int i = this.cursor;
            if (i >= ArrayList.SubList.this.size)
              throw new NoSuchElementException(); 
            Object[] arrayOfObject = ArrayList.this.elementData;
            if (offset + i >= arrayOfObject.length)
              throw new ConcurrentModificationException(); 
            this.cursor = i + 1;
            return (E)arrayOfObject[offset + (this.lastRet = i)];
          }
          
          public boolean hasPrevious() { return (this.cursor != 0); }
          
          public E previous() {
            checkForComodification();
            int i = this.cursor - 1;
            if (i < 0)
              throw new NoSuchElementException(); 
            Object[] arrayOfObject = ArrayList.this.elementData;
            if (offset + i >= arrayOfObject.length)
              throw new ConcurrentModificationException(); 
            this.cursor = i;
            return (E)arrayOfObject[offset + (this.lastRet = i)];
          }
          
          public void forEachRemaining(Consumer<? super E> param2Consumer) {
            Objects.requireNonNull(param2Consumer);
            int i = ArrayList.SubList.this.size;
            int j = this.cursor;
            if (j >= i)
              return; 
            Object[] arrayOfObject = ArrayList.this.elementData;
            if (offset + j >= arrayOfObject.length)
              throw new ConcurrentModificationException(); 
            while (j != i && ArrayList.SubList.this.modCount == this.expectedModCount)
              param2Consumer.accept(arrayOfObject[offset + j++]); 
            this.lastRet = this.cursor = j;
            checkForComodification();
          }
          
          public int nextIndex() { return this.cursor; }
          
          public int previousIndex() { return this.cursor - 1; }
          
          public void remove() {
            if (this.lastRet < 0)
              throw new IllegalStateException(); 
            checkForComodification();
            try {
              ArrayList.SubList.this.remove(this.lastRet);
              this.cursor = this.lastRet;
              this.lastRet = -1;
              this.expectedModCount = ArrayList.this.modCount;
            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
              throw new ConcurrentModificationException();
            } 
          }
          
          public void set(E param2E) {
            if (this.lastRet < 0)
              throw new IllegalStateException(); 
            checkForComodification();
            try {
              ArrayList.SubList.this.this$0.set(offset + this.lastRet, param2E);
            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
              throw new ConcurrentModificationException();
            } 
          }
          
          public void add(E param2E) {
            checkForComodification();
            try {
              int i = this.cursor;
              ArrayList.SubList.this.add(i, param2E);
              this.cursor = i + 1;
              this.lastRet = -1;
              this.expectedModCount = ArrayList.this.modCount;
            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
              throw new ConcurrentModificationException();
            } 
          }
          
          final void checkForComodification() {
            if (this.expectedModCount != ArrayList.this.modCount)
              throw new ConcurrentModificationException(); 
          }
        };
    }
    
    public List<E> subList(int param1Int1, int param1Int2) {
      ArrayList.subListRangeCheck(param1Int1, param1Int2, this.size);
      return new SubList(this, this.offset, param1Int1, param1Int2);
    }
    
    private void rangeCheck(int param1Int) {
      if (param1Int < 0 || param1Int >= this.size)
        throw new IndexOutOfBoundsException(outOfBoundsMsg(param1Int)); 
    }
    
    private void rangeCheckForAdd(int param1Int) {
      if (param1Int < 0 || param1Int > this.size)
        throw new IndexOutOfBoundsException(outOfBoundsMsg(param1Int)); 
    }
    
    private String outOfBoundsMsg(int param1Int) { return "Index: " + param1Int + ", Size: " + this.size; }
    
    private void checkForComodification() {
      if (ArrayList.this.modCount != this.modCount)
        throw new ConcurrentModificationException(); 
    }
    
    public Spliterator<E> spliterator() {
      checkForComodification();
      return new ArrayList.ArrayListSpliterator(ArrayList.this, this.offset, this.offset + this.size, this.modCount);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\ArrayList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */