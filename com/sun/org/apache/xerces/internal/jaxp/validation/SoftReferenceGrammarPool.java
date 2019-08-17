package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLSchemaDescription;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

final class SoftReferenceGrammarPool implements XMLGrammarPool {
  protected static final int TABLE_SIZE = 11;
  
  protected static final Grammar[] ZERO_LENGTH_GRAMMAR_ARRAY = new Grammar[0];
  
  protected Entry[] fGrammars = null;
  
  protected boolean fPoolIsLocked;
  
  protected int fGrammarCount = 0;
  
  protected final ReferenceQueue fReferenceQueue = new ReferenceQueue();
  
  public SoftReferenceGrammarPool() {
    this.fGrammars = new Entry[11];
    this.fPoolIsLocked = false;
  }
  
  public SoftReferenceGrammarPool(int paramInt) {
    this.fGrammars = new Entry[paramInt];
    this.fPoolIsLocked = false;
  }
  
  public Grammar[] retrieveInitialGrammarSet(String paramString) {
    synchronized (this.fGrammars) {
      clean();
      return ZERO_LENGTH_GRAMMAR_ARRAY;
    } 
  }
  
  public void cacheGrammars(String paramString, Grammar[] paramArrayOfGrammar) {
    if (!this.fPoolIsLocked)
      for (byte b = 0; b < paramArrayOfGrammar.length; b++)
        putGrammar(paramArrayOfGrammar[b]);  
  }
  
  public Grammar retrieveGrammar(XMLGrammarDescription paramXMLGrammarDescription) { return getGrammar(paramXMLGrammarDescription); }
  
  public void putGrammar(Grammar paramGrammar) {
    if (!this.fPoolIsLocked)
      synchronized (this.fGrammars) {
        clean();
        XMLGrammarDescription xMLGrammarDescription = paramGrammar.getGrammarDescription();
        int i = hashCode(xMLGrammarDescription);
        int j = (i & 0x7FFFFFFF) % this.fGrammars.length;
        Entry entry;
        for (entry = this.fGrammars[j]; entry != null; entry = entry.next) {
          if (entry.hash == i && equals(entry.desc, xMLGrammarDescription)) {
            if (entry.grammar.get() != paramGrammar)
              entry.grammar = new SoftGrammarReference(entry, paramGrammar, this.fReferenceQueue); 
            return;
          } 
        } 
        entry = new Entry(i, j, xMLGrammarDescription, paramGrammar, this.fGrammars[j], this.fReferenceQueue);
        this.fGrammars[j] = entry;
        this.fGrammarCount++;
      }  
  }
  
  public Grammar getGrammar(XMLGrammarDescription paramXMLGrammarDescription) {
    synchronized (this.fGrammars) {
      clean();
      int i = hashCode(paramXMLGrammarDescription);
      int j = (i & 0x7FFFFFFF) % this.fGrammars.length;
      for (Entry entry = this.fGrammars[j]; entry != null; entry = entry.next) {
        Grammar grammar = (Grammar)entry.grammar.get();
        if (grammar == null) {
          removeEntry(entry);
        } else if (entry.hash == i && equals(entry.desc, paramXMLGrammarDescription)) {
          return grammar;
        } 
      } 
      return null;
    } 
  }
  
  public Grammar removeGrammar(XMLGrammarDescription paramXMLGrammarDescription) {
    synchronized (this.fGrammars) {
      clean();
      int i = hashCode(paramXMLGrammarDescription);
      int j = (i & 0x7FFFFFFF) % this.fGrammars.length;
      for (Entry entry = this.fGrammars[j]; entry != null; entry = entry.next) {
        if (entry.hash == i && equals(entry.desc, paramXMLGrammarDescription))
          return removeEntry(entry); 
      } 
      return null;
    } 
  }
  
  public boolean containsGrammar(XMLGrammarDescription paramXMLGrammarDescription) {
    synchronized (this.fGrammars) {
      clean();
      int i = hashCode(paramXMLGrammarDescription);
      int j = (i & 0x7FFFFFFF) % this.fGrammars.length;
      for (Entry entry = this.fGrammars[j]; entry != null; entry = entry.next) {
        Grammar grammar = (Grammar)entry.grammar.get();
        if (grammar == null) {
          removeEntry(entry);
        } else if (entry.hash == i && equals(entry.desc, paramXMLGrammarDescription)) {
          return true;
        } 
      } 
      return false;
    } 
  }
  
  public void lockPool() { this.fPoolIsLocked = true; }
  
  public void unlockPool() { this.fPoolIsLocked = false; }
  
  public void clear() {
    for (byte b = 0; b < this.fGrammars.length; b++) {
      if (this.fGrammars[b] != null) {
        this.fGrammars[b].clear();
        this.fGrammars[b] = null;
      } 
    } 
    this.fGrammarCount = 0;
  }
  
  public boolean equals(XMLGrammarDescription paramXMLGrammarDescription1, XMLGrammarDescription paramXMLGrammarDescription2) {
    if (paramXMLGrammarDescription1 instanceof XMLSchemaDescription) {
      if (!(paramXMLGrammarDescription2 instanceof XMLSchemaDescription))
        return false; 
      XMLSchemaDescription xMLSchemaDescription1 = (XMLSchemaDescription)paramXMLGrammarDescription1;
      XMLSchemaDescription xMLSchemaDescription2 = (XMLSchemaDescription)paramXMLGrammarDescription2;
      String str1 = xMLSchemaDescription1.getTargetNamespace();
      if (str1 != null) {
        if (!str1.equals(xMLSchemaDescription2.getTargetNamespace()))
          return false; 
      } else if (xMLSchemaDescription2.getTargetNamespace() != null) {
        return false;
      } 
      String str2 = xMLSchemaDescription1.getExpandedSystemId();
      if (str2 != null) {
        if (!str2.equals(xMLSchemaDescription2.getExpandedSystemId()))
          return false; 
      } else if (xMLSchemaDescription2.getExpandedSystemId() != null) {
        return false;
      } 
      return true;
    } 
    return paramXMLGrammarDescription1.equals(paramXMLGrammarDescription2);
  }
  
  public int hashCode(XMLGrammarDescription paramXMLGrammarDescription) {
    if (paramXMLGrammarDescription instanceof XMLSchemaDescription) {
      XMLSchemaDescription xMLSchemaDescription = (XMLSchemaDescription)paramXMLGrammarDescription;
      String str1 = xMLSchemaDescription.getTargetNamespace();
      String str2 = xMLSchemaDescription.getExpandedSystemId();
      null = (str1 != null) ? str1.hashCode() : 0;
      return (str2 != null) ? str2.hashCode() : 0;
    } 
    return paramXMLGrammarDescription.hashCode();
  }
  
  private Grammar removeEntry(Entry paramEntry) {
    if (paramEntry.prev != null) {
      paramEntry.prev.next = paramEntry.next;
    } else {
      this.fGrammars[paramEntry.bucket] = paramEntry.next;
    } 
    if (paramEntry.next != null)
      paramEntry.next.prev = paramEntry.prev; 
    this.fGrammarCount--;
    paramEntry.grammar.entry = null;
    return (Grammar)paramEntry.grammar.get();
  }
  
  private void clean() {
    for (Reference reference = this.fReferenceQueue.poll(); reference != null; reference = this.fReferenceQueue.poll()) {
      Entry entry = ((SoftGrammarReference)reference).entry;
      if (entry != null)
        removeEntry(entry); 
    } 
  }
  
  static final class Entry {
    public int hash;
    
    public int bucket;
    
    public Entry prev;
    
    public Entry next;
    
    public XMLGrammarDescription desc;
    
    public SoftReferenceGrammarPool.SoftGrammarReference grammar;
    
    protected Entry(int param1Int1, int param1Int2, XMLGrammarDescription param1XMLGrammarDescription, Grammar param1Grammar, Entry param1Entry, ReferenceQueue param1ReferenceQueue) {
      this.hash = param1Int1;
      this.bucket = param1Int2;
      this.prev = null;
      this.next = param1Entry;
      if (param1Entry != null)
        param1Entry.prev = this; 
      this.desc = param1XMLGrammarDescription;
      this.grammar = new SoftReferenceGrammarPool.SoftGrammarReference(this, param1Grammar, param1ReferenceQueue);
    }
    
    protected void clear() {
      this.desc = null;
      this.grammar = null;
      if (this.next != null) {
        this.next.clear();
        this.next = null;
      } 
    }
  }
  
  static final class SoftGrammarReference extends SoftReference {
    public SoftReferenceGrammarPool.Entry entry;
    
    protected SoftGrammarReference(SoftReferenceGrammarPool.Entry param1Entry, Grammar param1Grammar, ReferenceQueue param1ReferenceQueue) {
      super(param1Grammar, param1ReferenceQueue);
      this.entry = param1Entry;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\SoftReferenceGrammarPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */