package com.sun.org.apache.xml.internal.dtm.ref;

public class ExpandedNameTable {
  private ExtendedType[] m_extendedTypes;
  
  private static int m_initialSize = 128;
  
  private int m_nextType;
  
  public static final int ELEMENT = 1;
  
  public static final int ATTRIBUTE = 2;
  
  public static final int TEXT = 3;
  
  public static final int CDATA_SECTION = 4;
  
  public static final int ENTITY_REFERENCE = 5;
  
  public static final int ENTITY = 6;
  
  public static final int PROCESSING_INSTRUCTION = 7;
  
  public static final int COMMENT = 8;
  
  public static final int DOCUMENT = 9;
  
  public static final int DOCUMENT_TYPE = 10;
  
  public static final int DOCUMENT_FRAGMENT = 11;
  
  public static final int NOTATION = 12;
  
  public static final int NAMESPACE = 13;
  
  ExtendedType hashET = new ExtendedType(-1, "", "");
  
  private static ExtendedType[] m_defaultExtendedTypes;
  
  private static float m_loadFactor = 0.75F;
  
  private static int m_initialCapacity = 203;
  
  private int m_capacity = m_initialCapacity;
  
  private int m_threshold = (int)(this.m_capacity * m_loadFactor);
  
  private HashEntry[] m_table = new HashEntry[this.m_capacity];
  
  public ExpandedNameTable() { initExtendedTypes(); }
  
  private void initExtendedTypes() {
    this.m_extendedTypes = new ExtendedType[m_initialSize];
    for (byte b = 0; b < 14; b++) {
      this.m_extendedTypes[b] = m_defaultExtendedTypes[b];
      this.m_table[b] = new HashEntry(m_defaultExtendedTypes[b], b, b, null);
    } 
    this.m_nextType = 14;
  }
  
  public int getExpandedTypeID(String paramString1, String paramString2, int paramInt) { return getExpandedTypeID(paramString1, paramString2, paramInt, false); }
  
  public int getExpandedTypeID(String paramString1, String paramString2, int paramInt, boolean paramBoolean) {
    if (null == paramString1)
      paramString1 = ""; 
    if (null == paramString2)
      paramString2 = ""; 
    int i = paramInt + paramString1.hashCode() + paramString2.hashCode();
    this.hashET.redefine(paramInt, paramString1, paramString2, i);
    int j = i % this.m_capacity;
    if (j < 0)
      j = -j; 
    for (HashEntry hashEntry1 = this.m_table[j]; hashEntry1 != null; hashEntry1 = hashEntry1.next) {
      if (hashEntry1.hash == i && hashEntry1.key.equals(this.hashET))
        return hashEntry1.value; 
    } 
    if (paramBoolean)
      return -1; 
    if (this.m_nextType > this.m_threshold) {
      rehash();
      j = i % this.m_capacity;
      if (j < 0)
        j = -j; 
    } 
    ExtendedType extendedType = new ExtendedType(paramInt, paramString1, paramString2, i);
    if (this.m_extendedTypes.length == this.m_nextType) {
      ExtendedType[] arrayOfExtendedType = new ExtendedType[this.m_extendedTypes.length * 2];
      System.arraycopy(this.m_extendedTypes, 0, arrayOfExtendedType, 0, this.m_extendedTypes.length);
      this.m_extendedTypes = arrayOfExtendedType;
    } 
    this.m_extendedTypes[this.m_nextType] = extendedType;
    HashEntry hashEntry2 = new HashEntry(extendedType, this.m_nextType, i, this.m_table[j]);
    this.m_table[j] = hashEntry2;
    return this.m_nextType++;
  }
  
  private void rehash() {
    int i = this.m_capacity;
    HashEntry[] arrayOfHashEntry = this.m_table;
    int j = 2 * i + 1;
    this.m_capacity = j;
    this.m_threshold = (int)(j * m_loadFactor);
    this.m_table = new HashEntry[j];
    for (int k = i - 1; k >= 0; k--) {
      HashEntry hashEntry = arrayOfHashEntry[k];
      while (hashEntry != null) {
        HashEntry hashEntry1 = hashEntry;
        hashEntry = hashEntry.next;
        int m = hashEntry1.hash % j;
        if (m < 0)
          m = -m; 
        hashEntry1.next = this.m_table[m];
        this.m_table[m] = hashEntry1;
      } 
    } 
  }
  
  public int getExpandedTypeID(int paramInt) { return paramInt; }
  
  public String getLocalName(int paramInt) { return this.m_extendedTypes[paramInt].getLocalName(); }
  
  public final int getLocalNameID(int paramInt) { return this.m_extendedTypes[paramInt].getLocalName().equals("") ? 0 : paramInt; }
  
  public String getNamespace(int paramInt) {
    String str = this.m_extendedTypes[paramInt].getNamespace();
    return str.equals("") ? null : str;
  }
  
  public final int getNamespaceID(int paramInt) { return this.m_extendedTypes[paramInt].getNamespace().equals("") ? 0 : paramInt; }
  
  public final short getType(int paramInt) { return (short)this.m_extendedTypes[paramInt].getNodeType(); }
  
  public int getSize() { return this.m_nextType; }
  
  public ExtendedType[] getExtendedTypes() { return this.m_extendedTypes; }
  
  static  {
    m_defaultExtendedTypes = new ExtendedType[14];
    for (byte b = 0; b < 14; b++)
      m_defaultExtendedTypes[b] = new ExtendedType(b, "", ""); 
  }
  
  private static final class HashEntry {
    ExtendedType key;
    
    int value;
    
    int hash;
    
    HashEntry next;
    
    protected HashEntry(ExtendedType param1ExtendedType, int param1Int1, int param1Int2, HashEntry param1HashEntry) {
      this.key = param1ExtendedType;
      this.value = param1Int1;
      this.hash = param1Int2;
      this.next = param1HashEntry;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\ExpandedNameTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */