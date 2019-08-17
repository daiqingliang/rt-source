package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import sun.misc.SharedSecrets;
import sun.misc.Unsafe;

public class CopyOnWriteArrayList<E> extends Object implements List<E>, RandomAccess, Cloneable, Serializable {
  private static final long serialVersionUID = 8673264195747942595L;
  
  final ReentrantLock lock = new ReentrantLock();
  
  private static final Unsafe UNSAFE;
  
  private static final long lockOffset;
  
  final Object[] getArray() { return this.array; }
  
  final void setArray(Object[] paramArrayOfObject) { this.array = paramArrayOfObject; }
  
  public CopyOnWriteArrayList() { setArray(new Object[0]); }
  
  public CopyOnWriteArrayList(Collection<? extends E> paramCollection) {
    if (paramCollection.getClass() == CopyOnWriteArrayList.class) {
      arrayOfObject = ((CopyOnWriteArrayList)paramCollection).getArray();
    } else {
      arrayOfObject = paramCollection.toArray();
      if (arrayOfObject.getClass() != Object[].class)
        arrayOfObject = Arrays.copyOf(arrayOfObject, arrayOfObject.length, Object[].class); 
    } 
    setArray(arrayOfObject);
  }
  
  public CopyOnWriteArrayList(E[] paramArrayOfE) { setArray(Arrays.copyOf(paramArrayOfE, paramArrayOfE.length, Object[].class)); }
  
  public int size() { return getArray().length; }
  
  public boolean isEmpty() { return (size() == 0); }
  
  private static boolean eq(Object paramObject1, Object paramObject2) { return (paramObject1 == null) ? ((paramObject2 == null)) : paramObject1.equals(paramObject2); }
  
  private static int indexOf(Object paramObject, Object[] paramArrayOfObject, int paramInt1, int paramInt2) {
    if (paramObject == null) {
      for (int i = paramInt1; i < paramInt2; i++) {
        if (paramArrayOfObject[i] == null)
          return i; 
      } 
    } else {
      for (int i = paramInt1; i < paramInt2; i++) {
        if (paramObject.equals(paramArrayOfObject[i]))
          return i; 
      } 
    } 
    return -1;
  }
  
  private static int lastIndexOf(Object paramObject, Object[] paramArrayOfObject, int paramInt) {
    if (paramObject == null) {
      for (int i = paramInt; i >= 0; i--) {
        if (paramArrayOfObject[i] == null)
          return i; 
      } 
    } else {
      for (int i = paramInt; i >= 0; i--) {
        if (paramObject.equals(paramArrayOfObject[i]))
          return i; 
      } 
    } 
    return -1;
  }
  
  public boolean contains(Object paramObject) {
    Object[] arrayOfObject = getArray();
    return (indexOf(paramObject, arrayOfObject, 0, arrayOfObject.length) >= 0);
  }
  
  public int indexOf(Object paramObject) {
    Object[] arrayOfObject = getArray();
    return indexOf(paramObject, arrayOfObject, 0, arrayOfObject.length);
  }
  
  public int indexOf(E paramE, int paramInt) {
    Object[] arrayOfObject = getArray();
    return indexOf(paramE, arrayOfObject, paramInt, arrayOfObject.length);
  }
  
  public int lastIndexOf(Object paramObject) {
    Object[] arrayOfObject = getArray();
    return lastIndexOf(paramObject, arrayOfObject, arrayOfObject.length - 1);
  }
  
  public int lastIndexOf(E paramE, int paramInt) {
    Object[] arrayOfObject = getArray();
    return lastIndexOf(paramE, arrayOfObject, paramInt);
  }
  
  public Object clone() {
    try {
      CopyOnWriteArrayList copyOnWriteArrayList = (CopyOnWriteArrayList)super.clone();
      copyOnWriteArrayList.resetLock();
      return copyOnWriteArrayList;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError();
    } 
  }
  
  public Object[] toArray() {
    Object[] arrayOfObject = getArray();
    return Arrays.copyOf(arrayOfObject, arrayOfObject.length);
  }
  
  public <T> T[] toArray(T[] paramArrayOfT) {
    Object[] arrayOfObject = getArray();
    int i = arrayOfObject.length;
    if (paramArrayOfT.length < i)
      return (T[])(Object[])Arrays.copyOf(arrayOfObject, i, paramArrayOfT.getClass()); 
    System.arraycopy(arrayOfObject, 0, paramArrayOfT, 0, i);
    if (paramArrayOfT.length > i)
      paramArrayOfT[i] = null; 
    return paramArrayOfT;
  }
  
  private E get(Object[] paramArrayOfObject, int paramInt) { return (E)paramArrayOfObject[paramInt]; }
  
  public E get(int paramInt) { return (E)get(getArray(), paramInt); }
  
  public E set(int paramInt, E paramE) {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject = getArray();
      Object object1 = get(arrayOfObject, paramInt);
      if (object1 != paramE) {
        int i = arrayOfObject.length;
        Object[] arrayOfObject1 = Arrays.copyOf(arrayOfObject, i);
        arrayOfObject1[paramInt] = paramE;
        setArray(arrayOfObject1);
      } else {
        setArray(arrayOfObject);
      } 
      object2 = object1;
      return (E)object2;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public boolean add(E paramE) {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject1 = getArray();
      int i = arrayOfObject1.length;
      Object[] arrayOfObject2 = Arrays.copyOf(arrayOfObject1, i + 1);
      arrayOfObject2[i] = paramE;
      setArray(arrayOfObject2);
      return true;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public void add(int paramInt, E paramE) {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject2;
      Object[] arrayOfObject1 = getArray();
      int i = arrayOfObject1.length;
      if (paramInt > i || paramInt < 0)
        throw new IndexOutOfBoundsException("Index: " + paramInt + ", Size: " + i); 
      int j = i - paramInt;
      if (j == 0) {
        arrayOfObject2 = Arrays.copyOf(arrayOfObject1, i + 1);
      } else {
        arrayOfObject2 = new Object[i + 1];
        System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, paramInt);
        System.arraycopy(arrayOfObject1, paramInt, arrayOfObject2, paramInt + 1, j);
      } 
      arrayOfObject2[paramInt] = paramE;
      setArray(arrayOfObject2);
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public E remove(int paramInt) {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject = getArray();
      int i = arrayOfObject.length;
      Object object1 = get(arrayOfObject, paramInt);
      int j = i - paramInt - 1;
      if (j == 0) {
        setArray(Arrays.copyOf(arrayOfObject, i - 1));
      } else {
        Object[] arrayOfObject1 = new Object[i - 1];
        System.arraycopy(arrayOfObject, 0, arrayOfObject1, 0, paramInt);
        System.arraycopy(arrayOfObject, paramInt + 1, arrayOfObject1, paramInt, j);
        setArray(arrayOfObject1);
      } 
      object2 = object1;
      return (E)object2;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public boolean remove(Object paramObject) {
    Object[] arrayOfObject = getArray();
    int i = indexOf(paramObject, arrayOfObject, 0, arrayOfObject.length);
    return (i < 0) ? false : remove(paramObject, arrayOfObject, i);
  }
  
  private boolean remove(Object paramObject, Object[] paramArrayOfObject, int paramInt) {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject1 = getArray();
      int i = arrayOfObject1.length;
      if (paramArrayOfObject != arrayOfObject1) {
        int j = Math.min(paramInt, i);
        for (null = 0; null < j; null++) {
          if (arrayOfObject1[null] != paramArrayOfObject[null] && eq(paramObject, arrayOfObject1[null])) {
            paramInt = null;
            // Byte code: goto -> 135
          } 
        } 
        if (paramInt >= i)
          return false; 
        if (arrayOfObject1[paramInt] != paramObject) {
          paramInt = indexOf(paramObject, arrayOfObject1, paramInt, i);
          if (paramInt < 0)
            return false; 
        } 
      } 
      Object[] arrayOfObject2 = new Object[i - 1];
      System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, paramInt);
      System.arraycopy(arrayOfObject1, paramInt + 1, arrayOfObject2, paramInt, i - paramInt - 1);
      setArray(arrayOfObject2);
      return true;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  void removeRange(int paramInt1, int paramInt2) {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject = getArray();
      int i = arrayOfObject.length;
      if (paramInt1 < 0 || paramInt2 > i || paramInt2 < paramInt1)
        throw new IndexOutOfBoundsException(); 
      int j = i - paramInt2 - paramInt1;
      int k = i - paramInt2;
      if (k == 0) {
        setArray(Arrays.copyOf(arrayOfObject, j));
      } else {
        Object[] arrayOfObject1 = new Object[j];
        System.arraycopy(arrayOfObject, 0, arrayOfObject1, 0, paramInt1);
        System.arraycopy(arrayOfObject, paramInt2, arrayOfObject1, paramInt1, k);
        setArray(arrayOfObject1);
      } 
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public boolean addIfAbsent(E paramE) {
    Object[] arrayOfObject = getArray();
    return (indexOf(paramE, arrayOfObject, 0, arrayOfObject.length) >= 0) ? false : addIfAbsent(paramE, arrayOfObject);
  }
  
  private boolean addIfAbsent(E paramE, Object[] paramArrayOfObject) {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject1 = getArray();
      int i = arrayOfObject1.length;
      if (paramArrayOfObject != arrayOfObject1) {
        int j = Math.min(paramArrayOfObject.length, i);
        for (null = 0; null < j; null++) {
          if (arrayOfObject1[null] != paramArrayOfObject[null] && eq(paramE, arrayOfObject1[null]))
            return false; 
        } 
        if (indexOf(paramE, arrayOfObject1, j, i) >= 0)
          return false; 
      } 
      Object[] arrayOfObject2 = Arrays.copyOf(arrayOfObject1, i + 1);
      arrayOfObject2[i] = paramE;
      setArray(arrayOfObject2);
      return true;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public boolean containsAll(Collection<?> paramCollection) {
    Object[] arrayOfObject = getArray();
    int i = arrayOfObject.length;
    for (Object object : paramCollection) {
      if (indexOf(object, arrayOfObject, 0, i) < 0)
        return false; 
    } 
    return true;
  }
  
  public boolean removeAll(Collection<?> paramCollection) {
    if (paramCollection == null)
      throw new NullPointerException(); 
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject = getArray();
      int i = arrayOfObject.length;
      if (i != 0) {
        byte b = 0;
        Object[] arrayOfObject1 = new Object[i];
        for (null = 0; null < i; null++) {
          Object object = arrayOfObject[null];
          if (!paramCollection.contains(object))
            arrayOfObject1[b++] = object; 
        } 
        if (b != i) {
          setArray(Arrays.copyOf(arrayOfObject1, b));
          return true;
        } 
      } 
      return false;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public boolean retainAll(Collection<?> paramCollection) {
    if (paramCollection == null)
      throw new NullPointerException(); 
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject = getArray();
      int i = arrayOfObject.length;
      if (i != 0) {
        byte b = 0;
        Object[] arrayOfObject1 = new Object[i];
        for (null = 0; null < i; null++) {
          Object object = arrayOfObject[null];
          if (paramCollection.contains(object))
            arrayOfObject1[b++] = object; 
        } 
        if (b != i) {
          setArray(Arrays.copyOf(arrayOfObject1, b));
          return true;
        } 
      } 
      return false;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public int addAllAbsent(Collection<? extends E> paramCollection) {
    Object[] arrayOfObject = paramCollection.toArray();
    if (arrayOfObject.length == 0)
      return 0; 
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject1 = getArray();
      int i = arrayOfObject1.length;
      int j = 0;
      for (null = 0; null < arrayOfObject.length; null++) {
        Object object = arrayOfObject[null];
        if (indexOf(object, arrayOfObject1, 0, i) < 0 && indexOf(object, arrayOfObject, 0, j) < 0)
          arrayOfObject[j++] = object; 
      } 
      if (j > 0) {
        Object[] arrayOfObject2 = Arrays.copyOf(arrayOfObject1, i + j);
        System.arraycopy(arrayOfObject, 0, arrayOfObject2, i, j);
        setArray(arrayOfObject2);
      } 
      return j;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public void clear() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      setArray(new Object[0]);
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public boolean addAll(Collection<? extends E> paramCollection) {
    Object[] arrayOfObject = (paramCollection.getClass() == CopyOnWriteArrayList.class) ? ((CopyOnWriteArrayList)paramCollection).getArray() : paramCollection.toArray();
    if (arrayOfObject.length == 0)
      return false; 
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject1 = getArray();
      int i = arrayOfObject1.length;
      if (i == 0 && arrayOfObject.getClass() == Object[].class) {
        setArray(arrayOfObject);
      } else {
        Object[] arrayOfObject2 = Arrays.copyOf(arrayOfObject1, i + arrayOfObject.length);
        System.arraycopy(arrayOfObject, 0, arrayOfObject2, i, arrayOfObject.length);
        setArray(arrayOfObject2);
      } 
      return true;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public boolean addAll(int paramInt, Collection<? extends E> paramCollection) {
    Object[] arrayOfObject = paramCollection.toArray();
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject2;
      Object[] arrayOfObject1 = getArray();
      int i = arrayOfObject1.length;
      if (paramInt > i || paramInt < 0)
        throw new IndexOutOfBoundsException("Index: " + paramInt + ", Size: " + i); 
      if (arrayOfObject.length == 0)
        return false; 
      int j = i - paramInt;
      if (j == 0) {
        arrayOfObject2 = Arrays.copyOf(arrayOfObject1, i + arrayOfObject.length);
      } else {
        arrayOfObject2 = new Object[i + arrayOfObject.length];
        System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, paramInt);
        System.arraycopy(arrayOfObject1, paramInt, arrayOfObject2, paramInt + arrayOfObject.length, j);
      } 
      System.arraycopy(arrayOfObject, 0, arrayOfObject2, paramInt, arrayOfObject.length);
      setArray(arrayOfObject2);
      return true;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public void forEach(Consumer<? super E> paramConsumer) {
    if (paramConsumer == null)
      throw new NullPointerException(); 
    for (Object object : getArray())
      paramConsumer.accept(object); 
  }
  
  public boolean removeIf(Predicate<? super E> paramPredicate) {
    if (paramPredicate == null)
      throw new NullPointerException(); 
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject = getArray();
      int i = arrayOfObject.length;
      if (i != 0) {
        byte b = 0;
        Object[] arrayOfObject1 = new Object[i];
        for (null = 0; null < i; null++) {
          Object object = arrayOfObject[null];
          if (!paramPredicate.test(object))
            arrayOfObject1[b++] = object; 
        } 
        if (b != i) {
          setArray(Arrays.copyOf(arrayOfObject1, b));
          return true;
        } 
      } 
      return false;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public void replaceAll(UnaryOperator<E> paramUnaryOperator) {
    if (paramUnaryOperator == null)
      throw new NullPointerException(); 
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject1 = getArray();
      int i = arrayOfObject1.length;
      Object[] arrayOfObject2 = Arrays.copyOf(arrayOfObject1, i);
      for (byte b = 0; b < i; b++) {
        Object object = arrayOfObject1[b];
        arrayOfObject2[b] = paramUnaryOperator.apply(object);
      } 
      setArray(arrayOfObject2);
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public void sort(Comparator<? super E> paramComparator) {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject1 = getArray();
      Object[] arrayOfObject2 = Arrays.copyOf(arrayOfObject1, arrayOfObject1.length);
      Object[] arrayOfObject3 = (Object[])arrayOfObject2;
      Arrays.sort(arrayOfObject3, paramComparator);
      setArray(arrayOfObject2);
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    Object[] arrayOfObject = getArray();
    paramObjectOutputStream.writeInt(arrayOfObject.length);
    for (Object object : arrayOfObject)
      paramObjectOutputStream.writeObject(object); 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    resetLock();
    int i = paramObjectInputStream.readInt();
    SharedSecrets.getJavaOISAccess().checkArray(paramObjectInputStream, Object[].class, i);
    Object[] arrayOfObject = new Object[i];
    for (byte b = 0; b < i; b++)
      arrayOfObject[b] = paramObjectInputStream.readObject(); 
    setArray(arrayOfObject);
  }
  
  public String toString() { return Arrays.toString(getArray()); }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof List))
      return false; 
    List list = (List)paramObject;
    Iterator iterator = list.iterator();
    Object[] arrayOfObject = getArray();
    int i = arrayOfObject.length;
    for (byte b = 0; b < i; b++) {
      if (!iterator.hasNext() || !eq(arrayOfObject[b], iterator.next()))
        return false; 
    } 
    return !iterator.hasNext();
  }
  
  public int hashCode() {
    byte b = 1;
    for (Object object : getArray())
      b = 31 * b + ((object == null) ? 0 : object.hashCode()); 
    return b;
  }
  
  public Iterator<E> iterator() { return new COWIterator(getArray(), 0, null); }
  
  public ListIterator<E> listIterator() { return new COWIterator(getArray(), 0, null); }
  
  public ListIterator<E> listIterator(int paramInt) {
    Object[] arrayOfObject = getArray();
    int i = arrayOfObject.length;
    if (paramInt < 0 || paramInt > i)
      throw new IndexOutOfBoundsException("Index: " + paramInt); 
    return new COWIterator(arrayOfObject, paramInt, null);
  }
  
  public Spliterator<E> spliterator() { return Spliterators.spliterator(getArray(), 1040); }
  
  public List<E> subList(int paramInt1, int paramInt2) {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject = getArray();
      int i = arrayOfObject.length;
      if (paramInt1 < 0 || paramInt2 > i || paramInt1 > paramInt2)
        throw new IndexOutOfBoundsException(); 
      return new COWSubList(this, paramInt1, paramInt2);
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  private void resetLock() { UNSAFE.putObjectVolatile(this, lockOffset, new ReentrantLock()); }
  
  static  {
    try {
      UNSAFE = Unsafe.getUnsafe();
      Class clazz = CopyOnWriteArrayList.class;
      lockOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("lock"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
  
  static final class COWIterator<E> extends Object implements ListIterator<E> {
    private final Object[] snapshot;
    
    private int cursor;
    
    private COWIterator(Object[] param1ArrayOfObject, int param1Int) {
      this.cursor = param1Int;
      this.snapshot = param1ArrayOfObject;
    }
    
    public boolean hasNext() { return (this.cursor < this.snapshot.length); }
    
    public boolean hasPrevious() { return (this.cursor > 0); }
    
    public E next() {
      if (!hasNext())
        throw new NoSuchElementException(); 
      return (E)this.snapshot[this.cursor++];
    }
    
    public E previous() {
      if (!hasPrevious())
        throw new NoSuchElementException(); 
      return (E)this.snapshot[--this.cursor];
    }
    
    public int nextIndex() { return this.cursor; }
    
    public int previousIndex() { return this.cursor - 1; }
    
    public void remove() { throw new UnsupportedOperationException(); }
    
    public void set(E param1E) { throw new UnsupportedOperationException(); }
    
    public void add(E param1E) { throw new UnsupportedOperationException(); }
    
    public void forEachRemaining(Consumer<? super E> param1Consumer) {
      Objects.requireNonNull(param1Consumer);
      Object[] arrayOfObject = this.snapshot;
      int i = arrayOfObject.length;
      for (int j = this.cursor; j < i; j++) {
        Object object = arrayOfObject[j];
        param1Consumer.accept(object);
      } 
      this.cursor = i;
    }
  }
  
  private static class COWSubList<E> extends AbstractList<E> implements RandomAccess {
    private final CopyOnWriteArrayList<E> l;
    
    private final int offset;
    
    private int size;
    
    private Object[] expectedArray;
    
    COWSubList(CopyOnWriteArrayList<E> param1CopyOnWriteArrayList, int param1Int1, int param1Int2) {
      this.l = param1CopyOnWriteArrayList;
      this.expectedArray = this.l.getArray();
      this.offset = param1Int1;
      this.size = param1Int2 - param1Int1;
    }
    
    private void checkForComodification() {
      if (this.l.getArray() != this.expectedArray)
        throw new ConcurrentModificationException(); 
    }
    
    private void rangeCheck(int param1Int) {
      if (param1Int < 0 || param1Int >= this.size)
        throw new IndexOutOfBoundsException("Index: " + param1Int + ",Size: " + this.size); 
    }
    
    public E set(int param1Int, E param1E) {
      reentrantLock = this.l.lock;
      reentrantLock.lock();
      try {
        rangeCheck(param1Int);
        checkForComodification();
        Object object1 = this.l.set(param1Int + this.offset, param1E);
        this.expectedArray = this.l.getArray();
        object2 = object1;
        return (E)object2;
      } finally {
        reentrantLock.unlock();
      } 
    }
    
    public E get(int param1Int) {
      reentrantLock = this.l.lock;
      reentrantLock.lock();
      try {
        rangeCheck(param1Int);
        checkForComodification();
        object = this.l.get(param1Int + this.offset);
        return (E)object;
      } finally {
        reentrantLock.unlock();
      } 
    }
    
    public int size() {
      reentrantLock = this.l.lock;
      reentrantLock.lock();
      try {
        checkForComodification();
        return this.size;
      } finally {
        reentrantLock.unlock();
      } 
    }
    
    public void add(int param1Int, E param1E) {
      reentrantLock = this.l.lock;
      reentrantLock.lock();
      try {
        checkForComodification();
        if (param1Int < 0 || param1Int > this.size)
          throw new IndexOutOfBoundsException(); 
        this.l.add(param1Int + this.offset, param1E);
        this.expectedArray = this.l.getArray();
        this.size++;
      } finally {
        reentrantLock.unlock();
      } 
    }
    
    public void clear() {
      reentrantLock = this.l.lock;
      reentrantLock.lock();
      try {
        checkForComodification();
        this.l.removeRange(this.offset, this.offset + this.size);
        this.expectedArray = this.l.getArray();
        this.size = 0;
      } finally {
        reentrantLock.unlock();
      } 
    }
    
    public E remove(int param1Int) {
      reentrantLock = this.l.lock;
      reentrantLock.lock();
      try {
        rangeCheck(param1Int);
        checkForComodification();
        Object object1 = this.l.remove(param1Int + this.offset);
        this.expectedArray = this.l.getArray();
        this.size--;
        object2 = object1;
        return (E)object2;
      } finally {
        reentrantLock.unlock();
      } 
    }
    
    public boolean remove(Object param1Object) {
      int i = indexOf(param1Object);
      if (i == -1)
        return false; 
      remove(i);
      return true;
    }
    
    public Iterator<E> iterator() {
      reentrantLock = this.l.lock;
      reentrantLock.lock();
      try {
        checkForComodification();
        return new CopyOnWriteArrayList.COWSubListIterator(this.l, 0, this.offset, this.size);
      } finally {
        reentrantLock.unlock();
      } 
    }
    
    public ListIterator<E> listIterator(int param1Int) {
      reentrantLock = this.l.lock;
      reentrantLock.lock();
      try {
        checkForComodification();
        if (param1Int < 0 || param1Int > this.size)
          throw new IndexOutOfBoundsException("Index: " + param1Int + ", Size: " + this.size); 
        return new CopyOnWriteArrayList.COWSubListIterator(this.l, param1Int, this.offset, this.size);
      } finally {
        reentrantLock.unlock();
      } 
    }
    
    public List<E> subList(int param1Int1, int param1Int2) {
      reentrantLock = this.l.lock;
      reentrantLock.lock();
      try {
        checkForComodification();
        if (param1Int1 < 0 || param1Int2 > this.size || param1Int1 > param1Int2)
          throw new IndexOutOfBoundsException(); 
        return new COWSubList(this.l, param1Int1 + this.offset, param1Int2 + this.offset);
      } finally {
        reentrantLock.unlock();
      } 
    }
    
    public void forEach(Consumer<? super E> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      int i = this.offset;
      int j = this.offset + this.size;
      Object[] arrayOfObject = this.expectedArray;
      if (this.l.getArray() != arrayOfObject)
        throw new ConcurrentModificationException(); 
      if (i < 0 || j > arrayOfObject.length)
        throw new IndexOutOfBoundsException(); 
      for (int k = i; k < j; k++) {
        Object object = arrayOfObject[k];
        param1Consumer.accept(object);
      } 
    }
    
    public void replaceAll(UnaryOperator<E> param1UnaryOperator) {
      if (param1UnaryOperator == null)
        throw new NullPointerException(); 
      reentrantLock = this.l.lock;
      reentrantLock.lock();
      try {
        int i = this.offset;
        int j = this.offset + this.size;
        Object[] arrayOfObject1 = this.expectedArray;
        if (this.l.getArray() != arrayOfObject1)
          throw new ConcurrentModificationException(); 
        int k = arrayOfObject1.length;
        if (i < 0 || j > k)
          throw new IndexOutOfBoundsException(); 
        Object[] arrayOfObject2 = Arrays.copyOf(arrayOfObject1, k);
        for (int m = i; m < j; m++) {
          Object object = arrayOfObject1[m];
          arrayOfObject2[m] = param1UnaryOperator.apply(object);
        } 
        this.l.setArray(this.expectedArray = arrayOfObject2);
      } finally {
        reentrantLock.unlock();
      } 
    }
    
    public void sort(Comparator<? super E> param1Comparator) {
      reentrantLock = this.l.lock;
      reentrantLock.lock();
      try {
        int i = this.offset;
        int j = this.offset + this.size;
        Object[] arrayOfObject1 = this.expectedArray;
        if (this.l.getArray() != arrayOfObject1)
          throw new ConcurrentModificationException(); 
        int k = arrayOfObject1.length;
        if (i < 0 || j > k)
          throw new IndexOutOfBoundsException(); 
        Object[] arrayOfObject2 = Arrays.copyOf(arrayOfObject1, k);
        Object[] arrayOfObject3 = (Object[])arrayOfObject2;
        Arrays.sort(arrayOfObject3, i, j, param1Comparator);
        this.l.setArray(this.expectedArray = arrayOfObject2);
      } finally {
        reentrantLock.unlock();
      } 
    }
    
    public boolean removeAll(Collection<?> param1Collection) {
      if (param1Collection == null)
        throw new NullPointerException(); 
      boolean bool = false;
      reentrantLock = this.l.lock;
      reentrantLock.lock();
      try {
        int i = this.size;
        if (i > 0) {
          int j = this.offset;
          int k = this.offset + i;
          Object[] arrayOfObject1 = this.expectedArray;
          if (this.l.getArray() != arrayOfObject1)
            throw new ConcurrentModificationException(); 
          int m = arrayOfObject1.length;
          if (j < 0 || k > m)
            throw new IndexOutOfBoundsException(); 
          int n = 0;
          Object[] arrayOfObject2 = new Object[i];
          for (int i1 = j; i1 < k; i1++) {
            Object object = arrayOfObject1[i1];
            if (!param1Collection.contains(object))
              arrayOfObject2[n++] = object; 
          } 
          if (n != i) {
            Object[] arrayOfObject = new Object[m - i + n];
            System.arraycopy(arrayOfObject1, 0, arrayOfObject, 0, j);
            System.arraycopy(arrayOfObject2, 0, arrayOfObject, j, n);
            System.arraycopy(arrayOfObject1, k, arrayOfObject, j + n, m - k);
            this.size = n;
            bool = true;
            this.l.setArray(this.expectedArray = arrayOfObject);
          } 
        } 
      } finally {
        reentrantLock.unlock();
      } 
      return bool;
    }
    
    public boolean retainAll(Collection<?> param1Collection) {
      if (param1Collection == null)
        throw new NullPointerException(); 
      boolean bool = false;
      reentrantLock = this.l.lock;
      reentrantLock.lock();
      try {
        int i = this.size;
        if (i > 0) {
          int j = this.offset;
          int k = this.offset + i;
          Object[] arrayOfObject1 = this.expectedArray;
          if (this.l.getArray() != arrayOfObject1)
            throw new ConcurrentModificationException(); 
          int m = arrayOfObject1.length;
          if (j < 0 || k > m)
            throw new IndexOutOfBoundsException(); 
          int n = 0;
          Object[] arrayOfObject2 = new Object[i];
          for (int i1 = j; i1 < k; i1++) {
            Object object = arrayOfObject1[i1];
            if (param1Collection.contains(object))
              arrayOfObject2[n++] = object; 
          } 
          if (n != i) {
            Object[] arrayOfObject = new Object[m - i + n];
            System.arraycopy(arrayOfObject1, 0, arrayOfObject, 0, j);
            System.arraycopy(arrayOfObject2, 0, arrayOfObject, j, n);
            System.arraycopy(arrayOfObject1, k, arrayOfObject, j + n, m - k);
            this.size = n;
            bool = true;
            this.l.setArray(this.expectedArray = arrayOfObject);
          } 
        } 
      } finally {
        reentrantLock.unlock();
      } 
      return bool;
    }
    
    public boolean removeIf(Predicate<? super E> param1Predicate) {
      if (param1Predicate == null)
        throw new NullPointerException(); 
      boolean bool = false;
      reentrantLock = this.l.lock;
      reentrantLock.lock();
      try {
        int i = this.size;
        if (i > 0) {
          int j = this.offset;
          int k = this.offset + i;
          Object[] arrayOfObject1 = this.expectedArray;
          if (this.l.getArray() != arrayOfObject1)
            throw new ConcurrentModificationException(); 
          int m = arrayOfObject1.length;
          if (j < 0 || k > m)
            throw new IndexOutOfBoundsException(); 
          int n = 0;
          Object[] arrayOfObject2 = new Object[i];
          for (int i1 = j; i1 < k; i1++) {
            Object object = arrayOfObject1[i1];
            if (!param1Predicate.test(object))
              arrayOfObject2[n++] = object; 
          } 
          if (n != i) {
            Object[] arrayOfObject = new Object[m - i + n];
            System.arraycopy(arrayOfObject1, 0, arrayOfObject, 0, j);
            System.arraycopy(arrayOfObject2, 0, arrayOfObject, j, n);
            System.arraycopy(arrayOfObject1, k, arrayOfObject, j + n, m - k);
            this.size = n;
            bool = true;
            this.l.setArray(this.expectedArray = arrayOfObject);
          } 
        } 
      } finally {
        reentrantLock.unlock();
      } 
      return bool;
    }
    
    public Spliterator<E> spliterator() {
      int i = this.offset;
      int j = this.offset + this.size;
      Object[] arrayOfObject = this.expectedArray;
      if (this.l.getArray() != arrayOfObject)
        throw new ConcurrentModificationException(); 
      if (i < 0 || j > arrayOfObject.length)
        throw new IndexOutOfBoundsException(); 
      return Spliterators.spliterator(arrayOfObject, i, j, 1040);
    }
  }
  
  private static class COWSubListIterator<E> extends Object implements ListIterator<E> {
    private final ListIterator<E> it;
    
    private final int offset;
    
    private final int size;
    
    COWSubListIterator(List<E> param1List, int param1Int1, int param1Int2, int param1Int3) {
      this.offset = param1Int2;
      this.size = param1Int3;
      this.it = param1List.listIterator(param1Int1 + param1Int2);
    }
    
    public boolean hasNext() { return (nextIndex() < this.size); }
    
    public E next() {
      if (hasNext())
        return (E)this.it.next(); 
      throw new NoSuchElementException();
    }
    
    public boolean hasPrevious() { return (previousIndex() >= 0); }
    
    public E previous() {
      if (hasPrevious())
        return (E)this.it.previous(); 
      throw new NoSuchElementException();
    }
    
    public int nextIndex() { return this.it.nextIndex() - this.offset; }
    
    public int previousIndex() { return this.it.previousIndex() - this.offset; }
    
    public void remove() { throw new UnsupportedOperationException(); }
    
    public void set(E param1E) { throw new UnsupportedOperationException(); }
    
    public void add(E param1E) { throw new UnsupportedOperationException(); }
    
    public void forEachRemaining(Consumer<? super E> param1Consumer) {
      Objects.requireNonNull(param1Consumer);
      int i = this.size;
      ListIterator listIterator = this.it;
      while (nextIndex() < i)
        param1Consumer.accept(listIterator.next()); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\CopyOnWriteArrayList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */