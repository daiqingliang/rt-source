package java.util;

public abstract class AbstractSequentialList<E> extends AbstractList<E> {
  public E get(int paramInt) {
    try {
      return (E)listIterator(paramInt).next();
    } catch (NoSuchElementException noSuchElementException) {
      throw new IndexOutOfBoundsException("Index: " + paramInt);
    } 
  }
  
  public E set(int paramInt, E paramE) {
    try {
      ListIterator listIterator = listIterator(paramInt);
      Object object = listIterator.next();
      listIterator.set(paramE);
      return (E)object;
    } catch (NoSuchElementException noSuchElementException) {
      throw new IndexOutOfBoundsException("Index: " + paramInt);
    } 
  }
  
  public void add(int paramInt, E paramE) {
    try {
      listIterator(paramInt).add(paramE);
    } catch (NoSuchElementException noSuchElementException) {
      throw new IndexOutOfBoundsException("Index: " + paramInt);
    } 
  }
  
  public E remove(int paramInt) {
    try {
      ListIterator listIterator = listIterator(paramInt);
      Object object = listIterator.next();
      listIterator.remove();
      return (E)object;
    } catch (NoSuchElementException noSuchElementException) {
      throw new IndexOutOfBoundsException("Index: " + paramInt);
    } 
  }
  
  public boolean addAll(int paramInt, Collection<? extends E> paramCollection) {
    try {
      boolean bool = false;
      ListIterator listIterator = listIterator(paramInt);
      Iterator iterator = paramCollection.iterator();
      while (iterator.hasNext()) {
        listIterator.add(iterator.next());
        bool = true;
      } 
      return bool;
    } catch (NoSuchElementException noSuchElementException) {
      throw new IndexOutOfBoundsException("Index: " + paramInt);
    } 
  }
  
  public Iterator<E> iterator() { return listIterator(); }
  
  public abstract ListIterator<E> listIterator(int paramInt);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\AbstractSequentialList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */