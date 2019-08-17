package java.util;

public abstract class AbstractSet<E> extends AbstractCollection<E> implements Set<E> {
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof Set))
      return false; 
    Collection collection = (Collection)paramObject;
    if (collection.size() != size())
      return false; 
    try {
      return containsAll(collection);
    } catch (ClassCastException classCastException) {
      return false;
    } catch (NullPointerException nullPointerException) {
      return false;
    } 
  }
  
  public int hashCode() {
    int i = 0;
    for (Object object : this) {
      if (object != null)
        i += object.hashCode(); 
    } 
    return i;
  }
  
  public boolean removeAll(Collection<?> paramCollection) {
    Objects.requireNonNull(paramCollection);
    boolean bool = false;
    if (size() > paramCollection.size()) {
      Iterator iterator = paramCollection.iterator();
      while (iterator.hasNext())
        bool |= remove(iterator.next()); 
    } else {
      Iterator iterator = iterator();
      while (iterator.hasNext()) {
        if (paramCollection.contains(iterator.next())) {
          iterator.remove();
          bool = true;
        } 
      } 
    } 
    return bool;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\AbstractSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */