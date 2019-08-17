package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

public class NamespaceSupport implements NamespaceContext {
  protected String[] fNamespace = new String[32];
  
  protected int fNamespaceSize;
  
  protected int[] fContext = new int[8];
  
  protected int fCurrentContext;
  
  protected String[] fPrefixes = new String[16];
  
  public NamespaceSupport() {}
  
  public NamespaceSupport(NamespaceContext paramNamespaceContext) {
    pushContext();
    Enumeration enumeration = paramNamespaceContext.getAllPrefixes();
    while (enumeration.hasMoreElements()) {
      String str1 = (String)enumeration.nextElement();
      String str2 = paramNamespaceContext.getURI(str1);
      declarePrefix(str1, str2);
    } 
  }
  
  public void reset() {
    this.fNamespaceSize = 0;
    this.fCurrentContext = 0;
    this.fNamespace[this.fNamespaceSize++] = XMLSymbols.PREFIX_XML;
    this.fNamespace[this.fNamespaceSize++] = NamespaceContext.XML_URI;
    this.fNamespace[this.fNamespaceSize++] = XMLSymbols.PREFIX_XMLNS;
    this.fNamespace[this.fNamespaceSize++] = NamespaceContext.XMLNS_URI;
    this.fContext[this.fCurrentContext] = this.fNamespaceSize;
  }
  
  public void pushContext() {
    if (this.fCurrentContext + 1 == this.fContext.length) {
      int[] arrayOfInt = new int[this.fContext.length * 2];
      System.arraycopy(this.fContext, 0, arrayOfInt, 0, this.fContext.length);
      this.fContext = arrayOfInt;
    } 
    this.fContext[++this.fCurrentContext] = this.fNamespaceSize;
  }
  
  public void popContext() { this.fNamespaceSize = this.fContext[this.fCurrentContext--]; }
  
  public boolean declarePrefix(String paramString1, String paramString2) {
    if (paramString1 == XMLSymbols.PREFIX_XML || paramString1 == XMLSymbols.PREFIX_XMLNS)
      return false; 
    for (int i = this.fNamespaceSize; i > this.fContext[this.fCurrentContext]; i -= 2) {
      if (this.fNamespace[i - 2] == paramString1) {
        this.fNamespace[i - 1] = paramString2;
        return true;
      } 
    } 
    if (this.fNamespaceSize == this.fNamespace.length) {
      String[] arrayOfString = new String[this.fNamespaceSize * 2];
      System.arraycopy(this.fNamespace, 0, arrayOfString, 0, this.fNamespaceSize);
      this.fNamespace = arrayOfString;
    } 
    this.fNamespace[this.fNamespaceSize++] = paramString1;
    this.fNamespace[this.fNamespaceSize++] = paramString2;
    return true;
  }
  
  public String getURI(String paramString) {
    for (int i = this.fNamespaceSize; i > 0; i -= 2) {
      if (this.fNamespace[i - 2] == paramString)
        return this.fNamespace[i - 1]; 
    } 
    return null;
  }
  
  public String getPrefix(String paramString) {
    for (int i = this.fNamespaceSize; i > 0; i -= 2) {
      if (this.fNamespace[i - true] == paramString && getURI(this.fNamespace[i - 2]) == paramString)
        return this.fNamespace[i - 2]; 
    } 
    return null;
  }
  
  public int getDeclaredPrefixCount() { return (this.fNamespaceSize - this.fContext[this.fCurrentContext]) / 2; }
  
  public String getDeclaredPrefixAt(int paramInt) { return this.fNamespace[this.fContext[this.fCurrentContext] + paramInt * 2]; }
  
  public Iterator getPrefixes() {
    byte b1 = 0;
    if (this.fPrefixes.length < this.fNamespace.length / 2) {
      String[] arrayOfString = new String[this.fNamespaceSize];
      this.fPrefixes = arrayOfString;
    } 
    String str = null;
    boolean bool = true;
    for (byte b2 = 2; b2 < this.fNamespaceSize - 2; b2 += 2) {
      str = this.fNamespace[b2 + 2];
      for (byte b = 0; b < b1; b++) {
        if (this.fPrefixes[b] == str) {
          bool = false;
          break;
        } 
      } 
      if (bool)
        this.fPrefixes[b1++] = str; 
      bool = true;
    } 
    return new IteratorPrefixes(this.fPrefixes, b1);
  }
  
  public Enumeration getAllPrefixes() {
    byte b1 = 0;
    if (this.fPrefixes.length < this.fNamespace.length / 2) {
      String[] arrayOfString = new String[this.fNamespaceSize];
      this.fPrefixes = arrayOfString;
    } 
    String str = null;
    boolean bool = true;
    for (byte b2 = 2; b2 < this.fNamespaceSize - 2; b2 += 2) {
      str = this.fNamespace[b2 + 2];
      for (byte b = 0; b < b1; b++) {
        if (this.fPrefixes[b] == str) {
          bool = false;
          break;
        } 
      } 
      if (bool)
        this.fPrefixes[b1++] = str; 
      bool = true;
    } 
    return new Prefixes(this.fPrefixes, b1);
  }
  
  public Vector getPrefixes(String paramString) {
    boolean bool1 = false;
    Object object = null;
    boolean bool2 = true;
    Vector vector = new Vector();
    for (int i = this.fNamespaceSize; i > 0; i -= 2) {
      if (this.fNamespace[i - true] == paramString && !vector.contains(this.fNamespace[i - 2]))
        vector.add(this.fNamespace[i - 2]); 
    } 
    return vector;
  }
  
  public boolean containsPrefix(String paramString) {
    for (int i = this.fNamespaceSize; i > 0; i -= 2) {
      if (this.fNamespace[i - 2] == paramString)
        return true; 
    } 
    return false;
  }
  
  public boolean containsPrefixInCurrentContext(String paramString) {
    for (int i = this.fContext[this.fCurrentContext]; i < this.fNamespaceSize; i += 2) {
      if (this.fNamespace[i] == paramString)
        return true; 
    } 
    return false;
  }
  
  protected final class IteratorPrefixes implements Iterator {
    private String[] prefixes;
    
    private int counter = 0;
    
    private int size = 0;
    
    public IteratorPrefixes(String[] param1ArrayOfString, int param1Int) {
      this.prefixes = param1ArrayOfString;
      this.size = param1Int;
    }
    
    public boolean hasNext() { return (this.counter < this.size); }
    
    public Object next() {
      if (this.counter < this.size)
        return NamespaceSupport.this.fPrefixes[this.counter++]; 
      throw new NoSuchElementException("Illegal access to Namespace prefixes enumeration.");
    }
    
    public String toString() {
      StringBuffer stringBuffer = new StringBuffer();
      for (byte b = 0; b < this.size; b++) {
        stringBuffer.append(this.prefixes[b]);
        stringBuffer.append(" ");
      } 
      return stringBuffer.toString();
    }
    
    public void remove() { throw new UnsupportedOperationException(); }
  }
  
  protected final class Prefixes implements Enumeration {
    private String[] prefixes;
    
    private int counter = 0;
    
    private int size = 0;
    
    public Prefixes(String[] param1ArrayOfString, int param1Int) {
      this.prefixes = param1ArrayOfString;
      this.size = param1Int;
    }
    
    public boolean hasMoreElements() { return (this.counter < this.size); }
    
    public Object nextElement() {
      if (this.counter < this.size)
        return NamespaceSupport.this.fPrefixes[this.counter++]; 
      throw new NoSuchElementException("Illegal access to Namespace prefixes enumeration.");
    }
    
    public String toString() {
      StringBuffer stringBuffer = new StringBuffer();
      for (byte b = 0; b < this.size; b++) {
        stringBuffer.append(this.prefixes[b]);
        stringBuffer.append(" ");
      } 
      return stringBuffer.toString();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\NamespaceSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */