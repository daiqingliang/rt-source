package java.util;

public abstract class AbstractList<E> extends AbstractCollection<E> implements List<E> {
  protected int modCount = 0;
  
  public boolean add(E paramE) {
    add(size(), paramE);
    return true;
  }
  
  public abstract E get(int paramInt);
  
  public E set(int paramInt, E paramE) { throw new UnsupportedOperationException(); }
  
  public void add(int paramInt, E paramE) { throw new UnsupportedOperationException(); }
  
  public E remove(int paramInt) { throw new UnsupportedOperationException(); }
  
  public int indexOf(Object paramObject) {
    ListIterator listIterator = listIterator();
    if (paramObject == null) {
      while (listIterator.hasNext()) {
        if (listIterator.next() == null)
          return listIterator.previousIndex(); 
      } 
    } else {
      while (listIterator.hasNext()) {
        if (paramObject.equals(listIterator.next()))
          return listIterator.previousIndex(); 
      } 
    } 
    return -1;
  }
  
  public int lastIndexOf(Object paramObject) {
    ListIterator listIterator = listIterator(size());
    if (paramObject == null) {
      while (listIterator.hasPrevious()) {
        if (listIterator.previous() == null)
          return listIterator.nextIndex(); 
      } 
    } else {
      while (listIterator.hasPrevious()) {
        if (paramObject.equals(listIterator.previous()))
          return listIterator.nextIndex(); 
      } 
    } 
    return -1;
  }
  
  public void clear() { removeRange(0, size()); }
  
  public boolean addAll(int paramInt, Collection<? extends E> paramCollection) {
    rangeCheckForAdd(paramInt);
    boolean bool = false;
    for (Object object : paramCollection) {
      add(paramInt++, object);
      bool = true;
    } 
    return bool;
  }
  
  public Iterator<E> iterator() { return new Itr(null); }
  
  public ListIterator<E> listIterator() { return listIterator(0); }
  
  public ListIterator<E> listIterator(int paramInt) {
    rangeCheckForAdd(paramInt);
    return new ListItr(paramInt);
  }
  
  public List<E> subList(int paramInt1, int paramInt2) { return (this instanceof RandomAccess) ? new RandomAccessSubList(this, paramInt1, paramInt2) : new SubList(this, paramInt1, paramInt2); }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof List))
      return false; 
    ListIterator listIterator1 = listIterator();
    ListIterator listIterator2 = ((List)paramObject).listIterator();
    while (listIterator1.hasNext() && listIterator2.hasNext()) {
      Object object1 = listIterator1.next();
      Object object2 = listIterator2.next();
      if ((object1 == null) ? (object2 == null) : object1.equals(object2))
        continue; 
      return false;
    } 
    return (!listIterator1.hasNext() && !listIterator2.hasNext());
  }
  
  public int hashCode() {
    byte b = 1;
    for (Object object : this)
      b = 31 * b + ((object == null) ? 0 : object.hashCode()); 
    return b;
  }
  
  protected void removeRange(int paramInt1, int paramInt2) {
    ListIterator listIterator = listIterator(paramInt1);
    byte b = 0;
    int i = paramInt2 - paramInt1;
    while (b < i) {
      listIterator.next();
      listIterator.remove();
      b++;
    } 
  }
  
  private void rangeCheckForAdd(int paramInt) {
    if (paramInt < 0 || paramInt > size())
      throw new IndexOutOfBoundsException(outOfBoundsMsg(paramInt)); 
  }
  
  private String outOfBoundsMsg(int paramInt) { return "Index: " + paramInt + ", Size: " + size(); }
  
  private class Itr extends Object implements Iterator<E> {
    int cursor = 0;
    
    int lastRet = -1;
    
    int expectedModCount = AbstractList.this.modCount;
    
    private Itr() {}
    
    public boolean hasNext() { return (this.cursor != AbstractList.this.size()); }
    
    public E next() {
      checkForComodification();
      try {
        int i = this.cursor;
        Object object = AbstractList.this.get(i);
        this.lastRet = i;
        this.cursor = i + 1;
        return (E)object;
      } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
        checkForComodification();
        throw new NoSuchElementException();
      } 
    }
    
    public void remove() {
      if (this.lastRet < 0)
        throw new IllegalStateException(); 
      checkForComodification();
      try {
        AbstractList.this.remove(this.lastRet);
        if (this.lastRet < this.cursor)
          this.cursor--; 
        this.lastRet = -1;
        this.expectedModCount = AbstractList.this.modCount;
      } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
        throw new ConcurrentModificationException();
      } 
    }
    
    final void checkForComodification() {
      if (AbstractList.this.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
    }
  }
  
  private class ListItr extends Itr implements ListIterator<E> {
    ListItr(int param1Int) {
      super(AbstractList.this, null);
      this.cursor = param1Int;
    }
    
    public boolean hasPrevious() { return (this.cursor != 0); }
    
    public E previous() {
      checkForComodification();
      try {
        int i = this.cursor - 1;
        Object object = AbstractList.this.get(i);
        this.lastRet = this.cursor = i;
        return (E)object;
      } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
        checkForComodification();
        throw new NoSuchElementException();
      } 
    }
    
    public int nextIndex() { return this.cursor; }
    
    public int previousIndex() { return this.cursor - 1; }
    
    public void set(E param1E) {
      if (this.lastRet < 0)
        throw new IllegalStateException(); 
      checkForComodification();
      try {
        AbstractList.this.set(this.lastRet, param1E);
        this.expectedModCount = AbstractList.this.modCount;
      } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
        throw new ConcurrentModificationException();
      } 
    }
    
    public void add(E param1E) {
      checkForComodification();
      try {
        int i = this.cursor;
        AbstractList.this.add(i, param1E);
        this.lastRet = -1;
        this.cursor = i + 1;
        this.expectedModCount = AbstractList.this.modCount;
      } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
        throw new ConcurrentModificationException();
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\AbstractList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */