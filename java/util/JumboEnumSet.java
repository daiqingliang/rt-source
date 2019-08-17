package java.util;

class JumboEnumSet<E extends Enum<E>> extends EnumSet<E> {
  private static final long serialVersionUID = 334349849919042784L;
  
  private long[] elements;
  
  private int size = 0;
  
  JumboEnumSet(Class<E> paramClass, Enum<?>[] paramArrayOfEnum) {
    super(paramClass, paramArrayOfEnum);
    this.elements = new long[paramArrayOfEnum.length + 63 >>> 6];
  }
  
  void addRange(E paramE1, E paramE2) {
    int i = paramE1.ordinal() >>> 6;
    int j = paramE2.ordinal() >>> 6;
    if (i == j) {
      this.elements[i] = -1L >>> paramE1.ordinal() - paramE2.ordinal() - 1 << paramE1.ordinal();
    } else {
      this.elements[i] = -1L << paramE1.ordinal();
      for (int k = i + 1; k < j; k++)
        this.elements[k] = -1L; 
      this.elements[j] = -1L >>> 63 - paramE2.ordinal();
    } 
    this.size = paramE2.ordinal() - paramE1.ordinal() + 1;
  }
  
  void addAll() {
    for (byte b = 0; b < this.elements.length; b++)
      this.elements[b] = -1L; 
    this.elements[this.elements.length - 1] = this.elements[this.elements.length - 1] >>> -this.universe.length;
    this.size = this.universe.length;
  }
  
  void complement() {
    for (byte b = 0; b < this.elements.length; b++)
      this.elements[b] = this.elements[b] ^ 0xFFFFFFFFFFFFFFFFL; 
    this.elements[this.elements.length - 1] = this.elements[this.elements.length - 1] & -1L >>> -this.universe.length;
    this.size = this.universe.length - this.size;
  }
  
  public Iterator<E> iterator() { return new EnumSetIterator(); }
  
  public int size() { return this.size; }
  
  public boolean isEmpty() { return (this.size == 0); }
  
  public boolean contains(Object paramObject) {
    if (paramObject == null)
      return false; 
    Class clazz = paramObject.getClass();
    if (clazz != this.elementType && clazz.getSuperclass() != this.elementType)
      return false; 
    int i = ((Enum)paramObject).ordinal();
    return ((this.elements[i >>> 6] & 1L << i) != 0L);
  }
  
  public boolean add(E paramE) {
    typeCheck(paramE);
    int i = paramE.ordinal();
    int j = i >>> 6;
    long l = this.elements[j];
    this.elements[j] = this.elements[j] | 1L << i;
    boolean bool = (this.elements[j] != l);
    if (bool)
      this.size++; 
    return bool;
  }
  
  public boolean remove(Object paramObject) {
    if (paramObject == null)
      return false; 
    Class clazz = paramObject.getClass();
    if (clazz != this.elementType && clazz.getSuperclass() != this.elementType)
      return false; 
    int i = ((Enum)paramObject).ordinal();
    int j = i >>> 6;
    long l = this.elements[j];
    this.elements[j] = this.elements[j] & (1L << i ^ 0xFFFFFFFFFFFFFFFFL);
    boolean bool = (this.elements[j] != l);
    if (bool)
      this.size--; 
    return bool;
  }
  
  public boolean containsAll(Collection<?> paramCollection) {
    if (!(paramCollection instanceof JumboEnumSet))
      return super.containsAll(paramCollection); 
    JumboEnumSet jumboEnumSet = (JumboEnumSet)paramCollection;
    if (jumboEnumSet.elementType != this.elementType)
      return jumboEnumSet.isEmpty(); 
    for (byte b = 0; b < this.elements.length; b++) {
      if ((jumboEnumSet.elements[b] & (this.elements[b] ^ 0xFFFFFFFFFFFFFFFFL)) != 0L)
        return false; 
    } 
    return true;
  }
  
  public boolean addAll(Collection<? extends E> paramCollection) {
    if (!(paramCollection instanceof JumboEnumSet))
      return super.addAll(paramCollection); 
    JumboEnumSet jumboEnumSet = (JumboEnumSet)paramCollection;
    if (jumboEnumSet.elementType != this.elementType) {
      if (jumboEnumSet.isEmpty())
        return false; 
      throw new ClassCastException(jumboEnumSet.elementType + " != " + this.elementType);
    } 
    for (byte b = 0; b < this.elements.length; b++)
      this.elements[b] = this.elements[b] | jumboEnumSet.elements[b]; 
    return recalculateSize();
  }
  
  public boolean removeAll(Collection<?> paramCollection) {
    if (!(paramCollection instanceof JumboEnumSet))
      return super.removeAll(paramCollection); 
    JumboEnumSet jumboEnumSet = (JumboEnumSet)paramCollection;
    if (jumboEnumSet.elementType != this.elementType)
      return false; 
    for (byte b = 0; b < this.elements.length; b++)
      this.elements[b] = this.elements[b] & (jumboEnumSet.elements[b] ^ 0xFFFFFFFFFFFFFFFFL); 
    return recalculateSize();
  }
  
  public boolean retainAll(Collection<?> paramCollection) {
    if (!(paramCollection instanceof JumboEnumSet))
      return super.retainAll(paramCollection); 
    JumboEnumSet jumboEnumSet = (JumboEnumSet)paramCollection;
    if (jumboEnumSet.elementType != this.elementType) {
      boolean bool = (this.size != 0);
      clear();
      return bool;
    } 
    for (byte b = 0; b < this.elements.length; b++)
      this.elements[b] = this.elements[b] & jumboEnumSet.elements[b]; 
    return recalculateSize();
  }
  
  public void clear() {
    Arrays.fill(this.elements, 0L);
    this.size = 0;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof JumboEnumSet))
      return super.equals(paramObject); 
    JumboEnumSet jumboEnumSet = (JumboEnumSet)paramObject;
    return (jumboEnumSet.elementType != this.elementType) ? ((this.size == 0 && jumboEnumSet.size == 0)) : Arrays.equals(jumboEnumSet.elements, this.elements);
  }
  
  private boolean recalculateSize() {
    int i = this.size;
    this.size = 0;
    for (long l : this.elements)
      this.size += Long.bitCount(l); 
    return (this.size != i);
  }
  
  public EnumSet<E> clone() {
    JumboEnumSet jumboEnumSet = (JumboEnumSet)super.clone();
    jumboEnumSet.elements = (long[])jumboEnumSet.elements.clone();
    return jumboEnumSet;
  }
  
  private class EnumSetIterator<E extends Enum<E>> extends Object implements Iterator<E> {
    long unseen;
    
    int unseenIndex = 0;
    
    long lastReturned = 0L;
    
    int lastReturnedIndex = 0;
    
    EnumSetIterator() { this.unseen = this$0.elements[0]; }
    
    public boolean hasNext() {
      while (this.unseen == 0L && this.unseenIndex < JumboEnumSet.this.elements.length - 1)
        this.unseen = JumboEnumSet.this.elements[++this.unseenIndex]; 
      return (this.unseen != 0L);
    }
    
    public E next() {
      if (!hasNext())
        throw new NoSuchElementException(); 
      this.lastReturned = this.unseen & -this.unseen;
      this.lastReturnedIndex = this.unseenIndex;
      this.unseen -= this.lastReturned;
      return (E)JumboEnumSet.this.universe[(this.lastReturnedIndex << 6) + Long.numberOfTrailingZeros(this.lastReturned)];
    }
    
    public void remove() {
      if (this.lastReturned == 0L)
        throw new IllegalStateException(); 
      long l = JumboEnumSet.this.elements[this.lastReturnedIndex];
      JumboEnumSet.this.elements[this.lastReturnedIndex] = JumboEnumSet.this.elements[this.lastReturnedIndex] & (this.lastReturned ^ 0xFFFFFFFFFFFFFFFFL);
      if (l != JumboEnumSet.this.elements[this.lastReturnedIndex])
        JumboEnumSet.this.size--; 
      this.lastReturned = 0L;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\JumboEnumSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */