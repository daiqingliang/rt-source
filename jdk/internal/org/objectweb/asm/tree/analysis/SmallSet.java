package jdk.internal.org.objectweb.asm.tree.analysis;

import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

class SmallSet<E> extends AbstractSet<E> implements Iterator<E> {
  E e1;
  
  E e2;
  
  static final <T> Set<T> emptySet() { return new SmallSet(null, null); }
  
  SmallSet(E paramE1, E paramE2) {
    this.e1 = paramE1;
    this.e2 = paramE2;
  }
  
  public Iterator<E> iterator() { return new SmallSet(this.e1, this.e2); }
  
  public int size() { return (this.e1 == null) ? 0 : ((this.e2 == null) ? 1 : 2); }
  
  public boolean hasNext() { return (this.e1 != null); }
  
  public E next() {
    if (this.e1 == null)
      throw new NoSuchElementException(); 
    Object object = this.e1;
    this.e1 = this.e2;
    this.e2 = null;
    return (E)object;
  }
  
  public void remove() {}
  
  Set<E> union(SmallSet<E> paramSmallSet) {
    if ((paramSmallSet.e1 == this.e1 && paramSmallSet.e2 == this.e2) || (paramSmallSet.e1 == this.e2 && paramSmallSet.e2 == this.e1))
      return this; 
    if (paramSmallSet.e1 == null)
      return this; 
    if (this.e1 == null)
      return paramSmallSet; 
    if (paramSmallSet.e2 == null) {
      if (this.e2 == null)
        return new SmallSet(this.e1, paramSmallSet.e1); 
      if (paramSmallSet.e1 == this.e1 || paramSmallSet.e1 == this.e2)
        return this; 
    } 
    if (this.e2 == null && (this.e1 == paramSmallSet.e1 || this.e1 == paramSmallSet.e2))
      return paramSmallSet; 
    HashSet hashSet = new HashSet(4);
    hashSet.add(this.e1);
    if (this.e2 != null)
      hashSet.add(this.e2); 
    hashSet.add(paramSmallSet.e1);
    if (paramSmallSet.e2 != null)
      hashSet.add(paramSmallSet.e2); 
    return hashSet;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\tree\analysis\SmallSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */