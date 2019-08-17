package com.sun.java.util.jar.pack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

final class FixedList<E> extends Object implements List<E> {
  private final ArrayList<E> flist;
  
  protected FixedList(int paramInt) {
    this.flist = new ArrayList(paramInt);
    for (byte b = 0; b < paramInt; b++)
      this.flist.add(null); 
  }
  
  public int size() { return this.flist.size(); }
  
  public boolean isEmpty() { return this.flist.isEmpty(); }
  
  public boolean contains(Object paramObject) { return this.flist.contains(paramObject); }
  
  public Iterator<E> iterator() { return this.flist.iterator(); }
  
  public Object[] toArray() { return this.flist.toArray(); }
  
  public <T> T[] toArray(T[] paramArrayOfT) { return (T[])this.flist.toArray(paramArrayOfT); }
  
  public boolean add(E paramE) throws UnsupportedOperationException { throw new UnsupportedOperationException("operation not permitted"); }
  
  public boolean remove(Object paramObject) { throw new UnsupportedOperationException("operation not permitted"); }
  
  public boolean containsAll(Collection<?> paramCollection) { return this.flist.containsAll(paramCollection); }
  
  public boolean addAll(Collection<? extends E> paramCollection) throws UnsupportedOperationException { throw new UnsupportedOperationException("operation not permitted"); }
  
  public boolean addAll(int paramInt, Collection<? extends E> paramCollection) throws UnsupportedOperationException { throw new UnsupportedOperationException("operation not permitted"); }
  
  public boolean removeAll(Collection<?> paramCollection) { throw new UnsupportedOperationException("operation not permitted"); }
  
  public boolean retainAll(Collection<?> paramCollection) { throw new UnsupportedOperationException("operation not permitted"); }
  
  public void clear() throws UnsupportedOperationException { throw new UnsupportedOperationException("operation not permitted"); }
  
  public E get(int paramInt) { return (E)this.flist.get(paramInt); }
  
  public E set(int paramInt, E paramE) { return (E)this.flist.set(paramInt, paramE); }
  
  public void add(int paramInt, E paramE) throws UnsupportedOperationException { throw new UnsupportedOperationException("operation not permitted"); }
  
  public E remove(int paramInt) { throw new UnsupportedOperationException("operation not permitted"); }
  
  public int indexOf(Object paramObject) { return this.flist.indexOf(paramObject); }
  
  public int lastIndexOf(Object paramObject) { return this.flist.lastIndexOf(paramObject); }
  
  public ListIterator<E> listIterator() { return this.flist.listIterator(); }
  
  public ListIterator<E> listIterator(int paramInt) { return this.flist.listIterator(paramInt); }
  
  public List<E> subList(int paramInt1, int paramInt2) { return this.flist.subList(paramInt1, paramInt2); }
  
  public String toString() { return "FixedList{plist=" + this.flist + '}'; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jav\\util\jar\pack\FixedList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */