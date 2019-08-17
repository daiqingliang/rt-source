package java.lang;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

final class ProcessEnvironment extends HashMap<String, String> {
  private static final long serialVersionUID = -8017839552603542824L;
  
  static final int MIN_NAME_LENGTH = 1;
  
  private static final NameComparator nameComparator = new NameComparator(null);
  
  private static final EntryComparator entryComparator = new EntryComparator(null);
  
  private static final ProcessEnvironment theEnvironment = new ProcessEnvironment();
  
  private static final Map<String, String> theUnmodifiableEnvironment = Collections.unmodifiableMap(theEnvironment);
  
  private static final Map<String, String> theCaseInsensitiveEnvironment;
  
  private static String validateName(String paramString) {
    if (paramString.indexOf('=', 1) != -1 || paramString.indexOf(false) != -1)
      throw new IllegalArgumentException("Invalid environment variable name: \"" + paramString + "\""); 
    return paramString;
  }
  
  private static String validateValue(String paramString) {
    if (paramString.indexOf(false) != -1)
      throw new IllegalArgumentException("Invalid environment variable value: \"" + paramString + "\""); 
    return paramString;
  }
  
  private static String nonNullString(Object paramObject) {
    if (paramObject == null)
      throw new NullPointerException(); 
    return (String)paramObject;
  }
  
  public String put(String paramString1, String paramString2) { return (String)super.put(validateName(paramString1), validateValue(paramString2)); }
  
  public String get(Object paramObject) { return (String)super.get(nonNullString(paramObject)); }
  
  public boolean containsKey(Object paramObject) { return super.containsKey(nonNullString(paramObject)); }
  
  public boolean containsValue(Object paramObject) { return super.containsValue(nonNullString(paramObject)); }
  
  public String remove(Object paramObject) { return (String)super.remove(nonNullString(paramObject)); }
  
  public Set<String> keySet() { return new CheckedKeySet(super.keySet()); }
  
  public Collection<String> values() { return new CheckedValues(super.values()); }
  
  public Set<Map.Entry<String, String>> entrySet() { return new CheckedEntrySet(super.entrySet()); }
  
  private ProcessEnvironment() {}
  
  private ProcessEnvironment(int paramInt) { super(paramInt); }
  
  static String getenv(String paramString) { return (String)theCaseInsensitiveEnvironment.get(paramString); }
  
  static Map<String, String> getenv() { return theUnmodifiableEnvironment; }
  
  static Map<String, String> environment() { return (Map)theEnvironment.clone(); }
  
  static Map<String, String> emptyEnvironment(int paramInt) { return new ProcessEnvironment(paramInt); }
  
  private static native String environmentBlock();
  
  String toEnvironmentBlock() {
    ArrayList arrayList = new ArrayList(entrySet());
    Collections.sort(arrayList, entryComparator);
    StringBuilder stringBuilder = new StringBuilder(size() * 30);
    int i = -1;
    for (Map.Entry entry : arrayList) {
      String str1 = (String)entry.getKey();
      String str2 = (String)entry.getValue();
      if (i < 0 && (i = nameComparator.compare(str1, "SystemRoot")) > 0)
        addToEnvIfSet(stringBuilder, "SystemRoot"); 
      addToEnv(stringBuilder, str1, str2);
    } 
    if (i < 0)
      addToEnvIfSet(stringBuilder, "SystemRoot"); 
    if (stringBuilder.length() == 0)
      stringBuilder.append(false); 
    stringBuilder.append(false);
    return stringBuilder.toString();
  }
  
  private static void addToEnvIfSet(StringBuilder paramStringBuilder, String paramString) {
    String str = getenv(paramString);
    if (str != null)
      addToEnv(paramStringBuilder, paramString, str); 
  }
  
  private static void addToEnv(StringBuilder paramStringBuilder, String paramString1, String paramString2) { paramStringBuilder.append(paramString1).append('=').append(paramString2).append(false); }
  
  static String toEnvironmentBlock(Map<String, String> paramMap) { return (paramMap == null) ? null : ((ProcessEnvironment)paramMap).toEnvironmentBlock(); }
  
  static  {
    String str = environmentBlock();
    int j;
    int k;
    for (int i = 0; (j = str.indexOf(false, i)) != -1 && (k = str.indexOf('=', i + 1)) != -1; i = j + 1) {
      if (k < j)
        theEnvironment.put(str.substring(i, k), str.substring(k + 1, j)); 
    } 
    theCaseInsensitiveEnvironment = new TreeMap(nameComparator);
    theCaseInsensitiveEnvironment.putAll(theEnvironment);
  }
  
  private static class CheckedEntry extends Object implements Map.Entry<String, String> {
    private final Map.Entry<String, String> e;
    
    public CheckedEntry(Map.Entry<String, String> param1Entry) { this.e = param1Entry; }
    
    public String getKey() { return (String)this.e.getKey(); }
    
    public String getValue() { return (String)this.e.getValue(); }
    
    public String setValue(String param1String) { return (String)this.e.setValue(ProcessEnvironment.validateValue(param1String)); }
    
    public String toString() { return getKey() + "=" + getValue(); }
    
    public boolean equals(Object param1Object) { return this.e.equals(param1Object); }
    
    public int hashCode() { return this.e.hashCode(); }
  }
  
  private static class CheckedEntrySet extends AbstractSet<Map.Entry<String, String>> {
    private final Set<Map.Entry<String, String>> s;
    
    public CheckedEntrySet(Set<Map.Entry<String, String>> param1Set) { this.s = param1Set; }
    
    public int size() { return this.s.size(); }
    
    public boolean isEmpty() { return this.s.isEmpty(); }
    
    public void clear() { this.s.clear(); }
    
    public Iterator<Map.Entry<String, String>> iterator() { return new Iterator<Map.Entry<String, String>>() {
          Iterator<Map.Entry<String, String>> i = ProcessEnvironment.CheckedEntrySet.this.s.iterator();
          
          public boolean hasNext() { return this.i.hasNext(); }
          
          public Map.Entry<String, String> next() { return new ProcessEnvironment.CheckedEntry((Map.Entry)this.i.next()); }
          
          public void remove() { this.i.remove(); }
        }; }
    
    private static Map.Entry<String, String> checkedEntry(Object param1Object) {
      Map.Entry entry = (Map.Entry)param1Object;
      ProcessEnvironment.nonNullString(entry.getKey());
      ProcessEnvironment.nonNullString(entry.getValue());
      return entry;
    }
    
    public boolean contains(Object param1Object) { return this.s.contains(checkedEntry(param1Object)); }
    
    public boolean remove(Object param1Object) { return this.s.remove(checkedEntry(param1Object)); }
  }
  
  private static class CheckedKeySet extends AbstractSet<String> {
    private final Set<String> s;
    
    public CheckedKeySet(Set<String> param1Set) { this.s = param1Set; }
    
    public int size() { return this.s.size(); }
    
    public boolean isEmpty() { return this.s.isEmpty(); }
    
    public void clear() { this.s.clear(); }
    
    public Iterator<String> iterator() { return this.s.iterator(); }
    
    public boolean contains(Object param1Object) { return this.s.contains(ProcessEnvironment.nonNullString(param1Object)); }
    
    public boolean remove(Object param1Object) { return this.s.remove(ProcessEnvironment.nonNullString(param1Object)); }
  }
  
  private static class CheckedValues extends AbstractCollection<String> {
    private final Collection<String> c;
    
    public CheckedValues(Collection<String> param1Collection) { this.c = param1Collection; }
    
    public int size() { return this.c.size(); }
    
    public boolean isEmpty() { return this.c.isEmpty(); }
    
    public void clear() { this.c.clear(); }
    
    public Iterator<String> iterator() { return this.c.iterator(); }
    
    public boolean contains(Object param1Object) { return this.c.contains(ProcessEnvironment.nonNullString(param1Object)); }
    
    public boolean remove(Object param1Object) { return this.c.remove(ProcessEnvironment.nonNullString(param1Object)); }
  }
  
  private static final class EntryComparator extends Object implements Comparator<Map.Entry<String, String>> {
    private EntryComparator() {}
    
    public int compare(Map.Entry<String, String> param1Entry1, Map.Entry<String, String> param1Entry2) { return nameComparator.compare((String)param1Entry1.getKey(), (String)param1Entry2.getKey()); }
  }
  
  private static final class NameComparator extends Object implements Comparator<String> {
    private NameComparator() {}
    
    public int compare(String param1String1, String param1String2) {
      int i = param1String1.length();
      int j = param1String2.length();
      int k = Math.min(i, j);
      for (byte b = 0; b < k; b++) {
        char c1 = param1String1.charAt(b);
        char c2 = param1String2.charAt(b);
        if (c1 != c2) {
          c1 = Character.toUpperCase(c1);
          c2 = Character.toUpperCase(c2);
          if (c1 != c2)
            return c1 - c2; 
        } 
      } 
      return i - j;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\ProcessEnvironment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */