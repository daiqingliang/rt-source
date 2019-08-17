package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class PrefixArray extends ValueArray {
  public static final int PREFIX_MAP_SIZE = 64;
  
  private int _initialCapacity;
  
  public String[] _array;
  
  private PrefixArray _readOnlyArray;
  
  private PrefixEntry[] _prefixMap = new PrefixEntry[64];
  
  private PrefixEntry _prefixPool;
  
  private NamespaceEntry _namespacePool;
  
  private NamespaceEntry[] _inScopeNamespaces;
  
  public int[] _currentInScope;
  
  public int _declarationId;
  
  public PrefixArray(int paramInt1, int paramInt2) {
    this._initialCapacity = paramInt1;
    this._maximumCapacity = paramInt2;
    this._array = new String[paramInt1];
    this._inScopeNamespaces = new NamespaceEntry[paramInt1 + 2];
    this._currentInScope = new int[paramInt1 + 2];
    increaseNamespacePool(paramInt1);
    increasePrefixPool(paramInt1);
    initializeEntries();
  }
  
  public PrefixArray() { this(10, 2147483647); }
  
  private final void initializeEntries() {
    (this._inScopeNamespaces[0]).prefix = "";
    (this._inScopeNamespaces[0]).namespaceName = "";
    (this._inScopeNamespaces[0]).namespaceIndex = this._currentInScope[0] = 0;
    int i = KeyIntMap.indexFor(KeyIntMap.hashHash((this._inScopeNamespaces[0]).prefix.hashCode()), this._prefixMap.length);
    (this._prefixMap[i]).prefixId = 0;
    (this._inScopeNamespaces[1]).prefix = "xml";
    (this._inScopeNamespaces[1]).namespaceName = "http://www.w3.org/XML/1998/namespace";
    (this._inScopeNamespaces[1]).namespaceIndex = this._currentInScope[1] = 1;
    i = KeyIntMap.indexFor(KeyIntMap.hashHash((this._inScopeNamespaces[1]).prefix.hashCode()), this._prefixMap.length);
    if (this._prefixMap[i] == null) {
      (this._prefixMap[i]).next = null;
    } else {
      PrefixEntry prefixEntry = this._prefixMap[i];
      (this._prefixMap[i]).next = prefixEntry;
    } 
    (this._prefixMap[i]).prefixId = 1;
  }
  
  private final void increaseNamespacePool(int paramInt) {
    if (this._namespacePool == null)
      this._namespacePool = new NamespaceEntry(null); 
    for (byte b = 0; b < paramInt; b++) {
      NamespaceEntry namespaceEntry;
      namespaceEntry.next = this._namespacePool;
      this._namespacePool = namespaceEntry;
    } 
  }
  
  private final void increasePrefixPool(int paramInt) {
    if (this._prefixPool == null)
      this._prefixPool = new PrefixEntry(null); 
    for (byte b = 0; b < paramInt; b++) {
      PrefixEntry prefixEntry;
      prefixEntry.next = this._prefixPool;
      this._prefixPool = prefixEntry;
    } 
  }
  
  public int countNamespacePool() {
    byte b = 0;
    for (NamespaceEntry namespaceEntry = this._namespacePool; namespaceEntry != null; namespaceEntry = namespaceEntry.next)
      b++; 
    return b;
  }
  
  public int countPrefixPool() {
    byte b = 0;
    for (PrefixEntry prefixEntry = this._prefixPool; prefixEntry != null; prefixEntry = prefixEntry.next)
      b++; 
    return b;
  }
  
  public final void clear() {
    for (int i = this._readOnlyArraySize; i < this._size; i++)
      this._array[i] = null; 
    this._size = this._readOnlyArraySize;
  }
  
  public final void clearCompletely() {
    this._prefixPool = null;
    this._namespacePool = null;
    byte b;
    for (b = 0; b < this._size + 2; b++) {
      this._currentInScope[b] = 0;
      this._inScopeNamespaces[b] = null;
    } 
    for (b = 0; b < this._prefixMap.length; b++)
      this._prefixMap[b] = null; 
    increaseNamespacePool(this._initialCapacity);
    increasePrefixPool(this._initialCapacity);
    initializeEntries();
    this._declarationId = 0;
    clear();
  }
  
  public final String[] getArray() {
    if (this._array == null)
      return null; 
    String[] arrayOfString = new String[this._array.length];
    System.arraycopy(this._array, 0, arrayOfString, 0, this._array.length);
    return arrayOfString;
  }
  
  public final void setReadOnlyArray(ValueArray paramValueArray, boolean paramBoolean) {
    if (!(paramValueArray instanceof PrefixArray))
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[] { paramValueArray })); 
    setReadOnlyArray((PrefixArray)paramValueArray, paramBoolean);
  }
  
  public final void setReadOnlyArray(PrefixArray paramPrefixArray, boolean paramBoolean) {
    if (paramPrefixArray != null) {
      this._readOnlyArray = paramPrefixArray;
      this._readOnlyArraySize = paramPrefixArray.getSize();
      clearCompletely();
      this._inScopeNamespaces = new NamespaceEntry[this._readOnlyArraySize + this._inScopeNamespaces.length];
      this._currentInScope = new int[this._readOnlyArraySize + this._currentInScope.length];
      initializeEntries();
      if (paramBoolean)
        clear(); 
      this._array = getCompleteArray();
      this._size = this._readOnlyArraySize;
    } 
  }
  
  public final String[] getCompleteArray() {
    if (this._readOnlyArray == null)
      return getArray(); 
    String[] arrayOfString1 = this._readOnlyArray.getCompleteArray();
    String[] arrayOfString2 = new String[this._readOnlyArraySize + this._array.length];
    System.arraycopy(arrayOfString1, 0, arrayOfString2, 0, this._readOnlyArraySize);
    return arrayOfString2;
  }
  
  public final String get(int paramInt) { return this._array[paramInt]; }
  
  public final int add(String paramString) {
    if (this._size == this._array.length)
      resize(); 
    this._array[this._size++] = paramString;
    return this._size;
  }
  
  protected final void resize() {
    if (this._size == this._maximumCapacity)
      throw new ValueArrayResourceException(CommonResourceBundle.getInstance().getString("message.arrayMaxCapacity")); 
    int i = this._size * 3 / 2 + 1;
    if (i > this._maximumCapacity)
      i = this._maximumCapacity; 
    String[] arrayOfString = new String[i];
    System.arraycopy(this._array, 0, arrayOfString, 0, this._size);
    this._array = arrayOfString;
    i += 2;
    NamespaceEntry[] arrayOfNamespaceEntry = new NamespaceEntry[i];
    System.arraycopy(this._inScopeNamespaces, 0, arrayOfNamespaceEntry, 0, this._inScopeNamespaces.length);
    this._inScopeNamespaces = arrayOfNamespaceEntry;
    int[] arrayOfInt = new int[i];
    System.arraycopy(this._currentInScope, 0, arrayOfInt, 0, this._currentInScope.length);
    this._currentInScope = arrayOfInt;
  }
  
  public final void clearDeclarationIds() {
    for (byte b = 0; b < this._size; b++) {
      NamespaceEntry namespaceEntry = this._inScopeNamespaces[b];
      if (namespaceEntry != null)
        namespaceEntry.declarationId = 0; 
    } 
    this._declarationId = 1;
  }
  
  public final void pushScope(int paramInt1, int paramInt2) {
    if (this._namespacePool == null)
      increaseNamespacePool(16); 
    NamespaceEntry namespaceEntry1;
    this._namespacePool = namespaceEntry1.next;
    NamespaceEntry namespaceEntry2 = this._inScopeNamespaces[++paramInt1];
    if (namespaceEntry2 == null) {
      namespaceEntry1.declarationId = this._declarationId;
      namespaceEntry1.namespaceIndex = this._currentInScope[paramInt1] = ++paramInt2;
      namespaceEntry1.next = null;
      this._inScopeNamespaces[paramInt1] = namespaceEntry1;
    } else if (namespaceEntry2.declarationId < this._declarationId) {
      namespaceEntry1.declarationId = this._declarationId;
      namespaceEntry1.namespaceIndex = this._currentInScope[paramInt1] = ++paramInt2;
      namespaceEntry2.declarationId = 0;
      this._inScopeNamespaces[paramInt1] = namespaceEntry1;
    } else {
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.duplicateNamespaceAttribute"));
    } 
  }
  
  public final void pushScopeWithPrefixEntry(String paramString1, String paramString2, int paramInt1, int paramInt2) throws FastInfosetException {
    if (this._namespacePool == null)
      increaseNamespacePool(16); 
    if (this._prefixPool == null)
      increasePrefixPool(16); 
    NamespaceEntry namespaceEntry1;
    this._namespacePool = namespaceEntry1.next;
    NamespaceEntry namespaceEntry2 = this._inScopeNamespaces[++paramInt1];
    if (namespaceEntry2 == null) {
      namespaceEntry1.declarationId = this._declarationId;
      namespaceEntry1.namespaceIndex = this._currentInScope[paramInt1] = ++paramInt2;
      namespaceEntry1.next = null;
      this._inScopeNamespaces[paramInt1] = namespaceEntry1;
    } else if (namespaceEntry2.declarationId < this._declarationId) {
      namespaceEntry1.declarationId = this._declarationId;
      namespaceEntry1.namespaceIndex = this._currentInScope[paramInt1] = ++paramInt2;
      namespaceEntry2.declarationId = 0;
      this._inScopeNamespaces[paramInt1] = namespaceEntry1;
    } else {
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.duplicateNamespaceAttribute"));
    } 
    PrefixEntry prefixEntry1;
    prefixEntry1.prefixId = paramInt1;
    namespaceEntry1.prefix = paramString1;
    namespaceEntry1.namespaceName = paramString2;
    namespaceEntry1.prefixEntryIndex = KeyIntMap.indexFor(KeyIntMap.hashHash(paramString1.hashCode()), this._prefixMap.length);
    PrefixEntry prefixEntry2;
    prefixEntry1.next = prefixEntry2;
    this._prefixMap[namespaceEntry1.prefixEntryIndex] = prefixEntry1;
  }
  
  public final void popScope(int paramInt) {
    NamespaceEntry namespaceEntry;
    this._currentInScope[paramInt] = (namespaceEntry.next != null) ? namespaceEntry.next.namespaceIndex : 0;
    namespaceEntry.next = this._namespacePool;
    this._namespacePool = namespaceEntry;
  }
  
  public final void popScopeWithPrefixEntry(int paramInt) {
    NamespaceEntry namespaceEntry;
    this._currentInScope[paramInt] = (namespaceEntry.next != null) ? namespaceEntry.next.namespaceIndex : 0;
    namespaceEntry.prefix = namespaceEntry.namespaceName = null;
    namespaceEntry.next = this._namespacePool;
    PrefixEntry prefixEntry;
    if (prefixEntry.prefixId == paramInt) {
      prefixEntry.next = this._prefixPool;
      this._prefixPool = prefixEntry;
    } else {
      PrefixEntry prefixEntry1;
      for (PrefixEntry prefixEntry = prefixEntry.next; prefixEntry != null; prefixEntry = prefixEntry.next) {
        if (prefixEntry.prefixId == paramInt) {
          prefixEntry.next = this._prefixPool;
          this._prefixPool = prefixEntry;
          break;
        } 
      } 
    } 
  }
  
  public final String getNamespaceFromPrefix(String paramString) {
    int i = KeyIntMap.indexFor(KeyIntMap.hashHash(paramString.hashCode()), this._prefixMap.length);
    for (PrefixEntry prefixEntry = this._prefixMap[i]; prefixEntry != null; prefixEntry = prefixEntry.next) {
      NamespaceEntry namespaceEntry;
      if (paramString == namespaceEntry.prefix || paramString.equals(namespaceEntry.prefix))
        return namespaceEntry.namespaceName; 
    } 
    return null;
  }
  
  public final String getPrefixFromNamespace(String paramString) {
    byte b = 0;
    while (++b < this._size + 2) {
      NamespaceEntry namespaceEntry;
      if (namespaceEntry != null && paramString.equals(namespaceEntry.namespaceName))
        return namespaceEntry.prefix; 
    } 
    return null;
  }
  
  public final Iterator getPrefixes() { return new Iterator() {
        int _position = 1;
        
        PrefixArray.NamespaceEntry _ne = PrefixArray.this._inScopeNamespaces[this._position];
        
        public boolean hasNext() { return (this._ne != null); }
        
        public Object next() {
          if (this._position == PrefixArray.this._size + 2)
            throw new NoSuchElementException(); 
          String str = this._ne.prefix;
          moveToNext();
          return str;
        }
        
        public void remove() { throw new UnsupportedOperationException(); }
        
        private final void moveToNext() {
          while (++this._position < PrefixArray.this._size + 2) {
            this._ne = PrefixArray.this._inScopeNamespaces[this._position];
            if (this._ne != null)
              return; 
          } 
          this._ne = null;
        }
      }; }
  
  public final Iterator getPrefixesFromNamespace(final String namespaceName) { return new Iterator() {
        String _namespaceName = namespaceName;
        
        int _position = 0;
        
        PrefixArray.NamespaceEntry _ne;
        
        public boolean hasNext() { return (this._ne != null); }
        
        public Object next() {
          if (this._position == PrefixArray.this._size + 2)
            throw new NoSuchElementException(); 
          String str = this._ne.prefix;
          moveToNext();
          return str;
        }
        
        public void remove() { throw new UnsupportedOperationException(); }
        
        private final void moveToNext() {
          while (++this._position < PrefixArray.this._size + 2) {
            if (this._ne != null && this._namespaceName.equals(this._ne.namespaceName))
              return; 
          } 
          this._ne = null;
        }
      }; }
  
  private static class NamespaceEntry {
    private NamespaceEntry next;
    
    private int declarationId;
    
    private int namespaceIndex;
    
    private String prefix;
    
    private String namespaceName;
    
    private int prefixEntryIndex;
    
    private NamespaceEntry() {}
  }
  
  private static class PrefixEntry {
    private PrefixEntry next;
    
    private int prefixId;
    
    private PrefixEntry() {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfose\\util\PrefixArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */