package javax.swing;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

class MultiUIDefaults extends UIDefaults {
  private UIDefaults[] tables;
  
  public MultiUIDefaults(UIDefaults[] paramArrayOfUIDefaults) { this.tables = paramArrayOfUIDefaults; }
  
  public MultiUIDefaults() { this.tables = new UIDefaults[0]; }
  
  public Object get(Object paramObject) {
    Object object = super.get(paramObject);
    if (object != null)
      return object; 
    for (UIDefaults uIDefaults : this.tables) {
      object = (uIDefaults != null) ? uIDefaults.get(paramObject) : null;
      if (object != null)
        return object; 
    } 
    return null;
  }
  
  public Object get(Object paramObject, Locale paramLocale) {
    Object object = super.get(paramObject, paramLocale);
    if (object != null)
      return object; 
    for (UIDefaults uIDefaults : this.tables) {
      object = (uIDefaults != null) ? uIDefaults.get(paramObject, paramLocale) : null;
      if (object != null)
        return object; 
    } 
    return null;
  }
  
  public int size() { return entrySet().size(); }
  
  public boolean isEmpty() { return (size() == 0); }
  
  public Enumeration<Object> keys() { return new MultiUIDefaultsEnumerator(MultiUIDefaultsEnumerator.Type.KEYS, entrySet()); }
  
  public Enumeration<Object> elements() { return new MultiUIDefaultsEnumerator(MultiUIDefaultsEnumerator.Type.ELEMENTS, entrySet()); }
  
  public Set<Map.Entry<Object, Object>> entrySet() {
    HashSet hashSet = new HashSet();
    for (int i = this.tables.length - 1; i >= 0; i--) {
      if (this.tables[i] != null)
        hashSet.addAll(this.tables[i].entrySet()); 
    } 
    hashSet.addAll(super.entrySet());
    return hashSet;
  }
  
  protected void getUIError(String paramString) {
    if (this.tables.length > 0) {
      this.tables[0].getUIError(paramString);
    } else {
      super.getUIError(paramString);
    } 
  }
  
  public Object remove(Object paramObject) {
    Object object1 = null;
    for (int i = this.tables.length - 1; i >= 0; i--) {
      if (this.tables[i] != null) {
        Object object = this.tables[i].remove(paramObject);
        if (object != null)
          object1 = object; 
      } 
    } 
    Object object2 = super.remove(paramObject);
    if (object2 != null)
      object1 = object2; 
    return object1;
  }
  
  public void clear() {
    super.clear();
    for (UIDefaults uIDefaults : this.tables) {
      if (uIDefaults != null)
        uIDefaults.clear(); 
    } 
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("{");
    Enumeration enumeration = keys();
    while (enumeration.hasMoreElements()) {
      Object object = enumeration.nextElement();
      stringBuffer.append(object + "=" + get(object) + ", ");
    } 
    int i = stringBuffer.length();
    if (i > 1)
      stringBuffer.delete(i - 2, i); 
    stringBuffer.append("}");
    return stringBuffer.toString();
  }
  
  private static class MultiUIDefaultsEnumerator extends Object implements Enumeration<Object> {
    private Iterator<Map.Entry<Object, Object>> iterator;
    
    private Type type;
    
    MultiUIDefaultsEnumerator(Type param1Type, Set<Map.Entry<Object, Object>> param1Set) {
      this.type = param1Type;
      this.iterator = param1Set.iterator();
    }
    
    public boolean hasMoreElements() { return this.iterator.hasNext(); }
    
    public Object nextElement() { // Byte code:
      //   0: getstatic javax/swing/MultiUIDefaults$1.$SwitchMap$javax$swing$MultiUIDefaults$MultiUIDefaultsEnumerator$Type : [I
      //   3: aload_0
      //   4: getfield type : Ljavax/swing/MultiUIDefaults$MultiUIDefaultsEnumerator$Type;
      //   7: invokevirtual ordinal : ()I
      //   10: iaload
      //   11: lookupswitch default -> 72, 1 -> 36, 2 -> 54
      //   36: aload_0
      //   37: getfield iterator : Ljava/util/Iterator;
      //   40: invokeinterface next : ()Ljava/lang/Object;
      //   45: checkcast java/util/Map$Entry
      //   48: invokeinterface getKey : ()Ljava/lang/Object;
      //   53: areturn
      //   54: aload_0
      //   55: getfield iterator : Ljava/util/Iterator;
      //   58: invokeinterface next : ()Ljava/lang/Object;
      //   63: checkcast java/util/Map$Entry
      //   66: invokeinterface getValue : ()Ljava/lang/Object;
      //   71: areturn
      //   72: aconst_null
      //   73: areturn }
    
    public enum Type {
      KEYS, ELEMENTS;
    }
  }
  
  public enum Type {
    KEYS, ELEMENTS;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\MultiUIDefaults.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */