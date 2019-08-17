package sun.util.resources;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import sun.util.ResourceBundleEnumeration;

public abstract class OpenListResourceBundle extends ResourceBundle {
  protected Object handleGetObject(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    loadLookupTablesIfNecessary();
    return this.lookup.get(paramString);
  }
  
  public Enumeration<String> getKeys() {
    ResourceBundle resourceBundle = this.parent;
    return new ResourceBundleEnumeration(handleKeySet(), (resourceBundle != null) ? resourceBundle.getKeys() : null);
  }
  
  protected Set<String> handleKeySet() {
    loadLookupTablesIfNecessary();
    return this.lookup.keySet();
  }
  
  public Set<String> keySet() {
    if (this.keyset != null)
      return this.keyset; 
    Set set = createSet();
    set.addAll(handleKeySet());
    if (this.parent != null)
      set.addAll(this.parent.keySet()); 
    synchronized (this) {
      if (this.keyset == null)
        this.keyset = set; 
    } 
    return this.keyset;
  }
  
  protected abstract Object[][] getContents();
  
  void loadLookupTablesIfNecessary() {
    if (this.lookup == null)
      loadLookup(); 
  }
  
  private void loadLookup() {
    Object[][] arrayOfObject = getContents();
    Map map = createMap(arrayOfObject.length);
    for (byte b = 0; b < arrayOfObject.length; b++) {
      String str = (String)arrayOfObject[b][0];
      Object object = arrayOfObject[b][1];
      if (str == null || object == null)
        throw new NullPointerException(); 
      map.put(str, object);
    } 
    synchronized (this) {
      if (this.lookup == null)
        this.lookup = map; 
    } 
  }
  
  protected <K, V> Map<K, V> createMap(int paramInt) { return new HashMap(paramInt); }
  
  protected <E> Set<E> createSet() { return new HashSet(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\resources\OpenListResourceBundle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */