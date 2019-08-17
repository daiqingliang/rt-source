package com.sun.jmx.mbeanserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class Util {
  public static ObjectName newObjectName(String paramString) {
    try {
      return new ObjectName(paramString);
    } catch (MalformedObjectNameException malformedObjectNameException) {
      throw new IllegalArgumentException(malformedObjectNameException);
    } 
  }
  
  static <K, V> Map<K, V> newMap() { return new HashMap(); }
  
  static <K, V> Map<K, V> newSynchronizedMap() { return Collections.synchronizedMap(newMap()); }
  
  static <K, V> IdentityHashMap<K, V> newIdentityHashMap() { return new IdentityHashMap(); }
  
  static <K, V> Map<K, V> newSynchronizedIdentityHashMap() {
    IdentityHashMap identityHashMap = newIdentityHashMap();
    return Collections.synchronizedMap(identityHashMap);
  }
  
  static <K, V> SortedMap<K, V> newSortedMap() { return new TreeMap(); }
  
  static <K, V> SortedMap<K, V> newSortedMap(Comparator<? super K> paramComparator) { return new TreeMap(paramComparator); }
  
  static <K, V> Map<K, V> newInsertionOrderMap() { return new LinkedHashMap(); }
  
  static <E> Set<E> newSet() { return new HashSet(); }
  
  static <E> Set<E> newSet(Collection<E> paramCollection) { return new HashSet(paramCollection); }
  
  static <E> List<E> newList() { return new ArrayList(); }
  
  static <E> List<E> newList(Collection<E> paramCollection) { return new ArrayList(paramCollection); }
  
  public static <T> T cast(Object paramObject) { return (T)paramObject; }
  
  public static int hashCode(String[] paramArrayOfString, Object[] paramArrayOfObject) {
    int i = 0;
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      int j;
      Object object = paramArrayOfObject[b];
      if (object == null) {
        j = 0;
      } else if (object instanceof Object[]) {
        j = Arrays.deepHashCode((Object[])object);
      } else if (object.getClass().isArray()) {
        j = Arrays.deepHashCode(new Object[] { object }) - 31;
      } else {
        j = object.hashCode();
      } 
      i += (paramArrayOfString[b].toLowerCase().hashCode() ^ j);
    } 
    return i;
  }
  
  private static boolean wildmatch(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int j = -1;
    int i = j;
    while (true) {
      if (paramInt3 < paramInt4) {
        char c = paramString2.charAt(paramInt3);
        switch (c) {
          case '?':
            if (paramInt1 == paramInt2)
              break; 
            paramInt1++;
            paramInt3++;
            continue;
          case '*':
            j = ++paramInt3;
            i = paramInt1;
            continue;
          default:
            if (paramInt1 < paramInt2 && paramString1.charAt(paramInt1) == c) {
              paramInt1++;
              paramInt3++;
              continue;
            } 
            break;
        } 
      } else if (paramInt1 == paramInt2) {
        return true;
      } 
      if (j < 0 || i == paramInt2)
        return false; 
      paramInt3 = j;
      paramInt1 = ++i;
    } 
  }
  
  public static boolean wildmatch(String paramString1, String paramString2) { return wildmatch(paramString1, paramString2, 0, paramString1.length(), 0, paramString2.length()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\mbeanserver\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */