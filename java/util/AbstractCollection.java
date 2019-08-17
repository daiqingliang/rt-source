package java.util;

import java.lang.reflect.Array;

public abstract class AbstractCollection<E> extends Object implements Collection<E> {
  private static final int MAX_ARRAY_SIZE = 2147483639;
  
  public abstract Iterator<E> iterator();
  
  public abstract int size();
  
  public boolean isEmpty() { return (size() == 0); }
  
  public boolean contains(Object paramObject) {
    Iterator iterator = iterator();
    if (paramObject == null) {
      while (iterator.hasNext()) {
        if (iterator.next() == null)
          return true; 
      } 
    } else {
      while (iterator.hasNext()) {
        if (paramObject.equals(iterator.next()))
          return true; 
      } 
    } 
    return false;
  }
  
  public Object[] toArray() {
    Object[] arrayOfObject = new Object[size()];
    Iterator iterator = iterator();
    for (byte b = 0; b < arrayOfObject.length; b++) {
      if (!iterator.hasNext())
        return Arrays.copyOf(arrayOfObject, b); 
      arrayOfObject[b] = iterator.next();
    } 
    return iterator.hasNext() ? finishToArray(arrayOfObject, iterator) : arrayOfObject;
  }
  
  public <T> T[] toArray(T[] paramArrayOfT) {
    int i = size();
    T[] arrayOfT = (paramArrayOfT.length >= i) ? paramArrayOfT : (Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), i);
    Iterator iterator = iterator();
    for (byte b = 0; b < arrayOfT.length; b++) {
      if (!iterator.hasNext()) {
        if (paramArrayOfT == arrayOfT) {
          arrayOfT[b] = null;
        } else {
          if (paramArrayOfT.length < b)
            return (T[])Arrays.copyOf(arrayOfT, b); 
          System.arraycopy(arrayOfT, 0, paramArrayOfT, 0, b);
          if (paramArrayOfT.length > b)
            paramArrayOfT[b] = null; 
        } 
        return paramArrayOfT;
      } 
      arrayOfT[b] = iterator.next();
    } 
    return (T[])(iterator.hasNext() ? finishToArray(arrayOfT, iterator) : arrayOfT);
  }
  
  private static <T> T[] finishToArray(T[] paramArrayOfT, Iterator<?> paramIterator) {
    int i = paramArrayOfT.length;
    while (paramIterator.hasNext()) {
      int j = paramArrayOfT.length;
      if (i == j) {
        int k = j + (j >> 1) + 1;
        if (k - 2147483639 > 0)
          k = hugeCapacity(j + 1); 
        paramArrayOfT = (T[])Arrays.copyOf(paramArrayOfT, k);
      } 
      paramArrayOfT[i++] = paramIterator.next();
    } 
    return (i == paramArrayOfT.length) ? paramArrayOfT : Arrays.copyOf(paramArrayOfT, i);
  }
  
  private static int hugeCapacity(int paramInt) {
    if (paramInt < 0)
      throw new OutOfMemoryError("Required array size too large"); 
    return (paramInt > 2147483639) ? Integer.MAX_VALUE : 2147483639;
  }
  
  public boolean add(E paramE) { throw new UnsupportedOperationException(); }
  
  public boolean remove(Object paramObject) {
    Iterator iterator = iterator();
    if (paramObject == null) {
      while (iterator.hasNext()) {
        if (iterator.next() == null) {
          iterator.remove();
          return true;
        } 
      } 
    } else {
      while (iterator.hasNext()) {
        if (paramObject.equals(iterator.next())) {
          iterator.remove();
          return true;
        } 
      } 
    } 
    return false;
  }
  
  public boolean containsAll(Collection<?> paramCollection) {
    for (Object object : paramCollection) {
      if (!contains(object))
        return false; 
    } 
    return true;
  }
  
  public boolean addAll(Collection<? extends E> paramCollection) {
    boolean bool = false;
    for (Object object : paramCollection) {
      if (add(object))
        bool = true; 
    } 
    return bool;
  }
  
  public boolean removeAll(Collection<?> paramCollection) {
    Objects.requireNonNull(paramCollection);
    boolean bool = false;
    Iterator iterator = iterator();
    while (iterator.hasNext()) {
      if (paramCollection.contains(iterator.next())) {
        iterator.remove();
        bool = true;
      } 
    } 
    return bool;
  }
  
  public boolean retainAll(Collection<?> paramCollection) {
    Objects.requireNonNull(paramCollection);
    boolean bool = false;
    Iterator iterator = iterator();
    while (iterator.hasNext()) {
      if (!paramCollection.contains(iterator.next())) {
        iterator.remove();
        bool = true;
      } 
    } 
    return bool;
  }
  
  public void clear() {
    Iterator iterator = iterator();
    while (iterator.hasNext()) {
      iterator.next();
      iterator.remove();
    } 
  }
  
  public String toString() {
    Iterator iterator = iterator();
    if (!iterator.hasNext())
      return "[]"; 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append('[');
    while (true) {
      Object object = iterator.next();
      stringBuilder.append((object == this) ? "(this Collection)" : object);
      if (!iterator.hasNext())
        return stringBuilder.append(']').toString(); 
      stringBuilder.append(',').append(' ');
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\AbstractCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */