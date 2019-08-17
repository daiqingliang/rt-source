package java.util;

class SubList<E> extends AbstractList<E> {
  private final AbstractList<E> l;
  
  private final int offset;
  
  private int size;
  
  SubList(AbstractList<E> paramAbstractList, int paramInt1, int paramInt2) {
    if (paramInt1 < 0)
      throw new IndexOutOfBoundsException("fromIndex = " + paramInt1); 
    if (paramInt2 > paramAbstractList.size())
      throw new IndexOutOfBoundsException("toIndex = " + paramInt2); 
    if (paramInt1 > paramInt2)
      throw new IllegalArgumentException("fromIndex(" + paramInt1 + ") > toIndex(" + paramInt2 + ")"); 
    this.l = paramAbstractList;
    this.offset = paramInt1;
    this.size = paramInt2 - paramInt1;
    this.modCount = this.l.modCount;
  }
  
  public E set(int paramInt, E paramE) {
    rangeCheck(paramInt);
    checkForComodification();
    return (E)this.l.set(paramInt + this.offset, paramE);
  }
  
  public E get(int paramInt) {
    rangeCheck(paramInt);
    checkForComodification();
    return (E)this.l.get(paramInt + this.offset);
  }
  
  public int size() {
    checkForComodification();
    return this.size;
  }
  
  public void add(int paramInt, E paramE) {
    rangeCheckForAdd(paramInt);
    checkForComodification();
    this.l.add(paramInt + this.offset, paramE);
    this.modCount = this.l.modCount;
    this.size++;
  }
  
  public E remove(int paramInt) {
    rangeCheck(paramInt);
    checkForComodification();
    Object object = this.l.remove(paramInt + this.offset);
    this.modCount = this.l.modCount;
    this.size--;
    return (E)object;
  }
  
  protected void removeRange(int paramInt1, int paramInt2) {
    checkForComodification();
    this.l.removeRange(paramInt1 + this.offset, paramInt2 + this.offset);
    this.modCount = this.l.modCount;
    this.size -= paramInt2 - paramInt1;
  }
  
  public boolean addAll(Collection<? extends E> paramCollection) { return addAll(this.size, paramCollection); }
  
  public boolean addAll(int paramInt, Collection<? extends E> paramCollection) {
    rangeCheckForAdd(paramInt);
    int i = paramCollection.size();
    if (i == 0)
      return false; 
    checkForComodification();
    this.l.addAll(this.offset + paramInt, paramCollection);
    this.modCount = this.l.modCount;
    this.size += i;
    return true;
  }
  
  public Iterator<E> iterator() { return listIterator(); }
  
  public ListIterator<E> listIterator(final int index) {
    checkForComodification();
    rangeCheckForAdd(paramInt);
    return new ListIterator<E>() {
        private final ListIterator<E> i = SubList.this.l.listIterator(index + SubList.this.offset);
        
        public boolean hasNext() { return (nextIndex() < SubList.this.size); }
        
        public E next() {
          if (hasNext())
            return (E)this.i.next(); 
          throw new NoSuchElementException();
        }
        
        public boolean hasPrevious() { return (previousIndex() >= 0); }
        
        public E previous() {
          if (hasPrevious())
            return (E)this.i.previous(); 
          throw new NoSuchElementException();
        }
        
        public int nextIndex() { return this.i.nextIndex() - SubList.this.offset; }
        
        public int previousIndex() { return this.i.previousIndex() - SubList.this.offset; }
        
        public void remove() {
          this.i.remove();
          SubList.this.modCount = this.this$0.l.modCount;
          SubList.this.size--;
        }
        
        public void set(E param1E) { this.i.set(param1E); }
        
        public void add(E param1E) {
          this.i.add(param1E);
          SubList.this.modCount = this.this$0.l.modCount;
          SubList.this.size++;
        }
      };
  }
  
  public List<E> subList(int paramInt1, int paramInt2) { return new SubList(this, paramInt1, paramInt2); }
  
  private void rangeCheck(int paramInt) {
    if (paramInt < 0 || paramInt >= this.size)
      throw new IndexOutOfBoundsException(outOfBoundsMsg(paramInt)); 
  }
  
  private void rangeCheckForAdd(int paramInt) {
    if (paramInt < 0 || paramInt > this.size)
      throw new IndexOutOfBoundsException(outOfBoundsMsg(paramInt)); 
  }
  
  private String outOfBoundsMsg(int paramInt) { return "Index: " + paramInt + ", Size: " + this.size; }
  
  private void checkForComodification() {
    if (this.modCount != this.l.modCount)
      throw new ConcurrentModificationException(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\SubList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */