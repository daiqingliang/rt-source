package javax.management.openmbean;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class CompositeDataSupport implements CompositeData, Serializable {
  static final long serialVersionUID = 8003518976613702244L;
  
  private final SortedMap<String, Object> contents;
  
  private final CompositeType compositeType;
  
  public CompositeDataSupport(CompositeType paramCompositeType, String[] paramArrayOfString, Object[] paramArrayOfObject) throws OpenDataException { this(makeMap(paramArrayOfString, paramArrayOfObject), paramCompositeType); }
  
  private static SortedMap<String, Object> makeMap(String[] paramArrayOfString, Object[] paramArrayOfObject) throws OpenDataException {
    if (paramArrayOfString == null || paramArrayOfObject == null)
      throw new IllegalArgumentException("Null itemNames or itemValues"); 
    if (paramArrayOfString.length == 0 || paramArrayOfObject.length == 0)
      throw new IllegalArgumentException("Empty itemNames or itemValues"); 
    if (paramArrayOfString.length != paramArrayOfObject.length)
      throw new IllegalArgumentException("Different lengths: itemNames[" + paramArrayOfString.length + "], itemValues[" + paramArrayOfObject.length + "]"); 
    TreeMap treeMap = new TreeMap();
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      String str = paramArrayOfString[b];
      if (str == null || str.equals(""))
        throw new IllegalArgumentException("Null or empty item name"); 
      if (treeMap.containsKey(str))
        throw new OpenDataException("Duplicate item name " + str); 
      treeMap.put(paramArrayOfString[b], paramArrayOfObject[b]);
    } 
    return treeMap;
  }
  
  public CompositeDataSupport(CompositeType paramCompositeType, Map<String, ?> paramMap) throws OpenDataException { this(makeMap(paramMap), paramCompositeType); }
  
  private static SortedMap<String, Object> makeMap(Map<String, ?> paramMap) {
    if (paramMap == null || paramMap.isEmpty())
      throw new IllegalArgumentException("Null or empty items map"); 
    TreeMap treeMap = new TreeMap();
    for (Object object : paramMap.keySet()) {
      if (object == null || object.equals(""))
        throw new IllegalArgumentException("Null or empty item name"); 
      if (!(object instanceof String))
        throw new ArrayStoreException("Item name is not string: " + object); 
      treeMap.put((String)object, paramMap.get(object));
    } 
    return treeMap;
  }
  
  private CompositeDataSupport(SortedMap<String, Object> paramSortedMap, CompositeType paramCompositeType) throws OpenDataException {
    if (paramCompositeType == null)
      throw new IllegalArgumentException("Argument compositeType cannot be null."); 
    Set set1 = paramCompositeType.keySet();
    Set set2 = paramSortedMap.keySet();
    if (!set1.equals(set2)) {
      TreeSet treeSet1 = new TreeSet(set1);
      treeSet1.removeAll(set2);
      TreeSet treeSet2 = new TreeSet(set2);
      treeSet2.removeAll(set1);
      if (!treeSet1.isEmpty() || !treeSet2.isEmpty())
        throw new OpenDataException("Item names do not match CompositeType: names in items but not in CompositeType: " + treeSet2 + "; names in CompositeType but not in items: " + treeSet1); 
    } 
    for (String str : set1) {
      Object object = paramSortedMap.get(str);
      if (object != null) {
        OpenType openType = paramCompositeType.getType(str);
        if (!openType.isValue(object))
          throw new OpenDataException("Argument value of wrong type for item " + str + ": value " + object + ", type " + openType); 
      } 
    } 
    this.compositeType = paramCompositeType;
    this.contents = paramSortedMap;
  }
  
  public CompositeType getCompositeType() { return this.compositeType; }
  
  public Object get(String paramString) {
    if (paramString == null || paramString.trim().equals(""))
      throw new IllegalArgumentException("Argument key cannot be a null or empty String."); 
    if (!this.contents.containsKey(paramString.trim()))
      throw new InvalidKeyException("Argument key=\"" + paramString.trim() + "\" is not an existing item name for this CompositeData instance."); 
    return this.contents.get(paramString.trim());
  }
  
  public Object[] getAll(String[] paramArrayOfString) {
    if (paramArrayOfString == null || paramArrayOfString.length == 0)
      return new Object[0]; 
    Object[] arrayOfObject = new Object[paramArrayOfString.length];
    for (byte b = 0; b < paramArrayOfString.length; b++)
      arrayOfObject[b] = get(paramArrayOfString[b]); 
    return arrayOfObject;
  }
  
  public boolean containsKey(String paramString) { return (paramString == null || paramString.trim().equals("")) ? false : this.contents.containsKey(paramString); }
  
  public boolean containsValue(Object paramObject) { return this.contents.containsValue(paramObject); }
  
  public Collection<?> values() { return Collections.unmodifiableCollection(this.contents.values()); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof CompositeData))
      return false; 
    CompositeData compositeData = (CompositeData)paramObject;
    if (!getCompositeType().equals(compositeData.getCompositeType()))
      return false; 
    if (this.contents.size() != compositeData.values().size())
      return false; 
    for (Map.Entry entry : this.contents.entrySet()) {
      Object object1 = entry.getValue();
      Object object2 = compositeData.get((String)entry.getKey());
      if (object1 == object2)
        continue; 
      if (object1 == null)
        return false; 
      boolean bool = object1.getClass().isArray() ? Arrays.deepEquals(new Object[] { object1 }, new Object[] { object2 }) : object1.equals(object2);
      if (!bool)
        return false; 
    } 
    return true;
  }
  
  public int hashCode() {
    int i = this.compositeType.hashCode();
    for (Object object : this.contents.values()) {
      if (object instanceof Object[]) {
        i += Arrays.deepHashCode((Object[])object);
        continue;
      } 
      if (object instanceof byte[]) {
        i += Arrays.hashCode((byte[])object);
        continue;
      } 
      if (object instanceof short[]) {
        i += Arrays.hashCode((short[])object);
        continue;
      } 
      if (object instanceof int[]) {
        i += Arrays.hashCode((int[])object);
        continue;
      } 
      if (object instanceof long[]) {
        i += Arrays.hashCode((long[])object);
        continue;
      } 
      if (object instanceof char[]) {
        i += Arrays.hashCode((char[])object);
        continue;
      } 
      if (object instanceof float[]) {
        i += Arrays.hashCode((float[])object);
        continue;
      } 
      if (object instanceof double[]) {
        i += Arrays.hashCode((double[])object);
        continue;
      } 
      if (object instanceof boolean[]) {
        i += Arrays.hashCode((boolean[])object);
        continue;
      } 
      if (object != null)
        i += object.hashCode(); 
    } 
    return i;
  }
  
  public String toString() { return getClass().getName() + "(compositeType=" + this.compositeType.toString() + ",contents=" + contentString() + ")"; }
  
  private String contentString() {
    StringBuilder stringBuilder = new StringBuilder("{");
    String str = "";
    for (Map.Entry entry : this.contents.entrySet()) {
      stringBuilder.append(str).append((String)entry.getKey()).append("=");
      String str1 = Arrays.deepToString(new Object[] { entry.getValue() });
      stringBuilder.append(str1.substring(1, str1.length() - 1));
      str = ", ";
    } 
    stringBuilder.append("}");
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\openmbean\CompositeDataSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */