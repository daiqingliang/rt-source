package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;

public class XMLGrammarPoolImpl implements XMLGrammarPool {
  protected static final int TABLE_SIZE = 11;
  
  protected Entry[] fGrammars = null;
  
  protected boolean fPoolIsLocked;
  
  protected int fGrammarCount = 0;
  
  private static final boolean DEBUG = false;
  
  public XMLGrammarPoolImpl() {
    this.fGrammars = new Entry[11];
    this.fPoolIsLocked = false;
  }
  
  public XMLGrammarPoolImpl(int paramInt) {
    this.fGrammars = new Entry[paramInt];
    this.fPoolIsLocked = false;
  }
  
  public Grammar[] retrieveInitialGrammarSet(String paramString) {
    synchronized (this.fGrammars) {
      int i = this.fGrammars.length;
      Grammar[] arrayOfGrammar1 = new Grammar[this.fGrammarCount];
      byte b1 = 0;
      for (byte b2 = 0; b2 < i; b2++) {
        for (Entry entry = this.fGrammars[b2]; entry != null; entry = entry.next) {
          if (entry.desc.getGrammarType().equals(paramString))
            arrayOfGrammar1[b1++] = entry.grammar; 
        } 
      } 
      Grammar[] arrayOfGrammar2 = new Grammar[b1];
      System.arraycopy(arrayOfGrammar1, 0, arrayOfGrammar2, 0, b1);
      return arrayOfGrammar2;
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
        XMLGrammarDescription xMLGrammarDescription = paramGrammar.getGrammarDescription();
        int i = hashCode(xMLGrammarDescription);
        int j = (i & 0x7FFFFFFF) % this.fGrammars.length;
        Entry entry;
        for (entry = this.fGrammars[j]; entry != null; entry = entry.next) {
          if (entry.hash == i && equals(entry.desc, xMLGrammarDescription)) {
            entry.grammar = paramGrammar;
            return;
          } 
        } 
        entry = new Entry(i, xMLGrammarDescription, paramGrammar, this.fGrammars[j]);
        this.fGrammars[j] = entry;
        this.fGrammarCount++;
      }  
  }
  
  public Grammar getGrammar(XMLGrammarDescription paramXMLGrammarDescription) {
    synchronized (this.fGrammars) {
      int i = hashCode(paramXMLGrammarDescription);
      int j = (i & 0x7FFFFFFF) % this.fGrammars.length;
      for (Entry entry = this.fGrammars[j]; entry != null; entry = entry.next) {
        if (entry.hash == i && equals(entry.desc, paramXMLGrammarDescription))
          return entry.grammar; 
      } 
      return null;
    } 
  }
  
  public Grammar removeGrammar(XMLGrammarDescription paramXMLGrammarDescription) {
    synchronized (this.fGrammars) {
      int i = hashCode(paramXMLGrammarDescription);
      int j = (i & 0x7FFFFFFF) % this.fGrammars.length;
      Entry entry1 = this.fGrammars[j];
      Entry entry2 = null;
      while (entry1 != null) {
        if (entry1.hash == i && equals(entry1.desc, paramXMLGrammarDescription)) {
          if (entry2 != null) {
            entry2.next = entry1.next;
          } else {
            this.fGrammars[j] = entry1.next;
          } 
          Grammar grammar = entry1.grammar;
          entry1.grammar = null;
          this.fGrammarCount--;
          return grammar;
        } 
        entry2 = entry1;
        entry1 = entry1.next;
      } 
      return null;
    } 
  }
  
  public boolean containsGrammar(XMLGrammarDescription paramXMLGrammarDescription) {
    synchronized (this.fGrammars) {
      int i = hashCode(paramXMLGrammarDescription);
      int j = (i & 0x7FFFFFFF) % this.fGrammars.length;
      for (Entry entry = this.fGrammars[j]; entry != null; entry = entry.next) {
        if (entry.hash == i && equals(entry.desc, paramXMLGrammarDescription))
          return true; 
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
  
  public boolean equals(XMLGrammarDescription paramXMLGrammarDescription1, XMLGrammarDescription paramXMLGrammarDescription2) { return paramXMLGrammarDescription1.equals(paramXMLGrammarDescription2); }
  
  public int hashCode(XMLGrammarDescription paramXMLGrammarDescription) { return paramXMLGrammarDescription.hashCode(); }
  
  protected static final class Entry {
    public int hash;
    
    public XMLGrammarDescription desc;
    
    public Grammar grammar;
    
    public Entry next;
    
    protected Entry(int param1Int, XMLGrammarDescription param1XMLGrammarDescription, Grammar param1Grammar, Entry param1Entry) {
      this.hash = param1Int;
      this.desc = param1XMLGrammarDescription;
      this.grammar = param1Grammar;
      this.next = param1Entry;
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
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\XMLGrammarPoolImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */