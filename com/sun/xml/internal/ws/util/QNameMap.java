package com.sun.xml.internal.ws.util;

import com.sun.istack.internal.NotNull;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.xml.namespace.QName;

public final class QNameMap<V> extends Object {
  private static final int DEFAULT_INITIAL_CAPACITY = 16;
  
  private static final int MAXIMUM_CAPACITY = 1073741824;
  
  Entry<V>[] table = new Entry[16];
  
  int size;
  
  private int threshold = 12;
  
  private static final float DEFAULT_LOAD_FACTOR = 0.75F;
  
  private Set<Entry<V>> entrySet = null;
  
  private Iterable<V> views = new Iterable<V>() {
      public Iterator<V> iterator() { return new QNameMap.ValueIterator(QNameMap.this, null); }
    };
  
  public QNameMap() { this.table = new Entry[16]; }
  
  public void put(String paramString1, String paramString2, V paramV) {
    assert paramString2 != null;
    assert paramString1 != null;
    int i = hash(paramString2);
    int j = indexFor(i, this.table.length);
    for (Entry entry = this.table[j]; entry != null; entry = entry.next) {
      if (entry.hash == i && paramString2.equals(entry.localName) && paramString1.equals(entry.nsUri)) {
        entry.value = paramV;
        return;
      } 
    } 
    addEntry(i, paramString1, paramString2, paramV, j);
  }
  
  public void put(QName paramQName, V paramV) { put(paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramV); }
  
  public V get(@NotNull String paramString1, String paramString2) {
    Entry entry = getEntry(paramString1, paramString2);
    return (entry == null) ? null : (V)entry.value;
  }
  
  public V get(QName paramQName) { return (V)get(paramQName.getNamespaceURI(), paramQName.getLocalPart()); }
  
  public int size() { return this.size; }
  
  public QNameMap<V> putAll(QNameMap<? extends V> paramQNameMap) {
    int i = paramQNameMap.size();
    if (i == 0)
      return this; 
    if (i > this.threshold) {
      int j = i;
      if (j > 1073741824)
        j = 1073741824; 
      int k;
      for (k = this.table.length; k < j; k <<= 1);
      if (k > this.table.length)
        resize(k); 
    } 
    for (Entry entry : paramQNameMap.entrySet())
      put(entry.nsUri, entry.localName, entry.getValue()); 
    return this;
  }
  
  public QNameMap<V> putAll(Map<QName, ? extends V> paramMap) {
    for (Map.Entry entry : paramMap.entrySet()) {
      QName qName = (QName)entry.getKey();
      put(qName.getNamespaceURI(), qName.getLocalPart(), entry.getValue());
    } 
    return this;
  }
  
  private static int hash(String paramString) {
    null = paramString.hashCode();
    null += (null << 9 ^ 0xFFFFFFFF);
    null ^= null >>> 14;
    null += (null << 4);
    return null >>> 10;
  }
  
  private static int indexFor(int paramInt1, int paramInt2) { return paramInt1 & paramInt2 - 1; }
  
  private void addEntry(int paramInt1, String paramString1, String paramString2, V paramV, int paramInt2) {
    Entry entry = this.table[paramInt2];
    this.table[paramInt2] = new Entry(paramInt1, paramString1, paramString2, paramV, entry);
    if (this.size++ >= this.threshold)
      resize(2 * this.table.length); 
  }
  
  private void resize(int paramInt) {
    Entry[] arrayOfEntry1 = this.table;
    int i = arrayOfEntry1.length;
    if (i == 1073741824) {
      this.threshold = Integer.MAX_VALUE;
      return;
    } 
    Entry[] arrayOfEntry2 = new Entry[paramInt];
    transfer(arrayOfEntry2);
    this.table = arrayOfEntry2;
    this.threshold = paramInt;
  }
  
  private void transfer(Entry<V>[] paramArrayOfEntry) {
    Entry[] arrayOfEntry = this.table;
    int i = paramArrayOfEntry.length;
    for (byte b = 0; b < arrayOfEntry.length; b++) {
      Entry entry = arrayOfEntry[b];
      if (entry != null) {
        arrayOfEntry[b] = null;
        do {
          Entry entry1 = entry.next;
          int j = indexFor(entry.hash, i);
          entry.next = paramArrayOfEntry[j];
          paramArrayOfEntry[j] = entry;
          entry = entry1;
        } while (entry != null);
      } 
    } 
  }
  
  public Entry<V> getOne() {
    for (Entry entry : this.table) {
      if (entry != null)
        return entry; 
    } 
    return null;
  }
  
  public Collection<QName> keySet() {
    HashSet hashSet = new HashSet();
    for (Entry entry : entrySet())
      hashSet.add(entry.createQName()); 
    return hashSet;
  }
  
  public Iterable<V> values() { return this.views; }
  
  public boolean containsKey(@NotNull String paramString1, String paramString2) { return (getEntry(paramString1, paramString2) != null); }
  
  public boolean isEmpty() { return (this.size == 0); }
  
  public Set<Entry<V>> entrySet() {
    Set set = this.entrySet;
    return (set != null) ? set : (this.entrySet = new EntrySet(null));
  }
  
  private Iterator<Entry<V>> newEntryIterator() { return new EntryIterator(null); }
  
  private Entry<V> getEntry(@NotNull String paramString1, String paramString2) {
    int i = hash(paramString2);
    int j = indexFor(i, this.table.length);
    Entry entry;
    for (entry = this.table[j]; entry != null && (!paramString2.equals(entry.localName) || !paramString1.equals(entry.nsUri)); entry = entry.next);
    return entry;
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append('{');
    for (Entry entry : entrySet()) {
      if (stringBuilder.length() > 1)
        stringBuilder.append(','); 
      stringBuilder.append('[');
      stringBuilder.append(entry);
      stringBuilder.append(']');
    } 
    stringBuilder.append('}');
    return stringBuilder.toString();
  }
  
  public static final class Entry<V> extends Object {
    public final String nsUri;
    
    public final String localName;
    
    V value;
    
    final int hash;
    
    Entry<V> next;
    
    Entry(int param1Int, String param1String1, String param1String2, V param1V, Entry<V> param1Entry) {
      this.value = param1V;
      this.next = param1Entry;
      this.nsUri = param1String1;
      this.localName = param1String2;
      this.hash = param1Int;
    }
    
    public QName createQName() { return new QName(this.nsUri, this.localName); }
    
    public V getValue() { return (V)this.value; }
    
    public V setValue(V param1V) {
      Object object = this.value;
      this.value = param1V;
      return (V)object;
    }
    
    public boolean equals(Object param1Object) {
      if (!(param1Object instanceof Entry))
        return false; 
      Entry entry = (Entry)param1Object;
      String str1 = this.nsUri;
      String str2 = entry.nsUri;
      String str3 = this.localName;
      String str4 = entry.localName;
      if (str1.equals(str2) && str3.equals(str4)) {
        Object object1 = getValue();
        Object object2 = entry.getValue();
        if (object1 == object2 || (object1 != null && object1.equals(object2)))
          return true; 
      } 
      return false;
    }
    
    public int hashCode() { return this.localName.hashCode() ^ ((this.value == null) ? 0 : this.value.hashCode()); }
    
    public String toString() { return '"' + this.nsUri + "\",\"" + this.localName + "\"=" + getValue(); }
  }
  
  private class EntryIterator extends HashIterator<Entry<V>> {
    private EntryIterator() { super(QNameMap.this); }
    
    public QNameMap.Entry<V> next() { return nextEntry(); }
  }
  
  private class EntrySet extends AbstractSet<Entry<V>> {
    private EntrySet() {}
    
    public Iterator<QNameMap.Entry<V>> iterator() { return QNameMap.this.newEntryIterator(); }
    
    public boolean contains(Object param1Object) {
      if (!(param1Object instanceof QNameMap.Entry))
        return false; 
      QNameMap.Entry entry1 = (QNameMap.Entry)param1Object;
      QNameMap.Entry entry2 = QNameMap.this.getEntry(entry1.nsUri, entry1.localName);
      return (entry2 != null && entry2.equals(entry1));
    }
    
    public boolean remove(Object param1Object) { throw new UnsupportedOperationException(); }
    
    public int size() { return QNameMap.this.size; }
  }
  
  private abstract class HashIterator<E> extends Object implements Iterator<E> {
    QNameMap.Entry<V> next;
    
    int index;
    
    HashIterator() {
      QNameMap.Entry[] arrayOfEntry = QNameMap.this.table;
      int i = arrayOfEntry.length;
      QNameMap.Entry entry = null;
      if (QNameMap.this.size != 0)
        while (i > 0 && (entry = arrayOfEntry[--i]) == null); 
      this.next = entry;
      this.index = i;
    }
    
    public boolean hasNext() { return (this.next != null); }
    
    QNameMap.Entry<V> nextEntry() {
      QNameMap.Entry entry1 = this.next;
      if (entry1 == null)
        throw new NoSuchElementException(); 
      QNameMap.Entry entry2 = entry1.next;
      QNameMap.Entry[] arrayOfEntry = QNameMap.this.table;
      int i = this.index;
      while (entry2 == null && i > 0)
        entry2 = arrayOfEntry[--i]; 
      this.index = i;
      this.next = entry2;
      return entry1;
    }
    
    public void remove() { throw new UnsupportedOperationException(); }
  }
  
  private class ValueIterator extends HashIterator<V> {
    private ValueIterator() { super(QNameMap.this); }
    
    public V next() { return (V)(nextEntry()).value; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\QNameMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */