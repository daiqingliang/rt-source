package com.sun.corba.se.impl.util;

import java.util.Dictionary;
import java.util.Enumeration;

public final class IdentityHashtable extends Dictionary {
  private IdentityHashtableEntry[] table;
  
  private int count;
  
  private int threshold;
  
  private float loadFactor;
  
  public IdentityHashtable(int paramInt, float paramFloat) {
    if (paramInt <= 0 || paramFloat <= 0.0D)
      throw new IllegalArgumentException(); 
    this.loadFactor = paramFloat;
    this.table = new IdentityHashtableEntry[paramInt];
    this.threshold = (int)(paramInt * paramFloat);
  }
  
  public IdentityHashtable(int paramInt) { this(paramInt, 0.75F); }
  
  public IdentityHashtable() { this(101, 0.75F); }
  
  public int size() { return this.count; }
  
  public boolean isEmpty() { return (this.count == 0); }
  
  public Enumeration keys() { return new IdentityHashtableEnumerator(this.table, true); }
  
  public Enumeration elements() { return new IdentityHashtableEnumerator(this.table, false); }
  
  public boolean contains(Object paramObject) {
    if (paramObject == null)
      throw new NullPointerException(); 
    IdentityHashtableEntry[] arrayOfIdentityHashtableEntry = this.table;
    int i = arrayOfIdentityHashtableEntry.length;
    while (i-- > 0) {
      for (IdentityHashtableEntry identityHashtableEntry = arrayOfIdentityHashtableEntry[i]; identityHashtableEntry != null; identityHashtableEntry = identityHashtableEntry.next) {
        if (identityHashtableEntry.value == paramObject)
          return true; 
      } 
    } 
    return false;
  }
  
  public boolean containsKey(Object paramObject) {
    IdentityHashtableEntry[] arrayOfIdentityHashtableEntry = this.table;
    int i = System.identityHashCode(paramObject);
    int j = (i & 0x7FFFFFFF) % arrayOfIdentityHashtableEntry.length;
    for (IdentityHashtableEntry identityHashtableEntry = arrayOfIdentityHashtableEntry[j]; identityHashtableEntry != null; identityHashtableEntry = identityHashtableEntry.next) {
      if (identityHashtableEntry.hash == i && identityHashtableEntry.key == paramObject)
        return true; 
    } 
    return false;
  }
  
  public Object get(Object paramObject) {
    IdentityHashtableEntry[] arrayOfIdentityHashtableEntry = this.table;
    int i = System.identityHashCode(paramObject);
    int j = (i & 0x7FFFFFFF) % arrayOfIdentityHashtableEntry.length;
    for (IdentityHashtableEntry identityHashtableEntry = arrayOfIdentityHashtableEntry[j]; identityHashtableEntry != null; identityHashtableEntry = identityHashtableEntry.next) {
      if (identityHashtableEntry.hash == i && identityHashtableEntry.key == paramObject)
        return identityHashtableEntry.value; 
    } 
    return null;
  }
  
  protected void rehash() {
    int i = this.table.length;
    IdentityHashtableEntry[] arrayOfIdentityHashtableEntry1 = this.table;
    int j = i * 2 + 1;
    IdentityHashtableEntry[] arrayOfIdentityHashtableEntry2 = new IdentityHashtableEntry[j];
    this.threshold = (int)(j * this.loadFactor);
    this.table = arrayOfIdentityHashtableEntry2;
    int k = i;
    while (k-- > 0) {
      IdentityHashtableEntry identityHashtableEntry = arrayOfIdentityHashtableEntry1[k];
      while (identityHashtableEntry != null) {
        IdentityHashtableEntry identityHashtableEntry1 = identityHashtableEntry;
        identityHashtableEntry = identityHashtableEntry.next;
        int m = (identityHashtableEntry1.hash & 0x7FFFFFFF) % j;
        identityHashtableEntry1.next = arrayOfIdentityHashtableEntry2[m];
        arrayOfIdentityHashtableEntry2[m] = identityHashtableEntry1;
      } 
    } 
  }
  
  public Object put(Object paramObject1, Object paramObject2) {
    if (paramObject2 == null)
      throw new NullPointerException(); 
    IdentityHashtableEntry[] arrayOfIdentityHashtableEntry = this.table;
    int i = System.identityHashCode(paramObject1);
    int j = (i & 0x7FFFFFFF) % arrayOfIdentityHashtableEntry.length;
    IdentityHashtableEntry identityHashtableEntry;
    for (identityHashtableEntry = arrayOfIdentityHashtableEntry[j]; identityHashtableEntry != null; identityHashtableEntry = identityHashtableEntry.next) {
      if (identityHashtableEntry.hash == i && identityHashtableEntry.key == paramObject1) {
        Object object = identityHashtableEntry.value;
        identityHashtableEntry.value = paramObject2;
        return object;
      } 
    } 
    if (this.count >= this.threshold) {
      rehash();
      return put(paramObject1, paramObject2);
    } 
    identityHashtableEntry = new IdentityHashtableEntry();
    identityHashtableEntry.hash = i;
    identityHashtableEntry.key = paramObject1;
    identityHashtableEntry.value = paramObject2;
    identityHashtableEntry.next = arrayOfIdentityHashtableEntry[j];
    arrayOfIdentityHashtableEntry[j] = identityHashtableEntry;
    this.count++;
    return null;
  }
  
  public Object remove(Object paramObject) {
    IdentityHashtableEntry[] arrayOfIdentityHashtableEntry = this.table;
    int i = System.identityHashCode(paramObject);
    int j = (i & 0x7FFFFFFF) % arrayOfIdentityHashtableEntry.length;
    IdentityHashtableEntry identityHashtableEntry1 = arrayOfIdentityHashtableEntry[j];
    IdentityHashtableEntry identityHashtableEntry2 = null;
    while (identityHashtableEntry1 != null) {
      if (identityHashtableEntry1.hash == i && identityHashtableEntry1.key == paramObject) {
        if (identityHashtableEntry2 != null) {
          identityHashtableEntry2.next = identityHashtableEntry1.next;
        } else {
          arrayOfIdentityHashtableEntry[j] = identityHashtableEntry1.next;
        } 
        this.count--;
        return identityHashtableEntry1.value;
      } 
      identityHashtableEntry2 = identityHashtableEntry1;
      identityHashtableEntry1 = identityHashtableEntry1.next;
    } 
    return null;
  }
  
  public void clear() {
    IdentityHashtableEntry[] arrayOfIdentityHashtableEntry = this.table;
    int i = arrayOfIdentityHashtableEntry.length;
    while (--i >= 0)
      arrayOfIdentityHashtableEntry[i] = null; 
    this.count = 0;
  }
  
  public String toString() {
    int i = size() - 1;
    StringBuffer stringBuffer = new StringBuffer();
    Enumeration enumeration1 = keys();
    Enumeration enumeration2 = elements();
    stringBuffer.append("{");
    for (byte b = 0; b <= i; b++) {
      String str1 = enumeration1.nextElement().toString();
      String str2 = enumeration2.nextElement().toString();
      stringBuffer.append(str1 + "=" + str2);
      if (b < i)
        stringBuffer.append(", "); 
    } 
    stringBuffer.append("}");
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\imp\\util\IdentityHashtable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */