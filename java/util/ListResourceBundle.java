package java.util;

import sun.util.ResourceBundleEnumeration;

public abstract class ListResourceBundle extends ResourceBundle {
  private Map<String, Object> lookup = null;
  
  public final Object handleGetObject(String paramString) {
    if (this.lookup == null)
      loadLookup(); 
    if (paramString == null)
      throw new NullPointerException(); 
    return this.lookup.get(paramString);
  }
  
  public Enumeration<String> getKeys() {
    if (this.lookup == null)
      loadLookup(); 
    ResourceBundle resourceBundle = this.parent;
    return new ResourceBundleEnumeration(this.lookup.keySet(), (resourceBundle != null) ? resourceBundle.getKeys() : null);
  }
  
  protected Set<String> handleKeySet() {
    if (this.lookup == null)
      loadLookup(); 
    return this.lookup.keySet();
  }
  
  protected abstract Object[][] getContents();
  
  private void loadLookup() {
    if (this.lookup != null)
      return; 
    Object[][] arrayOfObject = getContents();
    HashMap hashMap = new HashMap(arrayOfObject.length);
    for (byte b = 0; b < arrayOfObject.length; b++) {
      String str = (String)arrayOfObject[b][0];
      Object object = arrayOfObject[b][1];
      if (str == null || object == null)
        throw new NullPointerException(); 
      hashMap.put(str, object);
    } 
    this.lookup = hashMap;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\ListResourceBundle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */