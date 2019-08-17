package java.util.concurrent;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CopyOnWriteArraySet<E> extends AbstractSet<E> implements Serializable {
  private static final long serialVersionUID = 5457747651344034263L;
  
  private final CopyOnWriteArrayList<E> al;
  
  public CopyOnWriteArraySet() { this.al = new CopyOnWriteArrayList(); }
  
  public CopyOnWriteArraySet(Collection<? extends E> paramCollection) {
    if (paramCollection.getClass() == CopyOnWriteArraySet.class) {
      CopyOnWriteArraySet copyOnWriteArraySet = (CopyOnWriteArraySet)paramCollection;
      this.al = new CopyOnWriteArrayList(copyOnWriteArraySet.al);
    } else {
      this.al = new CopyOnWriteArrayList();
      this.al.addAllAbsent(paramCollection);
    } 
  }
  
  public int size() { return this.al.size(); }
  
  public boolean isEmpty() { return this.al.isEmpty(); }
  
  public boolean contains(Object paramObject) { return this.al.contains(paramObject); }
  
  public Object[] toArray() { return this.al.toArray(); }
  
  public <T> T[] toArray(T[] paramArrayOfT) { return (T[])this.al.toArray(paramArrayOfT); }
  
  public void clear() { this.al.clear(); }
  
  public boolean remove(Object paramObject) { return this.al.remove(paramObject); }
  
  public boolean add(E paramE) { return this.al.addIfAbsent(paramE); }
  
  public boolean containsAll(Collection<?> paramCollection) { return this.al.containsAll(paramCollection); }
  
  public boolean addAll(Collection<? extends E> paramCollection) { return (this.al.addAllAbsent(paramCollection) > 0); }
  
  public boolean removeAll(Collection<?> paramCollection) { return this.al.removeAll(paramCollection); }
  
  public boolean retainAll(Collection<?> paramCollection) { return this.al.retainAll(paramCollection); }
  
  public Iterator<E> iterator() { return this.al.iterator(); }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof Set))
      return false; 
    Set set = (Set)paramObject;
    Iterator iterator = set.iterator();
    Object[] arrayOfObject = this.al.getArray();
    int i = arrayOfObject.length;
    boolean[] arrayOfBoolean = new boolean[i];
    byte b = 0;
    label24: while (iterator.hasNext()) {
      if (++b > i)
        return false; 
      Object object = iterator.next();
      for (byte b1 = 0; b1 < i; b1++) {
        if (!arrayOfBoolean[b1] && eq(object, arrayOfObject[b1])) {
          arrayOfBoolean[b1] = true;
          continue label24;
        } 
      } 
      return false;
    } 
    return (b == i);
  }
  
  public boolean removeIf(Predicate<? super E> paramPredicate) { return this.al.removeIf(paramPredicate); }
  
  public void forEach(Consumer<? super E> paramConsumer) { this.al.forEach(paramConsumer); }
  
  public Spliterator<E> spliterator() { return Spliterators.spliterator(this.al.getArray(), 1025); }
  
  private static boolean eq(Object paramObject1, Object paramObject2) { return (paramObject1 == null) ? ((paramObject2 == null)) : paramObject1.equals(paramObject2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\CopyOnWriteArraySet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */