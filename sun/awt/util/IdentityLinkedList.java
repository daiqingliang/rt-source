package sun.awt.util;

import java.lang.reflect.Array;
import java.util.AbstractSequentialList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class IdentityLinkedList<E> extends AbstractSequentialList<E> implements List<E>, Deque<E> {
  private Entry<E> header = new Entry(null, null, null);
  
  private int size = 0;
  
  public IdentityLinkedList() { this.header.next = this.header.previous = this.header; }
  
  public IdentityLinkedList(Collection<? extends E> paramCollection) {
    this();
    addAll(paramCollection);
  }
  
  public E getFirst() {
    if (this.size == 0)
      throw new NoSuchElementException(); 
    return (E)this.header.next.element;
  }
  
  public E getLast() {
    if (this.size == 0)
      throw new NoSuchElementException(); 
    return (E)this.header.previous.element;
  }
  
  public E removeFirst() { return (E)remove(this.header.next); }
  
  public E removeLast() { return (E)remove(this.header.previous); }
  
  public void addFirst(E paramE) { addBefore(paramE, this.header.next); }
  
  public void addLast(E paramE) { addBefore(paramE, this.header); }
  
  public boolean contains(Object paramObject) { return (indexOf(paramObject) != -1); }
  
  public int size() { return this.size; }
  
  public boolean add(E paramE) {
    addBefore(paramE, this.header);
    return true;
  }
  
  public boolean remove(Object paramObject) {
    for (Entry entry = this.header.next; entry != this.header; entry = entry.next) {
      if (paramObject == entry.element) {
        remove(entry);
        return true;
      } 
    } 
    return false;
  }
  
  public boolean addAll(Collection<? extends E> paramCollection) { return addAll(this.size, paramCollection); }
  
  public boolean addAll(int paramInt, Collection<? extends E> paramCollection) {
    if (paramInt < 0 || paramInt > this.size)
      throw new IndexOutOfBoundsException("Index: " + paramInt + ", Size: " + this.size); 
    Object[] arrayOfObject = paramCollection.toArray();
    int i = arrayOfObject.length;
    if (i == 0)
      return false; 
    this.modCount++;
    Entry entry1 = (paramInt == this.size) ? this.header : entry(paramInt);
    Entry entry2 = entry1.previous;
    for (byte b = 0; b < i; b++) {
      Entry entry = new Entry(arrayOfObject[b], entry1, entry2);
      entry2.next = entry;
      entry2 = entry;
    } 
    entry1.previous = entry2;
    this.size += i;
    return true;
  }
  
  public void clear() {
    for (Entry entry = this.header.next; entry != this.header; entry = entry1) {
      Entry entry1 = entry.next;
      entry.next = entry.previous = null;
      entry.element = null;
    } 
    this.header.next = this.header.previous = this.header;
    this.size = 0;
    this.modCount++;
  }
  
  public E get(int paramInt) { return (E)(entry(paramInt)).element; }
  
  public E set(int paramInt, E paramE) {
    Entry entry = entry(paramInt);
    Object object = entry.element;
    entry.element = paramE;
    return (E)object;
  }
  
  public void add(int paramInt, E paramE) { addBefore(paramE, (paramInt == this.size) ? this.header : entry(paramInt)); }
  
  public E remove(int paramInt) { return (E)remove(entry(paramInt)); }
  
  private Entry<E> entry(int paramInt) {
    if (paramInt < 0 || paramInt >= this.size)
      throw new IndexOutOfBoundsException("Index: " + paramInt + ", Size: " + this.size); 
    Entry entry = this.header;
    if (paramInt < this.size >> 1) {
      for (byte b = 0; b <= paramInt; b++)
        entry = entry.next; 
    } else {
      for (int i = this.size; i > paramInt; i--)
        entry = entry.previous; 
    } 
    return entry;
  }
  
  public int indexOf(Object paramObject) {
    byte b = 0;
    for (Entry entry = this.header.next; entry != this.header; entry = entry.next) {
      if (paramObject == entry.element)
        return b; 
      b++;
    } 
    return -1;
  }
  
  public int lastIndexOf(Object paramObject) {
    int i = this.size;
    for (Entry entry = this.header.previous; entry != this.header; entry = entry.previous) {
      i--;
      if (paramObject == entry.element)
        return i; 
    } 
    return -1;
  }
  
  public E peek() { return (this.size == 0) ? null : (E)getFirst(); }
  
  public E element() { return (E)getFirst(); }
  
  public E poll() { return (this.size == 0) ? null : (E)removeFirst(); }
  
  public E remove() { return (E)removeFirst(); }
  
  public boolean offer(E paramE) { return add(paramE); }
  
  public boolean offerFirst(E paramE) {
    addFirst(paramE);
    return true;
  }
  
  public boolean offerLast(E paramE) {
    addLast(paramE);
    return true;
  }
  
  public E peekFirst() { return (this.size == 0) ? null : (E)getFirst(); }
  
  public E peekLast() { return (this.size == 0) ? null : (E)getLast(); }
  
  public E pollFirst() { return (this.size == 0) ? null : (E)removeFirst(); }
  
  public E pollLast() { return (this.size == 0) ? null : (E)removeLast(); }
  
  public void push(E paramE) { addFirst(paramE); }
  
  public E pop() { return (E)removeFirst(); }
  
  public boolean removeFirstOccurrence(Object paramObject) { return remove(paramObject); }
  
  public boolean removeLastOccurrence(Object paramObject) {
    for (Entry entry = this.header.previous; entry != this.header; entry = entry.previous) {
      if (paramObject == entry.element) {
        remove(entry);
        return true;
      } 
    } 
    return false;
  }
  
  public ListIterator<E> listIterator(int paramInt) { return new ListItr(paramInt); }
  
  private Entry<E> addBefore(E paramE, Entry<E> paramEntry) {
    Entry entry = new Entry(paramE, paramEntry, paramEntry.previous);
    entry.previous.next = entry;
    entry.next.previous = entry;
    this.size++;
    this.modCount++;
    return entry;
  }
  
  private E remove(Entry<E> paramEntry) {
    if (paramEntry == this.header)
      throw new NoSuchElementException(); 
    Object object = paramEntry.element;
    paramEntry.previous.next = paramEntry.next;
    paramEntry.next.previous = paramEntry.previous;
    paramEntry.next = paramEntry.previous = null;
    paramEntry.element = null;
    this.size--;
    this.modCount++;
    return (E)object;
  }
  
  public Iterator<E> descendingIterator() { return new DescendingIterator(null); }
  
  public Object[] toArray() {
    Object[] arrayOfObject = new Object[this.size];
    byte b = 0;
    for (Entry entry = this.header.next; entry != this.header; entry = entry.next)
      arrayOfObject[b++] = entry.element; 
    return arrayOfObject;
  }
  
  public <T> T[] toArray(T[] paramArrayOfT) {
    if (paramArrayOfT.length < this.size)
      paramArrayOfT = (T[])(Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), this.size); 
    byte b = 0;
    T[] arrayOfT = paramArrayOfT;
    for (Entry entry = this.header.next; entry != this.header; entry = entry.next)
      arrayOfT[b++] = entry.element; 
    if (paramArrayOfT.length > this.size)
      paramArrayOfT[this.size] = null; 
    return paramArrayOfT;
  }
  
  private class DescendingIterator implements Iterator {
    final IdentityLinkedList<E>.ListItr itr = new IdentityLinkedList.ListItr(IdentityLinkedList.this, IdentityLinkedList.this.size());
    
    private DescendingIterator() {}
    
    public boolean hasNext() { return this.itr.hasPrevious(); }
    
    public E next() { return (E)this.itr.previous(); }
    
    public void remove() { this.itr.remove(); }
  }
  
  private static class Entry<E> extends Object {
    E element;
    
    Entry<E> next;
    
    Entry<E> previous;
    
    Entry(E param1E, Entry<E> param1Entry1, Entry<E> param1Entry2) {
      this.element = param1E;
      this.next = param1Entry1;
      this.previous = param1Entry2;
    }
  }
  
  private class ListItr extends Object implements ListIterator<E> {
    private IdentityLinkedList.Entry<E> lastReturned = IdentityLinkedList.this.header;
    
    private IdentityLinkedList.Entry<E> next;
    
    private int nextIndex;
    
    private int expectedModCount = IdentityLinkedList.this.modCount;
    
    ListItr(int param1Int) {
      if (param1Int < 0 || param1Int > this$0.size)
        throw new IndexOutOfBoundsException("Index: " + param1Int + ", Size: " + this$0.size); 
      if (param1Int < this$0.size >> 1) {
        this.next = this$0.header.next;
        this.nextIndex = 0;
        while (this.nextIndex < param1Int) {
          this.next = this.next.next;
          this.nextIndex++;
        } 
      } else {
        this.next = this$0.header;
        this.nextIndex = this$0.size;
        while (this.nextIndex > param1Int) {
          this.next = this.next.previous;
          this.nextIndex--;
        } 
      } 
    }
    
    public boolean hasNext() { return (this.nextIndex != IdentityLinkedList.this.size); }
    
    public E next() {
      checkForComodification();
      if (this.nextIndex == IdentityLinkedList.this.size)
        throw new NoSuchElementException(); 
      this.lastReturned = this.next;
      this.next = this.next.next;
      this.nextIndex++;
      return (E)this.lastReturned.element;
    }
    
    public boolean hasPrevious() { return (this.nextIndex != 0); }
    
    public E previous() {
      if (this.nextIndex == 0)
        throw new NoSuchElementException(); 
      this.lastReturned = this.next = this.next.previous;
      this.nextIndex--;
      checkForComodification();
      return (E)this.lastReturned.element;
    }
    
    public int nextIndex() { return this.nextIndex; }
    
    public int previousIndex() { return this.nextIndex - 1; }
    
    public void remove() {
      checkForComodification();
      IdentityLinkedList.Entry entry = this.lastReturned.next;
      try {
        IdentityLinkedList.this.remove(this.lastReturned);
      } catch (NoSuchElementException noSuchElementException) {
        throw new IllegalStateException();
      } 
      if (this.next == this.lastReturned) {
        this.next = entry;
      } else {
        this.nextIndex--;
      } 
      this.lastReturned = IdentityLinkedList.this.header;
      this.expectedModCount++;
    }
    
    public void set(E param1E) {
      if (this.lastReturned == IdentityLinkedList.this.header)
        throw new IllegalStateException(); 
      checkForComodification();
      this.lastReturned.element = param1E;
    }
    
    public void add(E param1E) {
      checkForComodification();
      this.lastReturned = IdentityLinkedList.this.header;
      IdentityLinkedList.this.addBefore(param1E, this.next);
      this.nextIndex++;
      this.expectedModCount++;
    }
    
    final void checkForComodification() {
      if (IdentityLinkedList.this.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\aw\\util\IdentityLinkedList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */