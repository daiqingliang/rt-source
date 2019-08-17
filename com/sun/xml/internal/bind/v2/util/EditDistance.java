package com.sun.xml.internal.bind.v2.util;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.WeakHashMap;

public class EditDistance {
  private static final WeakHashMap<AbstractMap.SimpleEntry<String, String>, Integer> CACHE = new WeakHashMap();
  
  private int[] cost;
  
  private int[] back;
  
  private final String a;
  
  private final String b;
  
  public static int editDistance(String paramString1, String paramString2) {
    AbstractMap.SimpleEntry simpleEntry = new AbstractMap.SimpleEntry(paramString1, paramString2);
    Integer integer = null;
    if (CACHE.containsKey(simpleEntry))
      integer = (Integer)CACHE.get(simpleEntry); 
    if (integer == null) {
      integer = Integer.valueOf((new EditDistance(paramString1, paramString2)).calc());
      CACHE.put(simpleEntry, integer);
    } 
    return integer.intValue();
  }
  
  public static String findNearest(String paramString, String[] paramArrayOfString) { return findNearest(paramString, Arrays.asList(paramArrayOfString)); }
  
  public static String findNearest(String paramString, Collection<String> paramCollection) {
    int i = Integer.MAX_VALUE;
    String str = null;
    for (String str1 : paramCollection) {
      int j = editDistance(paramString, str1);
      if (i > j) {
        i = j;
        str = str1;
      } 
    } 
    return str;
  }
  
  private EditDistance(String paramString1, String paramString2) {
    this.a = paramString1;
    this.b = paramString2;
    this.cost = new int[paramString1.length() + 1];
    this.back = new int[paramString1.length() + 1];
    for (byte b1 = 0; b1 <= paramString1.length(); b1++)
      this.cost[b1] = b1; 
  }
  
  private void flip() {
    int[] arrayOfInt = this.cost;
    this.cost = this.back;
    this.back = arrayOfInt;
  }
  
  private int min(int paramInt1, int paramInt2, int paramInt3) { return Math.min(paramInt1, Math.min(paramInt2, paramInt3)); }
  
  private int calc() {
    for (byte b1 = 0; b1 < this.b.length(); b1++) {
      flip();
      this.cost[0] = b1 + true;
      for (byte b2 = 0; b2 < this.a.length(); b2++) {
        int i = (this.a.charAt(b2) == this.b.charAt(b1)) ? 0 : 1;
        this.cost[b2 + 1] = min(this.back[b2] + i, this.cost[b2] + 1, this.back[b2 + 1] + 1);
      } 
    } 
    return this.cost[this.a.length()];
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v\\util\EditDistance.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */