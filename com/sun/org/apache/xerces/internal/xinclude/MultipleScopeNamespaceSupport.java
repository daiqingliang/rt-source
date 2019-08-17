package com.sun.org.apache.xerces.internal.xinclude;

import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import java.util.Enumeration;

public class MultipleScopeNamespaceSupport extends NamespaceSupport {
  protected int[] fScope = new int[8];
  
  protected int fCurrentScope = 0;
  
  public MultipleScopeNamespaceSupport() { this.fScope[0] = 0; }
  
  public MultipleScopeNamespaceSupport(NamespaceContext paramNamespaceContext) {
    super(paramNamespaceContext);
    this.fScope[0] = 0;
  }
  
  public Enumeration getAllPrefixes() {
    byte b = 0;
    if (this.fPrefixes.length < this.fNamespace.length / 2) {
      String[] arrayOfString = new String[this.fNamespaceSize];
      this.fPrefixes = arrayOfString;
    } 
    String str = null;
    boolean bool = true;
    for (int i = this.fContext[this.fScope[this.fCurrentScope]]; i <= this.fNamespaceSize - 2; i += 2) {
      str = this.fNamespace[i];
      for (byte b1 = 0; b1 < b; b1++) {
        if (this.fPrefixes[b1] == str) {
          bool = false;
          break;
        } 
      } 
      if (bool)
        this.fPrefixes[b++] = str; 
      bool = true;
    } 
    return new NamespaceSupport.Prefixes(this, this.fPrefixes, b);
  }
  
  public int getScopeForContext(int paramInt) {
    int i;
    for (i = this.fCurrentScope; paramInt < this.fScope[i]; i--);
    return i;
  }
  
  public String getPrefix(String paramString) { return getPrefix(paramString, this.fNamespaceSize, this.fContext[this.fScope[this.fCurrentScope]]); }
  
  public String getURI(String paramString) { return getURI(paramString, this.fNamespaceSize, this.fContext[this.fScope[this.fCurrentScope]]); }
  
  public String getPrefix(String paramString, int paramInt) { return getPrefix(paramString, this.fContext[paramInt + 1], this.fContext[this.fScope[getScopeForContext(paramInt)]]); }
  
  public String getURI(String paramString, int paramInt) { return getURI(paramString, this.fContext[paramInt + 1], this.fContext[this.fScope[getScopeForContext(paramInt)]]); }
  
  public String getPrefix(String paramString, int paramInt1, int paramInt2) {
    if (paramString == NamespaceContext.XML_URI)
      return XMLSymbols.PREFIX_XML; 
    if (paramString == NamespaceContext.XMLNS_URI)
      return XMLSymbols.PREFIX_XMLNS; 
    for (int i = paramInt1; i > paramInt2; i -= 2) {
      if (this.fNamespace[i - true] == paramString && getURI(this.fNamespace[i - 2]) == paramString)
        return this.fNamespace[i - 2]; 
    } 
    return null;
  }
  
  public String getURI(String paramString, int paramInt1, int paramInt2) {
    if (paramString == XMLSymbols.PREFIX_XML)
      return NamespaceContext.XML_URI; 
    if (paramString == XMLSymbols.PREFIX_XMLNS)
      return NamespaceContext.XMLNS_URI; 
    for (int i = paramInt1; i > paramInt2; i -= 2) {
      if (this.fNamespace[i - 2] == paramString)
        return this.fNamespace[i - 1]; 
    } 
    return null;
  }
  
  public void reset() {
    this.fCurrentContext = this.fScope[this.fCurrentScope];
    this.fNamespaceSize = this.fContext[this.fCurrentContext];
  }
  
  public void pushScope() {
    if (this.fCurrentScope + 1 == this.fScope.length) {
      int[] arrayOfInt = new int[this.fScope.length * 2];
      System.arraycopy(this.fScope, 0, arrayOfInt, 0, this.fScope.length);
      this.fScope = arrayOfInt;
    } 
    pushContext();
    this.fScope[++this.fCurrentScope] = this.fCurrentContext;
  }
  
  public void popScope() {
    this.fCurrentContext = this.fScope[this.fCurrentScope--];
    popContext();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xinclude\MultipleScopeNamespaceSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */