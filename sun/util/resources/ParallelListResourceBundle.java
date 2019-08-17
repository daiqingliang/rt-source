package sun.util.resources;

import java.util.AbstractSet;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicMarkableReference;

public abstract class ParallelListResourceBundle extends ResourceBundle {
  private final AtomicMarkableReference<Object[][]> parallelContents = new AtomicMarkableReference(null, false);
  
  protected abstract Object[][] getContents();
  
  ResourceBundle getParent() { return this.parent; }
  
  public void setParallelContents(OpenListResourceBundle paramOpenListResourceBundle) {
    if (paramOpenListResourceBundle == null) {
      this.parallelContents.compareAndSet(null, null, false, true);
    } else {
      this.parallelContents.compareAndSet(null, paramOpenListResourceBundle.getContents(), false, false);
    } 
  }
  
  boolean areParallelContentsComplete() {
    if (this.parallelContents.isMarked())
      return true; 
    boolean[] arrayOfBoolean = new boolean[1];
    Object[][] arrayOfObject = (Object[][])this.parallelContents.get(arrayOfBoolean);
    return (arrayOfObject != null || arrayOfBoolean[0]);
  }
  
  protected Object handleGetObject(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    loadLookupTablesIfNecessary();
    return this.lookup.get(paramString);
  }
  
  public Enumeration<String> getKeys() { return Collections.enumeration(keySet()); }
  
  public boolean containsKey(String paramString) { return keySet().contains(paramString); }
  
  protected Set<String> handleKeySet() {
    loadLookupTablesIfNecessary();
    return this.lookup.keySet();
  }
  
  public Set<String> keySet() {
    Set set;
    while ((set = this.keyset) == null) {
      set = new KeySet(handleKeySet(), this.parent, null);
      synchronized (this) {
        if (this.keyset == null)
          this.keyset = set; 
      } 
    } 
    return set;
  }
  
  void resetKeySet() { this.keyset = null; }
  
  void loadLookupTablesIfNecessary() {
    ConcurrentMap concurrentMap = this.lookup;
    if (concurrentMap == null) {
      concurrentMap = new ConcurrentHashMap();
      for (Object[] arrayOfObject1 : getContents())
        concurrentMap.put((String)arrayOfObject1[0], arrayOfObject1[1]); 
    } 
    Object[][] arrayOfObject = (Object[][])this.parallelContents.getReference();
    if (arrayOfObject != null) {
      for (Object[] arrayOfObject1 : arrayOfObject)
        concurrentMap.putIfAbsent((String)arrayOfObject1[0], arrayOfObject1[1]); 
      this.parallelContents.set(null, true);
    } 
    if (this.lookup == null)
      synchronized (this) {
        if (this.lookup == null)
          this.lookup = concurrentMap; 
      }  
  }
  
  private static class KeySet extends AbstractSet<String> {
    private final Set<String> set;
    
    private final ResourceBundle parent;
    
    private KeySet(Set<String> param1Set, ResourceBundle param1ResourceBundle) {
      this.set = param1Set;
      this.parent = param1ResourceBundle;
    }
    
    public boolean contains(Object param1Object) { return this.set.contains(param1Object) ? true : ((this.parent != null) ? this.parent.containsKey((String)param1Object) : 0); }
    
    public Iterator<String> iterator() { return (this.parent == null) ? this.set.iterator() : new Iterator<String>() {
          private Iterator<String> itr = ParallelListResourceBundle.KeySet.this.set.iterator();
          
          private boolean usingParent;
          
          public boolean hasNext() {
            if (this.itr.hasNext())
              return true; 
            if (!this.usingParent) {
              HashSet hashSet = new HashSet(ParallelListResourceBundle.KeySet.this.parent.keySet());
              hashSet.removeAll(ParallelListResourceBundle.KeySet.this.set);
              this.itr = hashSet.iterator();
              this.usingParent = true;
            } 
            return this.itr.hasNext();
          }
          
          public String next() {
            if (hasNext())
              return (String)this.itr.next(); 
            throw new NoSuchElementException();
          }
          
          public void remove() { throw new UnsupportedOperationException(); }
        }; }
    
    public int size() {
      if (this.parent == null)
        return this.set.size(); 
      HashSet hashSet = new HashSet(this.set);
      hashSet.addAll(this.parent.keySet());
      return hashSet.size();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\resources\ParallelListResourceBundle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */