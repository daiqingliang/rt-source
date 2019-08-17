package sun.awt.util;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

public class IdentityArrayList<E> extends AbstractList<E> implements List<E>, RandomAccess {
  private Object[] elementData;
  
  private int size;
  
  public IdentityArrayList(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("Illegal Capacity: " + paramInt); 
    this.elementData = new Object[paramInt];
  }
  
  public IdentityArrayList() { this(10); }
  
  public IdentityArrayList(Collection<? extends E> paramCollection) {
    this.elementData = paramCollection.toArray();
    this.size = this.elementData.length;
    if (this.elementData.getClass() != Object[].class)
      this.elementData = Arrays.copyOf(this.elementData, this.size, Object[].class); 
  }
  
  public void trimToSize() {
    this.modCount++;
    int i = this.elementData.length;
    if (this.size < i)
      this.elementData = Arrays.copyOf(this.elementData, this.size); 
  }
  
  public void ensureCapacity(int paramInt) {
    this.modCount++;
    int i = this.elementData.length;
    if (paramInt > i) {
      Object[] arrayOfObject = this.elementData;
      int j = i * 3 / 2 + 1;
      if (j < paramInt)
        j = paramInt; 
      this.elementData = Arrays.copyOf(this.elementData, j);
    } 
  }
  
  public int size() { return this.size; }
  
  public boolean isEmpty() { return (this.size == 0); }
  
  public boolean contains(Object paramObject) { return (indexOf(paramObject) >= 0); }
  
  public int indexOf(Object paramObject) {
    for (byte b = 0; b < this.size; b++) {
      if (paramObject == this.elementData[b])
        return b; 
    } 
    return -1;
  }
  
  public int lastIndexOf(Object paramObject) {
    for (int i = this.size - 1; i >= 0; i--) {
      if (paramObject == this.elementData[i])
        return i; 
    } 
    return -1;
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
  
  public E get(int paramInt) {
    rangeCheck(paramInt);
    return (E)this.elementData[paramInt];
  }
  
  public E set(int paramInt, E paramE) {
    rangeCheck(paramInt);
    Object object = this.elementData[paramInt];
    this.elementData[paramInt] = paramE;
    return (E)object;
  }
  
  public boolean add(E paramE) {
    ensureCapacity(this.size + 1);
    this.elementData[this.size++] = paramE;
    return true;
  }
  
  public void add(int paramInt, E paramE) {
    rangeCheckForAdd(paramInt);
    ensureCapacity(this.size + 1);
    System.arraycopy(this.elementData, paramInt, this.elementData, paramInt + 1, this.size - paramInt);
    this.elementData[paramInt] = paramE;
    this.size++;
  }
  
  public E remove(int paramInt) {
    rangeCheck(paramInt);
    this.modCount++;
    Object object = this.elementData[paramInt];
    int i = this.size - paramInt - 1;
    if (i > 0)
      System.arraycopy(this.elementData, paramInt + 1, this.elementData, paramInt, i); 
    this.elementData[--this.size] = null;
    return (E)object;
  }
  
  public boolean remove(Object paramObject) {
    for (byte b = 0; b < this.size; b++) {
      if (paramObject == this.elementData[b]) {
        fastRemove(b);
        return true;
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
    ensureCapacity(this.size + i);
    System.arraycopy(arrayOfObject, 0, this.elementData, this.size, i);
    this.size += i;
    return (i != 0);
  }
  
  public boolean addAll(int paramInt, Collection<? extends E> paramCollection) {
    rangeCheckForAdd(paramInt);
    Object[] arrayOfObject = paramCollection.toArray();
    int i = arrayOfObject.length;
    ensureCapacity(this.size + i);
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
    while (this.size != j)
      this.elementData[--this.size] = null; 
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
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\aw\\util\IdentityArrayList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */